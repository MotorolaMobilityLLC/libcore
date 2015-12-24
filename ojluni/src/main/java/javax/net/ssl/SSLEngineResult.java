/*
 * Copyright (c) 2003, 2004, Oracle and/or its affiliates. All rights reserved.
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

package javax.net.ssl;

/**
 * An encapsulation of the result state produced by
 * <code>SSLEngine</code> I/O calls.
 *
 * <p> A <code>SSLEngine</code> provides a means for establishing
 * secure communication sessions between two peers.  <code>SSLEngine</code>
 * operations typically consume bytes from an input buffer and produce
 * bytes in an output buffer.  This class provides operational result
 * values describing the state of the <code>SSLEngine</code>, including
 * indications of what operations are needed to finish an
 * ongoing handshake.  Lastly, it reports the number of bytes consumed
 * and produced as a result of this operation.
 *
 * @see SSLEngine
 * @see SSLEngine#wrap(ByteBuffer, ByteBuffer)
 * @see SSLEngine#unwrap(ByteBuffer, ByteBuffer)
 *
 * @author Brad R. Wetmore
 * @since 1.5
 */

public class SSLEngineResult {

    /**
     * An <code>SSLEngineResult</code> enum describing the overall result
     * of the <code>SSLEngine</code> operation.
     *
     * The <code>Status</code> value does not reflect the
     * state of a <code>SSLEngine</code> handshake currently
     * in progress.  The <code>SSLEngineResult's HandshakeStatus</code>
     * should be consulted for that information.
     *
     * @author Brad R. Wetmore
     * @since 1.5
     */
    public static enum Status {

        /**
         * The <code>SSLEngine</code> was not able to unwrap the
         * incoming data because there were not enough source bytes
         * available to make a complete packet.
         *
         * <P>
         * Repeat the call once more bytes are available.
         */
        BUFFER_UNDERFLOW,

        /**
         * The <code>SSLEngine</code> was not able to process the
         * operation because there are not enough bytes available in the
         * destination buffer to hold the result.
         * <P>
         * Repeat the call once more bytes are available.
         *
         * @see SSLSession#getPacketBufferSize()
         * @see SSLSession#getApplicationBufferSize()
         */
        BUFFER_OVERFLOW,

        /**
         * The <code>SSLEngine</code> completed the operation, and
         * is available to process similar calls.
         */
        OK,

        /**
         * The operation just closed this side of the
         * <code>SSLEngine</code>, or the operation
         * could not be completed because it was already closed.
         */
        CLOSED;
    }

    /**
     * An <code>SSLEngineResult</code> enum describing the current
     * handshaking state of this <code>SSLEngine</code>.
     *
     * @author Brad R. Wetmore
     * @since 1.5
     */
    public static enum HandshakeStatus {

        /**
         * The <code>SSLEngine</code> is not currently handshaking.
         */
        NOT_HANDSHAKING,

        /**
         * The <code>SSLEngine</code> has just finished handshaking.
         * <P>
         * This value is only generated by a call to
         * <code>SSLEngine.wrap()/unwrap()</code> when that call
         * finishes a handshake.  It is never generated by
         * <code>SSLEngine.getHandshakeStatus()</code>.
         *
         * @see SSLEngine#wrap(ByteBuffer, ByteBuffer)
         * @see SSLEngine#unwrap(ByteBuffer, ByteBuffer)
         * @see SSLEngine#getHandshakeStatus()
         */
        FINISHED,

        /**
         * The <code>SSLEngine</code> needs the results of one (or more)
         * delegated tasks before handshaking can continue.
         *
         * @see SSLEngine#getDelegatedTask()
         */
        NEED_TASK,

        /**
         * The <code>SSLEngine</code> must send data to the remote side
         * before handshaking can continue, so <code>SSLEngine.wrap()</code>
         * should be called.
         *
         * @see SSLEngine#wrap(ByteBuffer, ByteBuffer)
         */
        NEED_WRAP,

        /**
         * The <code>SSLEngine</code> needs to receive data from the
         * remote side before handshaking can continue.
         */
        NEED_UNWRAP;
    }


    private final Status status;
    private final HandshakeStatus handshakeStatus;
    private final int bytesConsumed;
    private final int bytesProduced;

    /**
     * Initializes a new instance of this class.
     *
     * @param   status
     *          the return value of the operation.
     *
     * @param   handshakeStatus
     *          the current handshaking status.
     *
     * @param   bytesConsumed
     *          the number of bytes consumed from the source ByteBuffer
     *
     * @param   bytesProduced
     *          the number of bytes placed into the destination ByteBuffer
     *
     * @throws  IllegalArgumentException
     *          if the <code>status</code> or <code>handshakeStatus</code>
     *          arguments are null, or if <<code>bytesConsumed</code> or
     *          <code>bytesProduced</code> is negative.
     */
    public SSLEngineResult(Status status, HandshakeStatus handshakeStatus,
            int bytesConsumed, int bytesProduced) {

        if ((status == null) || (handshakeStatus == null) ||
                (bytesConsumed < 0) || (bytesProduced < 0)) {
            throw new IllegalArgumentException("Invalid Parameter(s)");
        }

        this.status = status;
        this.handshakeStatus = handshakeStatus;
        this.bytesConsumed = bytesConsumed;
        this.bytesProduced = bytesProduced;
    }

    /**
     * Gets the return value of this <code>SSLEngine</code> operation.
     *
     * @return  the return value
     */
    final public Status getStatus() {
        return status;
    }

    /**
     * Gets the handshake status of this <code>SSLEngine</code>
     * operation.
     *
     * @return  the handshake status
     */
    final public HandshakeStatus getHandshakeStatus() {
        return handshakeStatus;
    }

    /**
     * Returns the number of bytes consumed from the input buffer.
     *
     * @return  the number of bytes consumed.
     */
    final public int bytesConsumed() {
        return bytesConsumed;
    }

    /**
     * Returns the number of bytes written to the output buffer.
     *
     * @return  the number of bytes produced
     */
    final public int bytesProduced() {
        return bytesProduced;
    }

    /**
     * Returns a String representation of this object.
     */
    public String toString() {
        return ("Status = " + status +
            " HandshakeStatus = " + handshakeStatus +
            "\nbytesConsumed = " + bytesConsumed +
            " bytesProduced = " + bytesProduced);
    }
}
