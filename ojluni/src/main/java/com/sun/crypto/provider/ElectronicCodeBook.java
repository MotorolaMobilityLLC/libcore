/*
 * Copyright (c) 1997, 2007, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.crypto.provider;

import java.security.InvalidKeyException;

/**
 * This class represents ciphers in electronic codebook (ECB) mode.
 *
 * <p>This mode is implemented independently of a particular cipher.
 * Ciphers to which this mode should apply (e.g., DES) must be
 * <i>plugged-in</i> using the constructor.
 *
 * <p>NOTE: This class does not deal with buffering or padding.
 *
 * @author Gigi Ankeny
 */

final class ElectronicCodeBook extends FeedbackCipher {

    ElectronicCodeBook(SymmetricCipher embeddedCipher) {
        super(embeddedCipher);
    }

    /**
     * Gets the name of the feedback mechanism
     *
     * @return the name of the feedback mechanism
     */
    String getFeedback() {
        return "ECB";
    }

    /**
     * Resets the iv to its original value.
     * This is used when doFinal is called in the Cipher class, so that the
     * cipher can be reused (with its original iv).
     */
    void reset() {
        // empty
    }

    /**
     * Save the current content of this cipher.
     */
    void save() {}

    /**
     * Restores the content of this cipher to the previous saved one.
     */
    void restore() {}

    /**
     * Initializes the cipher in the specified mode with the given key
     * and iv.
     *
     * @param decrypting flag indicating encryption or decryption
     * @param algorithm the algorithm name
     * @param key the key
     * @param iv the iv
     *
     * @exception InvalidKeyException if the given key is inappropriate for
     * initializing this cipher
     */
    void init(boolean decrypting, String algorithm, byte[] key, byte[] iv)
            throws InvalidKeyException {
        if ((key == null) || (iv != null)) {
            throw new InvalidKeyException("Internal error");
        }
        embeddedCipher.init(decrypting, algorithm, key);
    }

    /**
     * Performs encryption operation.
     *
     * <p>The input plain text <code>plain</code>, starting at
     * <code>plainOffset</code> and ending at
     * <code>(plainOffset + len - 1)</code>, is encrypted.
     * The result is stored in <code>cipher</code>, starting at
     * <code>cipherOffset</code>.
     *
     * <p>It is the application's responsibility to make sure that
     * <code>plainLen</code> is a multiple of the embedded cipher's block size,
     * as any excess bytes are ignored.
     *
     * <p>It is also the application's responsibility to make sure that
     * <code>init</code> has been called before this method is called.
     * (This check is omitted here, to avoid double checking.)
     *
     * @param in the buffer with the input data to be encrypted
     * @param inOffset the offset in <code>plain</code>
     * @param len the length of the input data
     * @param out the buffer for the result
     * @param outOff the offset in <code>cipher</code>
     */
    void encrypt(byte[] in, int inOff, int len, byte[] out, int outOff) {
        while (len >= blockSize) {
            embeddedCipher.encryptBlock(in, inOff, out, outOff);
            len -= blockSize;
            inOff += blockSize;
            outOff += blockSize;
        }
    }

    /**
     * Performs decryption operation.
     *
     * <p>The input cipher text <code>cipher</code>, starting at
     * <code>cipherOffset</code> and ending at
     * <code>(cipherOffset + len - 1)</code>, is decrypted.
     * The result is stored in <code>plain</code>, starting at
     * <code>plainOffset</code>.
     *
     * <p>It is the application's responsibility to make sure that
     * <code>cipherLen</code> is a multiple of the embedded cipher's block
     * size, as any excess bytes are ignored.
     *
     * <p>It is also the application's responsibility to make sure that
     * <code>init</code> has been called before this method is called.
     * (This check is omitted here, to avoid double checking.)
     *
     * @param in the buffer with the input data to be decrypted
     * @param inOff the offset in <code>cipherOffset</code>
     * @param len the length of the input data
     * @param out the buffer for the result
     * @param outOff the offset in <code>plain</code>
     */
    void decrypt(byte[] in, int inOff, int len, byte[] out, int outOff) {
        while (len >= blockSize) {
            embeddedCipher.decryptBlock(in, inOff, out, outOff);
            len -= blockSize;
            inOff += blockSize;
            outOff += blockSize;
        }
    }

}
