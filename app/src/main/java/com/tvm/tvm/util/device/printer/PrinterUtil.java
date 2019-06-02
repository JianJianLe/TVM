package com.tvm.tvm.util.device.printer;

import android.util.Log;

import com.tvm.tvm.util.device.QRCodeUtil;

public class PrinterUtil {

    static {
        System.loadLibrary("printer");
    }

    //JNI
    private native int jPrinterInit();
    private native void jPrinterDataSend(byte[] buffer, int data_len);
    private native static String getMessageFromJNI();//JNI Test

    private String TAG = "Test";

    //打印二维码
    //打印一个二维码的内容为： 01234567
    //1）居中对齐：1B 61 01
    private byte[] CenterCMD = {0x1b, 0x61, 0x01};
    //2）QR码的模块类型:  1D 28 6B 03 00 31 43 03
    private byte[] typeQRCode = {0x1d,0x28,0x6b,0x03,0x00,0x31,0x43,0x03};
    //3）QR码的错误校正水平误差： 1D 28 6B 03 00 31 45 30
    private byte[] correctQRCode = { 0x1d,0x28,0x6b,0x03,0x00,0x31,0x45,0x30 };
    //4）打印QR码：1D 6B 61 08 02 08 00 30 31 32 33 34 35 36 37
    //1D  6B  61  v  r  nl  nH d1…dk
    private byte[] printQRCode = {0x1d,0x6b,0x61,0x08,0x02};

    //常规
    private byte[] printDataCMD = {0x0a}; //打印并换行
    private byte[] cutPaperCMD = { 0x1d, 0x56, 0x41, 0x00 }; //0x42:半切  0x41:全切

    //设置居中大字符
    //1B40:初始化 1C26:设置汉字模式 1B6101:文字居中 1D2111:大字符
    private byte[] CenterLargeCN = {0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x61, 0x01, 0x1d, 0x21, 0x11};

    //设置居中正常中文字符
    private byte[] CenterNormalCN = {0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x61, 0x01};

    //设置左对齐正常中文字符
    private byte[] LeftNormalCN = {0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x61, 0x00};

    //分割线 Default Setting (Created by 董工)
    private byte[] splitLine = {0x1b, 0x40, 0x1c, 0x26, 0x1b, 0x61, 0x01,
                                0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D,
                                0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D,
                                0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D,
                                0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D, 0x2D,
                                0x2D, 0x2D, 0x2D, 0x0a};

    //[Large]: 居中大字符
    //[Small]: 居中正常字符
    //[LeftSmall]: 左对齐正常字符
    private String printTemplate;

    /***********************************************************************************/
    //初始化打印机
    private void printerInit() {
        int fd = jPrinterInit();//连接打印机
        if (fd < 0) {
            Log.i(TAG, "Device init ERR!");
        }
    }

    public void PrintTicket(PrinterKeys msg, String printTemplate){
        this.printTemplate=printTemplate;
        printerInit();
        parsePrintMessage(initPrintMessage(msg));
    }

    /***********************************************************************************/

    private String initPrintMessage(PrinterKeys msg){
        String printData = printTemplate;
        printData=printData.replace("[DeviceNumber]", msg.getDeviceNumber());
        printData=printData.replace("[TicketName]", msg.getTicketName());
        printData=printData.replace("[Price]", msg.getPrice());
        printData=printData.replace("[TicketNumber]", msg.getTicketNumber());
        printData=printData.replace("[PayType]", msg.getPayType());
        printData=printData.replace("[DateTime]", msg.getDateStr());
        return printData;
    }

    private void parsePrintMessage(String printData){
        String[] msgArray = printData.split("\n");
        for(String msgStr:msgArray){
            String[] tempMsg=msgStr.split("->");
            if(tempMsg.length>1){
                switch (tempMsg[0]){
                    case "[CenterLarge]":
                        if(tempMsg[1].length()!=0)
                            printLargeCN(tempMsg[1]);
                        break;
                    case "[CenterSmall]":
                        if(tempMsg[1].length()!=0)
                            printNormalCN(tempMsg[1]);
                        break;
                    case "[LeftSmall]":
                        if(tempMsg[1].length()!=0)
                            printLeftNomalCN(tempMsg[1]);
                        break;
                }
            }else{
                switch (tempMsg[0]){
                    case "[Enter]":
                        printEnter();
                        break;
                    case "[SplitLine]":
                        printSplitLine();
                        break;
                }
            }
        }
        printQRCode(QRCodeUtil.getInstance().getTicketQRCodeData());
        completePrint();//切割纸张
    }

