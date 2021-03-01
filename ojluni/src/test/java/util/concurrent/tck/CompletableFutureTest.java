/*
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
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea and Martin Buchholz with assistance from
 * members of JCP JSR-166 Expert Group and released to the public
 * domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package test.java.util.concurrent.tck;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.failedFuture;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

public class CompletableFutureTest extends JSR166TestCase {

    public static void main(String[] args) {
        main(suite(), args);
    }
    public static Test suite() {
        return new TestSuite(CompletableFutureTest.class);
    }

    static class CFException extends RuntimeException {}

    void checkIncomplete(CompletableFuture<?> f) {
        assertFalse(f.isDone());
        assertFalse(f.isCancelled());
        assertTrue(f.toString().contains("Not completed"));
        try {
            assertNull(f.getNow(null));
        } catch (Throwable fail) { threadUnexpectedException(fail); }
        try {
            f.get(0L, SECONDS);
            shouldThrow();
        }
        catch (TimeoutException success) {}
        catch (Throwable fail) { threadUnexpectedException(fail); }
    }

    <T> void checkCompletedNormally(CompletableFuture<T> f, T value) {
        checkTimedGet(f, value);

        try {
            assertEquals(value, f.join());
        } catch (Throwable fail) { threadUnexpectedException(fail); }
        try {
            assertEquals(value, f.getNow(null));
        } catch (Throwable fail) { threadUnexpectedException(fail); }
        try {
            assertEquals(value, f.get());
        } catch (Throwable fail) { threadUnexpectedException(fail); }
        assertTrue(f.isDone());
        assertFalse(f.isCancelled());
        assertFalse(f.isCompletedExceptionally());
        assertTrue(f.toString().contains("[Completed normally]"));
    }

    /**
     * Returns the "raw" internal exceptional completion of f,
     * without any additional wrapping with CompletionException.
     */
    Throwable exceptionalCompletion(CompletableFuture<?> f) {
        // handle (and whenComplete and exceptionally) can distinguish
        // between "direct" and "wrapped" exceptional completion
        return f.handle((u, t) -> t).join();
    }

    void checkCompletedExceptionally(CompletableFuture<?> f,
                                     boolean wrapped,
                                     Consumer<Throwable> checker) {
        Throwable cause = exceptionalCompletion(f);
        if (wrapped) {
            assertTrue(cause instanceof CompletionException);
            cause = cause.getCause();
        }
        checker.accept(cause);

        long startTime = System.nanoTime();
        try {
            f.get(LONG_DELAY_MS, MILLISECONDS);
            shouldThrow();
        } catch (ExecutionException success) {
            assertSame(cause, success.getCause());
        } catch (Throwable fail) { threadUnexpectedException(fail); }
        assertTrue(millisElapsedSince(startTime) < LONG_DELAY_MS / 2);

        try {
            f.join();
            shouldThrow();
        } catch (CompletionException success) {
            assertSame(cause, success.getCause());
        } catch (Throwable fail) { threadUnexpectedException(fail); }

        try {
            f.getNow(null);
            shouldThrow();
        } catch (CompletionException success) {
            assertSame(cause, success.getCause());
        } catch (Throwable fail) { threadUnexpectedException(fail); }

        try {
            f.get();
            shouldThrow();
        } catch (ExecutionException success) {
            assertSame(cause, success.getCause());
        } catch (Throwable fail) { threadUnexpectedException(fail); }

        assertFalse(f.isCancelled());
        assertTrue(f.isDone());
        assertTrue(f.isCompletedExceptionally());
        assertTrue(f.toString().contains("[Completed exceptionally]"));
    }

    void checkCompletedWithWrappedCFException(CompletableFuture<?> f) {
        checkCompletedExceptionally(f, true,
            t -> assertTrue(t instanceof CFException));
    }

    void checkCompletedWithWrappedCancellationException(CompletableFuture<?> f) {
        checkCompletedExceptionally(f, true,
            t -> assertTrue(t instanceof CancellationException));
    }

    void checkCompletedWithTimeoutException(CompletableFuture<?> f) {
        checkCompletedExceptionally(f, false,
            t -> assertTrue(t instanceof TimeoutException));
    }

    void checkCompletedWithWrappedException(CompletableFuture<?> f,
                                            Throwable ex) {
        checkCompletedExceptionally(f, true, t -> assertSame(t, ex));
    }

    void checkCompletedExceptionally(CompletableFuture<?> f, Throwable ex) {
        checkCompletedExceptionally(f, false, t -> assertSame(t, ex));
    }

    void checkCancelled(CompletableFuture<?> f) {
        long startTime = System.nanoTime();
        try {
            f.get(LONG_DELAY_MS, MILLISECONDS);
            shouldThrow();
        } catch (CancellationException success) {
        } catch (Throwable fail) { threadUnexpectedException(fail); }
        assertTrue(millisElapsedSince(startTime) < LONG_DELAY_MS / 2);

        try {
            f.join();
            shouldThrow();
        } catch (CancellationException success) {}
        try {
            f.getNow(null);
            shouldThrow();
        } catch (CancellationException success) {}
        try {
            f.get();
            shouldThrow();
        } catch (CancellationException success) {
        } catch (Throwable fail) { threadUnexpectedException(fail); }

        assertTrue(exceptionalCompletion(f) instanceof CancellationException);

        assertTrue(f.isDone());
        assertTrue(f.isCompletedExceptionally());
        assertTrue(f.isCancelled());
        assertTrue(f.toString().contains("[Completed exceptionally]"));
    }

    /**
     * A newly constructed CompletableFuture is incomplete, as indicated
     * by methods isDone, isCancelled, and getNow
     */
    public void testConstructor() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        checkIncomplete(f);
    }

    /**
     * complete completes normally, as indicated by methods isDone,
     * isCancelled, join, get, and getNow
     */
    public void testComplete() {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        checkIncomplete(f);
        assertTrue(f.complete(v1));
        assertFalse(f.complete(v1));
        checkCompletedNormally(f, v1);
    }}

    /**
     * completeExceptionally completes exceptionally, as indicated by
     * methods isDone, isCancelled, join, get, and getNow
     */
    public void testCompleteExceptionally() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CFException ex = new CFException();
        checkIncomplete(f);
        f.completeExceptionally(ex);
        checkCompletedExceptionally(f, ex);
    }

    /**
     * cancel completes exceptionally and reports cancelled, as indicated by
     * methods isDone, isCancelled, join, get, and getNow
     */
    public void testCancel() {
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        checkIncomplete(f);
        assertTrue(f.cancel(mayInterruptIfRunning));
        assertTrue(f.cancel(mayInterruptIfRunning));
        assertTrue(f.cancel(!mayInterruptIfRunning));
        checkCancelled(f);
    }}

    /**
     * obtrudeValue forces completion with given value
     */
    public void testObtrudeValue() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        checkIncomplete(f);
        assertTrue(f.complete(one));
        checkCompletedNormally(f, one);
        f.obtrudeValue(three);
        checkCompletedNormally(f, three);
        f.obtrudeValue(two);
        checkCompletedNormally(f, two);
        f = new CompletableFuture<>();
        f.obtrudeValue(three);
        checkCompletedNormally(f, three);
        f.obtrudeValue(null);
        checkCompletedNormally(f, null);
        f = new CompletableFuture<>();
        f.completeExceptionally(new CFException());
        f.obtrudeValue(four);
        checkCompletedNormally(f, four);
    }

    /**
     * obtrudeException forces completion with given exception
     */
    public void testObtrudeException() {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CFException ex;
        CompletableFuture<Integer> f;

        f = new CompletableFuture<>();
        assertTrue(f.complete(v1));
        for (int i = 0; i < 2; i++) {
            f.obtrudeException(ex = new CFException());
            checkCompletedExceptionally(f, ex);
        }

        f = new CompletableFuture<>();
        for (int i = 0; i < 2; i++) {
            f.obtrudeException(ex = new CFException());
            checkCompletedExceptionally(f, ex);
        }

        f = new CompletableFuture<>();
        f.completeExceptionally(ex = new CFException());
        f.obtrudeValue(v1);
        checkCompletedNormally(f, v1);
        f.obtrudeException(ex = new CFException());
        checkCompletedExceptionally(f, ex);
        f.completeExceptionally(new CFException());
        checkCompletedExceptionally(f, ex);
        assertFalse(f.complete(v1));
        checkCompletedExceptionally(f, ex);
    }}

    /**
     * getNumberOfDependents returns number of dependent tasks
     */
    public void testGetNumberOfDependents() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        assertEquals(0, f.getNumberOfDependents());
        final CompletableFuture<Void> g = m.thenRun(f, new Noop(m));
        assertEquals(1, f.getNumberOfDependents());
        assertEquals(0, g.getNumberOfDependents());
        final CompletableFuture<Void> h = m.thenRun(f, new Noop(m));
        assertEquals(2, f.getNumberOfDependents());
        assertEquals(0, h.getNumberOfDependents());
        assertTrue(f.complete(v1));
        checkCompletedNormally(g, null);
        checkCompletedNormally(h, null);
        assertEquals(0, f.getNumberOfDependents());
        assertEquals(0, g.getNumberOfDependents());
        assertEquals(0, h.getNumberOfDependents());
    }}

    /**
     * toString indicates current completion state
     */
    public void testToString() {
        CompletableFuture<String> f;

        f = new CompletableFuture<String>();
        assertTrue(f.toString().contains("[Not completed]"));

        assertTrue(f.complete("foo"));
        assertTrue(f.toString().contains("[Completed normally]"));

        f = new CompletableFuture<String>();
        assertTrue(f.completeExceptionally(new IndexOutOfBoundsException()));
        assertTrue(f.toString().contains("[Completed exceptionally]"));

        for (boolean mayInterruptIfRunning : new boolean[] { true, false }) {
            f = new CompletableFuture<String>();
            assertTrue(f.cancel(mayInterruptIfRunning));
            assertTrue(f.toString().contains("[Completed exceptionally]"));
        }
    }

    /**
     * completedFuture returns a completed CompletableFuture with given value
     */
    public void testCompletedFuture() {
        CompletableFuture<String> f = CompletableFuture.completedFuture("test");
        checkCompletedNormally(f, "test");
    }

    abstract static class CheckedAction {
        int invocationCount = 0;
        final ExecutionMode m;
        CheckedAction(ExecutionMode m) { this.m = m; }
        void invoked() {
            m.checkExecutionMode();
            assertEquals(0, invocationCount++);
        }
        void assertNotInvoked() { assertEquals(0, invocationCount); }
        void assertInvoked() { assertEquals(1, invocationCount); }
    }

    abstract static class CheckedIntegerAction extends CheckedAction {
        Integer value;
        CheckedIntegerAction(ExecutionMode m) { super(m); }
        void assertValue(Integer expected) {
            assertInvoked();
            assertEquals(expected, value);
        }
    }

    static class IntegerSupplier extends CheckedAction
        implements Supplier<Integer>
    {
        final Integer value;
        IntegerSupplier(ExecutionMode m, Integer value) {
            super(m);
            this.value = value;
        }
        public Integer get() {
            invoked();
            return value;
        }
    }

    // A function that handles and produces null values as well.
    static Integer inc(Integer x) {
        return (x == null) ? null : x + 1;
    }

    static class NoopConsumer extends CheckedIntegerAction
        implements Consumer<Integer>
    {
        NoopConsumer(ExecutionMode m) { super(m); }
        public void accept(Integer x) {
            invoked();
            value = x;
        }
    }

    static class IncFunction extends CheckedIntegerAction
        implements Function<Integer,Integer>
    {
        IncFunction(ExecutionMode m) { super(m); }
        public Integer apply(Integer x) {
            invoked();
            return value = inc(x);
        }
    }

    // Choose non-commutative actions for better coverage
    // A non-commutative function that handles and produces null values as well.
    static Integer subtract(Integer x, Integer y) {
        return (x == null && y == null) ? null :
            ((x == null) ? 42 : x.intValue())
            - ((y == null) ? 99 : y.intValue());
    }

    static class SubtractAction extends CheckedIntegerAction
        implements BiConsumer<Integer, Integer>
    {
        SubtractAction(ExecutionMode m) { super(m); }
        public void accept(Integer x, Integer y) {
            invoked();
            value = subtract(x, y);
        }
    }

    static class SubtractFunction extends CheckedIntegerAction
        implements BiFunction<Integer, Integer, Integer>
    {
        SubtractFunction(ExecutionMode m) { super(m); }
        public Integer apply(Integer x, Integer y) {
            invoked();
            return value = subtract(x, y);
        }
    }

    static class Noop extends CheckedAction implements Runnable {
        Noop(ExecutionMode m) { super(m); }
        public void run() {
            invoked();
        }
    }

    static class FailingSupplier extends CheckedAction
        implements Supplier<Integer>
    {
        final CFException ex;
        FailingSupplier(ExecutionMode m) { super(m); ex = new CFException(); }
        public Integer get() {
            invoked();
            throw ex;
        }
    }

    static class FailingConsumer extends CheckedIntegerAction
        implements Consumer<Integer>
    {
        final CFException ex;
        FailingConsumer(ExecutionMode m) { super(m); ex = new CFException(); }
        public void accept(Integer x) {
            invoked();
            value = x;
            throw ex;
        }
    }

    static class FailingBiConsumer extends CheckedIntegerAction
        implements BiConsumer<Integer, Integer>
    {
        final CFException ex;
        FailingBiConsumer(ExecutionMode m) { super(m); ex = new CFException(); }
        public void accept(Integer x, Integer y) {
            invoked();
            value = subtract(x, y);
            throw ex;
        }
    }

    static class FailingFunction extends CheckedIntegerAction
        implements Function<Integer, Integer>
    {
        final CFException ex;
        FailingFunction(ExecutionMode m) { super(m); ex = new CFException(); }
        public Integer apply(Integer x) {
            invoked();
            value = x;
            throw ex;
        }
    }

    static class FailingBiFunction extends CheckedIntegerAction
        implements BiFunction<Integer, Integer, Integer>
    {
        final CFException ex;
        FailingBiFunction(ExecutionMode m) { super(m); ex = new CFException(); }
        public Integer apply(Integer x, Integer y) {
            invoked();
            value = subtract(x, y);
            throw ex;
        }
    }

    static class FailingRunnable extends CheckedAction implements Runnable {
        final CFException ex;
        FailingRunnable(ExecutionMode m) { super(m); ex = new CFException(); }
        public void run() {
            invoked();
            throw ex;
        }
    }

    static class CompletableFutureInc extends CheckedIntegerAction
        implements Function<Integer, CompletableFuture<Integer>>
    {
        CompletableFutureInc(ExecutionMode m) { super(m); }
        public CompletableFuture<Integer> apply(Integer x) {
            invoked();
            value = x;
            CompletableFuture<Integer> f = new CompletableFuture<>();
            assertTrue(f.complete(inc(x)));
            return f;
        }
    }

    static class FailingCompletableFutureFunction extends CheckedIntegerAction
        implements Function<Integer, CompletableFuture<Integer>>
    {
        final CFException ex;
        FailingCompletableFutureFunction(ExecutionMode m) { super(m); ex = new CFException(); }
        public CompletableFuture<Integer> apply(Integer x) {
            invoked();
            value = x;
            throw ex;
        }
    }

    static class CountingRejectingExecutor implements Executor {
        final RejectedExecutionException ex = new RejectedExecutionException();
        final AtomicInteger count = new AtomicInteger(0);
        public void execute(Runnable r) {
            count.getAndIncrement();
            throw ex;
        }
    }

    // Used for explicit executor tests
    static final class ThreadExecutor implements Executor {
        final AtomicInteger count = new AtomicInteger(0);
        static final ThreadGroup tg = new ThreadGroup("ThreadExecutor");
        static boolean startedCurrentThread() {
            return Thread.currentThread().getThreadGroup() == tg;
        }

        public void execute(Runnable r) {
            count.getAndIncrement();
            new Thread(tg, r).start();
        }
    }

    static final boolean defaultExecutorIsCommonPool
        = ForkJoinPool.getCommonPoolParallelism() > 1;

    /**
     * Permits the testing of parallel code for the 3 different
     * execution modes without copy/pasting all the test methods.
     */
    enum ExecutionMode {
        SYNC {
            public void checkExecutionMode() {
                assertFalse(ThreadExecutor.startedCurrentThread());
                assertNull(ForkJoinTask.getPool());
            }
            public CompletableFuture<Void> runAsync(Runnable a) {
                throw new UnsupportedOperationException();
            }
            public <U> CompletableFuture<U> supplyAsync(Supplier<U> a) {
                throw new UnsupportedOperationException();
            }
            public <T> CompletableFuture<Void> thenRun
                (CompletableFuture<T> f, Runnable a) {
                return f.thenRun(a);
            }
            public <T> CompletableFuture<Void> thenAccept
                (CompletableFuture<T> f, Consumer<? super T> a) {
                return f.thenAccept(a);
            }
            public <T,U> CompletableFuture<U> thenApply
                (CompletableFuture<T> f, Function<? super T,U> a) {
                return f.thenApply(a);
            }
            public <T,U> CompletableFuture<U> thenCompose
                (CompletableFuture<T> f,
                 Function<? super T,? extends CompletionStage<U>> a) {
                return f.thenCompose(a);
            }
            public <T,U> CompletableFuture<U> handle
                (CompletableFuture<T> f,
                 BiFunction<? super T,Throwable,? extends U> a) {
                return f.handle(a);
            }
            public <T> CompletableFuture<T> whenComplete
                (CompletableFuture<T> f,
                 BiConsumer<? super T,? super Throwable> a) {
                return f.whenComplete(a);
            }
            public <T,U> CompletableFuture<Void> runAfterBoth
                (CompletableFuture<T> f, CompletableFuture<U> g, Runnable a) {
                return f.runAfterBoth(g, a);
            }
            public <T,U> CompletableFuture<Void> thenAcceptBoth
                (CompletableFuture<T> f,
                 CompletionStage<? extends U> g,
                 BiConsumer<? super T,? super U> a) {
                return f.thenAcceptBoth(g, a);
            }
            public <T,U,V> CompletableFuture<V> thenCombine
                (CompletableFuture<T> f,
                 CompletionStage<? extends U> g,
                 BiFunction<? super T,? super U,? extends V> a) {
                return f.thenCombine(g, a);
            }
            public <T> CompletableFuture<Void> runAfterEither
                (CompletableFuture<T> f,
                 CompletionStage<?> g,
                 java.lang.Runnable a) {
                return f.runAfterEither(g, a);
            }
            public <T> CompletableFuture<Void> acceptEither
                (CompletableFuture<T> f,
                 CompletionStage<? extends T> g,
                 Consumer<? super T> a) {
                return f.acceptEither(g, a);
            }
            public <T,U> CompletableFuture<U> applyToEither
                (CompletableFuture<T> f,
                 CompletionStage<? extends T> g,
                 Function<? super T,U> a) {
                return f.applyToEither(g, a);
            }
        },

        ASYNC {
            public void checkExecutionMode() {
                assertEquals(defaultExecutorIsCommonPool,
                             (ForkJoinPool.commonPool() == ForkJoinTask.getPool()));
            }
            public CompletableFuture<Void> runAsync(Runnable a) {
                return CompletableFuture.runAsync(a);
            }
            public <U> CompletableFuture<U> supplyAsync(Supplier<U> a) {
                return CompletableFuture.supplyAsync(a);
            }
            public <T> CompletableFuture<Void> thenRun
                (CompletableFuture<T> f, Runnable a) {
                return f.thenRunAsync(a);
            }
            public <T> CompletableFuture<Void> thenAccept
                (CompletableFuture<T> f, Consumer<? super T> a) {
                return f.thenAcceptAsync(a);
            }
            public <T,U> CompletableFuture<U> thenApply
                (CompletableFuture<T> f, Function<? super T,U> a) {
                return f.thenApplyAsync(a);
            }
            public <T,U> CompletableFuture<U> thenCompose
                (CompletableFuture<T> f,
                 Function<? super T,? extends CompletionStage<U>> a) {
                return f.thenComposeAsync(a);
            }
            public <T,U> CompletableFuture<U> handle
                (CompletableFuture<T> f,
                 BiFunction<? super T,Throwable,? extends U> a) {
                return f.handleAsync(a);
            }
            public <T> CompletableFuture<T> whenComplete
                (CompletableFuture<T> f,
                 BiConsumer<? super T,? super Throwable> a) {
                return f.whenCompleteAsync(a);
            }
            public <T,U> CompletableFuture<Void> runAfterBoth
                (CompletableFuture<T> f, CompletableFuture<U> g, Runnable a) {
                return f.runAfterBothAsync(g, a);
            }
            public <T,U> CompletableFuture<Void> thenAcceptBoth
                (CompletableFuture<T> f,
                 CompletionStage<? extends U> g,
                 BiConsumer<? super T,? super U> a) {
                return f.thenAcceptBothAsync(g, a);
            }
            public <T,U,V> CompletableFuture<V> thenCombine
                (CompletableFuture<T> f,
                 CompletionStage<? extends U> g,
                 BiFunction<? super T,? super U,? extends V> a) {
                return f.thenCombineAsync(g, a);
            }
            public <T> CompletableFuture<Void> runAfterEither
                (CompletableFuture<T> f,
                 CompletionStage<?> g,
                 java.lang.Runnable a) {
                return f.runAfterEitherAsync(g, a);
            }
            public <T> CompletableFuture<Void> acceptEither
                (CompletableFuture<T> f,
                 CompletionStage<? extends T> g,
                 Consumer<? super T> a) {
                return f.acceptEitherAsync(g, a);
            }
            public <T,U> CompletableFuture<U> applyToEither
                (CompletableFuture<T> f,
                 CompletionStage<? extends T> g,
                 Function<? super T,U> a) {
                return f.applyToEitherAsync(g, a);
            }
        },

        EXECUTOR {
            public void checkExecutionMode() {
                assertTrue(ThreadExecutor.startedCurrentThread());
            }
            public CompletableFuture<Void> runAsync(Runnable a) {
                return CompletableFuture.runAsync(a, new ThreadExecutor());
            }
            public <U> CompletableFuture<U> supplyAsync(Supplier<U> a) {
                return CompletableFuture.supplyAsync(a, new ThreadExecutor());
            }
            public <T> CompletableFuture<Void> thenRun
                (CompletableFuture<T> f, Runnable a) {
                return f.thenRunAsync(a, new ThreadExecutor());
            }
            public <T> CompletableFuture<Void> thenAccept
                (CompletableFuture<T> f, Consumer<? super T> a) {
                return f.thenAcceptAsync(a, new ThreadExecutor());
            }
            public <T,U> CompletableFuture<U> thenApply
                (CompletableFuture<T> f, Function<? super T,U> a) {
                return f.thenApplyAsync(a, new ThreadExecutor());
            }
            public <T,U> CompletableFuture<U> thenCompose
                (CompletableFuture<T> f,
                 Function<? super T,? extends CompletionStage<U>> a) {
                return f.thenComposeAsync(a, new ThreadExecutor());
            }
            public <T,U> CompletableFuture<U> handle
                (CompletableFuture<T> f,
                 BiFunction<? super T,Throwable,? extends U> a) {
                return f.handleAsync(a, new ThreadExecutor());
            }
            public <T> CompletableFuture<T> whenComplete
                (CompletableFuture<T> f,
                 BiConsumer<? super T,? super Throwable> a) {
                return f.whenCompleteAsync(a, new ThreadExecutor());
            }
            public <T,U> CompletableFuture<Void> runAfterBoth
                (CompletableFuture<T> f, CompletableFuture<U> g, Runnable a) {
                return f.runAfterBothAsync(g, a, new ThreadExecutor());
            }
            public <T,U> CompletableFuture<Void> thenAcceptBoth
                (CompletableFuture<T> f,
                 CompletionStage<? extends U> g,
                 BiConsumer<? super T,? super U> a) {
                return f.thenAcceptBothAsync(g, a, new ThreadExecutor());
            }
            public <T,U,V> CompletableFuture<V> thenCombine
                (CompletableFuture<T> f,
                 CompletionStage<? extends U> g,
                 BiFunction<? super T,? super U,? extends V> a) {
                return f.thenCombineAsync(g, a, new ThreadExecutor());
            }
            public <T> CompletableFuture<Void> runAfterEither
                (CompletableFuture<T> f,
                 CompletionStage<?> g,
                 java.lang.Runnable a) {
                return f.runAfterEitherAsync(g, a, new ThreadExecutor());
            }
            public <T> CompletableFuture<Void> acceptEither
                (CompletableFuture<T> f,
                 CompletionStage<? extends T> g,
                 Consumer<? super T> a) {
                return f.acceptEitherAsync(g, a, new ThreadExecutor());
            }
            public <T,U> CompletableFuture<U> applyToEither
                (CompletableFuture<T> f,
                 CompletionStage<? extends T> g,
                 Function<? super T,U> a) {
                return f.applyToEitherAsync(g, a, new ThreadExecutor());
            }
        };

        public abstract void checkExecutionMode();
        public abstract CompletableFuture<Void> runAsync(Runnable a);
        public abstract <U> CompletableFuture<U> supplyAsync(Supplier<U> a);
        public abstract <T> CompletableFuture<Void> thenRun
            (CompletableFuture<T> f, Runnable a);
        public abstract <T> CompletableFuture<Void> thenAccept
            (CompletableFuture<T> f, Consumer<? super T> a);
        public abstract <T,U> CompletableFuture<U> thenApply
            (CompletableFuture<T> f, Function<? super T,U> a);
        public abstract <T,U> CompletableFuture<U> thenCompose
            (CompletableFuture<T> f,
             Function<? super T,? extends CompletionStage<U>> a);
        public abstract <T,U> CompletableFuture<U> handle
            (CompletableFuture<T> f,
             BiFunction<? super T,Throwable,? extends U> a);
        public abstract <T> CompletableFuture<T> whenComplete
            (CompletableFuture<T> f,
             BiConsumer<? super T,? super Throwable> a);
        public abstract <T,U> CompletableFuture<Void> runAfterBoth
            (CompletableFuture<T> f, CompletableFuture<U> g, Runnable a);
        public abstract <T,U> CompletableFuture<Void> thenAcceptBoth
            (CompletableFuture<T> f,
             CompletionStage<? extends U> g,
             BiConsumer<? super T,? super U> a);
        public abstract <T,U,V> CompletableFuture<V> thenCombine
            (CompletableFuture<T> f,
             CompletionStage<? extends U> g,
             BiFunction<? super T,? super U,? extends V> a);
        public abstract <T> CompletableFuture<Void> runAfterEither
            (CompletableFuture<T> f,
             CompletionStage<?> g,
             java.lang.Runnable a);
        public abstract <T> CompletableFuture<Void> acceptEither
            (CompletableFuture<T> f,
             CompletionStage<? extends T> g,
             Consumer<? super T> a);
        public abstract <T,U> CompletableFuture<U> applyToEither
            (CompletableFuture<T> f,
             CompletionStage<? extends T> g,
             Function<? super T,U> a);
    }

    /**
     * exceptionally action is not invoked when source completes
     * normally, and source result is propagated
     */
    public void testExceptionally_normalCompletion() {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) assertTrue(f.complete(v1));
        final CompletableFuture<Integer> g = f.exceptionally
            ((Throwable t) -> {
                a.getAndIncrement();
                threadFail("should not be called");
                return null;            // unreached
            });
        if (createIncomplete) assertTrue(f.complete(v1));

        checkCompletedNormally(g, v1);
        checkCompletedNormally(f, v1);
        assertEquals(0, a.get());
    }}

    /**
     * exceptionally action completes with function value on source
     * exception
     */
    public void testExceptionally_exceptionalCompletion() {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CFException ex = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) f.completeExceptionally(ex);
        final CompletableFuture<Integer> g = f.exceptionally
            ((Throwable t) -> {
                ExecutionMode.SYNC.checkExecutionMode();
                threadAssertSame(t, ex);
                a.getAndIncrement();
                return v1;
            });
        if (createIncomplete) f.completeExceptionally(ex);

        checkCompletedNormally(g, v1);
        assertEquals(1, a.get());
    }}

    /**
     * If an "exceptionally action" throws an exception, it completes
     * exceptionally with that exception
     */
    public void testExceptionally_exceptionalCompletionActionFailed() {
        for (boolean createIncomplete : new boolean[] { true, false })
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CFException ex1 = new CFException();
        final CFException ex2 = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) f.completeExceptionally(ex1);
        final CompletableFuture<Integer> g = f.exceptionally
            ((Throwable t) -> {
                ExecutionMode.SYNC.checkExecutionMode();
                threadAssertSame(t, ex1);
                a.getAndIncrement();
                throw ex2;
            });
        if (createIncomplete) f.completeExceptionally(ex1);

        checkCompletedWithWrappedException(g, ex2);
        checkCompletedExceptionally(f, ex1);
        assertEquals(1, a.get());
    }}

    /**
     * whenComplete action executes on normal completion, propagating
     * source result.
     */
    public void testWhenComplete_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) assertTrue(f.complete(v1));
        final CompletableFuture<Integer> g = m.whenComplete
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertSame(result, v1);
                threadAssertNull(t);
                a.getAndIncrement();
            });
        if (createIncomplete) assertTrue(f.complete(v1));

        checkCompletedNormally(g, v1);
        checkCompletedNormally(f, v1);
        assertEquals(1, a.get());
    }}

    /**
     * whenComplete action executes on exceptional completion, propagating
     * source result.
     */
    public void testWhenComplete_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CFException ex = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) f.completeExceptionally(ex);
        final CompletableFuture<Integer> g = m.whenComplete
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertNull(result);
                threadAssertSame(t, ex);
                a.getAndIncrement();
            });
        if (createIncomplete) f.completeExceptionally(ex);

        checkCompletedWithWrappedException(g, ex);
        checkCompletedExceptionally(f, ex);
        assertEquals(1, a.get());
    }}

    /**
     * whenComplete action executes on cancelled source, propagating
     * CancellationException.
     */
    public void testWhenComplete_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (boolean createIncomplete : new boolean[] { true, false })
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) assertTrue(f.cancel(mayInterruptIfRunning));
        final CompletableFuture<Integer> g = m.whenComplete
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertNull(result);
                threadAssertTrue(t instanceof CancellationException);
                a.getAndIncrement();
            });
        if (createIncomplete) assertTrue(f.cancel(mayInterruptIfRunning));

        checkCompletedWithWrappedCancellationException(g);
        checkCancelled(f);
        assertEquals(1, a.get());
    }}

    /**
     * If a whenComplete action throws an exception when triggered by
     * a normal completion, it completes exceptionally
     */
    public void testWhenComplete_sourceCompletedNormallyActionFailed() {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CFException ex = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) assertTrue(f.complete(v1));
        final CompletableFuture<Integer> g = m.whenComplete
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertSame(result, v1);
                threadAssertNull(t);
                a.getAndIncrement();
                throw ex;
            });
        if (createIncomplete) assertTrue(f.complete(v1));

        checkCompletedWithWrappedException(g, ex);
        checkCompletedNormally(f, v1);
        assertEquals(1, a.get());
    }}

    /**
     * If a whenComplete action throws an exception when triggered by
     * a source completion that also throws an exception, the source
     * exception takes precedence (unlike handle)
     */
    public void testWhenComplete_sourceFailedActionFailed() {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (ExecutionMode m : ExecutionMode.values())
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CFException ex1 = new CFException();
        final CFException ex2 = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();

        if (!createIncomplete) f.completeExceptionally(ex1);
        final CompletableFuture<Integer> g = m.whenComplete
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertSame(t, ex1);
                threadAssertNull(result);
                a.getAndIncrement();
                throw ex2;
            });
        if (createIncomplete) f.completeExceptionally(ex1);

        checkCompletedWithWrappedException(g, ex1);
        checkCompletedExceptionally(f, ex1);
        if (testImplementationDetails) {
            assertEquals(1, ex1.getSuppressed().length);
            assertSame(ex2, ex1.getSuppressed()[0]);
        }
        assertEquals(1, a.get());
    }}

    /**
     * handle action completes normally with function value on normal
     * completion of source
     */
    public void testHandle_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final AtomicInteger a = new AtomicInteger(0);
        if (!createIncomplete) assertTrue(f.complete(v1));
        final CompletableFuture<Integer> g = m.handle
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertSame(result, v1);
                threadAssertNull(t);
                a.getAndIncrement();
                return inc(v1);
            });
        if (createIncomplete) assertTrue(f.complete(v1));

        checkCompletedNormally(g, inc(v1));
        checkCompletedNormally(f, v1);
        assertEquals(1, a.get());
    }}

    /**
     * handle action completes normally with function value on
     * exceptional completion of source
     */
    public void testHandle_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final AtomicInteger a = new AtomicInteger(0);
        final CFException ex = new CFException();
        if (!createIncomplete) f.completeExceptionally(ex);
        final CompletableFuture<Integer> g = m.handle
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertNull(result);
                threadAssertSame(t, ex);
                a.getAndIncrement();
                return v1;
            });
        if (createIncomplete) f.completeExceptionally(ex);

        checkCompletedNormally(g, v1);
        checkCompletedExceptionally(f, ex);
        assertEquals(1, a.get());
    }}

    /**
     * handle action completes normally with function value on
     * cancelled source
     */
    public void testHandle_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final AtomicInteger a = new AtomicInteger(0);
        if (!createIncomplete) assertTrue(f.cancel(mayInterruptIfRunning));
        final CompletableFuture<Integer> g = m.handle
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertNull(result);
                threadAssertTrue(t instanceof CancellationException);
                a.getAndIncrement();
                return v1;
            });
        if (createIncomplete) assertTrue(f.cancel(mayInterruptIfRunning));

        checkCompletedNormally(g, v1);
        checkCancelled(f);
        assertEquals(1, a.get());
    }}

    /**
     * If a "handle action" throws an exception when triggered by
     * a normal completion, it completes exceptionally
     */
    public void testHandle_sourceCompletedNormallyActionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final AtomicInteger a = new AtomicInteger(0);
        final CFException ex = new CFException();
        if (!createIncomplete) assertTrue(f.complete(v1));
        final CompletableFuture<Integer> g = m.handle
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertSame(result, v1);
                threadAssertNull(t);
                a.getAndIncrement();
                throw ex;
            });
        if (createIncomplete) assertTrue(f.complete(v1));

        checkCompletedWithWrappedException(g, ex);
        checkCompletedNormally(f, v1);
        assertEquals(1, a.get());
    }}

    /**
     * If a "handle action" throws an exception when triggered by
     * a source completion that also throws an exception, the action
     * exception takes precedence (unlike whenComplete)
     */
    public void testHandle_sourceFailedActionFailed() {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (ExecutionMode m : ExecutionMode.values())
    {
        final AtomicInteger a = new AtomicInteger(0);
        final CFException ex1 = new CFException();
        final CFException ex2 = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();

        if (!createIncomplete) f.completeExceptionally(ex1);
        final CompletableFuture<Integer> g = m.handle
            (f,
             (Integer result, Throwable t) -> {
                m.checkExecutionMode();
                threadAssertNull(result);
                threadAssertSame(ex1, t);
                a.getAndIncrement();
                throw ex2;
            });
        if (createIncomplete) f.completeExceptionally(ex1);

        checkCompletedWithWrappedException(g, ex2);
        checkCompletedExceptionally(f, ex1);
        assertEquals(1, a.get());
    }}

    /**
     * runAsync completes after running Runnable
     */
    public void testRunAsync_normalCompletion() {
        ExecutionMode[] executionModes = {
            ExecutionMode.ASYNC,
            ExecutionMode.EXECUTOR,
        };
        for (ExecutionMode m : executionModes)
    {
        final Noop r = new Noop(m);
        final CompletableFuture<Void> f = m.runAsync(r);
        assertNull(f.join());
        checkCompletedNormally(f, null);
        r.assertInvoked();
    }}

    /**
     * failing runAsync completes exceptionally after running Runnable
     */
    public void testRunAsync_exceptionalCompletion() {
        ExecutionMode[] executionModes = {
            ExecutionMode.ASYNC,
            ExecutionMode.EXECUTOR,
        };
        for (ExecutionMode m : executionModes)
    {
        final FailingRunnable r = new FailingRunnable(m);
        final CompletableFuture<Void> f = m.runAsync(r);
        checkCompletedWithWrappedException(f, r.ex);
        r.assertInvoked();
    }}

    public void testRunAsync_rejectingExecutor() {
        CountingRejectingExecutor e = new CountingRejectingExecutor();
        try {
            CompletableFuture.runAsync(() -> {}, e);
            shouldThrow();
        } catch (Throwable t) {
            assertSame(e.ex, t);
        }

        assertEquals(1, e.count.get());
    }

    /**
     * supplyAsync completes with result of supplier
     */
    public void testSupplyAsync_normalCompletion() {
        ExecutionMode[] executionModes = {
            ExecutionMode.ASYNC,
            ExecutionMode.EXECUTOR,
        };
        for (ExecutionMode m : executionModes)
        for (Integer v1 : new Integer[] { 1, null })
    {
        final IntegerSupplier r = new IntegerSupplier(m, v1);
        final CompletableFuture<Integer> f = m.supplyAsync(r);
        assertSame(v1, f.join());
        checkCompletedNormally(f, v1);
        r.assertInvoked();
    }}

    /**
     * Failing supplyAsync completes exceptionally
     */
    public void testSupplyAsync_exceptionalCompletion() {
        ExecutionMode[] executionModes = {
            ExecutionMode.ASYNC,
            ExecutionMode.EXECUTOR,
        };
        for (ExecutionMode m : executionModes)
    {
        FailingSupplier r = new FailingSupplier(m);
        CompletableFuture<Integer> f = m.supplyAsync(r);
        checkCompletedWithWrappedException(f, r.ex);
        r.assertInvoked();
    }}

    public void testSupplyAsync_rejectingExecutor() {
        CountingRejectingExecutor e = new CountingRejectingExecutor();
        try {
            CompletableFuture.supplyAsync(() -> null, e);
            shouldThrow();
        } catch (Throwable t) {
            assertSame(e.ex, t);
        }

        assertEquals(1, e.count.get());
    }

    // seq completion methods

    /**
     * thenRun result completes normally after normal completion of source
     */
    public void testThenRun_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final Noop[] rs = new Noop[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new Noop(m);

        final CompletableFuture<Void> h0 = m.thenRun(f, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterBoth(f, f, rs[1]);
        final CompletableFuture<Void> h2 = m.runAfterEither(f, f, rs[2]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(f.complete(v1));
        final CompletableFuture<Void> h3 = m.thenRun(f, rs[3]);
        final CompletableFuture<Void> h4 = m.runAfterBoth(f, f, rs[4]);
        final CompletableFuture<Void> h5 = m.runAfterEither(f, f, rs[5]);

        checkCompletedNormally(h0, null);
        checkCompletedNormally(h1, null);
        checkCompletedNormally(h2, null);
        checkCompletedNormally(h3, null);
        checkCompletedNormally(h4, null);
        checkCompletedNormally(h5, null);
        checkCompletedNormally(f, v1);
        for (Noop r : rs) r.assertInvoked();
    }}

    /**
     * thenRun result completes exceptionally after exceptional
     * completion of source
     */
    public void testThenRun_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
    {
        final CFException ex = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final Noop[] rs = new Noop[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new Noop(m);

        final CompletableFuture<Void> h0 = m.thenRun(f, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterBoth(f, f, rs[1]);
        final CompletableFuture<Void> h2 = m.runAfterEither(f, f, rs[2]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(f.completeExceptionally(ex));
        final CompletableFuture<Void> h3 = m.thenRun(f, rs[3]);
        final CompletableFuture<Void> h4 = m.runAfterBoth(f, f, rs[4]);
        final CompletableFuture<Void> h5 = m.runAfterEither(f, f, rs[5]);

        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        checkCompletedWithWrappedException(h4, ex);
        checkCompletedWithWrappedException(h5, ex);
        checkCompletedExceptionally(f, ex);
        for (Noop r : rs) r.assertNotInvoked();
    }}

    /**
     * thenRun result completes exceptionally if source cancelled
     */
    public void testThenRun_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final Noop[] rs = new Noop[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new Noop(m);

        final CompletableFuture<Void> h0 = m.thenRun(f, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterBoth(f, f, rs[1]);
        final CompletableFuture<Void> h2 = m.runAfterEither(f, f, rs[2]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(f.cancel(mayInterruptIfRunning));
        final CompletableFuture<Void> h3 = m.thenRun(f, rs[3]);
        final CompletableFuture<Void> h4 = m.runAfterBoth(f, f, rs[4]);
        final CompletableFuture<Void> h5 = m.runAfterEither(f, f, rs[5]);

        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        checkCompletedWithWrappedCancellationException(h4);
        checkCompletedWithWrappedCancellationException(h5);
        checkCancelled(f);
        for (Noop r : rs) r.assertNotInvoked();
    }}

    /**
     * thenRun result completes exceptionally if action does
     */
    public void testThenRun_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final FailingRunnable[] rs = new FailingRunnable[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new FailingRunnable(m);

        final CompletableFuture<Void> h0 = m.thenRun(f, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterBoth(f, f, rs[1]);
        final CompletableFuture<Void> h2 = m.runAfterEither(f, f, rs[2]);
        assertTrue(f.complete(v1));
        final CompletableFuture<Void> h3 = m.thenRun(f, rs[3]);
        final CompletableFuture<Void> h4 = m.runAfterBoth(f, f, rs[4]);
        final CompletableFuture<Void> h5 = m.runAfterEither(f, f, rs[5]);

        checkCompletedWithWrappedException(h0, rs[0].ex);
        checkCompletedWithWrappedException(h1, rs[1].ex);
        checkCompletedWithWrappedException(h2, rs[2].ex);
        checkCompletedWithWrappedException(h3, rs[3].ex);
        checkCompletedWithWrappedException(h4, rs[4].ex);
        checkCompletedWithWrappedException(h5, rs[5].ex);
        checkCompletedNormally(f, v1);
    }}

    /**
     * thenApply result completes normally after normal completion of source
     */
    public void testThenApply_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final IncFunction[] rs = new IncFunction[4];
        for (int i = 0; i < rs.length; i++) rs[i] = new IncFunction(m);

        final CompletableFuture<Integer> h0 = m.thenApply(f, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(f, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        assertTrue(f.complete(v1));
        final CompletableFuture<Integer> h2 = m.thenApply(f, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(f, f, rs[3]);

        checkCompletedNormally(h0, inc(v1));
        checkCompletedNormally(h1, inc(v1));
        checkCompletedNormally(h2, inc(v1));
        checkCompletedNormally(h3, inc(v1));
        checkCompletedNormally(f, v1);
        for (IncFunction r : rs) r.assertValue(inc(v1));
    }}

    /**
     * thenApply result completes exceptionally after exceptional
     * completion of source
     */
    public void testThenApply_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
    {
        final CFException ex = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final IncFunction[] rs = new IncFunction[4];
        for (int i = 0; i < rs.length; i++) rs[i] = new IncFunction(m);

        final CompletableFuture<Integer> h0 = m.thenApply(f, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(f, f, rs[1]);
        assertTrue(f.completeExceptionally(ex));
        final CompletableFuture<Integer> h2 = m.thenApply(f, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(f, f, rs[3]);

        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        checkCompletedExceptionally(f, ex);
        for (IncFunction r : rs) r.assertNotInvoked();
    }}

    /**
     * thenApply result completes exceptionally if source cancelled
     */
    public void testThenApply_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final IncFunction[] rs = new IncFunction[4];
        for (int i = 0; i < rs.length; i++) rs[i] = new IncFunction(m);

        final CompletableFuture<Integer> h0 = m.thenApply(f, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(f, f, rs[1]);
        assertTrue(f.cancel(mayInterruptIfRunning));
        final CompletableFuture<Integer> h2 = m.thenApply(f, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(f, f, rs[3]);

        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        checkCancelled(f);
        for (IncFunction r : rs) r.assertNotInvoked();
    }}

    /**
     * thenApply result completes exceptionally if action does
     */
    public void testThenApply_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final FailingFunction[] rs = new FailingFunction[4];
        for (int i = 0; i < rs.length; i++) rs[i] = new FailingFunction(m);

        final CompletableFuture<Integer> h0 = m.thenApply(f, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(f, f, rs[1]);
        assertTrue(f.complete(v1));
        final CompletableFuture<Integer> h2 = m.thenApply(f, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(f, f, rs[3]);

        checkCompletedWithWrappedException(h0, rs[0].ex);
        checkCompletedWithWrappedException(h1, rs[1].ex);
        checkCompletedWithWrappedException(h2, rs[2].ex);
        checkCompletedWithWrappedException(h3, rs[3].ex);
        checkCompletedNormally(f, v1);
    }}

    /**
     * thenAccept result completes normally after normal completion of source
     */
    public void testThenAccept_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final NoopConsumer[] rs = new NoopConsumer[4];
        for (int i = 0; i < rs.length; i++) rs[i] = new NoopConsumer(m);

        final CompletableFuture<Void> h0 = m.thenAccept(f, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(f, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        assertTrue(f.complete(v1));
        final CompletableFuture<Void> h2 = m.thenAccept(f, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(f, f, rs[3]);

        checkCompletedNormally(h0, null);
        checkCompletedNormally(h1, null);
        checkCompletedNormally(h2, null);
        checkCompletedNormally(h3, null);
        checkCompletedNormally(f, v1);
        for (NoopConsumer r : rs) r.assertValue(v1);
    }}

    /**
     * thenAccept result completes exceptionally after exceptional
     * completion of source
     */
    public void testThenAccept_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
    {
        final CFException ex = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final NoopConsumer[] rs = new NoopConsumer[4];
        for (int i = 0; i < rs.length; i++) rs[i] = new NoopConsumer(m);

        final CompletableFuture<Void> h0 = m.thenAccept(f, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(f, f, rs[1]);
        assertTrue(f.completeExceptionally(ex));
        final CompletableFuture<Void> h2 = m.thenAccept(f, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(f, f, rs[3]);

        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        checkCompletedExceptionally(f, ex);
        for (NoopConsumer r : rs) r.assertNotInvoked();
    }}

    /**
     * thenAccept result completes exceptionally if source cancelled
     */
    public void testThenAccept_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final NoopConsumer[] rs = new NoopConsumer[4];
        for (int i = 0; i < rs.length; i++) rs[i] = new NoopConsumer(m);

        final CompletableFuture<Void> h0 = m.thenAccept(f, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(f, f, rs[1]);
        assertTrue(f.cancel(mayInterruptIfRunning));
        final CompletableFuture<Void> h2 = m.thenAccept(f, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(f, f, rs[3]);

        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        checkCancelled(f);
        for (NoopConsumer r : rs) r.assertNotInvoked();
    }}

    /**
     * thenAccept result completes exceptionally if action does
     */
    public void testThenAccept_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final FailingConsumer[] rs = new FailingConsumer[4];
        for (int i = 0; i < rs.length; i++) rs[i] = new FailingConsumer(m);

        final CompletableFuture<Void> h0 = m.thenAccept(f, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(f, f, rs[1]);
        assertTrue(f.complete(v1));
        final CompletableFuture<Void> h2 = m.thenAccept(f, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(f, f, rs[3]);

        checkCompletedWithWrappedException(h0, rs[0].ex);
        checkCompletedWithWrappedException(h1, rs[1].ex);
        checkCompletedWithWrappedException(h2, rs[2].ex);
        checkCompletedWithWrappedException(h3, rs[3].ex);
        checkCompletedNormally(f, v1);
    }}

    /**
     * thenCombine result completes normally after normal completion
     * of sources
     */
    public void testThenCombine_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final SubtractFunction[] rs = new SubtractFunction[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new SubtractFunction(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Integer w1 =  fFirst ? v1 : v2;
        final Integer w2 = !fFirst ? v1 : v2;

        final CompletableFuture<Integer> h0 = m.thenCombine(f, g, rs[0]);
        final CompletableFuture<Integer> h1 = m.thenCombine(fst, fst, rs[1]);
        assertTrue(fst.complete(w1));
        final CompletableFuture<Integer> h2 = m.thenCombine(f, g, rs[2]);
        final CompletableFuture<Integer> h3 = m.thenCombine(fst, fst, rs[3]);
        checkIncomplete(h0); rs[0].assertNotInvoked();
        checkIncomplete(h2); rs[2].assertNotInvoked();
        checkCompletedNormally(h1, subtract(w1, w1));
        checkCompletedNormally(h3, subtract(w1, w1));
        rs[1].assertValue(subtract(w1, w1));
        rs[3].assertValue(subtract(w1, w1));
        assertTrue(snd.complete(w2));
        final CompletableFuture<Integer> h4 = m.thenCombine(f, g, rs[4]);

        checkCompletedNormally(h0, subtract(v1, v2));
        checkCompletedNormally(h2, subtract(v1, v2));
        checkCompletedNormally(h4, subtract(v1, v2));
        rs[0].assertValue(subtract(v1, v2));
        rs[2].assertValue(subtract(v1, v2));
        rs[4].assertValue(subtract(v1, v2));

        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
    }}

    /**
     * thenCombine result completes exceptionally after exceptional
     * completion of either source
     */
    public void testThenCombine_exceptionalCompletion() throws Throwable {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (boolean failFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final SubtractFunction r1 = new SubtractFunction(m);
        final SubtractFunction r2 = new SubtractFunction(m);
        final SubtractFunction r3 = new SubtractFunction(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Callable<Boolean> complete1 = failFirst ?
            () -> fst.completeExceptionally(ex) :
            () -> fst.complete(v1);
        final Callable<Boolean> complete2 = failFirst ?
            () -> snd.complete(v1) :
            () -> snd.completeExceptionally(ex);

        final CompletableFuture<Integer> h1 = m.thenCombine(f, g, r1);
        assertTrue(complete1.call());
        final CompletableFuture<Integer> h2 = m.thenCombine(f, g, r2);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(complete2.call());
        final CompletableFuture<Integer> h3 = m.thenCombine(f, g, r3);

        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        r1.assertNotInvoked();
        r2.assertNotInvoked();
        r3.assertNotInvoked();
        checkCompletedNormally(failFirst ? snd : fst, v1);
        checkCompletedExceptionally(failFirst ? fst : snd, ex);
    }}

    /**
     * thenCombine result completes exceptionally if either source cancelled
     */
    public void testThenCombine_sourceCancelled() throws Throwable {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (boolean fFirst : new boolean[] { true, false })
        for (boolean failFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final SubtractFunction r1 = new SubtractFunction(m);
        final SubtractFunction r2 = new SubtractFunction(m);
        final SubtractFunction r3 = new SubtractFunction(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Callable<Boolean> complete1 = failFirst ?
            () -> fst.cancel(mayInterruptIfRunning) :
            () -> fst.complete(v1);
        final Callable<Boolean> complete2 = failFirst ?
            () -> snd.complete(v1) :
            () -> snd.cancel(mayInterruptIfRunning);

        final CompletableFuture<Integer> h1 = m.thenCombine(f, g, r1);
        assertTrue(complete1.call());
        final CompletableFuture<Integer> h2 = m.thenCombine(f, g, r2);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(complete2.call());
        final CompletableFuture<Integer> h3 = m.thenCombine(f, g, r3);

        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        r1.assertNotInvoked();
        r2.assertNotInvoked();
        r3.assertNotInvoked();
        checkCompletedNormally(failFirst ? snd : fst, v1);
        checkCancelled(failFirst ? fst : snd);
    }}

    /**
     * thenCombine result completes exceptionally if action does
     */
    public void testThenCombine_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final FailingBiFunction r1 = new FailingBiFunction(m);
        final FailingBiFunction r2 = new FailingBiFunction(m);
        final FailingBiFunction r3 = new FailingBiFunction(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Integer w1 =  fFirst ? v1 : v2;
        final Integer w2 = !fFirst ? v1 : v2;

        final CompletableFuture<Integer> h1 = m.thenCombine(f, g, r1);
        assertTrue(fst.complete(w1));
        final CompletableFuture<Integer> h2 = m.thenCombine(f, g, r2);
        assertTrue(snd.complete(w2));
        final CompletableFuture<Integer> h3 = m.thenCombine(f, g, r3);

        checkCompletedWithWrappedException(h1, r1.ex);
        checkCompletedWithWrappedException(h2, r2.ex);
        checkCompletedWithWrappedException(h3, r3.ex);
        r1.assertInvoked();
        r2.assertInvoked();
        r3.assertInvoked();
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
    }}

    /**
     * thenAcceptBoth result completes normally after normal
     * completion of sources
     */
    public void testThenAcceptBoth_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final SubtractAction r1 = new SubtractAction(m);
        final SubtractAction r2 = new SubtractAction(m);
        final SubtractAction r3 = new SubtractAction(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Integer w1 =  fFirst ? v1 : v2;
        final Integer w2 = !fFirst ? v1 : v2;

        final CompletableFuture<Void> h1 = m.thenAcceptBoth(f, g, r1);
        assertTrue(fst.complete(w1));
        final CompletableFuture<Void> h2 = m.thenAcceptBoth(f, g, r2);
        checkIncomplete(h1);
        checkIncomplete(h2);
        r1.assertNotInvoked();
        r2.assertNotInvoked();
        assertTrue(snd.complete(w2));
        final CompletableFuture<Void> h3 = m.thenAcceptBoth(f, g, r3);

        checkCompletedNormally(h1, null);
        checkCompletedNormally(h2, null);
        checkCompletedNormally(h3, null);
        r1.assertValue(subtract(v1, v2));
        r2.assertValue(subtract(v1, v2));
        r3.assertValue(subtract(v1, v2));
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
    }}

    /**
     * thenAcceptBoth result completes exceptionally after exceptional
     * completion of either source
     */
    public void testThenAcceptBoth_exceptionalCompletion() throws Throwable {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (boolean failFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final SubtractAction r1 = new SubtractAction(m);
        final SubtractAction r2 = new SubtractAction(m);
        final SubtractAction r3 = new SubtractAction(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Callable<Boolean> complete1 = failFirst ?
            () -> fst.completeExceptionally(ex) :
            () -> fst.complete(v1);
        final Callable<Boolean> complete2 = failFirst ?
            () -> snd.complete(v1) :
            () -> snd.completeExceptionally(ex);

        final CompletableFuture<Void> h1 = m.thenAcceptBoth(f, g, r1);
        assertTrue(complete1.call());
        final CompletableFuture<Void> h2 = m.thenAcceptBoth(f, g, r2);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(complete2.call());
        final CompletableFuture<Void> h3 = m.thenAcceptBoth(f, g, r3);

        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        r1.assertNotInvoked();
        r2.assertNotInvoked();
        r3.assertNotInvoked();
        checkCompletedNormally(failFirst ? snd : fst, v1);
        checkCompletedExceptionally(failFirst ? fst : snd, ex);
    }}

    /**
     * thenAcceptBoth result completes exceptionally if either source cancelled
     */
    public void testThenAcceptBoth_sourceCancelled() throws Throwable {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (boolean fFirst : new boolean[] { true, false })
        for (boolean failFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final SubtractAction r1 = new SubtractAction(m);
        final SubtractAction r2 = new SubtractAction(m);
        final SubtractAction r3 = new SubtractAction(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Callable<Boolean> complete1 = failFirst ?
            () -> fst.cancel(mayInterruptIfRunning) :
            () -> fst.complete(v1);
        final Callable<Boolean> complete2 = failFirst ?
            () -> snd.complete(v1) :
            () -> snd.cancel(mayInterruptIfRunning);

        final CompletableFuture<Void> h1 = m.thenAcceptBoth(f, g, r1);
        assertTrue(complete1.call());
        final CompletableFuture<Void> h2 = m.thenAcceptBoth(f, g, r2);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(complete2.call());
        final CompletableFuture<Void> h3 = m.thenAcceptBoth(f, g, r3);

        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        r1.assertNotInvoked();
        r2.assertNotInvoked();
        r3.assertNotInvoked();
        checkCompletedNormally(failFirst ? snd : fst, v1);
        checkCancelled(failFirst ? fst : snd);
    }}

    /**
     * thenAcceptBoth result completes exceptionally if action does
     */
    public void testThenAcceptBoth_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final FailingBiConsumer r1 = new FailingBiConsumer(m);
        final FailingBiConsumer r2 = new FailingBiConsumer(m);
        final FailingBiConsumer r3 = new FailingBiConsumer(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Integer w1 =  fFirst ? v1 : v2;
        final Integer w2 = !fFirst ? v1 : v2;

        final CompletableFuture<Void> h1 = m.thenAcceptBoth(f, g, r1);
        assertTrue(fst.complete(w1));
        final CompletableFuture<Void> h2 = m.thenAcceptBoth(f, g, r2);
        assertTrue(snd.complete(w2));
        final CompletableFuture<Void> h3 = m.thenAcceptBoth(f, g, r3);

        checkCompletedWithWrappedException(h1, r1.ex);
        checkCompletedWithWrappedException(h2, r2.ex);
        checkCompletedWithWrappedException(h3, r3.ex);
        r1.assertInvoked();
        r2.assertInvoked();
        r3.assertInvoked();
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
    }}

    /**
     * runAfterBoth result completes normally after normal
     * completion of sources
     */
    public void testRunAfterBoth_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final Noop r1 = new Noop(m);
        final Noop r2 = new Noop(m);
        final Noop r3 = new Noop(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Integer w1 =  fFirst ? v1 : v2;
        final Integer w2 = !fFirst ? v1 : v2;

        final CompletableFuture<Void> h1 = m.runAfterBoth(f, g, r1);
        assertTrue(fst.complete(w1));
        final CompletableFuture<Void> h2 = m.runAfterBoth(f, g, r2);
        checkIncomplete(h1);
        checkIncomplete(h2);
        r1.assertNotInvoked();
        r2.assertNotInvoked();
        assertTrue(snd.complete(w2));
        final CompletableFuture<Void> h3 = m.runAfterBoth(f, g, r3);

        checkCompletedNormally(h1, null);
        checkCompletedNormally(h2, null);
        checkCompletedNormally(h3, null);
        r1.assertInvoked();
        r2.assertInvoked();
        r3.assertInvoked();
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
    }}

    /**
     * runAfterBoth result completes exceptionally after exceptional
     * completion of either source
     */
    public void testRunAfterBoth_exceptionalCompletion() throws Throwable {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (boolean failFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final Noop r1 = new Noop(m);
        final Noop r2 = new Noop(m);
        final Noop r3 = new Noop(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Callable<Boolean> complete1 = failFirst ?
            () -> fst.completeExceptionally(ex) :
            () -> fst.complete(v1);
        final Callable<Boolean> complete2 = failFirst ?
            () -> snd.complete(v1) :
            () -> snd.completeExceptionally(ex);

        final CompletableFuture<Void> h1 = m.runAfterBoth(f, g, r1);
        assertTrue(complete1.call());
        final CompletableFuture<Void> h2 = m.runAfterBoth(f, g, r2);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(complete2.call());
        final CompletableFuture<Void> h3 = m.runAfterBoth(f, g, r3);

        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        r1.assertNotInvoked();
        r2.assertNotInvoked();
        r3.assertNotInvoked();
        checkCompletedNormally(failFirst ? snd : fst, v1);
        checkCompletedExceptionally(failFirst ? fst : snd, ex);
    }}

    /**
     * runAfterBoth result completes exceptionally if either source cancelled
     */
    public void testRunAfterBoth_sourceCancelled() throws Throwable {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (boolean fFirst : new boolean[] { true, false })
        for (boolean failFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final Noop r1 = new Noop(m);
        final Noop r2 = new Noop(m);
        final Noop r3 = new Noop(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Callable<Boolean> complete1 = failFirst ?
            () -> fst.cancel(mayInterruptIfRunning) :
            () -> fst.complete(v1);
        final Callable<Boolean> complete2 = failFirst ?
            () -> snd.complete(v1) :
            () -> snd.cancel(mayInterruptIfRunning);

        final CompletableFuture<Void> h1 = m.runAfterBoth(f, g, r1);
        assertTrue(complete1.call());
        final CompletableFuture<Void> h2 = m.runAfterBoth(f, g, r2);
        checkIncomplete(h1);
        checkIncomplete(h2);
        assertTrue(complete2.call());
        final CompletableFuture<Void> h3 = m.runAfterBoth(f, g, r3);

        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        r1.assertNotInvoked();
        r2.assertNotInvoked();
        r3.assertNotInvoked();
        checkCompletedNormally(failFirst ? snd : fst, v1);
        checkCancelled(failFirst ? fst : snd);
    }}

    /**
     * runAfterBoth result completes exceptionally if action does
     */
    public void testRunAfterBoth_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final FailingRunnable r1 = new FailingRunnable(m);
        final FailingRunnable r2 = new FailingRunnable(m);
        final FailingRunnable r3 = new FailingRunnable(m);

        final CompletableFuture<Integer> fst =  fFirst ? f : g;
        final CompletableFuture<Integer> snd = !fFirst ? f : g;
        final Integer w1 =  fFirst ? v1 : v2;
        final Integer w2 = !fFirst ? v1 : v2;

        final CompletableFuture<Void> h1 = m.runAfterBoth(f, g, r1);
        assertTrue(fst.complete(w1));
        final CompletableFuture<Void> h2 = m.runAfterBoth(f, g, r2);
        assertTrue(snd.complete(w2));
        final CompletableFuture<Void> h3 = m.runAfterBoth(f, g, r3);

        checkCompletedWithWrappedException(h1, r1.ex);
        checkCompletedWithWrappedException(h2, r2.ex);
        checkCompletedWithWrappedException(h3, r3.ex);
        r1.assertInvoked();
        r2.assertInvoked();
        r3.assertInvoked();
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
    }}

    /**
     * applyToEither result completes normally after normal completion
     * of either source
     */
    public void testApplyToEither_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final IncFunction[] rs = new IncFunction[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new IncFunction(m);

        final CompletableFuture<Integer> h0 = m.applyToEither(f, g, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        f.complete(v1);
        checkCompletedNormally(h0, inc(v1));
        checkCompletedNormally(h1, inc(v1));
        final CompletableFuture<Integer> h2 = m.applyToEither(f, g, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(g, f, rs[3]);
        checkCompletedNormally(h2, inc(v1));
        checkCompletedNormally(h3, inc(v1));
        g.complete(v2);

        // unspecified behavior - both source completions available
        final CompletableFuture<Integer> h4 = m.applyToEither(f, g, rs[4]);
        final CompletableFuture<Integer> h5 = m.applyToEither(g, f, rs[5]);
        rs[4].assertValue(h4.join());
        rs[5].assertValue(h5.join());
        assertTrue(Objects.equals(inc(v1), h4.join()) ||
                   Objects.equals(inc(v2), h4.join()));
        assertTrue(Objects.equals(inc(v1), h5.join()) ||
                   Objects.equals(inc(v2), h5.join()));

        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
        checkCompletedNormally(h0, inc(v1));
        checkCompletedNormally(h1, inc(v1));
        checkCompletedNormally(h2, inc(v1));
        checkCompletedNormally(h3, inc(v1));
        for (int i = 0; i < 4; i++) rs[i].assertValue(inc(v1));
    }}

    /**
     * applyToEither result completes exceptionally after exceptional
     * completion of either source
     */
    public void testApplyToEither_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final IncFunction[] rs = new IncFunction[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new IncFunction(m);

        final CompletableFuture<Integer> h0 = m.applyToEither(f, g, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        f.completeExceptionally(ex);
        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        final CompletableFuture<Integer> h2 = m.applyToEither(f, g, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(g, f, rs[3]);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        g.complete(v1);

        // unspecified behavior - both source completions available
        final CompletableFuture<Integer> h4 = m.applyToEither(f, g, rs[4]);
        final CompletableFuture<Integer> h5 = m.applyToEither(g, f, rs[5]);
        try {
            assertEquals(inc(v1), h4.join());
            rs[4].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h4, ex);
            rs[4].assertNotInvoked();
        }
        try {
            assertEquals(inc(v1), h5.join());
            rs[5].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h5, ex);
            rs[5].assertNotInvoked();
        }

        checkCompletedExceptionally(f, ex);
        checkCompletedNormally(g, v1);
        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        checkCompletedWithWrappedException(h4, ex);
        for (int i = 0; i < 4; i++) rs[i].assertNotInvoked();
    }}

    public void testApplyToEither_exceptionalCompletion2() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final IncFunction[] rs = new IncFunction[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new IncFunction(m);

        final CompletableFuture<Integer> h0 = m.applyToEither(f, g, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(g, f, rs[1]);
        assertTrue(fFirst ? f.complete(v1) : g.completeExceptionally(ex));
        assertTrue(!fFirst ? f.complete(v1) : g.completeExceptionally(ex));
        final CompletableFuture<Integer> h2 = m.applyToEither(f, g, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(g, f, rs[3]);

        // unspecified behavior - both source completions available
        try {
            assertEquals(inc(v1), h0.join());
            rs[0].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h0, ex);
            rs[0].assertNotInvoked();
        }
        try {
            assertEquals(inc(v1), h1.join());
            rs[1].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h1, ex);
            rs[1].assertNotInvoked();
        }
        try {
            assertEquals(inc(v1), h2.join());
            rs[2].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h2, ex);
            rs[2].assertNotInvoked();
        }
        try {
            assertEquals(inc(v1), h3.join());
            rs[3].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h3, ex);
            rs[3].assertNotInvoked();
        }

        checkCompletedNormally(f, v1);
        checkCompletedExceptionally(g, ex);
    }}

    /**
     * applyToEither result completes exceptionally if either source cancelled
     */
    public void testApplyToEither_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final IncFunction[] rs = new IncFunction[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new IncFunction(m);

        final CompletableFuture<Integer> h0 = m.applyToEither(f, g, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        f.cancel(mayInterruptIfRunning);
        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        final CompletableFuture<Integer> h2 = m.applyToEither(f, g, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(g, f, rs[3]);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        g.complete(v1);

        // unspecified behavior - both source completions available
        final CompletableFuture<Integer> h4 = m.applyToEither(f, g, rs[4]);
        final CompletableFuture<Integer> h5 = m.applyToEither(g, f, rs[5]);
        try {
            assertEquals(inc(v1), h4.join());
            rs[4].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h4);
            rs[4].assertNotInvoked();
        }
        try {
            assertEquals(inc(v1), h5.join());
            rs[5].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h5);
            rs[5].assertNotInvoked();
        }

        checkCancelled(f);
        checkCompletedNormally(g, v1);
        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        for (int i = 0; i < 4; i++) rs[i].assertNotInvoked();
    }}

    public void testApplyToEither_sourceCancelled2() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final IncFunction[] rs = new IncFunction[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new IncFunction(m);

        final CompletableFuture<Integer> h0 = m.applyToEither(f, g, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(g, f, rs[1]);
        assertTrue(fFirst ? f.complete(v1) : g.cancel(mayInterruptIfRunning));
        assertTrue(!fFirst ? f.complete(v1) : g.cancel(mayInterruptIfRunning));
        final CompletableFuture<Integer> h2 = m.applyToEither(f, g, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(g, f, rs[3]);

        // unspecified behavior - both source completions available
        try {
            assertEquals(inc(v1), h0.join());
            rs[0].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h0);
            rs[0].assertNotInvoked();
        }
        try {
            assertEquals(inc(v1), h1.join());
            rs[1].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h1);
            rs[1].assertNotInvoked();
        }
        try {
            assertEquals(inc(v1), h2.join());
            rs[2].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h2);
            rs[2].assertNotInvoked();
        }
        try {
            assertEquals(inc(v1), h3.join());
            rs[3].assertValue(inc(v1));
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h3);
            rs[3].assertNotInvoked();
        }

        checkCompletedNormally(f, v1);
        checkCancelled(g);
    }}

    /**
     * applyToEither result completes exceptionally if action does
     */
    public void testApplyToEither_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final FailingFunction[] rs = new FailingFunction[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new FailingFunction(m);

        final CompletableFuture<Integer> h0 = m.applyToEither(f, g, rs[0]);
        final CompletableFuture<Integer> h1 = m.applyToEither(g, f, rs[1]);
        f.complete(v1);
        final CompletableFuture<Integer> h2 = m.applyToEither(f, g, rs[2]);
        final CompletableFuture<Integer> h3 = m.applyToEither(g, f, rs[3]);
        checkCompletedWithWrappedException(h0, rs[0].ex);
        checkCompletedWithWrappedException(h1, rs[1].ex);
        checkCompletedWithWrappedException(h2, rs[2].ex);
        checkCompletedWithWrappedException(h3, rs[3].ex);
        for (int i = 0; i < 4; i++) rs[i].assertValue(v1);

        g.complete(v2);

        // unspecified behavior - both source completions available
        final CompletableFuture<Integer> h4 = m.applyToEither(f, g, rs[4]);
        final CompletableFuture<Integer> h5 = m.applyToEither(g, f, rs[5]);

        checkCompletedWithWrappedException(h4, rs[4].ex);
        assertTrue(Objects.equals(v1, rs[4].value) ||
                   Objects.equals(v2, rs[4].value));
        checkCompletedWithWrappedException(h5, rs[5].ex);
        assertTrue(Objects.equals(v1, rs[5].value) ||
                   Objects.equals(v2, rs[5].value));

        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
    }}

    /**
     * acceptEither result completes normally after normal completion
     * of either source
     */
    public void testAcceptEither_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final NoopConsumer[] rs = new NoopConsumer[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new NoopConsumer(m);

        final CompletableFuture<Void> h0 = m.acceptEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        f.complete(v1);
        checkCompletedNormally(h0, null);
        checkCompletedNormally(h1, null);
        rs[0].assertValue(v1);
        rs[1].assertValue(v1);
        final CompletableFuture<Void> h2 = m.acceptEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(g, f, rs[3]);
        checkCompletedNormally(h2, null);
        checkCompletedNormally(h3, null);
        rs[2].assertValue(v1);
        rs[3].assertValue(v1);
        g.complete(v2);

        // unspecified behavior - both source completions available
        final CompletableFuture<Void> h4 = m.acceptEither(f, g, rs[4]);
        final CompletableFuture<Void> h5 = m.acceptEither(g, f, rs[5]);
        checkCompletedNormally(h4, null);
        checkCompletedNormally(h5, null);
        assertTrue(Objects.equals(v1, rs[4].value) ||
                   Objects.equals(v2, rs[4].value));
        assertTrue(Objects.equals(v1, rs[5].value) ||
                   Objects.equals(v2, rs[5].value));

        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
        checkCompletedNormally(h0, null);
        checkCompletedNormally(h1, null);
        checkCompletedNormally(h2, null);
        checkCompletedNormally(h3, null);
        for (int i = 0; i < 4; i++) rs[i].assertValue(v1);
    }}

    /**
     * acceptEither result completes exceptionally after exceptional
     * completion of either source
     */
    public void testAcceptEither_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final NoopConsumer[] rs = new NoopConsumer[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new NoopConsumer(m);

        final CompletableFuture<Void> h0 = m.acceptEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        f.completeExceptionally(ex);
        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        final CompletableFuture<Void> h2 = m.acceptEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(g, f, rs[3]);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);

        g.complete(v1);

        // unspecified behavior - both source completions available
        final CompletableFuture<Void> h4 = m.acceptEither(f, g, rs[4]);
        final CompletableFuture<Void> h5 = m.acceptEither(g, f, rs[5]);
        try {
            assertNull(h4.join());
            rs[4].assertValue(v1);
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h4, ex);
            rs[4].assertNotInvoked();
        }
        try {
            assertNull(h5.join());
            rs[5].assertValue(v1);
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h5, ex);
            rs[5].assertNotInvoked();
        }

        checkCompletedExceptionally(f, ex);
        checkCompletedNormally(g, v1);
        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        checkCompletedWithWrappedException(h4, ex);
        for (int i = 0; i < 4; i++) rs[i].assertNotInvoked();
    }}

    public void testAcceptEither_exceptionalCompletion2() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final NoopConsumer[] rs = new NoopConsumer[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new NoopConsumer(m);

        final CompletableFuture<Void> h0 = m.acceptEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(g, f, rs[1]);
        assertTrue(fFirst ? f.complete(v1) : g.completeExceptionally(ex));
        assertTrue(!fFirst ? f.complete(v1) : g.completeExceptionally(ex));
        final CompletableFuture<Void> h2 = m.acceptEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(g, f, rs[3]);

        // unspecified behavior - both source completions available
        try {
            assertEquals(null, h0.join());
            rs[0].assertValue(v1);
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h0, ex);
            rs[0].assertNotInvoked();
        }
        try {
            assertEquals(null, h1.join());
            rs[1].assertValue(v1);
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h1, ex);
            rs[1].assertNotInvoked();
        }
        try {
            assertEquals(null, h2.join());
            rs[2].assertValue(v1);
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h2, ex);
            rs[2].assertNotInvoked();
        }
        try {
            assertEquals(null, h3.join());
            rs[3].assertValue(v1);
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h3, ex);
            rs[3].assertNotInvoked();
        }

        checkCompletedNormally(f, v1);
        checkCompletedExceptionally(g, ex);
    }}

    /**
     * acceptEither result completes exceptionally if either source cancelled
     */
    public void testAcceptEither_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final NoopConsumer[] rs = new NoopConsumer[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new NoopConsumer(m);

        final CompletableFuture<Void> h0 = m.acceptEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        f.cancel(mayInterruptIfRunning);
        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        final CompletableFuture<Void> h2 = m.acceptEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(g, f, rs[3]);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);

        g.complete(v1);

        // unspecified behavior - both source completions available
        final CompletableFuture<Void> h4 = m.acceptEither(f, g, rs[4]);
        final CompletableFuture<Void> h5 = m.acceptEither(g, f, rs[5]);
        try {
            assertNull(h4.join());
            rs[4].assertValue(v1);
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h4);
            rs[4].assertNotInvoked();
        }
        try {
            assertNull(h5.join());
            rs[5].assertValue(v1);
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h5);
            rs[5].assertNotInvoked();
        }

        checkCancelled(f);
        checkCompletedNormally(g, v1);
        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        for (int i = 0; i < 4; i++) rs[i].assertNotInvoked();
    }}

    /**
     * acceptEither result completes exceptionally if action does
     */
    public void testAcceptEither_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final FailingConsumer[] rs = new FailingConsumer[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new FailingConsumer(m);

        final CompletableFuture<Void> h0 = m.acceptEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.acceptEither(g, f, rs[1]);
        f.complete(v1);
        final CompletableFuture<Void> h2 = m.acceptEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.acceptEither(g, f, rs[3]);
        checkCompletedWithWrappedException(h0, rs[0].ex);
        checkCompletedWithWrappedException(h1, rs[1].ex);
        checkCompletedWithWrappedException(h2, rs[2].ex);
        checkCompletedWithWrappedException(h3, rs[3].ex);
        for (int i = 0; i < 4; i++) rs[i].assertValue(v1);

        g.complete(v2);

        // unspecified behavior - both source completions available
        final CompletableFuture<Void> h4 = m.acceptEither(f, g, rs[4]);
        final CompletableFuture<Void> h5 = m.acceptEither(g, f, rs[5]);

        checkCompletedWithWrappedException(h4, rs[4].ex);
        assertTrue(Objects.equals(v1, rs[4].value) ||
                   Objects.equals(v2, rs[4].value));
        checkCompletedWithWrappedException(h5, rs[5].ex);
        assertTrue(Objects.equals(v1, rs[5].value) ||
                   Objects.equals(v2, rs[5].value));

        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
    }}

    /**
     * runAfterEither result completes normally after normal completion
     * of either source
     */
    public void testRunAfterEither_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
        for (boolean pushNop : new boolean[] { true, false })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final Noop[] rs = new Noop[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new Noop(m);

        final CompletableFuture<Void> h0 = m.runAfterEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        if (pushNop) {          // ad hoc test of intra-completion interference
            m.thenRun(f, () -> {});
            m.thenRun(g, () -> {});
        }
        f.complete(v1);
        checkCompletedNormally(h0, null);
        checkCompletedNormally(h1, null);
        rs[0].assertInvoked();
        rs[1].assertInvoked();
        final CompletableFuture<Void> h2 = m.runAfterEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.runAfterEither(g, f, rs[3]);
        checkCompletedNormally(h2, null);
        checkCompletedNormally(h3, null);
        rs[2].assertInvoked();
        rs[3].assertInvoked();

        g.complete(v2);

        final CompletableFuture<Void> h4 = m.runAfterEither(f, g, rs[4]);
        final CompletableFuture<Void> h5 = m.runAfterEither(g, f, rs[5]);

        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
        checkCompletedNormally(h0, null);
        checkCompletedNormally(h1, null);
        checkCompletedNormally(h2, null);
        checkCompletedNormally(h3, null);
        checkCompletedNormally(h4, null);
        checkCompletedNormally(h5, null);
        for (int i = 0; i < 6; i++) rs[i].assertInvoked();
    }}

    /**
     * runAfterEither result completes exceptionally after exceptional
     * completion of either source
     */
    public void testRunAfterEither_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final Noop[] rs = new Noop[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new Noop(m);

        final CompletableFuture<Void> h0 = m.runAfterEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        assertTrue(f.completeExceptionally(ex));
        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        final CompletableFuture<Void> h2 = m.runAfterEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.runAfterEither(g, f, rs[3]);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);

        assertTrue(g.complete(v1));

        // unspecified behavior - both source completions available
        final CompletableFuture<Void> h4 = m.runAfterEither(f, g, rs[4]);
        final CompletableFuture<Void> h5 = m.runAfterEither(g, f, rs[5]);
        try {
            assertNull(h4.join());
            rs[4].assertInvoked();
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h4, ex);
            rs[4].assertNotInvoked();
        }
        try {
            assertNull(h5.join());
            rs[5].assertInvoked();
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h5, ex);
            rs[5].assertNotInvoked();
        }

        checkCompletedExceptionally(f, ex);
        checkCompletedNormally(g, v1);
        checkCompletedWithWrappedException(h0, ex);
        checkCompletedWithWrappedException(h1, ex);
        checkCompletedWithWrappedException(h2, ex);
        checkCompletedWithWrappedException(h3, ex);
        checkCompletedWithWrappedException(h4, ex);
        for (int i = 0; i < 4; i++) rs[i].assertNotInvoked();
    }}

    public void testRunAfterEither_exceptionalCompletion2() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean fFirst : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CFException ex = new CFException();
        final Noop[] rs = new Noop[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new Noop(m);

        final CompletableFuture<Void> h0 = m.runAfterEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterEither(g, f, rs[1]);
        assertTrue( fFirst ? f.complete(v1) : g.completeExceptionally(ex));
        assertTrue(!fFirst ? f.complete(v1) : g.completeExceptionally(ex));
        final CompletableFuture<Void> h2 = m.runAfterEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.runAfterEither(g, f, rs[3]);

        // unspecified behavior - both source completions available
        try {
            assertEquals(null, h0.join());
            rs[0].assertInvoked();
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h0, ex);
            rs[0].assertNotInvoked();
        }
        try {
            assertEquals(null, h1.join());
            rs[1].assertInvoked();
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h1, ex);
            rs[1].assertNotInvoked();
        }
        try {
            assertEquals(null, h2.join());
            rs[2].assertInvoked();
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h2, ex);
            rs[2].assertNotInvoked();
        }
        try {
            assertEquals(null, h3.join());
            rs[3].assertInvoked();
        } catch (CompletionException ok) {
            checkCompletedWithWrappedException(h3, ex);
            rs[3].assertNotInvoked();
        }

        checkCompletedNormally(f, v1);
        checkCompletedExceptionally(g, ex);
    }}

    /**
     * runAfterEither result completes exceptionally if either source cancelled
     */
    public void testRunAfterEither_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final Noop[] rs = new Noop[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new Noop(m);

        final CompletableFuture<Void> h0 = m.runAfterEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterEither(g, f, rs[1]);
        checkIncomplete(h0);
        checkIncomplete(h1);
        rs[0].assertNotInvoked();
        rs[1].assertNotInvoked();
        f.cancel(mayInterruptIfRunning);
        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        final CompletableFuture<Void> h2 = m.runAfterEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.runAfterEither(g, f, rs[3]);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);

        assertTrue(g.complete(v1));

        // unspecified behavior - both source completions available
        final CompletableFuture<Void> h4 = m.runAfterEither(f, g, rs[4]);
        final CompletableFuture<Void> h5 = m.runAfterEither(g, f, rs[5]);
        try {
            assertNull(h4.join());
            rs[4].assertInvoked();
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h4);
            rs[4].assertNotInvoked();
        }
        try {
            assertNull(h5.join());
            rs[5].assertInvoked();
        } catch (CompletionException ok) {
            checkCompletedWithWrappedCancellationException(h5);
            rs[5].assertNotInvoked();
        }

        checkCancelled(f);
        checkCompletedNormally(g, v1);
        checkCompletedWithWrappedCancellationException(h0);
        checkCompletedWithWrappedCancellationException(h1);
        checkCompletedWithWrappedCancellationException(h2);
        checkCompletedWithWrappedCancellationException(h3);
        for (int i = 0; i < 4; i++) rs[i].assertNotInvoked();
    }}

    /**
     * runAfterEither result completes exceptionally if action does
     */
    public void testRunAfterEither_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (Integer v1 : new Integer[] { 1, null })
        for (Integer v2 : new Integer[] { 2, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final FailingRunnable[] rs = new FailingRunnable[6];
        for (int i = 0; i < rs.length; i++) rs[i] = new FailingRunnable(m);

        final CompletableFuture<Void> h0 = m.runAfterEither(f, g, rs[0]);
        final CompletableFuture<Void> h1 = m.runAfterEither(g, f, rs[1]);
        assertTrue(f.complete(v1));
        final CompletableFuture<Void> h2 = m.runAfterEither(f, g, rs[2]);
        final CompletableFuture<Void> h3 = m.runAfterEither(g, f, rs[3]);
        checkCompletedWithWrappedException(h0, rs[0].ex);
        checkCompletedWithWrappedException(h1, rs[1].ex);
        checkCompletedWithWrappedException(h2, rs[2].ex);
        checkCompletedWithWrappedException(h3, rs[3].ex);
        for (int i = 0; i < 4; i++) rs[i].assertInvoked();
        assertTrue(g.complete(v2));
        final CompletableFuture<Void> h4 = m.runAfterEither(f, g, rs[4]);
        final CompletableFuture<Void> h5 = m.runAfterEither(g, f, rs[5]);
        checkCompletedWithWrappedException(h4, rs[4].ex);
        checkCompletedWithWrappedException(h5, rs[5].ex);

        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v2);
        for (int i = 0; i < 6; i++) rs[i].assertInvoked();
    }}

    /**
     * thenCompose result completes normally after normal completion of source
     */
    public void testThenCompose_normalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFutureInc r = new CompletableFutureInc(m);
        if (!createIncomplete) assertTrue(f.complete(v1));
        final CompletableFuture<Integer> g = m.thenCompose(f, r);
        if (createIncomplete) assertTrue(f.complete(v1));

        checkCompletedNormally(g, inc(v1));
        checkCompletedNormally(f, v1);
        r.assertValue(v1);
    }}

    /**
     * thenCompose result completes exceptionally after exceptional
     * completion of source
     */
    public void testThenCompose_exceptionalCompletion() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
    {
        final CFException ex = new CFException();
        final CompletableFutureInc r = new CompletableFutureInc(m);
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) f.completeExceptionally(ex);
        final CompletableFuture<Integer> g = m.thenCompose(f, r);
        if (createIncomplete) f.completeExceptionally(ex);

        checkCompletedWithWrappedException(g, ex);
        checkCompletedExceptionally(f, ex);
        r.assertNotInvoked();
    }}

    /**
     * thenCompose result completes exceptionally if action does
     */
    public void testThenCompose_actionFailed() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final FailingCompletableFutureFunction r
            = new FailingCompletableFutureFunction(m);
        if (!createIncomplete) assertTrue(f.complete(v1));
        final CompletableFuture<Integer> g = m.thenCompose(f, r);
        if (createIncomplete) assertTrue(f.complete(v1));

        checkCompletedWithWrappedException(g, r.ex);
        checkCompletedNormally(f, v1);
    }}

    /**
     * thenCompose result completes exceptionally if source cancelled
     */
    public void testThenCompose_sourceCancelled() {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean createIncomplete : new boolean[] { true, false })
        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
    {
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFutureInc r = new CompletableFutureInc(m);
        if (!createIncomplete) assertTrue(f.cancel(mayInterruptIfRunning));
        final CompletableFuture<Integer> g = m.thenCompose(f, r);
        if (createIncomplete) {
            checkIncomplete(g);
            assertTrue(f.cancel(mayInterruptIfRunning));
        }

        checkCompletedWithWrappedCancellationException(g);
        checkCancelled(f);
    }}

    /**
     * thenCompose result completes exceptionally if the result of the action does
     */
    public void testThenCompose_actionReturnsFailingFuture() {
        for (ExecutionMode m : ExecutionMode.values())
        for (int order = 0; order < 6; order++)
        for (Integer v1 : new Integer[] { 1, null })
    {
        final CFException ex = new CFException();
        final CompletableFuture<Integer> f = new CompletableFuture<>();
        final CompletableFuture<Integer> g = new CompletableFuture<>();
        final CompletableFuture<Integer> h;
        // Test all permutations of orders
        switch (order) {
        case 0:
            assertTrue(f.complete(v1));
            assertTrue(g.completeExceptionally(ex));
            h = m.thenCompose(f, (x -> g));
            break;
        case 1:
            assertTrue(f.complete(v1));
            h = m.thenCompose(f, (x -> g));
            assertTrue(g.completeExceptionally(ex));
            break;
        case 2:
            assertTrue(g.completeExceptionally(ex));
            assertTrue(f.complete(v1));
            h = m.thenCompose(f, (x -> g));
            break;
        case 3:
            assertTrue(g.completeExceptionally(ex));
            h = m.thenCompose(f, (x -> g));
            assertTrue(f.complete(v1));
            break;
        case 4:
            h = m.thenCompose(f, (x -> g));
            assertTrue(f.complete(v1));
            assertTrue(g.completeExceptionally(ex));
            break;
        case 5:
            h = m.thenCompose(f, (x -> g));
            assertTrue(f.complete(v1));
            assertTrue(g.completeExceptionally(ex));
            break;
        default: throw new AssertionError();
        }

        checkCompletedExceptionally(g, ex);
        checkCompletedWithWrappedException(h, ex);
        checkCompletedNormally(f, v1);
    }}

    // other static methods

    /**
     * allOf(no component futures) returns a future completed normally
     * with the value null
     */
    public void testAllOf_empty() throws Exception {
        CompletableFuture<Void> f = CompletableFuture.allOf();
        checkCompletedNormally(f, null);
    }

    /**
     * allOf returns a future completed normally with the value null
     * when all components complete normally
     */
    public void testAllOf_normal() throws Exception {
        for (int k = 1; k < 10; k++) {
            CompletableFuture<Integer>[] fs
                = (CompletableFuture<Integer>[]) new CompletableFuture[k];
            for (int i = 0; i < k; i++)
                fs[i] = new CompletableFuture<>();
            CompletableFuture<Void> f = CompletableFuture.allOf(fs);
            for (int i = 0; i < k; i++) {
                checkIncomplete(f);
                checkIncomplete(CompletableFuture.allOf(fs));
                fs[i].complete(one);
            }
            checkCompletedNormally(f, null);
            checkCompletedNormally(CompletableFuture.allOf(fs), null);
        }
    }

    public void testAllOf_normal_backwards() throws Exception {
        for (int k = 1; k < 10; k++) {
            CompletableFuture<Integer>[] fs
                = (CompletableFuture<Integer>[]) new CompletableFuture[k];
            for (int i = 0; i < k; i++)
                fs[i] = new CompletableFuture<>();
            CompletableFuture<Void> f = CompletableFuture.allOf(fs);
            for (int i = k - 1; i >= 0; i--) {
                checkIncomplete(f);
                checkIncomplete(CompletableFuture.allOf(fs));
                fs[i].complete(one);
            }
            checkCompletedNormally(f, null);
            checkCompletedNormally(CompletableFuture.allOf(fs), null);
        }
    }

    public void testAllOf_exceptional() throws Exception {
        for (int k = 1; k < 10; k++) {
            CompletableFuture<Integer>[] fs
                = (CompletableFuture<Integer>[]) new CompletableFuture[k];
            CFException ex = new CFException();
            for (int i = 0; i < k; i++)
                fs[i] = new CompletableFuture<>();
            CompletableFuture<Void> f = CompletableFuture.allOf(fs);
            for (int i = 0; i < k; i++) {
                checkIncomplete(f);
                checkIncomplete(CompletableFuture.allOf(fs));
                if (i != k / 2) {
                    fs[i].complete(i);
                    checkCompletedNormally(fs[i], i);
                } else {
                    fs[i].completeExceptionally(ex);
                    checkCompletedExceptionally(fs[i], ex);
                }
            }
            checkCompletedWithWrappedException(f, ex);
            checkCompletedWithWrappedException(CompletableFuture.allOf(fs), ex);
        }
    }

    /**
     * anyOf(no component futures) returns an incomplete future
     */
    public void testAnyOf_empty() throws Exception {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Object> f = CompletableFuture.anyOf();
        checkIncomplete(f);

        f.complete(v1);
        checkCompletedNormally(f, v1);
    }}

    /**
     * anyOf returns a future completed normally with a value when
     * a component future does
     */
    public void testAnyOf_normal() throws Exception {
        for (int k = 0; k < 10; k++) {
            CompletableFuture[] fs = new CompletableFuture[k];
            for (int i = 0; i < k; i++)
                fs[i] = new CompletableFuture<>();
            CompletableFuture<Object> f = CompletableFuture.anyOf(fs);
            checkIncomplete(f);
            for (int i = 0; i < k; i++) {
                fs[i].complete(i);
                checkCompletedNormally(f, 0);
                int x = (int) CompletableFuture.anyOf(fs).join();
                assertTrue(0 <= x && x <= i);
            }
        }
    }
    public void testAnyOf_normal_backwards() throws Exception {
        for (int k = 0; k < 10; k++) {
            CompletableFuture[] fs = new CompletableFuture[k];
            for (int i = 0; i < k; i++)
                fs[i] = new CompletableFuture<>();
            CompletableFuture<Object> f = CompletableFuture.anyOf(fs);
            checkIncomplete(f);
            for (int i = k - 1; i >= 0; i--) {
                fs[i].complete(i);
                checkCompletedNormally(f, k - 1);
                int x = (int) CompletableFuture.anyOf(fs).join();
                assertTrue(i <= x && x <= k - 1);
            }
        }
    }

    /**
     * anyOf result completes exceptionally when any component does.
     */
    public void testAnyOf_exceptional() throws Exception {
        for (int k = 0; k < 10; k++) {
            CompletableFuture[] fs = new CompletableFuture[k];
            CFException[] exs = new CFException[k];
            for (int i = 0; i < k; i++) {
                fs[i] = new CompletableFuture<>();
                exs[i] = new CFException();
            }
            CompletableFuture<Object> f = CompletableFuture.anyOf(fs);
            checkIncomplete(f);
            for (int i = 0; i < k; i++) {
                fs[i].completeExceptionally(exs[i]);
                checkCompletedWithWrappedException(f, exs[0]);
                checkCompletedWithWrappedCFException(CompletableFuture.anyOf(fs));
            }
        }
    }

    public void testAnyOf_exceptional_backwards() throws Exception {
        for (int k = 0; k < 10; k++) {
            CompletableFuture[] fs = new CompletableFuture[k];
            CFException[] exs = new CFException[k];
            for (int i = 0; i < k; i++) {
                fs[i] = new CompletableFuture<>();
                exs[i] = new CFException();
            }
            CompletableFuture<Object> f = CompletableFuture.anyOf(fs);
            checkIncomplete(f);
            for (int i = k - 1; i >= 0; i--) {
                fs[i].completeExceptionally(exs[i]);
                checkCompletedWithWrappedException(f, exs[k - 1]);
                checkCompletedWithWrappedCFException(CompletableFuture.anyOf(fs));
            }
        }
    }

    /**
     * Completion methods throw NullPointerException with null arguments
     */
    public void testNPE() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletableFuture<Integer> g = new CompletableFuture<>();
        CompletableFuture<Integer> nullFuture = (CompletableFuture<Integer>)null;
        ThreadExecutor exec = new ThreadExecutor();

        Runnable[] throwingActions = {
            () -> CompletableFuture.supplyAsync(null),
            () -> CompletableFuture.supplyAsync(null, exec),
            () -> CompletableFuture.supplyAsync(new IntegerSupplier(ExecutionMode.SYNC, 42), null),

            () -> CompletableFuture.runAsync(null),
            () -> CompletableFuture.runAsync(null, exec),
            () -> CompletableFuture.runAsync(() -> {}, null),

            () -> f.completeExceptionally(null),

            () -> f.thenApply(null),
            () -> f.thenApplyAsync(null),
            () -> f.thenApplyAsync(x -> x, null),
            () -> f.thenApplyAsync(null, exec),

            () -> f.thenAccept(null),
            () -> f.thenAcceptAsync(null),
            () -> f.thenAcceptAsync(x -> {} , null),
            () -> f.thenAcceptAsync(null, exec),

            () -> f.thenRun(null),
            () -> f.thenRunAsync(null),
            () -> f.thenRunAsync(() -> {} , null),
            () -> f.thenRunAsync(null, exec),

            () -> f.thenCombine(g, null),
            () -> f.thenCombineAsync(g, null),
            () -> f.thenCombineAsync(g, null, exec),
            () -> f.thenCombine(nullFuture, (x, y) -> x),
            () -> f.thenCombineAsync(nullFuture, (x, y) -> x),
            () -> f.thenCombineAsync(nullFuture, (x, y) -> x, exec),
            () -> f.thenCombineAsync(g, (x, y) -> x, null),

            () -> f.thenAcceptBoth(g, null),
            () -> f.thenAcceptBothAsync(g, null),
            () -> f.thenAcceptBothAsync(g, null, exec),
            () -> f.thenAcceptBoth(nullFuture, (x, y) -> {}),
            () -> f.thenAcceptBothAsync(nullFuture, (x, y) -> {}),
            () -> f.thenAcceptBothAsync(nullFuture, (x, y) -> {}, exec),
            () -> f.thenAcceptBothAsync(g, (x, y) -> {}, null),

            () -> f.runAfterBoth(g, null),
            () -> f.runAfterBothAsync(g, null),
            () -> f.runAfterBothAsync(g, null, exec),
            () -> f.runAfterBoth(nullFuture, () -> {}),
            () -> f.runAfterBothAsync(nullFuture, () -> {}),
            () -> f.runAfterBothAsync(nullFuture, () -> {}, exec),
            () -> f.runAfterBothAsync(g, () -> {}, null),

            () -> f.applyToEither(g, null),
            () -> f.applyToEitherAsync(g, null),
            () -> f.applyToEitherAsync(g, null, exec),
            () -> f.applyToEither(nullFuture, x -> x),
            () -> f.applyToEitherAsync(nullFuture, x -> x),
            () -> f.applyToEitherAsync(nullFuture, x -> x, exec),
            () -> f.applyToEitherAsync(g, x -> x, null),

            () -> f.acceptEither(g, null),
            () -> f.acceptEitherAsync(g, null),
            () -> f.acceptEitherAsync(g, null, exec),
            () -> f.acceptEither(nullFuture, x -> {}),
            () -> f.acceptEitherAsync(nullFuture, x -> {}),
            () -> f.acceptEitherAsync(nullFuture, x -> {}, exec),
            () -> f.acceptEitherAsync(g, x -> {}, null),

            () -> f.runAfterEither(g, null),
            () -> f.runAfterEitherAsync(g, null),
            () -> f.runAfterEitherAsync(g, null, exec),
            () -> f.runAfterEither(nullFuture, () -> {}),
            () -> f.runAfterEitherAsync(nullFuture, () -> {}),
            () -> f.runAfterEitherAsync(nullFuture, () -> {}, exec),
            () -> f.runAfterEitherAsync(g, () -> {}, null),

            () -> f.thenCompose(null),
            () -> f.thenComposeAsync(null),
            () -> f.thenComposeAsync(new CompletableFutureInc(ExecutionMode.EXECUTOR), null),
            () -> f.thenComposeAsync(null, exec),

            () -> f.exceptionally(null),

            () -> f.handle(null),

            () -> CompletableFuture.allOf((CompletableFuture<?>)null),
            () -> CompletableFuture.allOf((CompletableFuture<?>[])null),
            () -> CompletableFuture.allOf(f, null),
            () -> CompletableFuture.allOf(null, f),

            () -> CompletableFuture.anyOf((CompletableFuture<?>)null),
            () -> CompletableFuture.anyOf((CompletableFuture<?>[])null),
            () -> CompletableFuture.anyOf(f, null),
            () -> CompletableFuture.anyOf(null, f),

            () -> f.obtrudeException(null),

            () -> CompletableFuture.delayedExecutor(1L, SECONDS, null),
            () -> CompletableFuture.delayedExecutor(1L, null, exec),
            () -> CompletableFuture.delayedExecutor(1L, null),

            () -> f.orTimeout(1L, null),
            () -> f.completeOnTimeout(42, 1L, null),

            () -> CompletableFuture.failedFuture(null),
            () -> CompletableFuture.failedStage(null),
        };

        assertThrows(NullPointerException.class, throwingActions);
        assertEquals(0, exec.count.get());
    }

    /**
     * Test submissions to an executor that rejects all tasks.
     */
    public void testRejectingExecutor() {
        for (Integer v : new Integer[] { 1, null })
    {
        final CountingRejectingExecutor e = new CountingRejectingExecutor();

        final CompletableFuture<Integer> complete = CompletableFuture.completedFuture(v);
        final CompletableFuture<Integer> incomplete = new CompletableFuture<>();

        List<CompletableFuture<?>> futures = new ArrayList<>();

        List<CompletableFuture<Integer>> srcs = new ArrayList<>();
        srcs.add(complete);
        srcs.add(incomplete);

        for (CompletableFuture<Integer> src : srcs) {
            List<CompletableFuture<?>> fs = new ArrayList<>();
            fs.add(src.thenRunAsync(() -> {}, e));
            fs.add(src.thenAcceptAsync(z -> {}, e));
            fs.add(src.thenApplyAsync(z -> z, e));

            fs.add(src.thenCombineAsync(src, (x, y) -> x, e));
            fs.add(src.thenAcceptBothAsync(src, (x, y) -> {}, e));
            fs.add(src.runAfterBothAsync(src, () -> {}, e));

            fs.add(src.applyToEitherAsync(src, z -> z, e));
            fs.add(src.acceptEitherAsync(src, z -> {}, e));
            fs.add(src.runAfterEitherAsync(src, () -> {}, e));

            fs.add(src.thenComposeAsync(z -> null, e));
            fs.add(src.whenCompleteAsync((z, t) -> {}, e));
            fs.add(src.handleAsync((z, t) -> null, e));

            for (CompletableFuture<?> future : fs) {
                if (src.isDone())
                    checkCompletedWithWrappedException(future, e.ex);
                else
                    checkIncomplete(future);
            }
            futures.addAll(fs);
        }

        {
            List<CompletableFuture<?>> fs = new ArrayList<>();

            fs.add(complete.thenCombineAsync(incomplete, (x, y) -> x, e));
            fs.add(incomplete.thenCombineAsync(complete, (x, y) -> x, e));

            fs.add(complete.thenAcceptBothAsync(incomplete, (x, y) -> {}, e));
            fs.add(incomplete.thenAcceptBothAsync(complete, (x, y) -> {}, e));

            fs.add(complete.runAfterBothAsync(incomplete, () -> {}, e));
            fs.add(incomplete.runAfterBothAsync(complete, () -> {}, e));

            for (CompletableFuture<?> future : fs)
                checkIncomplete(future);
            futures.addAll(fs);
        }

        {
            List<CompletableFuture<?>> fs = new ArrayList<>();

            fs.add(complete.applyToEitherAsync(incomplete, z -> z, e));
            fs.add(incomplete.applyToEitherAsync(complete, z -> z, e));

            fs.add(complete.acceptEitherAsync(incomplete, z -> {}, e));
            fs.add(incomplete.acceptEitherAsync(complete, z -> {}, e));

            fs.add(complete.runAfterEitherAsync(incomplete, () -> {}, e));
            fs.add(incomplete.runAfterEitherAsync(complete, () -> {}, e));

            for (CompletableFuture<?> future : fs)
                checkCompletedWithWrappedException(future, e.ex);
            futures.addAll(fs);
        }

        incomplete.complete(v);

        for (CompletableFuture<?> future : futures)
            checkCompletedWithWrappedException(future, e.ex);

        assertEquals(futures.size(), e.count.get());
    }}

    /**
     * Test submissions to an executor that rejects all tasks, but
     * should never be invoked because the dependent future is
     * explicitly completed.
     */
    public void testRejectingExecutorNeverInvoked() {
        for (Integer v : new Integer[] { 1, null })
    {
        final CountingRejectingExecutor e = new CountingRejectingExecutor();

        final CompletableFuture<Integer> complete = CompletableFuture.completedFuture(v);
        final CompletableFuture<Integer> incomplete = new CompletableFuture<>();

        List<CompletableFuture<?>> futures = new ArrayList<>();

        List<CompletableFuture<Integer>> srcs = new ArrayList<>();
        srcs.add(complete);
        srcs.add(incomplete);

        List<CompletableFuture<?>> fs = new ArrayList<>();
        fs.add(incomplete.thenRunAsync(() -> {}, e));
        fs.add(incomplete.thenAcceptAsync(z -> {}, e));
        fs.add(incomplete.thenApplyAsync(z -> z, e));

        fs.add(incomplete.thenCombineAsync(incomplete, (x, y) -> x, e));
        fs.add(incomplete.thenAcceptBothAsync(incomplete, (x, y) -> {}, e));
        fs.add(incomplete.runAfterBothAsync(incomplete, () -> {}, e));

        fs.add(incomplete.applyToEitherAsync(incomplete, z -> z, e));
        fs.add(incomplete.acceptEitherAsync(incomplete, z -> {}, e));
        fs.add(incomplete.runAfterEitherAsync(incomplete, () -> {}, e));

        fs.add(incomplete.thenComposeAsync(z -> null, e));
        fs.add(incomplete.whenCompleteAsync((z, t) -> {}, e));
        fs.add(incomplete.handleAsync((z, t) -> null, e));

        fs.add(complete.thenCombineAsync(incomplete, (x, y) -> x, e));
        fs.add(incomplete.thenCombineAsync(complete, (x, y) -> x, e));

        fs.add(complete.thenAcceptBothAsync(incomplete, (x, y) -> {}, e));
        fs.add(incomplete.thenAcceptBothAsync(complete, (x, y) -> {}, e));

        fs.add(complete.runAfterBothAsync(incomplete, () -> {}, e));
        fs.add(incomplete.runAfterBothAsync(complete, () -> {}, e));

        for (CompletableFuture<?> future : fs)
            checkIncomplete(future);

        for (CompletableFuture<?> future : fs)
            future.complete(null);

        incomplete.complete(v);

        for (CompletableFuture<?> future : fs)
            checkCompletedNormally(future, null);

        assertEquals(0, e.count.get());
    }}

    /**
     * toCompletableFuture returns this CompletableFuture.
     */
    public void testToCompletableFuture() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        assertSame(f, f.toCompletableFuture());
    }

    // jdk9

    /**
     * newIncompleteFuture returns an incomplete CompletableFuture
     */
    public void testNewIncompleteFuture() {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletableFuture<Integer> g = f.newIncompleteFuture();
        checkIncomplete(f);
        checkIncomplete(g);
        f.complete(v1);
        checkCompletedNormally(f, v1);
        checkIncomplete(g);
        g.complete(v1);
        checkCompletedNormally(g, v1);
        assertSame(g.getClass(), CompletableFuture.class);
    }}

    /**
     * completedStage returns a completed CompletionStage
     */
    public void testCompletedStage() {
        AtomicInteger x = new AtomicInteger(0);
        AtomicReference<Throwable> r = new AtomicReference<>();
        CompletionStage<Integer> f = CompletableFuture.completedStage(1);
        f.whenComplete((v, e) -> {if (e != null) r.set(e); else x.set(v);});
        assertEquals(x.get(), 1);
        assertNull(r.get());
    }

    /**
     * defaultExecutor by default returns the commonPool if
     * it supports more than one thread.
     */
    public void testDefaultExecutor() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        Executor e = f.defaultExecutor();
        Executor c = ForkJoinPool.commonPool();
        if (ForkJoinPool.getCommonPoolParallelism() > 1)
            assertSame(e, c);
        else
            assertNotSame(e, c);
    }

    /**
     * failedFuture returns a CompletableFuture completed
     * exceptionally with the given Exception
     */
    public void testFailedFuture() {
        CFException ex = new CFException();
        CompletableFuture<Integer> f = CompletableFuture.failedFuture(ex);
        checkCompletedExceptionally(f, ex);
    }

    /**
     * failedFuture(null) throws NPE
     */
    public void testFailedFuture_null() {
        try {
            CompletableFuture<Integer> f = CompletableFuture.failedFuture(null);
            shouldThrow();
        } catch (NullPointerException success) {}
    }

    /**
     * copy returns a CompletableFuture that is completed normally,
     * with the same value, when source is.
     */
    public void testCopy_normalCompletion() {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) assertTrue(f.complete(v1));
        CompletableFuture<Integer> g = f.copy();
        if (createIncomplete) {
            checkIncomplete(f);
            checkIncomplete(g);
            assertTrue(f.complete(v1));
        }
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v1);
    }}

    /**
     * copy returns a CompletableFuture that is completed exceptionally
     * when source is.
     */
    public void testCopy_exceptionalCompletion() {
        for (boolean createIncomplete : new boolean[] { true, false })
    {
        CFException ex = new CFException();
        CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) f.completeExceptionally(ex);
        CompletableFuture<Integer> g = f.copy();
        if (createIncomplete) {
            checkIncomplete(f);
            checkIncomplete(g);
            f.completeExceptionally(ex);
        }
        checkCompletedExceptionally(f, ex);
        checkCompletedWithWrappedException(g, ex);
    }}

    /**
     * Completion of a copy does not complete its source.
     */
    public void testCopy_oneWayPropagation() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        assertTrue(f.copy().complete(1));
        assertTrue(f.copy().complete(null));
        assertTrue(f.copy().cancel(true));
        assertTrue(f.copy().cancel(false));
        assertTrue(f.copy().completeExceptionally(new CFException()));
        checkIncomplete(f);
    }

    /**
     * minimalCompletionStage returns a CompletableFuture that is
     * completed normally, with the same value, when source is.
     */
    public void testMinimalCompletionStage() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletionStage<Integer> g = f.minimalCompletionStage();
        AtomicInteger x = new AtomicInteger(0);
        AtomicReference<Throwable> r = new AtomicReference<>();
        checkIncomplete(f);
        g.whenComplete((v, e) -> {if (e != null) r.set(e); else x.set(v);});
        f.complete(1);
        checkCompletedNormally(f, 1);
        assertEquals(x.get(), 1);
        assertNull(r.get());
    }

    /**
     * minimalCompletionStage returns a CompletableFuture that is
     * completed exceptionally when source is.
     */
    public void testMinimalCompletionStage2() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletionStage<Integer> g = f.minimalCompletionStage();
        AtomicInteger x = new AtomicInteger(0);
        AtomicReference<Throwable> r = new AtomicReference<>();
        g.whenComplete((v, e) -> {if (e != null) r.set(e); else x.set(v);});
        checkIncomplete(f);
        CFException ex = new CFException();
        f.completeExceptionally(ex);
        checkCompletedExceptionally(f, ex);
        assertEquals(x.get(), 0);
        assertEquals(r.get().getCause(), ex);
    }

    /**
     * failedStage returns a CompletionStage completed
     * exceptionally with the given Exception
     */
    public void testFailedStage() {
        CFException ex = new CFException();
        CompletionStage<Integer> f = CompletableFuture.failedStage(ex);
        AtomicInteger x = new AtomicInteger(0);
        AtomicReference<Throwable> r = new AtomicReference<>();
        f.whenComplete((v, e) -> {if (e != null) r.set(e); else x.set(v);});
        assertEquals(x.get(), 0);
        assertEquals(r.get(), ex);
    }

    /**
     * completeAsync completes with value of given supplier
     */
    public void testCompleteAsync() {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        f.completeAsync(() -> v1);
        f.join();
        checkCompletedNormally(f, v1);
    }}

    /**
     * completeAsync completes exceptionally if given supplier throws
     */
    public void testCompleteAsync2() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CFException ex = new CFException();
        f.completeAsync(() -> { throw ex; });
        try {
            f.join();
            shouldThrow();
        } catch (CompletionException success) {}
        checkCompletedWithWrappedException(f, ex);
    }

    /**
     * completeAsync with given executor completes with value of given supplier
     */
    public void testCompleteAsync3() {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        ThreadExecutor executor = new ThreadExecutor();
        f.completeAsync(() -> v1, executor);
        assertSame(v1, f.join());
        checkCompletedNormally(f, v1);
        assertEquals(1, executor.count.get());
    }}

    /**
     * completeAsync with given executor completes exceptionally if
     * given supplier throws
     */
    public void testCompleteAsync4() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CFException ex = new CFException();
        ThreadExecutor executor = new ThreadExecutor();
        f.completeAsync(() -> { throw ex; }, executor);
        try {
            f.join();
            shouldThrow();
        } catch (CompletionException success) {}
        checkCompletedWithWrappedException(f, ex);
        assertEquals(1, executor.count.get());
    }

    /**
     * orTimeout completes with TimeoutException if not complete
     */
    public void testOrTimeout_timesOut() {
        long timeoutMillis = timeoutMillis();
        CompletableFuture<Integer> f = new CompletableFuture<>();
        long startTime = System.nanoTime();
        assertSame(f, f.orTimeout(timeoutMillis, MILLISECONDS));
        checkCompletedWithTimeoutException(f);
        assertTrue(millisElapsedSince(startTime) >= timeoutMillis);
    }

    /**
     * orTimeout completes normally if completed before timeout
     */
    public void testOrTimeout_completed() {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletableFuture<Integer> g = new CompletableFuture<>();
        long startTime = System.nanoTime();
        f.complete(v1);
        assertSame(f, f.orTimeout(LONG_DELAY_MS, MILLISECONDS));
        assertSame(g, g.orTimeout(LONG_DELAY_MS, MILLISECONDS));
        g.complete(v1);
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v1);
        assertTrue(millisElapsedSince(startTime) < LONG_DELAY_MS / 2);
    }}

    /**
     * completeOnTimeout completes with given value if not complete
     */
    public void testCompleteOnTimeout_timesOut() {
        testInParallel(() -> testCompleteOnTimeout_timesOut(42),
                       () -> testCompleteOnTimeout_timesOut(null));
    }

    /**
     * completeOnTimeout completes with given value if not complete
     */
    public void testCompleteOnTimeout_timesOut(Integer v) {
        long timeoutMillis = timeoutMillis();
        CompletableFuture<Integer> f = new CompletableFuture<>();
        long startTime = System.nanoTime();
        assertSame(f, f.completeOnTimeout(v, timeoutMillis, MILLISECONDS));
        assertSame(v, f.join());
        assertTrue(millisElapsedSince(startTime) >= timeoutMillis);
        f.complete(99);         // should have no effect
        checkCompletedNormally(f, v);
    }

    /**
     * completeOnTimeout has no effect if completed within timeout
     */
    public void testCompleteOnTimeout_completed() {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletableFuture<Integer> g = new CompletableFuture<>();
        long startTime = System.nanoTime();
        f.complete(v1);
        assertSame(f, f.completeOnTimeout(-1, LONG_DELAY_MS, MILLISECONDS));
        assertSame(g, g.completeOnTimeout(-1, LONG_DELAY_MS, MILLISECONDS));
        g.complete(v1);
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v1);
        assertTrue(millisElapsedSince(startTime) < LONG_DELAY_MS / 2);
    }}

    /**
     * delayedExecutor returns an executor that delays submission
     */
    public void testDelayedExecutor() {
        testInParallel(() -> testDelayedExecutor(null, null),
                       () -> testDelayedExecutor(null, 1),
                       () -> testDelayedExecutor(new ThreadExecutor(), 1),
                       () -> testDelayedExecutor(new ThreadExecutor(), 1));
    }

    public void testDelayedExecutor(Executor executor, Integer v) throws Exception {
        long timeoutMillis = timeoutMillis();
        // Use an "unreasonably long" long timeout to catch lingering threads
        long longTimeoutMillis = 1000 * 60 * 60 * 24;
        final Executor delayer, longDelayer;
        if (executor == null) {
            delayer = CompletableFuture.delayedExecutor(timeoutMillis, MILLISECONDS);
            longDelayer = CompletableFuture.delayedExecutor(longTimeoutMillis, MILLISECONDS);
        } else {
            delayer = CompletableFuture.delayedExecutor(timeoutMillis, MILLISECONDS, executor);
            longDelayer = CompletableFuture.delayedExecutor(longTimeoutMillis, MILLISECONDS, executor);
        }
        long startTime = System.nanoTime();
        CompletableFuture<Integer> f =
            CompletableFuture.supplyAsync(() -> v, delayer);
        CompletableFuture<Integer> g =
            CompletableFuture.supplyAsync(() -> v, longDelayer);

        assertNull(g.getNow(null));

        assertSame(v, f.get(LONG_DELAY_MS, MILLISECONDS));
        long millisElapsed = millisElapsedSince(startTime);
        assertTrue(millisElapsed >= timeoutMillis);
        assertTrue(millisElapsed < LONG_DELAY_MS / 2);

        checkCompletedNormally(f, v);

        checkIncomplete(g);
        assertTrue(g.cancel(true));
    }

    //--- tests of implementation details; not part of official tck ---

    Object resultOf(CompletableFuture<?> f) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                System.setSecurityManager(null);
            } catch (SecurityException giveUp) {
                return "Reflection not available";
            }
        }

        try {
            java.lang.reflect.Field resultField
                = CompletableFuture.class.getDeclaredField("result");
            resultField.setAccessible(true);
            return resultField.get(f);
        } catch (Throwable t) {
            throw new AssertionError(t);
        } finally {
            if (sm != null) System.setSecurityManager(sm);
        }
    }

    public void testExceptionPropagationReusesResultObject() {
        if (!testImplementationDetails) return;
        for (ExecutionMode m : ExecutionMode.values())
    {
        final CFException ex = new CFException();
        final CompletableFuture<Integer> v42 = CompletableFuture.completedFuture(42);
        final CompletableFuture<Integer> incomplete = new CompletableFuture<>();

        final Runnable noopRunnable = new Noop(m);
        final Consumer<Integer> noopConsumer = new NoopConsumer(m);
        final Function<Integer, Integer> incFunction = new IncFunction(m);

        List<Function<CompletableFuture<Integer>, CompletableFuture<?>>> funs
            = new ArrayList<>();

        funs.add(y -> m.thenRun(y, noopRunnable));
        funs.add(y -> m.thenAccept(y, noopConsumer));
        funs.add(y -> m.thenApply(y, incFunction));

        funs.add(y -> m.runAfterEither(y, incomplete, noopRunnable));
        funs.add(y -> m.acceptEither(y, incomplete, noopConsumer));
        funs.add(y -> m.applyToEither(y, incomplete, incFunction));

        funs.add(y -> m.runAfterBoth(y, v42, noopRunnable));
        funs.add(y -> m.runAfterBoth(v42, y, noopRunnable));
        funs.add(y -> m.thenAcceptBoth(y, v42, new SubtractAction(m)));
        funs.add(y -> m.thenAcceptBoth(v42, y, new SubtractAction(m)));
        funs.add(y -> m.thenCombine(y, v42, new SubtractFunction(m)));
        funs.add(y -> m.thenCombine(v42, y, new SubtractFunction(m)));

        funs.add(y -> m.whenComplete(y, (Integer r, Throwable t) -> {}));

        funs.add(y -> m.thenCompose(y, new CompletableFutureInc(m)));

        funs.add(y -> CompletableFuture.allOf(y));
        funs.add(y -> CompletableFuture.allOf(y, v42));
        funs.add(y -> CompletableFuture.allOf(v42, y));
        funs.add(y -> CompletableFuture.anyOf(y));
        funs.add(y -> CompletableFuture.anyOf(y, incomplete));
        funs.add(y -> CompletableFuture.anyOf(incomplete, y));

        for (Function<CompletableFuture<Integer>, CompletableFuture<?>>
                 fun : funs) {
            CompletableFuture<Integer> f = new CompletableFuture<>();
            f.completeExceptionally(ex);
            CompletableFuture<Integer> src = m.thenApply(f, incFunction);
            checkCompletedWithWrappedException(src, ex);
            CompletableFuture<?> dep = fun.apply(src);
            checkCompletedWithWrappedException(dep, ex);
            assertSame(resultOf(src), resultOf(dep));
        }

        for (Function<CompletableFuture<Integer>, CompletableFuture<?>>
                 fun : funs) {
            CompletableFuture<Integer> f = new CompletableFuture<>();
            CompletableFuture<Integer> src = m.thenApply(f, incFunction);
            CompletableFuture<?> dep = fun.apply(src);
            f.completeExceptionally(ex);
            checkCompletedWithWrappedException(src, ex);
            checkCompletedWithWrappedException(dep, ex);
            assertSame(resultOf(src), resultOf(dep));
        }

        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (Function<CompletableFuture<Integer>, CompletableFuture<?>>
                 fun : funs) {
            CompletableFuture<Integer> f = new CompletableFuture<>();
            f.cancel(mayInterruptIfRunning);
            checkCancelled(f);
            CompletableFuture<Integer> src = m.thenApply(f, incFunction);
            checkCompletedWithWrappedCancellationException(src);
            CompletableFuture<?> dep = fun.apply(src);
            checkCompletedWithWrappedCancellationException(dep);
            assertSame(resultOf(src), resultOf(dep));
        }

        for (boolean mayInterruptIfRunning : new boolean[] { true, false })
        for (Function<CompletableFuture<Integer>, CompletableFuture<?>>
                 fun : funs) {
            CompletableFuture<Integer> f = new CompletableFuture<>();
            CompletableFuture<Integer> src = m.thenApply(f, incFunction);
            CompletableFuture<?> dep = fun.apply(src);
            f.cancel(mayInterruptIfRunning);
            checkCancelled(f);
            checkCompletedWithWrappedCancellationException(src);
            checkCompletedWithWrappedCancellationException(dep);
            assertSame(resultOf(src), resultOf(dep));
        }
    }}

    /**
     * Minimal completion stages throw UOE for most non-CompletionStage methods
     */
    public void testMinimalCompletionStage_minimality() {
        if (!testImplementationDetails) return;
        Function<Method, String> toSignature =
            method -> method.getName() + Arrays.toString(method.getParameterTypes());
        Predicate<Method> isNotStatic =
            method -> (method.getModifiers() & Modifier.STATIC) == 0;
        List<Method> minimalMethods =
            Stream.of(Object.class, CompletionStage.class)
            .flatMap(klazz -> Stream.of(klazz.getMethods()))
            .filter(isNotStatic)
            .collect(Collectors.toList());
        // Methods from CompletableFuture permitted NOT to throw UOE
        String[] signatureWhitelist = {
            "newIncompleteFuture[]",
            "defaultExecutor[]",
            "minimalCompletionStage[]",
            "copy[]",
        };
        Set<String> permittedMethodSignatures =
            Stream.concat(minimalMethods.stream().map(toSignature),
                          Stream.of(signatureWhitelist))
            .collect(Collectors.toSet());
        List<Method> allMethods = Stream.of(CompletableFuture.class.getMethods())
            .filter(isNotStatic)
            .filter(method -> !permittedMethodSignatures.contains(toSignature.apply(method)))
            .collect(Collectors.toList());

        List<CompletionStage<Integer>> stages = new ArrayList<>();
        CompletionStage<Integer> min =
            new CompletableFuture<Integer>().minimalCompletionStage();
        stages.add(min);
        stages.add(min.thenApply(x -> x));
        stages.add(CompletableFuture.completedStage(1));
        stages.add(CompletableFuture.failedStage(new CFException()));

        List<Method> bugs = new ArrayList<>();
        for (Method method : allMethods) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] args = new Object[parameterTypes.length];
            // Manufacture boxed primitives for primitive params
            for (int i = 0; i < args.length; i++) {
                Class<?> type = parameterTypes[i];
                if (parameterTypes[i] == boolean.class)
                    args[i] = false;
                else if (parameterTypes[i] == int.class)
                    args[i] = 0;
                else if (parameterTypes[i] == long.class)
                    args[i] = 0L;
            }
            for (CompletionStage<Integer> stage : stages) {
                try {
                    method.invoke(stage, args);
                    bugs.add(method);
                }
                catch (java.lang.reflect.InvocationTargetException expected) {
                    if (! (expected.getCause() instanceof UnsupportedOperationException)) {
                        bugs.add(method);
                        // expected.getCause().printStackTrace();
                    }
                }
                catch (ReflectiveOperationException bad) { throw new Error(bad); }
            }
        }
        if (!bugs.isEmpty())
            throw new Error("Methods did not throw UOE: " + bugs);
    }

    /**
     * minimalStage.toCompletableFuture() returns a CompletableFuture that
     * is completed normally, with the same value, when source is.
     */
    public void testMinimalCompletionStage_toCompletableFuture_normalCompletion() {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletionStage<Integer> minimal = f.minimalCompletionStage();
        if (!createIncomplete) assertTrue(f.complete(v1));
        CompletableFuture<Integer> g = minimal.toCompletableFuture();
        if (createIncomplete) {
            checkIncomplete(f);
            checkIncomplete(g);
            assertTrue(f.complete(v1));
        }
        checkCompletedNormally(f, v1);
        checkCompletedNormally(g, v1);
    }}

    /**
     * minimalStage.toCompletableFuture() returns a CompletableFuture that
     * is completed exceptionally when source is.
     */
    public void testMinimalCompletionStage_toCompletableFuture_exceptionalCompletion() {
        for (boolean createIncomplete : new boolean[] { true, false })
    {
        CFException ex = new CFException();
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletionStage<Integer> minimal = f.minimalCompletionStage();
        if (!createIncomplete) f.completeExceptionally(ex);
        CompletableFuture<Integer> g = minimal.toCompletableFuture();
        if (createIncomplete) {
            checkIncomplete(f);
            checkIncomplete(g);
            f.completeExceptionally(ex);
        }
        checkCompletedExceptionally(f, ex);
        checkCompletedWithWrappedException(g, ex);
    }}

    /**
     * minimalStage.toCompletableFuture() gives mutable CompletableFuture
     */
    public void testMinimalCompletionStage_toCompletableFuture_mutable() {
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletionStage minimal = f.minimalCompletionStage();
        CompletableFuture<Integer> g = minimal.toCompletableFuture();
        assertTrue(g.complete(v1));
        checkCompletedNormally(g, v1);
        checkIncomplete(f);
        checkIncomplete(minimal.toCompletableFuture());
    }}

    /**
     * minimalStage.toCompletableFuture().join() awaits completion
     */
    public void testMinimalCompletionStage_toCompletableFuture_join() throws Exception {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        if (!createIncomplete) assertTrue(f.complete(v1));
        CompletionStage<Integer> minimal = f.minimalCompletionStage();
        if (createIncomplete) assertTrue(f.complete(v1));
        assertEquals(v1, minimal.toCompletableFuture().join());
        assertEquals(v1, minimal.toCompletableFuture().get());
        checkCompletedNormally(minimal.toCompletableFuture(), v1);
    }}

    /**
     * Completion of a toCompletableFuture copy of a minimal stage
     * does not complete its source.
     */
    public void testMinimalCompletionStage_toCompletableFuture_oneWayPropagation() {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletionStage<Integer> g = f.minimalCompletionStage();
        assertTrue(g.toCompletableFuture().complete(1));
        assertTrue(g.toCompletableFuture().complete(null));
        assertTrue(g.toCompletableFuture().cancel(true));
        assertTrue(g.toCompletableFuture().cancel(false));
        assertTrue(g.toCompletableFuture().completeExceptionally(new CFException()));
        checkIncomplete(g.toCompletableFuture());
        f.complete(1);
        checkCompletedNormally(g.toCompletableFuture(), 1);
    }

    /** Demo utility method for external reliable toCompletableFuture */
    static <T> CompletableFuture<T> toCompletableFuture(CompletionStage<T> stage) {
        CompletableFuture<T> f = new CompletableFuture<>();
        stage.handle((T t, Throwable ex) -> {
                         if (ex != null) f.completeExceptionally(ex);
                         else f.complete(t);
                         return null;
                     });
        return f;
    }

    /** Demo utility method to join a CompletionStage */
    static <T> T join(CompletionStage<T> stage) {
        return toCompletableFuture(stage).join();
    }

    /**
     * Joining a minimal stage "by hand" works
     */
    public void testMinimalCompletionStage_join_by_hand() {
        for (boolean createIncomplete : new boolean[] { true, false })
        for (Integer v1 : new Integer[] { 1, null })
    {
        CompletableFuture<Integer> f = new CompletableFuture<>();
        CompletionStage<Integer> minimal = f.minimalCompletionStage();
        CompletableFuture<Integer> g = new CompletableFuture<>();
        if (!createIncomplete) assertTrue(f.complete(v1));
        minimal.thenAccept(x -> g.complete(x));
        if (createIncomplete) assertTrue(f.complete(v1));
        g.join();
        checkCompletedNormally(g, v1);
        checkCompletedNormally(f, v1);
        assertEquals(v1, join(minimal));
    }}

    static class Monad {
        static class ZeroException extends RuntimeException {
            public ZeroException() { super("monadic zero"); }
        }
        // "return", "unit"
        static <T> CompletableFuture<T> unit(T value) {
            return completedFuture(value);
        }
        // monadic zero ?
        static <T> CompletableFuture<T> zero() {
            return failedFuture(new ZeroException());
        }
        // >=>
        static <T,U,V> Function<T, CompletableFuture<V>> compose
            (Function<T, CompletableFuture<U>> f,
             Function<U, CompletableFuture<V>> g) {
            return x -> f.apply(x).thenCompose(g);
        }

        static void assertZero(CompletableFuture<?> f) {
            try {
                f.getNow(null);
                throw new AssertionFailedError("should throw");
            } catch (CompletionException success) {
                assertTrue(success.getCause() instanceof ZeroException);
            }
        }

        static <T> void assertFutureEquals(CompletableFuture<T> f,
                                           CompletableFuture<T> g) {
            T fval = null, gval = null;
            Throwable fex = null, gex = null;

            try { fval = f.get(); }
            catch (ExecutionException ex) { fex = ex.getCause(); }
            catch (Throwable ex) { fex = ex; }

            try { gval = g.get(); }
            catch (ExecutionException ex) { gex = ex.getCause(); }
            catch (Throwable ex) { gex = ex; }

            if (fex != null || gex != null)
                assertSame(fex.getClass(), gex.getClass());
            else
                assertEquals(fval, gval);
        }

        static class PlusFuture<T> extends CompletableFuture<T> {
            AtomicReference<Throwable> firstFailure = new AtomicReference<>(null);
        }

        /** Implements "monadic plus". */
        static <T> CompletableFuture<T> plus(CompletableFuture<? extends T> f,
                                             CompletableFuture<? extends T> g) {
            PlusFuture<T> plus = new PlusFuture<T>();
            BiConsumer<T, Throwable> action = (T result, Throwable ex) -> {
                try {
                    if (ex == null) {
                        if (plus.complete(result))
                            if (plus.firstFailure.get() != null)
                                plus.firstFailure.set(null);
                    }
                    else if (plus.firstFailure.compareAndSet(null, ex)) {
                        if (plus.isDone())
                            plus.firstFailure.set(null);
                    }
                    else {
                        // first failure has precedence
                        Throwable first = plus.firstFailure.getAndSet(null);

                        // may fail with "Self-suppression not permitted"
                        try { first.addSuppressed(ex); }
                        catch (Exception ignored) {}

                        plus.completeExceptionally(first);
                    }
                } catch (Throwable unexpected) {
                    plus.completeExceptionally(unexpected);
                }
            };
            f.whenComplete(action);
            g.whenComplete(action);
            return plus;
        }
    }

    /**
     * CompletableFuture is an additive monad - sort of.
     * https://en.wikipedia.org/wiki/Monad_(functional_programming)#Additive_monads
     */
    public void testAdditiveMonad() throws Throwable {
        Function<Long, CompletableFuture<Long>> unit = Monad::unit;
        CompletableFuture<Long> zero = Monad.zero();

        // Some mutually non-commutative functions
        Function<Long, CompletableFuture<Long>> triple
            = x -> Monad.unit(3 * x);
        Function<Long, CompletableFuture<Long>> inc
            = x -> Monad.unit(x + 1);

        // unit is a right identity: m >>= unit === m
        Monad.assertFutureEquals(inc.apply(5L).thenCompose(unit),
                                 inc.apply(5L));
        // unit is a left identity: (unit x) >>= f === f x
        Monad.assertFutureEquals(unit.apply(5L).thenCompose(inc),
                                 inc.apply(5L));

        // associativity: (m >>= f) >>= g === m >>= ( \x -> (f x >>= g) )
        Monad.assertFutureEquals(
            unit.apply(5L).thenCompose(inc).thenCompose(triple),
            unit.apply(5L).thenCompose(x -> inc.apply(x).thenCompose(triple)));

        // The case for CompletableFuture as an additive monad is weaker...

        // zero is a monadic zero
        Monad.assertZero(zero);

        // left zero: zero >>= f === zero
        Monad.assertZero(zero.thenCompose(inc));
        // right zero: f >>= (\x -> zero) === zero
        Monad.assertZero(inc.apply(5L).thenCompose(x -> zero));

        // f plus zero === f
        Monad.assertFutureEquals(Monad.unit(5L),
                                 Monad.plus(Monad.unit(5L), zero));
        // zero plus f === f
        Monad.assertFutureEquals(Monad.unit(5L),
                                 Monad.plus(zero, Monad.unit(5L)));
        // zero plus zero === zero
        Monad.assertZero(Monad.plus(zero, zero));
        {
            CompletableFuture<Long> f = Monad.plus(Monad.unit(5L),
                                                   Monad.unit(8L));
            // non-determinism
            assertTrue(f.get() == 5L || f.get() == 8L);
        }

        CompletableFuture<Long> godot = new CompletableFuture<>();
        // f plus godot === f (doesn't wait for godot)
        Monad.assertFutureEquals(Monad.unit(5L),
                                 Monad.plus(Monad.unit(5L), godot));
        // godot plus f === f (doesn't wait for godot)
        Monad.assertFutureEquals(Monad.unit(5L),
                                 Monad.plus(godot, Monad.unit(5L)));
    }

    /** Test long recursive chains of CompletableFutures with cascading completions */
    public void testRecursiveChains() throws Throwable {
        for (ExecutionMode m : ExecutionMode.values())
        for (boolean addDeadEnds : new boolean[] { true, false })
    {
        final int val = 42;
        final int n = expensiveTests ? 1_000 : 2;
        CompletableFuture<Integer> head = new CompletableFuture<>();
        CompletableFuture<Integer> tail = head;
        for (int i = 0; i < n; i++) {
            if (addDeadEnds) m.thenApply(tail, v -> v + 1);
            tail = m.thenApply(tail, v -> v + 1);
            if (addDeadEnds) m.applyToEither(tail, tail, v -> v + 1);
            tail = m.applyToEither(tail, tail, v -> v + 1);
            if (addDeadEnds) m.thenCombine(tail, tail, (v, w) -> v + 1);
            tail = m.thenCombine(tail, tail, (v, w) -> v + 1);
        }
        head.complete(val);
        assertEquals(val + 3 * n, (int) tail.join());
    }}

    /**
     * A single CompletableFuture with many dependents.
     * A demo of scalability - runtime is O(n).
     */
    public void testManyDependents() throws Throwable {
        final int n = expensiveTests ? 1_000_000 : 10;
        final CompletableFuture<Void> head = new CompletableFuture<>();
        final CompletableFuture<Void> complete = CompletableFuture.completedFuture((Void)null);
        final AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < n; i++) {
            head.thenRun(() -> count.getAndIncrement());
            head.thenAccept(x -> count.getAndIncrement());
            head.thenApply(x -> count.getAndIncrement());

            head.runAfterBoth(complete, () -> count.getAndIncrement());
            head.thenAcceptBoth(complete, (x, y) -> count.getAndIncrement());
            head.thenCombine(complete, (x, y) -> count.getAndIncrement());
            complete.runAfterBoth(head, () -> count.getAndIncrement());
            complete.thenAcceptBoth(head, (x, y) -> count.getAndIncrement());
            complete.thenCombine(head, (x, y) -> count.getAndIncrement());

            head.runAfterEither(new CompletableFuture<Void>(), () -> count.getAndIncrement());
            head.acceptEither(new CompletableFuture<Void>(), x -> count.getAndIncrement());
            head.applyToEither(new CompletableFuture<Void>(), x -> count.getAndIncrement());
            new CompletableFuture<Void>().runAfterEither(head, () -> count.getAndIncrement());
            new CompletableFuture<Void>().acceptEither(head, x -> count.getAndIncrement());
            new CompletableFuture<Void>().applyToEither(head, x -> count.getAndIncrement());
        }
        head.complete(null);
        assertEquals(5 * 3 * n, count.get());
    }

    /** ant -Dvmoptions=-Xmx8m -Djsr166.expensiveTests=true -Djsr166.tckTestClass=CompletableFutureTest tck */
    public void testCoCompletionGarbageRetention() throws Throwable {
        final int n = expensiveTests ? 1_000_000 : 10;
        final CompletableFuture<Integer> incomplete = new CompletableFuture<>();
        CompletableFuture<Integer> f;
        for (int i = 0; i < n; i++) {
            f = new CompletableFuture<>();
            f.runAfterEither(incomplete, () -> {});
            f.complete(null);

            f = new CompletableFuture<>();
            f.acceptEither(incomplete, x -> {});
            f.complete(null);

            f = new CompletableFuture<>();
            f.applyToEither(incomplete, x -> x);
            f.complete(null);

            f = new CompletableFuture<>();
            CompletableFuture.anyOf(new CompletableFuture<?>[] { f, incomplete });
            f.complete(null);
        }

        for (int i = 0; i < n; i++) {
            f = new CompletableFuture<>();
            incomplete.runAfterEither(f, () -> {});
            f.complete(null);

            f = new CompletableFuture<>();
            incomplete.acceptEither(f, x -> {});
            f.complete(null);

            f = new CompletableFuture<>();
            incomplete.applyToEither(f, x -> x);
            f.complete(null);

            f = new CompletableFuture<>();
            CompletableFuture.anyOf(new CompletableFuture<?>[] { incomplete, f });
            f.complete(null);
        }
    }

    /**
     * Reproduction recipe for:
     * 8160402: Garbage retention with CompletableFuture.anyOf
     * cvs update -D '2016-05-01' ./src/main/java/util/concurrent/CompletableFuture.java && ant -Dvmoptions=-Xmx8m -Djsr166.expensiveTests=true -Djsr166.tckTestClass=CompletableFutureTest -Djsr166.methodFilter=testAnyOfGarbageRetention tck; cvs update -A
     */
    public void testAnyOfGarbageRetention() throws Throwable {
        for (Integer v : new Integer[] { 1, null })
    {
        final int n = expensiveTests ? 100_000 : 10;
        CompletableFuture<Integer>[] fs
            = (CompletableFuture<Integer>[]) new CompletableFuture<?>[100];
        for (int i = 0; i < fs.length; i++)
            fs[i] = new CompletableFuture<>();
        fs[fs.length - 1].complete(v);
        for (int i = 0; i < n; i++)
            checkCompletedNormally(CompletableFuture.anyOf(fs), v);
    }}

    /**
     * Checks for garbage retention with allOf.
     *
     * As of 2016-07, fails with OOME:
     * ant -Dvmoptions=-Xmx8m -Djsr166.expensiveTests=true -Djsr166.tckTestClass=CompletableFutureTest -Djsr166.methodFilter=testCancelledAllOfGarbageRetention tck
     */
    public void testCancelledAllOfGarbageRetention() throws Throwable {
        final int n = expensiveTests ? 100_000 : 10;
        CompletableFuture<Integer>[] fs
            = (CompletableFuture<Integer>[]) new CompletableFuture<?>[100];
        for (int i = 0; i < fs.length; i++)
            fs[i] = new CompletableFuture<>();
        for (int i = 0; i < n; i++)
            assertTrue(CompletableFuture.allOf(fs).cancel(false));
    }

    /**
     * Checks for garbage retention when a dependent future is
     * cancelled and garbage-collected.
     * 8161600: Garbage retention when source CompletableFutures are never completed
     *
     * As of 2016-07, fails with OOME:
     * ant -Dvmoptions=-Xmx8m -Djsr166.expensiveTests=true -Djsr166.tckTestClass=CompletableFutureTest -Djsr166.methodFilter=testCancelledGarbageRetention tck
     */
    public void testCancelledGarbageRetention() throws Throwable {
        final int n = expensiveTests ? 100_000 : 10;
        CompletableFuture<Integer> neverCompleted = new CompletableFuture<>();
        for (int i = 0; i < n; i++)
            assertTrue(neverCompleted.thenRun(() -> {}).cancel(true));
    }

    /**
     * Checks for garbage retention when MinimalStage.toCompletableFuture()
     * is invoked many times.
     * 8161600: Garbage retention when source CompletableFutures are never completed
     *
     * As of 2016-07, fails with OOME:
     * ant -Dvmoptions=-Xmx8m -Djsr166.expensiveTests=true -Djsr166.tckTestClass=CompletableFutureTest -Djsr166.methodFilter=testToCompletableFutureGarbageRetention tck
     */
    public void testToCompletableFutureGarbageRetention() throws Throwable {
        final int n = expensiveTests ? 900_000 : 10;
        CompletableFuture<Integer> neverCompleted = new CompletableFuture<>();
        CompletionStage minimal = neverCompleted.minimalCompletionStage();
        for (int i = 0; i < n; i++)
            assertTrue(minimal.toCompletableFuture().cancel(true));
    }

//     static <U> U join(CompletionStage<U> stage) {
//         CompletableFuture<U> f = new CompletableFuture<>();
//         stage.whenComplete((v, ex) -> {
//             if (ex != null) f.completeExceptionally(ex); else f.complete(v);
//         });
//         return f.join();
//     }

//     static <U> boolean isDone(CompletionStage<U> stage) {
//         CompletableFuture<U> f = new CompletableFuture<>();
//         stage.whenComplete((v, ex) -> {
//             if (ex != null) f.completeExceptionally(ex); else f.complete(v);
//         });
//         return f.isDone();
//     }

//     static <U> U join2(CompletionStage<U> stage) {
//         return stage.toCompletableFuture().copy().join();
//     }

//     static <U> boolean isDone2(CompletionStage<U> stage) {
//         return stage.toCompletableFuture().copy().isDone();
//     }

}
