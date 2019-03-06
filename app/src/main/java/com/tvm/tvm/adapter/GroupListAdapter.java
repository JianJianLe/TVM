package com.tvm.tvm.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Map;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/3/6
 * - @Time： 23:33
 */
public class GroupListAdapter extends BaseAdapter {

    private Context context;

    private Map<String,Double> map;

    public GroupListAdapter(Context context,Map<String,Double> map){
        this.context = context;
        this.map = map;
    }

    @Override
    public int getCount() {
        return map.size();
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
