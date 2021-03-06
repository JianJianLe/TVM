package com.tvm.tvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.activity.PriceEditActivity;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.util.BitmapUtils;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.view.ConfirmDialogUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.List;

/**
 * - @Description:  价格列表
 * - @Author:  Jat
 * - @Date:  2019/1/6
 * - @Time： 20:18
 */
public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.TicketItemViewHolder> {

    private Context context;

    private List<Price> priceLists;

    private OnItemClickListener onItemClickListener;

    public PriceListAdapter(Context context, List<Price> priceLists ,OnItemClickListener onItemClickListener){
        this.context = context;
        this.priceLists = priceLists;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TicketItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_ticket,null);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v);
            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onLongClick(v);
                return false;
            }
        });
        return new TicketItemViewHolder(v, onItemClickListener);
    }


    @Override
    public void onBindViewHolder(TicketItemViewHolder viewHolder, int i) {
        viewHolder.tv_item_ticket_desc.setText(priceLists.get(i).getDescription());
        viewHolder.tv_item_ticket_title.setText(priceLists.get(i).getTitle());
        viewHolder.tv_item_ticket_price.setText(priceLists.get(i).getPrice()+"");
        viewHolder.iv_item_ticket_icon.setImageBitmap(BitmapUtils.byte2Bitmap(priceLists.get(i).getPic()));
        viewHolder.ll_item_ticket_operation.setVisibility(View.GONE);
        viewHolder.itemView.setTag(i);
    }

    @Override
    public int getItemCount() {
        if (priceLists!=null){
            return priceLists.size();
        }else {
            return 0;
        }
    }

    class TicketItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        public TextView tv_item_ticket_title;

        public TextView tv_item_ticket_price;

        public TextView tv_item_ticket_desc;

        public ImageView iv_item_ticket_icon;

        public LinearLayout ll_item_ticket_operation;

        private OnItemClickListener onItemClickListener;

        public TicketItemViewHolder(@NonNull View itemView,OnItemClickListener onItemClickListener) {
            super(itemView);
            tv_item_ticket_desc = itemView.findViewById(R.id.tv_item_ticket_desc);
            tv_item_ticket_price = itemView.findViewById(R.id.tv_item_ticket_price);
            tv_item_ticket_title = itemView.findViewById(R.id.tv_item_ticket_title);
            iv_item_ticket_icon = itemView.findViewById(R.id.iv_item_ticket_icon);
            ll_item_ticket_operation = itemView.findViewById(R.id.ll_item_ticket_operation);
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(v);
        }


        @Override
        public boolean onLongClick(View v) {
            onItemClickListener.onLongClick(v);
            return false;
        }
    }

    public  static interface OnItemClickListener{
        void onItemClick(View view);
        void onLongClick(View view);
    }
}
