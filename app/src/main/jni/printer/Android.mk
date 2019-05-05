LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := printer
LOCAL_SRC_FILES := com_tvm_tvm_util_device_printerdevice_PrinterUtil.c
LOCAL_LDLIBS := -llog
include $(BUILD_SHARED_LIBRARY)