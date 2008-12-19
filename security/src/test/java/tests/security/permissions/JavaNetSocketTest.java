/*
 * Copyright (C) 2007 The Android Open Source Project
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

package tests.security.permissions;

import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.Socket;
/*
 * This class tests the secrity permissions which are documented in
 * http://java.sun.com/j2se/1.5.0/docs/guide/security/permissions.html#PermsAndMethods
 * for class java.net.Socket
 */
@TestTargetClass(SecurityManager.class)
public class JavaNetSocketTest extends TestCase {
    
    SecurityManager old;

    @Override
    protected void setUp() throws Exception {
        old = System.getSecurityManager();
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        System.setSecurityManager(old);
        super.tearDown();
    }
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies that java.net.ServerSocket constructor calls " +
            "checkConnect of security permissions.",
      targets = {
        @TestTarget(
          methodName = "checkConnect",
          methodArgs = {java.lang.String.class, int.class}
        )
    })
    public void test_ctor() throws IOException {
        class TestSecurityManager extends SecurityManager {
            boolean called = false;
            String host = null;
            int port = 0;
            void reset(){
                called = false;
                host = null;
                port = 0;
            }
            @Override
            public void checkConnect(String host, int port) {
                this.called = true;
                this.port = port;
                this.host = host;
                super.checkConnect(host, port);
            }
        }
        
        TestSecurityManager s = new TestSecurityManager();
        System.setSecurityManager(s);
        
        s.reset();
        String host = "www.google.ch";
        int port = 80;
        new Socket(host, port);
        assertTrue("java.net.ServerSocket ctor must call checkConnect on security permissions", s.called);
        assertEquals("Argument of checkConnect is not correct", host, s.host);
        assertEquals("Argument of checkConnect is not correct", port, s.port);
    }
    
}
