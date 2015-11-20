/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class sun_nio_ch_Net */

#ifndef _Included_sun_nio_ch_Net
#define _Included_sun_nio_ch_Net
#ifdef __cplusplus
extern "C" {
#endif
#undef sun_nio_ch_Net_SHUT_RD
#define sun_nio_ch_Net_SHUT_RD 0L
#undef sun_nio_ch_Net_SHUT_WR
#define sun_nio_ch_Net_SHUT_WR 1L
#undef sun_nio_ch_Net_SHUT_RDWR
#define sun_nio_ch_Net_SHUT_RDWR 2L
/*
 * Class:     sun_nio_ch_Net
 * Method:    isIPv6Available0
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_sun_nio_ch_Net_isIPv6Available0
  (JNIEnv *, jclass);

/*
 * Class:     sun_nio_ch_Net
 * Method:    isExclusiveBindAvailable
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_isExclusiveBindAvailable
  (JNIEnv *, jclass);

/*
 * Class:     sun_nio_ch_Net
 * Method:    canIPv6SocketJoinIPv4Group0
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_sun_nio_ch_Net_canIPv6SocketJoinIPv4Group0
  (JNIEnv *, jclass);

/*
 * Class:     sun_nio_ch_Net
 * Method:    canJoin6WithIPv4Group0
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_sun_nio_ch_Net_canJoin6WithIPv4Group0
  (JNIEnv *, jclass);

/*
 * Class:     sun_nio_ch_Net
 * Method:    socket0
 * Signature: (ZZZ)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_socket0
  (JNIEnv *, jclass, jboolean, jboolean, jboolean);

/*
 * Class:     sun_nio_ch_Net
 * Method:    bind0
 * Signature: (Ljava/io/FileDescriptor;ZZLjava/net/InetAddress;I)V
 */
JNIEXPORT void JNICALL Java_sun_nio_ch_Net_bind0
  (JNIEnv *, jclass, jobject, jboolean, jboolean, jobject, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    listen
 * Signature: (Ljava/io/FileDescriptor;I)V
 */
JNIEXPORT void JNICALL Java_sun_nio_ch_Net_listen
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    connect0
 * Signature: (ZLjava/io/FileDescriptor;Ljava/net/InetAddress;I)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_connect0
  (JNIEnv *, jclass, jboolean, jobject, jobject, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    shutdown
 * Signature: (Ljava/io/FileDescriptor;I)V
 */
JNIEXPORT void JNICALL Java_sun_nio_ch_Net_shutdown
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    localPort
 * Signature: (Ljava/io/FileDescriptor;)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_localPort
  (JNIEnv *, jclass, jobject);

/*
 * Class:     sun_nio_ch_Net
 * Method:    localInetAddress
 * Signature: (Ljava/io/FileDescriptor;)Ljava/net/InetAddress;
 */
JNIEXPORT jobject JNICALL Java_sun_nio_ch_Net_localInetAddress
  (JNIEnv *, jclass, jobject);

/*
 * Class:     sun_nio_ch_Net
 * Method:    remotePort
 * Signature: (Ljava/io/FileDescriptor;)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_remotePort
  (JNIEnv *, jclass, jobject);

/*
 * Class:     sun_nio_ch_Net
 * Method:    remoteInetAddress
 * Signature: (Ljava/io/FileDescriptor;)Ljava/net/InetAddress;
 */
JNIEXPORT jobject JNICALL Java_sun_nio_ch_Net_remoteInetAddress
  (JNIEnv *, jclass, jobject);

/*
 * Class:     sun_nio_ch_Net
 * Method:    getIntOption0
 * Signature: (Ljava/io/FileDescriptor;ZII)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_getIntOption0
  (JNIEnv *, jclass, jobject, jboolean, jint, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    setIntOption0
 * Signature: (Ljava/io/FileDescriptor;ZIII)V
 */
JNIEXPORT void JNICALL Java_sun_nio_ch_Net_setIntOption0
  (JNIEnv *, jclass, jobject, jboolean, jint, jint, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    joinOrDrop4
 * Signature: (ZLjava/io/FileDescriptor;III)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_joinOrDrop4
  (JNIEnv *, jclass, jboolean, jobject, jint, jint, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    blockOrUnblock4
 * Signature: (ZLjava/io/FileDescriptor;III)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_blockOrUnblock4
  (JNIEnv *, jclass, jboolean, jobject, jint, jint, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    joinOrDrop6
 * Signature: (ZLjava/io/FileDescriptor;[BI[B)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_joinOrDrop6
  (JNIEnv *, jclass, jboolean, jobject, jbyteArray, jint, jbyteArray);

/*
 * Class:     sun_nio_ch_Net
 * Method:    blockOrUnblock6
 * Signature: (ZLjava/io/FileDescriptor;[BI[B)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_blockOrUnblock6
  (JNIEnv *, jclass, jboolean, jobject, jbyteArray, jint, jbyteArray);

/*
 * Class:     sun_nio_ch_Net
 * Method:    setInterface4
 * Signature: (Ljava/io/FileDescriptor;I)V
 */
JNIEXPORT void JNICALL Java_sun_nio_ch_Net_setInterface4
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    getInterface4
 * Signature: (Ljava/io/FileDescriptor;)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_getInterface4
  (JNIEnv *, jclass, jobject);

/*
 * Class:     sun_nio_ch_Net
 * Method:    setInterface6
 * Signature: (Ljava/io/FileDescriptor;I)V
 */
JNIEXPORT void JNICALL Java_sun_nio_ch_Net_setInterface6
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     sun_nio_ch_Net
 * Method:    getInterface6
 * Signature: (Ljava/io/FileDescriptor;)I
 */
JNIEXPORT jint JNICALL Java_sun_nio_ch_Net_getInterface6
  (JNIEnv *, jclass, jobject);

/*
 * Class:     sun_nio_ch_Net
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_sun_nio_ch_Net_initIDs
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
