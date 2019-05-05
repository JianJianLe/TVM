package com.tvm.tvm.util.device.paydevice;

import android.util.Log;

import com.tvm.tvm.util.DataUtils;
import com.tvm.tvm.util.device.SerialPortUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayDeviceUtil {

    private SerialPortUtil serialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
//    private byte[] PD_sendData;
//    public byte[] PD_receiveData;
    private static String serialPortFilePath1="/dev/ttyS1";//"/dev/ttyS0";
    private static String serialPortFilePath2="/dev/ttyGS1";//"/dev/ttyGS0";

    private static PayDeviceUtil instance;
    public synchronized static PayDeviceUtil getInstance(){
        if(instance==null)
            instance=new PayDeviceUtil();
        return instance;
    }

    public void initPayDevice() {

        File serialPortFile = new File(serialPortFilePath1);
        if(!serialPortFile.exists()){
            serialPortFile = new File(serialPortFilePath2);
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

        String cmdStr=getCMD(DataUtils.bytesToHex(buffer));
        Log.i("Test", cmdStr);

        if(compareCMD(cmdStr,"AA0B0101.*DD"))
            write("AA0502017C72DD");
    }

    private void write(String cmdStr) {
        try {
            byte[] sendData = DataUtils.hexToByteArray(cmdStr);
            mOutputStream.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    private boolean query_ServerSendCMD(String cmdStr){
//        return compareCMD(cmdStr,"AA0B0101.*DD");
//    }
//
//    private String query_ClientReplyCMD(){
//        return "AA 05 02 01 7C 72 DD";
//    }

    private String getCMD(String cmdStr){
        String partern="AA.*DD";
        Pattern r=Pattern.compile(partern);
        Matcher m=r.matcher(cmdStr);
        if(m.find())
            return m.group(0);
        else
            return null;
    }

    private boolean compareCMD(String cmdStr, String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m =r.matcher(cmdStr);
        if(m.find())
            return true;
        else
            return false;
    }
}
