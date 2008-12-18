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
import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;

import java.security.NoSuchAlgorithmException;

@TestTargetClass(NoSuchAlgorithmException.class)
public class NoSuchAlgorithmException2Test extends junit.framework.TestCase {

    /**
     * @tests java.security.NoSuchAlgorithmException#NoSuchAlgorithmException()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "NoSuchAlgorithmException",
          methodArgs = {}
        )
    })
    public void test_Constructor() {
        try {
            throw new NoSuchAlgorithmException();
        } catch (NoSuchAlgorithmException e) {
            assertNull("Message should be null", e.getMessage());
            assertEquals("Unexpected toString value",
                    "java.security.NoSuchAlgorithmException", e.toString());
        }
    }

    /**
     * @tests java.security.NoSuchAlgorithmException#NoSuchAlgorithmException(java.lang.String)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Null parameter checking missed",
      targets = {
        @TestTarget(
          methodName = "NoSuchAlgorithmException",
          methodArgs = {String.class}
        )
    })
    public void test_ConstructorLjava_lang_String() {
        // Test for method
        // java.security.NoSuchAlgorithmException(java.lang.String)
        try {
            throw new NoSuchAlgorithmException("Test string");
        } catch (NoSuchAlgorithmException e) {
            assertEquals("Wrong message", "Test string", e.getMessage());
            assertEquals("Unexpected toString value",
                    "java.security.NoSuchAlgorithmException: Test string", e
                            .toString());
        }
    }
}