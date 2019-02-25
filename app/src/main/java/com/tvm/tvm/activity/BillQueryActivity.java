package com.tvm.tvm.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.PayRecordAdpter;
import com.tvm.tvm.adapter.PaymentListAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.util.DateUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillQueryActivity extends BaseActivity{

    @BindView(R.id.ib_bill_query_back)
    ImageButton ib_bill_query_back;

    @BindView(R.id.tv_bill_query_start_date)
    TextView tv_bill_query_start_date;

    @BindView(R.id.tv_bill_query_end_date)
    TextView tv_bill_query_end_date;

    @BindView(R.id.btn_bill_query_start_date)
    Button btn_bill_query_start_date;

    @BindView(R.id.btn_bill_query_end_date)
    Button btn_bill_query_end_date;

    @BindView(R.id.btn_bill_query_query)
    Button btn_bill_query_query;

    @BindView(R.id.rv_bill_query_list)
    ListView rv_bill_query_list;

    //时间选择器弹出框
    private DatePickerDialog datePickerDialog;
    //日历
    private Calendar cal;
    //当前年月日
    private int year,month,day;

    private PayRecordAdpter listAdapter;

    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_query);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        initView();
        getNowDate();
    }

    private void initView(){
        tv_bill_query_start_date.setText(DateUtils.formatDate(new Date()));
        tv_bill_query_end_date.setText(DateUtils.formatDate(new Date()));
    }

    private void getNowDate(){
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy","year"+year);
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
    }


    @OnClick({R.id.btn_bill_query_end_date,R.id.btn_bill_query_start_date,R.id.btn_bill_query_query,R.id.ib_bill_query_back})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ib_bill_query_back:
                this.finish();
                break;
            case R.id.btn_bill_query_start_date:
                showDateDialog(0);
                break;
            case R.id.btn_bill_query_end_date:
                showDateDialog(1);
                break;
            case R.id.btn_bill_query_query:
                query();
                break;
        }
    }


    /**
     * 显示时间选择框
     * @param type 类型：开始还是结束时间 0-开始 1-结束
     */
    public void showDateDialog(final int type){
        datePickerDialog = new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthStr = ++month +"";
                if (monthStr.length()==1){
                    monthStr = "0"+monthStr;
                }
                //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                if (type==0){
                    tv_bill_query_start_date.setText(year+"-"+monthStr+"-"+dayOfMonth);
                    datePickerDialog.dismiss();
                }else {
                    tv_bill_query_end_date.setText(year+"-"+monthStr+"-"+dayOfMonth);
                    datePickerDialog.dismiss();
                }
            }
        },year,month,day);
        datePickerDialog.show();
    }

    public void query(){
        if (checkMandatery()){
            List<PaymentRecord> recordList = daoSession.getPaymentRecordDao().queryBuilder().where(PaymentRecordDao.Properties.PayTime.between(DateUtils.formatDate(tv_bill_query_start_date.getText().toString().trim(),0),DateUtils.formatDate(tv_bill_query_end_date.getText().toString().trim(),1))).list();
            listAdapter = new PayRecordAdpter(this,recordList);
            rv_bill_query_list.setAdapter(listAdapter);
        }else {
            ToastUtils.showText(this,"开始时间必须大于结束时间，请重新选择再查询！！！");
        }
    }

    public boolean checkMandatery(){
        boolean canQuery = true;
        if (DateUtils.compare2Date(tv_bill_query_start_date.getText().toString().trim(),tv_bill_query_end_date.getText().toString().trim())<0){
            canQuery = false;
            return canQuery;
        }
        return canQuery;
    }

}
