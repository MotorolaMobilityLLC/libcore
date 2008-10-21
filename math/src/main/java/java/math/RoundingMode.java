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

package java.math;

import org.apache.harmony.math.internal.nls.Messages;

/**
 * An enum to specify the rounding behaviour for operations whose results
 * cannot be represented exactly.
 * 
 * @author Intel Middleware Product Division
 * @author Instituto Tecnologico de Cordoba
 */
public enum RoundingMode {

    /**
     * Rounding mode where positive values are rounded towards positive infinity
     * and negative values towards negative infinity.
     * 
     * Rule: x.round().abs() >= x.abs()
     */
    UP(BigDecimal.ROUND_UP),

    /**
     * Rounding mode where the values are rounded towards zero.
     * 
     * Rule: x.round().abs() <= x.abs()
     */
    DOWN(BigDecimal.ROUND_DOWN),

    /**
     * Rounding mode to round towards positive infinity. For positive values
     * this rounding mode behaves as UP, for negative values as 
     * DOWN.
     * 
     * Rule: x.round() >= x
     */
    CEILING(BigDecimal.ROUND_CEILING),

    /**
     * Rounding mode to round towards negative infinity. For positive values
     * this rounding mode behaves as DOWN, for negative values as UP.
     * 
     * Rule: x.round() <= x 
     */
    FLOOR(BigDecimal.ROUND_FLOOR),

    /**
     * Rounding mode where values are rounded towards the nearest neighbour.
     * Ties are broken by rounding up.
     */
    HALF_UP(BigDecimal.ROUND_HALF_UP),

    /**
     * Rounding mode where values are rounded towards the nearest neighbour.
     * Ties are broken by rounding down.
     */
    HALF_DOWN(BigDecimal.ROUND_HALF_DOWN),

    /**
     * Rounding mode where values are rounded towards the nearest neighbour.
     * Ties are broken by rounding to the even neighbour.
     */
    HALF_EVEN(BigDecimal.ROUND_HALF_EVEN),

    /**
     * Rounding mode where the rounding operations throws an ArithmeticException
     * for the case that rounding is necessary, i.e. for the case that the value
     * cannot be represented exactly.
     */
    UNNECESSARY(BigDecimal.ROUND_UNNECESSARY);

    /** The old constant of <code>BigDecimal</code>. */
    private final int bigDecimalRM;

    /** It sets the old constant. */
    RoundingMode(int rm) {
        bigDecimalRM = rm;
    }

    /**
     * Converts rounding mode constants from class BigDecimal into
     * RoundingMode values.
     * 
     * @param rM rounding mode constant as defined in class BigDecimal
     * @return corresponding rounding mode object
     */
    public static RoundingMode valueOf(int rM) {
        switch (rM) {
            case BigDecimal.ROUND_CEILING:
                return CEILING;
            case BigDecimal.ROUND_DOWN:
                return DOWN;
            case BigDecimal.ROUND_FLOOR:
                return FLOOR;
            case BigDecimal.ROUND_HALF_DOWN:
                return HALF_DOWN;
            case BigDecimal.ROUND_HALF_EVEN:
                return HALF_EVEN;
            case BigDecimal.ROUND_HALF_UP:
                return HALF_UP;
            case BigDecimal.ROUND_UNNECESSARY:
                return UNNECESSARY;
            case BigDecimal.ROUND_UP:
                return UP;
            default:
                // math.00=Invalid rounding mode
                throw new IllegalArgumentException(Messages.getString("math.00")); //$NON-NLS-1$
        }
    }
}
