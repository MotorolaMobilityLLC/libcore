/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "OSNetworkSystem"

#include "AsynchronousSocketCloseMonitor.h"
#include "JNIHelp.h"
#include "JniConstants.h"
#include "JniException.h"
#include "NetFd.h"
#include "NetworkUtilities.h"
#include "ScopedPrimitiveArray.h"
#include "jni.h"
#include "valueOf.h"

#include <arpa/inet.h>
#include <errno.h>
#include <net/if.h>
#include <netdb.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/un.h>
#include <unistd.h>

/* constants for OSNetworkSystem_selectImpl */
#define SOCKET_OP_NONE 0
#define SOCKET_OP_READ 1
#define SOCKET_OP_WRITE 2

static void jniThrowSocketTimeoutException(JNIEnv* env, int error) {
    jniThrowExceptionWithErrno(env, "java/net/SocketTimeoutException", error);
}

/**
 * Returns the port number in a sockaddr_storage structure.
 *
 * @param address the sockaddr_storage structure to get the port from
 *
 * @return the port number, or -1 if the address family is unknown.
 */
static int getSocketAddressPort(sockaddr_storage* ss) {
    switch (ss->ss_family) {
    case AF_INET:
        return ntohs(reinterpret_cast<sockaddr_in*>(ss)->sin_port);
    case AF_INET6:
        return ntohs(reinterpret_cast<sockaddr_in6*>(ss)->sin6_port);
    default:
        return -1;
    }
}

// Creates mapped addresses.
// TODO: can this move to inetAddressToSocketAddress?
class CompatibleSocketAddress {
public:
    CompatibleSocketAddress(const sockaddr_storage& ss, bool mapUnspecified) {
        mCompatibleAddress = reinterpret_cast<const sockaddr*>(&ss);
        if (ss.ss_family == AF_INET) {
            // Map the IPv4 address in ss into an IPv6 address in mTmp.
            const sockaddr_in* sin = reinterpret_cast<const sockaddr_in*>(&ss);
            sockaddr_in6* sin6 = reinterpret_cast<sockaddr_in6*>(&mTmp);
            memset(sin6, 0, sizeof(*sin6));
            sin6->sin6_family = AF_INET6;
            sin6->sin6_port = sin->sin_port;
            // TODO: mapUnspecified was introduced because kernels < 2.6.31 don't allow
            // you to bind to ::ffff:0.0.0.0. When we move to something >= 2.6.31, we
            // should make the code behave as if mapUnspecified were always true, and
            // remove the parameter.
            // TODO: this code still appears to be necessary on 2.6.32, so there's something
            // wrong in the above comment.
            if (sin->sin_addr.s_addr != 0 || mapUnspecified) {
                memset(&(sin6->sin6_addr.s6_addr[10]), 0xff, 2);
            }
            memcpy(&sin6->sin6_addr.s6_addr[12], &sin->sin_addr.s_addr, 4);
            mCompatibleAddress = reinterpret_cast<const sockaddr*>(&mTmp);
        }
    }
    // Returns a pointer to an IPv6 address.
    const sockaddr* get() const {
        return mCompatibleAddress;
    }
private:
    const sockaddr* mCompatibleAddress;
    sockaddr_storage mTmp;
};

// Converts a number of milliseconds to a timeval.
static timeval toTimeval(long ms) {
    timeval tv;
    tv.tv_sec = ms / 1000;
    tv.tv_usec = (ms - tv.tv_sec*1000) * 1000;
    return tv;
}

/**
 * Establish a connection to a peer with a timeout.  The member functions are called
 * repeatedly in order to carry out the connect and to allow other tasks to
 * proceed on certain platforms. The caller must first call ConnectHelper::start.
 * if the result is -EINPROGRESS it will then
 * call ConnectHelper::isConnected until either another error or 0 is returned to
 * indicate the connect is complete.  Each time the function should sleep for no
 * more than 'timeout' milliseconds.  If the connect succeeds or an error occurs,
 * the caller must always end the process by calling ConnectHelper::done.
 *
 * Member functions return 0 if no errors occur, otherwise -errno. TODO: use +errno.
 */
class ConnectHelper {
public:
    ConnectHelper(JNIEnv* env) : mEnv(env) {
    }

