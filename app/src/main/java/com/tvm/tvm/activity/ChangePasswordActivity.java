package com.tvm.tvm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tvm.tvm.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  修改密码
 * - @Author:  Jat
 * - @Date:  2018/12/15
 * - @Time： 0:11
 */
public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.ib_change_password_back)
    ImageButton ib_change_password_back;
    @BindView(R.id.et_change_password_psd)
    EditText et_change_password_psd;
    @BindView(R.id.et_change_password_confirm)
    EditText et_change_password_confirm;
    @BindView(R.id.bt_change_password_change)
    Button bt_change_password_change;
    @BindView(R.id.tv_change_password_username)
    TextView tv_change_password_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        initView(getIntent().getStringExtra("userName"));
    }

    public void initView(String userName){
        tv_change_password_username.setText(userName);
    }



    @OnClick({R.id.ib_change_password_back,R.id.bt_change_password_change})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ib_change_password_back:
                this.finish();
                break;
            case R.id.bt_change_password_change:
                break;
        }
    }
}
