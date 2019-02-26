package com.tvm.tvm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.BillSettingListAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.BillSetting;
import com.tvm.tvm.bean.dao.BillSettingDao;
import com.tvm.tvm.bean.dao.DaoSession;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BillSettingListActivity extends BaseActivity {

    @BindView(R.id.ib_bill_setting_list_back)
    ImageButton ib_bill_setting_list_back;

    @BindView(R.id.lv_bill_setting_list_data)
    ListView lv_bill_setting_list_data;

    List<BillSetting> billSettingList = new ArrayList<>();

    BillSettingListAdapter billSettingListAdapter;

    DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_setting_list);
        ButterKnife.bind(this);
        daoSession = AppApplication.getApplication().getDaoSession();
        initData();
    }

    /**
     * 初始化数据
     */
    public void initData(){
        BillSettingDao billSettingDao = daoSession.getBillSettingDao();
        billSettingList = billSettingDao.queryBuilder().list();
        billSettingListAdapter = new BillSettingListAdapter(this,billSettingList);
        lv_bill_setting_list_data.setAdapter(billSettingListAdapter);
    }

    @OnClick({R.id.ib_bill_setting_list_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_bill_setting_list_back:
                this.finish();
                break;
        }
    }
}
