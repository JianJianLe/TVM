package com.tvm.tvm.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.User;
import com.tvm.tvm.bean.dao.UserDao;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    private Context TAG = LoginActivity.this;

    @BindView(R.id.et_login_username)
    EditText et_login_username;
    @BindView(R.id.et_login_password)
    EditText et_login_password;
    @BindView(R.id.bt_login_login)
    Button bt_login_login;
    @BindView(R.id.ib_login_back)
    ImageButton ib_login_back;

    //用户dao
    private UserDao userDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userDao = AppApplication.getApplication().getDaoSession().getUserDao();
    }

    @OnClick({R.id.bt_login_login,R.id.ib_login_back})
    public void login(View view){
        switch (view.getId()){
            case R.id.bt_login_login:
                loginLogic();
                break;
            case R.id.ib_login_back:
                this.finish();
                break;
        }
    }

    /**
     * 登录操作
     */
    public void loginLogic(){
        if ("".equals(et_login_username.getText().toString().trim())){
            ToastUtils.showText(TAG,StringUtils.USER_NAME_EMPTY);
            return;
        }
        if ("".equals(et_login_password.getText().toString().trim())){
            ToastUtils.showText(TAG,StringUtils.PASS_WORD_EMPTY);
            return;
        }

        if (proccessLogin(et_login_username.getText().toString().trim(),et_login_password.getText().toString().trim())){
            if ("superManager".equals(et_login_username.getText().toString().trim())){
                initUser();
            }else {
                startActivity(TAG,SettingsActivity.class);
                SharedPrefsUtil.putValue(getApplicationContext(),PreConfig.USER,et_login_username.getText().toString().trim());
                this.finish();
            }
        }
    }

    /**
     * 登录与初始化用户
     * @param userName
     * @param passWord
     * @return
     */
    private boolean proccessLogin(String userName , String passWord){
        List<User> userList = userDao.queryBuilder().where(UserDao.Properties.UserName.eq(userName)).list();
        if (userList==null||userList.size()==0){
            ToastUtils.showText(TAG,StringUtils.NO_USER,Toast.LENGTH_SHORT);
        }else {
            User user = userList.get(0);
            if (passWord.equals(user.getPassword())){
                return true;
            }else {
                ToastUtils.showText(TAG,StringUtils.WRONG_PASS_WORD);
                return false;
            }
        }
        return false;
    }
    /**
     * 初始化用户
     */
    public void initUser(){
        ToastUtils.showText(this,StringUtils.INIT_USER,Toast.LENGTH_SHORT);
        //添加初始用户
        User admin = new User();
        admin.setUserName("admin");
        admin.setPassword("admin");
        User manager = new User();
        manager.setUserName("manager");
        manager.setPassword("manager123");
        userDao.update(admin);
        userDao.update(manager);
        ToastUtils.showText(this,StringUtils.INIT_FINISH,Toast.LENGTH_SHORT);
    }

}
