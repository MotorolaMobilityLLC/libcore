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

package java.lang;

import com.ibm.icu4jni.regex.NativeRegEx;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.harmony.luni.util.PriviAction;

/**
 * An immutable sequence of characters/code units ({@code char}s). A
 * {@code String} is represented by array of UTF-16 values, such that
 * Unicode supplementary characters (code points) are stored/encoded as
 * surrogate pairs via Unicode code units ({@code char}).
 *
 * @see StringBuffer
 * @see StringBuilder
 * @see Charset
 * @since 1.0
 */
public final class String implements Serializable, Comparable<String>, CharSequence {

    private static final long serialVersionUID = -6849794470754667710L;

    private static final char REPLACEMENT_CHAR = (char) 0xfffd;

    /**
     * CaseInsensitiveComparator compares Strings ignoring the case of the
     * characters.
     */
    private static final class CaseInsensitiveComparator implements
            Comparator<String>, Serializable {
        private static final long serialVersionUID = 8575799808933029326L;

        /**
         * Compare the two objects to determine the relative ordering.
         *
         * @param o1
         *            an Object to compare
         * @param o2
         *            an Object to compare
         * @return an int < 0 if object1 is less than object2, 0 if they are
         *         equal, and > 0 if object1 is greater
         *
         * @exception ClassCastException
         *                if objects are not the correct type
         */
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }

    /**
     * A comparator ignoring the case of the characters.
     */
    public static final Comparator<String> CASE_INSENSITIVE_ORDER = new CaseInsensitiveComparator();

    private static final char[] ascii;

    private final char[] value;

    private final int offset;

    private final int count;

    private int hashCode;

    private static Charset DefaultCharset;

    private static Charset lastCharset;

    static {
        ascii = new char[128];
        for (int i = 0; i < ascii.length; i++) {
            ascii[i] = (char) i;
        }
    }

    /**
     * Creates an empty string.
     */
    public String() {
        value = new char[0];
        offset = 0;
        count = 0;
    }

    /*
     * Private constructor used for JIT optimization.
     */
    @SuppressWarnings("unused")
    private String(String s, char c) {
        offset = 0;
        value = new char[s.count + 1];
        count = s.count + 1;
        System.arraycopy(s.value, s.offset, value, 0, s.count);
        value[s.count] = c;
    }

    /**
     * Converts the byte array to a string using the default encoding as
     * specified by the file.encoding system property. If the system property is
     * not defined, the default encoding is ISO8859_1 (ISO-Latin-1). If 8859-1
     * is not available, an ASCII encoding is used.
     * 
     * @param data
     *            the byte array to convert to a string.
     */
    public String(byte[] data) {
        this(data, 0, data.length);
    }

    /**
     * Converts the byte array to a string, setting the high byte of every
     * character to the specified value.
     * 
     * @param data
     *            the byte array to convert to a string.
     * @param high
     *            the high byte to use.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @deprecated Use {@link #String(byte[])} or
     *             {@link #String(byte[], String)} instead.
     */
    @Deprecated
    public String(byte[] data, int high) {
        this(data, high, 0, data.length);
    }

    /**
     * Converts the byte array to a string using the default encoding as
     * specified by the file.encoding system property. If the system property is
     * not defined, the default encoding is ISO8859_1 (ISO-Latin-1). If 8859-1
     * is not available, an ASCII encoding is used.
     * 
     * @param data
     *            the byte array to convert to a string.
     * @param start
     *            the starting offset in the byte array.
     * @param length
     *            the number of bytes to convert.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or {@code start + length >
     *             data.length}.
     */
    public String(byte[] data, int start, int length) {
        // start + length could overflow, start/length maybe MaxInt
        if (start >= 0 && 0 <= length && length <= data.length - start) {
            offset = 0;
            Charset charset = defaultCharset();
            int result;
            CharBuffer cb = charset
                    .decode(ByteBuffer.wrap(data, start, length));
            if ((result = cb.length()) > 0) {
                value = cb.array();
                count = result;
            } else {
                count = 0;
                value = new char[0];
            }
        } else {
            throw new StringIndexOutOfBoundsException();
        }
    }

    /**
     * Converts the byte array to a string, setting the high byte of every
     * character to the specified value.
     *
     * @param data
     *            the byte array to convert to a string.
     * @param high
     *            the high byte to use.
     * @param start
     *            the starting offset in the byte array.
     * @param length
     *            the number of bytes to convert.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or
     *             {@code start + length > data.length}
     *
     * @deprecated Use {@link #String(byte[], int, int)} instead.
     */
    @Deprecated
    public String(byte[] data, int high, int start, int length) {
        if (data != null) {
            // start + length could overflow, start/length maybe MaxInt
            if (start >= 0 && 0 <= length && length <= data.length - start) {
                offset = 0;
                value = new char[length];
                count = length;
                high <<= 8;
                for (int i = 0; i < count; i++) {
                    value[i] = (char) (high + (data[start++] & 0xff));
                }
            } else {
                throw new StringIndexOutOfBoundsException();
            }
        } else {
            throw new NullPointerException();
        }
    }

