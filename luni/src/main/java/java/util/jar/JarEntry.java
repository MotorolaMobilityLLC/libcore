/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.util.jar;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.security.CodeSigner;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

/**
 * Represents a single file in a JAR archive together with the manifest
 * attributes and digital signatures associated with it.
 *
 * @see JarFile
 * @see JarInputStream
 */
public class JarEntry extends ZipEntry {
    private Attributes attributes;

    final JarFile parentJar;

    CodeSigner signers[];

    // Cached factory used to build CertPath-s in <code>getCodeSigners()</code>.
    private CertificateFactory factory;

    private boolean isFactoryChecked = false;

    /**
     * Creates a new {@code JarEntry} named name.
     *
     * @param name
     *            The name of the new {@code JarEntry}.
     */
    public JarEntry(String name) {
        super(name);
        parentJar = null;
    }

    /**
     * Creates a new {@code JarEntry} using the values obtained from entry.
     *
     * @param entry
     *            The ZipEntry to obtain values from.
     */
    public JarEntry(ZipEntry entry) {
        this(entry, null);
    }

    JarEntry(ZipEntry entry, JarFile parentJar) {
        super(entry);
        this.parentJar = parentJar;
    }

    /**
     * Create a new {@code JarEntry} using the values obtained from the
     * argument.
     *
     * @param je
     *            The {@code JarEntry} to obtain values from.
     */
    public JarEntry(JarEntry je) {
        super(je);
        parentJar = je.parentJar;
        attributes = je.attributes;
        signers = je.signers;
    }


    /**
     * Returns the {@code Attributes} object associated with this entry or
     * {@code null} if none exists.
     *
     * @return the {@code Attributes} for this entry.
     * @throws IOException
     *                If an error occurs obtaining the {@code Attributes}.
     * @see Attributes
     */
    public Attributes getAttributes() throws IOException {
        if (attributes != null || parentJar == null) {
            return attributes;
        }
        Manifest manifest = parentJar.getManifest();
        if (manifest == null) {
            return null;
        }
        return attributes = manifest.getAttributes(getName());
    }

    /**
     * Returns an array of {@code Certificate} Objects associated with this
     * entry or {@code null} if none exists. Make sure that the everything is
     * read from the input stream before calling this method, or else the method
     * returns {@code null}.
     * <p>
     * This method returns all the signers' unverified chains concatenated
     * together in one array. To know which certificates were tied to the
     * private keys that made the signatures on this entry, see
     * {@link #getCodeSigners()} instead.
     *
     * @see java.security.cert.Certificate
     */
    public Certificate[] getCertificates() {
        if (parentJar == null) {
            return null;
        }
        JarVerifier jarVerifier = parentJar.verifier;
        if (jarVerifier == null) {
            return null;
        }
        return jarVerifier.getCertificates(getName());
    }

    void setAttributes(Attributes attrib) {
        attributes = attrib;
    }

    /**
     * Returns the code signers for the digital signatures associated with the
     * JAR file. If there is no such code signer, it returns {@code null}. Make
     * sure that the everything is read from the input stream before calling
     * this method, or else the method returns {@code null}.
     * <p>
     * Only the digital signature on the entry is cryptographically verified.
     * None of the certificates in the the {@link CertPath} returned from
     * {@link CodeSigner#getSignerCertPath()} are verified and must be verified
     * by the caller if needed. See {@link CertPathValidator} for more
     * information.
     *
     * @return an array of CodeSigner for this JAR entry.
     * @see CodeSigner
     */
    public CodeSigner[] getCodeSigners() {
        if (signers == null) {
            signers = getCodeSigners(getCertificates());
        }
        if (signers == null) {
            return null;
        }

        CodeSigner[] tmp = new CodeSigner[signers.length];
        System.arraycopy(signers, 0, tmp, 0, tmp.length);
        return tmp;
    }

    private CodeSigner[] getCodeSigners(Certificate[] certs) {
        if (certs == null) {
            return null;
        }

        X500Principal prevIssuer = null;
        ArrayList<Certificate> list = new ArrayList<Certificate>(certs.length);
        ArrayList<CodeSigner> asigners = new ArrayList<CodeSigner>();

        for (Certificate element : certs) {
            if (!(element instanceof X509Certificate)) {
                // Only X509Certificate-s are taken into account - see API spec.
                continue;
            }
            X509Certificate x509 = (X509Certificate) element;
            if (prevIssuer != null) {
                X500Principal subj = x509.getSubjectX500Principal();
                if (!prevIssuer.equals(subj)) {
                    // Ok, this ends the previous chain,
                    // so transform this one into CertPath ...
                    addCodeSigner(asigners, list);
                    // ... and start a new one
                    list.clear();
                }// else { it's still the same chain }

            }
            prevIssuer = x509.getIssuerX500Principal();
            list.add(x509);
        }
        if (!list.isEmpty()) {
            addCodeSigner(asigners, list);
        }
        if (asigners.isEmpty()) {
            // 'signers' is 'null' already
            return null;
        }

        CodeSigner[] tmp = new CodeSigner[asigners.size()];
        asigners.toArray(tmp);
        return tmp;

    }

    private void addCodeSigner(ArrayList<CodeSigner> asigners,
            List<Certificate> list) {
        CertPath certPath = null;
        if (!isFactoryChecked) {
            try {
                factory = CertificateFactory.getInstance("X.509");
            } catch (CertificateException ex) {
                // do nothing
            } finally {
                isFactoryChecked = true;
            }
        }
        if (factory == null) {
            return;
        }
        try {
            certPath = factory.generateCertPath(list);
        } catch (CertificateException ex) {
            // do nothing
        }
        if (certPath != null) {
            asigners.add(new CodeSigner(certPath, null));
        }
    }
}
