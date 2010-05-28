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

// BEGIN android-note
// Reimiplemented toString, bit-twiddling, etc. Faster and cleaner.
// BEGIN android-note

package java.lang;

/**
 * The wrapper for the primitive type {@code long}.
 * <p>
 * Implementation note: The "bit twiddling" methods in this class use techniques
 * described in <a href="http://www.hackersdelight.org/">Henry S. Warren,
 * Jr.'s Hacker's Delight, (Addison Wesley, 2002)</a> and <a href=
 * "http://graphics.stanford.edu/~seander/bithacks.html">Sean Anderson's
 * Bit Twiddling Hacks.</a>
 *
 * @see java.lang.Integer
 * @since 1.0
 */
public final class Long extends Number implements Comparable<Long> {

    private static final long serialVersionUID = 4290774380558885855L;

    /**
     * The value which the receiver represents.
     */
    private final long value;

    /**
     * Constant for the maximum {@code long} value, 2<sup>63</sup>-1.
     */
    public static final long MAX_VALUE = 0x7FFFFFFFFFFFFFFFL;

    /**
     * Constant for the minimum {@code long} value, -2<sup>63</sup>.
     */
    public static final long MIN_VALUE = 0x8000000000000000L;

    /**
     * The {@link Class} object that represents the primitive type {@code long}.
     */
    @SuppressWarnings("unchecked")
    public static final Class<Long> TYPE
            = (Class<Long>) long[].class.getComponentType();
    // Note: Long.TYPE can't be set to "long.class", since *that* is
    // defined to be "java.lang.Long.TYPE";

    /**
     * Constant for the number of bits needed to represent a {@code long} in
     * two's complement form.
     *
     * @since 1.5
     */
    public static final int SIZE = 64;

    /**
     * Table for MOD / DIV 10 computation described in Section 10-21
     * of Hank Warren's "Hacker's Delight" online addendum.
     * http://www.hackersdelight.org/divcMore.pdf
     */
    private static final char[] MOD_10_TABLE = {
        0, 1, 2, 2, 3, 3, 4, 5, 5, 6, 7, 7, 8, 8, 9, 0
    };

    /**
     * Constructs a new {@code Long} with the specified primitive long value.
     *
     * @param value
     *            the primitive long value to store in the new instance.
     */
    public Long(long value) {
        this.value = value;
    }

    /**
     * Constructs a new {@code Long} from the specified string.
     *
     * @param string
     *            the string representation of a long value.
     * @throws NumberFormatException
     *             if {@code string} can not be decoded into a long value.
     * @see #parseLong(String)
     */
    public Long(String string) throws NumberFormatException {
        this(parseLong(string));
    }

    @Override
    public byte byteValue() {
        return (byte) value;
    }

    /**
     * Compares this object to the specified long object to determine their
     * relative order.
     *
     * @param object
     *            the long object to compare this object to.
     * @return a negative value if the value of this long is less than the value
     *         of {@code object}; 0 if the value of this long and the value of
     *         {@code object} are equal; a positive value if the value of this
     *         long is greater than the value of {@code object}.
     * @see java.lang.Comparable
     * @since 1.2
     */
    public int compareTo(Long object) {
        long thisValue = this.value;
        long thatValue = object.value;
        return thisValue < thatValue ? -1 : (thisValue == thatValue ? 0 : 1);
    }

