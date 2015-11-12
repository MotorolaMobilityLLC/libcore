/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class sun_nio_ch_FileChannelImpl */

#ifndef _Included_sun_nio_ch_FileChannelImpl
#define _Included_sun_nio_ch_FileChannelImpl
#ifdef __cplusplus
extern "C" {
#endif
#undef sun_nio_ch_FileChannelImpl_MAPPED_TRANSFER_SIZE
#define sun_nio_ch_FileChannelImpl_MAPPED_TRANSFER_SIZE 8388608LL
#undef sun_nio_ch_FileChannelImpl_TRANSFER_SIZE
#define sun_nio_ch_FileChannelImpl_TRANSFER_SIZE 8192L
#undef sun_nio_ch_FileChannelImpl_MAP_RO
#define sun_nio_ch_FileChannelImpl_MAP_RO 0L
#undef sun_nio_ch_FileChannelImpl_MAP_RW
#define sun_nio_ch_FileChannelImpl_MAP_RW 1L
#undef sun_nio_ch_FileChannelImpl_MAP_PV
#define sun_nio_ch_FileChannelImpl_MAP_PV 2L
/*
 * Class:     sun_nio_ch_FileChannelImpl
 * Method:    map0
 * Signature: (IJJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_nio_ch_FileChannelImpl_map0
  (JNIEnv *, jobject, jint, jlong, jlong);

/*
 * Class:     sun_nio_ch_FileChannelImpl
 * Method:    unmap0
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_FileChannelImpl_unmap0
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     sun_nio_ch_FileChannelImpl
 * Method:    transferTo0
 * Signature: (IJJI)J
 */
JNIEXPORT jlong JNICALL Java_sun_nio_ch_FileChannelImpl_transferTo0
  (JNIEnv *, jobject, jint, jlong, jlong, jint);

/*
 * Class:     sun_nio_ch_FileChannelImpl
 * Method:    position0
 * Signature: (Ljava/io/FileDescriptor;J)J
 */
JNIEXPORT jlong JNICALL Java_sun_nio_ch_FileChannelImpl_position0
  (JNIEnv *, jobject, jobject, jlong);

/*
 * Class:     sun_nio_ch_FileChannelImpl
 * Method:    initIDs
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_sun_nio_ch_FileChannelImpl_initIDs
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
