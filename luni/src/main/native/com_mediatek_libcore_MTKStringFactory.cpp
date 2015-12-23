/* Copyright Statement:
 *
 * This software/firmware and related documentation ("MediaTek Software") are
 * protected under relevant copyright laws. The information contained herein
 * is confidential and proprietary to MediaTek Inc. and/or its licensors.
 * Without the prior written permission of MediaTek inc. and/or its licensors,
 * any reproduction, modification, use or disclosure of MediaTek Software,
 * and information contained herein, in whole or in part, shall be strictly prohibited.
 *
 * MediaTek Inc. (C) 2010. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("MEDIATEK SOFTWARE")
 * RECEIVED FROM MEDIATEK AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER ON
 * AN "AS-IS" BASIS ONLY. MEDIATEK EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.
 * NEITHER DOES MEDIATEK PROVIDE ANY WARRANTY WHATSOEVER WITH RESPECT TO THE
 * SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY, INCORPORATED IN, OR
 * SUPPLIED WITH THE MEDIATEK SOFTWARE, AND RECEIVER AGREES TO LOOK ONLY TO SUCH
 * THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO. RECEIVER EXPRESSLY ACKNOWLEDGES
 * THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES
 * CONTAINED IN MEDIATEK SOFTWARE. MEDIATEK SHALL ALSO NOT BE RESPONSIBLE FOR ANY MEDIATEK
 * SOFTWARE RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND MEDIATEK'S ENTIRE AND
 * CUMULATIVE LIABILITY WITH RESPECT TO THE MEDIATEK SOFTWARE RELEASED HEREUNDER WILL BE,
 * AT MEDIATEK'S OPTION, TO REVISE OR REPLACE THE MEDIATEK SOFTWARE AT ISSUE,
 * OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE CHARGE PAID BY RECEIVER TO
 * MEDIATEK FOR SUCH MEDIATEK SOFTWARE AT ISSUE.
 */

#define LOG_TAG "MTKStringFactory"
#include <nativehelper/JNIHelp.h>
#include <nativehelper/jni_macros.h>
#include "JniConstants.h"
#include "JniException.h"
#include "jni.h"


