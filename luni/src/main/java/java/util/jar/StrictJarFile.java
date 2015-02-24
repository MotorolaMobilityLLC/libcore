/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package java.util.jar;

import dalvik.system.CloseGuard;
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import libcore.io.IoUtils;
import libcore.io.Streams;

/**
 * A subset of the JarFile API implemented as a thin wrapper over
 * system/core/libziparchive.
 *
 * @hide for internal use only. Not API compatible (or as forgiving) as
 *        {@link java.util.jar.JarFile}
 */
public final class StrictJarFile {

    private final long nativeHandle;

    // NOTE: It's possible to share a file descriptor with the native
    // code, at the cost of some additional complexity.
    private final RandomAccessFile raf;

    private final StrictJarVerifier verifier;

    private final boolean isSigned;

    private final CloseGuard guard = CloseGuard.get();
    private boolean closed;

    public StrictJarFile(String fileName) throws IOException {
        this.nativeHandle = nativeOpenJarFile(fileName);
        this.raf = new RandomAccessFile(fileName, "r");

        try {
            // Read the MANIFEST and signature files up front and try to
            // parse them. We never want to accept a JAR File with broken signatures
            // or manifests, so it's best to throw as early as possible.
            HashMap<String, byte[]> metaEntries = getMetaEntries();
            StrictJarManifest manifest =
                    new StrictJarManifest(metaEntries.get(JarFile.MANIFEST_NAME), true);
            this.verifier = new StrictJarVerifier(fileName, manifest, metaEntries);

            isSigned = verifier.readCertificates() && verifier.isSignedJar();
        } catch (IOException ioe) {
            nativeClose(this.nativeHandle);
            throw ioe;
        }

        guard.open("close");
    }

    public Iterator<ZipEntry> iterator() throws IOException {
        return new EntryIterator(nativeHandle, "");
    }

    public ZipEntry findEntry(String name) {
        return nativeFindEntry(nativeHandle, name);
    }

    /**
     * Return all certificate chains for a given {@link ZipEntry} belonging to this jar.
     * This method MUST be called only after fully exhausting the InputStream belonging
     * to this entry.
     *
     * Returns {@code null} if this jar file isn't signed or if this method is
     * called before the stream is processed.
     */
    public Certificate[][] getCertificateChains(ZipEntry ze) {
        if (isSigned) {
            return verifier.getCertificateChains(ze.getName());
        }

        return null;
    }

    /**
     * Return all certificates for a given {@link ZipEntry} belonging to this jar.
     * This method MUST be called only after fully exhausting the InputStream belonging
     * to this entry.
     *
     * Returns {@code null} if this jar file isn't signed or if this method is
     * called before the stream is processed.
     *
     * @deprecated Switch callers to use getCertificateChains instead
     */
    @Deprecated
    public Certificate[] getCertificates(ZipEntry ze) {
        if (isSigned) {
            Certificate[][] certChains = verifier.getCertificateChains(ze.getName());

            // Measure number of certs.
            int count = 0;
            for (Certificate[] chain : certChains) {
                count += chain.length;
            }

            // Create new array and copy all the certs into it.
            Certificate[] certs = new Certificate[count];
            int i = 0;
            for (Certificate[] chain : certChains) {
                System.arraycopy(chain, 0, certs, i, chain.length);
                i += chain.length;
            }

            return certs;
        }

        return null;
    }

    public InputStream getInputStream(ZipEntry ze) {
        final InputStream is = getZipInputStream(ze);

        if (isSigned) {
            StrictJarVerifier.VerifierEntry entry = verifier.initEntry(ze.getName());
            if (entry == null) {
                return is;
            }

            return new JarFileInputStream(is, ze.getSize(), entry);
        }

        return is;
    }

    public void close() throws IOException {
        if (!closed) {
            guard.close();

            nativeClose(nativeHandle);
            IoUtils.closeQuietly(raf);
            closed = true;
        }
    }

    private InputStream getZipInputStream(ZipEntry ze) {
        if (ze.getMethod() == ZipEntry.STORED) {
            return new ZipFile.RAFStream(raf, ze.getDataOffset(),
                    ze.getDataOffset() + ze.getSize());
        } else {
            final ZipFile.RAFStream wrapped = new ZipFile.RAFStream(
                    raf, ze.getDataOffset(), ze.getDataOffset() + ze.getCompressedSize());

            int bufSize = Math.max(1024, (int) Math.min(ze.getSize(), 65535L));
            return new ZipFile.ZipInflaterInputStream(wrapped, new Inflater(true), bufSize, ze);
        }
    }

    static final class EntryIterator implements Iterator<ZipEntry> {
        private final long iterationHandle;
        private ZipEntry nextEntry;

        EntryIterator(long nativeHandle, String prefix) throws IOException {
            iterationHandle = nativeStartIteration(nativeHandle, prefix);
        }

        public ZipEntry next() {
            if (nextEntry != null) {
                final ZipEntry ze = nextEntry;
                nextEntry = null;
                return ze;
            }

            return nativeNextEntry(iterationHandle);
        }

        public boolean hasNext() {
            if (nextEntry != null) {
                return true;
            }

            final ZipEntry ze = nativeNextEntry(iterationHandle);
            if (ze == null) {
                return false;
            }

            nextEntry = ze;
            return true;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private HashMap<String, byte[]> getMetaEntries() throws IOException {
        HashMap<String, byte[]> metaEntries = new HashMap<String, byte[]>();

        Iterator<ZipEntry> entryIterator = new EntryIterator(nativeHandle, "META-INF/");
        while (entryIterator.hasNext()) {
            final ZipEntry entry = entryIterator.next();
            metaEntries.put(entry.getName(), Streams.readFully(getInputStream(entry)));
        }

        return metaEntries;
    }

    static final class JarFileInputStream extends FilterInputStream {
        private final StrictJarVerifier.VerifierEntry entry;

        private long count;
        private boolean done = false;

        JarFileInputStream(InputStream is, long size, StrictJarVerifier.VerifierEntry e) {
            super(is);
            entry = e;

            count = size;
        }

        @Override
        public int read() throws IOException {
            if (done) {
                return -1;
            }
            if (count > 0) {
                int r = super.read();
                if (r != -1) {
                    entry.write(r);
                    count--;
                } else {
                    count = 0;
                }
                if (count == 0) {
                    done = true;
                    entry.verify();
                }
                return r;
            } else {
                done = true;
                entry.verify();
                return -1;
            }
        }

        @Override
        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            if (done) {
                return -1;
            }
            if (count > 0) {
                int r = super.read(buffer, byteOffset, byteCount);
                if (r != -1) {
                    int size = r;
                    if (count < size) {
                        size = (int) count;
                    }
                    entry.write(buffer, byteOffset, size);
                    count -= size;
                } else {
                    count = 0;
                }
                if (count == 0) {
                    done = true;
                    entry.verify();
                }
                return r;
            } else {
                done = true;
                entry.verify();
                return -1;
            }
        }

        @Override
        public int available() throws IOException {
            if (done) {
                return 0;
            }
            return super.available();
        }

        @Override
        public long skip(long byteCount) throws IOException {
            return Streams.skipByReading(this, byteCount);
        }
    }

    private static native long nativeOpenJarFile(String fileName) throws IOException;
    private static native long nativeStartIteration(long nativeHandle, String prefix);
    private static native ZipEntry nativeNextEntry(long iterationHandle);
    private static native ZipEntry nativeFindEntry(long nativeHandle, String entryName);
    private static native void nativeClose(long nativeHandle);
}
