#include "com_tvm_tvm_util_device_printer_PrinterUtil.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <signal.h>

#include <fcntl.h>
#include <termios.h>
#include <errno.h>

#include <android/log.h>

//#define PRINTER_DEVICE_NAME "/dev/ttyS1" //old android board
//#define PRINTER_DEVICE_NAME "/dev/ttyS2" //new android board

int p_fd;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_com_tvm_tvm_util_device_printer_PrinterUtil_jPrinterInit
  (JNIEnv *env, jobject obj, jstring path){

	/* Opening device */
	{
		jboolean iscopy;
		const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
		p_fd = open(path_utf, O_RDWR | O_NOCTTY);
		(*env)->ReleaseStringUTFChars(env, path, path_utf);
		if (p_fd == -1) {
			return NULL;
		}
	}

  	struct termios options;

  	tcgetattr(p_fd, &options);

  	options.c_cflag |= (CLOCAL | CREAD);
  	options.c_cflag &= ~CSIZE;
  	options.c_cflag &= ~CRTSCTS;
  	options.c_cflag |= CS8;
  	options.c_cflag &= ~CSTOPB;

  	options.c_iflag &= ~(BRKINT | ICRNL | INPCK | ISTRIP | IXON);
  	options.c_oflag &= ~OPOST;
  	options.c_cflag |= CLOCAL | CREAD;
  	options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);

  	cfsetispeed(&options, B57600);
  	cfsetospeed(&options, B57600);

  	tcflush(p_fd, TCIFLUSH);
  	tcsetattr(p_fd, TCSANOW, &options);

  	return p_fd;
 }


JNIEXPORT void JNICALL Java_com_tvm_tvm_util_device_printer_PrinterUtil_jPrinterDataSend
  (JNIEnv *env, jobject obj, jbyteArray buffer, jint data_len){
    int len;

    unsigned char array[data_len];

    (*env)->GetByteArrayRegion(env, buffer, 0, data_len, array);

    len = write(p_fd, array, sizeof(array));
}


JNIEXPORT jstring JNICALL Java_com_tvm_tvm_util_device_printer_PrinterUtil_getMessageFromJNI
        (JNIEnv *env, jclass obj){

    return (*env)->NewStringUTF(env,"This is message from PrinterUtil JNI");;
}

#ifdef __cplusplus
}
#endif

