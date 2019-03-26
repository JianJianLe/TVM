package com.tvm.tvm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.util.BackPrevious;
import com.tvm.tvm.util.device.BillAcceptorUtil;
import com.tvm.tvm.util.device.PrinterCase;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayDetailActivity extends BaseActivity{

    //返回键
    @BindView(R.id.iv_pay_detail_back)
    ImageView iv_pay_detail_back;

    //支付二维码
    @BindView(R.id.iv_pay_detail_qr_code)
    ImageView iv_pay_detail_qr_code;

    //票数
    @BindView(R.id.tv_pay_detail_num)
    TextView tv_pay_detail_num;

    //描述
    @BindView(R.id.tv_pay_detail_desc)
    TextView tv_pay_detail_desc;

    //支付金额
    @BindView(R.id.tv_pay_detail_pay_amount)
    TextView tv_pay_detail_pay_amount;

    //已收金额
    @BindView(R.id.tv_pay_detail_receive_amount)
    TextView tv_pay_detail_receive_amount;

    //未收金额
    @BindView(R.id.tv_pay_detail_left_amount)
    TextView tv_pay_detail_left_amount;

    //单价
    private double ticketPrice = 0d;
    //已收钱
    private double receivedAmount = 0d;
    //剩余应收钱
    private double leftAmount = 0d;
    //票数
    private int num = 0;

    private DaoSession daoSession;

    private ScheduledExecutorService scheduledExecutorService;
    //传递的票列表
    private List<TicketBean> ticketList;
    //返回上一级倒计时
    private BackPrevious backPrevious;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        ticketList = (List<TicketBean>) getIntent().getSerializableExtra("list");
        initData();
    }

    /**
     - @Description: 监控纸钞机金额，监控支付结果，判断是否支付成功，
     -               如果是，则去到支付成功界面并打印小票
     - @Author:  Star 2019/02/16
     - @Date:  ${DATE}
     - @Time： ${TIME}
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    cashPay();
                    checkPayResult();
                    break;
            }
        }
    };

    //检测支付结果 @Star
    private void checkPayResult(){
        //amountRecord可以为现金支付，也可以为微信，支付宝支付
        if(PrinterCase.getInstance().amountRecord>0){
            double balance=PrinterCase.getInstance().amountRecord - (num * ticketPrice);
            if(balance>=0){
                PrinterCase.getInstance().balanceRecord=balance;
                PrinterCase.getInstance().numRecord=num;//几张票
                PrinterCase.getInstance().amountRecord=0;
                //@Star goto Next Activity
                Intent intent = new Intent();
//                intent.putExtra("priceId",priceId);
                intent.putExtra("list", (Serializable) ticketList);
                startActivity(this,intent,PaySuccessActivity.class);
                this.finish();
            }
        }
    }

    //现金支付 @Star
    private void cashPay(){
        int cash=BillAcceptorUtil.getInstance().rcvdMoney;
        if(cash>0){
            PrinterCase.getInstance().amountRecord+=cash;
            receivedAmount = PrinterCase.getInstance().amountRecord;
            updateAmount();
            PrinterCase.getInstance().msg.setPayType("现金");
            BillAcceptorUtil.getInstance().rcvdMoney=0;
            cash=-1;
        }
    }

    /**
     - @Description:  初始化
     - @Author:  Jat
     - @Date:  ${DATE}
     - @Time： ${TIME}
     */
    public void initData(){
        int num = 0;
        double amount = 0d;
        for (TicketBean bean:ticketList){
            num = num+bean.getNumber();
            amount = amount + bean.getNumber()*bean.getPrice();
        }

        amount = new BigDecimal(amount).doubleValue();

        tv_pay_detail_num.setText(num+"");
        tv_pay_detail_pay_amount.setText(amount+"");

        tv_pay_detail_desc.setText(setting.getPayDesc());

        backPrevious = new BackPrevious(setting.getPayTimeOut()*1000,1000,PayDetailActivity.this);
//        PriceDao priceDao = daoSession.getPriceDao();
//        Price price = priceDao.queryBuilder().where(PriceDao.Properties.Id.eq(priceId)).unique();
//        if (price!=null){
//            ticketPrice = price.getPrice();
//            PrinterCase.getInstance().msg.setPrice(String.valueOf((int)ticketPrice));
//        }else {
//            ToastUtils.showText(this,"找不到对应票价");
//        }
    }

    /**
     * 监听函数
     * @param view
     */
    @OnClick({R.id.iv_pay_detail_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_pay_detail_back:
                PrinterCase.getInstance().amountRecord=0;//when the activity is finished, the amountRecord should be 0.
                this.finish();
                break;
        }
    }

    /**
     - @Description:  更新界面和价钱
     - @Author:  Jat
     - @Date:  ${DATE}
     - @Time： ${TIME}
     */
    public void updateAmount(){
        tv_pay_detail_num.setText(String.valueOf(num));
        tv_pay_detail_pay_amount.setText(String.valueOf((int)ticketPrice*num));
        tv_pay_detail_receive_amount.setText(String.valueOf((int)receivedAmount));
        leftAmount = ticketPrice* num - receivedAmount;
        tv_pay_detail_left_amount.setText(String.valueOf((int)leftAmount));
    }


    /**
     * 时间任务 时间更新
     * @author Administrator
     *
     */
    private class TimeTask implements Runnable {
        public void run() {
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
    }

    //开启时执行延迟服务
    @Override
    protected void onStart() {
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //时间更新，一分钟刷新一次
        scheduledExecutorService.scheduleWithFixedDelay(new TimeTask(), 1, 1 ,TimeUnit.SECONDS);
        Log.i("Test","PayDetailActivity onStart scheduledExecutorService open!");
    }

    //关掉延迟服务
    @Override
    public void onDestroy(){
        super.onDestroy();
        scheduledExecutorService.shutdown();
        BillAcceptorUtil.getInstance().ba_Disable();//@Star Feb16
        Log.i("Test","PayDetailActivity onDestroy scheduledExecutorService shutdown");
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
