package com.tvm.tvm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.SelectPriceAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.bean.dao.TicketSummaryDao;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.device.PrinterCase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/15
 * - @Time： 21:14
 */
public class SelectPriceActivity extends BaseActivity {

    private Context TAG = SelectPriceActivity.this;

    @BindView(R.id.tv_select_price_header_time_date)
    TextView tv_select_price_header_time_date;//日期

    @BindView(R.id.tv_select_price_header_time_time)
    TextView tv_select_price_header_time_time;//时间

    @BindView(R.id.tv_select_price_header_ticket_num)
    TextView tv_select_price_header_ticket_num;//票数

    @BindView(R.id.gv_select_price_list)
    GridView gv_select_price_list;

    private SelectPriceAdapter adapter;

    //格式化当前时间
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日");

    private List<Price> priceList;

    private DaoSession daoSession;

    private ScheduledExecutorService scheduledExecutorService;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //更新时间
                    tv_select_price_header_time_date.setText(dateFormat.format(new Date()));
                    tv_select_price_header_time_time.setText(format.format(new Date()));
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
        //初始时间
        tv_select_price_header_time_date.setText(dateFormat.format(new Date()));
        tv_select_price_header_time_time.setText(format.format(new Date()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置票数
        setTicketNum();
    }

    //开启时执行延迟服务
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //时间更新，一分钟刷新一次
        scheduledExecutorService.scheduleWithFixedDelay(new TimeTask(), 1, 1 ,TimeUnit.SECONDS);

        Log.i("Test","Select price onStart scheduledExecutorService open!");
    }

    //关掉延迟服务
    @Override
    public void onDestroy(){
        super.onDestroy();
        scheduledExecutorService.shutdown();
        Log.i("Test","onDestroy scheduledExecutorService shutdown");
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

    private void setPrice(){
        PriceDao priceDao = daoSession.getPriceDao();
        priceList = priceDao.queryBuilder().list();
        adapter = new SelectPriceAdapter(this,priceList);
        gv_select_price_list.setAdapter(adapter);
        gv_select_price_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long priceId = priceList.get(position).getId();
                Intent intent = new Intent();
                intent.putExtra("priceId",priceId);
                startActivity(TAG,intent,PayDetailActivity.class);
                SelectPriceActivity.this.finish();
            }
        });
    }

    private void setTicketNum(){
        TicketSummaryDao ticketSummaryDao = daoSession.getTicketSummaryDao();
        List<TicketSummary> ticketSummaryList = ticketSummaryDao.queryBuilder().list();
        if (ticketSummaryList.size()==0){
            tv_select_price_header_ticket_num.setText("001");
        }else {
            tv_select_price_header_ticket_num.setText(PrinterCase.getInstance().OrderDispose(
                    ticketSummaryList.get(ticketSummaryList.size()-1).getNum()));
        }
    }
}
