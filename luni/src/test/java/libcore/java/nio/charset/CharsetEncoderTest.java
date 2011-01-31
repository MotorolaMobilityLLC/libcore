/*
 * Copyright (C) 2009 The Android Open Source Project
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

package libcore.java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

public class CharsetEncoderTest extends junit.framework.TestCase {
    // None of the harmony or jtreg tests actually check that replaceWith does the right thing!
    public void test_replaceWith() throws Exception {
        Charset ascii = Charset.forName("US-ASCII");
        CharsetEncoder e = ascii.newEncoder();
        e.onMalformedInput(CodingErrorAction.REPLACE);
        e.onUnmappableCharacter(CodingErrorAction.REPLACE);
        e.replaceWith("=".getBytes("US-ASCII"));
        String input = "hello\u0666world";
        String output = ascii.decode(e.encode(CharBuffer.wrap(input))).toString();
        assertEquals("hello=world", output);
    }

    private void assertReplacementBytesForEncoder(String charset, byte[] bytes) {
        byte[] result = Charset.forName(charset).newEncoder().replacement();
        assertEquals(Arrays.toString(bytes), Arrays.toString(result));
    }

    // For all the guaranteed built-in charsets, check that we have the right default replacements.
    public void test_defaultReplacementBytesIso_8859_1() throws Exception {
        assertReplacementBytesForEncoder("ISO-8859-1", new byte[] { (byte) '?' });
    }
    public void test_defaultReplacementBytesUs_Ascii() throws Exception {
        assertReplacementBytesForEncoder("US-ASCII", new byte[] { (byte) '?' });
    }
    public void test_defaultReplacementBytesUtf_16() throws Exception {
        assertReplacementBytesForEncoder("UTF-16", new byte[] { (byte) 0xff, (byte) 0xfd });
    }
    public void test_defaultReplacementBytesUtf_16be() throws Exception {
        assertReplacementBytesForEncoder("UTF-16BE", new byte[] { (byte) 0xff, (byte) 0xfd });
    }
    public void test_defaultReplacementBytesUtf_16le() throws Exception {
        assertReplacementBytesForEncoder("UTF-16LE", new byte[] { (byte) 0xfd, (byte) 0xff });
    }
    public void test_defaultReplacementBytesUtf_8() throws Exception {
        assertReplacementBytesForEncoder("UTF-8", new byte[] { (byte) '?' });
    }

    public void testBrokenByDesign() throws Exception {
        Charset cs = Charset.forName("UTF-32BE");
        CharsetEncoder e;
        ByteBuffer bb;
        CoderResult cr;

        // okay: surrogate pair seen all at once is decoded to U+20b9f.
        e = cs.newEncoder();
        bb = ByteBuffer.allocate(128);
        cr = e.encode(CharBuffer.wrap(new char[] { '\ud842', '\udf9f' }), bb, false);
        assertEquals(CoderResult.UNDERFLOW, cr);
        assertEquals(4, bb.position());
        assertEquals((byte) 0x00, bb.get(0));
        assertEquals((byte) 0x02, bb.get(1));
        assertEquals((byte) 0x0b, bb.get(2));
        assertEquals((byte) 0x9f, bb.get(3));

        // malformed: low surrogate first.
        e = cs.newEncoder();
        bb = ByteBuffer.allocate(128);
        cr = e.encode(CharBuffer.wrap(new char[] { '\udf9f' }), bb, false);
        assertTrue(cr.toString(), cr.isMalformed());
        assertEquals(1, cr.length());

        // retarded: writing the surrogate pair in separate writes is an error because the
        // CharsetEncoder doesn't remember its state! So IGNORE just ignores both characters,
        // REPORT complains that the second is invalid (because it doesn't remember seeing the
        // first), and REPLACE inserts a replacement character U+fffd when it sees the second
        // character (because it doesn't remember seeing the first).
        CodingErrorAction[] ceas = new CodingErrorAction[] {
            CodingErrorAction.IGNORE, CodingErrorAction.REPORT, CodingErrorAction.REPLACE
        };
        for (CodingErrorAction cea : ceas) {
            System.err.println(cea);
            e = cs.newEncoder();
            e.onMalformedInput(cea);
            e.onUnmappableCharacter(cea);
            bb = ByteBuffer.allocate(128);
            cr = e.encode(CharBuffer.wrap(new char[] { '\ud842' }), bb, false);
            assertEquals(CoderResult.UNDERFLOW, cr);
            assertEquals(0, bb.position());
            cr = e.encode(CharBuffer.wrap(new char[] { '\udf9f' }), bb, false);
            if (cea == CodingErrorAction.REPORT) {
                assertTrue(cr.toString(), cr.isMalformed());
                assertEquals(1, cr.length());
                continue;
            }
            assertEquals(CoderResult.UNDERFLOW, cr);
            int expectedPosition = 0;
            if (cea == CodingErrorAction.REPLACE) {
                expectedPosition = 4;
                assertEquals((byte) 0x00, bb.get(0));
                assertEquals((byte) 0x00, bb.get(1));
                assertEquals((byte) 0xff, bb.get(2));
                assertEquals((byte) 0xfd, bb.get(3));
            }
            assertEquals(expectedPosition, bb.position());
            cr = e.encode(CharBuffer.wrap(new char[] { }), bb, true);
            assertEquals(CoderResult.UNDERFLOW, cr);
            assertEquals(expectedPosition, bb.position());
            cr = e.flush(bb);
            assertEquals(CoderResult.UNDERFLOW, cr);
            assertEquals(expectedPosition, bb.position());
        }
    }
}
