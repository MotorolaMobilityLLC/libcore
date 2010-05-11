/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests.api.java.util;

import dalvik.annotation.TestTargetNew;
import dalvik.annotation.TestTargets;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass; 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;

import tests.util.SerializationTester;

import junit.framework.TestCase;

@TestTargetClass(PriorityQueue.class) 
public class PriorityQueueTest extends TestCase {

    private static final String SERIALIZATION_FILE_NAME = "/serialization/tests/api/java/util/PriorityQueue.golden.ser"; //$NON-NLS-1$    
                                                            
    /**
     * @tests java.util.PriorityQueue#iterator()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "iterator",
        args = {}
    )
    public void test_iterator() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        Integer[] array = { 2, 45, 7, -12, 9 };
        for (int i = 0; i < array.length; i++) {
            integerQueue.offer(array[i]);
        }
        Iterator<Integer> iter = integerQueue.iterator();
        assertNotNull(iter);
        ArrayList<Integer> iterResult = new ArrayList<Integer>();
        while (iter.hasNext()) {
            iterResult.add(iter.next());
        }
        Object[] resultArray = iterResult.toArray();
        Arrays.sort(array);
        Arrays.sort(resultArray);
        assertTrue(Arrays.equals(array, resultArray));
    }

    /**
     * @tests java.util.PriorityQueue#iterator()
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NoSuchElementException, IllegalStateException.",
        method = "iterator",
        args = {}
    )
    public void test_iterator_empty() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        Iterator<Integer> iter = integerQueue.iterator();
        try {
            iter.next();
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }

        iter = integerQueue.iterator();
        try {
            iter.remove();
            fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#iterator()
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NoSuchElementException.",
        method = "iterator",
        args = {}
    )
    public void test_iterator_outofbound() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        integerQueue.offer(0);
        Iterator<Integer> iter = integerQueue.iterator();
        iter.next();
        try {
            iter.next();
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }

        iter = integerQueue.iterator();
        iter.next();
        iter.remove();
        try {
            iter.next();
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#iterator()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "iterator",
        args = {}
    )
    public void test_iterator_remove() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        Integer[] array = { 2, 45, 7, -12, 9 };
        for (int i = 0; i < array.length; i++) {
            integerQueue.offer(array[i]);
        }
        Iterator<Integer> iter = integerQueue.iterator();
        assertNotNull(iter);
        for (int i = 0; i < array.length; i++) {
            iter.next();
            if (2 == i) {
                iter.remove();
            }
        }
        assertEquals(array.length - 1, integerQueue.size());

        iter = integerQueue.iterator();
        Integer[] newArray = new Integer[array.length - 1];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = iter.next();
        }

        Arrays.sort(newArray);
        for (int i = 0; i < integerQueue.size(); i++) {
            assertEquals(newArray[i], integerQueue.poll());
        }

    }

    /**
     * @tests java.util.PriorityQueue#iterator()
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies IllegalStateException.",
        method = "iterator",
        args = {}
    )
    public void test_iterator_remove_illegalState() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        Integer[] array = { 2, 45, 7, -12, 9 };
        for (int i = 0; i < array.length; i++) {
            integerQueue.offer(array[i]);
        }
        Iterator<Integer> iter = integerQueue.iterator();
        assertNotNull(iter);
        try {
            iter.remove();
            fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
        iter.next();
        iter.remove();
        try {
            iter.remove();
            fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }

    }

    /**
     * @tests java.util.PriorityQueue.size()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "size",
        args = {}
    )
    public void test_size() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        assertEquals(0, integerQueue.size());
        int[] array = { 2, 45, 7, -12, 9 };
        for (int i = 0; i < array.length; i++) {
            integerQueue.offer(array[i]);
        }
        assertEquals(array.length, integerQueue.size());
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "PriorityQueue",
        args = {}
    )
    public void test_Constructor() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        assertNotNull(queue);
        assertEquals(0, queue.size());
        assertNull(queue.comparator());
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(int)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "PriorityQueue",
        args = {int.class}
    )
    public void test_ConstructorI() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>(100);
        assertNotNull(queue);
        assertEquals(0, queue.size());
        assertNull(queue.comparator());
        
        try {
            new PriorityQueue(0);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            //expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(int, Comparator<? super E>)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify IllegalArgumentException.",
        method = "PriorityQueue",
        args = {int.class, java.util.Comparator.class}
    )
    public void test_ConstructorILjava_util_Comparator() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>(100,
                (Comparator<Object>) null);
        assertNotNull(queue);
        assertEquals(0, queue.size());
        assertNull(queue.comparator());

        MockComparator<Object> comparator = new MockComparator<Object>();
        queue = new PriorityQueue<Object>(100, comparator);
        assertNotNull(queue);
        assertEquals(0, queue.size());
        assertEquals(comparator, queue.comparator());
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(int, Comparator<? super E>)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies IllegalArgumentException.",
        method = "PriorityQueue",
        args = {int.class, java.util.Comparator.class}
    )
    public void test_ConstructorILjava_util_Comparator_illegalCapacity() {
        try {
            new PriorityQueue<Object>(0, new MockComparator<Object>());
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            new PriorityQueue<Object>(-1, new MockComparator<Object>());
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(int, Comparator<? super E>)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify IllegalArgumentException.",
        method = "PriorityQueue",
        args = {int.class, java.util.Comparator.class}
    )
    public void test_ConstructorILjava_util_Comparator_cast() {
        MockComparatorCast<Object> objectComparator = new MockComparatorCast<Object>();
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>(100,
                objectComparator);
        assertNotNull(integerQueue);
        assertEquals(0, integerQueue.size());
        assertEquals(objectComparator, integerQueue.comparator());
        Integer[] array = { 2, 45, 7, -12, 9 };
        List<Integer> list = Arrays.asList(array);
        integerQueue.addAll(list);
        assertEquals(list.size(), integerQueue.size());
        // just test here no cast exception raises.
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(Collection)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify ClassCastException, NullPointerException.",
        method = "PriorityQueue",
        args = {java.util.Collection.class}
    )
    public void test_ConstructorLjava_util_Colleciton() {
        Integer[] array = { 2, 45, 7, -12, 9 };
        List<Integer> list = Arrays.asList(array);
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>(list);
        assertEquals(array.length, integerQueue.size());
        assertNull(integerQueue.comparator());
        Arrays.sort(array);
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], integerQueue.poll());
        }
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(Collection)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NullPointerException.",
        method = "PriorityQueue",
        args = {java.util.Collection.class}
    )
    public void test_ConstructorLjava_util_Colleciton_null() {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(new Float(11));
        list.add(null);
        list.add(new Integer(10));
        try {
            new PriorityQueue<Object>(list);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(Collection)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies ClassCastException.",
        method = "PriorityQueue",
        args = {java.util.Collection.class}
    )
    public void test_ConstructorLjava_util_Colleciton_non_comparable() {
        ArrayList<Object> list = new ArrayList<Object>();
        list.add(new Float(11));
        list.add(new Integer(10));
        try {
            new PriorityQueue<Object>(list);
            fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(Collection)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify ClassCastException, NullPointerException.",
        method = "PriorityQueue",
        args = {java.util.Collection.class}
    )
    public void test_ConstructorLjava_util_Colleciton_from_priorityqueue() {
        String[] array = { "AAAAA", "AA", "AAAA", "AAAAAAAA" };
        PriorityQueue<String> queue = new PriorityQueue<String>(4,
                new MockComparatorStringByLength());
        for (int i = 0; i < array.length; i++) {
            queue.offer(array[i]);
        }
        Collection<String> c = queue;
        PriorityQueue<String> constructedQueue = new PriorityQueue<String>(c);
        assertEquals(queue.comparator(), constructedQueue.comparator());
        while (queue.size() > 0) {
            assertEquals(queue.poll(), constructedQueue.poll());
        }
        assertEquals(0, constructedQueue.size());
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(Collection)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify ClassCastException, NullPointerException.",
        method = "PriorityQueue",
        args = {java.util.Collection.class}
    )
    public void test_ConstructorLjava_util_Colleciton_from_sortedset() {
        int[] array = { 3, 5, 79, -17, 5 };
        TreeSet<Integer> treeSet = new TreeSet<Integer>(new MockComparator<Integer>());
        for (int i = 0; i < array.length; i++) {
            treeSet.add(array[i]);
        }
        Collection<? extends Integer> c = treeSet;
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(c);
        assertEquals(treeSet.comparator(), queue.comparator());
        Iterator<Integer> iter = treeSet.iterator();
        while (iter.hasNext()) {
            assertEquals(iter.next(), queue.poll());
        }
        assertEquals(0, queue.size());
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(PriorityQueue<? * extends
     *        E>)
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "ClassCastException can not be checked.",
        method = "PriorityQueue",
        args = {java.util.PriorityQueue.class}
    )
    public void test_ConstructorLjava_util_PriorityQueue() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        int[] array = { 2, 45, 7, -12, 9 };
        for (int i = 0; i < array.length; i++) {
            integerQueue.offer(array[i]);
        }
        PriorityQueue objectQueue = new PriorityQueue(
                integerQueue);
        assertEquals(integerQueue.size(), objectQueue.size());
        assertEquals(integerQueue.comparator(), objectQueue.comparator());
        Arrays.sort(array);
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], objectQueue.poll());
        }

        try {
            new PriorityQueue((PriorityQueue)null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            //expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(PriorityQueue<? * extends
     *        E>)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NullPointerException.",
        method = "PriorityQueue",
        args = {java.util.PriorityQueue.class}
    )
    public void test_ConstructorLjava_util_PriorityQueue_null() {
        try {
            new PriorityQueue<Object>((PriorityQueue<Integer>) null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(SortedSet<? extends E>)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify ClassCastException, NullPointerException.",
        method = "PriorityQueue",
        args = {java.util.SortedSet.class}
    )
    public void test_ConstructorLjava_util_SortedSet() {
        int[] array = { 3, 5, 79, -17, 5 };
        TreeSet<Integer> treeSet = new TreeSet<Integer>();
        for (int i = 0; i < array.length; i++) {
            treeSet.add(array[i]);
        }
        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(treeSet);
        Iterator<Integer> iter = treeSet.iterator();
        while (iter.hasNext()) {
            assertEquals(iter.next(), queue.poll());
        }
    }

    /**
     * @tests java.util.PriorityQueue#PriorityQueue(SortedSet<? extends E>)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NullPointerException.",
        method = "PriorityQueue",
        args = {java.util.SortedSet.class}
    )
    public void test_ConstructorLjava_util_SortedSet_null() {
        try {
            new PriorityQueue<Integer>((SortedSet<? extends Integer>) null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#offer(Object)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify exceptions.",
        method = "offer",
        args = {java.lang.Object.class}
    )
    public void test_offerLjava_lang_Object() {
        PriorityQueue<String> queue = new PriorityQueue<String>(10,
                new MockComparatorStringByLength());
        String[] array = { "AAAAA", "AA", "AAAA", "AAAAAAAA" };
        for (int i = 0; i < array.length; i++) {
            queue.offer(array[i]);
        }
        String[] sortedArray = { "AA", "AAAA", "AAAAA", "AAAAAAAA" };
        for (int i = 0; i < sortedArray.length; i++) {
            assertEquals(sortedArray[i], queue.poll());
        }
        assertEquals(0, queue.size());
        assertNull(queue.poll());
    }

    /**
     * @tests java.util.PriorityQueue#offer(Object)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NullPointerException.",
        method = "offer",
        args = {java.lang.Object.class}
    )
    public void test_offerLjava_lang_Object_null() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        try {
            queue.offer(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#offer(Object)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies ClassCastException.",
        method = "offer",
        args = {java.lang.Object.class}
    )
    public void test_offer_Ljava_lang_Object_non_Comparable() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        queue.offer(new Integer(10));
        try {
            queue.offer(new Float(1.3));
            fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }

        queue = new PriorityQueue<Object>();
        queue.offer(new Integer(10));
        try {
            queue.offer(new Object());
            fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#poll()
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "",
        method = "poll",
        args = {}
    )
    public void test_poll() {
        PriorityQueue<String> stringQueue = new PriorityQueue<String>();
        String[] array = { "MYTESTSTRING", "AAAAA", "BCDEF", "ksTRD", "AAAAA" };
        for (int i = 0; i < array.length; i++) {
            stringQueue.offer(array[i]);
        }
        Arrays.sort(array);
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], stringQueue.poll());
        }
        assertEquals(0, stringQueue.size());
        assertNull(stringQueue.poll());
    }

    /**
     * @tests java.util.PriorityQueue#poll()
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies poll method for empty queue.",
        method = "poll",
        args = {}
    )
    public void test_poll_empty() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        assertEquals(0, queue.size());
        assertNull(queue.poll());
    }

    /**
     * @tests java.util.PriorityQueue#peek()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "peek",
        args = {}
    )
    public void test_peek() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        int[] array = { 2, 45, 7, -12, 9 };
        for (int i = 0; i < array.length; i++) {
            integerQueue.add(array[i]);
        }
        Arrays.sort(array);
        assertEquals(new Integer(array[0]), integerQueue.peek());
        assertEquals(new Integer(array[0]), integerQueue.peek());
    }

    /**
     * @tests java.util.PriorityQueue#peek()
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies peek method for empty queue.",
        method = "peek",
        args = {}
    )
    public void test_peek_empty() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        assertNull(queue.peek());
    }

    /**
     * @tests java.util.PriorityQueue#Clear()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "clear",
        args = {}
    )
    public void test_clear() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        int[] array = { 2, 45, 7, -12, 9 };
        for (int i = 0; i < array.length; i++) {
            integerQueue.offer(array[i]);
        }
        integerQueue.clear();
        assertTrue(integerQueue.isEmpty());
    }

    /**
     * @tests java.util.PriorityQueue#add(Object)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Doesn't verify NullPointerException, ClassCastException.",
        method = "add",
        args = {java.lang.Object.class}
    )
    public void test_add_Ljava_lang_Object() {
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>();
        Integer[] array = { 2, 45, 7, -12, 9 };
        for (int i = 0; i < array.length; i++) {
            integerQueue.add(array[i]);
        }
        Arrays.sort(array);
        assertEquals(array.length, integerQueue.size());
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], integerQueue.poll());
        }
        assertEquals(0, integerQueue.size());
    }

    /**
     * @tests java.util.PriorityQueue#add(Object)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies NullPointerException.",
        method = "add",
        args = {java.lang.Object.class}
    )
    public void test_add_Ljava_lang_Object_null() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        try {
            queue.add(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#add(Object)
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies ClassCastException.",
        method = "add",
        args = {java.lang.Object.class}
    )
    public void test_add_Ljava_lang_Object_non_Comparable() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        queue.add(new Integer(10));
        try {
            queue.add(new Float(1.3));
            fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }

        queue = new PriorityQueue<Object>();
        queue.add(new Integer(10));
        try {
            queue.add(new Object());
            fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#remove(Object)
     * 
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "",
        method = "remove",
        args = {java.lang.Object.class}
    )
    public void test_remove_Ljava_lang_Object() {
        Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
        List<Integer> list = Arrays.asList(array);
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>(list);
        assertTrue(integerQueue.remove(16));
        Integer[] newArray = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 39 };
        Arrays.sort(newArray);
        for (int i = 0; i < newArray.length; i++) {
            assertEquals(newArray[i], integerQueue.poll());
        }
        assertEquals(0, integerQueue.size());
    }

    /**
     * @tests java.util.PriorityQueue#remove(Object)
     * 
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "",
        method = "remove",
        args = {java.lang.Object.class}
    )
    public void test_remove_Ljava_lang_Object_using_comparator() {
        PriorityQueue<String> queue = new PriorityQueue<String>(10,
                new MockComparatorStringByLength());
        String[] array = { "AAAAA", "AA", "AAAA", "AAAAAAAA" };
        for (int i = 0; i < array.length; i++) {
            queue.offer(array[i]);
        }
        assertFalse(queue.contains("BB"));
        // Even though "BB" is equivalent to "AA" using the string length comparator, remove()
        // uses equals(), so only "AA" succeeds in removing element "AA".
        assertFalse(queue.remove("BB"));
        assertTrue(queue.remove("AA"));
    }

    /**
     * @tests java.util.PriorityQueue#remove(Object)
     * 
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies ClassCastException.",
        method = "remove",
        args = {java.lang.Object.class}
    )
    public void test_remove_Ljava_lang_Object_not_exists() {
        Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
        List<Integer> list = Arrays.asList(array);
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>(list);
        assertFalse(integerQueue.remove(111));
        assertFalse(integerQueue.remove(null));
        try {
            integerQueue.remove("");
            fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#remove(Object)
     * 
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies null as a parameter.",
        method = "remove",
        args = {java.lang.Object.class}
    )
    public void test_remove_Ljava_lang_Object_null() {
        Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
        List<Integer> list = Arrays.asList(array);
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>(list);
        assertFalse(integerQueue.remove(null));
    }

    /**
     * @tests java.util.PriorityQueue#remove(Object)
     * 
     */
    @TestTargetNew(
        level = TestLevel.PARTIAL_COMPLETE,
        notes = "Verifies ClassCastException.",
        method = "remove",
        args = {java.lang.Object.class}
    )
    public void test_remove_Ljava_lang_Object_not_Compatible() {
        Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
        List<Integer> list = Arrays.asList(array);
        PriorityQueue<Integer> integerQueue = new PriorityQueue<Integer>(list);
        try {
            integerQueue.remove(new Float(1.3F));
            fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }

        // although argument element type is not compatible with those in queue,
        // but comparator supports it.
        MockComparator<Object> comparator = new MockComparator<Object>();
        PriorityQueue<Integer> integerQueue1 = new PriorityQueue<Integer>(100,
                comparator);
        integerQueue1.offer(1);
        assertFalse(integerQueue1.remove(new Float(1.3F)));

        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        Object o = new Object();
        queue.offer(o);
        try {
            queue.remove(o);
            fail("should throw ClassCastException");
        } catch (ClassCastException e) {
            // expected
        }
    }

