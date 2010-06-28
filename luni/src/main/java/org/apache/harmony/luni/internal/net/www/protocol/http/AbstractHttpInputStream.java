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

package org.apache.harmony.luni.internal.net.www.protocol.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.CacheRequest;

/**
 * An input stream for the payload of an HTTP response.
 *
 * <p>Since a single socket stream may carry multiple HTTP responses (in
 * series), closing this stream doesn't necessarily close the underlying
 * socket stream. Closing this stream before all data has been read
 * means the socket will not be reused for subsequent HTTP requests.
 *
 * <p>A side effect of reading an HTTP response is that the response cache
 * is populated. If the stream is closed early, that cache entry will be
 * invalidated.
 */
abstract class AbstractHttpInputStream extends InputStream {
    protected final InputStream in;
    protected final HttpURLConnectionImpl httpURLConnection;
    protected final CacheRequest cacheRequest;
    protected final OutputStream cacheOut;
    protected boolean closed;
    private byte[] skipBuffer;

    AbstractHttpInputStream(InputStream in, HttpURLConnectionImpl httpURLConnection,
            CacheRequest cacheRequest) throws IOException {
        this.in = in;
        this.httpURLConnection = httpURLConnection;
        this.cacheRequest = cacheRequest;
        this.cacheOut = cacheRequest != null ? cacheRequest.getBody() : null;
    }

    /**
     * read() is implemented using read(byte[], int, int) so subclasses only
     * need to override the latter.
     */
    @Override public final int read() throws IOException {
        byte[] buffer = new byte[1];
        int count = read(buffer, 0, 1);
        return count == -1 ? -1 : buffer[0];
    }

    /**
     * skip(long) is implemented using read(byte[], int, int) so subclasses
     * only need to override the latter.
     */
    @Override public final long skip(long n) throws IOException {
        if (skipBuffer == null) {
            skipBuffer = new byte[4096];
        }
        long total = 0;
        while (total < n) {
            // Calling read() ensures the skipped bytes make it into the response cache.
            int count = read(skipBuffer, 0, (int) Math.min(n - total, skipBuffer.length));
            if (count == -1) {
                break;
            }
            total += count;
        }
        return total;
    }

    protected final void checkBounds(byte[] buffer, int offset, int length) {
        // Force buf null check first, and avoid int overflow
        if (offset < 0 || offset > buffer.length) {
            throw new ArrayIndexOutOfBoundsException("Offset out of bounds: " + offset);
        }
        if (length < 0 || buffer.length - offset < length) {
            throw new ArrayIndexOutOfBoundsException("Length out of bounds: " + length);
        }
    }

    protected final void checkNotClosed() throws IOException {
        if (closed) {
            throw new IOException("stream closed");
        }
    }

    protected final void cacheWrite(byte[] buffer, int offset, int count) throws IOException {
        if (cacheOut != null) {
            cacheOut.write(buffer, offset, count);
        }
    }

    /**
     * Closes the cache entry and makes the socket available for reuse. This
     * should be invoked when the end of the payload has been reached.
     */
    protected final void endOfInput(boolean closeSocket) throws IOException {
        if (cacheRequest != null) {
            cacheOut.close();
        }
        httpURLConnection.releaseSocket(closeSocket);
    }

    /**
     * Calls abort on the cache entry and disconnects the socket. This
     * should be invoked when the connection is closed unexpectedly to
     * invalidate the cache entry and to prevent the HTTP connection from
     * being reused. HTTP messages are sent in serial so whenever a message
     * cannot be read to completion, subsequent messages cannot be read
     * either and the connection must be discarded.
     *
     * <p>An earlier implementation skipped the remaining bytes, but this
     * requires that the entire transfer be completed. If the intention was
     * to cancel the transfer, closing the connection is the only solution.
     */
    protected final void unexpectedEndOfInput() {
        if (cacheRequest != null) {
            cacheRequest.abort();
        }
        httpURLConnection.releaseSocket(true);
    }
}
