package com.tvm.tvm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tvm.tvm.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_query);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_bill_query_end_date,R.id.btn_bill_query_start_date,R.id.btn_bill_query_query,R.id.ib_bill_query_back})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ib_bill_query_back:
                this.finish();
                break;
            case R.id.btn_bill_query_start_date:
                break;
            case R.id.btn_bill_query_end_date:
                break;
            case R.id.btn_bill_query_query:
                break;
        }
    }

}
