package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.util.BackPrevious;
import com.tvm.tvm.util.device.printer.PrinterCase;
import com.tvm.tvm.util.device.printer.NormalTicket;
import com.tvm.tvm.util.TimeUtil;

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
    //返回上一级倒计时
    private BackPrevious backPrevious;

    private boolean isContinueed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_finish);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        initData();
        backPrevious = new BackPrevious(setting.getPrintTimeOut()*1000,1000,PayFinishActivity.this);
    }

    @OnClick({R.id.tv_pay_finish_continue,R.id.tv_pay_finish_print})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_pay_finish_continue:
                //继续购票
                isContinueed=true;
                backToBuyTicket();
                break;
            case R.id.tv_pay_finish_print:
                //打印余额
                tv_pay_finish_remain.setText("0");
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
            }
        }.start();
    }

    private void printBalanceAfterTimeOut(){
        new Thread(){
            public void run() {
                double balance= PrinterCase.getInstance().balanceRecord;
                if (balance!=0){
                    Log.i("Test","Print Balance After Time Out");
                    balanceTicketSettings(balance);
                    PrinterCase.getInstance().print();
                    TimeUtil.delay(1000);
                }
            }
        }.start();
    }

    //@Star Print Balance 08Apr
    private void balanceTicketSettings(double balance){
        String currentTime =TimeUtil.dateFormat.format(new Date());
        NormalTicket balanceTicket = PrinterCase.getInstance().normalTicket;
        balanceTicket.setPrice(balance +"");
        balanceTicket.setTicketName("余额票");
        balanceTicket.setDateStr(currentTime);
    }

    public void initData() {
        isContinueed=false;
        tv_pay_finish_company_name.setText(setting.getShopName());
        tv_pay_finish_remain.setText((int)PrinterCase.getInstance().balanceRecord+"");
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

    @Override
    protected void onResume() {
        super.onResume();
        timeStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backPrevious.cancel();
    }

    //region 无操作 返回上一页
    private void timeStart() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                backPrevious.start();
            }
        });
    }

    //关掉延迟服务
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(!isContinueed && PrinterCase.getInstance().balanceRecord>0){
            tv_pay_finish_remain.setText("0");
            printBalanceAfterTimeOut();
            PrinterCase.getInstance().balanceRecord=0d;//reset balance after print balance ticket.
        }
        Log.i("Test","PayFinishActivity onDestroy scheduledExecutorService shutdown");
    }

    /**
     * 主要的方法，重写dispatchTouchEvent
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //获取触摸动作，如果ACTION_UP，计时开始。
            case MotionEvent.ACTION_UP:
                backPrevious.start();
                break;
            //否则其他动作计时取消
            default:
                backPrevious.cancel();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
