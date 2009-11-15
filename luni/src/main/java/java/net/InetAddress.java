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

package java.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.harmony.luni.net.NetUtil;
import org.apache.harmony.luni.platform.INetworkSystem;
import org.apache.harmony.luni.platform.Platform;
import org.apache.harmony.luni.util.Inet6Util;
import org.apache.harmony.luni.util.Msg;
import org.apache.harmony.luni.util.PriviAction;

/**
 * The Internet Protocol (IP) address representation class. This class
 * encapsulates an IP address and provides name and reverse name resolution
 * functions. The address is stored in network order, but as a signed (rather
 * than unsigned) integer.
 */
public class InetAddress extends Object implements Serializable {

    private final static INetworkSystem NETIMPL = Platform.getNetworkSystem();

    private static final String ERRMSG_CONNECTION_REFUSED = "Connection refused"; //$NON-NLS-1$

    private static final long serialVersionUID = 3286316764910316507L;

    // BEGIN android-added
    /**
     * default time-to-live for DNS cache entries; 600 seconds == 10 minutes
     */
    private static final String DEFAULT_NETADDR_CACHE_TTL_SECS = "600";
    // END android-added

    String hostName;

    private static class WaitReachable {
    }

    private transient Object waitReachable = new WaitReachable();

    private boolean reached;

    private int addrCount;

    int family = 0;
    static final int AF_INET = 2;
    static final int AF_INET6 = 10;

    byte[] ipaddress;

    // BEGIN android-removed
    // // Fill in the JNI id caches
    // private static native void oneTimeInitialization(boolean supportsIPv6);
    //
    // static {
    //     oneTimeInitialization(true);
    // }
    // END android-removed

    /**
     * Constructs an {@code InetAddress}.
     *
     * Note: this constructor should not be used. Creating an InetAddress
     * without specifying whether it's an IPv4 or IPv6 address does not make
     * sense, because subsequent code cannot know which of of the subclasses'
     * methods need to be called to implement a given InetAddress method. The
     * proper way to create an InetAddress is to call new Inet4Address or
     * Inet6Address or to use one of the static methods that return
     * InetAddresses (e.g., getByAddress). That is why the API does not have
     * public constructors for any of these classes.
     */
    InetAddress() {
        super();
    }

    // BEGIN android-removed
    /**
     * Constructs an {@code InetAddress}, representing the {@code address} and
     * {@code hostName}.
     *
     * @param address
     *            the network address.
     */
    // InetAddress(byte[] address) {
    //     super();
    //     this.ipaddress = address;
    // }
    // END android-removed

    // BEGIN android-removed
    /**
     * Constructs an {@code InetAddress}, representing the {@code address} and
     * {@code hostName}.
     *
     * @param address
     *            the network address.
     *
     */
    // InetAddress(byte[] address, String hostName) {
    //     super();
    //     this.ipaddress = address;
    //     this.hostName = hostName;
    // }
    // END android-removed

    // BEGIN android-removed
    // CacheElement cacheElement() {
    //     return new CacheElement();
    // }
    // END android-removed

    /**
     * Compares this {@code InetAddress} instance against the specified address
     * in {@code obj}. Two addresses are equal if their address byte arrays have
     * the same length and if the bytes in the arrays are equal.
     *
     * @param obj
     *            the object to be tested for equality.
     * @return {@code true} if both objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        // BEGIN android-changed
        if (!(obj instanceof InetAddress)) {
            return false;
        }
        return Arrays.equals(this.ipaddress, ((InetAddress) obj).ipaddress);
        // END android-changed
    }

    /**
     * Returns the IP address represented by this {@code InetAddress} instance
     * as a byte array. The elements are in network order (the highest order
     * address byte is in the zeroth element).
     *
     * @return the address in form of a byte array.
     */
    public byte[] getAddress() {
        return ipaddress.clone();
    }

    // BEGIN android-added
    static final Comparator<byte[]> SHORTEST_FIRST = new Comparator<byte[]>() {
        public int compare(byte[] a1, byte[] a2) {
            return a1.length - a2.length;
        }
    };

    static final Comparator<byte[]> LONGEST_FIRST = new Comparator<byte[]>() {
        public int compare(byte[] a1, byte[] a2) {
            return a2.length - a1.length;
        }
    };

    /**
     * Converts an array of byte arrays representing raw IP addresses of a host
     * to an array of InetAddress objects, sorting to respect the value of the
     * system preferIPv6Addresses preference.
     *
     * @param rawAddresses the raw addresses to convert.
     * @param hostName the hostname corresponding to the IP address.
     * @return the corresponding InetAddresses, appropriately sorted.
     */
    static InetAddress[] bytesToInetAddresses(byte[][] rawAddresses,
            String hostName) {
        // Sort the raw byte arrays.
        Comparator<byte[]> comparator = preferIPv6Addresses()
                ? LONGEST_FIRST : SHORTEST_FIRST;
        Arrays.sort(rawAddresses, comparator);

        // Convert the byte arrays to InetAddresses.
        InetAddress[] returnedAddresses = new InetAddress[rawAddresses.length];
        for (int i = 0; i < rawAddresses.length; i++) {
            byte[] rawAddress = rawAddresses[i];
            if (rawAddress.length == 16) {
                returnedAddresses[i] = new Inet6Address(rawAddress, hostName);
            } else if (rawAddress.length == 4) {
                returnedAddresses[i] = new Inet4Address(rawAddress, hostName);
            } else {
              // Cannot happen, because the underlying code only returns
              // addresses that are 4 or 16 bytes long.
              throw new AssertionError("Impossible address length " +
                                       rawAddress.length);
            }
        }
        return returnedAddresses;
    }
    // END android-added

