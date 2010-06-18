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

package tests.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * A scriptable web server. Callers supply canned responses and the server
 * replays them upon request in sequence.
 */
public final class MockWebServer {

    static final String ASCII = "US-ASCII";

    private final BlockingQueue<RecordedRequest> requestQueue
            = new LinkedBlockingQueue<RecordedRequest>();
    private final BlockingQueue<MockResponse> responseQueue
            = new LinkedBlockingDeque<MockResponse>();
    private int bodyLimit = Integer.MAX_VALUE;
    private SSLSocketFactory sslSocketFactory;
    private boolean tunnelProxy;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private int port = -1;

    public int getPort() {
        if (port == -1) {
            throw new IllegalStateException("Cannot retrieve port before calling play()");
        }
        return port;
    }

    public Proxy toProxyAddress() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", getPort()));
    }

    /**
     * Returns a URL for connecting to this server.
     *
     * @param path the request path, such as "/".
     */
    public URL getUrl(String path) throws MalformedURLException {
        return new URL("http://localhost:" + getPort() + path);
    }

    /**
     * Sets the number of bytes of the POST body to keep in memory to the given
     * limit.
     */
    public void setBodyLimit(int maxBodyLength) {
        this.bodyLimit = maxBodyLength;
    }

    /**
     * Serve requests with HTTPS rather than otherwise.
     *
     * @param tunnelProxy whether to expect the HTTP CONNECT method before
     *     negotiating TLS.
     */
    public void useHttps(SSLSocketFactory sslSocketFactory, boolean tunnelProxy) {
        this.sslSocketFactory = sslSocketFactory;
        this.tunnelProxy = tunnelProxy;
    }

    /**
     * Awaits the next HTTP request, removes it, and returns it. Callers should
     * use this to verify the request sent was as intended.
     */
    public RecordedRequest takeRequest() throws InterruptedException {
        return requestQueue.take();
    }

    public void enqueue(MockResponse response) {
        responseQueue.add(response);
    }

    /**
     * Starts the server, serves all enqueued requests, and shuts the server
     * down.
     */
    public void play() throws IOException {
        final ServerSocket ss;
        ss = new ServerSocket(0);
        ss.setReuseAddress(true);

        port = ss.getLocalPort();
        executor.submit(new Callable<Void>() {
            public Void call() throws Exception {
                int count = 0;
                while (true) {
                    if (count > 0 && responseQueue.isEmpty()) {
                        ss.close();
                        executor.shutdown();
                        return null;
                    }

                    serveConnection(ss.accept());
                    count++;
                }
            }
        });
    }

    private void serveConnection(final Socket raw) {
        executor.submit(new Callable<Void>() {
            int sequenceNumber = 0;

            public Void call() throws Exception {
                Socket socket;
                if (sslSocketFactory != null) {
                    if (tunnelProxy) {
                        if (!processOneRequest(raw.getInputStream(), raw.getOutputStream())) {
                            throw new IllegalStateException("Tunnel without any CONNECT!");
                        }
                    }
                    socket = sslSocketFactory.createSocket(
                            raw, raw.getInetAddress().getHostAddress(), raw.getPort(), true);
                    ((SSLSocket) socket).setUseClientMode(false);
                } else {
                    socket = raw;
                }

                InputStream in = new BufferedInputStream(socket.getInputStream());
                OutputStream out = new BufferedOutputStream(socket.getOutputStream());

                if (!processOneRequest(in, out)) {
                    throw new IllegalStateException("Connection without any request!");
                }
                while (processOneRequest(in, out)) {}

                in.close();
                out.close();
                return null;
            }

            /**
             * Reads a request and writes its response. Returns true if a request
             * was processed.
             */
            private boolean processOneRequest(InputStream in, OutputStream out)
                    throws IOException, InterruptedException {
                RecordedRequest request = readRequest(in, sequenceNumber);
                if (request == null) {
                    return false;
                }
                requestQueue.add(request);
                writeResponse(out, computeResponse(request));
                sequenceNumber++;
                return true;
            }
        });
    }

    /**
     * @param sequenceNumber the index of this request on this connection.
     */
    private RecordedRequest readRequest(InputStream in, int sequenceNumber) throws IOException {
        String request = readAsciiUntilCrlf(in);
        if (request.isEmpty()) {
            return null; // end of data; no more requests
        }

        List<String> headers = new ArrayList<String>();
        int contentLength = -1;
        boolean chunked = false;
        String header;
        while (!(header = readAsciiUntilCrlf(in)).isEmpty()) {
            headers.add(header);
            String lowercaseHeader = header.toLowerCase();
            if (contentLength == -1 && lowercaseHeader.startsWith("content-length:")) {
                contentLength = Integer.parseInt(header.substring(15).trim());
            }
            if (lowercaseHeader.startsWith("transfer-encoding:") &&
                    lowercaseHeader.substring(18).trim().equals("chunked")) {
                chunked = true;
            }
        }

        boolean hasBody = false;
        TruncatingOutputStream requestBody = new TruncatingOutputStream();
        List<Integer> chunkSizes = new ArrayList<Integer>();
        if (contentLength != -1) {
            hasBody = true;
            transfer(contentLength, in, requestBody);
        } else if (chunked) {
            hasBody = true;
            while (true) {
                int chunkSize = Integer.parseInt(readAsciiUntilCrlf(in).trim(), 16);
                if (chunkSize == 0) {
                    readEmptyLine(in);
                    break;
                }
                chunkSizes.add(chunkSize);
                transfer(chunkSize, in, requestBody);
                readEmptyLine(in);
            }
        }

        if (request.startsWith("GET ") || request.startsWith("CONNECT ")) {
            if (hasBody) {
                throw new IllegalArgumentException("GET requests should not have a body!");
            }
        } else if (request.startsWith("POST ")) {
            if (!hasBody) {
                throw new IllegalArgumentException("POST requests must have a body!");
            }
        } else {
            throw new UnsupportedOperationException("Unexpected method: " + request);
        }

        return new RecordedRequest(request, headers, chunkSizes,
                requestBody.numBytesReceived, requestBody.toByteArray(), sequenceNumber);
    }

    /**
     * Returns a response to satisfy {@code request}.
     */
    private MockResponse computeResponse(RecordedRequest request) throws InterruptedException {
        if (responseQueue.isEmpty()) {
            throw new IllegalStateException("Unexpected request: " + request);
        }
        return responseQueue.take();
    }

    private void writeResponse(OutputStream out, MockResponse response) throws IOException {
        out.write((response.getStatus() + "\r\n").getBytes(ASCII));
        for (String header : response.getHeaders()) {
            out.write((header + "\r\n").getBytes(ASCII));
        }
        out.write(("\r\n").getBytes(ASCII));
        out.write(response.getBody());
        out.flush();
    }

    /**
     * Transfer bytes from {@code in} to {@code out} until either {@code length}
     * bytes have been transferred or {@code in} is exhausted.
     */
    private void transfer(int length, InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (length > 0) {
            int count = in.read(buffer, 0, Math.min(buffer.length, length));
            if (count == -1) {
                return;
            }
            out.write(buffer, 0, count);
            length -= count;
        }
    }

    /**
     * Returns the text from {@code in} until the next "\r\n", or null if
     * {@code in} is exhausted.
     */
    private String readAsciiUntilCrlf(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            int c = in.read();
            if (c == '\n' && builder.length() > 0 && builder.charAt(builder.length() - 1) == '\r') {
                builder.deleteCharAt(builder.length() - 1);
                return builder.toString();
            } else if (c == -1) {
                return builder.toString();
            } else {
                builder.append((char) c);
            }
        }
    }

    private void readEmptyLine(InputStream in) throws IOException {
        String line = readAsciiUntilCrlf(in);
        if (!line.isEmpty()) {
            throw new IllegalStateException("Expected empty but was: " + line);
        }
    }

    /**
     * An output stream that drops data after bodyLimit bytes.
     */
    private class TruncatingOutputStream extends ByteArrayOutputStream {
        private int numBytesReceived = 0;
        @Override public void write(byte[] buffer, int offset, int len) {
            numBytesReceived += len;
            super.write(buffer, offset, Math.min(len, bodyLimit - count));
        }
        @Override public void write(int oneByte) {
            numBytesReceived++;
            if (count < bodyLimit) {
                super.write(oneByte);
            }
        }
    }
}
