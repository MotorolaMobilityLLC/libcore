/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
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

provider hs_private {
  probe hashtable__new_entry(void*, uint32_t, uintptr_t, void*); 
  probe safepoint__begin();
  probe safepoint__end();
  probe cms__initmark__begin();
  probe cms__initmark__end();
  probe cms__remark__begin();
  probe cms__remark__end();
};

#pragma D attributes Private/Private/Common provider hs_private provider
#pragma D attributes Private/Private/Unknown provider hs_private module
#pragma D attributes Private/Private/Unknown provider hs_private function
#pragma D attributes Private/Private/Common provider hs_private name
#pragma D attributes Private/Private/Common provider hs_private args