    /***********************************************************************************/

    //结束打印，全切
    private void completePrint(){
        jPrinterDataSend(cutPaperCMD, cutPaperCMD.length);
    }

    //打印并换行
    private void printEnter(){
        jPrinterDataSend(printDataCMD, printDataCMD.length);
    }

    //打印虚线
    private void printSplitLine(){
        jPrinterDataSend(splitLine, splitLine.length);
    }

    //打印大字符 居中
    private void printLargeCN(String targetStr){
        byte[] printData=getGbk(targetStr);
        byte[] outputData=setPrintLargeCN(printData);
        jPrinterDataSend(outputData, outputData.length);
    }

    //拼接打印指令 居中
    private byte[] setPrintLargeCN(byte[] inputCN){
        return joinBytes(joinBytes(CenterLargeCN,inputCN), printDataCMD);
    }

    //打印默认大小字符 居中
    private void printNormalCN(String targetStr){
        byte[] printData=getGbk(targetStr);
        byte[] outputData=setPrintNormalCN(printData);
        jPrinterDataSend(outputData, outputData.length);
    }

    //拼接打印指令 居中
    private byte[] setPrintNormalCN(byte[] inputCN){
        return joinBytes(joinBytes(CenterNormalCN,inputCN), printDataCMD);
    }

    //打印默认大小字符 左对齐
    private void printLeftNomalCN(String targetStr){
        byte[] printData=getGbk(targetStr);
        byte[] outputData=setPrintLeftNormalCN(printData);
        jPrinterDataSend(outputData, outputData.length);
    }

    //拼接打印指令 左对齐
    private byte[] setPrintLeftNormalCN(byte[] inputCN){
        return joinBytes(joinBytes(LeftNormalCN,inputCN), printDataCMD);
    }

//    //1）居中对齐：1B 61 01
//    private byte[] CenterCMD = {0x1b, 0x61, 0x01};
//    //2）QR码的模块类型:  1D 28 6B 03 00 31 43 03
//    private byte[] typeQRCode = {0x1d,0x28,0x6b,0x03,0x00,0x31,0x43,0x03};
//    //3）QR码的错误校正水平误差： 1D 28 6B 03 00 31 45 30
//    private byte[] correctQRCode = { 0x1d,0x28,0x6b,0x03,0x00,0x31,0x45,0x30 };
//    //4）打印QR码：1D 6B 61 08 02 08 00 30 31 32 33 34 35 36 37
//    //1D  6B  61  v  r  nl  nH d1…dk
//    private byte[] printQRCode = {0x1d,0x6b,0x61,0x08,0x02};

    //打印二维码
    private void printQRCode(byte[] dataQRCode){
        if(dataQRCode!=null && dataQRCode.length>0) {
            Log.i(TAG, "Print Ticket QR Code");
            jPrinterDataSend(CenterCMD,CenterCMD.length);
            jPrinterDataSend(typeQRCode,typeQRCode.length);
            jPrinterDataSend(correctQRCode,correctQRCode.length);
            byte[] outputData=joinBytes(printQRCode, dataQRCode);
            jPrinterDataSend(outputData, outputData.length);
        }
    }

    /***********************************************************************************/
    //把文字转换成字节
    private byte[] getGbk(String targetStr) {
        byte[] bytes = null;
        try {
            bytes = targetStr.getBytes("GBK");
        } catch (Exception ex) {
            Log.e(TAG,ex.getMessage());
        }
        return bytes;
    }

    //连接两段字节
    private byte[] joinBytes(byte[] b1, byte[] b2){
        byte[] bs=new byte[b1.length+b2.length];
        for(int i=0; i<bs.length;i++){
            if(i<b1.length){
                bs[i]=b1[i];
            }else{
                bs[i]=b2[i-b1.length];
            }
        }
        return bs;
    }

}
