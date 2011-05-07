/*
 * Copyright (C) 2010 The Android Open Source Project
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

#define LOG_TAG "NetworkUtilities"

#include "NetworkUtilities.h"
#include "JNIHelp.h"
#include "JniConstants.h"

#include <arpa/inet.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <sys/socket.h>

jobject socketAddressToInetAddress(JNIEnv* env, const sockaddr_storage* ss) {
    // Convert IPv4-mapped IPv6 addresses to IPv4 addresses.
    // The RI states "Java will never return an IPv4-mapped address".
    sockaddr_storage tmp;
    memset(&tmp, 0, sizeof(tmp));
    const sockaddr_in6* sin6 = reinterpret_cast<const sockaddr_in6*>(ss);
    if (ss->ss_family == AF_INET6 && IN6_IS_ADDR_V4MAPPED(&sin6->sin6_addr)) {
        // Copy the IPv6 address into the temporary sockaddr_storage.
        memcpy(&tmp, ss, sizeof(tmp));
        // Unmap it into an IPv4 address.
        sockaddr_in* sin = reinterpret_cast<sockaddr_in*>(&tmp);
        sin->sin_family = AF_INET;
        sin->sin_port = sin6->sin6_port;
        memcpy(&sin->sin_addr.s_addr, &sin6->sin6_addr.s6_addr[12], 4);
        // Fall through into the regular conversion using the unmapped address.
        ss = &tmp;
    }

    const void* rawAddress;
    size_t addressLength;
    if (ss->ss_family == AF_INET) {
        const sockaddr_in* sin = reinterpret_cast<const sockaddr_in*>(ss);
        rawAddress = &sin->sin_addr.s_addr;
        addressLength = 4;
    } else if (ss->ss_family == AF_INET6) {
        const sockaddr_in6* sin6 = reinterpret_cast<const sockaddr_in6*>(ss);
        rawAddress = &sin6->sin6_addr.s6_addr;
        addressLength = 16;
    } else {
        // We can't throw SocketException. We aren't meant to see bad addresses, so seeing one
        // really does imply an internal error.
        jniThrowExceptionFmt(env, "java/lang/IllegalArgumentException",
                "socketAddressToInetAddress bad ss_family: %i", ss->ss_family);
        return NULL;
    }

    jbyteArray byteArray = env->NewByteArray(addressLength);
    if (byteArray == NULL) {
        return NULL;
    }
    env->SetByteArrayRegion(byteArray, 0, addressLength, reinterpret_cast<const jbyte*>(rawAddress));

    static jmethodID getByAddressMethod = env->GetStaticMethodID(JniConstants::inetAddressClass,
            "getByAddress", "([B)Ljava/net/InetAddress;");
    if (getByAddressMethod == NULL) {
        return NULL;
    }
    return env->CallStaticObjectMethod(JniConstants::inetAddressClass, getByAddressMethod, byteArray);
}

static bool inetAddressToSocketAddress(JNIEnv* env, jobject inetAddress, int port, sockaddr_storage* ss, bool map) {
    memset(ss, 0, sizeof(*ss));

    // Get the byte array that stores the IP address bytes in the InetAddress.
    if (inetAddress == NULL) {
        jniThrowNullPointerException(env, NULL);
        return false;
    }
    static jfieldID fid = env->GetFieldID(JniConstants::inetAddressClass, "ipaddress", "[B");
    jbyteArray addressBytes = reinterpret_cast<jbyteArray>(env->GetObjectField(inetAddress, fid));
    if (addressBytes == NULL) {
        jniThrowNullPointerException(env, NULL);
        return false;
    }

    // We use AF_INET6 sockets, so we want an IPv6 address (which may be a IPv4-mapped address).
    sockaddr_in6* sin6 = reinterpret_cast<sockaddr_in6*>(ss);
    sin6->sin6_family = AF_INET6;
    sin6->sin6_port = htons(port);

    // Convert the IP address bytes to the appropriate kind of sockaddr.
    size_t addressLength = env->GetArrayLength(addressBytes);
    if (addressLength == 4) {
        if (map) {
            // We should represent this IPv4 address as an IPv4-mapped IPv6 sockaddr_in6.
            // Copy the bytes...
            jbyte* dst = reinterpret_cast<jbyte*>(&sin6->sin6_addr.s6_addr[12]);
            env->GetByteArrayRegion(addressBytes, 0, 4, dst);
            // INADDR_ANY and in6addr_any are both all-zeros...
            if (!IN6_IS_ADDR_UNSPECIFIED(&sin6->sin6_addr)) {
                // ...but all other IPv4-mapped addresses are ::ffff:a.b.c.d, so insert the ffff...
                memset(&(sin6->sin6_addr.s6_addr[10]), 0xff, 2);
            }
        } else {
            // We should represent this IPv4 address as an IPv4 sockaddr_in.
            sockaddr_in* sin = reinterpret_cast<sockaddr_in*>(ss);
            sin->sin_family = AF_INET;
            sin->sin_port = htons(port);
            jbyte* dst = reinterpret_cast<jbyte*>(&sin->sin_addr.s_addr);
            env->GetByteArrayRegion(addressBytes, 0, 4, dst);
        }
        return true;
    } else if (addressLength == 16) {
        // IPv6 address. Copy the bytes...
        jbyte* dst = reinterpret_cast<jbyte*>(&sin6->sin6_addr.s6_addr);
        env->GetByteArrayRegion(addressBytes, 0, 16, dst);
        // ...and set the scope id...
        static jfieldID fid = env->GetFieldID(JniConstants::inet6AddressClass, "scope_id", "I");
        sin6->sin6_scope_id = env->GetIntField(inetAddress, fid);
        return true;
    }

    // We can't throw SocketException. We aren't meant to see bad addresses, so seeing one
    // really does imply an internal error.
    jniThrowExceptionFmt(env, "java/lang/IllegalArgumentException",
            "inetAddressToSocketAddress bad array length: %i", addressLength);
    return false;
}

bool inetAddressToSocketAddressAny(JNIEnv* env, jobject inetAddress, int port, sockaddr_storage* ss) {
    return inetAddressToSocketAddress(env, inetAddress, port, ss, false);
}

bool inetAddressToSocketAddress6(JNIEnv* env, jobject inetAddress, int port, sockaddr_storage* ss) {
    return inetAddressToSocketAddress(env, inetAddress, port, ss, true);
}

bool setBlocking(int fd, bool blocking) {
    int flags = fcntl(fd, F_GETFL);
    if (flags == -1) {
        return false;
    }

    if (!blocking) {
        flags |= O_NONBLOCK;
    } else {
        flags &= ~O_NONBLOCK;
    }

    int rc = fcntl(fd, F_SETFL, flags);
    return (rc != -1);
}
