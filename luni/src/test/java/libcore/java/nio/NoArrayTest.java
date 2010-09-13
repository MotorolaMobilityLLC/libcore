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

package libcore.java.nio;

import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import junit.framework.TestCase;

public final class NoArrayTest extends TestCase {

    public void testWrappedReadOnly() {
        assertNoArray(ByteBuffer.wrap(new byte[32]).asReadOnlyBuffer());
    }

    public void testAllocatedReadOnly() {
        assertNoArray(ByteBuffer.allocate(32).asReadOnlyBuffer());
    }

    public void testAllocatedDirect() {
        assertNoArray(ByteBuffer.allocateDirect(32));
    }

    private void assertNoArray(ByteBuffer buf) {
        try {
            buf.asReadOnlyBuffer().array();
            fail();
        } catch (ReadOnlyBufferException expected) {
        } catch (UnsupportedOperationException expected) {
        }
        try {
            buf.arrayOffset();
            fail();
        } catch (ReadOnlyBufferException expected) {
        } catch (UnsupportedOperationException expected) {
        }
    }
}
