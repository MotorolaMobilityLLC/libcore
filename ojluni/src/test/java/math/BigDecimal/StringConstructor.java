/*
 * Copyright (c) 1999, 2017, Oracle and/or its affiliates. All rights reserved.
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
package test.java.math.BigDecimal;

/*
 * @test
 * @library /test/lib
 * @build jdk.test.lib.RandomFactory
 * @run main StringConstructor
 * @bug 4103117 4331084 4488017 4490929 6255285 6268365 8074460 8078672
 * @summary Tests the BigDecimal string constructor (use -Dseed=X to set PRNG seed).
 * @key randomness
 */

import java.math.*;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

// Android-changed: Replace error counting with asserts.
public class StringConstructor {

    @Test
    public void testBadStrings() {
        constructWithError("");
        constructWithError("+");
        constructWithError("-");
        constructWithError("+e");
        constructWithError("-e");
        constructWithError("e+");
        constructWithError("1.-0");
        constructWithError(".-123");
        constructWithError("-");
        constructWithError("--1.1");
        constructWithError("-+1.1");
        constructWithError("+-1.1");
        constructWithError("1-.1");
        constructWithError("1+.1");
        constructWithError("1.111+1");
        constructWithError("1.111-1");
        constructWithError("11.e+");
        constructWithError("11.e-");
        constructWithError("11.e+-");
        constructWithError("11.e-+");
        constructWithError("11.e-+1");
        constructWithError("11.e+-1");

        // Range checks
        constructWithError("1e" + Integer.MIN_VALUE);
        constructWithError("10e" + Integer.MIN_VALUE);
        constructWithError("0.01e" + Integer.MIN_VALUE);
        constructWithError("1e" + ((long) Integer.MIN_VALUE - 1));
        constructWithError("1e" + ((long) Integer.MAX_VALUE + 1));
    }

    @Test
    public void testRoundtrip() {
        // Roundtrip tests
        Random random = new Random();
        for (int i=0; i<100; i++) {
            int size = random.nextInt(100) + 1;
            BigInteger bi = new BigInteger(size, random);
            if (random.nextBoolean())
                bi = bi.negate();
            int decimalLength = bi.toString().length();
            int scale = random.nextInt(decimalLength);
            BigDecimal bd = new BigDecimal(bi, scale);
            String bdString = bd.toString();
            BigDecimal bdDoppel = new BigDecimal(bdString);
            Assert.assertEquals(bd, bdDoppel, "bd string: scale: " + bd.scale() +
                                   "\t" + bdString + "\nbd doppel: scale: " + bdDoppel.scale() +
                                   "\t" + bdDoppel.toString());
        }
    }

    /*
     * Verify precision is set properly if the significand has
     * non-ASCII leading zeros.
     */
    @Test
    public void nonAsciiZeroTest() {
        String[] values = {
            "00004e5",
            "\u0660\u0660\u0660\u06604e5",
        };

        BigDecimal expected = new BigDecimal("4e5");

        for(String s : values) {
            BigDecimal tmp = new BigDecimal(s);
            Assert.assertFalse(! expected.equals(tmp) || tmp.precision() != 1,
                "Bad conversion of " + s + "got " +
                                   tmp + "precision = " + tmp.precision());
        }

    }

    @Test
    public void testLeadingExponentZeroTest() {
        BigDecimal twelve = new BigDecimal("12");
        BigDecimal onePointTwo = new BigDecimal("1.2");

        String start = "1.2e0";
        String end = "1";
        String middle = "";

        // Test with more excess zeros than the largest number of
        // decimal digits needed to represent a long
        int limit  = ((int)Math.log10(Long.MAX_VALUE)) + 6;
        for(int i = 0; i < limit; i++, middle += "0") {
            String t1 = start + middle;
            String t2 = t1 + end;

            testString(t1, onePointTwo);
            testString(t2, twelve);
        }
    }

    private static void testString(String s, BigDecimal expected) {
        testString0(s, expected);
        testString0(switchZero(s), expected);
    }

    private static void testString0(String s, BigDecimal expected) {
        Assert.assertEquals(new BigDecimal(s), expected, s + " is not equal to " + expected);
    }

    private static String switchZero(String s) {
        return s.replace('0', '\u0660'); // Arabic-Indic zero
    }

    private static void constructWithError(String badString) {
        try {
            BigDecimal d = new BigDecimal(badString);
            Assert.fail(badString + " accepted");
        } catch(NumberFormatException e) {
            // expected
        }
    }
}
