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

import java.math.BigInteger;

import org.apache.harmony.security.internal.nls.Messages;

/**
 * The parameter specification used with Elliptic Curve Cryptography (ECC). 
 * 
 * @since Android 1.0
 */
public class ECParameterSpec implements AlgorithmParameterSpec {
    // Elliptic curve for which this is parameter
    private final EllipticCurve curve;
    // Distinguished point on the elliptic curve called generator or base point
    private final ECPoint generator;
    // Order of the generator
    private final BigInteger order;
    // Cofactor
    private final int cofactor;

    /**
     * Creates a new {@code ECParameterSpec} with the specified elliptic curve,
     * the base point, the order of the generator (or base point) and the
     * co-factor.
     * 
     * @param curve
     *            the elliptic curve.
     * @param generator
     *            the generator (or base point).
     * @param order
     *            the order of the generator.
     * @param cofactor
     *            the co-factor.
     * @throws IllegalArgumentException
     *             if {@code order <= zero} or {@code cofactor <= zero}.
     * @since Android 1.0
     */
    public ECParameterSpec(EllipticCurve curve, ECPoint generator,
            BigInteger order, int cofactor) {
        this.curve = curve;
        this.generator = generator;
        this.order = order;
        this.cofactor = cofactor;
        // throw NullPointerException if curve, generator or order is null
        if (this.curve == null) {
            throw new NullPointerException(Messages.getString("security.83", "curve")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (this.generator == null) {
            throw new NullPointerException(Messages.getString("security.83", "generator")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (this.order == null) {
            throw new NullPointerException(Messages.getString("security.83", "order")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // throw IllegalArgumentException if order or cofactor is not positive
        if (!(this.order.compareTo(BigInteger.ZERO) > 0)) {
            throw new
            IllegalArgumentException(Messages.getString("security.86", "order")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (!(this.cofactor > 0)) {
            throw new
            IllegalArgumentException(Messages.getString("security.86", "cofactor")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Returns the {@code cofactor}.
     * 
     * @return the {@code cofactor}.
     * @since Android 1.0
     */
    public int getCofactor() {
        return cofactor;
    }

    /**
     * Returns the elliptic curve.
     * 
     * @return the elliptic curve.
     * @since Android 1.0
     */
    public EllipticCurve getCurve() {
        return curve;
    }

    /**
     * Returns the generator (or base point).
     * 
     * @return the generator (or base point).
     * @since Android 1.0
     */
    public ECPoint getGenerator() {
        return generator;
    }

    /**
     * Returns the order of the generator.
     * 
     * @return the order of the generator.
     * @since Android 1.0
     */
    public BigInteger getOrder() {
        return order;
    }
}
