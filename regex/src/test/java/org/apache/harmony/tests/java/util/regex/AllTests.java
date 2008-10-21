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

package org.apache.harmony.tests.java.util.regex;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for java.util.regex package
 * 
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Tests for java.util.regex");
        //$JUnit-BEGIN$

        suite.addTestSuite(Matcher2Test.class);
        suite.addTestSuite(MatcherTest.class);
        suite.addTestSuite(ModeTest.class);
        suite.addTestSuite(Pattern2Test.class);
        suite.addTestSuite(PatternErrorTest.class);
        suite.addTestSuite(PatternTest.class);
        suite.addTestSuite(ReplaceTest.class);
        suite.addTestSuite(SplitTest.class);

        //$JUnit-END$
        return suite;
    }
}
