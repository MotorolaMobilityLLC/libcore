/*
 * Copyright (c) 2002, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

/* @test
 * @bug 4629307
 * @summary Socket with OP_READ would get selected on connect
 * @author kladko
 */

import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ReadAfterConnect {
    public static void main(String[] argv) throws Exception {
        try (ByteServer server = new ByteServer();
             SocketChannel sc = SocketChannel.open(server.address())) {

            server.acceptConnection();

            try (Selector sel = Selector.open()) {
                sc.configureBlocking(false);
                sc.register(sel, SelectionKey.OP_READ);
                // Previously channel would get selected here, although there is nothing to read
                if (sel.selectNow() != 0)
                    throw new Exception("Select returned nonzero value");
            }
        }
    }

}
