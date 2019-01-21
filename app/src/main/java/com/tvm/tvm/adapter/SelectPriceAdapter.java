package com.tvm.tvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.util.BitmapUtils;

import java.util.List;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/15
 * - @Time： 22:46
 */
public class SelectPriceAdapter extends BaseAdapter {

    private Context context;

    private List<Price> priceList;

    public SelectPriceAdapter(Context context,List<Price> priceList){
        this.context = context;
        this.priceList = priceList;
    }

    @Override
    public int getCount() {
        return priceList.size();
    }

    @Override
    public Object getItem(int position) {
        return priceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return priceList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_ticket,null);
        TextView tv_item_ticket_title = convertView.findViewById(R.id.tv_item_ticket_title);
        TextView tv_item_ticket_price = convertView.findViewById(R.id.tv_item_ticket_price);
        TextView tv_item_ticket_desc = convertView.findViewById(R.id.tv_item_ticket_desc);
        ImageView iv_item_ticket_icon = convertView.findViewById(R.id.iv_item_ticket_icon);
        tv_item_ticket_desc.setText(priceList.get(position).getDescription());
        tv_item_ticket_title.setText(priceList.get(position).getTitle());
        tv_item_ticket_price.setText(String.valueOf(priceList.get(position).getPrice()));
        iv_item_ticket_icon.setImageBitmap(BitmapUtils.byte2Bitmap(priceList.get(position).getPic()));
        return convertView;
    }
}
