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

package java.io;

// BEGIN android-added
import org.apache.harmony.luni.util.Msg;
// ENd android-added

/**
 * A specialized {@link Writer} that writes characters to a {@code StringBuffer}
 * in a sequential manner, appending them in the process. The result can later
 * be queried using the {@link #StringWriter(int)} or {@link #toString()}
 * methods.
 * 
 * @see StringReader
 * 
 * @since Android 1.0
 */
public class StringWriter extends Writer {

    private StringBuffer buf;

    /**
     * Constructs a new {@code StringWriter} which has a {@link StringBuffer}
     * allocated with the default size of 16 characters. The {@code
     * StringBuffer} is also the {@code lock} used to synchronize access to this
     * writer.
     * 
     * @since Android 1.0
     */
    public StringWriter() {
        super();
        buf = new StringBuffer(16);
        lock = buf;
    }

    /**
     * Constructs a new {@code StringWriter} which has a {@link StringBuffer}
     * allocated with a size of {@code initialSize} characters. The {@code
     * StringBuffer} is also the {@code lock} used to synchronize access to this
     * writer.
     * 
     * @param initialSize
     *            the intial size of the target string buffer.
     * @since Android 1.0
     */
    public StringWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException();
        }
        buf = new StringBuffer(initialSize);
        lock = buf;
    }

    /**
     * Calling this method has no effect. In contrast to most {@code Writer} subclasses,
     * the other methods in {@code StringWriter} do not throw an {@code IOException} if
     * {@code close()} has been called.
     * 
     * @throws IOException
     *             if an error occurs while closing this writer.
     * @since Android 1.0
     */
    @Override
    public void close() throws IOException {
        /* empty */
    }

    /**
     * Calling this method has no effect.
     * 
     * @since Android 1.0
     */
    @Override
    public void flush() {
        /* empty */
    }

    /**
     * Gets a reference to this writer's internal {@link StringBuffer}. Any
     * changes made to the returned buffer are reflected in this writer.
     * 
     * @return a reference to this writer's internal {@code StringBuffer}.
     * @since Android 1.0
     */
    public StringBuffer getBuffer() {
        synchronized (lock) {
            return buf;
        }
    }

    /**
     * Gets a copy of the contents of this writer as a string.
     * 
     * @return this writer's contents as a string.
     * @since Android 1.0
     */
    @Override
    public String toString() {
        synchronized (lock) {
            return buf.toString();
        }
    }

    /**
     * Writes {@code count} characters starting at {@code offset} in {@code buf}
     * to this writer's {@code StringBuffer}.
     * 
     * @param cbuf
     *            the non-null character array to write.
     * @param offset
     *            the index of the first character in {@code cbuf} to write.
     * @param count
     *            the maximum number of characters to write.
     * @throws IndexOutOfBoundsException
     *             if {@code offset < 0} or {@code count < 0}, or if {@code
     *             offset + count} is greater than the size of {@code buf}.
     * @since Android 1.0
     */
    @Override
    public void write(char[] cbuf, int offset, int count) {
        // avoid int overflow
        // BEGIN android-changed
        // Exception priorities (in case of multiple errors) differ from
        // RI, but are spec-compliant.
        // removed redundant check, added null check, used (offset | count) < 0
        // instead of (offset < 0) || (count < 0) to safe one operation
        if (cbuf == null) {
            throw new NullPointerException(Msg.getString("K0047")); //$NON-NLS-1$
        }
        if ((offset | count) < 0 || count > cbuf.length - offset) {
            throw new IndexOutOfBoundsException(Msg.getString("K002f")); //$NON-NLS-1$
        }
        // END android-changed
        synchronized (lock) {
            this.buf.append(cbuf, offset, count);
        }
    }

    /**
     * Writes one character to this writer's {@code StringBuffer}. Only the two
     * least significant bytes of the integer {@code oneChar} are written.
     * 
     * @param oneChar
     *            the character to write to this writer's {@code StringBuffer}.
     * @since Android 1.0
     */
    @Override
    public void write(int oneChar) {
        synchronized (lock) {
            buf.append((char) oneChar);
        }
    }

    /**
     * Writes the characters from the specified string to this writer's {@code
     * StringBuffer}.
     * 
     * @param str
     *            the non-null string containing the characters to write.
     * @since Android 1.0
     */
    @Override
    public void write(String str) {
        synchronized (lock) {
            buf.append(str);
        }
    }

    /**
     * Writes {@code count} characters from {@code str} starting at {@code
     * offset} to this writer's {@code StringBuffer}.
     * 
     * @param str
     *            the non-null string containing the characters to write.
     * @param offset
     *            the index of the first character in {@code str} to write.
     * @param count
     *            the number of characters from {@code str} to write.
     * @throws StringIndexOutOfBoundsException
     *             if {@code offset < 0} or {@code count < 0}, or if {@code
     *             offset + count} is greater than the length of {@code str}.
     * @since Android 1.0
     */
    @Override
    public void write(String str, int offset, int count) {
        String sub = str.substring(offset, offset + count);
        synchronized (lock) {
            buf.append(sub);
        }
    }

    /**
     * Appends the character {@code c} to this writer's {@code StringBuffer}.
     * This method works the same way as {@link #write(int)}.
     * 
     * @param c
     *            the character to append to the target stream.
     * @return this writer.
     * @since Android 1.0
     */
    @Override
    public StringWriter append(char c) {
        write(c);
        return this;
    }

    /**
     * Appends the character sequence {@code csq} to this writer's {@code
     * StringBuffer}. This method works the same way as {@code
     * StringWriter.write(csq.toString())}. If {@code csq} is {@code null}, then
     * the string "null" is written to the target stream.
     * 
     * @param csq
     *            the character sequence appended to the target.
     * @return this writer.
     * @since Android 1.0
     */
    @Override
    public StringWriter append(CharSequence csq) {
        if (null == csq) {
            append(TOKEN_NULL, 0, TOKEN_NULL.length());
        } else {
            append(csq, 0, csq.length());
        }
        return this;
    }

    /**
     * Appends a subsequence of the character sequence {@code csq} to this
     * writer's {@code StringBuffer}. This method works the same way as {@code
     * StringWriter.writer(csq.subsequence(start, end).toString())}. If {@code
     * csq} is {@code null}, then the specified subsequence of the string "null"
     * will be written to the target.
     * 
     * @param csq
     *            the character sequence appended to the target.
     * @param start
     *            the index of the first char in the character sequence appended
     *            to the target.
     * @param end
     *            the index of the character following the last character of the
     *            subsequence appended to the target.
     * @return this writer.
     * @throws StringIndexOutOfBoundsException
     *             if {@code start > end}, {@code start < 0}, {@code end < 0} or
     *             either {@code start} or {@code end} are greater or equal than
     *             the length of {@code csq}.
     * @since Android 1.0
     */
    @Override
    public StringWriter append(CharSequence csq, int start, int end) {
        if (null == csq) {
            csq = TOKEN_NULL;
        }
        String output = csq.subSequence(start, end).toString();
        write(output, 0, output.length());
        return this;
    }
}
