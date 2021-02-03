/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

/**
 * @test
 * @bug 8031695
 * @summary CHA ignores default methods during analysis leading to incorrect code generation
 *
 * @run main/othervm -Xbatch DefaultAndConcreteMethodsCHA
 */
interface I {
    default int m() { return 0; }
}

class A implements I {}

class C extends A { }
class D extends A { public int m() { return 1; } }

public class DefaultAndConcreteMethodsCHA {
    public static int test(A obj) {
        return obj.m();
    }
    public static void main(String[] args) {
        for (int i = 0; i < 10000; i++) {
            int idC = test(new C());
            if (idC != 0) {
                throw new Error("C.m didn't invoke I.m: id "+idC);
            }

            int idD = test(new D());
            if (idD != 1) {
                throw new Error("D.m didn't invoke D.m: id "+idD);
            }
        }

    }
}
