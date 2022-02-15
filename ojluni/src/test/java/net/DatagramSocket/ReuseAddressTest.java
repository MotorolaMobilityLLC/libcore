/* Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
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
package test.java.net.DatagramSocket;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import org.testng.annotations.Test;
import static org.junit.Assert.fail;

/*
 * @test
 * @bug 8153674
 * @key intermittent
 * @summary This test might fail intermittently as it needs a UDP socket that
 *          binds to the wildcard address.
 * @summary Expected SocketException not thrown when calling bind() with
 *   setReuseAddress(false)
 * @run main/othervm ReuseAddressTest
 */

public class ReuseAddressTest {

    String getInfo(DatagramSocket soc) {
        if (soc == null) {
            return null;
        }

        return "localPort: " + soc.getLocalPort()
                + "; localAddress: " + soc.getLocalAddress()
                + "; remotePort: " + soc.getPort()
                + "; remoteAddress: " + soc.getInetAddress()
                + "; isClosed: " + soc.isClosed()
                + "; isBound: " + soc.isBound();
    }

    static InetSocketAddress createSocketAddress(int testMcastPort) throws Exception {
        InetAddress localAddress = InetAddress.getLocalHost();
        InetSocketAddress localSocketAddress = new InetSocketAddress(localAddress, testMcastPort);
        return localSocketAddress;
    }

    /* standalone interface */
    @Test
    public void testReuseAddress() throws Exception {
        ReuseAddressTest test = new ReuseAddressTest();
        test.DatagramSocket0029();
        test.DatagramSocket0030();
        test.DatagramSocket0031();
        test.DatagramSocket0032();
        test.DatagramSocket0034();
        test.DatagramSocket0035();
        test.DatagramSocket2028();
        test.DatagramSocket2029();
        test.DatagramSocket2030();

    }

    /**
     * Equivalence class partitioning with input values orientation for public
     * void setReuseAddress(boolean on) throws SocketException,
     * <br><b>on</b>: false.
     * <br><b>Expected results</b>: getReuseAddress() will return false
     */
    public void DatagramSocket0029() throws Exception {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket(null);
            ds.setReuseAddress(false);
            if (ds.getReuseAddress() == true) {
                fail("SO_REUSEADDR is not set to false");
            }
        } catch (IOException e) {
            fail("unexpected: " + e);
        } catch (SecurityException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }

