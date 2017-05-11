/*
 * Copyright (C) 2014 The Android Open Source Project
 * Copyright (c) 1994, 2010, Oracle and/or its affiliates. All rights reserved.
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

/**
 * FTP stream opener.
 */

package sun.net.www.protocol.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.SocketPermission;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.security.Permission;
import libcore.net.NetworkSecurityPolicy;
import sun.net.NetworkClient;
import sun.net.www.MessageHeader;
import sun.net.www.MeteredStream;
import sun.net.www.URLConnection;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;
import sun.net.ProgressSource;
import sun.net.ProgressMonitor;
import sun.net.www.ParseUtil;
import sun.security.action.GetPropertyAction;


/**
 * This class Opens an FTP input (or output) stream given a URL.
 * It works as a one shot FTP transfer :
 * <UL>
 * <LI>Login</LI>
 * <LI>Get (or Put) the file</LI>
 * <LI>Disconnect</LI>
 * </UL>
 * You should not have to use it directly in most cases because all will be handled
 * in a abstract layer. Here is an example of how to use the class :
 * <P>
 * <code>URL url = new URL("ftp://ftp.sun.com/pub/test.txt");<p>
 * UrlConnection con = url.openConnection();<p>
 * InputStream is = con.getInputStream();<p>
 * ...<p>
 * is.close();</code>
 *
 * @see sun.net.ftp.FtpClient
 */
public class FtpURLConnection extends URLConnection {

    // Android-changed: Removed support for proxying FTP over HTTP.
    // It relies on the removed sun.net.www.protocol.http.HttpURLConnection API.
    /*
    // In case we have to use proxies, we use HttpURLConnection
    HttpURLConnection http = null;
    */
    private Proxy instProxy;

    InputStream is = null;
    OutputStream os = null;

    FtpClient ftp = null;
    Permission permission;

    String password;
    String user;

    String host;
    String pathname;
    String filename;
    String fullpath;
    int port;
    static final int NONE = 0;
    static final int ASCII = 1;
    static final int BIN = 2;
    static final int DIR = 3;
    int type = NONE;
    /* Redefine timeouts from java.net.URLConnection as we need -1 to mean
     * not set. This is to ensure backward compatibility.
     */
    private int connectTimeout = NetworkClient.DEFAULT_CONNECT_TIMEOUT;;
    private int readTimeout = NetworkClient.DEFAULT_READ_TIMEOUT;;

    /**
     * For FTP URLs we need to have a special InputStream because we
     * need to close 2 sockets after we're done with it :
     *  - The Data socket (for the file).
     *   - The command socket (FtpClient).
     * Since that's the only class that needs to see that, it is an inner class.
     */
    protected class FtpInputStream extends FilterInputStream {
        FtpClient ftp;
        FtpInputStream(FtpClient cl, InputStream fd) {
            super(new BufferedInputStream(fd));
            ftp = cl;
        }

        @Override
        public void close() throws IOException {
            super.close();
            if (ftp != null) {
                ftp.close();
            }
        }
    }

    /**
     * For FTP URLs we need to have a special OutputStream because we
     * need to close 2 sockets after we're done with it :
     *  - The Data socket (for the file).
     *   - The command socket (FtpClient).
     * Since that's the only class that needs to see that, it is an inner class.
     */
    protected class FtpOutputStream extends FilterOutputStream {
        FtpClient ftp;
        FtpOutputStream(FtpClient cl, OutputStream fd) {
            super(fd);
            ftp = cl;
        }

        @Override
        public void close() throws IOException {
            super.close();
            if (ftp != null) {
                ftp.close();
            }
        }
    }

    /**
     * Creates an FtpURLConnection from a URL.
     *
     * @param   url     The <code>URL</code> to retrieve or store.
     */
    // Android-changed: Ctors can throw IOException for NetworkSecurityPolicy enforcement.
    // public FtpURLConnection(URL url) {
    public FtpURLConnection(URL url) throws IOException {
        this(url, null);
    }

    /**
     * Same as FtpURLconnection(URL) with a per connection proxy specified
     */
    // Android-changed: Ctors can throw IOException for NetworkSecurityPolicy enforcement.
    // FtpURLConnection(URL url, Proxy p) {
    FtpURLConnection(URL url, Proxy p) throws IOException {
        super(url);
        instProxy = p;
        host = url.getHost();
        port = url.getPort();
        String userInfo = url.getUserInfo();
        // BEGIN Android-added: Enforce NetworkSecurityPolicy.isClearTextTrafficPermitted().
        if (!NetworkSecurityPolicy.getInstance().isCleartextTrafficPermitted()) {
            // Cleartext network traffic is not permitted -- refuse this connection.
            throw new IOException("Cleartext traffic not permitted: "
                    + url.getProtocol() + "://" + host
                    + ((url.getPort() >= 0) ? (":" + url.getPort()) : ""));
        }
        // END Android-added: Enforce NetworkSecurityPolicy.isClearTextTrafficPermitted().

        if (userInfo != null) { // get the user and password
            int delimiter = userInfo.indexOf(':');
            if (delimiter == -1) {
                user = ParseUtil.decode(userInfo);
                password = null;
            } else {
                user = ParseUtil.decode(userInfo.substring(0, delimiter++));
                password = ParseUtil.decode(userInfo.substring(delimiter));
            }
        }
    }

