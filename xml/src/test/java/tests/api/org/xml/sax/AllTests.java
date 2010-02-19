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

package tests.api.org.xml.sax;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("Tests for org.xml.sax package");
        // $JUnit-BEGIN$

        suite.addTestSuite(HandlerBaseTest.class);
        suite.addTestSuite(InputSourceTest.class);
        suite.addTestSuite(SAXExceptionTest.class);
        suite.addTestSuite(SAXNotRecognizedExceptionTest.class);
        suite.addTestSuite(SAXNotSupportedExceptionTest.class);
        suite.addTestSuite(SAXParseExceptionTest.class);

        suite.addTest(tests.api.org.xml.sax.ext.AllTests.suite());
        suite.addTest(tests.api.org.xml.sax.helpers.AllTests.suite());
        
        // $JUnit-END$
        return suite;
    }
}
