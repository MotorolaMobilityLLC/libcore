/*
 * Copyright (C) 2016 The Android Open Source Project
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
 * limitations under the License
 */

package libcore.java.util;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class HashtableTest extends junit.framework.TestCase {

    public void test_getOrDefault() {
        MapDefaultMethodTester.test_getOrDefault(new Hashtable<>(), false /*doesNotAcceptNullKey*/,
                false /*doesNotAcceptNullValue*/, true /*getAcceptsAnyObject*/);
    }

    public void test_forEach() {
        MapDefaultMethodTester.test_forEach(new Hashtable<>());
    }

    public void test_putIfAbsent() {
        MapDefaultMethodTester.test_putIfAbsent(new Hashtable<>(), false /*doesNotAcceptNullKey*/,
                false /*doesNotAcceptNullValue*/);
    }

    public void test_remove() {
        MapDefaultMethodTester.test_remove(new Hashtable<>(), false /*doesNotAcceptNullKey*/,
                false /*doesNotAcceptNullValue*/);
    }

    public void test_replace$K$V$V() {
        MapDefaultMethodTester.
                test_replace$K$V$V(new Hashtable<>(), false /*doesNotAcceptNullKey*/,
                        false /*doesNotAcceptNullValue*/);
    }

    public void test_replace$K$V() {
        MapDefaultMethodTester.test_replace$K$V(new Hashtable<>(), false /*doesNotAcceptNullKey*/,
                false /*doesNotAcceptNullValue*/);
    }

    public void test_computeIfAbsent() {
        MapDefaultMethodTester.test_computeIfAbsent(new Hashtable<>(),
                false /*doesNotAcceptNullKey*/, false /*doesNotAcceptNullValue*/);
    }

    public void test_computeIfPresent() {
        MapDefaultMethodTester.test_computeIfPresent(new Hashtable<>(),
                false /*doesNotAcceptNullKey*/);
    }

    public void test_compute() {
        MapDefaultMethodTester.test_compute(new Hashtable<>(), false /*doesNotAcceptNullKey*/);
    }

    public void test_merge() {
        MapDefaultMethodTester.test_merge(new Hashtable<>(), false /*doesNotAcceptNullKey*/);
    }

    public void test_replaceAll() throws Exception {
        Hashtable<String, String> ht = new Hashtable<>();
        ht.put("one", "1");
        ht.put("two", "2");
        ht.put("three", "3");

        ht.replaceAll((k, v) -> k + v);
        assertEquals("one1", ht.get("one"));
        assertEquals("two2", ht.get("two"));
        assertEquals("three3", ht.get("three"));
        assertEquals(3, ht.size());

        try {
            ht.replaceAll(new java.util.function.BiFunction<String, String, String>() {
                @Override
                public String apply(String k, String v) {
                    ht.put("foo", v);
                    return v;
                }
            });
            fail();
        } catch(ConcurrentModificationException expected) {}

        try {
            ht.replaceAll(null);
            fail();
        } catch(NullPointerException expected) {}

        try {
            ht.replaceAll((k, v) -> null);
        } catch (NullPointerException expected) {}
    }


    /**
     * Check that {@code Hashtable.Entry} compiles and refers to
     * {@link java.util.Map.Entry}, which is required for source
     * compatibility with earlier versions of Android.
     * If this test fails to compile, the code under test is broken.
     */
    public void test_entryCompatibility_compiletime() {
        Class c = Hashtable.Entry.class;
        assertEquals("java.util.Map$Entry", c.getName());
        assertEquals(Map.Entry.class, c);
    }

    /**
     * Checks that there is no nested class named 'Entry' in Hashtable.
     * If {@link #test_entryCompatibility_compiletime()} passes but
     * this test fails, then the test was probably compiled against a
     * version of Hashtable that does not have a nested Entry class,
     * but run against a version that does.
     */
    public void test_entryCompatibility_runtime() {
        String forbiddenClassName = "java.util.Hashtable$Entry";
        try {
            Class.forName(forbiddenClassName);
            fail("Class " + forbiddenClassName + " should not exist");
        } catch (ClassNotFoundException expected) {
            // pass
        }
    }
}
