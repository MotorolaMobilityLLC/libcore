/*
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

/*
 * Native method support for java.util.zip.ZipFile
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <ctype.h>
#include <assert.h>
#include "JNIHelp.h"
#include "jlong.h"
#include "jvm.h"
#include "jni.h"
#include "jni_util.h"
#include "zip_util.h"
#ifdef WIN32
#include "io_util_md.h"
#else
#include "io_util.h"
#endif

#include "java_util_zip_ZipFile.h"

#define NATIVE_METHOD(className, functionName, signature) \
{ #functionName, signature, (void*)(className ## _ ## functionName) }

#define DEFLATED 8
#define STORED 0

static jfieldID jzfileID;

static int OPEN_READ = java_util_zip_ZipFile_OPEN_READ;
static int OPEN_DELETE = java_util_zip_ZipFile_OPEN_DELETE;

JNIEXPORT void JNICALL
ZipFile_initIDs(JNIEnv *env, jclass cls)
{
    jzfileID = (*env)->GetFieldID(env, cls, "jzfile", "J");
    assert(jzfileID != 0);
}

static void
ThrowZipException(JNIEnv *env, const char *msg)
{
    jstring s = NULL;
    jobject x;

    if (msg != NULL) {
        s = JNU_NewStringPlatform(env, msg);
    }
    x = JNU_NewObjectByName(env,
                            "java/util/zip/ZipException",
                            "(Ljava/lang/String;)V", s);
    if (x != NULL) {
        (*env)->Throw(env, x);
    }
}

JNIEXPORT jlong JNICALL
ZipFile_open(JNIEnv *env, jclass cls, jstring name,
                                        jint mode, jlong lastModified,
                                        jboolean usemmap)
{
    const char *path = JNU_GetStringPlatformChars(env, name, 0);
    char *msg = 0;
    jlong result = 0;
    int flag = 0;
    jzfile *zip = 0;

    if (mode & OPEN_READ) flag |= O_RDONLY;
    if (mode & OPEN_DELETE) flag |= JVM_O_DELETE;

    if (path != 0) {
        zip = ZIP_Get_From_Cache(path, &msg, lastModified);
        if (zip == 0 && msg == 0) {
            ZFILE zfd = 0;
#ifdef WIN32
            zfd = winFileHandleOpen(env, name, flag);
            if (zfd == -1) {
                /* Exception already pending. */
                goto finally;
            }
#else
            zfd = JVM_Open(path, flag, 0);
            if (zfd < 0) {
                throwFileNotFoundException(env, name);
                goto finally;
            }
#endif
            zip = ZIP_Put_In_Cache0(path, zfd, &msg, lastModified, usemmap);
        }

        if (zip != 0) {
            result = ptr_to_jlong(zip);
        } else if (msg != 0) {
            ThrowZipException(env, msg);
            free(msg);
        } else if (errno == ENOMEM) {
            JNU_ThrowOutOfMemoryError(env, 0);
        } else {
            ThrowZipException(env, "error in opening zip file");
        }
finally:
        JNU_ReleaseStringPlatformChars(env, name, path);
    }
    return result;
}

JNIEXPORT jint JNICALL
ZipFile_getTotal(JNIEnv *env, jclass cls, jlong zfile)
{
    jzfile *zip = jlong_to_ptr(zfile);

    return zip->total;
}

JNIEXPORT jboolean JNICALL
ZipFile_startsWithLOC(JNIEnv *env, jclass cls, jlong zfile)
{
    jzfile *zip = jlong_to_ptr(zfile);

    return zip->locsig;
}

JNIEXPORT void JNICALL
ZipFile_close(JNIEnv *env, jclass cls, jlong zfile)
{
    ZIP_Close(jlong_to_ptr(zfile));
}

