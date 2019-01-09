package com.tvm.tvm.activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.PriceListAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PriceList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/6
 * - @Time： 10:14
 */
public class PriceListActivity extends BaseActivity {

    @BindView(R.id.rv_ticket_list_list)
    RecyclerView rv_ticket_list_list;

    @BindView(R.id.ib_ticket_list_add)
    ImageButton ib_ticket_list_add;

    @BindView(R.id.ib_ticket_list_back)
    ImageButton ib_ticket_list_back;

    private List<PriceList> priceLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);
        ButterKnife.bind(this);
        getList();
        initLayout();
    }

    public void getList(){
        priceLists = AppApplication.getApplication().getDaoSession().getPriceListDao().queryBuilder().list();
    }

    private void initLayout(){
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_ticket_list_list.setLayoutManager(layoutManager);
        rv_ticket_list_list.setAdapter(new PriceListAdapter(this,priceLists));
        rv_ticket_list_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        rv_ticket_list_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
    }

    @OnClick({R.id.ib_ticket_list_add,R.id.ib_ticket_list_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_ticket_list_back:
                this.finish();
                break;
            case R.id.ib_ticket_list_add:
                startActivity(this,PriceEditActivity.class);
                break;
        }
    }
}
