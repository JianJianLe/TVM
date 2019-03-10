package com.tvm.tvm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tvm.tvm.R;
import com.tvm.tvm.adapter.ViewpagerDotsAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.TicketSummary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.bean.dao.TicketSummaryDao;
import com.tvm.tvm.util.BitmapUtils;
import com.tvm.tvm.util.FirstInitApp;
import com.tvm.tvm.util.FolderUtil;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.BillAcceptorUtil;
import com.tvm.tvm.util.device.PrinterCase;
import com.tvm.tvm.util.player.MPlayer;
import com.tvm.tvm.util.player.MPlayerException;
import com.tvm.tvm.util.player.MinimalDisplay;
import com.tvm.tvm.util.player.PlayerCallback;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @BindView(R.id.ll_main_ticket_list_2)
    LinearLayout ll_main_ticket_list_2;

    @BindView(R.id.ll_main_ticket_list_1)
    LinearLayout ll_main_ticket_list_1;

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


    @BindView(R.id.iv_main_ticke_list_icon2)
    ImageView iv_main_ticke_list_icon2;

    @BindView(R.id.tv_main_ticke_list_title2)
    TextView tv_main_ticke_list_title2;

    @BindView(R.id.tv_main_ticke_list_price2)
    TextView tv_main_ticke_list_price2;

    @BindView(R.id.tv_main_ticke_list_desc2)
    TextView tv_main_ticke_list_desc2;

    @BindView(R.id.tv_main_click_buy)
    TextView tv_main_click_buy;

    @BindView(R.id.vp_main_ads)
    ViewPager vp_main_ads;

    private DaoSession daoSession;

    //装载动态轮播图
    private List<View> dots = new ArrayList<View>();
    private List<ImageView> list_img = new ArrayList<ImageView>();

    private List<String> videos = new ArrayList<>();

    private List<String> pictures = new ArrayList<>();

    //格式化当前时间
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年M月d日");

    private MPlayer player;

    //显示视频
    private final int VIDEO_SHOW = 0;

    //显示轮播图
    private final int PICTURE_SHOW = 1;

    //当前轮播图
    private int currentItem = -1;
    //票价列表
    List<Price> priceList = new ArrayList<>();

    private ScheduledExecutorService scheduledExecutorService;

    //广告类型，0：不设置 1：视频 2：广告 3：视频广告一起
    private int type = 0;

    //当前播放的哪个视频
    private int indexVideo = 0;
    //图片播放时间
    private int imageShowTime=3;
    //设置了图片播放
    private boolean isShowImage = true;

    //当前播放视频还是广告，0：广告 1：视频
    private int whatShow = 0;

    //时间
    private int timeFlag=-1;

    static {
        System.loadLibrary("printer");
        System.loadLibrary("serial_port");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //检查数据库，看是否是第一次安装
        FirstInitApp.initDatabase(getApplicationContext());
        //获取数据库
        daoSession = AppApplication.getApplication().getDaoSession();

        type=2;
        initAds();

        //初始化时间
        tv_main_header_time_date.setText(dateFormat.format(new Date()));
        tv_main_header_time_time.setText(format.format(new Date()));

        initBillAcceptor();
        /******Test******/
        //Test tv_main_title_title
        //((TextView) findViewById(R.id.tv_main_title_title)).setText(PrinterUtil.getMessageFromJNI());
        //PrinterCase.getInstance().printerCaseTest();
        /******Test******/

    }

    public void initBillAcceptor(){
        //纸钞机初始化
        BillAcceptorUtil.getInstance().billAcceptorCmdInit();
        BillAcceptorUtil.getInstance().billAcceptorDeviceInit();
        BillAcceptorUtil.getInstance().ba_Disable();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //更新时间
                    tv_main_header_time_date.setText(dateFormat.format(new Date()));
                    tv_main_header_time_time.setText(format.format(new Date()));

                    //切换轮播图
                    //播放广告或者混合播放的时候而且正在播放广告图片才切换轮播图
                    if ((type==2 || type==3) && whatShow==0){
                        //开始轮播或者时间到了改切换，才去切换
                        if(timeFlag==imageShowTime || timeFlag==-1){
                            //时间置0，重新算时间
                            timeFlag=0;
                            int flag= SetCurrentImage();
                            if(flag==0){
                                //切换轮播图，并且更新时间
                                Log.d("Test","CurrentItem = " + currentItem);
                                vp_main_ads.setCurrentItem(currentItem);
                            }else if(flag==1){
                                //当是视频图片轮播时，判断是否图片的最后一张，当flag=1时，表示为最后一张图片
                                setAdsLayout(VIDEO_SHOW);//视频
                                whatShow = 1;
                                setVideo();
                                Log.d("Test", "Video start");
                            }
                        }
                        //=============
                        timeFlag++;
                    }

                    break;
                case 1://播放广告
                    setAdsLayout(PICTURE_SHOW);
                    break;
                case 2:
                    setAdsLayout(VIDEO_SHOW);
                    break;
                case 3:
                    break;
            }
        }
    };

    public void setVideo(){
        //如果vedio可见，播放视频,播放一个视频
        if (sv_main_video.getVisibility()==View.VISIBLE) {

            if (videos!=null && videos.size()>0) {
                try {
                    if (type==3) {
                        player.setSource((ArrayList<String>) videos,true);
                        player.setCallback(new PlayerCallback() {

                            @Override
                            public void complete() {
                                // TODO Auto-generated method stub
                                Log.d("Test", "Video completed");
                                setAdsLayout(PICTURE_SHOW);//图片
                                whatShow = 0;
                            }
                        });
                    }else {
                        player.setSource((ArrayList<String>) videos,false);
                    }
                    player.play();
                } catch (MPlayerException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    @OnLongClick(R.id.tv_main_comany_name)
    public boolean login(){
        startActivity(this,LoginActivity.class);
        return true;
    }

    @OnClick({R.id.tv_main_click_buy,R.id.ll_main_ticket_list_1,R.id.ll_main_ticket_list_2})
    public void onClick(View view){
        Long priceId;
        switch (view.getId()){
            case R.id.tv_main_click_buy:
                //选择价格，价格列表为空的话不能购票
                if (priceList==null || priceList.size() == 0){
                    ToastUtils.showText(this,StringUtils.EMPTY_PRICE_LIST);
                }else {
                    startActivity(this,SelectPriceActivity.class);
                }
                break;
            case R.id.ll_main_ticket_list_1:
                priceId = priceList.get(0).getId();
                Intent intent = new Intent();
                intent.putExtra("priceId",priceId);
                startActivity(this,intent,PayDetailActivity.class);
                break;
            case R.id.ll_main_ticket_list_2:
                priceId = priceList.get(1).getId();
                Intent intent2 = new Intent();
                intent2.putExtra("priceId",priceId);
                startActivity(this,intent2,PayDetailActivity.class);
                break;
        }
    }

    private int SetCurrentImage(){
        int flag=0;
        //播完之后切换0
        if(currentItem==list_img.size()-1){
            currentItem=-1;
        }

        currentItem++;
        //Log.d("Test","currentItem:"+currentItem);
        if(type==3 && currentItem == list_img.size()-1 && isShowImage==true){
            // 图片视频轮播，当播放到最后一张图片后，开始播放视频，即设置flag=1。
            // （list_img.size()-1）(图片播放到最后一张)
            // isShowImage=True(设置了图片播放)
            flag=1;
        }else if(type==3 && isShowImage==false){
            //当在播放视频的时候，设置currentItem = -1;
            //这样当切换回图片时，可以显示第一张图片
            currentItem=-1;
        }else{
            flag=0;//播放图片
        }
        return flag;
    }

    private void getPriceList(){
        //获取价格列表
        PriceDao priceDao = daoSession.getPriceDao();
        priceList = priceDao.queryBuilder().list();
        if (priceList==null || priceList.size()>2 || priceList.size()==0){
            ll_main_ticke_list.setVisibility(View.GONE);
            ll_main_click.setVisibility(View.VISIBLE);
        }
        if (priceList.size()==1){
            tv_main_ticke_list_null.setVisibility(View.GONE);
            ll_main_ticket_list_2.setVisibility(View.GONE);
            tv_main_ticke_list_desc.setText(priceList.get(0).getDescription());
            tv_main_ticke_list_price.setText((int)priceList.get(0).getPrice()+"");
            tv_main_ticke_list_title.setText(priceList.get(0).getTitle());
            iv_main_ticke_list_icon.setImageBitmap(BitmapUtils.byte2Bitmap(priceList.get(0).getPic()));
        }

        if (priceList.size()==2){
            tv_main_ticke_list_desc.setText(priceList.get(0).getDescription());
            tv_main_ticke_list_price.setText((int)priceList.get(0).getPrice()+"");
            tv_main_ticke_list_title.setText(priceList.get(0).getTitle());
            iv_main_ticke_list_icon.setImageBitmap(BitmapUtils.byte2Bitmap(priceList.get(0).getPic()));


            tv_main_ticke_list_desc2.setText(priceList.get(1).getDescription());
            tv_main_ticke_list_price2.setText((int)priceList.get(1).getPrice()+"");
            tv_main_ticke_list_title2.setText(priceList.get(1).getTitle());
            iv_main_ticke_list_icon2.setImageBitmap(BitmapUtils.byte2Bitmap(priceList.get(1).getPic()));
        }
    }

    private void setAdsLayout(int flag){
        switch (flag){
            case VIDEO_SHOW:
                isShowImage=false;
                sv_main_video.setVisibility(View.VISIBLE);
                fl_main_ads.setVisibility(View.GONE);
                break;
            case PICTURE_SHOW:
                currentItem=-1;
                isShowImage=true;
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

        player = new MPlayer();
        player.setDisplay(new MinimalDisplay(sv_main_video));

        // 清空list
        videos.clear();
        pictures.clear();

        //视频文件
        String videoPath = FolderUtil.getVideoPath();
        videos = FolderUtil.getFolderFiles(videoPath);
        //图片文件
        String picturePath = FolderUtil.getImagePath();
        pictures = FolderUtil.getFolderFiles(picturePath);

        if (pictures!=null){
            getBanner();
        }

        //判断播放广告方式
        if (videos.size()>0 && pictures.size()>0){
            //视频与广告图片一起轮播
            type = 3;
            whatShow=0;
        }else if (videos.size()== 0 && pictures.size()>0){
            //图片轮播
            type = 2;
            whatShow=0;
        }else {
            //播放视频
            type = 1;
            whatShow=1;
        }
        //发送消息播放广告
        Message message = new Message();
        message.what = 1;
        handler.sendMessage(message);
    }

    private void getBanner(){
        //动态设置轮播图数量
        list_img.clear();
        for (int i = 0; i < pictures.size(); i++) {
            int m=400;
            int h=300;
            ImageView iv = new ImageView(MainActivity.this);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            File f=new File(pictures.get(i));
            Picasso.with(MainActivity.this)
                    .load(f)
                    .resize(m, h)
                    .centerCrop()
                    .into(iv);
            list_img.add(iv);
            iv=null;
        }

        //设置轮播图
        vp_main_ads.setAdapter(new ViewpagerDotsAdapter(MainActivity.this, list_img));
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
        List<TicketSummary> ticketSummaryList = ticketSummaryDao.queryBuilder().list();
        if (ticketSummaryList.size()==0){
            tv_main_header_ticket_num.setText("001");
        }else {
            tv_main_header_ticket_num.setText(PrinterCase.getInstance().OrderDispose(
                    ticketSummaryList.get(ticketSummaryList.size()-1).getNum()));
        }
    }

    //开启时执行延迟服务
    @Override
    protected void onStart() {
        super.onStart();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //时间更新，一分钟刷新一次
        scheduledExecutorService.scheduleWithFixedDelay(new TimeTask(), 1, 1 ,TimeUnit.SECONDS);
        //设置公司名和购买指引
        SettingDao settingDao = daoSession.getSettingDao();
        Setting setting = settingDao.queryBuilder().where(SettingDao.Properties.Id.eq(1)).unique();
        if (setting!=null){
            tv_main_comany_name.setText(setting.getShopName());
            tv_main_pay_desc.setText(setting.getPayDesc());
        }

        Log.i("Test","MainActivity onStart scheduledExecutorService open!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置票数
        setTicketNum();
        getPriceList();
    }

    //关掉延迟服务
    @Override
    public void onDestroy(){
        super.onDestroy();
        scheduledExecutorService.shutdown();
        Log.i("Test","MainActvity onDestroy scheduledExecutorService shutdown");
    }

}
