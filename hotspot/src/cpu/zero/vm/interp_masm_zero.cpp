/*
 * Copyright (c) 1997, 2010, Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009 Red Hat, Inc.
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

#include "precompiled.hpp"
#include "interp_masm_zero.hpp"
#include "interpreter/interpreter.hpp"
#include "interpreter/interpreterRuntime.hpp"
#include "oops/arrayOop.hpp"
#include "oops/markOop.hpp"
#include "oops/methodDataOop.hpp"
#include "oops/methodOop.hpp"
#include "prims/jvmtiExport.hpp"
#include "prims/jvmtiRedefineClassesTrace.hpp"
#include "prims/jvmtiThreadState.hpp"
#include "runtime/basicLock.hpp"
#include "runtime/biasedLocking.hpp"
#include "runtime/sharedRuntime.hpp"
#ifdef TARGET_OS_FAMILY_linux
# include "thread_linux.inline.hpp"
#endif
#ifdef TARGET_OS_FAMILY_bsd
# include "thread_bsd.inline.hpp"
#endif

// This file is intentionally empty
