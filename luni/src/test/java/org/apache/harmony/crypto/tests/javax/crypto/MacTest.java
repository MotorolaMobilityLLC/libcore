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

package org.apache.harmony.crypto.tests.javax.crypto;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.DSAParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.DHGenParameterSpec;

import javax.crypto.spec.SecretKeySpec;

import org.apache.harmony.crypto.tests.support.MyMacSpi;
import org.apache.harmony.security.tests.support.SpiEngUtils;
import org.apache.harmony.xnet.provider.jsse.NativeCryptoTest;
import org.apache.harmony.xnet.provider.jsse.OpenSSLEngine;
import org.apache.harmony.xnet.provider.jsse.OpenSSLProvider;

import junit.framework.TestCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for Mac class constructors and methods
 *
 */
public class MacTest extends TestCase {

    public static final String srvMac = "Mac";

    private static String defaultAlgorithm = null;

    private static String defaultProviderName = null;

    private static Provider defaultProvider = null;

    private static boolean DEFSupported = false;

    private static final String NotSupportedMsg = "There is no suitable provider for Mac";

    private static final String[] invalidValues = SpiEngUtils.invalidValues;

    private static String[] validValues = new String[3];

    public static final String validAlgorithmsMac [] =
        {"HmacSHA1", "HmacMD5", "HmacSHA256", "HmacSHA384", "HmacSHA512"};


    static {
        for (int i = 0; i < validAlgorithmsMac.length; i++) {
            defaultProvider = SpiEngUtils.isSupport(validAlgorithmsMac[i],
                    srvMac);
            DEFSupported = (defaultProvider != null);
            if (DEFSupported) {
                defaultAlgorithm = validAlgorithmsMac[i];
                defaultProviderName = defaultProvider.getName();
                validValues[0] = defaultAlgorithm;
                validValues[1] = defaultAlgorithm.toUpperCase();
                validValues[2] = defaultAlgorithm.toLowerCase();
                break;
            }
        }
    }

