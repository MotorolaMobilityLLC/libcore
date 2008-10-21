/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

#ifndef EXCEPTIONS_H
#define EXCEPTIONS_H

#include <string.h>
//#include "vmi.h"
#include "JNIHelp.h"

#ifdef __cplusplus
extern "C" {
#endif

void throwNewExceptionByName(JNIEnv* env,
                             const char* name, const char* message);
void throwNewOutOfMemoryError(JNIEnv* env, const char* message);
void throwJavaIoIOException(JNIEnv* env, const char* message);
void throwJavaIoIOExceptionClosed(JNIEnv* env);
void throwNPException(JNIEnv* env, const char* message);
void throwIndexOutOfBoundsException(JNIEnv* env);

#ifdef __cplusplus
}
#endif

#endif
