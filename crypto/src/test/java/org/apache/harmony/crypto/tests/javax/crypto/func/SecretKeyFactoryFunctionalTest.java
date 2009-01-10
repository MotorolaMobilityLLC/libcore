/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.crypto.tests.javax.crypto.func;

import junit.framework.TestCase;


import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;

public class SecretKeyFactoryFunctionalTest extends TestCase {
    
    public void test_() throws Exception {
        String[] algArray = {"DES", "DESede", "PBEWITHMD5ANDDES",
                "PBEWithSHA1AndDESede", "PBEWithSHA1AndRC2_40"};

        SecretKeyFactoryThread skft = new SecretKeyFactoryThread(algArray);
        skft.launcher();
        
        assertEquals(skft.getFailureMessages(), 0, skft.getTotalFailuresNumber());
    }
}
