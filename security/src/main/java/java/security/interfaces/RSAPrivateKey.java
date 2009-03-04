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

package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

/**
 * The interface for an PKCS#1 RSA private key.
 * 
 * @since Android 1.0
 */
public interface RSAPrivateKey extends PrivateKey, RSAKey {

    /**
     * The serial version identifier.
     * 
     * @since Android 1.0
     */
    public static final long serialVersionUID = 5187144804936595022L;

    /**
     * Returns the private exponent {@code d}.
     * 
     * @return the private exponent {@code d}.
     * @since Android 1.0
     */
    public BigInteger getPrivateExponent();
}