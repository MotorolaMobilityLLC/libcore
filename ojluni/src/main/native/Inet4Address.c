/*
 * Copyright (c) 2000, 2002, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

#include <string.h>

#include "java_net_Inet4Address.h"
#include "net_util.h"
#include "JNIHelp.h"

#define NATIVE_METHOD(className, functionName, signature) \
{ #functionName, signature, (void*)(className ## _ ## functionName) }

/************************************************************************
 * Inet4Address
 */
jclass ia4_class;
jmethodID ia4_ctrID;

/*
 * Class:     java_net_Inet4Address
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Inet4Address_init(JNIEnv *env, jclass cls) {
    jclass c = (*env)->FindClass(env, "java/net/Inet4Address");
    CHECK_NULL(c);
    ia4_class = (*env)->NewGlobalRef(env, c);
    CHECK_NULL(ia4_class);
    ia4_ctrID = (*env)->GetMethodID(env, ia4_class, "<init>", "()V");
    CHECK_NULL(ia4_ctrID);
}

static JNINativeMethod gMethods[] = {
  NATIVE_METHOD(Inet4Address, init, "()V"),
};

void register_java_net_Inet4Address(JNIEnv* env) {
  jniRegisterNativeMethods(env, "java/net/Inet4Address", gMethods, NELEM(gMethods));
}
