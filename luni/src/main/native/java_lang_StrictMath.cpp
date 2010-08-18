/*
 * Copyright (C) 2006 The Android Open Source Project
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

#define LOG_TAG "StrictMath"

// We include this header file first, because it's unhygienic when it comes to checking whether
// things are already #defined. It's also missing the 'extern "C"', and leaves a bunch of stuff.
extern "C" {
#include "../../external/fdlibm/fdlibm.h"
}
// fdlibm.h #defines __P. glibc uses #undef itself to supply its own definition, but bionic
// assumes no-one's been polluting the namespace.
#undef __P

#include "jni.h"
#include "JNIHelp.h"
#include "JniConstants.h"

static jdouble StrictMath_sin(JNIEnv*, jclass, jdouble a) {
    return ieee_sin(a);
}

static jdouble StrictMath_cos(JNIEnv*, jclass, jdouble a) {
    return ieee_cos(a);
}

static jdouble StrictMath_tan(JNIEnv*, jclass, jdouble a) {
    return ieee_tan(a);
}

static jdouble StrictMath_asin(JNIEnv*, jclass, jdouble a) {
    return ieee_asin(a);
}

static jdouble StrictMath_acos(JNIEnv*, jclass, jdouble a) {
    return ieee_acos(a);
}

static jdouble StrictMath_atan(JNIEnv*, jclass, jdouble a) {
    return ieee_atan(a);
}

static jdouble StrictMath_exp(JNIEnv*, jclass, jdouble a) {
    return ieee_exp(a);
}

static jdouble StrictMath_log(JNIEnv*, jclass, jdouble a) {
    return ieee_log(a);
}

static jdouble StrictMath_sqrt2(JNIEnv*, jclass, jdouble a) {
    return ieee_sqrt(a);
}

static jdouble StrictMath_IEEEremainder(JNIEnv*, jclass, jdouble a, jdouble b) {
    return ieee_remainder(a, b);
}

static jdouble StrictMath_floor(JNIEnv*, jclass, jdouble a) {
    return ieee_floor(a);
}

static jdouble StrictMath_ceil(JNIEnv*, jclass, jdouble a) {
    return ieee_ceil(a);
}

static jdouble StrictMath_rint(JNIEnv*, jclass, jdouble a) {
    return ieee_rint(a);
}

static jdouble StrictMath_atan2(JNIEnv*, jclass, jdouble a, jdouble b) {
    return ieee_atan2(a, b);
}

static jdouble StrictMath_pow(JNIEnv*, jclass, jdouble a, jdouble b) {
    return ieee_pow(a,b);
}

static jdouble StrictMath_sinh(JNIEnv*, jclass, jdouble a) {
    return ieee_sinh(a);
}

static jdouble StrictMath_tanh(JNIEnv*, jclass, jdouble a) {
    return ieee_tanh(a);
}

static jdouble StrictMath_cosh(JNIEnv*, jclass, jdouble a) {
    return ieee_cosh(a);
}

static jdouble StrictMath_log10(JNIEnv*, jclass, jdouble a) {
    return ieee_log10(a);
}

static jdouble StrictMath_cbrt(JNIEnv*, jclass, jdouble a) {
    return ieee_cbrt(a);
}

static jdouble StrictMath_expm1(JNIEnv*, jclass, jdouble a) {
    return ieee_expm1(a);
}

static jdouble StrictMath_hypot(JNIEnv*, jclass, jdouble a, jdouble b) {
    return ieee_hypot(a, b);
}

static jdouble StrictMath_log1p(JNIEnv*, jclass, jdouble a) {
    return ieee_log1p(a);
}

static jdouble StrictMath_nextafter(JNIEnv*, jclass, jdouble a, jdouble b) {
    return ieee_nextafter(a, b);
}

extern jint Float_floatToRawBits(JNIEnv*, jclass, jfloat);
extern jfloat Float_intBitsToFloat(JNIEnv*, jclass, jint val);

// TODO: we should make Float.floatToRawBits and Float.intBitsToFloat intrinsics, and move
// this kind of code into Java.
static jfloat StrictMath_nextafterf(JNIEnv*, jclass, jfloat arg1, jfloat arg2) {
    jint hx = Float_floatToRawBits(NULL, NULL, arg1);
    jint hy = Float_floatToRawBits(NULL, NULL, arg2);

    if (!(hx & 0x7fffffff)) { /* arg1 == 0 */
        return Float_intBitsToFloat(NULL, NULL, (hy & 0x80000000) | 0x1);
    }

    if ((hx > 0) ^ (hx > hy)) { /* |arg1| < |arg2| */
        hx += 1;
    } else {
        hx -= 1;
    }
    return Float_intBitsToFloat(NULL, NULL, hx);
}

static JNINativeMethod gMethods[] = {
    { "IEEEremainder", "(DD)D", (void*)StrictMath_IEEEremainder },
    { "acos",          "(D)D",  (void*)StrictMath_acos },
    { "asin",          "(D)D",  (void*)StrictMath_asin },
    { "atan",          "(D)D",  (void*)StrictMath_atan },
    { "atan2",         "(DD)D", (void*)StrictMath_atan2 },
    { "cbrt",          "(D)D",  (void*)StrictMath_cbrt },
    { "ceil",          "(D)D",  (void*)StrictMath_ceil },
    { "cos",           "(D)D",  (void*)StrictMath_cos },
    { "cosh",          "(D)D",  (void*)StrictMath_cosh },
    { "exp",           "(D)D",  (void*)StrictMath_exp },
    { "expm1",         "(D)D",  (void*)StrictMath_expm1 },
    { "floor",         "(D)D",  (void*)StrictMath_floor },
    { "hypot",         "(DD)D", (void*)StrictMath_hypot },
    { "log",           "(D)D",  (void*)StrictMath_log },
    { "log10",         "(D)D",  (void*)StrictMath_log10 },
    { "log1p",         "(D)D",  (void*)StrictMath_log1p },
    { "nextafter",     "(DD)D", (void*)StrictMath_nextafter },
    { "nextafterf",    "(FF)F", (void*)StrictMath_nextafterf },
    { "pow",           "(DD)D", (void*)StrictMath_pow },
    { "rint",          "(D)D",  (void*)StrictMath_rint },
    { "sin",           "(D)D",  (void*)StrictMath_sin },
    { "sinh",          "(D)D",  (void*)StrictMath_sinh },
    { "sqrt",          "(D)D",  (void*)StrictMath_sqrt2 },
    { "tan",           "(D)D",  (void*)StrictMath_tan },
    { "tanh",          "(D)D",  (void*)StrictMath_tanh },
};

int register_java_lang_StrictMath(JNIEnv* env) {
    return jniRegisterNativeMethods(env, "java/lang/StrictMath", gMethods, NELEM(gMethods));
}
