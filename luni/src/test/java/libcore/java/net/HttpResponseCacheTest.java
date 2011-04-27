/*
 * Copyright (C) 2011 The Android Open Source Project
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.SecureCacheResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import junit.framework.TestCase;
import libcore.javax.net.ssl.TestSSLContext;
import libcore.net.http.HttpDate;
import libcore.net.http.HttpResponseCache;
import tests.http.MockResponse;
import tests.http.MockWebServer;
import tests.http.RecordedRequest;
import static tests.http.SocketPolicy.DISCONNECT_AT_END;

public final class HttpResponseCacheTest extends TestCase {
    private MockWebServer server = new MockWebServer();
    private HttpResponseCache cache = new HttpResponseCache();

    @Override protected void setUp() throws Exception {
        super.setUp();
        ResponseCache.setDefault(cache);
    }

    @Override protected void tearDown() throws Exception {
        ResponseCache.setDefault(null);
        server.shutdown();
        super.tearDown();
    }

    /**
     * Test that response caching is consistent with the RI and the spec.
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.4
     */
    public void test_responseCaching() throws Exception {
        // Test each documented HTTP/1.1 code, plus the first unused value in each range.
        // http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html

        // We can't test 100 because it's not really a response.
        // assertCached(false, 100);
        assertCached(false, 101);
        assertCached(false, 102);
        assertCached(true,  200);
        assertCached(false, 201);
        assertCached(false, 202);
        assertCached(true,  203);
        assertCached(false, 204);
        assertCached(false, 205);
        assertCached(true,  206);
        assertCached(false, 207);
        // (See test_responseCaching_300.)
        assertCached(true,  301);
        for (int i = 302; i <= 308; ++i) {
            assertCached(false, i);
        }
        for (int i = 400; i <= 406; ++i) {
            assertCached(false, i);
        }
        // (See test_responseCaching_407.)
        assertCached(false, 408);
        assertCached(false, 409);
        // (See test_responseCaching_410.)
        for (int i = 411; i <= 418; ++i) {
            assertCached(false, i);
        }
        for (int i = 500; i <= 506; ++i) {
            assertCached(false, i);
        }
    }

    public void test_responseCaching_300() throws Exception {
        // TODO: fix this for android
        assertCached(false, 300);
    }

    /**
     * Response code 407 should only come from proxy servers. Android's client
     * throws if it is sent by an origin server.
     */
    public void testOriginServerSends407() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(407));
        server.play();

        URL url = server.getUrl("/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.getResponseCode();
            fail();
        } catch (IOException expected) {
        }
    }

    public void test_responseCaching_410() throws Exception {
        // the HTTP spec permits caching 410s, but the RI doesn't.
        assertCached(false, 410);
    }

    private void assertCached(boolean shouldPut, int responseCode) throws Exception {
        server = new MockWebServer();
        server.enqueue(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .setResponseCode(responseCode)
                .setBody("ABCDE")
                .addHeader("WWW-Authenticate: challenge"));
        server.play();

        URL url = server.getUrl("/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        assertEquals(responseCode, conn.getResponseCode());

        // exhaust the content stream
        try {
            // TODO: remove special case once testUnauthorizedResponseHandling() is fixed
            if (responseCode != 401) {
                readAscii(conn);
            }
        } catch (IOException expected) {
        }

        Set<URI> expectedCachedUris = shouldPut
                ? Collections.singleton(url.toURI())
                : Collections.<URI>emptySet();
        assertEquals(Integer.toString(responseCode),
                expectedCachedUris, cache.getContents().keySet());
        server.shutdown(); // tearDown() isn't sufficient; this test starts multiple servers
    }

    /**
     * Test that we can interrogate the response when the cache is being
     * populated. http://code.google.com/p/android/issues/detail?id=7787
     */
    public void testResponseCacheCallbackApis() throws Exception {
        final String body = "ABCDE";
        final AtomicInteger cacheCount = new AtomicInteger();

        server.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 200 Fantastic")
                .addHeader("fgh: ijk")
                .setBody(body));
        server.play();

        ResponseCache.setDefault(new ResponseCache() {
            @Override public CacheResponse get(URI uri, String requestMethod,
                    Map<String, List<String>> requestHeaders) throws IOException {
                return null;
            }
            @Override public CacheRequest put(URI uri, URLConnection conn) throws IOException {
                HttpURLConnection httpConnection = (HttpURLConnection) conn;
                assertEquals("HTTP/1.1 200 Fantastic", httpConnection.getHeaderField(null));
                assertEquals(Arrays.asList("HTTP/1.1 200 Fantastic"),
                        httpConnection.getHeaderFields().get(null));
                assertEquals(200, httpConnection.getResponseCode());
                assertEquals("Fantastic", httpConnection.getResponseMessage());
                assertEquals(body.length(), httpConnection.getContentLength());
                assertEquals("ijk", httpConnection.getHeaderField("fgh"));
                try {
                    httpConnection.getInputStream(); // the RI doesn't forbid this, but it should
                    fail();
                } catch (IOException expected) {
                }
                cacheCount.incrementAndGet();
                return null;
            }
        });

        URL url = server.getUrl("/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        assertEquals(body, readAscii(connection));
        assertEquals(1, cacheCount.get());
    }


    public void testResponseCachingAndInputStreamSkipWithFixedLength() throws IOException {
        testResponseCaching(TransferKind.FIXED_LENGTH);
    }

    public void testResponseCachingAndInputStreamSkipWithChunkedEncoding() throws IOException {
        testResponseCaching(TransferKind.CHUNKED);
    }

    public void testResponseCachingAndInputStreamSkipWithNoLengthHeaders() throws IOException {
        testResponseCaching(TransferKind.END_OF_STREAM);
    }

    /**
     * HttpURLConnection.getInputStream().skip(long) causes ResponseCache corruption
     * http://code.google.com/p/android/issues/detail?id=8175
     */
    private void testResponseCaching(TransferKind transferKind) throws IOException {
        MockResponse response = new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .setStatus("HTTP/1.1 200 Fantastic");
        transferKind.setBody(response, "I love puppies but hate spiders", 1);
        server.enqueue(response);
        server.play();

        // Make sure that calling skip() doesn't omit bytes from the cache.
        HttpURLConnection urlConnection = (HttpURLConnection) server.getUrl("/").openConnection();
        InputStream in = urlConnection.getInputStream();
        assertEquals("I love ", readAscii(urlConnection, "I love ".length()));
        reliableSkip(in, "puppies but hate ".length());
        assertEquals("spiders", readAscii(urlConnection, "spiders".length()));
        assertEquals(-1, in.read());
        in.close();
        assertEquals(1, cache.getSuccessCount());
        assertEquals(0, cache.getAbortCount());

        urlConnection = (HttpURLConnection) server.getUrl("/").openConnection(); // cached!
        in = urlConnection.getInputStream();
        assertEquals("I love puppies but hate spiders",
                readAscii(urlConnection, "I love puppies but hate spiders".length()));
        assertEquals(200, urlConnection.getResponseCode());
        assertEquals("Fantastic", urlConnection.getResponseMessage());

        assertEquals(-1, in.read());
        assertEquals(1, cache.getMissCount());
        assertEquals(1, cache.getHitCount());
        assertEquals(1, cache.getSuccessCount());
        assertEquals(0, cache.getAbortCount());
    }

    public void testSecureResponseCaching() throws IOException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .setBody("ABC"));
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertEquals("ABC", readAscii(connection));

        // OpenJDK 6 fails on this line, complaining that the connection isn't open yet
        String suite = connection.getCipherSuite();
        List<Certificate> localCerts = toListOrNull(connection.getLocalCertificates());
        List<Certificate> serverCerts = toListOrNull(connection.getServerCertificates());
        Principal peerPrincipal = connection.getPeerPrincipal();
        Principal localPrincipal = connection.getLocalPrincipal();

        connection = (HttpsURLConnection) server.getUrl("/").openConnection(); // cached!
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertEquals("ABC", readAscii(connection));

        assertEquals(1, cache.getMissCount());
        assertEquals(1, cache.getHitCount());

        assertEquals(suite, connection.getCipherSuite());
        assertEquals(localCerts, toListOrNull(connection.getLocalCertificates()));
        assertEquals(serverCerts, toListOrNull(connection.getServerCertificates()));
        assertEquals(peerPrincipal, connection.getPeerPrincipal());
        assertEquals(localPrincipal, connection.getLocalPrincipal());
    }

    public void testCacheReturnsInsecureResponseForSecureRequest() throws IOException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse().setBody("ABC"));
        server.enqueue(new MockResponse().setBody("DEF"));
        server.play();

        ResponseCache.setDefault(new InsecureResponseCache());

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertEquals("ABC", readAscii(connection));

        connection = (HttpsURLConnection) server.getUrl("/").openConnection(); // not cached!
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertEquals("DEF", readAscii(connection));
    }

    public void testResponseCachingAndRedirects() throws IOException {
        server.enqueue(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .setResponseCode(HttpURLConnection.HTTP_MOVED_PERM)
                .addHeader("Location: /foo"));
        server.enqueue(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .setBody("ABC"));
        server.enqueue(new MockResponse().setBody("DEF"));
        server.play();

        HttpURLConnection connection = (HttpURLConnection) server.getUrl("/").openConnection();
        assertEquals("ABC", readAscii(connection));

        connection = (HttpURLConnection) server.getUrl("/").openConnection(); // cached!
        assertEquals("ABC", readAscii(connection));

        assertEquals(2, cache.getMissCount()); // 1 redirect + 1 final response = 2
        assertEquals(2, cache.getHitCount());
    }

    public void testSecureResponseCachingAndRedirects() throws IOException {
        TestSSLContext testSSLContext = TestSSLContext.create();
        server.useHttps(testSSLContext.serverContext.getSocketFactory(), false);
        server.enqueue(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .setResponseCode(HttpURLConnection.HTTP_MOVED_PERM)
                .addHeader("Location: /foo"));
        server.enqueue(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .setBody("ABC"));
        server.enqueue(new MockResponse().setBody("DEF"));
        server.play();

        HttpsURLConnection connection = (HttpsURLConnection) server.getUrl("/").openConnection();
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertEquals("ABC", readAscii(connection));

        connection = (HttpsURLConnection) server.getUrl("/").openConnection(); // cached!
        connection.setSSLSocketFactory(testSSLContext.clientContext.getSocketFactory());
        assertEquals("ABC", readAscii(connection));

        assertEquals(2, cache.getMissCount()); // 1 redirect + 1 final response = 2
        assertEquals(2, cache.getHitCount());
    }

    public void testResponseCacheRequestHeaders() throws IOException, URISyntaxException {
        server.enqueue(new MockResponse().setBody("ABC"));
        server.play();

        final AtomicReference<Map<String, List<String>>> requestHeadersRef
                = new AtomicReference<Map<String, List<String>>>();
        ResponseCache.setDefault(new ResponseCache() {
            @Override public CacheResponse get(URI uri, String requestMethod,
                    Map<String, List<String>> requestHeaders) throws IOException {
                requestHeadersRef.set(requestHeaders);
                return null;
            }
            @Override public CacheRequest put(URI uri, URLConnection conn) throws IOException {
                return null;
            }
        });

        URL url = server.getUrl("/");
        URLConnection urlConnection = url.openConnection();
        urlConnection.addRequestProperty("A", "android");
        readAscii(urlConnection);
        assertEquals(Arrays.asList("android"), requestHeadersRef.get().get("A"));
    }


    public void testServerDisconnectsPrematurelyWithContentLengthHeader() throws IOException {
        testServerPrematureDisconnect(TransferKind.FIXED_LENGTH);
    }

    public void testServerDisconnectsPrematurelyWithChunkedEncoding() throws IOException {
        testServerPrematureDisconnect(TransferKind.CHUNKED);
    }

    public void testServerDisconnectsPrematurelyWithNoLengthHeaders() throws IOException {
        /*
         * Intentionally empty. This case doesn't make sense because there's no
         * such thing as a premature disconnect when the disconnect itself
         * indicates the end of the data stream.
         */
    }

    private void testServerPrematureDisconnect(TransferKind transferKind) throws IOException {
        MockResponse response = new MockResponse();
        transferKind.setBody(response, "ABCDE\nFGHIJKLMNOPQRSTUVWXYZ", 16);
        server.enqueue(truncateViolently(response, 16));
        server.enqueue(new MockResponse().setBody("Request #2"));
        server.play();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                server.getUrl("/").openConnection().getInputStream()));
        assertEquals("ABCDE", reader.readLine());
        try {
            reader.readLine();
            fail("This implementation silently ignored a truncated HTTP body.");
        } catch (IOException expected) {
        }

        assertEquals(1, cache.getAbortCount());
        assertEquals(0, cache.getSuccessCount());
        URLConnection connection = server.getUrl("/").openConnection();
        assertEquals("Request #2", readAscii(connection));
        assertEquals(1, cache.getAbortCount());
        assertEquals(1, cache.getSuccessCount());
    }

    public void testClientPrematureDisconnectWithContentLengthHeader() throws IOException {
        testClientPrematureDisconnect(TransferKind.FIXED_LENGTH);
    }

    public void testClientPrematureDisconnectWithChunkedEncoding() throws IOException {
        testClientPrematureDisconnect(TransferKind.CHUNKED);
    }

    public void testClientPrematureDisconnectWithNoLengthHeaders() throws IOException {
        testClientPrematureDisconnect(TransferKind.END_OF_STREAM);
    }

    private void testClientPrematureDisconnect(TransferKind transferKind) throws IOException {
        MockResponse response = new MockResponse();
        transferKind.setBody(response, "ABCDE\nFGHIJKLMNOPQRSTUVWXYZ", 1024);
        server.enqueue(response);
        server.enqueue(new MockResponse().setBody("Request #2"));
        server.play();

        URLConnection connection = server.getUrl("/").openConnection();
        InputStream in = connection.getInputStream();
        assertEquals("ABCDE", readAscii(connection, 5));
        in.close();
        try {
            in.read();
            fail("Expected an IOException because the stream is closed.");
        } catch (IOException expected) {
        }

        assertEquals(1, cache.getAbortCount());
        assertEquals(0, cache.getSuccessCount());
        connection = server.getUrl("/").openConnection();
        assertEquals("Request #2", readAscii(connection, Integer.MAX_VALUE));
        assertEquals(1, cache.getAbortCount());
        assertEquals(1, cache.getSuccessCount());
    }

    public void testLastModifiedHeaderInThePast() throws Exception {
        // TODO: firefox uses a +10% heuristic in this case.
        // For example, this should be validated after 12 minutes (10% of two hours)
        String lastModifiedDate = formatDate(-2, TimeUnit.HOURS);
        RecordedRequest conditionalRequest = assertConditionallyCached(new MockResponse()
                .addHeader("Last-Modified: " + lastModifiedDate));
        List<String> headers = conditionalRequest.getHeaders();
        assertTrue(headers.contains("If-Modified-Since: " + lastModifiedDate));
    }

    public void testExpirationDateInThePastWithLastModifiedHeader() throws Exception {
        String lastModifiedDate = formatDate(-2, TimeUnit.HOURS);
        RecordedRequest conditionalRequest = assertConditionallyCached(new MockResponse()
                .addHeader("Last-Modified: " + lastModifiedDate)
                .addHeader("Expires: " + formatDate(-1, TimeUnit.HOURS)));
        List<String> headers = conditionalRequest.getHeaders();
        assertTrue(headers.contains("If-Modified-Since: " + lastModifiedDate));
    }

    public void testExpirationDateInThePastWithNoLastModifiedHeader() throws Exception {
        assertNotCached(new MockResponse()
                .addHeader("Expires: " + formatDate(-1, TimeUnit.HOURS)));
    }

    public void testExpirationDateInTheFuture() throws Exception {
        assertFullyCached(new MockResponse()
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS)));
    }

    public void testMaxAgePreferredWithMaxAgeAndExpires() throws Exception {
        assertFullyCached(new MockResponse()
                .addHeader("Date: " + formatDate(0, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Cache-Control: max-age=60"));
    }

    public void testMaxAgeInThePastWithDateAndLastModifiedHeaders() throws Exception {
        String lastModifiedDate = formatDate(-2, TimeUnit.HOURS);
        RecordedRequest conditionalRequest = assertConditionallyCached(new MockResponse()
                .addHeader("Date: " + formatDate(-120, TimeUnit.SECONDS))
                .addHeader("Last-Modified: " + lastModifiedDate)
                .addHeader("Cache-Control: max-age=60"));
        List<String> headers = conditionalRequest.getHeaders();
        assertTrue(headers.contains("If-Modified-Since: " + lastModifiedDate));
    }

    public void testMaxAgeInThePastWithDateHeaderButNoLastModifiedHeader() throws Exception {
        assertNotCached(new MockResponse()
                .addHeader("Date: " + formatDate(-120, TimeUnit.SECONDS))
                .addHeader("Cache-Control: max-age=60"));
    }

    public void testMaxAgeInTheFutureWithDateHeader() throws Exception {
        assertFullyCached(new MockResponse()
                .addHeader("Date: " + formatDate(0, TimeUnit.HOURS))
                .addHeader("Cache-Control: max-age=60"));
    }

    public void testMaxAgeInTheFutureWithNoDateHeader() throws Exception {
        // TODO: this is failing until we default 'Date:' to the request date
        assertFullyCached(new MockResponse()
                .addHeader("Cache-Control: max-age=60"));
    }

    public void testRequestMethodOptionsIsNotCached() throws Exception {
        testRequestMethod("OPTIONS", false);
    }

    public void testRequestMethodGetIsCached() throws Exception {
        testRequestMethod("GET", true);
    }

    public void testRequestMethodHeadIsNotCached() throws Exception {
        // We could support this but choose not to for implementation simplicity
        testRequestMethod("HEAD", false);
    }

    public void testRequestMethodPostIsNotCached() throws Exception {
        // We could support this but choose not to for implementation simplicity
        testRequestMethod("POST", false);
    }

    public void testRequestMethodPutIsNotCached() throws Exception {
        testRequestMethod("PUT", false);
    }

    public void testRequestMethodDeleteIsNotCached() throws Exception {
        testRequestMethod("DELETE", false);
    }

    public void testRequestMethodTraceIsNotCached() throws Exception {
        testRequestMethod("TRACE", false);
    }

    private void testRequestMethod(String requestMethod, boolean expectCached) throws Exception {
        /*
         * 1. seed the cache (potentially)
         * 2. expect a cache hit or miss
         */
        server.enqueue(new MockResponse()
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .addHeader("X-Response-ID: 1"));
        server.enqueue(new MockResponse()
                .addHeader("X-Response-ID: 2"));
        server.play();

        URL url = server.getUrl("/");

        HttpURLConnection request1 = (HttpURLConnection) url.openConnection();
        request1.setRequestMethod(requestMethod);
        addRequestBodyIfNecessary(requestMethod, request1);
        assertEquals("1", request1.getHeaderField("X-Response-ID"));

        HttpURLConnection request2 = (HttpURLConnection) url.openConnection();
        if (expectCached) {
            assertEquals("1", request1.getHeaderField("X-Response-ID"));
        } else {
            assertEquals("2", request2.getHeaderField("X-Response-ID"));
        }
    }

    public void testPostInvalidatesCache() throws Exception {
        testMethodInvalidates("POST");
    }

    public void testPutInvalidatesCache() throws Exception {
        testMethodInvalidates("PUT");
    }

    public void testDeleteMethodInvalidatesCache() throws Exception {
        testMethodInvalidates("DELETE");
    }

    private void testMethodInvalidates(String requestMethod) throws Exception {
        /*
         * 1. seed the cache
         * 2. invalidate it
         * 3. expect a cache miss
         */
        server.enqueue(new MockResponse().setBody("A")
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS)));
        server.enqueue(new MockResponse().setBody("B"));
        server.enqueue(new MockResponse().setBody("C"));
        server.play();

        URL url = server.getUrl("/");

        assertEquals("A", readAscii(url.openConnection()));

        HttpURLConnection invalidate = (HttpURLConnection) url.openConnection();
        invalidate.setRequestMethod(requestMethod);
        addRequestBodyIfNecessary(requestMethod, invalidate);
        assertEquals("B", readAscii(invalidate));

        assertEquals("C", readAscii(url.openConnection()));
    }

    public void testEtag() throws Exception {
        RecordedRequest conditionalRequest = assertConditionallyCached(new MockResponse()
                .addHeader("ETag: v1"));
        assertTrue(conditionalRequest.getHeaders().contains("If-None-Match: v1"));
    }

    public void testEtagAndExpirationDateInThePast() throws Exception {
        String lastModifiedDate = formatDate(-2, TimeUnit.HOURS);
        RecordedRequest conditionalRequest = assertConditionallyCached(new MockResponse()
                .addHeader("ETag: v1")
                .addHeader("Last-Modified: " + lastModifiedDate)
                .addHeader("Expires: " + formatDate(-1, TimeUnit.HOURS)));
        List<String> headers = conditionalRequest.getHeaders();
        assertTrue(headers.contains("If-None-Match: v1"));
        assertTrue(headers.contains("If-Modified-Since: " + lastModifiedDate));
    }

    public void testEtagAndExpirationDateInTheFuture() throws Exception {
        assertFullyCached(new MockResponse()
                .addHeader("ETag: v1")
                .addHeader("Last-Modified: " + formatDate(-2, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS)));
    }

    public void testCacheControlNoCache() throws Exception {
        assertNotCached(new MockResponse().addHeader("Cache-Control: no-cache"));
    }

    public void testCacheControlNoCacheAndExpirationDateInTheFuture() throws Exception {
        String lastModifiedDate = formatDate(-2, TimeUnit.HOURS);
        RecordedRequest conditionalRequest = assertConditionallyCached(new MockResponse()
                .addHeader("Last-Modified: " + lastModifiedDate)
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .addHeader("Cache-Control: no-cache"));
        List<String> headers = conditionalRequest.getHeaders();
        assertTrue(headers.contains("If-Modified-Since: " + lastModifiedDate));
    }

    public void testPragmaNoCache() throws Exception {
        assertNotCached(new MockResponse().addHeader("Pragma: no-cache"));
    }

    public void testPragmaNoCacheAndExpirationDateInTheFuture() throws Exception {
        String lastModifiedDate = formatDate(-2, TimeUnit.HOURS);
        RecordedRequest conditionalRequest = assertConditionallyCached(new MockResponse()
                .addHeader("Last-Modified: " + lastModifiedDate)
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .addHeader("Pragma: no-cache"));
        List<String> headers = conditionalRequest.getHeaders();
        assertTrue(headers.contains("If-Modified-Since: " + lastModifiedDate));
    }

    public void testCacheControlNoStore() throws Exception {
        assertNotCached(new MockResponse().addHeader("Cache-Control: no-store"));
    }

    public void testCacheControlNoStoreAndExpirationDateInTheFuture() throws Exception {
        assertNotCached(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-2, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .addHeader("Cache-Control: no-store"));
    }

    public void testPartialRangeResponsesDoNotCorruptCache() throws Exception {
        /*
         * 1. request a range
         * 2. request a full document, expecting a cache miss
         */
        server.enqueue(new MockResponse().setBody("AA")
                .setResponseCode(HttpURLConnection.HTTP_PARTIAL)
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS))
                .addHeader("Content-Range: bytes 1000-1001/2000"));
        server.enqueue(new MockResponse().setBody("BB"));
        server.play();

        URL url = server.getUrl("/");

        URLConnection range = url.openConnection();
        range.addRequestProperty("Range", "bytes=1000-1001");
        assertEquals("AA", readAscii(range));

        assertEquals("BB", readAscii(url.openConnection()));
    }

    public void testServerReturnsDocumentOlderThanCache() throws Exception {
        server.enqueue(new MockResponse().setBody("A")
                .addHeader("Last-Modified: " + formatDate(-2, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(-1, TimeUnit.HOURS)));
        server.enqueue(new MockResponse().setBody("B")
                .addHeader("Last-Modified: " + formatDate(-4, TimeUnit.HOURS)));
        server.play();

        URL url = server.getUrl("/");

        assertEquals("A", readAscii(url.openConnection()));
        assertEquals("A", readAscii(url.openConnection()));
    }

    public void testNonIdentityEncodingAndConditionalCache() throws Exception {
        assertNonIdentityEncodingCached(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-2, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(-1, TimeUnit.HOURS)));
    }

    public void testNonIdentityEncodingAndFullCache() throws Exception {
        assertNonIdentityEncodingCached(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-2, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(1, TimeUnit.HOURS)));
    }

    private void assertNonIdentityEncodingCached(MockResponse response) throws Exception {
        server.enqueue(response
                .setBody(gzip("ABCABCABC".getBytes("UTF-8")))
                .addHeader("Content-Encoding: gzip"));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED));

        server.play();
        assertEquals("ABCABCABC", readAscii(server.getUrl("/").openConnection()));
        assertEquals("ABCABCABC", readAscii(server.getUrl("/").openConnection()));
    }

    public void testExpiresDateBeforeModifiedDate() throws Exception {
        assertConditionallyCached(new MockResponse()
                .addHeader("Last-Modified: " + formatDate(-1, TimeUnit.HOURS))
                .addHeader("Expires: " + formatDate(-2, TimeUnit.HOURS)));
    }

    /**
     * @param delta the offset from the current date to use. Negative
     *     values yield dates in the past; positive values yield dates in the
     *     future.
     */
    public String formatDate(long delta, TimeUnit timeUnit) {
        return HttpDate.format(new Date(System.currentTimeMillis() + timeUnit.toMillis(delta)));
    }

    private void addRequestBodyIfNecessary(String requestMethod, HttpURLConnection invalidate)
            throws IOException {
        if (requestMethod.equals("POST") || requestMethod.equals("PUT")) {
            invalidate.setDoOutput(true);
            OutputStream requestBody = invalidate.getOutputStream();
            requestBody.write('x');
            requestBody.close();
        }
    }

    private void assertNotCached(MockResponse response) throws Exception {
        server.enqueue(response.setBody("A"));
        server.enqueue(new MockResponse().setBody("B"));
        server.play();

        URL url = server.getUrl("/");
        assertEquals("A", readAscii(url.openConnection()));
        assertEquals("B", readAscii(url.openConnection()));
    }

    /**
     * @return the request with the conditional get headers.
     */
    private RecordedRequest assertConditionallyCached(MockResponse response) throws Exception {
        // scenario 1: condition succeeds
        server.enqueue(response.setBody("A"));
        server.enqueue(new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED));

        // scenario 2: condition fails
        server.enqueue(response.setBody("B"));
        server.enqueue(new MockResponse().setBody("C"));

        server.play();

        URL valid = server.getUrl("/valid");
        assertEquals("A", readAscii(valid.openConnection()));
        assertEquals("A", readAscii(valid.openConnection()));

        URL invalid = server.getUrl("/invalid");
        assertEquals("B", readAscii(invalid.openConnection()));
        assertEquals("C", readAscii(invalid.openConnection()));

        server.takeRequest(); // regular get
        return server.takeRequest(); // conditional get
    }

    private void assertFullyCached(MockResponse response) throws Exception {
        server.enqueue(response.setBody("A"));
        server.enqueue(response.setBody("B"));
        server.play();

        URL url = server.getUrl("/");
        assertEquals("A", readAscii(url.openConnection()));
        assertEquals("A", readAscii(url.openConnection()));
    }

    /**
     * Shortens the body of {@code response} but not the corresponding headers.
     * Only useful to test how clients respond to the premature conclusion of
     * the HTTP body.
     */
    private MockResponse truncateViolently(MockResponse response, int numBytesToKeep) {
        response.setSocketPolicy(DISCONNECT_AT_END);
        List<String> headers = new ArrayList<String>(response.getHeaders());
        response.setBody(Arrays.copyOfRange(response.getBody(), 0, numBytesToKeep));
        response.getHeaders().clear();
        response.getHeaders().addAll(headers);
        return response;
    }

    /**
     * Reads {@code count} characters from the stream. If the stream is
     * exhausted before {@code count} characters can be read, the remaining
     * characters are returned and the stream is closed.
     */
    private String readAscii(URLConnection connection, int count) throws IOException {
        InputStream in = connection.getInputStream();
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

    private String readAscii(URLConnection connection) throws IOException {
        return readAscii(connection, Integer.MAX_VALUE);
    }

    private void reliableSkip(InputStream in, int length) throws IOException {
        while (length > 0) {
            length -= in.skip(length);
        }
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

    private <T> List<T> toListOrNull(T[] arrayOrNull) {
        return arrayOrNull != null ? Arrays.asList(arrayOrNull) : null;
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

    private static class InsecureResponseCache extends ResponseCache {
        private final HttpResponseCache delegate = new HttpResponseCache();

        @Override public CacheRequest put(URI uri, URLConnection connection) throws IOException {
            return delegate.put(uri, connection);
        }

        @Override public CacheResponse get(URI uri, String requestMethod,
                Map<String, List<String>> requestHeaders) throws IOException {
            final CacheResponse response = delegate.get(uri, requestMethod, requestHeaders);
            if (response instanceof SecureCacheResponse) {
                return new CacheResponse() {
                    @Override public InputStream getBody() throws IOException {
                        return response.getBody();
                    }
                    @Override public Map<String, List<String>> getHeaders() throws IOException {
                        return response.getHeaders();
                    }
                };
            }
            return response;
        }
    }
}
