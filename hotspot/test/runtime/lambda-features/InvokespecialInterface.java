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

/*
 * @test
 * @bug 8032024
 * @bug 8025937
 * @bug 8033528
 * @summary [JDK 8] Test invokespecial and invokeinterface with the same JVM_CONSTANT_InterfaceMethodref
 * @run main/othervm -XX:+StressRewriter InvokespecialInterface
 */
import java.util.function.*;
import java.util.*;

public class InvokespecialInterface {
interface I {
  default void imethod() { System.out.println("I::imethod"); }
}

static class C implements I {
  public void foo() { I.super.imethod(); }  // invokespecial InterfaceMethod
  public void bar() { I i = this; i.imethod(); } // invokeinterface same
  public void doSomeInvokedynamic() {
      String str = "world";
      Supplier<String> foo = ()->"hello, "+str;
      String res = foo.get();
      System.out.println(res);
  }
}

  public static void main(java.lang.String[] unused) {
     // need to create C and call I::foo()
     C c = new C();
     c.foo();
     c.bar();
     c.doSomeInvokedynamic();
  }
};


