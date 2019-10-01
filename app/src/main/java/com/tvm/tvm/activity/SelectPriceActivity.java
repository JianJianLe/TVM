package com.tvm.tvm.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.SelectPriceAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.receiver.MediaReceiver;
import com.tvm.tvm.util.BackPrevious;
import com.tvm.tvm.util.LongClickUtils;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.billacceptor.BillAcceptorUtil;
import com.tvm.tvm.util.device.paydevice.LYYDevice;
import com.tvm.tvm.util.device.printer.PrinterCase;
import com.tvm.tvm.util.view.ConfirmDialogUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  选票页面
 * - @Author:  Jat
 * - @Date:  2019/1/15
 * - @Time： 21:14
 */
public class SelectPriceActivity extends BaseActivity implements View.OnTouchListener,GestureDetector.OnGestureListener {

    private Context TAG = SelectPriceActivity.this;

    @BindView(R.id.tv_select_price_title_title)
    TextView tv_select_price_title_title;

    @BindView(R.id.gv_select_price_list)
    GridView gv_select_price_list;

    @BindView(R.id.tv_select_price_num)
    TextView tv_select_price_num;

    @BindView(R.id.tv_select_price_amount)
    TextView tv_select_price_amount;

    @BindView(R.id.iv_select_price_buy)
    ImageView iv_select_price_buy;

    @BindView(R.id.iv_select_price_cancel)
    ImageView iv_select_price_cancel;

    @BindView(R.id.ll_activity_select_price_layout)
    LinearLayout ll_activity_select_price_layout;

    private SelectPriceAdapter adapter;

    private List<Price> priceList;

    private DaoSession daoSession;

    private UpdateBroadcastReceiver receiver;

    private IntentFilter intentFilter;

    private ScheduledExecutorService scheduledExecutorService;

    private Map<Integer,TicketBean> ticketList = new HashMap<>();
    //返回上一页倒计时辅助类
    private BackPrevious backPrevious;

    //监听滑动事件，记录初始滑动的坐标
    private float mPosX = 0 ;
    private float mPosY = 0;
    //记录当前手指的坐标
    private float curX = 0;
    private float curY = 0;

    private GestureDetector gestureDetector;

    private MediaReceiver mediaReceiver;

