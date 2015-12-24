/*
 * Copyright (c) 2005, 2009, Oracle and/or its affiliates. All rights reserved.
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
/*
 *******************************************************************************
 * (C) Copyright IBM Corp. and others, 1996-2009 - All Rights Reserved         *
 *                                                                             *
 * The original version of this source code and documentation is copyrighted   *
 * and owned by IBM, These materials are provided under terms of a License     *
 * Agreement between IBM and Sun. This technology is protected by multiple     *
 * US and International patents. This notice and attribution to IBM may not    *
 * to removed.                                                                 *
 *******************************************************************************
 */

package sun.text.normalizer;

/**
 * <p>Standalone utility class providing UTF16 character conversions and
 * indexing conversions.</p>
 * <p>Code that uses strings alone rarely need modification.
 * By design, UTF-16 does not allow overlap, so searching for strings is a safe
 * operation. Similarly, concatenation is always safe. Substringing is safe if
 * the start and end are both on UTF-32 boundaries. In normal code, the values
 * for start and end are on those boundaries, since they arose from operations
 * like searching. If not, the nearest UTF-32 boundaries can be determined
 * using <code>bounds()</code>.</p>
 * <strong>Examples:</strong>
 * <p>The following examples illustrate use of some of these methods.
 * <pre>
 * // iteration forwards: Original
 * for (int i = 0; i &lt; s.length(); ++i) {
 *     char ch = s.charAt(i);
 *     doSomethingWith(ch);
 * }
 *
 * // iteration forwards: Changes for UTF-32
 * int ch;
 * for (int i = 0; i &lt; s.length(); i+=UTF16.getCharCount(ch)) {
 *     ch = UTF16.charAt(s,i);
 *     doSomethingWith(ch);
 * }
 *
 * // iteration backwards: Original
 * for (int i = s.length() -1; i >= 0; --i) {
 *     char ch = s.charAt(i);
 *     doSomethingWith(ch);
 * }
 *
 * // iteration backwards: Changes for UTF-32
 * int ch;
 * for (int i = s.length() -1; i > 0; i-=UTF16.getCharCount(ch)) {
 *     ch = UTF16.charAt(s,i);
 *     doSomethingWith(ch);
 * }
 * </pre>
 * <strong>Notes:</strong>
 * <ul>
 *   <li>
 *   <strong>Naming:</strong> For clarity, High and Low surrogates are called
 *   <code>Lead</code> and <code>Trail</code> in the API, which gives a better
 *   sense of their ordering in a string. <code>offset16</code> and
 *   <code>offset32</code> are used to distinguish offsets to UTF-16
 *   boundaries vs offsets to UTF-32 boundaries. <code>int char32</code> is
 *   used to contain UTF-32 characters, as opposed to <code>char16</code>,
 *   which is a UTF-16 code unit.
 *   </li>
 *   <li>
 *   <strong>Roundtripping Offsets:</strong> You can always roundtrip from a
 *   UTF-32 offset to a UTF-16 offset and back. Because of the difference in
 *   structure, you can roundtrip from a UTF-16 offset to a UTF-32 offset and
 *   back if and only if <code>bounds(string, offset16) != TRAIL</code>.
 *   </li>
 *   <li>
 *    <strong>Exceptions:</strong> The error checking will throw an exception
 *   if indices are out of bounds. Other than than that, all methods will
 *   behave reasonably, even if unmatched surrogates or out-of-bounds UTF-32
 *   values are present. <code>UCharacter.isLegal()</code> can be used to check
 *   for validity if desired.
 *   </li>
 *   <li>
 *   <strong>Unmatched Surrogates:</strong> If the string contains unmatched
 *   surrogates, then these are counted as one UTF-32 value. This matches
 *   their iteration behavior, which is vital. It also matches common display
 *   practice as missing glyphs (see the Unicode Standard Section 5.4, 5.5).
 *   </li>
 *   <li>
 *     <strong>Optimization:</strong> The method implementations may need
 *     optimization if the compiler doesn't fold static final methods. Since
 *     surrogate pairs will form an exceeding small percentage of all the text
 *     in the world, the singleton case should always be optimized for.
 *   </li>
 * </ul>
 * @author Mark Davis, with help from Markus Scherer
 * @stable ICU 2.1
 */

public final class UTF16
{
    // public variables ---------------------------------------------------

