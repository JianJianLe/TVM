package com.tvm.tvm.activity;

import android.os.Bundle;

import com.tvm.tvm.R;

import butterknife.ButterKnife;

public class PayDetailActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_detail);
        ButterKnife.bind(this);
    }


}
