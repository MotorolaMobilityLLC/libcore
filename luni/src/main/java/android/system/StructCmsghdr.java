/*
 * Copyright (c) 2021, The Linux Foundation. All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *   *Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *   *Redistributions in binary form must reproduce the above
 *    copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided
 *    with the distribution.
 *   *Neither the name of The Linux Foundation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package android.system;

import libcore.util.NonNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Corresponds to C's {@code struct cmsghdr}.
 *
 */
public final class StructCmsghdr {
    /** Originating protocol */
    public final int cmsg_level;

    /** Protocol-specific type */
    public final int cmsg_type;

    /** message data sent/received */
    @NonNull public final byte[] cmsg_data;

    public StructCmsghdr(int cmsg_level, int cmsg_type, short value) {
        // Short.Size unit is bits, ByteBuffer data unit is bytes
        ByteBuffer buf = ByteBuffer.allocate(Short.SIZE / 8);
        buf.order(ByteOrder.nativeOrder());
        buf.putShort(value);

        this.cmsg_level = cmsg_level;
        this.cmsg_type = cmsg_type;
        this.cmsg_data = buf.array();
    }

    public StructCmsghdr(int cmsg_level, int cmsg_type, @NonNull byte[] value) {
        this.cmsg_level = cmsg_level;
        this.cmsg_type = cmsg_type;
        this.cmsg_data = value;
    }

}
