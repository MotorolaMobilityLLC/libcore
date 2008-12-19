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
package org.apache.harmony.nio.tests.java.nio;

import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@TestTargetClass(java.nio.FloatBuffer.class)
public class DirectFloatBufferTest extends FloatBufferTest {
    public void setUp(){
        buf = ByteBuffer.allocateDirect(BUFFER_LENGTH*4).asFloatBuffer();
        loadTestData1(buf);
        baseBuf = buf;
    }
    
    public void tearDown(){
        buf = null;
        baseBuf = null;
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies hasArray method for direct FloatBuffer.",
      targets = {
        @TestTarget(
          methodName = "hasArray",
          methodArgs = {}
        )
    })
    public void testHasArray() {
        assertFalse(buf.hasArray());
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies array method for direct FloatBuffer.",
      targets = {
        @TestTarget(
          methodName = "array",
          methodArgs = {}
        )
    })
    public void testArray() {
        try {
            buf.array();
            fail("Should throw UnsupportedOperationException"); //$NON-NLS-1$
        } catch (UnsupportedOperationException e) {
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies arrayOffset method for direct FloatBuffer.",
      targets = {
        @TestTarget(
          methodName = "arrayOffset",
          methodArgs = {}
        )
    })
    public void testArrayOffset() {
        try {
            buf.arrayOffset();
            fail("Should throw UnsupportedOperationException"); //$NON-NLS-1$
        } catch (UnsupportedOperationException e) {
            //expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies isDirect method for direct FloatBuffer.",
      targets = {
        @TestTarget(
          methodName = "isDirect",
          methodArgs = {}
        )
    })
    public void testIsDirect() {
        assertTrue(buf.isDirect());
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies order method for direct FloatBuffer.",
      targets = {
        @TestTarget(
          methodName = "order",
          methodArgs = {}
        )
    })
    public void testOrder() {
        assertEquals(ByteOrder.BIG_ENDIAN, buf.order());
    }
}
