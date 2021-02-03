/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package sun.jvm.hotspot.debugger.windows.amd64;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.debugger.amd64.*;
import sun.jvm.hotspot.debugger.cdbg.*;
import sun.jvm.hotspot.debugger.cdbg.basic.*;
import sun.jvm.hotspot.debugger.windbg.*;

public class WindowsAMD64CFrame extends BasicCFrame {
  private Address rbp;
  private Address pc;

  private static final int ADDRESS_SIZE = 8;

  /** Constructor for topmost frame */
  public WindowsAMD64CFrame(WindbgDebugger dbg, Address rbp, Address pc) {
    super(dbg.getCDebugger());
    this.rbp = rbp;
    this.pc  = pc;
    this.dbg = dbg;
  }

  public CFrame sender(ThreadProxy thread) {
    AMD64ThreadContext context = (AMD64ThreadContext) thread.getContext();
    Address rsp = context.getRegisterAsAddress(AMD64ThreadContext.RSP);

    if ( (rbp == null) || rbp.lessThan(rsp) ) {
      return null;
    }

    // Check alignment of rbp
    if ( dbg.getAddressValue(rbp) % ADDRESS_SIZE != 0) {
        return null;
    }

    Address nextRBP = rbp.getAddressAt( 0 * ADDRESS_SIZE);
    if (nextRBP == null || nextRBP.lessThanOrEqual(rbp)) {
      return null;
    }
    Address nextPC  = rbp.getAddressAt( 1 * ADDRESS_SIZE);
    if (nextPC == null) {
      return null;
    }
    return new WindowsAMD64CFrame(dbg, nextRBP, nextPC);
  }

  public Address pc() {
    return pc;
  }

  public Address localVariableBase() {
    return rbp;
  }

  private WindbgDebugger dbg;
}
