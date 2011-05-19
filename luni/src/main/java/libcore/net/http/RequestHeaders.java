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

package libcore.net.http;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Parsed HTTP request headers.
 */
final class RequestHeaders {
    final URI uri;
    final RawHeaders headers;

    /** Don't use a cache to satisfy this request. */
    boolean noCache;
    int maxAgeSeconds = -1;
    int maxStaleSeconds = -1;
    int minFreshSeconds = -1;

    /**
     * This field's name "only-if-cached" is misleading. It actually means "do
     * not use the network". It is set by a client who only wants to make a
     * request if it can be fully satisfied by the cache. Cached responses that
     * would require validation (ie. conditional gets) are not permitted if this
     * header is set.
     */
    boolean onlyIfCached;

    /**
     * True if the request contains an authorization field. Although this isn't
     * necessarily a shared cache, it follows the spec's strict requirements for
     * shared caches.
     */
    boolean hasAuthorization;

    int contentLength = -1;
    String transferEncoding;
    String userAgent;
    String host;
    String connection;
    String acceptEncoding;
    String contentType;
    String ifModifiedSince;
    String ifNoneMatch;
    String proxyAuthorization;

    public RequestHeaders(URI uri, RawHeaders headers) {
        this.uri = uri;
        this.headers = headers;

        HeaderParser.CacheControlHandler handler = new HeaderParser.CacheControlHandler() {
            @Override public void handle(String directive, String parameter) {
                if (directive.equalsIgnoreCase("no-cache")) {
                    noCache = true;
                } else if (directive.equalsIgnoreCase("max-age")) {
                    maxAgeSeconds = HeaderParser.parseSeconds(parameter);
                } else if (directive.equalsIgnoreCase("max-stale")) {
                    maxStaleSeconds = HeaderParser.parseSeconds(parameter);
                } else if (directive.equalsIgnoreCase("min-fresh")) {
                    minFreshSeconds = HeaderParser.parseSeconds(parameter);
                } else if (directive.equalsIgnoreCase("only-if-cached")) {
                    onlyIfCached = true;
                }
            }
        };

        for (int i = 0; i < headers.length(); i++) {
            String fieldName = headers.getFieldName(i);
            String value = headers.getValue(i);
            if ("Cache-Control".equalsIgnoreCase(fieldName)) {
                HeaderParser.parseCacheControl(value, handler);
            } else if ("Pragma".equalsIgnoreCase(fieldName)) {
                if (value.equalsIgnoreCase("no-cache")) {
                    noCache = true;
                }
            } else if ("If-None-Match".equalsIgnoreCase(fieldName)) {
                ifNoneMatch = value;
            } else if ("If-Modified-Since".equalsIgnoreCase(fieldName)) {
                ifModifiedSince = value;
            } else if ("Authorization".equalsIgnoreCase(fieldName)) {
                hasAuthorization = true;
            } else if ("Content-Length".equalsIgnoreCase(fieldName)) {
                try {
                    contentLength = Integer.parseInt(value);
                } catch (NumberFormatException ignored) {
                }
            } else if ("Transfer-Encoding".equalsIgnoreCase(fieldName)) {
                transferEncoding = value;
            } else if ("User-Agent".equalsIgnoreCase(fieldName)) {
                userAgent = value;
            } else if ("Host".equalsIgnoreCase(fieldName)) {
                host = value;
            } else if ("Connection".equalsIgnoreCase(fieldName)) {
                connection = value;
            } else if ("Accept-Encoding".equalsIgnoreCase(fieldName)) {
                acceptEncoding = value;
            } else if ("Content-Type".equalsIgnoreCase(fieldName)) {
                contentType = value;
            } else if ("Proxy-Authorization".equalsIgnoreCase(fieldName)) {
                proxyAuthorization = value;
            }
        }
    }

    public boolean isChunked() {
        return "chunked".equalsIgnoreCase(transferEncoding);
    }

    public boolean hasConnectionClose() {
        return "close".equalsIgnoreCase(connection);
    }

    public void setChunked() {
        if (this.transferEncoding != null) {
            headers.removeAll("Transfer-Encoding");
        }
        headers.add("Transfer-Encoding", "chunked");
        this.transferEncoding = "chunked";
    }

    public void setContentLength(int contentLength) {
        if (this.contentLength != -1) {
            headers.removeAll("Content-Length");
        }
        headers.add("Content-Length", Integer.toString(contentLength));
        this.contentLength = contentLength;
    }

    public void setUserAgent(String userAgent) {
        if (this.userAgent != null) {
            headers.removeAll("User-Agent");
        }
        headers.add("User-Agent", userAgent);
        this.userAgent = userAgent;
    }

    public void setHost(String host) {
        if (this.host != null) {
            headers.removeAll("Host");
        }
        headers.add("Host", host);
        this.host = host;
    }

    public void setConnection(String connection) {
        if (this.connection != null) {
            headers.removeAll("Connection");
        }
        headers.add("Connection", connection);
        this.connection = connection;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        if (this.acceptEncoding != null) {
            headers.removeAll("Accept-Encoding");
        }
        headers.add("Accept-Encoding", acceptEncoding);
        this.acceptEncoding = acceptEncoding;
    }

    public void setContentType(String contentType) {
        if (this.contentType != null) {
            headers.removeAll("Content-Type");
        }
        headers.add("Content-Type", contentType);
        this.contentType = contentType;
    }

    public void setIfModifiedSince(Date date) {
        if (ifModifiedSince != null) {
            headers.removeAll("If-Modified-Since");
        }
        String formattedDate = HttpDate.format(date);
        headers.add("If-Modified-Since", formattedDate);
        ifModifiedSince = formattedDate;
    }

    public void setIfNoneMatch(String ifNoneMatch) {
        if (this.ifNoneMatch != null) {
            headers.removeAll("If-None-Match");
        }
        headers.add("If-None-Match", ifNoneMatch);
        this.ifNoneMatch = ifNoneMatch;
    }

    /**
     * Returns true if the request contains conditions that save the server from
     * sending a response that the client has locally. When the caller adds
     * conditions, this cache won't participate in the request.
     */
    public boolean hasConditions() {
        return ifModifiedSince != null || ifNoneMatch != null;
    }

    public void addCookies(Map<String, List<String>> allCookieHeaders) {
        for (Map.Entry<String, List<String>> entry : allCookieHeaders.entrySet()) {
            String key = entry.getKey();
            if ("Cookie".equalsIgnoreCase(key) || "Cookie2".equalsIgnoreCase(key)) {
                headers.addAll(key, entry.getValue());
            }
        }
    }
}
