/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.harmony.security.tests.java.security;

import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;

import java.security.SignatureException;

@TestTargetClass(SignatureException.class)
public class SignatureException2Test extends junit.framework.TestCase {

    /**
     * @tests java.security.SignatureException#SignatureException()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "SignatureException",
          methodArgs = {}
        )
    })
    public void test_Constructor() {
        // Test for method java.security.SignatureException()
        SignatureException e = new SignatureException();
        assertEquals("Failed toString test for constructed instance", "java.security.SignatureException", e
                .toString());
    }

    /**
     * @tests java.security.SignatureException#SignatureException(java.lang.String)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verification with null string parameter missed",
      targets = {
        @TestTarget(
          methodName = "SignatureException",
          methodArgs = {String.class}
        )
    })
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.security.SignatureException(java.lang.String)
        SignatureException e = new SignatureException("test message");
        assertEquals("Failed toString test for constructed instance", 
                        "java.security.SignatureException: test message", e
                .toString());
    }
}