    private Mac [] createMacs() {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return null;
        }
        try {
            Mac m [] = new Mac[3];
            m[0] = Mac.getInstance(defaultAlgorithm);
            m[1] = Mac.getInstance(defaultAlgorithm, defaultProvider);
            m[2] = Mac.getInstance(defaultAlgorithm, defaultProviderName);
            return m;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available
     */
    public void testMac01() {
        try {
            Mac.getInstance(null);
            fail("NullPointerException or NoSuchAlgorithmException should be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                Mac.getInstance(invalidValues[i]);
                fail("NoSuchAlgorithmException must be thrown when algorithm is not available: "
                        .concat(invalidValues[i]));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm)</code> method
     * Assertion: returns Mac object
     */
    public void testMac02() throws NoSuchAlgorithmException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac mac;
        for (int i = 0; i < validValues.length; i++) {
            mac = Mac.getInstance(validValues[i]);
            assertEquals("Incorrect algorithm", mac.getAlgorithm(), validValues[i]);
        }
    }
    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws IllegalArgumentException when provider is null or empty
     * throws NoSuchProviderException when provider is not available
     */
    public void testMac03() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        String provider = null;
        for (int i = 0; i < validValues.length; i++) {
            try {
                Mac.getInstance(validValues[i], provider);
                fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
            try {
                Mac.getInstance(validValues[i], "");
                fail("IllegalArgumentException must be thrown when provider is empty");
            } catch (IllegalArgumentException e) {
            }
            for (int j = 1; j < invalidValues.length; j++) {
                try {
                    Mac.getInstance(validValues[i], invalidValues[j]);
                    fail("NoSuchProviderException must be thrown (algorithm: "
                            .concat(validValues[i]).concat(" provider: ")
                            .concat(invalidValues[j]).concat(")"));
                } catch (NoSuchProviderException e) {
                }
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available
     */
    public void testMac04() throws NoSuchAlgorithmException,
            IllegalArgumentException, NoSuchProviderException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        try {
            Mac.getInstance(null, defaultProviderName);
            fail("NullPointerException or NoSuchAlgorithmException should be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                Mac.getInstance(invalidValues[i], defaultProviderName);
                fail("NoSuchAlgorithmException must be throws when algorithm is not available: "
                        .concat(invalidValues[i]));
            } catch( NoSuchAlgorithmException e) {
            }
        }
    }
    /**
     * Test for <code>getInstance(String algorithm, String provider)</code> method
     * Assertion: returns Mac object
     */
    public void testMac05() throws NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac mac;
        for (int i = 0; i < validValues.length; i++) {
            mac = Mac.getInstance(validValues[i], defaultProviderName);
            assertEquals("Incorrect algorithm", mac.getAlgorithm(), validValues[i]);
            assertEquals("Incorrect provider", mac.getProvider().getName(),
                    defaultProviderName);
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
     * Assertion: throws IllegalArgumentException when provider is null
     */
    public void testMac06() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Provider provider = null;
        for (int i = 0; i < validValues.length; i++) {
            try {
                Mac.getInstance(validValues[i], provider);
                fail("IllegalArgumentException must be thrown when provider is null");
            } catch (IllegalArgumentException e) {
            }
        }
    }
    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
     * Assertion:
     * throws NullPointerException when algorithm is null
     * throws NoSuchAlgorithmException when algorithm is not available
     */
    public void testMac07() throws NoSuchAlgorithmException,
            NoSuchProviderException, IllegalArgumentException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        try {
            Mac.getInstance(null, defaultProvider);
            fail("NullPointerException or NoSuchAlgorithmException should be thrown when algorithm is null");
        } catch (NullPointerException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        for (int i = 0; i < invalidValues.length; i++) {
            try {
                Mac.getInstance(invalidValues[i], defaultProvider);
                fail("NoSuchAlgorithmException must be thrown when algorithm is not available: "
                        .concat(invalidValues[i]));
            } catch (NoSuchAlgorithmException e) {
            }
        }
    }

    /**
     * Test for <code>getInstance(String algorithm, Provider provider)</code> method
     * Assertion: returns Mac object
     */
    public void testMac08() throws NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac mac;
        for (int i = 0; i < validValues.length; i++) {
            mac = Mac.getInstance(validValues[i], defaultProvider);
            assertEquals("Incorrect algorithm", mac.getAlgorithm(), validValues[i]);
            assertEquals("Incorrect provider", mac.getProvider(), defaultProvider);
        }
    }
    /**
     * Test for <code>update</code> and <code>doFinal</code> methods
     * Assertion: throws IllegalStateException when Mac is not initialized
     * @throws Exception
     */
    public void testMac09() throws Exception {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] buf = new byte[10];
        ByteBuffer bBuf = ByteBuffer.wrap(buf, 0, 10);
        byte [] bb = {(byte)1, (byte)2, (byte)3, (byte)4, (byte)5};
        SecretKeySpec sks = new SecretKeySpec(bb, "SHA1");
        for (int i = 0; i < macs.length; i++) {
            try {
                macs[i].update((byte)0);
                fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].update(buf);
                fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].update(buf, 0, 3);
                fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].update(bBuf);
                fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].doFinal();
                fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].doFinal(new byte[10]);
                fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }
            try {
                macs[i].doFinal(new byte[10], 0);
                fail("IllegalStateException must be thrown");
            } catch (IllegalStateException e) {
            }

            macs[i].init(sks);
            try {
                macs[i].doFinal(new byte[1], 0);
                fail("ShortBufferException expected");
            } catch (ShortBufferException e) {
                //expected
            }
        }
    }
    /**
     * Test for <code>doFinal(byte[] output, int outOffset)</code> method
     * Assertion:
     * throws ShotBufferException when outOffset  is negative or
     * outOffset >= output.length  or when given buffer is small
     */
    public void testMac10() throws NoSuchAlgorithmException,
            NoSuchProviderException, IllegalArgumentException,
            IllegalStateException, InvalidKeyException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac[] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte[] b = { (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0 };
        byte[] byteA = new byte[b.length];
        SecretKeySpec sks = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < macs.length; i++) {
            macs[i].init(sks);
            try {
                macs[i].doFinal(null, 10);
                fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
            try {
                macs[i].doFinal(byteA, -4);
                fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
            try {
                macs[i].doFinal(byteA, 10);
                fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
            try {
                macs[i].doFinal(new byte[1], 0);
                fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
            byte[] res = macs[i].doFinal();
            try {
                macs[i].doFinal(new byte[res.length - 1], 0);
                fail("ShortBufferException must be thrown");
            } catch (ShortBufferException e) {
            }
        }
    }

    /**
     * Test for <code>doFinal(byte[] output, int outOffset)</code> and
     * <code>doFinal()</code> methods Assertion: Mac result is stored in
     * output buffer
     */
    public void testMac11() throws NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException, IllegalStateException,
            InvalidKeyException, ShortBufferException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] b = {(byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
        SecretKeySpec scs = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < macs.length; i++) {
            macs[i].init(scs);
            byte [] res1 = macs[i].doFinal();
            byte [] res2 = new byte[res1.length + 10];
            macs[i].doFinal(res2, 0);
            for (int j = 0; j < res1.length; j++) {
                assertEquals("Not equals byte number: "
                        .concat(Integer.toString(j)), res1[j], res2[j]);
            }
        }
    }
    /**
     * Test for <code>doFinal(byte[] input)</code> method
     * Assertion: update Mac and returns result
     */
    public void testMac12() throws NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException, IllegalStateException,
            InvalidKeyException  {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] b = {(byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
        byte [] upd = {(byte)5, (byte)4, (byte)3, (byte)2, (byte)1, (byte)0};
        SecretKeySpec scs = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < macs.length; i++) {
            macs[i].init(scs);
            byte[] res1 = macs[i].doFinal();
            byte[] res2 = macs[i].doFinal();
            assertEquals("Results are not the same",
                    IntegralToString.bytesToHexString(res1, false),
                    IntegralToString.bytesToHexString(res2, false));

            res2 = macs[i].doFinal(upd);
            macs[i].update(upd);
            res1 = macs[i].doFinal();
            assertEquals("Results are not the same",
                    IntegralToString.bytesToHexString(res1, false),
                    IntegralToString.bytesToHexString(res2, false));
        }
    }

    /**
     * Test for <code>update(byte[] input, int outset, int len)</code> method
     * Assertion: throws IllegalArgumentException when offset or len is negative,
     * offset + len >= input.length
     */
    public void testMac13() throws NoSuchAlgorithmException,
            NoSuchProviderException, IllegalArgumentException, IllegalStateException,
            InvalidKeyException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] b = {(byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
        SecretKeySpec scs = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < macs.length; i++) {
            macs[i].init(scs);
            try {
                macs[i].update(b, -10, b.length);
                fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                macs[i].update(b, 0, -10);
                fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                macs[i].update(b, 0, b.length + 1);
                fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
            try {
                macs[i].update(b, b.length - 1, 2);
                fail("IllegalArgumentException must be thrown");
            } catch (IllegalArgumentException e) {
            }
        }
    }
    /**
     * Test for <code>update(byte[] input, int outset, int len)</code> and
     * <code>update(byte[] input</code>
     * methods
     * Assertion: updates Mac
     */
    public void testMac14() throws NoSuchAlgorithmException,
            NoSuchProviderException, IllegalArgumentException, IllegalStateException,
            InvalidKeyException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] b = {(byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
        byte [] upd1 = {(byte)0, (byte)1, (byte)5, (byte)4, (byte)3, (byte)2};
        byte [] upd2 = {(byte)5, (byte)4, (byte)3, (byte)2};
        byte [] res1;
        byte [] res2;
        SecretKeySpec scs = new SecretKeySpec(b, "SHA1");
        for (int i = 0; i < macs.length; i++) {
            macs[i].init(scs);
            macs[i].update(upd1, 2, 4);
            res1 = macs[i].doFinal();
            macs[i].init(scs);
            macs[i].update(upd2);
            res2 = macs[i].doFinal();
            assertEquals("Results are not the same", res1.length, res2.length);
            for(int t = 0; t < res1.length; t++) {
                assertEquals("Results are not the same", res1[t], res2[t]);
            }
            macs[i].init(scs);
            macs[i].update((byte)5);
            res1 = macs[i].doFinal();
            macs[i].init(scs);
            macs[i].update(upd1,2,1);
            res2 = macs[i].doFinal();
            assertEquals("Results are not the same", res1.length, res2.length);
            for(int t = 0; t < res1.length; t++) {
                assertEquals("Results are not the same", res1[t], res2[t]);
            }
        }
    }
    /**
     * Test for <code>clone()</code> method
     * Assertion: returns Mac object or throws CloneNotSupportedException
     */
    public void testMacClone() throws NoSuchAlgorithmException, CloneNotSupportedException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        for (int i = 0; i < macs.length; i++) {
            try {
                Mac mac1 = (Mac) macs[i].clone();
                assertEquals(mac1.getAlgorithm(), macs[i].getAlgorithm());
                assertEquals(mac1.getProvider(), macs[i].getProvider());
                assertFalse(macs[i].equals(mac1));
            } catch (CloneNotSupportedException e) {
            }
        }
    }
    /**
     * Test for
     * <code>init(Key key, AlgorithmParameterSpec params)</code>
     * <code>init(Key key)</code>
     * methods
     * Assertion: throws InvalidKeyException and InvalidAlgorithmParameterException
     * when parameters are not appropriate
     */
    public void testInit() throws NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException, IllegalStateException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] b = {(byte)1, (byte)2, (byte)3, (byte)4, (byte)5};
        SecretKeySpec sks = new SecretKeySpec(b, "SHA1");
        DHGenParameterSpec algPS = new DHGenParameterSpec(1, 2);
        PSSParameterSpec algPSS = new PSSParameterSpec(20);
        SecretKeySpec sks1 = new SecretKeySpec(b, "RSA");

        for (int i = 0; i < macs.length; i++) {
            macs[i].init(sks);
            try {
                macs[i].init(sks1, algPSS);
                fail("init(..) accepts incorrect AlgorithmParameterSpec parameter");
            } catch (InvalidAlgorithmParameterException e) {
            }
            try {
                macs[i].init(sks, algPS);
                fail("init(..) accepts incorrect AlgorithmParameterSpec parameter");
            } catch (InvalidAlgorithmParameterException e) {
            }

            try {
                macs[i].init(null, null);
                fail("InvalidKeyException must be thrown");
            } catch (InvalidKeyException e) {
            }

            try {
                macs[i].init(null);
                fail("InvalidKeyException must be thrown");
            } catch (InvalidKeyException e) {
            }
//            macs[i].init(sks, null);
        }
    }

    /**
     * Test for <code>update(ByteBuffer input)</code>
     * <code>update(byte[] input, int offset, int len)</code>
     * methods
     * Assertion: processes Mac; if input is null then do nothing
     */
    public void testUpdateByteBuffer01() throws NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException, IllegalStateException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] bb = {(byte)1, (byte)2, (byte)3, (byte)4, (byte)5};
        SecretKeySpec sks = new SecretKeySpec(bb, "SHA1");
        ByteBuffer byteNull = null;
        ByteBuffer byteBuff = ByteBuffer.allocate(0);
        byte [] bb1;
        byte [] bb2;
        for (int i = 0; i < macs.length; i++) {
            macs[i].init(sks);
            bb1 = macs[i].doFinal();
            try {
                macs[i].update(byteNull);
                fail("IllegalArgumentException must be thrown because buffer is null");
            } catch (IllegalArgumentException e) {
            }
            macs[i].update(byteBuff);
            bb2 = macs[i].doFinal();
            for (int t = 0; t < bb1.length; t++) {
                assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
            macs[i].init(sks);
            bb1 = macs[i].doFinal();
            macs[i].update(null, 0, 0);
            bb2 = macs[i].doFinal();
            for (int t = 0; t < bb1.length; t++) {
                assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
        }
    }
    /**
     * Test for <code>update(ByteBuffer input)</code>
     * <code>update(byte[] input, int offset, int len)</code>
     * methods
     * Assertion: processes Mac
     */
    public void testUpdateByteBuffer02() throws NoSuchAlgorithmException, NoSuchProviderException,
            IllegalArgumentException, IllegalStateException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] bb = {(byte)1, (byte)2, (byte)3, (byte)4, (byte)5};
        SecretKeySpec sks = new SecretKeySpec(bb, "SHA1");
        byte [] bbuf = {(byte)5, (byte)4, (byte)3, (byte)2, (byte)1};
        ByteBuffer byteBuf;
        byte [] bb1;
        byte [] bb2;
        for (int i = 0; i < macs.length; i++) {
            byteBuf = ByteBuffer.allocate(5);
            byteBuf.put(bbuf);
            byteBuf.position(2);
            macs[i].init(sks);
            macs[i].update(byteBuf);
            bb1 = macs[i].doFinal();

            macs[i].init(sks);
            macs[i].update(bbuf, 2, 3);
            bb2 = macs[i].doFinal();
            for (int t = 0; t < bb1.length; t++) {
                assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
        }
    }
    /**
     * Test for <code>clone()</code> method
     * Assertion: clone if provider is clo
     */
    public void testClone()  {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        Mac res;
        for (int i = 0; i < macs.length; i++) {
            try {
                res = (Mac)macs[i].clone();
                assertTrue("Object should not be equals", !macs[i].equals(res));
                assertEquals("Incorrect class", macs[i].getClass(), res.getClass());
            } catch (CloneNotSupportedException e) {
            }
        }
    }
    /**
     * Test for <code>getMacLength()</code> method
     * Assertion: return Mac length
     */
    public void testGetMacLength() {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        for (int i = 0; i < macs.length; i++) {
            assertTrue("Length should be positive", (macs[i].getMacLength() >= 0));
        }
    }

    /**
     * Test for <code>reset()</code> method
     * Assertion: return Mac length
     */
    public void testReset() throws InvalidKeyException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        Mac [] macs = createMacs();
        assertNotNull("Mac objects were not created", macs);
        byte [] bb = {(byte)1, (byte)2, (byte)3, (byte)4, (byte)5};
        SecretKeySpec sks = new SecretKeySpec(bb, "SHA1");
        byte [] bbuf = {(byte)5, (byte)4, (byte)3, (byte)2, (byte)1};
        byte [] bb1;
        byte [] bb2;
        for (int i = 0; i < macs.length; i++) {
            macs[i].init(sks);
            bb1 = macs[i].doFinal();
            macs[i].reset();
            bb2 = macs[i].doFinal();
            assertEquals("incorrect result",bb1.length, bb2.length);
            for (int t = 0; t < bb1.length; t++) {
               assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
            macs[i].reset();
            macs[i].update(bbuf);
            bb1 = macs[i].doFinal();
            macs[i].reset();
            macs[i].update(bbuf, 0, bbuf.length);
            bb2 = macs[i].doFinal();
            assertEquals("incorrect result",bb1.length, bb2.length);
            for (int t = 0; t < bb1.length; t++) {
               assertEquals("Incorrect doFinal result", bb1[t], bb2[t]);
            }
        }
    }
    /**
     * Test for <code>Mac</code> constructor
     * Assertion: returns Mac object
     */
    public void testMacConstructor() throws NoSuchAlgorithmException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        if (!DEFSupported) {
            fail(NotSupportedMsg);
            return;
        }
        MacSpi spi = new MyMacSpi();
        Mac mac = new myMac(spi, defaultProvider, defaultAlgorithm);
        assertEquals("Incorrect algorithm", mac.getAlgorithm(),
                defaultAlgorithm);
        assertEquals("Incorrect provider", mac.getProvider(), defaultProvider);
        try {
            mac.init(null, null);
            fail("Exception should be thrown because init(..) uses incorrect parameters");
        } catch (Exception e) {
        }
        assertEquals("Invalid mac length", mac.getMacLength(), 0);

        mac = new myMac(null, null, null);
        assertNull("Algorithm must be null", mac.getAlgorithm());
        assertNull("Provider must be null", mac.getProvider());
        try {
            mac.init(null, null);
            fail("Exception should be thrown because init(..) uses incorrect parameters");
        } catch (Exception e) {
        }
        try {
            mac.getMacLength();
            fail("NullPointerException must be thrown");
        } catch (NullPointerException e) {
        }
    }

    public void test_getAlgorithm() throws NoSuchAlgorithmException {
        Mac mac;
        for (int i = 0; i < validValues.length; i++) {
            mac = Mac.getInstance(validValues[i]);
            assertEquals("Incorrect algorithm", mac.getAlgorithm(), validValues[i]);
        }

        mac = new Mock_Mac(null, null, null);
        assertNull(mac.getAlgorithm());
    }

    public void test_getProvider() throws NoSuchAlgorithmException {
        Mac mac;
        for (int i = 0; i < validValues.length; i++) {
            mac = Mac.getInstance(validValues[i]);
            assertNotNull(mac.getProvider());
        }

        mac = new Mock_Mac(null, null, null);
        assertNull(mac.getProvider());
    }

    private static final byte[] TEST_INPUT = new byte[] {
            0x01, (byte) 0xFF, 0x55, (byte) 0xAA
    };

    public void test_ConsistentBetweenProviders() throws Exception {
        SecretKey key = new SecretKeySpec(new byte[] {
                (byte) 0x7b, (byte) 0x10, (byte) 0x6d, (byte) 0x68, (byte) 0x3f, (byte) 0x70,
                (byte) 0xa3, (byte) 0xb5, (byte) 0xa3, (byte) 0xdd, (byte) 0x9f, (byte) 0x54,
                (byte) 0x74, (byte) 0x36, (byte) 0xde, (byte) 0xa7, (byte) 0x88, (byte) 0x81,
                (byte) 0x0d, (byte) 0x89, (byte) 0xef, (byte) 0x2e, (byte) 0x42, (byte) 0x4f,
        }, "HmacMD5");
        byte[] label = new byte[] {
                (byte) 0x6b, (byte) 0x65, (byte) 0x79, (byte) 0x20, (byte) 0x65, (byte) 0x78,
                (byte) 0x70, (byte) 0x61, (byte) 0x6e, (byte) 0x73, (byte) 0x69, (byte) 0x6f,
                (byte) 0x6e,
        };
        byte[] seed = new byte[] {
                (byte) 0x50, (byte) 0xf9, (byte) 0xce, (byte) 0x14, (byte) 0xb2, (byte) 0xdd,
                (byte) 0x3d, (byte) 0xfa, (byte) 0x96, (byte) 0xd9, (byte) 0xfe, (byte) 0x3a,
                (byte) 0x1a, (byte) 0xe5, (byte) 0x79, (byte) 0x55, (byte) 0xe7, (byte) 0xbc,
                (byte) 0x84, (byte) 0x68, (byte) 0x0e, (byte) 0x2d, (byte) 0x20, (byte) 0xd0,
                (byte) 0x6e, (byte) 0xb4, (byte) 0x03, (byte) 0xbf, (byte) 0xa2, (byte) 0xe6,
                (byte) 0xc4, (byte) 0x9d, (byte) 0x50, (byte) 0xf9, (byte) 0xce, (byte) 0x14,
                (byte) 0xbc, (byte) 0xc5, (byte) 0x9e, (byte) 0x9a, (byte) 0x36, (byte) 0xa7,
                (byte) 0xaa, (byte) 0xfe, (byte) 0x3b, (byte) 0xca, (byte) 0xcb, (byte) 0x4c,
                (byte) 0xfa, (byte) 0x87, (byte) 0x9a, (byte) 0xac, (byte) 0x02, (byte) 0x25,
                (byte) 0xce, (byte) 0xda, (byte) 0x74, (byte) 0x10, (byte) 0x86, (byte) 0x9c,
                (byte) 0x03, (byte) 0x18, (byte) 0x0f, (byte) 0xe2,
        };
        Provider[] providers = Security.getProviders("Mac.HmacMD5");
        Provider defProvider = null;
        byte[] output = null;
        byte[] output2 = null;
        for (int i = 0; i < providers.length; i++) {
            System.out.println("provider = " + providers[i].getName());
            Mac mac = Mac.getInstance("HmacMD5", providers[i]);
            mac.init(key);
            mac.update(label);
            mac.update(seed);
            if (output == null) {
                output = new byte[mac.getMacLength()];
                defProvider = providers[i];
                mac.doFinal(output, 0);
                mac.init(new SecretKeySpec(label, "HmacMD5"));
                output2 = mac.doFinal(output);
            } else {
                byte[] tmp = new byte[mac.getMacLength()];
                mac.doFinal(tmp, 0);
                assertEquals(defProvider.getName() + " vs. " + providers[i].getName(),
                        Arrays.toString(output), Arrays.toString(tmp));
                mac.init(new SecretKeySpec(label, "HmacMD5"));
                assertEquals(defProvider.getName() + " vs. " + providers[i].getName(),
                        Arrays.toString(output2), Arrays.toString(mac.doFinal(output)));
            }

        }
    }

    public void test_getInstance_OpenSSL_ENGINE() throws Exception {
        final String secret = "-HMAC-test1";
        final byte[] testString = "testing123".getBytes();

        Provider p = Security.getProvider(OpenSSLProvider.PROVIDER_NAME);
        NativeCryptoTest.loadTestEngine();
        OpenSSLEngine engine = OpenSSLEngine.getInstance(NativeCryptoTest.TEST_ENGINE_ID);

        /*
         * The "-HMAC-" prefix is a special prefix recognized by
         * test_openssl_engine.cpp
         */
        SecretKey key1 = engine.getSecretKeyById(secret, "HmacSHA256");
        SecretKey key1dupe = engine.getSecretKeyById(secret, "HmacSHA256");

        /* Non-ENGINE-based SecretKey */
        SecretKey key2 = new SecretKeySpec(secret.getBytes(), "HmacSHA256");

        /* The one that is ENGINE-based can't be equal to a non-ENGINE one. */
        assertFalse(key1.equals(key2));
        assertEquals(key1, key1dupe);
        assertNull(key1.getFormat());
        assertNull(key1.getEncoded());
        assertEquals("RAW", key2.getFormat());
        assertEquals(Arrays.toString(secret.getBytes()), Arrays.toString(key2.getEncoded()));

        Mac mac1 = Mac.getInstance("HmacSHA256", p);
        mac1.init(key1);
        mac1.update(testString);
        byte[] output1 = mac1.doFinal();
        assertEquals(mac1.getMacLength(), output1.length);

        Mac mac2 = Mac.getInstance("HmacSHA256", p);
        mac2.init(key2);
        mac2.update(testString);
        byte[] output2 = mac2.doFinal();

        assertEquals(Arrays.toString(output2), Arrays.toString(output1));
    }

    class Mock_Mac extends Mac {
        protected Mock_Mac(MacSpi arg0, Provider arg1, String arg2) {
            super(arg0, arg1, arg2);
        }
    }

    public static Test suite() {
        return new TestSuite(MacTest.class);
    }
}
/**
 * Additional class for Mac constructor verification
 */
class myMac extends Mac {

    public myMac(MacSpi macSpi, Provider provider,
            String algorithm) {
        super(macSpi, provider, algorithm);
    }
}
