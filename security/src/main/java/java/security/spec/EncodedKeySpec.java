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

package java.security.spec;

/**
 * The abstract key specification for a public or a private key in encoded
 * format.
 */
public abstract class EncodedKeySpec implements KeySpec {
    // Encoded key
    private final byte[] encodedKey;

    /**
     * Creates a new {@code EncodedKeySpec} with the specified encoded key bytes.
     * 
     * @param encodedKey
     *            the encoded key bytes.
     */
    public EncodedKeySpec(byte[] encodedKey) {
        // Defensively copies parameter
        // to prevent subsequent modification
        this.encodedKey = new byte[encodedKey.length];
        System.arraycopy(encodedKey, 0,
                this.encodedKey, 0, this.encodedKey.length);
    }

    /**
     * Returns the encoded key bytes.
     * 
     * @return the encoded key bytes.
     */
    public byte[] getEncoded() {
        // Defensively copies private array
        // to prevent subsequent modification
        byte[] ret = new byte[encodedKey.length];
        System.arraycopy(encodedKey, 0, ret, 0, ret.length);
        return ret;
    }

    /**
     * Returns the name of the encoding format of this encoded key
     * specification.
     * 
     * @return the name of the encoding format of this encoded key
     *         specification.
     */
    public abstract String getFormat();
}
