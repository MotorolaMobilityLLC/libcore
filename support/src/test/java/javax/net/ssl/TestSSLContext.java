/*
 * Copyright (C) 2010 The Android Open Source Project
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

package javax.net.ssl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.StandardNames;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import junit.framework.Assert;

/**
 * TestSSLContext is a convenience class for other tests that
 * want a canned SSLContext and related state for testing so they
 * don't have to duplicate the logic.
 */
public final class TestSSLContext extends Assert {

    /*
     * The RI and Android have very different default SSLSession cache behaviors.
     * The RI keeps an unlimited number of SSLSesions around for 1 day.
     * Android keeps 10 SSLSessions forever.
     */
    private static final boolean IS_RI = StandardNames.IS_RI;
    public static final int EXPECTED_DEFAULT_CLIENT_SSL_SESSION_CACHE_SIZE = (IS_RI) ? 0 : 10;
    public static final int EXPECTED_DEFAULT_SERVER_SSL_SESSION_CACHE_SIZE = (IS_RI) ? 0 : 100;
    public static final int EXPECTED_DEFAULT_SSL_SESSION_CACHE_TIMEOUT = (IS_RI) ? 86400 : 0;

    /**
     * The Android SSLSocket and SSLServerSocket implementations are
     * based on a version of OpenSSL which includes support for RFC
     * 4507 session tickets. When using session tickets, the server
     * does not need to keep a cache mapping session IDs to SSL
     * sessions for reuse. Instead, the client presents the server
     * with a session ticket it received from the server earlier,
     * which is an SSL session encrypted by the server's secret
     * key. Since in this case the server does not need to keep a
     * cache, some tests may find different results depending on
     * whether or not the session tickets are in use. These tests can
     * use this function to determine if loopback SSL connections are
     * expected to use session tickets and conditionalize their
     * results appropriately.
     */
    public static boolean sslServerSocketSupportsSessionTickets () {
        // Disabled session tickets for better compatability b/2682876
        // return !IS_RI;
        return false;
    }

    public final KeyStore clientKeyStore;
    public final char[] clientKeyStorePassword;
    public final KeyStore serverKeyStore;
    public final char[] serverKeyStorePassword;
    public final X509ExtendedKeyManager clientKeyManager;
    public final X509ExtendedKeyManager serverKeyManager;
    public final X509TrustManager clientTrustManager;
    public final X509TrustManager serverTrustManager;
    public final SSLContext clientContext;
    public final SSLContext serverContext;
    public final SSLServerSocket serverSocket;
    public final InetAddress host;
    public final int port;

    private TestSSLContext(KeyStore clientKeyStore,
                           char[] clientKeyStorePassword,
                           KeyStore serverKeyStore,
                           char[] serverKeyStorePassword,
                           X509ExtendedKeyManager clientKeyManager,
                           X509ExtendedKeyManager serverKeyManager,
                           X509TrustManager clientTrustManager,
                           X509TrustManager serverTrustManager,
                           SSLContext clientContext,
                           SSLContext serverContext,
                           SSLServerSocket serverSocket,
                           InetAddress host,
                           int port) {
        this.clientKeyStore = clientKeyStore;
        this.clientKeyStorePassword = clientKeyStorePassword;
        this.serverKeyStore = serverKeyStore;
        this.serverKeyStorePassword = serverKeyStorePassword;
        this.clientKeyManager = clientKeyManager;
        this.serverKeyManager = serverKeyManager;
        this.clientTrustManager = clientTrustManager;
        this.serverTrustManager = serverTrustManager;
        this.clientContext = clientContext;
        this.serverContext = serverContext;
        this.serverSocket = serverSocket;
        this.host = host;
        this.port = port;
    }

    /**
     * Usual TestSSLContext creation method, creates underlying
     * SSLContext with certificate and key as well as SSLServerSocket
     * listening provided host and port.
     */
    public static TestSSLContext create() {
        return create(TestKeyStore.getClient(),
                      TestKeyStore.getServer());
    }

    /**
     * TestSSLContext creation method that allows separate creation of server key store
     */
    public static TestSSLContext create(TestKeyStore client, TestKeyStore server) {
        return create(client.keyStore, client.keyStorePassword,
                      server.keyStore, server.keyStorePassword);
    }

