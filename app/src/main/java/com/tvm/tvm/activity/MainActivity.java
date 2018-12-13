package com.tvm.tvm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.tvm.tvm.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnLongClick(R.id.tv_main_comany_name)
    public boolean login(){
        startActivity(this,LoginActivity.class);
        return true;
    }
}
