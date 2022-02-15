/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
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

/*
 * @test
 * @bug 8145635
 * @summary Add TCP_QUICKACK socket option
 * @modules jdk.net
 * @run main QuickAckTest
 */
package test.java.net.SocketOptions;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import jdk.net.ExtendedSocketOptions;
import jdk.net.Sockets;
import org.testng.annotations.Test;
import org.testng.Assert;

public class QuickAckTest {

    private static final String LOCAL_HOST = "127.0.0.1";

    @Test
    public void TestQuickAck() throws IOException {

        try (ServerSocket ss = new ServerSocket(0);
             Socket s = new Socket(LOCAL_HOST, ss.getLocalPort());
             DatagramSocket ds = new DatagramSocket(0);
             MulticastSocket mc = new MulticastSocket(0)) {

            if (ss.supportedOptions().contains(ExtendedSocketOptions.TCP_QUICKACK)) {
                Sockets.setOption(ss, ExtendedSocketOptions.TCP_QUICKACK, true);
                if (!ss.getOption(ExtendedSocketOptions.TCP_QUICKACK)) {
                    Assert.fail("Test failed, TCP_QUICKACK should"
                            + " have been set");
                }
            }
            if (s.supportedOptions().contains(ExtendedSocketOptions.TCP_QUICKACK)) {
                Sockets.setOption(s, ExtendedSocketOptions.TCP_QUICKACK, true);
                if (!s.getOption(ExtendedSocketOptions.TCP_QUICKACK)) {
                    Assert.fail("Test failed, TCP_QUICKACK should"
                            + " have been set");
                }
            }
            if (ds.supportedOptions().contains(ExtendedSocketOptions.TCP_QUICKACK)) {
                Assert.fail("Test failed, TCP_QUICKACK is applicable"
                        + " for TCP Sockets only.");
            }
            if (mc.supportedOptions().contains(ExtendedSocketOptions.TCP_QUICKACK)) {
                Assert.fail("Test failed, TCP_QUICKACK is applicable"
                        + " for TCP Sockets only");
            }
        }
    }
}