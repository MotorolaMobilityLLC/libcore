/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

import static java.lang.System.out;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This is a abstract class used to test various ciphers
 */
public abstract class TestCipher {

    private final String SUNJCE = "SunJCE";
    private final String ALGORITHM;
    private final String[] MODES;
    private final String[] PADDINGS;

    /* Used to test cipher with different key strengths
       Key size tested is increment of KEYCUTTER from MINIMUM_KEY_SIZE to
       maximum allowed keysize.
       DES/DESede/Blowfish work with currently selected key sizes.
    */
    private final int variousKeySize;
    private final int KEYCUTTER = 8;
    private final int MINIMUM_KEY_SIZE = 32;

    // Used to assert that Encryption/Decryption works with same buffer
    // TEXT_LEN is multiple of blocks in order to work against ciphers w/ NoPadding
    private final int TEXT_LEN = 800;
    private final int ENC_OFFSET = 6;
    private final int STORAGE_OFFSET = 3;
    private final int PAD_BYTES = 16;

    private final byte[] IV;
    private final byte[] INPUT_TEXT;

    TestCipher(String algo, String[] modes, String[] paddings,
            boolean keyStrength) throws NoSuchAlgorithmException {
        ALGORITHM = algo;
        MODES = modes;
        PADDINGS = paddings;
        this.variousKeySize
                = keyStrength ? Cipher.getMaxAllowedKeyLength(ALGORITHM) : 0;

        IV = generateBytes(8);
        INPUT_TEXT = generateBytes(TEXT_LEN + PAD_BYTES + ENC_OFFSET);
    }

    TestCipher(String algo, String[] modes, String[] paddings) {
        ALGORITHM = algo;
        MODES = modes;
        PADDINGS = paddings;
        variousKeySize = 0;

        IV = generateBytes(8);
        INPUT_TEXT = generateBytes(TEXT_LEN + PAD_BYTES + ENC_OFFSET);
    }

    private static byte[] generateBytes(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) (i & 0xff);
        }
        return bytes;
    }

    private boolean isKeyStrenthSupported() {
        return (variousKeySize != 0);
    }

    public void runAll() throws InvalidKeyException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            ShortBufferException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException,
            NoSuchProviderException {

        for (String mode : MODES) {
            for (String padding : PADDINGS) {
                if (!isKeyStrenthSupported()) {
                    runTest(mode, padding, 0);
                } else {
                    int keySize = variousKeySize;
                    while (keySize >= MINIMUM_KEY_SIZE) {
                        out.println("With Key Strength: " + keySize);
                        runTest(mode, padding, keySize);
                        keySize -= KEYCUTTER;
                    }
                }
            }
        }
    }

    private void runTest(String mo, String pad, int keySize)
            throws NoSuchPaddingException, BadPaddingException,
            ShortBufferException, IllegalBlockSizeException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            NoSuchAlgorithmException, NoSuchProviderException {

        String TRANSFORMATION = ALGORITHM + "/" + mo + "/" + pad;
        out.println("Testing: " + TRANSFORMATION);

        // Initialization
        Cipher ci = Cipher.getInstance(TRANSFORMATION, SUNJCE);
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM, SUNJCE);
        if (keySize != 0) {
            kg.init(keySize);
        }
        SecretKey key = kg.generateKey();
        SecretKeySpec skeySpec = new SecretKeySpec(key.getEncoded(), ALGORITHM);

        AlgorithmParameterSpec aps = new IvParameterSpec(IV);
        if (mo.equalsIgnoreCase("ECB")) {
            ci.init(Cipher.ENCRYPT_MODE, key);
        } else {
            ci.init(Cipher.ENCRYPT_MODE, key, aps);
        }

        // Encryption
        int PAD_LEN = 0;
        if (pad.equalsIgnoreCase("PKCS5Padding")) {
            // Need to consider pad bytes
            PAD_LEN = 8;
        }

        byte[] plainText = INPUT_TEXT.clone();

        // Generate cipher and save to separate buffer
        byte[] cipherText = ci.doFinal(INPUT_TEXT, ENC_OFFSET, TEXT_LEN);

        // Generate cipher and save to same buffer
        int offset = ci.update(
                INPUT_TEXT, ENC_OFFSET, TEXT_LEN, INPUT_TEXT, STORAGE_OFFSET);
        ci.doFinal(INPUT_TEXT, offset + STORAGE_OFFSET);

        if (!equalsBlock(
                INPUT_TEXT, STORAGE_OFFSET, cipherText, 0, cipherText.length)) {
            throw new RuntimeException(
                    "Different ciphers generated with same buffer");
        }

        // Decryption
        if (mo.equalsIgnoreCase("ECB")) {
            ci.init(Cipher.DECRYPT_MODE, skeySpec);
        } else {
            ci.init(Cipher.DECRYPT_MODE, skeySpec, aps);
        }

        // Recover text from cipher and save to separate buffer
        byte[] recoveredText = ci.doFinal(cipherText, 0, cipherText.length);

        if (!equalsBlock(
                plainText, ENC_OFFSET, recoveredText, 0,
                recoveredText.length)) {
            throw new RuntimeException(
                    "Recovered text not same as plain text");
        } else {
            out.println("Recovered and plain text are same");
        }

        // Recover text from cipher and save to same buffer
        ci.update(INPUT_TEXT, STORAGE_OFFSET, TEXT_LEN + PAD_LEN, INPUT_TEXT,
                ENC_OFFSET);
        ci.doFinal(INPUT_TEXT, ENC_OFFSET);

        if (!equalsBlock(
                plainText, ENC_OFFSET, recoveredText, 0,
                recoveredText.length)) {
            throw new RuntimeException(
                    "Recovered text not same as plain text with same buffer");
        } else {
            out.println("Recovered and plain text are same with same buffer");
        }

        out.println("Test Passed.");
    }

    private static boolean equalsBlock(byte[] b1, int off1, byte[] b2, int off2,
            int len) {
        for (int i = off1, j = off2, k = 0; k < len; i++, j++, k++) {
            if (b1[i] != b2[j]) {
                return false;
            }
        }
        return true;
    }
}
