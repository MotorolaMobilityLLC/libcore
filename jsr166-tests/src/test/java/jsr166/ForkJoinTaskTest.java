/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package jsr166;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.HashSet;
import junit.framework.*;

public class ForkJoinTaskTest extends JSR166TestCase {

    // Runs with "mainPool" use > 1 thread. singletonPool tests use 1
    static final int mainPoolSize =
        Math.max(2, Runtime.getRuntime().availableProcessors());

    private static ForkJoinPool mainPool() {
        return new ForkJoinPool(mainPoolSize);
    }

    private static ForkJoinPool singletonPool() {
        return new ForkJoinPool(1);
    }

    private static ForkJoinPool asyncSingletonPool() {
        return new ForkJoinPool(1,
                                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                                null, true);
    }

    private void testInvokeOnPool(ForkJoinPool pool, RecursiveAction a) {
        try {
            assertFalse(a.isDone());
            assertFalse(a.isCompletedNormally());
            assertFalse(a.isCompletedAbnormally());
            assertFalse(a.isCancelled());
            assertNull(a.getException());
            assertNull(a.getRawResult());

            assertNull(pool.invoke(a));

            assertTrue(a.isDone());
            assertTrue(a.isCompletedNormally());
            assertFalse(a.isCompletedAbnormally());
            assertFalse(a.isCancelled());
            assertNull(a.getException());
            assertNull(a.getRawResult());
        } finally {
            joinPool(pool);
        }
    }

    void checkNotDone(ForkJoinTask a) {
        assertFalse(a.isDone());
        assertFalse(a.isCompletedNormally());
        assertFalse(a.isCompletedAbnormally());
        assertFalse(a.isCancelled());
        assertNull(a.getException());
        assertNull(a.getRawResult());

        try {
            a.get(0L, SECONDS);
            shouldThrow();
        } catch (TimeoutException success) {
        } catch (Throwable fail) { threadUnexpectedException(fail); }
    }

    <T> void checkCompletedNormally(ForkJoinTask<T> a) {
        checkCompletedNormally(a, null);
    }

    <T> void checkCompletedNormally(ForkJoinTask<T> a, T expected) {
        assertTrue(a.isDone());
        assertFalse(a.isCancelled());
        assertTrue(a.isCompletedNormally());
        assertFalse(a.isCompletedAbnormally());
        assertNull(a.getException());
        assertSame(expected, a.getRawResult());

        {
            Thread.currentThread().interrupt();
            long t0 = System.nanoTime();
            assertSame(expected, a.join());
            assertTrue(millisElapsedSince(t0) < SMALL_DELAY_MS);
            Thread.interrupted();
        }

        {
            Thread.currentThread().interrupt();
            long t0 = System.nanoTime();
            a.quietlyJoin();        // should be no-op
            assertTrue(millisElapsedSince(t0) < SMALL_DELAY_MS);
            Thread.interrupted();
        }

        assertFalse(a.cancel(false));
        assertFalse(a.cancel(true));
        try {
            assertSame(expected, a.get());
        } catch (Throwable fail) { threadUnexpectedException(fail); }
        try {
            assertSame(expected, a.get(5L, SECONDS));
        } catch (Throwable fail) { threadUnexpectedException(fail); }
    }

    void checkCancelled(ForkJoinTask a) {
        assertTrue(a.isDone());
        assertTrue(a.isCancelled());
        assertFalse(a.isCompletedNormally());
        assertTrue(a.isCompletedAbnormally());
        assertTrue(a.getException() instanceof CancellationException);
        assertNull(a.getRawResult());
        assertTrue(a.cancel(false));
        assertTrue(a.cancel(true));

        try {
            Thread.currentThread().interrupt();
            a.join();
            shouldThrow();
        } catch (CancellationException success) {
        } catch (Throwable fail) { threadUnexpectedException(fail); }
        Thread.interrupted();

        {
            long t0 = System.nanoTime();
            a.quietlyJoin();        // should be no-op
            assertTrue(millisElapsedSince(t0) < SMALL_DELAY_MS);
        }

        try {
            a.get();
            shouldThrow();
        } catch (CancellationException success) {
        } catch (Throwable fail) { threadUnexpectedException(fail); }

        try {
            a.get(5L, SECONDS);
            shouldThrow();
        } catch (CancellationException success) {
        } catch (Throwable fail) { threadUnexpectedException(fail); }
    }

