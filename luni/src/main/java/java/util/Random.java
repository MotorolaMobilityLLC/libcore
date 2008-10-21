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

package java.util;


import java.io.Serializable;

/**
 * This class provides methods that generates pseudo-random numbers of different
 * types, such as int, long, double and float using either
 * 
 * @see Properties
 * @see PropertyResourceBundle
 */
public class Random implements Serializable {
    
    private static final long serialVersionUID = 3905348978240129619L;

    static final long multiplier = 0x5deece66dL;

    /**
     * The boolean value indicating if the second Gaussian number is available.
     * 
     * @serial
     */
    boolean haveNextNextGaussian = false;

    /**
     * @serial It is associated with the internal state of this generator.
     */
    long seed;

    /**
     * The second Gaussian generated number.
     * 
     * @serial
     */
    double nextNextGaussian = 0;

    /**
     * Construct a random generator with the current time of day in milliseconds
     * as the initial state.
     * 
     * @see #setSeed
     */
    public Random() {
        setSeed(System.currentTimeMillis());
    }

    /**
     * Construct a random generator with the given <code>seed</code> as the
     * initial state.
     * 
     * @param seed
     *            the seed that will determine the initial state of this random
     *            number generator
     * 
     * @see #setSeed
     */
    public Random(long seed) {
        setSeed(seed);
    }

    /**
     * Returns a pseudo-random uniformly distributed <code>int</code> value of
     * the number of bits specified by the argument <code>bits</code> as
     * described by Donald E. Knuth in <i>The Art of Computer Programming,
     * Volume 2: Seminumerical Algorithms</i>, section 3.2.1.
     * 
     * @return int a pseudo-random generated int number
     * @param bits
     *            number of bits of the returned value
     * 
     * @see #nextBytes
     * @see #nextDouble
     * @see #nextFloat
     * @see #nextInt()
     * @see #nextInt(int)
     * @see #nextGaussian
     * @see #nextLong
     */
    protected synchronized int next(int bits) {
        seed = (seed * multiplier + 0xbL) & ((1L << 48) - 1);
        return (int) (seed >>> (48 - bits));
    }

    /**
     * Returns the next pseudo-random, uniformly distributed boolean value
     * generated by this generator.
     * 
     * @return boolean a pseudo-random, uniformly distributed boolean value
     */
    public boolean nextBoolean() {
        return next(1) != 0;
    }

    /**
     * Modifies the byte array by a random sequence of bytes generated by this
     * random number generator.
     * 
     * @param buf
     *            non-null array to contain the new random bytes
     * 
     * @see #next
     */
    public void nextBytes(byte[] buf) {
        int rand = 0, count = 0, loop = 0;
        while (count < buf.length) {
            if (loop == 0) {
                rand = nextInt();
                loop = 3;
            } else {
                loop--;
            }
            buf[count++] = (byte) rand;
            rand >>= 8;
        }
    }

    /**
     * Generates a normally distributed random double number between 0.0
     * inclusively and 1.0 exclusively.
     * 
     * @return double
     * 
     * @see #nextFloat
     */
    public double nextDouble() {
        return ((((long) next(26) << 27) + next(27)) / (double) (1L << 53));
    }

    /**
     * Generates a normally distributed random float number between 0.0
     * inclusively and 1.0 exclusively.
     * 
     * @return float a random float number between 0.0 and 1.0
     * 
     * @see #nextDouble
     */
    public float nextFloat() {
        return (next(24) / 16777216f);
    }

    /**
     * pseudo-randomly generates (approximately) a normally distributed
     * <code>double</code> value with mean 0.0 and a standard deviation value
     * of <code>1.0</code> using the <i>polar method<i> of G. E. P. Box, M.
     * E. Muller, and G. Marsaglia, as described by Donald E. Knuth in <i>The
     * Art of Computer Programming, Volume 2: Seminumerical Algorithms</i>,
     * section 3.4.1, subsection C, algorithm P
     * 
     * @return double
     * 
     * @see #nextDouble
     */
    public synchronized double nextGaussian() {
        if (haveNextNextGaussian) { // if X1 has been returned, return the
                                    // second Gaussian
            haveNextNextGaussian = false;
            return nextNextGaussian;
        }
        
        double v1, v2, s;
        do {
            v1 = 2 * nextDouble() - 1; // Generates two independent random
                                        // variables U1, U2
            v2 = 2 * nextDouble() - 1;
            s = v1 * v1 + v2 * v2;
        } while (s >= 1);
        double norm = Math.sqrt(-2 * Math.log(s) / s);
        nextNextGaussian = v2 * norm; // should that not be norm instead
                                        // of multiplier ?
        haveNextNextGaussian = true;
        return v1 * norm; // should that not be norm instead of multiplier
                            // ?
    }

    /**
     * Generates a uniformly distributed 32-bit <code>int</code> value from
     * the this random number sequence.
     * 
     * @return int uniformly distributed <code>int</code> value
     * 
     * @see java.lang.Integer#MAX_VALUE
     * @see java.lang.Integer#MIN_VALUE
     * @see #next
     * @see #nextLong
     */
    public int nextInt() {
        return next(32);
    }

    /**
     * Returns to the caller a new pseudo-random integer value which is uniformly
     * distributed between 0 (inclusively) and the value of <code>n</code>
     * (exclusively).
     * 
     * @return int
     * @param n
     *            int
     */
    public int nextInt(int n) {
        if (n > 0) {
            if ((n & -n) == n) {
                return (int) ((n * (long) next(31)) >> 31);
            }
            int bits, val;
            do {
                bits = next(31);
                val = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Generates a uniformly distributed 64-bit <code>int</code> value from
     * the this random number sequence.
     * 
     * @return 64-bit <code>int</code> random number
     * 
     * @see java.lang.Integer#MAX_VALUE
     * @see java.lang.Integer#MIN_VALUE
     * @see #next
     * @see #nextInt()
     * @see #nextInt(int)
     */
    public long nextLong() {
        return ((long) next(32) << 32) + next(32);
    }

    /**
     * Modifies the seed using linear congruential formula presented in <i>The
     * Art of Computer Programming, Volume 2</i>, Section 3.2.1.
     * 
     * @param seed
     *            the seed that alters the state of the random number generator
     * 
     * @see #next
     * @see #Random()
     * @see #Random(long)
     */
    public synchronized void setSeed(long seed) {
        this.seed = (seed ^ multiplier) & ((1L << 48) - 1);
        haveNextNextGaussian = false;
    }
}
