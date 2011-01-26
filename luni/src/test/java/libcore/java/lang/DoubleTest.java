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

package libcore.java.lang;

import junit.framework.TestCase;

public class DoubleTest extends TestCase {
    public void testDoubleToStringUnsignedDivide() throws Exception {
        // http://b/3238333
        assertEquals("0.008", Double.toString(0.008));
        assertEquals("0.008366", Double.toString(0.008366));
        // http://code.google.com/p/android/issues/detail?id=14033
        assertEquals("0.009", Double.toString(0.009));
        // http://code.google.com/p/android/issues/detail?id=14302
        assertEquals("0.008567856012638986", Double.toString(0.008567856012638986));
        assertEquals("0.010206713752229896", Double.toString(0.010206713752229896));
    }

    public void testNamedDoubles() throws Exception {
        assertEquals(Double.NaN, Double.parseDouble("NaN"));
        assertEquals(Double.NaN, Double.parseDouble("-NaN"));
        assertEquals(Double.NaN, Double.parseDouble("+NaN"));
        try {
            Double.parseDouble("NNaN");
            fail();
        } catch (NumberFormatException expected) {
        }
        try {
            Double.parseDouble("NaNN");
            fail();
        } catch (NumberFormatException expected) {
        }

        assertEquals(Double.POSITIVE_INFINITY, Double.parseDouble("+Infinity"));
        assertEquals(Double.POSITIVE_INFINITY, Double.parseDouble("Infinity"));
        assertEquals(Double.NEGATIVE_INFINITY, Double.parseDouble("-Infinity"));
        try {
            Double.parseDouble("IInfinity");
            fail();
        } catch (NumberFormatException expected) {
        }
        try {
            Double.parseDouble("Infinityy");
            fail();
        } catch (NumberFormatException expected) {
        }
    }
}
