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

package tests.api.java.io;

import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTarget;
import dalvik.annotation.TestTargetClass; 

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

@TestTargetClass(PushbackInputStream.class) 
public class PushbackInputStreamTest extends junit.framework.TestCase {

    PushbackInputStream pis;

    public String fileString = "Test_All_Tests\nTest_java_io_BufferedInputStream\nTest_java_io_BufferedOutputStream\nTest_java_io_ByteArrayInputStream\nTest_java_io_ByteArrayOutputStream\nTest_java_io_DataInputStream\nTest_java_io_File\nTest_java_io_FileDescriptor\nTest_java_io_FileInputStream\nTest_java_io_FileNotFoundException\nTest_java_io_FileOutputStream\nTest_java_io_FilterInputStream\nTest_java_io_FilterOutputStream\nTest_java_io_InputStream\nTest_java_io_IOException\nTest_java_io_OutputStream\nTest_java_io_PrintStream\nTest_java_io_RandomAccessFile\nTest_java_io_SyncFailedException\nTest_java_lang_AbstractMethodError\nTest_java_lang_ArithmeticException\nTest_java_lang_ArrayIndexOutOfBoundsException\nTest_java_lang_ArrayStoreException\nTest_java_lang_Boolean\nTest_java_lang_Byte\nTest_java_lang_Character\nTest_java_lang_Class\nTest_java_lang_ClassCastException\nTest_java_lang_ClassCircularityError\nTest_java_lang_ClassFormatError\nTest_java_lang_ClassLoader\nTest_java_lang_ClassNotFoundException\nTest_java_lang_CloneNotSupportedException\nTest_java_lang_Double\nTest_java_lang_Error\nTest_java_lang_Exception\nTest_java_lang_ExceptionInInitializerError\nTest_java_lang_Float\nTest_java_lang_IllegalAccessError\nTest_java_lang_IllegalAccessException\nTest_java_lang_IllegalArgumentException\nTest_java_lang_IllegalMonitorStateException\nTest_java_lang_IllegalThreadStateException\nTest_java_lang_IncompatibleClassChangeError\nTest_java_lang_IndexOutOfBoundsException\nTest_java_lang_InstantiationError\nTest_java_lang_InstantiationException\nTest_java_lang_Integer\nTest_java_lang_InternalError\nTest_java_lang_InterruptedException\nTest_java_lang_LinkageError\nTest_java_lang_Long\nTest_java_lang_Math\nTest_java_lang_NegativeArraySizeException\nTest_java_lang_NoClassDefFoundError\nTest_java_lang_NoSuchFieldError\nTest_java_lang_NoSuchMethodError\nTest_java_lang_NullPointerException\nTest_java_lang_Number\nTest_java_lang_NumberFormatException\nTest_java_lang_Object\nTest_java_lang_OutOfMemoryError\nTest_java_lang_RuntimeException\nTest_java_lang_SecurityManager\nTest_java_lang_Short\nTest_java_lang_StackOverflowError\nTest_java_lang_String\nTest_java_lang_StringBuffer\nTest_java_lang_StringIndexOutOfBoundsException\nTest_java_lang_System\nTest_java_lang_Thread\nTest_java_lang_ThreadDeath\nTest_java_lang_ThreadGroup\nTest_java_lang_Throwable\nTest_java_lang_UnknownError\nTest_java_lang_UnsatisfiedLinkError\nTest_java_lang_VerifyError\nTest_java_lang_VirtualMachineError\nTest_java_lang_vm_Image\nTest_java_lang_vm_MemorySegment\nTest_java_lang_vm_ROMStoreException\nTest_java_lang_vm_VM\nTest_java_lang_Void\nTest_java_net_BindException\nTest_java_net_ConnectException\nTest_java_net_DatagramPacket\nTest_java_net_DatagramSocket\nTest_java_net_DatagramSocketImpl\nTest_java_net_InetAddress\nTest_java_net_NoRouteToHostException\nTest_java_net_PlainDatagramSocketImpl\nTest_java_net_PlainSocketImpl\nTest_java_net_Socket\nTest_java_net_SocketException\nTest_java_net_SocketImpl\nTest_java_net_SocketInputStream\nTest_java_net_SocketOutputStream\nTest_java_net_UnknownHostException\nTest_java_util_ArrayEnumerator\nTest_java_util_Date\nTest_java_util_EventObject\nTest_java_util_HashEnumerator\nTest_java_util_Hashtable\nTest_java_util_Properties\nTest_java_util_ResourceBundle\nTest_java_util_tm\nTest_java_util_Vector\n";