    void checkCompletedAbnormally(ForkJoinTask a, Throwable t) {
        assertTrue(a.isDone());
        assertFalse(a.isCancelled());
        assertFalse(a.isCompletedNormally());
        assertTrue(a.isCompletedAbnormally());
        assertSame(t.getClass(), a.getException().getClass());
        assertNull(a.getRawResult());
        assertFalse(a.cancel(false));
        assertFalse(a.cancel(true));

        try {
            Thread.currentThread().interrupt();
            a.join();
            shouldThrow();
        } catch (Throwable expected) {
            assertSame(t.getClass(), expected.getClass());
        }
        Thread.interrupted();

        {
            long t0 = System.nanoTime();
            a.quietlyJoin();        // should be no-op
            assertTrue(millisElapsedSince(t0) < SMALL_DELAY_MS);
        }

        try {
            a.get();
            shouldThrow();
        } catch (ExecutionException success) {
            assertSame(t.getClass(), success.getCause().getClass());
        } catch (Throwable fail) { threadUnexpectedException(fail); }

        try {
            a.get(5L, SECONDS);
            shouldThrow();
        } catch (ExecutionException success) {
            assertSame(t.getClass(), success.getCause().getClass());
        } catch (Throwable fail) { threadUnexpectedException(fail); }
    }

    /*
     * Testing coverage notes:
     *
     * To test extension methods and overrides, most tests use
     * BinaryAsyncAction extension class that processes joins
     * differently than supplied Recursive forms.
     */

    public static final class FJException extends RuntimeException {
        FJException() { super(); }
    }

    abstract static class BinaryAsyncAction extends ForkJoinTask<Void> {
        private volatile int controlState;

        static final AtomicIntegerFieldUpdater<BinaryAsyncAction> controlStateUpdater =
            AtomicIntegerFieldUpdater.newUpdater(BinaryAsyncAction.class,
                                                 "controlState");

        private BinaryAsyncAction parent;

        private BinaryAsyncAction sibling;

        protected BinaryAsyncAction() {
        }

        public final Void getRawResult() { return null; }
        protected final void setRawResult(Void mustBeNull) { }

        public final void linkSubtasks(BinaryAsyncAction x, BinaryAsyncAction y) {
            x.parent = y.parent = this;
            x.sibling = y;
            y.sibling = x;
        }

        protected void onComplete(BinaryAsyncAction x, BinaryAsyncAction y) {
        }

        protected boolean onException() {
            return true;
        }

        public void linkAndForkSubtasks(BinaryAsyncAction x, BinaryAsyncAction y) {
            linkSubtasks(x, y);
            y.fork();
            x.fork();
        }

        private void completeThis() {
            super.complete(null);
        }

        private void completeThisExceptionally(Throwable ex) {
            super.completeExceptionally(ex);
        }

        public final void complete() {
            BinaryAsyncAction a = this;
            for (;;) {
                BinaryAsyncAction s = a.sibling;
                BinaryAsyncAction p = a.parent;
                a.sibling = null;
                a.parent = null;
                a.completeThis();
                if (p == null || p.compareAndSetControlState(0, 1))
                    break;
                try {
                    p.onComplete(a, s);
                } catch (Throwable rex) {
                    p.completeExceptionally(rex);
                    return;
                }
                a = p;
            }
        }

        public final void completeExceptionally(Throwable ex) {
            BinaryAsyncAction a = this;
            while (!a.isCompletedAbnormally()) {
                a.completeThisExceptionally(ex);
                BinaryAsyncAction s = a.sibling;
                if (s != null)
                    s.cancel(false);
                if (!a.onException() || (a = a.parent) == null)
                    break;
            }
        }

        public final BinaryAsyncAction getParent() {
            return parent;
        }

        public BinaryAsyncAction getSibling() {
            return sibling;
        }

        public void reinitialize() {
            parent = sibling = null;
            super.reinitialize();
        }

        protected final int getControlState() {
            return controlState;
        }

