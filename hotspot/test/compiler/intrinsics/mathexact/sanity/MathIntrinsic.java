/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.Executable;
import java.util.concurrent.Callable;

public class MathIntrinsic {

    enum IntIntrinsic implements CompilerWhiteBoxTest.TestCase {
        Add {
            @Override
            Object execMathMethod() {
                return intR = Math.addExact(int1, int2);
            }
        },
        Subtract {
            @Override
            Object execMathMethod() {
                return intR = Math.subtractExact(int1, int2);
            }
        },
        Multiply {
            @Override
            Object execMathMethod() {
                return intR = Math.multiplyExact(int1, int2);
            }
        },
        Increment {
            @Override
            Object execMathMethod() {
                return intR = Math.incrementExact(int1);
            }
        },
        Decrement {
            @Override
            Object execMathMethod() {
                return intR = Math.decrementExact(int1);
            }
        },
        Negate {
            @Override
            Object execMathMethod() {
                return intR = Math.negateExact(int1);
            }
        };
        protected int int1;
        protected int int2;
        protected int intR;

        abstract Object execMathMethod();

        @Override
        public Executable getExecutable() {
            try {
                return getClass().getDeclaredMethod("execMathMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Test bug, no such method: " + e);
            }
        }

        @Override
        public Callable<Integer> getCallable() {
            return null;
        }

        @Override
        public boolean isOsr() {
            return false;
        }

    }

    enum LongIntrinsic implements CompilerWhiteBoxTest.TestCase {
        Add {
            @Override
            Object execMathMethod() {
                return longR = Math.addExact(long1, long2);
            }
        },
        Subtract {
            @Override
            Object execMathMethod() {
                return longR = Math.subtractExact(long1, long2);
            }
        },
        Multiply {
            @Override
            Object execMathMethod() {
                return longR = Math.multiplyExact(long1, long2);
            }
        },
        Increment {
            @Override
            Object execMathMethod() {
                return longR = Math.incrementExact(long1);
            }
        },
        Decrement {
            @Override
            Object execMathMethod() {
                return longR = Math.decrementExact(long1);
            }
        },
        Negate {
            @Override
            Object execMathMethod() {
                return longR = Math.negateExact(long1);
            }
        };
        protected long long1;
        protected long long2;
        protected long longR;

        abstract Object execMathMethod();

        @Override
        public Executable getExecutable() {
            try {
                return getClass().getDeclaredMethod("execMathMethod");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Test bug, no such method: " + e);
            }
        }

        @Override
        public Callable<Integer> getCallable() {
            return null;
        }

        @Override
        public boolean isOsr() {
            return false;
        }
    }
}