    /**
     * The lowest Unicode code point value.
     * @stable ICU 2.1
     */
    public static final int CODEPOINT_MIN_VALUE = 0;
    /**
     * The highest Unicode code point value (scalar value) according to the
     * Unicode Standard.
     * @stable ICU 2.1
     */
    public static final int CODEPOINT_MAX_VALUE = 0x10ffff;
    /**
     * The minimum value for Supplementary code points
     * @stable ICU 2.1
     */
    public static final int SUPPLEMENTARY_MIN_VALUE  = 0x10000;
    /**
     * Lead surrogate minimum value
     * @stable ICU 2.1
     */
    public static final int LEAD_SURROGATE_MIN_VALUE = 0xD800;
    /**
     * Trail surrogate minimum value
     * @stable ICU 2.1
     */
    public static final int TRAIL_SURROGATE_MIN_VALUE = 0xDC00;
    /**
     * Lead surrogate maximum value
     * @stable ICU 2.1
     */
    public static final int LEAD_SURROGATE_MAX_VALUE = 0xDBFF;
    /**
     * Trail surrogate maximum value
     * @stable ICU 2.1
     */
    public static final int TRAIL_SURROGATE_MAX_VALUE = 0xDFFF;
    /**
     * Surrogate minimum value
     * @stable ICU 2.1
     */
    public static final int SURROGATE_MIN_VALUE = LEAD_SURROGATE_MIN_VALUE;

    // public method ------------------------------------------------------

    /**
     * Extract a single UTF-32 value from a string.
     * Used when iterating forwards or backwards (with
     * <code>UTF16.getCharCount()</code>, as well as random access. If a
     * validity check is required, use
     * <code><a href="../lang/UCharacter.html#isLegal(char)">
     * UCharacter.isLegal()</a></code> on the return value.
     * If the char retrieved is part of a surrogate pair, its supplementary
     * character will be returned. If a complete supplementary character is
     * not found the incomplete character will be returned
     * @param source array of UTF-16 chars
     * @param offset16 UTF-16 offset to the start of the character.
     * @return UTF-32 value for the UTF-32 value that contains the char at
     *         offset16. The boundaries of that codepoint are the same as in
     *         <code>bounds32()</code>.
     * @exception IndexOutOfBoundsException thrown if offset16 is out of
     *            bounds.
     * @stable ICU 2.1
     */
    public static int charAt(String source, int offset16) {
        char single = source.charAt(offset16);
        if (single < LEAD_SURROGATE_MIN_VALUE) {
            return single;
        }
        return _charAt(source, offset16, single);
    }

    private static int _charAt(String source, int offset16, char single) {
        if (single > TRAIL_SURROGATE_MAX_VALUE) {
            return single;
        }

        // Convert the UTF-16 surrogate pair if necessary.
        // For simplicity in usage, and because the frequency of pairs is
        // low, look both directions.

        if (single <= LEAD_SURROGATE_MAX_VALUE) {
            ++offset16;
            if (source.length() != offset16) {
                char trail = source.charAt(offset16);
                if (trail >= TRAIL_SURROGATE_MIN_VALUE && trail <= TRAIL_SURROGATE_MAX_VALUE) {
                    return UCharacterProperty.getRawSupplementary(single, trail);
                }
            }
        } else {
            --offset16;
            if (offset16 >= 0) {
                // single is a trail surrogate so
                char lead = source.charAt(offset16);
                if (lead >= LEAD_SURROGATE_MIN_VALUE && lead <= LEAD_SURROGATE_MAX_VALUE) {
                    return UCharacterProperty.getRawSupplementary(lead, single);
                }
            }
        }
        return single; // return unmatched surrogate
    }

    /**
     * Extract a single UTF-32 value from a substring.
     * Used when iterating forwards or backwards (with
     * <code>UTF16.getCharCount()</code>, as well as random access. If a
     * validity check is required, use
     * <code><a href="../lang/UCharacter.html#isLegal(char)">UCharacter.isLegal()
     * </a></code> on the return value.
     * If the char retrieved is part of a surrogate pair, its supplementary
     * character will be returned. If a complete supplementary character is
     * not found the incomplete character will be returned
     * @param source array of UTF-16 chars
     * @param start offset to substring in the source array for analyzing
     * @param limit offset to substring in the source array for analyzing
     * @param offset16 UTF-16 offset relative to start
     * @return UTF-32 value for the UTF-32 value that contains the char at
     *         offset16. The boundaries of that codepoint are the same as in
     *         <code>bounds32()</code>.
     * @exception IndexOutOfBoundsException thrown if offset16 is not within
     *            the range of start and limit.
     * @stable ICU 2.1
     */
    public static int charAt(char source[], int start, int limit,
                             int offset16)
    {
        offset16 += start;
        if (offset16 < start || offset16 >= limit) {
            throw new ArrayIndexOutOfBoundsException(offset16);
        }

        char single = source[offset16];
        if (!isSurrogate(single)) {
            return single;
        }

        // Convert the UTF-16 surrogate pair if necessary.
        // For simplicity in usage, and because the frequency of pairs is
        // low, look both directions.
        if (single <= LEAD_SURROGATE_MAX_VALUE) {
            offset16 ++;
            if (offset16 >= limit) {
                return single;
            }
            char trail = source[offset16];
            if (isTrailSurrogate(trail)) {
                return UCharacterProperty.getRawSupplementary(single, trail);
            }
        }
        else { // isTrailSurrogate(single), so
            if (offset16 == start) {
                return single;
            }
            offset16 --;
            char lead = source[offset16];
            if (isLeadSurrogate(lead))
                return UCharacterProperty.getRawSupplementary(lead, single);
        }
        return single; // return unmatched surrogate
    }

