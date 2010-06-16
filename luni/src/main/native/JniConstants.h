/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef JNI_CONSTANTS_H_included
#define JNI_CONSTANTS_H_included

#include "JNIHelp.h"

struct JniConstants {
    static void init(JNIEnv* env);

    static jclass bidiRunClass;
    static jclass bigDecimalClass;
    static jclass booleanClass;
    static jclass byteClass;
    static jclass byteArrayClass;
    static jclass charsetICUClass;
    static jclass constructorClass;
    static jclass datagramPacketClass;
    static jclass deflaterClass;
    static jclass doubleClass;
    static jclass fieldClass;
    static jclass fieldPositionIteratorClass;
    static jclass fileDescriptorClass;
    static jclass genericIPMreqClass;
    static jclass inetAddressClass;
    static jclass inflaterClass;
    static jclass integerClass;
    static jclass interfaceAddressClass;
    static jclass localeDataClass;
    static jclass longClass;
    static jclass methodClass;
    static jclass parsePositionClass;
    static jclass patternSyntaxExceptionClass;
    static jclass socketClass;
    static jclass socketImplClass;
    static jclass stringClass;
    static jclass vmRuntimeClass;
};

#endif  // JNI_CONSTANTS_H_included
