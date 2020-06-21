package com.tvm.tvm.util.device.printer;

import android.util.Log;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.QRCodeUtil;
import com.tvm.tvm.util.device.paydevice.LYYDevice;
import com.tvm.tvm.util.device.paydevice.WMQDevice;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrinterAction {

    private Long priceId;
    private DaoSession daoSession;
    private List<TicketBean> ticketList;
    private double ticketPrice;
    private String ticketTitle;
    private String ticketDescription;
    private int payType;
    private NormalTicket normalTicket;


    public PrinterAction(){
        ticketList=PrinterCase.getInstance().ticketList;
        daoSession = AppApplication.getApplication().getDaoSession();
        normalTicket = PrinterCase.getInstance().normalTicket;
    }

    public void PrintTicket(){
        new Thread(){
            public void run() {
                PrintTicketList();
            }
        }.start();
    }

    public void PrintTicketList(){
        normalTicket.setDeviceNumber(getDeviceNO());
        for (TicketBean bean:ticketList){
            ticketPrice=bean.getPrice();//价格
            ticketDescription=bean.getDescription().replace("（","(").replace("）",")");//描述
            priceId=bean.getId();
            for(int i=0; i<bean.getNumber();i++){
                ticketTitle=bean.getTitle();//标题
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

                if(IsPackage(ticketDescription)){
                    List<String> packageList=getPackageList(ticketDescription);
                    for(int index=0; index<packageList.size();index++){
                        ticketTitle=getPackageTicketName(packageList.get(index),bean.getTitle());
                        Log.i("Test","updated ticketTitle="+ticketTitle);
                        normalTicket.setTicketName(ticketTitle);
                        PrintTargetTicket(bean);
                    }
                }else{
                    PrintTargetTicket(bean);
                }


            }
        }
    }


    private void PrintTargetTicket(TicketBean bean){
        //上报交易结果
        if(payType==StringUtils.OnlinePay){
            if(PreConfig.PayDeviceName.equals("LYY"))
                LYYDevice.getInstance().cmd_OnlinePayReport(bean);
            else
                WMQDevice.getInstance().cmd_UploadOnlinePayReuslt(bean,payType);
        }else{
            if(PreConfig.PayDeviceName.equals("LYY"))
                LYYDevice.getInstance().cmd_CashReport(bean);
            else
                WMQDevice.getInstance().cmd_UploadOnlinePayReuslt(bean,payType);
        }
        PrinterCase.getInstance().print();
        TimeUtil.delay(3000);
    }


    //@Star 2020/06/14
    private String getPackageTicketName(String strVal,String strTitle){
        return strVal.trim() + "(" + strTitle + ")";
    }

    private boolean IsPackage(String strDesc){
        return IsContainedByRegex(strDesc,"\\(.+.\\)");
    }

    private List<String> getPackageList(String strDesc){
        String strPackage=getPackageStr(strDesc);
        return Arrays.asList(strPackage.split("\\+"));
    }

    private String getPackageStr(String strDesc){
        return getContentByRegex(strDesc,"(?<=\\().+.(?=\\))");
    }

    private boolean IsContainedByRegex(String content, String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m =r.matcher(content);
        return m.find();
    }

    private String getContentByRegex(String source,String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher((source));
        if(m.find())
            return m.group(0);
        else
            return null;
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
        payType=paymentRecord.getTypeNumber(normalTicket.getPayType());
        paymentRecord.setType(payType);
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
