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

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;

import junit.framework.TestCase;

public class MacFunctionalTest extends TestCase {
    public void test_Mac() throws Exception {
        String[] algArray = {"HMACSHA1", "HMACSHA256", "HMACSHA384",
                "HMACSHA512", "HMACMD5"};

        MacThread mt = new MacThread(algArray);
        mt.launcher();

        assertEquals(mt.getFailureMessages(), 0, mt.getTotalFailuresNumber());
    }
}
