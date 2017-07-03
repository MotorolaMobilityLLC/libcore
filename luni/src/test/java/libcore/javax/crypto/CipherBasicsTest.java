/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.javax.crypto;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import libcore.util.HexEncoding;

/**
 * Test for basic compliance for ciphers.  This test uses reference vectors produced by
 * standards bodies and confirms that all implementations produce the correct answer
 * for the given inputs.
 */
public final class CipherBasicsTest extends TestCase {

    private static final Map<String, String> CIPHER_TO_TEST_DATA = new HashMap<>();
    static {
        CIPHER_TO_TEST_DATA.put("AES/ECB/NoPadding", "/crypto/aes-ecb.csv");
        CIPHER_TO_TEST_DATA.put("AES/CBC/NoPadding", "/crypto/aes-cbc.csv");
        CIPHER_TO_TEST_DATA.put("AES/CFB8/NoPadding", "/crypto/aes-cfb8.csv");
        CIPHER_TO_TEST_DATA.put("AES/CFB128/NoPadding", "/crypto/aes-cfb128.csv");
        CIPHER_TO_TEST_DATA.put("AES/OFB/NoPadding", "/crypto/aes-ofb.csv");
        CIPHER_TO_TEST_DATA.put("DESEDE/ECB/NoPadding", "/crypto/desede-ecb.csv");
        CIPHER_TO_TEST_DATA.put("DESEDE/CBC/NoPadding", "/crypto/desede-cbc.csv");
        CIPHER_TO_TEST_DATA.put("DESEDE/CFB8/NoPadding", "/crypto/desede-cfb8.csv");
        CIPHER_TO_TEST_DATA.put("DESEDE/CFB64/NoPadding", "/crypto/desede-cfb64.csv");
        CIPHER_TO_TEST_DATA.put("DESEDE/OFB/NoPadding", "/crypto/desede-ofb.csv");
    }

    private static final int KEY_INDEX = 0;
    private static final int IV_INDEX = 1;
    private static final int PLAINTEXT_INDEX = 2;
    private static final int CIPHERTEXT_INDEX = 3;

    public void testEncryption() throws Exception {
        for (Provider p : Security.getProviders()) {
            for (Map.Entry<String, String> entry : CIPHER_TO_TEST_DATA.entrySet()) {
                String transformation = entry.getKey();

                Cipher cipher;
                try {
                    cipher = Cipher.getInstance(transformation, p);
                } catch (NoSuchAlgorithmException e) {
                    // This provider doesn't provide this algorithm, ignore it
                    continue;
                }

                List<String[]> data = readCsvResource(entry.getValue());
                for (String[] line : data) {
                    Key key = new SecretKeySpec(toBytes(line[KEY_INDEX]),
                            getBaseAlgorithm(transformation));
                    byte[] iv = toBytes(line[IV_INDEX]);
                    byte[] plaintext = toBytes(line[PLAINTEXT_INDEX]);
                    byte[] ciphertext = toBytes(line[CIPHERTEXT_INDEX]);

                    // Initialize the IV, if there is one
                    AlgorithmParameters params;
                    if (iv.length > 0) {
                        params = AlgorithmParameters.getInstance(getBaseAlgorithm(transformation));
                        params.init(iv, "RAW");
                    } else {
                        params = null;
                    }

                    try {
                        cipher.init(Cipher.ENCRYPT_MODE, key, params);
                        assertTrue("Provider " + p.getName()
                                        + ", algorithm " + transformation
                                        + " failed on encryption, data is " + Arrays.toString(line),
                                Arrays.equals(ciphertext, cipher.doFinal(plaintext)));

                        cipher.init(Cipher.DECRYPT_MODE, key, params);
                        assertTrue("Provider " + p.getName()
                                        + ", algorithm " + transformation
                                        + " failed on decryption, data is " + Arrays.toString(line),
                                Arrays.equals(plaintext, cipher.doFinal(ciphertext)));
                    } catch (InvalidKeyException e) {
                        // Some providers may not support raw SecretKeySpec keys, that's allowed
                    }
                }
            }
        }
    }

    private static List<String[]> readCsvResource(String resourceName) throws IOException {
        InputStream stream = CipherBasicsTest.class.getResourceAsStream(resourceName);
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                lines.add(line.split(","));
            }
        }
        return lines;
    }

    /**
     * Returns the underlying cipher name given a cipher transformation.  For example,
     * passing {@code AES/CBC/NoPadding} returns {@code AES}.
     */
    private static String getBaseAlgorithm(String transformation) {
        if (transformation.contains("/")) {
            return transformation.substring(0, transformation.indexOf('/'));
        }
        return transformation;
    }

    private static byte[] toBytes(String hex) {
        return HexEncoding.decode(hex, /* allowSingleChar= */ true);
    }
}
