/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_tvm_tvm_util_device_PrinterUtil */

#ifndef _Included_com_tvm_tvm_util_device_PrinterUtil
#define _Included_com_tvm_tvm_util_device_PrinterUtil
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_tvm_tvm_util_device_PrinterUtil
 * Method:    jPrinterInit
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_tvm_tvm_util_device_PrinterUtil_jPrinterInit
  (JNIEnv *, jobject);

/*
 * Class:     com_tvm_tvm_util_device_PrinterUtil
 * Method:    jPrinterDataSend
 * Signature: ([BI)V
 */
JNIEXPORT void JNICALL Java_com_tvm_tvm_util_device_PrinterUtil_jPrinterDataSend
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     com_tvm_tvm_util_device_PrinterUtil
 * Method:    getMessageFromJNI
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_tvm_tvm_util_device_PrinterUtil_getMessageFromJNI
        (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
