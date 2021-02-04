/*
 * Copyright (c) 2013, 2015, Oracle and/or its affiliates. All rights reserved.
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
 * @summary Test for badExtendedKeyUsage warning
 * @library /lib/testlibrary ../
 * @run main BadExtendedKeyUsageTest
 */
public class BadExtendedKeyUsageTest extends Test {

    /**
     * The test signs and verifies a jar that contains entries
     * whose signer certificate's ExtendedKeyUsage extension
     * doesn't allow code signing (badExtendedKeyUsage).
     * Warning message is expected.
     */
    public static void main(String[] args) throws Throwable {
        BadExtendedKeyUsageTest test = new BadExtendedKeyUsageTest();
        test.start();
    }

    private void start() throws Throwable {
        // create a jar file that contains one class file
        Utils.createFiles(FIRST_FILE);
        JarUtils.createJar(UNSIGNED_JARFILE, FIRST_FILE);

        // create a certificate whose signer certificate's
        // ExtendedKeyUsage extension doesn't allow code signing
        ProcessTools.executeCommand(KEYTOOL,
                "-genkey",
                "-alias", KEY_ALIAS,
                "-keyalg", KEY_ALG,
                "-keysize", Integer.toString(KEY_SIZE),
                "-keystore", KEYSTORE,
                "-storepass", PASSWORD,
                "-keypass", PASSWORD,
                "-dname", "CN=Test",
                "-ext", "ExtendedkeyUsage=serverAuth",
                "-validity", Integer.toString(VALIDITY)).shouldHaveExitValue(0);

        // sign jar
        OutputAnalyzer analyzer = ProcessTools.executeCommand(JARSIGNER,
                "-verbose",
                "-keystore", KEYSTORE,
                "-storepass", PASSWORD,
                "-keypass", PASSWORD,
                "-signedjar", SIGNED_JARFILE,
                UNSIGNED_JARFILE,
                KEY_ALIAS);

        checkSigning(analyzer, BAD_EXTENDED_KEY_USAGE_SIGNING_WARNING);

        // verify signed jar
        analyzer = ProcessTools.executeCommand(JARSIGNER,
                "-verify",
                "-verbose",
                "-keystore", KEYSTORE,
                "-storepass", PASSWORD,
                "-keypass", PASSWORD,
                SIGNED_JARFILE);

        checkVerifying(analyzer, 0, BAD_EXTENDED_KEY_USAGE_VERIFYING_WARNING);

        // verity signed jar in strict mode
        analyzer = ProcessTools.executeCommand(JARSIGNER,
                "-verify",
                "-verbose",
                "-strict",
                "-keystore", KEYSTORE,
                "-storepass", PASSWORD,
                "-keypass", PASSWORD,
                SIGNED_JARFILE);

        checkVerifying(analyzer, BAD_EXTENDED_KEY_USAGE_EXIT_CODE,
                BAD_EXTENDED_KEY_USAGE_VERIFYING_WARNING);

        System.out.println("Test passed");
    }

}
