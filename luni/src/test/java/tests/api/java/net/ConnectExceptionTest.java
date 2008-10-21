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

package tests.api.java.net;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import tests.support.Support_PortManager;

public class ConnectExceptionTest extends junit.framework.TestCase {

	/**
	 * @tests java.net.ConnectException#ConnectException()
     * @tests java.net.ConnectException#ConnectException(java.lang.String)
	 */
	public void test_Constructor() {
        assertNull("Wrong message", new ConnectException().getMessage());
	    assertEquals("Wrong message", "message", new ConnectException("message").getMessage());
	}
}
