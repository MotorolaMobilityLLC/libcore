/*
 * Copyright (C) 2008 The Android Open Source Project
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

package org.apache.harmony.xnet.provider.jsse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.openssl.PEMWriter;

/**
 * Provides the Java side of our JNI glue for OpenSSL. Currently only
 * hashing and verifying are covered. Is expected to grow over
 * time. Also needs to move into libcore/openssl at some point.
 */
public class NativeCrypto {

    // --- OpenSSL library initialization --------------------------------------
    static {
        clinit();
    }

    private native static void clinit();

    // --- DSA/RSA public/private key handling functions -----------------------

    public static native int EVP_PKEY_new_DSA(byte[] p, byte[] q, byte[] g, byte[] priv_key, byte[] pub_key);

    public static native int EVP_PKEY_new_RSA(byte[] n, byte[] e, byte[] d, byte[] p, byte[] q);

    public static native void EVP_PKEY_free(int pkey);

    // --- General context handling functions (despite the names) --------------

    public static native int EVP_new();

    public static native void EVP_free(int ctx);

    // --- Digest handling functions -------------------------------------------

    public static native void EVP_DigestInit(int ctx, String algorithm);

    public static native void EVP_DigestUpdate(int ctx, byte[] buffer, int offset, int length);

    public static native int EVP_DigestFinal(int ctx, byte[] hash, int offset);

    public static native int EVP_DigestSize(int ctx);

    public static native int EVP_DigestBlockSize(int ctx);

    // --- Signature handling functions ----------------------------------------

    public static native void EVP_VerifyInit(int ctx, String algorithm);

    public static native void EVP_VerifyUpdate(int ctx, byte[] buffer, int offset, int length);

    public static native int EVP_VerifyFinal(int ctx, byte[] signature, int offset, int length, int key);

    // --- Legacy Signature handling -------------------------------------------
    // TODO rewrite/replace with EVP_Verify*
    /**
     * Verifies an RSA signature. Conceptually, this method doesn't really
     * belong here, but due to its native code being closely tied to OpenSSL
     * (just like the rest of this class), we put it here for the time being.
     * This also solves potential problems with native library initialization.
     *
     * @param message The message to verify
     * @param signature The signature to verify
     * @param algorithm The hash/sign algorithm to use, i.e. "RSA-SHA1"
     * @param key The RSA public key to use
     * @return true if the verification succeeds, false otherwise
     */
    public static boolean verifySignature(byte[] message, byte[] signature, String algorithm, RSAPublicKey key) {
        byte[] modulus = key.getModulus().toByteArray();
        byte[] exponent = key.getPublicExponent().toByteArray();

        return verifySignature(message, signature, algorithm, modulus, exponent) == 1;
    }

    private static native int verifySignature(byte[] message, byte[] signature,
            String algorithm, byte[] modulus, byte[] exponent);

    // --- SSL handling --------------------------------------------------------

    private static final String SUPPORTED_PROTOCOL_SSLV3 = "SSLv3";
    private static final String SUPPORTED_PROTOCOL_TLSV1 = "TLSv1";

    public static final Map<String, String> OPENSSL_TO_STANDARD = new HashMap<String, String>();
    public static final Map<String, String> STANDARD_TO_OPENSSL = new LinkedHashMap<String, String>();

    private static void add(String standard, String openssl) {
        OPENSSL_TO_STANDARD.put(openssl, standard);
        STANDARD_TO_OPENSSL.put(standard, openssl);
    }

