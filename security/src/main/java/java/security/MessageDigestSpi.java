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
* @author Boris V. Kuznetsov
* @version $Revision$
*/

package java.security;

import java.nio.ByteBuffer;

import org.apache.harmony.security.internal.nls.Messages;

/**
 * {@code MessageDigestSpi} is the Service Provider Interface (SPI) definition
 * for {@link MessageDigest}. Examples of digest algorithms are MD5 and SHA. A
 * digest is a secure one way hash function for a stream of bytes. It acts like
 * a fingerprint for a stream of bytes.
 * 
 * @see MessageDigest
 * @since Android 1.0
 */
public abstract class MessageDigestSpi {
    
    /**
     * Returns the engine digest length in bytes. If the implementation does not
     * implement this function {@code 0} is returned.
     * 
     * @return the digest length in bytes, or {@code 0}.
     * @since Android 1.0
     */
    protected int engineGetDigestLength() {
        return 0;
    }
    
    /**
     * Updates this {@code MessageDigestSpi} using the given {@code byte}.
     * 
     * @param input
     *            the {@code byte} to update this {@code MessageDigestSpi} with.
     * @see #engineReset()
     * @since Android 1.0
     */
    protected abstract void engineUpdate(byte input);

    /**
     * Updates this {@code MessageDigestSpi} using the given {@code byte[]}.
     * 
     * @param input
     *            the {@code byte} array.
     * @param offset
     *            the index of the first byte in {@code input} to update from.
     * @param len
     *            the number of bytes in {@code input} to update from.
     * @throws IllegalArgumentException
     *             if {@code offset} or {@code len} are not valid in respect to
     *             {@code input}.
     * @since Android 1.0
     */
    protected abstract void engineUpdate(byte[] input, int offset, int len);
    
    /**
     * Updates this {@code MessageDigestSpi} using the given {@code input}.
     * 
     * @param input
     *            the {@code ByteBuffer}.
     * @since Android 1.0
     */
    protected void engineUpdate(ByteBuffer input) {
        if (!input.hasRemaining()) {
            return;
        }
        byte[] tmp;
        if (input.hasArray()) {
            tmp = input.array();
            int offset = input.arrayOffset();
            int position = input.position();
            int limit = input.limit();
            engineUpdate(tmp, offset+position, limit - position);
            input.position(limit);
        } else {
            tmp = new byte[input.limit() - input.position()];
            input.get(tmp);
            engineUpdate(tmp, 0, tmp.length);
        }    
    }
    
    /**
     * Computes and returns the final hash value for this
     * {@link MessageDigestSpi}. After the digest is computed the receiver is
     * reset.
     * 
     * @return the computed one way hash value.
     * @see #engineReset()
     * @since Android 1.0
     */
    protected abstract byte[] engineDigest();
    
    /**
     * Computes and stores the final hash value for this
     * {@link MessageDigestSpi}. After the digest is computed the receiver is
     * reset.
     * 
     * @param buf
     *            the buffer to store the result in.
     * @param offset
     *            the index of the first byte in {@code buf} to store in.
     * @param len
     *            the number of bytes allocated for the digest.
     * @return the number of bytes written to {@code buf}.
     * @throws DigestException
     *             if an error occures.
     * @throws IllegalArgumentException
     *             if {@code offset} or {@code len} are not valid in respect to
     *             {@code buf}.
     * @see #engineReset()
     * @since Android 1.0
     */
    protected int engineDigest(byte[] buf, int offset, int len)
                    throws DigestException {
        if (len < engineGetDigestLength()) {
            engineReset();
            throw new DigestException(Messages.getString("security.1B"));  //$NON-NLS-1$
        }
        if (offset < 0) {
            engineReset();
            throw new DigestException(Messages.getString("security.1C")); //$NON-NLS-1$
        }
        if (offset + len > buf.length) {
            engineReset();
            throw new DigestException(Messages.getString("security.1D")); //$NON-NLS-1$
        }
        byte tmp[] = engineDigest();
        if (len < tmp.length) {
            throw new DigestException(Messages.getString("security.1B")); //$NON-NLS-1$
        }
        System.arraycopy(tmp, 0, buf, offset, tmp.length);
        return tmp.length;            
    }
    
    /**
     * Puts this {@code MessageDigestSpi} back in an initial state, such that it
     * is ready to compute a one way hash value.
     * 
     * @since Android 1.0
     */
    protected abstract void engineReset();
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
