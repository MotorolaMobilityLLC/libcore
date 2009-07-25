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

package java.nio;

// BEGIN android-added
// copied from newer version of harmony
import org.apache.harmony.nio.internal.DirectBuffer;
import org.apache.harmony.luni.platform.PlatformAddress;
// END android-added

/**
 * This class wraps a byte buffer to be a long buffer.
 * <p>
 * Implementation notice:
 * <ul>
 * <li>After a byte buffer instance is wrapped, it becomes privately owned by
 * the adapter. It must NOT be accessed outside the adapter any more.</li>
 * <li>The byte buffer's position and limit are NOT linked with the adapter.
 * The adapter extends Buffer, thus has its own position and limit.</li>
 * </ul>
 * </p>
 * 
 */
// BEGIN android-changed
// copied from newer version of harmony
final class LongToByteBufferAdapter extends LongBuffer implements DirectBuffer {
// END android-changed

    static LongBuffer wrap(ByteBuffer byteBuffer) {
        return new LongToByteBufferAdapter(byteBuffer.slice());
    }

    private final ByteBuffer byteBuffer;

    LongToByteBufferAdapter(ByteBuffer byteBuffer) {
        super((byteBuffer.capacity() >> 3));
        this.byteBuffer = byteBuffer;
        this.byteBuffer.clear();
    }

    // BEGIN android-added
    // copied from newer version of harmony
    public int getByteCapacity() {
        if (byteBuffer instanceof DirectBuffer) {
            return ((DirectBuffer)byteBuffer).getByteCapacity();
        } else {
            assert false : byteBuffer;
            return -1;
        }            
    }

    public PlatformAddress getEffectiveAddress() {
        if (byteBuffer instanceof DirectBuffer) {
            // BEGIN android-changed
            PlatformAddress addr = ((DirectBuffer)byteBuffer).getEffectiveAddress();
            effectiveDirectAddress = addr.toInt();
            return addr;
            // END android-changed
        } else {
            assert false : byteBuffer;
            return null;
        }
    }

    public PlatformAddress getBaseAddress() {
        if (byteBuffer instanceof DirectBuffer) {
            return ((DirectBuffer)byteBuffer).getBaseAddress();
        } else {
            assert false : byteBuffer;
            return null;
        }
    }
            
    public boolean isAddressValid() {
        if (byteBuffer instanceof DirectBuffer) {
            return ((DirectBuffer)byteBuffer).isAddressValid();
        } else {
            assert false : byteBuffer;
            return false;
        }
    }

  public void addressValidityCheck() {
        if (byteBuffer instanceof DirectBuffer) {
            ((DirectBuffer)byteBuffer).addressValidityCheck();
        } else {
            assert false : byteBuffer;
        }
    }
            
    public void free() {
        if (byteBuffer instanceof DirectBuffer) {
            ((DirectBuffer)byteBuffer).free();
        } else {
            assert false : byteBuffer;
        }   
    }
    // END android-added

    public LongBuffer asReadOnlyBuffer() {
        LongToByteBufferAdapter buf = new LongToByteBufferAdapter(byteBuffer
                .asReadOnlyBuffer());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    public LongBuffer compact() {
        if (byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        byteBuffer.limit(limit << 3);
        byteBuffer.position(position << 3);
        byteBuffer.compact();
        byteBuffer.clear();
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    public LongBuffer duplicate() {
        LongToByteBufferAdapter buf = new LongToByteBufferAdapter(byteBuffer
                .duplicate());
        buf.limit = limit;
        buf.position = position;
        buf.mark = mark;
        return buf;
    }

    public long get() {
        if (position == limit) {
            throw new BufferUnderflowException();
        }
        return byteBuffer.getLong(position++ << 3);
    }

    public long get(int index) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        return byteBuffer.getLong(index << 3);
    }

    public boolean isDirect() {
        return byteBuffer.isDirect();
    }

    public boolean isReadOnly() {
        return byteBuffer.isReadOnly();
    }

    public ByteOrder order() {
        return byteBuffer.order();
    }

    protected long[] protectedArray() {
        throw new UnsupportedOperationException();
    }

    protected int protectedArrayOffset() {
        throw new UnsupportedOperationException();
    }

    protected boolean protectedHasArray() {
        return false;
    }

    public LongBuffer put(long c) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        byteBuffer.putLong(position++ << 3, c);
        return this;
    }

    public LongBuffer put(int index, long c) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        byteBuffer.putLong(index << 3, c);
        return this;
    }

    public LongBuffer slice() {
        byteBuffer.limit(limit << 3);
        byteBuffer.position(position << 3);
        LongBuffer result = new LongToByteBufferAdapter(byteBuffer.slice());
        byteBuffer.clear();
        return result;
    }

}
