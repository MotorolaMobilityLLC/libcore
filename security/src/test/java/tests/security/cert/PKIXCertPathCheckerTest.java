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
* @author Vladimir N. Molotkov
* @version $Revision$
*/

package tests.security.cert;

import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass;

import junit.framework.TestCase;

import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.PKIXCertPathChecker;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.harmony.security.tests.support.cert.MyCertificate;
import org.apache.harmony.security.tests.support.cert.TestUtils;

/**
 * Tests for <code>PKIXCertPathChecker</code>
 * 
 */
@TestTargetClass(PKIXCertPathChecker.class)
public class PKIXCertPathCheckerTest extends TestCase {

    /**
     * Constructor for PKIXCertPathCheckerTest.
     * @param name
     */
    public PKIXCertPathCheckerTest(String name) {
        super(name);
    }

    //
    // Tests
    //
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "PKIXCertPathChecker",
          methodArgs = {}
        )
    })
    public final void testConstructor() {
        try {
            new MyPKIXCertPathChecker();
        } catch(Exception e) {
            fail("Unexpected exception " + e.getMessage());
        }
    }
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "clone",
          methodArgs = {}
        )
    })
    public final void testClone() {
        PKIXCertPathChecker pc1 = TestUtils.getTestCertPathChecker();
        PKIXCertPathChecker pc2 = (PKIXCertPathChecker) pc1.clone();
        assertNotSame("notSame", pc1, pc2);
    }

    //
    // the following tests just call methods
    // that are abstract in <code>PKIXCertPathChecker</code>
    // (So they just like signature tests)
    //
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "isForwardCheckingSupported",
          methodArgs = {}
        )
    })
    public final void testIsForwardCheckingSupported() {
        PKIXCertPathChecker pc = TestUtils.getTestCertPathChecker();
        pc.isForwardCheckingSupported();
    }
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "init",
          methodArgs = {boolean.class}
        )
    })
    public final void testInit()
        throws CertPathValidatorException {
        PKIXCertPathChecker pc = TestUtils.getTestCertPathChecker();
        pc.init(true);
    }
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getSupportedExtensions",
          methodArgs = {}
        )
    })
    public final void testGetSupportedExtensions() {
        PKIXCertPathChecker pc = TestUtils.getTestCertPathChecker();
        pc.getSupportedExtensions();
    }
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "check",
          methodArgs = {java.security.cert.Certificate.class, java.util.Collection.class}
        )
    })
    public final void testCheck() throws CertPathValidatorException {
        PKIXCertPathChecker pc = TestUtils.getTestCertPathChecker();
        pc.check(new MyCertificate("", null), new HashSet<String>());
    }

    class MyPKIXCertPathChecker extends PKIXCertPathChecker {

        public MyPKIXCertPathChecker() {
            super();
        }

        @Override
        public void check(Certificate cert,
                Collection<String> unresolvedCritExts)
        throws CertPathValidatorException {
        }

        @Override
        public Set<String> getSupportedExtensions() {
            return null;
        }

        @Override
        public void init(boolean forward) throws CertPathValidatorException {
        }

        @Override
        public boolean isForwardCheckingSupported() {
            return false;
        }

    }

}
