package com.tvm.tvm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.util.PreConfig;
import com.tvm.tvm.util.SharedPrefsUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  $desc$
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
                break;
            case R.id.tv_user_list_manager:
                break;
            case R.id.ib_user_list_back:
                this.finish();
                break;
        }
    }
}
