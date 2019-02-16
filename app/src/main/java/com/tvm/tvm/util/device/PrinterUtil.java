package com.tvm.tvm.util.device;

import android.util.Log;

public class PrinterUtil {

    static {
        System.loadLibrary("printer");
    }

    //JNI
    private native int jPrinterInit();
    private native void jPrinterDataSend(byte[] buffer, int data_len);
    private native static String getMessageFromJNI();//JNI Test

    private String TAG = "Test";

    //行间距,打印二维码时可以使用（00: 无间距, Default: n=3）设置行间距为(n*0.125 毫米)
    private byte[] lineSpaceZero = {0x1b, 0x33, 0x00};
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
    private String printTemplate = "[Large]>[Title]\n" +
            "[Small]>[Title Description]\n" +
            "[Small]>本店编号：[ShopNumber]\n" +
            "[Large]>[TicketName]\n" +
            "[Large]>价格：[Price]元\n" +
            "[Large]>[TicketNumber]\n" +
            "[Small]>支付方式：[PayType]\n" +
            "[Small]>[Date]\n" +
            "[SplitLine]\n" +
            "[Description]\n" +
            "[Enter]\n" +
            "[Enter]\n" +
            "[Enter]";

    /***********************************************************************************/
    //初始化打印机
    private void printerInit() {
        int fd = jPrinterInit();//连接打印机
        if (fd < 0) {
            Log.i(TAG, "Device init ERR!");
        }
    }

    public void PrintTicket(PrinterMessage msg){
        printerInit();
        parsePrintMessage(initPrintMessage(msg));
    }

    /***********************************************************************************/

    private String initPrintMessage(PrinterMessage msg){
        String printData = printTemplate;
        printData=printData.replace("[Title]", msg.getTitle());
        printData=printData.replace("[Title Description]", msg.getTitleDesc());
        printData=printData.replace("[ShopNumber]", msg.getShopNumber());
        printData=printData.replace("[TicketName]", msg.getTicketName());
        printData=printData.replace("[Price]", msg.getPrice());
        printData=printData.replace("[TicketNumber]", msg.getTicketNumber());
        printData=printData.replace("[PayType]", msg.getPayType());
        printData=printData.replace("[Date]", msg.getDateStr());
        printData=printData.replace("[Description]", msg.getTicketDesc());
        return printData;
    }

    private void parsePrintMessage(String printData){
        String[] msgArray = printData.split("\n");
        for(int i=0; i<msgArray.length;i++){
            String[] tempMsg=msgArray[i].split(">");
            if(tempMsg.length>1){
                switch (tempMsg[0]){
                    case "[Large]":
                        if(tempMsg[1].length()!=0)
                            printLargeCN(tempMsg[1]);
                        break;
                    case "[Small]":
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

    //将byte[]转换为String  [For Testing]
    private String printHexString( byte[] b) {
        String temp=null;
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            temp=temp + hex.toUpperCase();
        }
        return temp;
    }

}