    /**
     * @tests java.io.PushbackInputStream#PushbackInputStream(java.io.InputStream)
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "PushbackInputStream",
          methodArgs = {java.io.InputStream.class}
        )
    })
    public void test_ConstructorLjava_io_InputStream() {
        // Test for method java.io.PushbackInputStream(java.io.InputStream)
        try {
            pis = new PushbackInputStream(new ByteArrayInputStream("Hello"
                    .getBytes()));
            pis.unread("He".getBytes());
        } catch (IOException e) {
            // Correct
            // Pushback buffer should be full
            return;

        }
        fail("Failed to throw exception on unread when buffer full");
    }

    /**
     * @tests java.io.PushbackInputStream#PushbackInputStream(java.io.InputStream,
     *        int)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "IllegalArgumentException checking missed.",
      targets = {
        @TestTarget(
          methodName = "PushbackInputStream",
          methodArgs = {java.io.InputStream.class, int.class}
        )
    })
    public void test_ConstructorLjava_io_InputStreamI() {
        // Test for method java.io.PushbackInputStream(java.io.InputStream, int)
        try {
            pis = new PushbackInputStream(new ByteArrayInputStream("Hello"
                    .getBytes()), 5);
            pis.unread("Hellos".getBytes());
        } catch (IOException e) {
            // Correct
            // Pushback buffer should be full
            return;

        }
        fail("Failed to throw exception on unread when buffer full");
    }

    /**
     * @tests java.io.PushbackInputStream#available()
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "IOException checking missed.",
      targets = {
        @TestTarget(
          methodName = "available",
          methodArgs = {}
        )
    })
    public void test_available() {
        // Test for method int java.io.PushbackInputStream.available()
        try {
            assertTrue("Available returned incorrect number of bytes", pis
                    .available() == fileString.getBytes().length);
        } catch (IOException e) {
            fail("Exception during available test: " + e.toString());
        }
    }

    /**
     * @tests java.io.PushbackInputStream#markSupported()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "markSupported",
          methodArgs = {}
        )
    })
    public void test_markSupported() {
        // Test for method boolean java.io.PushbackInputStream.markSupported()
        assertTrue("markSupported returned true", !pis.markSupported());
    }

    /**
     * @tests java.io.PushbackInputStream#read()
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "IOException checking missed.",
      targets = {
        @TestTarget(
          methodName = "read",
          methodArgs = {}
        )
    })
    public void test_read() {
        // Test for method int java.io.PushbackInputStream.read()
        try {
            assertTrue("Incorrect byte read", pis.read() == fileString
                    .getBytes()[0]);
        } catch (IOException e) {
            fail("Exception during read test : " + e.getMessage());
        }
    }

    /**
     * @tests java.io.PushbackInputStream#read(byte[], int, int)
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "IOException checking missed.",
      targets = {
        @TestTarget(
          methodName = "read",
          methodArgs = {byte[].class, int.class, int.class}
        )
    })
    public void test_read$BII() {
        // Test for method int java.io.PushbackInputStream.read(byte [], int,
        // int)
        try {
            byte[] buf = new byte[100];
            pis.read(buf, 0, buf.length);
            assertTrue("Incorrect bytes read", new String(buf)
                    .equals(fileString.substring(0, 100)));
        } catch (IOException e) {
            fail("Exception during read test : " + e.getMessage());
        }
    }

    /**
     * @tests java.io.PushbackInputStream#skip(long)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "IOException checking missed.",
      targets = {
        @TestTarget(
          methodName = "skip",
          methodArgs = {long.class}
        )
    })
    public void test_skipJ() {
        // Test for method long java.io.PushbackInputStream.skip(long)
        try {
            byte[] buf = new byte[50];
            pis.skip(50);
            pis.read(buf, 0, buf.length);
            assertTrue("a) Incorrect bytes read", new String(buf)
                    .equals(fileString.substring(50, 100)));
            pis.unread(buf);
            pis.skip(25);
            byte[] buf2 = new byte[25];
            pis.read(buf2, 0, buf2.length);
            assertTrue("b) Incorrect bytes read", new String(buf2)
                    .equals(fileString.substring(75, 100)));
        } catch (Exception e) {
            fail("Exception during test : " + e.getMessage());
        }
    }

    /**
     * @tests java.io.PushbackInputStream#unread(byte[])
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "IOException checking missed.",
      targets = {
        @TestTarget(
          methodName = "unread",
          methodArgs = {byte[].class}
        )
    })
    public void test_unread$B() {
        // Test for method void java.io.PushbackInputStream.unread(byte [])
        try {
            byte[] buf = new byte[100];
            pis.read(buf, 0, buf.length);
            assertTrue("Incorrect bytes read", new String(buf)
                    .equals(fileString.substring(0, 100)));
            pis.unread(buf);
            pis.read(buf, 0, 50);
            assertTrue("Failed to unread bytes", new String(buf, 0, 50)
                    .equals(fileString.substring(0, 50)));
        } catch (IOException e) {
            fail("IOException during unread test : " + e.getMessage());
        }
    }

    /**
     * @tests java.io.PushbackInputStream#unread(byte[], int, int)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "IOException checking missed.",
      targets = {
        @TestTarget(
          methodName = "unread",
          methodArgs = {byte[].class, int.class, int.class}
        )
    })
    public void test_unread$BII() {
        // Test for method void java.io.PushbackInputStream.unread(byte [], int,
        // int)
        try {
            byte[] buf = new byte[100];
            pis.read(buf, 0, buf.length);
            assertTrue("Incorrect bytes read", new String(buf)
                    .equals(fileString.substring(0, 100)));
            pis.unread(buf, 50, 50);
            pis.read(buf, 0, 50);
            assertTrue("Failed to unread bytes", new String(buf, 0, 50)
                    .equals(fileString.substring(50, 100)));
        } catch (IOException e) {
            fail("IOException during unread test : " + e.getMessage());
        }
    }

    /**
     * @tests java.io.PushbackInputStream#unread(int)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "IOException checking missed.",
      targets = {
        @TestTarget(
          methodName = "unread",
          methodArgs = {int.class}
        )
    })
    public void test_unreadI() {
        // Test for method void java.io.PushbackInputStream.unread(int)
        try {
            int x;
            assertTrue("Incorrect byte read", (x = pis.read()) == fileString
                    .getBytes()[0]);
            pis.unread(x);
            assertTrue("Failed to unread", pis.read() == x);
        } catch (IOException e) {
            fail("IOException during read test : " + e.getMessage());
        }
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {

        pis = new PushbackInputStream(new ByteArrayInputStream(fileString
                .getBytes()), 65535);
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() {
        try {
            pis.close();
        } catch (IOException e) {
            fail("IOException during tearDown : " + e.getMessage());
        }
    }
}
