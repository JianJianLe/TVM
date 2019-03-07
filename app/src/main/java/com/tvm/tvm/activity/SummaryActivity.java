package com.tvm.tvm.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.util.DateUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  统计
 * - @Author:  Jat
 * - @Date:  2019/3/5
 * - @Time： 22:51
 */
public class SummaryActivity extends BaseActivity{

    @BindView(R.id.ib_summary_back)
    ImageButton ib_summary_back;

    @BindView(R.id.tv_summary_cash_total)
    TextView tv_summary_cash_total;

    @BindView(R.id.tv_summary_wechat_total)
    TextView tv_summary_wechat_total;

    @BindView(R.id.tv_summary_alipay_total)
    TextView tv_summary_alipay_total;

    @BindView(R.id.tv_summary_bill_summary)
    TextView tv_summary_bill_summary;

    private DaoSession daoSession;

    String startTime;

    String endTime;

    Map<String,Double> group = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        Intent intent = getIntent();
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
    }

    @OnClick({R.id.ib_summary_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_summary_back:
                this.finish();
                break;
        }
    }

    public void initData(){
        //aoumt
        tv_summary_bill_summary.setText(String.valueOf(getAmount()));
        //wechat
        tv_summary_wechat_total.setText(String.valueOf(getClassify(1)));
        //alipay
        tv_summary_alipay_total.setText(String.valueOf(getClassify(0)));
        //cash
        tv_summary_cash_total.setText(String.valueOf(getClassify(2)));
        //group

    }

    public double getAmount(){
        double sum = 0;
        String sql = "SELECT sum(amount) from payment_record WHERE payTime>'"+DateUtils.formatDate(startTime,0)+"' and payTime<'"+DateUtils.formatDate(endTime,1)+"'";
        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery("",null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    sum = cursor.getDouble(0);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return sum;
    }

    public double getClassify(int type){
        double sum = 0;
        String sql = "SELECT sum(amount) from payment_record WHERE payTime>'"+DateUtils.formatDate(startTime,0)+"' and payTime<'"+DateUtils.formatDate(endTime,1)+"' and type = "+type;
        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery("",null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    sum = cursor.getDouble(0);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return  sum;
    }

    public void groupBy(){
        String sql = "SELECT sum(amount) from payment_record WHERE payTime>'"+DateUtils.formatDate(startTime,0)+"' and payTime<'"+DateUtils.formatDate(endTime,1)+"' GROUP BY title";
        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery("",null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    group.put(cursor.getString(0),cursor.getDouble(1));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
    }

}
