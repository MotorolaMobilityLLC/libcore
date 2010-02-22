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

package tests.api.java.lang;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TODO Type description
 */
public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for java.lang");

        // $JUnit-BEGIN$
        
        suite.addTestSuite(Process2Test.class);
        suite.addTestSuite(ProcessTest.class);
        suite.addTestSuite(ProcessManagerTest.class);
        suite.addTestSuite(BooleanTest.class);
        suite.addTestSuite(StringTest.class);

        // $JUnit-END$

        return suite;
    }
}
