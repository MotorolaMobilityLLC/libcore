/*
 * Copyright (c) 2013, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import jdk.testlibrary.OutputAnalyzer;
import jdk.testlibrary.ProcessTools;
import jdk.testlibrary.JarUtils;

/**
 * @test
 * @bug 8024302 8026037
 * @summary Test for hasExpiringCert warning
 * @library /lib/testlibrary ../
 * @run main HasExpiringCertTest
 */
public class HasExpiringCertTest extends Test {

    static final int SHORT_VALIDITY = 90; // less than 6 month

    /**
     * The test signs and verifies a jar that contains entries
     * whose signer certificate will expire within six months (hasExpiringCert).
     * Warning message is expected.
     */
    public static void main(String[] args) throws Throwable {
        HasExpiringCertTest test = new HasExpiringCertTest();
        test.start();
    }

    private void start() throws Throwable {
        // create a jar file that contains one class file
        Utils.createFiles(FIRST_FILE);
        JarUtils.createJar(UNSIGNED_JARFILE, FIRST_FILE);

        // create key pair for jar signing
        createAlias(CA_KEY_ALIAS);
        createAlias(KEY_ALIAS);

        issueCert(
                KEY_ALIAS,
                "-validity", Integer.toString(SHORT_VALIDITY));

        // sign jar
        OutputAnalyzer analyzer = ProcessTools.executeCommand(JARSIGNER,
                "-keystore", KEYSTORE,
                "-verbose",
                "-storepass", PASSWORD,
                "-keypass", PASSWORD,
                "-signedjar", SIGNED_JARFILE,
                UNSIGNED_JARFILE,
                KEY_ALIAS);

        checkSigning(analyzer, HAS_EXPIRING_CERT_SIGNING_WARNING);

        // verify signed jar
        analyzer = ProcessTools.executeCommand(JARSIGNER,
                "-verify",
                "-verbose",
                "-keystore", KEYSTORE,
                "-storepass", PASSWORD,
                "-keypass", PASSWORD,
                SIGNED_JARFILE,
                KEY_ALIAS);

        checkVerifying(analyzer, 0, HAS_EXPIRING_CERT_VERIFYING_WARNING);

        // verify signed jar in strict mode
        analyzer = ProcessTools.executeCommand(JARSIGNER,
                "-verify",
                "-verbose",
                "-strict",
                "-keystore", KEYSTORE,
                "-storepass", PASSWORD,
                "-keypass", PASSWORD,
                SIGNED_JARFILE,
                KEY_ALIAS);

        checkVerifying(analyzer, 0, HAS_EXPIRING_CERT_VERIFYING_WARNING);

        System.out.println("Test passed");
    }

}