    int start(NetFd& fd, jobject inetAddr, jint port) {
        sockaddr_storage ss;
        if (!inetAddressToSocketAddress(mEnv, inetAddr, port, &ss)) {
            return -EINVAL; // Bogus, but clearly a failure, and we've already thrown.
        }

        // Initiate a connection attempt...
        const CompatibleSocketAddress compatibleAddress(ss, true);
        int rc = connect(fd.get(), compatibleAddress.get(), sizeof(sockaddr_storage));

        // Did we get interrupted?
        if (fd.isClosed()) {
            return -EINVAL; // Bogus, but clearly a failure, and we've already thrown.
        }

        // Did we fail to connect?
        if (rc == -1) {
            if (errno != EINPROGRESS) {
                didFail(-errno); // Permanent failure, so throw.
            }
            return -errno;
        }

        // We connected straight away!
        return 0;
    }

    // Returns 0 if we're connected; -EINPROGRESS if we're still hopeful, -errno if we've failed.
    // 'timeout' the timeout in milliseconds. If timeout is negative, perform a blocking operation.
    int isConnected(int fd, int timeout) {
        timeval passedTimeout(toTimeval(timeout));

        // Initialize the fd sets for the select.
        fd_set readSet;
        fd_set writeSet;
        FD_ZERO(&readSet);
        FD_ZERO(&writeSet);
        FD_SET(fd, &readSet);
        FD_SET(fd, &writeSet);

        int nfds = fd + 1;
        timeval* tp = timeout >= 0 ? &passedTimeout : NULL;
        int rc = select(nfds, &readSet, &writeSet, NULL, tp);
        if (rc == -1) {
            if (errno == EINTR) {
                // We can't trivially retry a select with TEMP_FAILURE_RETRY, so punt and ask the
                // caller to try again.
                return -EINPROGRESS;
            }
            return -errno;
        }

        // If the fd is just in the write set, we're connected.
        if (FD_ISSET(fd, &writeSet) && !FD_ISSET(fd, &readSet)) {
            return 0;
        }

        // If the fd is in both the read and write set, there was an error.
        if (FD_ISSET(fd, &readSet) || FD_ISSET(fd, &writeSet)) {
            // Get the pending error.
            int error = 0;
            socklen_t errorLen = sizeof(error);
            if (getsockopt(fd, SOL_SOCKET, SO_ERROR, &error, &errorLen) == -1) {
                return -errno; // Couldn't get the real error, so report why not.
            }
            return -error;
        }

        // Timeout expired.
        return -EINPROGRESS;
    }

    void didFail(int result) {
        if (result == -ECONNRESET || result == -ECONNREFUSED || result == -EADDRNOTAVAIL ||
                result == -EADDRINUSE || result == -ENETUNREACH) {
            jniThrowExceptionWithErrno(mEnv, "java/net/ConnectException", -result);
        } else if (result == -EACCES) {
            jniThrowExceptionWithErrno(mEnv, "java/lang/SecurityException", -result);
        } else if (result == -ETIMEDOUT) {
            jniThrowSocketTimeoutException(mEnv, -result);
        } else {
            jniThrowSocketException(mEnv, -result);
        }
    }

private:
    JNIEnv* mEnv;
};

static jint OSNetworkSystem_writeDirect(JNIEnv* env, jobject,
        jobject fileDescriptor, jint address, jint offset, jint count) {
    if (count <= 0) {
        return 0;
    }

    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return 0;
    }

    jbyte* src = reinterpret_cast<jbyte*>(static_cast<uintptr_t>(address + offset));

    ssize_t bytesSent;
    {
        int intFd = fd.get();
        AsynchronousSocketCloseMonitor monitor(intFd);
        bytesSent = NET_FAILURE_RETRY(fd, write(intFd, src, count));
    }
    if (env->ExceptionOccurred()) {
        return -1;
    }

    if (bytesSent == -1) {
        if (errno == EAGAIN || errno == EWOULDBLOCK) {
            // We were asked to write to a non-blocking socket, but were told
            // it would block, so report "no bytes written".
            return 0;
        } else {
            jniThrowSocketException(env, errno);
            return 0;
        }
    }
    return bytesSent;
}

static jint OSNetworkSystem_write(JNIEnv* env, jobject,
        jobject fileDescriptor, jbyteArray byteArray, jint offset, jint count) {
    ScopedByteArrayRW bytes(env, byteArray);
    if (bytes.get() == NULL) {
        return -1;
    }
    jint address = static_cast<jint>(reinterpret_cast<uintptr_t>(bytes.get()));
    int result = OSNetworkSystem_writeDirect(env, NULL, fileDescriptor, address, offset, count);
    return result;
}

