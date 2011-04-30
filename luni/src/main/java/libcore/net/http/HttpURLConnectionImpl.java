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

package libcore.net.http;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.CookieHandler;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.ResponseCache;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charsets;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import libcore.io.IoUtils;
import libcore.io.Streams;
import org.apache.harmony.luni.util.Base64;

/**
 * This subclass extends <code>HttpURLConnection</code> which in turns extends
 * <code>URLConnection</code> This is the actual class that "does the work",
 * such as connecting, sending request and getting the content from the remote
 * server.
 *
 * <h3>What does 'connected' mean?</h3>
 * This class inherits a {@code connected} field from the superclass. That field
 * is <strong>not</strong> used to indicate not whether this URLConnection is
 * currently connected. Instead, it indicates whether a connection has ever been
 * attempted. Once a connection has been attempted, certain properties (request
 * headers, request method, etc.) are immutable. Test the {@code connection}
 * field on this class for null/non-null to determine of an instance is
 * currently connected to a server.
 */
public class HttpURLConnectionImpl extends HttpURLConnection {
    public static final String OPTIONS = "OPTIONS";
    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String TRACE = "TRACE";
    public static final String CONNECT = "CONNECT";

    public static final int HTTP_CONTINUE = 100;

    /**
     * HTTP 1.1 doesn't specify how many redirects to follow, but HTTP/1.0
     * recommended 5. http://www.w3.org/Protocols/HTTP/1.0/spec.html#Code3xx
     */
    public static final int MAX_REDIRECTS = 5;

    /**
     * The subset of HTTP methods that the user may select via {@link
     * #setRequestMethod(String)}.
     */
    public static String PERMITTED_USER_METHODS[] = {
            OPTIONS,
            GET,
            HEAD,
            POST,
            PUT,
            DELETE,
            TRACE
            // Note: we don't allow users to specify "CONNECT"
    };

    public static final int DEFAULT_CHUNK_LENGTH = 1024;

    /**
     * The maximum number of bytes to buffer when sending a header and a request
     * body. When the header and body can be sent in a single write, the request
     * completes sooner. In one WiFi benchmark, using a large enough buffer sped
     * up some uploads by half.
     */
    private static final int MAX_REQUEST_BUFFER_LENGTH = 32768;

    private final int defaultPort;

    /**
     * The version this client will use. Either 0 for HTTP/1.0, or 1 for
     * HTTP/1.1. Upon receiving a non-HTTP/1.1 response, this client
     * automatically sets its version to HTTP/1.0.
     */
    private int httpMinorVersion = 1; // Assume HTTP/1.1

    protected HttpConnection connection;
    private InputStream socketIn;
    private OutputStream socketOut;

    /**
     * This stream buffers the request header and the request body when their
     * combined size is less than MAX_REQUEST_BUFFER_LENGTH. By combining them
     * we can save socket writes, which in turn saves a packet transmission.
     * This is socketOut if the request size is large or unknown.
     */
    private OutputStream requestOut;

    private AbstractHttpOutputStream requestBodyOut;

    protected InputStream responseBodyIn;

    private ResponseCache responseCache;

    protected CacheResponse cacheResponse;

    private CacheRequest cacheRequest;

    private ResponseSource responseSource;

    private long sentRequestMillis;
    private long receivedResponseMillis;
    private boolean sentRequestHeaders;

    /**
     * True if this client added an "Accept-Encoding: gzip" header and is
     * therefore responsible for also decompressing the transfer stream.
     */
    private boolean transparentGzip = false;

    boolean sendChunked;

    // proxy which is used to make the connection.
    private Proxy proxy;

    // the destination URI
    private URI uri;

    private static HttpHeaders defaultRequestHeader = new HttpHeaders();

    private final HttpHeaders requestHeader;

    /** Null until a response is received from the network or the cache */
    private HttpHeaders responseHeader;

    private int redirectionCount;

    /**
     * Intermediate responses are always followed by another request for the
     * same content, possibly from a different URL or with different headers.
     */
    protected boolean intermediateResponse = false;

    /*
     * The cache response currently being validated on a conditional get. Null
     * if the cached response doesn't exist or doesn't need validation. If the
     * conditional get succeeds, these will be used for the response header and
     * body. If it fails, these be closed and set to null.
     */
    private CacheHeader cacheResponseHeader;
    private HttpHeaders responseHeaderToValidate;
    private InputStream responseBodyToValidate;

    /**
     * Creates an instance of the <code>HttpURLConnection</code>
     *
     * @param url
     *            URL The URL this connection is connecting
     * @param port
     *            int The default connection port
     */
    protected HttpURLConnectionImpl(URL url, int port) {
        super(url);
        defaultPort = port;
        requestHeader = new HttpHeaders(defaultRequestHeader);
        responseCache = ResponseCache.getDefault();
    }

