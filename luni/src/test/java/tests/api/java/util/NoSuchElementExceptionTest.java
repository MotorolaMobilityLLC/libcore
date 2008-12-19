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

package tests.api.java.util;

import dalvik.annotation.TestTarget;
import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass; 

import java.util.NoSuchElementException;
import java.util.Vector;

@TestTargetClass(NoSuchElementException.class) 
public class NoSuchElementExceptionTest extends junit.framework.TestCase {

    /**
     * @tests java.util.NoSuchElementException#NoSuchElementException()
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Need to call constructor directly.",
      targets = {
        @TestTarget(
          methodName = "NoSuchElementException",
          methodArgs = {}
        )
    })
    public void test_Constructor() {
        // Test for method java.util.NoSuchElementException()

        try {
            Vector v = new Vector();
            v.elements().nextElement();
        } catch (NoSuchElementException e) {
            return;
        }
        // if we make it to here, assert a fail
        fail("Failed to catch expected Exception");
    }

    /**
     * @tests java.util.NoSuchElementException#NoSuchElementException(java.lang.String)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Doesn't call constructor with different parameters.",
      targets = {
        @TestTarget(
          methodName = "NoSuchElementException",
          methodArgs = {java.lang.String.class}
        )
    })
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.util.NoSuchElementException(java.lang.String)

        try {
            Vector v = new Vector();
            v.firstElement();
        } catch (NoSuchElementException e) {
            return;
        }
        // if we make it to here, assert a fail
        fail("Failed to catch Exception");
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() {
    }
}
