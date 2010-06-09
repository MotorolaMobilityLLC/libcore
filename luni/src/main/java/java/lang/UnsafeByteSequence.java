/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.lang;

/**
 * A cheaper ByteArrayOutputStream for internal use. This class is unsynchronized,
 * and returns its internal array if it's the right size. This makes String.getBytes("UTF-8")
 * 10x faster than the baseline non-fast-path implementation instead of 8x faster when using
 * ByteArrayOutputStream. When GC and uncontended synchronization become cheap, we should be
 * able to get rid of this class. In the meantime, if you need to add further API, please try
 * to keep it plug-compatible with ByteArrayOutputStream with an eye to future obsolescence.
 *
 * @hide
 */
class UnsafeByteSequence {
    private byte[] bytes;
    private int count;

    public UnsafeByteSequence(int initialCapacity) {
        this.bytes = new byte[initialCapacity];
    }

    public void write(int b) {
        if (count == bytes.length) {
            byte[] newBytes = new byte[count * 2];
            System.arraycopy(bytes, 0, newBytes, 0, count);
            bytes = newBytes;
        }
        bytes[count++] = (byte) b;
    }

    public byte[] toByteArray() {
        if (count == bytes.length) {
            return bytes;
        }
        byte[] result = new byte[count];
        System.arraycopy(bytes, 0, result, 0, count);
        return result;
    }
}
