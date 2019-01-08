package com.tvm.tvm.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.activity.TicketEditActivity;
import com.tvm.tvm.bean.PriceList;

import java.util.List;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/6
 * - @Time： 20:18
 */
public class TicketListAdapter extends RecyclerView.Adapter {

    private Context context;

    List<PriceList> priceLists;

    public TicketListAdapter(Context context,List<PriceList> priceLists){
        this.context = context;
        this.priceLists = priceLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_ticket,null);
        return new TicketItemViewHolder(v, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PriceList item = priceLists.get(position+1);
                Intent intent = new Intent(context,TicketEditActivity.class);
                intent.putExtra("id",item.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 15;
    }

    class TicketItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv_item_ticket_title;

        private TextView tv_item_ticket_price;

        private TextView tv_item_ticket_desc;

        private ImageView iv_item_ticket_icon;

        private OnItemClickListener onItemClickListener;

        public TicketItemViewHolder(@NonNull View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            tv_item_ticket_desc = itemView.findViewById(R.id.tv_item_ticket_desc);
            tv_item_ticket_price = itemView.findViewById(R.id.tv_item_ticket_price);
            tv_item_ticket_title = itemView.findViewById(R.id.tv_item_ticket_title);
            iv_item_ticket_icon = itemView.findViewById(R.id.iv_item_ticket_icon);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v,getPosition());
        }
    }

    public  static interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
}
