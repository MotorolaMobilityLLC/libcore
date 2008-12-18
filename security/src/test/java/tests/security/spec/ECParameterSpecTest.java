/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests.security.spec;

import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass;

import junit.framework.TestCase;

import java.math.BigInteger;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

@TestTargetClass(ECParameterSpec.class)
public class ECParameterSpecTest extends TestCase {

    EllipticCurve curve;

    ECPoint ecpoint;

    ECParameterSpec ecps;

    /**
     * Constructor for ECParameterSpecTest.
     * 
     * @param name
     */
    public ECParameterSpecTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        curve = new EllipticCurve(new ECFieldF2m(2), BigInteger.valueOf(1),
                BigInteger.valueOf(1));
        ecpoint = new ECPoint(BigInteger.valueOf(1), BigInteger.valueOf(1));
        ecps = new ECParameterSpec(curve, ecpoint, BigInteger.valueOf(1), 1);
    }

    protected void tearDown() throws Exception {
        curve = null;
        ecpoint = null;
        ecps = null;
        super.tearDown();
    }

    /**
     * test for ECParameterSpec(EllipticCurve, ECPoint, BigInteger, int) constructor
     * test covers following usecases: 
     * case 1: creating object with valid parameters
     * case 2: NullPointerException - if curve is null 
     * case 3: NullPointerException - if g is null
     * case 4: NullPointerException - if n is null
     * case 5: IllegalArgumentException - if n is not positive
     * case 6: IllegalArgumentException - if h is not positive
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "ECParameterSpec",
          methodArgs = {java.security.spec.EllipticCurve.class, java.security.spec.ECPoint.class, java.math.BigInteger.class, int.class}
        )
    })
    public void test_constructorLjava_security_spec_EllipticCurveLjava_security_spec_ECPointLjava_math_BigIntegerI() {
        
        // case 1: creating object with valid parameters
        assertEquals("wrong cofactor was returned", 1, ecps.getCofactor());
        assertEquals("wrong elliptic curve", curve, ecps.getCurve());
        assertEquals("wrong generator was returned", ecpoint, ecps
                .getGenerator());
        assertEquals("wrong order was reteurned", BigInteger.valueOf(1), ecps
                .getOrder());

        // case 2: NullPointerException - if curve is null.
        try {
            new ECParameterSpec(null, ecpoint, BigInteger.valueOf(1), 1);
            fail("NullPointerException exception has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }
        
        // case 3: NullPointerException - if g is null.
        try {
            new ECParameterSpec(curve, null, BigInteger.valueOf(1), 1);
            fail("NullPointerException exception has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }

        // case 4: NullPointerException - if n is null.
        try {
            new ECParameterSpec(curve, ecpoint, null, 1);
            fail("NullPointerException exception has not been thrown");
        } catch (NullPointerException e) {
            // expected
        }

        // case 5: IllegalArgumentException - if n is not positive.
        try {
            new ECParameterSpec(curve, ecpoint, BigInteger.valueOf(-1), 1);
            fail("IllegalArgumentException exception has not been thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // case 6: IllegalArgumentException - if h is not positive.
        try {
            new ECParameterSpec(curve, ecpoint, BigInteger.valueOf(1), -1);
            fail("IllegalArgumentException exception has not been thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * test for getCurve() method
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getCurve",
          methodArgs = {}
        )
    })
    public void test_GetCurve() {
        assertEquals("wrong elliptic curve", curve, ecps.getCurve());
    }

    /**
     * test for getGenerator() method
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getGenerator",
          methodArgs = {}
        )
    })
    public void test_GetGenerator() {
        assertEquals("wrong generator was returned", ecpoint, ecps
                .getGenerator());
    }

    /**
     * test for getOrder() method
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getOrder",
          methodArgs = {}
        )
    })
    public void test_GetOrder() {
        assertEquals("wrong order was reteurned", BigInteger.valueOf(1), ecps
                .getOrder());
    }

    /**
     * test for getCofactor() method
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getCofactor",
          methodArgs = {}
        )
    })
    public void test_GetCofactor() {
        assertEquals("wrong cofactor was returned", 1, ecps.getCofactor());
    }
}
