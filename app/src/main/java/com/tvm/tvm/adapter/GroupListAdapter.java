package com.tvm.tvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.bean.Summary;

import java.util.List;
import java.util.Map;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/3/6
 * - @Time： 23:33
 */
public class GroupListAdapter extends BaseAdapter {

    private Context context;

    private List<Summary> summaryList;

    public GroupListAdapter(Context context,List<Summary> summaryList){
        this.context = context;
        this.summaryList = summaryList;
    }

    @Override
    public int getCount() {
        return summaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return summaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_summary_group_list,null);
        TextView tv_item_sumarry_group_list_title = convertView.findViewById(R.id.tv_item_sumarry_group_list_title);
        TextView tv_item_sumarry_group_list_sum = convertView.findViewById(R.id.tv_item_sumarry_group_list_sum);
        tv_item_sumarry_group_list_sum.setText(summaryList.get(position).getAmount()+"");
        tv_item_sumarry_group_list_title.setText(summaryList.get(position).getTitle());
        return convertView;
    }
}
