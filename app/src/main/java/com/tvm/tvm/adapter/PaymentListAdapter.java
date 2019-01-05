package com.tvm.tvm.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tvm.tvm.bean.PaymentRecord;

import java.util.List;

public class PaymentListAdapter extends BaseAdapter {

    private Context context;

    private List<PaymentRecord> paymentRecordList;

    public PaymentListAdapter(Context context, List<PaymentRecord> paymentRecordList){
        this.context = context;
        this.paymentRecordList = paymentRecordList;
    }

    @Override
    public int getCount() {
        return paymentRecordList.size()+1;
    }

    @Override
    public Object getItem(int position) {
        if (position==0){
            return null;
        }else {
            return paymentRecordList.get(position+1);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private class ViewHolder{
//        private TextView
    }
}
