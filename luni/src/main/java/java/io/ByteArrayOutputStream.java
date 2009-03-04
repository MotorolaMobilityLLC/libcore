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

package java.io;

import org.apache.harmony.luni.util.Msg;

/**
 * A specialized {@link OutputStream} for class for writing content to an
 * (internal) byte array. As bytes are written to this stream, the byte array
 * may be expanded to hold more bytes. When the writing is considered to be
 * finished, a copy of the byte array can be requested from the class.
 * 
 * @see ByteArrayInputStream
 * 
 * @since Android 1.0
 */
public class ByteArrayOutputStream extends OutputStream {
    /**
     * The byte array containing the bytes written.
     * 
     * @since Android 1.0
     */
    protected byte[] buf;

    /**
     * The number of bytes written.
     * 
     * @since Android 1.0
     */
    protected int count;

    /**
     * Constructs a new ByteArrayOutputStream with a default size of 32 bytes.
     * If more than 32 bytes are written to this instance, the underlying byte
     * array will expand.
     * 
     * @since Android 1.0
     */
    public ByteArrayOutputStream() {
        super();
        buf = new byte[32];
    }

    /**
     * Constructs a new {@code ByteArrayOutputStream} with a default size of
     * {@code size} bytes. If more than {@code size} bytes are written to this
     * instance, the underlying byte array will expand.
     * 
     * @param size
     *            initial size for the underlying byte array, must be
     *            non-negative.
     * @throws IllegalArgumentException
     *             if {@code size} < 0.
     * @since Android 1.0
     */
    public ByteArrayOutputStream(int size) {
        super();
        if (size >= 0) {
            buf = new byte[size];
        } else {
            throw new IllegalArgumentException(Msg.getString("K005e")); //$NON-NLS-1$
        }
    }

    /**
     * Closes this stream. This releases system resources used for this stream.
     * 
     * @throws IOException
     *             if an error occurs while attempting to close this stream.
     * @since Android 1.0
     */
    @Override
    public void close() throws IOException {
        /**
         * Although the spec claims "A closed stream cannot perform output
         * operations and cannot be reopened.", this implementation must do
         * nothing.
         */
        super.close();
    }

    private void expand(int i) {
        /* Can the buffer handle @i more bytes, if not expand it */
        if (count + i <= buf.length) {
            return;
        }

        byte[] newbuf = new byte[(count + i) * 2];
        System.arraycopy(buf, 0, newbuf, 0, count);
        buf = newbuf;
    }

    /**
     * Resets this stream to the beginning of the underlying byte array. All
     * subsequent writes will overwrite any bytes previously stored in this
     * stream.
     * 
     * @since Android 1.0
     */
    public synchronized void reset() {
        count = 0;
    }

    /**
     * Returns the total number of bytes written to this stream so far.
     * 
     * @return the number of bytes written to this stream.
     * @since Android 1.0
     */
    public int size() {
        return count;
    }

    /**
     * Returns the contents of this ByteArrayOutputStream as a byte array. Any
     * changes made to the receiver after returning will not be reflected in the
     * byte array returned to the caller.
     * 
     * @return this stream's current contents as a byte array.
     * @since Android 1.0
     */
    public synchronized byte[] toByteArray() {
        byte[] newArray = new byte[count];
        System.arraycopy(buf, 0, newArray, 0, count);
        return newArray;
    }

    /**
     * Returns the contents of this ByteArrayOutputStream as a string. Any
     * changes made to the receiver after returning will not be reflected in the
     * string returned to the caller.
     * 
     * @return this stream's current contents as a string.
     * @since Android 1.0
     */

    @Override
    public String toString() {
        return new String(buf, 0, count);
    }

    /**
     * Returns the contents of this ByteArrayOutputStream as a string. Each byte
     * {@code b} in this stream is converted to a character {@code c} using the
     * following function:
     * {@code c == (char)(((hibyte & 0xff) << 8) | (b & 0xff))}. This method is
     * deprecated and either {@link #toString()} or {@link #toString(String)}
     * should be used.
     * 
     * @param hibyte
     *            the high byte of each resulting Unicode character.
     * @return this stream's current contents as a string with the high byte set
     *         to {@code hibyte}.
     * @deprecated Use {@link #toString()}.
     * @since Android 1.0
     */
    @Deprecated
    public String toString(int hibyte) {
        char[] newBuf = new char[size()];
        for (int i = 0; i < newBuf.length; i++) {
            newBuf[i] = (char) (((hibyte & 0xff) << 8) | (buf[i] & 0xff));
        }
        return new String(newBuf);
    }

    /**
     * Returns the contents of this ByteArrayOutputStream as a string converted
     * according to the encoding declared in {@code enc}.
     * 
     * @param enc
     *            a string representing the encoding to use when translating
     *            this stream to a string.
     * @return this stream's current contents as an encoded string.
     * @throws UnsupportedEncodingException
     *             if the provided encoding is not supported.
     * @since Android 1.0
     */
    public String toString(String enc) throws UnsupportedEncodingException {
        return new String(buf, 0, count, enc);
    }

    /**
     * Writes {@code count} bytes from the byte array {@code buffer} starting at
     * offset {@code index} to this stream.
     * 
     * @param buffer
     *            the buffer to be written.
     * @param offset
     *            the initial position in {@code buffer} to retrieve bytes.
     * @param len
     *            the number of bytes of {@code buffer} to write.
     * @throws IndexOutOfBoundsException
     *             if {@code offset < 0} or {@code len < 0}, or if
     *             {@code offset + len} is greater than the length of
     *             {@code buffer}.
     * @since Android 1.0
     */
    @Override
    public synchronized void write(byte[] buffer, int offset, int len) {
        // avoid int overflow
        // BEGIN android-changed
        // Exception priorities (in case of multiple errors) differ from
        // RI, but are spec-compliant.
        // removed redundant check, made implicit null check explicit,
        // used (offset | len) < 0 instead of (offset < 0) || (len < 0)
        // to safe one operation
        if (buffer == null) {
            throw new NullPointerException(Msg.getString("K0047")); //$NON-NLS-1$
        }
        if ((offset | len) < 0 || len > buffer.length - offset) {
            throw new IndexOutOfBoundsException(Msg.getString("K002f")); //$NON-NLS-1$
        }
        // END android-changed
        if (len == 0) {
            return;
        }

        /* Expand if necessary */
        expand(len);
        System.arraycopy(buffer, offset, buf, this.count, len);
        this.count += len;
    }

    /**
     * Writes the specified byte {@code oneByte} to the OutputStream. Only the
     * low order byte of {@code oneByte} is written.
     * 
     * @param oneByte
     *            the byte to be written.
     * @since Android 1.0
     */
    @Override
    public synchronized void write(int oneByte) {
        if (count == buf.length) {
            expand(1);
        }
        buf[count++] = (byte)oneByte;
    }

    /**
     * Takes the contents of this stream and writes it to the output stream
     * {@code out}.
     * 
     * @param out
     *            an OutputStream on which to write the contents of this stream.
     * @throws IOException
     *             if an error occurs while writing to {@code out}.
     * @since Android 1.0
     */
    public synchronized void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }
}