    static {
        // Note these are added in priority order
        // Android doesn't currently support Elliptic Curve or Diffie-Hellman
        add("SSL_RSA_WITH_RC4_128_MD5",              "RC4-MD5");
        add("SSL_RSA_WITH_RC4_128_SHA",              "RC4-SHA");
        add("TLS_RSA_WITH_AES_128_CBC_SHA",          "AES128-SHA");
        add("TLS_RSA_WITH_AES_256_CBC_SHA",          "AES256-SHA");
        // add("TLS_ECDH_ECDSA_WITH_RC4_128_SHA",       "ECDH-ECDSA-RC4-SHA");
        // add("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",   "ECDH-ECDSA-AES128-SHA");
        // add("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA",   "ECDH-ECDSA-AES256-SHA");
        // add("TLS_ECDH_RSA_WITH_RC4_128_SHA",         "ECDH-RSA-RC4-SHA");
        // add("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA",     "ECDH-RSA-AES128-SHA");
        // add("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA",     "ECDH-RSA-AES256-SHA");
        // add("TLS_ECDHE_ECDSA_WITH_RC4_128_SHA",      "ECDHE-ECDSA-RC4-SHA");
        // add("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",  "ECDHE-ECDSA-AES128-SHA");
        // add("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",  "ECDHE-ECDSA-AES256-SHA");
        // add("TLS_ECDHE_RSA_WITH_RC4_128_SHA",        "ECDHE-RSA-RC4-SHA");
        // add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",    "ECDHE-RSA-AES128-SHA");
        // add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",    "ECDHE-RSA-AES256-SHA");
        // add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA",      "DHE-RSA-AES128-SHA");
        // add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA",      "DHE-RSA-AES256-SHA");
        // add("TLS_DHE_DSS_WITH_AES_128_CBC_SHA",      "DHE-DSS-AES128-SHA");
        // add("TLS_DHE_DSS_WITH_AES_256_CBC_SHA",      "DHE-DSS-AES256-SHA");
        add("SSL_RSA_WITH_3DES_EDE_CBC_SHA",         "DES-CBC3-SHA");
        // add("TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA",  "ECDH-ECDSA-DES-CBC3-SHA");
        // add("TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA",    "ECDH-RSA-DES-CBC3-SHA");
        // add("TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", "ECDHE-ECDSA-DES-CBC3-SHA");
        // add("TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",   "ECDHE-RSA-DES-CBC3-SHA");
        // add("SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",     "EDH-RSA-DES-CBC3-SHA");
        // add("SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",     "EDH-DSS-DES-CBC3-SHA");
        add("SSL_RSA_WITH_DES_CBC_SHA",              "DES-CBC-SHA");
        // add("SSL_DHE_RSA_WITH_DES_CBC_SHA",          "EDH-RSA-DES-CBC-SHA");
        // add("SSL_DHE_DSS_WITH_DES_CBC_SHA",          "EDH-DSS-DES-CBC-SHA");
        add("SSL_RSA_EXPORT_WITH_RC4_40_MD5",        "EXP-RC4-MD5");
        add("SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",     "EXP-DES-CBC-SHA");
        // add("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", "EXP-EDH-RSA-DES-CBC-SHA");
        // add("SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", "EXP-EDH-DSS-DES-CBC-SHA");
        add("SSL_RSA_WITH_NULL_MD5",                 "NULL-MD5");
        add("SSL_RSA_WITH_NULL_SHA",                 "NULL-SHA");
        // add("TLS_ECDH_ECDSA_WITH_NULL_SHA",          "ECDH-ECDSA-NULL-SHA");
        // add("TLS_ECDH_RSA_WITH_NULL_SHA",            "ECDH-RSA-NULL-SHA");
        // add("TLS_ECDHE_ECDSA_WITH_NULL_SHA",         "ECDHE-ECDSA-NULL-SHA");
        // add("TLS_ECDHE_RSA_WITH_NULL_SHA",           "ECDHE-RSA-NULL-SHA");
        // add("SSL_DH_anon_WITH_RC4_128_MD5",          "ADH-RC4-MD5");
        // add("TLS_DH_anon_WITH_AES_128_CBC_SHA",      "ADH-AES128-SHA");
        // add("TLS_DH_anon_WITH_AES_256_CBC_SHA",      "ADH-AES256-SHA");
        // add("SSL_DH_anon_WITH_3DES_EDE_CBC_SHA",     "ADH-DES-CBC3-SHA");
        // add("SSL_DH_anon_WITH_DES_CBC_SHA",          "ADH-DES-CBC-SHA");
        // add("TLS_ECDH_anon_WITH_RC4_128_SHA",        "AECDH-RC4-SHA");
        // add("TLS_ECDH_anon_WITH_AES_128_CBC_SHA",    "AECDH-AES128-SHA");
        // add("TLS_ECDH_anon_WITH_AES_256_CBC_SHA",    "AECDH-AES256-SHA");
        // add("TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA",   "AECDH-DES-CBC3-SHA");
        // add("SSL_DH_anon_EXPORT_WITH_RC4_40_MD5",    "EXP-ADH-RC4-MD5");
        // add("SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", "EXP-ADH-DES-CBC-SHA");
        // add("TLS_ECDH_anon_WITH_NULL_SHA",           "AECDH-NULL-SHA");

        // No Kerberos in Android
        // add("TLS_KRB5_WITH_RC4_128_SHA",           "KRB5-RC4-SHA");
        // add("TLS_KRB5_WITH_RC4_128_MD5",           "KRB5-RC4-MD5");
        // add("TLS_KRB5_WITH_3DES_EDE_CBC_SHA",      "KRB5-DES-CBC3-SHA");
        // add("TLS_KRB5_WITH_3DES_EDE_CBC_MD5",      "KRB5-DES-CBC3-MD5");
        // add("TLS_KRB5_WITH_DES_CBC_SHA",           "KRB5-DES-CBC-SHA");
        // add("TLS_KRB5_WITH_DES_CBC_MD5",           "KRB5-DES-CBC-MD5");
        // add("TLS_KRB5_EXPORT_WITH_RC4_40_SHA",     "EXP-KRB5-RC4-SHA");
        // add("TLS_KRB5_EXPORT_WITH_RC4_40_MD5",     "EXP-KRB5-RC4-MD5");
        // add("TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", "EXP-KRB5-DES-CBC-SHA");
        // add("TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", "EXP-KRB5-DES-CBC-MD5");

        // not implemented by either RI or OpenSSL
        // add("SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA", null);
        // add("SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA", null);

        // EXPORT1024 suites were never standardized but were widely implemented.
        // OpenSSL 0.9.8c and later have disabled TLS1_ALLOW_EXPERIMENTAL_CIPHERSUITES
        // add("SSL_RSA_EXPORT1024_WITH_DES_CBC_SHA", "EXP1024-DES-CBC-SHA");
        // add("SSL_RSA_EXPORT1024_WITH_RC4_56_SHA",  "EXP1024-RC4-SHA");

        // No RC2
        // add("SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5",  "EXP-RC2-CBC-MD5");
        // add("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_SHA", "EXP-KRB5-RC2-CBC-SHA");
        // add("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_MD5", "EXP-KRB5-RC2-CBC-MD5");

        // PSK is Private Shared Key - didn't exist in Froyo's openssl - no JSSE equivalent
        // add(null, "PSK-3DES-EDE-CBC-SHA");
        // add(null, "PSK-AES128-CBC-SHA");
        // add(null, "PSK-AES256-CBC-SHA");
        // add(null, "PSK-RC4-SHA");

    }