JNIEXPORT jlong JNICALL
ZipFile_getEntry(JNIEnv *env, jclass cls, jlong zfile,
                 jbyteArray name, jboolean addSlash)
{
#define MAXNAME 1024
    jzfile *zip = jlong_to_ptr(zfile);
    jsize ulen = (*env)->GetArrayLength(env, name);
    char buf[MAXNAME+2], *path;
    jzentry *ze;

    if (ulen > MAXNAME) {
        path = malloc(ulen + 2);
        if (path == 0) {
            JNU_ThrowOutOfMemoryError(env, 0);
            return 0;
        }
    } else {
        path = buf;
    }
    (*env)->GetByteArrayRegion(env, name, 0, ulen, (jbyte *)path);
    path[ulen] = '\0';
    if (addSlash == JNI_FALSE) {
        ze = ZIP_GetEntry(zip, path, 0);
    } else {
        ze = ZIP_GetEntry(zip, path, (jint)ulen);
    }
    if (path != buf) {
        free(path);
    }
    return ptr_to_jlong(ze);
}

JNIEXPORT void JNICALL
ZipFile_freeEntry(JNIEnv *env, jclass cls, jlong zfile,
                                    jlong zentry)
{
    jzfile *zip = jlong_to_ptr(zfile);
    jzentry *ze = jlong_to_ptr(zentry);
    ZIP_FreeEntry(zip, ze);
}

JNIEXPORT jlong JNICALL
ZipFile_getNextEntry(JNIEnv *env, jclass cls, jlong zfile,
                                        jint n)
{
    jzentry *ze = ZIP_GetNextEntry(jlong_to_ptr(zfile), n);
    return ptr_to_jlong(ze);
}

JNIEXPORT jint JNICALL
ZipFile_getEntryMethod(JNIEnv *env, jclass cls, jlong zentry)
{
    jzentry *ze = jlong_to_ptr(zentry);
    return ze->csize != 0 ? DEFLATED : STORED;
}

JNIEXPORT jint JNICALL
ZipFile_getEntryFlag(JNIEnv *env, jclass cls, jlong zentry)
{
    jzentry *ze = jlong_to_ptr(zentry);
    return ze->flag;
}

JNIEXPORT jlong JNICALL
ZipFile_getEntryCSize(JNIEnv *env, jclass cls, jlong zentry)
{
    jzentry *ze = jlong_to_ptr(zentry);
    return ze->csize != 0 ? ze->csize : ze->size;
}

JNIEXPORT jlong JNICALL
ZipFile_getEntrySize(JNIEnv *env, jclass cls, jlong zentry)
{
    jzentry *ze = jlong_to_ptr(zentry);
    return ze->size;
}

JNIEXPORT jlong JNICALL
ZipFile_getEntryTime(JNIEnv *env, jclass cls, jlong zentry)
{
    jzentry *ze = jlong_to_ptr(zentry);
    return (jlong)ze->time & 0xffffffffUL;
}

JNIEXPORT jlong JNICALL
ZipFile_getEntryCrc(JNIEnv *env, jclass cls, jlong zentry)
{
    jzentry *ze = jlong_to_ptr(zentry);
    return (jlong)ze->crc & 0xffffffffUL;
}

JNIEXPORT jbyteArray JNICALL
ZipFile_getCommentBytes(JNIEnv *env, jclass cls, jlong zfile)
{
    jzfile *zip = jlong_to_ptr(zfile);
    jbyteArray jba = NULL;

    if (zip->comment != NULL) {
        if ((jba = (*env)->NewByteArray(env, zip->clen)) == NULL)
            return NULL;
        (*env)->SetByteArrayRegion(env, jba, 0, zip->clen, (jbyte*)zip->comment);
    }
    return jba;
}

