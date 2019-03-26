package com.tvm.tvm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.dao.SettingDao;

/**
 * activity基类
 */
public class BaseActivity extends Activity {

    Setting setting;

    public void startActivity(Context context,Class cls){
        Intent intent = new Intent();
        intent.setClass(this,cls);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
        this.startActivity(intent);
    }

    public void startActivity(Context context,Intent intent,Class cls){
        intent.setClass(this,cls);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init(){
        SettingDao settingDao = AppApplication.getApplication().getDaoSession().getSettingDao();
        setting = settingDao.queryBuilder().where(SettingDao.Properties.Id.eq(1)).unique();
    }

}
