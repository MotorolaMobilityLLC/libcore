/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.security.cert;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Set;

import org.apache.harmony.security.internal.nls.Messages;

/**
 * The parameter specification for a PKIX {@code CertPathBuilder}
 * algorithm used to {@link CertPathBuilder#build(CertPathParameters) build} 
 * certificate chains validated with the PKIX certification path validation. 
 * <p>
 * The parameters must be created with <i>trusted</i> certificate authorities
 * and constraints for the target certificates.
 * </p>
 * 
 * @see CertPathBuilder
 * @see CertPathParameters
 * @since Android 1.0
 */
public class PKIXBuilderParameters extends PKIXParameters {
    // Maximum certificate path length (5 by default)
    private int maxPathLength = 5;

    /**
     * Creates a new {@code PKIXBuilderParameters} instance with the specified
     * set of {@code TrustAnchor} and certificate constraints.
     * 
     * @param trustAnchors
     *            the set of {@code TrustAnchors}.
     * @param targetConstraints
     *            the certificate constraints.
     * @throws InvalidAlgorithmParameterException
     *             if {@code trustAnchors} is empty.
     * @throws ClassCastException
     *             if one of the items in {@code trustAnchors} is not an
     *             instance of {@code java.security.cert.TrustAnchor}.
     * @since Android 1.0
     */
    public PKIXBuilderParameters(Set<TrustAnchor> trustAnchors,
            CertSelector targetConstraints)
        throws InvalidAlgorithmParameterException {
        super(trustAnchors);
        super.setTargetCertConstraints(targetConstraints);
    }

    /**
     * Creates a new {@code PKIXBuilderParameters} instance with the trusted
     * {@code X509Certificate} entries from the specified {@code KeyStore}.
     * 
     * @param keyStore
     *            the key store containing trusted certificates.
     * @param targetConstraints
     *            the certificate constraints.
     * @throws KeyStoreException
     *             if the {@code keyStore} is not initialized.
     * @throws InvalidAlgorithmParameterException
     *             if {@code keyStore} does not contained any trusted
     *             certificate entry.
     * @since Android 1.0
     */
    public PKIXBuilderParameters(KeyStore keyStore,
            CertSelector targetConstraints)
        throws KeyStoreException,
               InvalidAlgorithmParameterException {
        super(keyStore);
        super.setTargetCertConstraints(targetConstraints);
    }

    /**
     * Returns the maximum length of a certification path.
     * <p>
     * This is the maximum number of non-self-signed certificates in a
     * certification path.
     * </p>
     * 
     * @return the maximum length of a certification path, or {@code -1} if it
     *         is unlimited.
     * @since Android 1.0
     */
    public int getMaxPathLength() {
        return maxPathLength;
    }

    /**
     * Set the maximum length of a certification path.
     * <p>
     * This is the maximum number of non-self-signed certificates in a
     * certification path.
     * </p>
     * 
     * @param maxPathLength
     *            the maximum length of a certification path.
     * @throws InvalidParameterException
     *             if {@code maxPathLength} is less than {@code -1}.
     * @since Android 1.0
     */
    public void setMaxPathLength(int maxPathLength) {
        if (maxPathLength < -1) {
            throw new InvalidParameterException(
                    Messages.getString("security.5B")); //$NON-NLS-1$
        }
        this.maxPathLength = maxPathLength;
    }

    /**
     * Returns a string representation of this {@code PKIXBuilderParameters}
     * instance.
     * 
     * @return a string representation of this {@code PKIXBuilderParameters}
     *         instance.
     * @since Android 1.0
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("[\n"); //$NON-NLS-1$
        sb.append(super.toString());
        sb.append(" Max Path Length: "); //$NON-NLS-1$
        sb.append(maxPathLength);
        sb.append("\n]"); //$NON-NLS-1$
        return sb.toString();
    }
}
