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

import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass; 

import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInput;

@TestTargetClass(InputStream.class) 
public class InputStreamTest extends junit.framework.TestCase {

    public static final String testString = "Lorem ipsum dolor sit amet,\n" +
        "consectetur adipisicing elit,\nsed do eiusmod tempor incididunt ut" +
        "labore et dolore magna aliqua.\n";

    private InputStream is;

    class MockInputStream extends java.io.InputStream {
        
        private byte[] input;
        private int position;
        
        public MockInputStream() {
            super();
            input = testString.getBytes();
            position = 0;
        }
        
        public int read() throws IOException {
            int result = -1;
            if (position < input.length) {
                result = input[position];
                position++;
            }
            return result;
        }
    }
    
    /**
     * @tests java.io.InputStream#InputStream()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies constructor InputStream(). Since this constructor does nothing, this test is intentionally kept basic.",
        method = "InputStream",
        args = {}
    )     
    public void test_Constructor() {
        try {
            InputStream myIS = new MockInputStream();
            myIS.close();
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    /**
     * @tests java.io.InputStream#available()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies that available() returns 0.",
        method = "available",
        args = {}
    )     
    public void test_available() throws IOException {
        assertEquals(is.available(), 0);
    }
    
    /**
     * @tests java.io.InputStream#close()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies close(). Since this method does nothing, this test is intentionally kept basic.",
        method = "close",
        args = {}
    )     
    public void test_close() {
        try {
            is.close();
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    /**
     * @tests java.io.InputStream#mark(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies mark(int). Since this method does nothing, this test is intentionally kept basic.",
        method = "mark",
        args = {int.class}
    )     
    public void test_markI() {
        try {
            is.mark(10);
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    /**
     * @tests java.io.InputStream#markSupported()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies that markSupported() returns false.",
        method = "markSupported",
        args = {}
    )     
    public void test_markSupported() throws IOException {
        assertFalse("markSupported() has returned the wrong default value.", 
                is.markSupported());
    }
    
    /**
     * @tests java.io.InputStream#read(byte[])
     */
    @TestTargets (
            { @TestTargetNew(
                    level = TestLevel.COMPLETE,
                    notes = "Verifies read(byte[]).",
                    method = "read",
                    args = {byte[].class}
              ),     
              @TestTargetNew(
                    level = TestLevel.COMPLETE,
                    notes = "Verifies ObjectInput.read(byte[]) since " +
                            "ObjectInputStream inherits the implementation " +
                            "of read(byte[]) from InputStream.",
                    clazz = ObjectInput.class,
                    method = "read",
                    args = {byte[].class}
              )
            }
        
    )
    public void test_read$B() throws IOException {
        byte[] b = new byte[10];
        byte[] ref = testString.getBytes();
        boolean equal = true;
        int bytesRead = 0;
        int i;
        
        // Test 1: This read operation should complete without an error.
        assertEquals("Test 1: Incorrect count of bytes read.", 
                is.read(b), 10);
    
        for (i = 0; i < 10; i++) {
            equal &= (b[i] == ref[i]);
        }
        assertTrue("Test 1: Wrong bytes read.", equal);
    
        // Test 2: Test that the correct number of bytes read is returned
        // if the source has less bytes available than fit in the buffer.
        b = new byte[ref.length];
        bytesRead = is.read(b); 
        assertEquals("Test 2: Incorrect count of bytes read.", 
                bytesRead, ref.length - 10);
    
        for (i = 0; i < bytesRead; i++) {
            equal &= (b[i] == ref[i + 10]);
        }
        assertTrue("Test 2: Wrong bytes read.", equal);
    
        // Test 3: Since we have now reached the end of the source,
        // the next call of read(byte[]) should return -1.
        bytesRead = is.read(b); 
        assertEquals("Test 3:", bytesRead, -1);
    }
            