    private void setTimeouts() {
        if (ftp != null) {
            if (connectTimeout >= 0) {
                ftp.setConnectTimeout(connectTimeout);
            }
            if (readTimeout >= 0) {
                ftp.setReadTimeout(readTimeout);
            }
        }
    }

    /**
     * Connects to the FTP server and logs in.
     *
     * @throws  FtpLoginException if the login is unsuccessful
     * @throws  FtpProtocolException if an error occurs
     * @throws  UnknownHostException if trying to connect to an unknown host
     */

    public synchronized void connect() throws IOException {
        if (connected) {
            return;
        }

        Proxy p = null;
        if (instProxy == null) { // no per connection proxy specified
            /**
             * Do we have to use a proxy?
             */
            ProxySelector sel = java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<ProxySelector>() {
                        public ProxySelector run() {
                            return ProxySelector.getDefault();
                        }
                    });
            if (sel != null) {
                URI uri = sun.net.www.ParseUtil.toURI(url);
                Iterator<Proxy> it = sel.select(uri).iterator();
                while (it.hasNext()) {
                    p = it.next();
                    if (p == null || p == Proxy.NO_PROXY ||
                        p.type() == Proxy.Type.SOCKS) {
                        break;
                    }
                    if (p.type() != Proxy.Type.HTTP ||
                            !(p.address() instanceof InetSocketAddress)) {
                        sel.connectFailed(uri, p.address(), new IOException("Wrong proxy type"));
                        continue;
                    }
                    // OK, we have an http proxy
                    // BEGIN Android-changed: Removed support for proxying FTP over HTTP.
                    // It relies on the removed sun.net.www.protocol.http.HttpURLConnection API.
                    /*
                    InetSocketAddress paddr = (InetSocketAddress) p.address();
                    try {
                        http = new HttpURLConnection(url, p);
                        http.setDoInput(getDoInput());
                        http.setDoOutput(getDoOutput());
                        if (connectTimeout >= 0) {
                            http.setConnectTimeout(connectTimeout);
                        }
                        if (readTimeout >= 0) {
                            http.setReadTimeout(readTimeout);
                        }
                        http.connect();
                        connected = true;
                        return;
                    } catch (IOException ioe) {
                        sel.connectFailed(uri, paddr, ioe);
                        http = null;
                    }
                    */
                    sel.connectFailed(uri, p.address(), new IOException("FTP connections over HTTP proxy not supported"));
                    continue;
                    // END Android-changed: Removed support for proxying FTP over HTTP.
                }
            }
        } else { // per connection proxy specified
            p = instProxy;
            // BEGIN Android-changed: Removed support for proxying FTP over HTTP.
            // It relies on the removed sun.net.www.protocol.http.HttpURLConnection API.
            // As specified in the documentation for URL.openConnection(Proxy), we
            // ignore the unsupported proxy and attempt a normal (direct) connection
            /*
            if (p.type() == Proxy.Type.HTTP) {
                http = new HttpURLConnection(url, instProxy);
                http.setDoInput(getDoInput());
                http.setDoOutput(getDoOutput());
                if (connectTimeout >= 0) {
                    http.setConnectTimeout(connectTimeout);
                }
                if (readTimeout >= 0) {
                    http.setReadTimeout(readTimeout);
                }
                http.connect();
                connected = true;
                return;
            }
            */
            // END Android-changed: Removed support for proxying FTP over HTTP.
        }

