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

 package javax.net.ssl;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

/**
 * The abstract implementation of secure communications using SSL, TLS, or other
 * protocols. It includes the setup, handshake, and encrypt/decrypt functionality
 * needed to create a secure connection.
 * 
 * @since Android 1.0
 */
public abstract class SSLEngine {
    // Store host value
    private final String host;

    // Store port value
    private final int port;

    /**
     * Creates a new {@code SSLEngine} instance.
     * 
     * @since Android 1.0
     */
    protected SSLEngine() {
        host = null;
        port = -1;
    }

    /**
     * Creates a new {@code SSLEngine} instance with the specified host and
     * port.
     * 
     * @param host
     *            the name of the host.
     * @param port
     *            the port of the host.
     * @since Android 1.0
     */
    protected SSLEngine(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Initiates a handshake on this engine.
     * <p>
     * Calling this method is not needed for the initial handshake: it will be
     * called by {@code wrap} or {@code unwrap} if the initial handshake has not
     * been started yet.
     * </p>
     * 
     * @throws SSLException
     *             if starting the handshake fails.
     * @throws IllegalStateException
     *             if the engine does not have all the needed settings (e.g.
     *             client/server mode not set).
     * @since Android 1.0
     */
    public abstract void beginHandshake() throws SSLException;

    /**
     * Notifies this engine instance that no more inbound network data will be
     * sent to this engine.
     * 
     * @throws SSLException
     *             if this engine did not receive a needed protocol specific
     *             close notification message from the peer.
     * @since Android 1.0
     */
    public abstract void closeInbound() throws SSLException;

    /**
     * Notifies this engine instance that no more outbound application data will
     * be sent to this engine.
     * 
     * @since Android 1.0
     */
    public abstract void closeOutbound();

    /**
     * Returns a delegate task for this engine instance. Some engine operations
     * may require the results of blocking or long running operations, and the
     * {@code SSLEngineResult} instances returned by this engine may indicate
     * that a delegated task result is needed. In this case the
     * {@link Runnable#run() run} method of the returned {@code Runnable}
     * delegated task must be called.
     * 
     * @return a delegate task, or {@code null} if none are available.
     * @since Android 1.0
     */
    public abstract Runnable getDelegatedTask();

    /**
     * Returns the SSL cipher suite names that are enabled in this engine
     * instance.
     * 
     * @return the SSL cipher suite names that are enabled in this engine
     *         instance.
     * @since Android 1.0
     */
    public abstract String[] getEnabledCipherSuites();

    /**
     * Returns the protocol version names that are enabled in this engine
     * instance.
     * 
     * @return the protocol version names that are enabled in this engine
     *         instance.
     * @since Android 1.0
     */
    public abstract String[] getEnabledProtocols();

    /**
     * Returns whether new SSL sessions may be established by this engine.
     * 
     * @return {@code true} if new session may be established, {@code false} if
     *         existing sessions must be reused.
     * @since Android 1.0
     */
    public abstract boolean getEnableSessionCreation();

    /**
     * Returns the status of the handshake of this engine instance.
     * 
     * @return the status of the handshake of this engine instance.
     * @since Android 1.0
     */
    public abstract SSLEngineResult.HandshakeStatus getHandshakeStatus();

    /**
     * Returns whether this engine instance will require client authentication.
     * 
     * @return {@code true} if this engine will require client authentication,
     *         {@code false} if no client authentication is needed.
     * @since Android 1.0
     */
    public abstract boolean getNeedClientAuth();

    /**
     * Returns the name of the peer host.
     * 
     * @return the name of the peer host, or {@code null} if none is available.
     * @since Android 1.0
     */
    public String getPeerHost() {
        return host;
    }

    /**
     * Returns the port number of the peer host.
     * 
     * @return the port number of the peer host, or {@code -1} is none is
     *         available.
     * @since Android 1.0
     */
    public int getPeerPort() {
        return port;
    }

    /**
     * Returns the SSL session for this engine instance.
     * 
     * @return the SSL session for this engine instance.
     * @since Android 1.0
     */
    public abstract SSLSession getSession();

    /**
     * Returns the SSL cipher suite names that are supported by this engine.
     * These cipher suites can be enabled using
     * {@link #setEnabledCipherSuites(String[])}.
     * 
     * @return the SSL cipher suite names that are supported by this engine.
     * @since Android 1.0
     */
    public abstract String[] getSupportedCipherSuites();

    /**
     * Returns the protocol names that are supported by this engine. These
     * protocols can be enables using {@link #setEnabledProtocols(String[])}.
     * 
     * @return the protocol names that are supported by this engine.
     * @since Android 1.0
     */
    public abstract String[] getSupportedProtocols();

    /**
     * Returns whether this engine is set to act in client mode when
     * handshaking.
     * 
     * @return {@code true} if the engine is set to do handshaking in client
     *         mode.
     * @since Android 1.0
     */
    public abstract boolean getUseClientMode();

    /**
     * Returns whether this engine will request client authentication.
     * 
     * @return {@code true} if client authentication will be requested,
     *         {@code false} otherwise.
     * @since Android 1.0
     */
    public abstract boolean getWantClientAuth();

    /**
     * Returns whether no more inbound data will be accepted by this engine.
     * 
     * @return {@code true} if no more inbound data will be accepted by this
     *         engine, {@code false} otherwise.
     * @since Android 1.0
     */
    public abstract boolean isInboundDone();

    /**
     * Returns whether no more outbound data will be produced by this engine.
     * 
     * @return {@code true} if no more outbound data will be producted by this
     *         engine, {@code otherwise} false.
     * @since Android 1.0
     */
    public abstract boolean isOutboundDone();

    /**
     * Sets the SSL cipher suite names that should be enabled in this engine
     * instance. Only cipher suites listed by {@code getSupportedCipherSuites()}
     * are allowed.
     * 
     * @param suites
     *            the SSL cipher suite names to be enabled.
     * @throws IllegalArgumentException
     *             if one of the specified cipher suites is not supported, or if
     *             {@code suites} is {@code null}.
     * @since Android 1.0
     */
    public abstract void setEnabledCipherSuites(String[] suites);

    /**
     * Sets the protocol version names that should be enabled in this engine
     * instance. Only protocols listed by {@code getSupportedProtocols()} are
     * allowed.
     * 
     * @param protocols
     *            the protocol version names to be enabled.
     * @throws IllegalArgumentException
     *             if one of the protocol version names is not supported, or if
     *             {@code protocols} is {@code null}.
     * @since Android 1.0
     */
    public abstract void setEnabledProtocols(String[] protocols);

    /**
     * Sets whether new SSL sessions may be established by this engine instance.
     * 
     * @param flag
     *            {@code true} if new SSL sessions may be established,
     *            {@code false} if existing SSL sessions must be reused.
     * @since Android 1.0
     */
    public abstract void setEnableSessionCreation(boolean flag);

    /**
     * Sets whether this engine must require client authentication. The client
     * authentication is one of:
     * <ul>
     * <li>authentication required</li>
     * <li>authentication requested</li>
     * <li>no authentication needed</li>
     * </ul>
     * This method overrides the setting of {@link #setWantClientAuth(boolean)}.
     * 
     * @param need
     *            {@code true} if client authentication is required,
     *            {@code false} if no authentication is needed.
     * @since Android 1.0
     */
    public abstract void setNeedClientAuth(boolean need);

    /**
     * Sets whether this engine should act in client (or server) mode when
     * handshaking.
     * 
     * @param mode
     *            {@code true} if this engine should act in client mode,
     *            {@code false} if not.
     * @throws IllegalArgumentException
     *             if this method is called after starting the initial
     *             handshake.
     * @since Android 1.0
     */
    public abstract void setUseClientMode(boolean mode);

    /**
     * Sets whether this engine should request client authentication. The client
     * authentication is one of the following:
     * <ul>
     * <li>authentication required</li>
     * <li>authentication requested</li>
     * <li>no authentication needed</li>
     * </ul>
     * This method overrides the setting of {@link #setNeedClientAuth(boolean)}.
     * 
     * @param want
     *            {@code true} if client authentication should be requested,
     *            {@code false} if no authentication is needed.
     * @since Android 1.0
     */
    public abstract void setWantClientAuth(boolean want);

    /**
     * Decodes the incoming network data buffer into application data buffers.
     * If a handshake has not been started yet, it will automatically be
     * started.
     * 
     * @param src
     *            the buffer with incoming network data
     * @param dsts
     *            the array of destination buffers for incoming application
     *            data.
     * @param offset
     *            the offset in the array of destination buffers to which data
     *            is to be transferred.
     * @param length
     *            the maximum number of destination buffers to be used. 
     * @return the result object of this operation.
     * @throws SSLException
     *             if a problem occurred while processing the data.
     * @throws IndexOutOfBoundsException
     *             if {@code length} is greater than
     *             {@code dsts.length - offset}.
     * @throws ReadOnlyBufferException
     *             if one of the destination buffers is read-only.
     * @throws IllegalArgumentException
     *             if {@code src}, {@code dsts}, or one of the entries in
     *             {@code dsts} is {@code null}.
     * @throws IllegalStateException
     *             if the engine does not have all the needed settings (e.g.
     *             client/server mode not set).
     * @since Android 1.0
     */
    public abstract SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts,
            int offset, int length) throws SSLException;

