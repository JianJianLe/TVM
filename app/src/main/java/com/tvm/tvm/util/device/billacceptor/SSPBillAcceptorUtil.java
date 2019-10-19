package com.tvm.tvm.util.device.billacceptor;

import android.os.Handler;
import android.util.Log;

import com.tvm.tvm.util.DataUtils;
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.device.SerialPortUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSPBillAcceptorUtil {

    public int rcvdMoney=-1;

    private SerialPortUtil serialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private static SSPBillAcceptorUtil instance;
    public synchronized static SSPBillAcceptorUtil getInstance(){
        if(instance==null){
            instance = new SSPBillAcceptorUtil();
        }
        return instance;
    }


    // 接收投币器数据的定时器
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[5];
                    int size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onDataReceived(final byte[] buffer, final int size) {

        try {
            for(int i=0;i<size;i++){
                dataCheck(buffer[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void dataCheck(final byte buffer) throws IOException {

    }

    //======== 纸钞机指令 Start========
    //数据包格式：STX SEQ LENGTH DATA CRCL CRCH
    //STX:起始字节， 0x7F
    //SEQ：标志位，0x80，0x00交替
    //LENGTH:包长度，不包含STX，SEQ， CRCL， CRCH
    //CRCL，CRCH：CRC-16校验，从SEQ到DATA

    public void init_BillAcceptorCmd(){

    }

    public void init_BillAcceptorDevice(){

    }

    public void ba_Enable(){

    }

    public void ba_Disable(){

    }
    //======== 纸钞机指令 End========


    //======== Common Function Start ========

    private void write(String cmdStr) {
        try {
            printInfo( "SSP Bill Acceptor Util Write CMD: "+cmdStr);
            byte[] sendData = DataUtils.hexToByteArray(cmdStr);
            mOutputStream.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAllCMD(String cmdStr){
        String pattern="AA.*AB";
        Pattern r=Pattern.compile(pattern);
        Matcher m=r.matcher(cmdStr);
        if(m.find())
            return m.group(0);
        else
            return null;
    }

    private String getCMDDataByRegex(String content,String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher((content));
        if(m.find())
            return m.group(0);
        else
            return null;
    }

    private boolean compareCMD(String cmdStr, String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m =r.matcher(cmdStr);
        return m.find();
    }

    private boolean checkCRC(){
        return false;
    }

    private void printInfo(String infoStr){
        Log.i("Test", infoStr);
    }
    //======== Common Function End ========
}
