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
* @author Vera Y. Petrashkova
* @version $Revision$
*/

package tests.security.cert;

import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass;

import junit.framework.TestCase;

import java.security.cert.CertificateException;


/**
 * Tests for <code>CertificateException</code> class constructors and methods.
 * 
 */
@TestTargetClass(CertificateException.class)
public class CertificateExceptionTest extends TestCase {

    public static void main(String[] args) {
    }

    /**
     * Constructor for CertificateExceptionTests.
     * 
     * @param arg0
     */
    public CertificateExceptionTest(String arg0) {
        super(arg0);
    }

    private static String[] msgs = {
            "",
            "Check new message",
            "Check new message Check new message Check new message Check new message Check new message" };

    private static Throwable tCause = new Throwable("Throwable for exception");

    /**
     * Test for <code>CertificateException()</code> constructor Assertion:
     * constructs CertificateException with no detail message
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {}
        )
    })
    public void testCertificateException01() {
        CertificateException tE = new CertificateException();
        assertNull("getMessage() must return null.", tE.getMessage());
        assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateException(String)</code> constructor
     * Assertion: constructs CertificateException with detail message msg.
     * Parameter <code>msg</code> is not null.
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {java.lang.String.class}
        )
    })
    public void testCertificateException02() {
        CertificateException tE;
        for (int i = 0; i < msgs.length; i++) {
            tE = new CertificateException(msgs[i]);
            assertEquals("getMessage() must return: ".concat(msgs[i]), tE
                    .getMessage(), msgs[i]);
            assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertificateException(String)</code> constructor
     * Assertion: constructs CertificateException when <code>msg</code> is
     * null
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies null as a parameter.",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {java.lang.String.class}
        )
    })
    public void testCertificateException03() {
        String msg = null;
        CertificateException tE = new CertificateException(msg);
        assertNull("getMessage() must return null.", tE.getMessage());
        assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateException(Throwable)</code> constructor
     * Assertion: constructs CertificateException when <code>cause</code> is
     * null
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies null as a parameter.",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {java.lang.Throwable.class}
        )
    })
    public void testCertificateException04() {
        Throwable cause = null;
        CertificateException tE = new CertificateException(cause);
        assertNull("getMessage() must return null.", tE.getMessage());
        assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateException(Throwable)</code> constructor
     * Assertion: constructs CertificateException when <code>cause</code> is
     * not null
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {java.lang.Throwable.class}
        )
    })
    public void testCertificateException05() {
        CertificateException tE = new CertificateException(tCause);
        if (tE.getMessage() != null) {
            String toS = tCause.toString();
            String getM = tE.getMessage();
            assertTrue("getMessage() should contain ".concat(toS), (getM
                    .indexOf(toS) != -1));
        }
        assertNotNull("getCause() must not return null", tE.getCause());
        assertEquals("getCause() must return ".concat(tCause.toString()), tE
                .getCause(), tCause);
    }

    /**
     * Test for <code>CertificateException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateException when
     * <code>cause</code> is null <code>msg</code> is null
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies null as parameters.",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {java.lang.String.class, java.lang.Throwable.class}
        )
    })
    public void testCertificateException06() {
        CertificateException tE = new CertificateException(null, null);
        assertNull("getMessage() must return null", tE.getMessage());
        assertNull("getCause() must return null", tE.getCause());
    }

    /**
     * Test for <code>CertificateException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateException when
     * <code>cause</code> is null <code>msg</code> is not null
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies null as Throwable parameter.",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {java.lang.String.class, java.lang.Throwable.class}
        )
    })
    public void testCertificateException07() {
        CertificateException tE;
        for (int i = 0; i < msgs.length; i++) {
            tE = new CertificateException(msgs[i], null);
            assertEquals("getMessage() must return: ".concat(msgs[i]), tE
                    .getMessage(), msgs[i]);
            assertNull("getCause() must return null", tE.getCause());
        }
    }

    /**
     * Test for <code>CertificateException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateException when
     * <code>cause</code> is not null <code>msg</code> is null
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies as String parameter.",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {java.lang.String.class, java.lang.Throwable.class}
        )
    })
    public void testCertificateException08() {
        CertificateException tE = new CertificateException(null, tCause);
        if (tE.getMessage() != null) {
            String toS = tCause.toString();
            String getM = tE.getMessage();
            assertTrue("getMessage() must should ".concat(toS), (getM
                    .indexOf(toS) != -1));
        }
        assertNotNull("getCause() must not return null", tE.getCause());
        assertEquals("getCause() must return ".concat(tCause.toString()), tE
                .getCause(), tCause);
    }

    /**
     * Test for <code>CertificateException(String, Throwable)</code>
     * constructor Assertion: constructs CertificateException when
     * <code>cause</code> is not null <code>msg</code> is not null
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "CertificateException",
          methodArgs = {java.lang.String.class, java.lang.Throwable.class}
        )
    })
    public void testCertificateException09() {
        CertificateException tE;
        for (int i = 0; i < msgs.length; i++) {
            tE = new CertificateException(msgs[i], tCause);
            String getM = tE.getMessage();
            String toS = tCause.toString();
            if (msgs[i].length() > 0) {
                assertTrue("getMessage() must contain ".concat(msgs[i]), getM
                        .indexOf(msgs[i]) != -1);
                if (!getM.equals(msgs[i])) {
                    assertTrue("getMessage() should contain ".concat(toS), getM
                            .indexOf(toS) != -1);
                }
            }
            assertNotNull("getCause() must not return null", tE.getCause());
            assertEquals("getCause() must return ".concat(tCause.toString()),
                    tE.getCause(), tCause);
        }
    }
}
