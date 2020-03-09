package com.tvm.tvm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.BillListAdpter;
import com.tvm.tvm.adapter.PriceListAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.util.DateUtils;
import com.tvm.tvm.util.DialogUtils;
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.device.printer.NormalTicket;
import com.tvm.tvm.util.device.printer.PrinterCase;

import java.util.ArrayList;
import java.util.Date;
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

    private PaymentRecord paymentRecord;

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
        bindPrintTicketEvents();
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

    //打印补单
    public void bindPrintTicketEvents(){
        lv_bill_list_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                paymentRecord =(PaymentRecord) parent.getItemAtPosition(position);
                billListAdpter.getItem(position);
                billListAdpter.notifyDataSetInvalidated();
                printTicketDialog((position+1)+"");
            }
        });
    }


    private void printTicketDialog(String indexStr){
        Dialog dialog=new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否要打印补单:  编号" + indexStr + "?")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printReplacementTicket();
                        return;
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).create();
        dialog.show();
    }

    private void printReplacementTicket(){
        new Thread(){
            public void run() {
                setReplacementTicket();
                PrinterCase.getInstance().print();
                TimeUtil.delay(1000);
            }
        }.start();
    }

    private void setReplacementTicket(){
        NormalTicket normalTicket=PrinterCase.getInstance().normalTicket;
        normalTicket.setTicketName(getString(R.string.printer_replacement));//"补单票"
        normalTicket.setDeviceNumber(setting.getDeviceNo());
        normalTicket.setPayType(paymentRecord.getTypeStr());
        normalTicket.setTicketTitle(paymentRecord.getTitle());
        normalTicket.setPrice(new Double(paymentRecord.getPrice()).intValue()+"");
        normalTicket.setDateStr(TimeUtil.dateFormat.format(paymentRecord.getPayTime()));
    }

}
