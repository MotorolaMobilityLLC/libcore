/*
 * Copyright (C) 2014 The Android Open Source Project
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

import dalvik.annotation.compat.UnsupportedAppUsage;

@SuppressWarnings({"unchecked", "deprecation", "all"})
public final class Long extends java.lang.Number implements java.lang.Comparable<java.lang.Long> {

    public Long(long value) {
        throw new RuntimeException("Stub!");
    }

    public Long(java.lang.String s) throws java.lang.NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.String toString(long i, int radix) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.String toUnsignedString(long i, int radix) {
        throw new RuntimeException("Stub!");
    }

    private static java.math.BigInteger toUnsignedBigInteger(long i) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.String toHexString(long i) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.String toOctalString(long i) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.String toBinaryString(long i) {
        throw new RuntimeException("Stub!");
    }

    static java.lang.String toUnsignedString0(long val, int shift) {
        throw new RuntimeException("Stub!");
    }

    static int formatUnsignedLong(long val, int shift, char[] buf, int offset, int len) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.String toString(long i) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.String toUnsignedString(long i) {
        throw new RuntimeException("Stub!");
    }

    static void getChars(long i, int index, char[] buf) {
        throw new RuntimeException("Stub!");
    }

    static int stringSize(long x) {
        throw new RuntimeException("Stub!");
    }

    public static long parseLong(java.lang.String s, int radix)
            throws java.lang.NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static long parseLong(java.lang.String s) throws java.lang.NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static long parseUnsignedLong(java.lang.String s, int radix)
            throws java.lang.NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static long parseUnsignedLong(java.lang.String s)
            throws java.lang.NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.Long valueOf(java.lang.String s, int radix)
            throws java.lang.NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.Long valueOf(java.lang.String s)
            throws java.lang.NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.Long valueOf(long l) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.Long decode(java.lang.String nm)
            throws java.lang.NumberFormatException {
        throw new RuntimeException("Stub!");
    }

    public byte byteValue() {
        throw new RuntimeException("Stub!");
    }

    public short shortValue() {
        throw new RuntimeException("Stub!");
    }

    public int intValue() {
        throw new RuntimeException("Stub!");
    }

    public long longValue() {
        throw new RuntimeException("Stub!");
    }

    public float floatValue() {
        throw new RuntimeException("Stub!");
    }

    public double doubleValue() {
        throw new RuntimeException("Stub!");
    }

    public java.lang.String toString() {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public static int hashCode(long value) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(java.lang.Object obj) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.Long getLong(java.lang.String nm) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.Long getLong(java.lang.String nm, long val) {
        throw new RuntimeException("Stub!");
    }

    public static java.lang.Long getLong(java.lang.String nm, java.lang.Long val) {
        throw new RuntimeException("Stub!");
    }

    public int compareTo(java.lang.Long anotherLong) {
        throw new RuntimeException("Stub!");
    }

    public static int compare(long x, long y) {
        throw new RuntimeException("Stub!");
    }

    public static int compareUnsigned(long x, long y) {
        throw new RuntimeException("Stub!");
    }

    public static long divideUnsigned(long dividend, long divisor) {
        throw new RuntimeException("Stub!");
    }

    public static long remainderUnsigned(long dividend, long divisor) {
        throw new RuntimeException("Stub!");
    }

    public static long highestOneBit(long i) {
        throw new RuntimeException("Stub!");
    }

    public static long lowestOneBit(long i) {
        throw new RuntimeException("Stub!");
    }

    public static int numberOfLeadingZeros(long i) {
        throw new RuntimeException("Stub!");
    }

    public static int numberOfTrailingZeros(long i) {
        throw new RuntimeException("Stub!");
    }

    public static int bitCount(long i) {
        throw new RuntimeException("Stub!");
    }

    public static long rotateLeft(long i, int distance) {
        throw new RuntimeException("Stub!");
    }

    public static long rotateRight(long i, int distance) {
        throw new RuntimeException("Stub!");
    }

    public static long reverse(long i) {
        throw new RuntimeException("Stub!");
    }

    public static int signum(long i) {
        throw new RuntimeException("Stub!");
    }

    public static long reverseBytes(long i) {
        throw new RuntimeException("Stub!");
    }

    public static long sum(long a, long b) {
        throw new RuntimeException("Stub!");
    }

    public static long max(long a, long b) {
        throw new RuntimeException("Stub!");
    }

    public static long min(long a, long b) {
        throw new RuntimeException("Stub!");
    }

    public static final int BYTES = 8; // 0x8

    public static final long MAX_VALUE = 9223372036854775807L; // 0x7fffffffffffffffL

    public static final long MIN_VALUE = -9223372036854775808L; // 0x8000000000000000L

    public static final int SIZE = 64; // 0x40

    public static final java.lang.Class<java.lang.Long> TYPE;

    static {
        TYPE = null;
    }

    private static final long serialVersionUID = 4290774380558885855L; // 0x3b8be490cc8f23dfL

    /**
     * @deprecated Use {@link #longValue()}.
     */
    @UnsupportedAppUsage(maxTargetSdk = UnsupportedAppUsage.VERSION_CODES.P)
    private final long value;

    {
        value = 0;
    }

    @SuppressWarnings({"unchecked", "deprecation", "all"})
    private static class LongCache {

        private LongCache() {
            throw new RuntimeException("Stub!");
        }

        static final java.lang.Long[] cache;

        static {
            cache = new java.lang.Long[0];
        }
    }
}
