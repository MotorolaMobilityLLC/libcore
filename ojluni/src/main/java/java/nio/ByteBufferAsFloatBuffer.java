/*
 * Copyright (c) 2000, 2008, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.nio;

import libcore.io.Memory;

class ByteBufferAsFloatBuffer extends FloatBuffer {       // package-private

    protected final ByteBuffer bb;
    protected final int offset;
    private final ByteOrder order;

    ByteBufferAsFloatBuffer(ByteBuffer bb, ByteOrder order) {   // package-private
        super(-1, 0,
              bb.remaining() >> 2,
              bb.remaining() >> 2);
        this.bb = bb.duplicate().order(bb.order());
        this.isReadOnly = bb.isReadOnly;
        this.address = bb.address;
        this.order = order;
        int cap = this.capacity();
        this.limit(cap);
        int pos = this.position();
        assert (pos <= cap);
        offset = pos;
    }

    ByteBufferAsFloatBuffer(ByteBuffer bb,
                            int mark, int pos, int lim, int cap,
                            int off, ByteOrder order) {
        super(mark, pos, lim, cap);
        this.bb = bb.duplicate().order(bb.order());
        this.isReadOnly = bb.isReadOnly;
        this.address = bb.address;
        this.order = order;
        offset = off;
    }

    public FloatBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        int off = (pos << 2) + offset;
        assert (off >= 0);
        return new ByteBufferAsFloatBuffer(bb, -1, 0, rem, rem, off, order);
    }

    public FloatBuffer duplicate() {
        return new ByteBufferAsFloatBuffer(bb,
                                           markValue(),
                                           position(),
                                           limit(),
                                           capacity(),
                                           offset,
                                           order);
    }

    public FloatBuffer asReadOnlyBuffer() {
        return new ByteBufferAsFloatBuffer(bb.asReadOnlyBuffer(),
                                           markValue(),
                                           position(),
                                           limit(),
                                           capacity(),
                                           offset,
                                           order);
    }

    protected int ix(int i) {
        return (i << 2) + offset;
    }

    public float get() {
        return get(nextGetIndex());
    }

    public float get(int i) {
        return bb.getFloat(ix(i));
    }

    public FloatBuffer put(float x) {
        put(nextPutIndex(), x);
        return this;
    }

    public FloatBuffer put(int i, float x) {
        bb.putFloat(ix(i), x);
        return this;
    }

    public FloatBuffer compact() {
        if (isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        if (!(bb instanceof DirectByteBuffer)) {
            System.arraycopy(bb.array(), ix(pos), bb.array(), ix(0), rem << 2);
        } else {
            Memory.memmove(this, ix(0), this, ix(pos), rem << 2);
        }
        position(rem);
        limit(capacity());
        discardMark();
        return this;
    }

    public boolean isDirect() {
        return bb.isDirect();
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public ByteOrder order() {
        return order;
    }
}
