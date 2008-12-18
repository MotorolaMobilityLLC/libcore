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

import java.nio.IntBuffer;
import java.nio.ReadOnlyBufferException;

@TestTargetClass(java.nio.IntBuffer.class)
public class ReadOnlyIntBufferTest extends IntBufferTest {
    protected void setUp() throws Exception {
        super.setUp();
        buf = buf.asReadOnlyBuffer();
        baseBuf = buf;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies isReadOnly method for read only IntBuffer.",
      targets = {
        @TestTarget(
          methodName = "isReadOnly",
          methodArgs = {}
        )
    })
    public void testIsReadOnly() {
        assertTrue(buf.isReadOnly());
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies hasArray method returns false for read only " +
            "IntBuffer.",
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
      purpose = "Verifies ReadOnlyBufferException.",
      targets = {
        @TestTarget(
          methodName = "array",
          methodArgs = {}
        )
    })
    public void testArray() {
        try {
            buf.array();
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            //expected
        }
    }
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "hashCode",
          methodArgs = {}
        )
    })
    public void testHashCode() {
        IntBuffer duplicate = buf.duplicate();
        assertEquals(buf.hashCode(), duplicate.hashCode());
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies UnsupportedOperationException.",
      targets = {
        @TestTarget(
          methodName = "arrayOffset",
          methodArgs = {}
        )
    })
    public void testArrayOffset() {
        try {
            buf.arrayOffset();
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (UnsupportedOperationException e) {
            //expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies ReadOnlyBufferException.",
      targets = {
        @TestTarget(
          methodName = "compact",
          methodArgs = {}
        )
    })
    public void testCompact() {
        try {
            buf.compact();
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies ReadOnlyBufferException.",
      targets = {
        @TestTarget(
          methodName = "put",
          methodArgs = {int.class}
        )
    })
    public void testPutint() {
        try {
            buf.put(0);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies ReadOnlyBufferException.",
      targets = {
        @TestTarget(
          methodName = "put",
          methodArgs = {int[].class}
        )
    })
    public void testPutintArray() {
        int array[] = new int[1];
        try {
            buf.put(array);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put((int[]) null);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies ReadOnlyBufferException.",
      targets = {
        @TestTarget(
          methodName = "put",
          methodArgs = {int[].class, int.class, int.class}
        )
    })
    public void testPutintArrayintint() {
        int array[] = new int[1];
        try {
            buf.put(array, 0, array.length);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put((int[]) null, -1, 1);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put(new int[buf.capacity() + 1], 0, buf.capacity() + 1);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put(array, -1, array.length);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies ReadOnlyBufferException.",
      targets = {
        @TestTarget(
          methodName = "put",
          methodArgs = {java.nio.IntBuffer.class}
        )
    })
    public void testPutIntBuffer() {
        IntBuffer other = IntBuffer.allocate(1);
        try {
            buf.put(other);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put((IntBuffer) null);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put(buf);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
    }
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies ReadOnlyBufferException.",
      targets = {
        @TestTarget(
          methodName = "put",
          methodArgs = {int.class, int.class}
        )
    })
    public void testPutintint() {
        try {
            buf.put(0, (int) 0);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
        try {
            buf.put(-1, (int) 0);
            fail("Should throw ReadOnlyBufferException"); //$NON-NLS-1$
        } catch (ReadOnlyBufferException e) {
            // expected
        }
    }
}
