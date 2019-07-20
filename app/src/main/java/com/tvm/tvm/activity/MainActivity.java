package com.tvm.tvm.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.receiver.MediaReceiver;
import com.tvm.tvm.util.AuthorizeUtil;
import com.tvm.tvm.util.BitmapUtils;
import com.tvm.tvm.util.FileUtils;
import com.tvm.tvm.util.FirstInitApp;
import com.tvm.tvm.util.FolderUtil;
import com.tvm.tvm.util.LongClickUtils;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.billacceptor.BillAcceptorUtil;
import com.tvm.tvm.util.device.paydevice.PayDeviceUtil;
import com.tvm.tvm.util.device.printer.PrinterCase;
import com.tvm.tvm.util.player.MPlayer;
import com.tvm.tvm.util.player.MPlayerException;
import com.tvm.tvm.util.player.MinimalDisplay;
import com.tvm.tvm.util.player.PlayerCallback;
import com.tvm.tvm.util.view.ConfirmDialogUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.File;
import java.io.Serializable;
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

    //点击购票布局
    @BindView(R.id.ll_main_click)
    LinearLayout ll_main_click;

    //购票列表布局
    @BindView(R.id.ll_main_ticke_list)
    LinearLayout ll_main_ticke_list;

    //购票汇总布局
    @BindView(R.id.ll_main_ticket_detail)
    LinearLayout ll_main_ticket_detail;

    //描述
    @BindView(R.id.ll_main_desc)
    LinearLayout ll_main_desc;

    //图标
    @BindView(R.id.ll_main_icon)
    LinearLayout ll_main_icon;

    //图标与描述一起
    @BindView(R.id.ll_main_desc_and_icon)
    LinearLayout ll_main_desc_and_icon;

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

    //有票时点击购买按钮
    @BindView(R.id.iv_main_ticket_buy)
    ImageView iv_main_ticket_buy;

    //有票时取消按钮按钮
    @BindView(R.id.iv_main_ticket_cancel)
    ImageView iv_main_ticket_cancel;

    @BindView(R.id.vp_main_ads)
    ViewPager vp_main_ads;

    //票1加
    @BindView(R.id.iv_main_ticket_1_add)
    ImageView iv_main_ticket_1_add;
    //票1减
    @BindView(R.id.iv_main_ticket_1_sub)
    ImageView iv_main_ticket_1_sub;
    //票1数量
    @BindView(R.id.tv_main_ticket_1_num)
    TextView tv_main_ticket_1_num;

    //票2加
    @BindView(R.id.iv_main_ticket_2_add)
    ImageView iv_main_ticket_2_add;
    //票2减
    @BindView(R.id.iv_main_ticket_2_sub)
    ImageView iv_main_ticket_2_sub;
    //票2数量
    @BindView(R.id.tv_main_ticket_2_num)
    TextView tv_main_ticket_2_num;

    //总票数
    @BindView(R.id.tv_main_ticket_num)
    TextView tv_main_ticket_num;
    //总金额
    @BindView(R.id.tv_main_ticket_amount)
    TextView tv_main_ticket_amount;

    private DaoSession daoSession;

    //装载动态轮播图
    private List<View> dots = new ArrayList<View>();
    private List<ImageView> list_img = new ArrayList<ImageView>();

    private List<String> videos = new ArrayList<>();

    private List<String> pictures = new ArrayList<>();

    //格式化当前时间
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

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
    private int imageShowTime = 3;
    //设置了图片播放
    private boolean isShowImage = true;
    //当轮播的时候，判断是否可以播放视频
    private int mediaFlag = 0;

    //当前播放视频还是广告，0：广告 1：视频
    private int whatShow = 0;

    //时间
    private int timeFlag = -1;

    private MediaReceiver mediaReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //检查数据库，看是否是第一次安装
        FirstInitApp.initDatabase(getApplicationContext());
        //获取数据库
        daoSession = AppApplication.getApplication().getDaoSession();

        //初始化视频播放
        player = new MPlayer();
        player.setDisplay(new MinimalDisplay(sv_main_video));

        initView();
        initAds();
        initBillAcceptor();
        initPayDevice();
        tvmRegisterAction();
    }

    private void initView() {

        //初始化时间
        tv_main_header_time_date.setText(dateFormat.format(new Date()));
        tv_main_header_time_time.setText(format.format(new Date()));

        int delayMillis=(PreConfig.Envir=="DEV")? 500:10000;

        //长按十秒公司名称，进入登录管理页面
        LongClickUtils.setLongClick(new Handler(), tv_main_comany_name, delayMillis, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //todo:补充长按事件的处理逻辑
                startActivity(MainActivity.this, LoginActivity.class);
                return true;
            }
        });

        //设置公司名和购买指引
        if (setting != null) {
            tv_main_comany_name.setText(setting.getShopName());
            tv_main_pay_desc.setText(setting.getPayDesc());
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


    public void initBillAcceptor() {
        //纸钞机初始化
        BillAcceptorUtil.getInstance().init_BillAcceptorCmd();
        BillAcceptorUtil.getInstance().init_BillAcceptorDevice();
        BillAcceptorUtil.getInstance().ba_Disable();
    }

    public void initPayDevice() {
        PayDeviceUtil.getInstance().initPayDevice();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //更新时间
                    tv_main_header_time_date.setText(dateFormat.format(new Date()));
                    tv_main_header_time_time.setText(format.format(new Date()));

                    //切换轮播图
                    //播放广告或者混合播放的时候而且正在播放广告图片才切换轮播图
                    if ((type == 2 || type == 3) && whatShow == 0) {
                        //开始轮播或者时间到了改切换，才去切换
                        if (timeFlag == imageShowTime || timeFlag == -1) {
                            //时间置0，重新算时间
                            timeFlag = 0;
                            mediaFlag = SetCurrentImage();
                            if (mediaFlag == 2) {
                                //当是视频图片轮播时，判断是否图片的最后一张，当flag=1时，表示为最后一张图片
                                setAdsLayout(VIDEO_SHOW);//视频
                                whatShow = 1;
                                Log.d("Test", "The Image is completed,Show video and Set Video");
                                setVideo();
                                Log.d("Test", "Video start");
                            } else {
                                //切换轮播图，并且更新时间
                                Log.d("Test", "CurrentItem = " + currentItem);
                                vp_main_ads.setCurrentItem(currentItem);
                            }
                        }
                        //=============
                        Log.d("Test", "timeFlag = " + timeFlag);
                        timeFlag++;
                    }

                    break;
                case 1://播放广告
                    setAdsLayout(PICTURE_SHOW);
                    break;
                case 2://播放视频
                    setAdsLayout(VIDEO_SHOW);
                    setVideo();
                    Log.d("Test", "Video start");
                    break;
                case 3:
                    break;
            }
        }
    };

    public void setVideo() {
        //如果vedio可见，播放视频,播放一个视频
        if (sv_main_video.getVisibility() == View.VISIBLE) {

            if (videos != null && videos.size() > 0) {
                try {
                    if (type == 3) {
                        player.setSource((ArrayList<String>) videos, true);
                        player.setCallback(new PlayerCallback() {

                            @Override
                            public void complete() {
                                Log.d("Test", "Video completed，show Picture");
                                setAdsLayout(PICTURE_SHOW);//图片
                                whatShow = 0;
                            }
                        });
                    } else {
                        player.setSource((ArrayList<String>) videos, false);
                    }
                    player.play();
                } catch (MPlayerException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private boolean checkInstallation() {
        if (!AuthorizeUtil.getInstance().isKeyExisted()) {
            final ConfirmDialogUtils confirmDialogUtils = new ConfirmDialogUtils(this, StringUtils.INSTALLATION_ERROR, " ", false);
            confirmDialogUtils.show();
            confirmDialogUtils.setOnDialogClickListener(new ConfirmDialogUtils.OnDialogClickListener() {
                @Override
                public void onOKClick() {
                    confirmDialogUtils.dismiss();
                }

                @Override
                public void onCancelClick() {
                    confirmDialogUtils.dismiss();
                }
            });
            return false;
        }
        return true;
    }

    @OnClick({R.id.tv_main_click_buy,
            R.id.iv_main_ticket_cancel, R.id.iv_main_ticket_buy,
            R.id.iv_main_ticket_1_add, R.id.iv_main_ticket_1_sub,
            R.id.iv_main_ticket_2_add, R.id.iv_main_ticket_2_sub,
            R.id.tv_main_header_ticket_num, R.id.ll_main})
    public void onClick(View view) {
        if (!checkInstallation())
            return;
        int ticketNum = 0;
        switch (view.getId()) {
//            case R.id.tv_main_title_title:
//                PayDeviceUtil.getInstance().cmd_DrawBack_Test();
//                PayDeviceUtil.getInstance().cmd_GetQRCode(1000);//1000分->10元
//                break;
            case R.id.tv_main_header_ticket_num:
//                PayDeviceUtil.getInstance().cmd_ReplySever();
                break;
            case R.id.tv_main_click_buy:
                if (PrinterCase.getInstance().checkTicketTemplate() == false) {
                    ToastUtils.showText(this, StringUtils.EMPTY_TICKET_LIST);
                    break;
                }
                //选择价格，价格列表为空的话不能购票
                if (priceList == null || priceList.size() == 0) {
                    ToastUtils.showText(this, StringUtils.EMPTY_PRICE_LIST);
                } else {
                    startActivity(this, SelectPriceActivity.class);
                }
                break;
            case R.id.iv_main_ticket_cancel:
                tv_main_ticket_1_num.setText("0");
                tv_main_ticket_2_num.setText("0");
                tv_main_ticket_num.setText("0");
                tv_main_ticket_amount.setText("0");
                //取消购买，票数价钱清零
                break;
            case R.id.iv_main_ticket_buy:
                //点击购买，跳到对应购买页面
                if (PrinterCase.getInstance().checkTicketTemplate() == false) {
                    ToastUtils.showText(this, StringUtils.EMPTY_TICKET_LIST);
                    break;
                }
                confirmPay();
                break;
            case R.id.iv_main_ticket_1_add:
                ticketNum = Integer.valueOf(tv_main_ticket_1_num.getText().toString().trim());
                ticketNum = ticketNum + 1;
                tv_main_ticket_1_num.setText(ticketNum + "");
                countTicketNumAndAmount();
                break;
            case R.id.iv_main_ticket_1_sub:
                ticketNum = Integer.valueOf(tv_main_ticket_1_num.getText().toString().trim());
                if (ticketNum == 0) {
                    ToastUtils.showText(this, StringUtils.TICKET_NUM_LESS_ZERO);
                } else {
                    ticketNum = ticketNum - 1;
                    tv_main_ticket_1_num.setText(ticketNum + "");
                    countTicketNumAndAmount();
                }
                break;
            case R.id.iv_main_ticket_2_add:
                ticketNum = Integer.valueOf(tv_main_ticket_2_num.getText().toString().trim());
                ticketNum = ticketNum + 1;
                tv_main_ticket_2_num.setText(ticketNum + "");
                countTicketNumAndAmount();
                break;
            case R.id.iv_main_ticket_2_sub:
                ticketNum = Integer.valueOf(tv_main_ticket_2_num.getText().toString().trim());
                if (ticketNum == 0) {
                    ToastUtils.showText(this, StringUtils.TICKET_NUM_LESS_ZERO);
                } else {
                    ticketNum = ticketNum - 1;
                    tv_main_ticket_2_num.setText(ticketNum + "");
                    countTicketNumAndAmount();
                }
                break;
            case R.id.ll_main:
                if (PrinterCase.getInstance().checkTicketTemplate() == false) {
                    ToastUtils.showText(this, StringUtils.EMPTY_TICKET_LIST);
                    break;
                }
                //选择价格，价格列表为空的话不能购票
                if (priceList == null || priceList.size() < 3) {

                } else {
                    startActivity(this, SelectPriceActivity.class);
                }
                break;
        }
    }

    /**
     * 确认支付
     * 封装传递参数
     * 跳转对应页面
     */
    public void confirmPay() {
        List<TicketBean> ticketList = new ArrayList<>();
        if ("0".equals(tv_main_ticket_num.getText().toString().trim())) {
            ToastUtils.showText(this, StringUtils.TICKET_NUM_LESS_ZERO);
        } else {
            if (Integer.valueOf(tv_main_ticket_1_num.getText().toString().trim()) > 0) {
                TicketBean bean = new TicketBean();
                bean.copyFromPrice(priceList.get(0));
                bean.setNumber(Integer.valueOf(tv_main_ticket_1_num.getText().toString().trim()));
                ticketList.add(bean);
            }
            if (Integer.valueOf(tv_main_ticket_2_num.getText().toString().trim()) > 0) {
                TicketBean bean = new TicketBean();
                bean.copyFromPrice(priceList.get(1));
                bean.setNumber(Integer.valueOf(tv_main_ticket_2_num.getText().toString().trim()));
                ticketList.add(bean);
            }
            BillAcceptorUtil.getInstance().ba_Enable();//@Star 16Feb
            PrinterCase.getInstance().ticketList = ticketList;
            startActivity(this, PayDetailActivity.class);
        }
    }

    /**
     * 重新计算总额
     */
    public void countTicketNumAndAmount() {
        //票数
        int num = Integer.valueOf(tv_main_ticket_1_num.getText().toString().trim()) + Integer.valueOf(tv_main_ticket_2_num.getText().toString().trim());
        tv_main_ticket_num.setText(num + "");
        //金额
        double amount = Integer.valueOf(tv_main_ticket_1_num.getText().toString().trim()) * Double.valueOf(tv_main_ticke_list_price.getText().toString().trim()) + Integer.valueOf(tv_main_ticket_2_num.getText().toString().trim()) * Double.valueOf(tv_main_ticke_list_price2.getText().toString().trim());
        tv_main_ticket_amount.setText((int) amount + "");
    }

    /**
     * 设置轮播图
     *
     * @return
     */
    private int SetCurrentImage() {
        Log.d("Test", "SetCurrentImage currentItem = " + currentItem);
        int flag = mediaFlag;
        //播完之后切换0
        if (currentItem == list_img.size() - 1) {
            currentItem = -1;
        }

        currentItem++;

        if (flag == 1) {
            flag = 2;
        } else {
            if (type == 3 && currentItem == list_img.size() - 1 && isShowImage == true) {
                // 图片视频轮播，当播放到最后一张图片后，开始播放视频，即设置flag=1。
                // （list_img.size()-1）(图片播放到最后一张)
                // isShowImage=True(设置了图片播放)
                flag = 1;
            } else if (type == 3 && isShowImage == false) {
                //当在播放视频的时候，设置currentItem = -1;
                //这样当切换回图片时，可以显示第一张图片
                currentItem = 0;
            } else {
                flag = 0;//播放图片
            }
        }
        return flag;
    }

    /**
     * 根据票列表显示或者隐藏对应布局
     *
     * @param i 0:不显示票列表 1：显示票列表
     */
    public void setLayout(int i) {
        if (i == 0) {
            ll_main_ticke_list.setVisibility(View.GONE);
            ll_main_ticket_detail.setVisibility(View.GONE);
            ll_main_desc_and_icon.setVisibility(View.GONE);
            ll_main_desc.setVisibility(View.VISIBLE);
            ll_main_icon.setVisibility(View.VISIBLE);
            ll_main_click.setVisibility(View.VISIBLE);
        } else {
            ll_main_ticke_list.setVisibility(View.VISIBLE);
            ll_main_ticket_detail.setVisibility(View.VISIBLE);
            ll_main_desc_and_icon.setVisibility(View.VISIBLE);
            ll_main_desc.setVisibility(View.GONE);
            ll_main_icon.setVisibility(View.GONE);
            ll_main_click.setVisibility(View.GONE);
        }
    }

    private void getPriceList() {
        //获取价格列表
        PriceDao priceDao = daoSession.getPriceDao();
        priceList = priceDao.queryBuilder().where(PriceDao.Properties.IsDelete.eq(0)).list();
        if (priceList == null || priceList.size() > 2 || priceList.size() == 0) {
            setLayout(0);
        }
        if (priceList.size() == 1) {
            setLayout(1);
            tv_main_ticke_list_null.setVisibility(View.GONE);
            ll_main_ticket_list_2.setVisibility(View.GONE);
            tv_main_ticke_list_desc.setText(priceList.get(0).getDescription());
            tv_main_ticke_list_price.setText((int) priceList.get(0).getPrice() + "");
            tv_main_ticke_list_title.setText(priceList.get(0).getTitle());
            iv_main_ticke_list_icon.setImageBitmap(BitmapUtils.byte2Bitmap(priceList.get(0).getPic()));
        }

        if (priceList.size() == 2) {

            setLayout(1);

            tv_main_ticke_list_desc.setText(priceList.get(0).getDescription());
            tv_main_ticke_list_price.setText((int) priceList.get(0).getPrice() + "");
            tv_main_ticke_list_title.setText(priceList.get(0).getTitle());
            iv_main_ticke_list_icon.setImageBitmap(BitmapUtils.byte2Bitmap(priceList.get(0).getPic()));


            tv_main_ticke_list_desc2.setText(priceList.get(1).getDescription());
            tv_main_ticke_list_price2.setText((int) priceList.get(1).getPrice() + "");
            tv_main_ticke_list_title2.setText(priceList.get(1).getTitle());
            iv_main_ticke_list_icon2.setImageBitmap(BitmapUtils.byte2Bitmap(priceList.get(1).getPic()));
        }
    }

    /**
     * s设置广告
     *
     * @param flag
     */
    private void setAdsLayout(int flag) {
        switch (flag) {
            case VIDEO_SHOW:
                Log.i("Test","Set AdsLayout: Video Show");
                isShowImage = false;
                sv_main_video.setVisibility(View.VISIBLE);
                fl_main_ads.setVisibility(View.GONE);
                break;
            case PICTURE_SHOW:
                Log.i("Test","Set AdsLayout: Picture Show");
                currentItem = -1;
                isShowImage = true;
                sv_main_video.setVisibility(View.GONE);
                fl_main_ads.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * - @Description:  获取机器内存，检索是否有广告视频
     * - @Author:  Jat
     * - @Date:  ${DATE}
     * - @Time： ${TIME}
     */
    public void initAds() {

        Log.i("Test","=== MainActivity InitAds ===");
        // 清空list
        videos.clear();
        pictures.clear();

        //视频文件
        String videoPath = FolderUtil.getVideoPath();
        videos = FolderUtil.getFolderFiles(videoPath);
        //图片文件
        String picturePath = FolderUtil.getImagePath();
        pictures = FolderUtil.getFolderFiles(picturePath);

        if (pictures != null) {
            getBanner();
        }

        type = 2;
        //发送消息播放广告
        Message message = new Message();
        message.what = 1;//播放图片

        //判断播放广告方式
        if (videos.size() > 0 && pictures.size() > 0) {
            //视频与广告图片一起轮播
            type = 3;
            whatShow = 0;
        } else if (videos.size() == 0 && pictures.size() > 0) {
            //图片轮播
            type = 2;
            whatShow = 0;
        } else {
            //播放视频
            type = 1;
            whatShow = 1;
            message.what=2;//播放视频
        }
        handler.sendMessage(message);
    }

    /**
     * 获取轮播图
     */
    private void getBanner() {
        //动态设置轮播图数量
        list_img.clear();
        for (int i = 0; i < pictures.size(); i++) {
            int m = 400;
            int h = 300;
            ImageView iv = new ImageView(MainActivity.this);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            File f = new File(pictures.get(i));
            Picasso.with(MainActivity.this)
                    .load(f)
                    .resize(m, h)
                    .centerCrop()
                    .into(iv);
            list_img.add(iv);
            iv = null;
        }

        //设置轮播图
        vp_main_ads.setAdapter(new ViewpagerDotsAdapter(MainActivity.this, list_img));
    }

    /**
     * 时间任务 时间更新
     *
     * @author Administrator
     */
    private class TimeTask implements Runnable {
        public void run() {
            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);
        }
    }

    /**
     * 设置右上角的总票数
     */
    private void setTicketNum() {
        tv_main_header_ticket_num.setText(PrinterCase.getInstance().getCurrentTicketNumber());
    }

    //开启时执行延迟服务
    @Override
    protected void onStart() {
        super.onStart();
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            //时间更新，一分钟刷新一次
            scheduledExecutorService.scheduleWithFixedDelay(new TimeTask(), 1, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * 重回页面是刷新票数量等信息
     */
    public void initTicket() {
        //票数
        tv_main_ticket_num.setText("0");
        tv_main_ticket_1_num.setText("0");
        tv_main_ticket_2_num.setText("0");
        //金额
        tv_main_ticket_amount.setText("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Test","MainActivity OnResume");
        //设置票数
        setTicketNum();
        getPriceList();
        initTicket();

        player.onResume();
        initAds();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Test","MainActivity OnPause");
        player.onPause();
    }

    //关掉延迟服务
    @Override
    public void onDestroy() {
        super.onDestroy();
        player.onDestroy();
        unregisterReceiver(mediaReceiver);
        shutDownScheduledExecutorService();
        Log.i("Test", "MainActvity onDestroy scheduledExecutorService shutdown");
    }

    private void shutDownScheduledExecutorService() {
        if (!scheduledExecutorService.isShutdown())
            scheduledExecutorService.shutdown();
    }

}
