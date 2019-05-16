package com.tvm.tvm.util.device.paydevice;

import android.provider.ContactsContract;
import android.util.Log;

import com.tvm.tvm.util.DataUtils;
import com.tvm.tvm.util.device.SerialPortUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayDeviceUtil {
    public String QRData=null;

    private SerialPortUtil serialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private static String serialPortFilePath1="/dev/ttyS0";
    private static String serialPortFilePath2="/dev/ttyGS0";

    private String receivedCMD;

    private static PayDeviceUtil instance;
    public synchronized static PayDeviceUtil getInstance(){
        if(instance==null)
            instance=new PayDeviceUtil();
        return instance;
    }

    public void initPayDevice() {

        printInfo("Init PayDevice");
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
                    receivedCMD="";
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[256];
                    int size = mInputStream.read(buffer);
                    if (size > 0)
                        onDataReceived(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void onDataReceived(final byte[] buffer) {
        String cmdStr=DataUtils.bytesToHex(buffer);
        if(cmdStr.startsWith("AA")){
            receivedCMD=getAllCMD(cmdStr);
            if(receivedCMD==null){
                printInfo("receivedCMD = null, Server CMD:"+cmdStr);
                return;
            }
            printInfo("Server CMD:"+receivedCMD);
            if(checkXOR()){
                if(hasServerQuery())
                    cmd_ReplySever();
                if(hasQRCode())
                    QRData = getQRData();
            }
        }else{
            printInfo("Invalid Server CMD:"+cmdStr);
        }
    }

    private void cmd_ReplySever(){
        printInfo("Reply Server");
        write("AA0502017C0872DD");
    }

    private boolean hasServerQuery(){
        return compareCMD(receivedCMD,"AA0B0101.*DD");
    }


    //售票机发送获取二维码支付链接指令（子命令 0x0A）
    //AA 0E 02 C9 0A  [6byte随机数]  [4byte支付金额] Check DD
    //支付盒子应答：AA XX 01 C9 0A data[n] Check DD （data[n]为支付二维码链接数据）

    public void cmd_GetQRCode(int amount){
        QRData=null;
        printInfo("Amount = "+amount+", Send CMD to get QR code.");
        String cmdStr="0E02C90A"+getRandomHex(6)+getAmountHex(amount);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    private String getRandomHex(int count){
        return DataUtils.bytesToHex(getRandomByte(count));
    }

    private byte[] getRandomByte(int count){
        byte[] randomBytes=new byte[count];
        Random random=new Random();
        random.nextBytes(randomBytes);
        return randomBytes;
    }

    private String getAmountHex(int amount){
        String hexStr = DataUtils.decToHex(amount);
        while (hexStr.length()<8)
            hexStr += "0";
        return hexStr;
    }
    
    private String getQRData(){
        String hexStr= getCMDDataByRegex(receivedCMD,"(?<=AA..01C90A).*(?=..DD)");
        printInfo(hexStr);
        return DataUtils.convertHexToString(hexStr);
    }

    private boolean hasQRCode(){
        return compareCMD(receivedCMD,"AA..01C90A.*DD");
    }

    private boolean checkXOR(){
        String hexStr = getCMDDataByRegex(receivedCMD,"(?<=AA).*(?=DD)");
        String result = hexStr.substring(hexStr.length()-2,hexStr.length());
        String cmdStr = hexStr.substring(0,hexStr.length()-2);
        return DataUtils.xor(cmdStr).equals(result);
    }
    //查询终端基础参数CMD05：
    //支付盒子发送：AA 03 01 05 07 DD
    //售票机应答：AA 2B 02 05 data[n] Check DD
    public void cmd_QueryParam(){

    }
    private String getClientParam(){
        return null;
    }

    //设置终端基础参数CMD06：
    //支付盒子发送给售票机：AA 2B 01 06 data[n] Check DD
    //售票机应答：AA 04 02 06 01 07 DD （00失败，01成功）
    public void cmd_SetParam(){
        SetClientParam();

    }

    private void SetClientParam(){

    }

    //###########################
    //Common Function -- Start
    //###########################

    private String addEndCMD(String cmdStr){
        return DataUtils.removeSpace(cmdStr) + DataUtils.xor(cmdStr) + "DD";
    }

    private void write(String cmdStr) {
        try {
            printInfo( "Client Write CMD: "+cmdStr);
            byte[] sendData = DataUtils.hexToByteArray(cmdStr);
            mOutputStream.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getAllCMD(String cmdStr){
        String pattern="AA.*DD";
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
    
    private void printInfo(String infoStr){
        Log.i("Test", infoStr);
    }
    //###########################
    //Common Function -- End
    //###########################
}
