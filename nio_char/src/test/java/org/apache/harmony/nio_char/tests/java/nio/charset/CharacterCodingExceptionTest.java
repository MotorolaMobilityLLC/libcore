/* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.harmony.nio_char.tests.java.nio.charset;

import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestInfo;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestLevel;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;

import junit.framework.TestCase;

import org.apache.harmony.testframework.serialization.SerializationTest;

@TestTargetClass(CharacterCodingException.class)
/**
 * Test CharacterCodingException
 */
public class CharacterCodingExceptionTest extends TestCase {

@TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "CharacterCodingException",
          methodArgs = {}
        )
    })
    public void testConstructor() {
        CharacterCodingException ex = new CharacterCodingException();
        assertTrue(ex instanceof IOException);
        assertNull(ex.getCause());
        assertNull(ex.getMessage());
    }

    /**
     * @tests serialization/deserialization compatibility.
     */
@TestInfo(
          level = TestLevel.COMPLETE,
          purpose = "Verifies serialization.",
          targets = {
            @TestTarget(
              methodName = "!SerializationSelf",
              methodArgs = {}
            )
        })
    public void testSerializationSelf() throws Exception {

        SerializationTest.verifySelf(new CharacterCodingException());
    }

    /**
     * @tests serialization/deserialization compatibility with RI.
     */
@TestInfo(
          level = TestLevel.COMPLETE,
          purpose = "Verifies serialization.",
          targets = {
            @TestTarget(
              methodName = "!SerializationGolden",
              methodArgs = {}
            )
        })
    public void testSerializationCompatibility() throws Exception {
        SerializationTest.verifyGolden(this, new CharacterCodingException());

    }
}
