package com.tvm.tvm.util.device.printer;

import android.util.Log;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.BillSetting;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.BillSettingDao;
import com.tvm.tvm.bean.dao.TicketSummaryDao;
import com.tvm.tvm.util.TimeUtil;

import java.util.Date;
import java.util.List;

public class PrinterCase {

    public double amountRecord=0;//总共多少金额
    public double balanceRecord=0;//余额
    public List<TicketBean> ticketList;
    public NormalTicket normalTicket = new NormalTicket();
    public SummaryTicket summaryTicket= new SummaryTicket();

    private List<TicketSummary> ticketSummaryList;

    private String printTemplate = "[CenterLarge]->快剪\n" +
            "[CenterSmall]->欢迎光临快剪专营店\n" +
            "[CenterSmall]->本店编号：[DeviceNumber]\n" +
            "[CenterLarge]->[TicketName]\n" +
            "[CenterLarge]->价格：[Price]元\n" +
            "[CenterLarge]->[TicketNumber]\n" +
            "[CenterSmall]->支付方式：[PayType]\n" +
            "[CenterSmall]->[DateTime]\n" +
            "[SplitLine]\n" +
            "[LeftSmall]->1.此凭条为儿童（身高1.4米以下）\n" +
            "[LeftSmall]->  剪发专用凭证；\n" +
            "[LeftSmall]->2.凭此凭条可以在本店享受专业剪\n" +
            "[LeftSmall]->  发一次，复印无效；\n" +
            "[LeftSmall]->3.本凭条仅可在购买本店使用；\n" +
            "[LeftSmall]->4.此凭条不记名，不挂失，不能兑\n" +
            "[LeftSmall]->  换现金，用完即止；\n" +
            "[LeftSmall]->5.此凭条从购买之日起，有效期为\n" +
            "[LeftSmall]->  当天，过期作废；\n" +
            "[LeftSmall]->6.本公司可能在法律允许范围内对\n" +
            "[LeftSmall]->  此细则作出适当调整。\n" +
            "[Enter]\n" +
            "[Enter]\n" +
            "[Enter]";

    private static PrinterCase instance;

    public synchronized static PrinterCase getInstance(){
        if (instance==null) {
            instance = new PrinterCase();
        }
        return instance;
    }

    public void printerCaseTest(){
        NormalTicket normalTicket = new NormalTicket();
        normalTicket.setDeviceNumber("0001");
        normalTicket.setTicketName("儿童票");
        normalTicket.setPrice("10");
        normalTicket.setTicketNumber("001");
        normalTicket.setPayType("现金");
        Log.i("Test",TimeUtil.dateFormat.format(new Date()));
        normalTicket.setDateStr("2019-01-27 17:00:00");
        PrinterUtil printerUtil=new PrinterUtil();
        printerUtil.PrintTicket(normalTicket,printTemplate);
    }

    public void print(){
        PrinterUtil printerUtil=new PrinterUtil();
        printerUtil.PrintTicket(normalTicket,getTicketTemplate());
    }

    private String getTicketTemplate(){
        BillSettingDao billSettingDao = AppApplication.getApplication().getDaoSession().getBillSettingDao();
        BillSetting billSetting=billSettingDao.queryBuilder().where(
                                    BillSettingDao.Properties.TicketName.like(
                                            "[TicketName]="+ normalTicket.getTicketName())
                                    ).unique();
        return billSetting.getTicketBody();
    }

    public boolean checkTicketTemplate(){
        BillSettingDao billSettingDao = AppApplication.getApplication().getDaoSession().getBillSettingDao();
        return billSettingDao.count()>0?true:false;
    }

    //@Star 获取Order number
    public String getTicketNumber(String currentTime){
        int orderNum=getPreTicketOrderNumber();
        orderNum++;
        return OrderDispose(orderNum) ;
    }

    //@Star 09Apr
    public String getCurrentTicketNumber(){
        int orderNum=getPreTicketOrderNumber();
        if(orderNum==0)
            return "001";
        else
            return OrderDispose(getPreTicketOrderNumber());
    }

    //@Star 获取数据库里面的最新的Order Number
    private int getPreTicketOrderNumber(){
        TicketSummaryDao ticketSummaryDao = AppApplication.getApplication().getDaoSession().getTicketSummaryDao();
        ticketSummaryList = ticketSummaryDao.queryBuilder().list();
        if (ticketSummaryList.size()==0){
            return 0;
        }else {
            return getNumByTime();
        }
    }

    //如果是新的一天，则TicketNumber清零
    private int getNumByTime(){
        TicketSummary ticket = ticketSummaryList.get(ticketSummaryList.size()-1);
        String ticketTime=ticket.getDate();
        if(TimeUtil.isToday(ticketTime))
            return ticket.getNum();
        else
            return 0;
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
