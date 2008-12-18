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

package org.apache.harmony.security.tests.java.security;

import dalvik.annotation.TestTargetClass;
import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;

import java.security.Key;

import junit.framework.TestCase;
@TestTargetClass(Key.class)
/**
 * Tests for <code>Key</code> class field
 * 
 */

public class KeyTest extends TestCase {

    /**
     * Constructor for KeyTest.
     * 
     * @param arg0
     */
    public KeyTest(String arg0) {
        super(arg0);
    }

    /**
     * Test for <code>serialVersionUID</code> field
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "Field testing",
      targets = {
        @TestTarget(
          methodName = "serialVersionUID",
          methodArgs = {}
        )
    })
    public void testField() {
        checkKey mk = new checkKey();
        assertEquals("Incorrect serialVersionUID", mk.getSerVerUID(), //Key.serialVersionUID,
                6603384152749567654L);
    }
    
    public class checkKey implements Key {
        public String getAlgorithm() {
            return "Key";
        }
        public String getFormat() {
            return "Format";
        }
        public byte[] getEncoded() {
            return new byte[0];
        }
        public long getSerVerUID() {
            return serialVersionUID;
        }
    }

}


