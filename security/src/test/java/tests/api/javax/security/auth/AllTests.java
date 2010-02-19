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

package tests.api.javax.security.auth;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is autogenerated source file. Includes tests for package tests.api.javax.security.auth;
 */

public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("All tests for package tests.api.javax.security.auth;");
        // $JUnit-BEGIN$

        suite.addTestSuite(AuthPermissionTest.class);
        suite.addTestSuite(PrivateCredentialPermissionTest.class);
        suite.addTestSuite(SubjectTest.class);
        suite.addTestSuite(SubjectDomainCombinerTest.class);
        suite.addTestSuite(DestroyFailedExceptionTest.class);
        suite.addTestSuite(DestroyableTest.class);
        
        suite.addTestSuite(LoginExceptionTest.class);
        suite.addTestSuite(X500PrincipalTest.class);
        suite.addTestSuite(UnsupportedCallbackExceptionTest.class);
        suite.addTestSuite(PasswordCallbackTest.class);
        suite.addTestSuite(CallbackHandlerTest.class);

        // $JUnit-END$
        return suite;
    }
}
