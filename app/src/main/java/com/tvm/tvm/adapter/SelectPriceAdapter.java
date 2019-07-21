package com.tvm.tvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.util.BitmapUtils;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.view.ToastUtils;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final PriceViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ticket,null);
            viewHolder = new PriceViewHolder();
            viewHolder.ll_item_ticket_layout = convertView.findViewById(R.id.ll_item_ticket_layout);
            viewHolder.tv_item_ticket_title = convertView.findViewById(R.id.tv_item_ticket_title);
            viewHolder.tv_item_ticket_price = convertView.findViewById(R.id.tv_item_ticket_price);
            viewHolder.tv_item_ticket_desc = convertView.findViewById(R.id.tv_item_ticket_desc);
            viewHolder.iv_item_ticket_icon = convertView.findViewById(R.id.iv_item_ticket_icon);
            viewHolder.iv_item_ticket_add = convertView.findViewById(R.id.iv_item_ticket_add);
            viewHolder.iv_item_ticket_sub = convertView.findViewById(R.id.iv_item_ticket_sub);
            viewHolder.tv_item_ticket_num = convertView.findViewById(R.id.tv_item_ticket_num);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (PriceViewHolder) convertView.getTag();
        }
        viewHolder.tv_item_ticket_desc.setText(priceList.get(position).getDescription());
        viewHolder.tv_item_ticket_title.setText(priceList.get(position).getTitle());
        viewHolder.tv_item_ticket_price.setText(String.valueOf((int)priceList.get(position).getPrice()));
        viewHolder.iv_item_ticket_icon.setImageBitmap(BitmapUtils.byte2Bitmap(priceList.get(position).getPic()));
        if (position%2 == 0){
            viewHolder.ll_item_ticket_layout.setBackgroundResource(R.drawable.bg_ticket_two);
        }
        viewHolder.iv_item_ticket_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.valueOf(viewHolder.tv_item_ticket_num.getText().toString().trim());
                num ++;
                viewHolder.tv_item_ticket_num.setText(num+"");
                updateBroadcastReceiver(position,0);
            }
        });
        viewHolder.iv_item_ticket_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = Integer.valueOf(viewHolder.tv_item_ticket_num.getText().toString().trim());
                if (num<=0){
                    ToastUtils.showText(context,StringUtils.TICKET_NUM_LESS_ZERO);
                }else {
                    num --;
                    viewHolder.tv_item_ticket_num.setText(num+"");
                    updateBroadcastReceiver(position,1);
                }
            }
        });
        return convertView;
    }

    private class PriceViewHolder{
        LinearLayout ll_item_ticket_layout ;
        TextView tv_item_ticket_title ;
        TextView tv_item_ticket_price ;
        TextView tv_item_ticket_desc ;
        ImageView iv_item_ticket_icon ;
        ImageView iv_item_ticket_add;
        ImageView iv_item_ticket_sub;
        TextView tv_item_ticket_num;
    }

    /**
     * 发送广播通知activity更改总票数和价格
     * @param position
     * @param operation 0--加，1--减
     */
    public void updateBroadcastReceiver(int position, int operation){
        Intent intent = new Intent();
        intent.putExtra("position",position);
        intent.putExtra("operation",operation);
        intent.setAction(StringUtils.TICKET_RECEIVER);
        context.sendBroadcast(intent);
    }

}
