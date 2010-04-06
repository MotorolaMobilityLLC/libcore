/*
 * Copyright (C) 2007 The Android Open Source Project
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

package org.apache.harmony.xnet.provider.jsse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;

import org.apache.harmony.security.provider.cert.X509CertImpl;

/**
 * Implementation of the class OpenSSLSocketImpl
 * based on OpenSSL. The JNI native interface for some methods
 * of this this class are defined in the file:
 * org_apache_harmony_xnet_provider_jsse_NativeCrypto.cpp
 *
 * This class only supports SSLv3 and TLSv1. This should be documented elsewhere
 * later, for example in the package.html or a separate reference document.
 */
public class OpenSSLSocketImpl
        extends javax.net.ssl.SSLSocket
        implements NativeCrypto.CertificateChainVerifier {
    private int sslNativePointer;
    private InputStream is;
    private OutputStream os;
    private final Object handshakeLock = new Object();
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    private SSLParameters sslParameters;
    private OpenSSLSessionImpl sslSession;
    private Socket socket;
    private boolean autoClose;
    private boolean handshakeStarted = false;
    private ArrayList<HandshakeCompletedListener> listeners;
    private int timeout = 0;
    // BEGIN android-added
    private int handshakeTimeout = -1;  // -1 = same as timeout; 0 = infinite
    // END android-added
    private InetSocketAddress address;

    private static final AtomicInteger instanceCount = new AtomicInteger(0);

    public static int getInstanceCount() {
        return instanceCount.get();
    }

    private static void updateInstanceCount(int amount) {
        instanceCount.addAndGet(amount);
    }

    /**
     * Class constructor with 1 parameter
     *
     * @param sslParameters Parameters for the SSL
     *            context
     * @throws IOException if network fails
     */
    protected OpenSSLSocketImpl(SSLParameters sslParameters) throws IOException {
        super();
        init(sslParameters);
    }

    /**
     * Class constructor with 3 parameters
     *
     * @throws IOException if network fails
     * @throws java.net.UnknownHostException host not defined
     */
    protected OpenSSLSocketImpl(String host, int port,
            SSLParameters sslParameters)
        throws IOException {
        super(host, port);
        init(sslParameters);
    }

    /**
     * Class constructor with 3 parameters: 1st is InetAddress
     *
     * @throws IOException if network fails
     * @throws java.net.UnknownHostException host not defined
     */
    protected OpenSSLSocketImpl(InetAddress address, int port,
            SSLParameters sslParameters)
        throws IOException {
        super(address, port);
        init(sslParameters);
    }


    /**
     * Class constructor with 5 parameters: 1st is host
     *
     * @throws IOException if network fails
     * @throws java.net.UnknownHostException host not defined
     */
    protected OpenSSLSocketImpl(String host, int port, InetAddress clientAddress,
            int clientPort, SSLParameters sslParameters)
        throws IOException {
        super(host, port, clientAddress, clientPort);
        init(sslParameters);
    }

    /**
     * Class constructor with 5 parameters: 1st is InetAddress
     *
     * @throws IOException if network fails
     * @throws java.net.UnknownHostException host not defined
     */
    protected OpenSSLSocketImpl(InetAddress address, int port,
            InetAddress clientAddress, int clientPort, SSLParameters sslParameters)
        throws IOException {
        super(address, port, clientAddress, clientPort);
        init(sslParameters);
    }

    /**
     * Constructor with 5 parameters: 1st is socket. Enhances an existing socket
     * with SSL functionality.
     *
     * @throws IOException if network fails
     */
    protected OpenSSLSocketImpl(Socket socket, String host, int port,
            boolean autoClose, SSLParameters sslParameters) throws IOException {
        super();
        this.socket = socket;
        this.timeout = socket.getSoTimeout();
        this.address = new InetSocketAddress(host, port);
        this.autoClose = autoClose;
        init(sslParameters);
    }

    /**
     * Initialize the SSL socket and set the certificates for the
     * future handshaking.
     */
    private void init(SSLParameters sslParameters) throws IOException {
        this.sslParameters = sslParameters;
        this.sslNativePointer = NativeCrypto.SSL_new(sslParameters);
        updateInstanceCount(1);
    }

    /**
     * Construct a OpenSSLSocketImpl from an SSLParameters and an
     * existing SSL native pointer. Used for transitioning accepting
     * the OpenSSLSocketImpl within OpenSSLServerSocketImpl.
     */
    protected OpenSSLSocketImpl(SSLParameters sslParameters,
                                OpenSSLServerSocketImpl dummy) {
        super();
        this.sslParameters = (SSLParameters) sslParameters.clone();
        updateInstanceCount(1);
    }

    /**
     * Adds OpenSSL functionality to the existing socket and starts the SSL
     * handshaking.
     */
    private native boolean nativeconnect(int sslNativePointer, Socket sock, int timeout, boolean client_mode, int sslSessionNativePointer) throws IOException;
    private native int nativegetsslsession(int sslNativePointer);
    private native String nativecipherauthenticationmethod(int sslNativePointer);

    /**
     * Gets the suitable session reference from the session cache container.
     *
     * @return OpenSSLSessionImpl
     */
    private OpenSSLSessionImpl getCachedClientSession() {
        if (!sslParameters.getUseClientMode()) {
            return null;
        }
        if (super.getInetAddress() == null ||
                super.getInetAddress().getHostAddress() == null ||
                super.getInetAddress().getHostName() == null) {
            return null;
        }
        ClientSessionContext sessionContext
                = sslParameters.getClientSessionContext();
        return (OpenSSLSessionImpl) sessionContext.getSession(
                super.getInetAddress().getHostName(),
                super.getPort());
    }

    /**
     * Ensures that logger is lazily loaded. The outer class seems to load
     * before logging is ready.
     */
    static class LoggerHolder {
        static final Logger logger = Logger.getLogger(OpenSSLSocketImpl.class.getName());
    }

    /**
     * Starts a TLS/SSL handshake on this connection using some native methods
     * from the OpenSSL library. It can negotiate new encryption keys, change
     * cipher suites, or initiate a new session. The certificate chain is
     * verified if the correspondent property in java.Security is set. All
     * listeners are notified at the end of the TLS/SSL handshake.
     *
     * @throws <code>IOException</code> if network fails
     */
    public synchronized void startHandshake() throws IOException {
        synchronized (handshakeLock) {
            if (!handshakeStarted) {
                handshakeStarted = true;
            } else {
                return;
            }
        }

        OpenSSLSessionImpl session = getCachedClientSession();

        // Check if it's allowed to create a new session (default is true)
        if (session == null && !sslParameters.getEnableSessionCreation()) {
            throw new SSLHandshakeException("SSL Session may not be created");
        }

        // BEGIN android-added
        // Temporarily use a different timeout for the handshake process
        int savedTimeout = timeout;
        if (handshakeTimeout >= 0) {
            setSoTimeout(handshakeTimeout);
        }
        // END android-added

        Socket socket = this.socket != null ? this.socket : this;
        int sslSessionNativePointer = session != null ? session.sslSessionNativePointer : 0;
        boolean reusedSession = nativeconnect(sslNativePointer, socket, timeout,
                                              sslParameters.getUseClientMode(), sslSessionNativePointer);
        if (reusedSession) {
            // nativeconnect shouldn't return true if the session is not
            // done
            session.lastAccessedTime = System.currentTimeMillis();
            sslSession = session;

            LoggerHolder.logger.fine("Reused cached session for "
                                     + getInetAddress().getHostName() + ".");
        } else {
            if (session != null) {
                LoggerHolder.logger.fine("Reuse of cached session for "
                                         + getInetAddress().getHostName() + " failed.");
            } else {
                LoggerHolder.logger.fine("Created new session for "
                                         + getInetAddress().getHostName() + ".");
            }

            AbstractSessionContext sessionContext =
                (sslParameters.getUseClientMode()) ?
                sslParameters.getClientSessionContext() :
                sslParameters.getServerSessionContext();
            sslSessionNativePointer = nativegetsslsession(sslNativePointer);
            if (address == null) {
                sslSession = new OpenSSLSessionImpl(sslSessionNativePointer, sslParameters,
                                                    super.getInetAddress().getHostName(),
                                                    super.getPort(), sessionContext);
            } else  {
                sslSession = new OpenSSLSessionImpl(sslSessionNativePointer, sslParameters,
                                                    address.getHostName(), address.getPort(),
                                                    sessionContext);
            }

            try {
                X509Certificate[] peerCertificates = (X509Certificate[])
                    sslSession.getPeerCertificates();

                if (peerCertificates == null
                    || peerCertificates.length == 0) {
                    throw new SSLException("Server sends no certificate");
                }

                String authMethod = nativecipherauthenticationmethod(sslNativePointer);
                sslParameters.getTrustManager().checkServerTrusted(peerCertificates,
                                                                   authMethod);
                sessionContext.putSession(sslSession);
            } catch (CertificateException e) {
                throw new SSLException("Not trusted server certificate", e);
            }
        }

        // BEGIN android-added
        // Restore the original timeout now that the handshake is complete
        if (handshakeTimeout >= 0) {
            setSoTimeout(savedTimeout);
        }
        // END android-added

        if (listeners != null) {
            // notify the listeners
            HandshakeCompletedEvent event =
                new HandshakeCompletedEvent(this, sslSession);
            int size = listeners.size();
            for (int i = 0; i < size; i++) {
                listeners.get(i).handshakeCompleted(event);
            }
        }
    }

    // To be synchronized because of the verify_callback
    native synchronized int nativeaccept(int sslNativePointer, Socket socketObject);

    /**
     * Performs the first part of a SSL/TLS handshaking process with a given
     * 'host' connection and initializes the SSLSession.
     */
    protected void accept(int serverSslNativePointer) throws IOException {
        // Must be set because no handshaking is necessary
        // in this situation
        handshakeStarted = true;

        sslNativePointer = nativeaccept(serverSslNativePointer, this);

        ServerSessionContext sessionContext
                = sslParameters.getServerSessionContext();
        sslSession = new OpenSSLSessionImpl(nativegetsslsession(sslNativePointer),
                sslParameters, super.getInetAddress().getHostName(),
                super.getPort(), sessionContext);
        sslSession.lastAccessedTime = System.currentTimeMillis();
        sessionContext.putSession(sslSession);
    }

    /**
     * Implementation of NativeCrypto.CertificateChainVerifier.
     *
     * Callback method for the OpenSSL native certificate verification process.
     *
     * @param bytes Byte array containing the cert's
     *            information.
     * @return false if the certificate verification fails or true if OK
     */
    @SuppressWarnings("unused")
    public boolean verifyCertificateChain(byte[][] bytes) {
        try {
            X509Certificate[] peerCertificateChain
                    = new X509Certificate[bytes.length];
            for(int i = 0; i < bytes.length; i++) {
                peerCertificateChain[i] =
                    new X509CertImpl(javax.security.cert.X509Certificate.getInstance(bytes[i]).getEncoded());
            }

            try {
                // TODO "null" String
                sslParameters.getTrustManager().checkClientTrusted(peerCertificateChain, "null");
            } catch (CertificateException e) {
                throw new AlertException(AlertProtocol.BAD_CERTIFICATE,
                        new SSLException("Not trusted server certificate", e));
            }
        } catch (javax.security.cert.CertificateException e) {
            // TODO throw in all cases for consistency
            return false;
        } catch (IOException e) {
            // TODO throw in all cases for consistency
            return false;
        }
        return true;
    }

    /**
     * Returns an input stream for this SSL socket using native calls to the
     * OpenSSL library.
     *
     * @return: an input stream for reading bytes from this socket.
     * @throws: <code>IOException</code> if an I/O error occurs when creating
     *          the input stream, the socket is closed, the socket is not
     *          connected, or the socket input has been shutdown.
     */
    public InputStream getInputStream() throws IOException {
        synchronized(this) {
            if (is == null) {
                is = new SSLInputStream();
            }

            return is;
        }
    }

    /**
     * Returns an output stream for this SSL socket using native calls to the
     * OpenSSL library.
     *
     * @return an output stream for writing bytes to this socket.
     * @throws <code>IOException</code> if an I/O error occurs when creating
     *             the output stream, or no connection to the socket exists.
     */
    public OutputStream getOutputStream() throws IOException {
        synchronized(this) {
            if (os == null) {
                os = new SSLOutputStream();
            }

            return os;
        }
    }

    public void shutdownInput() throws IOException {
        if (socket == null) {
            super.shutdownInput();
            return;
        }
        socket.shutdownInput();
    }

    public void shutdownOutput() throws IOException {
        if (socket == null) {
            super.shutdownOutput();
            return;
        }
        socket.shutdownOutput();
    }

    /**
     * Reads with the native SSL_read function from the encrypted data stream
     * @return -1 if error or the end of the stream is reached.
     */
    private native int nativeread(int sslNativePointer, int timeout) throws IOException;
    private native int nativeread(int sslNativePointer, byte[] b, int off, int len, int timeout) throws IOException;

    /**
     * This inner class provides input data stream functionality
     * for the OpenSSL native implementation. It is used to
     * read data received via SSL protocol.
     */
    private class SSLInputStream extends InputStream {
        SSLInputStream() throws IOException {
            /**
            /* Note: When startHandshake() throws an exception, no
             * SSLInputStream object will be created.
             */
            OpenSSLSocketImpl.this.startHandshake();
        }

        /**
         * Reads one byte. If there is no data in the underlying buffer,
         * this operation can block until the data will be
         * available.
         * @return read value.
         * @throws <code>IOException</code>
         */
        public int read() throws IOException {
            synchronized(readLock) {
                return OpenSSLSocketImpl.this.nativeread(sslNativePointer, timeout);
            }
        }

        /**
         * Method acts as described in spec for superclass.
         * @see java.io.InputStream#read(byte[],int,int)
         */
        public int read(byte[] b, int off, int len) throws IOException {
            synchronized(readLock) {
                return OpenSSLSocketImpl.this.nativeread(sslNativePointer, b, off, len, timeout);
            }
        }
    }

    /**
     * Writes with the native SSL_write function to the encrypted data stream.
     */
    private native void nativewrite(int sslNativePointer, int b) throws IOException;
    private native void nativewrite(int sslNativePointer, byte[] b, int off, int len) throws IOException;

    /**
     * This inner class provides output data stream functionality
     * for the OpenSSL native implementation. It is used to
     * write data according to the encryption parameters given in SSL context.
     */
    private class SSLOutputStream extends OutputStream {
        SSLOutputStream() throws IOException {
            /**
            /* Note: When startHandshake() throws an exception, no
             * SSLOutputStream object will be created.
             */
            OpenSSLSocketImpl.this.startHandshake();
        }

        /**
         * Method acts as described in spec for superclass.
         * @see java.io.OutputStream#write(int)
         */
        public void write(int b) throws IOException {
            synchronized(writeLock) {
                OpenSSLSocketImpl.this.nativewrite(sslNativePointer, b);
            }
        }

        /**
         * Method acts as described in spec for superclass.
         * @see java.io.OutputStream#write(byte[],int,int)
         */
        public void write(byte[] b, int start, int len) throws IOException {
            synchronized(writeLock) {
                OpenSSLSocketImpl.this.nativewrite(sslNativePointer, b, start, len);
            }
        }
    }


    /**
     * The SSL session used by this connection is returned. The SSL session
     * determines which cipher suite should be used by all connections within
     * that session and which identities have the session's client and server.
     * This method starts the SSL handshake.
     * @return the SSLSession.
     * @throws <code>IOException</code> if the handshake fails
     */
    public SSLSession getSession() {
        try {
            startHandshake();
        } catch (IOException e) {
            // return an invalid session with
            // invalid cipher suite of "SSL_NULL_WITH_NULL_NULL"
            return SSLSessionImpl.NULL_SESSION;
        }
        return sslSession;
    }

    /**
     * Registers a listener to be notified that a SSL handshake
     * was successfully completed on this connection.
     * @throws <code>IllegalArgumentException</code> if listener is null.
     */
    public void addHandshakeCompletedListener(
            HandshakeCompletedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Provided listener is null");
        }
        if (listeners == null) {
            listeners = new ArrayList();
        }
        listeners.add(listener);
    }

    /**
     * The method removes a registered listener.
     * @throws IllegalArgumentException if listener is null or not registered
     */
    public void removeHandshakeCompletedListener(
            HandshakeCompletedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Provided listener is null");
        }
        if (listeners == null) {
            throw new IllegalArgumentException(
                    "Provided listener is not registered");
        }
        if (!listeners.remove(listener)) {
            throw new IllegalArgumentException(
                    "Provided listener is not registered");
        }
    }

    /**
     * Returns true if new SSL sessions may be established by this socket.
     *
     * @return true if the session may be created; false if a session already
     *         exists and must be resumed.
     */
    public boolean getEnableSessionCreation() {
        return sslParameters.getEnableSessionCreation();
    }

    /**
     * Set a flag for the socket to inhibit or to allow the creation of a new
     * SSL sessions. If the flag is set to false, and there are no actual
     * sessions to resume, then there will be no successful handshaking.
     *
     * @param flag true if session may be created; false
     *            if a session already exists and must be resumed.
     */
    public void setEnableSessionCreation(boolean flag) {
        sslParameters.setEnableSessionCreation(flag);
    }

    /**
     * The names of the cipher suites which could be used by the SSL connection
     * are returned.
     * @return an array of cipher suite names
     */
    public String[] getSupportedCipherSuites() {
        return NativeCrypto.getSupportedCipherSuites();
    }

    /**
     * The names of the cipher suites that are in use in the actual the SSL
     * connection are returned.
     *
     * @return an array of cipher suite names
     */
    public String[] getEnabledCipherSuites() {
        return NativeCrypto.SSL_get_ciphers(sslNativePointer);
    }

    /**
     * This method enables the cipher suites listed by
     * getSupportedCipherSuites().
     *
     * @param suites names of all the cipher suites to
     *            put on use
     * @throws IllegalArgumentException when one or more of the
     *             ciphers in array suites are not supported, or when the array
     *             is null.
     */
    public void setEnabledCipherSuites(String[] suites) {
        NativeCrypto.setEnabledCipherSuites(sslNativePointer, suites);
    }

    /**
     * The names of the protocols' versions that may be used on this SSL
     * connection.
     * @return an array of protocols names
     */
    public String[] getSupportedProtocols() {
        return NativeCrypto.getSupportedProtocols();
    }

    /**
     * The names of the protocols' versions that are in use on this SSL
     * connection.
     *
     * @return an array of protocols names
     */
    @Override
    public String[] getEnabledProtocols() {
        return NativeCrypto.getEnabledProtocols(sslNativePointer);
    }

    /**
     * This method enables the protocols' versions listed by
     * getSupportedProtocols().
     *
     * @param protocols The names of all the protocols to put on use
     *
     * @throws IllegalArgumentException when one or more of the names in the
     *             array are not supported, or when the array is null.
     */
    @Override
    public synchronized void setEnabledProtocols(String[] protocols) {
        NativeCrypto.setEnabledProtocols(sslNativePointer, protocols);
    }

    /**
     * This method gives true back if the SSL socket is set to client mode.
     *
     * @return true if the socket should do the handshaking as client.
     */
    public boolean getUseClientMode() {
        return sslParameters.getUseClientMode();
    }

    /**
     * This method set the actual SSL socket to client mode.
     *
     * @param mode true if the socket starts in client
     *            mode
     * @throws IllegalArgumentException if mode changes during
     *             handshake.
     */
    public synchronized void setUseClientMode(boolean mode) {
        if (handshakeStarted) {
            throw new IllegalArgumentException(
            "Could not change the mode after the initial handshake has begun.");
        }
        sslParameters.setUseClientMode(mode);
    }

    /**
     * Returns true if the SSL socket requests client's authentication. Relevant
     * only for server sockets!
     *
     * @return true if client authentication is desired, false if not.
     */
    public boolean getWantClientAuth() {
        return sslParameters.getWantClientAuth();
    }

    /**
     * Returns true if the SSL socket needs client's authentication. Relevant
     * only for server sockets!
     *
     * @return true if client authentication is desired, false if not.
     */
    public boolean getNeedClientAuth() {
        return sslParameters.getNeedClientAuth();
    }

    /**
     * Sets the SSL socket to use client's authentication. Relevant only for
     * server sockets!
     *
     * @param need true if client authentication is
     *            desired, false if not.
     */
    public void setNeedClientAuth(boolean need) {
        sslParameters.setNeedClientAuth(need);
    }

    /**
     * Sets the SSL socket to use client's authentication. Relevant only for
     * server sockets! Notice that in contrast to setNeedClientAuth(..) this
     * method will continue the negotiation if the client decide not to send
     * authentication credentials.
     *
     * @param want true if client authentication is
     *            desired, false if not.
     */
    public void setWantClientAuth(boolean want) {
        sslParameters.setWantClientAuth(want);
    }

    /**
     * This method is not supported for SSLSocket implementation.
     */
    public void sendUrgentData(int data) throws IOException {
        throw new SocketException(
                "Method sendUrgentData() is not supported.");
    }

    /**
     * This method is not supported for SSLSocket implementation.
     */
    public void setOOBInline(boolean on) throws SocketException {
        throw new SocketException(
                "Methods sendUrgentData, setOOBInline are not supported.");
    }

    /**
     * Set the read timeout on this socket. The SO_TIMEOUT option, is specified
     * in milliseconds. The read operation will block indefinitely for a zero
     * value.
     *
     * @param timeout the read timeout value
     * @throws SocketException if an error occurs setting the option
     */
    public synchronized void setSoTimeout(int timeout) throws SocketException {
        super.setSoTimeout(timeout);
        this.timeout = timeout;
    }

    // BEGIN android-added
    /**
     * Set the handshake timeout on this socket.  This timeout is specified in
     * milliseconds and will be used only during the handshake process.
     *
     * @param timeout the handshake timeout value
     */
    public synchronized void setHandshakeTimeout(int timeout) throws SocketException {
        this.handshakeTimeout = timeout;
    }
    // END android-added

    private native void nativeinterrupt(int sslNativePointer) throws IOException;
    private native void nativeclose(int sslNativePointer) throws IOException;

    /**
     * Closes the SSL socket. Once closed, a socket is not available for further
     * use anymore under any circumstance. A new socket must be created.
     *
     * @throws <code>IOException</code> if an I/O error happens during the
     *             socket's closure.
     */
    public void close() throws IOException {
        // TODO: Close SSL sockets using a background thread so they close
        // gracefully.

        synchronized (handshakeLock) {
            if (!handshakeStarted) {
                handshakeStarted = true;

                synchronized (this) {
                    free();

                    if (socket != null) {
                        if (autoClose && !socket.isClosed()) socket.close();
                    } else {
                        if (!super.isClosed()) super.close();
                    }
                }

                return;
            }
        }

        nativeinterrupt(sslNativePointer);

        synchronized (this) {
            synchronized (writeLock) {
                synchronized (readLock) {

                    IOException pendingException = null;

                    // Shut down the SSL connection, per se.
                    try {
                        if (handshakeStarted) {
                            nativeclose(sslNativePointer);
                        }
                    } catch (IOException ex) {
                        /*
                         * Note the exception at this point, but try to continue
                         * to clean the rest of this all up before rethrowing.
                         */
                        pendingException = ex;
                    }

                    /*
                     * Even if the above call failed, it is still safe to free
                     * the native structs, and we need to do so lest we leak
                     * memory.
                     */
                    free();

                    if (socket != null) {
                        if (autoClose && !socket.isClosed())
                            socket.close();
                    } else {
                        if (!super.isClosed())
                            super.close();
                    }

                    if (pendingException != null) {
                        throw pendingException;
                    }
                }
            }
        }
    }

    private void free() {
        if (sslNativePointer == 0) {
            return;
        }
        NativeCrypto.SSL_free(sslNativePointer);
        sslNativePointer = 0;
    }

    protected void finalize() throws IOException {
        updateInstanceCount(-1);

        if (sslNativePointer == 0) {
            /*
             * It's already been closed, so there's no need to do anything
             * more at this point.
             */
            return;
        }

        // Note the underlying socket up-front, for possible later use.
        Socket underlyingSocket = socket;

        // Fire up a thread to (hopefully) do all the real work.
        Finalizer f = new Finalizer();
        f.setDaemon(true);
        f.start();

        /*
         * Give the finalizer thread one second to run. If it fails to
         * terminate in that time, interrupt it (which may help if it
         * is blocked on an interruptible I/O operation), make a note
         * in the log, and go ahead and close the underlying socket if
         * possible.
         */
        try {
            f.join(1000);
        } catch (InterruptedException ex) {
            // Reassert interrupted status.
            Thread.currentThread().interrupt();
        }

        if (f.isAlive()) {
            f.interrupt();
            Logger.global.log(Level.SEVERE,
                    "Slow finalization of SSL socket (" + this + ", for " +
                    underlyingSocket + ")");
            if ((underlyingSocket != null) && !underlyingSocket.isClosed()) {
                underlyingSocket.close();
            }
        }
    }

    /**
     * Helper class for a thread that knows how to call
     * {@link OpenSSLSocketImpl#close} on behalf of instances being finalized,
     * since that call can take arbitrarily long (e.g., due to a slow network),
     * and an overly long-running finalizer will cause the process to be
     * totally aborted.
     */
    private class Finalizer extends Thread {
        public void run() {
            Socket underlyingSocket = socket; // for error reporting
            try {
                close();
            } catch (IOException ex) {
                /*
                 * Clear interrupted status, so that the Logger call
                 * immediately below won't get spuriously interrupted.
                 */
                Thread.interrupted();

                Logger.global.log(Level.SEVERE,
                        "Trouble finalizing SSL socket (" +
                        OpenSSLSocketImpl.this + ", for " + underlyingSocket +
                        ")",
                        ex);
            }
        }
    }

    /**
     * Verifies an RSA signature. Conceptually, this method doesn't really
     * belong here, but due to its native code being closely tied to OpenSSL
     * (just like the rest of this class), we put it here for the time being.
     * This also solves potential problems with native library initialization.
     *
     * @param message The message to verify
     * @param signature The signature to verify
     * @param algorithm The hash/sign algorithm to use, i.e. "RSA-SHA1"
     * @param key The RSA public key to use
     * @return true if the verification succeeds, false otherwise
     */
    public static boolean verifySignature(byte[] message, byte[] signature, String algorithm, RSAPublicKey key) {
        byte[] modulus = key.getModulus().toByteArray();
        byte[] exponent = key.getPublicExponent().toByteArray();

        return nativeverifysignature(message, signature, algorithm, modulus, exponent) == 1;
    }

    private static native int nativeverifysignature(byte[] message, byte[] signature,
            String algorithm, byte[] modulus, byte[] exponent);
}
