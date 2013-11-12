/* Licensed to the Apache Software Foundation (ASF) under one or more
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

package tests.api.java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.Authenticator.RequestorType;

import junit.framework.TestCase;
import tests.support.Support_PortManager;

public class AuthenticatorTest extends TestCase {

    /**
     * java.net.Authenticator.RequestorType#valueOf(String)
     */
    public void test_RequestorType_valueOfLjava_lang_String() throws Exception {
        assertEquals(RequestorType.PROXY, Authenticator.RequestorType
                .valueOf("PROXY"));
        assertEquals(RequestorType.SERVER, Authenticator.RequestorType
                .valueOf("SERVER"));
        try {
            RequestorType rt = Authenticator.RequestorType.valueOf("BADNAME");
            fail("Must throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // correct
        }
        // Some old RIs throw IllegalArgumentException 
        // Latest RIs throw NullPointerException.
        try {
            Authenticator.RequestorType.valueOf(null);
            fail("Must throw an exception");
        } catch (NullPointerException e) {
            // May be caused by some compilers' code
        } catch (IllegalArgumentException e) {
            // other compilers will throw this
        }
    }

    /**
     * java.net.Authenticator.RequestorType#values()
     */
    public void test_RequestorType_values() throws Exception {
        RequestorType[] rt = RequestorType.values();
        assertEquals(RequestorType.PROXY, rt[0]);
        assertEquals(RequestorType.SERVER, rt[1]);
    }

    /**
     * java.net.Authenticator#requestPasswordAuthentication(java.net.InetAddress, int, String, String, String)
     */
    public void test_requestPasswordAuthentication_InetAddress_int_String_String_String() throws Exception {
        // Regression test for Harmony-2413
        MockAuthenticator mock = new MockAuthenticator();
        InetAddress addr = InetAddress.getLocalHost();
        Authenticator.setDefault(mock);
        Authenticator.requestPasswordAuthentication(addr, -1, "http", "promt", "HTTP");
        assertEquals(mock.getRequestorType(), RequestorType.SERVER);
    }

    /**
     * java.net.Authenticator#requestPasswordAuthentication(String, java.net.InetAddress, int, String, String, String)
     */
    public void test_requestPasswordAuthentication_String_InetAddress_int_String_String_String() throws Exception {
        // Regression test for Harmony-2413
        MockAuthenticator mock = new MockAuthenticator();
        InetAddress addr = InetAddress.getLocalHost();
        Authenticator.setDefault(mock);
        Authenticator.requestPasswordAuthentication("test_host", addr, -1, "http", "promt", "HTTP");
        assertEquals(mock.getRequestorType(), RequestorType.SERVER);
    }

    /**
     * java.net.Authenticator#
     * requestPasswordAuthentication_String_InetAddress_int_String_String_String_URL_Authenticator_RequestorType()
     */
    public void test_requestPasswordAuthentication_String_InetAddress_int_String_String_String_URL_Authenticator_RequestorType()
            throws UnknownHostException, MalformedURLException {
        MockAuthenticator mock = new MockAuthenticator();
        URL url = new URL("http://127.0.0.1");
        Authenticator.requestPasswordAuthentication("localhost", InetAddress
                .getByName("127.0.0.1"), 80, "HTTP", "", "", url,
                RequestorType.PROXY);
        assertNull(mock.getRequestingURL());
        assertNull(mock.getRequestorType());
    }

    /**
     * java.net.Authenticator#getRequestingURL()
     */
    public void test_getRequestingURL() throws Exception {
        MockAuthenticator mock = new MockAuthenticator();
        assertNull(mock.getRequestingURL());
    }

    /**
     * java.net.Authenticator#getRequestorType()
     */
    public void test_getRequestorType() throws Exception {
        MockAuthenticator mock = new MockAuthenticator();
        assertNull(mock.getRequestorType());
    }

    /**
     * java.net.Authenticator#setDefault(java.net.Authenticator)
     */
    public void test_setDefault() {
        final int port = Support_PortManager.getNextPort();
        final Object lock = new Object();
        final int[] result = new int[1];
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    ServerSocket ss = null;
                    synchronized (lock) {
                        ss = new ServerSocket(port);
                        lock.notifyAll();
                    }
                    Socket s = ss.accept();
                    InputStream in = s.getInputStream();
                    in.read(new byte[1024]);
                    OutputStream out = s.getOutputStream();
                    out
                            .write("HTTP/1.1 401 Unauthorized\r\nWWW-Authenticate:Basic realm=\"something\"\r\n\r\n"
                                    .getBytes("ISO8859_1"));
                    Thread.sleep(500);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        synchronized (lock) {
            t.start();
            try {
                lock.wait();
            } catch (InterruptedException e) {
                // ignored
            }
        }
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                synchronized (lock) {
                    result[0] = 1;
                }
                return null;
            }
        });
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                synchronized (lock) {
                    result[0] = 2;
                }
                return null;
            }
        });
        try {
            new URL("http://localhost:" + port).openStream();
        } catch (IOException e) {
            // ignored
        }
        synchronized (lock) {
            assertEquals("wrong authenticator: " + result[0], 2, result[0]);
        }
    }

    /*
     * Mock Authernticator for test
     */
    class MockAuthenticator extends java.net.Authenticator {
        public MockAuthenticator() {
            super();
        }

        public URL getRequestingURL() {
            return super.getRequestingURL();
        }

        public Authenticator.RequestorType getRequestorType() {
            return super.getRequestorType();
        }
    }
}
