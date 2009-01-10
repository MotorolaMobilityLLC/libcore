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

// BEGIN android-note
// This test was copied from a newer version of Harmony
// END android-note

package org.apache.harmony.nio.tests.java.nio;

import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargetClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import junit.framework.TestCase;

@TestTargetClass(
    value = MappedByteBuffer.class,
    untestedMethods = {
        @TestTargetNew(
            level = TestLevel.NOT_FEASIBLE,
            method = "isLoaded",
            args = {}
        )
    }
)
public class MappedByteBufferTest extends TestCase {

    File tmpFile;
    
    /**
     * A regression test for failing to correctly set capacity of underlying
     * wrapped buffer from a mapped byte buffer.
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "A regression test for failing to correctly set capacity",
        method = "asIntBuffer",
        args = {}
    )
    public void test_asIntBuffer() throws IOException {
        // Map file
        FileInputStream fis = new FileInputStream(tmpFile);
        FileChannel fc = fis.getChannel();
        MappedByteBuffer mmb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc
                .size());
        int len = mmb.capacity();
        assertEquals("Got wrong number of bytes", 46, len); //$NON-NLS-1$

        // Read in our 26 bytes
        for (int i = 0; i < 26; i++) {
            byte b = mmb.get();
            assertEquals("Got wrong byte value", (byte) 'A' + i, b); //$NON-NLS-1$
        }

        // Now convert to an IntBuffer to read our ints
        IntBuffer ibuffer = mmb.asIntBuffer();
        for (int i = 0; i < 5; i++) {
            int val = ibuffer.get();
            assertEquals("Got wrong int value", i + 1, val); //$NON-NLS-1$
        }
        fc.close();
    }
    
    /**
     * @tests {@link java.nio.MappedByteBuffer#force()}
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "force",
        args = {}
    )
    public void test_force() throws IOException {
        // buffer was not mapped in read/write mode
        FileInputStream fileInputStream = new FileInputStream(tmpFile);
        FileChannel fileChannelRead = fileInputStream.getChannel();
        MappedByteBuffer mmbRead = fileChannelRead.map(MapMode.READ_ONLY, 0,
                fileChannelRead.size());

        mmbRead.force();

        FileInputStream inputStream = new FileInputStream(tmpFile);
        FileChannel fileChannelR = inputStream.getChannel();
        MappedByteBuffer resultRead = fileChannelR.map(MapMode.READ_ONLY, 0,
                fileChannelR.size());

        //If this buffer was not mapped in read/write mode, then invoking this method has no effect.
        assertEquals(
                "Invoking force() should have no effect when this buffer was not mapped in read/write mode",
                mmbRead, resultRead);

        // Buffer was mapped in read/write mode
        RandomAccessFile randomFile = new RandomAccessFile(tmpFile, "rw");
        FileChannel fileChannelReadWrite = randomFile.getChannel();
        MappedByteBuffer mmbReadWrite = fileChannelReadWrite.map(
                FileChannel.MapMode.READ_WRITE, 0, fileChannelReadWrite.size());

        mmbReadWrite.put((byte) 'o');
        mmbReadWrite.force();

        RandomAccessFile random = new RandomAccessFile(tmpFile, "rw");
        FileChannel fileChannelRW = random.getChannel();
        MappedByteBuffer resultReadWrite = fileChannelRW.map(
                FileChannel.MapMode.READ_WRITE, 0, fileChannelRW.size());

        // Invoking force() will change the buffer
        assertFalse(mmbReadWrite.equals(resultReadWrite));
        
        fileChannelRead.close();
        fileChannelR.close();
        fileChannelReadWrite.close();
        fileChannelRW.close();
    }

    /**
     * @tests {@link java.nio.MappedByteBuffer#load()}
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "load",
        args = {}
    )
    public void test_load() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(tmpFile);
        FileChannel fileChannelRead = fileInputStream.getChannel();
        MappedByteBuffer mmbRead = fileChannelRead.map(MapMode.READ_ONLY, 0,
                fileChannelRead.size());

        assertEquals(mmbRead, mmbRead.load());

        RandomAccessFile randomFile = new RandomAccessFile(tmpFile, "rw");
        FileChannel fileChannelReadWrite = randomFile.getChannel();
        MappedByteBuffer mmbReadWrite = fileChannelReadWrite.map(
                FileChannel.MapMode.READ_WRITE, 0, fileChannelReadWrite.size());

        assertEquals(mmbReadWrite, mmbReadWrite.load());

        fileChannelRead.close();
        fileChannelReadWrite.close();
    }

    protected void setUp() throws IOException {
        // Create temp file with 26 bytes and 5 ints
        tmpFile = new File(System.getProperty("ctsdir"), "MappedByteBufferTest");  //$NON-NLS-1$//$NON-NLS-2$
        tmpFile.createNewFile();
        tmpFile.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
        FileChannel fileChannel = fileOutputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(26 + 20);
        for (int i = 0; i < 26; i++) {
            byteBuffer.put((byte) ('A' + i));
        }
        for (int i = 0; i < 5; i++) {
            byteBuffer.putInt(i + 1);
        }
        byteBuffer.rewind();
        fileChannel.write(byteBuffer);
        fileChannel.close();
        fileOutputStream.close();
    }
}