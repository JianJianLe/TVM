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
import com.tvm.tvm.util.device.printer.PrinterAction;
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

    private PrinterAction printerAction;
    //传递过来得票价id
//    private Long priceId;
//    private DaoSession daoSession;
//    private List<TicketBean> ticketList;
//    private double ticketPrice;
//    private String ticketTitle;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ButterKnife.bind(this);
        printerAction=new PrinterAction();
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
                printerAction.PrintTicketList();
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