static jboolean OSNetworkSystem_connect(JNIEnv* env, jobject, jobject fileDescriptor, jobject inetAddr, jint port) {
    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return JNI_FALSE;
    }

    ConnectHelper context(env);
    return context.start(fd, inetAddr, port) == 0;
}

static jboolean OSNetworkSystem_isConnected(JNIEnv* env, jobject, jobject fileDescriptor, jint timeout) {
    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return JNI_FALSE;
    }

    ConnectHelper context(env);
    int result = context.isConnected(fd.get(), timeout);
    if (result == 0) {
        return JNI_TRUE;
    } else if (result == -EINPROGRESS) {
        // Not yet connected, but not yet denied either... Try again later.
        return JNI_FALSE;
    } else {
        context.didFail(result);
        return JNI_FALSE;
    }
}

static void OSNetworkSystem_bind(JNIEnv* env, jobject, jobject fileDescriptor,
        jobject inetAddress, jint port) {
    sockaddr_storage ss;
    if (!inetAddressToSocketAddress(env, inetAddress, port, &ss)) {
        return;
    }

    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return;
    }

    const CompatibleSocketAddress compatibleAddress(ss, false);
    int rc = TEMP_FAILURE_RETRY(bind(fd.get(), compatibleAddress.get(), sizeof(sockaddr_storage)));
    if (rc == -1) {
        jniThrowExceptionWithErrno(env, "java/net/BindException", errno);
    }
}

static void OSNetworkSystem_accept(JNIEnv* env, jobject, jobject serverFileDescriptor,
        jobject newSocket, jobject clientFileDescriptor) {

    if (newSocket == NULL) {
        jniThrowNullPointerException(env, NULL);
        return;
    }

    NetFd serverFd(env, serverFileDescriptor);
    if (serverFd.isClosed()) {
        return;
    }

    sockaddr_storage ss;
    socklen_t addrLen = sizeof(ss);
    sockaddr* sa = reinterpret_cast<sockaddr*>(&ss);

    int clientFd;
    {
        int intFd = serverFd.get();
        AsynchronousSocketCloseMonitor monitor(intFd);
        clientFd = NET_FAILURE_RETRY(serverFd, accept(intFd, sa, &addrLen));
    }
    if (env->ExceptionOccurred()) {
        return;
    }
    if (clientFd == -1) {
        if (errno == EAGAIN || errno == EWOULDBLOCK) {
            jniThrowSocketTimeoutException(env, errno);
        } else {
            jniThrowSocketException(env, errno);
        }
        return;
    }

    // Reset the inherited read timeout to the Java-specified default of 0.
    timeval timeout(toTimeval(0));
    int rc = setsockopt(clientFd, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
    if (rc == -1) {
        LOGE("couldn't reset SO_RCVTIMEO on accepted socket fd %i: %s", clientFd, strerror(errno));
        jniThrowSocketException(env, errno);
    }

    /*
     * For network sockets, put the peer address and port in instance variables.
     * We don't bother to do this for UNIX domain sockets, since most peers are
     * anonymous anyway.
     */
    if (ss.ss_family == AF_INET || ss.ss_family == AF_INET6) {
        // Remote address and port.
        jobject remoteAddress = socketAddressToInetAddress(env, &ss);
        if (remoteAddress == NULL) {
            close(clientFd);
            return;
        }
        int remotePort = getSocketAddressPort(&ss);

        // Local port.
        memset(&ss, 0, addrLen);
        int rc = getsockname(clientFd, sa, &addrLen);
        if (rc == -1) {
            close(clientFd);
            jniThrowSocketException(env, errno);
            return;
        }
        int localPort = getSocketAddressPort(&ss);

        static jfieldID addressFid = env->GetFieldID(JniConstants::socketImplClass, "address", "Ljava/net/InetAddress;");
        static jfieldID localPortFid = env->GetFieldID(JniConstants::socketImplClass, "localport", "I");
        static jfieldID portFid = env->GetFieldID(JniConstants::socketImplClass, "port", "I");
        env->SetObjectField(newSocket, addressFid, remoteAddress);
        env->SetIntField(newSocket, portFid, remotePort);
        env->SetIntField(newSocket, localPortFid, localPort);
    }

    jniSetFileDescriptorOfFD(env, clientFileDescriptor, clientFd);
}

static void OSNetworkSystem_sendUrgentData(JNIEnv* env, jobject,
        jobject fileDescriptor, jbyte value) {
    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return;
    }

    int rc = send(fd.get(), &value, 1, MSG_OOB);
    if (rc == -1) {
        jniThrowSocketException(env, errno);
    }
}

