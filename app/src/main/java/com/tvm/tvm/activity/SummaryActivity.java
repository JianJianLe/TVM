package com.tvm.tvm.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.GroupListAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.Summary;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.util.DateUtils;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @BindView(R.id.lv_summary_group_list)
    ListView lv_summary_group_list;

    @BindView(R.id.tv_summary_print)
    TextView tv_summary_print;

    private DaoSession daoSession;

    String startTime;

    String endTime;

    private List<Summary> summaryList = new ArrayList<>();

    private GroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        Intent intent = getIntent();
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        initData();
    }

    @OnClick({R.id.ib_summary_back,R.id.tv_summary_print})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_summary_back:
                this.finish();
                break;
            case R.id.tv_summary_print:
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
        groupBy();
        groupListAdapter = new GroupListAdapter(this,summaryList);
        lv_summary_group_list.setAdapter(groupListAdapter);
    }

    public double getAmount(){
        double sum = 0;
        String sql = "SELECT sum(amount) as sum from "+PaymentRecordDao.TABLENAME+" WHERE "+PaymentRecordDao.Properties.PayTime.columnName
                +"   BETWEEN  "+DateUtils.formatDate(startTime,0).getTime()+" AND "+DateUtils.formatDate(endTime,1).getTime();
        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery(sql,new String[0]);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int index = cursor.getColumnIndex("sum");
                    sum = cursor.getDouble(index);
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
        String sql = "SELECT sum(amount) as sum from "+PaymentRecordDao.TABLENAME+" WHERE "+PaymentRecordDao.Properties.PayTime.columnName
                +"   BETWEEN  "+DateUtils.formatDate(startTime,0).getTime()+" AND "+DateUtils.formatDate(endTime,1).getTime()+" and type = "+type;
        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery(sql,new String[0]);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int index = cursor.getColumnIndex("sum");
                    sum = cursor.getDouble(index);
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
        String sql = "SELECT title,sum(amount) from "+PaymentRecordDao.TABLENAME+" WHERE "+PaymentRecordDao.Properties.PayTime.columnName
                +"   BETWEEN  "+DateUtils.formatDate(startTime,0).getTime()+" AND "+DateUtils.formatDate(endTime,1).getTime()+" GROUP BY title";
        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery(sql,new String[0]);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Summary summary = new Summary();
                    summary.setTitle(cursor.getString(0));
                    summary.setAmount(cursor.getDouble(1));
                    summaryList.add(summary);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
    }

}
