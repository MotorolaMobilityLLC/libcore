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

package java.net;

/**
 * An implementation of this class is able to obtain authentication information
 * for a connection in several ways. For this purpose it has to set the default
 * authenticator which extends {@code Authenticator} by {@code
 * setDefault(Authenticator a)}. Then it should override {@code
 * getPasswordAuthentication()} which dictates how the authentication info is
 * obtained. Usually, it prompts the user for the required input.
 * 
 * @see #setDefault
 * @see #getPasswordAuthentication
 * @since Android 1.0
 */
public abstract class Authenticator {

    // the default authenticator that needs to be set
    private static Authenticator thisAuthenticator;

    private static final NetPermission requestPasswordAuthenticationPermission = new NetPermission(
            "requestPasswordAuthentication"); //$NON-NLS-1$

    private static final NetPermission setDefaultAuthenticatorPermission = new NetPermission(
            "setDefaultAuthenticator"); //$NON-NLS-1$

    // the requester connection info
    private String host;

    private InetAddress addr;

    private int port;

    private String protocol;

    private String prompt;

    private String scheme;

    private URL url;

    private RequestorType rt;

    /**
     * Returns the collected username and password for authorization. The
     * subclass has to override this method to return a value different to the
     * default which is {@code null}.
     * <p>
     * Returns {@code null} by default.
     * </p>
     * 
     * @return collected password authentication data.
     * @since Android 1.0
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        return null;
    }

    /**
     * Returns the port of the connection that requests authorization.
     * 
     * @return port of the connection.
     * @since Android 1.0
     */
    protected final int getRequestingPort() {
        return this.port;
    }

    /**
     * Returns the address of the connection that requests authorization or
     * {@code null} if unknown.
     * 
     * @return address of the connection.
     * @since Android 1.0
     */
    protected final InetAddress getRequestingSite() {
        return this.addr;
    }

    /**
     * Returns the realm (prompt string) of the connection that requests
     * authorization.
     * 
     * @return prompt string of the connection.
     * @since Android 1.0
     */
    protected final String getRequestingPrompt() {
        return this.prompt;
    }

    /**
     * Returns the protocol of the connection that requests authorization.
     * 
     * @return protocol of the connection.
     * @since Android 1.0
     */
    protected final String getRequestingProtocol() {
        return this.protocol;
    }

    /**
     * Returns the scheme of the connection that requests authorization, for
     * example HTTP Basic Authentication.
     * 
     * @return scheme of the connection.
     * @since Android 1.0
     */
    protected final String getRequestingScheme() {
        return this.scheme;
    }

