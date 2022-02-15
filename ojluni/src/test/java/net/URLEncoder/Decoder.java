/*
 * Copyright (c) 2001, 2002, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test
 * @bug 4407610
 * @summary java.net.URLDecode.decode(st,"UTF-16")
 */
package test.java.net.URLEncoder;

import java.net.*;
import org.testng.annotations.Test;
import org.testng.Assert;

public class Decoder {

    @Test
    public void testDecoder() throws Exception {

        String enc = "UTF-16";
        String strings[] = {
                "\u0100\u0101",
                "\u0100 \u0101",
                "\u0100 \u0101\u0102",
                "\u0100 \u0101 \u0102",
                "\u0100C\u0101 \u0102",
                "\u0100\u0101\u0102",
                "?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&?&",
                "foobar",
                "foo?bar"
        };

        for (int i = 0; i < strings.length; i++) {
            String encoded = URLEncoder.encode(strings[i], enc);
            String decoded = URLDecoder.decode(encoded, enc);
            Assert.assertEquals(strings[i], (decoded));
        }
    }
}