package com.tvm.tvm.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.BillListAdpter;
import com.tvm.tvm.adapter.TitleSpinnerAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.util.DateUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.ArrayList;
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

    @BindView(R.id.btn_bill_query_summary)
    Button btn_bill_query_summary;

    @BindView(R.id.sp_bill_query_title_drop_down_list)
    Spinner sp_bill_query_title_drop_down_list;

    //时间选择器弹出框
    private DatePickerDialog datePickerDialog;
    //时间选择器
    private TimePickerDialog timePickerDialog;
    //日历
    private Calendar cal;
    //当前年月日
    private int year,month,day;

    List<String> ticketTitleList = new ArrayList<>();

    TitleSpinnerAdapter spinnerAdapter;

    String startTime;
    String endTime;

    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_query);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        getNowDate();
        getDistinctTickectTitle();
        initView();
    }

    private void initView(){
        tv_bill_query_start_date.setText(DateUtils.formatDate(new Date())+" 00:00:00");
        tv_bill_query_end_date.setText(DateUtils.formatDate(new Date())+" 23:59:59");

        spinnerAdapter = new TitleSpinnerAdapter(this,ticketTitleList);
        sp_bill_query_title_drop_down_list.setAdapter(spinnerAdapter);
    }

    private void getNowDate(){
        cal=Calendar.getInstance();
        year=cal.get(Calendar.YEAR);       //获取年月日时分秒
        Log.i("wxy","year"+year);
        month=cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day=cal.get(Calendar.DAY_OF_MONTH);
    }


    @OnClick({R.id.btn_bill_query_end_date,R.id.btn_bill_query_start_date,
            R.id.btn_bill_query_query,R.id.ib_bill_query_back,R.id.btn_bill_query_summary})
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
                if (query()){
                    //添加日期，跳转到列表页面
                    Intent intent = new Intent();
                    intent.putExtra("startTime",startTime);
                    intent.putExtra("endTime",endTime);
                    String title = (String) sp_bill_query_title_drop_down_list.getSelectedItem();
                    intent.putExtra("title",title);
                    startActivity(BillQueryActivity.this,intent,BillListActivity.class);
                }
                break;
            case R.id.btn_bill_query_summary:
                if (query()){
                    //添加日期，跳转到列表页面
                    Intent intent = new Intent();
                    intent.putExtra("startTime",startTime);
                    intent.putExtra("endTime",endTime);
                    String title = (String) sp_bill_query_title_drop_down_list.getSelectedItem();
                    intent.putExtra("title",title);
                    startActivity(BillQueryActivity.this,intent,SummaryActivity.class);
                }
                break;
        }
    }

    public void getDistinctTickectTitle(){
        PaymentRecordDao paymentRecordDao = daoSession.getPaymentRecordDao();
        String sql = "SELECT DISTINCT "+PaymentRecordDao.Properties.Title.columnName + " FROM " +paymentRecordDao.TABLENAME;
        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery(sql,null);
        ticketTitleList.add("所有");
        try {
            if (cursor.moveToFirst()) {
                do {
                    int index = cursor.getColumnIndex(PaymentRecordDao.Properties.Title.columnName);
                    String title = cursor.getString(index);
                    ticketTitleList.add(title);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
    }


    /**
     * 显示日期选择框
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
                String dayStr = String.valueOf(dayOfMonth);
                if (dayStr.length()==1){
                    dayStr = "0"+dayStr;
                }
                //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                if (type==0){
                    tv_bill_query_start_date.setText(year+"-"+monthStr+"-"+dayStr+" "+tv_bill_query_start_date.getText().toString().split(" ")[1]);
                    datePickerDialog.dismiss();
                }else {
                    tv_bill_query_end_date.setText(year+"-"+monthStr+"-"+dayStr+" "+tv_bill_query_end_date.getText().toString().split(" ")[1]);
                    datePickerDialog.dismiss();
                }
                showTimeDialog(type);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    /**
     * 显示时间选择框
     * @param type
     */
    public void showTimeDialog(final int type){
        int hourOfDay , minuteOfDay;
        if (type==0){
            hourOfDay = 0;
            minuteOfDay = 0;
        }else {
            hourOfDay = 23;
            minuteOfDay = 59;
        }
        timePickerDialog = new TimePickerDialog(this, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                String hour = String.valueOf(hourOfDay).length()==1 ? "0"+String.valueOf(hourOfDay):String.valueOf(hourOfDay);

                String minuteStr = String.valueOf(minute).length()==1 ? "0"+String.valueOf(minute):String.valueOf(minute);

                if (type==0){
                    tv_bill_query_start_date.setText(tv_bill_query_start_date.getText().toString().split(" ")[0]+" "+hour+":"+minuteStr+":00");
                    timePickerDialog.dismiss();
                }else {
                    tv_bill_query_end_date.setText(tv_bill_query_end_date.getText().toString().split(" ")[0]+" "+hour+":"+minuteStr+":59");
                    timePickerDialog.dismiss();
                }
            }
        },hourOfDay,minuteOfDay,true);
        timePickerDialog.show();
    }

    public boolean query(){
        startTime = tv_bill_query_start_date.getText().toString().trim();
        endTime = tv_bill_query_end_date.getText().toString().trim();
        if (checkMandatery()){
            return true;
        }else {
            ToastUtils.showText(this,"开始时间必须大于结束时间，请重新选择再查询！！！");
            return false;
        }
    }

    public boolean checkMandatery(){
        boolean canQuery = true;
        if (DateUtils.compare2Date(tv_bill_query_start_date.getText().toString(),tv_bill_query_end_date.getText().toString())){

        }else {
            canQuery = false;
        }
        return canQuery;
    }
}
