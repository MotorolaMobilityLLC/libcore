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
 * @author Elena Semukhina
 * @version $Revision$
 */

package libcore.java.math;

import java.math.BigInteger;
import junit.framework.TestCase;

public class OldBigIntegerDivideTest extends TestCase {

    /**
     * divideAndRemainder of division by zero
     */
    public void testCase21byZero() {
        byte aBytes[] = {1, 2, 3, 4, 5, 6, 7};
        byte bBytes[] = {0};
        int aSign = 1;
        int bSign = 0;
        BigInteger aNumber = new BigInteger(aSign, aBytes);
        BigInteger bNumber = new BigInteger(bSign, bBytes);
        try {
            aNumber.divideAndRemainder(bNumber);
            fail("ArithmeticException has not been caught");
        } catch (ArithmeticException e) {
        }
    }
}
