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

import dalvik.annotation.TestTarget;
import dalvik.annotation.TestInfo;
import dalvik.annotation.TestLevel;
import dalvik.annotation.TestTargetClass; 

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

@TestTargetClass(Observable.class) 
public class ObservableTest extends junit.framework.TestCase {

    static class TestObserver implements Observer {
        public Vector objv = new Vector();

        int updateCount = 0;

        public void update(Observable observed, Object arg) {
            ++updateCount;
            objv.add(arg);
        }

        public int updateCount() {
            return updateCount;
        }

    }

    static class DeleteTestObserver implements Observer {
        int updateCount = 0;

        boolean deleteAll = false;

        public DeleteTestObserver(boolean all) {
            deleteAll = all;
        }

        public void update(Observable observed, Object arg) {
            ++updateCount;
            if (deleteAll)
                observed.deleteObservers();
            else
                observed.deleteObserver(this);
        }

        public int updateCount() {
            return updateCount;
        }

    }

    static class TestObservable extends Observable {
        public void doChange() {
            setChanged();
        }
    }

    Observer observer;

    TestObservable observable;

    /**
     * @tests java.util.Observable#Observable()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "Observable",
          methodArgs = {}
        )
    })
    public void test_Constructor() {
        // Test for method java.util.Observable()
        try {
            Observable ov = new Observable();
            assertTrue("Wrong initial values.", !ov.hasChanged());
            assertEquals("Wrong initial values.", 0, ov.countObservers());
        } catch (Exception e) {
            fail("Exception during test : " + e.getMessage());
        }
    }

    /**
     * @tests java.util.Observable#addObserver(java.util.Observer)
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "addObserver",
          methodArgs = {java.util.Observer.class}
        )
    })
    public void test_addObserverLjava_util_Observer() {
        // Test for method void
        // java.util.Observable.addObserver(java.util.Observer)
        TestObserver test = new TestObserver();
        observable.addObserver(test);
        assertEquals("Failed to add observer", 1, observable.countObservers());
        observable.addObserver(test);
        assertEquals("Duplicate observer", 1, observable.countObservers());

        Observable o = new Observable();
        try {
            o.addObserver(null);
            fail("Expected adding a null observer to throw a NPE.");
        } catch (NullPointerException ex) {
            // expected;
        } catch (Throwable ex) {
            fail("Did not expect adding a new observer to throw a "
                    + ex.getClass().getName());
        }
    }

    /**
     * @tests java.util.Observable#countObservers()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "countObservers",
          methodArgs = {}
        )
    })
    public void test_countObservers() {
        // Test for method int java.util.Observable.countObservers()
        assertEquals("New observable had > 0 observers", 0, observable
                .countObservers());
        observable.addObserver(new TestObserver());
        assertEquals("Observable with observer returned other than 1", 1, observable
                .countObservers());
    }

    /**
     * @tests java.util.Observable#deleteObserver(java.util.Observer)
     */
    @TestInfo(
      level = TestLevel.PARTIAL,
      purpose = "Doesn't verify negative case: delete non-existent observer.",
      targets = {
        @TestTarget(
          methodName = "deleteObserver",
          methodArgs = {java.util.Observer.class}
        )
    })
    public void test_deleteObserverLjava_util_Observer() {
        // Test for method void
        // java.util.Observable.deleteObserver(java.util.Observer)
        observable.addObserver(observer = new TestObserver());
        observable.deleteObserver(observer);
        assertEquals("Failed to delete observer",
                0, observable.countObservers());

    }

    /**
     * @tests java.util.Observable#deleteObservers()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "deleteObservers",
          methodArgs = {}
        )
    })
    public void test_deleteObservers() {
        // Test for method void java.util.Observable.deleteObservers()
        observable.addObserver(new TestObserver());
        observable.addObserver(new TestObserver());
        observable.addObserver(new TestObserver());
        observable.addObserver(new TestObserver());
        observable.addObserver(new TestObserver());
        observable.addObserver(new TestObserver());
        observable.addObserver(new TestObserver());
        observable.addObserver(new TestObserver());
        observable.deleteObservers();
        assertEquals("Failed to delete observers",
                0, observable.countObservers());
    }

    /**
     * @tests java.util.Observable#hasChanged()
     */
    @TestInfo(
      level = TestLevel.TODO,
      purpose = "Empty test.",
      targets = {
        @TestTarget(
          methodName = "hasChanged",
          methodArgs = {}
        )
    })
    public void test_hasChanged() {
        // TODO : Implement test
    }

    /**
     * @tests java.util.Observable#notifyObservers()
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "notifyObservers",
          methodArgs = {}
        )
    })
    public void test_notifyObservers() {
        // Test for method void java.util.Observable.notifyObservers()
        observable.addObserver(observer = new TestObserver());
        observable.notifyObservers();
        assertEquals("Notified when unchnaged", 0, ((TestObserver) observer)
                .updateCount());
        ((TestObservable) observable).doChange();
        observable.notifyObservers();
        assertEquals("Failed to notify",
                1, ((TestObserver) observer).updateCount());

        DeleteTestObserver observer1, observer2;
        observable.deleteObservers();
        observable.addObserver(observer1 = new DeleteTestObserver(false));
        observable.addObserver(observer2 = new DeleteTestObserver(false));
        observable.doChange();
        observable.notifyObservers();
        assertTrue("Failed to notify all", observer1.updateCount() == 1
                && observer2.updateCount() == 1);
        assertEquals("Failed to delete all", 0, observable.countObservers());

        observable.addObserver(observer1 = new DeleteTestObserver(false));
        observable.addObserver(observer2 = new DeleteTestObserver(false));
        observable.doChange();
        observable.notifyObservers();
        assertTrue("Failed to notify all 2", observer1.updateCount() == 1
                && observer2.updateCount() == 1);
        assertEquals("Failed to delete all 2", 0, observable.countObservers());
    }

    /**
     * @tests java.util.Observable#notifyObservers(java.lang.Object)
     */
    @TestInfo(
      level = TestLevel.COMPLETE,
      purpose = "",
      targets = {
        @TestTarget(
          methodName = "notifyObservers",
          methodArgs = {java.lang.Object.class}
        )
    })
    public void test_notifyObserversLjava_lang_Object() {
        // Test for method void
        // java.util.Observable.notifyObservers(java.lang.Object)
        Object obj;
        observable.addObserver(observer = new TestObserver());
        observable.notifyObservers();
        assertEquals("Notified when unchanged", 0, ((TestObserver) observer)
                .updateCount());
        ((TestObservable) observable).doChange();
        observable.notifyObservers(obj = new Object());
        assertEquals("Failed to notify",
                1, ((TestObserver) observer).updateCount());
        assertTrue("Failed to pass Object arg", ((TestObserver) observer).objv
                .elementAt(0).equals(obj));
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     */
    protected void setUp() {
        observable = new TestObservable();
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    protected void tearDown() {
    }
}
