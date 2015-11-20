/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

#include "jni.h"
#include "jni_util.h"
#include "jvm.h"

#include "java_lang_Shutdown.h"
#include "JNIHelp.h"

#define NATIVE_METHOD(className, functionName, signature) \
{ #functionName, signature, (void*)(className ## _ ## functionName) }


JNIEXPORT void JNICALL
Shutdown_halt0(JNIEnv *env, jclass ignored, jint code)
{
    JVM_Halt(code);
}


JNIEXPORT void JNICALL
Shutdown_runAllFinalizers(JNIEnv *env, jclass ignored)
{
    jclass cl;
    jmethodID mid;

    if ((cl = (*env)->FindClass(env, "java/lang/ref/Finalizer"))
        && (mid = (*env)->GetStaticMethodID(env, cl,
                                            "runAllFinalizers", "()V"))) {
        (*env)->CallStaticVoidMethod(env, cl, mid);
    }
}

static JNINativeMethod gMethods[] = {
  NATIVE_METHOD(Shutdown, halt0, "(I)V"),
  NATIVE_METHOD(Shutdown, runAllFinalizers, "()V"),
};

void register_java_lang_Shutdown(JNIEnv* env) {
  jniRegisterNativeMethods(env, "java/lang/Shutdown", gMethods, NELEM(gMethods));
}
