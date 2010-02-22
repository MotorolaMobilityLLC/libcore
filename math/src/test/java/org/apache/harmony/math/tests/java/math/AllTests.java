/*
 * Copyright (C) 2007 The Android Open Source Project
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

package org.apache.harmony.math.tests.java.math;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is autogenerated source file. Includes tests for package org.apache.harmony.tests.java.math;
 */

public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("All tests for package org.apache.harmony.tests.java.math;");
        // $JUnit-BEGIN$

        suite.addTestSuite(BigDecimalArithmeticTest.class);
        suite.addTestSuite(BigDecimalCompareTest.class);
        suite.addTestSuite(BigDecimalConstructorsTest.class);
        suite.addTestSuite(BigDecimalConvertTest.class);
        suite.addTestSuite(BigDecimalScaleOperationsTest.class);
        suite.addTestSuite(BigIntegerAddTest.class);
        suite.addTestSuite(BigIntegerAndTest.class);
        suite.addTestSuite(BigIntegerCompareTest.class);
        suite.addTestSuite(BigIntegerConstructorsTest.class);
        suite.addTestSuite(BigIntegerConvertTest.class);
        suite.addTestSuite(BigIntegerDivideTest.class);
        suite.addTestSuite(BigIntegerHashCodeTest.class);
        suite.addTestSuite(BigIntegerModPowTest.class);
        suite.addTestSuite(BigIntegerMultiplyTest.class);
        suite.addTestSuite(BigIntegerNotTest.class);
        suite.addTestSuite(BigIntegerOperateBitsTest.class);
        suite.addTestSuite(BigIntegerOrTest.class);
        suite.addTestSuite(BigIntegerSubtractTest.class);
        suite.addTestSuite(BigIntegerToStringTest.class);
        suite.addTestSuite(BigIntegerXorTest.class);

        // $JUnit-END$
        return suite;
    }
}
