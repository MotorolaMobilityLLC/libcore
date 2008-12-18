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

import dalvik.annotation.TestTarget;
import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass; 

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

@TestTargetClass(AbstractQueue.class) 
public class AbstractQueueTest extends TestCase {

    private MockAbstractQueue<Object> queue;

    private class MockAbstractQueue<E> extends AbstractQueue<E> {

        static final int CAPACITY = 10;

        private int size = 0;

        private Object[] elements = new Object[CAPACITY];

        public Iterator<E> iterator() {
            return new Iterator<E>() {

                private int currentIndex = -1;

                public boolean hasNext() {
                    return size > 0 && currentIndex < size;
                }

                public E next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    currentIndex++;
                    return (E) elements[currentIndex];
                }

                public void remove() {
                    if (-1 == currentIndex) {
                        throw new IllegalStateException();
                    }
                    for (int i = currentIndex; i < size - 1; i++) {
                        elements[i] = elements[i + 1];
                    }
                    size--;
                }
            };
        }

        public int size() {
            return size;
        }

        public boolean offer(E o) {
            if (null == o) {
                throw new NullPointerException();
            }

            if (size >= CAPACITY) {
                return false;
            }

            elements[size++] = o;
            return true;
        }

        public E poll() {
            if (isEmpty()) {
                return null;
            }
            E e = (E) elements[0];
            for (int i = 0; i < size - 1; i++) {
                elements[i] = elements[i + 1];
            }
            size--;
            return e;
        }

        public E peek() {
            if (isEmpty()) {
                return null;
            }
            return (E) elements[0];
        }

    }

    /**
     * @tests java.util.AbstractQueue.add(E)
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies NullPointerException.",
      targets = {
        @TestTarget(
          methodName = "add",
          methodArgs = {Object.class}
        )
    })
    public void test_addLE_null() {
        try {
            queue.add(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.util.AbstractQueue.add(E)
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies IllegalStateException.",
      targets = {
        @TestTarget(
          methodName = "add",
          methodArgs = {Object.class}
        )
    })
    public void test_addLE_Full() {
        Object o = new Object();

        for(int i = 0; i < MockAbstractQueue.CAPACITY; i++ ) {
            queue.add(o);
        }

        try {
            queue.add(o);
            fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            //expected
        }
    }

    /**
     * @tests java.util.AbstractQueue#add(E)
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Doesn't verify NullPointerException, IllegalStateException.",
      targets = {
        @TestTarget(
          methodName = "add",
          methodArgs = {Object.class}
        )
    })
    public void test_addLE() {
        Object o = new Object();
        final int LAST_INDEX = 4;
        for (int i = 0; i < LAST_INDEX; i++) {
            queue.add(o);
        }
        Integer I = new Integer(123456);
        queue.add(I);
        assertTrue(queue.contains(I));
        Iterator iter = queue.iterator();
        for (int i = 0; i < LAST_INDEX; i++) {
            iter.next();
        }
        assertTrue(I == iter.next());
    }

    /**
     * @tests java.util.AbstractQueue#addAll(E)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies NullPointerException.",
      targets = {
        @TestTarget(
          methodName = "addAll",
          methodArgs = {java.util.Collection.class}
        )
    })
    public void test_addAllLE_null() {
        try {
            queue.addAll(null);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.util.AbstractQueue#addAll(E)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies NullPointerException.",
      targets = {
        @TestTarget(
          methodName = "addAll",
          methodArgs = {java.util.Collection.class}
        )
    })
    public void test_addAllLE_with_null() {
        List list = Arrays.asList("MYTESTSTRING", null, new Float(123.456));
        try {
            queue.addAll(list);
            fail("should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * @tests java.util.AbstractQueue#addAll(E)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies IllegalStateException.",
      targets = {
        @TestTarget(
          methodName = "addAll",
          methodArgs = {java.util.Collection.class}
        )
    })
    public void test_addAllLE_full() {
        List list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        try {
            queue.addAll(list);
            fail("should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    /**
     * @tests java.util.AbstractQueue#addAll(E)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Regression test. Doesn't verify returned true value.",
      targets = {
        @TestTarget(
          methodName = "addAll",
          methodArgs = {java.util.Collection.class}
        )
    })
    public void test_addAllLE_empty() {
        // Regression test for HARMONY-1178
        List list = new ArrayList<Object>(0);
        assertFalse("Non modification to queue should return false", queue.addAll(list));
    }

    /**
     * @tests java.util.AbstractQueue#addAll(E)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies IllegalArgumentException.",
      targets = {
        @TestTarget(
          methodName = "addAll",
          methodArgs = {java.util.Collection.class}
        )
    })
    public void test_addAllLE_this() {
        try {
            queue.addAll(queue);
            fail("should throw IllegalArgumentException ");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * @tests java.util.AbstractQueue#clear()
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies clear method for empty queue.",
      targets = {
        @TestTarget(
          methodName = "clear",
          methodArgs = {}
        )
    })
    public void test_clear_empty() {
        queue.clear();
        assertTrue(queue.isEmpty());
        assertNull(queue.peek());
    }

    /**
     * @tests java.util.AbstractQueue#clear()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "clear",
          methodArgs = {}
        )
    })
    public void test_clear() {
        List list = Arrays.asList(123.456, "MYTESTSTRING", new Object(), 'c');
        queue.addAll(list);
        queue.clear();
        assertTrue(queue.isEmpty());
        assertNull(queue.peek());
    }

    /**
     * @tests java.util.AbstractQueue#AbstractQueue()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "AbstractQueue",
          methodArgs = {}
        )
    })
    public void test_Constructor() {
        MockAbstractQueue queue = new MockAbstractQueue();
        assertNotNull(queue);
    }

    /**
     * @tests java.util.AbstractQueue#remove()
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Verifies NoSuchElementException.",
      targets = {
        @TestTarget(
          methodName = "remove",
          methodArgs = {}
        )
    })
    public void test_remove_null() {
        try {
            queue.remove();
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }

    }

    /**
     * @tests java.util.AbstractQueue#remove()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "Verifies positive functionality, NoSuchElementException.",
      targets = {
        @TestTarget(
          methodName = "remove",
          methodArgs = {}
        )
    })
    public void test_remove() {
        char c = 'a';
        queue.add(c);
        c = 'b';
        queue.add(c);
        assertEquals('a', queue.remove());
        assertEquals('b', queue.remove());
        try {
            queue.remove();
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    /**
     * @tests java.util.AbstractQueue#element()
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Verifies NoSuchElementException.",
      targets = {
        @TestTarget(
          methodName = "element",
          methodArgs = {}
        )
    })
    public void test_element_empty() {
        try {
            queue.element();
            fail("should throw NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    /**
     * @tests java.util.AbstractQueue#element()
     */
    @TestInfo(
      level = TestLevel.PARTIAL_OK,
      purpose = "Doesn't verify NoSuchElementException.",
      targets = {
        @TestTarget(
          methodName = "element",
          methodArgs = {}
        )
    })
    public void test_element() {
        String s = "MYTESTSTRING_ONE";
        queue.add(s);
        s = "MYTESTSTRING_TWO";
        queue.add(s);
        assertEquals("MYTESTSTRING_ONE", queue.element());
        // still the first element
        assertEquals("MYTESTSTRING_ONE", queue.element());
    }

    protected void setUp() throws Exception {
        super.setUp();
        queue = new MockAbstractQueue<Object>();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        queue = null;
    }
}
