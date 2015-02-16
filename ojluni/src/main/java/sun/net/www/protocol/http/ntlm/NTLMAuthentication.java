/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.net.www.protocol.http.ntlm;

import com.sun.security.ntlm.Client;
import com.sun.security.ntlm.NTLMException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.UnknownHostException;
import java.net.URL;
import java.security.GeneralSecurityException;

import sun.net.www.HeaderParser;
import sun.net.www.protocol.http.AuthenticationInfo;
import sun.net.www.protocol.http.AuthScheme;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 * NTLMAuthentication:
 *
 * @author Michael McMahon
 */

/*
 * NTLM authentication is nominally based on the framework defined in RFC2617,
 * but differs from the standard (Basic & Digest) schemes as follows:
 *
 * 1. A complete authentication requires three request/response transactions
 *    as shown below:
 *            REQ ------------------------------->
 *            <---- 401 (signalling NTLM) --------
 *
 *            REQ (with type1 NTLM msg) --------->
 *            <---- 401 (with type 2 NTLM msg) ---
 *
 *            REQ (with type3 NTLM msg) --------->
 *            <---- OK ---------------------------
 *
 * 2. The scope of the authentication is the TCP connection (which must be kept-alive)
 *    after the type2 response is received. This means that NTLM does not work end-to-end
 *    through a proxy, rather between client and proxy, or between client and server (with no proxy)
 */

public class NTLMAuthentication extends AuthenticationInfo {
    private static final long serialVersionUID = 170L;

    private static final NTLMAuthenticationCallback NTLMAuthCallback =
        NTLMAuthenticationCallback.getNTLMAuthenticationCallback();

    private String hostname;
    private static String defaultDomain; /* Domain to use if not specified by user */

    static {
        defaultDomain = java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("http.auth.ntlm.domain",
                                                      "domain"));
    };

    public static boolean supportsTransparentAuth () {
        return false;
    }

    /**
     * Returns true if the given site is trusted, i.e. we can try
     * transparent Authentication.
     */
    public static boolean isTrustedSite(URL url) {
        return NTLMAuthCallback.isTrustedSite(url);
    }

    private void init0() {

        hostname = java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<String>() {
            public String run() {
                String localhost;
                try {
                    localhost = InetAddress.getLocalHost().getHostName().toUpperCase();
                } catch (UnknownHostException e) {
                     localhost = "localhost";
                }
                return localhost;
            }
        });
        int x = hostname.indexOf ('.');
        if (x != -1) {
            hostname = hostname.substring (0, x);
        }
    };

    PasswordAuthentication pw;

    Client client;
    /**
     * Create a NTLMAuthentication:
     * Username may be specified as domain<BACKSLASH>username in the application Authenticator.
     * If this notation is not used, then the domain will be taken
     * from a system property: "http.auth.ntlm.domain".
     */
    public NTLMAuthentication(boolean isProxy, URL url, PasswordAuthentication pw) {
        super(isProxy ? PROXY_AUTHENTICATION : SERVER_AUTHENTICATION,
                AuthScheme.NTLM,
                url,
                "");
        init (pw);
    }

    private void init (PasswordAuthentication pw) {
        String username;
        String ntdomain;
        char[] password;
        this.pw = pw;
        String s = pw.getUserName();
        int i = s.indexOf ('\\');
        if (i == -1) {
            username = s;
            ntdomain = defaultDomain;
        } else {
            ntdomain = s.substring (0, i).toUpperCase();
            username = s.substring (i+1);
        }
        password = pw.getPassword();
        init0();
        try {
            client = new Client(System.getProperty("ntlm.version"), hostname,
                    username, ntdomain, password);
        } catch (NTLMException ne) {
            try {
                client = new Client(null, hostname, username, ntdomain, password);
            } catch (NTLMException ne2) {
                // Will never happen
                throw new AssertionError("Really?");
            }
        }
    }

   /**
    * Constructor used for proxy entries
    */
    public NTLMAuthentication(boolean isProxy, String host, int port,
                                PasswordAuthentication pw) {
        super(isProxy ? PROXY_AUTHENTICATION : SERVER_AUTHENTICATION,
                AuthScheme.NTLM,
                host,
                port,
                "");
        init (pw);
    }

    /**
     * @return true if this authentication supports preemptive authorization
     */
    @Override
    public boolean supportsPreemptiveAuthorization() {
        return false;
    }

    /**
     * Not supported. Must use the setHeaders() method
     */
    @Override
    public String getHeaderValue(URL url, String method) {
        throw new RuntimeException ("getHeaderValue not supported");
    }

    /**
     * Check if the header indicates that the current auth. parameters are stale.
     * If so, then replace the relevant field with the new value
     * and return true. Otherwise return false.
     * returning true means the request can be retried with the same userid/password
     * returning false means we have to go back to the user to ask for a new
     * username password.
     */
    @Override
    public boolean isAuthorizationStale (String header) {
        return false; /* should not be called for ntlm */
    }

    /**
     * Set header(s) on the given connection.
     * @param conn The connection to apply the header(s) to
     * @param p A source of header values for this connection, not used because
     *          HeaderParser converts the fields to lower case, use raw instead
     * @param raw The raw header field.
     * @return true if all goes well, false if no headers were set.
     */
    @Override
    public synchronized boolean setHeaders(HttpURLConnection conn, HeaderParser p, String raw) {

        try {
            String response;
            if (raw.length() < 6) { /* NTLM<sp> */
                response = buildType1Msg ();
            } else {
                String msg = raw.substring (5); /* skip NTLM<sp> */
                response = buildType3Msg (msg);
            }
            conn.setAuthenticationProperty(getHeaderName(), response);
            return true;
        } catch (IOException e) {
            return false;
        } catch (GeneralSecurityException e) {
            return false;
        }
    }

    private String buildType1Msg () {
        byte[] msg = client.type1();
        String result = "NTLM " + (new B64Encoder()).encode (msg);
        return result;
    }

    private String buildType3Msg (String challenge) throws GeneralSecurityException,
                                                           IOException  {
        /* First decode the type2 message to get the server nonce */
        /* nonce is located at type2[24] for 8 bytes */

        byte[] type2 = (new sun.misc.BASE64Decoder()).decodeBuffer (challenge);
        byte[] nonce = new byte[8];
        new java.util.Random().nextBytes(nonce);
        byte[] msg = client.type3(type2, nonce);
        String result = "NTLM " + (new B64Encoder()).encode (msg);
        return result;
    }
}

class B64Encoder extends sun.misc.BASE64Encoder {
    /* to force it to to the entire encoding in one line */
    protected int bytesPerLine () {
        return 1024;
    }
}
