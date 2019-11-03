package com.tvm.tvm.util.device.billacceptor;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

public class ITLBillAcceptorUtil {

    public int rcvdMoney=-1;

    private String receivedCMD=null;
    private boolean isEnable=false;
    //private boolean isInit=false;
    //private String cmdType="00";
    private BillParser billParser=new BillParser();
    private int countSEQ=0;
    private CRCUtils crcUtils=new CRCUtils(CRCUtils.Parameters.CRC16_SSP);

    private SerialPortUtil serialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private static ITLBillAcceptorUtil instance;
    public synchronized static ITLBillAcceptorUtil getInstance(){
        if(instance==null){
            instance = new ITLBillAcceptorUtil();
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

    private class SendRunnable implements Runnable {
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


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    receivedCMD=(String) msg.obj;
                    break;
                case 1:
                    rcvdMoney=(int) msg.obj;
                    break;
            }
        }
    };

    private void onDataReceived(final byte[] buffer) {
        String validCMD=null;
        String cmdStr = DataUtils.bytesToHex(buffer);
        //printInfo("cmdStr="+cmdStr);
        if(cmdStr.startsWith("7F")){
            validCMD=getAllCMD(cmdStr);
            if(validCMD==null){
                printInfo("receivedCMD=null, Invalid CMD:"+cmdStr);
                return;
            }
            printInfo("SSP Bill Acceptor Actual CMD="+validCMD);

            if(checkCRC(validCMD)){
                Message msg=Message.obtain();
                msg.what=0;
                msg.obj=validCMD;
                handler.sendMessage(msg);
            }else{
                printInfo("checkCRC=false");
            }
        }else{
            printInfo("SSP Invalid CMD: " + cmdStr);
        }

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


    //======== 纸钞机指令 Start========
    //数据包格式：STX SEQ LENGTH DATA CRCL CRCH
    //STX:起始字节， 0x7F
    //SEQ：标志位，0x80，0x00交替
    //LENGTH:包长度，不包含STX，SEQ， CRCL， CRCH
    //CRCL，CRCH：CRC-16校验，从SEQ到DATA
    public void init_BillAcceptorCmd(){
        //0x11
        cmd_Connection();

        //0x05
        if(hasReplied()){
            cmd_ReadChannelInfo();
        }

        //0x02
        if(hasChannelInfo()){
            billParser.parseBillChannel(receivedCMD);
            cmd_SetBillChannel();
        }

        //0x09
        if(hasReplied()){
            ba_Disable();
        }
    }

    private void cmd_Connection(){
        //发送0x11号指令查找纸币机是否连接
        String cmdStr=getSEQCMD()+"0111";
        printInfo("发送11指令");
        write(getFinalCMD(cmdStr));
        if(hasReplied()){
            printInfo("纸币机已经连接成功");
        }
    }

    private void cmd_ReadChannelInfo(){
        //发送0x05指令读取纸币机通道配置情况
        String cmdStr=getSEQCMD()+"0105";
        printInfo("发送05指令");
        write(getFinalCMD(cmdStr));
        if(hasReplied()){
            printInfo("读取纸币机通道配置信息成功");
        }
    }

    private boolean hasChannelInfo(){
        return receivedCMD!=null && receivedCMD.startsWith("7F001DF000");
    }

    private void cmd_SetBillChannel(){
        //发送0x02指令设置允许识别哪几种纸币
        //第一字节的最高位Bit7必须为1
        //可以识别100，50，20，10，5，2，1
        //只识别10，20，50，100，则为（1111 1000）F8
        //7F 80 03 02 FF 00 27 A6
        //cmdType="02";
        String sendCmd=getSEQCMD()+"0302" + billParser.getBillTypeCMD();
        printInfo("发送02指令");
        write(getFinalCMD(sendCmd));
    }



    //要使能纸钞机，要先发送 0x02 号命令设置允许识别哪几种纸币
    //然后在接到02指令成功信息后，发送0A指令使能
    public void ba_Enable(){
        cmd_SetBillChannel();
        if(hasReplied()){
            String cmdStr=getSEQCMD()+"010A" ;
            printInfo("发送0A指令");
            write(getFinalCMD(cmdStr));

            if(hasReplied()){
                isEnable=true;
                printInfo("允许接收纸币成功");
                CatchCashThread catchCashThread=new CatchCashThread();
                catchCashThread.start();
            }
        }
    }

    //发送0x09指令禁止纸币机识别纸币
    public void ba_Disable(){
        isEnable=false;
        //发送0x09指令允许纸币机识别纸币（使能）
        String cmdStr=getSEQCMD()+"0109";
        printInfo("发送09指令");
        write(getFinalCMD(cmdStr));

        if(hasReplied()){
            printInfo("禁止接收纸币成功");
        }
    }

    private void cmd_Poll(){
        String cmdStr = getSEQCMD()+"0107";
        write(getFinalCMD(cmdStr));
        TimeUtil.delay(500);
    }

    //循环发送Poll指令，等待接收纸币
    private class CatchCashThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted() && isEnable) {
                cmd_Poll();
                String resultStr=getReceivedCash(receivedCMD);
                if(resultStr!=null){
                    Message msg=Message.obtain();
                    msg.what=1;
                    msg.obj= billParser.getReceivedMoney(resultStr);
                    handler.sendMessage(msg);
                    for(int i=0;i<2;i++){
                        cmd_Poll();
                    }
                }
            }
        }
    }

    private String getReceivedCash(String receivedCMDStr){
        String result=getCMDDataByRegex(receivedCMDStr,"(?<=7F....F0EE).*(?=CC)");
        if(result==null){
            result=getCMDDataByRegex(receivedCMDStr,"(?<=7F....F0E6).*(?=CC)");
        }
        return result;
    }

    private boolean hasReplied(){
        return  receivedCMD!=null && (receivedCMD.equals("7F8001F02380") ||
                receivedCMD.equals("7F0001F0200A"));
    }
    //======== 纸钞机指令 End========


    //======== Common Function Start ========

    private String getFinalCMD(String cmdStr){
        String crcStr=DataUtils.decToHex((int) crcUtils.calculateCRC(DataUtils.hexToByteArray(cmdStr)));
        return  "7F" + cmdStr + crcStr;
    }

    private String getSEQCMD(){
        String cmdStr=null;
        if(countSEQ%2==0){
            cmdStr= "80";
        }else{
            cmdStr= "00";
        }
        countSEQ++;
        if(countSEQ==10){
            countSEQ=0;
        }
        return cmdStr;
    }

    private void write(final String cmdStr) {

        new Thread(){
            public void run() {
                try {
                    printInfo( "SSP Bill Acceptor Util Write CMD: "+cmdStr);
                    byte[] sendData = DataUtils.hexToByteArray(cmdStr);
                    mOutputStream.write(sendData);
                    TimeUtil.delay(200);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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