static void OSNetworkSystem_disconnectDatagram(JNIEnv* env, jobject, jobject fileDescriptor) {
    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return;
    }

    // To disconnect a datagram socket, we connect to a bogus address with
    // the family AF_UNSPEC.
    sockaddr_storage ss;
    memset(&ss, 0, sizeof(ss));
    ss.ss_family = AF_UNSPEC;
    const sockaddr* sa = reinterpret_cast<const sockaddr*>(&ss);
    int rc = TEMP_FAILURE_RETRY(connect(fd.get(), sa, sizeof(ss)));
    if (rc == -1) {
        jniThrowSocketException(env, errno);
    }
}

// TODO: can we merge this with recvDirect?
static jint OSNetworkSystem_readDirect(JNIEnv* env, jobject, jobject fileDescriptor,
        jint address, jint count) {
    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return 0;
    }

    jbyte* dst = reinterpret_cast<jbyte*>(static_cast<uintptr_t>(address));
    ssize_t bytesReceived;
    {
        int intFd = fd.get();
        AsynchronousSocketCloseMonitor monitor(intFd);
        bytesReceived = NET_FAILURE_RETRY(fd, read(intFd, dst, count));
    }
    if (env->ExceptionOccurred()) {
        return -1;
    }
    if (bytesReceived == 0) {
        return -1;
    } else if (bytesReceived == -1) {
        if (errno == EAGAIN || errno == EWOULDBLOCK) {
            // We were asked to read a non-blocking socket with no data
            // available, so report "no bytes read".
            return 0;
        } else {
            jniThrowSocketException(env, errno);
            return 0;
        }
    } else {
        return bytesReceived;
    }
}

static jint OSNetworkSystem_read(JNIEnv* env, jclass, jobject fileDescriptor,
        jbyteArray byteArray, jint offset, jint count) {
    ScopedByteArrayRW bytes(env, byteArray);
    if (bytes.get() == NULL) {
        return -1;
    }
    jint address = static_cast<jint>(reinterpret_cast<uintptr_t>(bytes.get() + offset));
    return OSNetworkSystem_readDirect(env, NULL, fileDescriptor, address, count);
}

// TODO: can we merge this with readDirect?
static jint OSNetworkSystem_recvDirect(JNIEnv* env, jobject, jobject fileDescriptor, jobject packet,
        jint address, jint offset, jint length, jboolean peek, jboolean connected) {
    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return 0;
    }

    char* buf = reinterpret_cast<char*>(static_cast<uintptr_t>(address + offset));
    const int flags = peek ? MSG_PEEK : 0;
    sockaddr_storage ss;
    memset(&ss, 0, sizeof(ss));
    socklen_t sockAddrLen = sizeof(ss);
    sockaddr* from = connected ? NULL : reinterpret_cast<sockaddr*>(&ss);
    socklen_t* fromLength = connected ? NULL : &sockAddrLen;

    ssize_t bytesReceived;
    {
        int intFd = fd.get();
        AsynchronousSocketCloseMonitor monitor(intFd);
        bytesReceived = NET_FAILURE_RETRY(fd, recvfrom(intFd, buf, length, flags, from, fromLength));
    }
    if (env->ExceptionOccurred()) {
        return -1;
    }
    if (bytesReceived == -1) {
        if (connected && errno == ECONNREFUSED) {
            jniThrowException(env, "java/net/PortUnreachableException", "");
        } else if (errno == EAGAIN || errno == EWOULDBLOCK) {
            jniThrowSocketTimeoutException(env, errno);
        } else {
            jniThrowSocketException(env, errno);
        }
        return 0;
    }

    static jfieldID addressFid = env->GetFieldID(JniConstants::datagramPacketClass, "address", "Ljava/net/InetAddress;");
    static jfieldID lengthFid = env->GetFieldID(JniConstants::datagramPacketClass, "length", "I");
    static jfieldID portFid = env->GetFieldID(JniConstants::datagramPacketClass, "port", "I");
    if (packet != NULL) {
        env->SetIntField(packet, lengthFid, bytesReceived);
        if (!connected) {
            jobject sender = socketAddressToInetAddress(env, &ss);
            if (sender == NULL) {
                return 0;
            }
            int port = getSocketAddressPort(&ss);
            env->SetObjectField(packet, addressFid, sender);
            env->SetIntField(packet, portFid, port);
        }
    }
    return bytesReceived;
}

