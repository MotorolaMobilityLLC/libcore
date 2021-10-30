/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 4851777
 * @summary Tests of BigDecimal.sqrt().
 */

import java.math.*;
import java.util.*;

import org.testng.Assert;
import org.testng.annotations.Test;

// Android-changed: Replace error counting with asserts.
public class SquareRootTests {

    @Test
    public void negativeTests() {
        for (long i = -10; i < 0; i++) {
            for (int j = -5; j < 5; j++) {
                try {
                    BigDecimal input = BigDecimal.valueOf(i, j);
                    BigDecimal result = input.sqrt(MathContext.DECIMAL64);
                    Assert.fail("Unexpected sqrt of negative: (" +
                                       input + ").sqrt()  = " + result );
                } catch (ArithmeticException e) {
                    ; // Expected
                }
            }
        }
    }

    @Test
    public void zeroTests() {
        for (int i = -100; i < 100; i++) {
            BigDecimal expected = BigDecimal.valueOf(0L, i/2);
            // These results are independent of rounding mode
            compare(BigDecimal.valueOf(0L, i).sqrt(MathContext.UNLIMITED),
                                expected, true, "zeros");

            compare(BigDecimal.valueOf(0L, i).sqrt(MathContext.DECIMAL64),
                                expected, true, "zeros");
        }
    }

    /**
     * sqrt(10^2N) is 10^N
     * Both numerical value and representation should be verified
     */
    @Test
    public void evenPowersOfTenTests() {
        MathContext oneDigitExactly = new MathContext(1, RoundingMode.UNNECESSARY);

        for (int scale = -100; scale <= 100; scale++) {
            BigDecimal testValue       = BigDecimal.valueOf(1, 2*scale);
            BigDecimal expectedNumericalResult = BigDecimal.valueOf(1, scale);

            BigDecimal result;


            equalNumerically(expectedNumericalResult,
                                           result = testValue.sqrt(MathContext.DECIMAL64),
                                           "Even powers of 10, DECIMAL64");

            // Can round to one digit of precision exactly
            equalNumerically(expectedNumericalResult,
                                           result = testValue.sqrt(oneDigitExactly),
                                           "even powers of 10, 1 digit");
            if (result.precision() > 1) {
                Assert.fail("Excess precision for " + result);
            }
            // If rounding to more than one digit, do precision / scale checking...
        }
    }

    @Test
    public void squareRootTwoTests() {
        BigDecimal TWO = new BigDecimal(2);

        // Square root of 2 truncated to 65 digits
        BigDecimal highPrecisionRoot2 =
            new BigDecimal("1.41421356237309504880168872420969807856967187537694807317667973799");


        RoundingMode[] modes = {
            RoundingMode.UP,       RoundingMode.DOWN,
            RoundingMode.CEILING, RoundingMode.FLOOR,
            RoundingMode.HALF_UP, RoundingMode.HALF_DOWN, RoundingMode.HALF_EVEN
        };

        // For each interesting rounding mode, for precisions 1 to, say
        // 63 numerically compare TWO.sqrt(mc) to
        // highPrecisionRoot2.round(mc)

        for (RoundingMode mode : modes) {
            for (int precision = 1; precision < 63; precision++) {
                MathContext mc = new MathContext(precision, mode);
                BigDecimal expected = highPrecisionRoot2.round(mc);
                BigDecimal computed = TWO.sqrt(mc);

                equalNumerically(expected, computed, "sqrt(2)");
            }
        }
    }

    @Test
    public void lowPrecisionPerfectSquares() {
        // For 5^2 through 9^2, if the input is rounded to one digit
        // first before the root is computed, the wrong answer will
        // result. Verify results and scale for different rounding
        // modes and precisions.
        long[][] squaresWithOneDigitRoot = {{ 4, 2},
                                            { 9, 3},
                                            {25, 5},
                                            {36, 6},
                                            {49, 7},
                                            {64, 8},
                                            {81, 9}};

        for (long[] squareAndRoot : squaresWithOneDigitRoot) {
            BigDecimal square     = new BigDecimal(squareAndRoot[0]);
            BigDecimal expected   = new BigDecimal(squareAndRoot[1]);

            for (int scale = 0; scale <= 4; scale++) {
                BigDecimal scaledSquare = square.setScale(scale, RoundingMode.UNNECESSARY);
                int expectedScale = scale/2;
                for (int precision = 0; precision <= 5; precision++) {
                    for (RoundingMode rm : RoundingMode.values()) {
                        MathContext mc = new MathContext(precision, rm);
                        BigDecimal computedRoot = scaledSquare.sqrt(mc);

                        equalNumerically(expected, computedRoot, "simple squares");

                        int computedScale = computedRoot.scale();
                        if (precision >=  expectedScale + 1 && computedScale != expectedScale) {
                            Assert.fail(String.format("%s\tprecision=%d\trm=%s%n",
                                          computedRoot, precision, rm) +
                                        String.format("\t%s does not have expected scale of %d%n.",
                                              computedRoot, expectedScale));
                        }
                    }
                }
            }
        }
    }

    private static void compare(BigDecimal a, BigDecimal b, boolean expected, String prefix) {
        boolean result = a.equals(b);
        Assert.assertEquals(result, expected, "Testing " + prefix +
                               "(" + a + ").compareTo(" + b + ") => " + result +
                               "\n\tExpected " + expected);
    }

    private static void equalNumerically(BigDecimal a, BigDecimal b, String prefix) {
        compareNumerically(a, b, 0, prefix);
    }

    private static void compareNumerically(BigDecimal a, BigDecimal b,
                                          int expected, String prefix) {
        int result = a.compareTo(b);
        Assert.assertEquals(result, expected, "Testing " + prefix +
                               "(" + a + ").compareTo(" + b + ") => " + result +
                               "\n\tExpected " + expected);
    }
}
