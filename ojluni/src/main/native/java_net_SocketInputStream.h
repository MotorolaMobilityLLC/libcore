/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class java_net_SocketInputStream */

#ifndef _Included_java_net_SocketInputStream
#define _Included_java_net_SocketInputStream
#ifdef __cplusplus
extern "C" {
#endif
#undef java_net_SocketInputStream_MAX_SKIP_BUFFER_SIZE
#define java_net_SocketInputStream_MAX_SKIP_BUFFER_SIZE 2048L
/*
 * Class:     java_net_SocketInputStream
 * Method:    socketRead0
 * Signature: (Ljava/io/FileDescriptor;[BIII)I
 */
JNIEXPORT jint JNICALL Java_java_net_SocketInputStream_socketRead0
  (JNIEnv *, jobject, jobject, jbyteArray, jint, jint, jint);

/*
 * Class:     java_net_SocketInputStream
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_java_net_SocketInputStream_init
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
