package com.tvm.tvm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.device.TimeUtil;

import java.util.List;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/2/25
 * - @Time： 23:09
 */
public class BillListAdpter extends BaseAdapter {

    private Context context;
    private List<PaymentRecord> paymentRecordList;

    public BillListAdpter(Context context, List<PaymentRecord> paymentRecordList){
        this.context = context;
        this.paymentRecordList = paymentRecordList;
    }

    @Override
    public int getCount() {
        return paymentRecordList.size();
    }

    @Override
    public Object getItem(int position) {
        return paymentRecordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_payment_list,null);
        TextView tv_item_payment_ticket_no = convertView.findViewById(R.id.tv_item_payment_ticket_no);
        TextView tv_item_payment_amount = convertView.findViewById(R.id.tv_item_payment_amount);
        TextView tv_item_payment_num = convertView.findViewById(R.id.tv_item_payment_num);
        TextView tv_item_payment_type = convertView.findViewById(R.id.tv_item_payment_type);
        TextView tv_item_payment_time = convertView.findViewById(R.id.tv_item_payment_time);

        tv_item_payment_ticket_no.setText(paymentRecordList.get(position).getId()+"");
        tv_item_payment_amount.setText(paymentRecordList.get(position).getAmount()+"");
        tv_item_payment_num.setText(paymentRecordList.get(position).getNum()+"");
        tv_item_payment_type.setText(paymentRecordList.get(position).getTypeStr());
        tv_item_payment_time.setText(TimeUtil.dateFormat.format(paymentRecordList.get(position).getPayTime()));
        return convertView;
    }
}
