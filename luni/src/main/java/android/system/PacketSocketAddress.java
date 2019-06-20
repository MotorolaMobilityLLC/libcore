/*
 * Copyright (C) 2015 The Android Open Source Project
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

package android.system;

import dalvik.annotation.compat.UnsupportedAppUsage;
import libcore.util.Objects;
import java.net.SocketAddress;

/**
 * Packet socket address.
 *
 * Corresponds to Linux's {@code struct sockaddr_ll}.
 *
 * @hide
 */
@libcore.api.CorePlatformApi
public final class PacketSocketAddress extends SocketAddress {
    /** Protocol. An Ethernet protocol type, e.g., {@code ETH_P_IPV6}. */
    public final short sll_protocol;

    /** Interface index. */
    public final int sll_ifindex;

    /** ARP hardware type. One of the {@code ARPHRD_*} constants. */
    public final short sll_hatype;

    /** Packet type. One of the {@code PACKET_*} constants, such as {@code PACKET_OTHERHOST}. */
    public final byte sll_pkttype;

    /** Hardware address. */
    public final byte[] sll_addr;

    /** Constructs a new PacketSocketAddress. Used from native code. */
    public PacketSocketAddress(short sll_protocol, int sll_ifindex, short sll_hatype,
            byte sll_pkttype, byte[] sll_addr) {
        this.sll_protocol = sll_protocol;
        this.sll_ifindex = sll_ifindex;
        this.sll_hatype = sll_hatype;
        this.sll_pkttype = sll_pkttype;
        this.sll_addr = sll_addr;
    }

    /** Constructs a new PacketSocketAddress with all the "in" parameters. */
    @libcore.api.CorePlatformApi
    public PacketSocketAddress(short sll_protocol, int sll_ifindex, byte[] sll_addr) {
        this.sll_protocol = sll_protocol;
        this.sll_ifindex = sll_ifindex;
        this.sll_hatype = (short) 0;
        this.sll_pkttype = (byte) 0;
        this.sll_addr = sll_addr;
    }

    /** Legacy constructor. Kept for @UnsupportedAppUsage only. */
    @UnsupportedAppUsage
    public PacketSocketAddress(short sll_protocol, int sll_ifindex) {
        this.sll_protocol = sll_protocol;
        this.sll_ifindex = sll_ifindex;
        this.sll_hatype = (short) 0;
        this.sll_pkttype = (byte) 0;
        this.sll_addr = null;
    }

    /** Legacy constructor. Kept for @UnsupportedAppUsage only. */
    @UnsupportedAppUsage
    public PacketSocketAddress(int sll_ifindex, byte[] sll_addr) {
        this.sll_protocol = (short) 0;
        this.sll_ifindex = sll_ifindex;
        this.sll_hatype = (short) 0;
        this.sll_pkttype = (byte) 0;
        this.sll_addr = sll_addr;
    }

    @Override
    public String toString() {
        return Objects.toString(this);
    }
}