    /**
     * Parses the specified string and returns a {@code Long} instance if the
     * string can be decoded into a long value. The string may be an optional
     * minus sign "-" followed by a hexadecimal ("0x..." or "#..."), octal
     * ("0..."), or decimal ("...") representation of a long.
     *
     * @param string
     *            a string representation of a long value.
     * @return a {@code Long} containing the value represented by {@code string}.
     * @throws NumberFormatException
     *             if {@code string} can not be parsed as a long value.
     */
    public static Long decode(String string) throws NumberFormatException {
        int length = string.length(), i = 0;
        if (length == 0) {
            throw new NumberFormatException();
        }
        char firstDigit = string.charAt(i);
        boolean negative = firstDigit == '-';
        if (negative) {
            if (length == 1) {
                throw new NumberFormatException(string);
            }
            firstDigit = string.charAt(++i);
        }

        int base = 10;
        if (firstDigit == '0') {
            if (++i == length) {
                return valueOf(0L);
            }
            if ((firstDigit = string.charAt(i)) == 'x' || firstDigit == 'X') {
                if (i == length) {
                    throw new NumberFormatException(string);
                }
                i++;
                base = 16;
            } else {
                base = 8;
            }
        } else if (firstDigit == '#') {
            if (i == length) {
                throw new NumberFormatException(string);
            }
            i++;
            base = 16;
        }

        long result = parse(string, i, base, negative);
        return valueOf(result);
    }

    @Override
    public double doubleValue() {
        return value;
    }