    /**
     * Converts the byte array to a string using the specified encoding.
     * 
     * @param data
     *            the byte array to convert to a string.
     * @param start
     *            the starting offset in the byte array.
     * @param length
     *            the number of bytes to convert.
     * @param encoding
     *            the encoding.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or {@code start + length >
     *             data.length}.
     * @throws UnsupportedEncodingException
     *             if {@code encoding} is not supported.
     */
    public String(byte[] data, int start, int length, final String encoding)
            throws UnsupportedEncodingException {
        if (encoding == null) {
            throw new NullPointerException();
        }
        // start + length could overflow, start/length maybe MaxInt
        if (start >= 0 && 0 <= length && length <= data.length - start) {
            offset = 0;
            // BEGIN android-added
            // Special-case ISO-88589-1 and UTF 8 decoding
            if (encoding.equalsIgnoreCase("ISO-8859-1") ||
                encoding.equalsIgnoreCase("ISO8859_1")) {
                value = new char[length];
                count = length;
                for (int i = 0; i < count; i++) {
                    value[i] = (char) (data[start++] & 0xff);
                }
                return;
            } else if ("utf8".equals(encoding) ||
                       "utf-8".equals(encoding) ||
                       "UTF8".equals(encoding) ||
                       "UTF-8".equals(encoding)) {
                // We inline UTF8 decoding for speed and because a
                // non-constructor can't write directly to the final
                // members 'value' or 'count'.
                byte[] d = data;
                char[] v = new char[length];

                int idx = start, last = start + length, s = 0;
                outer:
                while (idx < last) {
                    byte b0 = d[idx++];
                    if ((b0 & 0x80) == 0) {
                        // 0xxxxxxx
                        // Range:  U-00000000 - U-0000007F
                        int val = b0 & 0xff;
                        v[s++] = (char) val;
                    } else if (((b0 & 0xe0) == 0xc0) ||
                               ((b0 & 0xf0) == 0xe0) ||
                               ((b0 & 0xf8) == 0xf0) ||
                               ((b0 & 0xfc) == 0xf8) ||
                               ((b0 & 0xfe) == 0xfc)) {
                        int utfCount = 1;
                        if ((b0 & 0xf0) == 0xe0) utfCount = 2;
                        else if ((b0 & 0xf8) == 0xf0) utfCount = 3;
                        else if ((b0 & 0xfc) == 0xf8) utfCount = 4;
                        else if ((b0 & 0xfe) == 0xfc) utfCount = 5;

                        // 110xxxxx (10xxxxxx)+
                        // Range:  U-00000080 - U-000007FF (count == 1)
                        // Range:  U-00000800 - U-0000FFFF (count == 2)
                        // Range:  U-00010000 - U-001FFFFF (count == 3)
                        // Range:  U-00200000 - U-03FFFFFF (count == 4)
                        // Range:  U-04000000 - U-7FFFFFFF (count == 5)

                        if (idx + utfCount > last) {
                            v[s++] = REPLACEMENT_CHAR;
                            break;
                        }

                        // Extract usable bits from b0
                        int val = b0 & (0x1f >> (utfCount - 1));
                        for (int i = 0; i < utfCount; i++) {
                            byte b = d[idx++];
                            if ((b & 0xC0) != 0x80) {
                                v[s++] = REPLACEMENT_CHAR;
                                idx--; // Put the input char back
                                continue outer;
                            }
                            // Push new bits in from the right side
                            val <<= 6;
                            val |= b & 0x3f;
                        }

                        // Note: Java allows overlong char
                        // specifications To disallow, check that val
                        // is greater than or equal to the minimum
                        // value for each count:
                        //
                        // count    min value
                        // -----   ----------
                        //   1           0x80
                        //   2          0x800
                        //   3        0x10000
                        //   4       0x200000
                        //   5      0x4000000

                        // Allow surrogate values (0xD800 - 0xDFFF) to
                        // be specified using 3-byte UTF values only
                        if ((utfCount != 2) &&
                            (val >= 0xD800) && (val <= 0xDFFF)) {
                            v[s++] = REPLACEMENT_CHAR;
                            continue;
                        }

                        // Reject chars greater than the Unicode
                        // maximum of U+10FFFF
                        if (val > 0x10FFFF) {
                            v[s++] = REPLACEMENT_CHAR;
                            continue;
                        }

                        // Encode chars from U+10000 up as surrogate pairs
                        if (val < 0x10000) {
                            v[s++] = (char) val;
                        } else {
                            int x = val & 0xffff;
                            int u = (val >> 16) & 0x1f;
                            int w = (u - 1) & 0xffff;
                            int hi = 0xd800 | (w << 6) | (x >> 10);
                            int lo = 0xdc00 | (x & 0x3ff);
                            v[s++] = (char) hi;
                            v[s++] = (char) lo;
                        }
                    } else {
                        // Illegal values 0x8*, 0x9*, 0xa*, 0xb*, 0xfd-0xff
                        v[s++] = REPLACEMENT_CHAR;
                    }
                }

                // Reallocate the array to fit the contents
                count = s;
                value = new char[s];
                System.arraycopy(v, 0, value, 0, s);
                return;
            }
            // END android-added
            Charset charset = getCharset(encoding);

            int result;
            CharBuffer cb;
            try {
                cb = charset.decode(ByteBuffer.wrap(data, start, length));
            } catch (Exception e) {
                // do nothing. according to spec:
                // behavior is unspecified for invalid array
                cb = CharBuffer.wrap("\u003f".toCharArray());
            }
            if ((result = cb.length()) > 0) {
                value = cb.array();
                count = result;
            } else {
                count = 0;
                value = new char[0];
            }
        } else {
            throw new StringIndexOutOfBoundsException();
        }
    }

    /**
     * Converts the byte array to a string using the specified encoding.
     * 
     * @param data
     *            the byte array to convert to a string.
     * @param encoding
     *            the encoding.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws UnsupportedEncodingException
     *             if {@code encoding} is not supported.
     */
    public String(byte[] data, String encoding) throws UnsupportedEncodingException {
        this(data, 0, data.length, encoding);
    }

    /**
     * Converts the byte array to a String using the specified encoding.
     * 
     * @param data
     *            the byte array to convert to a String
     * @param start
     *            the starting offset in the byte array
     * @param length
     *            the number of bytes to convert
     * @param encoding
     *            the encoding
     * 
     * @throws IndexOutOfBoundsException
     *             when <code>length &lt; 0, start &lt; 0</code> or
     *             <code>start + length &gt; data.length</code>
     * @throws NullPointerException
     *             when data is null
     * 
     * @see #getBytes()
     * @see #getBytes(int, int, byte[], int)
     * @see #getBytes(String)
     * @see #valueOf(boolean)
     * @see #valueOf(char)
     * @see #valueOf(char[])
     * @see #valueOf(char[], int, int)
     * @see #valueOf(double)
     * @see #valueOf(float)
     * @see #valueOf(int)
     * @see #valueOf(long)
     * @see #valueOf(Object)
     * @since 1.6
     * @hide
     */
    public String(byte[] data, int start, int length, final Charset encoding) {
        if (encoding == null) {
            throw new NullPointerException();
        }
        if (start < 0 || length < 0 || length > data.length - start) {
            throw new StringIndexOutOfBoundsException();
        }
        CharBuffer cb = encoding.decode(ByteBuffer.wrap(data, start, length));
        this.lastCharset = encoding;
        this.offset = 0;
        this.count = cb.length();
        this.value = new char[count];
        System.arraycopy(cb.array(), 0, value, 0, count);
    }

    /**
     * Converts the byte array to a String using the specified encoding.
     * 
     * @param data
     *            the byte array to convert to a String
     * @param encoding
     *            the encoding
     * 
     * @throws NullPointerException
     *             when data is null
     * 
     * @see #getBytes()
     * @see #getBytes(int, int, byte[], int)
     * @see #getBytes(String)
     * @see #valueOf(boolean)
     * @see #valueOf(char)
     * @see #valueOf(char[])
     * @see #valueOf(char[], int, int)
     * @see #valueOf(double)
     * @see #valueOf(float)
     * @see #valueOf(int)
     * @see #valueOf(long)
     * @see #valueOf(Object)
     * @since 1.6
     * @hide
     */
    public String(byte[] data, Charset encoding) {
        this(data, 0, data.length, encoding);
    }
    
    /**
     * Initializes this string to contain the characters in the specified
     * character array. Modifying the character array after creating the string
     * has no effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     */
    public String(char[] data) {
        this(data, 0, data.length);
    }

    /**
     * Initializes this string to contain the specified characters in the
     * character array. Modifying the character array after creating the string
     * has no effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @param start
     *            the starting offset in the character array.
     * @param length
     *            the number of characters to use.
     * @throws NullPointerException
     *             when {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or {@code start + length >
     *             data.length}
     */
    public String(char[] data, int start, int length) {
        // range check everything so a new char[] is not created
        // start + length could overflow, start/length maybe MaxInt
        if (start >= 0 && 0 <= length && length <= data.length - start) {
            offset = 0;
            value = new char[length];
            count = length;
            System.arraycopy(data, start, value, 0, count);
        } else {
            throw new StringIndexOutOfBoundsException();
        }
    }

    /*
     * Internal version of string constructor. Does not range check, null check,
     * or copy the character array.
     */
    String(int start, int length, char[] data) {
        value = data;
        offset = start;
        count = length;
    }

    /**
     * Creates a {@code String} that is a copy of the specified string.
     * 
     * @param string
     *            the string to copy.
     */
    public String(String string) {
        value = string.value;
        offset = string.offset;
        count = string.count;
    }

    /*
     * Private constructor useful for JIT optimization.
     */
    @SuppressWarnings( { "unused", "nls" })
    private String(String s1, String s2) {
        if (s1 == null) {
            s1 = "null";
        }
        if (s2 == null) {
            s2 = "null";
        }
        count = s1.count + s2.count;
        value = new char[count];
        offset = 0;
        System.arraycopy(s1.value, s1.offset, value, 0, s1.count);
        System.arraycopy(s2.value, s2.offset, value, s1.count, s2.count);
    }

