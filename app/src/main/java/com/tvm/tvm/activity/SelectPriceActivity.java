package com.tvm.tvm.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.SelectPriceAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.bean.dao.TicketSummaryDao;
import com.tvm.tvm.util.BackPrevious;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.PrinterCase;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  选票页面
 * - @Author:  Jat
 * - @Date:  2019/1/15
 * - @Time： 21:14
 */
public class SelectPriceActivity extends BaseActivity {

    private Context TAG = SelectPriceActivity.this;

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

    private SelectPriceAdapter adapter;

    private List<Price> priceList;

    private DaoSession daoSession;

    private UpdateBroadcastReceiver receiver;

    private IntentFilter intentFilter;

    private ScheduledExecutorService scheduledExecutorService;

    private Map<Integer,TicketBean> ticketList = new HashMap<>();
    //返回上一页倒计时辅助类
    private BackPrevious backPrevious;

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
        daoSession = AppApplication.getApplication().getDaoSession();
        setPrice();

        backPrevious = new BackPrevious(setting.getSelectTimeOut()*1000,1000,SelectPriceActivity.this);

        //注册广播接受者，广播接受者更新票价总数
        receiver = new UpdateBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(StringUtils.TICKET_RECEIVER);
        registerReceiver(receiver,intentFilter);
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
            intent.putExtra("list", (Serializable) beanList);
            startActivity(this,intent,PayDetailActivity.class);
            this.finish();
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
        tv_select_price_amount.setText(amount+"");
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
        backPrevious.cancel();
    }

    //开启时执行延迟服务
    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Test","Select price onStart scheduledExecutorService open!");
    }

    //关掉延迟服务
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.i("Test","SelectPrice Activity onDestroy scheduledExecutorService shutdown");
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
