package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.TicketSummaryDao;
import com.tvm.tvm.util.FirstInitApp;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.tv_main_header_time_date)
    TextView tv_main_header_time_date;//日期

    @BindView(R.id.tv_main_header_time_time)
    TextView tv_main_header_time_time;//时间

    @BindView(R.id.tv_main_header_ticket_num)
    TextView tv_main_header_ticket_num;//票数

    @BindView(R.id.tv_main_comany_name)
    TextView tv_main_comany_name;//公司名

    @BindView(R.id.tv_main_pay_desc)
    TextView tv_main_pay_desc;

    private DaoSession daoSession;

    //格式化当前时间
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日");

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //检查数据库，看是否是第一次安装
        FirstInitApp.initDatabase(getApplicationContext());
        //获取数据库
        daoSession = AppApplication.getApplication().getDaoSession();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    tv_main_header_time_date.setText(dateFormat.format(new Date()));
                    tv_main_header_time_time.setText(format.format(new Date()));
                    break;
            }
        }
    };

    @OnLongClick(R.id.tv_main_comany_name)
    public boolean login(){
        startActivity(this,LoginActivity.class);
        return true;
    }

    /**
     * 时间任务
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

    private void setTicketNum(){
        TicketSummaryDao ticketSummaryDao = daoSession.getTicketSummaryDao();
        List<TicketSummary> ticketSummaryList = ticketSummaryDao.queryBuilder().where(TicketSummaryDao.Properties.Date.eq(dateFormat.format(new Date()))).list();
        if (ticketSummaryList.size()==0){
            tv_main_header_ticket_num.setText(0);
        }else {
            tv_main_header_ticket_num.setText(ticketSummaryList.get(0).getNum());
        }
    }

    //开启时执行延迟服务
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new TimeTask(), 1, 1 ,TimeUnit.SECONDS);
        tv_main_comany_name.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.COMPANY_NAME,""));
        tv_main_pay_desc.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.PAY_DESC,""));
        Log.i("Test","MainActivity onStart scheduledExecutorService open!");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setTicketNum();
    }

    //关掉延迟服务
    @Override
    public void onDestroy(){
        // TODO Auto-generated method stub
        super.onDestroy();
        scheduledExecutorService.shutdown();
        Log.i("Test","MainActvity onDestroy scheduledExecutorService shutdown");
    }
}
