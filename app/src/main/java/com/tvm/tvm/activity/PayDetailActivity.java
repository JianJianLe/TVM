package com.tvm.tvm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.util.view.ToastUtils;

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

    @BindView(R.id.iv_pay_detail_increase)
    ImageView iv_pay_detail_increase;

    @BindView(R.id.iv_pay_detail_decrease)
    ImageView iv_pay_detail_decrease;

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
    @BindView(R.id.tv_pay_detail_receieve_amount)
    TextView tv_pay_detail_receieve_amount;

    //未收金额
    @BindView(R.id.tv_pay_detail_left_amount)
    TextView tv_pay_detail_left_amount;

    //单价
    private double ticketPrice = 0d;
    //已收钱
    private double receieveAmount = 0d;
    //剩余应收钱
    private double leftAmount = 0d;
    //票数
    private int num = 0;
    //传递过来得票价id
    private Long priceId;

    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        priceId = getIntent().getLongExtra("priceId",0l);
        initData();
    }

    /**
     - @Description:  初始化
     - @Author:  Jat
     - @Date:  ${DATE}
     - @Time： ${TIME}
     */
    public void initData(){
        PriceDao priceDao = daoSession.getPriceDao();
        Price price = priceDao.queryBuilder().where(PriceDao.Properties.Id.eq(priceId)).unique();
        if (price!=null){
            ticketPrice = price.getPrice();
        }else {
            ToastUtils.showText(this,"找不到对应票价");
        }
    }

    /**
     * 监听函数
     * @param view
     */
    @OnClick({R.id.iv_pay_detail_decrease,R.id.iv_pay_detail_increase,R.id.iv_pay_detail_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_pay_detail_back:
                this.finish();
                break;
            case R.id.iv_pay_detail_decrease:
                //判断票数不能小于0
                if (num==0){
                    ToastUtils.showText(this,"票数不能少于0");
                }else {
                    num--;
                    updateAmount();
                }
                break;
            case R.id.iv_pay_detail_increase:
                //更改票数和价钱
                num++;
                updateAmount();
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
        tv_pay_detail_pay_amount.setText(String.valueOf(ticketPrice*num));
        leftAmount = ticketPrice* num - receieveAmount;
        tv_pay_detail_left_amount.setText(String.valueOf(leftAmount));
    }
}