    /**
     * TestSSLContext creation method that allows separate creation of client and server key store
     */
    public static TestSSLContext create(KeyStore clientKeyStore, char[] clientKeyStorePassword,
                                        KeyStore serverKeyStore, char[] serverKeyStorePassword) {
        try {
            KeyManager[] clientKeyManagers = createKeyManagers(clientKeyStore,
                                                               clientKeyStorePassword);
            KeyManager[] serverKeyManagers = createKeyManagers(serverKeyStore,
                                                               serverKeyStorePassword);

            TrustManager[] clientTrustManagers = createTrustManagers(clientKeyStore,
                                                                     clientKeyStorePassword);
            TrustManager[] serverTrustManagers = createTrustManagers(serverKeyStore,
                                                                     serverKeyStorePassword);

            SSLContext clientContext = createSSLContext(clientKeyManagers, clientTrustManagers);
            SSLContext serverContext = createSSLContext(serverKeyManagers, serverTrustManagers);

            SSLServerSocket serverSocket = (SSLServerSocket)
                serverContext.getServerSocketFactory().createServerSocket(0);
            InetSocketAddress sa = (InetSocketAddress) serverSocket.getLocalSocketAddress();
            InetAddress host = sa.getAddress();
            int port = sa.getPort();

            return new TestSSLContext(clientKeyStore, clientKeyStorePassword,
                                      serverKeyStore, serverKeyStorePassword,
                                      (X509ExtendedKeyManager) clientKeyManagers[0],
                                      (X509ExtendedKeyManager) serverKeyManagers[0],
                                      (X509TrustManager) clientTrustManagers[0],
                                      (X509TrustManager) serverTrustManagers[0],
                                      clientContext, serverContext,
                                      serverSocket, host, port);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a SSLContext with a KeyManager using the private key and
     * certificate chain from the given KeyStore and a TrustManager
     * using the certificates authorities from the same KeyStore.
     */
    public static final SSLContext createSSLContext(final KeyManager[] keyManagers,
                                                    final TrustManager[] trustManagers)
            throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagers, trustManagers, new SecureRandom());
        return context;
    }

    public static KeyManager[] createKeyManagers(final KeyStore keyStore,
                                                 final char[] keyStorePassword) throws Exception {
        String kmfa = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(kmfa);
        kmf.init(keyStore, keyStorePassword);
        return kmf.getKeyManagers();
    }

    public static TrustManager[] createTrustManagers(final KeyStore keyStore,
                                                   final char[] keyStorePassword) throws Exception {
        String tmfa = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfa);
        tmf.init(keyStore);
        return tmf.getTrustManagers();
    }

    public static void assertCertificateInKeyStore(Principal principal,
                                                   KeyStore keyStore) throws Exception {
        String subjectName = principal.getName();
        boolean found = false;
        for (String alias: Collections.list(keyStore.aliases())) {
            if (!keyStore.isCertificateEntry(alias)) {
                continue;
            }
            X509Certificate keyStoreCertificate = (X509Certificate) keyStore.getCertificate(alias);
            if (subjectName.equals(keyStoreCertificate.getSubjectDN().getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    public static void assertCertificateInKeyStore(Certificate certificate,
                                                   KeyStore keyStore) throws Exception {
        boolean found = false;
        for (String alias: Collections.list(keyStore.aliases())) {
            if (!keyStore.isCertificateEntry(alias)) {
                continue;
            }
            Certificate keyStoreCertificate = keyStore.getCertificate(alias);
            if (certificate.equals(keyStoreCertificate)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    public static void assertServerCertificateChain(X509TrustManager trustManager,
                                                    Certificate[] serverChain)
            throws CertificateException {
        X509Certificate[] chain = (X509Certificate[]) serverChain;
        trustManager.checkServerTrusted(chain, chain[0].getPublicKey().getAlgorithm());
    }

    public static void assertClientCertificateChain(X509TrustManager trustManager,
                                                    Certificate[] clientChain)
            throws CertificateException {
        X509Certificate[] chain = (X509Certificate[]) clientChain;
        trustManager.checkClientTrusted(chain, chain[0].getPublicKey().getAlgorithm());
    }
}
