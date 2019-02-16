package com.tvm.tvm.util.device;

import android.util.Log;

import java.util.Date;

public class PrinterCase {

    public double amountRecord=0;//总共多少金额
    public double balanceRecord=0;//余额
    public int numRecord=0;//一共打印多少张票
    public int ticketNumRecord=0;

    public String ticketDesc="" +
            "[LeftSmall]>1.此凭条为儿童（身高1.4米以下）\n" +
            "[LeftSmall]>  剪发专用凭证；\n" +
            "[LeftSmall]>2.凭此凭条可以在本店享受专业剪\n" +
            "[LeftSmall]>  发一次，复印无效；\n" +
            "[LeftSmall]>3.本凭条仅可在购买本店使用；\n" +
            "[LeftSmall]>4.此凭条不记名，不挂失，不能兑\n" +
            "[LeftSmall]>  换现金，用完即止；\n" +
            "[LeftSmall]>5.此凭条从购买之日起，有效期为\n" +
            "[LeftSmall]>  当天，过期作废；\n" +
            "[LeftSmall]>6.本公司可能在法律允许范围内对\n" +
            "[LeftSmall]>  此细则作出适当调整。";

    public PrinterMessage msg = new PrinterMessage();

    private static PrinterCase instance;

    public synchronized static PrinterCase getInstance(){
        if (instance==null) {
            instance = new PrinterCase();
        }
        return instance;
    }

    public void printerCaseTest(){
        PrinterMessage msg = new PrinterMessage();
        msg.setTitle("快剪");
        msg.setTitleDesc("欢迎光临快剪专营店");
        msg.setShopNumber("0001");
        msg.setTicketName("儿童票");
        msg.setPrice("10");
        msg.setTicketNumber("001");
        msg.setPayType("现金");
        Log.i("Test",TimeUtil.dateFormat.format(new Date()));
        msg.setDateStr("2019-01-27 17:00:00");
        msg.setTicketDesc(ticketDesc);
        PrinterUtil printerUtil=new PrinterUtil();
        printerUtil.PrintTicket(msg);
    }

    public void print(){
        msg.setTitle("快剪");
        msg.setTitleDesc("欢迎光临快剪专营店");
//        msg.setShopNumber(keys.getShopNumber());
//        msg.setTicketName(keys.getTicketName());
//        msg.setPrice(keys.getPrice());
//        msg.setTicketNumber(keys.getTicketNumber());
//        msg.setPayType(keys.getPayType());
//        msg.setDateStr(keys.getDateStr());
//        msg.setTicketDesc(ticketDesc);
        PrinterUtil printerUtil=new PrinterUtil();
        printerUtil.PrintTicket(msg);
    }

}
