package com.tvm.tvm.activity;

import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.view.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  常规设置
 * - @Author:  Jat
 * - @Date:  2018/12/17
 * - @Time： 22:04
 */
public class NormalSettingActivity extends BaseActivity {

    @BindView(R.id.ib_normal_setting_back)
    ImageButton ib_normal_setting_back;

    @BindView(R.id.et_normal_setting_company_name)
    EditText et_normal_setting_company_name;

    @BindView(R.id.et_normal_setting_time_out)
    EditText et_normal_setting_time_out;

    @BindView(R.id.et_normal_setting_pay_time_out)
    EditText et_normal_setting_pay_time_out;

    @BindView(R.id.et_normal_setting_print_time_out)
    EditText et_normal_setting_print_time_out;

    @BindView(R.id.et_normal_setting_pay_desc)
    EditText et_normal_setting_pay_desc;

    private DaoSession daoSession;


    String compayName;
    int timeOut ;
    int payTimeOut ;
    int printTimeOut ;
    String payDesc ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_setting);
        ButterKnife.bind(this);
        setListener();
        daoSession = AppApplication.getApplication().getDaoSession();
        initView();
    }

    private void initView(){
        et_normal_setting_company_name.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.COMPANY_NAME,""));
        et_normal_setting_time_out.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.SELECT_TIME_OUT,0)+"");
        et_normal_setting_pay_time_out.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.PAY_TIME_OUT,0)+"");
        et_normal_setting_print_time_out.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.PRINT_TIME_OUT,0)+"");
        et_normal_setting_pay_desc.setText(SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.PAY_DESC,""));
    }

    private void setListener(){
        et_normal_setting_pay_time_out.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        et_normal_setting_time_out.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        et_normal_setting_print_time_out.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
    }

    private boolean checkSubmit(){
        boolean isOk = true;
        compayName = et_normal_setting_company_name.getText().toString().trim();
        timeOut =Integer.valueOf(et_normal_setting_time_out.getText().toString().trim().equals("")? "0" : et_normal_setting_time_out.getText().toString().trim());
        payTimeOut =Integer.valueOf(et_normal_setting_pay_time_out.getText().toString().trim().equals("")? "0" : et_normal_setting_pay_time_out.getText().toString().trim());
        printTimeOut =Integer.valueOf(et_normal_setting_print_time_out.getText().toString().trim().equals("")? "0" : et_normal_setting_print_time_out.getText().toString().trim());
        payDesc = et_normal_setting_pay_desc.getText().toString().trim();
        if (payDesc==null || payDesc.equals("")){
            ToastUtils.showText(this,StringUtils.EMPTY_PAY_DESC);
            return false;
        }
        if (compayName==null || compayName.equals("")){
            ToastUtils.showText(this,StringUtils.EMPTY_COMPANY_NAME);
            return false;
        }
        if (timeOut == 0 || payTimeOut == 0 || printTimeOut==0){
            ToastUtils.showText(this,StringUtils.EMPTY_TIME_OUT);
            return false;
        }
        return isOk;
    }

    private void updateSetting(){
        if(checkSubmit()){
            SharedPrefsUtil.putValue(getApplicationContext(),PreConfig.COMPANY_NAME,compayName);
            SharedPrefsUtil.putValue(getApplicationContext(),PreConfig.PAY_DESC,payDesc);
            SharedPrefsUtil.putValue(getApplicationContext(),PreConfig.SELECT_TIME_OUT,timeOut);
            SharedPrefsUtil.putValue(getApplicationContext(),PreConfig.PAY_TIME_OUT,payTimeOut);
            SharedPrefsUtil.putValue(getApplicationContext(),PreConfig.PRINT_TIME_OUT,printTimeOut);
            ToastUtils.showText(getApplicationContext(),StringUtils.UPDATE_SUCCESS,true);
        }
    }

    @OnClick({R.id.ib_normal_setting_back,R.id.bt_normal_setting_confirm})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ib_normal_setting_back:
                this.finish();
                break;
            case R.id.bt_normal_setting_confirm:
                updateSetting();
                break;
        }
    }

}
