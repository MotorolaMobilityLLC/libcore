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

package org.apache.harmony.luni.platform;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketImpl;

/*
 * The interface for network methods.
 */
public interface INetworkSystem {
    public void accept(FileDescriptor serverFd, SocketImpl newSocket, FileDescriptor clientFd)
            throws IOException;

    public void bind(FileDescriptor fd, InetAddress inetAddress, int port) throws SocketException;

    public int read(FileDescriptor fd, byte[] data, int offset, int count) throws IOException;

    public int readDirect(FileDescriptor fd, int address, int count) throws IOException;

    public int write(FileDescriptor fd, byte[] data, int offset, int count) throws IOException;

    public int writeDirect(FileDescriptor fd, int address, int offset, int count) throws IOException;

    public boolean connectNonBlocking(FileDescriptor fd, InetAddress inetAddress, int port)
            throws IOException;
    public boolean isConnected(FileDescriptor fd, int timeout) throws IOException;

    public int send(FileDescriptor fd, byte[] data, int offset, int length,
            int port, InetAddress inetAddress) throws IOException;
    public int sendDirect(FileDescriptor fd, int address, int offset, int length,
            int port, InetAddress inetAddress) throws IOException;

    public int recv(FileDescriptor fd, DatagramPacket packet, byte[] data, int offset,
            int length, boolean peek, boolean connected) throws IOException;
    public int recvDirect(FileDescriptor fd, DatagramPacket packet, int address, int offset,
            int length, boolean peek, boolean connected) throws IOException;

    public void disconnectDatagram(FileDescriptor fd) throws SocketException;

    public void sendUrgentData(FileDescriptor fd, byte value);

    public void connect(FileDescriptor fd, InetAddress inetAddress, int port, int timeout)
            throws SocketException;

    /**
     * Select the given file descriptors for read and write operations.
     *
     * <p>The first {@code numReadable} file descriptors of {@code readFDs} will
     * be selected for read-ready operations. The first {@code numWritable} file
     * descriptors in {@code writeFDs} will be selected for write-ready
     * operations. A file descriptor can appear in either or both and must not
     * be null. If the file descriptor is closed during the select the behavior
     * depends upon the underlying OS.
     *
     * @param readFDs
     *            all sockets interested in read and accept
     * @param writeFDs
     *            all sockets interested in write and connect
     * @param numReadable
     *            the size of the subset of readFDs to read or accept.
     * @param numWritable
     *            the size of the subset of writeFDs to write or connect
     * @param timeout
     *            timeout in milliseconds
     * @param flags
     *            for output. Length must be at least {@code numReadable
     *            + numWritable}. Upon returning, each element describes the
     *            state of the descriptor in the corresponding read or write
     *            array. See {@code SelectorImpl.READABLE} and {@code
     *            SelectorImpl.WRITEABLE}
     * @return true
     *            unless selection timed out or was interrupted
     * @throws SocketException
     */
    public boolean select(FileDescriptor[] readFDs, FileDescriptor[] writeFDs,
            int numReadable, int numWritable, long timeout, int[] flags)
            throws SocketException;

    /**
     * It is an error to close the same file descriptor from multiple threads
     * concurrently.
     */
    public void close(FileDescriptor fd) throws IOException;
}
