/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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
package test.java.math.BigDecimal;

/*
 * @test
 * @bug 6806261
 * @summary Tests of BigDecimal.longValueExact
 */

import java.math.*;

import org.testng.Assert;
import org.testng.annotations.Test;

// Android-changed: Replace error counting with asserts.
public class LongValueExactTests {

    @Test
    public static void longValueExactTests() {
        String[] testStrings = {
            "9223372036854775807",
            "9223372036854775807.0",
            "9223372036854775807.00",
            "-9223372036854775808",
            "-9223372036854775808.0",
            "-9223372036854775808.00",
        };

        for (String longValue : testStrings) {
            try {
                BigDecimal bd = new BigDecimal(longValue);
                long longValueExact = bd.longValueExact();
            } catch (Exception e) {
                Assert.fail("Unexpected exception for longValueExact(" + longValue + "): " + e.getMessage());
            }
        }

        // The following Strings are supposed to make longValueExact throw
        // ArithmeticException.
        String[] testStrings2 = {
            "9223372036854775808",
            "9223372036854775808.0",
            "9223372036854775808.00",
            "-9223372036854775809",
            "-9223372036854775808.1",
            "-9223372036854775808.01",
        };

        for (String bigValue : testStrings2) {
            try {
                BigDecimal bd = new BigDecimal(bigValue);
                long longValueExact = bd.longValueExact();
                Assert.fail("Unexpectedly no throw for longValueExact(" + bigValue + ")");
            } catch (ArithmeticException e) {
                // Success;
            }
        }
    }
}
