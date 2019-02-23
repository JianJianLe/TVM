package com.tvm.tvm.util.device;

import android.util.Log;

import java.util.Date;

public class PrinterCase {

    public double amountRecord=0;//总共多少金额
    public double balanceRecord=0;//余额
    public int numRecord=0;//一共打印多少张票

    public PrinterKeys msg = new PrinterKeys();

    private static PrinterCase instance;

    public synchronized static PrinterCase getInstance(){
        if (instance==null) {
            instance = new PrinterCase();
        }
        return instance;
    }

    public void printerCaseTest(){
        PrinterKeys msg = new PrinterKeys();
        msg.setDeviceNumber("0001");
        msg.setTicketName("儿童票");
        msg.setPrice("10");
        msg.setTicketNumber("001");
        msg.setPayType("现金");
        Log.i("Test",TimeUtil.dateFormat.format(new Date()));
        msg.setDateStr("2019-01-27 17:00:00");
        PrinterUtil printerUtil=new PrinterUtil();
        printerUtil.PrintTicket(msg);
    }

    public void print(){
        PrinterUtil printerUtil=new PrinterUtil();
        printerUtil.PrintTicket(msg);
    }

    // 处理顺序号，只支持1000以内
    public String OrderDispose(int OrderData) {
        String reOrder = "";
        if (OrderData < 10) {
            String s = String.valueOf(OrderData);
            reOrder = "0" + "0" + s;
        }
        if ((OrderData >= 10) && (OrderData < 100)) {
            String s = String.valueOf(OrderData);
            reOrder ="0" + s;
        }
        if(OrderData >=100) {
            String s = String.valueOf(OrderData);
            reOrder=s;
        }
        return reOrder;
    }
}
