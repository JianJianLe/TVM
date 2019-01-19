package com.tvm.tvm.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/19
 * - @Time： 19:08
 */
public class PaySuccessActivity extends BaseActivity {

    @BindView(R.id.tv_pay_success_company_name)
    TextView tv_pay_success_company_name;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        ButterKnife.bind(this);
        initData();
    }

    public void initData(){
        String companyName = SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.COMPANY_NAME,"");
        tv_pay_success_company_name.setText(companyName);
    }
}