    /**
     * Determines how many chars this char32 requires.
     * If a validity check is required, use <code>
     * <a href="../lang/UCharacter.html#isLegal(char)">isLegal()</a></code> on
     * char32 before calling.
     * @param char32 the input codepoint.
     * @return 2 if is in supplementary space, otherwise 1.
     * @stable ICU 2.1
     */
    public static int getCharCount(int char32)
    {
        if (char32 < SUPPLEMENTARY_MIN_VALUE) {
            return 1;
        }
        return 2;
    }

    /**
     * Determines whether the code value is a surrogate.
     * @param char16 the input character.
     * @return true iff the input character is a surrogate.
     * @stable ICU 2.1
     */
    public static boolean isSurrogate(char char16)
    {
        return LEAD_SURROGATE_MIN_VALUE <= char16 &&
            char16 <= TRAIL_SURROGATE_MAX_VALUE;
    }

    /**
     * Determines whether the character is a trail surrogate.
     * @param char16 the input character.
     * @return true iff the input character is a trail surrogate.
     * @stable ICU 2.1
     */
    public static boolean isTrailSurrogate(char char16)
    {
        return (TRAIL_SURROGATE_MIN_VALUE <= char16 &&
                char16 <= TRAIL_SURROGATE_MAX_VALUE);
    }

    /**
     * Determines whether the character is a lead surrogate.
     * @param char16 the input character.
     * @return true iff the input character is a lead surrogate
     * @stable ICU 2.1
     */
    public static boolean isLeadSurrogate(char char16)
    {
        return LEAD_SURROGATE_MIN_VALUE <= char16 &&
            char16 <= LEAD_SURROGATE_MAX_VALUE;
    }

    /**
     * Returns the lead surrogate.
     * If a validity check is required, use
     * <code><a href="../lang/UCharacter.html#isLegal(char)">isLegal()</a></code>
     * on char32 before calling.
     * @param char32 the input character.
     * @return lead surrogate if the getCharCount(ch) is 2; <br>
     *         and 0 otherwise (note: 0 is not a valid lead surrogate).
     * @stable ICU 2.1
     */
    public static char getLeadSurrogate(int char32)
    {
        if (char32 >= SUPPLEMENTARY_MIN_VALUE) {
            return (char)(LEAD_SURROGATE_OFFSET_ +
                          (char32 >> LEAD_SURROGATE_SHIFT_));
        }

        return 0;
    }

    /**
     * Returns the trail surrogate.
     * If a validity check is required, use
     * <code><a href="../lang/UCharacter.html#isLegal(char)">isLegal()</a></code>
     * on char32 before calling.
     * @param char32 the input character.
     * @return the trail surrogate if the getCharCount(ch) is 2; <br>otherwise
     *         the character itself
     * @stable ICU 2.1
     */
    public static char getTrailSurrogate(int char32)
    {
        if (char32 >= SUPPLEMENTARY_MIN_VALUE) {
            return (char)(TRAIL_SURROGATE_MIN_VALUE +
                          (char32 & TRAIL_SURROGATE_MASK_));
        }

        return (char)char32;
    }

    /**
     * Convenience method corresponding to String.valueOf(char). Returns a one
     * or two char string containing the UTF-32 value in UTF16 format. If a
     * validity check is required, use
     * <code><a href="../lang/UCharacter.html#isLegal(char)">isLegal()</a></code>
     * on char32 before calling.
     * @param char32 the input character.
     * @return string value of char32 in UTF16 format
     * @exception IllegalArgumentException thrown if char32 is a invalid
     *            codepoint.
     * @stable ICU 2.1
     */
    public static String valueOf(int char32)
    {
        if (char32 < CODEPOINT_MIN_VALUE || char32 > CODEPOINT_MAX_VALUE) {
            throw new IllegalArgumentException("Illegal codepoint");
        }
        return toString(char32);
    }

