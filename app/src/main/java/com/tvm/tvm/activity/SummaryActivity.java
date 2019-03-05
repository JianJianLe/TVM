package com.tvm.tvm.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.tvm.tvm.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * - @Description:  统计
 * - @Author:  Jat
 * - @Date:  2019/3/5
 * - @Time： 22:51
 */
public class SummaryActivity extends BaseActivity{

    @BindView(R.id.tv_summary_bill_total)
    TextView tv_summary_bill_total;

    @BindView(R.id.tv_summary_wechat_total)
    TextView tv_summary_wechat_total;

    @BindView(R.id.tv_summary_alipay_total)
    TextView tv_summary_alipay_total;

    @BindView(R.id.tv_summary_bill_summary)
    TextView tv_summary_bill_summary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);
    }



}
