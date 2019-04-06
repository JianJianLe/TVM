package com.tvm.tvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.util.constant.PreConfig;

import java.util.List;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/3/11
 * - @Time： 22:22
 */
public class TitleSpinnerAdapter extends BaseAdapter {

    private Context context;

    private List<String> titleList;

    public TitleSpinnerAdapter(Context context,List<String> titleList){
        this.titleList = titleList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (titleList!=null){
            return titleList.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return titleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_layout,null);
        TextView tv_item_spinner_title = convertView.findViewById(R.id.tv_item_spinner_title);
        tv_item_spinner_title.setText(titleList.get(position));
        tv_item_spinner_title.setTextSize(PreConfig.ADAPTER_FONT_SIZE);
        return convertView;
    }
}
