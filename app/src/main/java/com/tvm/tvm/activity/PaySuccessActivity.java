package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.bean.dao.TicketSummaryDao;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.device.PrinterCase;
import com.tvm.tvm.util.device.PrinterKeys;
import com.tvm.tvm.util.device.TimeUtil;

import java.text.ParseException;
import java.util.Calendar;
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
    private List<TicketSummary> ticketSummaryList;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        priceId = getIntent().getLongExtra("priceId",0l);
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
                    gotoNextActivity();
                    break;
            }
        }
    };

    public void initData(){
        String companyName = SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.COMPANY_NAME,"");
        tv_pay_success_company_name.setText(companyName);
    }

    private void printTicket(){
        new Thread(){
            public void run() {
                int num =PrinterCase.getInstance().numRecord;
                for(int i=0;i<num;i++){
                    ticketSettings();
                    PrinterCase.getInstance().print();
                    TimeUtil.delay(3000);
                }
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            };
        }.start();
    }

    private void ticketSettings(){
        String currentTime =TimeUtil.dateFormat.format(new Date());
        PrinterKeys msg = PrinterCase.getInstance().msg;
        msg.setDeviceNumber("000001");
        msg.setTicketName(getTicketName());
        msg.setTicketNumber(getTicketNumber(currentTime));
        msg.setDateStr(currentTime);
    }

    private String getTicketNumber(String currentTime){
        int orderNum=getPreTicketNumber();
        orderNum++;
        saveTicketInfo(currentTime,orderNum);
        return PrinterCase.getInstance().OrderDispose(orderNum) ;
    }

    private int getPreTicketNumber(){
        TicketSummaryDao ticketSummaryDao = daoSession.getTicketSummaryDao();
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
        if(isToday(ticketTime))
            return ticket.getNum();
        else
            return 0;
    }

    private void saveTicketInfo(String currentTime, int orderNum){
        TicketSummary ticketSummary=new TicketSummary();
        ticketSummary.setDate(currentTime);
        ticketSummary.setNum(orderNum);
        daoSession.getTicketSummaryDao().save(ticketSummary);
    }

    private String getTicketName(){
        PriceDao priceDao = daoSession.getPriceDao();
        Price price=priceDao.queryBuilder().where(PriceDao.Properties.Id.eq(priceId)).unique();
        return price.getTitle();
    }

    private void gotoNextActivity(){
        startActivity(this,MainActivity.class);
        this.finish();
    }

    private boolean isToday(String dateStr) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(TimeUtil.getDate(dateStr));
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH)+1;
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        int year2 = c2.get(Calendar.YEAR);
        int month2 = c2.get(Calendar.MONTH)+1;
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        if(year1 == year2 && month1 == month2 && day1 == day2){
            return true;
        }
        return false;
    }
}
