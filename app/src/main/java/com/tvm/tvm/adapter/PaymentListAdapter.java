package com.tvm.tvm.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.bean.PaymentRecord;

import java.util.List;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.PaymentViewHolder> {

    private Context context;

    private List<PaymentRecord> paymentRecordList;

    public PaymentListAdapter(Context context, List<PaymentRecord> paymentRecordList){
        this.context = context;
        this.paymentRecordList = paymentRecordList;
    }

    @NonNull
    @Override
    public PaymentListAdapter.PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_payment_list,null);
        return new PaymentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PaymentListAdapter.PaymentViewHolder viewHolder, int i) {
        if (i==0){
            //留列表头
        }else {
            viewHolder.tv_item_payment_ticket_no.setText(paymentRecordList.get(i-1).getId()+"");
            viewHolder.tv_item_payment_amount.setText(paymentRecordList.get(i-1).getAmount()+"");
            viewHolder.tv_item_payment_num.setText(paymentRecordList.get(i-1).getNum()+"");
            viewHolder.tv_item_payment_type.setText(paymentRecordList.get(i-1).getTypeStr());
            viewHolder.tv_item_payment_time.setText(paymentRecordList.get(i-1).getPayTime()+"");
        }
    }

    @Override
    public int getItemCount() {
        if (paymentRecordList==null || paymentRecordList.size()==0){
            return 0;
        }else {
            return paymentRecordList.size()+1;
        }
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_item_payment_ticket_no;

        private TextView tv_item_payment_amount;

        private TextView tv_item_payment_num;

        private TextView tv_item_payment_type;

        private TextView tv_item_payment_time;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_item_payment_ticket_no = itemView.findViewById(R.id.tv_item_payment_ticket_no);
            tv_item_payment_amount = itemView.findViewById(R.id.tv_item_payment_amount);
            tv_item_payment_num = itemView.findViewById(R.id.tv_item_payment_num);
            tv_item_payment_type = itemView.findViewById(R.id.tv_item_payment_type);
            tv_item_payment_time = itemView.findViewById(R.id.tv_item_payment_time);
        }
    }

}
