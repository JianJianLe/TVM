package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.util.device.printer.PrinterCase;
import com.tvm.tvm.util.device.printer.PrinterKeys;
import com.tvm.tvm.util.TimeUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/19
 * - @Time： 19:08
 */
public class PaySuccessActivity extends BaseActivity {

    @BindView(R.id.tv_pay_success_company_name)
    TextView tv_pay_success_company_name;

    //传递过来得票价id
    private Long priceId;
    private DaoSession daoSession;
    private List<TicketBean> ticketList;
    private double ticketPrice;
    private String ticketTitle;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        ticketList = (List<TicketBean>) getIntent().getSerializableExtra("list");
        initData();
        printTicket();
    }

    //@Star Feb16
    //用于打印票据以及返回主界面
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    gotoMainActivity();
                    break;
                case 1:
                    gotoPayFinishActivity();
                    break;
            }
        }
    };

    public void initData(){
        tv_pay_success_company_name.setText(setting.getShopName());
    }

    private void printTicket(){
        new Thread(){
            public void run() {
                printTicketList();
                double balance= PrinterCase.getInstance().balanceRecord;
                if(balance!=0){
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }else{
                    Message message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }
        }.start();
    }

    private void printTicketList(){

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

    //@Star Main Activity
    private void gotoMainActivity(){
        startActivity(this,MainActivity.class);
        this.finish();
    }

    //@Star PayFinishActivity
    private void gotoPayFinishActivity(){
        startActivity(this,PayFinishActivity.class);
        this.finish();
    }
}
