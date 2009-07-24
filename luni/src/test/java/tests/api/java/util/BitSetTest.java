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

package tests.api.java.util;

import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass; 

import java.util.BitSet;

@TestTargetClass(BitSet.class) 
public class BitSetTest extends junit.framework.TestCase {

    BitSet eightbs;

    /**
     * @tests java.util.BitSet#BitSet()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "BitSet",
        args = {}
    )
    public void test_Constructor() {
        // Test for method java.util.BitSet()
        BitSet bs = new BitSet();
        // Default size for a BitSet should be 64 elements;

        assertEquals("Created BitSet of incorrect size", 64, bs.size());
        assertEquals("New BitSet had invalid string representation", "{}", bs
                .toString());
    }

    /**
     * @tests java.util.BitSet#BitSet(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "BitSet",
        args = {int.class}
    )
    public void test_ConstructorI() {
        // Test for method java.util.BitSet(int)
        BitSet bs = new BitSet(128);
        // Default size for a BitSet should be 64 elements;

        assertEquals("Created BitSet of incorrect size", 128, bs.size());
        assertTrue("New BitSet had invalid string representation: "
                + bs.toString(), bs.toString().equals("{}"));

        // All BitSets are created with elements of multiples of 64

        bs = new BitSet(89);
        assertEquals("Failed to round BitSet element size", 128, bs.size());

        try {
            bs = new BitSet(-9);
            fail(
                    "Failed to throw exception when creating a new BitSet with negative elements value");
        } catch (NegativeArraySizeException e) {
            // Correct behaviour
        }
    }

    /**
     * @tests java.util.BitSet#clone()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "clone",
        args = {}
    )
    public void test_clone() {
        // Test for method java.lang.Object java.util.BitSet.clone()
        BitSet bs;
        bs = (BitSet) eightbs.clone();
        assertTrue("Clone failed to return equal BitSet", eightbs.equals(bs));

    }

    /**
     * @tests java.util.BitSet#equals(java.lang.Object)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "equals",
        args = {java.lang.Object.class}
    )
    public void test_equalsLjava_lang_Object() {
        // Test for method boolean java.util.BitSet.equals(java.lang.Object)
        BitSet bs;

        bs = (BitSet) eightbs.clone();
        assertTrue("Same BitSet returned false", eightbs.equals(eightbs));
        assertTrue("Identical BitSets returned false", eightbs.equals(bs));
        bs.clear(6);
        assertTrue("Different BitSets returned true", !eightbs.equals(bs));
        // Grow the BitSet
        bs = (BitSet) eightbs.clone();
        bs.set(128);
        assertTrue("Different sized BitSet with higher bit set returned true",
                !eightbs.equals(bs));
        bs.clear(128);
        assertTrue(
                "Different sized BitSet with higher bits not set returned false",
                eightbs.equals(bs));
    }

    /**
     * @tests java.util.BitSet#hashCode()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "hashCode",
        args = {}
    )
    public void test_hashCode() {
        // Test for method int java.util.BitSet.hashCode()
        BitSet bs = (BitSet) eightbs.clone();
        bs.clear(2);
        bs.clear(6);
        assertTrue("BitSet returns wrong hash value: " + bs.hashCode(), bs
                .hashCode() == 1129);
        bs.set(10);
        bs.clear(3);
        assertTrue("BitSet returns wrong hash value: " + bs.hashCode(), bs
                .hashCode() == 97);
    }

    /**
     * @tests java.util.BitSet#clear()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "clear",
        args = {}
    )
    public void test_clear() {
        eightbs.clear();
        for (int i = 0; i < 8; i++)
            assertTrue("Clear didn't clear bit " + i, !eightbs.get(i));
        assertEquals("Test1: Wrong length", 0, eightbs.length());

        BitSet bs = new BitSet(3400);
        bs.set(0, bs.size() - 1); // ensure all bits are 1's
        bs.set(bs.size() - 1);
        bs.clear();
        assertEquals("Test2: Wrong length", 0, bs.length());
        assertTrue("Test2: isEmpty() returned incorrect value", bs
                .isEmpty());
        assertEquals("Test2: cardinality() returned incorrect value", 0, bs
                .cardinality());
    }

    /**
     * @tests java.util.BitSet#clear(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "clear",
        args = {int.class}
    )
    public void test_clearI() {
        // Test for method void java.util.BitSet.clear(int)

        eightbs.clear(7);
        assertTrue("Failed to clear bit", !eightbs.get(7));

        // Check to see all other bits are still set
        for (int i = 0; i < 7; i++)
            assertTrue("Clear cleared incorrect bits", eightbs.get(i));

        eightbs.clear(165);
        assertTrue("Failed to clear bit", !eightbs.get(165));
        // Try out of range
        try {
            eightbs.clear(-1);
            fail("Failed to throw expected out of bounds exception");
        } catch (IndexOutOfBoundsException e) {
            // Correct behaviour
        }

        BitSet bs = new BitSet(0);
        assertTrue("Test1: Wrong length, " + bs.size(), bs.length() == 0);
        bs.clear(0);
        assertTrue("Test2: Wrong length" + bs.size(), bs.length() == 0);
    }

    /**
     * @tests java.util.BitSet#clear(int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "clear",
        args = {int.class, int.class}
    )
    public void test_clearII() {
        // Test for method void java.util.BitSet.clear(int, int)
        // pos1 and pos2 are in the same bitset element
        BitSet bs = new BitSet(16);
        int initialSize = bs.size();
        bs.set(0, initialSize);
        bs.clear(5);
        bs.clear(15);
        bs.clear(7, 11);
        for (int i = 0; i < 7; i++) {
            if (i == 5)
                assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
            else
                assertTrue("Shouldn't have cleared bit " + i, bs.get(i));
        }
        for (int i = 7; i < 11; i++)
            assertTrue("Failed to clear bit " + i, !bs.get(i));

        for (int i = 11; i < initialSize; i++) {
            if (i == 15)
                assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
            else
                assertTrue("Shouldn't have cleared bit " + i, bs.get(i));
        }

        for (int i = initialSize; i < bs.size(); i++) {
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        }

        // pos1 and pos2 is in the same bitset element, boundary testing
        bs = new BitSet(16);
        initialSize = bs.size();
        bs.set(0, initialSize);
        bs.clear(7, 64);
        assertEquals("Failed to grow BitSet", 64, bs.size());
        for (int i = 0; i < 7; i++)
            assertTrue("Shouldn't have cleared bit " + i, bs.get(i));
        for (int i = 7; i < 64; i++)
            assertTrue("Failed to clear bit " + i, !bs.get(i));
        for (int i = 64; i < bs.size(); i++) {
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        }
        // more boundary testing
        bs = new BitSet(32);
        initialSize = bs.size();
        bs.set(0, initialSize);
        bs.clear(0, 64);
        for (int i = 0; i < 64; i++)
            assertTrue("Failed to clear bit " + i, !bs.get(i));
        for (int i = 64; i < bs.size(); i++) {
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        }

        bs = new BitSet(32);
        initialSize = bs.size();
        bs.set(0, initialSize);
        bs.clear(0, 65);
        for (int i = 0; i < 65; i++)
            assertTrue("Failed to clear bit " + i, !bs.get(i));
        for (int i = 65; i < bs.size(); i++) {
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        }

        // pos1 and pos2 are in two sequential bitset elements
        bs = new BitSet(128);
        initialSize = bs.size();
        bs.set(0, initialSize);
        bs.clear(7);
        bs.clear(110);
        bs.clear(9, 74);
        for (int i = 0; i < 9; i++) {
            if (i == 7)
                assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
            else
                assertTrue("Shouldn't have cleared bit " + i, bs.get(i));
        }
        for (int i = 9; i < 74; i++)
            assertTrue("Failed to clear bit " + i, !bs.get(i));
        for (int i = 74; i < initialSize; i++) {
            if (i == 110)
                assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
            else
                assertTrue("Shouldn't have cleared bit " + i, bs.get(i));
        }
        for (int i = initialSize; i < bs.size(); i++) {
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        }

        // pos1 and pos2 are in two non-sequential bitset elements
        bs = new BitSet(256);
        bs.set(0, 256);
        bs.clear(7);
        bs.clear(255);
        bs.clear(9, 219);
        for (int i = 0; i < 9; i++) {
            if (i == 7)
                assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
            else
                assertTrue("Shouldn't have cleared bit " + i, bs.get(i));
        }

        for (int i = 9; i < 219; i++)
            assertTrue("failed to clear bit " + i, !bs.get(i));

        for (int i = 219; i < 255; i++)
            assertTrue("Shouldn't have cleared bit " + i, bs.get(i));

        for (int i = 255; i < bs.size(); i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));

        // test illegal args
        bs = new BitSet(10);
        try {
            bs.clear(-1, 3);
            fail(
                    "Test1: Attempt to flip with  negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            bs.clear(2, -1);
            fail(
                    "Test2: Attempt to flip with negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            bs.clear(4, 2);
            fail(
                    "Test4: Attempt to flip with illegal args failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * @tests java.util.BitSet#get(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "get",
        args = {int.class}
    )
    public void test_getI() {
        // Test for method boolean java.util.BitSet.get(int)

        BitSet bs = new BitSet();
        bs.set(8);
        assertTrue("Get returned true for index out of range", !eightbs.get(99));
        assertTrue("Get returned false for set value", eightbs.get(3));
        assertTrue("Get returned true for a non set value", !bs.get(0));

        try {
            bs.get(-1);
            fail(
                    "Attempt to get at negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
            // Correct behaviour
        }

        bs = new BitSet(1);
        assertTrue("Access greater than size", !bs.get(64));

        bs = new BitSet();
        bs.set(63);
        assertTrue("Test highest bit", bs.get(63));
    }

    /**
     * @tests java.util.BitSet#get(int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "get",
        args = {int.class, int.class}
    )
    public void test_getII() {
        // Test for method boolean java.util.BitSet.get(int, int)
        BitSet bs, resultbs, correctbs;
        bs = new BitSet(512);
        bs.set(3, 9);
        bs.set(10, 20);
        bs.set(60, 75);
        bs.set(121);
        bs.set(130, 140);

        // pos1 and pos2 are in the same bitset element, at index0
        resultbs = bs.get(3, 6);
        correctbs = new BitSet(3);
        correctbs.set(0, 3);
        assertTrue("Test1: Returned incorrect BitSet", resultbs
                .equals(correctbs));

        // pos1 and pos2 are in the same bitset element, at index 1
        resultbs = bs.get(100, 125);
        correctbs = new BitSet(25);
        correctbs.set(21);
        assertTrue("Test2: Returned incorrect BitSet", resultbs
                .equals(correctbs));

        // pos1 in bitset element at index 0, and pos2 in bitset element at
        // index 1
        resultbs = bs.get(15, 125);
        correctbs = new BitSet(25);
        correctbs.set(0, 5);
        correctbs.set(45, 60);
        correctbs.set(121 - 15);
        assertTrue("Test3: Returned incorrect BitSet", resultbs
                .equals(correctbs));

        // pos1 in bitset element at index 1, and pos2 in bitset element at
        // index 2
        resultbs = bs.get(70, 145);
        correctbs = new BitSet(75);
        correctbs.set(0, 5);
        correctbs.set(51);
        correctbs.set(60, 70);
        assertTrue("Test4: Returned incorrect BitSet", resultbs
                .equals(correctbs));

        // pos1 in bitset element at index 0, and pos2 in bitset element at
        // index 2
        resultbs = bs.get(5, 145);
        correctbs = new BitSet(140);
        correctbs.set(0, 4);
        correctbs.set(5, 15);
        correctbs.set(55, 70);
        correctbs.set(116);
        correctbs.set(125, 135);
        assertTrue("Test5: Returned incorrect BitSet", resultbs
                .equals(correctbs));

        // pos1 in bitset element at index 0, and pos2 in bitset element at
        // index 3
        resultbs = bs.get(5, 250);
        correctbs = new BitSet(200);
        correctbs.set(0, 4);
        correctbs.set(5, 15);
        correctbs.set(55, 70);
        correctbs.set(116);
        correctbs.set(125, 135);
        assertTrue("Test6: Returned incorrect BitSet", resultbs
                .equals(correctbs));

        assertTrue("equality principle", bs.equals(bs.get(0, bs.size())));

        // more tests
        BitSet bs2 = new BitSet(129);
        bs2.set(0, 20);
        bs2.set(62, 65);
        bs2.set(121, 123);
        resultbs = bs2.get(1, 124);
        correctbs = new BitSet(129);
        correctbs.set(0, 19);
        correctbs.set(61, 64);
        correctbs.set(120, 122);
        assertTrue("Test6: Returned incorrect BitSet", resultbs
                .equals(correctbs));

        // equality principle with some boundary conditions
        bs2 = new BitSet(128);
        bs2.set(2, 20);
        bs2.set(62);
        bs2.set(121, 123);
        bs2.set(127);
        resultbs = bs2.get(0, bs2.size());
        assertTrue("equality principle", bs2.equals(resultbs));

        bs2 = new BitSet(128);
        bs2.set(2, 20);
        bs2.set(62);
        bs2.set(121, 123);
        bs2.set(127);
        bs2.flip(0, 128);
        resultbs = bs2.get(0, bs.size());
        assertTrue("equality principle", bs2.equals(resultbs));
        
        try {
            bs2.get(-1, 0);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            //expected
        }
        
        try {
            bs2.get(bs2.size()/2, 0);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            //expected
        }
        
        try {
            bs2.get(bs2.size()/2, -1);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            //expected
        }
    }

    /**
     * @tests java.util.BitSet#set(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "set",
        args = {int.class}
    )
    public void test_setI() {
        // Test for method void java.util.BitSet.set(int)

        BitSet bs = new BitSet();
        bs.set(8);
        assertTrue("Failed to set bit", bs.get(8));

        try {
            bs.set(-1);
            fail(
                    "Attempt to set at negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
            // Correct behaviour
        }

        // Try setting a bit on a 64 boundary
        bs.set(128);
        assertEquals("Failed to grow BitSet", 192, bs.size());
        assertTrue("Failed to set bit", bs.get(128));

        bs = new BitSet(64);
        for (int i = bs.size(); --i >= 0;) {
            bs.set(i);
            assertTrue("Incorrectly set", bs.get(i));
            assertTrue("Incorrect length", bs.length() == (i + 1));
            for (int j = bs.size(); --j > i;)
                assertTrue("Incorrectly set bit " + j, !bs.get(j));
            for (int j = i; --j >= 0;)
                assertTrue("Incorrectly set bit " + j, !bs.get(j));
            bs.clear(i);
        }

        bs = new BitSet(0);
        assertTrue("Test1: Wrong length, " + bs.size(), bs.length() == 0);
        bs.set(0);
        assertTrue("Test2: Wrong length" + bs.size(), bs.length() == 1);
    }

    /**
     * @tests java.util.BitSet#set(int, boolean)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "set",
        args = {int.class, boolean.class}
    )
    public void test_setIZ() {
        // Test for method void java.util.BitSet.set(int, boolean)
        eightbs.set(5, false);
        assertTrue("Should have set bit 5 to true", !eightbs.get(5));

        eightbs.set(5, true);
        assertTrue("Should have set bit 5 to false", eightbs.get(5));
        
        try {
            eightbs.set(-5, false);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            //expected
        }
    }

    /**
     * @tests java.util.BitSet#set(int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "set",
        args = {int.class, int.class}
    )
    public void test_setII() {
        // Test for method void java.util.BitSet.set(int, int)
        // pos1 and pos2 are in the same bitset element
        BitSet bs = new BitSet(16);
        bs.set(5);
        bs.set(15);
        bs.set(7, 11);
        for (int i = 0; i < 7; i++) {
            if (i == 5)
                assertTrue("Shouldn't have flipped bit " + i, bs.get(i));
            else
                assertTrue("Shouldn't have set bit " + i, !bs.get(i));
        }
        for (int i = 7; i < 11; i++)
            assertTrue("Failed to set bit " + i, bs.get(i));
        for (int i = 11; i < bs.size(); i++) {
            if (i == 15)
                assertTrue("Shouldn't have flipped bit " + i, bs.get(i));
            else
                assertTrue("Shouldn't have set bit " + i, !bs.get(i));
        }

        // pos1 and pos2 is in the same bitset element, boundary testing
        bs = new BitSet(16);
        bs.set(7, 64);
        assertEquals("Failed to grow BitSet", 64, bs.size());
        for (int i = 0; i < 7; i++)
            assertTrue("Shouldn't have set bit " + i, !bs.get(i));
        for (int i = 7; i < 64; i++)
            assertTrue("Failed to set bit " + i, bs.get(i));
        assertTrue("Shouldn't have set bit 64", !bs.get(64));

        // more boundary testing
        bs = new BitSet(32);
        bs.set(0, 64);
        for (int i = 0; i < 64; i++)
            assertTrue("Failed to set bit " + i, bs.get(i));
        assertTrue("Shouldn't have set bit 64", !bs.get(64));

        bs = new BitSet(32);
        bs.set(0, 65);
        for (int i = 0; i < 65; i++)
            assertTrue("Failed to set bit " + i, bs.get(i));
        assertTrue("Shouldn't have set bit 65", !bs.get(65));

        // pos1 and pos2 are in two sequential bitset elements
        bs = new BitSet(128);
        bs.set(7);
        bs.set(110);
        bs.set(9, 74);
        for (int i = 0; i < 9; i++) {
            if (i == 7)
                assertTrue("Shouldn't have flipped bit " + i, bs.get(i));
            else
                assertTrue("Shouldn't have set bit " + i, !bs.get(i));
        }
        for (int i = 9; i < 74; i++)
            assertTrue("Failed to set bit " + i, bs.get(i));
        for (int i = 74; i < bs.size(); i++) {
            if (i == 110)
                assertTrue("Shouldn't have flipped bit " + i, bs.get(i));
            else
                assertTrue("Shouldn't have set bit " + i, !bs.get(i));
        }

        // pos1 and pos2 are in two non-sequential bitset elements
        bs = new BitSet(256);
        bs.set(7);
        bs.set(255);
        bs.set(9, 219);
        for (int i = 0; i < 9; i++) {
            if (i == 7)
                assertTrue("Shouldn't have set flipped " + i, bs.get(i));
            else
                assertTrue("Shouldn't have set bit " + i, !bs.get(i));
        }

        for (int i = 9; i < 219; i++)
            assertTrue("failed to set bit " + i, bs.get(i));

        for (int i = 219; i < 255; i++)
            assertTrue("Shouldn't have set bit " + i, !bs.get(i));

        assertTrue("Shouldn't have flipped bit 255", bs.get(255));

        // test illegal args
        bs = new BitSet(10);
        try {
            bs.set(-1, 3);
            fail(
                    "Test1: Attempt to flip with  negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            bs.set(2, -1);
            fail(
                    "Test2: Attempt to flip with negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            bs.set(4, 2);
            fail(
                    "Test4: Attempt to flip with illegal args failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * @tests java.util.BitSet#set(int, int, boolean)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "set",
        args = {int.class, int.class, boolean.class}
    )
    public void test_setIIZ() {
        // Test for method void java.util.BitSet.set(int, int, boolean)
        eightbs.set(3, 6, false);
        assertTrue("Should have set bits 3, 4, and 5 to false", !eightbs.get(3)
                && !eightbs.get(4) && !eightbs.get(5));

        eightbs.set(3, 6, true);
        assertTrue("Should have set bits 3, 4, and 5 to true", eightbs.get(3)
                && eightbs.get(4) && eightbs.get(5));

        try {
            eightbs.set(-3, 6, false);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            //expected
        }

        try {
            eightbs.set(3, -6, false);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            //expected
        }

        try {
            eightbs.set(6, 3, false);
            fail("IndexOutOfBoundsException expected");
        } catch (IndexOutOfBoundsException e) {
            //expected
        }
    }

    /**
     * @tests java.util.BitSet#flip(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "flip",
        args = {int.class}
    )
    public void test_flipI() {
        // Test for method void java.util.BitSet.flip(int)
        BitSet bs = new BitSet();
        bs.clear(8);
        bs.clear(9);
        bs.set(10);
        bs.flip(9);
        assertTrue("Failed to flip bit", !bs.get(8));
        assertTrue("Failed to flip bit", bs.get(9));
        assertTrue("Failed to flip bit", bs.get(10));

        bs.set(8);
        bs.set(9);
        bs.clear(10);
        bs.flip(9);
        assertTrue("Failed to flip bit", bs.get(8));
        assertTrue("Failed to flip bit", !bs.get(9));
        assertTrue("Failed to flip bit", !bs.get(10));

        try {
            bs.flip(-1);
            fail(
                    "Attempt to flip at negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
            // Correct behaviour
        }

        // Try setting a bit on a 64 boundary
        bs.flip(128);
        assertEquals("Failed to grow BitSet", 192, bs.size());
        assertTrue("Failed to flip bit", bs.get(128));

        bs = new BitSet(64);
        for (int i = bs.size(); --i >= 0;) {
            bs.flip(i);
            assertTrue("Test1: Incorrectly flipped bit" + i, bs.get(i));
            assertTrue("Incorrect length", bs.length() == (i + 1));
            for (int j = bs.size(); --j > i;)
                assertTrue("Test2: Incorrectly flipped bit" + j, !bs.get(j));
            for (int j = i; --j >= 0;)
                assertTrue("Test3: Incorrectly flipped bit" + j, !bs.get(j));
            bs.flip(i);
        }

        BitSet bs0 = new BitSet(0);
        assertEquals("Test1: Wrong size", 0, bs0.size());
        assertEquals("Test1: Wrong length", 0, bs0.length());

        bs0.flip(0);
        assertEquals("Test2: Wrong size", 64, bs0.size());
        assertEquals("Test2: Wrong length", 1, bs0.length());

        bs0.flip(63);
        assertEquals("Test3: Wrong size", 64, bs0.size());
        assertEquals("Test3: Wrong length", 64, bs0.length());

        eightbs.flip(7);
        assertTrue("Failed to flip bit 7", !eightbs.get(7));

        // Check to see all other bits are still set
        for (int i = 0; i < 7; i++)
            assertTrue("Flip flipped incorrect bits", eightbs.get(i));

        eightbs.flip(127);
        assertTrue("Failed to flip bit 127", eightbs.get(127));

        eightbs.flip(127);
        assertTrue("Failed to flip bit 127", !eightbs.get(127));
    }

    /**
     * @tests java.util.BitSet#flip(int, int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "flip",
        args = {int.class, int.class}
    )
    public void test_flipII() {
        // Test for method void java.util.BitSet.flip(int, int)
        // pos1 and pos2 are in the same bitset element
        BitSet bs = new BitSet(16);
        bs.set(7);
        bs.set(10);
        bs.flip(7, 11);
        for (int i = 0; i < 7; i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        assertTrue("Failed to flip bit 7", !bs.get(7));
        assertTrue("Failed to flip bit 8", bs.get(8));
        assertTrue("Failed to flip bit 9", bs.get(9));
        assertTrue("Failed to flip bit 10", !bs.get(10));
        for (int i = 11; i < bs.size(); i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));

        // pos1 and pos2 is in the same bitset element, boundary testing
        bs = new BitSet(16);
        bs.set(7);
        bs.set(10);
        bs.flip(7, 64);
        assertEquals("Failed to grow BitSet", 64, bs.size());
        for (int i = 0; i < 7; i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        assertTrue("Failed to flip bit 7", !bs.get(7));
        assertTrue("Failed to flip bit 8", bs.get(8));
        assertTrue("Failed to flip bit 9", bs.get(9));
        assertTrue("Failed to flip bit 10", !bs.get(10));
        for (int i = 11; i < 64; i++)
            assertTrue("failed to flip bit " + i, bs.get(i));
        assertTrue("Shouldn't have flipped bit 64", !bs.get(64));

        // more boundary testing
        bs = new BitSet(32);
        bs.flip(0, 64);
        for (int i = 0; i < 64; i++)
            assertTrue("Failed to flip bit " + i, bs.get(i));
        assertTrue("Shouldn't have flipped bit 64", !bs.get(64));

        bs = new BitSet(32);
        bs.flip(0, 65);
        for (int i = 0; i < 65; i++)
            assertTrue("Failed to flip bit " + i, bs.get(i));
        assertTrue("Shouldn't have flipped bit 65", !bs.get(65));

        // pos1 and pos2 are in two sequential bitset elements
        bs = new BitSet(128);
        bs.set(7);
        bs.set(10);
        bs.set(72);
        bs.set(110);
        bs.flip(9, 74);
        for (int i = 0; i < 7; i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        assertTrue("Shouldn't have flipped bit 7", bs.get(7));
        assertTrue("Shouldn't have flipped bit 8", !bs.get(8));
        assertTrue("Failed to flip bit 9", bs.get(9));
        assertTrue("Failed to flip bit 10", !bs.get(10));
        for (int i = 11; i < 72; i++)
            assertTrue("failed to flip bit " + i, bs.get(i));
        assertTrue("Failed to flip bit 72", !bs.get(72));
        assertTrue("Failed to flip bit 73", bs.get(73));
        for (int i = 74; i < 110; i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        assertTrue("Shouldn't have flipped bit 110", bs.get(110));
        for (int i = 111; i < bs.size(); i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));

        // pos1 and pos2 are in two non-sequential bitset elements
        bs = new BitSet(256);
        bs.set(7);
        bs.set(10);
        bs.set(72);
        bs.set(110);
        bs.set(181);
        bs.set(220);
        bs.flip(9, 219);
        for (int i = 0; i < 7; i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));
        assertTrue("Shouldn't have flipped bit 7", bs.get(7));
        assertTrue("Shouldn't have flipped bit 8", !bs.get(8));
        assertTrue("Failed to flip bit 9", bs.get(9));
        assertTrue("Failed to flip bit 10", !bs.get(10));
        for (int i = 11; i < 72; i++)
            assertTrue("failed to flip bit " + i, bs.get(i));
        assertTrue("Failed to flip bit 72", !bs.get(72));
        for (int i = 73; i < 110; i++)
            assertTrue("failed to flip bit " + i, bs.get(i));
        assertTrue("Failed to flip bit 110", !bs.get(110));
        for (int i = 111; i < 181; i++)
            assertTrue("failed to flip bit " + i, bs.get(i));
        assertTrue("Failed to flip bit 181", !bs.get(181));
        for (int i = 182; i < 219; i++)
            assertTrue("failed to flip bit " + i, bs.get(i));
        assertTrue("Shouldn't have flipped bit 219", !bs.get(219));
        assertTrue("Shouldn't have flipped bit 220", bs.get(220));
        for (int i = 221; i < bs.size(); i++)
            assertTrue("Shouldn't have flipped bit " + i, !bs.get(i));

        // test illegal args
        bs = new BitSet(10);
        try {
            bs.flip(-1, 3);
            fail(
                    "Test1: Attempt to flip with  negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            bs.flip(2, -1);
            fail(
                    "Test2: Attempt to flip with negative index failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            bs.flip(4, 2);
            fail(
                    "Test4: Attempt to flip with illegal args failed to generate exception");
        } catch (IndexOutOfBoundsException e) {
        }
    }

    /**
     * @tests java.util.BitSet#set(int, int)
     * @tests java.util.BitSet#cardinality()
     * @tests java.util.BitSet#get(int)
     * @tests java.util.BitSet#flip(int, int)
     * @tests java.util.BitSet#clear(int,int)
     */
    @TestTargets({
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "",
            method = "flip",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "",
            method = "get",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "",
            method = "set",
            args = {int.class, int.class}
        ),
        @TestTargetNew(
            level = TestLevel.PARTIAL_COMPLETE,
            notes = "",
            method = "clear",
            args = {int.class, int.class}
        )
    })
    public void test_111478() {
        // BitSet shouldn't be modified by any of the operations below,
        // since the affected bits for these methods are defined as inclusive of
        // pos1, exclusive of pos2.
        try {
            eightbs.flip(0, 0);
            assertTrue("Bit got flipped incorrectly ", eightbs.get(0));

            BitSet bsnew = eightbs.get(2, 2);
            assertEquals("BitSet retrieved incorrectly ",
                    0, bsnew.cardinality());

            eightbs.set(10, 10);
            assertTrue("Bit got set incorrectly ", !eightbs.get(10));

            eightbs.clear(3, 3);
            assertTrue("Bit cleared incorrectly ", eightbs.get(3));
        } catch (IndexOutOfBoundsException e) {
            fail("Unexpected IndexOutOfBoundsException when pos1 ==pos2");
        }
    }

    /**
     * @tests java.util.BitSet#intersects(java.util.BitSet)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "intersects",
        args = {java.util.BitSet.class}
    )
    public void test_intersectsLjava_util_BitSet() {
        // Test for method boolean java.util.BitSet.intersects(java.util.BitSet)
        BitSet bs = new BitSet(500);
        bs.set(5);
        bs.set(63);
        bs.set(64);
        bs.set(71, 110);
        bs.set(127, 130);
        bs.set(192);
        bs.set(450);

        BitSet bs2 = new BitSet(8);
        assertFalse("Test1: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertFalse("Test1: intersects() returned incorrect value", bs2
                .intersects(bs));

        bs2.set(4);
        assertFalse("Test2: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertFalse("Test2: intersects() returned incorrect value", bs2
                .intersects(bs));

        bs2.clear();
        bs2.set(5);
        assertTrue("Test3: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertTrue("Test3: intersects() returned incorrect value", bs2
                .intersects(bs));

        bs2.clear();
        bs2.set(63);
        assertTrue("Test4: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertTrue("Test4: intersects() returned incorrect value", bs2
                .intersects(bs));

        bs2.clear();
        bs2.set(80);
        assertTrue("Test5: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertTrue("Test5: intersects() returned incorrect value", bs2
                .intersects(bs));

        bs2.clear();
        bs2.set(127);
        assertTrue("Test6: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertTrue("Test6: intersects() returned incorrect value", bs2
                .intersects(bs));

        bs2.clear();
        bs2.set(192);
        assertTrue("Test7: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertTrue("Test7: intersects() returned incorrect value", bs2
                .intersects(bs));

        bs2.clear();
        bs2.set(450);
        assertTrue("Test8: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertTrue("Test8: intersects() returned incorrect value", bs2
                .intersects(bs));

        bs2.clear();
        bs2.set(500);
        assertFalse("Test9: intersects() returned incorrect value", bs
                .intersects(bs2));
        assertFalse("Test9: intersects() returned incorrect value", bs2
                .intersects(bs));
    }

    /**
     * @tests java.util.BitSet#and(java.util.BitSet)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "and",
        args = {java.util.BitSet.class}
    )
    public void test_andLjava_util_BitSet() {
        // Test for method void java.util.BitSet.and(java.util.BitSet)
        BitSet bs = new BitSet(128);
        // Initialize the bottom half of the BitSet

        for (int i = 64; i < 128; i++)
            bs.set(i);
        eightbs.and(bs);
        assertTrue("AND failed to clear bits", !eightbs.equals(bs));
        eightbs.set(3);
        bs.set(3);
        eightbs.and(bs);
        assertTrue("AND failed to maintain set bits", bs.get(3));
        bs.and(eightbs);
        for (int i = 64; i < 128; i++)
            assertTrue("Failed to clear extra bits in the receiver BitSet", !bs
                    .get(i));
    }

    /**
     * @tests java.util.BitSet#andNot(java.util.BitSet)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "andNot",
        args = {java.util.BitSet.class}
    )
    public void test_andNotLjava_util_BitSet() {
        BitSet bs = (BitSet) eightbs.clone();
        bs.clear(5);
        BitSet bs2 = new BitSet();
        bs2.set(2);
        bs2.set(3);
        bs.andNot(bs2);
        assertEquals("Incorrect bitset after andNot", 
                "{0, 1, 4, 6, 7}", bs.toString());

        bs = new BitSet(0);
        bs.andNot(bs2);
        assertEquals("Incorrect size", 0, bs.size());
    }

    /**
     * @tests java.util.BitSet#or(java.util.BitSet)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "or",
        args = {java.util.BitSet.class}
    )
    public void test_orLjava_util_BitSet() {
        // Test for method void java.util.BitSet.or(java.util.BitSet)
        BitSet bs = new BitSet(128);
        bs.or(eightbs);
        for (int i = 0; i < 8; i++)
            assertTrue("OR failed to set bits", bs.get(i));

        bs = new BitSet(0);
        bs.or(eightbs);
        for (int i = 0; i < 8; i++)
            assertTrue("OR(0) failed to set bits", bs.get(i));

        eightbs.clear(5);
        bs = new BitSet(128);
        bs.or(eightbs);
        assertTrue("OR set a bit which should be off", !bs.get(5));
    }

    /**
     * @tests java.util.BitSet#xor(java.util.BitSet)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "xor",
        args = {java.util.BitSet.class}
    )
    public void test_xorLjava_util_BitSet() {
        // Test for method void java.util.BitSet.xor(java.util.BitSet)

        BitSet bs = (BitSet) eightbs.clone();
        bs.xor(eightbs);
        for (int i = 0; i < 8; i++)
            assertTrue("XOR failed to clear bits", !bs.get(i));

        bs.xor(eightbs);
        for (int i = 0; i < 8; i++)
            assertTrue("XOR failed to set bits", bs.get(i));

        bs = new BitSet(0);
        bs.xor(eightbs);
        for (int i = 0; i < 8; i++)
            assertTrue("XOR(0) failed to set bits", bs.get(i));

        bs = new BitSet();
        bs.set(63);
        assertEquals("Test highest bit", "{63}", bs.toString());
    }

    /**
     * @tests java.util.BitSet#size()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "size",
        args = {}
    )
    public void test_size() {
        // Test for method int java.util.BitSet.size()
        assertEquals("Returned incorrect size", 64, eightbs.size());
        eightbs.set(129);
        assertTrue("Returned incorrect size", eightbs.size() >= 129);

    }

    /**
     * @tests java.util.BitSet#toString()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "toString",
        args = {}
    )
    public void test_toString() {
        // Test for method java.lang.String java.util.BitSet.toString()
        assertEquals("Returned incorrect string representation", "{0, 1, 2, 3, 4, 5, 6, 7}", eightbs
                .toString());
        eightbs.clear(2);
        assertEquals("Returned incorrect string representation", "{0, 1, 3, 4, 5, 6, 7}", eightbs
                .toString());
    }

    /**
     * @tests java.util.BitSet#length()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "length",
        args = {}
    )
    public void test_length() {
        BitSet bs = new BitSet();
        assertTrue("BitSet returned wrong length--wanted 0, got: "
                + bs.length(), bs.length() == 0);
        bs.set(5);
        assertTrue("BitSet returned wrong length--wanted 6, got: "
                + bs.length(), bs.length() == 6);
        bs.set(10);
        assertTrue("BitSet returned wrong length--wanted 11, got: "
                + bs.length(), bs.length() == 11);
        bs.set(432);
        assertTrue("BitSet returned wrong length--wanted 433, got: "
                + bs.length(), bs.length() == 433);
        bs.set(300);
        assertTrue("BitSet returned wrong length--wanted 433 (again), got: "
                + bs.length(), bs.length() == 433);
    }

    /**
     * @tests java.util.BitSet#nextSetBit(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "nextSetBit",
        args = {int.class}
    )
    public void test_nextSetBitI() {
        // Test for method int java.util.BitSet.nextSetBit()
        BitSet bs = new BitSet(500);
        bs.set(5);
        bs.set(32);
        bs.set(63);
        bs.set(64);
        bs.set(71, 110);
        bs.set(127, 130);
        bs.set(193);
        bs.set(450);
        try {
            bs.nextSetBit(-1);
            fail("Expected IndexOutOfBoundsException for negative index");
        } catch (IndexOutOfBoundsException e) {
        }
        assertEquals("nextSetBit() returned the wrong value", 5, bs
                .nextSetBit(0));
        assertEquals("nextSetBit() returned the wrong value", 5, bs
                .nextSetBit(5));
        assertEquals("nextSetBit() returned the wrong value", 32, bs
                .nextSetBit(6));
        assertEquals("nextSetBit() returned the wrong value", 32, bs
                .nextSetBit(32));
        assertEquals("nextSetBit() returned the wrong value", 63, bs
                .nextSetBit(33));

        // boundary tests
        assertEquals("nextSetBit() returned the wrong value", 63, bs
                .nextSetBit(63));
        assertEquals("nextSetBit() returned the wrong value", 64, bs
                .nextSetBit(64));

        // at bitset element 1
        assertEquals("nextSetBit() returned the wrong value", 71, bs
                .nextSetBit(65));
        assertEquals("nextSetBit() returned the wrong value", 71, bs
                .nextSetBit(71));
        assertEquals("nextSetBit() returned the wrong value", 72, bs
                .nextSetBit(72));
        assertEquals("nextSetBit() returned the wrong value", 127, bs
                .nextSetBit(110));

        // boundary tests
        assertEquals("nextSetBit() returned the wrong value", 127, bs
                .nextSetBit(127));
        assertEquals("nextSetBit() returned the wrong value", 128, bs
                .nextSetBit(128));

        // at bitset element 2
        assertEquals("nextSetBit() returned the wrong value", 193, bs
                .nextSetBit(130));

        assertEquals("nextSetBit() returned the wrong value", 193, bs
                .nextSetBit(191));
        assertEquals("nextSetBit() returned the wrong value", 193, bs
                .nextSetBit(192));
        assertEquals("nextSetBit() returned the wrong value", 193, bs
                .nextSetBit(193));
        assertEquals("nextSetBit() returned the wrong value", 450, bs
                .nextSetBit(194));
        assertEquals("nextSetBit() returned the wrong value", 450, bs
                .nextSetBit(255));
        assertEquals("nextSetBit() returned the wrong value", 450, bs
                .nextSetBit(256));
        assertEquals("nextSetBit() returned the wrong value", 450, bs
                .nextSetBit(450));

        assertEquals("nextSetBit() returned the wrong value", -1, bs
                .nextSetBit(451));
        assertEquals("nextSetBit() returned the wrong value", -1, bs
                .nextSetBit(511));
        assertEquals("nextSetBit() returned the wrong value", -1, bs
                .nextSetBit(512));
        assertEquals("nextSetBit() returned the wrong value", -1, bs
                .nextSetBit(800));
    }

    /**
     * @tests java.util.BitSet#nextClearBit(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "nextClearBit",
        args = {int.class}
    )
    public void test_nextClearBitI() {
        // Test for method int java.util.BitSet.nextSetBit()
        BitSet bs = new BitSet(500);
        // ensure all the bits from 0 to bs.size() - 1 are set to true
        bs.set(0, bs.size() - 1); 
        bs.set(bs.size() - 1);
        bs.clear(5);
        bs.clear(32);
        bs.clear(63);
        bs.clear(64);
        bs.clear(71, 110);
        bs.clear(127, 130);
        bs.clear(193);
        bs.clear(450);
        try {
            bs.nextClearBit(-1);
            fail("Expected IndexOutOfBoundsException for negative index");
        } catch (IndexOutOfBoundsException e) {
        }
        assertEquals("nextClearBit() returned the wrong value", 5, bs
                .nextClearBit(0));
        assertEquals("nextClearBit() returned the wrong value", 5, bs
                .nextClearBit(5));
        assertEquals("nextClearBit() returned the wrong value", 32, bs
                .nextClearBit(6));
        assertEquals("nextClearBit() returned the wrong value", 32, bs
                .nextClearBit(32));
        assertEquals("nextClearBit() returned the wrong value", 63, bs
                .nextClearBit(33));

        // boundary tests
        assertEquals("nextClearBit() returned the wrong value", 63, bs
                .nextClearBit(63));
        assertEquals("nextClearBit() returned the wrong value", 64, bs
                .nextClearBit(64));

        // at bitset element 1
        assertEquals("nextClearBit() returned the wrong value", 71, bs
                .nextClearBit(65));
        assertEquals("nextClearBit() returned the wrong value", 71, bs
                .nextClearBit(71));
        assertEquals("nextClearBit() returned the wrong value", 72, bs
                .nextClearBit(72));
        assertEquals("nextClearBit() returned the wrong value", 127, bs
                .nextClearBit(110));

        // boundary tests
        assertEquals("nextClearBit() returned the wrong value", 127, bs
                .nextClearBit(127));
        assertEquals("nextClearBit() returned the wrong value", 128, bs
                .nextClearBit(128));

        // at bitset element 2
        assertEquals("nextClearBit() returned the wrong value", 193, bs
                .nextClearBit(130));
        assertEquals("nextClearBit() returned the wrong value", 193, bs
                .nextClearBit(191));

        assertEquals("nextClearBit() returned the wrong value", 193, bs
                .nextClearBit(192));
        assertEquals("nextClearBit() returned the wrong value", 193, bs
                .nextClearBit(193));
        assertEquals("nextClearBit() returned the wrong value", 450, bs
                .nextClearBit(194));
        assertEquals("nextClearBit() returned the wrong value", 450, bs
                .nextClearBit(255));
        assertEquals("nextClearBit() returned the wrong value", 450, bs
                .nextClearBit(256));
        assertEquals("nextClearBit() returned the wrong value", 450, bs
                .nextClearBit(450));

        // bitset has 1 still the end of bs.size() -1, but calling nextClearBit
        // with any index value after the last true bit should return bs.size()
        assertEquals("nextClearBit() returned the wrong value", 512, bs
                .nextClearBit(451));
        assertEquals("nextClearBit() returned the wrong value", 512, bs
                .nextClearBit(511));
        assertEquals("nextClearBit() returned the wrong value", 512, bs
                .nextClearBit(512));

        // if the index is larger than bs.size(), nextClearBit should return
        // index
        assertEquals("nextClearBit() returned the wrong value", 513, bs
                .nextClearBit(513));
        assertEquals("nextClearBit() returned the wrong value", 800, bs
                .nextClearBit(800));
    }

    /**
     * @tests java.util.BitSet#isEmpty()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "isEmpty",
        args = {}
    )
    public void test_isEmpty() {
        BitSet bs = new BitSet(500);
        assertTrue("Test: isEmpty() returned wrong value", bs.isEmpty());

        // at bitset element 0
        bs.set(3);
        assertFalse("Test0: isEmpty() returned wrong value", bs
                .isEmpty());

        // at bitset element 1
        bs.clear();
        bs.set(12);
        assertFalse("Test1: isEmpty() returned wrong value", bs
                .isEmpty());

        // at bitset element 2
        bs.clear();
        bs.set(128);
        assertFalse("Test2: isEmpty() returned wrong value", bs
                .isEmpty());

        // boundary testing
        bs.clear();
        bs.set(459);
        assertFalse("Test3: isEmpty() returned wrong value", bs
                .isEmpty());

        bs.clear();
        bs.set(511);
        assertFalse("Test4: isEmpty() returned wrong value", bs
                .isEmpty());
    }

    /**
     * @tests java.util.BitSet#cardinality()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "cardinality",
        args = {}
    )
    public void test_cardinality() {
        // test for method int java.util.BitSet.cardinality()
        BitSet bs = new BitSet(500);
        bs.set(5);
        bs.set(32);
        bs.set(63);
        bs.set(64);
        bs.set(71, 110);
        bs.set(127, 130);
        bs.set(193);
        bs.set(450);
        assertEquals("cardinality() returned wrong value", 48, bs.cardinality());

        bs.flip(0, 500);
        assertEquals("cardinality() returned wrong value", 452, bs
                .cardinality());

        bs.clear();
        assertEquals("cardinality() returned wrong value", 0, bs.cardinality());

        bs.set(0, 500);
        assertEquals("cardinality() returned wrong value", 500, bs
                .cardinality());
    }

    /**
     * helper method to display the contents of a bitset
     * 
     */
    private static void printBitset(BitSet bs) {
        System.out.println();
        for (int i = bs.size() - 1; i >= 0; i--) {
            if (bs.get(i))
                System.out.print(1);
            else
                System.out.print(0);
        }
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {

        eightbs = new BitSet();

        for (int i = 0; i < 8; i++)
            eightbs.set(i);
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() {
    }
}
