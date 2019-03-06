package com.tvm.tvm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.BillListAdpter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.util.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/3/6
 * - @Time： 19:52
 */
public class BillListActivity extends BaseActivity {

    @BindView(R.id.lv_bill_list_list)
    ListView lv_bill_list_list;

    private DaoSession daoSession;

    private BillListAdpter billListAdpter;

    String startTime;

    String endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        Intent intent = getIntent();
        startTime = intent.getStringExtra("startTime");
        endTime = intent.getStringExtra("endTime");
    }

    public void query(){
        List<PaymentRecord> recordList = daoSession.getPaymentRecordDao().queryBuilder().where(PaymentRecordDao.Properties.PayTime.between(DateUtils.formatDate(startTime,0),DateUtils.formatDate(endTime,1))).list();
        billListAdpter = new BillListAdpter(this,recordList);
        lv_bill_list_list.setAdapter(billListAdpter);
    }

}