        if (user == null) {
            user = "anonymous";
            String vers = java.security.AccessController.doPrivileged(
                    new GetPropertyAction("java.version"));
            password = java.security.AccessController.doPrivileged(
                    new GetPropertyAction("ftp.protocol.user",
                                          "Java" + vers + "@"));
        }
        try {
            ftp = FtpClient.create();
            if (p != null) {
                ftp.setProxy(p);
            }
            setTimeouts();
            if (port != -1) {
                ftp.connect(new InetSocketAddress(host, port));
            } else {
                ftp.connect(new InetSocketAddress(host, FtpClient.defaultPort()));
            }
        } catch (UnknownHostException e) {
            // Maybe do something smart here, like use a proxy like iftp.
            // Just keep throwing for now.
            throw e;
        } catch (FtpProtocolException fe) {
            throw new IOException(fe);
        }
        try {
            ftp.login(user, password == null ? null : password.toCharArray());
        } catch (sun.net.ftp.FtpProtocolException e) {
            ftp.close();
            // Backward compatibility
            throw new sun.net.ftp.FtpLoginException("Invalid username/password");
        }
        connected = true;
    }


    /*
     * Decodes the path as per the RFC-1738 specifications.
     */
    private void decodePath(String path) {
        int i = path.indexOf(";type=");
        if (i >= 0) {
            String s1 = path.substring(i + 6, path.length());
            if ("i".equalsIgnoreCase(s1)) {
                type = BIN;
            }
            if ("a".equalsIgnoreCase(s1)) {
                type = ASCII;
            }
            if ("d".equalsIgnoreCase(s1)) {
                type = DIR;
            }
            path = path.substring(0, i);
        }
        if (path != null && path.length() > 1 &&
                path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path == null || path.length() == 0) {
            path = "./";
        }
        if (!path.endsWith("/")) {
            i = path.lastIndexOf('/');
            if (i > 0) {
                filename = path.substring(i + 1, path.length());
                filename = ParseUtil.decode(filename);
                pathname = path.substring(0, i);
            } else {
                filename = ParseUtil.decode(path);
                pathname = null;
            }
        } else {
            pathname = path.substring(0, path.length() - 1);
            filename = null;
        }
        if (pathname != null) {
            fullpath = pathname + "/" + (filename != null ? filename : "");
        } else {
            fullpath = filename;
        }
    }

    /*
     * As part of RFC-1738 it is specified that the path should be
     * interpreted as a series of FTP CWD commands.
     * This is because, '/' is not necessarly the directory delimiter
     * on every systems.
     */
    private void cd(String path) throws FtpProtocolException, IOException {
        if (path == null || path.isEmpty()) {
            return;
        }
        if (path.indexOf('/') == -1) {
            ftp.changeDirectory(ParseUtil.decode(path));
            return;
        }

        StringTokenizer token = new StringTokenizer(path, "/");
        while (token.hasMoreTokens()) {
            ftp.changeDirectory(ParseUtil.decode(token.nextToken()));
        }
    }

    /**
     * Get the InputStream to retreive the remote file. It will issue the
     * "get" (or "dir") command to the ftp server.
     *
     * @return  the <code>InputStream</code> to the connection.
     *
     * @throws  IOException if already opened for output
     * @throws  FtpProtocolException if errors occur during the transfert.
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!connected) {
            connect();
        }

        // Android-changed: Removed support for proxying FTP over HTTP.
        // It relies on the removed sun.net.www.protocol.http.HttpURLConnection API.
        /*
        if (http != null) {
            return http.getInputStream();
        }
        */

        if (os != null) {
            throw new IOException("Already opened for output");
        }

        if (is != null) {
            return is;
        }

        MessageHeader msgh = new MessageHeader();

        boolean isAdir = false;
        try {
            decodePath(url.getPath());
            if (filename == null || type == DIR) {
                ftp.setAsciiType();
                cd(pathname);
                if (filename == null) {
                    is = new FtpInputStream(ftp, ftp.list(null));
                } else {
                    is = new FtpInputStream(ftp, ftp.nameList(filename));
                }
            } else {
                if (type == ASCII) {
                    ftp.setAsciiType();
                } else {
                    ftp.setBinaryType();
                }
                cd(pathname);
                is = new FtpInputStream(ftp, ftp.getFileStream(filename));
            }

            /* Try to get the size of the file in bytes.  If that is
            successful, then create a MeteredStream. */
            try {
                long l = ftp.getLastTransferSize();
                msgh.add("content-length", Long.toString(l));
                if (l > 0) {

                    // Wrap input stream with MeteredStream to ensure read() will always return -1
                    // at expected length.

                    // Check if URL should be metered
                    boolean meteredInput = ProgressMonitor.getDefault().shouldMeterInput(url, "GET");
                    ProgressSource pi = null;

                    if (meteredInput) {
                        pi = new ProgressSource(url, "GET", l);
                        pi.beginTracking();
                    }

                    is = new MeteredStream(is, pi, l);
                }
            } catch (Exception e) {
                e.printStackTrace();
            /* do nothing, since all we were doing was trying to
            get the size in bytes of the file */
            }

            if (isAdir) {
                msgh.add("content-type", "text/plain");
                msgh.add("access-type", "directory");
            } else {
                msgh.add("access-type", "file");
                String ftype = guessContentTypeFromName(fullpath);
                if (ftype == null && is.markSupported()) {
                    ftype = guessContentTypeFromStream(is);
                }
                if (ftype != null) {
                    msgh.add("content-type", ftype);
                }
            }
        } catch (FileNotFoundException e) {
            try {
                cd(fullpath);
                /* if that worked, then make a directory listing
                and build an html stream with all the files in
                the directory */
                ftp.setAsciiType();

                is = new FtpInputStream(ftp, ftp.list(null));
                msgh.add("content-type", "text/plain");
                msgh.add("access-type", "directory");
            } catch (IOException ex) {
                throw new FileNotFoundException(fullpath);
            } catch (FtpProtocolException ex2) {
                throw new FileNotFoundException(fullpath);
            }
        } catch (FtpProtocolException ftpe) {
            throw new IOException(ftpe);
        }
        setProperties(msgh);
        return is;
    }

    /**
     * Get the OutputStream to store the remote file. It will issue the
     * "put" command to the ftp server.
     *
     * @return  the <code>OutputStream</code> to the connection.
     *
     * @throws  IOException if already opened for input or the URL
     *          points to a directory
     * @throws  FtpProtocolException if errors occur during the transfert.
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!connected) {
            connect();
        }

        // Android-changed: Removed support for proxying FTP over HTTP.
        // It relies on the removed sun.net.www.protocol.http.HttpURLConnection API.
        /*
        if (http != null) {
            OutputStream out = http.getOutputStream();
            // getInputStream() is neccessary to force a writeRequests()
            // on the http client.
            http.getInputStream();
            return out;
        }
        */

        if (is != null) {
            throw new IOException("Already opened for input");
        }

        if (os != null) {
            return os;
        }

        decodePath(url.getPath());
        if (filename == null || filename.length() == 0) {
            throw new IOException("illegal filename for a PUT");
        }
        try {
            if (pathname != null) {
                cd(pathname);
            }
            if (type == ASCII) {
                ftp.setAsciiType();
            } else {
                ftp.setBinaryType();
            }
            os = new FtpOutputStream(ftp, ftp.putFileStream(filename, false));
        } catch (FtpProtocolException e) {
            throw new IOException(e);
        }
        return os;
    }

    String guessContentTypeFromFilename(String fname) {
        return guessContentTypeFromName(fname);
    }

    /**
     * Gets the <code>Permission</code> associated with the host & port.
     *
     * @return  The <code>Permission</code> object.
     */
    @Override
    public Permission getPermission() {
        if (permission == null) {
            int urlport = url.getPort();
            urlport = urlport < 0 ? FtpClient.defaultPort() : urlport;
            String urlhost = this.host + ":" + urlport;
            permission = new SocketPermission(urlhost, "connect");
        }
        return permission;
    }

    /**
     * Sets the general request property. If a property with the key already
     * exists, overwrite its value with the new value.
     *
     * @param   key     the keyword by which the request is known
     *                  (e.g., "<code>accept</code>").
     * @param   value   the value associated with it.
     * @throws IllegalStateException if already connected
     * @see #getRequestProperty(java.lang.String)
     */
    @Override
    public void setRequestProperty(String key, String value) {
        super.setRequestProperty(key, value);
        if ("type".equals(key)) {
            if ("i".equalsIgnoreCase(value)) {
                type = BIN;
            } else if ("a".equalsIgnoreCase(value)) {
                type = ASCII;
            } else if ("d".equalsIgnoreCase(value)) {
                type = DIR;
            } else {
                throw new IllegalArgumentException(
                        "Value of '" + key +
                        "' request property was '" + value +
                        "' when it must be either 'i', 'a' or 'd'");
            }
        }
    }

    /**
     * Returns the value of the named general request property for this
     * connection.
     *
     * @param key the keyword by which the request is known (e.g., "accept").
     * @return  the value of the named general request property for this
     *           connection.
     * @throws IllegalStateException if already connected
     * @see #setRequestProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getRequestProperty(String key) {
        String value = super.getRequestProperty(key);

        if (value == null) {
            if ("type".equals(key)) {
                value = (type == ASCII ? "a" : type == DIR ? "d" : "i");
            }
        }

        return value;
    }

    @Override
    public void setConnectTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeouts can't be negative");
        }
        connectTimeout = timeout;
    }

    @Override
    public int getConnectTimeout() {
        return (connectTimeout < 0 ? 0 : connectTimeout);
    }

    @Override
    public void setReadTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeouts can't be negative");
        }
        readTimeout = timeout;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout < 0 ? 0 : readTimeout;
    }
}