    /**
     * If the permission check of the security manager does not result in a
     * security exception, this method invokes the methods of the registered
     * authenticator to get the authentication info.
     * 
     * @return password authentication info or {@code null} if no authenticator
     *         exists.
     * @param rAddr
     *            address of the connection that requests authentication.
     * @param rPort
     *            port of the connection that requests authentication.
     * @param rProtocol
     *            protocol of the connection that requests authentication.
     * @param rPrompt
     *            realm of the connection that requests authentication.
     * @param rScheme
     *            scheme of the connection that requests authentication.
     * @throws SecurityException
     *             if a security manager denies the password authentication
     *             permission.
     * @since Android 1.0
     */
    public static synchronized PasswordAuthentication requestPasswordAuthentication(
            InetAddress rAddr, int rPort, String rProtocol, String rPrompt,
            String rScheme) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(requestPasswordAuthenticationPermission);
        }
        if (thisAuthenticator == null) {
            return null;
        }
        // set the requester info so it knows what it is requesting
        // authentication for
        thisAuthenticator.addr = rAddr;
        thisAuthenticator.port = rPort;
        thisAuthenticator.protocol = rProtocol;
        thisAuthenticator.prompt = rPrompt;
        thisAuthenticator.scheme = rScheme;
        thisAuthenticator.rt = RequestorType.SERVER;

        // returns the authentication info obtained by the registered
        // Authenticator
        return thisAuthenticator.getPasswordAuthentication();
    }

    /**
     * Sets {@code a} as the default authenticator. It will be called whenever
     * the realm that the URL is pointing to requires authorization. If there is
     * a security manager set then the caller must have the appropriate {@code
     * NetPermission}.
     * 
     * @param a
     *            authenticator which has to be set as default.
     * @throws SecurityException
     *             if a security manager denies the password authentication
     *             permission.
     * @since Android 1.0
     */
    public static void setDefault(Authenticator a) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(setDefaultAuthenticatorPermission);
        }
        thisAuthenticator = a;
    }

    /**
     * If the permission check of the security manager does not result in a
     * security exception, this method invokes the methods of the registered
     * authenticator to get the authentication info.
     * 
     * @return password authentication info or {@code null} if no authenticator
     *         exists.
     * @param rHost
     *            host name of the connection that requests authentication.
     * @param rAddr
     *            address of the connection that requests authentication.
     * @param rPort
     *            port of the connection that requests authentication.
     * @param rProtocol
     *            protocol of the connection that requests authentication.
     * @param rPrompt
     *            realm of the connection that requests authentication.
     * @param rScheme
     *            scheme of the connection that requests authentication.
     * @throws SecurityException
     *             if a security manager denies the password authentication
     *             permission.
     * @since Android 1.0
     */
    public static synchronized PasswordAuthentication requestPasswordAuthentication(
            String rHost, InetAddress rAddr, int rPort, String rProtocol,
            String rPrompt, String rScheme) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(requestPasswordAuthenticationPermission);
        }
        if (thisAuthenticator == null) {
            return null;
        }
        // set the requester info so it knows what it is requesting
        // authentication for
        thisAuthenticator.host = rHost;
        thisAuthenticator.addr = rAddr;
        thisAuthenticator.port = rPort;
        thisAuthenticator.protocol = rProtocol;
        thisAuthenticator.prompt = rPrompt;
        thisAuthenticator.scheme = rScheme;
        thisAuthenticator.rt = RequestorType.SERVER;

        // returns the authentication info obtained by the registered
        // Authenticator
        return thisAuthenticator.getPasswordAuthentication();
    }

    /**
     * Returns the host name of the connection that requests authentication or
     * {@code null} if unknown.
     * 
     * @return name of the requesting host or {@code null}.
     * @since Android 1.0
     */
    protected final String getRequestingHost() {
        return host;
    }

    /**
     * If the permission check of the security manager does not result in a
     * security exception, this method invokes the methods of the registered
     * authenticator to get the authentication info.
     * 
     * @return password authentication info or {@code null} if no authenticator
     *         exists.
     * @param rHost
     *            host name of the connection that requests authentication.
     * @param rAddr
     *            address of the connection that requests authentication.
     * @param rPort
     *            port of the connection that requests authentication.
     * @param rProtocol
     *            protocol of the connection that requests authentication.
     * @param rPrompt
     *            realm of the connection that requests authentication.
     * @param rScheme
     *            scheme of the connection that requests authentication.
     * @param rURL
     *            url of the connection that requests authentication.
     * @param reqType
     *            requestor type of the connection that requests authentication.
     * @throws SecurityException
     *             if a security manager denies the password authentication
     *             permission.
     * @since Android 1.0
     */
    public static PasswordAuthentication requestPasswordAuthentication(
            String rHost, InetAddress rAddr, int rPort, String rProtocol,
            String rPrompt, String rScheme, URL rURL,
            Authenticator.RequestorType reqType) {
        SecurityManager sm = System.getSecurityManager();
        if (null != sm) {
            sm.checkPermission(requestPasswordAuthenticationPermission);
        }
        if (null == thisAuthenticator) {
            return null;
        }
        // sets the requester info so it knows what it is requesting
        // authentication for
        thisAuthenticator.host = rHost;
        thisAuthenticator.addr = rAddr;
        thisAuthenticator.port = rPort;
        thisAuthenticator.protocol = rProtocol;
        thisAuthenticator.prompt = rPrompt;
        thisAuthenticator.scheme = rScheme;
        thisAuthenticator.url = rURL;
        thisAuthenticator.rt = reqType;

        // returns the authentication info obtained by the registered
        // Authenticator
        return thisAuthenticator.getPasswordAuthentication();

    }

    /**
     * Returns the URL of the authentication request.
     * 
     * @return authentication request url.
     * @since Android 1.0
     */
    protected URL getRequestingURL() {
        return url;
    }

    /**
     * Returns the type of this request, it can be {@code PROXY} or {@code SERVER}.
     * 
     * @return RequestorType of the authentication request.
     * @since Android 1.0
     */
    protected Authenticator.RequestorType getRequestorType() {
        return rt;
    }

    /**
     * Enumeration class for the origin of the authentication request.
     * 
     * @since Android 1.0
     */
    public enum RequestorType {

        /**
         * Type of proxy server
         */
        PROXY,

        /**
         * Type of origin server
         */
        SERVER
    }
}