    /**
     * Gets all IP addresses associated with the given {@code host} identified
     * by name or literal IP address. The IP address is resolved by the
     * configured name service. If the host name is empty or {@code null} an
     * {@code UnknownHostException} is thrown. If the host name is a literal IP
     * address string an array with the corresponding single {@code InetAddress}
     * is returned.
     *
     * @param host the hostname or literal IP string to be resolved.
     * @return the array of addresses associated with the specified host.
     * @throws UnknownHostException if the address lookup fails.
     */
    public static InetAddress[] getAllByName(String host)
            throws UnknownHostException {
        // BEGIN android-changed
        return getAllByNameImpl(host, true);
        // END android-changed
    }

    // BEGIN android-added
    /**
     * Implementation of getAllByName.
     *
     * @param host the hostname or literal IP string to be resolved.
     * @param returnUnshared requests a result that is modifiable by the caller.
     * @return the array of addresses associated with the specified host.
     * @throws UnknownHostException if the address lookup fails.
     */
    static InetAddress[] getAllByNameImpl(String host, boolean returnUnshared)
            throws UnknownHostException {
        if (host == null || 0 == host.length()) {
            if (preferIPv6Addresses()) {
                return new InetAddress[] { Inet6Address.LOOPBACK,
                                           Inet4Address.LOOPBACK };
            } else {
                return new InetAddress[] { Inet4Address.LOOPBACK,
                                           Inet6Address.LOOPBACK };
            }
        }

        // Special-case "0" for legacy IPv4 applications.
        if (host.equals("0")) { //$NON-NLS-1$
            return new InetAddress[] { Inet4Address.ANY };
        }

        if (isHostName(host)) {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkConnect(host, -1);
            }
            if (returnUnshared) {
                return lookupHostByName(host).clone();
            } else {
                return lookupHostByName(host);
            }
        }

