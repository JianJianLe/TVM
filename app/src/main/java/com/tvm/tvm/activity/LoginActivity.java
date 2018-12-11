package com.tvm.tvm.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;

import com.tvm.tvm.R;

import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }
}
