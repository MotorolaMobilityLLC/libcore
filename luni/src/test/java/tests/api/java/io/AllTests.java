/*
 * Copyright (C) 2007 The Android Open Source Project
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

package tests.api.java.io;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * This is autogenerated source file. Includes tests for package tests.api.java.io;
 */

public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite("All tests for package tests.api.java.io;");
        // $JUnit-BEGIN$

        suite.addTestSuite(BufferedInputStreamTest.class);
        suite.addTestSuite(BufferedOutputStreamTest.class);
        suite.addTestSuite(BufferedReaderTest.class);
        suite.addTestSuite(BufferedWriterTest.class);
        suite.addTestSuite(ByteArrayInputStreamTest.class);
        suite.addTestSuite(ByteArrayOutputStreamTest.class);
        suite.addTestSuite(CharArrayReaderTest.class);
        suite.addTestSuite(CharArrayWriterTest.class);
        suite.addTestSuite(CharConversionExceptionTest.class);
        suite.addTestSuite(ComputeSerialVersionUIDTest.class);
        suite.addTestSuite(DataInputStreamTest.class);
        suite.addTestSuite(DataInputOutputStreamTest.class);
        suite.addTestSuite(DataOutputStreamTest.class);
        suite.addTestSuite(EOFExceptionTest.class);
        suite.addTestSuite(FileDescriptorTest.class);
        suite.addTestSuite(FileInputStreamTest.class);
        suite.addTestSuite(FileNotFoundExceptionTest.class);
        suite.addTestSuite(FileOutputStreamTest.class);
        suite.addTestSuite(FilePermissionTest.class);
        suite.addTestSuite(FileReaderTest.class);
        suite.addTestSuite(FileTest.class);
        suite.addTestSuite(FileWriterTest.class);
        suite.addTestSuite(FilterInputStreamTest.class);
        suite.addTestSuite(FilterOutputStreamTest.class);
        suite.addTestSuite(FilterReaderTest.class);
        suite.addTestSuite(FilterWriterTest.class);
        suite.addTestSuite(IOExceptionTest.class);
        suite.addTestSuite(InputStreamReaderTest.class);
        suite.addTestSuite(InterruptedIOExceptionTest.class);
        suite.addTestSuite(InvalidClassExceptionTest.class);
        suite.addTestSuite(InvalidObjectExceptionTest.class);
        suite.addTestSuite(LineNumberInputStreamTest.class);
        suite.addTestSuite(LineNumberReaderTest.class);
        suite.addTestSuite(NotActiveExceptionTest.class);
        suite.addTestSuite(NotSerializableExceptionTest.class);
        suite.addTestSuite(ObjectInputStreamTest.class);
        suite.addTestSuite(ObjectInputStreamGetFieldTest.class);
        suite.addTestSuite(ObjectInputOutputStreamTest.class);
        suite.addTestSuite(ObjectOutputStreamTest.class);
        suite.addTestSuite(ObjectOutputStreamPutFieldTest.class);
        suite.addTestSuite(ObjectStreamClassTest.class);
        suite.addTestSuite(ObjectStreamExceptionTest.class);
        suite.addTestSuite(ObjectStreamFieldTest.class);
        suite.addTestSuite(OpenRandomFileTest.class);
        suite.addTestSuite(OutputStreamTest.class);
        suite.addTestSuite(OutputStreamWriterTest.class);
        suite.addTestSuite(PipedInputStreamTest.class);
        suite.addTestSuite(PipedOutputStreamTest.class);
        suite.addTestSuite(PipedReaderTest.class);
        suite.addTestSuite(PipedWriterTest.class);
        suite.addTestSuite(PrintStreamTest.class);
        suite.addTestSuite(PrintWriterTest.class);
        suite.addTestSuite(PushbackInputStreamTest.class);
        suite.addTestSuite(PushbackReaderTest.class);
        suite.addTestSuite(RandomAccessFileTest.class);
        suite.addTestSuite(SequenceInputStreamTest.class);
        suite.addTestSuite(SerializablePermissionTest.class);
        suite.addTestSuite(StreamCorruptedExceptionTest.class);
        suite.addTestSuite(StreamTokenizerTest.class);
        suite.addTestSuite(StringBufferInputStreamTest.class);
        suite.addTestSuite(StringReaderTest.class);
        suite.addTestSuite(StringWriterTest.class);
        suite.addTestSuite(SyncFailedExceptionTest.class);
        suite.addTestSuite(UTFDataFormatExceptionTest.class);
        suite.addTestSuite(UnsupportedEncodingExceptionTest.class);
        suite.addTestSuite(WriteAbortedExceptionTest.class);
        suite.addTestSuite(WriterTest.class);

        // $JUnit-END$
        return suite;
    }
}
