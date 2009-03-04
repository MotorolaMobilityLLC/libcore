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

/**
* @author Vladimir N. Molotkov
* @version $Revision$
*/

package java.security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@code DigestInputStream} is a {@code FilterInputStream} which maintains an
 * associated message digest.
 * 
 * @since Android 1.0
 */
public class DigestInputStream extends FilterInputStream {

    /**
     * The message digest for this stream.
     * 
     * @since Android 1.0
     */
    protected MessageDigest digest;

    // Indicates whether digest functionality is on or off
    private boolean isOn = true;

    /**
     * Constructs a new instance of this {@code DigestInputStream}, using the
     * given {@code stream} and the {@code digest}.
     * 
     * @param stream
     *            the input stream.
     * @param digest
     *            the message digest.
     * @since Android 1.0
     */
    public DigestInputStream(InputStream stream, MessageDigest digest) {
        super(stream);
        this.digest = digest;
    }

    /**
     * Returns the message digest for this stream.
     * 
     * @return the message digest for this stream.
     * @since Android 1.0
     */
    public MessageDigest getMessageDigest() {
        return digest;
    }

    /**
     * Sets the message digest which this stream will use.
     * 
     * @param digest
     *            the message digest which this stream will use.
     * @since Android 1.0
     */
    public void setMessageDigest(MessageDigest digest) {
        this.digest = digest;
    }

    /**
     * Reads the next byte and returns it as an {@code int}. Updates the digest
     * for the byte if this function is {@link #on(boolean)}.
     * <p>
     * This operation is blocking.
     * </p>
     * 
     * @return the byte which was read or -1 at end of stream.
     * @throws IOException
     *             if reading the source stream causes an {@code IOException}.
     * @since Android 1.0
     */
    public int read() throws IOException {
        // read the next byte
        int byteRead = in.read();
        // update digest only if
        // - digest functionality is on
        // - eos has not been reached
        if (isOn && (byteRead != -1)) {
            digest.update((byte)byteRead);
        }
        // return byte read
        return byteRead;
    }

    /**
     * Reads {@code len} bytes into the specified {@code byte[]}, starting from
     * the specified offset. Updates the digest if this function is
     * {@link #on(boolean)}.
     * <p>
     * This operation is blocking.
     * </p>
     * 
     * @param b
     *            the byte array in which to store the bytes
     * @param off
     *            the initial position in {@code b} to store the bytes read from
     *            this stream
     * @param len
     *            the maximum number of bytes to store in {@code b}
     * @return the number of bytes actually read or -1 if the end of the
     *         filtered stream has been reached while reading
     * @throws IOException
     *             if reading the source stream causes an {@code IOException}
     * @since Android 1.0
     */
    public int read(byte[] b, int off, int len) throws IOException {
        // read next up to len bytes
        int bytesRead = in.read(b, off, len);
        // update digest only if
        // - digest functionality is on
        // - eos has not been reached
        if (isOn && (bytesRead != -1)) {
            digest.update(b, off, bytesRead);
        }
        // return number of bytes read
        return bytesRead;
    }

    /**
     * Enables or disables the digest function (default is on).
     * 
     * @param on
     *            {@code true} if the digest should be computed, {@code false}
     *            otherwise.
     * @since Android 1.0
     */
    public void on(boolean on) {
        isOn = on;
    }

    /**
     * Returns a string containing a concise, human-readable description of this
     * {@code DigestInputStream} including the digest.
     * 
     * @return a printable representation for this {@code DigestInputStream}.
     * @since Android 1.0
     */
    public String toString() {
        return super.toString() + ", " + digest.toString() + //$NON-NLS-1$
            (isOn ? ", is on" : ", is off"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
