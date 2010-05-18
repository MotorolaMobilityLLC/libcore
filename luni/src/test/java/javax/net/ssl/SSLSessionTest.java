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

import java.util.Arrays;
import junit.framework.TestCase;

public class SSLSessionTest extends TestCase {

    public void test_SSLSocket_TestSSLSessions_create() {
        TestSSLSessions s = TestSSLSessions.create();
        assertNotNull(s.invalid);
        assertFalse(s.invalid.isValid());
        assertTrue(s.server.isValid());
        assertTrue(s.client.isValid());
    }

    public void test_SSLSession_getApplicationBufferSize() {
        TestSSLSessions s = TestSSLSessions.create();
        assertTrue(s.invalid.getApplicationBufferSize() > 0);
        assertTrue(s.server.getApplicationBufferSize() > 0);
        assertTrue(s.client.getApplicationBufferSize() > 0);
    }

    public void test_SSLSession_getCipherSuite() {
        TestSSLSessions s = TestSSLSessions.create();
        assertNotNull(s.invalid.getCipherSuite());
        assertEquals(StandardNames.CIPHER_SUITE_INVALID, s.invalid.getCipherSuite());
        assertNotNull(s.server.getCipherSuite());
        assertNotNull(s.client.getCipherSuite());
        assertEquals(s.server.getCipherSuite(),
                     s.client.getCipherSuite());
        assertTrue(StandardNames.CIPHER_SUITES.contains(s.server.getCipherSuite()));
    }

    public void test_SSLSession_getCreationTime() {
        TestSSLSessions s = TestSSLSessions.create();
        assertTrue(s.invalid.getCreationTime() > 0);
        assertTrue(s.server.getCreationTime() > 0);
        assertTrue(s.client.getCreationTime() > 0);
        assertTrue(Math.abs(s.server.getCreationTime() - s.client.getCreationTime()) < 1 * 1000);
    }

    public void test_SSLSession_getId() {
        TestSSLSessions s = TestSSLSessions.create();
        assertNotNull(s.invalid.getId());
        assertNotNull(s.server.getId());
        assertNotNull(s.client.getId());
        assertEquals(0, s.invalid.getId().length);
        if (TestSSLContext.sslServerSocketSupportsSessionTickets()) {
            assertEquals(0, s.server.getId().length);
        } else {
            assertEquals(32, s.server.getId().length);
            assertTrue(Arrays.equals(s.server.getId(), s.client.getId()));
        }
        assertEquals(32, s.client.getId().length);
    }

    public void test_SSLSession_getLastAccessedTime() {
        TestSSLSessions s = TestSSLSessions.create();
        assertTrue(s.invalid.getLastAccessedTime() > 0);
        assertTrue(s.server.getLastAccessedTime() > 0);
        assertTrue(s.client.getLastAccessedTime() > 0);
        assertTrue(Math.abs(s.server.getLastAccessedTime() -
                            s.client.getLastAccessedTime()) < 1 * 1000);
        assertTrue(s.server.getLastAccessedTime() >=
                   s.server.getCreationTime());
        assertTrue(s.client.getLastAccessedTime() >=
                   s.client.getCreationTime());
    }

