/*
 * Copyright (C) 2009 The Android Open Source Project
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
package tests.targets.security.cert;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

import junit.framework.TestCase;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathParameters;
public abstract class CertPathBuilderTest extends TestCase {

    private final String algorithmName;
    private CertPathParameters params;

    public CertPathBuilderTest(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    protected void setUp() throws Exception {
        super.setUp();
        
        params = getCertPathParameters();
    }

    abstract CertPathParameters getCertPathParameters();
    abstract void validateCertPath(CertPath path);

    @TestTargets({
        @TestTargetNew(
                level=TestLevel.ADDITIONAL,
                method="getInstance",
                args={String.class}
        ),
        @TestTargetNew(
                level=TestLevel.ADDITIONAL,
                method="build",
                args={CertPathParameters.class}
        ),
        @TestTargetNew(
                level=TestLevel.ADDITIONAL,
                clazz=CertPathBuilderResult.class,
                method="getCertPath",
                args={}
        ),
        @TestTargetNew(
                level=TestLevel.COMPLETE,
                method="method",
                args={}
        )
    })
    public void testCertPathBuilder() {
        CertPathBuilder pathBuilder = null;
        try {
            pathBuilder = CertPathBuilder.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            fail(e.getMessage());
        }

        CertPathBuilderResult builderResult = null;
        try {
            builderResult = pathBuilder.build(params);
        } catch (CertPathBuilderException e) {
            fail(e.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            fail(e.getMessage());
        }

        CertPath path = builderResult.getCertPath();

        assertNotNull("built path is null", path);
        
        validateCertPath(path);
    }
}