    /**
     * @tests java.util.PriorityQueue#comparator()
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "",
        method = "comparator",
        args = {}
    )
    public void test_comparator() {
        PriorityQueue<Object> queue = new PriorityQueue<Object>();
        assertNull(queue.comparator());

        MockComparator<Object> comparator = new MockComparator<Object>();
        queue = new PriorityQueue<Object>(100, comparator);
        assertEquals(comparator, queue.comparator());
    }

    /**
     * @tests serialization/deserialization.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies serialization/deserialization.",
        method = "!SerializationSelf",
        args = {}
    )
    public void test_Serialization() throws Exception {
        Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
        List<Integer> list = Arrays.asList(array);
        PriorityQueue<Integer> srcIntegerQueue = new PriorityQueue<Integer>(
                list);
        PriorityQueue<Integer> destIntegerQueue = (PriorityQueue<Integer>) SerializationTester
                .getDeserilizedObject(srcIntegerQueue);
        Arrays.sort(array);
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], destIntegerQueue.poll());
        }
        assertEquals(0, destIntegerQueue.size());
    }

    /**
     * @tests serialization/deserialization.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies serialization/deserialization.",
        method = "!SerializationSelf",
        args = {}
    )    
    public void test_Serialization_casting() throws Exception {
        Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
        List<Integer> list = Arrays.asList(array);
        PriorityQueue<Integer> srcIntegerQueue = new PriorityQueue<Integer>(
                list);
        PriorityQueue<String> destStringQueue = (PriorityQueue<String>) SerializationTester
                .getDeserilizedObject(srcIntegerQueue);
        // will not incur class cast exception.
        Object o = destStringQueue.peek();
        Arrays.sort(array);
        Integer I = (Integer) o;
        assertEquals(array[0], I);
    }

    /**
     * @tests serialization/deserialization compatibility with RI.
     */
    @TestTargetNew(
        level = TestLevel.COMPLETE,
        notes = "Verifies serialization/deserialization compatibility.",
        method = "!SerializationGolden",
        args = {}
    )    
    public void test_SerializationCompatibility_cast() throws Exception {
        Integer[] array = { 2, 45, 7, -12, 9, 23, 17, 1118, 10, 16, 39 };
        List<Integer> list = Arrays.asList(array);
        PriorityQueue<Integer> srcIntegerQueue = new PriorityQueue<Integer>(
                list);
        PriorityQueue<String> destStringQueue = (PriorityQueue<String>) SerializationTester
                .readObject(srcIntegerQueue, SERIALIZATION_FILE_NAME);

        // will not incur class cast exception.
        Object o = destStringQueue.peek();
        Arrays.sort(array);
        Integer I = (Integer) o;
        assertEquals(array[0], I);
    }

    private static class MockComparator<E> implements Comparator<E> {

        public int compare(E object1, E object2) {
            int hashcode1 = object1.hashCode();
            int hashcode2 = object2.hashCode();
            if (hashcode1 > hashcode2) {
                return 1;
            } else if (hashcode1 == hashcode2) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private static class MockComparatorStringByLength implements
            Comparator<String> {

        public int compare(String object1, String object2) {
            int length1 = object1.length();
            int length2 = object2.length();
            if (length1 > length2) {
                return 1;
            } else if (length1 == length2) {
                return 0;
            } else {
                return -1;
            }
        }

    }

    private static class MockComparatorCast<E> implements Comparator<E> {

        public int compare(E object1, E object2) {
            return 0;
        }
    }

}
