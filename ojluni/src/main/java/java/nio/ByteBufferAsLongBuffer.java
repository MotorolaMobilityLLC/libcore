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

class ByteBufferAsLongBuffer extends LongBuffer {                 // package-private

    protected final ByteBuffer bb;
    protected final int offset;
    private final boolean isReadOnly;
    private final ByteOrder order;

    ByteBufferAsLongBuffer(ByteBuffer bb, ByteOrder order) {
        this(bb, order, false);
    }

    ByteBufferAsLongBuffer(ByteBuffer bb, ByteOrder order, boolean isReadOnly) {   // package-privaet8
        super(-1, 0,
              bb.remaining() >> 3,
              bb.remaining() >> 3);
        this.bb = bb;
        this.order = order;
        this.isReadOnly = isReadOnly;
        int cap = this.capacity();
        this.limit(cap);
        int pos = this.position();
        assert (pos <= cap);
        offset = pos;
    }

    ByteBufferAsLongBuffer(ByteBuffer bb,
                           int mark, int pos, int lim, int cap,
                           int off, ByteOrder order) {
        this(bb, mark, pos, lim, cap, off, order, false);
    }

    ByteBufferAsLongBuffer(ByteBuffer bb,
                           int mark, int pos, int lim, int cap,
                           int off, ByteOrder order, boolean isReadOnly) {
        super(mark, pos, lim, cap);
        this.bb = bb;
        this.order = order;
        this.isReadOnly = isReadOnly;
        offset = off;
    }

    public LongBuffer slice() {
        int pos = this.position();
        int lim = this.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        int off = (pos << 3) + offset;
        assert (off >= 0);
        return new ByteBufferAsLongBuffer(bb, -1, 0, rem, rem, off, order, isReadOnly);
    }

    public LongBuffer duplicate() {
        return new ByteBufferAsLongBuffer(bb,
                                          this.markValue(),
                                          this.position(),
                                          this.limit(),
                                          this.capacity(),
                                          offset,
                                          order,
                                          isReadOnly);
    }

    public LongBuffer asReadOnlyBuffer() {
        return new ByteBufferAsLongBuffer(bb,
                                          this.markValue(),
                                          this.position(),
                                          this.limit(),
                                          this.capacity(),
                                          offset,
                                          order,
                                          true);
    }

    protected int ix(int i) {
        return (i << 3) + offset;
    }

    public long get() {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return Bits.getLongL(bb, ix(nextGetIndex()));
        } else {
            return Bits.getLongB(bb, ix(nextGetIndex()));
        }
    }

    public long get(int i) {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            return Bits.getLongL(bb, ix(checkIndex(i)));
        } else {
            return Bits.getLongB(bb, ix(checkIndex(i)));
        }
    }

    public LongBuffer put(long x) {
        if (isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (order == ByteOrder.LITTLE_ENDIAN) {
            Bits.putLongL(bb, ix(nextPutIndex()), x);
        } else {
            Bits.putLongB(bb, ix(nextPutIndex()), x);
        }
        return this;
    }

    public LongBuffer put(int i, long x) {
        if (isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        if (order == ByteOrder.LITTLE_ENDIAN) {
            Bits.putLongL(bb, ix(checkIndex(i)), x);
        } else {
            Bits.putLongB(bb, ix(checkIndex(i)), x);
        }
        return this;
    }

    public LongBuffer compact() {
        if (isReadOnly) {
            throw new ReadOnlyBufferException();
        }
        int pos = position();
        int lim = limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        ByteBuffer db = bb.duplicate();
        db.limit(ix(lim));
        db.position(ix(0));
        ByteBuffer sb = db.slice();
        sb.position(pos << 3);
        sb.compact();
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
