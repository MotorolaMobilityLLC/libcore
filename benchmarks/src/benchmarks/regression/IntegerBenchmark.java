/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package benchmarks.regression;

public class IntegerBenchmark {
    public int timeLongSignumBranch(int reps) {
        int t = 0;
        for (int i = 0; i < reps; ++i) {
            t += signum1(-i);
            t += signum1(0);
            t += signum1(i);
        }
        return t;
    }

    public int timeLongSignumBranchFree(int reps) {
        int t = 0;
        for (int i = 0; i < reps; ++i) {
            t += signum2(-i);
            t += signum2(0);
            t += signum2(i);
        }
        return t;
    }

    private static int signum1(long v) {
        return v < 0 ? -1 : (v == 0 ? 0 : 1);
    }

    private static int signum2(long v) {
        return ((int)(v >> 63)) | (int) (-v >>> 63); // Hacker's delight 2-7
    }

    public int timeLongBitCount_BitSet(int reps) {
        int t = 0;
        for (int i = 0; i < reps; ++i) {
            t += pop((long) i);
        }
        return t;
    }

    private static int pop(long l) {
        int count = popX(l & 0xffffffffL);
        count += popX(l >>> 32);
        return count;
    }

    private static int popX(long x) {
        // BEGIN android-note
        // delegate to Integer.bitCount(i); consider using native code
        // END android-note
        x = x - ((x >>> 1) & 0x55555555);
        x = (x & 0x33333333) + ((x >>> 2) & 0x33333333);
        x = (x + (x >>> 4)) & 0x0f0f0f0f;
        x = x + (x >>> 8);
        x = x + (x >>> 16);
        return (int) x & 0x0000003f;
    }

    public int timeLongBitCount_2Int(int reps) {
        int t = 0;
        for (int i = 0; i < reps; ++i) {
            t += pop2((long) i);
        }
        return t;
    }

    private static int pop2(long l) {
        int count = Integer.bitCount((int) (l & 0xffffffffL));
        count += Integer.bitCount((int) (l >>> 32));
        return count;
    }

    public int timeLongBitCount_Long(int reps) {
        int t = 0;
        for (int i = 0; i < reps; ++i) {
            t += Long.bitCount((long) i);
        }
        return t;
    }

    /**
     * Table for Seal's algorithm for Number of Trailing Zeros. Hacker's Delight
     * online, Figure 5-18 (http://www.hackersdelight.org/revisions.pdf)
     * The entries whose value is -1 are never referenced.
     */
    private static final byte[] NTZ_TABLE = {
        32,  0,  1, 12,  2,  6, -1, 13,   3, -1,  7, -1, -1, -1, -1, 14,
        10,  4, -1, -1,  8, -1, -1, 25,  -1, -1, -1, -1, -1, 21, 27, 15,
        31, 11,  5, -1, -1, -1, -1, -1,   9, -1, -1, 24, -1, -1, 20, 26,
        30, -1, -1, -1, -1, 23, -1, 19,  29, -1, 22, 18, 28, 17, 16, -1
    };

    private static int numberOfTrailingZerosHD(int i) {
        // Seal's algorithm - Hacker's Delight 5-18
        i &= -i;
        i = (i <<  4) + i;    // x *= 17
        i = (i <<  6) + i;    // x *= 65
        i = (i << 16) - i;    // x *= 65535
        return NTZ_TABLE[i >>> 26];
    }

    private static int numberOfTrailingZerosOL(int i) {
        return NTZ_TABLE[((i & -i) * 0x0450FBAF) >>> 26];
    }

    public int timeNumberOfTrailingZerosHD(int reps) {
        int t = 0;
        for (int i = 0; i < reps; ++i) {
            t += numberOfTrailingZerosHD(i);
        }
        return t;
    }

    public int timeNumberOfTrailingZerosOL(int reps) {
        int t = 0;
        for (int i = 0; i < reps; ++i) {
            t += numberOfTrailingZerosOL(i);
        }
        return t;
    }

    public int timeIntegerValueOf(int reps) throws Exception {
        String[] intStrings = new String[]{"0", "1", "12", "123", "1234", "12345",
                                           "123456", "1234567", "12345678"};
        int t = 0;
        for (int i = 0; i < reps; ++i) {
            for (int j = 0; j < intStrings.length; ++j) {
                t += Integer.valueOf(intStrings[j]);
            }
        }
        return t;
    }
}
