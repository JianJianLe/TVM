package com.tvm.tvm.activity;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tvm.tvm.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2018/12/13
 * - @Time： 21:23
 */
public class SettingsActivity extends BaseActivity {

    @BindView(R.id.ib_settings_back)
    ImageButton iv_settings_back;
    @BindView(R.id.tv_settings_person_manage)
    TextView tv_settings_person_manage;
    @BindView(R.id.tv_settings_normal)
    TextView tv_settings_normal;
    @BindView(R.id.tv_settings_ticket)
    TextView tv_settings_ticket;
    @BindView(R.id.tv_settings_bill)
    TextView tv_settings_bill;
    @BindView(R.id.tv_settings_bill_query)
    TextView tv_settings_bill_query;
    @BindView(R.id.tv_settings_exception_query)
    TextView tv_settings_exception_query;
    @BindView(R.id.tv_settings_about)
    TextView tv_settings_about;
    @BindView(R.id.tv_setting_disclaimer)
    TextView tv_setting_disclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.ib_settings_back)
    public void back(){
        this.finish();
    }

    @OnClick(R.id.tv_settings_person_manage)
    public void personal(){

    }


}
