package com.tvm.tvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.bean.BillSetting;
import com.tvm.tvm.util.view.ConfirmDialogUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.List;

public class BillSettingListAdapter extends BaseAdapter {

    private List<BillSetting> billSettingList;

    private Context context;

    public BillSettingListAdapter(Context context,List<BillSetting> billSettingList){
        this.context = context;
        this.billSettingList = billSettingList;
    }

    @Override
    public int getCount() {
        if (billSettingList!=null){
            return billSettingList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return billSettingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BillSettingItemViewHolder viewHolder;
        if (convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bill_setting_list,null);
            viewHolder = new BillSettingItemViewHolder();
            viewHolder.tv_item_bill_setting_template_num = convertView.findViewById(R.id.tv_item_bill_setting_template_num);
            viewHolder.tv_item_bill_setting_ticket_name = convertView.findViewById(R.id.tv_item_bill_setting_ticket_name);
            viewHolder.btn_item_bill_setting_delete = convertView.findViewById(R.id.btn_item_bill_setting_delete);
            viewHolder.btn_item_bill_setting_detail = convertView.findViewById(R.id.btn_item_bill_setting_detail);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (BillSettingItemViewHolder) convertView.getTag();
        }
        viewHolder.tv_item_bill_setting_ticket_name.setText(billSettingList.get(position).getTicketName());
        viewHolder.tv_item_bill_setting_template_num.setText(billSettingList.get(position).getTemplateNum());
        viewHolder.btn_item_bill_setting_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.btn_item_bill_setting_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfirmDialogUtils confirmDialogUtils = new ConfirmDialogUtils(context,"删除票据","确定删除【"+billSettingList.get(position).getTemplateNum()+"】的票据吗");

                confirmDialogUtils.setOnDialogClickListener(new ConfirmDialogUtils.OnDialogClickListener() {
                    @Override
                    public void onOKClick() {
                        confirmDialogUtils.dismiss();
                        billSettingList.remove(position);
                        BillSettingListAdapter.this.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelClick() {
                        confirmDialogUtils.dismiss();
                    }
                });

                confirmDialogUtils.show();
            }
        });
        return convertView;
    }

    private class BillSettingItemViewHolder{
        TextView tv_item_bill_setting_template_num;
        TextView tv_item_bill_setting_ticket_name;
        Button btn_item_bill_setting_delete;
        Button btn_item_bill_setting_detail;
    }
}
