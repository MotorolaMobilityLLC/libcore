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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.harmony.luni.internal.nls.Messages;

import tests.support.Support_CollectionTest;
import tests.support.Support_ListTest;
import tests.support.Support_SetTest;
import tests.support.Support_UnmodifiableCollectionTest;
import tests.support.Support_UnmodifiableMapTest;

public class CollectionsTest extends junit.framework.TestCase {

    LinkedList ll;

    LinkedList myll;

    LinkedList reversedLinkedList;

    LinkedList myReversedLinkedList;

    Set s;

    Set mys;

    HashMap hm;

    static Object[] objArray;

    static Object[] myobjArray;
    {
        objArray = new Object[1000];
        myobjArray = new Object[1000];
        for (int i = 0; i < objArray.length; i++) {
            objArray[i] = new Integer(i);
            myobjArray[i] = new MyInt(i);
        }
    }

    public static class ReversedMyIntComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            return -((MyInt) o1).compareTo((MyInt) o2);
        }

        public int equals(Object o1, Object o2) {
            return ((MyInt) o1).compareTo((MyInt) o2);
        }
    }

    public static class SynchCollectionChecker implements Runnable {
        Collection col;

        int colSize;

        int totalToRun;

        boolean offset;

        volatile int numberOfChecks = 0;

        boolean result = true;

        ArrayList normalCountingList;

        ArrayList offsetCountingList;

        public void run() {
            // ensure the list either contains the numbers from 0 to size-1 or
            // the numbers from size to 2*size -1
            while (numberOfChecks < totalToRun) {
                synchronized (col) {
                    if (!(col.isEmpty() || col.containsAll(normalCountingList) || col
                            .containsAll(offsetCountingList)))
                        result = false;
                    col.clear();
                }
                if (offset)
                    col.addAll(offsetCountingList);
                else
                    col.addAll(normalCountingList);
                numberOfChecks++;
            }
        }

        public SynchCollectionChecker(Collection c, boolean offset,
                int totalChecks) {
            // The collection to test, whether to offset the filler values by
            // size or not, and the min number of iterations to run
            totalToRun = totalChecks;
            col = c;
            colSize = c.size();
            normalCountingList = new ArrayList(colSize);
            offsetCountingList = new ArrayList(colSize);
            for (int counter = 0; counter < colSize; counter++)
                normalCountingList.add(new Integer(counter));
            for (int counter = 0; counter < colSize; counter++)
                offsetCountingList.add(new Integer(counter + colSize));
            col.clear();
            if (offset)
                col.addAll(offsetCountingList);
            else
                col.addAll(normalCountingList);
        }

        public boolean offset() {
            // answer true iff the list is filled with a counting sequence
            // starting at the value size to 2*size - 1
            // else the list with be filled starting at 0 to size - 1
            return offset;
        }

        public boolean getResult() {
            // answer true iff no corruption has been found in the collection
            return result;
        }

        public int getNumberOfChecks() {
            // answer the number of checks that have been performed on the list
            return numberOfChecks;
        }
    }

    public static class SynchMapChecker implements Runnable {
        Map map;

        int mapSize;

        int totalToRun;

        boolean offset;

        volatile int numberOfChecks = 0;

        boolean result = true;

        Map normalCountingMap;

        Map offsetCountingMap;

        public void run() {
            Object firstNormalValue = normalCountingMap.get(new Integer(0));
            Object lastNormalValue = normalCountingMap.get(new Integer(
                    mapSize - 1));
            Object firstOffsetValue = offsetCountingMap
                    .get(new Integer(mapSize));
            Object lastOffsetValue = offsetCountingMap.get(new Integer(
                    2 * mapSize - 1));
            // ensure the list either contains the numbers from 0 to size-1 or
            // the numbers from size to 2*size -1
            while (numberOfChecks < totalToRun) {
                synchronized (map) {
                    if (!(map.isEmpty()
                            || (map.containsValue(firstNormalValue) && map
                                    .containsValue(lastNormalValue)) || (map
                            .containsValue(firstOffsetValue) && map
                            .containsValue(lastOffsetValue))))
                        result = false;
                    map.clear();
                }
                if (offset)
                    map.putAll(offsetCountingMap);
                else
                    map.putAll(normalCountingMap);
                numberOfChecks++;
            }
        }

        public SynchMapChecker(Map m, boolean offset, int totalChecks) {
            // The collection to test, whether to offset the filler values by
            // size or not, and the min number of iterations to run
            Integer myInt;
            totalToRun = totalChecks;
            map = m;
            mapSize = m.size();
            normalCountingMap = new HashMap(mapSize);
            offsetCountingMap = new HashMap(mapSize);
            for (int counter = 0; counter < mapSize; counter++) {
                myInt = new Integer(counter);
                normalCountingMap.put(myInt, myInt);
            }
            for (int counter = 0; counter < mapSize; counter++) {
                myInt = new Integer(counter + mapSize);
                offsetCountingMap.put(myInt, myInt);
            }
            map.clear();
            if (offset)
                map.putAll(offsetCountingMap);
            else
                map.putAll(normalCountingMap);
        }

        public boolean offset() {
            // answer true iff the list is filled with a counting sequence
            // starting at the value size to 2*size - 1
            // else the list with be filled starting at 0 to size - 1
            return offset;
        }

        public boolean getResult() {
            // answer true iff no corruption has been found in the collection
            return result;
        }

        public int getNumberOfChecks() {
            // answer the number of checks that have been performed on the list
            return numberOfChecks;
        }
    }

    public static class CollectionTest extends junit.framework.TestCase {

        Collection col; // must contain the Integers 0 to 99

        public CollectionTest(String p1) {
            super(p1);
        }

        public CollectionTest(String p1, Collection c) {
            super(p1);
            col = c;
        }

    }

    static class MyInt {
        int data;

        public MyInt(int value) {
            data = value;
        }

        public int compareTo(MyInt object) {
            return data > object.data ? 1 : (data < object.data ? -1 : 0);
        }
    }

    /**
     * @tests java.util.Collections#binarySearch(java.util.List,
     *        java.lang.Object)
     */
    public void test_binarySearchLjava_util_ListLjava_lang_Object() {
        // Test for method int
        // java.util.Collections.binarySearch(java.util.List, java.lang.Object)
        // assumes ll is sorted and has no duplicate keys
        final int llSize = ll.size();
        // Ensure a NPE is thrown if the list is NULL
        try {
            Collections.binarySearch(null, new Object());
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }
        for (int counter = 0; counter < llSize; counter++) {
            assertTrue("Returned incorrect binary search item position", ll
                    .get(Collections.binarySearch(ll, ll.get(counter))) == ll
                    .get(counter));
        }
    }

    /**
     * @tests java.util.Collections#binarySearch(java.util.List,
     *        java.lang.Object, java.util.Comparator)
     */
    public void test_binarySearchLjava_util_ListLjava_lang_ObjectLjava_util_Comparator() {
        // Test for method int
        // java.util.Collections.binarySearch(java.util.List, java.lang.Object,
        // java.util.Comparator)
        // assumes reversedLinkedList is sorted in reversed order and has no
        // duplicate keys
        final int rSize = myReversedLinkedList.size();
        ReversedMyIntComparator comp = new ReversedMyIntComparator();
        // Ensure a NPE is thrown if the list is NULL
        try {
            Collections.binarySearch(null, new Object(), comp);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }
        for (int counter = 0; counter < rSize; counter++) {
            assertTrue(
                    "Returned incorrect binary search item position using custom comparator",
                    myReversedLinkedList.get(Collections.binarySearch(
                            myReversedLinkedList, myReversedLinkedList
                                    .get(counter), comp)) == myReversedLinkedList
                            .get(counter));
        }
    }

    /**
     * @tests java.util.Collections#copy(java.util.List, java.util.List)
     */
    public void test_copyLjava_util_ListLjava_util_List() {
        // Test for method void java.util.Collections.copy(java.util.List,
        // java.util.List)
        // Ensure a NPE is thrown if the list is NULL
        try {
            Collections.copy(null, ll);
            fail("Expected NullPointerException for null list first parameter");
        } catch (NullPointerException e) {
        }
        try {
            Collections.copy(ll, null);
            fail("Expected NullPointerException for null list second parameter");
        } catch (NullPointerException e) {
        }
        final int llSize = ll.size();
        ll.set(25, null);
        ArrayList al = new ArrayList();
        Integer extraElement = new Integer(1);
        Integer extraElement2 = new Integer(2);
        al.addAll(myReversedLinkedList);
        al.add(extraElement);
        al.add(extraElement2);
        Collections.copy(al, ll);
        for (int counter = 0; counter < llSize; counter++) {
            assertTrue("Elements do not match after copying collection", al
                    .get(counter) == ll.get(counter));
        }
        assertTrue("Elements after copied elements affected by copy",
                extraElement == al.get(llSize)
                        && extraElement2 == al.get(llSize + 1));
    }

    /**
     * @tests java.util.Collections#copy(java.util.List, java.util.List)
     */
    public void test_copy_check_index() {
        ArrayList a1 = new ArrayList();
        a1.add("one");
        a1.add("two");

        ArrayList a2 = new ArrayList();
        a2.add("aa");

        try {
            Collections.copy(a2, a1);
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
        }

        assertEquals("aa", a2.get(0));
    }

    /**
     * @tests java.util.Collections#enumeration(java.util.Collection)
     */
    public void test_enumerationLjava_util_Collection() {
        // Test for method java.util.Enumeration
        // java.util.Collections.enumeration(java.util.Collection)
        TreeSet ts = new TreeSet();
        ts.addAll(s);
        Enumeration e = Collections.enumeration(ts);
        int count = 0;
        while (e.hasMoreElements())
            assertTrue("Returned incorrect enumeration",
                    e.nextElement() == objArray[count++]);
        assertTrue("Enumeration missing elements: " + count,
                count == objArray.length);
    }

    /**
     * @tests java.util.Collections#fill(java.util.List, java.lang.Object)
     */
    public void test_fillLjava_util_ListLjava_lang_Object() {
        // Test for method void java.util.Collections.fill(java.util.List,
        // java.lang.Object)
        try {
            Collections.fill(null, new Object());
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }
        final int size = ll.size();
        Collections.fill(ll, "k");
        assertTrue("Fill modified list size", size == ll.size());
        Iterator i = ll.iterator();
        while (i.hasNext())
            assertEquals("Failed to fill elements", "k", i.next());

        Collections.fill(ll, null);
        assertTrue("Fill with nulls modified list size", size == ll.size());
        i = ll.iterator();
        while (i.hasNext())
            assertNull("Failed to fill with nulls", i.next());
    }

    /**
     * @tests java.util.Collections#max(java.util.Collection)
     */
    public void test_maxLjava_util_Collection() {
        // Test for method java.lang.Object
        // java.util.Collections.max(java.util.Collection)
        // assumes s, objArray are sorted
        assertTrue("Returned incorrect max element",
                Collections.max(s) == objArray[objArray.length - 1]);
    }

    /**
     * @tests java.util.Collections#max(java.util.Collection,
     *        java.util.Comparator)
     */
    public void test_maxLjava_util_CollectionLjava_util_Comparator() {
        // Test for method java.lang.Object
        // java.util.Collections.max(java.util.Collection, java.util.Comparator)
        // assumes s, objArray are sorted

        // With this custom (backwards) comparator the 'max' element should be
        // the smallest in the list
        assertTrue(
                "Returned incorrect max element using custom comparator",
                Collections.max(mys, new ReversedMyIntComparator()) == myobjArray[0]);
    }

    /**
     * @tests java.util.Collections#min(java.util.Collection)
     */
    public void test_minLjava_util_Collection() {
        // Test for method java.lang.Object
        // java.util.Collections.min(java.util.Collection)
        // assumes s, objArray are sorted
        assertTrue("Returned incorrect min element",
                Collections.min(s) == objArray[0]);
    }

    /**
     * @tests java.util.Collections#min(java.util.Collection,
     *        java.util.Comparator)
     */
    public void test_minLjava_util_CollectionLjava_util_Comparator() {
        // Test for method java.lang.Object
        // java.util.Collections.min(java.util.Collection, java.util.Comparator)
        // assumes s, objArray are sorted

        // With this custom (backwards) comparator the 'min' element should be
        // the largest in the list
        assertTrue(
                "Returned incorrect min element using custom comparator",
                Collections.min(mys, new ReversedMyIntComparator()) == myobjArray[objArray.length - 1]);
    }

    /**
     * @tests java.util.Collections#nCopies(int, java.lang.Object)
     */
    public void test_nCopiesILjava_lang_Object() {
        // Test for method java.util.List java.util.Collections.nCopies(int,
        // java.lang.Object)
        Object o = new Object();
        List l = Collections.nCopies(100, o);
        Iterator i = l.iterator();
        Object first = i.next();
        assertTrue("Returned list consists of copies not refs", first == o);
        assertEquals("Returned list of incorrect size", 100, l.size());
        assertTrue("Contains", l.contains(o));
        assertTrue("Contains null", !l.contains(null));
        assertTrue("null nCopies contains", !Collections.nCopies(2, null)
                .contains(o));
        assertTrue("null nCopies contains null", Collections.nCopies(2, null)
                .contains(null));
        l = Collections.nCopies(20, null);
        i = l.iterator();
        for (int counter = 0; i.hasNext(); counter++) {
            assertTrue("List is too large", counter < 20);
            assertNull("Element should be null: " + counter, i.next());
        }
        try {
            l.add(o);
            fail("Returned list is not immutable");
        } catch (UnsupportedOperationException e) {
            // Correct
            return;
        }
        try {
            Collections.nCopies(-2, new HashSet());
            fail("nCopies with negative arg didn't throw IAE");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    /**
     * @tests java.util.Collections#reverse(java.util.List)
     */
    public void test_reverseLjava_util_List() {
        // Test for method void java.util.Collections.reverse(java.util.List)
        try {
            Collections.reverse(null);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }
        Collections.reverse(ll);
        Iterator i = ll.iterator();
        int count = objArray.length - 1;
        while (i.hasNext()) {
            assertTrue("Failed to reverse collection",
                    i.next() == objArray[count]);
            --count;
        }
        ArrayList myList = new ArrayList();
        myList.add(null);
        myList.add(new Integer(20));
        Collections.reverse(myList);
        assertTrue("Did not reverse correctly--first element is: "
                + myList.get(0), myList.get(0).equals(new Integer(20)));
        assertNull("Did not reverse correctly--second element is: "
                + myList.get(1), myList.get(1));
    }

    /**
     * @tests java.util.Collections#reverseOrder()
     */
    public void test_reverseOrder() {
        // Test for method java.util.Comparator
        // java.util.Collections.reverseOrder()
        // assumes no duplicates in ll
        Comparator comp = Collections.reverseOrder();
        LinkedList list2 = new LinkedList(ll);
        Collections.sort(list2, comp);
        final int llSize = ll.size();
        for (int counter = 0; counter < llSize; counter++)
            assertTrue("New comparator does not reverse sorting order", ll
                    .get(counter) == list2.get(llSize - counter - 1));
    }

    /**
     * @tests java.util.Collections#shuffle(java.util.List)
     */
    public void test_shuffleLjava_util_List() {
        // Test for method void java.util.Collections.shuffle(java.util.List)
        // Assumes ll is sorted and has no duplicate keys and is large ( > 20
        // elements)

        // test shuffling a Sequential Access List
        try {
            Collections.shuffle(null);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }
        ArrayList al = new ArrayList();
        al.addAll(ll);
        testShuffle(al, "Sequential Access", false);

        // test shuffling a Random Access List
        LinkedList ll2 = new LinkedList();
        ll2.addAll(ll);
        testShuffle(ll2, "Random Access", false);
    }

    private void testShuffle(List list, String type, boolean random) {
        boolean sorted = true;
        boolean allMatch = true;
        int index = 0;
        final int size = list.size();

        if (random)
            Collections.shuffle(list);
        else
            Collections.shuffle(list, new Random(200));

        for (int counter = 0; counter < size - 1; counter++) {
            if (((Integer) list.get(counter)).compareTo((Integer)list.get(counter + 1)) > 0) {
                sorted = false;
            }
        }
        assertTrue("Shuffling sorted " + type
                + " list resulted in sorted list (should be unlikely)", !sorted);
        for (int counter = 0; counter < 20; counter++) {
            index = 30031 * counter % (size + 1); // 30031 is a large prime
            if (list.get(index) != ll.get(index))
                allMatch = false;
        }
        assertTrue("Too many element positions match in shuffled " + type
                + " list", !allMatch);
    }

    /**
     * @tests java.util.Collections#shuffle(java.util.List, java.util.Random)
     */
    public void test_shuffleLjava_util_ListLjava_util_Random() {
        // Test for method void java.util.Collections.shuffle(java.util.List,
        // java.util.Random)
        // Assumes ll is sorted and has no duplicate keys and is large ( > 20
        // elements)

        // test shuffling a Sequential Access List
        try {
            Collections.shuffle(null, new Random(200));
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }
        ArrayList al = new ArrayList();
        al.addAll(ll);
        testShuffle(al, "Sequential Access", true);

        // test shuffling a Random Access List
        LinkedList ll2 = new LinkedList();
        ll2.addAll(ll);
        testShuffle(ll2, "Random Access", true);
    }

    /**
     * @tests java.util.Collections#singleton(java.lang.Object)
     */
    public void test_singletonLjava_lang_Object() {
        // Test for method java.util.Set
        // java.util.Collections.singleton(java.lang.Object)
        Object o = new Object();
        Set single = Collections.singleton(o);
        assertEquals("Wrong size", 1, single.size());
        assertTrue("Contains", single.contains(o));
        assertTrue("Contains null", !single.contains(null));
        assertTrue("null nCopies contains", !Collections.singleton(null)
                .contains(o));
        assertTrue("null nCopies contains null", Collections.singleton(null)
                .contains(null));
        try {
            single.add("l");
        } catch (UnsupportedOperationException e) {
            // Correct
            return;
        }
        fail("Allowed modification of singleton");
    }

    /**
     * @tests java.util.Collections#sort(java.util.List)
     */
    public void test_sortLjava_util_List() {
        // Test for method void java.util.Collections.sort(java.util.List)
        // assumes no duplicate keys in ll
        final int llSize = ll.size();
        final int rllSize = reversedLinkedList.size();
        try {
                        Collections.sort((List)null);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }
        Collections.shuffle(ll);
        Collections.sort(ll);
        Collections.sort(reversedLinkedList);
        for (int counter = 0; counter < llSize - 1; counter++) {
            assertTrue(
                    "Sorting shuffled list resulted in unsorted list",
                    ((Integer) ll.get(counter)).compareTo((Integer)ll.get(counter + 1)) < 0);
        }

        for (int counter = 0; counter < rllSize - 1; counter++) {
            assertTrue("Sorting reversed list resulted in unsorted list",
                    ((Integer) reversedLinkedList.get(counter))
                            .compareTo((Integer)reversedLinkedList.get(counter + 1)) < 0);
        }
    }

    /**
     * @tests java.util.Collections#sort(java.util.List, java.util.Comparator)
     */
    public void test_sortLjava_util_ListLjava_util_Comparator() {
        // Test for method void java.util.Collections.sort(java.util.List,
        // java.util.Comparator)
        Comparator comp = new ReversedMyIntComparator();
        try {
            Collections.sort(null, comp);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }
        Collections.shuffle(myll);
        Collections.sort(myll, comp);
        final int llSize = myll.size();

        for (int counter = 0; counter < llSize - 1; counter++) {
            assertTrue(
                    "Sorting shuffled list with custom comparator resulted in unsorted list",
                    ((MyInt) myll.get(counter)).compareTo((MyInt) myll
                            .get(counter + 1)) >= 0);
        }
    }

    /**
     * @tests java.util.Collections#swap(java.util.List, int, int)
     */
    public void test_swapLjava_util_ListII() {
        // Test for method swap(java.util.List, int, int)

        LinkedList smallList = new LinkedList();
        for (int i = 0; i < 10; i++) {
            smallList.add(objArray[i]);
        }

        // test exception cases
        try {
            Collections.swap(smallList, -1, 6);
            fail("Expected IndexOutOfBoundsException for -1");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            Collections.swap(smallList, 6, -1);
            fail("Expected IndexOutOfBoundsException for -1");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            Collections.swap(smallList, 6, 11);
            fail("Expected IndexOutOfBoundsException for 11");
        } catch (IndexOutOfBoundsException e) {
        }

        try {
            Collections.swap(smallList, 11, 6);
            fail("Expected IndexOutOfBoundsException for 11");
        } catch (IndexOutOfBoundsException e) {
        }

        // Ensure a NPE is thrown if the list is NULL
        try {
            Collections.swap(null, 1, 1);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }

        // test with valid parameters
        Collections.swap(smallList, 4, 7);
        assertEquals("Didn't Swap the element at position 4 ", new Integer(7),
                smallList.get(4));
        assertEquals("Didn't Swap the element at position 7 ", new Integer(4),
                smallList.get(7));

        // make sure other elements didn't get swapped by mistake
        for (int i = 0; i < 10; i++) {
            if (i != 4 && i != 7)
                assertEquals("shouldn't have swapped the element at position "
                        + i, new Integer(i), smallList.get(i));
        }
    }

    /**
     * @tests java.util.Collections#replaceAll(java.util.List, java.lang.Object,
     *        java.lang.Object)
     */
    public void test_replaceAllLjava_util_ListLjava_lang_ObjectLjava_lang_Object() {
        // Test for method replaceAll(java.util.List, java.lang.Object,
        // java.lang.Object)

        String string1 = "A-B-C-D-E-S-JF-SUB-G-H-I-J-SUBL-K-L-LIST-M-N--S-S-O-SUBLIS-P-Q-R-SUBLIST-S-T-U-V-W-X-Y-Z";
        char[] chars = string1.toCharArray();
        List list = new ArrayList();
        for (int i = 0; i < chars.length; i++) {
            list.add(new Character(chars[i]));
        }

        try {
            Collections.replaceAll(null, new Object(), new Object());
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }

        // test replace for an element that is not in the list
        boolean result = Collections.replaceAll(list, new Character('1'),
                new Character('Z'));
        assertFalse("Test1: Collections.replaceAll() returned wrong result",
                result);
        assertEquals("Test2 : ReplaceAll modified the list incorrectly",
                string1, getString(list));

        // test replace for an element that is in the list
        result = Collections.replaceAll(list, new Character('S'),
                new Character('K'));
        assertTrue("Test3: Collections.replaceAll() returned wrong result",
                result);
        assertEquals("Test4: ReplaceAll modified the list incorrectly",
                (string1 = string1.replace('S', 'K')), getString(list));

        // test replace for the last element in the list
        result = Collections.replaceAll(list, new Character('Z'),
                new Character('N'));
        assertTrue("Test5: Collections.replaceAll() returned wrong result",
                result);
        assertEquals("Test6: ReplaceAll modified the list incorrectly",
                (string1 = string1.replace('Z', 'N')), getString(list));

        // test replace for the first element in the list
        result = Collections.replaceAll(list, new Character('A'),
                new Character('B'));
        assertTrue("Test7: Collections.replaceAll() returned wrong result",
                result);
        assertEquals("Test8: ReplaceAll modified the list incorrectly",
                (string1 = string1.replace('A', 'B')), getString(list));

        // test replacing elements with null
        LinkedList smallList = new LinkedList();
        for (int i = 0; i < 10; i++) {
            smallList.add(objArray[i]);
        }
        smallList.set(4, new Integer(5));
        result = Collections.replaceAll(smallList, new Integer(5), null);
        assertTrue("Test9: Collections.replaceAll() returned wrong result",
                result);
        for (int i = 0; i < smallList.size(); i++) {
            if (i == 4 || i == 5)
                assertSame("Test9: ReplaceAll didn't replace element at " + i,
                        null, smallList.get(i));
            else
                assertEquals(
                        "Test9: ReplaceAll shouldn't have replaced element at "
                                + i, new Integer(i), smallList.get(i));
        }

        // test replacing null elements with another value
        result = Collections.replaceAll(smallList, null, new Integer(99));
        assertTrue("Test10: Collections.replaceAll() returned wrong result",
                result);

        for (int i = 0; i < smallList.size(); i++) {
            if (i == 4 || i == 5)
                assertEquals("Test10: ReplaceAll didn't replace element at "
                        + i, new Integer(99), smallList.get(i));
            else
                assertEquals(
                        "Test10: ReplaceAll shouldn't have replaced element at "
                                + i, new Integer(i), smallList.get(i));
        }
    }

    /**
     * @tests java.util.Collections#rotate(java.util.List, int)
     */
    public void test_rotateLjava_util_ListI() {
        // Test for method rotate(java.util.List, int)

        try {
            Collections.rotate(null, 0);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }

        // Test rotating a Sequential Access List
        LinkedList list1 = new LinkedList();
        for (int i = 0; i < 10; i++) {
            list1.add(objArray[i]);
        }
        testRotate(list1, "Sequential Access");

        // Test rotating a Random Access List
        ArrayList list2 = new ArrayList();
        for (int i = 0; i < 10; i++) {
            list2.add(objArray[i]);
        }
        testRotate(list2, "Random Access");
    }

    private void testRotate(List list, String type) {
        // rotate with positive distance
        Collections.rotate(list, 7);
        assertEquals("Test1: rotate modified the " + type
                + " list incorrectly,", "3456789012", getString(list));

        // rotate with negative distance
        Collections.rotate(list, -2);
        assertEquals("Test2: rotate modified the " + type
                + " list incorrectly,", "5678901234", getString(list));

        // rotate sublist with negative distance
        List subList = list.subList(1, 5);
        Collections.rotate(subList, -1);
        assertEquals("Test3: rotate modified the " + type
                + " list incorrectly,", "5789601234", getString(list));

        // rotate sublist with positive distance
        Collections.rotate(subList, 2);
        assertEquals("Test4: rotate modified the " + type
                + " list incorrectly,", "5967801234", getString(list));

        // rotate with positive distance that is larger than list size
        Collections.rotate(list, 23);
        assertEquals("Test5: rotate modified the " + type
                + " list incorrectly,", "2345967801", getString(list));

        // rotate with negative distance that is larger than list size
        Collections.rotate(list, -23);
        assertEquals("Test6: rotate modified the " + type
                + " list incorrectly,", "5967801234", getString(list));

        // rotate with 0 and equivalent distances, this should make no
        // modifications to the list
        Collections.rotate(list, 0);
        assertEquals("Test7: rotate modified the " + type
                + " list incorrectly,", "5967801234", getString(list));

        Collections.rotate(list, -30);
        assertEquals("Test8: rotate modified the " + type
                + " list incorrectly,", "5967801234", getString(list));

        Collections.rotate(list, 30);
        assertEquals("Test9: rotate modified the " + type
                + " list incorrectly,", "5967801234", getString(list));
    }

    private String getString(List list) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            buffer.append(list.get(i));
        }
        return buffer.toString();
    }

    /**
     * @tests java.util.Collections#rotate(java.util.List, int)
     */
    public void test_rotate2() {
        List list = new ArrayList();
        try {
            Collections.rotate(list, 5);
        } catch (UnsupportedOperationException e) {
            fail("Unexpected UnsupportedOperationException for empty list, "
                    + e);
        }

        list.add(0, "zero");
        list.add(1, "one");
        list.add(2, "two");
        list.add(3, "three");
        list.add(4, "four");

        Collections.rotate(list, Integer.MIN_VALUE);
        assertEquals("Rotated incorrectly at position 0, ", "three",
                (String) list.get(0));
        assertEquals("Rotated incorrectly at position 1, ", "four",
                (String) list.get(1));
        assertEquals("Rotated incorrectly at position 2, ", "zero",
                (String) list.get(2));
        assertEquals("Rotated incorrectly at position 3, ", "one",
                (String) list.get(3));
        assertEquals("Rotated incorrectly at position 4, ", "two",
                (String) list.get(4));
    }

    /**
     * @tests java.util.Collections#indexOfSubList(java.util.List,
     *        java.util.List)
     */
    public void test_indexOfSubListLjava_util_ListLjava_util_List() {
        // Test for method int indexOfSubList(java.util.List, java.util.List)
        List list = new ArrayList();
        try {
            Collections.indexOfSubList(null, list);
            fail("Expected NullPointerException for null list first parameter");
        } catch (NullPointerException e) {
        }
        try {
            Collections.indexOfSubList(list, null);
            fail("Expected NullPointerException for null list second parameter");
        } catch (NullPointerException e) {
        }

        String string1 = "A-B-C-D-E-S-JF-SUB-G-H-I-J-SUBL-K-L-LIST-M-N--S-S-O-SUBLIS-P-Q-R-SUBLIST-S-T-U-V-W-X-Y-Z";

        testwithCharList(1, string1, "B", true);
        testwithCharList(2, string1, "LIST", true);
        testwithCharList(3, string1, "SUBLIST", true);
        testwithCharList(4, string1, "NONE", true);
        testwithCharList(5, string1, "END", true);

        // test boundary conditions:
        testwithCharList(6, "", "", true);
        testwithCharList(7, "LIST", "", true);
        testwithCharList(8, "", "SUBLIST", true);
    }

    /**
     * @tests java.util.Collections#indexOfSubList(java.util.List,
     *        java.util.List)
     */
    public void test_indexOfSubList2() {
        ArrayList sub = new ArrayList();
        sub.add(new Integer(1));
        sub.add(new Integer(2));
        sub.add(new Integer(3));

        ArrayList sub2 = new ArrayList();
        sub2.add(new Integer(7));
        sub2.add(new Integer(8));

        ArrayList src = new ArrayList();
        src.addAll(sub);
        src.addAll(sub);
        src.addAll(sub);
        src.add(new Integer(5));
        src.add(new Integer(6));

        // so src becomes a list like this:
        // [1, 2, 3, 1, 2, 3, 1, 2, 3, 5, 6]

        sub = new ArrayList(src.subList(3, 11));
        // [1, 2, 3, 1, 2, 3, 5, 6]
        assertEquals("TestA : Returned wrong indexOfSubList, ", 3, Collections
                .indexOfSubList(src, sub));

        sub = new ArrayList(src.subList(6, 11));
        // [1, 2, 3, 5, 6]
        assertEquals("TestB : Returned wrong indexOfSubList, ", 6, Collections
                .indexOfSubList(src, sub));

        sub = new ArrayList(src.subList(0, 3));
        // [1, 2, 3]
        assertEquals("TestCC : Returned wrong indexOfSubList, ", 0, Collections
                .indexOfSubList(src, sub));

        sub = new ArrayList(src.subList(9, 11));
        // [5, 6]
        assertEquals("TestD : Returned wrong indexOfSubList, ", 9, Collections
                .indexOfSubList(src, sub));

        sub = new ArrayList(src.subList(10, 11));
        // [6]
        assertEquals("TestE : Returned wrong indexOfSubList, ", 10, Collections
                .indexOfSubList(src, sub));

        sub = new ArrayList(src.subList(0, 11));
        // the whole list
        assertEquals("TestH : Returned wrong indexIndexOfSubList, ", 0,
                Collections.indexOfSubList(src, sub));

        // a non-matching list
        assertEquals("TestI : Returned wrong indexOfSubList, ", -1, Collections
                .indexOfSubList(src, sub2));
    }

    /**
     * @param string2
     * @param string1
     * @param index
     */
    private void testwithCharList(int count, String string1, String string2,
            boolean first) {
        char[] chars = string1.toCharArray();
        List list = new ArrayList();
        for (int i = 0; i < chars.length; i++) {
            list.add(new Character(chars[i]));
        }
        chars = string2.toCharArray();
        List sublist = new ArrayList();
        for (int i = 0; i < chars.length; i++) {
            sublist.add(new Character(chars[i]));
        }

        if (first)
            assertEquals("Test " + count + ": Returned wrong index:", string1
                    .indexOf(string2), Collections
                    .indexOfSubList(list, sublist));
        else
            assertEquals("Test " + count + ": Returned wrong index:", string1
                    .lastIndexOf(string2), Collections.lastIndexOfSubList(list,
                    sublist));
    }

    /**
     * @tests java.util.Collections#lastIndexOfSubList(java.util.List,
     *        java.util.List)
     */
    public void test_lastIndexOfSubListLjava_util_ListLjava_util_List() {
        // Test for method int lastIndexOfSubList(java.util.List,
        // java.util.List)
        String string1 = "A-B-C-D-E-S-JF-SUB-G-H-I-J-SUBL-K-L-LIST-M-N--S-S-O-SUBLIS-P-Q-R-SUBLIST-S-T-U-V-W-X-Y-Z-END";

        List list = new ArrayList();
        try {
            Collections.lastIndexOfSubList(null, list);
            fail("Expected NullPointerException for null list first parameter");
        } catch (NullPointerException e) {
        }
        try {
            Collections.lastIndexOfSubList(list, null);
            fail("Expected NullPointerException for null list second parameter");
        } catch (NullPointerException e) {
        }

        testwithCharList(1, string1, "B", false);
        testwithCharList(2, string1, "LIST", false);
        testwithCharList(3, string1, "SUBLIST", false);
        testwithCharList(4, string1, "END", false);
        testwithCharList(5, string1, "NONE", false);

        // test boundary conditions
        testwithCharList(6, "", "", false);
        testwithCharList(7, "LIST", "", false);
        testwithCharList(8, "", "SUBLIST", false);
    }

    /**
     * @tests java.util.Collections#lastIndexOfSubList(java.util.List,
     *        java.util.List)
     */
    public void test_lastIndexOfSubList2() {
        ArrayList sub = new ArrayList();
        sub.add(new Integer(1));
        sub.add(new Integer(2));
        sub.add(new Integer(3));

        ArrayList sub2 = new ArrayList();
        sub2.add(new Integer(7));
        sub2.add(new Integer(8));

        ArrayList src = new ArrayList();
        src.addAll(sub);
        src.addAll(sub);
        src.addAll(sub);
        src.add(new Integer(5));
        src.add(new Integer(6));

        // so src is a list like this:
        // [1, 2, 3, 1, 2, 3, 1, 2, 3, 5, 6]

        Collections.reverse(src);
        // it becomes like this :
        // [6, 5, 3, 2, 1, 3, 2, 1, 3, 2, 1]

        sub = new ArrayList(src.subList(0, 8));
        // [6, 5, 3, 2, 1, 3, 2, 1]
        assertEquals("TestA : Returned wrong lastIndexOfSubList, ", 0,
                Collections.lastIndexOfSubList(src, sub));

        sub = new ArrayList(src.subList(0, 5));
        // [6, 5, 3, 2, 1]
        assertEquals("TestB : Returned wrong lastIndexOfSubList, ", 0,
                Collections.lastIndexOfSubList(src, sub));

        sub = new ArrayList(src.subList(2, 5));
        // [3, 2, 1]
        assertEquals("TestC : Returned wrong lastIndexOfSubList, ", 8,
                Collections.lastIndexOfSubList(src, sub));

        sub = new ArrayList(src.subList(9, 11));
        // [2, 1]
        assertEquals("TestD : Returned wrong lastIndexOfSubList, ", 9,
                Collections.lastIndexOfSubList(src, sub));

        sub = new ArrayList(src.subList(10, 11));
        // [1]
        assertEquals("TestE : Returned wrong lastIndexOfSubList, ", 10,
                Collections.lastIndexOfSubList(src, sub));

        sub = new ArrayList(src.subList(0, 2));
        // [6, 5]
        assertEquals("TestF : Returned wrong lastIndexOfSubList, ", 0,
                Collections.lastIndexOfSubList(src, sub));

        sub = new ArrayList(src.subList(0, 1));
        // [6]
        assertEquals("TestG : Returned wrong lastIndexOfSubList, ", 0,
                Collections.lastIndexOfSubList(src, sub));

        sub = new ArrayList(src.subList(0, 11));
        // the whole list
        assertEquals("TestH : Returned wrong lastIndexOfSubList, ", 0,
                Collections.lastIndexOfSubList(src, sub));

        // a non-matching list
        assertEquals("TestI : Returned wrong lastIndexOfSubList, ", -1,
                Collections.lastIndexOfSubList(src, sub2));
    }

    /**
     * @tests java.util.Collections#list(java.util.Enumeration)
     */
    public void test_listLjava_util_Enumeration() {
        // Test for method java.util.ArrayList list(java.util.Enumeration)

        Enumeration e = Collections.enumeration(ll);
        ArrayList al = Collections.list(e);

        int size = al.size();
        assertEquals("Wrong size", ll.size(), size);

        for (int i = 0; i < size; i++) {
            assertEquals("wrong element at position " + i + ",", ll.get(i), al
                    .get(i));
        }
    }

    /**
     * @tests java.util.Collections#synchronizedCollection(java.util.Collection)
     */
    public void test_synchronizedCollectionLjava_util_Collection() {
        // Test for method java.util.Collection
        // java.util.Collections.synchronizedCollection(java.util.Collection)

        LinkedList smallList = new LinkedList();
        for (int i = 0; i < 50; i++) {
            smallList.add(objArray[i]);
        }

        final int numberOfLoops = 200;
        Collection synchCol = Collections.synchronizedCollection(smallList);
        // Replacing the previous line with the line below *should* cause the
        // test to fail--the collecion below isn't synchronized
        // Collection synchCol = smallList;

        SynchCollectionChecker normalSynchChecker = new SynchCollectionChecker(
                synchCol, false, numberOfLoops);
        SynchCollectionChecker offsetSynchChecker = new SynchCollectionChecker(
                synchCol, true, numberOfLoops);
        Thread normalThread = new Thread(normalSynchChecker);
        Thread offsetThread = new Thread(offsetSynchChecker);
        normalThread.start();
        offsetThread.start();
        while ((normalSynchChecker.getNumberOfChecks() < numberOfLoops)
                || (offsetSynchChecker.getNumberOfChecks() < numberOfLoops)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        assertTrue("Returned collection corrupted by multiple thread access",
                normalSynchChecker.getResult()
                        && offsetSynchChecker.getResult());
        try {
            normalThread.join(5000);
            offsetThread.join(5000);
        } catch (InterruptedException e) {
            fail("join() interrupted");
        }

        synchCol.add(null);
        assertTrue("Trying to use nulls in collection failed", synchCol
                .contains(null));

        smallList = new LinkedList();
        for (int i = 0; i < 100; i++) {
            smallList.add(objArray[i]);
        }
        new Support_CollectionTest("", Collections
                .synchronizedCollection(smallList)).runTest();
    }

    /**
     * @tests java.util.Collections#synchronizedList(java.util.List)
     */
    public void test_synchronizedListLjava_util_List() {
        try {
            Collections.synchronizedList(null);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }

        // test with a Sequential Access List
        List smallList = new LinkedList();
        testSynchronizedList(smallList, "Sequential Access");

        smallList = new LinkedList();
        List myList;
        for (int i = 0; i < 100; i++) {
            smallList.add(objArray[i]);
        }
        myList = Collections.synchronizedList(smallList);
        new Support_ListTest("", myList).runTest();

        // test with a Random Access List
        smallList = new ArrayList();
        testSynchronizedList(smallList, "Random Access");

        smallList = new ArrayList();
        for (int i = 0; i < 100; i++) {
            smallList.add(objArray[i]);
        }
        myList = Collections.synchronizedList(smallList);
        new Support_ListTest("", myList).runTest();
    }

    private void testSynchronizedList(List smallList, String type) {
        for (int i = 0; i < 50; i++) {
            smallList.add(objArray[i]);
        }
        final int numberOfLoops = 200;
        List synchList = Collections.synchronizedList(smallList);
        if (type.equals("Random Access"))
            assertTrue(
                    "Returned synchronized list should implement the Random Access interface",
                    synchList instanceof RandomAccess);
        else
            assertTrue(
                    "Returned synchronized list should not implement the Random Access interface",
                    !(synchList instanceof RandomAccess));

        // Replacing the previous line with the line below *should* cause the
        // test to fail--the list below isn't synchronized
        // List synchList = smallList;
        SynchCollectionChecker normalSynchChecker = new SynchCollectionChecker(
                synchList, false, numberOfLoops);
        SynchCollectionChecker offsetSynchChecker = new SynchCollectionChecker(
                synchList, true, numberOfLoops);
        Thread normalThread = new Thread(normalSynchChecker);
        Thread offsetThread = new Thread(offsetSynchChecker);
        normalThread.start();
        offsetThread.start();
        while ((normalSynchChecker.getNumberOfChecks() < numberOfLoops)
                || (offsetSynchChecker.getNumberOfChecks() < numberOfLoops)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        assertTrue(
                type
                        + " list tests: Returned list corrupted by multiple thread access",
                normalSynchChecker.getResult()
                        && offsetSynchChecker.getResult());
        try {
            normalThread.join(5000);
            offsetThread.join(5000);
        } catch (InterruptedException e) {
            fail(type + " list tests: join() interrupted");
        }
        synchList.set(25, null);
        assertNull(type + " list tests: Trying to use nulls in list failed",
                synchList.get(25));
    }

    /**
     * @tests java.util.Collections#synchronizedMap(java.util.Map)
     */
    public void test_synchronizedMapLjava_util_Map() {
        // Test for method java.util.Map
        // java.util.Collections.synchronizedMap(java.util.Map)
        HashMap smallMap = new HashMap();
        for (int i = 0; i < 50; i++) {
            smallMap.put(objArray[i], objArray[i]);
        }

        final int numberOfLoops = 200;
        Map synchMap = Collections.synchronizedMap(smallMap);
        // Replacing the previous line with the line below should cause the test
        // to fail--the list below isn't synchronized
        // Map synchMap = smallMap;

        SynchMapChecker normalSynchChecker = new SynchMapChecker(synchMap,
                false, numberOfLoops);
        SynchMapChecker offsetSynchChecker = new SynchMapChecker(synchMap,
                true, numberOfLoops);
        Thread normalThread = new Thread(normalSynchChecker);
        Thread offsetThread = new Thread(offsetSynchChecker);
        normalThread.start();
        offsetThread.start();
        while ((normalSynchChecker.getNumberOfChecks() < numberOfLoops)
                || (offsetSynchChecker.getNumberOfChecks() < numberOfLoops)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        assertTrue("Returned map corrupted by multiple thread access",
                normalSynchChecker.getResult()
                        && offsetSynchChecker.getResult());
        try {
            normalThread.join(5000);
            offsetThread.join(5000);
        } catch (InterruptedException e) {
            fail("join() interrupted");
        }

        // synchronized map does not have to permit null keys or values
        synchMap.put(new Long(25), null);
        synchMap.put(null, new Long(30));
        assertNull("Trying to use a null value in map failed", synchMap
                .get(new Long(25)));
        assertTrue("Trying to use a null key in map failed", synchMap.get(null)
                .equals(new Long(30)));

        smallMap = new HashMap();
        for (int i = 0; i < 100; i++) {
            smallMap.put(objArray[i].toString(), objArray[i]);
        }
        synchMap = Collections.synchronizedMap(smallMap);
        new Support_UnmodifiableMapTest("", synchMap).runTest();
        synchMap.keySet().remove(objArray[50].toString());
        assertNull(
                "Removing a key from the keySet of the synchronized map did not remove it from the synchronized map: ",
                synchMap.get(objArray[50].toString()));
        assertNull(
                "Removing a key from the keySet of the synchronized map did not remove it from the original map",
                smallMap.get(objArray[50].toString()));
    }

    /**
     * @tests java.util.Collections#synchronizedSet(java.util.Set)
     */
    public void test_synchronizedSetLjava_util_Set() {
        // Test for method java.util.Set
        // java.util.Collections.synchronizedSet(java.util.Set)
        HashSet smallSet = new HashSet();
        for (int i = 0; i < 50; i++) {
            smallSet.add(objArray[i]);
        }

        final int numberOfLoops = 200;
        Set synchSet = Collections.synchronizedSet(smallSet);
        // Replacing the previous line with the line below should cause the test
        // to fail--the set below isn't synchronized
        // Set synchSet = smallSet;

        SynchCollectionChecker normalSynchChecker = new SynchCollectionChecker(
                synchSet, false, numberOfLoops);
        SynchCollectionChecker offsetSynchChecker = new SynchCollectionChecker(
                synchSet, true, numberOfLoops);
        Thread normalThread = new Thread(normalSynchChecker);
        Thread offsetThread = new Thread(offsetSynchChecker);
        normalThread.start();
        offsetThread.start();
        while ((normalSynchChecker.getNumberOfChecks() < numberOfLoops)
                || (offsetSynchChecker.getNumberOfChecks() < numberOfLoops)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        assertTrue("Returned set corrupted by multiple thread access",
                normalSynchChecker.getResult()
                        && offsetSynchChecker.getResult());
        try {
            normalThread.join(5000);
            offsetThread.join(5000);
        } catch (InterruptedException e) {
            fail("join() interrupted");
        }

        Set mySet = Collections.synchronizedSet(smallSet);
        mySet.add(null);
        assertTrue("Trying to use nulls in list failed", mySet.contains(null));

        smallSet = new HashSet();
        for (int i = 0; i < 100; i++) {
            smallSet.add(objArray[i]);
        }
        new Support_SetTest("", Collections.synchronizedSet(smallSet))
                .runTest();
    }

    /**
     * @tests java.util.Collections#synchronizedSortedMap(java.util.SortedMap)
     */
    public void test_synchronizedSortedMapLjava_util_SortedMap() {
        // Test for method java.util.SortedMap
        // java.util.Collections.synchronizedSortedMap(java.util.SortedMap)
        TreeMap smallMap = new TreeMap();
        for (int i = 0; i < 50; i++) {
            smallMap.put(objArray[i], objArray[i]);
        }

        final int numberOfLoops = 200;
        Map synchMap = Collections.synchronizedMap(smallMap);
        // Replacing the previous line with the line below should cause the test
        // to fail--the list below isn't synchronized
        // Map synchMap = smallMap;

        SynchMapChecker normalSynchChecker = new SynchMapChecker(synchMap,
                false, numberOfLoops);
        SynchMapChecker offsetSynchChecker = new SynchMapChecker(synchMap,
                true, numberOfLoops);
        Thread normalThread = new Thread(normalSynchChecker);
        Thread offsetThread = new Thread(offsetSynchChecker);
        normalThread.start();
        offsetThread.start();
        while ((normalSynchChecker.getNumberOfChecks() < numberOfLoops)
                || (offsetSynchChecker.getNumberOfChecks() < numberOfLoops)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        assertTrue("Returned map corrupted by multiple thread access",
                normalSynchChecker.getResult()
                        && offsetSynchChecker.getResult());
        try {
            normalThread.join(5000);
            offsetThread.join(5000);
        } catch (InterruptedException e) {
            fail("join() interrupted");
        }

        smallMap = new TreeMap();
        for (int i = 0; i < 100; i++) {
            smallMap.put(objArray[i].toString(), objArray[i]);
        }
        synchMap = Collections.synchronizedSortedMap(smallMap);
        new Support_UnmodifiableMapTest("", synchMap).runTest();
        synchMap.keySet().remove(objArray[50].toString());
        assertNull(
                "Removing a key from the keySet of the synchronized map did not remove it from the synchronized map",
                synchMap.get(objArray[50].toString()));
        assertNull(
                "Removing a key from the keySet of the synchronized map did not remove it from the original map",
                smallMap.get(objArray[50].toString()));
    }

    /**
     * @tests java.util.Collections#synchronizedSortedSet(java.util.SortedSet)
     */
    public void test_synchronizedSortedSetLjava_util_SortedSet() {
        // Test for method java.util.SortedSet
        // java.util.Collections.synchronizedSortedSet(java.util.SortedSet)
        TreeSet smallSet = new TreeSet();
        for (int i = 0; i < 50; i++) {
            smallSet.add(objArray[i]);
        }

        final int numberOfLoops = 200;
        Set synchSet = Collections.synchronizedSet(smallSet);
        // Replacing the previous line with the line below should cause the test
        // to fail--the list below isn't synchronized
        // Set synchSet = smallSet;

        SynchCollectionChecker normalSynchChecker = new SynchCollectionChecker(
                synchSet, false, numberOfLoops);
        SynchCollectionChecker offsetSynchChecker = new SynchCollectionChecker(
                synchSet, true, numberOfLoops);
        Thread normalThread = new Thread(normalSynchChecker);
        Thread offsetThread = new Thread(offsetSynchChecker);
        normalThread.start();
        offsetThread.start();
        while ((normalSynchChecker.getNumberOfChecks() < numberOfLoops)
                || (offsetSynchChecker.getNumberOfChecks() < numberOfLoops)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
        assertTrue("Returned set corrupted by multiple thread access",
                normalSynchChecker.getResult()
                        && offsetSynchChecker.getResult());
        try {
            normalThread.join(5000);
            offsetThread.join(5000);
        } catch (InterruptedException e) {
            fail("join() interrupted");
        }
    }

    /**
     * @tests java.util.Collections#unmodifiableCollection(java.util.Collection)
     */
    public void test_unmodifiableCollectionLjava_util_Collection() {
        // Test for method java.util.Collection
        // java.util.Collections.unmodifiableCollection(java.util.Collection)
        boolean exception = false;
        Collection c = Collections.unmodifiableCollection(ll);
        assertTrue("Returned collection is of incorrect size", c.size() == ll
                .size());
        Iterator i = ll.iterator();
        while (i.hasNext())
            assertTrue("Returned list missing elements", c.contains(i.next()));
        try {
            c.add(new Object());
        } catch (UnsupportedOperationException e) {
            exception = true;
            // Correct
        }
        if (!exception) {
            fail("Allowed modification of collection");
        }
        
        try {
            c.remove(new Object());
            fail("Allowed modification of collection");
        } catch (UnsupportedOperationException e) {
            // Correct
        }        

        Collection myCollection = new ArrayList();
        myCollection.add(new Integer(20));
        myCollection.add(null);
        c = Collections.unmodifiableCollection(myCollection);
        assertTrue("Collection should contain null", c.contains(null));
        assertTrue("Collection should contain Integer(20)", c
                .contains(new Integer(20)));

        myCollection = new ArrayList();
        for (int counter = 0; counter < 100; counter++) {
            myCollection.add(objArray[counter]);
        }
        new Support_UnmodifiableCollectionTest("", Collections
                .unmodifiableCollection(myCollection)).runTest();
    }

    /**
     * @tests java.util.Collections#unmodifiableList(java.util.List)
     */
    public void test_unmodifiableListLjava_util_List() {
        // Test for method java.util.List
        // java.util.Collections.unmodifiableList(java.util.List)

        // test with a Sequential Access List
        boolean exception = false;
        List c = Collections.unmodifiableList(ll);
        // Ensure a NPE is thrown if the list is NULL
        try {
            Collections.unmodifiableList(null);
            fail("Expected NullPointerException for null list parameter");
        } catch (NullPointerException e) {
        }

        assertTrue("Returned list is of incorrect size", c.size() == ll.size());
        assertTrue(
                "Returned List should not implement Random Access interface",
                !(c instanceof RandomAccess));

        Iterator i = ll.iterator();
        while (i.hasNext())
            assertTrue("Returned list missing elements", c.contains(i.next()));
        try {
            c.add(new Object());
        } catch (UnsupportedOperationException e) {
            exception = true;
            // Correct
        }
        if (!exception) {
            fail("Allowed modification of list");
        }
        
        try {
            c.remove(new Object());
            fail("Allowed modification of list");
        } catch (UnsupportedOperationException e) {
            // Correct
        }        

        // test with a Random Access List
        List smallList = new ArrayList();
        smallList.add(null);
        smallList.add("yoink");
        c = Collections.unmodifiableList(smallList);
        assertNull("First element should be null", c.get(0));
        assertTrue("List should contain null", c.contains(null));
        assertTrue(
                "T1. Returned List should implement Random Access interface",
                c instanceof RandomAccess);

        smallList = new ArrayList();
        for (int counter = 0; counter < 100; counter++) {
            smallList.add(objArray[counter]);
        }
        List myList = Collections.unmodifiableList(smallList);
        assertTrue("List should not contain null", !myList.contains(null));
        assertTrue(
                "T2. Returned List should implement Random Access interface",
                myList instanceof RandomAccess);

        assertTrue("get failed on unmodifiable list", myList.get(50).equals(
                new Integer(50)));
        ListIterator listIterator = myList.listIterator();
        for (int counter = 0; listIterator.hasNext(); counter++) {
            assertTrue("List has wrong elements", ((Integer) listIterator
                    .next()).intValue() == counter);
        }
        new Support_UnmodifiableCollectionTest("", smallList).runTest();
    }

    /**
     * @tests java.util.Collections#unmodifiableMap(java.util.Map)
     */
    public void test_unmodifiableMapLjava_util_Map() {
        // Test for method java.util.Map
        // java.util.Collections.unmodifiableMap(java.util.Map)
        boolean exception = false;
        Map c = Collections.unmodifiableMap(hm);
        assertTrue("Returned map is of incorrect size", c.size() == hm.size());
        Iterator i = hm.keySet().iterator();
        while (i.hasNext()) {
            Object x = i.next();
            assertTrue("Returned map missing elements", c.get(x).equals(
                    hm.get(x)));
        }
        try {
            c.put(new Object(), "");
        } catch (UnsupportedOperationException e) {
            exception = true;
            // Correct
        }
        assertTrue("Allowed modification of map", exception);

        exception = false;
        try {
            c.remove(new Object());
        } catch (UnsupportedOperationException e) {
            // Correct
            exception = true;
        }
        assertTrue("Allowed modification of map", exception);

        exception = false;
        Iterator it = c.entrySet().iterator();
        Map.Entry entry = (Map.Entry) it.next();
        try {
            entry.setValue("modified");
        } catch (UnsupportedOperationException e) {
            // Correct
            exception = true;
        }
        assertTrue("Allowed modification of entry", exception);

        exception = false;
        Object[] array = c.entrySet().toArray();
        try {
            ((Map.Entry) array[0]).setValue("modified");
        } catch (UnsupportedOperationException e) {
            // Correct
            exception = true;
        }
        assertTrue("Allowed modification of array entry", exception);

        exception = false;
        Map.Entry[] array2 = (Map.Entry[]) c.entrySet().toArray(
                new Map.Entry[0]);
        try {
            array2[0].setValue("modified");
        } catch (UnsupportedOperationException e) {
            // Correct
            exception = true;
        }
        assertTrue("Allowed modification of array entry2", exception);

        HashMap smallMap = new HashMap();
        smallMap.put(null, new Long(30));
        smallMap.put(new Long(25), null);
        Map unmodMap = Collections.unmodifiableMap(smallMap);

        assertNull("Trying to use a null value in map failed", unmodMap
                .get(new Long(25)));
        assertTrue("Trying to use a null key in map failed", unmodMap.get(null)
                .equals(new Long(30)));

        smallMap = new HashMap();
        for (int counter = 0; counter < 100; counter++) {
            smallMap.put(objArray[counter].toString(), objArray[counter]);
        }
        unmodMap = Collections.unmodifiableMap(smallMap);
        new Support_UnmodifiableMapTest("", unmodMap).runTest();

    }

    /**
     * @tests java.util.Collections#unmodifiableSet(java.util.Set)
     */
    public void test_unmodifiableSetLjava_util_Set() {
        // Test for method java.util.Set
        // java.util.Collections.unmodifiableSet(java.util.Set)
        boolean exception = false;
        Set c = Collections.unmodifiableSet(s);
        assertTrue("Returned set is of incorrect size", c.size() == s.size());
        Iterator i = ll.iterator();
        while (i.hasNext())
            assertTrue("Returned set missing elements", c.contains(i.next()));
        try {
            c.add(new Object());
        } catch (UnsupportedOperationException e) {
            exception = true;
            // Correct
        }
        if (!exception) {
            fail("Allowed modification of set");
        }
        try {
            c.remove(new Object());
            fail("Allowed modification of set");
        } catch (UnsupportedOperationException e) {
            // Correct
        }        

        Set mySet = Collections.unmodifiableSet(new HashSet());
        assertTrue("Should not contain null", !mySet.contains(null));
        mySet = Collections.unmodifiableSet(Collections.singleton(null));
        assertTrue("Should contain null", mySet.contains(null));

        mySet = new TreeSet();
        for (int counter = 0; counter < 100; counter++) {
            mySet.add(objArray[counter]);
        }
        new Support_UnmodifiableCollectionTest("", Collections
                .unmodifiableSet(mySet)).runTest();
    }

    /**
     * @tests java.util.Collections#unmodifiableSortedMap(java.util.SortedMap)
     */
    public void test_unmodifiableSortedMapLjava_util_SortedMap() {
        // Test for method java.util.SortedMap
        // java.util.Collections.unmodifiableSortedMap(java.util.SortedMap)
        boolean exception = false;
        TreeMap tm = new TreeMap();
        tm.putAll(hm);
        Map c = Collections.unmodifiableSortedMap(tm);
        assertTrue("Returned map is of incorrect size", c.size() == tm.size());
        Iterator i = hm.keySet().iterator();
        while (i.hasNext()) {
            Object x = i.next();
            assertTrue("Returned map missing elements", c.get(x).equals(
                    tm.get(x)));
        }
        try {
            c.put(new Object(), "");
        } catch (UnsupportedOperationException e) {
            exception = true;
            // Correct
        }
        if (!exception) {
            fail("Allowed modification of map");
        }
        try {
            c.remove(new Object());
        } catch (UnsupportedOperationException e) {
            // Correct
            return;
        }
        fail("Allowed modification of map");
    }

    /**
     * @tests java.util.Collections#unmodifiableSortedSet(java.util.SortedSet)
     */
    public void test_unmodifiableSortedSetLjava_util_SortedSet() {
        // Test for method java.util.SortedSet
        // java.util.Collections.unmodifiableSortedSet(java.util.SortedSet)
        boolean exception = false;
        SortedSet ss = new TreeSet();
        ss.addAll(s);
        SortedSet c = Collections.unmodifiableSortedSet(ss);
        assertTrue("Returned set is of incorrect size", c.size() == ss.size());
        Iterator i = ll.iterator();
        while (i.hasNext())
            assertTrue("Returned set missing elements", c.contains(i.next()));
        try {
            c.add(new Object());
        } catch (UnsupportedOperationException e) {
            exception = true;
            // Correct
        }
        if (!exception) {
            fail("Allowed modification of set");
        }
        try {
            c.remove(new Object());
        } catch (UnsupportedOperationException e) {
            // Correct
            return;
        }
        fail("Allowed modification of set");
    }

    /**
     * Test unmodifiable objects toString methods
     */
    public void test_unmodifiable_toString_methods() {
        // Regression for HARMONY-552
        ArrayList al = new ArrayList();
        al.add("a");
        al.add("b");
        Collection uc = Collections.unmodifiableCollection(al);
        assertEquals("[a, b]", uc.toString());
        HashMap m = new HashMap();
        m.put("one", "1");
        m.put("two", "2");
        Map um = Collections.unmodifiableMap(m);
        assertEquals("{one=1, two=2}", um.toString());
    }
    
    /**
     * @tests java.util.Collections#checkType(Object, Class)
     */
    public void test_checkType_Ljava_lang_Object_Ljava_lang_Class() throws Exception {
        Method m = Collections.class.getDeclaredMethod("checkType", Object.class, Class.class);
           m.setAccessible(true);
           m.invoke(null, new Object(), Object.class);
            
           try {
               m.invoke(null, new Object(), int.class);
               fail("should throw InvocationTargetException");
           } catch (InvocationTargetException e) {
            String errMsg = Messages.getString(
                    "luni.05", Object.class, int.class);
               assertEquals(errMsg, e.getCause().getMessage());
           }
    }


    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {
        ll = new LinkedList();
        myll = new LinkedList();
        s = new HashSet();
        mys = new HashSet();
        reversedLinkedList = new LinkedList(); // to be sorted in reverse order
        myReversedLinkedList = new LinkedList(); // to be sorted in reverse
        // order
        hm = new HashMap();
        for (int i = 0; i < objArray.length; i++) {
            ll.add(objArray[i]);
            myll.add(myobjArray[i]);
            s.add(objArray[i]);
            mys.add(myobjArray[i]);
            reversedLinkedList.add(objArray[objArray.length - i - 1]);
            myReversedLinkedList.add(myobjArray[myobjArray.length - i - 1]);
            hm.put(objArray[i].toString(), objArray[i]);
        }
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() {
    }

    protected void doneSuite() {
        objArray = null;
    }
}
