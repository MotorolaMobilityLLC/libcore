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

/*
 * @test
 * @bug 8028207
 * @summary Verify that GVN doesn't mess up the two addExacts
 * @compile GVNTest.java
 * @run main GVNTest
 *
 */

public class GVNTest {
  public static int result = 0;
  public static int value = 93;
  public static void main(String[] args) {
    for (int i = 0; i < 50000; ++i) {
      result = runTest(value + i);
      result = runTest(value + i);
      result = runTest(value + i);
      result = runTest(value + i);
      result = runTest(value + i);
    }
  }

  public static int runTest(int value) {
    int v = value + value;
    int sum = 0;
    if (v < 4032) {
      for (int i = 0; i < 1023; ++i) {
        sum += Math.addExact(value, value);
      }
    } else {
      for (int i = 0; i < 321; ++i) {
        sum += Math.addExact(value, value);
      }
    }
    return sum + v;
  }
}