    private static final String[] SUPPORTED_CIPHER_SUITES
        = STANDARD_TO_OPENSSL.keySet().toArray(new String[0]);

    // SSL mode
    public static long SSL_MODE_HANDSHAKE_CUTTHROUGH = 0x00000040L;

    // SSL options
    public static long SSL_OP_NO_SSLv3 = 0x02000000L;
    public static long SSL_OP_NO_TLSv1 = 0x04000000L;

    public static native int SSL_CTX_new();

    public static String[] getDefaultCipherSuites() {
        return new String[] {
            // Android doesn't currently support Elliptic Curve or Diffie-Hellman
            "SSL_RSA_WITH_RC4_128_MD5",
            "SSL_RSA_WITH_RC4_128_SHA",
            "TLS_RSA_WITH_AES_128_CBC_SHA",
            // "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
            // "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
            "SSL_RSA_WITH_3DES_EDE_CBC_SHA",
            // "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
            // "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
            "SSL_RSA_WITH_DES_CBC_SHA",
            // "SSL_DHE_RSA_WITH_DES_CBC_SHA",
            // "SSL_DHE_DSS_WITH_DES_CBC_SHA",
            "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
            "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
            // "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
            // "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA"
        };
    }

    public static String[] getSupportedCipherSuites() {
        return SUPPORTED_CIPHER_SUITES.clone();
    }

    public static native void SSL_CTX_free(int ssl_ctx);

    public static native int SSL_new(int ssl_ctx, String privatekey, String certificate, byte[] seed) throws IOException;

