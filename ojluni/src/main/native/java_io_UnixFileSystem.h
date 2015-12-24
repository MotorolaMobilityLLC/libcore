/* This file was generated from java/io/UnixFileSystem.java and is licensed
 * under the same terms. The copyright and license information for
 * java/io/UnixFileSystem.java follows.
 *
 * Copyright (c) 1998, 2010, Oracle and/or its affiliates. All rights reserved.
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
/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class java_io_UnixFileSystem */

#ifndef _Included_java_io_UnixFileSystem
#define _Included_java_io_UnixFileSystem
#ifdef __cplusplus
extern "C" {
#endif
#undef java_io_UnixFileSystem_BA_EXISTS
#define java_io_UnixFileSystem_BA_EXISTS 1L
#undef java_io_UnixFileSystem_BA_REGULAR
#define java_io_UnixFileSystem_BA_REGULAR 2L
#undef java_io_UnixFileSystem_BA_DIRECTORY
#define java_io_UnixFileSystem_BA_DIRECTORY 4L
#undef java_io_UnixFileSystem_BA_HIDDEN
#define java_io_UnixFileSystem_BA_HIDDEN 8L
#undef java_io_UnixFileSystem_ACCESS_READ
#define java_io_UnixFileSystem_ACCESS_READ 4L
#undef java_io_UnixFileSystem_ACCESS_WRITE
#define java_io_UnixFileSystem_ACCESS_WRITE 2L
#undef java_io_UnixFileSystem_ACCESS_EXECUTE
#define java_io_UnixFileSystem_ACCESS_EXECUTE 1L
#undef java_io_UnixFileSystem_SPACE_TOTAL
#define java_io_UnixFileSystem_SPACE_TOTAL 0L
#undef java_io_UnixFileSystem_SPACE_FREE
#define java_io_UnixFileSystem_SPACE_FREE 1L
#undef java_io_UnixFileSystem_SPACE_USABLE
#define java_io_UnixFileSystem_SPACE_USABLE 2L
/*
 * Class:     java_io_UnixFileSystem
 * Method:    canonicalize0
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_java_io_UnixFileSystem_canonicalize0
  (JNIEnv *, jobject, jstring);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    getBooleanAttributes0
 * Signature: (Ljava/io/File;)I
 */
JNIEXPORT jint JNICALL Java_java_io_UnixFileSystem_getBooleanAttributes0
  (JNIEnv *, jobject, jobject);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    checkAccess
 * Signature: (Ljava/io/File;I)Z
 */
JNIEXPORT jboolean JNICALL Java_java_io_UnixFileSystem_checkAccess
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    getLastModifiedTime
 * Signature: (Ljava/io/File;)J
 */
JNIEXPORT jlong JNICALL Java_java_io_UnixFileSystem_getLastModifiedTime
  (JNIEnv *, jobject, jobject);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    getLength
 * Signature: (Ljava/io/File;)J
 */
JNIEXPORT jlong JNICALL Java_java_io_UnixFileSystem_getLength
  (JNIEnv *, jobject, jobject);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    setPermission
 * Signature: (Ljava/io/File;IZZ)Z
 */
JNIEXPORT jboolean JNICALL Java_java_io_UnixFileSystem_setPermission
  (JNIEnv *, jobject, jobject, jint, jboolean, jboolean);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    createFileExclusively
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_java_io_UnixFileSystem_createFileExclusively
  (JNIEnv *, jobject, jstring);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    delete0
 * Signature: (Ljava/io/File;)Z
 */
JNIEXPORT jboolean JNICALL Java_java_io_UnixFileSystem_delete0
  (JNIEnv *, jobject, jobject);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    list
 * Signature: (Ljava/io/File;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_java_io_UnixFileSystem_list
  (JNIEnv *, jobject, jobject);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    createDirectory
 * Signature: (Ljava/io/File;)Z
 */
JNIEXPORT jboolean JNICALL Java_java_io_UnixFileSystem_createDirectory
  (JNIEnv *, jobject, jobject);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    rename0
 * Signature: (Ljava/io/File;Ljava/io/File;)Z
 */
JNIEXPORT jboolean JNICALL Java_java_io_UnixFileSystem_rename0
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    setLastModifiedTime
 * Signature: (Ljava/io/File;J)Z
 */
JNIEXPORT jboolean JNICALL Java_java_io_UnixFileSystem_setLastModifiedTime
  (JNIEnv *, jobject, jobject, jlong);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    setReadOnly
 * Signature: (Ljava/io/File;)Z
 */
JNIEXPORT jboolean JNICALL Java_java_io_UnixFileSystem_setReadOnly
  (JNIEnv *, jobject, jobject);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    getSpace
 * Signature: (Ljava/io/File;I)J
 */
JNIEXPORT jlong JNICALL Java_java_io_UnixFileSystem_getSpace
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     java_io_UnixFileSystem
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_java_io_UnixFileSystem_initIDs
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
