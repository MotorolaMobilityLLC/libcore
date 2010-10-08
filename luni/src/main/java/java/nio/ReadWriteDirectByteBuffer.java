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

package java.nio;

import org.apache.harmony.luni.platform.OSMemory;

/**
 * DirectByteBuffer, ReadWriteDirectByteBuffer and ReadOnlyDirectByteBuffer
 * compose the implementation of platform memory based byte buffers.
 * <p>
 * ReadWriteDirectByteBuffer extends DirectByteBuffer with all the write
 * methods.
 * </p>
 * <p>
 * This class is marked final for runtime performance.
 * </p>
 */
final class ReadWriteDirectByteBuffer extends DirectByteBuffer {
    static ReadWriteDirectByteBuffer copy(DirectByteBuffer other, int markOfOther) {
        ReadWriteDirectByteBuffer buf =
                new ReadWriteDirectByteBuffer(other.block, other.capacity(), other.offset);
        buf.limit = other.limit;
        buf.position = other.position();
        buf.mark = markOfOther;
        return buf;
    }

    // Used by ByteBuffer.allocateDirect.
    ReadWriteDirectByteBuffer(int capacity) {
        super(MemoryBlock.malloc(capacity), capacity, 0);
    }

    // Used by the JNI NewDirectByteBuffer function.
    ReadWriteDirectByteBuffer(int address, int capacity) {
        super(MemoryBlock.wrapFromJni(address, capacity), capacity, 0);
    }

    ReadWriteDirectByteBuffer(MemoryBlock block, int capacity, int offset) {
        super(block, capacity, offset);
    }

    @Override
    public ByteBuffer asReadOnlyBuffer() {
        return ReadOnlyDirectByteBuffer.copy(this, mark);
    }

    @Override
    public ByteBuffer compact() {
        int addr = effectiveDirectAddress;
        OSMemory.memmove(addr, addr + position, remaining());
        position = limit - position;
        limit = capacity;
        mark = UNSET_MARK;
        return this;
    }

    @Override
    public ByteBuffer duplicate() {
        return copy(this, mark);
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public ByteBuffer put(byte value) {
        if (position == limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeByte(offset + position++, value);
        return this;
    }

    @Override
    public ByteBuffer put(int index, byte value) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException();
        }
        this.block.pokeByte(offset + index, value);
        return this;
    }

    @Override
    public ByteBuffer put(byte[] src, int srcOffset, int byteCount) {
        checkPutBounds(1, src.length, srcOffset, byteCount);
        this.block.pokeByteArray(offset + position, src, srcOffset, byteCount);
        position += byteCount;
        return this;
    }

    final void put(char[] src, int srcOffset, int charCount) {
        int byteCount = checkPutBounds(SIZEOF_CHAR, src.length, srcOffset, charCount);
        this.block.pokeCharArray(offset + position, src, srcOffset, charCount, order.needsSwap);
        position += byteCount;
    }

    final void put(double[] src, int srcOffset, int doubleCount) {
        int byteCount = checkPutBounds(SIZEOF_DOUBLE, src.length, srcOffset, doubleCount);
        this.block.pokeDoubleArray(offset + position, src, srcOffset, doubleCount, order.needsSwap);
        position += byteCount;
    }

    final void put(float[] src, int srcOffset, int floatCount) {
        int byteCount = checkPutBounds(SIZEOF_FLOAT, src.length, srcOffset, floatCount);
        this.block.pokeFloatArray(offset + position, src, srcOffset, floatCount, order.needsSwap);
        position += byteCount;
    }

    final void put(int[] src, int srcOffset, int intCount) {
        int byteCount = checkPutBounds(SIZEOF_INT, src.length, srcOffset, intCount);
        this.block.pokeIntArray(offset + position, src, srcOffset, intCount, order.needsSwap);
        position += byteCount;
    }

    final void put(long[] src, int srcOffset, int longCount) {
        int byteCount = checkPutBounds(SIZEOF_LONG, src.length, srcOffset, longCount);
        this.block.pokeLongArray(offset + position, src, srcOffset, longCount, order.needsSwap);
        position += byteCount;
    }

    final void put(short[] src, int srcOffset, int shortCount) {
        int byteCount = checkPutBounds(SIZEOF_SHORT, src.length, srcOffset, shortCount);
        this.block.pokeShortArray(offset + position, src, srcOffset, shortCount, order.needsSwap);
        position += byteCount;
    }

    @Override
    public ByteBuffer putChar(char value) {
        int newPosition = position + SIZEOF_CHAR;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeShort(offset + position, (short) value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putChar(int index, char value) {
        if (index < 0 || (long) index + SIZEOF_CHAR > limit) {
            throw new IndexOutOfBoundsException();
        }
        this.block.pokeShort(offset + index, (short) value, order);
        return this;
    }

    @Override
    public ByteBuffer putDouble(double value) {
        int newPosition = position + SIZEOF_DOUBLE;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeLong(offset + position, Double.doubleToRawLongBits(value), order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putDouble(int index, double value) {
        if (index < 0 || (long) index + SIZEOF_DOUBLE > limit) {
            throw new IndexOutOfBoundsException();
        }
        this.block.pokeLong(offset + index, Double.doubleToRawLongBits(value), order);
        return this;
    }

    @Override
    public ByteBuffer putFloat(float value) {
        int newPosition = position + SIZEOF_FLOAT;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeInt(offset + position, Float.floatToRawIntBits(value), order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putFloat(int index, float value) {
        if (index < 0 || (long) index + SIZEOF_FLOAT > limit) {
            throw new IndexOutOfBoundsException();
        }
        this.block.pokeInt(offset + index, Float.floatToRawIntBits(value), order);
        return this;
    }

    @Override
    public ByteBuffer putInt(int value) {
        int newPosition = position + SIZEOF_INT;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeInt(offset + position, value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putInt(int index, int value) {
        if (index < 0 || (long) index + SIZEOF_INT > limit) {
            throw new IndexOutOfBoundsException();
        }
        this.block.pokeInt(offset + index, value, order);
        return this;
    }

    @Override
    public ByteBuffer putLong(long value) {
        int newPosition = position + SIZEOF_LONG;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeLong(offset + position, value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putLong(int index, long value) {
        if (index < 0 || (long) index + SIZEOF_LONG > limit) {
            throw new IndexOutOfBoundsException();
        }
        this.block.pokeLong(offset + index, value, order);
        return this;
    }

    @Override
    public ByteBuffer putShort(short value) {
        int newPosition = position + SIZEOF_SHORT;
        if (newPosition > limit) {
            throw new BufferOverflowException();
        }
        this.block.pokeShort(offset + position, value, order);
        position = newPosition;
        return this;
    }

    @Override
    public ByteBuffer putShort(int index, short value) {
        if (index < 0 || (long) index + SIZEOF_SHORT > limit) {
            throw new IndexOutOfBoundsException();
        }
        this.block.pokeShort(offset + index, value, order);
        return this;
    }

    @Override
    public ByteBuffer slice() {
        return new ReadWriteDirectByteBuffer(block, remaining(), offset + position);
    }

}
