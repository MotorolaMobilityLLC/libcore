/*
 * Copyright (C) 2010 The Android Open Source Project
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

package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.TreeSet;

public class CharsetTest extends junit.framework.TestCase {
    public void test_charsetsAvailable() throws Exception {
        // All Java implementations must support these charsets.
        assertNotNull(Charset.forName("ISO-8859-1"));
        assertNotNull(Charset.forName("US-ASCII"));
        assertNotNull(Charset.forName("UTF-16"));
        assertNotNull(Charset.forName("UTF-16BE"));
        assertNotNull(Charset.forName("UTF-16LE"));
        assertNotNull(Charset.forName("UTF-8"));
    }

    public void test_UTF_32BE() throws Exception {
        // Writes big-endian, with no BOM.
        assertEncodes(Charset.forName("UTF-32BE"), "a\u0666", 0, 0, 0, 'a', 0, 0, 0x06, 0x66);
        // Treats a little-endian BOM as an error and continues to read big-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(Charset.forName("UTF-32BE"), "\ufffda\u0666", 0xff, 0xfe, 0, 0, 0, 0, 0, 'a', 0, 0, 0x06, 0x66);
        // Accepts a big-endian BOM and swallows the BOM.
        assertDecodes(Charset.forName("UTF-32BE"), "a\u0666", 0, 0, 0xfe, 0xff, 0, 0, 0, 'a', 0, 0, 0x06, 0x66);
        // Defaults to reading big-endian.
        assertDecodes(Charset.forName("UTF-32BE"), "a\u0666", 0, 0, 0, 'a', 0, 0, 0x06, 0x66);
    }

    public void test_UTF_32LE() throws Exception {
        // Writes little-endian, with no BOM.
        assertEncodes(Charset.forName("UTF-32LE"), "a\u0666", 'a', 0, 0, 0, 0x66, 0x06, 0, 0);
        // Accepts a little-endian BOM and swallows the BOM.
        assertDecodes(Charset.forName("UTF-32LE"), "a\u0666", 0xff, 0xfe, 0, 0, 'a', 0, 0, 0, 0x66, 0x06, 0, 0);
        // Treats a big-endian BOM as an error and continues to read little-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(Charset.forName("UTF-32LE"), "\ufffda\u0666", 0, 0, 0xfe, 0xff, 'a', 0, 0, 0, 0x66, 0x06, 0, 0);
        // Defaults to reading little-endian.
        assertDecodes(Charset.forName("UTF-32LE"), "a\u0666", 'a', 0, 0, 0, 0x66, 0x06, 0, 0);
    }

    public void test_UTF_32() throws Exception {
        // Writes big-endian, with no BOM.
        assertEncodes(Charset.forName("UTF-32"), "a\u0666", 0, 0, 0, 'a', 0, 0, 0x06, 0x66);
        // Reads whatever the BOM tells it to read...
        assertDecodes(Charset.forName("UTF-32"), "a\u0666", 0, 0, 0xfe, 0xff, 0, 0, 0, 'a', 0, 0, 0x06, 0x66);
        assertDecodes(Charset.forName("UTF-32"), "a\u0666", 0xff, 0xfe, 0, 0, 'a', 0, 0, 0, 0x66, 0x06, 0, 0);
        // ...but defaults to reading big-endian if there's no BOM.
        assertDecodes(Charset.forName("UTF-32"), "a\u0666", 0, 0, 0, 'a', 0, 0, 0x06, 0x66);
    }

    public void test_UTF_16BE() throws Exception {
        // Writes big-endian, with no BOM.
        assertEncodes(Charset.forName("UTF-16BE"), "a\u0666", 0, 'a', 0x06, 0x66);
        // Treats a little-endian BOM as an error and continues to read big-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(Charset.forName("UTF-16BE"), "\ufffda\u0666", 0xff, 0xfe, 0, 'a', 0x06, 0x66);
        // Accepts a big-endian BOM and includes U+FEFF in the decoded output.
        assertDecodes(Charset.forName("UTF-16BE"), "\ufeffa\u0666", 0xfe, 0xff, 0, 'a', 0x06, 0x66);
        // Defaults to reading big-endian.
        assertDecodes(Charset.forName("UTF-16BE"), "a\u0666", 0, 'a', 0x06, 0x66);
    }

    public void test_UTF_16LE() throws Exception {
        // Writes little-endian, with no BOM.
        assertEncodes(Charset.forName("UTF-16LE"), "a\u0666", 'a', 0, 0x66, 0x06);
        // Accepts a little-endian BOM and includes U+FEFF in the decoded output.
        assertDecodes(Charset.forName("UTF-16LE"), "\ufeffa\u0666", 0xff, 0xfe, 'a', 0, 0x66, 0x06);
        // Treats a big-endian BOM as an error and continues to read little-endian.
        // This test uses REPLACE mode, so we get the U+FFFD replacement character in the result.
        assertDecodes(Charset.forName("UTF-16LE"), "\ufffda\u0666", 0xfe, 0xff, 'a', 0, 0x66, 0x06);
        // Defaults to reading little-endian.
        assertDecodes(Charset.forName("UTF-16LE"), "a\u0666", 'a', 0, 0x66, 0x06);
    }

    public void test_UTF_16() throws Exception {
        // Writes big-endian, with a big-endian BOM.
        assertEncodes(Charset.forName("UTF-16"), "a\u0666", 0xfe, 0xff, 0, 'a', 0x06, 0x66);
        // Reads whatever the BOM tells it to read...
        assertDecodes(Charset.forName("UTF-16"), "a\u0666", 0xfe, 0xff, 0, 'a', 0x06, 0x66);
        assertDecodes(Charset.forName("UTF-16"), "a\u0666", 0xff, 0xfe, 'a', 0, 0x66, 0x06);
        // ...but defaults to reading big-endian if there's no BOM.
        assertDecodes(Charset.forName("UTF-16"), "a\u0666", 0, 'a', 0x06, 0x66);
    }

    private byte[] toByteArray(int[] ints) {
        byte[] result = new byte[ints.length];
        for (int i = 0; i < ints.length; ++i) {
            result[i] = (byte) ints[i];
        }
        return result;
    }

    private void assertEncodes(Charset cs, String s, int... expectedByteInts) throws Exception {
        ByteBuffer out = cs.encode(s);
        byte[] bytes = new byte[out.remaining()];
        out.get(bytes);
        assertEquals(Arrays.toString(toByteArray(expectedByteInts)), Arrays.toString(bytes));
    }

    private void assertDecodes(Charset cs, String s, int... byteInts) throws Exception {
        ByteBuffer in = ByteBuffer.wrap(toByteArray(byteInts));
        CharBuffer out = cs.decode(in);
        assertEquals(s, out.toString());
    }
}
