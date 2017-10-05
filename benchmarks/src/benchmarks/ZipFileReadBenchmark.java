/*
 * Copyright (C) 2017 The Android Open Source Project
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
 * limitations under the License
 */

package benchmarks;

import com.google.caliper.BeforeExperiment;
import com.google.caliper.Param;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public class ZipFileReadBenchmark {
    private File file;
    @Param({"1024", "16384", "65536"}) int readBufferSize;

    @BeforeExperiment
    protected void setUp() throws Exception {
        System.setProperty("java.io.tmpdir", "/data/local/tmp");
        file = File.createTempFile(getClass().getName(), ".zip");
        writeEntries(new ZipOutputStream(new FileOutputStream(file)), 2, 1024*1024);
        ZipFile zipFile = new ZipFile(file);
        for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
            ZipEntry zipEntry = e.nextElement();
        }
        zipFile.close();
    }

    /**
     * Compresses the given number of files, each of the given size, into a .zip archive.
     */
    protected void writeEntries(ZipOutputStream out, int entryCount, long entrySize)
            throws IOException {
        byte[] writeBuffer = new byte[8192];
        Random random = new Random();
        try {
            for (int entry = 0; entry < entryCount; ++entry) {
                ZipEntry ze = new ZipEntry(Integer.toHexString(entry));
                ze.setSize(entrySize);
                out.putNextEntry(ze);

                for (long i = 0; i < entrySize; i += writeBuffer.length) {
                    random.nextBytes(writeBuffer);
                    int byteCount = (int) Math.min(writeBuffer.length, entrySize - i);
                    out.write(writeBuffer, 0, byteCount);
                }

                out.closeEntry();
            }
        } finally {
            out.close();
        }
    }

    public void timeZipFileRead(int reps) throws Exception {
        byte readBuffer[] = new byte[readBufferSize];
        for (int i = 0; i < reps; ++i) {
            ZipFile zipFile = new ZipFile(file);
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); ) {
                ZipEntry zipEntry = e.nextElement();
                InputStream is = zipFile.getInputStream(zipEntry);
                while (true) {
                    if (is.read(readBuffer, 0, readBuffer.length) < 0) {
                        break;
                    }
                }
            }
            zipFile.close();
        }
    }
}