    /*
     * Private constructor useful for JIT optimization.
     */
    @SuppressWarnings( { "unused", "nls" })
    private String(String s1, String s2, String s3) {
        if (s1 == null) {
            s1 = "null";
        }
        if (s2 == null) {
            s2 = "null";
        }
        if (s3 == null) {
            s3 = "null";
        }
        count = s1.count + s2.count + s3.count;
        value = new char[count];
        offset = 0;
        System.arraycopy(s1.value, s1.offset, value, 0, s1.count);
        System.arraycopy(s2.value, s2.offset, value, s1.count, s2.count);
        System.arraycopy(s3.value, s3.offset, value, s1.count + s2.count,
                s3.count);
    }

    /**
     * Creates a {@code String} from the contents of the specified
     * {@code StringBuffer}.
     * 
     * @param stringbuffer
     *            the buffer to get the contents from.
     */
    public String(StringBuffer stringbuffer) {
        offset = 0;
        synchronized (stringbuffer) {
            value = stringbuffer.shareValue();
            count = stringbuffer.length();
        }
    }

    /**
     * Creates a {@code String} from the sub-array of Unicode code points.
     *
     * @param codePoints
     *            the array of Unicode code points to convert.
     * @param offset
     *            the inclusive index into {@code codePoints} to begin
     *            converting from.
     * @param count
     *            the number of elements in {@code codePoints} to copy.
     * @throws NullPointerException
     *             if {@code codePoints} is {@code null}.
     * @throws IllegalArgumentException
     *             if any of the elements of {@code codePoints} are not valid
     *             Unicode code points.
     * @throws IndexOutOfBoundsException
     *             if {@code offset} or {@code count} are not within the bounds
     *             of {@code codePoints}.
     * @since 1.5
     */
    public String(int[] codePoints, int offset, int count) {
        super();
        if (codePoints == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || count < 0 || (long) offset + (long) count > codePoints.length) {
            throw new StringIndexOutOfBoundsException();
        }
        this.offset = 0;
        this.value = new char[count * 2];
        int end = offset + count;
        int c = 0;
        for (int i = offset; i < end; i++) {
            c += Character.toChars(codePoints[i], this.value, c);
        }
        this.count = c;
    }

    /**
     * Creates a {@code String} from the contents of the specified {@code
     * StringBuilder}.
     * 
     * @param sb
     *            the {@code StringBuilder} to copy the contents from.
     * @throws NullPointerException
     *             if {@code sb} is {@code null}.
     * @since 1.5
     */
    public String(StringBuilder sb) {
        if (sb == null) {
            throw new NullPointerException();
        }
        this.offset = 0;
        this.count = sb.length();
        this.value = new char[this.count];
        sb.getChars(0, this.count, this.value, 0);
    }

    /*
     * Creates a {@code String} that is s1 + v1. May be used by JIT code.
     */
    @SuppressWarnings("unused")
    private String(String s1, int v1) {
        if (s1 == null) {
            s1 = "null";
        }
        String s2 = String.valueOf(v1);
        int len = s1.count + s2.count;
        value = new char[len];
        offset = 0;
        System.arraycopy(s1.value, s1.offset, value, 0, s1.count);
        System.arraycopy(s2.value, s2.offset, value, s1.count, s2.count);
        count = len;
    }

    /**
     * Returns the character at the specified offset in this string.
     * 
     * @param index
     *            the zero-based index in this string.
     * @return the character at the index.
     * @throws IndexOutOfBoundsException
     *             if {@code index < 0} or {@code index >= length()}.
     */
    public char charAt(int index) {
        if (0 <= index && index < count) {
            return value[offset + index];
        }
        throw new StringIndexOutOfBoundsException();
    }

    // Optimized for ASCII
    private char compareValue(char ch) {
        if (ch < 128) {
            if ('A' <= ch && ch <= 'Z') {
                return (char) (ch + ('a' - 'A'));
            }
            return ch;
        }
        return Character.toLowerCase(Character.toUpperCase(ch));
    }

    /**
     * Compares the specified string to this string using the Unicode values of
     * the characters. Returns 0 if the strings contain the same characters in
     * the same order. Returns a negative integer if the first non-equal
     * character in this string has a Unicode value which is less than the
     * Unicode value of the character at the same position in the specified
     * string, or if this string is a prefix of the specified string. Returns a
     * positive integer if the first non-equal character in this string has a
     * Unicode value which is greater than the Unicode value of the character at
     * the same position in the specified string, or if the specified string is
     * a prefix of this string.
     * 
     * @param string
     *            the string to compare.
     * @return 0 if the strings are equal, a negative integer if this string is
     *         before the specified string, or a positive integer if this string
     *         is after the specified string.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public int compareTo(String string) {
        // Code adapted from K&R, pg 101
        int o1 = offset, o2 = string.offset, result;
        int end = offset + (count < string.count ? count : string.count);
        char[] target = string.value;
        while (o1 < end) {
            if ((result = value[o1++] - target[o2++]) != 0) {
                return result;
            }
        }
        return count - string.count;
    }

    /**
     * Compares the specified string to this string using the Unicode values of
     * the characters, ignoring case differences. Returns 0 if the strings
     * contain the same characters in the same order. Returns a negative integer
     * if the first non-equal character in this string has a Unicode value which
     * is less than the Unicode value of the character at the same position in
     * the specified string, or if this string is a prefix of the specified
     * string. Returns a positive integer if the first non-equal character in
     * this string has a Unicode value which is greater than the Unicode value
     * of the character at the same position in the specified string, or if the
     * specified string is a prefix of this string.
     * 
     * @param string
     *            the string to compare.
     * @return 0 if the strings are equal, a negative integer if this string is
     *         before the specified string, or a positive integer if this string
     *         is after the specified string.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public int compareToIgnoreCase(String string) {
        int o1 = offset, o2 = string.offset, result;
        int end = offset + (count < string.count ? count : string.count);
        char c1, c2;
        char[] target = string.value;
        while (o1 < end) {
            if ((c1 = value[o1++]) == (c2 = target[o2++])) {
                continue;
            }
            c1 = compareValue(c1);
            c2 = compareValue(c2);
            if ((result = c1 - c2) != 0) {
                return result;
            }
        }
        return count - string.count;
    }

    /**
     * Concatenates this string and the specified string.
     * 
     * @param string
     *            the string to concatenate
     * @return a new string which is the concatenation of this string and the
     *         specified string.
     */
    public String concat(String string) {
        if (string.count > 0 && count > 0) {
            char[] buffer = new char[count + string.count];
            System.arraycopy(value, offset, buffer, 0, count);
            System.arraycopy(string.value, string.offset, buffer, count,
                    string.count);
            return new String(0, buffer.length, buffer);
        }
        return count == 0 ? string : this;
    }

    /**
     * Creates a new string containing the characters in the specified character
     * array. Modifying the character array after creating the string has no
     * effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @return the new string.
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     */
    public static String copyValueOf(char[] data) {
        return new String(data, 0, data.length);
    }

    /**
     * Creates a new string containing the specified characters in the character
     * array. Modifying the character array after creating the string has no
     * effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @param start
     *            the starting offset in the character array.
     * @param length
     *            the number of characters to use.
     * @return the new string.
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0, start < 0} or {@code start + length >
     *             data.length}.
     */
    public static String copyValueOf(char[] data, int start, int length) {
        return new String(data, start, length);
    }

    private Charset defaultCharset() {
        if (DefaultCharset == null) {
            String encoding = AccessController
                    .doPrivileged(new PriviAction<String>("file.encoding", "ISO8859_1"));
            // calling System.getProperty() may cause DefaultCharset to be
            // initialized
            try {
                DefaultCharset = Charset.forName(encoding);
            } catch (IllegalCharsetNameException e) {
                // Ignored
            } catch (UnsupportedCharsetException e) {
                // Ignored
            }

            if (DefaultCharset == null) {
                DefaultCharset = Charset.forName("ISO-8859-1");
            }
        }
        return DefaultCharset;
    }

