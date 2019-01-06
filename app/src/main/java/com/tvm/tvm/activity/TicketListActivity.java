package com.tvm.tvm.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.tvm.tvm.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/6
 * - @Time： 10:14
 */
public class TicketListActivity extends BaseActivity {

    @BindView(R.id.rv_ticket_list_list)
    RecyclerView rv_ticket_list_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_list);
        ButterKnife.bind(this);
    }


    private void initLayout(){
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_ticket_list_list.setLayoutManager(layoutManager);

    }

}