        protected final boolean compareAndSetControlState(int expect,
                                                          int update) {
            return controlStateUpdater.compareAndSet(this, expect, update);
        }

        protected final void setControlState(int value) {
            controlState = value;
        }

        protected final void incrementControlState() {
            controlStateUpdater.incrementAndGet(this);
        }

        protected final void decrementControlState() {
            controlStateUpdater.decrementAndGet(this);
        }

    }

    static final class AsyncFib extends BinaryAsyncAction {
        int number;
        public AsyncFib(int n) {
            this.number = n;
        }

        public final boolean exec() {
            AsyncFib f = this;
            int n = f.number;
            if (n > 1) {
                while (n > 1) {
                    AsyncFib p = f;
                    AsyncFib r = new AsyncFib(n - 2);
                    f = new AsyncFib(--n);
                    p.linkSubtasks(r, f);
                    r.fork();
                }
                f.number = n;
            }
            f.complete();
            return false;
        }

        protected void onComplete(BinaryAsyncAction x, BinaryAsyncAction y) {
            number = ((AsyncFib)x).number + ((AsyncFib)y).number;
        }
    }

    static final class FailingAsyncFib extends BinaryAsyncAction {
        int number;
        public FailingAsyncFib(int n) {
            this.number = n;
        }

        public final boolean exec() {
            FailingAsyncFib f = this;
            int n = f.number;
            if (n > 1) {
                while (n > 1) {
                    FailingAsyncFib p = f;
                    FailingAsyncFib r = new FailingAsyncFib(n - 2);
                    f = new FailingAsyncFib(--n);
                    p.linkSubtasks(r, f);
                    r.fork();
                }
                f.number = n;
            }
            f.complete();
            return false;
        }

        protected void onComplete(BinaryAsyncAction x, BinaryAsyncAction y) {
            completeExceptionally(new FJException());
        }
    }