    /**
     * Append a single UTF-32 value to the end of a StringBuffer.
     * If a validity check is required, use
     * <code><a href="../lang/UCharacter.html#isLegal(char)">isLegal()</a></code>
     * on char32 before calling.
     * @param target the buffer to append to
     * @param char32 value to append.
     * @return the updated StringBuffer
     * @exception IllegalArgumentException thrown when char32 does not lie
     *            within the range of the Unicode codepoints
     * @stable ICU 2.1
     */
    public static StringBuffer append(StringBuffer target, int char32)
    {
        // Check for irregular values
        if (char32 < CODEPOINT_MIN_VALUE || char32 > CODEPOINT_MAX_VALUE) {
            throw new IllegalArgumentException("Illegal codepoint: " + Integer.toHexString(char32));
        }

        // Write the UTF-16 values
        if (char32 >= SUPPLEMENTARY_MIN_VALUE)
            {
                target.append(getLeadSurrogate(char32));
                target.append(getTrailSurrogate(char32));
            }
        else {
            target.append((char)char32);
        }
        return target;
    }

    //// for StringPrep
    /**
     * Shifts offset16 by the argument number of codepoints within a subarray.
     * @param source char array
     * @param start position of the subarray to be performed on
     * @param limit position of the subarray to be performed on
     * @param offset16 UTF16 position to shift relative to start
     * @param shift32 number of codepoints to shift
     * @return new shifted offset16 relative to start
     * @exception IndexOutOfBoundsException if the new offset16 is out of
     *            bounds with respect to the subarray or the subarray bounds
     *            are out of range.
     * @stable ICU 2.1
     */
    public static int moveCodePointOffset(char source[], int start, int limit,
                                          int offset16, int shift32)
    {
        int         size = source.length;
        int         count;
        char        ch;
        int         result = offset16 + start;
        if (start<0 || limit<start) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (limit>size) {
            throw new StringIndexOutOfBoundsException(limit);
        }
        if (offset16<0 || result>limit) {
            throw new StringIndexOutOfBoundsException(offset16);
        }
        if (shift32 > 0 ) {
            if (shift32 + result > size) {
                throw new StringIndexOutOfBoundsException(result);
            }
            count = shift32;
            while (result < limit && count > 0)
            {
                ch = source[result];
                if (isLeadSurrogate(ch) && (result+1 < limit) &&
                        isTrailSurrogate(source[result+1])) {
                    result ++;
                }
                count --;
                result ++;
            }
        } else {
            if (result + shift32 < start) {
                throw new StringIndexOutOfBoundsException(result);
            }
            for (count=-shift32; count>0; count--) {
                result--;
                if (result<start) {
                    break;
                }
                ch = source[result];
                if (isTrailSurrogate(ch) && result>start && isLeadSurrogate(source[result-1])) {
                    result--;
                }
            }
        }
        if (count != 0)  {
            throw new StringIndexOutOfBoundsException(shift32);
        }
        result -= start;
        return result;
    }

    // private data members -------------------------------------------------

    /**
     * Shift value for lead surrogate to form a supplementary character.
     */
    private static final int LEAD_SURROGATE_SHIFT_ = 10;

    /**
     * Mask to retrieve the significant value from a trail surrogate.
     */
    private static final int TRAIL_SURROGATE_MASK_     = 0x3FF;

    /**
     * Value that all lead surrogate starts with
     */
    private static final int LEAD_SURROGATE_OFFSET_ =
        LEAD_SURROGATE_MIN_VALUE -
        (SUPPLEMENTARY_MIN_VALUE
         >> LEAD_SURROGATE_SHIFT_);

    // private methods ------------------------------------------------------

    /**
     * <p>Converts argument code point and returns a String object representing
     * the code point's value in UTF16 format.</p>
     * <p>This method does not check for the validity of the codepoint, the
     * results are not guaranteed if a invalid codepoint is passed as
     * argument.</p>
     * <p>The result is a string whose length is 1 for non-supplementary code
     * points, 2 otherwise.</p>
     * @param ch code point
     * @return string representation of the code point
     */
    private static String toString(int ch)
    {
        if (ch < SUPPLEMENTARY_MIN_VALUE) {
            return String.valueOf((char)ch);
        }

        StringBuffer result = new StringBuffer();
        result.append(getLeadSurrogate(ch));
        result.append(getTrailSurrogate(ch));
        return result.toString();
    }
}