    public void test_SSLSession_getLocalCertificates() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        assertNull(s.invalid.getLocalCertificates());
        assertNull(s.client.getLocalCertificates());
        assertNotNull(s.server.getLocalCertificates());
        assertEquals(1, s.server.getLocalCertificates().length);
        TestSSLContext.assertCertificateInKeyStore(s.server.getLocalCertificates()[0],
                                                   s.s.c.keyStore);
    }

    public void test_SSLSession_getLocalPrincipal() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        assertNull(s.invalid.getLocalPrincipal());
        assertNull(s.client.getLocalPrincipal());
        assertNotNull(s.server.getLocalPrincipal());
        assertNotNull(s.server.getLocalPrincipal().getName());
        TestSSLContext.assertCertificateInKeyStore(s.server.getLocalPrincipal(),
                                                   s.s.c.keyStore);
    }

    public void test_SSLSession_getPacketBufferSize() {
        TestSSLSessions s = TestSSLSessions.create();
        assertTrue(s.invalid.getPacketBufferSize() > 0);
        assertTrue(s.server.getPacketBufferSize() > 0);
        assertTrue(s.client.getPacketBufferSize() > 0);
    }

    public void test_SSLSession_getPeerCertificateChain() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        try {
            s.invalid.getPeerCertificateChain();
            fail();
        } catch (SSLPeerUnverifiedException e) {
        }
        assertNotNull(s.client.getPeerCertificates());
        assertEquals(1, s.client.getPeerCertificates().length);
        TestSSLContext.assertCertificateInKeyStore(s.client.getPeerCertificates()[0],
                                                   s.s.c.keyStore);

        try {
            assertNull(s.server.getPeerCertificates());
            fail();
        } catch (SSLPeerUnverifiedException e) {
        }
    }

    public void test_SSLSession_getPeerCertificates() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        try {
            s.invalid.getPeerCertificates();
            fail();
        } catch (SSLPeerUnverifiedException e) {
        }
        assertNotNull(s.client.getPeerCertificates());
        assertEquals(1, s.client.getPeerCertificates().length);
        TestSSLContext.assertCertificateInKeyStore(s.client.getPeerCertificates()[0],
                                                   s.s.c.keyStore);
        try {
            s.server.getPeerCertificates();
            fail();
        } catch (SSLPeerUnverifiedException e) {
        }
    }

    public void test_SSLSession_getPeerHost() {
        TestSSLSessions s = TestSSLSessions.create();
        assertNull(s.invalid.getPeerHost());
        assertNotNull(s.server.getPeerHost());
        assertNotNull(s.client.getPeerHost());
    }

    public void test_SSLSession_getPeerPort() {
        TestSSLSessions s = TestSSLSessions.create();
        assertEquals(-1, s.invalid.getPeerPort());
        assertTrue(s.server.getPeerPort() > 0);
        assertEquals(s.s.c.port, s.client.getPeerPort());
    }

    public void test_SSLSession_getPeerPrincipal() throws Exception {
        TestSSLSessions s = TestSSLSessions.create();
        try {
            s.invalid.getPeerPrincipal();
            fail();
        } catch (SSLPeerUnverifiedException e) {
        }
        try {
            s.server.getPeerPrincipal();
            fail();
        } catch (SSLPeerUnverifiedException e) {
        }
        assertNotNull(s.client.getPeerPrincipal());
        assertNotNull(s.client.getPeerPrincipal().getName());
        TestSSLContext.assertCertificateInKeyStore(s.client.getPeerPrincipal(),
                                                   s.s.c.keyStore);
    }

    public void test_SSLSession_getProtocol() {
        TestSSLSessions s = TestSSLSessions.create();
        assertNotNull(s.invalid.getProtocol());
        assertEquals("NONE", s.invalid.getProtocol());
        assertNotNull(s.server.getProtocol());
        assertNotNull(s.client.getProtocol());
        assertEquals(s.server.getProtocol(),
                     s.client.getProtocol());
        assertTrue(StandardNames.SSL_SOCKET_PROTOCOLS.contains(s.server.getProtocol()));
    }

    public void test_SSLSession_getSessionContext() {
        TestSSLSessions s = TestSSLSessions.create();
        assertNull(s.invalid.getSessionContext());
        assertNotNull(s.server.getSessionContext());
        assertNotNull(s.client.getSessionContext());
        assertEquals(s.s.c.sslContext.getServerSessionContext(),
                     s.server.getSessionContext());
        assertEquals(s.s.c.sslContext.getClientSessionContext(),
                     s.client.getSessionContext());
        assertNotSame(s.server.getSessionContext(),
                      s.client.getSessionContext());
    }

    public void test_SSLSession_getValue() {
        TestSSLSessions s = TestSSLSessions.create();
        try {
            s.invalid.getValue(null);
        } catch (IllegalArgumentException e) {
        }
        assertNull(s.invalid.getValue("BOGUS"));
    }

    public void test_SSLSession_getValueNames() {
        TestSSLSessions s = TestSSLSessions.create();
        assertNotNull(s.invalid.getValueNames());
        assertEquals(0, s.invalid.getValueNames().length);
    }

    public void test_SSLSession_invalidate() {
        TestSSLSessions s = TestSSLSessions.create();
        assertFalse(s.invalid.isValid());
        s.invalid.invalidate();
        assertFalse(s.invalid.isValid());
        assertNull(s.invalid.getSessionContext());

        assertTrue(s.server.isValid());
        s.server.invalidate();
        assertFalse(s.server.isValid());
        assertNull(s.server.getSessionContext());

        assertTrue(s.client.isValid());
        s.client.invalidate();
        assertFalse(s.client.isValid());
        assertNull(s.client.getSessionContext());
    }

    public void test_SSLSession_isValid() {
        TestSSLSessions s = TestSSLSessions.create();
        assertFalse(s.invalid.isValid());
        assertTrue(s.server.isValid());
        assertTrue(s.client.isValid());
    }

    public void test_SSLSession_putValue() {
        TestSSLSessions s = TestSSLSessions.create();
        String key = "KEY";
        String value = "VALUE";
        assertNull(s.invalid.getValue(key));
        assertEquals(0, s.invalid.getValueNames().length);
        s.invalid.putValue(key, value);
        assertSame(value, s.invalid.getValue(key));
        assertEquals(1, s.invalid.getValueNames().length);
        assertEquals(key, s.invalid.getValueNames()[0]);
    }

    public void test_SSLSession_removeValue() {
        TestSSLSessions s = TestSSLSessions.create();
        String key = "KEY";
        String value = "VALUE";
        s.invalid.putValue(key, value);
        assertEquals(1, s.invalid.getValueNames().length);
        assertEquals(key, s.invalid.getValueNames()[0]);
        s.invalid.removeValue(key);
        assertNull(s.invalid.getValue(key));
        assertEquals(0, s.invalid.getValueNames().length);
    }
}
