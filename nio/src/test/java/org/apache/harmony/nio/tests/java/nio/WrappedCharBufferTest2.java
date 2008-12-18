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

import java.nio.BufferOverflowException;
import java.nio.CharBuffer;
import java.nio.ReadOnlyBufferException;

@TestTargetClass(java.nio.CharBuffer.class)
public class WrappedCharBufferTest2 extends ReadOnlyCharBufferTest {
    protected static final String TEST_STRING = "123456789abcdef12345";

    protected void setUp() throws Exception {
        super.setUp();
        buf = CharBuffer.wrap(TEST_STRING);
        baseBuf = buf;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        baseBuf = null;
        buf = null;
    }
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies NullPointerException, IndexOutOfBoundsException.",
      targets = {
        @TestTarget(
          methodName = "wrap",
          methodArgs = {java.lang.CharSequence.class, int.class, int.class}
        )
    })
    public void testWrappedCharSequence_IllegalArg() {
        String str = TEST_STRING;
        try {
            CharBuffer.wrap(str, -1, 0);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            CharBuffer.wrap(str, 21, 21);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            CharBuffer.wrap(str, 2, 1);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            CharBuffer.wrap(str, 0, 21);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            CharBuffer.wrap((String)null, -1, 21);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies UnsupportedOperationException.",
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
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies ReadOnlyBufferException, NullPointerException, " +
            "BufferOverflowException, IndexOutOfBoundsException.",
      targets = {
        @TestTarget(
          methodName = "put",
          methodArgs = {char[].class, int.class, int.class}
        )
    })
    public void testPutcharArrayintint() {
        char array[] = new char[1];
        try {
            buf.put(array, 0, array.length);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put((char[]) null, 0, 1);
            fail("Should throw NullPointerException"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // expected
        }
        try {
            buf.put(new char[buf.capacity() + 1], 0, buf.capacity() + 1);
            fail("Should throw BufferOverflowException"); //$NON-NLS-1$
        } catch (BufferOverflowException e) {
            // expected
        }
        try {
            buf.put(array, -1, array.length);
            fail("Should throw IndexOutOfBoundsException"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies ReadOnlyBufferException, NullPointerException, " +
            "IllegalArgumentException.",
      targets = {
        @TestTarget(
          methodName = "read",
          methodArgs = {java.nio.CharBuffer.class}
        )
    })
    public void testPutCharBuffer() {
        CharBuffer other = CharBuffer.allocate(1);
        try {
            buf.put(other);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put((CharBuffer) null);
            fail("Should throw NullPointerException"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // expected
        }
        try {
            buf.put(buf);
            fail("Should throw IllegalArgumentException"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }    
}
