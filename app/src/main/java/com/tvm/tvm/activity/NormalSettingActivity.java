package com.tvm.tvm.activity;

import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.util.SharedPrefsUtil;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * - @Description:  常规设置
 * - @Author:  Jat
 * - @Date:  2018/12/17
 * - @Time： 22:04
 */
public class NormalSettingActivity extends BaseActivity {

    @BindView(R.id.normal_setting_layout)
    LinearLayout normal_setting_layout;

    @BindView(R.id.normal_setting_layout_md5_key)
    LinearLayout normal_setting_layout_md5_key;

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

    @BindView(R.id.et_normal_setting_device_no)
    EditText et_normal_setting_device_no;

    @BindView(R.id.et_normal_setting_md5_key)
    EditText et_normal_setting_md5_key;

    @BindView(R.id.rgp_print_qrcode)
    RadioGroup rgp_print_qrcode;

    @BindView(R.id.rbt_isprinted)
    RadioButton rbt_isprinted;

    @BindView(R.id.rbt_isnotprinted)
    RadioButton rbt_isnotprinted;

    @BindView(R.id.bt_normal_setting_confirm)
    Button bt_normal_setting_confirm;

    private DaoSession daoSession;

    String compayName;
    int timeOut ;
    int payTimeOut ;
    int printTimeOut ;
    String payDesc ;
    String deviceNo;
    String md5Key;
    String printQRCodeFlag;

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
        if (SharedPrefsUtil.getValue(getApplicationContext(),PreConfig.USER,"").equals("manager")){
            normal_setting_layout_md5_key.setVisibility(View.GONE);
            bt_normal_setting_confirm.setVisibility(View.GONE);
            et_normal_setting_company_name.setEnabled(false);
            et_normal_setting_time_out.setEnabled(false);
            et_normal_setting_pay_time_out.setEnabled(false);
            et_normal_setting_print_time_out.setEnabled(false);
            et_normal_setting_pay_desc.setEnabled(false);
            et_normal_setting_device_no.setEnabled(false);
            rgp_print_qrcode.setEnabled(false);
            rbt_isprinted.setEnabled(false);
            rbt_isnotprinted.setEnabled(false);
        }

        SettingDao settingDao = daoSession.getSettingDao();
        Setting setting = settingDao.queryBuilder().where(SettingDao.Properties.Id.eq(1l)).unique();
        if (setting!=null){
            et_normal_setting_company_name.setText(setting.getShopName());
            et_normal_setting_time_out.setText(setting.getSelectTimeOut()+"");
            et_normal_setting_pay_time_out.setText(setting.getPayTimeOut()+"");
            et_normal_setting_print_time_out.setText(setting.getPrintTimeOut()+"");
            et_normal_setting_pay_desc.setText(setting.getPayDesc());
            et_normal_setting_device_no.setText(setting.getDeviceNo());
            et_normal_setting_md5_key.setText(setting.getMd5Key());

            if(setting.getPrintQRCodeFlag().equals("Yes")){
                printQRCodeFlag="Yes";
                rgp_print_qrcode.check(R.id.rbt_isprinted);
            }
            else{
                printQRCodeFlag="No";
                rgp_print_qrcode.check(R.id.rbt_isnotprinted);
            }
        }
    }

    private void setListener(){
        et_normal_setting_pay_time_out.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        et_normal_setting_time_out.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        et_normal_setting_print_time_out.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        et_normal_setting_device_no.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
    }

    private boolean checkSubmit(){
        boolean isOk = true;
        compayName = et_normal_setting_company_name.getText().toString().trim();
        timeOut =Integer.valueOf(et_normal_setting_time_out.getText().toString().trim().equals("")? "0" : et_normal_setting_time_out.getText().toString().trim());
        payTimeOut =Integer.valueOf(et_normal_setting_pay_time_out.getText().toString().trim().equals("")? "0" : et_normal_setting_pay_time_out.getText().toString().trim());
        printTimeOut =Integer.valueOf(et_normal_setting_print_time_out.getText().toString().trim().equals("")? "0" : et_normal_setting_print_time_out.getText().toString().trim());
        payDesc = et_normal_setting_pay_desc.getText().toString().trim();
        deviceNo = et_normal_setting_device_no.getText().toString().trim();
        md5Key = et_normal_setting_md5_key.getText().toString().trim();
        if (deviceNo.length()!=5){
            ToastUtils.showText(this,StringUtils.WRONG_DEVICE_NO);
            return false;
        }
        if (!isContainChinese(md5Key)){//不含中文字符
            if (md5Key.length()>0&&md5Key.length()!=32){
                ToastUtils.showText(this,StringUtils.WRONG_MD5_KEY);
                return false;
            }
        }else {
            ToastUtils.showText(this,StringUtils.CONTAIN_CHINESE_IN_MD5);
            return false;
        }
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

    /**
     * 检测是否含有中文
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static boolean isContainChinese(String str) {

        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    private void updateSetting(){
        if(checkSubmit()){
            SettingDao settingDao = daoSession.getSettingDao();
            Setting setting = new Setting();
            setting.setId(1l);
            setting.setSelectTimeOut(timeOut);
            setting.setPayTimeOut(payTimeOut);
            setting.setPrintTimeOut(printTimeOut);
            setting.setDeviceNo(deviceNo);
            setting.setShopName(compayName);
            setting.setPayDesc(payDesc);
            setting.setMd5Key(md5Key);
            setting.setPrintQRCodeFlag(printQRCodeFlag);
            settingDao.update(setting);
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
    @OnCheckedChanged({R.id.rbt_isprinted, R.id.rbt_isnotprinted})
    public void onRadioButtonCheckChanged(CompoundButton button, boolean isChecked) {
        if(isChecked) {
            switch (button.getId()) {
                case R.id.rbt_isprinted:
                    printQRCodeFlag = "Yes";
                    break;
                case R.id.rbt_isnotprinted:
                    printQRCodeFlag = "No";
                    break;
            }
        }
    }
}
