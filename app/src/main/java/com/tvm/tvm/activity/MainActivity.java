package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.bean.dao.TicketSummaryDao;
import com.tvm.tvm.util.FirstInitApp;
import com.tvm.tvm.util.FolderUtil;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnLongClick;

public class MainActivity extends BaseActivity {
    //显示视频
    private final int VIDEO_SHOW = 0;
    //显示轮播图
    private final int PICTURE_SHOW = 1;

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

    @BindView(R.id.sv_main_video)
    SurfaceView sv_main_video;

    @BindView(R.id.fl_main_ads)
    FrameLayout fl_main_ads;

    @BindView(R.id.ll_main_fvp)
    LinearLayout ll_main_fvp;

    @BindView(R.id.ll_main_click)
    LinearLayout ll_main_click;

    @BindView(R.id.ll_main_ticke_list)
    LinearLayout ll_main_ticke_list;

    @BindView(R.id.tv_main_ticke_list_null)
    TextView tv_main_ticke_list_null;

    @BindView(R.id.iv_main_ticke_list_icon)
    ImageView iv_main_ticke_list_icon;

    @BindView(R.id.tv_main_ticke_list_title)
    TextView tv_main_ticke_list_title;

    @BindView(R.id.tv_main_ticke_list_price)
    TextView tv_main_ticke_list_price;

    @BindView(R.id.tv_main_ticke_list_desc)
    TextView tv_main_ticke_list_desc;

    @BindView(R.id.vp_main_ads)
    ViewPager vp_main_ads;

    //广告类型，0：不设置 1：视频 2：广告 3：视频广告一起
    private int type = 0;

    private List<String> videos = new ArrayList<>();

    private List<String> pictures = new ArrayList<>();

    //当前播放的哪个视频
    private int indexVideo = 0;

    private DaoSession daoSession;

    //票价列表
    List<Price> priceList = new ArrayList<>();

    //格式化当前时间
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日");

    private ScheduledExecutorService scheduledExecutorService;

//    static {
//        System.loadLibrary("printer_lib");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //检查数据库，看是否是第一次安装
        FirstInitApp.initDatabase(getApplicationContext());
        //获取数据库
        daoSession = AppApplication.getApplication().getDaoSession();

        //Test tv_main_title_title
        //((TextView) findViewById(R.id.tv_main_title_title)).setText(PrinterUtil.getMessageFromJNI());
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //更新时间
                    tv_main_header_time_date.setText(dateFormat.format(new Date()));
                    tv_main_header_time_time.setText(format.format(new Date()));
                    break;
                case 1://播放广告

                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
    };

    @OnLongClick(R.id.tv_main_comany_name)
    public boolean login(){
        startActivity(this,LoginActivity.class);
        return true;
    }

    private void getTicketList(){
        //获取价格列表
        PriceDao priceDao = daoSession.getPriceDao();
        priceList = priceDao.queryBuilder().list();
        if (priceList==null || priceList.size()>2 || priceList.size()==0){
            ll_main_ticke_list.setVisibility(View.GONE);
            ll_main_click.setVisibility(View.VISIBLE);
        }
        if (priceList.size()==1){

        }

        if (priceList.size()==2){

        }
    }

    private void setAdsLayout(int flag){
        switch (flag){
            case VIDEO_SHOW:
                sv_main_video.setVisibility(View.VISIBLE);
                fl_main_ads.setVisibility(View.GONE);
                break;
            case PICTURE_SHOW:
                sv_main_video.setVisibility(View.GONE);
                fl_main_ads.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     - @Description:  获取机器内存，检索是否有广告视频
     - @Author:  Jat
     - @Date:  ${DATE}
     - @Time： ${TIME}
     */
    public void initAds(){
        // 清空list
        videos.clear();
        pictures.clear();

        //视频文件
        String videoPath = FolderUtil.getVideoPath();
        videos = FolderUtil.getFolderFiles(videoPath);
        //图片文件
        String picturePath = FolderUtil.getImagePath();
        pictures = FolderUtil.getFolderFiles(picturePath);

        //判断播放广告方式
        if (videos.size()>0 && pictures.size()>0){
            //视频与广告图片一起轮播
            type = 3;
        }else if (videos.size()== 0 && pictures.size()>0){
            //图片轮播
            type = 2;
        }else {
            //播放视频
            type = 2;
        }
        //发送消息播放广告
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
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

    private void setTicketNum(){
        TicketSummaryDao ticketSummaryDao = daoSession.getTicketSummaryDao();
        List<TicketSummary> ticketSummaryList = ticketSummaryDao.queryBuilder().where(TicketSummaryDao.Properties.Date.eq(dateFormat.format(new Date()))).list();
        if (ticketSummaryList.size()==0){
            tv_main_header_ticket_num.setText("0");
        }else {
            tv_main_header_ticket_num.setText(ticketSummaryList.get(0).getNum()+"");
        }
    }

    //开启时执行延迟服务
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //时间更新，一分钟刷新一次
        scheduledExecutorService.scheduleWithFixedDelay(new TimeTask(), 1, 1 ,TimeUnit.SECONDS);
        //设置公司名和购买指引
        tv_main_comany_name.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.COMPANY_NAME,""));
        tv_main_pay_desc.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.PAY_DESC,""));

        Log.i("Test","MainActivity onStart scheduledExecutorService open!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置票数
        setTicketNum();
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
