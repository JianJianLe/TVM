package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.util.device.PrinterCase;
import com.tvm.tvm.util.device.PrinterKeys;
import com.tvm.tvm.util.device.TimeUtil;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/19
 * - @Time： 17:55
 */
public class PayFinishActivity extends BaseActivity {

    @BindView(R.id.tv_pay_finish_remain)
    TextView tv_pay_finish_remain;

    @BindView(R.id.tv_pay_finish_continue)
    TextView tv_pay_finish_continue;

    @BindView(R.id.tv_pay_finish_print)
    TextView tv_pay_finish_print;

    @BindView(R.id.tv_pay_finish_company_name)
    TextView tv_pay_finish_company_name;

    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_finish);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        initData();
    }

    @OnClick({R.id.tv_pay_finish_continue,R.id.tv_pay_finish_print})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_pay_finish_continue:
                //继续购票
                backToBuyTicket();
                break;
            case R.id.tv_pay_finish_print:
                //打印余额
                tv_pay_finish_remain.setText("0.0");
                printBalance();
                break;
        }
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
            }
        }
    };

    //@Star Print Balance 08Apr
    private void printBalance(){
        new Thread(){
            public void run() {
                double balance= PrinterCase.getInstance().balanceRecord;
                if (balance!=0){
                    balanceTicketSettings(balance);
                    PrinterCase.getInstance().print();
                    TimeUtil.delay(1000);
                }
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            };
        }.start();
    }

    //@Star Print Balance 08Apr
    private void balanceTicketSettings(double balance){
        String currentTime =TimeUtil.dateFormat.format(new Date());
        PrinterKeys balanceMsg = PrinterCase.getInstance().msg;
        balanceMsg.setPrice(balance +"");
        balanceMsg.setTicketName("余额票");
        balanceMsg.setDateStr(currentTime);
    }

    public void initData() {
        tv_pay_finish_company_name.setText(setting.getShopName());
        tv_pay_finish_remain.setText(PrinterCase.getInstance().balanceRecord+"");
    }

    private void backToBuyTicket(){
        PriceDao priceDao = daoSession.getPriceDao();
        List<Price> priceList = priceDao.queryBuilder().where(PriceDao.Properties.IsDelete.eq(0)).list();
        if(priceList.size()>2)
            startActivity(this,SelectPriceActivity.class);
        else
            startActivity(this,MainActivity.class);
        this.finish();
    }

    //@Star Main Activity
    private void gotoMainActivity(){
        PrinterCase.getInstance().balanceRecord=0d;
        startActivity(this,MainActivity.class);
        this.finish();
    }
}