    /**
     * Encodes the outgoing application data buffers into the network data
     * buffer. If a handshake has not been started yet, it will automatically be
     * started.
     * 
     * @param srcs
     *            the array of source buffers of outgoing application data.
     * @param offset
     *            the offset in the array of source buffers from which data is
     *            to be retrieved.
     * @param length
     *            the maximum number of source buffers to be used.
     * @param dst
     *            the destination buffer for network data.
     * @return the result object of this operation.
     * @throws SSLException
     *             if a problem occurred while processing the data.
     * @throws IndexOutOfBoundsException
     *             if {@code length} is greater than
     *             {@code srcs.length - offset}.
     * @throws ReadOnlyBufferException
     *             if the destination buffer is readonly.
     * @throws IllegalArgumentException
     *             if {@code srcs}, {@code dst}, or one the entries in
     *             {@code srcs} is {@code null}.
     * @throws IllegalStateException
     *             if the engine does not have all the needed settings (e.g.
     *             client/server mode not set).
     * @since Android 1.0
     */
    public abstract SSLEngineResult wrap(ByteBuffer[] srcs, int offset,
            int length, ByteBuffer dst) throws SSLException;

    /**
     * Decodes the incoming network data buffer into the application data
     * buffer. If a handshake has not been started yet, it will automatically be
     * started.
     * 
     * @param src
     *            the buffer with incoming network data
     * @param dst
     *            the destination buffer for incoming application data.
     * @return the result object of this operation.
     * @throws SSLException
     *             if a problem occurred while processing the data.
     * @throws ReadOnlyBufferException
     *             if one of the destination buffers is read-only.
     * @throws IllegalArgumentException
     *             if {@code src} or {@code dst} is {@code null}.
     * @throws IllegalStateException
     *             if the engine does not have all the needed settings (e.g.
     *             client/server mode not set).
     * @since Android 1.0
     */
    public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer dst)
            throws SSLException {
//        if (src == null) {
//            throw new IllegalArgumentException("Byte buffer src is null");
//        }
//        if (dst == null) {
//            throw new IllegalArgumentException("Byte buffer dst is null");
//        }
//        if (dst.isReadOnly()) {
//            throw new ReadOnlyBufferException();
//        }
        return unwrap(src, new ByteBuffer[] { dst }, 0, 1);
    }

    /**
     * Decodes the incoming network data buffer into the application data
     * buffers. If a handshake has not been started yet, it will automatically
     * be started.
     * 
     * @param src
     *            the buffer with incoming network data
     * @param dsts
     *            the array of destination buffers for incoming application
     *            data.
     * @return the result object of this operation.
     * @throws SSLException
     *             if a problem occurred while processing the data.
     * @throws ReadOnlyBufferException
     *             if one of the destination buffers is read-only.
     * @throws IllegalArgumentException
     *             if {@code src} or {@code dsts} is {@code null}.
     * @throws IllegalStateException
     *             if the engine does not have all the needed settings (e.g.
     *             client/server mode not set).
     * @since Android 1.0
     */
    public SSLEngineResult unwrap(ByteBuffer src, ByteBuffer[] dsts)
            throws SSLException {
//        if (src == null) {
//            throw new IllegalArgumentException("Byte buffer src is null");
//        }
        if (dsts == null) {
            throw new IllegalArgumentException("Byte buffer array dsts is null");
        }
//        for (int i = 0; i < dsts.length; i++) {
//            if (dsts[i] == null) {
//                throw new IllegalArgumentException("Byte buffer dsts[" + i
//                        + "]  is null");
//            }
//            if (dsts[i].isReadOnly()) {
//                throw new ReadOnlyBufferException();
//            }
//        }
        return unwrap(src, dsts, 0, dsts.length);
    }

    /**
     * Encodes the outgoing application data buffers into the network data
     * buffer. If a handshake has not been started yet, it will automatically be
     * started.
     * 
     * @param srcs
     *            the array of source buffers of outgoing application data.
     * @param dst
     *            the destination buffer for network data.
     * @return the result object of this operation.
     * @throws SSLException
     *             if a problem occurred while processing the data.
     * @throws ReadOnlyBufferException
     *             if the destination buffer is readonly.
     * @throws IllegalArgumentException
     *             if {@code srcs} or {@code dst} is {@code null}.
     * @throws IllegalStateException
     *             if the engine does not have all the needed settings (e.g.
     *             client/server mode not set).
     * @since Android 1.0
     */
    public SSLEngineResult wrap(ByteBuffer[] srcs, ByteBuffer dst)
            throws SSLException {
        if (srcs == null) {
            throw new IllegalArgumentException("Byte buffer array srcs is null");
        }
//        for (int i = 0; i < srcs.length; i++) {
//            if (srcs[i] == null) {
//                throw new IllegalArgumentException("Byte buffer srcs[" + i
//                        + "]  is null");
//            }
//        }
//        if (dst == null) {
//            throw new IllegalArgumentException("Byte buffer array dst is null");
//        }
//        if (dst.isReadOnly()) {
//            throw new ReadOnlyBufferException();
//        }
        return wrap(srcs, 0, srcs.length, dst);
    }

    /**
     * Encodes the outgoing application data buffer into the network data
     * buffer. If a handshake has not been started yet, it will automatically be
     * started.
     * 
     * @param src
     *            the source buffers of outgoing application data.
     * @param dst
     *            the destination buffer for network data.
     * @return the result object of this operation.
     * @throws SSLException
     *             if a problem occurred while processing the data.
     * @throws ReadOnlyBufferException
     *             if the destination buffer is readonly.
     * @throws IllegalArgumentException
     *             if {@code src} or {@code dst} is {@code null}.
     * @throws IllegalStateException
     *             if the engine does not have all the needed settings (e.g.
     *             client/server mode not set).
     * @since Android 1.0
     */
    public SSLEngineResult wrap(ByteBuffer src, ByteBuffer dst)
            throws SSLException {
//        if (src == null) {
//            throw new IllegalArgumentException("Byte buffer src is null");
//        }
//        if (dst == null) {
//            throw new IllegalArgumentException("Byte buffer dst is null");
//        }
//        if (dst.isReadOnly()) {
//            throw new ReadOnlyBufferException();
//        }
        return wrap(new ByteBuffer[] { src }, 0, 1, dst);
    }
}
