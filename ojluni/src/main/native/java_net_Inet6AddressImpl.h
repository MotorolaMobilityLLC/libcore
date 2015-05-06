/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class java_net_Inet6AddressImpl */

#ifndef _Included_java_net_Inet6AddressImpl
#define _Included_java_net_Inet6AddressImpl
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     java_net_Inet6AddressImpl
 * Method:    getLocalHostName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Inet6AddressImpl_getLocalHostName
  (JNIEnv *, jobject);

/*
 * Class:     java_net_Inet6AddressImpl
 * Method:    lookupAllHostAddr
 * Signature: (Ljava/lang/String;)[Ljava/net/InetAddress;
 */
JNIEXPORT jobjectArray JNICALL Inet6AddressImpl_lookupAllHostAddr
  (JNIEnv *, jobject, jstring);

/*
 * Class:     java_net_Inet6AddressImpl
 * Method:    getHostByAddr
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Inet6AddressImpl_getHostByAddr
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     java_net_Inet6AddressImpl
 * Method:    isReachable0
 * Signature: ([BII[BII)Z
 */
JNIEXPORT jboolean JNICALL Inet6AddressImpl_isReachable0
  (JNIEnv *, jobject, jbyteArray, jint, jint, jbyteArray, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
