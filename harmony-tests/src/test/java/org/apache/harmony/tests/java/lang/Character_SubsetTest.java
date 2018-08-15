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

package org.apache.harmony.tests.java.lang;

import junit.framework.TestCase;

public class Character_SubsetTest extends TestCase {

    /**
     * java.lang.Character.Subset#Character.Subset(java.lang.String)
     */
    public void test_Ctor() {

        try {
            // Regression for HARMONY-888
            new Character.Subset(null) {
            };
            fail("No expected NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    /**
     * java.lang.Character.Subset#toString()
     */
    public void test_toString() {

        String name = "name";
        Character.Subset subset = new Character.Subset(name) {
        };
        assertSame(name, subset.toString());
    }

}
