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

package org.apache.harmony.luni.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.net.SocketOptions;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.harmony.luni.platform.INetworkSystem;
import org.apache.harmony.luni.platform.Platform;

/**
 * A concrete connected-socket implementation.
 */
public class PlainSocketImpl extends SocketImpl {

    // For SOCKS support. A SOCKS bind() uses the last
    // host connected to in its request.
    static private InetAddress lastConnectedAddress;

    static private int lastConnectedPort;

    private static Field fdField;

    /**
     * used to store the trafficClass value which is simply returned as the
     * value that was set. We also need it to pass it to methods that specify an
     * address packets are going to be sent to
     */
    private int trafficClass;

    protected INetworkSystem netImpl = Platform.getNetworkSystem();

    public int receiveTimeout = 0;

    public boolean streaming = true;

    public boolean shutdownInput;

    Proxy proxy;

    public PlainSocketImpl(FileDescriptor fd) {
        this.fd = fd;
    }

    public PlainSocketImpl(Proxy proxy) {
        this(new FileDescriptor());
        this.proxy = proxy;
    }

    public PlainSocketImpl() {
        this(new FileDescriptor());
    }

    public PlainSocketImpl(FileDescriptor fd, int localport, InetAddress addr, int port) {
        super();
        this.fd = fd;
        this.localport = localport;
        this.address = addr;
        this.port = port;
    }

    @Override
    protected void accept(SocketImpl newImpl) throws IOException {
        if (NetUtil.usingSocks(proxy)) {
            ((PlainSocketImpl) newImpl).socksBind();
            ((PlainSocketImpl) newImpl).socksAccept();
            return;
        }

        try {
            if (newImpl instanceof PlainSocketImpl) {
                PlainSocketImpl newPlainSocketImpl = (PlainSocketImpl) newImpl;
                netImpl.accept(fd, newImpl, newPlainSocketImpl.getFileDescriptor(), receiveTimeout);
            } else {
                // if newImpl is not an instance of PlainSocketImpl, use
                // reflection to get/set protected fields.
                if (fdField == null) {
                    fdField = getSocketImplField("fd");
                }
                FileDescriptor newFd = (FileDescriptor) fdField.get(newImpl);
                netImpl.accept(fd, newImpl, newFd, receiveTimeout);
            }
        } catch (InterruptedIOException e) {
            throw new SocketTimeoutException(e.getMessage());
        } catch (IllegalAccessException e) {
            // empty
        }
    }