static jint OSNetworkSystem_recv(JNIEnv* env, jobject, jobject fd, jobject packet,
        jbyteArray javaBytes, jint offset, jint length, jboolean peek, jboolean connected) {
    ScopedByteArrayRW bytes(env, javaBytes);
    if (bytes.get() == NULL) {
        return -1;
    }
    uintptr_t address = reinterpret_cast<uintptr_t>(bytes.get());
    return OSNetworkSystem_recvDirect(env, NULL, fd, packet, address, offset, length, peek,
            connected);
}








static jint OSNetworkSystem_sendDirect(JNIEnv* env, jobject, jobject fileDescriptor, jint address, jint offset, jint length, jint port, jobject inetAddress) {
    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        return -1;
    }

    sockaddr_storage receiver;
    if (inetAddress != NULL && !inetAddressToSocketAddress(env, inetAddress, port, &receiver)) {
        return -1;
    }

    int flags = 0;
    char* buf = reinterpret_cast<char*>(static_cast<uintptr_t>(address + offset));
    sockaddr* to = inetAddress ? reinterpret_cast<sockaddr*>(&receiver) : NULL;
    socklen_t toLength = inetAddress ? sizeof(receiver) : 0;

    ssize_t bytesSent;
    {
        int intFd = fd.get();
        AsynchronousSocketCloseMonitor monitor(intFd);
        bytesSent = NET_FAILURE_RETRY(fd, sendto(intFd, buf, length, flags, to, toLength));
    }
    if (env->ExceptionOccurred()) {
        return -1;
    }
    if (bytesSent == -1) {
        if (errno == ECONNRESET || errno == ECONNREFUSED) {
            return 0;
        } else {
            jniThrowSocketException(env, errno);
        }
    }
    return bytesSent;
}

static jint OSNetworkSystem_send(JNIEnv* env, jobject, jobject fd,
        jbyteArray data, jint offset, jint length,
        jint port, jobject inetAddress) {
    ScopedByteArrayRO bytes(env, data);
    if (bytes.get() == NULL) {
        return -1;
    }
    return OSNetworkSystem_sendDirect(env, NULL, fd,
            reinterpret_cast<uintptr_t>(bytes.get()), offset, length, port, inetAddress);
}








static bool isValidFd(int fd) {
    return fd >= 0 && fd < FD_SETSIZE;
}

static bool initFdSet(JNIEnv* env, jobjectArray fdArray, jint count, fd_set* fdSet, int* maxFd) {
    for (int i = 0; i < count; ++i) {
        jobject fileDescriptor = env->GetObjectArrayElement(fdArray, i);
        if (fileDescriptor == NULL) {
            return false;
        }

        const int fd = jniGetFDFromFileDescriptor(env, fileDescriptor);
        if (!isValidFd(fd)) {
            LOGE("selectImpl: ignoring invalid fd %i", fd);
            continue;
        }

        FD_SET(fd, fdSet);

        if (fd > *maxFd) {
            *maxFd = fd;
        }
    }
    return true;
}

/*
 * Note: fdSet has to be non-const because although on Linux FD_ISSET() is sane
 * and takes a const fd_set*, it takes fd_set* on Mac OS. POSIX is not on our
 * side here:
 *   http://www.opengroup.org/onlinepubs/000095399/functions/select.html
 */
static bool translateFdSet(JNIEnv* env, jobjectArray fdArray, jint count, fd_set& fdSet, jint* flagArray, size_t offset, jint op) {
    for (int i = 0; i < count; ++i) {
        jobject fileDescriptor = env->GetObjectArrayElement(fdArray, i);
        if (fileDescriptor == NULL) {
            return false;
        }

        const int fd = jniGetFDFromFileDescriptor(env, fileDescriptor);
        if (isValidFd(fd) && FD_ISSET(fd, &fdSet)) {
            flagArray[i + offset] = op;
        } else {
            flagArray[i + offset] = SOCKET_OP_NONE;
        }
    }
    return true;
}