        byte[] hBytes = NETIMPL.ipStringToByteArray(host);
        if (hBytes.length == 4) {
            return (new InetAddress[] { new Inet4Address(hBytes) });
        } else if (hBytes.length == 16) {
            return (new InetAddress[] { new Inet6Address(hBytes) });
        } else {
            throw new UnknownHostException(
                    Msg.getString("K0339")); //$NON-NLS-1$
        }
    }
    // END android-added

    /**
     * Returns the address of a host according to the given host string name
     * {@code host}. The host string may be either a machine name or a dotted
     * string IP address. If the latter, the {@code hostName} field is
     * determined upon demand. {@code host} can be {@code null} which means that
     * an address of the loopback interface is returned.
     *
     * @param host
     *            the hostName to be resolved to an address or {@code null}.
     * @return the {@code InetAddress} instance representing the host.
     * @throws UnknownHostException
     *             if the address lookup fails.
     */
    public static InetAddress getByName(String host) throws UnknownHostException {
        return getAllByNameImpl(host, false)[0];
    }

    // BEGIN android-added
    /**
     * Convenience method to convert a byte array to a string, converting
     * exceptions to runtime exceptions. This is used when passing in byte
     * arrays that have been verified to be correct and is necessary because
     * some methods, such as getHostName(), are not allowed to throw exceptions
     * but are implemented using byteArrayToIpString(), which throws
     * UnknownHostException. Exceptions should never occur when the address is
     * valid, but they cannot be simply ignored because they could be due to
     * runtime errors such as out-of-memory conditions.
     *
     * @param ipaddress the byte array to convert
     */
    private static String ipAddressToString(byte[] ipaddress) {
        try {
            return NETIMPL.byteArrayToIpString(ipaddress);
        } catch(UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
    // END android-added

    /**
     * Gets the textual representation of this IP address.
     *
     * @return the textual representation of host's IP address.
     */
    public String getHostAddress() {
        return ipAddressToString(ipaddress);
    }

    /**
     * Gets the host name of this IP address. If the IP address could not be
     * resolved, the textual representation in a dotted-quad-notation is
     * returned.
     *
     * @return the corresponding string name of this IP address.
     */
    public String getHostName() {
        try {
            if (hostName == null) {
                int address = 0;
                if (ipaddress.length == 4) {
                    address = bytesToInt(ipaddress, 0);
                    if (address == 0) {
                        return hostName = ipAddressToString(ipaddress);
                    }
                }
                hostName = getHostByAddrImpl(ipaddress).hostName;
                if (hostName.equals("localhost") && ipaddress.length == 4 //$NON-NLS-1$
                        && address != 0x7f000001) {
                    return hostName = ipAddressToString(ipaddress);
                }
            }
        } catch (UnknownHostException e) {
            return hostName = ipAddressToString(ipaddress);
        }
        SecurityManager security = System.getSecurityManager();
        try {
            // Only check host names, not addresses
            if (security != null && isHostName(hostName)) {
                security.checkConnect(hostName, -1);
            }
        } catch (SecurityException e) {
            return ipAddressToString(ipaddress);
        }
        return hostName;
    }

    /**
     * Gets the fully qualified domain name for the host associated with this IP
     * address. If a security manager is set, it is checked if the method caller
     * is allowed to get the hostname. Otherwise, the textual representation in
     * a dotted-quad-notation is returned.
     *
     * @return the fully qualified domain name of this IP address.
     */
    public String getCanonicalHostName() {
        String canonicalName;
        try {
            int address = 0;
            if (ipaddress.length == 4) {
                address = bytesToInt(ipaddress, 0);
                if (address == 0) {
                    return ipAddressToString(ipaddress);
                }
            }
            canonicalName = getHostByAddrImpl(ipaddress).hostName;
        } catch (UnknownHostException e) {
            return ipAddressToString(ipaddress);
        }
        SecurityManager security = System.getSecurityManager();
        try {
            // Only check host names, not addresses
            if (security != null && isHostName(canonicalName)) {
                security.checkConnect(canonicalName, -1);
            }
        } catch (SecurityException e) {
            return ipAddressToString(ipaddress);
        }
        return canonicalName;
    }

    /**
     * Gets the local host address if the security policy allows this.
     * Otherwise, gets the loopback address which allows this machine to be
     * contacted.
     *
     * @return the {@code InetAddress} representing the local host.
     * @throws UnknownHostException
     *             if the address lookup fails.
     */
    public static InetAddress getLocalHost() throws UnknownHostException {
        String host = getHostNameImpl();
        SecurityManager security = System.getSecurityManager();
        try {
            if (security != null) {
                security.checkConnect(host, -1);
            }
        } catch (SecurityException e) {
            return Inet4Address.LOOPBACK;
        }
        return lookupHostByName(host)[0];
    }

    /**
     * Gets the hashcode of the represented IP address.
     *
     * @return the appropriate hashcode value.
     */
    @Override
    public int hashCode() {
        // BEGIN android-changed
        return Arrays.hashCode(ipaddress);
        // END android-changed
    }

    // BEGIN android-changed
    /*
     * Returns whether this address is an IP multicast address or not. This
     * implementation returns always {@code false}.
     *
     * @return {@code true} if this address is in the multicast group, {@code
     *         false} otherwise.
     */
    public boolean isMulticastAddress() {
        return false;
    }
    // END android-changed


    /**
     * Resolves a hostname to its IP addresses using a cache for faster lookups.
     *
     * @param host the hostname to resolve.
     * @return the IP addresses of the host.
     */
    static synchronized InetAddress[] lookupHostByName(String host)
            throws UnknownHostException {
        int ttl = -1;

        // BEGIN android-changed
        String ttlValue = AccessController
                .doPrivileged(new PriviAction<String>(
                        "networkaddress.cache.ttl", DEFAULT_NETADDR_CACHE_TTL_SECS)); //$NON-NLS-1$
        // END android-changed
        try {
            if (ttlValue != null) {
                ttl = Integer.decode(ttlValue).intValue();
            }
        } catch (NumberFormatException e) {
            // Ignored
        }
        CacheElement element = null;
        // BEGIN android-changed
        if (ttl == 0) {
            Cache.clear();
        } else {
            element = Cache.get(host);
            if (element != null && ttl > 0) {
                long delta = System.nanoTime() - element.nanoTimeAdded;
                if (delta > secondsToNanos(ttl)) {
                    element = null;
                }
            }
        }
        if (element != null) {
            return element.addresses();
        }
        // END android-changed

        // TODO Clean up NegativeCache; there's no need to maintain the failure message

        // now try the negative cache
        String failedMessage = NegativeCache.getFailedMessage(host);
        if (failedMessage != null) {
            throw new UnknownHostException(host);
        }

        // BEGIN android-changed
        // TODO: Avoid doing I/O from a static synchronized lock.
        byte[][] rawAddresses;
        try {
            rawAddresses = getallbyname(host, Socket.preferIPv4Stack());
        } catch (UnknownHostException e) {
            // put the entry in the negative cache
            NegativeCache.put(host, e.getMessage());
            // use host for message to match RI, save the cause for giggles
            throw (UnknownHostException)new UnknownHostException(host).initCause(e);
        }

        InetAddress[] addresses = bytesToInetAddresses(rawAddresses, host);

        Cache.add(host, addresses);
        return addresses;
        // END android-changed
    }

    // BEGIN android-added
    /**
     * Multiplies value by 1 billion.
     */
    private static long secondsToNanos(int ttl) {
        return (long) ttl * 1000000000;
    }
    // END android-added

    // BEGIN android-deleted
    // static native InetAddress[] getAliasesByNameImpl(String name)
    //     throws UnknownHostException;
    // END android-deleted

    // BEGIN android-added
    /**
     * Resolves a host name to its IP addresses. Thread safe.
     */
    private static native byte[][] getallbyname(String name,
            boolean preferIPv4Stack) throws UnknownHostException;
    // END android-added

    /**
     * Query the IP stack for the host address. The host is in address form.
     *
     * @param addr
     *            the host address to lookup.
     * @throws UnknownHostException
     *             if an error occurs during lookup.
     */
    // BEGIN android-changed
    // static native InetAddress getHostByAddrImpl(byte[] addr)
    //    throws UnknownHostException;
    static InetAddress getHostByAddrImpl(byte[] addr)
            throws UnknownHostException {
        if (addr.length == 4) {
            return new Inet4Address(addr, gethostbyaddr(addr));
        } else if (addr.length == 16) {
            return new Inet6Address(addr, gethostbyaddr(addr));
        } else {
            throw new UnknownHostException(Msg.getString(
                    "K0339")); //$NON-NLS-1$
        }
    }

    /**
     * Resolves an IP address to a hostname. Thread safe.
     */
    private static native String gethostbyaddr(byte[] addr);
    // END android-changed

    // BEGIN android-removed
    // static int inetAddr(String host) throws UnknownHostException
    // END android-removed

    /**
     * Convert a string containing an IPv4 Internet Protocol dotted address into
     * a binary address. Note, the special case of '255.255.255.255' throws an
     * exception, so this value should not be used as an argument. See also
     * inetAddr(String).
     */
    // BEGIN android-removed
    // static native int inetAddrImpl(String host) throws UnknownHostException;
    // END android-removed

    /**
     * Convert a binary address into a string containing an Ipv4 Internet
     * Protocol dotted address.
     */
    // BEGIN android-removed
    // static native String inetNtoaImpl(int hipAddr);
    // END android-removed

    // BEGIN android-removed
    /**
     * Query the IP stack for the host address. The host is in string name form.
     *
     * @param name
     *            the host name to lookup
     * @param preferIPv6Address
     *            address preference if underlying platform is V4/V6
     * @return InetAddress the host address
     * @throws UnknownHostException
     *             if an error occurs during lookup
     */
    // static native InetAddress getHostByNameImpl(String name,
    //         boolean preferIPv6Address) throws UnknownHostException;
    // END android-removed

    /**
     * Gets the host name of the system.
     *
     * @return String the system hostname
     */
    // BEGIN android-changed
    static String getHostNameImpl() {
        // TODO Mapped Harmony to Android native. Get rid of indirection later.

        return gethostname();
    }
    static native String gethostname();
    // END android-changed

    static String getHostNameInternal(String host) throws UnknownHostException {
        if (host == null || 0 == host.length()) {
            return Inet4Address.LOOPBACK.getHostAddress();
        }
        if (isHostName(host)) {
            return lookupHostByName(host)[0].getHostAddress();
        }
        return host;
    }

    /**
     * Returns a string containing a concise, human-readable description of this
     * IP address.
     *
     * @return the description, as host/address.
     */
    @Override
    public String toString() {
        return (hostName == null ? "" : hostName) + "/" + getHostAddress(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    // BEGIN android-changed
    // Partly copied from a newer version of harmony
    static class CacheElement {
        final long nanoTimeAdded = System.nanoTime();

        CacheElement next;
        final String hostName;
        final InetAddress[] addresses;

        CacheElement(String hostName, InetAddress[] addresses) {
            this.addresses = addresses;
            this.hostName = hostName;
        }

        String hostName() {
            return hostName;
        }

        InetAddress[] addresses() {
            return addresses;
        }
        // END android-changed
    }

    static class Cache {
        private static int maxSize = 5;

        private static int size = 0;

        private static CacheElement head;

        static synchronized void clear() {
            size = 0;
            head = null;
        }

        static synchronized void add(String hostName, InetAddress[] addresses) {
            CacheElement newElement = new CacheElement(hostName, addresses);
            if (size < maxSize) {
                size++;
            } else {
                deleteTail();
            }
            newElement.next = head; // If the head is null, this does no harm.
            head = newElement;
        }

        static synchronized CacheElement get(String name) {
            CacheElement previous = null;
            CacheElement current = head;
            boolean notFound = true;
            while ((null != current)
                    && (notFound = !(name.equals(current.hostName())))) {
                previous = current;
                current = current.next;
            }
            if (notFound) {
                return null;
            }
            moveToHead(current, previous);
            return current;
        }

        private synchronized static void deleteTail() {
            if (0 == size) {
                return;
            }
            if (1 == size) {
                head = null;
            }

            CacheElement previous = null;
            CacheElement current = head;
            while (null != current.next) {
                previous = current;
                current = current.next;
            }
            previous.next = null;
        }

        private synchronized static void moveToHead(CacheElement element,
                CacheElement elementPredecessor) {
            if (null == elementPredecessor) {
                head = element;
            } else {
                elementPredecessor.next = element.next;
                element.next = head;
                head = element;
            }
        }
    }

    /**
     * Returns true if the string is a host name, false if it is an IP Address.
     */
    private static boolean isHostName(String value) {
        try {
            NETIMPL.ipStringToByteArray(value);
            return false;
        } catch (UnknownHostException e) {
            return true;
        }
    }

    /**
     * Returns whether this address is a loopback address or not. This
     * implementation returns always {@code false}. Valid IPv4 loopback
     * addresses are 127.d.d.d The only valid IPv6 loopback address is ::1.
     *
     * @return {@code true} if this instance represents a loopback address,
     *         {@code false} otherwise.
     */
    public boolean isLoopbackAddress() {
        return false;
    }

    /**
     * Returns whether this address is a link-local address or not. This
     * implementation returns always {@code false}.
     * <p>
     * Valid IPv6 link-local addresses are FE80::0 through to
     * FEBF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF.
     * <p>
     * There are no valid IPv4 link-local addresses.
     *
     * @return {@code true} if this instance represents a link-local address,
     *         {@code false} otherwise.
     */
    public boolean isLinkLocalAddress() {
        return false;
    }

    /**
     * Returns whether this address is a site-local address or not. This
     * implementation returns always {@code false}.
     * <p>
     * Valid IPv6 site-local addresses are FEC0::0 through to
     * FEFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF.
     * <p>
     * There are no valid IPv4 site-local addresses.
     *
     * @return {@code true} if this instance represents a site-local address,
     *         {@code false} otherwise.
     */
    public boolean isSiteLocalAddress() {
        return false;
    }

    /**
     * Returns whether this address is a global multicast address or not. This
     * implementation returns always {@code false}.
     * <p>
     * Valid IPv6 link-global multicast addresses are FFxE:/112 where x is a set
     * of flags, and the additional 112 bits make up the global multicast
     * address space.
     * <p>
     * Valid IPv4 global multicast addresses are between: 224.0.1.0 to
     * 238.255.255.255.
     *
     * @return {@code true} if this instance represents a global multicast
     *         address, {@code false} otherwise.
     */
    public boolean isMCGlobal() {
        return false;
    }

    /**
     * Returns whether this address is a node-local multicast address or not.
     * This implementation returns always {@code false}.
     * <p>
     * Valid IPv6 node-local multicast addresses are FFx1:/112 where x is a set
     * of flags, and the additional 112 bits make up the node-local multicast
     * address space.
     * <p>
     * There are no valid IPv4 node-local multicast addresses.
     *
     * @return {@code true} if this instance represents a node-local multicast
     *         address, {@code false} otherwise.
     */
    public boolean isMCNodeLocal() {
        return false;
    }

    /**
     * Returns whether this address is a link-local multicast address or not.
     * This implementation returns always {@code false}.
     * <p>
     * Valid IPv6 link-local multicast addresses are FFx2:/112 where x is a set
     * of flags, and the additional 112 bits make up the link-local multicast
     * address space.
     * <p>
     * Valid IPv4 link-local addresses are between: 224.0.0.0 to 224.0.0.255
     *
     * @return {@code true} if this instance represents a link-local multicast
     *         address, {@code false} otherwise.
     */
    public boolean isMCLinkLocal() {
        return false;
    }

    /**
     * Returns whether this address is a site-local multicast address or not.
     * This implementation returns always {@code false}.
     * <p>
     * Valid IPv6 site-local multicast addresses are FFx5:/112 where x is a set
     * of flags, and the additional 112 bits make up the site-local multicast
     * address space.
     * <p>
     * Valid IPv4 site-local addresses are between: 239.252.0.0 to
     * 239.255.255.255
     *
     * @return {@code true} if this instance represents a site-local multicast
     *         address, {@code false} otherwise.
     */
    public boolean isMCSiteLocal() {
        return false;
    }

    /**
     * Returns whether this address is a organization-local multicast address or
     * not. This implementation returns always {@code false}.
     * <p>
     * Valid IPv6 organization-local multicast addresses are FFx8:/112 where x
     * is a set of flags, and the additional 112 bits make up the
     * organization-local multicast address space.
     * <p>
     * Valid IPv4 organization-local addresses are between: 239.192.0.0 to
     * 239.251.255.255
     *
     * @return {@code true} if this instance represents a organization-local
     *         multicast address, {@code false} otherwise.
     */
    public boolean isMCOrgLocal() {
        return false;
    }

    /**
     * Returns whether this is a wildcard address or not. This implementation
     * returns always {@code false}.
     *
     * @return {@code true} if this instance represents a wildcard address,
     *         {@code false} otherwise.
     */
    public boolean isAnyLocalAddress() {
        return false;
    }

    /**
     * Tries to reach this {@code InetAddress}. This method first tries to use
     * ICMP <i>(ICMP ECHO REQUEST)</i>. When first step fails, a TCP connection
     * on port 7 (Echo) of the remote host is established.
     *
     * @param timeout
     *            timeout in milliseconds before the test fails if no connection
     *            could be established.
     * @return {@code true} if this address is reachable, {@code false}
     *         otherwise.
     * @throws IOException
     *             if an error occurs during an I/O operation.
     * @throws IllegalArgumentException
     *             if timeout is less than zero.
     */
    public boolean isReachable(int timeout) throws IOException {
        return isReachable(null, 0, timeout);
    }

    /**
     * Tries to reach this {@code InetAddress}. This method first tries to use
     * ICMP <i>(ICMP ECHO REQUEST)</i>. When first step fails, a TCP connection
     * on port 7 (Echo) of the remote host is established.
     *
     * @param netif
     *            the network interface on which to connection should be
     *            established.
     * @param ttl
     *            the maximum count of hops (time-to-live).
     * @param timeout
     *            timeout in milliseconds before the test fails if no connection
     *            could be established.
     * @return {@code true} if this address is reachable, {@code false}
     *         otherwise.
     * @throws IOException
     *             if an error occurs during an I/O operation.
     * @throws IllegalArgumentException
     *             if ttl or timeout is less than zero.
     */
    public boolean isReachable(NetworkInterface netif, final int ttl,
            final int timeout) throws IOException {
        if (0 > ttl || 0 > timeout) {
            throw new IllegalArgumentException(Msg.getString("K0051")); //$NON-NLS-1$
        }
        boolean reachable = false;
        if (null == netif) {
            // network interface is null, binds to no address
            // BEGIN android-changed
            // reachable = NETIMPL.isReachableByICMP(this, null, ttl, timeout);
            // if (!reachable) {
                reachable = isReachableByTCP(this, null, timeout);
            // }
            // END android-changed
        } else {
            // Not Bind to any address
            if (null == netif.addresses) {
                return false;
            }
            // binds to all address on this NetworkInterface, tries ICMP ping
            // first
            // BEGIN android-changed
            // reachable = isReachableByICMPUseMultiThread(netif, ttl, timeout);
            // if (!reachable) {
                // tries TCP echo if ICMP ping fails
                reachable = isReachableByMultiThread(netif, ttl, timeout);
            // }
            // END adnroid-changed
        }
        return reachable;
    }

    /*
     * Uses multi-Thread to try if isReachable, returns true if any of threads
     * returns in time
     */
    // BEGIN android-changed
    private boolean isReachableByMultiThread(NetworkInterface netif,
            final int ttl, final int timeout)
    // END android-changed
            throws IOException {
        if (null == netif.addresses) {
            return false;
        }
        Enumeration<InetAddress> addresses = netif.getInetAddresses();
        reached = false;
        addrCount = netif.addresses.length;
        boolean needWait = false;
        while (addresses.hasMoreElements()) {
            final InetAddress addr = addresses.nextElement();

            // loopback interface can only reach to local addresses
            if (addr.isLoopbackAddress()) {
                Enumeration<NetworkInterface> NetworkInterfaces = NetworkInterface
                        .getNetworkInterfaces();
                while (NetworkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = NetworkInterfaces
                            .nextElement();
                    Enumeration<InetAddress> localAddresses = networkInterface
                            .getInetAddresses();
                    while (localAddresses.hasMoreElements()) {
                        if (InetAddress.this.equals(localAddresses
                                .nextElement())) {
                            return true;
                        }
                    }
                }

                synchronized (waitReachable) {
                    addrCount--;

                    if (addrCount == 0) {
                        // if count equals zero, all thread
                        // expired,notifies main thread
                        waitReachable.notifyAll();
                    }
                }
                continue;
            }

            needWait = true;
            new Thread() {
                @Override
                public void run() {
                    boolean threadReached = false;
                    // BEGIN android-changed
                    // if isICMP, tries ICMP ping, else TCP echo
                    // if (isICMP) {
                    //     threadReached = NETIMPL.isReachableByICMP(
                    //             InetAddress.this, addr, ttl, timeout);
                    // } else {
                        try {
                            threadReached = isReachableByTCP(addr,
                                    InetAddress.this, timeout);
                        } catch (IOException e) {
                            // do nothing
                        }
                    // }
                    // END android-changed

                    synchronized (waitReachable) {
                        if (threadReached) {
                            // if thread reached this address, sets reached to
                            // true and notifies main thread
                            reached = true;
                            waitReachable.notifyAll();
                        } else {
                            addrCount--;
                            if (0 == addrCount) {
                                // if count equals zero, all thread
                                // expired,notifies main thread
                                waitReachable.notifyAll();
                            }
                        }
                    }
                }
            }.start();
        }

        if (needWait) {
            synchronized (waitReachable) {
                try {
                    while (!reached && (addrCount != 0)) {
                        // wait for notification
                        waitReachable.wait(1000);
                    }
                } catch (InterruptedException e) {
                    // do nothing
                }
                return reached;
            }
        }

        return false;
    }

    // BEGIN android-removed
    // private boolean isReachableByICMPUseMultiThread(NetworkInterface netif,
    //         int ttl, int timeout) throws IOException {
    //     return isReachableByMultiThread(netif, ttl, timeout, true);
    // }
    //
    // private boolean isReachableByTCPUseMultiThread(NetworkInterface netif,
    //         int ttl, int timeout) throws IOException {
    //     return isReachableByMultiThread(netif, ttl, timeout, false);
    // }
    // END android-removed

    private boolean isReachableByTCP(InetAddress dest, InetAddress source,
            int timeout) throws IOException {
        FileDescriptor fd = new FileDescriptor();
        // define traffic only for parameter
        int traffic = 0;
        boolean reached = false;
        NETIMPL.createStreamSocket(fd, NetUtil.preferIPv4Stack());
        try {
            if (null != source) {
                NETIMPL.bind(fd, source, 0);
            }
            NETIMPL.connectStreamWithTimeoutSocket(fd, 7, timeout, traffic,
                    dest);
            reached = true;
        } catch (IOException e) {
            if (ERRMSG_CONNECTION_REFUSED.equals(e.getMessage())) {
                // Connection refused means the IP is reachable
                reached = true;
            }
        }

        NETIMPL.socketClose(fd);

        return reached;
    }

    /**
     * Returns the {@code InetAddress} corresponding to the array of bytes. In
     * the case of an IPv4 address there must be exactly 4 bytes and for IPv6
     * exactly 16 bytes. If not, an {@code UnknownHostException} is thrown.
     * <p>
     * The IP address is not validated by a name service.
     * <p>
     * The high order byte is {@code ipAddress[0]}.
     *
     * @param ipAddress
     *            is either a 4 (IPv4) or 16 (IPv6) byte long array.
     * @return an {@code InetAddress} instance representing the given IP address
     *         {@code ipAddress}.
     * @throws UnknownHostException
     *             if the given byte array has no valid length.
     */
    public static InetAddress getByAddress(byte[] ipAddress)
            throws UnknownHostException {
        // simply call the method by the same name specifying the default scope
        // id of 0
        return getByAddressInternal(null, ipAddress, 0);
    }

    /**
     * Returns the {@code InetAddress} corresponding to the array of bytes. In
     * the case of an IPv4 address there must be exactly 4 bytes and for IPv6
     * exactly 16 bytes. If not, an {@code UnknownHostException} is thrown. The
     * IP address is not validated by a name service. The high order byte is
     * {@code ipAddress[0]}.
     *
     * @param ipAddress
     *            either a 4 (IPv4) or 16 (IPv6) byte array.
     * @param scope_id
     *            the scope id for an IPV6 scoped address. If not a scoped
     *            address just pass in 0.
     * @return the InetAddress
     * @throws UnknownHostException
     */
    static InetAddress getByAddress(byte[] ipAddress, int scope_id)
            throws UnknownHostException {
        return getByAddressInternal(null, ipAddress, scope_id);
    }

    private static boolean isIPv4MappedAddress(byte ipAddress[]) {
        // Check if the address matches ::FFFF:d.d.d.d
        // The first 10 bytes are 0. The next to are -1 (FF).
        // The last 4 bytes are varied.
        if (ipAddress == null || ipAddress.length != 16) {
            return false;
        }
        for (int i = 0; i < 10; i++) {
            if (ipAddress[i] != 0) {
                return false;
            }
        }
        if (ipAddress[10] != -1 || ipAddress[11] != -1) {
            return false;
        }
        return true;
    }

    private static byte[] ipv4MappedToIPv4(byte[] mappedAddress) {
        byte[] ipv4Address = new byte[4];
        for(int i = 0; i < 4; i++) {
            ipv4Address[i] = mappedAddress[12 + i];
        }
        return ipv4Address;
    }

    /**
     * Returns the {@code InetAddress} corresponding to the array of bytes, and
     * the given hostname. In the case of an IPv4 address there must be exactly
     * 4 bytes and for IPv6 exactly 16 bytes. If not, an {@code
     * UnknownHostException} will be thrown.
     * <p>
     * The host name and IP address are not validated.
     * <p>
     * The hostname either be a machine alias or a valid IPv6 or IPv4 address
     * format.
     * <p>
     * The high order byte is {@code ipAddress[0]}.
     *
     * @param hostName
     *            the string representation of hostname or IP address.
     * @param ipAddress
     *            either a 4 (IPv4) or 16 (IPv6) byte long array.
     * @return an {@code InetAddress} instance representing the given IP address
     *         and hostname.
     * @throws UnknownHostException
     *             if the given byte array has no valid length.
     */
    public static InetAddress getByAddress(String hostName, byte[] ipAddress)
            throws UnknownHostException {
        // just call the method by the same name passing in a default scope id
        // of 0
        return getByAddressInternal(hostName, ipAddress, 0);
    }

    /**
     * Returns the {@code InetAddress} corresponding to the array of bytes, and
     * the given hostname. In the case of an IPv4 address there must be exactly
     * 4 bytes and for IPv6 exactly 16 bytes. If not, an {@code
     * UnknownHostException} is thrown. The host name and IP address are not
     * validated. The hostname either be a machine alias or a valid IPv6 or IPv4
     * address format. The high order byte is {@code ipAddress[0]}.
     *
     * @param hostName
     *            string representation of hostname or IP address.
     * @param ipAddress
     *            either a 4 (IPv4) or 16 (IPv6) byte array.
     * @param scope_id
     *            the scope id for a scoped address. If not a scoped address
     *            just pass in 0.
     * @return the InetAddress
     * @throws UnknownHostException
     */
    static InetAddress getByAddressInternal(String hostName, byte[] ipAddress,
            int scope_id) throws UnknownHostException {
        if (ipAddress == null) {
            // We don't throw NullPointerException here for RI compatibility,
            // but we do say "address is null" (K0331), instead of "addr is of
            // illegal length".
            throw new UnknownHostException(
                Msg.getString("K0331", hostName)); //$NON-NLS-1$
        }
        switch (ipAddress.length) {
            case 4:
                return new Inet4Address(ipAddress.clone());
            case 16:
                // First check to see if the address is an IPv6-mapped
                // IPv4 address. If it is, then we can make it a IPv4
                // address, otherwise, we'll create an IPv6 address.
                if (isIPv4MappedAddress(ipAddress)) {
                    return new Inet4Address(ipv4MappedToIPv4(ipAddress));
                } else {
                    return new Inet6Address(ipAddress.clone(), scope_id);
                }
            default:
                if (hostName != null) {
                    // "Invalid IP Address is neither 4 or 16 bytes: <hostName>"
                    throw new UnknownHostException(
                            Msg.getString("K0332", hostName)); //$NON-NLS-1$
                } else {
                    // "Invalid IP Address is neither 4 or 16 bytes"
                    throw new UnknownHostException(
                            Msg.getString("K0339")); //$NON-NLS-1$
                }
        }
    }

    /**
     * Takes the integer and chops it into 4 bytes, putting it into the byte
     * array starting with the high order byte at the index start. This method
     * makes no checks on the validity of the parameters.
     */
    static void intToBytes(int value, byte bytes[], int start) {
        // Shift the int so the current byte is right-most
        // Use a byte mask of 255 to single out the last byte.
        bytes[start] = (byte) ((value >> 24) & 255);
        bytes[start + 1] = (byte) ((value >> 16) & 255);
        bytes[start + 2] = (byte) ((value >> 8) & 255);
        bytes[start + 3] = (byte) (value & 255);
    }

    /**
     * Takes the byte array and creates an integer out of four bytes starting at
     * start as the high-order byte. This method makes no checks on the validity
     * of the parameters.
     */
    static int bytesToInt(byte bytes[], int start) {
        // First mask the byte with 255, as when a negative
        // signed byte converts to an integer, it has bits
        // on in the first 3 bytes, we are only concerned
        // about the right-most 8 bits.
        // Then shift the rightmost byte to align with its
        // position in the integer.
        int value = ((bytes[start + 3] & 255))
                | ((bytes[start + 2] & 255) << 8)
                | ((bytes[start + 1] & 255) << 16)
                | ((bytes[start] & 255) << 24);
        return value;
    }

    static boolean preferIPv6Addresses() {
        String result = AccessController.doPrivileged(new PriviAction<String>(
                "java.net.preferIPv6Addresses")); //$NON-NLS-1$
        return "true".equals(result); //$NON-NLS-1$
    }

    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("address", Integer.TYPE), //$NON-NLS-1$
            new ObjectStreamField("family", Integer.TYPE), //$NON-NLS-1$
            new ObjectStreamField("hostName", String.class) }; //$NON-NLS-1$

    private void writeObject(ObjectOutputStream stream) throws IOException {
        ObjectOutputStream.PutField fields = stream.putFields();
        if (ipaddress == null) {
            fields.put("address", 0); //$NON-NLS-1$
        } else {
            fields.put("address", bytesToInt(ipaddress, 0)); //$NON-NLS-1$
        }
        fields.put("family", family); //$NON-NLS-1$
        fields.put("hostName", hostName); //$NON-NLS-1$

        stream.writeFields();
    }

    private void readObject(ObjectInputStream stream) throws IOException,
            ClassNotFoundException {
        ObjectInputStream.GetField fields = stream.readFields();
        int addr = fields.get("address", 0); //$NON-NLS-1$
        ipaddress = new byte[4];
        intToBytes(addr, ipaddress, 0);
        hostName = (String) fields.get("hostName", null); //$NON-NLS-1$
        family = fields.get("family", 2); //$NON-NLS-1$
    }

    private Object readResolve() throws ObjectStreamException {
        return new Inet4Address(ipaddress, hostName);
    }
}