static jstring MTKStringFactory_newStringFromUTF8Bytes(JNIEnv* env, jclass, jbyteArray java_data,
                                                jint offset, jint byte_count) {
  // Local Define in here
  static const jchar REPLACEMENT_CHAR = 0xfffd;
  static const int DEFAULT_BUFFER_SIZE = 256;

  if (java_data == nullptr && offset == 0 && byte_count == 0) {
    jniThrowExceptionFmt(env, "java/lang/StringIndexOutOfBoundsException",
     "offset=%d; byte_count=%d", offset, byte_count);
    return nullptr;
  }

  jbyte* jRawArray = env->GetByteArrayElements(java_data, NULL);
  if (jRawArray != nullptr) {
    // Initial value
    jchar temp_buffer[DEFAULT_BUFFER_SIZE];
    jbyte* d = jRawArray;
    jchar* v;
    bool v_need_free = false;
    if (byte_count <= DEFAULT_BUFFER_SIZE) {
      v = temp_buffer;
    } else {
      v = new jchar[byte_count];
      v_need_free = true;
    }

    int idx = offset;
    int last = offset + byte_count;
    int s = 0;

    // OUTER LEBAL
    outer:

    while (idx < last) {
      jbyte b0 = d[idx++];
      if ((b0 & 0x80) == 0) {
        // 0xxxxxxx
        // Range:  U-00000000 - U-0000007F
        int val = b0 & 0xff;
        v[s++] = (jchar) val;
      } else if ((b0 & 0xe0) == 0xc0) {
        if (idx + 1 > last) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Extract usable bits from b0
        int val = b0 & 0x1f;
        jbyte b = d[idx++];
        if ((b & 0xc0) != 0x80) {
          v[s++] = REPLACEMENT_CHAR;
          idx--;  // Put the input char back
          goto outer;
        }
        // Push new bits in from the right side
        val <<= 6;
        val |= b & 0x3f;
        // Allow surrogate values (0xD800 - 0xDFFF) to
        // be specified using 3-byte UTF values only
        if ((val >= 0xD800) && (val <= 0xDFFF)) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Reject chars greater than the Unicode maximum of U+10FFFF.
        if (val > 0x10FFFF) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Encode chars from U+10000 up as surrogate pairs
        if (val < 0x10000) {
          v[s++] = (jchar) val;
        } else {
          int x = val & 0xffff;
          int u = (val >> 16) & 0x1f;
          int w = (u - 1) & 0xffff;
          int hi = 0xd800 | (w << 6) | (x >> 10);
          int lo = 0xdc00 | (x & 0x3ff);
          v[s++] = (jchar) hi;
          v[s++] = (jchar) lo;
        }
      } else if ((b0 & 0xf0) == 0xe0) {
        if (idx + 2 > last) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Extract usable bits from b0
        int val = b0 & (0x1f >> 1);
        for (int i = 0; i < 2; ++i) {
          jbyte b = d[idx++];
          if ((b & 0xc0) != 0x80) {
            v[s++] = REPLACEMENT_CHAR;
            idx--;  // Put the input char back
            goto outer;
          }
          // Push new bits in from the right side
          val <<= 6;
          val |= b & 0x3f;
        }
        // Reject chars greater than the Unicode maximum of U+10FFFF.
        if (val > 0x10FFFF) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Encode chars from U+10000 up as surrogate pairs
        if (val < 0x10000) {
          v[s++] = (jchar) val;
        } else {
          int x = val & 0xffff;
          int u = (val >> 16) & 0x1f;
          int w = (u - 1) & 0xffff;
          int hi = 0xd800 | (w << 6) | (x >> 10);
          int lo = 0xdc00 | (x & 0x3ff);
          v[s++] = (jchar) hi;
          v[s++] = (jchar) lo;
        }
      } else if ((b0 & 0xf8) == 0xf0) {
        if (idx + 3 > last) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Extract usable bits from b0
        int val = b0 & (0x1f >> 2);
        for (int i = 0; i < 3; ++i) {
          jbyte b = d[idx++];
          if ((b & 0xc0) != 0x80) {
            v[s++] = REPLACEMENT_CHAR;
            idx--;  // Put the input char back
            goto outer;
          }
          // Push new bits in from the right side
          val <<= 6;
          val |= b & 0x3f;
        }
        // Allow surrogate values (0xD800 - 0xDFFF) to
        // be specified using 3-byte UTF values only
        if ((val >= 0xD800) && (val <= 0xDFFF)) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Reject chars greater than the Unicode maximum of U+10FFFF.
        if (val > 0x10FFFF) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Encode chars from U+10000 up as surrogate pairs
        if (val < 0x10000) {
          v[s++] = (jchar) val;
        } else {
          int x = val & 0xffff;
          int u = (val >> 16) & 0x1f;
          int w = (u - 1) & 0xffff;
          int hi = 0xd800 | (w << 6) | (x >> 10);
          int lo = 0xdc00 | (x & 0x3ff);
          v[s++] = (jchar) hi;
          v[s++] = (jchar) lo;
        }
      } else if ((b0 & 0xfc) == 0xf8) {
        if (idx + 4 > last) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Extract usable bits from b0
        int val = b0 & (0x1f >> 3);
        for (int i = 0; i < 4; ++i) {
          jbyte b = d[idx++];
          if ((b & 0xc0) != 0x80) {
            v[s++] = REPLACEMENT_CHAR;
            idx--;  // Put the input char back
            goto outer;
          }
          // Push new bits in from the right side
          val <<= 6;
          val |= b & 0x3f;
        }
        // Allow surrogate values (0xD800 - 0xDFFF) to
        // be specified using 3-byte UTF values only
        if ((val >= 0xD800) && (val <= 0xDFFF)) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Reject chars greater than the Unicode maximum of U+10FFFF.
        if (val > 0x10FFFF) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Encode chars from U+10000 up as surrogate pairs
        if (val < 0x10000) {
          v[s++] = (jchar) val;
        } else {
          int x = val & 0xffff;
          int u = (val >> 16) & 0x1f;
          int w = (u - 1) & 0xffff;
          int hi = 0xd800 | (w << 6) | (x >> 10);
          int lo = 0xdc00 | (x & 0x3ff);
          v[s++] = (jchar) hi;
          v[s++] = (jchar) lo;
        }
      } else if ((b0 & 0xfe) == 0xfc) {
        if (idx + 5 > last) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Extract usable bits from b0
        int val = b0 & (0x1f >> 4);
        for (int i = 0; i < 5; ++i) {
          jbyte b = d[idx++];
          if ((b & 0xc0) != 0x80) {
            v[s++] = REPLACEMENT_CHAR;
            idx--;  // Put the input char back
            goto outer;
          }
          // Push new bits in from the right side
          val <<= 6;
          val |= b & 0x3f;
        }
        // Allow surrogate values (0xD800 - 0xDFFF) to
        // be specified using 3-byte UTF values only
        if ((val >= 0xD800) && (val <= 0xDFFF)) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Reject chars greater than the Unicode maximum of U+10FFFF.
        if (val > 0x10FFFF) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }
        // Encode chars from U+10000 up as surrogate pairs
        if (val < 0x10000) {
          v[s++] = (jchar) val;
        } else {
          int x = val & 0xffff;
          int u = (val >> 16) & 0x1f;
          int w = (u - 1) & 0xffff;
          int hi = 0xd800 | (w << 6) | (x >> 10);
          int lo = 0xdc00 | (x & 0x3ff);
          v[s++] = (jchar) hi;
          v[s++] = (jchar) lo;
        }
      /*
      } else if (((b0 & 0xe0) == 0xc0) || ((b0 & 0xf0) == 0xe0) ||
        ((b0 & 0xf8) == 0xf0) || ((b0 & 0xfc) == 0xf8) || ((b0 & 0xfe) == 0xfc)) {
        int utfCount = 1;
        if ((b0 & 0xf0) == 0xe0) utfCount = 2;
        else if ((b0 & 0xf8) == 0xf0) utfCount = 3;
        else if ((b0 & 0xfc) == 0xf8) utfCount = 4;
        else if ((b0 & 0xfe) == 0xfc) utfCount = 5;

        if (idx + utfCount > last) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }

        // Extract usable bits from b0
        int val = b0 & (0x1f >> (utfCount - 1));
        for (int i = 0; i < utfCount; ++i) {
          jbyte b = d[idx++];
          if ((b & 0xc0) != 0x80) {
            v[s++] = REPLACEMENT_CHAR;
            idx--; // Put the input char back
            goto outer;
          }
          // Push new bits in from the right side
          val <<= 6;
          val |= b & 0x3f;
        }

        // Allow surrogate values (0xD800 - 0xDFFF) to
        // be specified using 3-byte UTF values only
        if ((utfCount != 2) && (val >= 0xD800) && (val <= 0xDFFF)) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }

        // Reject chars greater than the Unicode maximum of U+10FFFF.
        if (val > 0x10FFFF) {
          v[s++] = REPLACEMENT_CHAR;
          continue;
        }

        // Encode chars from U+10000 up as surrogate pairs
        if (val < 0x10000) {
          v[s++] = (jchar) val;
        } else {
          int x = val & 0xffff;
          int u = (val >> 16) & 0x1f;
          int w = (u - 1) & 0xffff;
          int hi = 0xd800 | (w << 6) | (x >> 10);
          int lo = 0xdc00 | (x & 0x3ff);
          v[s++] = (jchar) hi;
          v[s++] = (jchar) lo;
        }
      */
      } else {
        // Illegal values 0x8*, 0x9*, 0xa*, 0xb*, 0xfd-0xff
        v[s++] = REPLACEMENT_CHAR;
      }
    }
    // Result handling
    // Release the orig. buffer
    env->ReleaseByteArrayElements(java_data, jRawArray, JNI_ABORT);
    if (env->ExceptionCheck() == JNI_TRUE) {
      if (v_need_free) {
        delete [] v;
      }
      return nullptr;
    }
    // Handle the new target string
    jstring rtn = env->NewString(v, s);

    // Free the template char array.
    if (v_need_free) {
      delete [] v;
    }
    return rtn;
  }
  return nullptr;
}


static JNINativeMethod gMethods[] = {
  NATIVE_METHOD(MTKStringFactory, newStringFromUTF8Bytes, "([BII)Ljava/lang/String;"),
};
void register_com_mediatek_libcore_MTKStringFactory(JNIEnv* env) {
  jniRegisterNativeMethods(env, "com/mediatek/libcore/MTKStringFactory", gMethods, NELEM(gMethods));
}
