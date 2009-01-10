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

package org.apache.harmony.crypto.tests.javax.crypto.spec;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is autogenerated source file. Includes tests for package org.apache.harmony.crypto.tests.javax.crypto.spec;
 */

public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }

    public static Test suite() {
        TestSuite suite = tests.TestSuiteFactory.createTestSuite("All tests for package org.apache.harmony.crypto.tests.javax.crypto.spec;");
        // $JUnit-BEGIN$

        suite.addTestSuite(DESKeySpecTest.class);
        suite.addTestSuite(DESedeKeySpecTest.class);
        suite.addTestSuite(DHGenParameterSpecTest.class);
        suite.addTestSuite(DHParameterSpecTest.class);
        suite.addTestSuite(DHPrivateKeySpecTest.class);
        suite.addTestSuite(DHPublicKeySpecTest.class);
        suite.addTestSuite(IvParameterSpecTest.class);
        suite.addTestSuite(OAEPParameterSpecTest.class);
        suite.addTestSuite(PBEKeySpecTest.class);
        suite.addTestSuite(PBEParameterSpecTest.class);
        suite.addTestSuite(PSourceTest.class);
        suite.addTestSuite(RC2ParameterSpecTest.class);
        suite.addTestSuite(RC5ParameterSpecTest.class);
        suite.addTestSuite(SecretKeySpecTest.class);

        // $JUnit-END$
        return suite;
    }
}
