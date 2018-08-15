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

package org.apache.harmony.tests.java.nio;

import java.nio.ByteBuffer;

public class WrappedByteBufferTest extends ByteBufferTest {
    
    protected void setUp() throws Exception {
        super.setUp();
        buf = ByteBuffer.wrap(new byte[BUFFER_LENGTH]);
        baseBuf = buf;
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        buf = null;
        baseBuf = null;
    }
    
    /**
     * @tests java.nio.ByteBuffer#allocate(byte[],int,int)
     * 
     */
    public void testWrappedByteBuffer_IllegalArg() {
        byte array[] = new byte[BUFFER_LENGTH];
        try {
            ByteBuffer.wrap(array, -1, 0);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ByteBuffer.wrap(array, BUFFER_LENGTH + 1, 0);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ByteBuffer.wrap(array, 0, -1);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ByteBuffer.wrap(array, 0, BUFFER_LENGTH + 1);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ByteBuffer.wrap(array, 1, Integer.MAX_VALUE);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ByteBuffer.wrap(array, Integer.MAX_VALUE, 1);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
        try {
            ByteBuffer.wrap((byte[])null, 1, Integer.MAX_VALUE);
            fail("Should throw Exception"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // expected
        }
    }
    
    public void testIsDirect() {
        assertFalse(buf.isDirect());
    }

    public void testHasArray() {
        assertTrue(buf.hasArray());
    }

    public void testIsReadOnly() {
        assertFalse(buf.isReadOnly());
    }

}
