package com.tvm.tvm.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.User;
import com.tvm.tvm.bean.dao.UserDao;
import com.tvm.tvm.util.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import freemarker.template.utility.StringUtil;

public class LoginActivity extends BaseActivity {

    private Context TAG = LoginActivity.this;

    @BindView(R.id.et_login_username)
    EditText et_login_username;
    @BindView(R.id.et_login_password)
    EditText et_login_password;
    @BindView(R.id.bt_login_login)
    Button bt_login_login;

    //用户dao
    private UserDao userDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userDao = AppApplication.getApplication().getDaoSession().getUserDao();
    }

    @OnClick(R.id.bt_login_login)
    public void login(){
        if ("".equals(et_login_username.getText().toString().trim())){
//            Toast.makeText(TAG,"用户名不能为空",Toast.LENGTH_LONG).show();
            showToastLong(TAG,StringUtils.USER_NAME_EMPTY);
            return;
        }
        if ("".equals(et_login_password.getText().toString().trim())){
            showToastLong(TAG,StringUtils.PASS_WORD_EMPTY);
            return;
        }

        if (proccessLogin(et_login_username.getText().toString().trim(),et_login_password.getText().toString().trim())){
            startActivity(TAG,SettingsActivity.class);
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
            userList = userDao.loadAll();
            if (userList==null||userList.size()==0){
                initDatabase();
                userList = userDao.loadAll();
                Toast.makeText(TAG,userList.toString(),Toast.LENGTH_LONG).show();
                showToastShort(TAG,StringUtils.TRY_AGAIN);
                return false;
            }
        }else {
            User user = userList.get(0);
            if (passWord.equals(user.getPassword())){
                return true;
            }else {
                showToastLong(TAG,StringUtils.WRONG_PASS_WORD);
                return false;
            }
        }
        return false;
    }

    /**
     * 初始化用户
     */
    public void initDatabase(){
        showToastShort(TAG,StringUtils.INIT_SYSTEM);
        //添加初始用户
        User admin = new User();
        admin.setUserName("admin");
        admin.setPassword("admin");
        User manager = new User();
        manager.setUserName("manager");
        manager.setPassword("manager123");
        userDao.save(admin);
        userDao.save(manager);
    }
}