    /**
     * Creates an instance of the <code>HttpURLConnection</code>
     *
     * @param url
     *            URL The URL this connection is connecting
     * @param port
     *            int The default connection port
     * @param proxy
     *            Proxy The proxy which is used to make the connection
     */
    protected HttpURLConnectionImpl(URL url, int port, Proxy proxy) {
        this(url, port);
        this.proxy = proxy;
    }

    @Override public final void connect() throws IOException {
        if (connected) {
            return;
        }
        prepareRequestHeaders(); // TODO: this work is done 2x
        makeConnection();
    }

    /**
     * Internal method to open a connection to the server. Unlike connect(),
     * this method may be called multiple times for a single response. This may
     * be necessary when following redirects.
     *
     * <p>Request parameters may not be changed after this method has been
     * called.
     */
    protected void makeConnection() throws IOException {
        connected = true;

        if (connection != null || responseBodyIn != null) {
            return;
        }

        try {
            uri = url.toURILenient();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        initResponseSource();
        if (!responseSource.requiresConnection()) {
            return;
        }

        // try to determine: to use the proxy or not
        if (proxy != null) {
            // try to make the connection to the proxy
            // specified in constructor.
            // IOException will be thrown in the case of failure
            connection = getHttpConnection(proxy);
        } else {
            // Use system-wide ProxySelect to select proxy list,
            // then try to connect via elements in the proxy list.
            ProxySelector selector = ProxySelector.getDefault();
            List<Proxy> proxyList = selector.select(uri);
            if (proxyList != null) {
                for (Proxy selectedProxy : proxyList) {
                    if (selectedProxy.type() == Proxy.Type.DIRECT) {
                        // the same as NO_PROXY
                        continue;
                    }
                    try {
                        connection = getHttpConnection(selectedProxy);
                        proxy = selectedProxy;
                        break; // connected
                    } catch (IOException e) {
                        // failed to connect, tell it to the selector
                        selector.connectFailed(uri, selectedProxy.address(), e);
                    }
                }
            }
            if (connection == null) {
                // make direct connection
                connection = getHttpConnection(null);
            }
        }
        connection.setSoTimeout(getReadTimeout());
        setUpTransportIO(connection);
    }

    /**
     * Returns connected socket to be used for this HTTP connection.
     */
    private HttpConnection getHttpConnection(Proxy proxy) throws IOException {
        HttpConnection.Address address;
        if (proxy == null || proxy.type() == Proxy.Type.DIRECT) {
            this.proxy = null; // not using proxy
            address = new HttpConnection.Address(uri);
        } else {
            address = new HttpConnection.Address(uri, proxy, requiresTunnel());
        }
        return HttpConnectionPool.INSTANCE.get(address, getConnectTimeout());
    }

    /**
     * Sets up the data streams used to send requests and read responses.
     */
    protected void setUpTransportIO(HttpConnection connection) throws IOException {
        socketOut = connection.getOutputStream();
        requestOut = socketOut;
        socketIn = connection.getInputStream();
    }

    /**
     * Attempts to satisfy the request either fully or partially using the
     * cache. This may mutate the request headers to permit a conditional get.
     */
    private void initResponseSource() throws IOException {
        if (responseSource != null) {
            return;
        }

        responseSource = ResponseSource.NETWORK;
        if (!useCaches || responseCache == null) {
            return;
        }

        CacheResponse candidate = responseCache.get(uri, method, requestHeader.toMultimap());
        if (candidate == null || !acceptCacheResponseType(candidate)) {
            return;
        }
        Map<String, List<String>> headersMap = candidate.getHeaders();
        if (headersMap == null) {
            return;
        }
        InputStream cacheBodyIn = candidate.getBody(); // must be closed
        if (cacheBodyIn == null) {
            return;
        }

        HttpHeaders headers = HttpHeaders.fromMultimap(headersMap);
        CacheHeader cacheHeader = new CacheHeader(headers);
        long now = System.currentTimeMillis();
        responseSource = cacheHeader.chooseResponseSource(now, requestHeader);
        if (responseSource == ResponseSource.CACHE) {
            cacheResponse = candidate;
            setResponse(headers, cacheBodyIn);
        } else if (responseSource == ResponseSource.CONDITIONAL_CACHE) {
            cacheResponseHeader = cacheHeader;
            cacheResponse = candidate;
            responseHeaderToValidate = headers;
            responseBodyToValidate = cacheBodyIn;
        } else if (responseSource == ResponseSource.NETWORK) {
            IoUtils.closeQuietly(cacheBodyIn);
        } else {
            throw new AssertionError();
        }
    }

    /**
     * @param body the response body, or null if it doesn't exist or isn't
     *     available.
     */
    private void setResponse(HttpHeaders headers, InputStream body) throws IOException {
        if (this.responseBodyIn != null) {
            throw new IllegalStateException();
        }
        this.responseHeader = headers;
        this.httpMinorVersion = responseHeader.getHttpMinorVersion();
        this.responseCode = responseHeader.getResponseCode();
        this.responseMessage = responseHeader.getResponseMessage();
        if (body != null) {
            initContentStream(body);
        }
    }

    /**
     * Returns true if {@code cacheResponse} is of the right type. This
     * condition is necessary but not sufficient for the cached response to
     * be used.
     */
    protected boolean acceptCacheResponseType(CacheResponse cacheResponse) {
        return true;
    }

    private void maybeCache() throws IOException {
        // Are we caching at all?
        if (!useCaches || responseCache == null) {
            return;
        }
        // Should we cache this particular response code?
        // TODO: cache response code 300 HTTP_MULT_CHOICE ?
        if (responseCode != HTTP_OK && responseCode != HTTP_NOT_AUTHORITATIVE &&
                responseCode != HTTP_PARTIAL && responseCode != HTTP_MOVED_PERM &&
                responseCode != HTTP_GONE) {
            return;
        }
        // Offer this request to the cache.
        cacheRequest = responseCache.put(uri, getConnectionForCaching());
    }

    /**
     * Close the socket connection to the remote origin server or proxy.
     */
    @Override public final void disconnect() {
        releaseSocket(false);
    }

    /**
     * Releases this connection so that it may be either reused or closed.
     */
    protected final void releaseSocket(boolean reuseSocket) {
        // we cannot recycle sockets that have incomplete output.
        if (requestBodyOut != null && !requestBodyOut.closed) {
            reuseSocket = false;
        }

        // if the headers specify that the connection shouldn't be reused, don't reuse it
        if (hasConnectionCloseHeader()) {
            reuseSocket = false;
        }

        /*
         * Don't return the socket to the connection pool if this is an
         * intermediate response; we're going to use it again right away.
         */
        if (intermediateResponse && reuseSocket) {
            return;
        }

        if (connection != null) {
            if (reuseSocket) {
                HttpConnectionPool.INSTANCE.recycle(connection);
            } else {
                connection.closeSocketAndStreams();
            }
            connection = null;
        }

        /*
         * Ensure that no further I/O attempts from this instance make their way
         * to the underlying connection (which may get recycled).
         */
        socketOut = null;
        socketIn = null;
        requestOut = null;
    }

    /**
     * Discard all state initialized from the HTTP response including response
     * code, message, headers and body.
     */
    protected final void discardIntermediateResponse() throws IOException {
        boolean oldIntermediateResponse = intermediateResponse;
        intermediateResponse = true;
        try {
            discardResponseBody(responseBodyIn);
            responseBodyIn = null;
            sentRequestMillis = 0;
            receivedResponseMillis = 0;
            sentRequestHeaders = false;
            responseHeader = null;
            responseCode = -1;
            responseMessage = null;
            cacheRequest = null;
            uri = null;
            cacheResponse = null;
            responseSource = null;
        } finally {
            intermediateResponse = oldIntermediateResponse;
        }
    }

    private void discardResponseBody(InputStream in) throws IOException {
        if (in != null) {
            if (!(in instanceof UnknownLengthHttpInputStream)) {
                // skip the response so that the connection may be reused for the retry
                Streams.skipAll(in);
            }
            in.close();
        }
    }

    /**
     * Returns an input stream from the server in the case of error such as the
     * requested file (txt, htm, html) is not found on the remote server.
     * <p>
     * If the content type is not what stated above,
     * <code>FileNotFoundException</code> is thrown.
     *
     * @return InputStream the error input stream returned by the server.
     */
    @Override
    public final InputStream getErrorStream() {
        if (connected && method != HEAD && responseCode >= HTTP_BAD_REQUEST) {
            return responseBodyIn;
        }
        return null;
    }

    /**
     * Returns the value of the field at {@code position}. Returns null if there
     * are fewer than {@code position} headers.
     */
    @Override
    public final String getHeaderField(int position) {
        try {
            retrieveResponse();
        } catch (IOException ignored) {
        }
        return responseHeader != null ? responseHeader.getValue(position) : null;
    }

    /**
     * Returns the value of the field corresponding to the <code>key</code>
     * Returns <code>null</code> if there is no such field.
     *
     * If there are multiple fields with that key, the last field value is
     * returned.
     *
     * @return java.lang.String The value of the header field
     * @param key
     *            java.lang.String the name of the header field
     *
     * @see #getHeaderField(int)
     * @see #getHeaderFieldKey(int)
     */
    @Override
    public final String getHeaderField(String key) {
        try {
            retrieveResponse();
        } catch (IOException ignored) {
        }
        if (responseHeader == null) {
            return null;
        }
        return key == null ? responseHeader.getStatusLine() : responseHeader.get(key);
    }

    @Override
    public final String getHeaderFieldKey(int position) {
        try {
            retrieveResponse();
        } catch (IOException ignored) {
        }
        return responseHeader != null ? responseHeader.getKey(position) : null;
    }

    @Override
    public final Map<String, List<String>> getHeaderFields() {
        try {
            retrieveResponse();
        } catch (IOException ignored) {
        }
        return responseHeader != null ? responseHeader.toMultimap() : null;
    }

    @Override
    public final Map<String, List<String>> getRequestProperties() {
        if (connected) {
            throw new IllegalStateException(
                    "Cannot access request header fields after connection is set");
        }
        return requestHeader.toMultimap();
    }

    @Override
    public final InputStream getInputStream() throws IOException {
        if (!doInput) {
            throw new ProtocolException("This protocol does not support input");
        }

        retrieveResponse();

        /*
         * if the requested file does not exist, throw an exception formerly the
         * Error page from the server was returned if the requested file was
         * text/html this has changed to return FileNotFoundException for all
         * file types
         */
        if (responseCode >= HTTP_BAD_REQUEST) {
            throw new FileNotFoundException(url.toString());
        }

        if (responseBodyIn == null) {
            throw new IOException("No response body exists; responseCode=" + responseCode);
        }

        return responseBodyIn;
    }

    private void initContentStream(InputStream transferStream) throws IOException {
        if (transparentGzip && "gzip".equalsIgnoreCase(responseHeader.get("Content-Encoding"))) {
            /*
             * If the response was transparently gzipped, remove the gzip header
             * so clients don't double decompress. http://b/3009828
             */
            responseHeader.removeAll("Content-Encoding");
            responseBodyIn = new GZIPInputStream(transferStream);
        } else {
            responseBodyIn = transferStream;
        }
    }

    private InputStream getTransferStream() throws IOException {
        if (!hasResponseBody()) {
            return new FixedLengthInputStream(socketIn, cacheRequest, this, 0);
        }

        if ("chunked".equalsIgnoreCase(responseHeader.get("Transfer-Encoding"))) {
            return new ChunkedInputStream(socketIn, cacheRequest, this);
        }

        String contentLength = responseHeader.get("Content-Length");
        if (contentLength != null) {
            try {
                int length = Integer.parseInt(contentLength);
                return new FixedLengthInputStream(socketIn, cacheRequest, this, length);
            } catch (NumberFormatException ignored) {
            }
        }

        /*
         * Wrap the input stream from the HttpConnection (rather than
         * just returning "socketIn" directly here), so that we can control
         * its use after the reference escapes.
         */
        return new UnknownLengthHttpInputStream(socketIn, cacheRequest, this);
    }

    @Override
    public final OutputStream getOutputStream() throws IOException {
        if (!doOutput) {
            throw new ProtocolException("Does not support output");
        }

        // you can't write after you read
        if (sentRequestHeaders) {
            // TODO: just return 'requestBodyOut' if that's non-null?
            throw new ProtocolException(
                    "OutputStream unavailable because request headers have already been sent!");
        }

        if (requestBodyOut != null) {
            return requestBodyOut;
        }

        // they are requesting a stream to write to. This implies a POST method
        if (method == GET) {
            method = POST;
        }

        // If the request method is neither PUT or POST, then you're not writing
        if (method != PUT && method != POST) {
            throw new ProtocolException(method + " does not support writing");
        }

        int contentLength = -1;
        String contentLengthString = requestHeader.get("Content-Length");
        if (contentLengthString != null) {
            contentLength = Integer.parseInt(contentLengthString);
        }

        String encoding = requestHeader.get("Transfer-Encoding");
        if (chunkLength > 0 || "chunked".equalsIgnoreCase(encoding)) {
            sendChunked = true;
            contentLength = -1;
            if (chunkLength == -1) {
                chunkLength = DEFAULT_CHUNK_LENGTH;
            }
        }

        connect();

        if (socketOut == null) {
            // TODO: what should we do if a cached response exists?
            throw new IOException("No socket to write to; was a POST cached?");
        }

        if (httpMinorVersion == 0) {
            sendChunked = false;
        }

        if (fixedContentLength != -1) {
            writeRequestHeaders(fixedContentLength);
            requestBodyOut = new FixedLengthOutputStream(requestOut, fixedContentLength);
        } else if (sendChunked) {
            writeRequestHeaders(-1);
            requestBodyOut = new ChunkedOutputStream(requestOut, chunkLength);
        } else if (contentLength != -1) {
            writeRequestHeaders(contentLength);
            requestBodyOut = new RetryableOutputStream(contentLength);
        } else {
            requestBodyOut = new RetryableOutputStream();
        }
        return requestBodyOut;
    }

    @Override
    public final Permission getPermission() throws IOException {
        String connectToAddress = getConnectToHost() + ":" + getConnectToPort();
        return new SocketPermission(connectToAddress, "connect, resolve");
    }

    @Override
    public final String getRequestProperty(String field) {
        if (field == null) {
            return null;
        }
        return requestHeader.get(field);
    }

    /**
     * Returns the characters up to but not including the next "\r\n", "\n", or
     * the end of the stream, consuming the end of line delimiter.
     */
    static String readLine(InputStream is) throws IOException {
        StringBuilder result = new StringBuilder(80);
        while (true) {
            int c = is.read();
            if (c == -1 || c == '\n') {
                break;
            }

            result.append((char) c);
        }
        int length = result.length();
        if (length > 0 && result.charAt(length - 1) == '\r') {
            result.setLength(length - 1);
        }
        return result.toString();
    }

    protected String requestString() {
        if (usingProxy()) {
            return url.toString();
        }
        String file = url.getFile();
        if (file == null || file.length() == 0) {
            file = "/";
        }
        return file;
    }

    private void readResponseHeaders() throws IOException {
        do {
            HttpHeaders headers = new HttpHeaders();
            headers.setStatusLine(readLine(socketIn).trim());
            readHeaders(headers);
            setResponse(headers, null);
        } while (responseCode == HTTP_CONTINUE);
    }

    /**
     * Returns true if the response must have a (possibly 0-length) body.
     * See RFC 2616 section 4.3.
     */
    private boolean hasResponseBody() {
        if (method != HEAD
                && method != CONNECT
                && (responseCode < HTTP_CONTINUE || responseCode >= 200)
                && responseCode != HTTP_NO_CONTENT
                && responseCode != HTTP_NOT_MODIFIED) {
            return true;
        }

        /*
         * If the Content-Length or Transfer-Encoding headers disagree with the
         * response code, the response is malformed. For best compatibility, we
         * honor the headers.
         */
        String contentLength = responseHeader.get("Content-Length");
        if (contentLength != null && Integer.parseInt(contentLength) > 0) {
            return true;
        }
        if ("chunked".equalsIgnoreCase(responseHeader.get("Transfer-Encoding"))) {
            return true;
        }

        return false;
    }

    @Override
    public final int getResponseCode() throws IOException {
        retrieveResponse();
        return responseCode;
    }

    /**
     * Trailers are headers included after the last chunk of a response encoded
     * with chunked encoding.
     */
    void readTrailers() throws IOException {
        readHeaders(responseHeader);
    }

    private void readHeaders(HttpHeaders headers) throws IOException {
        // parse the result headers until the first blank line
        String line;
        while ((line = readLine(socketIn)).length() > 1) {
            // Header parsing
            int index = line.indexOf(":");
            if (index == -1) {
                headers.add("", line);
            } else {
                headers.add(line.substring(0, index), line.substring(index + 1));
            }
        }

        CookieHandler cookieHandler = CookieHandler.getDefault();
        if (cookieHandler != null) {
            cookieHandler.put(uri, headers.toMultimap());
        }
    }

    /**
     * Prepares the HTTP headers and sends them to the server.
     *
     * <p>For streaming requests with a body, headers must be prepared
     * <strong>before</strong> the output stream has been written to. Otherwise
     * the body would need to be buffered!
     *
     * <p>For non-streaming requests with a body, headers must be prepared
     * <strong>after</strong> the output stream has been written to and closed.
     * This ensures that the {@code Content-Length} header receives the proper
     * value.
     *
     * @param contentLength the number of bytes in the request body, or -1 if
     *      the request body length is unknown.
     */
    private void writeRequestHeaders(int contentLength) throws IOException {
        HttpHeaders headersToSend = method == CONNECT
                ? getProxyConnectHeaders()
                : requestHeader;
        byte[] headerBytes = headersToSend.toHeaderString().getBytes(Charsets.ISO_8859_1);

        if (contentLength != -1
                && headerBytes.length + contentLength <= MAX_REQUEST_BUFFER_LENGTH) {
            requestOut = new BufferedOutputStream(socketOut, headerBytes.length + contentLength);
        }

        sentRequestMillis = System.currentTimeMillis();
        requestOut.write(headerBytes);
        sentRequestHeaders = true;
    }

    /**
     * If we're establishing an HTTPS tunnel with CONNECT (RFC 2817 5.2), send
     * only the minimum set of headers. This avoids sending potentially
     * sensitive data like HTTP cookies to the proxy unencrypted.
     */
    private HttpHeaders getProxyConnectHeaders() throws IOException {
        HttpHeaders proxyHeader = new HttpHeaders();
        proxyHeader.setStatusLine(getStatusLine());

        // always set Host and User-Agent
        String host = requestHeader.get("Host");
        if (host == null) {
            host = getOriginAddress(url);
        }
        proxyHeader.set("Host", host);

        String userAgent = requestHeader.get("User-Agent");
        if (userAgent == null) {
            userAgent = getDefaultUserAgent();
        }
        proxyHeader.set("User-Agent", userAgent);

        // copy over the Proxy-Authorization header if it exists
        String proxyAuthorization = requestHeader.get("Proxy-Authorization");
        if (proxyAuthorization != null) {
            proxyHeader.set("Proxy-Authorization", proxyAuthorization);
        }

        // Always set the Proxy-Connection to Keep-Alive for the benefit of
        // HTTP/1.0 proxies like Squid.
        proxyHeader.set("Proxy-Connection", "Keep-Alive");
        return proxyHeader;
    }

    /**
     * Populates requestHeader with the HTTP headers to be sent. Header values are
     * derived from the request itself and the cookie manager.
     *
     * <p>This client doesn't specify a default {@code Accept} header because it
     * doesn't know what content types the application is interested in.
     */
    private void prepareRequestHeaders() throws IOException {
        requestHeader.setStatusLine(getStatusLine());

        if (requestHeader.get("User-Agent") == null) {
            requestHeader.add("User-Agent", getDefaultUserAgent());
        }

        if (requestHeader.get("Host") == null) {
            requestHeader.add("Host", getOriginAddress(url));
        }

        if (httpMinorVersion > 0) {
            requestHeader.addIfAbsent("Connection", "Keep-Alive");
        }

        if (fixedContentLength != -1) {
            requestHeader.addIfAbsent("Content-Length", Integer.toString(fixedContentLength));
        } else if (sendChunked) {
            requestHeader.addIfAbsent("Transfer-Encoding", "chunked");
        } else if (requestBodyOut instanceof RetryableOutputStream) {
            int size = ((RetryableOutputStream) requestBodyOut).contentLength();
            requestHeader.addIfAbsent("Content-Length", Integer.toString(size));
        }

        if (requestBodyOut != null) {
            requestHeader.addIfAbsent("Content-Type", "application/x-www-form-urlencoded");
        }

        if (requestHeader.get("Accept-Encoding") == null) {
            transparentGzip = true;
            requestHeader.set("Accept-Encoding", "gzip");
        }

        CookieHandler cookieHandler = CookieHandler.getDefault();
        if (cookieHandler != null) {
            Map<String, List<String>> allCookieHeaders
                    = cookieHandler.get(uri, requestHeader.toMultimap());
            for (Map.Entry<String, List<String>> entry : allCookieHeaders.entrySet()) {
                String key = entry.getKey();
                if ("Cookie".equalsIgnoreCase(key) || "Cookie2".equalsIgnoreCase(key)) {
                    requestHeader.addAll(key, entry.getValue());
                }
            }
        }
    }

    private String getStatusLine() {
        String protocol = (httpMinorVersion == 0) ? "HTTP/1.0" : "HTTP/1.1";
        return method + " " + requestString() + " " + protocol;
    }

    private String getDefaultUserAgent() {
        String agent = System.getProperty("http.agent");
        return agent != null ? agent : ("Java" + System.getProperty("java.version"));
    }

    private boolean hasConnectionCloseHeader() {
        return (responseHeader != null
                && "close".equalsIgnoreCase(responseHeader.get("Connection")))
                || (requestHeader != null
                && "close".equalsIgnoreCase(requestHeader.get("Connection")));
    }

    private String getOriginAddress(URL url) {
        int port = url.getPort();
        String result = url.getHost();
        if (port > 0 && port != defaultPort) {
            result = result + ":" + port;
        }
        return result;
    }

    /**
     * A slightly different implementation from this parent's
     * <code>setIfModifiedSince()</code> Since this HTTP impl supports
     * IfModifiedSince as one of the header field, the request header is updated
     * with the new value.
     *
     *
     * @param newValue
     *            the number of millisecond since epoch
     *
     * @throws IllegalStateException
     *             if already connected.
     */
    @Override
    public final void setIfModifiedSince(long newValue) {
        super.setIfModifiedSince(newValue);
        // convert from millisecond since epoch to date string
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = sdf.format(new Date(newValue));
        requestHeader.add("If-Modified-Since", date);
    }

    @Override
    public final void setRequestProperty(String field, String newValue) {
        if (connected) {
            throw new IllegalStateException("Cannot set request property after connection is made");
        }
        if (field == null) {
            throw new NullPointerException();
        }
        requestHeader.set(field, newValue);
    }

    @Override
    public final void addRequestProperty(String field, String value) {
        if (connected) {
            throw new IllegalStateException("Cannot set request property after connection is made");
        }
        if (field == null) {
            throw new NullPointerException();
        }
        requestHeader.add(field, value);
    }

    /**
     * Returns the target port of the socket connection; either a port of the
     * origin server or an intermediate proxy.
     */
    private int getConnectToPort() {
        int hostPort = usingProxy()
                ? ((InetSocketAddress) proxy.address()).getPort()
                : url.getPort();
        return hostPort < 0 ? defaultPort : hostPort;
    }

    /**
     * Returns the target address of the socket connection; either the address
     * of the origin server or an intermediate proxy.
     */
    private InetAddress getConnectToInetAddress() throws IOException {
        return usingProxy()
                ? ((InetSocketAddress) proxy.address()).getAddress()
                : InetAddress.getByName(url.getHost());
    }

    /**
     * Returns the target host name of the socket connection; either the host
     * name of the origin server or an intermediate proxy.
     */
    private String getConnectToHost() {
        return usingProxy()
                ? ((InetSocketAddress) proxy.address()).getHostName()
                : url.getHost();
    }

    @Override public final boolean usingProxy() {
        return (proxy != null && proxy.type() != Proxy.Type.DIRECT);
    }

    protected boolean requiresTunnel() {
        return false;
    }

    /**
     * Returns this connection in a form suitable for use by the response cache.
     * If this returns an HTTPS connection, only secure cache responses will be
     * honored.
     */
    protected HttpURLConnection getConnectionForCaching() {
        return this;
    }

    /**
     * Aggressively tries to get the final HTTP response, potentially making
     * many HTTP requests in the process in order to cope with redirects and
     * authentication.
     */
    protected final void retrieveResponse() throws IOException {
        if (responseHeader != null) {
            return;
        }

        redirectionCount = 0;
        while (true) {
            // TODO: make connect() use this so transparentGzip isn't wrong for cached responses
            prepareRequestHeaders();

            makeConnection();

            if (responseBodyIn == null) {
                getFromNetwork();
            }

            Retry retry = processResponseHeaders();

            if (retry == Retry.NONE) {
                return;
            }

            /*
             * The first request wasn't sufficient. Prepare for another...
             */

            if (requestBodyOut != null && !(requestBodyOut instanceof RetryableOutputStream)) {
                throw new HttpRetryException("Cannot retry streamed HTTP body", responseCode);
            }

            if (retry == Retry.SAME_CONNECTION && hasConnectionCloseHeader()) {
                retry = Retry.NEW_CONNECTION;
            }

            discardIntermediateResponse();

            if (retry == Retry.NEW_CONNECTION) {
                releaseSocket(true);
            }
        }
    }

    private void getFromNetwork() throws IOException {
        if (!sentRequestHeaders) {
            int contentLength = requestBodyOut instanceof RetryableOutputStream
                    ? ((RetryableOutputStream) requestBodyOut).contentLength()
                    : -1;
            writeRequestHeaders(contentLength);
        }

        if (requestBodyOut != null) {
            requestBodyOut.close();
            if (requestBodyOut instanceof RetryableOutputStream) {
                ((RetryableOutputStream) requestBodyOut).writeToSocket(requestOut);
            }
        }

        requestOut.flush();
        requestOut = socketOut;

        readResponseHeaders();
        receivedResponseMillis = System.currentTimeMillis();
        responseHeader.add(CacheHeader.SENT_MILLIS, Long.toString(sentRequestMillis));
        responseHeader.add(CacheHeader.RECEIVED_MILLIS, Long.toString(receivedResponseMillis));

        if (responseSource == ResponseSource.CONDITIONAL_CACHE) {
            if (cacheResponseHeader.validate(requestHeader, responseHeader)) {
                // discard the network response
                discardResponseBody(getTransferStream());

                // use the cache response
                setResponse(responseHeaderToValidate, responseBodyToValidate);
                responseBodyToValidate = null;
                responseHeaderToValidate = null;
                return;
            } else {
                IoUtils.closeQuietly(responseBodyToValidate);
                responseBodyToValidate = null;
                responseHeaderToValidate = null;
            }
        }

        if (hasResponseBody()) {
            maybeCache(); // reentrant. this calls into user code which may call back into this!
        }

        initContentStream(getTransferStream());
    }

    enum Retry {
        NONE,
        SAME_CONNECTION,
        NEW_CONNECTION
    }

    /**
     * Returns the retry action to take for the current response headers. The
     * headers, proxy and target URL or this connection may be adjusted to
     * prepare for a follow up request.
     */
    private Retry processResponseHeaders() throws IOException {
        switch (responseCode) {
            case HTTP_PROXY_AUTH: // proxy authorization failed ?
                if (!usingProxy()) {
                    throw new IOException(
                            "Received HTTP_PROXY_AUTH (407) code while not using proxy");
                }
                return processAuthHeader("Proxy-Authenticate", "Proxy-Authorization");

            case HTTP_UNAUTHORIZED: // HTTP authorization failed ?
                return processAuthHeader("WWW-Authenticate", "Authorization");

            case HTTP_MULT_CHOICE:
            case HTTP_MOVED_PERM:
            case HTTP_MOVED_TEMP:
            case HTTP_SEE_OTHER:
            case HTTP_USE_PROXY:
                if (!getInstanceFollowRedirects()) {
                    return Retry.NONE;
                }
                if (requestBodyOut != null) {
                    // TODO: follow redirects for retryable output streams...
                    return Retry.NONE;
                }
                if (++redirectionCount > MAX_REDIRECTS) {
                    throw new ProtocolException("Too many redirects");
                }
                String location = getHeaderField("Location");
                if (location == null) {
                    return Retry.NONE;
                }
                if (responseCode == HTTP_USE_PROXY) {
                    int start = 0;
                    if (location.startsWith(url.getProtocol() + ':')) {
                        start = url.getProtocol().length() + 1;
                    }
                    if (location.startsWith("//", start)) {
                        start += 2;
                    }
                    setProxy(location.substring(start));
                    return Retry.NEW_CONNECTION;
                }
                URL previousUrl = url;
                url = new URL(previousUrl, location);
                if (!previousUrl.getProtocol().equals(url.getProtocol())) {
                    return Retry.NONE; // the scheme changed; don't retry.
                }
                if (previousUrl.getHost().equals(url.getHost())
                        && previousUrl.getEffectivePort() == url.getEffectivePort()) {
                    return Retry.SAME_CONNECTION;
                } else {
                    // TODO: strip cookies?
                    requestHeader.removeAll("Host");
                    return Retry.NEW_CONNECTION;
                }

            default:
                return Retry.NONE;
        }
    }

    /**
     * React to a failed authorization response by looking up new credentials.
     */
    private Retry processAuthHeader(String headerKey, String retryHeader) throws IOException {
        // keep asking for username/password until authorized
        String challenge = responseHeader.get(headerKey);
        if (challenge == null) {
            throw new IOException("Received authentication challenge is null");
        }
        String credentials = getAuthorizationCredentials(challenge);
        if (credentials == null) {
            return Retry.NONE; // could not find credentials, end request cycle
        }
        // add authorization credentials, bypassing the already-connected check
        requestHeader.set(retryHeader, credentials);
        return Retry.SAME_CONNECTION;
    }

    /**
     * Returns the authorization credentials on the base of provided challenge.
     */
    private String getAuthorizationCredentials(String challenge) throws IOException {
        int idx = challenge.indexOf(" ");
        if (idx == -1) {
            return null;
        }
        String scheme = challenge.substring(0, idx);
        int realm = challenge.indexOf("realm=\"") + 7;
        String prompt = null;
        if (realm != -1) {
            int end = challenge.indexOf('"', realm);
            if (end != -1) {
                prompt = challenge.substring(realm, end);
            }
        }
        // use the global authenticator to get the password
        PasswordAuthentication pa = Authenticator.requestPasswordAuthentication(
                getConnectToInetAddress(), getConnectToPort(), url.getProtocol(), prompt, scheme);
        if (pa == null) {
            return null;
        }
        // base64 encode the username and password
        String usernameAndPassword = pa.getUserName() + ":" + new String(pa.getPassword());
        byte[] bytes = usernameAndPassword.getBytes(Charsets.ISO_8859_1);
        String encoded = Base64.encode(bytes, Charsets.ISO_8859_1);
        return scheme + " " + encoded;
    }

    private void setProxy(String proxy) {
        // TODO: convert IllegalArgumentException etc. to ProtocolException?
        int colon = proxy.indexOf(':');
        String host;
        int port;
        if (colon != -1) {
            host = proxy.substring(0, colon);
            port = Integer.parseInt(proxy.substring(colon + 1));
        } else {
            host = proxy;
            port = defaultPort;
        }
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }
}
