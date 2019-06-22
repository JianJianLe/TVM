package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.util.BackPrevious;
import com.tvm.tvm.util.BitmapUtils;
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.QRCodeUtil;
import com.tvm.tvm.util.device.billacceptor.BillAcceptorUtil;
import com.tvm.tvm.util.device.paydevice.PayDeviceUtil;
import com.tvm.tvm.util.device.printer.PrinterCase;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.File;
import java.math.BigDecimal;
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
    //总价
    private double totalAmount=0d;

    private ScheduledExecutorService scheduledExecutorService;
    //传递的票列表
    private List<TicketBean> ticketList;
    //返回上一级倒计时
    private BackPrevious backPrevious;

    private int countGetQRCode=0;
    private File imgQRCodeFile;
    private boolean hasShownQRCode=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PayDeviceUtil.getInstance().activityRecord="PayDetailActivity";
        setContentView(R.layout.activity_pay_detail);
        ButterKnife.bind(this);
        ticketList = PrinterCase.getInstance().ticketList;
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
                    netWorkPay();
                    checkPayResult();
                    break;
                case 1:
                    iv_pay_detail_qr_code.setImageBitmap(BitmapUtils.file2Bitmap(imgQRCodeFile));
                    Log.i("Test", "Completed to show QR Code");
                    hasShownQRCode=true;
                    break;
                case 2:
                    connectFailed();
                    break;
            }
        }
    };

    //检测支付结果 @Star
    private void checkPayResult(){
        //amountRecord可以为现金支付，也可以为线上支付
        if(PrinterCase.getInstance().amountRecord>0){
            double balance=PrinterCase.getInstance().amountRecord - totalAmount;
            if(balance>=0){
                PayDeviceUtil.getInstance().cmd_CashReport((int)totalAmount*100);//Star 2019/05/26
                PrinterCase.getInstance().balanceRecord=balance;
                PrinterCase.getInstance().amountRecord=0;
                //@Star goto Next Activity
                startActivity(this,PaySuccessActivity.class);
                this.finish();
            }
        }
    }

    private void netWorkPay(){
        if(PayDeviceUtil.getInstance().paySuccess){
            PrinterCase.getInstance().amountRecord=totalAmount;
            receivedAmount=totalAmount;
            updateAmount();
            PrinterCase.getInstance().normalTicket.setPayType("线上支付");
            PayDeviceUtil.getInstance().paySuccess=false;
        }
    }

    //现金支付 @Star
    private void cashPay(){
        int cash=BillAcceptorUtil.getInstance().rcvdMoney;
        if(cash>0){
            PrinterCase.getInstance().amountRecord+=cash;
            receivedAmount = PrinterCase.getInstance().amountRecord;
            updateAmount();
            PrinterCase.getInstance().normalTicket.setPayType("现金");
            BillAcceptorUtil.getInstance().rcvdMoney=0;
        }
    }

    /**
     - @Description:  初始化
     - @Author:  Jat
     - @Date:  ${DATE}
     - @Time： ${TIME}
     */
    public void initData(){
        totalAmount=getTotalAmount();
        tv_pay_detail_num.setText(num+" ");
        tv_pay_detail_pay_amount.setText((int)totalAmount+"");
        tv_pay_detail_desc.setText(setting.getPayDesc());
        int balance = (int)PrinterCase.getInstance().balanceRecord;
        if(balance!=0){
            PrinterCase.getInstance().amountRecord=balance;
        }
        receivedAmount=PrinterCase.getInstance().amountRecord;
        updateAmount();
        setQRCode();
        backPrevious = new BackPrevious(setting.getPayTimeOut()*1000,1000,PayDetailActivity.this);
    }

    private void setQRCode(){
        countGetQRCode=0;
        PayDeviceUtil.getInstance().init_QRCode();
        hasShownQRCode=false;
        new Thread(){
            public void run() {
                while (!isInterrupted() && !hasShownQRCode){
                    PayDeviceUtil.getInstance().cmd_GetQRCode((int)(totalAmount*100));
                    TimeUtil.delay(1000);
                    if(PayDeviceUtil.getInstance().QRData!=null){
                        Log.i("Test", "QRData has data");
                        displayQRCode(PayDeviceUtil.getInstance().QRData);
                        break;
                    }

//                    if(PayDeviceUtil.getInstance().QRData!=null){
//                        Log.i("Test", "QRData has data");
//                        break;
//                    }else{
//                        Log.i("Test", "QRData is null");
//                    }
//
//                    if(waitQRCode(15))
//                        break;
//                    countGetQRCode++;
//                    if(countGetQRCode==5){//10 s
//                        //Connect failed.
//                        Message message = new Message();
//                        message.what = 2;
//                        handler.sendMessage(message);
//                        break;
//                    }
                }
            }
        }.start();
    }

    private boolean waitQRCode(int timeCount){
        for(int i=0;i<timeCount;i++){
            TimeUtil.delay(100);
            if(hasShownQRCode){
                return true;
            }
        }
        return false;
    }

    private void displayQRCode(String QRData){
        if(QRData!=null&&QRData.length()>0){
            Log.i("Test","display QR Code:" + QRData);
            imgQRCodeFile = new File(QRCodeUtil.getInstance().getQRCode(this,QRData));
            if(imgQRCodeFile.exists()){
                //Set Image into View
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

        }
    }

    private double getTotalAmount(){
        double amount = 0d;
        for (TicketBean bean:ticketList){
            num = num+bean.getNumber();
            amount = amount + bean.getNumber()*bean.getPrice();
        }
        amount = new BigDecimal(amount).doubleValue();
        return amount;
    }

    private void connectFailed(){
        ToastUtils.showText_Long(this,StringUtils.ONLINE_PAY_CONNECT_FAILED_CONTENT);
    }

    /**
     * 监听函数
     * @param view
     */
    @OnClick({R.id.iv_pay_detail_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_pay_detail_back:
                //PrinterCase.getInstance().amountRecord=0;//when the activity is finished, the amountRecord should be 0.
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
        tv_pay_detail_pay_amount.setText(String.valueOf((int)totalAmount));
        tv_pay_detail_receive_amount.setText(String.valueOf((int)receivedAmount));
        leftAmount = totalAmount - receivedAmount;
        tv_pay_detail_left_amount.setText(String.valueOf((int)(leftAmount<0 ? 0:leftAmount)));
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
        if(scheduledExecutorService==null || scheduledExecutorService.isShutdown()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            //时间更新，一分钟刷新一次
            scheduledExecutorService.scheduleWithFixedDelay(new TimeTask(), 1, 1, TimeUnit.SECONDS);
            Log.i("Test", "PayDetailActivity onStart scheduledExecutorService open!");
        }
    }

    //关掉延迟服务
    @Override
    public void onDestroy(){
        super.onDestroy();
        PayDeviceUtil.getInstance().activityRecord="GotoOtherActivity";
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
