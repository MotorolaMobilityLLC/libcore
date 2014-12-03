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
 *
 */

/**
 * @test
 * @bug 8054478
 * @summary dead backbranch in main loop results in erroneous array access
 * @run main/othervm -XX:CompileOnly=TestDeadBackbranchArrayAccess -Xcomp TestDeadBackbranchArrayAccess
 *
 */

public class TestDeadBackbranchArrayAccess {
    static char[] pattern0 = {0};
    static char[] pattern1 = {1};

    static void test(char[] array) {
        if (pattern1 == null) return;

        int i = 0;
        int pos = 0;
        char c = array[pos];

        while (i >= 0 && (c == pattern0[i] || c == pattern1[i])) {
            i--;
            pos--;
            if (pos != -1) {
                c = array[pos];
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000000; i++) {
            test(new char[1]);
        }
    }
}