JNIEXPORT jbyteArray JNICALL
ZipFile_getEntryBytes(JNIEnv *env, jclass cls, jlong zentry, jint type)
{
    jzentry *ze = jlong_to_ptr(zentry);
    int len = 0;
    jbyteArray jba = NULL;
    switch (type) {
    case java_util_zip_ZipFile_JZENTRY_NAME:
        if (ze->name != 0) {
            len = (int)strlen(ze->name);
            if (len == 0 || (jba = (*env)->NewByteArray(env, len)) == NULL)
                break;
            (*env)->SetByteArrayRegion(env, jba, 0, len, (jbyte *)ze->name);
        }
        break;
    case java_util_zip_ZipFile_JZENTRY_EXTRA:
        if (ze->extra != 0) {
            unsigned char *bp = (unsigned char *)&ze->extra[0];
            len = (bp[0] | (bp[1] << 8));
            if (len <= 0 || (jba = (*env)->NewByteArray(env, len)) == NULL)
                break;
            (*env)->SetByteArrayRegion(env, jba, 0, len, &ze->extra[2]);
        }
        break;
    case java_util_zip_ZipFile_JZENTRY_COMMENT:
        if (ze->comment != 0) {
            len = (int)strlen(ze->comment);
            if (len == 0 || (jba = (*env)->NewByteArray(env, len)) == NULL)
                break;
            (*env)->SetByteArrayRegion(env, jba, 0, len, (jbyte*)ze->comment);
        }
        break;
    }
    return jba;
}

JNIEXPORT jint JNICALL
ZipFile_read(JNIEnv *env, jclass cls, jlong zfile,
             jlong zentry, jlong pos, jbyteArray bytes,
             jint off, jint len)
{
    jzfile *zip = jlong_to_ptr(zfile);
    char *msg;

#define BUFSIZE 8192
    /* copy via tmp stack buffer: */
    jbyte buf[BUFSIZE];

    if (len > BUFSIZE) {
        len = BUFSIZE;
    }

    ZIP_Lock(zip);
    len = ZIP_Read(zip, jlong_to_ptr(zentry), pos, buf, len);
    msg = zip->msg;
    ZIP_Unlock(zip);
    if (len != -1) {
        (*env)->SetByteArrayRegion(env, bytes, off, len, buf);
    }

    if (len == -1) {
        if (msg != 0) {
            ThrowZipException(env, msg);
        } else {
            char errmsg[128];
            sprintf(errmsg, "errno: %d, error: %s\n",
                    errno, "Error reading ZIP file");
            JNU_ThrowIOExceptionWithLastError(env, errmsg);
        }
    }

    return len;
}

JNIEXPORT jstring JNICALL
ZipFile_getZipMessage(JNIEnv *env, jclass cls, jlong zfile)
{
    jzfile *zip = jlong_to_ptr(zfile);
    char *msg = zip->msg;
    if (msg == NULL) {
        return NULL;
    }
    return JNU_NewStringPlatform(env, msg);
}

static JNINativeMethod gMethods[] = {
  NATIVE_METHOD(ZipFile, initIDs, "()V"),
  NATIVE_METHOD(ZipFile, getEntry, "(J[BZ)J"),
  NATIVE_METHOD(ZipFile, freeEntry, "(JJ)V"),
  NATIVE_METHOD(ZipFile, getNextEntry, "(JI)J"),
  NATIVE_METHOD(ZipFile, close, "(J)V"),
  NATIVE_METHOD(ZipFile, open, "(Ljava/lang/String;IJZ)J"),
  NATIVE_METHOD(ZipFile, getTotal, "(J)I"),
  NATIVE_METHOD(ZipFile, startsWithLOC, "(J)Z"),
  NATIVE_METHOD(ZipFile, read, "(JJJ[BII)I"),
  NATIVE_METHOD(ZipFile, getEntryTime, "(J)J"),
  NATIVE_METHOD(ZipFile, getEntryCrc, "(J)J"),
  NATIVE_METHOD(ZipFile, getEntryCSize, "(J)J"),
  NATIVE_METHOD(ZipFile, getEntrySize, "(J)J"),
  NATIVE_METHOD(ZipFile, getEntryMethod, "(J)I"),
  NATIVE_METHOD(ZipFile, getEntryFlag, "(J)I"),
  NATIVE_METHOD(ZipFile, getCommentBytes, "(J)[B"),
  NATIVE_METHOD(ZipFile, getEntryBytes, "(JI)[B"),
  NATIVE_METHOD(ZipFile, getZipMessage, "(J)Ljava/lang/String;"),
};

void register_java_util_zip_ZipFile(JNIEnv* env) {
  jniRegisterNativeMethods(env, "java/util/zip/ZipFile", gMethods, NELEM(gMethods));
}
