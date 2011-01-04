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

package libcore.java.util.concurrent;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import junit.framework.TestCase;
import libcore.java.util.SerializableTester;

public final class CopyOnWriteArrayListTest extends TestCase {

    public void testIteratorAndNonStructuralChanges() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        Iterator<String> abcde = list.iterator();
        assertEquals("a", abcde.next());
        list.set(1, "B");
        assertEquals("b", abcde.next());
        assertEquals("c", abcde.next());
        assertEquals("d", abcde.next());
        assertEquals("e", abcde.next());
    }

    /**
     * The sub list throws on non-structural changes, even though that disagrees
     * with the subList() documentation which suggests that only size-changing
     * operations will trigger ConcurrentModificationException.
     */
    public void testSubListAndNonStructuralChanges() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        list.set(2, "C");
        try {
            bcd.get(1);
            fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void testSubListAndStructuralChanges() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        list.clear();
        try {
            bcd.get(1);
            fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void testSubListAndSizePreservingStructuralChanges() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        list.clear();
        list.addAll(Arrays.asList("A", "B", "C", "D", "E"));
        try {
            bcd.get(1);
            fail();
        } catch (ConcurrentModificationException expected) {
        }
    }

    public void testRemoveAll() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));

        list.removeAll(Arrays.asList());
        assertEquals(Arrays.asList("a", "b", "c", "d", "e"), list);

        list.removeAll(Arrays.asList("e"));
        assertEquals(Arrays.asList("a", "b", "c", "d"), list);

        list.removeAll(Arrays.asList("b", "c"));
        assertEquals(Arrays.asList("a", "d"), list);
    }

    public void testSubListClear() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.clear();
        assertEquals(Arrays.asList("a", "e"), list);
        bcd.addAll(Arrays.asList("B", "C", "D"));
        assertEquals(Arrays.asList("a", "B", "C", "D", "e"), list);
    }

    public void testSubListClearWhenEmpty() {
        new CopyOnWriteArrayList<String>().subList(0, 0).clear(); // the RI fails here
    }

    public void testSubListIteratorGetsSnapshot() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        Iterator<String> bcd = list.subList(1, 4).iterator();
        list.clear();
        assertEquals("b", bcd.next());
        assertEquals("c", bcd.next());
        assertEquals("d", bcd.next());
        assertFalse(bcd.hasNext());
    }

    public void testSubListRemoveByValue() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.remove("c"); // the RI fails here
        assertEquals(Arrays.asList("b", "d"), bcd);
        assertEquals(Arrays.asList("a", "b", "d", "e"), list);
    }

    public void testSubListRemoveByIndex() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.remove(1);
        assertEquals(Arrays.asList("b", "d"), bcd);
        assertEquals(Arrays.asList("a", "b", "d", "e"), list);
    }

    public void testSubListRetainAll() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i"));
        List<String> def = list.subList(3, 6);
        def.retainAll(Arrays.asList("c", "e", "h")); // the RI fails here
        assertEquals(Arrays.asList("a", "b", "c", "e", "g", "h", "i"), list);
        assertEquals(Arrays.asList("e"), def);
    }

    public void testSubListRemoveAll() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i"));
        List<String> def = list.subList(3, 6);
        def.removeAll(Arrays.asList("c", "e", "h"));  // the RI fails here
        assertEquals(Arrays.asList("a", "b", "c", "d", "f", "g", "h", "i"), list);
        assertEquals(Arrays.asList("d", "f"), def);
    }

    public void testAtomicAdds() throws Exception {
        testAddAllIsAtomic(new CopyOnWriteArrayList<Object>());
    }

    public void testSubListAtomicAdds() throws Exception {
        testAddAllIsAtomic(new CopyOnWriteArrayList<Object>().subList(0, 0));
    }

    /**
     * Attempts to observe {@code list} in the middle of an add. The RI's
     * CopyOnWriteArrayList passes this test, but its sub list doesn't.
     */
    private void testAddAllIsAtomic(final List<Object> list) throws Exception {
        final CountDownLatch done = new CountDownLatch(1);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(new Runnable() {
            @Override public void run() {
                while (done.getCount() > 0) {
                    int listSize = list.size();
                    assertEquals("addAll() not atomic; size=" + listSize, 0, listSize % 1000);
                    Thread.yield();
                }
            }
        });
        executor.shutdown();

        List<Object> toAdd = Arrays.asList(new Object[1000]);
        for (int i = 0; i < 100; i++) {
            list.addAll(toAdd);
            list.clear();
            Thread.yield();
        }

        done.countDown();
        future.get(); // this will throw the above exception
    }

    public void testSubListAddIsAtEnd() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.add("f");
        assertEquals(Arrays.asList("a", "b", "c", "d", "f", "e"), list);
        assertEquals(Arrays.asList("b", "c", "d", "f"), bcd);
    }

    public void testSubListAddAll() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        List<String> bcd = list.subList(1, 4);
        bcd.addAll(1, Arrays.asList("f", "g", "h", "i"));
        assertEquals(Arrays.asList("a", "b", "f", "g", "h", "i", "c", "d", "e"), list);
        assertEquals(Arrays.asList("b", "f", "g", "h", "i", "c", "d"), bcd);
    }

    public void testListIterator() {
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
        list.addAll(Arrays.asList("a", "b", "c", "d", "e"));
        ListIterator<String> i = list.listIterator(5);
        list.clear();

        assertEquals(5, i.nextIndex());
        assertEquals(4, i.previousIndex());
        assertEquals("e", i.previous());
        assertEquals(4, i.nextIndex());
        assertEquals(3, i.previousIndex());
        assertTrue(i.hasNext());
        assertTrue(i.hasPrevious());
        assertEquals("d", i.previous());
        assertEquals(3, i.nextIndex());
        assertEquals(2, i.previousIndex());
        assertTrue(i.hasNext());
        assertTrue(i.hasPrevious());
        assertEquals("c", i.previous());
        assertEquals(2, i.nextIndex());
        assertEquals(1, i.previousIndex());
        assertTrue(i.hasNext());
        assertTrue(i.hasPrevious());
        assertEquals("b", i.previous());
        assertEquals(1, i.nextIndex());
        assertEquals(0, i.previousIndex());
        assertTrue(i.hasNext());
        assertTrue(i.hasPrevious());
        assertEquals("a", i.previous());
        assertEquals(0, i.nextIndex());
        assertEquals(-1, i.previousIndex());
        assertTrue(i.hasNext());
        assertFalse(i.hasPrevious());
        try {
            i.previous();
            fail();
        } catch (NoSuchElementException expected) {
        }
    }

    public void testSerialize() {
        String s = "aced0005737200296a6176612e7574696c2e636f6e63757272656e742e436f70"
                + "794f6e577269746541727261794c697374785d9fd546ab90c3030000787077040"
                + "0000005740001617400016274000163707400016578";

        List<String> contents = Arrays.asList("a", "b", "c", null, "e");
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>(contents);

        new SerializableTester<CopyOnWriteArrayList<String>>(list, s).test();
    }
}
