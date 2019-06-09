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
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.device.printer.NormalTicket;
import com.tvm.tvm.util.device.printer.PrinterCase;
import com.tvm.tvm.util.device.printer.SummaryTicket;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.Date;
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

    @BindView(R.id.tv_summary_onlinepay_total)
    TextView tv_summary_onlinepay_total;

    @BindView(R.id.tv_summary_cash_count)
    TextView tv_summary_cash_count;

    @BindView(R.id.tv_summary_onlinepay_count)
    TextView tv_summary_onlinepay_count;

    @BindView(R.id.tv_summary_bill_summary)
    TextView tv_summary_bill_summary;

    @BindView(R.id.tv_summary_count_summary)
    TextView tv_summary_count_summary;

    @BindView(R.id.tv_summary_print)
    TextView tv_summary_print;

    private DaoSession daoSession;

    String startTime;

    String endTime;

    String title;

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
        title = intent.getStringExtra("title");
        initData();
    }

    @OnClick({R.id.ib_summary_back,R.id.tv_summary_print})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_summary_back:
                this.finish();
                break;
            case R.id.tv_summary_print:
                printSummaryTicket();
                break;
        }
    }

    private void printSummaryTicket(){
        SummaryTicket summaryTicket= PrinterCase.getInstance().summaryTicket;
        NormalTicket normalTicket=PrinterCase.getInstance().normalTicket;
        normalTicket.setTicketName("统计");
        normalTicket.setDateStr(TimeUtil.dateFormat.format(new Date()));
        normalTicket.setDeviceNumber(setting.getDeviceNo());
        summaryTicket.setStartDateTime(startTime);
        summaryTicket.setEndDateTime(endTime);
        summaryTicket.setCashTotalAmount(tv_summary_cash_total.getText().toString());
        summaryTicket.setCashTotalCount(tv_summary_cash_count.getText().toString());
        summaryTicket.setOnlinePayTotalAmount(tv_summary_onlinepay_total.getText().toString());
        summaryTicket.setOnlinePayTotalCount(tv_summary_onlinepay_count.getText().toString());
        summaryTicket.setTotalAmount(tv_summary_bill_summary.getText().toString());
        summaryTicket.setTotalCount(tv_summary_count_summary.getText().toString());
        PrinterCase.getInstance().print();
    }

    public void initData(){
        int total=0;
        //aoumt
        tv_summary_bill_summary.setText(String.valueOf(getAmount()));

        //online pay
        List<Double> onlinePayList = getClassify(0);
        if (onlinePayList!=null && onlinePayList.size()>0){
            tv_summary_onlinepay_total.setText(String.valueOf(onlinePayList.get(0)));
            int onlinePayCount=new Double(onlinePayList.get(1)).intValue();
            tv_summary_onlinepay_count.setText(onlinePayCount+"");
            total += onlinePayCount;
        }
        //cash
        List<Double> cashList = getClassify(2);
        if (cashList!=null && cashList.size()>0){
            tv_summary_cash_total.setText(String.valueOf(cashList.get(0)));
            int cashCount=new Double(cashList.get(1)).intValue();
            tv_summary_cash_count.setText(cashCount+"");
            total += cashCount;
        }

        //total count
        tv_summary_count_summary.setText(total+"");

        //group
//        groupBy();
//        groupListAdapter = new GroupListAdapter(this,summaryList);
//        lv_summary_group_list.setAdapter(groupListAdapter);
    }

    public double getAmount(){
        double sum = 0;
        String sql = "SELECT sum(amount) as sum from "+PaymentRecordDao.TABLENAME+" WHERE "+PaymentRecordDao.Properties.PayTime.columnName
                +"   BETWEEN  "+DateUtils.formatDate(startTime,0).getTime()+" AND "+DateUtils.formatDate(endTime,1).getTime();
        if (title!=null){
            if (!title.equals("所有")){
                sql = sql + " AND " + PaymentRecordDao.Properties.Title.columnName + " = '" + title+"'";
            }
        }
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

    public List<Double> getClassify(int type){
        List<Double> list = new ArrayList<>();
        double sum = 0;
        double num = 0;
        String sql = "SELECT sum(amount) as sum,sum(num) as num  from "+PaymentRecordDao.TABLENAME+" WHERE "+PaymentRecordDao.Properties.PayTime.columnName
                +"   BETWEEN  "+DateUtils.formatDate(startTime,0).getTime()+" AND "+DateUtils.formatDate(endTime,1).getTime()+" and type = "+type;
        if (title!=null){
            if (!title.equals("所有")){
                sql = sql + " AND " + PaymentRecordDao.Properties.Title.columnName + " = '" + title+"'";
            }
        }
        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery(sql,new String[0]);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int index = cursor.getColumnIndex("sum");
                    sum = cursor.getDouble(index);
                    list.add(sum);
                    index = cursor.getColumnIndex("num");
                    num = cursor.getDouble(index);
                    list.add(num);
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 根据票据分类统计
     */
//    public void groupBy(){
//        String sql = "SELECT title,sum(amount) from "+PaymentRecordDao.TABLENAME+" WHERE "+PaymentRecordDao.Properties.PayTime.columnName
//                +"   BETWEEN  "+DateUtils.formatDate(startTime,0).getTime()+" AND "+DateUtils.formatDate(endTime,1).getTime()+" GROUP BY title";
//        Cursor cursor = daoSession.getPaymentRecordDao().getDatabase().rawQuery(sql,new String[0]);
//        try {
//            if (cursor.moveToFirst()) {
//                do {
//                    Summary summary = new Summary();
//                    summary.setTitle(cursor.getString(0));
//                    summary.setAmount(cursor.getDouble(1));
//                    summaryList.add(summary);
//                } while (cursor.moveToNext());
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            cursor.close();
//        }
//    }

}
