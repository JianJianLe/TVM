package com.tvm.tvm.util.device.billacceptor;

import android.os.Handler;
import android.util.Log;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.util.BillParser;
import com.tvm.tvm.util.CRCUtils;
import com.tvm.tvm.util.DataUtils;
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.device.SerialPortUtil;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSPBillAcceptorUtil {

    public int rcvdMoney=-1;

    private String receivedCMD;
    private boolean isEnable=false;
    private boolean isInit=false;
    private String cmdType="00";
    private int pollFlag=0;
    private BillParser billParser=new BillParser();

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

    private Handler mHandler=new Handler();
    private class ReadThread extends Thread {
        private SendRunnable runnable = new SendRunnable();
        //第一次运行线程时设置成true
        private boolean beginning = false;
        //缓冲区()
        byte[] buffer = new byte[512];

        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                int size;
                try {
                    if (mInputStream == null) return;
                    //读取数据,同时获取数据长度(数据长度不是数组长度,而是实际接收到的数据长度),数据被读取到了缓冲区 buffer中
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        //临时数组,将缓冲区buffer中的有效数据读取出来,临时数据长度就是接收到的数据长度。
                        byte[] temp = new byte[size];
                        System.arraycopy(buffer, 0, temp, 0, size);
                        //具体注释见init方法
                        runnable.init(temp, size);
                        //如果程序第一次运行
                        if (!beginning) {
                            //运行runnable,只在第一次执行,如果重复执行虽不会抛出异常,但是也无法正常执行功能
                            mHandler.post(runnable);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public class SendRunnable implements Runnable {
        private byte[] lastBuffer;
        int time = 0;
        boolean work = true;
        private int lastBufferLength;

        //断包处理逻辑包含其中
        public void init(byte[] buffer, int size) {

            if (lastBuffer == null) {
                lastBuffer = buffer;
            } else {
                lastBufferLength = lastBuffer.length;
                byte[] temp = new byte[lastBufferLength + size];
                //先拷贝之前的数据
                System.arraycopy(lastBuffer, 0, temp, 0, lastBufferLength);
                //再拷贝刚接收到的数据
                System.arraycopy(buffer, 0, temp, lastBufferLength, size);
                lastBuffer = null;
                lastBuffer = temp;
                temp = null;
            }
            work = true;
            time = 0;
        }

        public void reStart() {
            work = true;
            time = 0;
        }

        public void stop() {
            work = false;
            time = 0;
        }
        //接收完成后重置完整消息缓冲区
        public void reset() {
            work = false;
            time = 0;
            lastBuffer = null;
        }

        @Override
        public void run() {
            while (work) {
                try {
                    Thread.sleep(20);
                    time += 20;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (time >= 100) {
                    byte[] finalBuffer = lastBuffer;
                    reset();
                    //业务处理方法
                    onDataReceived(finalBuffer);
                }
            }

        }
    }

    private void onDataReceived(final byte[] buffer) {
        String sendCmd=null;
        String cmdStr = DataUtils.bytesToHex(buffer);
        //printInfo("cmdStr="+cmdStr);
        if(cmdStr.startsWith("7F")){
            receivedCMD=getAllCMD(cmdStr);
            if(receivedCMD==null){
                printInfo("receivedCMD=null, Invalid CMD:"+cmdStr);
                return;
            }
            printInfo("SSP Bill Acceptor Actual CMD="+receivedCMD);

            if(checkCRC(receivedCMD)){
                //纸币机应答
                //7F 00 01 07 11 88
                //7F 80 01 F0 23 80
                if(receivedCMD.equals("7F8001F02380") ||
                   receivedCMD.equals("7F0001F0200A")){
                    //printInfo("纸币机应答OK，纸币机已经完成指令");
                    if(cmdType.equals("02")){
                        printInfo("设置成功: 设置允许识别哪几种纸币");
                        if(isInit)
                            ba_Disable();
                        else{
                            //发送0x0A指令允许纸币机识别纸币（使能）
                            cmdType="0A";
                            sendCmd="7F00010A3C08";
                            printInfo("发送0A指令");
                            write(sendCmd);
                        }
                    }
                    if(cmdType.equals("11")){
                        printInfo("纸币机已经连接成功");

                        //发送0x05指令读取纸币机通道配置情况
                        cmdType="05";
                        sendCmd="7F0001051E08";
                        printInfo("发送05指令");
                        write(sendCmd);
                    }

                    if(cmdType.equals("0A")){
                        printInfo("允许接收纸币成功");
                        isEnable=true;
                        CatchCashThread catchCashThread=new CatchCashThread();
                        catchCashThread.start();
                        cmdType="00";
                    }

                    if(cmdType.equals("09")){
                        printInfo("禁止接收纸币成功");
                        isEnable=false;
                        cmdType="00";
                    }

                    if(cmdType.equals("05")){
                        printInfo("读取纸币机通道配置信息成功");
                        cmdType="00";
                    }
                }

                if(receivedCMD.startsWith("7F001DF000")){
                    printInfo("纸币机通道信息:"+cmdStr);
                    //发送0x02指令设置允许识别哪几种纸币
                    //第一字节的最高位Bit7必须为1
                    //可以识别100，50，20，10，5，2，1
                    //只识别10，20，50，100，则为（1111 1000）F8
                    //7F 80 03 02 FF 00 27 A6
                    billParser.parseBillChannel(cmdStr);
                    cmd_SetBillChannel();
                }

                if(isEnable){
                    String resultStr=getReceivedCash();
                    //printInfo("getReceivedCash resultStr="+resultStr);
                    if(resultStr!=null){
                        rcvdMoney=billParser.getReceivedMoney(resultStr);
                    }
                }

            }else{
                printInfo("checkCRC=false");
            }
        }else{
            printInfo("Invalid CMD: " + cmdStr);
        }

    }


    //======== 纸钞机指令 Start========
    //数据包格式：STX SEQ LENGTH DATA CRCL CRCH
    //STX:起始字节， 0x7F
    //SEQ：标志位，0x80，0x00交替
    //LENGTH:包长度，不包含STX，SEQ， CRCL， CRCH
    //CRCL，CRCH：CRC-16校验，从SEQ到DATA

    public void init_BillAcceptorCmd(){
        //发送0x11号指令查找纸币机是否连接
        isInit=true;
        cmdType="11";
        String cmdStr="7F8001116582";
        printInfo("发送11指令");
        write(cmdStr);
    }

    public void init_BillAcceptorDevice(){
        String serialPortFilePath_One = "/dev/ttyS3";
        String serialPortFilePath_Two = "/dev/ttyGS3";

        if(PreConfig.AndroidBoardVersion=="2.0"){
            serialPortFilePath_One = "/dev/ttyS4";
            serialPortFilePath_Two = "/dev/ttyGS4";
        }

        File serialPortFile = new File(serialPortFilePath_One);
        if(!serialPortFile.exists()){
            serialPortFile = new File(serialPortFilePath_Two);
        }
        try {
            serialPort = new SerialPortUtil(serialPortFile,9600, 2);
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

    private void cmd_SetBillChannel(){
        //发送0x02指令设置允许识别哪几种纸币
        //第一字节的最高位Bit7必须为1
        //可以识别100，50，20，10，5，2，1
        //只识别10，20，50，100，则为（1111 1000）F8
        //7F 80 03 02 FF 00 27 A6
        cmdType="02";
        String sendCmd="800302" + billParser.getBillTypeCMD();
        //CRC校验
        CRCUtils crcUtils=new CRCUtils(CRCUtils.Parameters.CRC16_SSP);
        String crcStr=DataUtils.decToHex((int) crcUtils.calculateCRC(DataUtils.hexToByteArray(sendCmd)));
        //最终指令
        String finalCMD="7F" + sendCmd + crcStr;
        printInfo("发送02指令");
        write(finalCMD);
    }

    //要使能纸钞机，要先发送 0x02 号命令设置允许识别哪几种纸币
    //然后在接到02指令成功信息后，发送0A指令使能
    public void ba_Enable(){
        isInit=false;
        cmd_SetBillChannel(); //发送02指令
    }

    //发送0x09指令禁止纸币机识别纸币
    public void ba_Disable(){
        //发送0x09指令允许纸币机识别纸币（使能）
        cmdType="09";
        String cmdStr="7F0001093608";
        printInfo("发送09指令");
        write(cmdStr);
    }


    //循环发送Poll指令，等待接收纸币
    private class CatchCashThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted() && isEnable) {
                 String cmdStr=null;
                 if(pollFlag % 2 == 0){
                     cmdStr="7F0001071188";
                 }else {
                     cmdStr="7F8001071202";
                 }
                 write(cmdStr);
                 TimeUtil.delay(500);
                 pollFlag++;
                 if(pollFlag==10){
                     pollFlag=0;
                 }
            }
        }
    }

    private String getReceivedCash(){
        return getCMDDataByRegex(receivedCMD,"(?<=7F....F0EE).*(?=CC)");
    }
    //======== 纸钞机指令 End========


    //======== Common Function Start ========

    private void write(String cmdStr) {
        try {
            printInfo( "SSP Bill Acceptor Util Write CMD: "+cmdStr);
            byte[] sendData = DataUtils.hexToByteArray(cmdStr);
            mOutputStream.write(sendData);
            TimeUtil.delay(200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAllCMD(String cmdStr){
        String result=null;
        if(cmdStr!=null){
            result= cmdStr.replaceAll("0*$", "");
            if (result.length() % 2==1){
                result=result+"0";
            }
        }
        return result;
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

    private boolean checkCRC(String hexStr){
        String result=hexStr.substring(hexStr.length()-4,hexStr.length());
        //printInfo("result="+result);
        String cmdStr=hexStr.substring(2,hexStr.length()-4);
        CRCUtils crcUtils=new CRCUtils(CRCUtils.Parameters.CRC16_SSP);
        String crcStr=DataUtils.decToHex((int) crcUtils.calculateCRC(DataUtils.hexToByteArray(cmdStr)));
        //printInfo("crcStr="+crcStr);
        return crcStr.equals(result);
    }

    private void printInfo(String infoStr){
        Log.i("Test", infoStr);
    }
    //======== Common Function End ========
}