    /**
     * Compares the specified string to this string to determine if the
     * specified string is a suffix.
     * 
     * @param suffix
     *            the suffix to look for.
     * @return {@code true} if the specified string is a suffix of this string,
     *         {@code false} otherwise.
     * @throws NullPointerException
     *             if {@code suffix} is {@code null}.
     */
    public boolean endsWith(String suffix) {
        return regionMatches(count - suffix.count, suffix, 0, suffix.count);
    }

    /**
     * Compares the specified object to this string and returns true if they are
     * equal. The object must be an instance of string with the same characters
     * in the same order.
     * 
     * @param object
     *            the object to compare.
     * @return {@code true} if the specified object is equal to this string,
     *         {@code false} otherwise.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof String) {
            String s = (String) object;
            // BEGIN android-changed
            int hashCode1 = hashCode;
            int hashCode2 = s.hashCode;
            if (count != s.count
                || (hashCode1 != hashCode2 && hashCode1 != 0 && hashCode2 != 0)) {
                return false;
            }
            // inline 'return regionMatches(0, s, 0, count)'
            // omitting unnecessary bounds checks
            int o1 = offset, o2 = s.offset;
            char[] value1 = value;
            char[] value2 = s.value;
            for (int i = 0; i < count; ++i) {
                if (value1[o1 + i] != value2[o2 + i]) {
                    return false;
                }
            }
            return true;
            // END android-changed
        }
        return false;
    }

    /**
     * Compares the specified string to this string ignoring the case of the
     * characters and returns true if they are equal.
     * 
     * @param string
     *            the string to compare.
     * @return {@code true} if the specified string is equal to this string,
     *         {@code false} otherwise.
     */
    public boolean equalsIgnoreCase(String string) {
        if (string == this) {
            return true;
        }
        if (string == null || count != string.count) {
            return false;
        }

        int o1 = offset, o2 = string.offset;
        int end = offset + count;
        char c1, c2;
        char[] target = string.value;
        while (o1 < end) {
            if ((c1 = value[o1++]) != (c2 = target[o2++])
                    && Character.toUpperCase(c1) != Character.toUpperCase(c2)
                    // Required for unicode that we test both cases
                    && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts this string to a byte array using the default encoding as
     * specified by the file.encoding system property. If the system property is
     * not defined, the default encoding is ISO8859_1 (ISO-Latin-1). If 8859-1
     * is not available, an ASCII encoding is used.
     * 
     * @return the byte array encoding of this string.
     */
    public byte[] getBytes() {
        return getBytes(defaultCharset());
    }

    /**
     * Converts this string to a byte array, ignoring the high order bits of
     * each character.
     * 
     * @param start
     *            the starting offset of characters to copy.
     * @param end
     *            the ending offset of characters to copy.
     * @param data
     *            the destination byte array.
     * @param index
     *            the starting offset in the destination byte array.
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0}, {@code end > length()}, {@code index <
     *             0} or {@code end - start > data.length - index}.
     * @deprecated Use {@link #getBytes()} or {@link #getBytes(String)}
     */
    @Deprecated
    public void getBytes(int start, int end, byte[] data, int index) {
        if (0 <= start && start <= end && end <= count) {
            end += offset;
            try {
                for (int i = offset + start; i < end; i++) {
                    data[index++] = (byte) value[i];
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new StringIndexOutOfBoundsException();
            }
        } else {
            throw new StringIndexOutOfBoundsException();
        }
    }

    /**
     * Converts this string to a byte array using the specified encoding.
     * 
     * @param encoding
     *            the encoding to use.
     * @return the encoded byte array of this string.
     * @throws UnsupportedEncodingException
     *             if the encoding is not supported.
     */
    public byte[] getBytes(String encoding) throws UnsupportedEncodingException {
        return getBytes(getCharset(encoding));
    }

    private Charset getCharset(final String encoding)
            throws UnsupportedEncodingException {
        Charset charset = lastCharset;
        if (charset == null || !encoding.equalsIgnoreCase(charset.name())) {
            try {
                charset = Charset.forName(encoding);
            } catch (IllegalCharsetNameException e) {
                throw (UnsupportedEncodingException) (new UnsupportedEncodingException(
                        encoding).initCause(e));
            } catch (UnsupportedCharsetException e) {
                throw (UnsupportedEncodingException) (new UnsupportedEncodingException(
                        encoding).initCause(e));
            }
            lastCharset = charset;
        }
        return charset;
    }

    /**
     * Returns a new byte array containing the characters of this string encoded in the
     * given charset.
     * 
     * @param encoding the encoding
     * 
     * @since 1.6
     * @hide
     */
    public byte[] getBytes(Charset encoding) {
        CharBuffer chars = CharBuffer.wrap(this.value, this.offset, this.count);
        ByteBuffer buffer = encoding.encode(chars.asReadOnlyBuffer());
        byte[] bytes = new byte[buffer.limit()];
        buffer.get(bytes);
        return bytes;
    }

    /**
     * Copies the specified characters in this string to the character array
     * starting at the specified offset in the character array.
     * 
     * @param start
     *            the starting offset of characters to copy.
     * @param end
     *            the ending offset of characters to copy.
     * @param buffer
     *            the destination character array.
     * @param index
     *            the starting offset in the character array.
     * @throws NullPointerException
     *             if {@code buffer} is {@code null}.
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0}, {@code end > length()}, {@code start >
     *             end}, {@code index < 0}, {@code end - start > buffer.length -
     *             index}
     */
    public void getChars(int start, int end, char[] buffer, int index) {
        // NOTE last character not copied!
        // Fast range check.
        if (0 <= start && start <= end && end <= count) {
            System.arraycopy(value, start + offset, buffer, index, end - start);
        } else {
            throw new StringIndexOutOfBoundsException();
        }
    }

    // BEGIN android-added
    /**
     * Version of getChars without bounds checks, for use by other classes
     * within the java.lang package only.  The caller is responsible for
     * ensuring that 0 <= start && start <= end && end <= count.
     */
    void _getChars(int start, int end, char[] buffer, int index) {
        // NOTE last character not copied!
        System.arraycopy(value, start + offset, buffer, index, end - start);
    }
    // END android-added

    @Override
    public int hashCode() {
        // BEGIN android-changed
        int hash = hashCode;
        if (hash == 0) {
            int multiplier = 1;
            int _offset = offset;
            int _count = count;
            char[] _value = value;
            for (int i = _offset + _count - 1; i >= _offset; i--) {
                hash += _value[i] * multiplier;
                int shifted = multiplier << 5;
                multiplier = shifted - multiplier;
            }
            hashCode = hash;
        }
        return hash;
        // END android-changed
    }

    /**
     * Searches in this string for the first index of the specified character.
     * The search for the character starts at the beginning and moves towards
     * the end of this string.
     * 
     * @param c
     *            the character to find.
     * @return the index in this string of the specified character, -1 if the
     *         character isn't found.
     */
    public int indexOf(int c) {
        // TODO: just "return indexOf(c, 0);" when the JIT can inline that deep.
        if (c > 0xffff) {
            return indexOfSupplementary(c, 0);
        }
        return fastIndexOf(c, 0);
    }

    /**
     * Searches in this string for the index of the specified character. The
     * search for the character starts at the specified offset and moves towards
     * the end of this string.
     * 
     * @param c
     *            the character to find.
     * @param start
     *            the starting offset.
     * @return the index in this string of the specified character, -1 if the
     *         character isn't found.
     */
    public int indexOf(int c, int start) {
        if (c > 0xffff) {
            return indexOfSupplementary(c, start);
        }
        return fastIndexOf(c, start);
    }

    private int fastIndexOf(int c, int start) {
        // BEGIN android-changed
        int _count = count;
        if (start < _count) {
            if (start < 0) {
                start = 0;
            }
            int _offset = offset;
            int last = _offset + count;
            char[] _value = value;
            for (int i = _offset + start; i < last; i++) {
                if (_value[i] == c) {
                    return i - _offset;
                }
            }
        }
        return -1;
        // END android-changed
    }

    private int indexOfSupplementary(int c, int start) {
        if (!Character.isSupplementaryCodePoint(c)) {
            return -1;
        }
        char[] chars = Character.toChars(c);
        String needle = new String(0, chars.length, chars);
        return indexOf(needle, start);
    }

    /**
     * Searches in this string for the first index of the specified string. The
     * search for the string starts at the beginning and moves towards the end
     * of this string.
     * 
     * @param string
     *            the string to find.
     * @return the index of the first character of the specified string in this
     *         string, -1 if the specified string is not a substring.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public int indexOf(String string) {
        // BEGIN android-changed
        int start = 0;
        int subCount = string.count;
        int _count = count;
        if (subCount > 0) {
            if (subCount > _count) {
                return -1;
            }
            char[] target = string.value;
            int subOffset = string.offset;
            char firstChar = target[subOffset];
            int end = subOffset + subCount;
            while (true) {
                int i = indexOf(firstChar, start);
                if (i == -1 || subCount + i > _count) {
                    return -1; // handles subCount > count || start >= count
                }
                int o1 = offset + i, o2 = subOffset;
                char[] _value = value;
                while (++o2 < end && _value[++o1] == target[o2]) {
                    // Intentionally empty
                }
                if (o2 == end) {
                    return i;
                }
                start = i + 1;
            }
        }
        return start < _count ? start : _count;
        // END android-changed
    }

    /**
     * Searches in this string for the index of the specified string. The search
     * for the string starts at the specified offset and moves towards the end
     * of this string.
     * 
     * @param subString
     *            the string to find.
     * @param start
     *            the starting offset.
     * @return the index of the first character of the specified string in this
     *         string, -1 if the specified string is not a substring.
     * @throws NullPointerException
     *             if {@code subString} is {@code null}.
     */
    public int indexOf(String subString, int start) {
        // BEGIN android-changed
        if (start < 0) {
            start = 0;
        }
        int subCount = subString.count;
        int _count = count;
        if (subCount > 0) {
            if (subCount + start > _count) {
                return -1;
            }
            char[] target = subString.value;
            int subOffset = subString.offset;
            char firstChar = target[subOffset];
            int end = subOffset + subCount;
            while (true) {
                int i = indexOf(firstChar, start);
                if (i == -1 || subCount + i > _count) {
                    return -1; // handles subCount > count || start >= count
                }
                int o1 = offset + i, o2 = subOffset;
                char[] _value = value;
                while (++o2 < end && _value[++o1] == target[o2]) {
                    // Intentionally empty
                }
                if (o2 == end) {
                    return i;
                }
                start = i + 1;
            }
        }
        return start < _count ? start : _count;
        // END android-changed
    }

    /**
     * Searches an internal table of strings for a string equal to this string.
     * If the string is not in the table, it is added. Returns the string
     * contained in the table which is equal to this string. The same string
     * object is always returned for strings which are equal.
     * 
     * @return the interned string equal to this string.
     */
    native public String intern();

    /**
     * Returns true if the length of this string is 0.
     * 
     * @since 1.6
     * @hide
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * Searches in this string for the last index of the specified character.
     * The search for the character starts at the end and moves towards the
     * beginning of this string.
     * 
     * @param c
     *            the character to find.
     * @return the index in this string of the specified character, -1 if the
     *         character isn't found.
     */
    public int lastIndexOf(int c) {
        if (c > 0xffff) {
            return lastIndexOfSupplementary(c, Integer.MAX_VALUE);
        }
        // BEGIN android-changed
        int _count = count;
        int _offset = offset;
        char[] _value = value;
        for (int i = _offset + _count - 1; i >= _offset; --i) {
            if (_value[i] == c) {
                return i - _offset;
            }
        }
        return -1;
        // END android-changed
    }

    /**
     * Searches in this string for the index of the specified character. The
     * search for the character starts at the specified offset and moves towards
     * the beginning of this string.
     * 
     * @param c
     *            the character to find.
     * @param start
     *            the starting offset.
     * @return the index in this string of the specified character, -1 if the
     *         character isn't found.
     */
    public int lastIndexOf(int c, int start) {
        if (c > 0xffff) {
            return lastIndexOfSupplementary(c, start);
        }
        // BEGIN android-changed
        int _count = count;
        int _offset = offset;
        char[] _value = value;
        if (start >= 0) {
            if (start >= _count) {
                start = _count - 1;
            }
            for (int i = _offset + start; i >= _offset; --i) {
                if (_value[i] == c) {
                    return i - _offset;
                }
            }
        }
        return -1;
        // END android-changed
    }

    private int lastIndexOfSupplementary(int c, int start) {
        if (!Character.isSupplementaryCodePoint(c)) {
            return -1;
        }
        char[] chars = Character.toChars(c);
        String needle = new String(0, chars.length, chars);
        return lastIndexOf(needle, start);
    }

    /**
     * Searches in this string for the last index of the specified string. The
     * search for the string starts at the end and moves towards the beginning
     * of this string.
     * 
     * @param string
     *            the string to find.
     * @return the index of the first character of the specified string in this
     *         string, -1 if the specified string is not a substring.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public int lastIndexOf(String string) {
        // Use count instead of count - 1 so lastIndexOf("") returns count
        return lastIndexOf(string, count);
    }

    /**
     * Searches in this string for the index of the specified string. The search
     * for the string starts at the specified offset and moves towards the
     * beginning of this string.
     * 
     * @param subString
     *            the string to find.
     * @param start
     *            the starting offset.
     * @return the index of the first character of the specified string in this
     *         string , -1 if the specified string is not a substring.
     * @throws NullPointerException
     *             if {@code subString} is {@code null}.
     */
    public int lastIndexOf(String subString, int start) {
        int subCount = subString.count;
        if (subCount <= count && start >= 0) {
            if (subCount > 0) {
                if (start > count - subCount) {
                    start = count - subCount;
                }
                // count and subCount are both >= 1
                char[] target = subString.value;
                int subOffset = subString.offset;
                char firstChar = target[subOffset];
                int end = subOffset + subCount;
                while (true) {
                    int i = lastIndexOf(firstChar, start);
                    if (i == -1) {
                        return -1;
                    }
                    int o1 = offset + i, o2 = subOffset;
                    while (++o2 < end && value[++o1] == target[o2]) {
                        // Intentionally empty
                    }
                    if (o2 == end) {
                        return i;
                    }
                    start = i - 1;
                }
            }
            return start < count ? start : count;
        }
        return -1;
    }

    /**
     * Returns the size of this string.
     * 
     * @return the number of characters in this string.
     */
    public int length() {
        return count;
    }

    /**
     * Compares the specified string to this string and compares the specified
     * range of characters to determine if they are the same.
     * 
     * @param thisStart
     *            the starting offset in this string.
     * @param string
     *            the string to compare.
     * @param start
     *            the starting offset in the specified string.
     * @param length
     *            the number of characters to compare.
     * @return {@code true} if the ranges of characters are equal, {@code false}
     *         otherwise
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public boolean regionMatches(int thisStart, String string, int start,
            int length) {
        if (string == null) {
            throw new NullPointerException();
        }
        if (start < 0 || string.count - start < length) {
            return false;
        }
        if (thisStart < 0 || count - thisStart < length) {
            return false;
        }
        if (length <= 0) {
            return true;
        }
        int o1 = offset + thisStart, o2 = string.offset + start;
        // BEGIN android-changed
        char[] value1 = value;
        char[] value2 = string.value;
        for (int i = 0; i < length; ++i) {
            if (value1[o1 + i] != value2[o2 + i]) {
                return false;
            }
        }
        // END android-changed
        return true;
    }

    /**
     * Compares the specified string to this string and compares the specified
     * range of characters to determine if they are the same. When ignoreCase is
     * true, the case of the characters is ignored during the comparison.
     * 
     * @param ignoreCase
     *            specifies if case should be ignored.
     * @param thisStart
     *            the starting offset in this string.
     * @param string
     *            the string to compare.
     * @param start
     *            the starting offset in the specified string.
     * @param length
     *            the number of characters to compare.
     * @return {@code true} if the ranges of characters are equal, {@code false}
     *         otherwise.
     * @throws NullPointerException
     *             if {@code string} is {@code null}.
     */
    public boolean regionMatches(boolean ignoreCase, int thisStart,
            String string, int start, int length) {
        if (!ignoreCase) {
            return regionMatches(thisStart, string, start, length);
        }

        if (string != null) {
            if (thisStart < 0 || length > count - thisStart) {
                return false;
            }
            if (start < 0 || length > string.count - start) {
                return false;
            }

            thisStart += offset;
            start += string.offset;
            int end = thisStart + length;
            char c1, c2;
            char[] target = string.value;
            while (thisStart < end) {
                if ((c1 = value[thisStart++]) != (c2 = target[start++])
                        && Character.toUpperCase(c1) != Character.toUpperCase(c2)
                        // Required for unicode that we test both cases
                        && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                    return false;
                }
            }
            return true;
        }
        throw new NullPointerException();
    }

    /**
     * Copies this string replacing occurrences of the specified character with
     * another character.
     * 
     * @param oldChar
     *            the character to replace.
     * @param newChar
     *            the replacement character.
     * @return a new string with occurrences of oldChar replaced by newChar.
     */
    public String replace(char oldChar, char newChar) {
        // BEGIN endroid-changed
        char[] buffer = value;
        int _offset = offset;
        int _count = count;

        int idx = _offset;
        int last = _offset + _count;
        boolean copied = false;
        while (idx < last) {
            if (buffer[idx] == oldChar) {
                if (!copied) {
                    char[] newBuffer = new char[_count];
                    System.arraycopy(buffer, _offset, newBuffer, 0, _count);
                    buffer = newBuffer;
                    idx -= _offset;
                    last -= _offset;
                    copied = true;
                }
                buffer[idx] = newChar;
            }
            idx++;
        }

        return copied ? new String(0, count, buffer) : this;
        // END android-changed
    }
    
    /**
     * Copies this string replacing occurrences of the specified target sequence
     * with another sequence. The string is processed from the beginning to the
     * end.
     * 
     * @param target
     *            the sequence to replace.
     * @param replacement
     *            the replacement sequence.
     * @return the resulting string.
     * @throws NullPointerException
     *             if {@code target} or {@code replacement} is {@code null}.
     */
    public String replace(CharSequence target, CharSequence replacement) {
        if (target == null) {
            throw new NullPointerException("target should not be null");
        }
        if (replacement == null) {
            throw new NullPointerException("replacement should not be null");
        }
        String ts = target.toString();
        int index = indexOf(ts, 0);

        if (index == -1)
            return this;

        String rs = replacement.toString();
        StringBuilder buffer = new StringBuilder(count);
        int tl = target.length();
        int tail = 0;
        do {
            buffer.append(value, offset + tail, index - tail);
            buffer.append(rs);
            tail = index + tl;
        } while ((index = indexOf(ts, tail)) != -1);
        //append trailing chars
        buffer.append(value, offset + tail, count - tail);

        return buffer.toString();
    }

    /**
     * Compares the specified string to this string to determine if the
     * specified string is a prefix.
     * 
     * @param prefix
     *            the string to look for.
     * @return {@code true} if the specified string is a prefix of this string,
     *         {@code false} otherwise
     * @throws NullPointerException
     *             if {@code prefix} is {@code null}.
     */
    public boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }

    /**
     * Compares the specified string to this string, starting at the specified
     * offset, to determine if the specified string is a prefix.
     * 
     * @param prefix
     *            the string to look for.
     * @param start
     *            the starting offset.
     * @return {@code true} if the specified string occurs in this string at the
     *         specified offset, {@code false} otherwise.
     * @throws NullPointerException
     *             if {@code prefix} is {@code null}.
     */
    public boolean startsWith(String prefix, int start) {
        return regionMatches(start, prefix, 0, prefix.count);
    }

    /**
     * Copies a range of characters into a new string.
     * 
     * @param start
     *            the offset of the first character.
     * @return a new string containing the characters from start to the end of
     *         the string.
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0} or {@code start > length()}.
     */
    public String substring(int start) {
        if (start == 0) {
            return this;
        }
        if (0 <= start && start <= count) {
            return new String(offset + start, count - start, value);
        }
        throw new StringIndexOutOfBoundsException(start);
    }

    /**
     * Copies a range of characters into a new string.
     * 
     * @param start
     *            the offset of the first character.
     * @param end
     *            the offset one past the last character.
     * @return a new string containing the characters from start to end - 1
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0}, {@code start > end} or {@code end >
     *             length()}.
     */
    public String substring(int start, int end) {
        if (start == 0 && end == count) {
            return this;
        }
        // NOTE last character not copied!
        // Fast range check.
        if (0 <= start && start <= end && end <= count) {
            return new String(offset + start, end - start, value);
        }
        throw new StringIndexOutOfBoundsException();
    }

    /**
     * Copies the characters in this string to a character array.
     * 
     * @return a character array containing the characters of this string.
     */
    public char[] toCharArray() {
        char[] buffer = new char[count];
        System.arraycopy(value, offset, buffer, 0, count);
        return buffer;
    }

    /**
     * Converts this string to lowercase, using the rules of the default locale.
     * 
     * @return a new lowercase string, or {@code this} if it's already all-lowercase.
     */
    public String toLowerCase() {
        return CaseMapper.toLowerCase(Locale.getDefault(), this, value, offset, count);
    }

    /**
     * Converts this string to lowercase, using the rules of {@code locale}.
     *
     * <p>Most case mappings are unaffected by the language of a {@code Locale}. Exceptions include
     * dotted and dotless I in Azeri and Turkish locales, and dotted and dotless I and J in
     * Lithuanian locales. On the other hand, it isn't necessary to provide a Greek locale to get
     * correct case mapping of Greek characters: any locale will do.
     *
     * <p>See <a href="http://www.unicode.org/Public/UNIDATA/SpecialCasing.txt">http://www.unicode.org/Public/UNIDATA/SpecialCasing.txt</a>
     * for full details of context- and language-specific special cases.
     *
     * @return a new lowercase string, or {@code this} if it's already all-lowercase.
     */
    public String toLowerCase(Locale locale) {
        return CaseMapper.toLowerCase(locale, this, value, offset, count);
    }

    /**
     * Returns this string.
     *
     * @return this string.
     */
    @Override
    public String toString() {
        return this;
    }

    /**
     * Converts this this string to uppercase, using the rules of the default locale.
     * 
     * @return a new uppercase string, or {@code this} if it's already all-uppercase.
     */
    public String toUpperCase() {
        return CaseMapper.toUpperCase(Locale.getDefault(), this, value, offset, count);
    }

    /**
     * Converts this this string to uppercase, using the rules of {@code locale}.
     *
     * <p>Most case mappings are unaffected by the language of a {@code Locale}. Exceptions include
     * dotted and dotless I in Azeri and Turkish locales, and dotted and dotless I and J in
     * Lithuanian locales. On the other hand, it isn't necessary to provide a Greek locale to get
     * correct case mapping of Greek characters: any locale will do.
     *
     * <p>See <a href="http://www.unicode.org/Public/UNIDATA/SpecialCasing.txt">http://www.unicode.org/Public/UNIDATA/SpecialCasing.txt</a>
     * for full details of context- and language-specific special cases.
     *
     * @return a new uppercase string, or {@code this} if it's already all-uppercase.
     */
    public String toUpperCase(Locale locale) {
        return CaseMapper.toUpperCase(locale, this, value, offset, count);
    }

    /**
     * Copies this string removing white space characters from the beginning and
     * end of the string.
     * 
     * @return a new string with characters <code><= \\u0020</code> removed from
     *         the beginning and the end.
     */
    public String trim() {
        int start = offset, last = offset + count - 1;
        int end = last;
        while ((start <= end) && (value[start] <= ' ')) {
            start++;
        }
        while ((end >= start) && (value[end] <= ' ')) {
            end--;
        }
        if (start == offset && end == last) {
            return this;
        }
        return new String(start, end - start + 1, value);
    }

    /**
     * Creates a new string containing the characters in the specified character
     * array. Modifying the character array after creating the string has no
     * effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @return the new string.
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     */
    public static String valueOf(char[] data) {
        return new String(data, 0, data.length);
    }

    /**
     * Creates a new string containing the specified characters in the character
     * array. Modifying the character array after creating the string has no
     * effect on the string.
     * 
     * @param data
     *            the array of characters.
     * @param start
     *            the starting offset in the character array.
     * @param length
     *            the number of characters to use.
     * @return the new string.
     * @throws IndexOutOfBoundsException
     *             if {@code length < 0}, {@code start < 0} or {@code start +
     *             length > data.length}
     * @throws NullPointerException
     *             if {@code data} is {@code null}.
     */
    public static String valueOf(char[] data, int start, int length) {
        return new String(data, start, length);
    }

    /**
     * Converts the specified character to its string representation.
     * 
     * @param value
     *            the character.
     * @return the character converted to a string.
     */
    public static String valueOf(char value) {
        String s;
        if (value < 128) {
            s = new String(value, 1, ascii);
        } else {
            s = new String(0, 1, new char[] { value });
        }
        s.hashCode = value;
        return s;
    }

    /**
     * Converts the specified double to its string representation.
     * 
     * @param value
     *            the double.
     * @return the double converted to a string.
     */
    public static String valueOf(double value) {
        return Double.toString(value);
    }

    /**
     * Converts the specified float to its string representation.
     * 
     * @param value
     *            the float.
     * @return the float converted to a string.
     */
    public static String valueOf(float value) {
        return Float.toString(value);
    }

    /**
     * Converts the specified integer to its string representation.
     * 
     * @param value
     *            the integer.
     * @return the integer converted to a string.
     */
    public static String valueOf(int value) {
        return Integer.toString(value);
    }

    /**
     * Converts the specified long to its string representation.
     * 
     * @param value
     *            the long.
     * @return the long converted to a string.
     */
    public static String valueOf(long value) {
        return Long.toString(value);
    }

    /**
     * Converts the specified object to its string representation. If the object
     * is null return the string {@code "null"}, otherwise use {@code
     * toString()} to get the string representation.
     * 
     * @param value
     *            the object.
     * @return the object converted to a string, or the string {@code "null"}.
     */
    public static String valueOf(Object value) {
        return value != null ? value.toString() : "null";
    }

    /**
     * Converts the specified boolean to its string representation. When the
     * boolean is {@code true} return {@code "true"}, otherwise return {@code
     * "false"}.
     * 
     * @param value
     *            the boolean.
     * @return the boolean converted to a string.
     */
    public static String valueOf(boolean value) {
        return value ? "true" : "false";
    }

    /**
     * Returns whether the characters in the StringBuffer {@code strbuf} are the
     * same as those in this string.
     * 
     * @param strbuf
     *            the StringBuffer to compare this string to.
     * @return {@code true} if the characters in {@code strbuf} are identical to
     *         those in this string. If they are not, {@code false} will be
     *         returned.
     * @throws NullPointerException
     *             if {@code strbuf} is {@code null}.
     * @since 1.4
     */
    public boolean contentEquals(StringBuffer strbuf) {
        synchronized (strbuf) {
            int size = strbuf.length();
            if (count != size) {
                return false;
            }
            return regionMatches(0, new String(0, size, strbuf.getValue()), 0,
                    size);
        }
    }

    /**
     * Compares a {@code CharSequence} to this {@code String} to determine if
     * their contents are equal.
     *
     * @param cs
     *            the character sequence to compare to.
     * @return {@code true} if equal, otherwise {@code false}
     * @since 1.5
     */
    public boolean contentEquals(CharSequence cs) {
        if (cs == null) {
            throw new NullPointerException();
        }

        int len = cs.length();

        if (len != count) {
            return false;
        }

        if (len == 0 && count == 0) {
            return true; // since both are empty strings
        }

        return regionMatches(0, cs.toString(), 0, len);
    }

    /**
     * Determines whether this string matches a given regular expression.
     * 
     * @param expr
     *            the regular expression to be matched.
     * @return {@code true} if the expression matches, otherwise {@code false}.
     * @throws PatternSyntaxException
     *             if the syntax of the supplied regular expression is not
     *             valid.
     * @throws NullPointerException
     *             if {@code expr} is {@code null}.
     * @since 1.4
     */
    public boolean matches(String expr) {
        return Pattern.matches(expr, this);
    }

    /**
     * Replace any substrings within this string that match the supplied regular
     * expression {@code expr}, with the string {@code substitute}.
     * 
     * @param expr
     *            the regular expression to match.
     * @param substitute
     *            the string to replace the matching substring with.
     * @return the new string.
     * @throws PatternSyntaxException
     *             if the syntax of the supplied regular expression is not
     *             valid.
     * @see Pattern
     * @since 1.4
     */
    public String replaceAll(String expr, String substitute) {
        return Pattern.compile(expr).matcher(this).replaceAll(substitute);
    }

    /**
     * Replace the first substring within this string that matches the supplied
     * regular expression {@code expr}, with the string {@code substitute}.
     * 
     * @param expr
     *            the regular expression to match.
     * @param substitute
     *            the string to replace the matching substring with.
     * @return the new string.
     * @throws PatternSyntaxException
     *             if the syntax of the supplied regular expression is not
     *             valid.
     * @throws NullPointerException
     *             if {@code strbuf} is {@code null}.
     * @see Pattern
     * @since 1.4
     */
    public String replaceFirst(String expr, String substitute) {
        return Pattern.compile(expr).matcher(this).replaceFirst(substitute);
    }

    /**
     * Splits this string using the supplied regular expression {@code expr},
     * as if by {@code split(expr, 0)}.
     * 
     * @param expr
     *            the regular expression used to divide the string.
     * @return an array of Strings created by separating the string along
     *         matches of the regular expression.
     * @throws NullPointerException
     *             if {@code expr} is {@code null}.
     * @throws PatternSyntaxException
     *             if the syntax of the supplied regular expression is not
     *             valid.
     * @see Pattern
     * @since 1.4
     */
    public String[] split(String expr) {
        return split(expr, 0);
    }

    /**
     * Splits this string using the supplied regular expression {@code expr}.
     * The parameter {@code max} controls the behavior how many times the
     * pattern is applied to the string; see {@link Pattern#split(CharSequence, int)}
     * for details.
     * 
     * @param expr
     *            the regular expression used to divide the string.
     * @param max
     *            the number of entries in the resulting array.
     * @return an array of Strings created by separating the string along
     *         matches of the regular expression.
     * @throws NullPointerException
     *             if {@code expr} is {@code null}.
     * @throws PatternSyntaxException
     *             if the syntax of the supplied regular expression is not
     *             valid.
     * @see Pattern#split(CharSequence, int)
     * @since 1.4
     */
    public String[] split(String expr, int max) {
        String[] result = java.util.regex.Splitter.fastSplit(expr, this, max);
        return result != null ? result : Pattern.compile(expr).split(this, max);
    }

    /**
     * Has the same result as the substring function, but is present so that
     * string may implement the CharSequence interface.
     * 
     * @param start
     *            the offset the first character.
     * @param end
     *            the offset of one past the last character to include.
     * @return the subsequence requested.
     * @throws IndexOutOfBoundsException
     *             if {@code start < 0}, {@code end < 0}, {@code start > end} or
     *             {@code end > length()}.
     * @see java.lang.CharSequence#subSequence(int, int)
     * @since 1.4
     */
    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    /**
     * Returns the Unicode code point at the given {@code index}.
     * 
     * @throws IndexOutOfBoundsException if {@code index < 0 || index >= length()}
     * @see Character#codePointAt(char[], int, int)
     * @since 1.5
     */
    public int codePointAt(int index) {
        if (index < 0 || index >= count) {
            throw new StringIndexOutOfBoundsException();
        }
        return Character.codePointAt(value, offset + index, offset + count);
    }

    /**
     * Returns the Unicode code point that precedes the given {@code index}.
     * 
     * @throws IndexOutOfBoundsException if {@code index < 1 || index > length()}
     * @see Character#codePointBefore(char[], int, int)
     * @since 1.5
     */
    public int codePointBefore(int index) {
        if (index < 1 || index > count) {
            throw new StringIndexOutOfBoundsException();
        }
        return Character.codePointBefore(value, offset + index, offset);
    }

    /**
     * Calculates the number of Unicode code points between {@code beginIndex}
     * and {@code endIndex}.
     * 
     * @param beginIndex
     *            the inclusive beginning index of the subsequence.
     * @param endIndex
     *            the exclusive end index of the subsequence.
     * @return the number of Unicode code points in the subsequence.
     * @throws IndexOutOfBoundsException
     *         if {@code beginIndex < 0 || endIndex > length() || beginIndex > endIndex}
     * @see Character#codePointCount(CharSequence, int, int)
     * @since 1.5
     */
    public int codePointCount(int beginIndex, int endIndex) {
        if (beginIndex < 0 || endIndex > count || beginIndex > endIndex) {
            throw new StringIndexOutOfBoundsException();
        }
        return Character.codePointCount(value, offset + beginIndex, endIndex - beginIndex);
    }

    /**
     * Determines if this {@code String} contains the sequence of characters in
     * the {@code CharSequence} passed.
     *
     * @param cs
     *            the character sequence to search for.
     * @return {@code true} if the sequence of characters are contained in this
     *         string, otherwise {@code false}.
     * @since 1.5
     */
    public boolean contains(CharSequence cs) {
        if (cs == null) {
            throw new NullPointerException();
        }
        return indexOf(cs.toString()) >= 0;
    }

    /**
     * Returns the index within this object that is offset from {@code index} by
     * {@code codePointOffset} code points.
     *
     * @param index
     *            the index within this object to calculate the offset from.
     * @param codePointOffset
     *            the number of code points to count.
     * @return the index within this object that is the offset.
     * @throws IndexOutOfBoundsException
     *             if {@code index} is negative or greater than {@code length()}
     *             or if there aren't enough code points before or after {@code
     *             index} to match {@code codePointOffset}.
     * @since 1.5
     */
    public int offsetByCodePoints(int index, int codePointOffset) {
        int s = index + offset;
        int r = Character.offsetByCodePoints(value, offset, count, s, codePointOffset);
        return r - offset;
    }

    /**
     * Returns a localized formatted string, using the supplied format and arguments,
     * using the user's default locale.
     * 
     * <p>Note that this method can be dangerous: the user's default locale may
     * not be the locale you tested in, and this may have unexpected effects on
     * the output. In particular, floating point numbers may be output with
     * ',' instead of '.' as the decimal separator if that's what the user's
     * locale dictates. If you're formatting a string other than for human
     * consumption, you should use the {@code format(Locale, String, Object...)}
     * overload and supply {@code Locale.US}.
     * 
     * @param format
     *            a format string.
     * @param args
     *            arguments to replace format specifiers (may be none).
     * @return the formatted string.
     * @throws NullPointerException
     *             if {@code format} is {@code null}.
     * @throws java.util.IllegalFormatException
     *             if the format is invalid.
     * @see java.util.Formatter
     * @since 1.5
     */
    public static String format(String format, Object... args) {
        return format(Locale.getDefault(), format, args);
    }

    /**
     * Returns a formatted string, using the supplied format and arguments,
     * localized to the given locale.
     * <p>
     * Note that this is a convenience method. Using it involves creating an
     * internal {@link java.util.Formatter} instance on-the-fly, which is
     * somewhat costly in terms of memory and time. This is probably acceptable
     * if you use the method only rarely, but if you rely on it for formatting a
     * large number of strings, consider creating and reusing your own
     * {@link java.util.Formatter} instance instead.
     *
     * @param loc
     *            the locale to apply; {@code null} value means no localization.
     * @param format
     *            a format string.
     * @param args
     *            arguments to replace format specifiers (may be none).
     * @return the formatted string.
     * @throws NullPointerException
     *             if {@code format} is {@code null}.
     * @throws java.util.IllegalFormatException
     *             if the format is invalid.
     * @see java.util.Formatter
     * @since 1.5
     */
    public static String format(Locale loc, String format, Object... args) {
        if (format == null) {
            throw new NullPointerException("null format argument");
        }
        int bufferSize = format.length()
                + (args == null ? 0 : args.length * 10);
        Formatter f = new Formatter(new StringBuilder(bufferSize), loc);
        return f.format(format, args).toString();
    }

    /*
     * An implementation of a String.indexOf that is supposed to perform
     * substantially better than the default algorithm if the "needle" (the
     * subString being searched for) is a constant string.
     *
     * For example, a JIT, upon encountering a call to String.indexOf(String),
     * where the needle is a constant string, may compute the values cache, md2
     * and lastChar, and change the call to the following method.
     */
    @SuppressWarnings("unused")
    private static int indexOf(String haystackString, String needleString,
            int cache, int md2, char lastChar) {
        char[] haystack = haystackString.value;
        int haystackOffset = haystackString.offset;
        int haystackLength = haystackString.count;
        char[] needle = needleString.value;
        int needleOffset = needleString.offset;
        int needleLength = needleString.count;
        int needleLengthMinus1 = needleLength - 1;
        int haystackEnd = haystackOffset + haystackLength;
        outer_loop: for (int i = haystackOffset + needleLengthMinus1; i < haystackEnd;) {
            if (lastChar == haystack[i]) {
                for (int j = 0; j < needleLengthMinus1; ++j) {
                    if (needle[j + needleOffset] != haystack[i + j
                            - needleLengthMinus1]) {
                        int skip = 1;
                        if ((cache & (1 << haystack[i])) == 0) {
                            skip += j;
                        }
                        i += Math.max(md2, skip);
                        continue outer_loop;
                    }
                }
                return i - needleLengthMinus1 - haystackOffset;
            }

            if ((cache & (1 << haystack[i])) == 0) {
                i += needleLengthMinus1;
            }
            i++;
        }
        return -1;
    }
}
