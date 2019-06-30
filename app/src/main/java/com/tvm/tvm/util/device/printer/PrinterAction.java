package com.tvm.tvm.util.device.printer;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.device.QRCodeUtil;

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
        NormalTicket normalTicket = PrinterCase.getInstance().normalTicket;
        normalTicket.setDeviceNumber(getDeviceNO());
        for (TicketBean bean:ticketList){
            ticketPrice=bean.getPrice();//价格
            ticketTitle=bean.getTitle();//标题
            priceId=bean.getId();
            for(int i=0; i<bean.getNumber();i++){
                String currentTime=TimeUtil.dateFormat.format(new Date());
                normalTicket.setTicketNumber(PrinterCase.getInstance().getTicketNumber(currentTime));
                normalTicket.setTicketName(ticketTitle);
                normalTicket.setPrice((int)ticketPrice+"");
                normalTicket.setDateStr(currentTime);
                //QR code information
                QRCodeUtil.getInstance().setTimeData(currentTime);
                QRCodeUtil.getInstance().setPriceStr((int)ticketPrice+"");
                QRCodeUtil.getInstance().setTicketQRCodeData();
                //Save payment information to DB
                saveTicketInfo(currentTime,Integer.parseInt(normalTicket.getTicketNumber()));
                savePayment(PrinterCase.getInstance().normalTicket);
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
    private void savePayment(NormalTicket normalTicket){
        PaymentRecordDao paymentRecordDao=daoSession.getPaymentRecordDao();
        PaymentRecord paymentRecord=new PaymentRecord();
        paymentRecord.setAmount(Double.valueOf(ticketPrice));
        paymentRecord.setNum(1);
        paymentRecord.setPrice(Double.valueOf(ticketPrice));
        paymentRecord.setPriceId(priceId);
        paymentRecord.setTitle(ticketTitle);
        paymentRecord.setPayTime(TimeUtil.getDate(normalTicket.getDateStr()));
        paymentRecord.setType(paymentRecord.getTypeNumber(normalTicket.getPayType()));
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
