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

package java.net;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * An opaque key-value value pair held by an HTTP client to permit a stateful
 * session with an HTTP server. This class parses cookie headers for all three
 * commonly used HTTP cookie specifications:
 *
 * <ul>
 *     <li>The Netscape cookie spec is officially obsolete but widely used in
 *         practice. Each cookie contains one key-value pair and the following
 *         attributes: {@code Domain}, {@code Expires}, {@code Path}, and
 *         {@code Secure}. The {@link #getVersion() version} of cookies in this
 *         format is {@code 0}.
 *         <p>There are no accessors for the {@code Expires} attribute. When
 *         parsed, expires attributes are assigned to the {@link #getMaxAge()
 *         Max-Age} attribute as an offset from {@link System#currentTimeMillis()
 *         now}.
 *     <li><a href="http://www.ietf.org/rfc/rfc2109.txt">RFC 2109</a> formalizes
 *         the Netscape cookie spec. It replaces the {@code Expires} timestamp
 *         with a {@code Max-Age} duration and adds {@code Comment} and {@code
 *         Version} attributes. The {@link #getVersion() version} of cookies in
 *         this format is {@code 1}.
 *     <li><a href="http://www.ietf.org/rfc/rfc2965.txt">RFC 2965</a> refines
 *         RFC 2109. It adds {@code Discard}, {@code Port}, and {@code
 *         CommentURL} attributes and renames the header from {@code Set-Cookie}
 *         to {@code Set-Cookie2}. The {@link #getVersion() version} of cookies
 *         in this format is {@code 1}.
 * </ul>
 *
 * <p>This implementation silently discards unrecognized attributes. In
 * particular, the {@code HttpOnly} attribute is widely served but isn't in any
 * of the above specs. It was introduced by Internet Explorer to prevent server
 * cookies from being exposed in the DOM to JavaScript, etc.
 *
 * @since 1.6
 */
public final class HttpCookie implements Cloneable {

    private static final ThreadLocal<DateFormat[]> DATE_FORMATS = new ThreadLocal<DateFormat[]>() {
        @Override protected DateFormat[] initialValue() {
            return new DateFormat[] {
                    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"), // RFC 1123
                    new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz"),  // RFC 1036
                    new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy"),       // ANSI C asctime()
            };
        }
    };

    private static final Set<String> RESERVED_NAMES = new HashSet<String>();
    static {
        RESERVED_NAMES.add("comment");    //           RFC 2109  RFC 2965
        RESERVED_NAMES.add("commenturl"); //                     RFC 2965
        RESERVED_NAMES.add("discard");    //                     RFC 2965
        RESERVED_NAMES.add("domain");     // Netscape  RFC 2109  RFC 2965
        RESERVED_NAMES.add("expires");    // Netscape
        RESERVED_NAMES.add("max-age");    //           RFC 2109  RFC 2965
        RESERVED_NAMES.add("path");       // Netscape  RFC 2109  RFC 2965
        RESERVED_NAMES.add("port");       //                     RFC 2965
        RESERVED_NAMES.add("secure");     // Netscape  RFC 2109  RFC 2965
        RESERVED_NAMES.add("version");    //           RFC 2109  RFC 2965
    }

    /**
     * Returns true if {@code host} matches the domain pattern {@code domain}.
     *
     * @param domainPattern a host name (like {@code android.com} or {@code
     *     localhost}), or a pattern to match subdomains of a domain name (like
     *     {@code .android.com}). A special case pattern is {@code .local},
     *     which matches all hosts without a TLD (like {@code localhost}).
     * @param host the host name or IP address from an HTTP request.
     */
    public static boolean domainMatches(String domainPattern, String host) {
        if (domainPattern == null || host == null) {
            return false;
        }

        String a = host.toLowerCase();
        String b = domainPattern.toLowerCase();

        /*
         * From the spec: "both host names are IP addresses and their host name strings match
         * exactly; or both host names are FQDN strings and their host name strings match exactly"
         */
        if (a.equals(b) && (isFullyQualifiedDomainName(a, 0) || !InetAddress.isHostName(a))) {
            return true;
        }
        if (!isFullyQualifiedDomainName(a, 0)) {
            return b.equals(".local");
        }

        /*
         * From the spec: "A is a HDN string and has the form NB, where N is a
         * non-empty name string, B has the form .B', and B' is a HDN string.
         * (So, x.y.com domain-matches .Y.com but not Y.com.)
         */
        return a.length() > b.length()
                && a.endsWith(b)
                && ((b.startsWith(".") && isFullyQualifiedDomainName(b, 1)) || b.equals(".local"));
    }

    /**
     * Returns true if {@code cookie} should be sent to or accepted from {@code uri}. Cookies match
     * by directory prefix: URI "/foo" matches cookies "/foo", "/foo/" and "/foo/bar", but not "/"
     * or "/foobar".
     */
    static boolean pathMatches(URI uri, HttpCookie cookie) {
        String uriPath = matchablePath(uri.getPath());
        String cookiePath = matchablePath(cookie.getPath());
        return uriPath.startsWith(cookiePath);
    }

    /**
     * Returns a non-null path ending in "/".
     */
    private static String matchablePath(String path) {
        if (path == null) {
            return "/";
        } else if (path.endsWith("/")) {
            return path;
        } else {
            return path + "/";
        }
    }

    /**
     * Returns true if {@code s.substring(firstCharacter)} contains a dot
     * between its first and last characters, exclusive. This considers both
     * {@code android.com} and {@code co.uk} to be fully qualified domain names,
     * but not {@code android.com.}, {@code .com}. or {@code android}.
     */
    public static boolean isFullyQualifiedDomainName(String s, int firstCharacter) {
        int dotPosition = s.indexOf('.', firstCharacter + 1);
        return dotPosition != -1 && dotPosition < s.length() - 1;
    }

    /**
     * Constructs a cookie from a string. The string should comply with
     * set-cookie or set-cookie2 header format as specified in RFC 2965. Since
     * set-cookies2 syntax allows more than one cookie definitions in one
     * header, the returned object is a list.
     *
     * @param header
     *            a set-cookie or set-cookie2 header.
     * @return a list of constructed cookies
     * @throws IllegalArgumentException
     *             if the string does not comply with cookie specification, or
     *             the cookie name contains illegal characters, or reserved
     *             tokens of cookie specification appears
     * @throws NullPointerException
     *             if header is null
     */
    public static List<HttpCookie> parse(String header) {
        return new CookieParser(header).parse();
    }

    static class CookieParser {
        private static final String ATTRIBUTE_NAME_TERMINATORS = ",;= \t";
        private static final String WHITESPACE = " \t";
        private final String input;
        private final String inputLowerCase;
        private int pos = 0;

        /*
         * The cookie's version is set based on an overly complex heuristic:
         * If it has an expires attribute, the version is 0.
         * Otherwise, if it has a max-age attribute, the version is 1.
         * Otherwise, if the cookie started with "Set-Cookie2", the version is 1.
         * Otherwise, if it has any explicit version attributes, use the first one.
         * Otherwise, the version is 0.
         */
        boolean hasExpires = false;
        boolean hasMaxAge = false;
        boolean hasVersion = false;

        CookieParser(String input) {
            this.input = input;
            this.inputLowerCase = input.toLowerCase(Locale.US);
        }

        public List<HttpCookie> parse() {
            List<HttpCookie> cookies = new ArrayList<HttpCookie>(2);

            // The RI permits input without either the "Set-Cookie:" or "Set-Cookie2" headers.
            boolean pre2965 = true;
            if (inputLowerCase.startsWith("set-cookie2:")) {
                pos += "set-cookie2:".length();
                pre2965 = false;
                hasVersion = true;
            } else if (inputLowerCase.startsWith("set-cookie:")) {
                pos += "set-cookie:".length();
            }

            /*
             * Read a comma-separated list of cookies. Note that the values may contain commas!
             *   <NAME> "=" <VALUE> ( ";" <ATTR NAME> ( "=" <ATTR VALUE> )? )*
             */
            while (true) {
                String name = readAttributeName(false);
                if (name == null) {
                    if (cookies.isEmpty()) {
                        throw new IllegalArgumentException("No cookies in " + input);
                    }
                    return cookies;
                }

                if (!readEqualsSign()) {
                    throw new IllegalArgumentException(
                            "Expected '=' after " + name + " in " + input);
                }

                String value = readAttributeValue(pre2965 ? ";" : ",;");
                HttpCookie cookie = new HttpCookie(name, value);
                cookie.version = pre2965 ? 0 : 1;
                cookies.add(cookie);

                /*
                 * Read the attributes of the current cookie. Each iteration of this loop should
                 * enter with input either exhausted or prefixed with ';' or ',' as in ";path=/"
                 * and ",COOKIE2=value2".
                 */
                while (true) {
                    skipWhitespace();
                    if (pos == input.length()) {
                        break;
                    }

                    if (input.charAt(pos) == ',') {
                        pos++;
                        break; // a true comma delimiter; the current cookie is complete.
                    } else if (input.charAt(pos) == ';') {
                        pos++;
                    }

                    String attributeName = readAttributeName(true);
                    if (attributeName == null) {
                        continue; // for empty attribute as in "Set-Cookie: foo=Foo;;path=/"
                    }

                    /*
                     * Since expires and port attributes commonly include comma delimiters, always
                     * scan until a semicolon when parsing these attributes.
                     */
                    String terminators = pre2965
                            || "expires".equals(attributeName) || "port".equals(attributeName)
                            ? ";"
                            : ";,";
                    String attributeValue = null;
                    if (readEqualsSign()) {
                        attributeValue = readAttributeValue(terminators);
                    }
                    setAttribute(cookie, attributeName, attributeValue);
                }

                if (hasExpires) {
                    cookie.version = 0;
                } else if (hasMaxAge) {
                    cookie.version = 1;
                }
            }
        }

        private void setAttribute(HttpCookie cookie, String name, String value) {
            if (name.equals("comment") && cookie.comment == null) {
                cookie.comment = value;
            } else if (name.equals("commenturl") && cookie.commentURL == null) {
                cookie.commentURL = value;
            } else if (name.equals("discard")) {
                cookie.discard = true;
            } else if (name.equals("domain") && cookie.domain == null) {
                cookie.domain = value;
            } else if (name.equals("expires")) {
                hasExpires = true;
                if (cookie.maxAge == -1L) {
                    cookie.maxAge = 0; // only in case parsing fails
                    for (DateFormat dateFormat : DATE_FORMATS.get()) {
                        try {
                            Date date = dateFormat.parse(value);
                            cookie.setExpires(date);
                            return;
                        } catch (ParseException ignore) {
                        }
                    }
                }
            } else if (name.equals("max-age") && cookie.maxAge == -1L) {
                hasMaxAge = true;
                cookie.maxAge = Long.parseLong(value);
            } else if (name.equals("path") && cookie.path == null) {
                cookie.path = value;
            } else if (name.equals("port") && cookie.portList == null) {
                cookie.portList = value;
            } else if (name.equals("secure")) {
                cookie.secure = true;
            } else if (name.equals("version") && !hasVersion) {
                cookie.version = Integer.parseInt(value);
            }
        }

        /**
         * Returns the next attribute name, or null if the input has been
         * exhausted. Returns wth the cursor on the delimiter that follows.
         */
        private String readAttributeName(boolean returnLowerCase) {
            skipWhitespace();
            int c = find(ATTRIBUTE_NAME_TERMINATORS);
            String forSubstring = returnLowerCase ? inputLowerCase : input;
            String result = pos < c ? forSubstring.substring(pos, c) : null;
            pos = c;
            return result;
        }

        /**
         * Returns true if an equals sign was read and consumed.
         */
        private boolean readEqualsSign() {
            skipWhitespace();
            if (pos < input.length() && input.charAt(pos) == '=') {
                pos++;
                return true;
            }
            return false;
        }

        /**
         * Reads an attribute value, by parsing either a quoted string or until
         * the next character in {@code terminators}. The terminator character
         * is not consumed.
         */
        private String readAttributeValue(String terminators) {
            skipWhitespace();

            // quoted string: read 'til the close quote
            if (pos < input.length() && input.charAt(pos) == '"') {
                pos++;
                int closeQuote = input.indexOf('"', pos);
                if (closeQuote == -1) {
                    throw new IllegalArgumentException("Unterminated string literal in " + input);
                }
                String result = input.substring(pos, closeQuote);
                pos = closeQuote + 1;
                return result;
            }

            int c = find(terminators);
            String result = input.substring(pos, c);
            pos = c;
            return result;
        }

        /**
         * Returns the index of the next character in {@code chars}, or the end
         * of the string.
         */
        private int find(String chars) {
            for (int c = pos; c < input.length(); c++) {
                if (chars.indexOf(input.charAt(c)) != -1) {
                    return c;
                }
            }
            return input.length();
        }

        private void skipWhitespace() {
            for (; pos < input.length(); pos++) {
                if (WHITESPACE.indexOf(input.charAt(pos)) == -1) {
                    break;
                }
            }
        }
    }

    private String comment;
    private String commentURL;
    private boolean discard;
    private String domain;
    private long maxAge = -1l;
    private final String name;
    private String path;
    private String portList;
    private boolean secure;
    private String value;
    private int version = 1;

    /**
     * Creates a new cookie.
     *
     * @param name a non-empty string that contains only printable ASCII, no
     *     commas or semicolons, and is not prefixed with  {@code $}. May not be
     *     an HTTP attribute name.
     * @code value an opaque value from the HTTP server.
     * @throws IllegalArgumentException if {@code name} is invalid.
     */
    public HttpCookie(String name, String value) {
        String ntrim = name.trim(); // erase leading and trailing whitespace
        if (!isValidName(ntrim)) {
            throw new IllegalArgumentException();
        }

        this.name = ntrim;
        this.value = value;
    }


    private boolean isValidName(String n) {
        // name cannot be empty or begin with '$' or equals the reserved
        // attributes (case-insensitive)
        boolean isValid = !(n.length() == 0 || n.startsWith("$") || RESERVED_NAMES.contains(n.toLowerCase()));
        if (isValid) {
            for (int i = 0; i < n.length(); i++) {
                char nameChar = n.charAt(i);
                // name must be ASCII characters and cannot contain ';', ',' and
                // whitespace
                if (nameChar < 0
                        || nameChar >= 127
                        || nameChar == ';'
                        || nameChar == ','
                        || (Character.isWhitespace(nameChar) && nameChar != ' ')) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    /**
     * Returns the {@code Comment} attribute.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Returns the value of {@code CommentURL} attribute.
     */
    public String getCommentURL() {
        return commentURL;
    }

    /**
     * Returns the {@code Discard} attribute.
     */
    public boolean getDiscard() {
        return discard;
    }

    /**
     * Returns the {@code Domain} attribute.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Returns the {@code Max-Age} attribute, in delta-seconds.
     */
    public long getMaxAge() {
        return maxAge;
    }

    /**
     * Returns the name of this cookie.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@code Path} attribute. This cookie is visible to all
     * subpaths.
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the {@code Port} attribute, usually containing comma-separated
     * port numbers.
     */
    public String getPortlist() {
        return portList;
    }

    /**
     * Returns the {@code Secure} attribute.
     */
    public boolean getSecure() {
        return secure;
    }

    /**
     * Returns the value of this cookie.
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the version of this cookie.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Returns true if this cookie's Max-Age is 0.
     */
    public boolean hasExpired() {
        // -1 indicates the cookie will persist until browser shutdown
        // so the cookie is not expired.
        if (maxAge == -1l) {
            return false;
        }

        boolean expired = false;
        if (maxAge <= 0l) {
            expired = true;
        }
        return expired;
    }

    /**
     * Set the {@code Comment} attribute of this cookie.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Set the {@code CommentURL} attribute of this cookie.
     */
    public void setCommentURL(String commentURL) {
        this.commentURL = commentURL;
    }

    /**
     * Set the {@code Discard} attribute of this cookie.
     */
    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    /**
     * Set the {@code Domain} attribute of this cookie. HTTP clients send
     * cookies only to matching domains.
     */
    public void setDomain(String pattern) {
        domain = pattern == null ? null : pattern.toLowerCase();
    }

    /**
     * Sets the {@code Max-Age} attribute of this cookie.
     */
    public void setMaxAge(long deltaSeconds) {
        maxAge = deltaSeconds;
    }

    private void setExpires(Date expires) {
        maxAge = (expires.getTime() - System.currentTimeMillis()) / 1000;
    }

    /**
     * Set the {@code Path} attribute of this cookie. HTTP clients send cookies
     * to this path and its subpaths.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Set the {@code Port} attribute of this cookie.
     */
    public void setPortlist(String portList) {
        this.portList = portList;
    }

    /**
     * Sets the {@code Secure} attribute of this cookie.
     */
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * Sets the opaque value of this cookie.
     */
    public void setValue(String value) {
        // FIXME: According to spec, version 0 cookie value does not allow many
        // symbols. But RI does not implement it. Follow RI temporarily.
        this.value = value;
    }

    /**
     * Sets the {@code Version} attribute of the cookie.
     *
     * @throws IllegalArgumentException if v is neither 0 nor 1
     */
    public void setVersion(int v) {
        if (v != 0 && v != 1) {
            throw new IllegalArgumentException();
        }
        version = v;
    }

    @Override public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * Returns true if {@code object} is a cookie with the same domain, name and
     * path. Domain and name use case-insensitive comparison; path uses a
     * case-sensitive comparison.
     */
    @Override public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof HttpCookie) {
            HttpCookie that = (HttpCookie) object;
            return name.equalsIgnoreCase(that.getName())
                    && (domain != null ? domain.equalsIgnoreCase(that.domain) : that.domain == null)
                    && (path != null ? path.equals(that.path) : that.path == null);
        }
        return false;
    }

    /**
     * Returns the hash code of this HTTP cookie: <pre>   {@code
     *   name.toLowerCase().hashCode()
     *       + (domain == null ? 0 : domain.toLowerCase().hashCode())
     *       + (path == null ? 0 : path.hashCode())
     * }</pre>
     */
    @Override public int hashCode() {
        return name.toLowerCase().hashCode()
                + (domain == null ? 0 : domain.toLowerCase().hashCode())
                + (path == null ? 0 : path.hashCode());
    }

    /**
     * Returns a string representing this cookie in the format used by the
     * {@code Cookie} header line in an HTTP request.
     */
    @Override public String toString() {
        if (version == 0) {
            return name + "=" + value;
        }

        StringBuilder result = new StringBuilder()
                .append(name)
                .append("=")
                .append("\"")
                .append(value)
                .append("\"");
        appendAttribute(result, "Path", path);
        appendAttribute(result, "Domain", domain);
        appendAttribute(result, "Port", portList);
        return result.toString();
    }

    private void appendAttribute(StringBuilder builder, String name, String value) {
        if (value != null && builder != null) {
            builder.append(";$");
            builder.append(name);
            builder.append("=\"");
            builder.append(value);
            builder.append("\"");
        }
    }
}