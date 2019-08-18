package com.tvm.tvm.activity;

import android.os.Bundle;
import android.view.View;
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
    @BindView(R.id.tv_settings_ticket_list)
    TextView tv_settings_ticket_list;
    @BindView(R.id.tv_settings_bill)
    TextView tv_settings_bill;
    @BindView(R.id.tv_settings_bill_query)
    TextView tv_settings_bill_query;
    @BindView(R.id.tv_settings_exception_query)
    TextView tv_settings_exception_query;
    @BindView(R.id.tv_settings_about)
    TextView tv_settings_about;
    @BindView(R.id.tv_settings_disclaimer)
    TextView tv_settings_disclaimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.tv_settings_person_manage,R.id.tv_settings_normal,R.id.tv_settings_ticket_list,R.id.tv_settings_bill
            ,R.id.tv_settings_bill_query,R.id.tv_settings_exception_query,R.id.tv_settings_about,R.id.tv_settings_disclaimer,R.id.ib_settings_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_settings_back:
                startActivity(this,MainActivity.class);
                this.finish();
                break;
            case R.id.tv_settings_person_manage:
                startActivity(this,UserListActivity.class);
                break;
            case R.id.tv_settings_normal:
                startActivity(this,NormalSettingActivity.class);
                break;
            case R.id.tv_settings_ticket_list:
                startActivity(this,PriceListActivity.class);
                break;
            case R.id.tv_settings_bill:
                startActivity(this,BillSettingListActivity.class);
                break;
            case R.id.tv_settings_bill_query:
                startActivity(this,BillQueryActivity.class);
                break;
            case R.id.tv_settings_exception_query:
                break;
            case R.id.tv_settings_about:
                break;
            case R.id.tv_settings_disclaimer:
                break;
        }
    }
}
