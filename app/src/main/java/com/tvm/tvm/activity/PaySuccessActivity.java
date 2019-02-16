package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.device.PrinterCase;
import com.tvm.tvm.util.device.PrinterMessage;
import com.tvm.tvm.util.device.TimeUtil;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.Date;

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
        PrinterMessage msg = PrinterCase.getInstance().msg;
        msg.setShopNumber("000001");
        msg.setTicketNumber(getTicketNumber());
        msg.setTicketName(getTicketName());
        msg.setDateStr(TimeUtil.dateFormat.format(new Date()));
        msg.setTicketDesc(PrinterCase.getInstance().ticketDesc);
    }

    private String getTicketNumber(){
        int orderNum=PrinterCase.getInstance().ticketNumRecord;
        orderNum++;
        PrinterCase.getInstance().ticketNumRecord=orderNum;
        return OrderDispose(orderNum) ;
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

    // 处理顺序号，只支持1000以内
    private String OrderDispose(int OrderData) {
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
