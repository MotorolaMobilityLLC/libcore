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

package org.apache.harmony.luni.tests.java.net;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargetNew;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import org.apache.harmony.testframework.serialization.SerializationTest;
import org.apache.harmony.testframework.serialization.SerializationTest.SerializableAssert;

@TestTargetClass(Inet4Address.class)
public class Inet4AddressTest extends junit.framework.TestCase {

    private Inet4Address ipv4Localhost;
    private Inet4Address ipv4LoopbackIp;

    @Override protected void setUp() throws Exception {
        super.setUp();
        byte[] ipv4Loopback = { 127, 0, 0, 1 };
        ipv4LoopbackIp = (Inet4Address) InetAddress.getByAddress(ipv4Loopback);
        ipv4Localhost = (Inet4Address) InetAddress.getByAddress("localhost", ipv4Loopback);
    }

    /**
     * @tests java.net.Inet4Address#isMulticastAddress()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isMulticastAddress",
        args = {}
    )
    public void test_isMulticastAddress() {

        // Create 2 IP v4 addresses and call "isMulticastAddress()"
        // result should return true if the first 4 bits of the
        // address are: 1110, false otherwise
        // Make 1 address with 1110, and 1 without
        String addrName = "";
        try {
            addrName = "224.0.0.0"; // a multicast addr 1110 = 224-239
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue("Multicast address " + addrName + " not detected.", addr
                    .isMulticastAddress());

            addrName = "239.255.255.255"; // a multicast addr 1110 = 224-239
            addr = InetAddress.getByName(addrName);
            assertTrue("Multicast address " + addrName + " not detected.", addr
                    .isMulticastAddress());

            addrName = "42.42.42.42"; // a non-multicast address
            addr = InetAddress.getByName(addrName);
            assertTrue("Non multicast address " + addrName
                    + " reporting as a multicast address.", !addr
                    .isMulticastAddress());
        } catch (Exception e) {
            fail("Unknown address : " + addrName);
        }

    }

    /**
     * @tests java.net.Inet4Address#isAnyLocalAddress()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isAnyLocalAddress",
        args = {}
    )
    public void test_isAnyLocalAddress() {
        String addrName = "";
        try {
            addrName = "0.0.0.0";
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue("ANY address " + addrName + " not detected.", addr
                    .isAnyLocalAddress());
        } catch (Exception e) {
            fail("Unknown address : " + addrName);
        }
    }

    /**
     * @tests java.net.Inet4Address#isLoopbackAddress()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isLoopbackAddress",
        args = {}
    )
    public void test_isLoopbackAddress() {
        // Create some IP V4 addresses and test if they are local...

        String addrName = "";
        try {
            addrName = "127.0.0.0"; // a loopback address should be 127.d.d.d
            InetAddress addr = ipv4LoopbackIp;
            assertTrue("Loopback address " + addrName + " not detected.", addr
                    .isLoopbackAddress());

            addrName = "127.42.42.42"; // a loopback address should be
            // 127.d.d.d
            addr = InetAddress.getByName(addrName);
            assertTrue("Loopback address " + addrName + " not detected.", addr
                    .isLoopbackAddress());

            addrName = "42.42.42.42"; // a loopback address should be
            // 127.d.d.d
            addr = InetAddress.getByName(addrName);
            assertTrue("Address incorrectly " + addrName
                    + " detected as a loopback address.", !addr
                    .isLoopbackAddress());

        } catch (Exception e) {
            fail("Unknown address : " + addrName);
        }

    }

    /**
     * @tests java.net.Inet4Address#isLinkLocalAddress()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isLinkLocalAddress",
        args = {}
    )
    public void test_isLinkLocalAddress() {

        String addrName = "";
        try {
            // There are no link local addresses for IPv4
            // We'll test one to ensure we get "false"

            addrName = "42.42.42.42";
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 address " + addrName
                    + " incorrectly reporting as a link local address.", !addr
                    .isLinkLocalAddress());
        } catch (Exception e) {
            fail("Unknown address : " + e.getMessage());
        }
    }

    /**
     * @tests java.net.Inet4Address#isSiteLocalAddress()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isSiteLocalAddress",
        args = {}
    )
    public void test_isSiteLocalAddress() {
        String addrName = "";
        try {
            // There are no site local addresses for IPv4
            // We'll test one to ensure we get "false"

            addrName = "42.42.42.42";
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 address " + addrName
                    + " incorrectly reporting as a site local address.", !addr
                    .isSiteLocalAddress());
        } catch (Exception e) {
            fail("Unknown address : " + e.getMessage());
        }
    }

    /**
     * @tests java.net.Inet4Address#isMCGlobal()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isMCGlobal",
        args = {}
    )
    public void test_isMCGlobal() {

        // Create an IPv4 mulitcast address. It should return
        // false for globabl mutlicast. There are no valid IPv4
        // global multicast addresses

        String addrName = "";
        try {
            addrName = "224.0.0.0"; // a multicast addr 1110
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 link-local multicast address " + addrName
                    + " incorrectly identified as a global multicast address.",
                    !addr.isMCGlobal());

            addrName = "224.0.0.255"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 link-local multicast address " + addrName
                    + " incorrectly identified as a global multicast address.",
                    !addr.isMCGlobal());

            addrName = "224.0.1.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 global multicast address " + addrName
                    + " not identified as a global multicast address.", addr
                    .isMCGlobal());

            addrName = "238.255.255.255"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 global multicast address " + addrName
                    + " not identified as a global multicast address.", addr
                    .isMCGlobal());

            addrName = "239.0.0.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 reserved multicast address " + addrName
                    + " incorrectly identified as a global multicast address.",
                    !addr.isMCGlobal());

            addrName = "239.191.255.255"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 reserved multicast address " + addrName
                    + " incorrectly identified as a global multicast address.",
                    !addr.isMCGlobal());

        } catch (Exception e) {
            fail("Unknown address : " + e.getMessage());
        }
    }

    /**
     * @tests java.net.Inet4Address#isMCNodeLocal()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isMCNodeLocal",
        args = {}
    )
    public void test_isMCNodeLocal() {
        // Create an IPv4 mulitcast address. It should return
        // false for node-local mutlicast. There are no valid IPv4
        // node-local multicast addresses

        String addrName = "";
        try {
            addrName = "224.42.42.42"; // a multicast addr 1110 = 224
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue(
                    "IPv4 multicast address "
                            + addrName
                            + " incorrectly identified as a node-local multicast address.",
                    !addr.isMCNodeLocal());

            addrName = "239.0.0.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue(
                    "IPv4 reserved multicast address "
                            + addrName
                            + " incorrectly identified as a node-local multicast address.",
                    !addr.isMCNodeLocal());

        } catch (Exception e) {
            fail("Unknown address : " + e.getMessage());
        }
    }

    /**
     * @tests java.net.Inet4Address#isMCLinkLocal()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isMCLinkLocal",
        args = {}
    )
    public void test_isMCLinkLocal() {
        // Create an IPv4 mulitcast address. It should return
        // false for link-local mutlicast. There are no valid IPv4
        // link-local multicast addresses

        String addrName = "";
        try {
            addrName = "224.0.0.0"; // a multicast addr 1110
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 link-local multicast address " + addrName
                    + " not identified as a link-local multicast address.",
                    addr.isMCLinkLocal());

            addrName = "224.0.0.255"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 link-local multicast address " + addrName
                    + " not identified as a link-local multicast address.",
                    addr.isMCLinkLocal());

            addrName = "224.0.1.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue(
                    "IPv4 global multicast address "
                            + addrName
                            + " incorrectly identified as a link-local multicast address.",
                    !addr.isMCLinkLocal());

            addrName = "239.0.0.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue(
                    "IPv4 reserved multicast address "
                            + addrName
                            + " incorrectly identified as a link-local multicast address.",
                    !addr.isMCLinkLocal());

        } catch (Exception e) {
            fail("Unknown address : " + addrName);
        }
    }

    /**
     * @tests java.net.Inet4Address#isMCSiteLocal()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isMCSiteLocal",
        args = {}
    )
    public void test_isMCSiteLocal() {
        // Create an IPv4 mulitcast address. It should return
        // false for site-local mutlicast. There are no valid IPv4
        // site-local multicast addresses

        String addrName = "";
        try {
            addrName = "240.0.0.0"; // a multicast addr 1110 = 224
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue(
                    "IPv4 multicast address "
                            + addrName
                            + " incorrectly identified as a site-local multicast address.",
                    !addr.isMCSiteLocal());

            addrName = "239.0.0.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue(
                    "IPv4 reserved multicast address "
                            + addrName
                            + " incorrectly identified as a site-local multicast address.",
                    !addr.isMCSiteLocal());

            addrName = "239.255.0.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 site-local multicast address " + addrName
                    + " not identified as a site-local multicast address.",
                    addr.isMCSiteLocal());

            addrName = "239.255.255.255"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 site-local multicast address " + addrName
                    + " not identified as a site-local multicast address.",
                    addr.isMCSiteLocal());

            addrName = "239.255.2.2"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 site-local multicast address " + addrName
                    + " not identified as a site-local multicast address.",
                    addr.isMCSiteLocal());

        } catch (Exception e) {
            fail("Unknown address : " + addrName);
        }
    }

    /**
     * @tests java.net.Inet4Address#isMCOrgLocal()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isMCOrgLocal",
        args = {}
    )
    public void test_isMCOrgLocal() {
        // Create an IPv4 mulitcast address. It should return
        // false for organization-local mutlicast. There are no valid IPv4
        // organization-local multicast addresses

        String addrName = "";
        try {

            addrName = "239.191.255.255"; // a multicast addr 1110
            InetAddress addr = InetAddress.getByName(addrName);
            assertTrue(
                    "IPv4 reserved multicast address "
                            + addrName
                            + " incorrectly identified as a org-local multicast address.",
                    !addr.isMCOrgLocal());

            addrName = "239.252.0.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue(
                    "IPv4 site-local multicast address "
                            + addrName
                            + " incorrectly identified as a org-local multicast address.",
                    !addr.isMCOrgLocal());

            addrName = "239.192.0.0"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 org-local multicast address " + addrName
                    + " not identified as a org-local multicast address.", addr
                    .isMCOrgLocal());

            addrName = "239.195.255.255"; // a multicast addr 1110
            addr = InetAddress.getByName(addrName);
            assertTrue("IPv4 org-local multicast address " + addrName
                    + " not identified as a org-local multicast address.", addr
                    .isMCOrgLocal());

        } catch (Exception e) {
            fail("Unknown address : " + addrName);
        }
    }

    // comparator for Inet4Address objects
    private static final SerializableAssert COMPARATOR = new SerializableAssert() {
        public void assertDeserialized(Serializable initial,
                Serializable deserialized) {

            Inet4Address initAddr = (Inet4Address) initial;
            Inet4Address desrAddr = (Inet4Address) deserialized;

            byte[] iaAddresss = initAddr.getAddress();
            byte[] deIAAddresss = desrAddr.getAddress();
            for (int i = 0; i < iaAddresss.length; i++) {
                assertEquals(iaAddresss[i], deIAAddresss[i]);
            }
            assertEquals(4, deIAAddresss.length);
            assertEquals(initAddr.getHostName(), desrAddr.getHostName());
        }
    };

    /**
     * @tests serialization/deserialization compatibility.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Checks serialization",
        method = "!SerializationSelf",
        args = {}
    )
    public void testSerializationSelf() throws Exception {
        SerializationTest.verifySelf(ipv4LoopbackIp, COMPARATOR);
    }

    /**
     * @tests serialization/deserialization compatibility with RI.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Checks serialization",
        method = "!SerializationGolden",
        args = {}
    )
    public void testSerializationCompatibility() throws Exception {
        SerializationTest.verifyGolden(this, ipv4Localhost, COMPARATOR);
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "equals",
        args = {java.lang.Object.class}
    )
    public void test_equals() throws Exception {
        InetAddress addr = InetAddress.getByName("239.191.255.255");
        assertTrue(addr.equals(addr));
        assertTrue(ipv4LoopbackIp.equals(ipv4Localhost));
        assertFalse(addr.equals(ipv4LoopbackIp));

        InetAddress addr3 = InetAddress.getByName("127.0.0");
        assertFalse(ipv4LoopbackIp.equals(addr3));
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "getHostAddress",
        args = {}
    )
    public void test_getHostAddress() throws Exception {
        assertEquals("127.0.0.1", ipv4Localhost.getHostAddress());
        assertEquals("127.0.0.1", ipv4LoopbackIp.getHostAddress());

        InetAddress addr = InetAddress.getByName("224.0.0.0");
        assertEquals("224.0.0.0", addr.getHostAddress());

        addr = InetAddress.getByName("1");
        assertEquals("0.0.0.1", addr.getHostAddress());

        addr = InetAddress.getByName("1.1");
        assertEquals("1.0.0.1", addr.getHostAddress());

        addr = InetAddress.getByName("1.1.1");
        assertEquals("1.1.0.1", addr.getHostAddress());
    }

    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "hashCode",
        args = {}
    )
    public void test_hashCode() throws Exception {
        InetAddress addr1 = InetAddress.getByName("1.1");
        InetAddress addr2 = InetAddress.getByName("1.1.1");
        assertFalse(addr1.hashCode() == addr2.hashCode());

        addr2 = InetAddress.getByName("1.0.0.1");
        assertTrue(addr1.hashCode() == addr2.hashCode());

        assertTrue(ipv4LoopbackIp.hashCode() == ipv4Localhost.hashCode());
    }
}
