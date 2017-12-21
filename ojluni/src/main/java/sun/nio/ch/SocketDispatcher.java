/*
 * Copyright (c) 2000, 2009, Oracle and/or its affiliates. All rights reserved.
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

package sun.nio.ch;

import dalvik.system.BlockGuard;

import java.io.*;

/**
 * Allows different platforms to call different native methods
 * for read and write operations.
 */

class SocketDispatcher extends NativeDispatcher
{

    int read(FileDescriptor fd, long address, int len) throws IOException {
        // Android-added: BlockGuard support.
        BlockGuard.getThreadPolicy().onNetwork();
        return FileDispatcherImpl.read0(fd, address, len);
    }

    long readv(FileDescriptor fd, long address, int len) throws IOException {
        // Android-added: BlockGuard support.
        BlockGuard.getThreadPolicy().onNetwork();
        return FileDispatcherImpl.readv0(fd, address, len);
    }

    int write(FileDescriptor fd, long address, int len) throws IOException {
        // Android-added: BlockGuard support.
        BlockGuard.getThreadPolicy().onNetwork();
        return FileDispatcherImpl.write0(fd, address, len);
    }

    long writev(FileDescriptor fd, long address, int len) throws IOException {
        // Android-added: BlockGuard support.
        BlockGuard.getThreadPolicy().onNetwork();
        return FileDispatcherImpl.writev0(fd, address, len);
    }

    void close(FileDescriptor fd) throws IOException {
        FileDispatcherImpl.close0(fd);
    }

    void preClose(FileDescriptor fd) throws IOException {
        FileDispatcherImpl.preClose0(fd);
    }
}
