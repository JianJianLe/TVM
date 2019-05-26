package com.tvm.tvm.util.device.printer;

import android.os.Handler;
import android.os.Message;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.util.TimeUtil;

import java.util.Date;
import java.util.List;

public class PrinterAction {

    private Long priceId;
    private DaoSession daoSession;
    private List<TicketBean> ticketList;
    private double ticketPrice;
    private String ticketTitle;

    public PrinterAction(){
        ticketList=PrinterCase.getInstance().ticketList;
        daoSession = AppApplication.getApplication().getDaoSession();
    }

    public void PrintTicket(){
        new Thread(){
            public void run() {
                PrintTicketList();
            }
        }.start();
    }

    public void PrintTicketList(){

        PrinterKeys msg = PrinterCase.getInstance().msg;
        msg.setDeviceNumber(getDeviceNO());
        msg.setDateStr(TimeUtil.dateFormat.format(new Date()));
        for (TicketBean bean:ticketList){
            ticketPrice=bean.getPrice();//价格
            ticketTitle=bean.getTitle();//标题
            priceId=bean.getId();
            for(int i=0; i<bean.getNumber();i++){
                String currentTime=TimeUtil.dateFormat.format(new Date());
                msg.setTicketNumber(PrinterCase.getInstance().getTicketNumber(currentTime));
                msg.setTicketName(ticketTitle);
                msg.setPrice(ticketPrice+"");
                saveTicketInfo(currentTime,Integer.parseInt(msg.getTicketNumber()));
                savePayment(PrinterCase.getInstance().msg);
                PrinterCase.getInstance().print();
                TimeUtil.delay(3000);
            }
        }
    }

    //@Star 获取Device No
    private String getDeviceNO(){
        SettingDao settingDao = daoSession.getSettingDao();
        Setting setting = settingDao.queryBuilder().where(SettingDao.Properties.Id.eq(1l)).unique();
        if(setting==null)
            return "NULL";
        else
            return setting.getDeviceNo();
    }

    //@Star 保存支付记录
    private void savePayment(PrinterKeys msg){
        PaymentRecordDao paymentRecordDao=daoSession.getPaymentRecordDao();
        PaymentRecord paymentRecord=new PaymentRecord();
        paymentRecord.setAmount(Double.valueOf(ticketPrice));
        paymentRecord.setNum(1);
        paymentRecord.setPrice(Double.valueOf(ticketPrice));
        paymentRecord.setPriceId(priceId);
        paymentRecord.setTitle(ticketTitle);
        paymentRecord.setPayTime(TimeUtil.getDate(msg.getDateStr()));
        paymentRecord.setType(paymentRecord.getTypeNumber(msg.getPayType()));
        paymentRecordDao.save(paymentRecord);
    }

    //@Star 记录每一笔
    private void saveTicketInfo(String currentTime, int orderNum){
        TicketSummary ticketSummary=new TicketSummary();
        ticketSummary.setDate(currentTime);
        ticketSummary.setNum(orderNum);
        daoSession.getTicketSummaryDao().save(ticketSummary);
    }

}