    /**
     * Initialize the SSL socket and set the certificates for the
     * future handshaking.
     */
    public static int SSL_new(SSLParameters sslParameters) throws IOException {
        boolean client = sslParameters.getUseClientMode();

        final int ssl_ctx = (client) ?
            sslParameters.getClientSessionContext().sslCtxNativePointer :
            sslParameters.getServerSessionContext().sslCtxNativePointer;

        // TODO support more than RSA certificates?  non-openssl
        // SSLEngine implementation did these callbacks during
        // handshake after selecting cipher suite, not before
        // handshake. Should do the same via SSL_CTX_set_client_cert_cb
        final String alias = (client) ?
            sslParameters.getKeyManager().chooseClientAlias(new String[] { "RSA" }, null, null) :
            sslParameters.getKeyManager().chooseServerAlias("RSA", null, null);

        final String privateKeyString;
        final String certificateString;
        if (alias == null) {
            privateKeyString = null;
            certificateString = null;
        } else {
            PrivateKey privateKey = sslParameters.getKeyManager().getPrivateKey(alias);
            X509Certificate[] certificates = sslParameters.getKeyManager().getCertificateChain(alias);

            ByteArrayOutputStream privateKeyOS = new ByteArrayOutputStream();
            PEMWriter privateKeyPEMWriter = new PEMWriter(new OutputStreamWriter(privateKeyOS));
            privateKeyPEMWriter.writeObject(privateKey);
            privateKeyPEMWriter.close();
            privateKeyString = privateKeyOS.toString();

            ByteArrayOutputStream certificateOS = new ByteArrayOutputStream();
            PEMWriter certificateWriter = new PEMWriter(new OutputStreamWriter(certificateOS));

            for (X509Certificate certificate : certificates) {
                certificateWriter.writeObject(certificate);
            }
            certificateWriter.close();
            certificateString = certificateOS.toString();
        }

        final byte[] seed = (sslParameters.getSecureRandomMember() != null) ?
            sslParameters.getSecureRandomMember().generateSeed(1024) :
            null;

        return SSL_new(ssl_ctx,
                       privateKeyString,
                       certificateString,
                       seed);
    }


    public static native long SSL_get_mode(int ssl);

    public static native long SSL_set_mode(int ssl, long mode);

    public static native long SSL_clear_mode(int ssl, long mode);

    public static native long SSL_get_options(int ssl);

    public static native long SSL_set_options(int ssl, long options);

    public static native long SSL_clear_options(int ssl, long options);

    public static String[] getSupportedProtocols() {
        return new String[] { SUPPORTED_PROTOCOL_SSLV3, SUPPORTED_PROTOCOL_TLSV1 };
    }

    public static String[] getEnabledProtocols(int ssl) {
        long options = SSL_get_options(ssl);
        ArrayList<String> array = new ArrayList<String>();
        if ((options & NativeCrypto.SSL_OP_NO_SSLv3) == 0) {
            array.add(SUPPORTED_PROTOCOL_SSLV3);
        }
        if ((options & NativeCrypto.SSL_OP_NO_TLSv1) == 0) {
            array.add(SUPPORTED_PROTOCOL_TLSV1);
        }
        return array.toArray(new String[array.size()]);
    }

    public static void setEnabledProtocols(int ssl, String[] protocols) {
        if (protocols == null) {
            throw new IllegalArgumentException("protocols == null");
        }

        // openssl uses negative logic letting you disable protocols.
        // so first, assume we need to set all (disable all ) and clear none (enable none).
        // in the loop, selectively move bits from set to clear (from disable to enable)
        long optionsToSet = (SSL_OP_NO_SSLv3 | SSL_OP_NO_TLSv1);
        long optionsToClear = 0;
        for (int i = 0; i < protocols.length; i++) {
            String protocol = protocols[i];
            if (protocol == null) {
                throw new IllegalArgumentException("protocols[" + i + "] == null");
            }
            if (protocol.equals(SUPPORTED_PROTOCOL_SSLV3)) {
                optionsToSet &= ~SSL_OP_NO_SSLv3;
                optionsToClear |= SSL_OP_NO_SSLv3;
            } else if (protocol.equals(SUPPORTED_PROTOCOL_TLSV1)) {
                optionsToSet &= ~SSL_OP_NO_TLSv1;
                optionsToClear |= SSL_OP_NO_TLSv1;
            } else {
                throw new IllegalArgumentException("protocol " + protocol +
                                                   " is not supported");
            }
        }

        SSL_set_options(ssl, optionsToSet);
        SSL_clear_options(ssl, optionsToClear);
    }

    public static String[] checkEnabledProtocols(String[] protocols) {
        if (protocols == null) {
            throw new IllegalArgumentException("protocols parameter is null");
        }
        for (int i = 0; i < protocols.length; i++) {
            String protocol = protocols[i];
            if (protocol == null) {
                throw new IllegalArgumentException("protocols[" + i + "] == null");
            }
            if ((!protocol.equals(SUPPORTED_PROTOCOL_SSLV3))
                && (!protocol.equals(SUPPORTED_PROTOCOL_TLSV1))) {
                throw new IllegalArgumentException("protocol " + protocol +
                                                   " is not supported");
            }
        }
        return protocols;
    }

    public static native void SSL_set_cipher_lists(int ssl, String[] ciphers);