    private boolean isShowMainView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_price);
        ButterKnife.bind(this);

        isShowMainView=false;
        if(setting.getShowMainViewFlag().equals("Yes")){
            isShowMainView=true;
            backPrevious = new BackPrevious(setting.getSelectTimeOut()*1000,1000,SelectPriceActivity.this);
        }

        initView();

        gestureDetector = new GestureDetector(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        setPrice();

        //注册广播接受者，广播接受者更新票价总数
        receiver = new UpdateBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(StringUtils.TICKET_RECEIVER);
        registerReceiver(receiver,intentFilter);

        //注册广播,检测USB是否接入以及获取TVM相关文件
        tvmRegisterAction();


    }

    private void initView(){
        ll_activity_select_price_layout.setOnTouchListener(this);
        gv_select_price_list.setOnTouchListener(this);
        ll_activity_select_price_layout.setLongClickable(true);

        int delayMillis=(PreConfig.Envir=="DEV")? 500:10000;

        if(!isShowMainView){
            //长按十秒公司名称，进入登录管理页面
            LongClickUtils.setLongClick(new Handler(), tv_select_price_title_title, delayMillis, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    startActivity(SelectPriceActivity.this, LoginActivity.class);
                    return true;
                }
            });
        }
    }

    private void tvmRegisterAction() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addDataScheme("file");
        mediaReceiver = new MediaReceiver();
        registerReceiver(mediaReceiver, filter);
    }

    @OnClick({R.id.iv_select_price_cancel,R.id.iv_select_price_buy})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_select_price_buy:
                confirmPay();
                break;
            case R.id.iv_select_price_cancel:
                reset();
                break;
        }
    }

    /**
     * 重置所有的票数和金额
     */
    public void reset(){
        ticketList.clear();
        tv_select_price_num.setText("0");
        tv_select_price_amount.setText("0");
        adapter = new SelectPriceAdapter(this,priceList);
        gv_select_price_list.setAdapter(adapter);
    }

    /**
     * 确认支付
     * 封装所有的请求
     * 跳转对应的页面
     */
    public void confirmPay(){
        if (Integer.valueOf(tv_select_price_num.getText().toString().trim())<=0){
            ToastUtils.showText(this,StringUtils.TICKET_NUM_LESS_ZERO);
        }else {
            Intent intent = new Intent();
            List<TicketBean> beanList = new ArrayList<>();
            Iterator<Map.Entry<Integer, TicketBean>> iterator = ticketList.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Integer, TicketBean> entry = iterator.next();
                beanList.add(entry.getValue());
            }
            BillAcceptorUtil.getInstance().ba_Enable();//@Star 16Feb
            PrinterCase.getInstance().ticketList = beanList;
            startActivity(this,PayDetailActivity.class);
            if(isShowMainView) this.finish();
        }
    }

    /**
     * 发送广播通知activity更改总票数和价格
     * @param position
     * @param operation 0--加，1--减
     */
    public void updateTicketList(int position,int operation){
        TicketBean bean = ticketList.get(position);
        if (bean==null){
            bean = new TicketBean();
            bean.copyFromPrice(priceList.get(position));
            if (operation == 0){
                bean.setNumber(1);
            }
            ticketList.put(position,bean);
        }else {
            if (operation == 0){
                bean.setNumber(bean.getNumber()+1);
            }else {
                bean.setNumber(bean.getNumber()-1);
            }
            ticketList.put(position,bean);
            if (bean.getNumber()==0){
                ticketList.remove(position);
            }
        }

        updateAmount();
    }

    /**
     * 更新票数和总额
     */
    public void updateAmount(){
        int num = 0;
        double amount = 0;
        Iterator<Map.Entry<Integer,TicketBean>> iterator = ticketList.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<Integer,TicketBean> entry = iterator.next();
            TicketBean bean = entry.getValue();
            num = num + bean.getNumber();
            amount = amount + bean.getNumber()*bean.getPrice();
        }
        tv_select_price_num.setText(num+"");
        tv_select_price_amount.setText((int)amount+"");
    }

    @Override
    protected void onResume() {
        super.onResume();
        reset();
        timeStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isShowMainView) backPrevious.cancel();
    }

    //开启时执行延迟服务
    @Override
    protected void onStart() {
        super.onStart();
    }

    //关掉延迟服务
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i("Test","SelectPriceActivity onDestroy");
        unregisterReceiver(receiver);
        unregisterReceiver(mediaReceiver);
    }


    private void setPrice(){
        PriceDao priceDao = daoSession.getPriceDao();
        priceList = priceDao.queryBuilder().where(PriceDao.Properties.IsDelete.eq(0)).list();
        adapter = new SelectPriceAdapter(this,priceList);
        gv_select_price_list.setAdapter(adapter);
    }

    //region 无操作 返回上一页
    private void timeStart() {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(isShowMainView) backPrevious.start();
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
                if(isShowMainView) backPrevious.start();
                break;
            //否则其他动作计时取消
            default:
                if(isShowMainView) backPrevious.cancel();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1.getX()-e2.getX()>StringUtils.FLING_MIN_DISTANCE&&Math.abs(velocityX)>StringUtils.FLING_MIN_VELOCITY){
            final ConfirmDialogUtils confirmDialogUtils = new ConfirmDialogUtils(SelectPriceActivity.this,"购票确认","确认购买您选择的所有票吗？");
            confirmDialogUtils.show();
            confirmDialogUtils.setOnDialogClickListener(new ConfirmDialogUtils.OnDialogClickListener() {
                @Override
                public void onOKClick() {
                    confirmDialogUtils.dismiss();
                    confirmPay();
                }

                @Override
                public void onCancelClick() {
                    confirmDialogUtils.dismiss();
                }
            });
        } else if (e2.getX()-e1.getX() > StringUtils.FLING_MIN_DISTANCE && Math.abs(velocityX)>StringUtils.FLING_MIN_VELOCITY){
            final ConfirmDialogUtils confirmDialogUtils = new ConfirmDialogUtils(SelectPriceActivity.this,"取消确认","确认取消选票并返回上一页吗？");
            confirmDialogUtils.show();
            confirmDialogUtils.setOnDialogClickListener(new ConfirmDialogUtils.OnDialogClickListener() {
                @Override
                public void onOKClick() {
                    confirmDialogUtils.dismiss();
                    if(isShowMainView) SelectPriceActivity.this.finish();
                }

                @Override
                public void onCancelClick() {
                    confirmDialogUtils.dismiss();
                }
            });
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * 获取adapter的广播更新票信息
     */
    public class UpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position",0);
            int operation = intent.getIntExtra("operation",0);
            updateTicketList(position,operation);
        }
    }

}
