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

import org.apache.harmony.security.internal.nls.Messages;

/**
 * The parameters specifying an Elliptic Curve (EC) public key. 
 * 
 * @since Android 1.0
 */
public class ECPublicKeySpec implements KeySpec {
    // The public point
    private final ECPoint w;
    // The associated elliptic curve domain parameters
    private final ECParameterSpec params;

    /**
     * Creates a new {@code ECPublicKey} with the specified public elliptic
     * curve point and parameter specification.
     * 
     * @param w
     *            the public elliptic curve point {@code W}.
     * @param params
     *            the domain parameter specification.
     * @throws IllegalArgumentException
     *             if the specified point {@code W} is at infinity.
     * @since Android 1.0
     */
    public ECPublicKeySpec(ECPoint w, ECParameterSpec params) {
        this.w = w;
        this.params = params;
        // throw NullPointerException if w or params is null
        if (this.w == null) {
            throw new NullPointerException(Messages.getString("security.83", "w")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (this.params == null) {
            throw new NullPointerException(Messages.getString("security.83", "params")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // throw IllegalArgumentException if w is point at infinity
        if (this.w.equals(ECPoint.POINT_INFINITY)) {
            throw new IllegalArgumentException(
                Messages.getString("security.84")); //$NON-NLS-1$
        }
    }

    /**
     * Returns the domain parameter specification.
     * 
     * @return the domain parameter specification.
     * @since Android 1.0
     */
    public ECParameterSpec getParams() {
        return params;
    }

    /**
     * Returns the public elliptic curve point {@code W}.
     * 
     * @return the public elliptic curve point {@code W}.
     * @since Android 1.0
     */
    public ECPoint getW() {
        return w;
    }
}
