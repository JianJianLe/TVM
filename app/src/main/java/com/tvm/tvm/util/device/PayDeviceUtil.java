package com.tvm.tvm.util.device;

import android.util.Log;

import com.tvm.tvm.util.DataUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PayDeviceUtil {

    private SerialPortUtil serialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private byte[] PD_sendData;
    public byte[] PD_receiveData;

    private static PayDeviceUtil instance;

    public synchronized static PayDeviceUtil getInstance(){
        if(instance==null)
            instance=new PayDeviceUtil();
        return instance;
    }

    public void initPayDevice() {

        File serialPortFile = new File("/dev/ttyS0");
        if(!serialPortFile.exists()){
            serialPortFile = new File("/dev/ttyGS0");
        }
        try {
            serialPort = new SerialPortUtil(serialPortFile,38400);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mOutputStream = (FileOutputStream) serialPort.getOutputStream();
        mInputStream = (FileInputStream) serialPort.getInputStream();

        if(mReadThread==null || !mReadThread.isAlive()){
            mReadThread = new ReadThread();
            mReadThread.start();
        }
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[256];
                    int size = mInputStream.read(buffer);
                    if (size > 0)
                        onDataReceived(buffer, size);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void onDataReceived(final byte[] buffer, final int size) {

        Log.i("Test", DataUtils.bytesToHex(buffer));

        try {
            for(int i=0;i<size;i++){
                dataCheck(buffer[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write() {
        try {
            mOutputStream.write(PD_sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dataCheck(final byte buffer) throws IOException {

    }


}