    public static void setEnabledCipherSuites(int ssl, String[] cipherSuites) {
        checkEnabledCipherSuites(cipherSuites);
        List<String> opensslSuites = new ArrayList<String>();
        for (int i = 0; i < cipherSuites.length; i++) {
            String cipherSuite = cipherSuites[i];
            String openssl = STANDARD_TO_OPENSSL.get(cipherSuite);
            String cs = (openssl == null) ? cipherSuite : openssl;
            opensslSuites.add(cs);
        }
        SSL_set_cipher_lists(ssl, opensslSuites.toArray(new String[opensslSuites.size()]));
    }

    public static String[] checkEnabledCipherSuites(String[] cipherSuites) {
        if (cipherSuites == null) {
            throw new IllegalArgumentException("cipherSuites == null");
        }
        // makes sure all suites are valid, throwing on error
        for (int i = 0; i < cipherSuites.length; i++) {
            String cipherSuite = cipherSuites[i];
            if (cipherSuite == null) {
                throw new IllegalArgumentException("cipherSuites[" + i + "] == null");
            }
            if (STANDARD_TO_OPENSSL.containsKey(cipherSuite)) {
                continue;
            }
            if (OPENSSL_TO_STANDARD.containsKey(cipherSuite)) {
                // TODO log warning about using backward compatability
                continue;
            }
            throw new IllegalArgumentException("cipherSuite " + cipherSuite + " is not supported.");
        }
        return cipherSuites;
    }

    /*
     * See the OpenSSL ssl.h header file for more information.
     */
    public static final int SSL_VERIFY_NONE =                 0x00;
    public static final int SSL_VERIFY_PEER =                 0x01;
    public static final int SSL_VERIFY_FAIL_IF_NO_PEER_CERT = 0x02;
    public static final int SSL_VERIFY_CLIENT_ONCE =          0x04;

    public static native void SSL_set_verify(int sslNativePointer, int mode) throws IOException;

    public static native void SSL_set_session(int sslNativePointer, int sslSessionNativePointer) throws IOException;

    public static native void SSL_set_session_creation_enabled(int sslNativePointer, boolean creationEnabled) throws IOException;

    /**
     * Returns the sslSessionNativePointer of the negotiated session
     */
    public static native int SSL_do_handshake(int sslNativePointer, Socket sock,
                                              CertificateChainVerifier ccv, HandshakeCompletedCallback hcc,
                                              int timeout, boolean client_mode) throws IOException, CertificateException;

    public static native byte[][] SSL_get_certificate(int sslNativePointer);

    /**
     * Reads with the native SSL_read function from the encrypted data stream
     * @return -1 if error or the end of the stream is reached.
     */
    public static native int SSL_read_byte(int sslNativePointer, int timeout) throws IOException;
    public static native int SSL_read(int sslNativePointer, byte[] b, int off, int len, int timeout) throws IOException;

    /**
     * Writes with the native SSL_write function to the encrypted data stream.
     */
    public static native void SSL_write_byte(int sslNativePointer, int b) throws IOException;
    public static native void SSL_write(int sslNativePointer, byte[] b, int off, int len) throws IOException;

    public static native void SSL_interrupt(int sslNativePointer) throws IOException;
    public static native void SSL_shutdown(int sslNativePointer) throws IOException;

    public static native void SSL_free(int sslNativePointer);

    public static native byte[] SSL_SESSION_session_id(int sslSessionNativePointer);

    /**
     * Returns the X509 certificates of the peer in the PEM format.
     */
    public static native byte[][] SSL_SESSION_get_peer_cert_chain(int sslCtxNativePointer,
                                                                  int sslSessionNativePointer);

    public static native long SSL_SESSION_get_time(int sslSessionNativePointer);

    public static native String SSL_SESSION_get_version(int sslSessionNativePointer);

    public static native String SSL_SESSION_cipher(int sslSessionNativePointer);

    public static native void SSL_SESSION_free(int sslSessionNativePointer);

    public static native byte[] i2d_SSL_SESSION(int sslSessionNativePointer);

    public static native int d2i_SSL_SESSION(byte[] data, int size);

    public interface CertificateChainVerifier {
        /**
         * Verify that we trust the certificate chain is trusted.
         *
         * @param bytes An array of certficates in PEM encode bytes
         * @param authMethod auth algorithm name
         *
         * @throws CertificateException if the certificate is untrusted
         */
        public void verifyCertificateChain(byte[][] bytes, String authMethod) throws CertificateException;
    }

    public interface HandshakeCompletedCallback {
        /**
         * Called when SSL handshake is completed. Note that this can
         * be after SSL_do_handshake returns when handshake cutthrough
         * is enabled.
         */
        public void handshakeCompleted();
    }
}
