package com.tvm.tvm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.BillListAdpter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/3/6
 * - @Time： 19:52
 */
public class BillListActivity extends BaseActivity {

    @BindView(R.id.lv_bill_list_list)
    ListView lv_bill_list_list;

    @BindView(R.id.ib_bill_list_back)
    ImageButton ib_bill_list_back;

    private DaoSession daoSession;

    private BillListAdpter billListAdpter;

    String startTime;

    String endTime;

    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        Intent intent = getIntent();
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
        title = intent.getStringExtra("title");
        query();
    }

    @OnClick({R.id.ib_bill_list_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_bill_list_back:
                this.finish();
                break;
        }
    }

    public void query(){
        List<PaymentRecord> recordList = new ArrayList<>();
        if (title != null){
            if (title.equals("所有")){
                recordList = daoSession.getPaymentRecordDao().queryBuilder().where(PaymentRecordDao.Properties.PayTime.between(DateUtils.formatDate(startTime),DateUtils.formatDate(endTime))).list();
            }else {
                recordList = daoSession.getPaymentRecordDao().queryBuilder().where(PaymentRecordDao.Properties.PayTime.between(DateUtils.formatDate(startTime),DateUtils.formatDate(endTime)),PaymentRecordDao.Properties.Title.eq(title)).list();
            }
        }
         billListAdpter = new BillListAdpter(this,recordList);
        lv_bill_list_list.setAdapter(billListAdpter);
    }

}
