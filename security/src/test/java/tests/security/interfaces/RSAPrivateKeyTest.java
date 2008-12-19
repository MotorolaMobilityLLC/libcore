/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.security.interfaces;
import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass;

import junit.framework.TestCase;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;

@TestTargetClass(RSAPrivateKey.class)
public class RSAPrivateKeyTest extends TestCase {
    
    /**
     * @tests java.security.interfaces.RSAPrivateKey
     * #getPrivateExponent()
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
    public void test_getPrivateExponent() throws Exception {
        KeyFactory gen = KeyFactory.getInstance("RSA", Util.prov);
        final BigInteger n = BigInteger.valueOf(3233);
        final BigInteger d = BigInteger.valueOf(2753);
        RSAPrivateKey key = (RSAPrivateKey) gen.generatePrivate(new RSAPrivateKeySpec(
                n, d));
        assertEquals("invalid private exponent", d, key.getPrivateExponent());
    }
}
