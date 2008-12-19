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
* @author Vladimir N. Molotkov
* @version $Revision$
*/

package tests.security.spec;

import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass;

import junit.framework.TestCase;

import java.math.BigInteger;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;

/**
 * Tests for <code>RSAPrivateKeySpec</code> class fields and methods
 * 
 */
@TestTargetClass(RSAPrivateKeySpec.class)
public class RSAPrivateKeySpecTest extends TestCase {

    /**
     * Constructor for RSAPrivateKeySpecTest.
     * @param name
     */
    public RSAPrivateKeySpecTest(String name) {
        super(name);
    }

    /**
     * Test for <code>RSAPrivateKeySpec(BigInteger,BigInteger)</code> ctor
     * Assertion: constructs <code>RSAPrivateKeySpec</code>
     * object using valid parameters
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "RSAPrivateKeySpec",
          methodArgs = {java.math.BigInteger.class, java.math.BigInteger.class}
        )
    })
    public final void testRSAPrivateKeySpec() {
        KeySpec ks = new RSAPrivateKeySpec(BigInteger.valueOf(1234567890L),
                                           BigInteger.valueOf(3L));
        assertTrue(ks instanceof RSAPrivateKeySpec);
    }

    /**
     * Test for <code>getModulus()</code> method<br>
     * Assertion: returns modulus
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getModulus",
          methodArgs = {}
        )
    })
    public final void testGetModulus() {
        RSAPrivateKeySpec rpks =
            new RSAPrivateKeySpec(BigInteger.valueOf(1234567890L),
                                  BigInteger.valueOf(3L));
        assertEquals(1234567890L, rpks.getModulus().longValue());
    }

    /**
     * Test for <code>getPrivateExponent()</code> method<br>
     * Assertion: returns private exponent
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getPrivateExponent",
          methodArgs = {}
        )
    })
    public final void testGetPrivateExponent() {
        RSAPrivateKeySpec rpks =
            new RSAPrivateKeySpec(BigInteger.valueOf(1234567890L),
                                  BigInteger.valueOf(3L));
        assertEquals(3L, rpks.getPrivateExponent().longValue());
    }

}