    /**
     * Equivalence class partitioning with input values orientation for public
     * void setReuseAddress(boolean on) throws SocketException,
     * <br><b>on</b>: true.
     * <br><b>Expected results</b>: Allows completely duplicate bindings (same
     * address and port) on multicast sockets
     */
    public void DatagramSocket0030() throws Exception {
        MulticastSocket ms1 = null;
        MulticastSocket ms2 = null;
        try {
            InetSocketAddress addr = createSocketAddress(0);

            ms1 = new MulticastSocket(null);
            ms1.setReuseAddress(true);

            try {
                ms1.bind(addr);
            } catch (SocketException e) {
                fail("cannot bind first socket to " + addr
                        + " unexpected " + e);
            }

            addr = createSocketAddress(ms1.getLocalPort());
            ms2 = new MulticastSocket(null);
            ms2.setReuseAddress(true);

            try {
                ms2.bind(addr);
            } catch (SocketException e) {
                fail("cannot bind second socket to " + addr
                        + " unexpected " + e);
            }

            if (ms1.getLocalPort() != addr.getPort() || !ms1.isBound()
                    || ms2.getLocalPort() != addr.getPort() || !ms2.isBound()) {
                fail("bind() fails with: " + addr);
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
            fail("unexpected: " + e);
        } catch (SecurityException e) {
        } finally {
            if (ms1 != null) {
                ms1.close();
            }
            if (ms2 != null) {
                ms2.close();
            }
        }
    }

    /**
     * Equivalence class partitioning with input values orientation for public
     * void setReuseAddress(boolean on) throws SocketException,
     * <br><b>on</b>: false.
     * <br><b>Expected results</b>: The second bind will throw SocketException,
     * when SO_REUSEADDR disable
     */
    public void DatagramSocket0031() throws Exception {
        MulticastSocket ms1 = null;
        MulticastSocket ms2 = null;
        try {
            InetSocketAddress addr = createSocketAddress(0);

            ms1 = new MulticastSocket(null);
            try {
                ms1.bind(addr);
            } catch (SocketException e) {
                fail("cannot bind first socket to " + addr
                        + " unexpected " + e);
            }

            addr = createSocketAddress(ms1.getLocalPort());
            ms2 = new MulticastSocket(null);
            ms2.setReuseAddress(false);  // method under test

            try {
                ms2.bind(addr);
                fail("no exceptions from bind() with " + addr);
            } catch (SocketException e) {
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
            fail("unexpected: " + e);
        } catch (SecurityException e) {
        } finally {
            if (ms1 != null) {
                ms1.close();
            }
            if (ms2 != null) {
                ms2.close();
            }
        }
    }

    /**
     * Equivalence class partitioning with input values orientation for public
     * void setReuseAddress(boolean on) throws SocketException,
     * <br><b>on</b>: true.
     * <br><b>Expected results</b>: Allows a single process to bind the same
     * port to multiple sockets as long as each bind specifies a different local
     * IP address
     */
    public void DatagramSocket0032() throws Exception {
        DatagramSocket ds1 = null;
        DatagramSocket ds2 = null;
        try {

            InetSocketAddress isa1 = createSocketAddress(0);
            InetAddress addr = isa1.getAddress();
            InetAddress wildcard = InetAddress.getByName("0.0.0.0");


            ds1 = new DatagramSocket(null);
            ds1.setReuseAddress(true);    // method under test
            ds1.bind(isa1);

            InetSocketAddress isa2 = new InetSocketAddress(wildcard, ds1.getLocalPort());

            ds2 = new DatagramSocket(null);
            ds2.setReuseAddress(true);    // method under test
            if (!ds2.getReuseAddress()) {
            }

            try {
                ds2.bind(isa2);
            } catch (SocketException e) {
                fail("cannot bind second socket to " + isa2
                        + " unexpected " + e);
            }

            if (ds1.getLocalPort() != ds2.getLocalPort() || !ds1.isBound()
                    || !ds2.isBound()) {
                fail("bind() fails with: " + addr);
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
            fail("unexpected: " + e);
        } catch (SecurityException e) {
        } finally {
            if (ds1 != null) {
                ds1.close();
            }
            if (ds2 != null) {
                ds2.close();
            }
        }
    }

    /**
     * Assertion testing for public int getTrafficClass() throws
     * SocketException, will return a number in range from 0 to 255 or throw
     * SocketException.
     */
    public void DatagramSocket2028() throws Exception {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            int tc = ds.getTrafficClass();
            if (tc < 0 || tc > 255) {
                fail("getTrafficClass() returns: " + tc);
            }
        } catch (SecurityException e) {
        } catch (SocketException e) {
            e.printStackTrace(System.out);
            fail("Unexpected exception : " + e);
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }

    /**
     * Assertion testing for public void setTrafficClass(int tc) throws
     * SocketException, IAE will be thrown with tc less than 0 or greater than
     * 255.
     */
    public void DatagramSocket2029() throws Exception {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
        } catch (SecurityException e) {
        } catch (IOException e) {
            fail("cannot create socket: " + e);
        }

        int[] values = {
                Integer.MIN_VALUE, Integer.MIN_VALUE + 1, -1000, -2, -1,
                256, 257, 1000, 50000, Integer.MAX_VALUE - 1, Integer.MAX_VALUE
        };

        for (int i = 0; i < values.length; i++) {
            try {
                ds.setTrafficClass(values[i]);
                ds.close();
                fail("setTrafficClass() fails with : " + values[i]);
            } catch (SocketException e) {
                ds.close();
                fail("setTrafficClass() throws : " + e);
            } catch (IllegalArgumentException e) {
            }
        }
    }

    /**
     * Assertion testing for public void setTrafficClass(int tc) throws
     * SocketException, only SocketException may be thrown with tc in range from
     * 0 to 255.
     */
    public void DatagramSocket2030() throws Exception {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
        } catch (SecurityException e) {
        } catch (IOException e) {
            fail("cannot create socket: " + e);
        }

        for (int i = 0; i <= 255; i++) {
            try {
                ds.setTrafficClass(i);
            } catch (SocketException e) {
            }
        }
    }

    /**
     * Equivalence class partitioning with input values orientation for public
     * void setBroadcast(boolean on) throws SocketException,
     * <br><b>on</b>: false.
     * <br><b>Expected results</b>: getBroadcast() will return false
     */
    public void DatagramSocket0034() throws Exception {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            ds.setBroadcast(false);
            if (ds.getBroadcast() == true) {
                fail("SO_BROADCAST is not set to false");
            }
        } catch (IOException e) {
            fail("unexpected: " + e);
        } catch (SecurityException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }

    /**
     * Equivalence class partitioning with input values orientation for public
     * void setBroadcast(boolean on) throws SocketException,
     * <br><b>on</b>: true.
     * <br><b>Expected results</b>: getBroadcast() will return true
     */
    public void DatagramSocket0035() throws Exception {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket();
            ds.setBroadcast(true);
            if (ds.getBroadcast() == false) {
                fail("SO_BROADCAST is not set to true");
            }
        } catch (IOException e) {
            fail("unexpected: " + e);
        } catch (SecurityException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }
}