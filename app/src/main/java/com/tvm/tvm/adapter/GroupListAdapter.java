package com.tvm.tvm.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
