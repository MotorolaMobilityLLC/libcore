/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class sun_nio_ch_ServerSocketChannelImpl */

#ifndef _Included_sun_nio_ch_ServerSocketChannelImpl
#define _Included_sun_nio_ch_ServerSocketChannelImpl
#ifdef __cplusplus
extern "C" {
#endif
#undef sun_nio_ch_ServerSocketChannelImpl_ST_UNINITIALIZED
#define sun_nio_ch_ServerSocketChannelImpl_ST_UNINITIALIZED -1L
#undef sun_nio_ch_ServerSocketChannelImpl_ST_INUSE
#define sun_nio_ch_ServerSocketChannelImpl_ST_INUSE 0L
#undef sun_nio_ch_ServerSocketChannelImpl_ST_KILLED
#define sun_nio_ch_ServerSocketChannelImpl_ST_KILLED 1L
/*
 * Class:     sun_nio_ch_ServerSocketChannelImpl
 * Method:    accept0
 * Signature: (Ljava/io/FileDescriptor;Ljava/io/FileDescriptor;[Ljava/net/InetSocketAddress;)I
 */
JNIEXPORT jint JNICALL ServerSocketChannelImpl_accept0
  (JNIEnv *, jobject, jobject, jobject, jobjectArray);

/*
 * Class:     sun_nio_ch_ServerSocketChannelImpl
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL ServerSocketChannelImpl_initIDs
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