    /**
     * Compares this instance with the specified object and indicates if they
     * are equal. In order to be equal, {@code o} must be an instance of
     * {@code Long} and have the same long value as this object.
     *
     * @param o
     *            the object to compare this long with.
     * @return {@code true} if the specified object is equal to this
     *         {@code Long}; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Long && ((Long) o).value == value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    /**
     * Returns the {@code Long} value of the system property identified by
     * {@code string}. Returns {@code null} if {@code string} is {@code null}
     * or empty, if the property can not be found or if its value can not be
     * parsed as a long.
     *
     * @param string
     *            the name of the requested system property.
     * @return the requested property's value as a {@code Long} or {@code null}.
     */
    public static Long getLong(String string) {
        if (string == null || string.length() == 0) {
            return null;
        }
        String prop = System.getProperty(string);
        if (prop == null) {
            return null;
        }
        try {
            return decode(prop);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Returns the {@code Long} value of the system property identified by
     * {@code string}. Returns the specified default value if {@code string} is
     * {@code null} or empty, if the property can not be found or if its value
     * can not be parsed as a long.
     *
     * @param string
     *            the name of the requested system property.
     * @param defaultValue
     *            the default value that is returned if there is no long system
     *            property with the requested name.
     * @return the requested property's value as a {@code Long} or the default
     *         value.
     */
    public static Long getLong(String string, long defaultValue) {
        if (string == null || string.length() == 0) {
            return valueOf(defaultValue);
        }
        String prop = System.getProperty(string);
        if (prop == null) {
            return valueOf(defaultValue);
        }
        try {
            return decode(prop);
        } catch (NumberFormatException ex) {
            return valueOf(defaultValue);
        }
    }

    /**
     * Returns the {@code Long} value of the system property identified by
     * {@code string}. Returns the specified default value if {@code string} is
     * {@code null} or empty, if the property can not be found or if its value
     * can not be parsed as a long.
     *
     * @param string
     *            the name of the requested system property.
     * @param defaultValue
     *            the default value that is returned if there is no long system
     *            property with the requested name.
     * @return the requested property's value as a {@code Long} or the default
     *         value.
     */
    public static Long getLong(String string, Long defaultValue) {
        if (string == null || string.length() == 0) {
            return defaultValue;
        }
        String prop = System.getProperty(string);
        if (prop == null) {
            return defaultValue;
        }
        try {
            return decode(prop);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    /**
     * Gets the primitive value of this long.
     *
     * @return this object's primitive value.
     */
    @Override
    public long longValue() {
        return value;
    }

    /**
     * Parses the specified string as a signed decimal long value. The ASCII
     * character \u002d ('-') is recognized as the minus sign.
     *
     * @param string
     *            the string representation of a long value.
     * @return the primitive long value represented by {@code string}.
     * @throws NumberFormatException
     *             if {@code string} is {@code null}, has a length of zero or
     *             can not be parsed as a long value.
     */
    public static long parseLong(String string) throws NumberFormatException {
        return parseLong(string, 10);
    }

    /**
     * Parses the specified string as a signed long value using the specified
     * radix. The ASCII character \u002d ('-') is recognized as the minus sign.
     *
     * @param string
     *            the string representation of a long value.
     * @param radix
     *            the radix to use when parsing.
     * @return the primitive long value represented by {@code string} using
     *         {@code radix}.
     * @throws NumberFormatException
     *             if {@code string} is {@code null} or has a length of zero,
     *             {@code radix < Character.MIN_RADIX},
     *             {@code radix > Character.MAX_RADIX}, or if {@code string}
     *             can not be parsed as a long value.
     */
    public static long parseLong(String string, int radix)
            throws NumberFormatException {
        if (string == null || radix < Character.MIN_RADIX
                || radix > Character.MAX_RADIX) {
            throw new NumberFormatException();
        }
        int length = string.length(), i = 0;
        if (length == 0) {
            throw new NumberFormatException(string);
        }
        boolean negative = string.charAt(i) == '-';
        if (negative && ++i == length) {
            throw new NumberFormatException(string);
        }

        return parse(string, i, radix, negative);
    }

    private static long parse(String string, int offset, int radix,
            boolean negative) {
        long max = Long.MIN_VALUE / radix;
        long result = 0, length = string.length();
        while (offset < length) {
            int digit = Character.digit(string.charAt(offset++), radix);
            if (digit == -1) {
                throw new NumberFormatException(string);
            }
            if (max > result) {
                throw new NumberFormatException(string);
            }
            long next = result * radix - digit;
            if (next > result) {
                throw new NumberFormatException(string);
            }
            result = next;
        }
        if (!negative) {
            result = -result;
            if (result < 0) {
                throw new NumberFormatException(string);
            }
        }
        return result;
    }

    @Override
    public short shortValue() {
        return (short) value;
    }

    /**
     * Converts the specified long value into its binary string representation.
     * The returned string is a concatenation of '0' and '1' characters.
     *
     * @param v
     *            the long value to convert.
     * @return the binary string representation of {@code l}.
     */
    public static String toBinaryString(long v) {
        int i = (int) v;
        if (v >= 0 && i == v) {
            return Integer.toBinaryString(i);
        }

        int bufLen = 64;  // Max number of binary digits in a long
        char[] buf = new char[bufLen];
        int cursor = bufLen;

        do {
            buf[--cursor] = (char) ((v & 1) + '0');
        }  while ((v >>>= 1) != 0);

        return new String(cursor, bufLen - cursor, buf);
     }

    /**
     * Converts the specified long value into its hexadecimal string
     * representation. The returned string is a concatenation of characters from
     * '0' to '9' and 'a' to 'f'.
     *
     * @param v
     *            the long value to convert.
     * @return the hexadecimal string representation of {@code l}.
     */
    public static String toHexString(long v) {
        int i = (int) v;
        if (v >= 0 && i == v) {
            return Integer.toHexString(i);
        }

        int bufLen = 16;  // Max number of hex digits in a long
        char[] buf = new char[bufLen];
        int cursor = bufLen;

        do {
            buf[--cursor] = Integer.DIGITS[((int) v) & 0xF];
        } while ((v >>>= 4) != 0);

        return new String(cursor, bufLen - cursor, buf);
    }

    /**
     * Converts the specified long value into its octal string representation.
     * The returned string is a concatenation of characters from '0' to '7'.
     *
     * @param v
     *            the long value to convert.
     * @return the octal string representation of {@code l}.
     */
    public static String toOctalString(long v) {
        int i = (int) v;
        if (v >= 0 && i == v) {
            return Integer.toOctalString(i);
        }
        int bufLen = 22;  // Max number of octal digits in a long
        char[] buf = new char[bufLen];
        int cursor = bufLen;

        do {
            buf[--cursor] = (char) (((int)v & 7) + '0');
        } while ((v >>>= 3) != 0);

        return new String(cursor, bufLen - cursor, buf);
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    /**
     * Converts the specified long value into its decimal string representation.
     * The returned string is a concatenation of a minus sign if the number is
     * negative and characters from '0' to '9'.
     *
     * @param n
     *            the long to convert.
     * @return the decimal string representation of {@code l}.
     */
    public static String toString(long n) {
        int i = (int) n;
        if (i == n)
            return Integer.toString(i);

        boolean negative = (n < 0);
        if (negative) {
            n = -n;
            if (n < 0)  // If -n is still negative, n is Long.MIN_VALUE
                return "-9223372036854775808";
        }

        int bufLen = 20; // Maximum number of chars in result
        char[] buf = new char[bufLen];

        int low = (int) (n % 1000000000); // Extract low-order 9 digits
        int cursor = intIntoCharArray(buf, bufLen, low);

        // Zero-pad Low order part to 9 digits
        while (cursor != (bufLen - 9))
            buf[--cursor] = '0';

        /*
         * The remaining digits are (n - low) / 1,000,000,000.  This
         * "exact division" is done as per the online addendum to Hank Warren's
         * "Hacker's Delight" 10-20, http://www.hackersdelight.org/divcMore.pdf
         */
        n = ((n - low) >>> 9) * 0x8E47CE423A2E9C6DL;

        /*
         * If the remaining digits fit in an int, emit them using a
         * single call to intIntoCharArray. Otherwise, strip off the
         * low-order digit, put it in buf, and then call intIntoCharArray
         * on the remaining digits (which now fit in an int).
         */
        if ((n & (-1L << 32)) == 0) {
            cursor = intIntoCharArray(buf, cursor, (int) n);
        } else {
            /*
             * Set midDigit to n % 10
             */
            int lo32 = (int) n;
            int hi32 = (int) (n >>> 32);

            // midDigit = ((unsigned) low32) % 10, per "Hacker's Delight" 10-21
            int midDigit = MOD_10_TABLE[
                (0x19999999 * lo32 + (lo32 >>> 1) + (lo32 >>> 3)) >>> 28];

            // Adjust midDigit for hi32. (assert hi32 == 1 || hi32 == 2)
            midDigit -= hi32 << 2;  // 1L << 32 == -4 MOD 10
            if (midDigit < 0)
                midDigit += 10;

            buf[--cursor] = (char) (midDigit + '0');

            // Exact division as per Warren 10-20
            int rest = ((int) ((n - midDigit) >>> 1)) * 0xCCCCCCCD;
            cursor = intIntoCharArray(buf, cursor, rest);
        }

        if (negative)
            buf[--cursor] = '-';

        return new String(cursor, bufLen - cursor, buf);
    }

    /**
     * Inserts the unsigned decimal integer represented by n into the specified
     * character array starting at position cursor.  Returns the index after
     * the last character inserted (i.e., the value to pass in as cursor the
     * next time this method is called). Note that n is interpreted as a large
     * positive integer (not a negative integer) if its sign bit is set.
     */
    static int intIntoCharArray(char[] buf, int cursor, int n) {
        // Calculate digits two-at-a-time till remaining digits fit in 16 bits
        while ((n & 0xffff0000) != 0) {
            /*
             * Compute q = n/100 and r = n % 100 as per "Hacker's Delight" 10-8.
             * This computation is sligthly different from the corresponding
             * computation in Integer.toString: the shifts before and after
             * multiply can't be combined, as that would yield the wrong result
             * if n's sign bit were set.
             */
            int q = (int) ((0x51EB851FL * (n >>> 2)) >>> 35);
            // BEGIN android-changed
            int r = n - ((q << 6) + (q << 5) + (q << 2));  // int r = n - 100*q;
            // END android-changed

            buf[--cursor] = Integer.ONES[r];
            buf[--cursor] = Integer.TENS[r];
            n = q;
        }

        // Calculate remaining digits one-at-a-time for performance
        while (n != 0) {
            // Compute q = n / 10 and r = n % 10 as per "Hacker's Delight" 10-8
            int q = (0xCCCD * n) >>> 19;
            // BEGIN android-changed
            int r = n - ((q << 3) + (q << 1));  // int r = n - 10 * q;
            // END android-changed

            buf[--cursor] = (char) (r + '0');
            n = q;
        }
        return cursor;
    }

    /**
     * Converts the specified signed long value into a string representation based on
     * the specified radix. The returned string is a concatenation of a minus
     * sign if the number is negative and characters from '0' to '9' and 'a' to
     * 'z', depending on the radix. If {@code radix} is not in the interval
     * defined by {@code Character.MIN_RADIX} and {@code Character.MAX_RADIX}
     * then 10 is used as the base for the conversion.
     *
     * <p>This method treats its argument as signed. If you want to convert an
     * unsigned value to one of the common non-decimal bases, you may find
     * {@link #toBinaryString}, {@code #toHexString}, or {@link #toOctalString}
     * more convenient.
     *
     * @param v
     *            the signed long to convert.
     * @param radix
     *            the base to use for the conversion.
     * @return the string representation of {@code v}.
     */
    public static String toString(long v, int radix) {
        int i = (int) v;
        if (i == v) {
            return Integer.toString(i, radix);
        }

        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            radix = 10;
        }
        if (radix == 10) {
            return toString(v);
        }

        /*
         * If v is positive, negate it. This is the opposite of what one might
         * expect. It is necessary because the range of the negative values is
         * strictly larger than that of the positive values: there is no
         * positive value corresponding to Integer.MIN_VALUE.
         */
        boolean negative = false;
        if (v < 0) {
            negative = true;
        } else {
            v = -v;
        }

        int bufLen = radix < 8 ? 65 : 23;  // Max chars in result (conservative)
        char[] buf = new char[bufLen];
        int cursor = bufLen;

        do {
            long q = v / radix;
            buf[--cursor] = Integer.DIGITS[(int) (radix * q - v)];
            v = q;
        } while (v != 0);

        if (negative) {
            buf[--cursor] = '-';
        }

        return new String(cursor, bufLen - cursor, buf);
    }

    /**
     * Parses the specified string as a signed decimal long value.
     *
     * @param string
     *            the string representation of a long value.
     * @return a {@code Long} instance containing the long value represented by
     *         {@code string}.
     * @throws NumberFormatException
     *             if {@code string} is {@code null}, has a length of zero or
     *             can not be parsed as a long value.
     * @see #parseLong(String)
     */
    public static Long valueOf(String string) throws NumberFormatException {
        return valueOf(parseLong(string));
    }

    /**
     * Parses the specified string as a signed long value using the specified
     * radix.
     *
     * @param string
     *            the string representation of a long value.
     * @param radix
     *            the radix to use when parsing.
     * @return a {@code Long} instance containing the long value represented by
     *         {@code string} using {@code radix}.
     * @throws NumberFormatException
     *             if {@code string} is {@code null} or has a length of zero,
     *             {@code radix < Character.MIN_RADIX},
     *             {@code radix > Character.MAX_RADIX}, or if {@code string}
     *             can not be parsed as a long value.
     * @see #parseLong(String, int)
     */
    public static Long valueOf(String string, int radix)
            throws NumberFormatException {
        return valueOf(parseLong(string, radix));
    }

    /**
     * Determines the highest (leftmost) bit of the specified long value that is
     * 1 and returns the bit mask value for that bit. This is also referred to
     * as the Most Significant 1 Bit. Returns zero if the specified long is
     * zero.
     *
     * @param v
     *            the long to examine.
     * @return the bit mask indicating the highest 1 bit in {@code v}.
     * @since 1.5
     */
    public static long highestOneBit(long v) {
        // Hacker's Delight, Figure 3-1
        v |= (v >> 1);
        v |= (v >> 2);
        v |= (v >> 4);
        v |= (v >> 8);
        v |= (v >> 16);
        v |= (v >> 32);
        return v - (v >>> 1);
    }

    /**
     * Determines the lowest (rightmost) bit of the specified long value that is
     * 1 and returns the bit mask value for that bit. This is also referred to
     * as the Least Significant 1 Bit. Returns zero if the specified long is
     * zero.
     *
     * @param v
     *            the long to examine.
     * @return the bit mask indicating the lowest 1 bit in {@code v}.
     * @since 1.5
     */
    public static long lowestOneBit(long v) {
        return v & -v;
    }

    /**
     * Determines the number of leading zeros in the specified long value prior
     * to the {@link #highestOneBit(long) highest one bit}.
     *
     * @param v
     *            the long to examine.
     * @return the number of leading zeros in {@code v}.
     * @since 1.5
     */
    public static int numberOfLeadingZeros(long v) {
        // After Hacker's Delight, Figure 5-6
        if (v < 0) {
            return 0;
        }
        if (v == 0) {
            return 64;
        }
        // On a 64-bit VM, the two previous tests should probably be replaced by
        // if (v <= 0) return ((int) (~v >> 57)) & 64;

        int n = 1;
        int i = (int) (v >>> 32);
        if (i == 0) {
            n +=  32;
            i = (int) v;
        }
        if (i >>> 16 == 0) {
            n +=  16;
            i <<= 16;
        }
        if (i >>> 24 == 0) {
            n +=  8;
            i <<= 8;
        }
        if (i >>> 28 == 0) {
            n +=  4;
            i <<= 4;
        }
        if (i >>> 30 == 0) {
            n +=  2;
            i <<= 2;
        }
        return n - (i >>> 31);
    }

    /**
     * Determines the number of trailing zeros in the specified long value after
     * the {@link #lowestOneBit(long) lowest one bit}.
     *
     * @param v
     *            the long to examine.
     * @return the number of trailing zeros in {@code v}.
     * @since 1.5
     */
    public static int numberOfTrailingZeros(long v) {
        int low = (int) v;
        return low !=0 ? Integer.numberOfTrailingZeros(low)
                       : 32 + Integer.numberOfTrailingZeros((int) (v >>> 32));
    }

    /**
     * Counts the number of 1 bits in the specified long value; this is also
     * referred to as population count.
     *
     * @param v
     *            the long to examine.
     * @return the number of 1 bits in {@code v}.
     * @since 1.5
     */
    public static int bitCount(long v) {
        // Combines techniques from several sources
        v -=  (v >>> 1) & 0x5555555555555555L;
        v = (v & 0x3333333333333333L) + ((v >>> 2) & 0x3333333333333333L);
        int i =  ((int)(v >>> 32)) + (int) v;
        i = (i & 0x0F0F0F0F) + ((i >>> 4) & 0x0F0F0F0F);
        i += i >>> 8;
        i += i >>> 16;
        return i  & 0x0000007F;
    }

    /*
     * On a modern 64-bit processor with a fast hardware multiply, this is
     * much faster (assuming you're running a 64-bit VM):
     *
     * // http://chessprogramming.wikispaces.com/Population+Count
     * int bitCount (long x) {
     *     x -=  (x >>> 1) & 0x5555555555555555L;
     *     x = (x & 0x3333333333333333L) + ((x >>> 2) & 0x3333333333333333L);
     *     x = (x + (x >>> 4)) & 0x0f0f0f0f0f0f0f0fL;
     *     x = (x * 0x0101010101010101L) >>> 56;
     *     return (int) x;
     * }
     *
     * Really modern processors (e.g., Nehalem, K-10) have hardware popcount
     * instructions.
     */

    /**
     * Rotates the bits of the specified long value to the left by the specified
     * number of bits.
     *
     * @param v
     *            the long value to rotate left.
     * @param distance
     *            the number of bits to rotate.
     * @return the rotated value.
     * @since 1.5
     */
    public static long rotateLeft(long v, int distance) {
        // Shift distances are mod 64 (JLS3 15.19), so we needn't mask -distance
        return (v << distance) | (v >>> -distance);
    }

    /**
     * Rotates the bits of the specified long value to the right by the
     * specified number of bits.
     *
     * @param v
     *            the long value to rotate right.
     * @param distance
     *            the number of bits to rotate.
     * @return the rotated value.
     * @since 1.5
     */
    public static long rotateRight(long v, int distance) {
        // Shift distances are mod 64 (JLS3 15.19), so we needn't mask -distance
        return (v >>> distance) | (v << -distance);
    }

    /**
     * Reverses the order of the bytes of the specified long value.
     *
     * @param v
     *            the long value for which to reverse the byte order.
     * @return the reversed value.
     * @since 1.5
     */
    public static long reverseBytes(long v) {
        // Hacker's Delight 7-1, with minor tweak from Veldmeijer
        // http://graphics.stanford.edu/~seander/bithacks.html
        v = ((v >>> 8) & 0x00FF00FF00FF00FFL) | ((v & 0x00FF00FF00FF00FFL) << 8);
        v = ((v >>>16) & 0x0000FFFF0000FFFFL) | ((v & 0x0000FFFF0000FFFFL) <<16);
        return ((v >>>32)                   ) | ((v                      ) <<32);
    }

    /**
     * Reverses the order of the bits of the specified long value.
     *
     * @param v
     *            the long value for which to reverse the bit order.
     * @return the reversed value.
     * @since 1.5
     */
    public static long reverse(long v) {
        // Hacker's Delight 7-1, with minor tweak from Veldmeijer
        // http://graphics.stanford.edu/~seander/bithacks.html
        v = ((v >>> 1) & 0x5555555555555555L) | ((v & 0x5555555555555555L) << 1);
        v = ((v >>> 2) & 0x3333333333333333L) | ((v & 0x3333333333333333L) << 2);
        v = ((v >>> 4) & 0x0F0F0F0F0F0F0F0FL) | ((v & 0x0F0F0F0F0F0F0F0FL) << 4);
        v = ((v >>> 8) & 0x00FF00FF00FF00FFL) | ((v & 0x00FF00FF00FF00FFL) << 8);
        v = ((v >>>16) & 0x0000FFFF0000FFFFL) | ((v & 0x0000FFFF0000FFFFL) <<16);
        return ((v >>>32)                   ) | ((v                      ) <<32);
    }

    /**
     * Returns the value of the {@code signum} function for the specified long
     * value.
     *
     * @param v
     *            the long value to check.
     * @return -1 if {@code v} is negative, 1 if {@code v} is positive, 0 if
     *         {@code v} is zero.
     * @since 1.5
     */
    public static int signum(long v) {
        // BEGIN android-changed
        return v < 0 ? -1 : (v == 0 ? 0 : 1);
        // END android-changed
//      The following branch-free version is faster on modern desktops/servers
//      return ((int)(v >> 63)) | (int) (-v >>> 63); // Hacker's delight 2-7
    }

    /**
     * Returns a {@code Long} instance for the specified long value.
     * <p>
     * If it is not necessary to get a new {@code Long} instance, it is
     * recommended to use this method instead of the constructor, since it
     * maintains a cache of instances which may result in better performance.
     *
     * @param v
     *            the long value to store in the instance.
     * @return a {@code Long} instance containing {@code v}.
     * @since 1.5
     */
    public static Long valueOf(long v) {
        return  v >= 128 || v < -128 ? new Long(v)
                                     : SMALL_VALUES[((int) v) + 128];
    }

    /**
     * A cache of instances used by {@link Long#valueOf(long)} and auto-boxing.
     */
    private static final Long[] SMALL_VALUES = new Long[256];

    static {
        for(int i = -128; i < 128; i++) {
            SMALL_VALUES[i + 128] = new Long(i);
        }
    }
}
