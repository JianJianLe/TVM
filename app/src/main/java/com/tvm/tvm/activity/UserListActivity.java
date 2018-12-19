package com.tvm.tvm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.SharedPrefsUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  修改密码用户列表
 * - @Author:  Jat
 * - @Date:  2018/12/14
 * - @Time： 23:39
 */
public class UserListActivity extends BaseActivity {

    @BindView(R.id.tv_user_list_admin)
    TextView tv_user_list_admin;
    @BindView(R.id.tv_user_list_manager)
    TextView tv_user_list_manager;
    @BindView(R.id.ib_user_list_back)
    ImageButton ib_user_list_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        if (SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.USER,"").equals("manager")){
            tv_user_list_admin.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_user_list_admin,R.id.tv_user_list_manager,R.id.ib_user_list_back})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_user_list_admin:
                change(1);
                break;
            case R.id.tv_user_list_manager:
                change(2);
                break;
            case R.id.ib_user_list_back:
                this.finish();
                break;
        }
    }

    /**
     * 跳转修改密码页
     */
    public void change(int user){
        Intent intent = new Intent();
        if (user == 1){
            intent.putExtra("userName","admin");
        }else {
            intent.putExtra("userName","manager");
        }
        startActivity(this,intent,ChangePasswordActivity.class);
    }
}