    /**
     * invoke returns when task completes normally.
     * isCompletedAbnormally and isCancelled return false for normally
     * completed tasks; getRawResult returns null.
     */
    public void testInvoke() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertNull(f.invoke());
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * quietlyInvoke task returns when task completes normally.
     * isCompletedAbnormally and isCancelled return false for normally
     * completed tasks
     */
    public void testQuietlyInvoke() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                f.quietlyInvoke();
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * join of a forked task returns when task completes
     */
    public void testForkJoin() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertNull(f.join());
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * get of a forked task returns when task completes
     */
    public void testForkGet() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertNull(f.get());
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * timed get of a forked task returns when task completes
     */
    public void testForkTimedGet() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertNull(f.get(LONG_DELAY_MS, MILLISECONDS));
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * timed get with null time unit throws NPE
     */
    public void testForkTimedGetNPE() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                try {
                    f.get(5L, null);
                    shouldThrow();
                } catch (NullPointerException success) {}
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * quietlyJoin of a forked task returns when task completes
     */
    public void testForkQuietlyJoin() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                f.quietlyJoin();
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * helpQuiesce returns when tasks are complete.
     * getQueuedTaskCount returns 0 when quiescent
     */
    public void testForkHelpQuiesce() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                helpQuiesce();
                assertEquals(21, f.number);
                assertEquals(0, getQueuedTaskCount());
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invoke task throws exception when task completes abnormally
     */
    public void testAbnormalInvoke() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                try {
                    f.invoke();
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(f, success);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * quietlyInvoke task returns when task completes abnormally
     */
    public void testAbnormalQuietlyInvoke() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                f.quietlyInvoke();
                assertTrue(f.getException() instanceof FJException);
                checkCompletedAbnormally(f, f.getException());
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * join of a forked task throws exception when task completes abnormally
     */
    public void testAbnormalForkJoin() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                assertSame(f, f.fork());
                try {
                    f.join();
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(f, success);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * get of a forked task throws exception when task completes abnormally
     */
    public void testAbnormalForkGet() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                FailingAsyncFib f = new FailingAsyncFib(8);
                assertSame(f, f.fork());
                try {
                    f.get();
                    shouldThrow();
                } catch (ExecutionException success) {
                    Throwable cause = success.getCause();
                    assertTrue(cause instanceof FJException);
                    checkCompletedAbnormally(f, cause);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * timed get of a forked task throws exception when task completes abnormally
     */
    public void testAbnormalForkTimedGet() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                FailingAsyncFib f = new FailingAsyncFib(8);
                assertSame(f, f.fork());
                try {
                    f.get(LONG_DELAY_MS, MILLISECONDS);
                    shouldThrow();
                } catch (ExecutionException success) {
                    Throwable cause = success.getCause();
                    assertTrue(cause instanceof FJException);
                    checkCompletedAbnormally(f, cause);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * quietlyJoin of a forked task returns when task completes abnormally
     */
    public void testAbnormalForkQuietlyJoin() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                assertSame(f, f.fork());
                f.quietlyJoin();
                assertTrue(f.getException() instanceof FJException);
                checkCompletedAbnormally(f, f.getException());
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invoke task throws exception when task cancelled
     */
    public void testCancelledInvoke() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                try {
                    f.invoke();
                    shouldThrow();
                } catch (CancellationException success) {
                    checkCancelled(f);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * join of a forked task throws exception when task cancelled
     */
    public void testCancelledForkJoin() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                assertSame(f, f.fork());
                try {
                    f.join();
                    shouldThrow();
                } catch (CancellationException success) {
                    checkCancelled(f);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * get of a forked task throws exception when task cancelled
     */
    public void testCancelledForkGet() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                assertSame(f, f.fork());
                try {
                    f.get();
                    shouldThrow();
                } catch (CancellationException success) {
                    checkCancelled(f);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * timed get of a forked task throws exception when task cancelled
     */
    public void testCancelledForkTimedGet() throws Exception {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                assertSame(f, f.fork());
                try {
                    f.get(LONG_DELAY_MS, MILLISECONDS);
                    shouldThrow();
                } catch (CancellationException success) {
                    checkCancelled(f);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * quietlyJoin of a forked task returns when task cancelled
     */
    public void testCancelledForkQuietlyJoin() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                assertSame(f, f.fork());
                f.quietlyJoin();
                checkCancelled(f);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * getPool of executing task returns its pool
     */
    public void testGetPool() {
        final ForkJoinPool mainPool = mainPool();
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                assertSame(mainPool, getPool());
            }};
        testInvokeOnPool(mainPool, a);
    }

    /**
     * getPool of non-FJ task returns null
     */
    public void testGetPool2() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                assertNull(getPool());
            }};
        assertNull(a.invoke());
    }

    /**
     * inForkJoinPool of executing task returns true
     */
    public void testInForkJoinPool() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                assertTrue(inForkJoinPool());
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * inForkJoinPool of non-FJ task returns false
     */
    public void testInForkJoinPool2() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                assertFalse(inForkJoinPool());
            }};
        assertNull(a.invoke());
    }

    /**
     * setRawResult(null) succeeds
     */
    public void testSetRawResult() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                setRawResult(null);
                assertNull(getRawResult());
            }};
        assertNull(a.invoke());
    }

    /**
     * invoke task throws exception after invoking completeExceptionally
     */
    public void testCompleteExceptionally() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                f.completeExceptionally(new FJException());
                try {
                    f.invoke();
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(f, success);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(t1, t2) invokes all task arguments
     */
    public void testInvokeAll2() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                invokeAll(f, g);
                assertEquals(21, f.number);
                assertEquals(34, g.number);
                checkCompletedNormally(f);
                checkCompletedNormally(g);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(tasks) with 1 argument invokes task
     */
    public void testInvokeAll1() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                invokeAll(f);
                checkCompletedNormally(f);
                assertEquals(21, f.number);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(tasks) with > 2 argument invokes tasks
     */
    public void testInvokeAll3() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                AsyncFib h = new AsyncFib(7);
                invokeAll(f, g, h);
                assertEquals(21, f.number);
                assertEquals(34, g.number);
                assertEquals(13, h.number);
                checkCompletedNormally(f);
                checkCompletedNormally(g);
                checkCompletedNormally(h);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(collection) invokes all tasks in the collection
     */
    public void testInvokeAllCollection() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                AsyncFib h = new AsyncFib(7);
                HashSet set = new HashSet();
                set.add(f);
                set.add(g);
                set.add(h);
                invokeAll(set);
                assertEquals(21, f.number);
                assertEquals(34, g.number);
                assertEquals(13, h.number);
                checkCompletedNormally(f);
                checkCompletedNormally(g);
                checkCompletedNormally(h);
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(tasks) with any null task throws NPE
     */
    public void testInvokeAllNPE() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                AsyncFib h = null;
                try {
                    invokeAll(f, g, h);
                    shouldThrow();
                } catch (NullPointerException success) {}
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(t1, t2) throw exception if any task does
     */
    public void testAbnormalInvokeAll2() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                FailingAsyncFib g = new FailingAsyncFib(9);
                try {
                    invokeAll(f, g);
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(g, success);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(tasks) with 1 argument throws exception if task does
     */
    public void testAbnormalInvokeAll1() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib g = new FailingAsyncFib(9);
                try {
                    invokeAll(g);
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(g, success);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(tasks) with > 2 argument throws exception if any task does
     */
    public void testAbnormalInvokeAll3() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                FailingAsyncFib g = new FailingAsyncFib(9);
                AsyncFib h = new AsyncFib(7);
                try {
                    invokeAll(f, g, h);
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(g, success);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * invokeAll(collection)  throws exception if any task does
     */
    public void testAbnormalInvokeAllCollection() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                AsyncFib h = new AsyncFib(7);
                HashSet set = new HashSet();
                set.add(f);
                set.add(g);
                set.add(h);
                try {
                    invokeAll(set);
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(f, success);
                }
            }};
        testInvokeOnPool(mainPool(), a);
    }

    /**
     * tryUnfork returns true for most recent unexecuted task,
     * and suppresses execution
     */
    public void testTryUnfork() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib g = new AsyncFib(9);
                assertSame(g, g.fork());
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertTrue(f.tryUnfork());
                helpQuiesce();
                checkNotDone(f);
                checkCompletedNormally(g);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * getSurplusQueuedTaskCount returns > 0 when
     * there are more tasks than threads
     */
    public void testGetSurplusQueuedTaskCount() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib h = new AsyncFib(7);
                assertSame(h, h.fork());
                AsyncFib g = new AsyncFib(9);
                assertSame(g, g.fork());
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertTrue(getSurplusQueuedTaskCount() > 0);
                helpQuiesce();
                assertEquals(0, getSurplusQueuedTaskCount());
                checkCompletedNormally(f);
                checkCompletedNormally(g);
                checkCompletedNormally(h);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * peekNextLocalTask returns most recent unexecuted task.
     */
    public void testPeekNextLocalTask() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib g = new AsyncFib(9);
                assertSame(g, g.fork());
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertSame(f, peekNextLocalTask());
                assertNull(f.join());
                checkCompletedNormally(f);
                helpQuiesce();
                checkCompletedNormally(g);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * pollNextLocalTask returns most recent unexecuted task without
     * executing it
     */
    public void testPollNextLocalTask() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib g = new AsyncFib(9);
                assertSame(g, g.fork());
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertSame(f, pollNextLocalTask());
                helpQuiesce();
                checkNotDone(f);
                assertEquals(34, g.number);
                checkCompletedNormally(g);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * pollTask returns an unexecuted task without executing it
     */
    public void testPollTask() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib g = new AsyncFib(9);
                assertSame(g, g.fork());
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertSame(f, pollTask());
                helpQuiesce();
                checkNotDone(f);
                checkCompletedNormally(g);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * peekNextLocalTask returns least recent unexecuted task in async mode
     */
    public void testPeekNextLocalTaskAsync() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib g = new AsyncFib(9);
                assertSame(g, g.fork());
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertSame(g, peekNextLocalTask());
                assertNull(f.join());
                helpQuiesce();
                checkCompletedNormally(f);
                assertEquals(34, g.number);
                checkCompletedNormally(g);
            }};
        testInvokeOnPool(asyncSingletonPool(), a);
    }

    /**
     * pollNextLocalTask returns least recent unexecuted task without
     * executing it, in async mode
     */
    public void testPollNextLocalTaskAsync() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib g = new AsyncFib(9);
                assertSame(g, g.fork());
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertSame(g, pollNextLocalTask());
                helpQuiesce();
                assertEquals(21, f.number);
                checkCompletedNormally(f);
                checkNotDone(g);
            }};
        testInvokeOnPool(asyncSingletonPool(), a);
    }

    /**
     * pollTask returns an unexecuted task without executing it, in
     * async mode
     */
    public void testPollTaskAsync() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib g = new AsyncFib(9);
                assertSame(g, g.fork());
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertSame(g, pollTask());
                helpQuiesce();
                assertEquals(21, f.number);
                checkCompletedNormally(f);
                checkNotDone(g);
            }};
        testInvokeOnPool(asyncSingletonPool(), a);
    }

    // versions for singleton pools

    /**
     * invoke returns when task completes normally.
     * isCompletedAbnormally and isCancelled return false for normally
     * completed tasks; getRawResult returns null.
     */
    public void testInvokeSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertNull(f.invoke());
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * quietlyInvoke task returns when task completes normally.
     * isCompletedAbnormally and isCancelled return false for normally
     * completed tasks
     */
    public void testQuietlyInvokeSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                f.quietlyInvoke();
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * join of a forked task returns when task completes
     */
    public void testForkJoinSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertNull(f.join());
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * get of a forked task returns when task completes
     */
    public void testForkGetSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertNull(f.get());
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * timed get of a forked task returns when task completes
     */
    public void testForkTimedGetSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                assertNull(f.get(LONG_DELAY_MS, MILLISECONDS));
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * timed get with null time unit throws NPE
     */
    public void testForkTimedGetNPESingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                try {
                    f.get(5L, null);
                    shouldThrow();
                } catch (NullPointerException success) {}
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * quietlyJoin of a forked task returns when task completes
     */
    public void testForkQuietlyJoinSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                f.quietlyJoin();
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * helpQuiesce returns when tasks are complete.
     * getQueuedTaskCount returns 0 when quiescent
     */
    public void testForkHelpQuiesceSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertSame(f, f.fork());
                helpQuiesce();
                assertEquals(0, getQueuedTaskCount());
                assertEquals(21, f.number);
                checkCompletedNormally(f);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invoke task throws exception when task completes abnormally
     */
    public void testAbnormalInvokeSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                try {
                    f.invoke();
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(f, success);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * quietlyInvoke task returns when task completes abnormally
     */
    public void testAbnormalQuietlyInvokeSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                f.quietlyInvoke();
                assertTrue(f.getException() instanceof FJException);
                checkCompletedAbnormally(f, f.getException());
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * join of a forked task throws exception when task completes abnormally
     */
    public void testAbnormalForkJoinSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                assertSame(f, f.fork());
                try {
                    f.join();
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(f, success);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * get of a forked task throws exception when task completes abnormally
     */
    public void testAbnormalForkGetSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                FailingAsyncFib f = new FailingAsyncFib(8);
                assertSame(f, f.fork());
                try {
                    f.get();
                    shouldThrow();
                } catch (ExecutionException success) {
                    Throwable cause = success.getCause();
                    assertTrue(cause instanceof FJException);
                    checkCompletedAbnormally(f, cause);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * timed get of a forked task throws exception when task completes abnormally
     */
    public void testAbnormalForkTimedGetSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                FailingAsyncFib f = new FailingAsyncFib(8);
                assertSame(f, f.fork());
                try {
                    f.get(LONG_DELAY_MS, MILLISECONDS);
                    shouldThrow();
                } catch (ExecutionException success) {
                    Throwable cause = success.getCause();
                    assertTrue(cause instanceof FJException);
                    checkCompletedAbnormally(f, cause);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * quietlyJoin of a forked task returns when task completes abnormally
     */
    public void testAbnormalForkQuietlyJoinSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                assertSame(f, f.fork());
                f.quietlyJoin();
                assertTrue(f.getException() instanceof FJException);
                checkCompletedAbnormally(f, f.getException());
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invoke task throws exception when task cancelled
     */
    public void testCancelledInvokeSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                try {
                    f.invoke();
                    shouldThrow();
                } catch (CancellationException success) {
                    checkCancelled(f);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * join of a forked task throws exception when task cancelled
     */
    public void testCancelledForkJoinSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                assertSame(f, f.fork());
                try {
                    f.join();
                    shouldThrow();
                } catch (CancellationException success) {
                    checkCancelled(f);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * get of a forked task throws exception when task cancelled
     */
    public void testCancelledForkGetSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                assertSame(f, f.fork());
                try {
                    f.get();
                    shouldThrow();
                } catch (CancellationException success) {
                    checkCancelled(f);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * timed get of a forked task throws exception when task cancelled
     */
    public void testCancelledForkTimedGetSingleton() throws Exception {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() throws Exception {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                assertSame(f, f.fork());
                try {
                    f.get(LONG_DELAY_MS, MILLISECONDS);
                    shouldThrow();
                } catch (CancellationException success) {
                    checkCancelled(f);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * quietlyJoin of a forked task returns when task cancelled
     */
    public void testCancelledForkQuietlyJoinSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                assertTrue(f.cancel(true));
                assertSame(f, f.fork());
                f.quietlyJoin();
                checkCancelled(f);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invoke task throws exception after invoking completeExceptionally
     */
    public void testCompleteExceptionallySingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                f.completeExceptionally(new FJException());
                try {
                    f.invoke();
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(f, success);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(t1, t2) invokes all task arguments
     */
    public void testInvokeAll2Singleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                invokeAll(f, g);
                assertEquals(21, f.number);
                assertEquals(34, g.number);
                checkCompletedNormally(f);
                checkCompletedNormally(g);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(tasks) with 1 argument invokes task
     */
    public void testInvokeAll1Singleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                invokeAll(f);
                checkCompletedNormally(f);
                assertEquals(21, f.number);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(tasks) with > 2 argument invokes tasks
     */
    public void testInvokeAll3Singleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                AsyncFib h = new AsyncFib(7);
                invokeAll(f, g, h);
                assertEquals(21, f.number);
                assertEquals(34, g.number);
                assertEquals(13, h.number);
                checkCompletedNormally(f);
                checkCompletedNormally(g);
                checkCompletedNormally(h);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(collection) invokes all tasks in the collection
     */
    public void testInvokeAllCollectionSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                AsyncFib h = new AsyncFib(7);
                HashSet set = new HashSet();
                set.add(f);
                set.add(g);
                set.add(h);
                invokeAll(set);
                assertEquals(21, f.number);
                assertEquals(34, g.number);
                assertEquals(13, h.number);
                checkCompletedNormally(f);
                checkCompletedNormally(g);
                checkCompletedNormally(h);
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(tasks) with any null task throws NPE
     */
    public void testInvokeAllNPESingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                AsyncFib h = null;
                try {
                    invokeAll(f, g, h);
                    shouldThrow();
                } catch (NullPointerException success) {}
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(t1, t2) throw exception if any task does
     */
    public void testAbnormalInvokeAll2Singleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                FailingAsyncFib g = new FailingAsyncFib(9);
                try {
                    invokeAll(f, g);
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(g, success);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(tasks) with 1 argument throws exception if task does
     */
    public void testAbnormalInvokeAll1Singleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib g = new FailingAsyncFib(9);
                try {
                    invokeAll(g);
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(g, success);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(tasks) with > 2 argument throws exception if any task does
     */
    public void testAbnormalInvokeAll3Singleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                AsyncFib f = new AsyncFib(8);
                FailingAsyncFib g = new FailingAsyncFib(9);
                AsyncFib h = new AsyncFib(7);
                try {
                    invokeAll(f, g, h);
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(g, success);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

    /**
     * invokeAll(collection)  throws exception if any task does
     */
    public void testAbnormalInvokeAllCollectionSingleton() {
        RecursiveAction a = new CheckedRecursiveAction() {
            protected void realCompute() {
                FailingAsyncFib f = new FailingAsyncFib(8);
                AsyncFib g = new AsyncFib(9);
                AsyncFib h = new AsyncFib(7);
                HashSet set = new HashSet();
                set.add(f);
                set.add(g);
                set.add(h);
                try {
                    invokeAll(set);
                    shouldThrow();
                } catch (FJException success) {
                    checkCompletedAbnormally(f, success);
                }
            }};
        testInvokeOnPool(singletonPool(), a);
    }

}
