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
/**
 * @author Elena Semukhina
 * @version $Revision$
 */

package org.apache.harmony.math.tests.java.math;

import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;

import java.math.BigInteger;

import junit.framework.TestCase;
@TestTargetClass(BigInteger.class)
/**
 * Class:   java.math.BigInteger
 * Methods: modPow, modInverse, and gcd 
 */
public class BigIntegerModPowTest extends TestCase {
    /**
     * modPow: non-positive modulus
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modPow method.",
        method = "modPow",
        args = {java.math.BigInteger.class, java.math.BigInteger.class}
    )
    public void testModPowException() {
        byte aBytes[] = {1, 2, 3, 4, 5, 6, 7};
        byte eBytes[] = {1, 2, 3, 4, 5};
        byte mBytes[] = {1, 2, 3};
        int aSign = 1;
        int eSign = 1;        
        int mSign = -1;        
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger exp = new BigInteger(eSign, eBytes);
        BigInteger modulus = new BigInteger(mSign, mBytes);
        try {
            aNumber.modPow(exp, modulus);
            fail("ArithmeticException has not been caught");
        } catch (ArithmeticException e) {
            assertEquals("Improper exception message", "BigInteger: modulus not positive", e.getMessage());
        }
    }

    /**
     * modPow: positive exponent
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modPow method.",
        method = "modPow",
        args = {java.math.BigInteger.class, java.math.BigInteger.class}
    )
    public void testModPowPosExp() {
        byte aBytes[] = {-127, 100, 56, 7, 98, -1, 39, -128, 127, 75, 48, -7};
        byte eBytes[] = {27, -15, 65, 39};
        byte mBytes[] = {-128, 2, 3, 4, 5};
        int aSign = 1;
        int eSign = 1;        
        int mSign = 1;        
        byte rBytes[] = {113, 100, -84, -28, -85};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger exp =      new BigInteger(eSign, eBytes);
        BigInteger modulus = new BigInteger(mSign, mBytes);
        BigInteger result = aNumber.modPow(exp, modulus);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }
    
    /**
     * modPow: negative exponent
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modPow method.",
        method = "modPow",
        args = {java.math.BigInteger.class, java.math.BigInteger.class}
    )
    public void testModPowNegExp() {
        byte aBytes[] = {-127, 100, 56, 7, 98, -1, 39, -128, 127, 75, 48, -7};
        byte eBytes[] = {27, -15, 65, 39};
        byte mBytes[] = {-128, 2, 3, 4, 5};
        int aSign = 1;
        int eSign = -1;        
        int mSign = 1;        
        byte rBytes[] = {12, 118, 46, 86, 92};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger exp =      new BigInteger(eSign, eBytes);
        BigInteger modulus = new BigInteger(mSign, mBytes);
        BigInteger result = aNumber.modPow(exp, modulus);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }

    /**
     * modInverse: non-positive modulus
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modInverse method.",
        method = "modInverse",
        args = {java.math.BigInteger.class}
    )
    public void testmodInverseException() {
        byte aBytes[] = {1, 2, 3, 4, 5, 6, 7};
        byte mBytes[] = {1, 2, 3};
        int aSign = 1;
        int mSign = -1;        
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger modulus = new BigInteger(mSign, mBytes);
        try {
            aNumber.modInverse(modulus);
            fail("ArithmeticException has not been caught");
        } catch (ArithmeticException e) {
            assertEquals("Improper exception message", "BigInteger: modulus not positive", e.getMessage());
        }
    }
    
    /**
     * modInverse: non-invertible number
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modInverse method.",
        method = "modInverse",
        args = {java.math.BigInteger.class}
    )
    public void testmodInverseNonInvertible() {
        byte aBytes[] = {-15, 24, 123, 56, -11, -112, -34, -98, 8, 10, 12, 14, 25, 125, -15, 28, -127};
        byte mBytes[] = {-12, 1, 0, 0, 0, 23, 44, 55, 66};
        int aSign = 1;
        int mSign = 1;        
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger modulus = new BigInteger(mSign, mBytes);
        try {
            aNumber.modInverse(modulus);
            fail("ArithmeticException has not been caught");
        } catch (ArithmeticException e) {
            assertEquals("Improper exception message", "BigInteger not invertible.", e.getMessage());
        }
    }

    /**
     * modInverse: positive number
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modInverse method.",
        method = "modInverse",
        args = {java.math.BigInteger.class}
    )
    public void testmodInversePos1() {
        byte aBytes[] = {24, 123, 56, -11, -112, -34, -98, 8, 10, 12, 14, 25, 125, -15, 28, -127};
        byte mBytes[] = {122, 45, 36, 100, 122, 45};
        int aSign = 1;
        int mSign = 1;        
        byte rBytes[] = {47, 3, 96, 62, 87, 19};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger modulus = new BigInteger(mSign, mBytes);
        BigInteger result = aNumber.modInverse(modulus);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }

    /**
     * modInverse: positive number (another case: a < 0)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modInverse method.",
        method = "modInverse",
        args = {java.math.BigInteger.class}
    )
    public void testmodInversePos2() {
        byte aBytes[] = {15, 24, 123, 56, -11, -112, -34, -98, 8, 10, 12, 14, 25, 125, -15, 28, -127};
        byte mBytes[] = {2, 122, 45, 36, 100};
        int aSign = 1;
        int mSign = 1;        
        byte rBytes[] = {1, -93, 40, 127, 73};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger modulus = new BigInteger(mSign, mBytes);
        BigInteger result = aNumber.modInverse(modulus);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }

    /**
     * modInverse: negative number
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modInverse method.",
        method = "modInverse",
        args = {java.math.BigInteger.class}
    )
    public void testmodInverseNeg1() {
        byte aBytes[] = {15, 24, 123, 56, -11, -112, -34, -98, 8, 10, 12, 14, 25, 125, -15, 28, -127};
        byte mBytes[] = {2, 122, 45, 36, 100};
        int aSign = -1;
        int mSign = 1;        
        byte rBytes[] = {0, -41, 4, -91, 27};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger modulus = new BigInteger(mSign, mBytes);
        BigInteger result = aNumber.modInverse(modulus);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }

    /**
     * modInverse: negative number (another case: x < 0)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for modInverse method.",
        method = "modInverse",
        args = {java.math.BigInteger.class}
    )
    public void testmodInverseNeg2() {
        byte aBytes[] = {-15, 24, 123, 57, -15, 24, 123, 57, -15, 24, 123, 57};
        byte mBytes[] = {122, 2, 4, 122, 2, 4};
        byte rBytes[] = {85, 47, 127, 4, -128, 45};
        BigInteger aNumber = new BigInteger(aBytes);
        BigInteger modulus = new BigInteger(mBytes);
        BigInteger result = aNumber.modInverse(modulus);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }
    
    /**
     * gcd: the second number is zero
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for gcd method.",
        method = "gcd",
        args = {java.math.BigInteger.class}
    )
    public void testGcdSecondZero() {
        byte aBytes[] = {15, 24, 123, 57, -15, 24, 123, 57, -15, 24, 123, 57};
        byte bBytes[] = {0};
        int aSign = 1;
        int bSign = 1;
        byte rBytes[] = {15, 24, 123, 57, -15, 24, 123, 57, -15, 24, 123, 57};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger bNumber = new BigInteger(bSign, bBytes);
        BigInteger result = aNumber.gcd(bNumber);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }

    /**
     * gcd: the first number is zero
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for gcd method.",
        method = "gcd",
        args = {java.math.BigInteger.class}
    )
    public void testGcdFirstZero() {
        byte aBytes[] = {0};
        byte bBytes[] = {15, 24, 123, 57, -15, 24, 123, 57, -15, 24, 123, 57};
        int aSign = 1;
        int bSign = 1;
        byte rBytes[] = {15, 24, 123, 57, -15, 24, 123, 57, -15, 24, 123, 57};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger bNumber = new BigInteger(bSign, bBytes);
        BigInteger result = aNumber.gcd(bNumber);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }
    
    /**
     * gcd: the first number is ZERO
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for gcd method.",
        method = "gcd",
        args = {java.math.BigInteger.class}
    )
    public void testGcdFirstZERO() {
        byte bBytes[] = {15, 24, 123, 57, -15, 24, 123, 57, -15, 24, 123, 57};
        int bSign = 1;
        byte rBytes[] = {15, 24, 123, 57, -15, 24, 123, 57, -15, 24, 123, 57};
        BigInteger aNumber = BigInteger.ZERO;
        BigInteger bNumber = new BigInteger(bSign, bBytes);
        BigInteger result = aNumber.gcd(bNumber);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }

    /**
     * gcd: both numbers are zeros
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for gcd method.",
        method = "gcd",
        args = {java.math.BigInteger.class}
    )
    public void testGcdBothZeros() {
        byte rBytes[] = {0};
        BigInteger aNumber = new BigInteger("0");
        BigInteger bNumber = BigInteger.valueOf(0L);
        BigInteger result = aNumber.gcd(bNumber);
        byte resBytes[] = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 0, result.signum());
    }

    /**
     * gcd: the first number is longer
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for gcd method.",
        method = "gcd",
        args = {java.math.BigInteger.class}
    )
    public void testGcdFirstLonger() {
        byte aBytes[] = {-15, 24, 123, 56, -11, -112, -34, -98, 8, 10, 12, 14, 25, 125, -15, 28, -127};
        byte bBytes[] = {-12, 1, 0, 0, 0, 23, 44, 55, 66};
        int aSign = 1;
        int bSign = 1;
        byte rBytes[] = {7};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger bNumber = new BigInteger(bSign, bBytes);
        BigInteger result = aNumber.gcd(bNumber);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }

    /**
     * gcd: the second number is longer
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "This is a complete subset of tests for gcd method.",
        method = "gcd",
        args = {java.math.BigInteger.class}
    )
    public void testGcdSecondLonger() {
        byte aBytes[] = {-12, 1, 0, 0, 0, 23, 44, 55, 66};
        byte bBytes[] = {-15, 24, 123, 56, -11, -112, -34, -98, 8, 10, 12, 14, 25, 125, -15, 28, -127};
        int aSign = 1;
        int bSign = 1;
        byte rBytes[] = {7};
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger bNumber = new BigInteger(bSign, bBytes);
        BigInteger result = aNumber.gcd(bNumber);
        byte resBytes[] = new byte[rBytes.length];
        resBytes = result.toByteArray();
        for(int i = 0; i < resBytes.length; i++) {
            assertTrue(resBytes[i] == rBytes[i]);
        }
        assertEquals("incorrect sign", 1, result.signum());
    }
}
