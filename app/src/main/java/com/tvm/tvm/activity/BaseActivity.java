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

public class BaseActivity extends Activity {

    public void startActivity(Context context,Class cls){
        Intent intent = new Intent();
        intent.setClass(this,cls);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
        this.startActivity(intent);
    }

    public void showToastLong(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }

    public void showToastShort(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
