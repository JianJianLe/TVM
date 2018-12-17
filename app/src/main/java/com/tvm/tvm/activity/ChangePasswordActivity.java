package com.tvm.tvm.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.User;
import com.tvm.tvm.bean.dao.UserDao;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.view.ConfirmDialogUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.List;

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

    private Context tag = ChangePasswordActivity.this;

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

    ConfirmDialogUtils confirmDialogUtils;

    private String userName;

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    ToastUtils.showText(tag,StringUtils.UPDATE_PASSWORD_SUCCESS,true);
                    ChangePasswordActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        userName = getIntent().getStringExtra("userName");
        initView();
    }

    private void initView(){
        tv_change_password_username.setText(userName);
    }



    @OnClick({R.id.ib_change_password_back,R.id.bt_change_password_change})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ib_change_password_back:
                this.finish();
                break;
            case R.id.bt_change_password_change:
                if (checkPassword()){
                    changePassword();
                }
                break;
        }
    }

    /**
     * 修改密码
     */
    private void changePassword(){
        confirmDialogUtils = new ConfirmDialogUtils(tag,StringUtils.TITLE_CHANGE_PASSWORD,String.format(StringUtils.CONTENT_CHANGE_PASSWORD,userName));
        confirmDialogUtils.setOnDialogClickListener(new ConfirmDialogUtils.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                confirmDialogUtils.dismiss();
                updateDB();
            }

            @Override
            public void onCancelClick() {
                confirmDialogUtils.dismiss();
            }
        });
        confirmDialogUtils.show();
    }

    private void updateDB(){
        UserDao userDao = AppApplication.getApplication().getDaoSession().getUserDao();
        List<User> userList = userDao.queryBuilder().where(UserDao.Properties.UserName.eq(userName)).list();
        if (userList!=null && userList.size()>0){
            User user = userList.get(0);
            user.setPassword(et_change_password_psd.getText().toString().trim());
            userDao.update(user);
            Message msg = new Message();
            msg.what = 1;
            myHandler.sendMessage(msg);
        }else {
            ToastUtils.showText(this,StringUtils.NO_USER);
        }
    }

    /**
     * 验证密码是否一致
     * @return
     */
    private boolean checkPassword(){
        boolean isPass = true;
        String psd = et_change_password_psd.getText().toString().trim();
        String confirm = et_change_password_confirm.getText().toString().trim();
        if (psd==null || confirm == null || psd.equals("") || confirm.equals("")){
            ToastUtils.showText(this,StringUtils.PASS_WORD_EMPTY,false);
            return false;
        }
        if (!psd.equals(confirm)){
            ToastUtils.showText(this,StringUtils.PASS_WORD_DIFF,false);
            return false;
        }
        return isPass;
    }
}
