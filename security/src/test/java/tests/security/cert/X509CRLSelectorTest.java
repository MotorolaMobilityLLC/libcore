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
* @author Alexander Y. Kleymenov
* @version $Revision$
*/

package tests.security.cert;

import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;

import junit.framework.TestCase;

import java.io.IOException;
import java.security.cert.X509CRLSelector;
import java.util.Iterator;
import java.util.TreeSet;

import javax.security.auth.x500.X500Principal;

@TestTargetClass(X509CRLSelector.class)
public class X509CRLSelectorTest extends TestCase {

    /**
     * @tests java.security.cert.X509CRLSelector#addIssuer(javax.security.auth.x500.X500Principal)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "",
        method = "addIssuer",
        args = {javax.security.auth.x500.X500Principal.class}
    )
    public void test_addIssuerLjavax_security_auth_x500_X500Principal01()
            throws Exception {
        //Regression for HARMONY-465
        X509CRLSelector obj = new X509CRLSelector();
        try {
            obj.addIssuer((X500Principal) null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.security.cert.X509CRLSelector#addIssuerName(java.lang.String)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies IOException.",
        method = "addIssuerName",
        args = {java.lang.String.class}
    )
    public void test_addIssuerNameLjava_lang_String01() throws Exception {
        //Regression for HARMONY-465
        X509CRLSelector obj = new X509CRLSelector();
        try {
            obj.addIssuerName("234");
            fail("IOException expected");
        } catch (IOException e) {
            // expected
        }

        // Regression for HARMONY-1076
        try {
            new X509CRLSelector().addIssuerName("w=y");
            fail("IOException expected");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * @tests java.security.cert.X509CRLSelector#addIssuerName(java.lang.String)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies null as a parameter.",
        method = "addIssuerName",
        args = {java.lang.String.class}
    )
    public void test_addIssuerNameLjava_lang_String02() throws IOException {
        // Regression for HARMONY-736
        X509CRLSelector selector = new X509CRLSelector();

        // no exception for null
        selector.addIssuerName((String) null);
    }


    /**
     * @tests java.security.cert.X509CRLSelector#addIssuerName(byte[])
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies IOException.",
        method = "addIssuerName",
        args = {byte[].class}
    )
    public void test_addIssuerName$B_3() throws Exception {
        //Regression for HARMONY-465
        X509CRLSelector obj = new X509CRLSelector();
        try {
            obj.addIssuerName(new byte[] { (byte) 2, (byte) 3, (byte) 4 });
            fail("IOException expected");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * @tests java.security.cert.X509CRLSelector#addIssuerName(byte[])
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NullPointerException.",
        method = "addIssuerName",
        args = {byte[].class}
    )
    public void test_addIssuerName$B_4() throws Exception {
        //Regression for HARMONY-465
        X509CRLSelector obj = new X509CRLSelector();
        try {
            obj.addIssuerName((byte[]) null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests setIssuerNames(Collection <?> names)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL,
        notes = "Regression test.",
        method = "setIssuerNames",
        args = {java.util.Collection.class}
    )
    public void test_setIssuerNamesLjava_util_Collection01() throws IOException {
        // Regression for HARMONY-737
        X509CRLSelector selector = new X509CRLSelector();
        selector.setIssuerNames(new TreeSet<Comparable>() {
            private static final long serialVersionUID = 6009545505321092498L;

            public Iterator<Comparable> iterator() {
                return null;
            }
        });
    }
}
