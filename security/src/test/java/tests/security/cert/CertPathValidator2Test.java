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

import tests.security.cert.myCertPathBuilder.MyProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;

import org.apache.harmony.security.tests.support.SpiEngUtils;
/**
 * Tests for CertPathValidator class constructors and methods
 * 
 */
@TestTargetClass(CertPathValidator.class)
public class CertPathValidator2Test extends TestCase {
    private static final String defaultAlg = "CertPB";
    
    public static final String CertPathValidatorProviderClass = "tests.security.cert.support.cert.MyCertPathValidatorSpi";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static final String[] validValues;

    static {
        validValues = new String[4];
        validValues[0] = defaultAlg;
        validValues[1] = defaultAlg.toLowerCase();
        validValues[2] = "CeRtPb";
        validValues[3] = "cERTpb";
    }

    Provider mProv;

    protected void setUp() throws Exception {
        super.setUp();
        mProv = (new SpiEngUtils()).new MyProvider("MyCertPathValidatorProvider",
                "Provider for testing", CertPathValidator1Test.srvCertPathValidator
                        .concat(".").concat(defaultAlg),
                CertPathValidatorProviderClass);
        Security.insertProviderAt(mProv, 1);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        Security.removeProvider(mProv.getName());
    }

    /**
     * Constructor for CertPathValidator2Test.
     * 
     * @param arg0
     */
    public CertPathValidator2Test(String arg0) {
        super(arg0);
    }

    private void checkResult(CertPathValidator certV) 
            throws CertPathValidatorException, 
            InvalidAlgorithmParameterException {
        String dt = CertPathValidator.getDefaultType();
        String propName = "certpathvalidator.type";
        for (int i = 0; i <invalidValues.length; i++) {
            Security.setProperty(propName, invalidValues[i]);
            assertEquals("Incorrect default type", CertPathValidator.getDefaultType(),
                    invalidValues[i]);
        }
        Security.setProperty(propName, dt);
        assertEquals("Incorrect default type", CertPathValidator.getDefaultType(),
                dt);       certV.validate(null, null);
       try {
           certV.validate(null, null);           
       } catch (CertPathValidatorException e) {
       }
       try {
           certV.validate(null, null);           
       } catch (InvalidAlgorithmParameterException e) {
       }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method 
     * Assertions:
     * throws NullPointerException when algorithm is null 
     * throws NoSuchAlgorithmException when algorithm  is not available
     * returns CertPathValidator object
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getInstance",
          methodArgs = {java.lang.String.class}
        )
    })
    public void _testGetInstance01() throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, CertPathValidatorException {
        try {
            CertPathValidator.getInstance(null);
            fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                CertPathValidator.getInstance(invalidValues[i]);
                fail("NoSuchAlgorithmException must be thrown (type: ".concat(
                        invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        CertPathValidator cerPV;
        for (int i = 0; i < validValues.length; i++) {
            cerPV = CertPathValidator.getInstance(validValues[i]);
            assertEquals("Incorrect type", cerPV.getAlgorithm(), validValues[i]);
            assertEquals("Incorrect provider", cerPV.getProvider(), mProv);
            checkResult(cerPV);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertions: 
     * throws NullPointerException when algorithm is null 
     * throws NoSuchAlgorithmException when algorithm  is not available
     * throws IllegalArgumentException when provider is null or empty; 
     * throws NoSuchProviderException when provider is available; 
     * returns CertPathValidator object
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getInstance",
          methodArgs = {java.lang.String.class, java.lang.String.class}
        )
    })
    public void _testGetInstance02() throws NoSuchAlgorithmException,
            NoSuchProviderException, IllegalArgumentException,
            InvalidAlgorithmParameterException, CertPathValidatorException {
        try {
            CertPathValidator.getInstance(null, mProv.getName());
            fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                CertPathValidator.getInstance(invalidValues[i], mProv
                        .getName());
                fail("NoSuchAlgorithmException must be thrown (type: ".concat(
                        invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        String prov = null;
        for (int i = 0; i < validValues.length; i++) {
            try {
                CertPathValidator.getInstance(validValues[i], prov);
                fail("IllegalArgumentException must be thrown when provider is null (type: "
                        .concat(validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
            try {
                CertPathValidator.getInstance(validValues[i], "");
                fail("IllegalArgumentException must be thrown when provider is empty (type: "
                        .concat(validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        for (int i = 0; i < validValues.length; i++) {
            for (int j = 1; j < invalidValues.length; j++) {
                try {
                    CertPathValidator.getInstance(validValues[i],
                            invalidValues[j]);
                    fail("NoSuchProviderException must be thrown (type: "
                            .concat(validValues[i]).concat(" provider: ")
                            .concat(invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
        CertPathValidator cerPV;
        for (int i = 0; i < validValues.length; i++) {
            cerPV = CertPathValidator.getInstance(validValues[i], mProv
                    .getName());
            assertEquals("Incorrect type", cerPV.getAlgorithm(), validValues[i]);
            assertEquals("Incorrect provider", cerPV.getProvider().getName(),
                    mProv.getName());
            checkResult(cerPV);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code>
     * method 
     * Assertions: 
     * throws NullPointerException when algorithm is null 
     * throws NoSuchAlgorithmException when algorithm  is not available
     * throws IllegalArgumentException when provider is null; 
     * returns CertPathValidator object
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "getInstance",
          methodArgs = {java.lang.String.class, java.security.Provider.class}
        )
    })
    public void _testGetInstance03() throws NoSuchAlgorithmException,
            IllegalArgumentException,
            InvalidAlgorithmParameterException, CertPathValidatorException {
        try {
            CertPathValidator.getInstance(null, mProv);
            fail("NullPointerException or NoSuchAlgorithmException must be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                CertPathValidator.getInstance(invalidValues[i], mProv);
                fail("NoSuchAlgorithmException must be thrown (type: ".concat(
                        invalidValues[i]).concat(")"));
            } catch (NoSuchAlgorithmException e) {
            }
        }
        Provider prov = null;
        for (int i = 0; i < validValues.length; i++) {
            try {
                CertPathValidator.getInstance(validValues[i], prov);
                fail("IllegalArgumentException must be thrown when provider is null (type: "
                        .concat(validValues[i]).concat(")"));
            } catch (IllegalArgumentException e) {
            }
        }
        CertPathValidator cerPV;
        for (int i = 0; i < validValues.length; i++) {
            cerPV = CertPathValidator.getInstance(validValues[i], mProv);
            assertEquals("Incorrect type", cerPV.getAlgorithm(), validValues[i]);
            assertEquals("Incorrect provider", cerPV.getProvider(), mProv);
            checkResult(cerPV);
        }
    }
}
