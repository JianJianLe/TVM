package com.tvm.tvm.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tvm.tvm.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/19
 * - @Time： 17:55
 */
public class PayFinishActivity extends BaseActivity {

    @BindView(R.id.tv_pay_finish_remain)
    TextView tv_pay_finish_remain;

    @BindView(R.id.tv_pay_finish_continue)
    TextView tv_pay_finish_continue;

    @BindView(R.id.tv_pay_finish_print)
    TextView tv_pay_finish_print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_finish);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_pay_finish_continue,R.id.tv_pay_finish_print})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_pay_finish_continue:
                //继续购票
                break;
            case R.id.tv_pay_finish_print:
                //打印余额
                break;
        }
    }
}