static jboolean OSNetworkSystem_selectImpl(JNIEnv* env, jclass,
        jobjectArray readFDArray, jobjectArray writeFDArray, jint countReadC,
        jint countWriteC, jintArray outFlags, jlong timeoutMs) {

    // Initialize the fd_sets.
    int maxFd = -1;
    fd_set readFds;
    fd_set writeFds;
    FD_ZERO(&readFds);
    FD_ZERO(&writeFds);
    bool initialized = initFdSet(env, readFDArray, countReadC, &readFds, &maxFd) &&
                       initFdSet(env, writeFDArray, countWriteC, &writeFds, &maxFd);
    if (!initialized) {
        return -1;
    }

    // Initialize the timeout, if any.
    timeval tv;
    timeval* tvp = NULL;
    if (timeoutMs >= 0) {
        tv = toTimeval(timeoutMs);
        tvp = &tv;
    }

    // Perform the select.
    int result = select(maxFd + 1, &readFds, &writeFds, NULL, tvp);
    if (result == 0) {
        // Timeout.
        return JNI_FALSE;
    } else if (result == -1) {
        // Error.
        if (errno == EINTR) {
            return JNI_FALSE;
        } else {
            jniThrowSocketException(env, errno);
            return JNI_FALSE;
        }
    }

    // Translate the result into the int[] we're supposed to fill in.
    ScopedIntArrayRW flagArray(env, outFlags);
    if (flagArray.get() == NULL) {
        return JNI_FALSE;
    }
    return translateFdSet(env, readFDArray, countReadC, readFds, flagArray.get(), 0, SOCKET_OP_READ) &&
            translateFdSet(env, writeFDArray, countWriteC, writeFds, flagArray.get(), countReadC, SOCKET_OP_WRITE);
}

static void OSNetworkSystem_close(JNIEnv* env, jobject, jobject fileDescriptor) {
    NetFd fd(env, fileDescriptor);
    if (fd.isClosed()) {
        // Socket.close doesn't throw if you try to close an already-closed socket.
        env->ExceptionClear();
        return;
    }

    int oldFd = fd.get();
    jniSetFileDescriptorOfFD(env, fileDescriptor, -1);
    AsynchronousSocketCloseMonitor::signalBlockedThreads(oldFd);
    close(oldFd);
}

static JNINativeMethod gMethods[] = {
    NATIVE_METHOD(OSNetworkSystem, accept, "(Ljava/io/FileDescriptor;Ljava/net/SocketImpl;Ljava/io/FileDescriptor;)V"),
    NATIVE_METHOD(OSNetworkSystem, bind, "(Ljava/io/FileDescriptor;Ljava/net/InetAddress;I)V"),
    NATIVE_METHOD(OSNetworkSystem, close, "(Ljava/io/FileDescriptor;)V"),
    NATIVE_METHOD(OSNetworkSystem, connect, "(Ljava/io/FileDescriptor;Ljava/net/InetAddress;I)Z"),
    NATIVE_METHOD(OSNetworkSystem, disconnectDatagram, "(Ljava/io/FileDescriptor;)V"),
    NATIVE_METHOD(OSNetworkSystem, isConnected, "(Ljava/io/FileDescriptor;I)Z"),
    NATIVE_METHOD(OSNetworkSystem, read, "(Ljava/io/FileDescriptor;[BII)I"),
    NATIVE_METHOD(OSNetworkSystem, readDirect, "(Ljava/io/FileDescriptor;II)I"),
    NATIVE_METHOD(OSNetworkSystem, recv, "(Ljava/io/FileDescriptor;Ljava/net/DatagramPacket;[BIIZZ)I"),
    NATIVE_METHOD(OSNetworkSystem, recvDirect, "(Ljava/io/FileDescriptor;Ljava/net/DatagramPacket;IIIZZ)I"),
    NATIVE_METHOD(OSNetworkSystem, selectImpl, "([Ljava/io/FileDescriptor;[Ljava/io/FileDescriptor;II[IJ)Z"),
    NATIVE_METHOD(OSNetworkSystem, send, "(Ljava/io/FileDescriptor;[BIIILjava/net/InetAddress;)I"),
    NATIVE_METHOD(OSNetworkSystem, sendDirect, "(Ljava/io/FileDescriptor;IIIILjava/net/InetAddress;)I"),
    NATIVE_METHOD(OSNetworkSystem, sendUrgentData, "(Ljava/io/FileDescriptor;B)V"),
    NATIVE_METHOD(OSNetworkSystem, write, "(Ljava/io/FileDescriptor;[BII)I"),
    NATIVE_METHOD(OSNetworkSystem, writeDirect, "(Ljava/io/FileDescriptor;III)I"),
};

int register_org_apache_harmony_luni_platform_OSNetworkSystem(JNIEnv* env) {
    AsynchronousSocketCloseMonitor::init();
    return jniRegisterNativeMethods(env, "org/apache/harmony/luni/platform/OSNetworkSystem", gMethods, NELEM(gMethods));
}
