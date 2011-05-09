/*
 * Copyright (C) 2009 The Android Open Source Project
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

package libcore.java.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ConnectException;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.ResponseCache;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import libcore.java.security.TestKeyStore;
import libcore.javax.net.ssl.TestSSLContext;
import tests.http.MockResponse;
import tests.http.MockWebServer;
import tests.http.RecordedRequest;
import tests.http.SocketPolicy;
import static tests.http.SocketPolicy.DISCONNECT_AT_END;
import static tests.http.SocketPolicy.DISCONNECT_AT_START;
import static tests.http.SocketPolicy.SHUTDOWN_INPUT_AT_END;
import static tests.http.SocketPolicy.SHUTDOWN_OUTPUT_AT_END;
import tests.net.StuckServer;

public class URLConnectionTest extends junit.framework.TestCase {

    private static final Authenticator SIMPLE_AUTHENTICATOR = new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("username", "password".toCharArray());
        }
    };

    /** base64("username:password") */
    private static final String BASE_64_CREDENTIALS = "dXNlcm5hbWU6cGFzc3dvcmQ=";

    private MockWebServer server = new MockWebServer();
    private String hostName;

    @Override protected void setUp() throws Exception {
        super.setUp();
        hostName = server.getHostName();
    }

    @Override protected void tearDown() throws Exception {
        ResponseCache.setDefault(null);
        Authenticator.setDefault(null);
        System.clearProperty("proxyHost");
        System.clearProperty("proxyPort");
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");
        server.shutdown();
        super.tearDown();
    }

    public void testRequestHeaders() throws IOException, InterruptedException {
        server.enqueue(new MockResponse());
        server.play();

        HttpURLConnection urlConnection = (HttpURLConnection) server.getUrl("/").openConnection();
        urlConnection.addRequestProperty("D", "e");
        urlConnection.addRequestProperty("D", "f");
        Map<String, List<String>> requestHeaders = urlConnection.getRequestProperties();
        assertEquals(newSet("e", "f"), new HashSet<String>(requestHeaders.get("D")));
        try {
            requestHeaders.put("G", Arrays.asList("h"));
            fail("Modified an unmodifiable view.");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            requestHeaders.get("D").add("i");
            fail("Modified an unmodifiable view.");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            urlConnection.setRequestProperty(null, "j");
            fail();
        } catch (NullPointerException expected) {
        }
        try {
            urlConnection.addRequestProperty(null, "k");
            fail();
        } catch (NullPointerException expected) {
        }
        urlConnection.setRequestProperty("NullValue", null); // should fail silently!
        urlConnection.addRequestProperty("AnotherNullValue", null);  // should fail silently!

        urlConnection.getResponseCode();
        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "D: e");
        assertContains(request.getHeaders(), "D: f");
        assertContainsNoneMatching(request.getHeaders(), "NullValue.*");
        assertContainsNoneMatching(request.getHeaders(), "AnotherNullValue.*");
        assertContainsNoneMatching(request.getHeaders(), "G:.*");
        assertContainsNoneMatching(request.getHeaders(), "null:.*");

        try {
            urlConnection.addRequestProperty("N", "o");
            fail("Set header after connect");
        } catch (IllegalStateException expected) {
        }
        try {
            urlConnection.setRequestProperty("P", "q");
            fail("Set header after connect");
        } catch (IllegalStateException expected) {
        }
    }

    public void testResponseHeaders() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .setStatus("HTTP/1.0 200 Fantastic")
                .addHeader("A: b")
                .addHeader("A: c")
                .setChunkedBody("ABCDE\nFGHIJ\nKLMNO\nPQR", 8));
        server.play();

        HttpURLConnection urlConnection = (HttpURLConnection) server.getUrl("/").openConnection();
        assertEquals(200, urlConnection.getResponseCode());
        assertEquals("Fantastic", urlConnection.getResponseMessage());
        assertEquals("HTTP/1.0 200 Fantastic", urlConnection.getHeaderField(null));
        Map<String, List<String>> responseHeaders = urlConnection.getHeaderFields();
        assertEquals(Arrays.asList("HTTP/1.0 200 Fantastic"), responseHeaders.get(null));
        assertEquals(newSet("b", "c"), new HashSet<String>(responseHeaders.get("A")));
        try {
            responseHeaders.put("N", Arrays.asList("o"));
            fail("Modified an unmodifiable view.");
        } catch (UnsupportedOperationException expected) {
        }
        try {
            responseHeaders.get("A").add("d");
            fail("Modified an unmodifiable view.");
        } catch (UnsupportedOperationException expected) {
        }
    }

    // Check that if we don't read to the end of a response, the next request on the
    // recycled connection doesn't get the unread tail of the first request's response.
    // http://code.google.com/p/android/issues/detail?id=2939
    public void test_2939() throws Exception {
        MockResponse response = new MockResponse().setChunkedBody("ABCDE\nFGHIJ\nKLMNO\nPQR", 8);

        server.enqueue(response);
        server.enqueue(response);
        server.play();

        assertContent("ABCDE", server.getUrl("/").openConnection(), 5);
        assertContent("ABCDE", server.getUrl("/").openConnection(), 5);
    }

    // Check that we recognize a few basic mime types by extension.
    // http://code.google.com/p/android/issues/detail?id=10100
    public void test_10100() throws Exception {
        assertEquals("image/jpeg", URLConnection.guessContentTypeFromName("someFile.jpg"));
        assertEquals("application/pdf", URLConnection.guessContentTypeFromName("stuff.pdf"));
    }

    public void testConnectionsArePooled() throws Exception {
        MockResponse response = new MockResponse().setBody("ABCDEFGHIJKLMNOPQR");

        server.enqueue(response);
        server.enqueue(response);
        server.enqueue(response);
        server.play();

        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/foo").openConnection());
        assertEquals(0, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/bar?baz=quux").openConnection());
        assertEquals(1, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/z").openConnection());
        assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    public void testChunkedConnectionsArePooled() throws Exception {
        MockResponse response = new MockResponse().setChunkedBody("ABCDEFGHIJKLMNOPQR", 5);

        server.enqueue(response);
        server.enqueue(response);
        server.enqueue(response);
        server.play();

        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/foo").openConnection());
        assertEquals(0, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/bar?baz=quux").openConnection());
        assertEquals(1, server.takeRequest().getSequenceNumber());
        assertContent("ABCDEFGHIJKLMNOPQR", server.getUrl("/z").openConnection());
        assertEquals(2, server.takeRequest().getSequenceNumber());
    }

    public void testServerClosesSocket() throws Exception {
        testServerClosesOutput(DISCONNECT_AT_END);
    }

    public void testServerShutdownInput() throws Exception {
        testServerClosesOutput(SHUTDOWN_INPUT_AT_END);
    }

    public void testServerShutdownOutput() throws Exception {
        testServerClosesOutput(SHUTDOWN_OUTPUT_AT_END);
    }

    private void testServerClosesOutput(SocketPolicy socketPolicy) throws Exception {
        server.enqueue(new MockResponse()
                .setBody("This connection won't pool properly")
                .setSocketPolicy(socketPolicy));
        server.enqueue(new MockResponse()
                .setBody("This comes after a busted connection"));
        server.play();

        assertContent("This connection won't pool properly", server.getUrl("/a").openConnection());
        assertEquals(0, server.takeRequest().getSequenceNumber());
        assertContent("This comes after a busted connection", server.getUrl("/b").openConnection());
        // sequence number 0 means the HTTP socket connection was not reused
        assertEquals(0, server.takeRequest().getSequenceNumber());
    }

    enum WriteKind { BYTE_BY_BYTE, SMALL_BUFFERS, LARGE_BUFFERS }

    public void test_chunkedUpload_byteByByte() throws Exception {
        doUpload(TransferKind.CHUNKED, WriteKind.BYTE_BY_BYTE);
    }

    public void test_chunkedUpload_smallBuffers() throws Exception {
        doUpload(TransferKind.CHUNKED, WriteKind.SMALL_BUFFERS);
    }

    public void test_chunkedUpload_largeBuffers() throws Exception {
        doUpload(TransferKind.CHUNKED, WriteKind.LARGE_BUFFERS);
    }

    public void test_fixedLengthUpload_byteByByte() throws Exception {
        doUpload(TransferKind.FIXED_LENGTH, WriteKind.BYTE_BY_BYTE);
    }

    public void test_fixedLengthUpload_smallBuffers() throws Exception {
        doUpload(TransferKind.FIXED_LENGTH, WriteKind.SMALL_BUFFERS);
    }

    public void test_fixedLengthUpload_largeBuffers() throws Exception {
        doUpload(TransferKind.FIXED_LENGTH, WriteKind.LARGE_BUFFERS);
    }

    private void doUpload(TransferKind uploadKind, WriteKind writeKind) throws Exception {
        int n = 512*1024;
        server.setBodyLimit(0);
        server.enqueue(new MockResponse());
        server.play();

        HttpURLConnection conn = (HttpURLConnection) server.getUrl("/").openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        if (uploadKind == TransferKind.CHUNKED) {
            conn.setChunkedStreamingMode(-1);
        } else {
            conn.setFixedLengthStreamingMode(n);
        }
        OutputStream out = conn.getOutputStream();
        if (writeKind == WriteKind.BYTE_BY_BYTE) {
            for (int i = 0; i < n; ++i) {
                out.write('x');
            }
        } else {
            byte[] buf = new byte[writeKind == WriteKind.SMALL_BUFFERS ? 256 : 64*1024];
            Arrays.fill(buf, (byte) 'x');
            for (int i = 0; i < n; i += buf.length) {
                out.write(buf, 0, Math.min(buf.length, n - i));
            }
        }
        out.close();
        assertEquals(200, conn.getResponseCode());
        RecordedRequest request = server.takeRequest();
        assertEquals(n, request.getBodySize());
        if (uploadKind == TransferKind.CHUNKED) {
            assertTrue(request.getChunkSizes().size() > 0);
        } else {
            assertTrue(request.getChunkSizes().isEmpty());
        }
    }

    public void testGetResponseCodeNoResponseBody() throws Exception {
        server.enqueue(new MockResponse()
                .addHeader("abc: def"));
        server.play();

        URL url = server.getUrl("/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(false);
        assertEquals("def", conn.getHeaderField("abc"));
        assertEquals(200, conn.getResponseCode());
        try {
            conn.getInputStream();
            fail();
        } catch (ProtocolException expected) {
        }
    }

    public void testConnectViaHttps() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();

        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/foo").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());

        assertContent("this response comes via HTTPS", connection);

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
    }

    public void testConnectViaHttpsReusingConnections() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();

        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        server.enqueue(new MockResponse().setBody("another response via HTTPS"));
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertContent("this response comes via HTTPS", connection);

        connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertContent("another response via HTTPS", connection);

        assertEquals(0, server.takeRequest().getSequenceNumber());
        assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    public void testConnectViaHttpsReusingConnectionsDifferentFactories()
            throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();

        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        server.enqueue(new MockResponse().setBody("another response via HTTPS"));
        server.play();

        // install a custom SSL socket factory so the server can be authorized
        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertContent("this response comes via HTTPS", connection);

        connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        try {
            readAscii(connection.getInputStream(), Integer.MAX_VALUE);
            fail("without an SSL socket factory, the connection should fail");
        } catch (SSLException expected) {
        }
    }

    public void testConnectViaHttpsWithSSLFallback() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();

        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_START));
        server.enqueue(new MockResponse().setBody("this response comes via SSL"));
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/foo").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());

        assertContent("this response comes via SSL", connection);

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
    }

    /**
     * Verify that we don't retry connections on certificate verification errors.
     *
     * http://code.google.com/p/android/issues/detail?id=13178
     */
    public void testConnectViaHttpsToUntrustedServer() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create(TestKeyStore.getClientCA2(),
                                                              TestKeyStore.getServer());

        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse()); // unused
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/foo").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        try {
            connection.getInputStream();
            fail();
        } catch (SSLHandshakeException expected) {
            assertTrue(expected.getCause() instanceof CertificateException);
        }
        assertEquals(0, server.getRequestCount());
    }

    public void testConnectViaProxyUsingProxyArg() throws Exception {
        testConnectViaProxy(ProxyConfig.CREATE_ARG);
    }

    public void testConnectViaProxyUsingProxySystemProperty() throws Exception {
        testConnectViaProxy(ProxyConfig.PROXY_SYSTEM_PROPERTY);
    }

    public void testConnectViaProxyUsingHttpProxySystemProperty() throws Exception {
        testConnectViaProxy(ProxyConfig.HTTP_PROXY_SYSTEM_PROPERTY);
    }

    private void testConnectViaProxy(ProxyConfig proxyConfig) throws Exception {
        MockResponse mockResponse = new MockResponse().setBody("this response comes via a proxy");
        server.enqueue(mockResponse);
        server.play();

        URL url = new URL("http://android.com/foo");
        HttpURLConnection connection = proxyConfig.connect(server, url);
        assertContent("this response comes via a proxy", connection);

        RecordedRequest request = server.takeRequest();
        assertEquals("GET http://android.com/foo HTTP/1.1", request.getRequestLine());
        assertContains(request.getHeaders(), "Host: android.com");
    }

    public void testContentDisagreesWithContentLengthHeader() throws IOException {
        server.enqueue(new MockResponse()
                .setBody("abc\r\nYOU SHOULD NOT SEE THIS")
                .clearHeaders()
                .addHeader("Content-Length: 3"));
        server.play();

        assertContent("abc", server.getUrl("/").openConnection());
    }

    public void testContentDisagreesWithChunkedHeader() throws IOException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setChunkedBody("abc", 3);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        bytesOut.write(mockResponse.getBody());
        bytesOut.write("\r\nYOU SHOULD NOT SEE THIS".getBytes());
        mockResponse.setBody(bytesOut.toByteArray());
        mockResponse.clearHeaders();
        mockResponse.addHeader("Transfer-encoding: chunked");

        server.enqueue(mockResponse);
        server.play();

        assertContent("abc", server.getUrl("/").openConnection());
    }

    public void testConnectViaHttpProxyToHttpsUsingProxyArgWithNoProxy() throws Exception {
        testConnectViaDirectProxyToHttps(ProxyConfig.NO_PROXY);
    }

    public void testConnectViaHttpProxyToHttpsUsingHttpProxySystemProperty() throws Exception {
        // https should not use http proxy
        testConnectViaDirectProxyToHttps(ProxyConfig.HTTP_PROXY_SYSTEM_PROPERTY);
    }

    private void testConnectViaDirectProxyToHttps(ProxyConfig proxyConfig) throws Exception {
        TestSSLContext testSSLContext = TestSSLContext.create();

        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("this response comes via HTTPS"));
        server.play();

        URL url = server.getUrl("/foo");
        HttpsURLConnection connection = (HttpsURLConnection) proxyConfig.connect(server, url);
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());

        assertContent("this response comes via HTTPS", connection);

        RecordedRequest request = server.takeRequest();
        assertEquals("GET /foo HTTP/1.1", request.getRequestLine());
    }


    public void testConnectViaHttpProxyToHttpsUsingProxyArg() throws Exception {
        testConnectViaHttpProxyToHttps(ProxyConfig.CREATE_ARG);
    }

    /**
     * We weren't honoring all of the appropriate proxy system properties when
     * connecting via HTTPS. http://b/3097518
     */
    public void testConnectViaHttpProxyToHttpsUsingProxySystemProperty() throws Exception {
        testConnectViaHttpProxyToHttps(ProxyConfig.PROXY_SYSTEM_PROPERTY);
    }

    public void testConnectViaHttpProxyToHttpsUsingHttpsProxySystemProperty() throws Exception {
        testConnectViaHttpProxyToHttps(ProxyConfig.HTTPS_PROXY_SYSTEM_PROPERTY);
    }

    /**
     * We were verifying the wrong hostname when connecting to an HTTPS site
     * through a proxy. http://b/3097277
     */
    private void testConnectViaHttpProxyToHttps(ProxyConfig proxyConfig) throws Exception {
        TestSSLContext testSSLContext = TestSSLContext.create();
        RecordingHostnameVerifier hostnameVerifier = new RecordingHostnameVerifier();

        server.useHttps(testSSLContext.serverContext.getSocketFactory(), true);
        server.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.UPGRADE_TO_SSL_AT_END)
                .clearHeaders());
        server.enqueue(new MockResponse().setBody("this response comes via a secure proxy"));
        server.play();

        URL url = new URL("https://android.com/foo");
        HttpsURLConnection connection = (HttpsURLConnection) proxyConfig.connect(server, url);
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setHostnameVerifier(hostnameVerifier);

        assertContent("this response comes via a secure proxy", connection);

        RecordedRequest connect = server.takeRequest();
        assertEquals("Connect line failure on proxy",
                "CONNECT android.com:443 HTTP/1.1", connect.getRequestLine());
        assertContains(connect.getHeaders(), "Host: android.com");

        RecordedRequest get = server.takeRequest();
        assertEquals("GET /foo HTTP/1.1", get.getRequestLine());
        assertContains(get.getHeaders(), "Host: android.com");
        assertEquals(Arrays.asList("verify android.com"), hostnameVerifier.calls);
    }

    /**
     * Test which headers are sent unencrypted to the HTTP proxy.
     */
    public void testProxyConnectIncludesProxyHeadersOnly()
            throws IOException, InterruptedException {
        RecordingHostnameVerifier hostnameVerifier = new RecordingHostnameVerifier();
        TestSSLContext testSSLContext = TestSSLContext.create();

        server.useHttps(testSSLContext.serverContext.getSocketFactory(), true);
        server.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.UPGRADE_TO_SSL_AT_END)
                .clearHeaders());
        server.enqueue(new MockResponse().setBody("encrypted response from the origin server"));
        server.play();

        URL url = new URL("https://android.com/foo");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection(
                server.toProxyAddress());
        connection.addRequestProperty("Private", "Secret");
        connection.addRequestProperty("Proxy-Authorization", "bar");
        connection.addRequestProperty("User-Agent", "baz");
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setHostnameVerifier(hostnameVerifier);
        assertContent("encrypted response from the origin server", connection);

        RecordedRequest connect = server.takeRequest();
        assertContainsNoneMatching(connect.getHeaders(), "Private.*");
        assertContains(connect.getHeaders(), "Proxy-Authorization: bar");
        assertContains(connect.getHeaders(), "User-Agent: baz");
        assertContains(connect.getHeaders(), "Host: android.com");
        assertContains(connect.getHeaders(), "Proxy-Connection: Keep-Alive");

        RecordedRequest get = server.takeRequest();
        assertContains(get.getHeaders(), "Private: Secret");
        assertEquals(Arrays.asList("verify android.com"), hostnameVerifier.calls);
    }

    public void testProxyAuthenticateOnConnect() throws Exception {
        Authenticator.setDefault(SIMPLE_AUTHENTICATOR);
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), true);
        server.enqueue(new MockResponse()
                .setResponseCode(407)
                .addHeader("Proxy-Authenticate: Basic realm=\"localhost\""));
        server.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.UPGRADE_TO_SSL_AT_END)
                .clearHeaders());
        server.enqueue(new MockResponse().setBody("A"));
        server.play();

        URL url = new URL("https://android.com/foo");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection(
                server.toProxyAddress());
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setHostnameVerifier(new RecordingHostnameVerifier());
        assertContent("A", connection);

        RecordedRequest connect1 = server.takeRequest();
        assertEquals("CONNECT android.com:443 HTTP/1.1", connect1.getRequestLine());
        assertContainsNoneMatching(connect1.getHeaders(), "Proxy\\-Authorization.*");

        RecordedRequest connect2 = server.takeRequest();
        assertEquals("CONNECT android.com:443 HTTP/1.1", connect2.getRequestLine());
        assertContains(connect2.getHeaders(), "Proxy-Authorization: Basic " + BASE_64_CREDENTIALS);

        RecordedRequest get = server.takeRequest();
        assertEquals("GET /foo HTTP/1.1", get.getRequestLine());
        assertContainsNoneMatching(get.getHeaders(), "Proxy\\-Authorization.*");
    }

    public void testDisconnectedConnection() throws IOException {
        server.enqueue(new MockResponse().setBody("ABCDEFGHIJKLMNOPQR"));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        assertEquals('A', (char) in.read());
        connection.disconnect();
        try {
            in.read();
            fail("Expected a connection closed exception");
        } catch (IOException expected) {
        }
    }

    public void testDisconnectBeforeConnect() throws IOException {
        server.enqueue(new MockResponse().setBody("A"));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        connection.disconnect();

        assertContent("A", connection);
        assertEquals(200, connection.getResponseCode());
    }

    public void testDefaultRequestProperty() throws Exception {
        URLConnection.setDefaultRequestProperty("X-testSetDefaultRequestProperty", "A");
        assertNull(URLConnection.getDefaultRequestProperty("X-setDefaultRequestProperty"));
    }

    /**
     * Reads {@code count} characters from the stream. If the stream is
     * exhausted before {@code count} characters can be read, the remaining
     * characters are returned and the stream is closed.
     */
    private String readAscii(InputStream in, int count) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int value = in.read();
            if (value == -1) {
                in.close();
                break;
            }
            result.append((char) value);
        }
        return result.toString();
    }

    public void testMarkAndResetWithContentLengthHeader() throws IOException {
        testMarkAndReset(TransferKind.FIXED_LENGTH);
    }

    public void testMarkAndResetWithChunkedEncoding() throws IOException {
        testMarkAndReset(TransferKind.CHUNKED);
    }

    public void testMarkAndResetWithNoLengthHeaders() throws IOException {
        testMarkAndReset(TransferKind.END_OF_STREAM);
    }

    private void testMarkAndReset(TransferKind transferKind) throws IOException {
        MockResponse response = new MockResponse();
        transferKind.setBody(response, "ABCDEFGHIJKLMNOPQRSTUVWXYZ", 1024);
        server.enqueue(response);
        server.enqueue(response);
        server.play();

        InputStream in = server.getUrl("/").openConnection().getInputStream();
        assertFalse("This implementation claims to support mark().", in.markSupported());
        in.mark(5);
        assertEquals("ABCDE", readAscii(in, 5));
        try {
            in.reset();
            fail();
        } catch (IOException expected) {
        }
        assertEquals("FGHIJKLMNOPQRSTUVWXYZ", readAscii(in, Integer.MAX_VALUE));
        assertContent("ABCDEFGHIJKLMNOPQRSTUVWXYZ", server.getUrl("/").openConnection());
    }

    /**
     * We've had a bug where we forget the HTTP response when we see response
     * code 401. This causes a new HTTP request to be issued for every call into
     * the URLConnection.
     */
    public void testUnauthorizedResponseHandling() throws IOException {
        MockResponse response = new MockResponse()
                .addHeader("WWW-Authenticate: challenge")
                .setResponseCode(401) // UNAUTHORIZED
                .setBody("Unauthorized");
        server.enqueue(response);
        server.enqueue(response);
        server.enqueue(response);
        server.play();

        URL url = server.getUrl("/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        assertEquals(401, conn.getResponseCode());
        assertEquals(401, conn.getResponseCode());
        assertEquals(401, conn.getResponseCode());
        assertEquals(1, server.getRequestCount());
    }

    public void testNonHexChunkSize() throws IOException {
        server.enqueue(new MockResponse()
                .setBody("5\r\nABCDE\r\nG\r\nFGHIJKLMNOPQRSTU\r\n0\r\n\r\n")
                .clearHeaders()
                .addHeader("Transfer-encoding: chunked"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        try {
            readAscii(connection.getInputStream(), Integer.MAX_VALUE);
            fail();
        } catch (IOException e) {
        }
    }

    public void testMissingChunkBody() throws IOException {
        server.enqueue(new MockResponse()
                .setBody("5")
                .clearHeaders()
                .addHeader("Transfer-encoding: chunked")
                .setSocketPolicy(DISCONNECT_AT_END));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        try {
            readAscii(connection.getInputStream(), Integer.MAX_VALUE);
            fail();
        } catch (IOException e) {
        }
    }

    /**
     * This test checks whether connections are gzipped by default. This
     * behavior in not required by the API, so a failure of this test does not
     * imply a bug in the implementation.
     */
    public void testGzipEncodingEnabledByDefault() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .setBody(gzip("ABCABCABC".getBytes("UTF-8")))
                .addHeader("Content-Encoding: gzip"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        assertEquals("ABCABCABC", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        assertNull(connection.getContentEncoding());

        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "Accept-Encoding: gzip");
    }

    public void testClientConfiguredGzipContentEncoding() throws Exception {
        server.enqueue(new MockResponse()
                .setBody(gzip("ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes("UTF-8")))
                .addHeader("Content-Encoding: gzip"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        connection.addRequestProperty("Accept-Encoding", "gzip");
        InputStream gunzippedIn = new GZIPInputStream(connection.getInputStream());
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", readAscii(gunzippedIn, Integer.MAX_VALUE));

        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "Accept-Encoding: gzip");
    }

    public void testGzipAndConnectionReuseWithFixedLength() throws Exception {
        testClientConfiguredGzipContentEncodingAndConnectionReuse(TransferKind.FIXED_LENGTH);
    }

    public void testGzipAndConnectionReuseWithChunkedEncoding() throws Exception {
        testClientConfiguredGzipContentEncodingAndConnectionReuse(TransferKind.CHUNKED);
    }

    public void testClientConfiguredCustomContentEncoding() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("ABCDE")
                .addHeader("Content-Encoding: custom"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        connection.addRequestProperty("Accept-Encoding", "custom");
        assertEquals("ABCDE", readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        RecordedRequest request = server.takeRequest();
        assertContains(request.getHeaders(), "Accept-Encoding: custom");
    }

    /**
     * Test a bug where gzip input streams weren't exhausting the input stream,
     * which corrupted the request that followed.
     * http://code.google.com/p/android/issues/detail?id=7059
     */
    private void testClientConfiguredGzipContentEncodingAndConnectionReuse(
            TransferKind transferKind) throws Exception {
        MockResponse responseOne = new MockResponse();
        responseOne.addHeader("Content-Encoding: gzip");
        transferKind.setBody(responseOne, gzip("one (gzipped)".getBytes("UTF-8")), 5);
        server.enqueue(responseOne);
        MockResponse responseTwo = new MockResponse();
        transferKind.setBody(responseTwo, "two (identity)", 5);
        server.enqueue(responseTwo);
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        connection.addRequestProperty("Accept-Encoding", "gzip");
        InputStream gunzippedIn = new GZIPInputStream(connection.getInputStream());
        assertEquals("one (gzipped)", readAscii(gunzippedIn, Integer.MAX_VALUE));
        assertEquals(0, server.takeRequest().getSequenceNumber());

        connection = server.getUrl("/").openConnection();
        assertEquals("two (identity)", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        assertEquals(1, server.takeRequest().getSequenceNumber());
    }

    /**
     * Obnoxiously test that the chunk sizes transmitted exactly equal the
     * requested data+chunk header size. Although setChunkedStreamingMode()
     * isn't specific about whether the size applies to the data or the
     * complete chunk, the RI interprets it as a complete chunk.
     */
    public void testSetChunkedStreamingMode() throws IOException, InterruptedException {
        server.enqueue(new MockResponse());
        server.play();

        HttpURLConnection urlConnection = (HttpURLConnection) server.getUrl("/").openConnection();
        urlConnection.setChunkedStreamingMode(8);
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write("ABCDEFGHIJKLMNOPQ".getBytes("US-ASCII"));
        assertEquals(200, urlConnection.getResponseCode());

        RecordedRequest request = server.takeRequest();
        assertEquals("ABCDEFGHIJKLMNOPQ", new String(request.getBody(), "US-ASCII"));
        assertEquals(Arrays.asList(3, 3, 3, 3, 3, 2), request.getChunkSizes());
    }

    public void testAuthenticateWithFixedLengthStreaming() throws Exception {
        testAuthenticateWithStreamingPost(StreamingMode.FIXED_LENGTH);
    }

    public void testAuthenticateWithChunkedStreaming() throws Exception {
        testAuthenticateWithStreamingPost(StreamingMode.CHUNKED);
    }

    private void testAuthenticateWithStreamingPost(StreamingMode streamingMode) throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse()
                .setResponseCode(401)
                .addHeader("WWW-Authenticate: Basic realm=\"protected area\"")
                .setBody("Please authenticate.");
        server.enqueue(pleaseAuthenticate);
        server.play();

        Authenticator.setDefault(SIMPLE_AUTHENTICATOR);
        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        connection.setDoOutput(true);
        byte[] requestBody = { 'A', 'B', 'C', 'D' };
        if (streamingMode == StreamingMode.FIXED_LENGTH) {
            connection.setFixedLengthStreamingMode(requestBody.length);
        } else if (streamingMode == StreamingMode.CHUNKED) {
            connection.setChunkedStreamingMode(0);
        }
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody);
        outputStream.close();
        try {
            connection.getInputStream();
            fail();
        } catch (HttpRetryException expected) {
        }

        // no authorization header for the request...
        RecordedRequest request = server.takeRequest();
        assertContainsNoneMatching(request.getHeaders(), "Authorization: Basic .*");
        assertEquals(Arrays.toString(requestBody), Arrays.toString(request.getBody()));
    }

    public void testSecureFixedLengthStreaming() throws Exception {
        testSecureStreamingPost(StreamingMode.FIXED_LENGTH);
    }

    public void testSecureChunkedStreaming() throws Exception {
        testSecureStreamingPost(StreamingMode.CHUNKED);
    }

    /**
     * Users have reported problems using HTTPS with streaming request bodies.
     * http://code.google.com/p/android/issues/detail?id=12860
     */
    private void testSecureStreamingPost(StreamingMode streamingMode) throws Exception {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("Success!"));
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        connection.setDoOutput(true);
        byte[] requestBody = { 'A', 'B', 'C', 'D' };
        if (streamingMode == StreamingMode.FIXED_LENGTH) {
            connection.setFixedLengthStreamingMode(requestBody.length);
        } else if (streamingMode == StreamingMode.CHUNKED) {
            connection.setChunkedStreamingMode(0);
        }
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody);
        outputStream.close();
        assertEquals("Success!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        RecordedRequest request = server.takeRequest();
        assertEquals("POST / HTTP/1.1", request.getRequestLine());
        if (streamingMode == StreamingMode.FIXED_LENGTH) {
            assertEquals(Collections.<Integer>emptyList(), request.getChunkSizes());
        } else if (streamingMode == StreamingMode.CHUNKED) {
            assertEquals(Arrays.asList(4), request.getChunkSizes());
        }
        assertEquals(Arrays.toString(requestBody), Arrays.toString(request.getBody()));
    }

    enum StreamingMode {
        FIXED_LENGTH, CHUNKED
    }

    public void testAuthenticateWithPost() throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse()
                .setResponseCode(401)
                .addHeader("WWW-Authenticate: Basic realm=\"protected area\"")
                .setBody("Please authenticate.");
        // fail auth three times...
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        // ...then succeed the fourth time
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        server.play();

        Authenticator.setDefault(SIMPLE_AUTHENTICATOR);
        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        connection.setDoOutput(true);
        byte[] requestBody = { 'A', 'B', 'C', 'D' };
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody);
        outputStream.close();
        assertEquals("Successful auth!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        // no authorization header for the first request...
        RecordedRequest request = server.takeRequest();
        assertContainsNoneMatching(request.getHeaders(), "Authorization: Basic .*");

        // ...but the three requests that follow include an authorization header
        for (int i = 0; i < 3; i++) {
            request = server.takeRequest();
            assertEquals("POST / HTTP/1.1", request.getRequestLine());
            assertContains(request.getHeaders(), "Authorization: Basic " + BASE_64_CREDENTIALS);
            assertEquals(Arrays.toString(requestBody), Arrays.toString(request.getBody()));
        }
    }

    public void testAuthenticateWithGet() throws Exception {
        MockResponse pleaseAuthenticate = new MockResponse()
                .setResponseCode(401)
                .addHeader("WWW-Authenticate: Basic realm=\"protected area\"")
                .setBody("Please authenticate.");
        // fail auth three times...
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        server.enqueue(pleaseAuthenticate);
        // ...then succeed the fourth time
        server.enqueue(new MockResponse().setBody("Successful auth!"));
        server.play();

        Authenticator.setDefault(SIMPLE_AUTHENTICATOR);
        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        assertEquals("Successful auth!", readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        // no authorization header for the first request...
        RecordedRequest request = server.takeRequest();
        assertContainsNoneMatching(request.getHeaders(), "Authorization: Basic .*");

        // ...but the three requests that follow requests include an authorization header
        for (int i = 0; i < 3; i++) {
            request = server.takeRequest();
            assertEquals("GET / HTTP/1.1", request.getRequestLine());
            assertContains(request.getHeaders(), "Authorization: Basic " + BASE_64_CREDENTIALS);
        }
    }

    public void testRedirectedWithChunkedEncoding() throws Exception {
        testRedirected(TransferKind.CHUNKED, true);
    }

    public void testRedirectedWithContentLengthHeader() throws Exception {
        testRedirected(TransferKind.FIXED_LENGTH, true);
    }

    public void testRedirectedWithNoLengthHeaders() throws Exception {
        testRedirected(TransferKind.END_OF_STREAM, false);
    }

    private void testRedirected(TransferKind transferKind, boolean reuse) throws Exception {
        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
                .addHeader("Location: /foo");
        transferKind.setBody(response, "This page has moved!", 10);
        server.enqueue(response);
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        assertEquals("This is the new location!",
                readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        RecordedRequest first = server.takeRequest();
        assertEquals("GET / HTTP/1.1", first.getRequestLine());
        RecordedRequest retry = server.takeRequest();
        assertEquals("GET /foo HTTP/1.1", retry.getRequestLine());
        if (reuse) {
            assertEquals("Expected connection reuse", 1, retry.getSequenceNumber());
        }
    }

    public void testRedirectedOnHttps() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
                .addHeader("Location: /foo")
                .setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertEquals("This is the new location!",
                readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        RecordedRequest first = server.takeRequest();
        assertEquals("GET / HTTP/1.1", first.getRequestLine());
        RecordedRequest retry = server.takeRequest();
        assertEquals("GET /foo HTTP/1.1", retry.getRequestLine());
        assertEquals("Expected connection reuse", 1, retry.getSequenceNumber());
    }

    public void testNotRedirectedFromHttpsToHttp() throws IOException, InterruptedException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
                .addHeader("Location: http://anyhost/foo")
                .setBody("This page has moved!"));
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertEquals("This page has moved!",
                readAscii(connection.getInputStream(), Integer.MAX_VALUE));
    }

    public void testNotRedirectedFromHttpToHttps() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
                .addHeader("Location: https://anyhost/foo")
                .setBody("This page has moved!"));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        assertEquals("This page has moved!",
                readAscii(connection.getInputStream(), Integer.MAX_VALUE));
    }

    public void testRedirectToAnotherOriginServer() throws Exception {
        MockWebServer server2 = new MockWebServer();
        server2.enqueue(new MockResponse().setBody("This is the 2nd server!"));
        server2.play();

        server.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
                .addHeader("Location: " + server2.getUrl("/").toString())
                .setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("This is the first server again!"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        assertEquals("This is the 2nd server!",
                readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        assertEquals(server2.getUrl("/"), connection.getURL());

        // make sure the first server was careful to recycle the connection
        assertEquals("This is the first server again!",
                readAscii(server.getUrl("/").openStream(), Integer.MAX_VALUE));

        RecordedRequest first = server.takeRequest();
        assertContains(first.getHeaders(), "Host: " + hostName + ":" + server.getPort());
        RecordedRequest second = server2.takeRequest();
        assertContains(second.getHeaders(), "Host: " + hostName + ":" + server2.getPort());
        RecordedRequest third = server.takeRequest();
        assertEquals("Expected connection reuse", 1, third.getSequenceNumber());

        server2.shutdown();
    }

    public void testResponse300MultipleChoiceWithPost() throws Exception {
        // Chrome doesn't follow the redirect, but Firefox and the RI both do
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MULT_CHOICE);
    }

    public void testResponse301MovedPermanentlyWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MOVED_PERM);
    }

    public void testResponse302MovedTemporarilyWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_MOVED_TEMP);
    }

    public void testResponse303SeeOtherWithPost() throws Exception {
        testResponseRedirectedWithPost(HttpURLConnection.HTTP_SEE_OTHER);
    }

    private void testResponseRedirectedWithPost(int redirectCode) throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(redirectCode)
                .addHeader("Location: /page2")
                .setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("Page 2"));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/page1").openConnection();
        connection.setDoOutput(true);
        byte[] requestBody = { 'A', 'B', 'C', 'D' };
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody);
        outputStream.close();
        assertEquals("Page 2", readAscii(connection.getInputStream(), Integer.MAX_VALUE));
        assertTrue(connection.getDoOutput());

        RecordedRequest page1 = server.takeRequest();
        assertEquals("POST /page1 HTTP/1.1", page1.getRequestLine());
        assertEquals(Arrays.toString(requestBody), Arrays.toString(page1.getBody()));

        RecordedRequest page2 = server.takeRequest();
        assertEquals("GET /page2 HTTP/1.1", page2.getRequestLine());
    }

    public void testResponse305UseProxy() throws Exception {
        server.play();
        server.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_USE_PROXY)
                .addHeader("Location: " + server.getUrl("/"))
                .setBody("This page has moved!"));
        server.enqueue(new MockResponse().setBody("Proxy Response"));

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/foo").openConnection();
        // Fails on the RI, which gets "Proxy Response"
        assertEquals("This page has moved!",
                readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        RecordedRequest page1 = server.takeRequest();
        assertEquals("GET /foo HTTP/1.1", page1.getRequestLine());
        assertEquals(1, server.getRequestCount());
    }

    public void testHttpsWithCustomTrustManager() throws Exception {
        RecordingHostnameVerifier hostnameVerifier = new RecordingHostnameVerifier();
        RecordingTrustManager trustManager = new RecordingTrustManager();
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[] { trustManager }, new java.security.SecureRandom());

        HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        try {
            TestSSLContext testSSLContext = TestSSLContext.create();
            server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
            server.enqueue(new MockResponse().setBody("ABC"));
            server.enqueue(new MockResponse().setBody("DEF"));
            server.enqueue(new MockResponse().setBody("GHI"));
            server.play();

            URL url = server.getUrl("/");
            assertEquals("ABC", readAscii(url.openStream(), Integer.MAX_VALUE));
            assertEquals("DEF", readAscii(url.openStream(), Integer.MAX_VALUE));
            assertEquals("GHI", readAscii(url.openStream(), Integer.MAX_VALUE));

            assertEquals(Arrays.asList("verify " + hostName), hostnameVerifier.calls);
            assertEquals(Arrays.asList("checkServerTrusted ["
                    + "CN=" + hostName + " 1, "
                    + "CN=Test Intermediate Certificate Authority 1, "
                    + "CN=Test Root Certificate Authority 1"
                    + "] RSA"),
                    trustManager.calls);
        } finally {
            HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory);
        }
    }

    public void testConnectTimeouts() throws IOException {
        StuckServer ss = new StuckServer();
        int serverPort = ss.getLocalPort();
        URLConnection urlConnection = new URL("http://localhost:" + serverPort).openConnection();
        int timeout = 1000;
        urlConnection.setConnectTimeout(timeout);
        long start = System.currentTimeMillis();
        try {
            urlConnection.getInputStream();
            fail();
        } catch (SocketTimeoutException expected) {
            long actual = System.currentTimeMillis() - start;
            assertTrue(Math.abs(timeout - actual) < 500);
        } finally {
            ss.close();
        }
    }

    public void testReadTimeouts() throws IOException {
        /*
         * This relies on the fact that MockWebServer doesn't close the
         * connection after a response has been sent. This causes the client to
         * try to read more bytes than are sent, which results in a timeout.
         */
        MockResponse timeout = new MockResponse()
                .setBody("ABC")
                .clearHeaders()
                .addHeader("Content-Length: 4");
        server.enqueue(timeout);
        server.enqueue(new MockResponse().setBody("unused")); // to keep the server alive
        server.play();

        URLConnection urlConnection = server.getUrl("/").openConnection();
        urlConnection.setReadTimeout(1000);
        InputStream in = urlConnection.getInputStream();
        assertEquals('A', in.read());
        assertEquals('B', in.read());
        assertEquals('C', in.read());
        try {
            in.read(); // if Content-Length was accurate, this would return -1 immediately
            fail();
        } catch (SocketTimeoutException expected) {
        }
    }

    public void testSetChunkedEncodingAsRequestProperty() throws IOException, InterruptedException {
        server.enqueue(new MockResponse());
        server.play();

        HttpURLConnection urlConnection = (HttpURLConnection) server.getUrl("/").openConnection();
        urlConnection.setRequestProperty("Transfer-encoding", "chunked");
        urlConnection.setDoOutput(true);
        urlConnection.getOutputStream().write("ABC".getBytes("UTF-8"));
        assertEquals(200, urlConnection.getResponseCode());

        RecordedRequest request = server.takeRequest();
        assertEquals("ABC", new String(request.getBody(), "UTF-8"));
    }

    public void testConnectionCloseInRequest() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()); // server doesn't honor the connection: close header!
        server.enqueue(new MockResponse());
        server.play();

        HttpURLConnection a = (HttpURLConnection) server.getUrl("/").openConnection();
        a.setRequestProperty("Connection", "close");
        assertEquals(200, a.getResponseCode());

        HttpURLConnection b = (HttpURLConnection) server.getUrl("/").openConnection();
        assertEquals(200, b.getResponseCode());

        assertEquals(0, server.takeRequest().getSequenceNumber());
        assertEquals("When connection: close is used, each request should get its own connection",
                0, server.takeRequest().getSequenceNumber());
    }

    public void testConnectionCloseInResponse() throws IOException, InterruptedException {
        server.enqueue(new MockResponse().addHeader("Connection: close"));
        server.enqueue(new MockResponse());
        server.play();

        HttpURLConnection a = (HttpURLConnection) server.getUrl("/").openConnection();
        assertEquals(200, a.getResponseCode());

        HttpURLConnection b = (HttpURLConnection) server.getUrl("/").openConnection();
        assertEquals(200, b.getResponseCode());

        assertEquals(0, server.takeRequest().getSequenceNumber());
        assertEquals("When connection: close is used, each request should get its own connection",
                0, server.takeRequest().getSequenceNumber());
    }

    public void testConnectionCloseWithRedirect() throws IOException, InterruptedException {
        MockResponse response = new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_MOVED_TEMP)
                .addHeader("Location: /foo")
                .addHeader("Connection: close");
        server.enqueue(response);
        server.enqueue(new MockResponse().setBody("This is the new location!"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        assertEquals("This is the new location!",
                readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        assertEquals(0, server.takeRequest().getSequenceNumber());
        assertEquals("When connection: close is used, each request should get its own connection",
                0, server.takeRequest().getSequenceNumber());
    }

    public void testResponseCodeDisagreesWithHeaders() throws IOException, InterruptedException {
        server.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_NO_CONTENT)
                .setBody("This body is not allowed!"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        assertEquals("This body is not allowed!",
                readAscii(connection.getInputStream(), Integer.MAX_VALUE));
    }

    public void testSingleByteReadIsSigned() throws IOException {
        server.enqueue(new MockResponse().setBody(new byte[] { -2, -1 }));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        assertEquals(254, in.read());
        assertEquals(255, in.read());
        assertEquals(-1, in.read());
    }

    public void testFlushAfterStreamTransmittedWithChunkedEncoding() throws IOException {
        testFlushAfterStreamTransmitted(TransferKind.CHUNKED);
    }

    public void testFlushAfterStreamTransmittedWithFixedLength() throws IOException {
        testFlushAfterStreamTransmitted(TransferKind.FIXED_LENGTH);
    }

    public void testFlushAfterStreamTransmittedWithNoLengthHeaders() throws IOException {
        testFlushAfterStreamTransmitted(TransferKind.END_OF_STREAM);
    }

    /**
     * We explicitly permit apps to close the upload stream even after it has
     * been transmitted.  We also permit flush so that buffered streams can
     * do a no-op flush when they are closed. http://b/3038470
     */
    private void testFlushAfterStreamTransmitted(TransferKind transferKind) throws IOException {
        server.enqueue(new MockResponse().setBody("abc"));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        connection.setDoOutput(true);
        byte[] upload = "def".getBytes("UTF-8");

        if (transferKind == TransferKind.CHUNKED) {
            connection.setChunkedStreamingMode(0);
        } else if (transferKind == TransferKind.FIXED_LENGTH) {
            connection.setFixedLengthStreamingMode(upload.length);
        }

        OutputStream out = connection.getOutputStream();
        out.write(upload);
        assertEquals("abc", readAscii(connection.getInputStream(), Integer.MAX_VALUE));

        out.flush(); // dubious but permitted
        try {
            out.write("ghi".getBytes("UTF-8"));
            fail();
        } catch (IOException expected) {
        }
    }

    public void testGetHeadersThrows() throws IOException {
        server.enqueue(new MockResponse().setSocketPolicy(DISCONNECT_AT_START));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        try {
            connection.getInputStream();
            fail();
        } catch (IOException expected) {
        }

        try {
            connection.getInputStream();
            fail();
        } catch (IOException expected) {
        }
    }

    public void testGetKeepAlive() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("ABC"));
        server.play();

        // The request should work once and then fail
        URLConnection connection = server.getUrl("").openConnection();
        InputStream input = connection.getInputStream();
        assertEquals("ABC", readAscii(input, Integer.MAX_VALUE));
        input.close();
        try {
            server.getUrl("").openConnection().getInputStream();
            fail();
        } catch (ConnectException expected) {
        }
    }

    /**
     * This test goes through the exhaustive set of interesting ASCII characters
     * because most of those characters are interesting in some way according to
     * RFC 2396 and RFC 2732. http://b/1158780
     */
    public void testLenientUrlToUri() throws Exception {
        // alphanum
        testUrlToUriMapping("abzABZ09", "abzABZ09", "abzABZ09", "abzABZ09", "abzABZ09");

        // control characters
        testUrlToUriMapping("\u0001", "%01", "%01", "%01", "%01");
        testUrlToUriMapping("\u001f", "%1F", "%1F", "%1F", "%1F");

        // ascii characters
        testUrlToUriMapping("%20", "%20", "%20", "%20", "%20");
        testUrlToUriMapping("%20", "%20", "%20", "%20", "%20");
        testUrlToUriMapping(" ", "%20", "%20", "%20", "%20");
        testUrlToUriMapping("!", "!", "!", "!", "!");
        testUrlToUriMapping("\"", "%22", "%22", "%22", "%22");
        testUrlToUriMapping("#", null, null, null, "%23");
        testUrlToUriMapping("$", "$", "$", "$", "$");
        testUrlToUriMapping("&", "&", "&", "&", "&");
        testUrlToUriMapping("'", "'", "'", "'", "'");
        testUrlToUriMapping("(", "(", "(", "(", "(");
        testUrlToUriMapping(")", ")", ")", ")", ")");
        testUrlToUriMapping("*", "*", "*", "*", "*");
        testUrlToUriMapping("+", "+", "+", "+", "+");
        testUrlToUriMapping(",", ",", ",", ",", ",");
        testUrlToUriMapping("-", "-", "-", "-", "-");
        testUrlToUriMapping(".", ".", ".", ".", ".");
        testUrlToUriMapping("/", null, "/", "/", "/");
        testUrlToUriMapping(":", null, ":", ":", ":");
        testUrlToUriMapping(";", ";", ";", ";", ";");
        testUrlToUriMapping("<", "%3C", "%3C", "%3C", "%3C");
        testUrlToUriMapping("=", "=", "=", "=", "=");
        testUrlToUriMapping(">", "%3E", "%3E", "%3E", "%3E");
        testUrlToUriMapping("?", null, null, "?", "?");
        testUrlToUriMapping("@", "@", "@", "@", "@");
        testUrlToUriMapping("[", null, "%5B", null, "%5B");
        testUrlToUriMapping("\\", "%5C", "%5C", "%5C", "%5C");
        testUrlToUriMapping("]", null, "%5D", null, "%5D");
        testUrlToUriMapping("^", "%5E", "%5E", "%5E", "%5E");
        testUrlToUriMapping("_", "_", "_", "_", "_");
        testUrlToUriMapping("`", "%60", "%60", "%60", "%60");
        testUrlToUriMapping("{", "%7B", "%7B", "%7B", "%7B");
        testUrlToUriMapping("|", "%7C", "%7C", "%7C", "%7C");
        testUrlToUriMapping("}", "%7D", "%7D", "%7D", "%7D");
        testUrlToUriMapping("~", "~", "~", "~", "~");
        testUrlToUriMapping("~", "~", "~", "~", "~");
        testUrlToUriMapping("\u007f", "%7F", "%7F", "%7F", "%7F");

        // beyond ascii
        testUrlToUriMapping("\u0080", "%C2%80", "%C2%80", "%C2%80", "%C2%80");
        testUrlToUriMapping("\u20ac", "\u20ac", "\u20ac", "\u20ac", "\u20ac");
        testUrlToUriMapping("\ud842\udf9f",
                "\ud842\udf9f", "\ud842\udf9f", "\ud842\udf9f", "\ud842\udf9f");
    }

    public void testLenientUrlToUriNul() throws Exception {
        testUrlToUriMapping("\u0000", "%00", "%00", "%00", "%00"); // RI fails this
    }

    private void testUrlToUriMapping(String string, String asAuthority, String asFile,
            String asQuery, String asFragment) throws Exception {
        if (asAuthority != null) {
            assertEquals("http://host" + asAuthority + ".tld/",
                    backdoorUrlToUri(new URL("http://host" + string + ".tld/")).toString());
        }
        if (asFile != null) {
            assertEquals("http://host.tld/file" + asFile + "/",
                    backdoorUrlToUri(new URL("http://host.tld/file" + string + "/")).toString());
        }
        if (asQuery != null) {
            assertEquals("http://host.tld/file?q" + asQuery + "=x",
                    backdoorUrlToUri(new URL("http://host.tld/file?q" + string + "=x")).toString());
        }
        assertEquals("http://host.tld/file#" + asFragment + "-x",
                backdoorUrlToUri(new URL("http://host.tld/file#" + asFragment + "-x")).toString());
    }

    /**
     * Exercises HttpURLConnection to convert URL to a URI. Unlike URL#toURI,
     * HttpURLConnection recovers from URLs with unescaped but unsupported URI
     * characters like '{' and '|' by escaping these characters.
     */
    private URI backdoorUrlToUri(URL url) throws Exception {
        final AtomicReference<URI> uriReference = new AtomicReference<URI>();

        ResponseCache.setDefault(new ResponseCache() {
            @Override public CacheRequest put(URI uri, URLConnection connection) throws IOException {
                return null;
            }
            @Override public CacheResponse get(URI uri, String requestMethod,
                    Map<String, List<String>> requestHeaders) throws IOException {
                uriReference.set(uri);
                throw new UnsupportedOperationException();
            }
        });

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.getResponseCode();
        } catch (Exception expected) {
        }

        return uriReference.get();
    }

    /**
     * Don't explode if the cache returns a null body. http://b/3373699
     */
    public void testResponseCacheReturnsNullOutputStream() throws Exception {
        final AtomicBoolean aborted = new AtomicBoolean();
        ResponseCache.setDefault(new ResponseCache() {
            @Override public CacheResponse get(URI uri, String requestMethod,
                    Map<String, List<String>> requestHeaders) throws IOException {
                return null;
            }
            @Override public CacheRequest put(URI uri, URLConnection connection) throws IOException {
                return new CacheRequest() {
                    @Override public void abort() {
                        aborted.set(true);
                    }
                    @Override public OutputStream getBody() throws IOException {
                        return null;
                    }
                };
            }
        });

        server.enqueue(new MockResponse().setBody("abcdef"));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        assertEquals("abc", readAscii(in, 3));
        in.close();
        assertFalse(aborted.get()); // The best behavior is ambiguous, but RI 6 doesn't abort here
    }


    /**
     * http://code.google.com/p/android/issues/detail?id=14562
     */
    public void testReadAfterLastByte() throws Exception {
        server.enqueue(new MockResponse()
                .setBody("ABC")
                .clearHeaders()
                .addHeader("Connection: close")
                .setSocketPolicy(SocketPolicy.DISCONNECT_AT_END));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        assertEquals("ABC", readAscii(in, 3));
        assertEquals(-1, in.read());
        assertEquals(-1, in.read()); // throws IOException in Gingerbread
    }

    /**
     * Returns a gzipped copy of {@code bytes}.
     */
    public byte[] gzip(byte[] bytes) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        OutputStream gzippedOut = new GZIPOutputStream(bytesOut);
        gzippedOut.write(bytes);
        gzippedOut.close();
        return bytesOut.toByteArray();
    }

    /**
     * Reads at most {@code limit} characters from {@code in} and asserts that
     * content equals {@code expected}.
     */
    private void assertContent(String expected, URLConnection connection, int limit)
            throws IOException {
        connection.connect();
        assertEquals(expected, readAscii(connection.getInputStream(), limit));
        ((HttpURLConnection) connection).disconnect();
    }

    private void assertContent(String expected, URLConnection connection) throws IOException {
        assertContent(expected, connection, Integer.MAX_VALUE);
    }

    private void assertContains(List<String> headers, String header) {
        assertTrue(headers.toString(), headers.contains(header));
    }

    private void assertContainsNoneMatching(List<String> headers, String pattern) {
        for (String header : headers) {
            if (header.matches(pattern)) {
                fail("Header " + header + " matches " + pattern);
            }
        }
    }

    private Set<String> newSet(String... elements) {
        return new HashSet<String>(Arrays.asList(elements));
    }

    enum TransferKind {
        CHUNKED() {
            @Override void setBody(MockResponse response, byte[] content, int chunkSize)
                    throws IOException {
                response.setChunkedBody(content, chunkSize);
            }
        },
        FIXED_LENGTH() {
            @Override void setBody(MockResponse response, byte[] content, int chunkSize) {
                response.setBody(content);
            }
        },
        END_OF_STREAM() {
            @Override void setBody(MockResponse response, byte[] content, int chunkSize) {
                response.setBody(content);
                response.setSocketPolicy(DISCONNECT_AT_END);
                for (Iterator<String> h = response.getHeaders().iterator(); h.hasNext(); ) {
                    if (h.next().startsWith("Content-Length:")) {
                        h.remove();
                        break;
                    }
                }
            }
        };

        abstract void setBody(MockResponse response, byte[] content, int chunkSize)
                throws IOException;

        void setBody(MockResponse response, String content, int chunkSize) throws IOException {
            setBody(response, content.getBytes("UTF-8"), chunkSize);
        }
    }

    enum ProxyConfig {
        NO_PROXY() {
            @Override public HttpURLConnection connect(MockWebServer server, URL url)
                    throws IOException {
                return (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
            }
        },

        CREATE_ARG() {
            @Override public HttpURLConnection connect(MockWebServer server, URL url)
                    throws IOException {
                return (HttpURLConnection) url.openConnection(server.toProxyAddress());
            }
        },

        PROXY_SYSTEM_PROPERTY() {
            @Override public HttpURLConnection connect(MockWebServer server, URL url)
                    throws IOException {
                System.setProperty("proxyHost", "localhost");
                System.setProperty("proxyPort", Integer.toString(server.getPort()));
                return (HttpURLConnection) url.openConnection();
            }
        },

        HTTP_PROXY_SYSTEM_PROPERTY() {
            @Override public HttpURLConnection connect(MockWebServer server, URL url)
                    throws IOException {
                System.setProperty("http.proxyHost", "localhost");
                System.setProperty("http.proxyPort", Integer.toString(server.getPort()));
                return (HttpURLConnection) url.openConnection();
            }
        },

        HTTPS_PROXY_SYSTEM_PROPERTY() {
            @Override public HttpURLConnection connect(MockWebServer server, URL url)
                    throws IOException {
                System.setProperty("https.proxyHost", "localhost");
                System.setProperty("https.proxyPort", Integer.toString(server.getPort()));
                return (HttpURLConnection) url.openConnection();
            }
        };

        public abstract HttpURLConnection connect(MockWebServer server, URL url) throws IOException;
    }

    private static class RecordingTrustManager implements X509TrustManager {
        private final List<String> calls = new ArrayList<String>();

        public X509Certificate[] getAcceptedIssuers() {
            calls.add("getAcceptedIssuers");
            return new X509Certificate[] {};
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            calls.add("checkClientTrusted " + certificatesToString(chain) + " " + authType);
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            calls.add("checkServerTrusted " + certificatesToString(chain) + " " + authType);
        }

        private String certificatesToString(X509Certificate[] certificates) {
            List<String> result = new ArrayList<String>();
            for (X509Certificate certificate : certificates) {
                result.add(certificate.getSubjectDN() + " " + certificate.getSerialNumber());
            }
            return result.toString();
        }
    }

    private static class RecordingHostnameVerifier implements HostnameVerifier {
        private final List<String> calls = new ArrayList<String>();

        public boolean verify(String hostname, SSLSession session) {
            calls.add("verify " + hostname);
            return true;
        }
    }
}