    /**
     * @tests java.io.InputStream#read(byte[], int, int)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL,
        notes = "Verifies argument checking of read(byte[], int, int).",
        method = "read",
        args = {byte[].class, int.class, int.class}
    )     
    public void test_read$BII_IllegalArgument() throws IOException {
        byte[] b = new byte[10];
        int bytesRead = 0;
        
        // Test 1: Invalid offset.
        try {
            bytesRead = is.read(b, -1, 5);
            fail("Test 1: ArrayIndexOutOfBoundsException expected.");
        } catch (ArrayIndexOutOfBoundsException e) {
            // expected
        }

        // Test 2: Invalid length.
        try {
            bytesRead = is.read(b, 5, -1);
            fail("Test 2: ArrayIndexOutOfBoundsException expected.");
        } catch (ArrayIndexOutOfBoundsException e) {
            // expected
        }

        // Test 3: Invalid offset and length combination (sum is larger
        // than the length of b).
        try {
            bytesRead = is.read(b, 6, 5);
            fail("Test 3: ArrayIndexOutOfBoundsException expected.");
        } catch (ArrayIndexOutOfBoundsException e) {
            // expected
        }

        // Test 4: Border case.
        try {
            bytesRead = is.read(b, 6, 4);
        } catch (ArrayIndexOutOfBoundsException e) {
            fail("Test 4: Unexpected ArrayIndexOutOfBoundsException.");
        }
        assertEquals("Test 4:", bytesRead, 4);

        // Test 5: Border case.
        try {
            bytesRead = is.read(b, 6, 0);
        } catch (Exception e) {
            fail("Test 5: Unexpected Exception " + e.getMessage());
        }
        assertEquals("Test 5:", bytesRead, 0);
    }
        
    /**
     * @tests java.io.InputStream#read(byte[], int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies read(byte[], int, int).",
        method = "read",
        args = {byte[].class, int.class, int.class}
    )     
    public void test_read$BII() throws IOException {
        byte[] b = new byte[10];
        byte[] ref = testString.getBytes();
        boolean equal = true;
        int bytesRead = 0;
        int i;
        
        // Test 1: This read operation should complete without an error.
        assertEquals("Test 1: Incorrect count of bytes read.", 
                is.read(b, 0, 5), 5);
    
        for (i = 0; i < 5; i++) {
            equal &= (b[i] == ref[i]);
        }
        assertTrue("Test 1: Wrong bytes read.", equal);
    
        // Test 2: Read operation with offset.
        assertEquals("Test 2: Incorrect count of bytes read.", 
                is.read(b, 5, 3), 3);
    
        for (i = 5; i < 8; i++) {
            equal &= (b[i] == ref[i]);
        }
        assertTrue("Test 2: Wrong bytes read.", equal);

        // Test 3: Test that the correct number of bytes read is returned
        // if the source has less bytes available than fit in the buffer.
        b = new byte[ref.length];
        bytesRead = is.read(b, 2, b.length - 2); 
    
        // 8 bytes have been read during tests 1 and 2.
        assertEquals("Test 3: Incorrect count of bytes read.", 
                bytesRead, ref.length - 8);
    
        // The first two bytes of b should be 0 because of the offset.
        assertEquals("Test 3:", b[0], 0);
        assertEquals("Test 3:", b[1], 0);
        for (i = 0; i < bytesRead; i++) {
            equal &= (b[i + 2] == ref[i + 8]);
        }
        assertTrue("Test 2: Wrong bytes read.", equal);
    
        // Test 4: Since we have now reached the end of the source,
        // the next call of read(byte[], int, int) should return -1.
        assertEquals("Test 4:", is.read(b, 0, 2), -1); 
    }
    
    /**
     * @tests java.io.InputStream#reset()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies that reset() throws an IOException.",
        method = "reset",
        args = {}
    )     
    public void test_reset() {
        try {
            is.reset();
            fail("IOException expected.");
        } catch (IOException e) {
            // expected
        }
    }
    
    /**
     * @tests java.io.InputStream#skip(long)
     */
    @TestTargets (
            { @TestTargetNew(
                    level = TestLevel.COMPLETE,
                    notes = "Verifies skip(long).",
                    method = "skip",
                    args = {long.class}
              ),
              @TestTargetNew(
                    level = TestLevel.COMPLETE,
                    notes = "Verifies ObjectInput.skip(long) since " +
                            "ObjectInputStream inherits the implementation " +
                            "of skip(long) from InputStream.",
                    clazz = ObjectInput.class,
                    method = "skip",
                    args = {long.class}
              )
            }
    )     
    public void test_skipL() throws IOException {
        byte[] b = new byte[12];
        byte[] ref = testString.getBytes();
        int i;
        boolean equal = true;
        
        // Test 1: Check that skip(long) just returns 0 if called with a
        // negative number.
        assertEquals("Test 1:", is.skip(-42), 0);
        assertEquals("Test 1: Incorrect count of bytes read.", 
                is.read(b), 12);
        for (i = 0; i < 12; i++) {
            equal &= (b[i] == ref[i]);
        }
        assertTrue("Test 1: Wrong bytes read.", equal);
        
        // Test 2: Check if the number of bytes skipped matches the requested
        // number of bytes to skip.
        assertEquals("Test 2: Incorrect count of bytes skipped.", 
                is.skip(17), 17);
        
        // Test 3: Check that bytes have actually been skipped
        is.read(b, 0, 10);
        for (i = 0; i < 10; i++) {
            equal &= (b[i] == ref[i + 29]);
        }
        assertTrue("Test 3: Wrong bytes read.", equal);

        // Test 4: Test that the correct number of bytes skipped is returned
        // if the source has less bytes available than fit in the buffer.
        assertEquals("Test 4: Incorrect count of bytes skipped.", 
                is.skip(ref.length), ref.length - 39); 
    
        // Test 5: Since we have now reached the end of the source,
        // the next call of read(byte[]) should return -1 and calling
        // skip(long) should return 0.
        assertEquals("Test 5:", is.read(b), -1);
        assertEquals("Test 5:", is.skip(10), 0);
    }
    
    /**
     * This method is called before a test is executed. It creates a
     * MockInputStream instance.
     */
    protected void setUp() {
        is = new MockInputStream();
    }

    /**
     * This method is called after a test is executed. It closes the
     * MockInputStream instance.
     */
    protected void tearDown() {
        try {
            is.close();
        } catch (Exception e) {
        }
    }
}
