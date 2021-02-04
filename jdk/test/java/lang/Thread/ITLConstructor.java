/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
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

/*
 * @test
 * @summary Basic test for Thread(ThreadGroup,Runnable,String,long,boolean)
 */

import sun.misc.SharedSecrets;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ITLConstructor {
    static InheritableThreadLocal<Integer> n = new InheritableThreadLocal<Integer>() {
        protected Integer initialValue() {
            return 0;
        }

        protected Integer childValue(Integer parentValue) {
            return parentValue + 1;
        }
    };

    static final int CHILD_THREAD_COUNT = 10;

    public static void main(String args[]) throws Exception {
        test();
    }

    static void test() throws Exception {
        // concurrent access to separate indexes is ok
        int[] x = new int[CHILD_THREAD_COUNT];
        Thread child = createThread(new AnotherRunnable(0, x));
        child.start();
        child.join(); // waits for *all* threads to complete

        // Check results
        for(int i=0; i<CHILD_THREAD_COUNT; i++) {
            int expectedValue = 1;
            if (x[i] != expectedValue)
                throw (new Exception("Got x[" + i + "] = " + x[i]
                                     + ", expected: " + expectedValue));
        }
    }

    static class AnotherRunnable implements Runnable {
        final int threadId;
        final int[] x;
        AnotherRunnable(int threadId, int[] x) {
            this.threadId = threadId;
            this.x = x;
        }

        public void run() {
            int itlValue = n.get();

            if (threadId < CHILD_THREAD_COUNT-1) {
                Thread child = createThread(
                        new AnotherRunnable(threadId+1, x));
                child.start();
                try {
                    child.join();
                } catch(InterruptedException e) {
                    throw(new RuntimeException("Interrupted", e));
                }
            }

            x[threadId] = itlValue+1;
        }
    }

    static Thread createThread(final Runnable r) {
        final AccessControlContext acc = AccessController.getContext();
        // 4290486: doPrivileged is needed to create a thread in
        // an environment that restricts "modifyThreadGroup".
        return AccessController.doPrivileged(
                new PrivilegedAction<Thread>() {
                    public Thread run() {
                        return SharedSecrets.getJavaLangAccess()
                                .newThreadWithAcc(r, acc);
                    }
                }
        );
    }
}
