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
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SplittableRandomTest extends JSR166TestCase {

    public static void main(String[] args) {
        main(suite(), args);
    }
    public static Test suite() {
        return new TestSuite(SplittableRandomTest.class);
    }

    /*
     * Testing coverage notes:
     *
     * 1. Many of the test methods are adapted from ThreadLocalRandomTest.
     *
     * 2. These tests do not check for random number generator quality.
     * But we check for minimal API compliance by requiring that
     * repeated calls to nextX methods, up to NCALLS tries, produce at
     * least two distinct results. (In some possible universe, a
     * "correct" implementation might fail, but the odds are vastly
     * less than that of encountering a hardware failure while running
     * the test.) For bounded nextX methods, we sample various
     * intervals across multiples of primes. In other tests, we repeat
     * under REPS different values.
     */

    // max numbers of calls to detect getting stuck on one value
    static final int NCALLS = 10000;

    // max sampled int bound
    static final int MAX_INT_BOUND = (1 << 26);

    // max sampled long bound
    static final long MAX_LONG_BOUND = (1L << 40);

    // Number of replications for other checks
    static final int REPS =
        Integer.getInteger("SplittableRandomTest.reps", 4);

    /**
     * Repeated calls to nextInt produce at least two distinct results
     */
    public void testNextInt() {
        SplittableRandom sr = new SplittableRandom();
        int f = sr.nextInt();
        int i = 0;
        while (i < NCALLS && sr.nextInt() == f)
            ++i;
        assertTrue(i < NCALLS);
    }

    /**
     * Repeated calls to nextLong produce at least two distinct results
     */
    public void testNextLong() {
        SplittableRandom sr = new SplittableRandom();
        long f = sr.nextLong();
        int i = 0;
        while (i < NCALLS && sr.nextLong() == f)
            ++i;
        assertTrue(i < NCALLS);
    }

    /**
     * Repeated calls to nextDouble produce at least two distinct results
     */
    public void testNextDouble() {
        SplittableRandom sr = new SplittableRandom();
        double f = sr.nextDouble();
        int i = 0;
        while (i < NCALLS && sr.nextDouble() == f)
            ++i;
        assertTrue(i < NCALLS);
    }

    /**
     * Two SplittableRandoms created with the same seed produce the
     * same values for nextLong.
     */
    public void testSeedConstructor() {
        for (long seed = 2; seed < MAX_LONG_BOUND; seed += 15485863) {
            SplittableRandom sr1 = new SplittableRandom(seed);
            SplittableRandom sr2 = new SplittableRandom(seed);
            for (int i = 0; i < REPS; ++i)
                assertEquals(sr1.nextLong(), sr2.nextLong());
        }
    }

    /**
     * A SplittableRandom produced by split() of a default-constructed
     * SplittableRandom generates a different sequence
     */
    public void testSplit1() {
        SplittableRandom sr = new SplittableRandom();
        for (int reps = 0; reps < REPS; ++reps) {
            SplittableRandom sc = sr.split();
            int i = 0;
            while (i < NCALLS && sr.nextLong() == sc.nextLong())
                ++i;
            assertTrue(i < NCALLS);
        }
    }

    /**
     * A SplittableRandom produced by split() of a seeded-constructed
     * SplittableRandom generates a different sequence
     */
    public void testSplit2() {
        SplittableRandom sr = new SplittableRandom(12345);
        for (int reps = 0; reps < REPS; ++reps) {
            SplittableRandom sc = sr.split();
            int i = 0;
            while (i < NCALLS && sr.nextLong() == sc.nextLong())
                ++i;
            assertTrue(i < NCALLS);
        }
    }

    /**
     * nextInt(non-positive) throws IllegalArgumentException
     */
    public void testNextIntBoundNonPositive() {
        SplittableRandom sr = new SplittableRandom();
        Runnable[] throwingActions = {
            () -> sr.nextInt(-17),
            () -> sr.nextInt(0),
            () -> sr.nextInt(Integer.MIN_VALUE),
        };
        assertThrows(IllegalArgumentException.class, throwingActions);
    }

    /**
     * nextInt(least >= bound) throws IllegalArgumentException
     */
    public void testNextIntBadBounds() {
        SplittableRandom sr = new SplittableRandom();
        Runnable[] throwingActions = {
            () -> sr.nextInt(17, 2),
            () -> sr.nextInt(-42, -42),
            () -> sr.nextInt(Integer.MAX_VALUE, Integer.MIN_VALUE),
        };
        assertThrows(IllegalArgumentException.class, throwingActions);
    }

    /**
     * nextInt(bound) returns 0 <= value < bound;
     * repeated calls produce at least two distinct results
     */
    public void testNextIntBounded() {
        SplittableRandom sr = new SplittableRandom();
        for (int i = 0; i < 2; i++) assertEquals(0, sr.nextInt(1));
        // sample bound space across prime number increments
        for (int bound = 2; bound < MAX_INT_BOUND; bound += 524959) {
            int f = sr.nextInt(bound);
            assertTrue(0 <= f && f < bound);
            int i = 0;
            int j;
            while (i < NCALLS &&
                   (j = sr.nextInt(bound)) == f) {
                assertTrue(0 <= j && j < bound);
                ++i;
            }
            assertTrue(i < NCALLS);
        }
    }

    /**
     * nextInt(least, bound) returns least <= value < bound;
     * repeated calls produce at least two distinct results
     */
    public void testNextIntBounded2() {
        SplittableRandom sr = new SplittableRandom();
        for (int least = -15485863; least < MAX_INT_BOUND; least += 524959) {
            for (int bound = least + 2; bound > least && bound < MAX_INT_BOUND; bound += 49979687) {
                int f = sr.nextInt(least, bound);
                assertTrue(least <= f && f < bound);
                int i = 0;
                int j;
                while (i < NCALLS &&
                       (j = sr.nextInt(least, bound)) == f) {
                    assertTrue(least <= j && j < bound);
                    ++i;
                }
                assertTrue(i < NCALLS);
            }
        }
    }

    /**
     * nextLong(non-positive) throws IllegalArgumentException
     */
    public void testNextLongBoundNonPositive() {
        SplittableRandom sr = new SplittableRandom();
        Runnable[] throwingActions = {
            () -> sr.nextLong(-17L),
            () -> sr.nextLong(0L),
            () -> sr.nextLong(Long.MIN_VALUE),
        };
        assertThrows(IllegalArgumentException.class, throwingActions);
    }

    /**
     * nextLong(least >= bound) throws IllegalArgumentException
     */
    public void testNextLongBadBounds() {
        SplittableRandom sr = new SplittableRandom();
        Runnable[] throwingActions = {
            () -> sr.nextLong(17L, 2L),
            () -> sr.nextLong(-42L, -42L),
            () -> sr.nextLong(Long.MAX_VALUE, Long.MIN_VALUE),
        };
        assertThrows(IllegalArgumentException.class, throwingActions);
    }

    /**
     * nextLong(bound) returns 0 <= value < bound;
     * repeated calls produce at least two distinct results
     */
    public void testNextLongBounded() {
        SplittableRandom sr = new SplittableRandom();
        for (int i = 0; i < 2; i++) assertEquals(0L, sr.nextLong(1L));
        for (long bound = 2; bound < MAX_LONG_BOUND; bound += 15485863) {
            long f = sr.nextLong(bound);
            assertTrue(0 <= f && f < bound);
            int i = 0;
            long j;
            while (i < NCALLS &&
                   (j = sr.nextLong(bound)) == f) {
                assertTrue(0 <= j && j < bound);
                ++i;
            }
            assertTrue(i < NCALLS);
        }
    }

    /**
     * nextLong(least, bound) returns least <= value < bound;
     * repeated calls produce at least two distinct results
     */
    public void testNextLongBounded2() {
        SplittableRandom sr = new SplittableRandom();
        for (long least = -86028121; least < MAX_LONG_BOUND; least += 982451653L) {
            for (long bound = least + 2; bound > least && bound < MAX_LONG_BOUND; bound += Math.abs(bound * 7919)) {
                long f = sr.nextLong(least, bound);
                assertTrue(least <= f && f < bound);
                int i = 0;
                long j;
                while (i < NCALLS &&
                       (j = sr.nextLong(least, bound)) == f) {
                    assertTrue(least <= j && j < bound);
                    ++i;
                }
                assertTrue(i < NCALLS);
            }
        }
    }

    /**
     * nextDouble(non-positive) throws IllegalArgumentException
     */
    public void testNextDoubleBoundNonPositive() {
        SplittableRandom sr = new SplittableRandom();
        Runnable[] throwingActions = {
            () -> sr.nextDouble(-17.0d),
            () -> sr.nextDouble(0.0d),
            () -> sr.nextDouble(-Double.MIN_VALUE),
            () -> sr.nextDouble(Double.NEGATIVE_INFINITY),
            () -> sr.nextDouble(Double.NaN),
        };
        assertThrows(IllegalArgumentException.class, throwingActions);
    }

    /**
     * nextDouble(! (least < bound)) throws IllegalArgumentException
     */
    public void testNextDoubleBadBounds() {
        SplittableRandom sr = new SplittableRandom();
        Runnable[] throwingActions = {
            () -> sr.nextDouble(17.0d, 2.0d),
            () -> sr.nextDouble(-42.0d, -42.0d),
            () -> sr.nextDouble(Double.MAX_VALUE, Double.MIN_VALUE),
            () -> sr.nextDouble(Double.NaN, 0.0d),
            () -> sr.nextDouble(0.0d, Double.NaN),
        };
        assertThrows(IllegalArgumentException.class, throwingActions);
    }

    // TODO: Test infinite bounds!
    //() -> sr.nextDouble(Double.NEGATIVE_INFINITY, 0.0d),
    //() -> sr.nextDouble(0.0d, Double.POSITIVE_INFINITY),

    /**
     * nextDouble(least, bound) returns least <= value < bound;
     * repeated calls produce at least two distinct results
     */
    public void testNextDoubleBounded2() {
        SplittableRandom sr = new SplittableRandom();
        for (double least = 0.0001; least < 1.0e20; least *= 8) {
            for (double bound = least * 1.001; bound < 1.0e20; bound *= 16) {
                double f = sr.nextDouble(least, bound);
                assertTrue(least <= f && f < bound);
                int i = 0;
                double j;
                while (i < NCALLS &&
                       (j = sr.nextDouble(least, bound)) == f) {
                    assertTrue(least <= j && j < bound);
                    ++i;
                }
                assertTrue(i < NCALLS);
            }
        }
    }

    /**
     * Invoking sized ints, long, doubles, with negative sizes throws
     * IllegalArgumentException
     */
    public void testBadStreamSize() {
        SplittableRandom r = new SplittableRandom();
        Runnable[] throwingActions = {
            () -> { java.util.stream.IntStream x = r.ints(-1L); },
            () -> { java.util.stream.IntStream x = r.ints(-1L, 2, 3); },
            () -> { java.util.stream.LongStream x = r.longs(-1L); },
            () -> { java.util.stream.LongStream x = r.longs(-1L, -1L, 1L); },
            () -> { java.util.stream.DoubleStream x = r.doubles(-1L); },
            () -> { java.util.stream.DoubleStream x = r.doubles(-1L, .5, .6); },
        };
        assertThrows(IllegalArgumentException.class, throwingActions);
    }

    /**
     * Invoking bounded ints, long, doubles, with illegal bounds throws
     * IllegalArgumentException
     */
    public void testBadStreamBounds() {
        SplittableRandom r = new SplittableRandom();
        Runnable[] throwingActions = {
            () -> { java.util.stream.IntStream x = r.ints(2, 1); },
            () -> { java.util.stream.IntStream x = r.ints(10, 42, 42); },
            () -> { java.util.stream.LongStream x = r.longs(-1L, -1L); },
            () -> { java.util.stream.LongStream x = r.longs(10, 1L, -2L); },
            () -> { java.util.stream.DoubleStream x = r.doubles(0.0, 0.0); },
            () -> { java.util.stream.DoubleStream x = r.doubles(10, .5, .4); },
        };
        assertThrows(IllegalArgumentException.class, throwingActions);
    }

    /**
     * A parallel sized stream of ints generates the given number of values
     */
    public void testIntsCount() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 0;
        for (int reps = 0; reps < REPS; ++reps) {
            counter.reset();
            r.ints(size).parallel().forEach(x -> counter.increment());
            assertEquals(size, counter.sum());
            size += 524959;
        }
    }

    /**
     * A parallel sized stream of longs generates the given number of values
     */
    public void testLongsCount() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 0;
        for (int reps = 0; reps < REPS; ++reps) {
            counter.reset();
            r.longs(size).parallel().forEach(x -> counter.increment());
            assertEquals(size, counter.sum());
            size += 524959;
        }
    }

    /**
     * A parallel sized stream of doubles generates the given number of values
     */
    public void testDoublesCount() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 0;
        for (int reps = 0; reps < REPS; ++reps) {
            counter.reset();
            r.doubles(size).parallel().forEach(x -> counter.increment());
            assertEquals(size, counter.sum());
            size += 524959;
        }
    }

    /**
     * Each of a parallel sized stream of bounded ints is within bounds
     */
    public void testBoundedInts() {
        AtomicInteger fails = new AtomicInteger(0);
        SplittableRandom r = new SplittableRandom();
        long size = 12345L;
        for (int least = -15485867; least < MAX_INT_BOUND; least += 524959) {
            for (int bound = least + 2; bound > least && bound < MAX_INT_BOUND; bound += 67867967) {
                final int lo = least, hi = bound;
                r.ints(size, lo, hi).parallel().forEach(
                    x -> {
                        if (x < lo || x >= hi)
                            fails.getAndIncrement(); });
            }
        }
        assertEquals(0, fails.get());
    }

    /**
     * Each of a parallel sized stream of bounded longs is within bounds
     */
    public void testBoundedLongs() {
        AtomicInteger fails = new AtomicInteger(0);
        SplittableRandom r = new SplittableRandom();
        long size = 123L;
        for (long least = -86028121; least < MAX_LONG_BOUND; least += 1982451653L) {
            for (long bound = least + 2; bound > least && bound < MAX_LONG_BOUND; bound += Math.abs(bound * 7919)) {
                final long lo = least, hi = bound;
                r.longs(size, lo, hi).parallel().forEach(
                    x -> {
                        if (x < lo || x >= hi)
                            fails.getAndIncrement(); });
            }
        }
        assertEquals(0, fails.get());
    }

    /**
     * Each of a parallel sized stream of bounded doubles is within bounds
     */
    public void testBoundedDoubles() {
        AtomicInteger fails = new AtomicInteger(0);
        SplittableRandom r = new SplittableRandom();
        long size = 456;
        for (double least = 0.00011; least < 1.0e20; least *= 9) {
            for (double bound = least * 1.0011; bound < 1.0e20; bound *= 17) {
                final double lo = least, hi = bound;
                r.doubles(size, lo, hi).parallel().forEach(
                    x -> {
                        if (x < lo || x >= hi)
                            fails.getAndIncrement(); });
            }
        }
        assertEquals(0, fails.get());
    }

    /**
     * A parallel unsized stream of ints generates at least 100 values
     */
    public void testUnsizedIntsCount() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 100;
        r.ints().limit(size).parallel().forEach(x -> counter.increment());
        assertEquals(size, counter.sum());
    }

    /**
     * A parallel unsized stream of longs generates at least 100 values
     */
    public void testUnsizedLongsCount() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 100;
        r.longs().limit(size).parallel().forEach(x -> counter.increment());
        assertEquals(size, counter.sum());
    }

    /**
     * A parallel unsized stream of doubles generates at least 100 values
     */
    public void testUnsizedDoublesCount() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 100;
        r.doubles().limit(size).parallel().forEach(x -> counter.increment());
        assertEquals(size, counter.sum());
    }

    /**
     * A sequential unsized stream of ints generates at least 100 values
     */
    public void testUnsizedIntsCountSeq() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 100;
        r.ints().limit(size).forEach(x -> counter.increment());
        assertEquals(size, counter.sum());
    }

    /**
     * A sequential unsized stream of longs generates at least 100 values
     */
    public void testUnsizedLongsCountSeq() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 100;
        r.longs().limit(size).forEach(x -> counter.increment());
        assertEquals(size, counter.sum());
    }

    /**
     * A sequential unsized stream of doubles generates at least 100 values
     */
    public void testUnsizedDoublesCountSeq() {
        LongAdder counter = new LongAdder();
        SplittableRandom r = new SplittableRandom();
        long size = 100;
        r.doubles().limit(size).forEach(x -> counter.increment());
        assertEquals(size, counter.sum());
    }

}