    /**
     * gets SocketImpl field by reflection.
     */
    private Field getSocketImplField(final String fieldName) {
        return AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
                Field field = null;
                try {
                    field = SocketImpl.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    throw new Error(e);
                }
                return field;
            }
        });
    }

    public void initLocalPort(int localPort) {
        this.localport = localPort;
    }

    public void initRemoteAddressAndPort(InetAddress remoteAddress, int remotePort) {
        this.address = remoteAddress;
        this.port = remotePort;
    }

    @Override
    protected synchronized int available() throws IOException {
        // we need to check if the input has been shutdown. If so
        // we should return that there is no data to be read
        if (shutdownInput) {
            return 0;
        }
        return Platform.getFileSystem().ioctlAvailable(fd);
    }

    @Override
    protected void bind(InetAddress address, int port) throws IOException {
        netImpl.bind(fd, address, port);
        this.address = address;
        if (port != 0) {
            this.localport = port;
        } else {
            this.localport = netImpl.getSocketLocalPort(fd);
        }
    }

    @Override
    protected void close() throws IOException {
        synchronized (fd) {
            if (fd.valid()) {
                netImpl.close(fd);
                fd = new FileDescriptor();
            }
        }
    }

    @Override
    protected void connect(String aHost, int aPort) throws IOException {
        connect(InetAddress.getByName(aHost), aPort);
    }

    @Override
    protected void connect(InetAddress anAddr, int aPort) throws IOException {
        connect(anAddr, aPort, 0);
    }

    /**
     * Connects this socket to the specified remote host address/port.
     *
     * @param anAddr
     *            the remote host address to connect to
     * @param aPort
     *            the remote port to connect to
     * @param timeout
     *            a timeout where supported. 0 means no timeout
     * @throws IOException
     *             if an error occurs while connecting
     */
    private void connect(InetAddress anAddr, int aPort, int timeout) throws IOException {
        InetAddress normalAddr = anAddr.isAnyLocalAddress() ? InetAddress.getLocalHost() : anAddr;
        try {
            if (streaming) {
                if (NetUtil.usingSocks(proxy)) {
                    socksConnect(anAddr, aPort, 0);
                } else {
                    if (timeout == 0) {
                        netImpl.connect(fd, trafficClass, normalAddr, aPort);
                    } else {
                        netImpl.connectStreamWithTimeoutSocket(fd, aPort,
                                timeout, trafficClass, normalAddr);
                    }
                }
            } else {
                netImpl.connectDatagram(fd, aPort, trafficClass, normalAddr);
            }
        } catch (ConnectException e) {
            throw new ConnectException(anAddr + ":" + aPort + " - " + e.getMessage());
        }
        super.address = normalAddr;
        super.port = aPort;
    }

    @Override
    protected void create(boolean streaming) throws IOException {
        this.streaming = streaming;
        if (streaming) {
            netImpl.createStreamSocket(fd, NetUtil.preferIPv4Stack());
        } else {
            netImpl.createDatagramSocket(fd, NetUtil.preferIPv4Stack());
        }
    }

    @Override
    protected void finalize() throws IOException {
        close();
    }

    @Override
    protected synchronized InputStream getInputStream() throws IOException {
        if (!fd.valid()) {
            throw new SocketException("Socket is closed");
        }

        return new SocketInputStream(this);
    }

    @Override
    public Object getOption(int optID) throws SocketException {
        if (optID == SocketOptions.SO_TIMEOUT) {
            return Integer.valueOf(receiveTimeout);
        } else if (optID == SocketOptions.IP_TOS) {
            return Integer.valueOf(trafficClass);
        } else {
            return netImpl.getSocketOption(fd, optID);
        }
    }

    @Override
    protected synchronized OutputStream getOutputStream() throws IOException {
        if (!fd.valid()) {
            throw new SocketException("Socket is closed");
        }
        return new SocketOutputStream(this);
    }

    @Override
    protected void listen(int backlog) throws IOException {
        if (NetUtil.usingSocks(proxy)) {
            // Do nothing for a SOCKS connection. The listen occurs on the
            // server during the bind.
            return;
        }
        netImpl.listen(fd, backlog);
    }

    @Override
    public void setOption(int optID, Object val) throws SocketException {
        if (optID == SocketOptions.SO_TIMEOUT) {
            receiveTimeout = ((Integer) val).intValue();
        } else {
            try {
                netImpl.setSocketOption(fd, optID, val);
            } catch (SocketException e) {
                // we don't throw an exception for IP_TOS even if the platform
                // won't let us set the requested value
                if (optID != SocketOptions.IP_TOS) {
                    throw e;
                }
            }

            /*
             * save this value as it is actually used differently for IPv4 and
             * IPv6 so we cannot get the value using the getOption. The option
             * is actually only set for IPv4 and a masked version of the value
             * will be set as only a subset of the values are allowed on the
             * socket. Therefore we need to retain it to return the value that
             * was set. We also need the value to be passed into a number of
             * natives so that it can be used properly with IPv6
             */
            if (optID == SocketOptions.IP_TOS) {
                trafficClass = ((Integer) val).intValue();
            }
        }
    }

    /**
     * Gets the SOCKS proxy server port.
     */
    private int socksGetServerPort() {
        // get socks server port from proxy. It is unnecessary to check
        // "socksProxyPort" property, since proxy setting should only be
        // determined by ProxySelector.
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        return addr.getPort();

    }

    /**
     * Gets the InetAddress of the SOCKS proxy server.
     */
    private InetAddress socksGetServerAddress() throws UnknownHostException {
        String proxyName;
        // get socks server address from proxy. It is unnecessary to check
        // "socksProxyHost" property, since all proxy setting should be
        // determined by ProxySelector.
        InetSocketAddress addr = (InetSocketAddress) proxy.address();
        proxyName = addr.getHostName();
        if (null == proxyName) {
            proxyName = addr.getAddress().getHostAddress();
        }
        return InetAddress.getByName(proxyName);
    }

    /**
     * Connect using a SOCKS server.
     */
    private void socksConnect(InetAddress applicationServerAddress,
            int applicationServerPort, int timeout) throws IOException {
        try {
            if (timeout == 0) {
                netImpl.connect(fd, trafficClass, socksGetServerAddress(),
                        socksGetServerPort());
            } else {
                netImpl.connectStreamWithTimeoutSocket(fd,
                        socksGetServerPort(), timeout, trafficClass,
                        socksGetServerAddress());
            }

        } catch (Exception e) {
            throw new SocketException("SOCKS connection failed: " + e);
        }

        socksRequestConnection(applicationServerAddress, applicationServerPort);

        lastConnectedAddress = applicationServerAddress;
        lastConnectedPort = applicationServerPort;
    }

    /**
     * Request a SOCKS connection to the application server given. If the
     * request fails to complete successfully, an exception is thrown.
     */
    private void socksRequestConnection(InetAddress applicationServerAddress,
            int applicationServerPort) throws IOException {
        socksSendRequest(Socks4Message.COMMAND_CONNECT,
                applicationServerAddress, applicationServerPort);
        Socks4Message reply = socksReadReply();
        if (reply.getCommandOrResult() != Socks4Message.RETURN_SUCCESS) {
            throw new IOException(reply.getErrorString(reply
                    .getCommandOrResult()));
        }
    }

    /**
     * Perform an accept for a SOCKS bind.
     */
    public void socksAccept() throws IOException {
        Socks4Message reply = socksReadReply();
        if (reply.getCommandOrResult() != Socks4Message.RETURN_SUCCESS) {
            throw new IOException(reply.getErrorString(reply
                    .getCommandOrResult()));
        }
    }

    /**
     * Shutdown the input portion of the socket.
     */
    @Override
    protected void shutdownInput() throws IOException {
        shutdownInput = true;
        netImpl.shutdownInput(fd);
    }

    /**
     * Shutdown the output portion of the socket.
     */
    @Override
    protected void shutdownOutput() throws IOException {
        netImpl.shutdownOutput(fd);
    }

    /**
     * Bind using a SOCKS server.
     */
    private void socksBind() throws IOException {
        try {
            netImpl.connect(fd, trafficClass, socksGetServerAddress(), socksGetServerPort());
        } catch (Exception e) {
            throw new IOException("Unable to connect to SOCKS server: " + e);
        }

        // There must be a connection to an application host for the bind to
        // work.
        if (lastConnectedAddress == null) {
            throw new SocketException("Invalid SOCKS client");
        }

        // Use the last connected address and port in the bind request.
        socksSendRequest(Socks4Message.COMMAND_BIND, lastConnectedAddress,
                lastConnectedPort);
        Socks4Message reply = socksReadReply();

        if (reply.getCommandOrResult() != Socks4Message.RETURN_SUCCESS) {
            throw new IOException(reply.getErrorString(reply
                    .getCommandOrResult()));
        }

        // A peculiarity of socks 4 - if the address returned is 0, use the
        // original socks server address.
        if (reply.getIP() == 0) {
            address = socksGetServerAddress();
        } else {
            // IPv6 support not yet required as
            // currently the Socks4Message.getIP() only returns int,
            // so only works with IPv4 4byte addresses
            byte[] replyBytes = new byte[4];
            NetUtil.intToBytes(reply.getIP(), replyBytes, 0);
            address = InetAddress.getByAddress(replyBytes);
        }
        localport = reply.getPort();
    }

    /**
     * Send a SOCKS V4 request.
     */
    private void socksSendRequest(int command, InetAddress address, int port)
            throws IOException {
        Socks4Message request = new Socks4Message();
        request.setCommandOrResult(command);
        request.setPort(port);
        request.setIP(address.getAddress());
        request.setUserId("default");

        getOutputStream().write(request.getBytes(), 0, request.getLength());
    }

    /**
     * Read a SOCKS V4 reply.
     */
    private Socks4Message socksReadReply() throws IOException {
        Socks4Message reply = new Socks4Message();
        int bytesRead = 0;
        while (bytesRead < Socks4Message.REPLY_LENGTH) {
            int count = getInputStream().read(reply.getBytes(), bytesRead,
                    Socks4Message.REPLY_LENGTH - bytesRead);
            if (-1 == count) {
                break;
            }
            bytesRead += count;
        }
        if (Socks4Message.REPLY_LENGTH != bytesRead) {
            throw new SocketException("Malformed reply from SOCKS server");
        }
        return reply;
    }

    @Override
    protected void connect(SocketAddress remoteAddr, int timeout)
            throws IOException {
        InetSocketAddress inetAddr = (InetSocketAddress) remoteAddr;
        connect(inetAddr.getAddress(), inetAddr.getPort(), timeout);
    }

    @Override
    protected boolean supportsUrgentData() {
        return true;
    }

    @Override
    protected void sendUrgentData(int value) throws IOException {
        netImpl.sendUrgentData(fd, (byte) value);
    }

    FileDescriptor getFD() {
        return fd;
    }

    int read(byte[] buffer, int offset, int count) throws IOException {
        if (shutdownInput) {
            return -1;
        }
        int read = netImpl.read(fd, buffer, offset, count, receiveTimeout);
        // Return of zero bytes for a blocking socket means a timeout occurred
        if (read == 0) {
            throw new SocketTimeoutException();
        }
        // Return of -1 indicates the peer was closed
        if (read == -1) {
            shutdownInput = true;
        }
        return read;
    }

    int write(byte[] buffer, int offset, int count) throws IOException {
        if (!streaming) {
            return netImpl.sendDatagram2(fd, buffer, offset, count, port,
                    address);
        }
        return netImpl.write(fd, buffer, offset, count);
    }
}
