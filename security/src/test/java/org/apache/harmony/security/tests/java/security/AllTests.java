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

package org.apache.harmony.security.tests.java.security;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is autogenerated source file. Includes tests for package org.apache.harmony.security.tests.java.security;
 */

public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }

    public static Test suite() {
        TestSuite suite = tests.TestSuiteFactory.createTestSuite("All tests for package org.apache.harmony.security.tests.java.security;");
        // $JUnit-BEGIN$

        suite.addTestSuite(AccessControlException2Test.class);
        suite.addTestSuite(AccessControlExceptionTest.class);
        suite.addTestSuite(AccessController2Test.class);
        suite.addTestSuite(AlgorithmParameterGenerator1Test.class);
        suite.addTestSuite(AlgorithmParameterGenerator2Test.class);
        suite.addTestSuite(AlgorithmParameterGenerator3Test.class);
        suite.addTestSuite(AlgorithmParametersSpiTest.class);
        suite.addTestSuite(AlgorithmParametersTest.class);
        suite.addTestSuite(AllPermission2Test.class);
        suite.addTestSuite(AuthProviderTest.class);
        suite.addTestSuite(BasicPermission2Test.class);
        suite.addTestSuite(CodeSignerTest.class);
        suite.addTestSuite(CodeSource2Test.class);
        suite.addTestSuite(CodeSourceTest.class);
        suite.addTestSuite(DigestExceptionTest.class);
        suite.addTestSuite(DigestInputStream2Test.class);
        suite.addTestSuite(DigestInputStreamTest.class);
        suite.addTestSuite(DigestOutputStreamTest.class);
        suite.addTestSuite(GeneralSecurityExceptionTest.class);
        suite.addTestSuite(GuardedObjectTest.class);
        suite.addTestSuite(Identity2Test.class);
        suite.addTestSuite(IdentityScope2Test.class);
        suite.addTestSuite(IdentityScopeTest.class);
        suite.addTestSuite(InvalidAlgorithmParameterExceptionTest.class);
        suite.addTestSuite(InvalidKeyExceptionTest.class);
        suite.addTestSuite(InvalidParameterExceptionTest.class);
        suite.addTestSuite(KSCallbackHandlerProtectionTest.class);
        suite.addTestSuite(KSPasswordProtectionTest.class);
        suite.addTestSuite(KSPrivateKeyEntryTest.class);
        suite.addTestSuite(KSSecretKeyEntryTest.class);
        suite.addTestSuite(KSTrustedCertificateEntryTest.class);
        suite.addTestSuite(KeyExceptionTest.class);
        suite.addTestSuite(KeyFactory2Test.class);
        suite.addTestSuite(KeyFactorySpiTest.class);
        suite.addTestSuite(KeyManagementExceptionTest.class);
        suite.addTestSuite(KeyPairGenerator1Test.class);
        suite.addTestSuite(KeyPairGenerator2Test.class);
        suite.addTestSuite(KeyPairGenerator3Test.class);
        suite.addTestSuite(KeyPairGenerator4Test.class);
        suite.addTestSuite(KeyPairGeneratorSpiTest.class);
        suite.addTestSuite(KeyPairTest.class);
        suite.addTestSuite(KeyRepTest.class);
        suite.addTestSuite(KeyRepTypeTest.class);
        suite.addTestSuite(KeyStore2Test.class);
        suite.addTestSuite(KeyStore3Test.class);
        suite.addTestSuite(KeyStoreBuilderTest.class);
        suite.addTestSuite(KeyStoreExceptionTest.class);
        suite.addTestSuite(KeyStoreSpiTest.class);
        suite.addTestSuite(KeyStoreTest.class);
        suite.addTestSuite(KeyTest.class);
        suite.addTestSuite(MessageDigest1Test.class);
        suite.addTestSuite(MessageDigest2Test.class);
        suite.addTestSuite(MessageDigestSpiTest.class);
        suite.addTestSuite(NoSuchAlgorithmExceptionTest.class);
        suite.addTestSuite(NoSuchProviderExceptionTest.class);
        suite.addTestSuite(Permission2Test.class);
        suite.addTestSuite(PermissionCollectionTest.class);
        suite.addTestSuite(PermissionTest.class);
        suite.addTestSuite(Permissions2Test.class);
        suite.addTestSuite(PermissionsTest.class);
        suite.addTestSuite(PolicyTest.class);
        suite.addTestSuite(PrivateKeyTest.class);
        suite.addTestSuite(PrivilegedActionException2Test.class);
        suite.addTestSuite(PrivilegedActionExceptionTest.class);
        suite.addTestSuite(ProtectionDomainTest.class);
        suite.addTestSuite(Provider2Test.class);
        suite.addTestSuite(ProviderExceptionTest.class);
        suite.addTestSuite(ProviderServiceTest.class);
        suite.addTestSuite(ProviderTest.class);
        suite.addTestSuite(PublicKeyTest.class);
        suite.addTestSuite(SecureRandom2Test.class);
        suite.addTestSuite(SecureRandomSpiTest.class);
        suite.addTestSuite(Security2Test.class);
        suite.addTestSuite(SecurityPermission2Test.class);
        suite.addTestSuite(SecurityPermissionTest.class);
        suite.addTestSuite(SecurityTest.class);
        suite.addTestSuite(Signature2Test.class);
        suite.addTestSuite(SignatureExceptionTest.class);
        suite.addTestSuite(SignatureSpiTest.class);
        suite.addTestSuite(SignatureTest.class);
        suite.addTestSuite(SignedObjectTest.class);
        suite.addTestSuite(SignerTest.class);
        suite.addTestSuite(TimestampTest.class);
        suite.addTestSuite(UnrecoverableEntryExceptionTest.class);
        suite.addTestSuite(UnrecoverableKeyExceptionTest.class);
        suite.addTestSuite(UnresolvedPermissionTest.class);

        // $JUnit-END$
        return suite;
    }
}
