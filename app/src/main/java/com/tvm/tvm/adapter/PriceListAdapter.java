package com.tvm.tvm.adapter;

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

    List<Price> priceLists;

    public PriceListAdapter(Context context, List<Price> priceLists){
        this.context = context;
        this.priceLists = priceLists;
    }

    @NonNull
    @Override
    public TicketItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_ticket,null);
        return new TicketItemViewHolder(v, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Price item = priceLists.get(position+1);
                Intent intent = new Intent(context,PriceEditActivity.class);
                intent.putExtra("id",item.getId());
                context.startActivity(intent);
            }

            @Override
            public void onLongClick(View view, final int position) {
                final ConfirmDialogUtils confirmDialogUtils = new ConfirmDialogUtils(context,"删除价格","请确认是否删除价格【"+priceLists.get(position).getTitle()+"】");
                confirmDialogUtils.show();
                confirmDialogUtils.setOnDialogClickListener(new ConfirmDialogUtils.OnDialogClickListener() {
                    @Override
                    public void onOKClick() {
                        deletePrice(priceLists.get(position).getId());
                        ToastUtils.showText(context,StringUtils.DELETE_SUCCESS);
                        confirmDialogUtils.dismiss();
                    }

                    @Override
                    public void onCancelClick() {
                        confirmDialogUtils.dismiss();
                    }
                });
            }
        });
    }

    private void deletePrice(Long priceId){
        PriceDao priceDao = AppApplication.getApplication().getDaoSession().getPriceDao();
        priceDao.deleteByKey(priceId);
    }

    @Override
    public void onBindViewHolder(TicketItemViewHolder viewHolder, int i) {
        viewHolder.tv_item_ticket_desc.setText(priceLists.get(i).getDescription());
        viewHolder.tv_item_ticket_title.setText(priceLists.get(i).getTitle());
        viewHolder.tv_item_ticket_price.setText(priceLists.get(i).getPrice()+"");
        viewHolder.iv_item_ticket_icon.setImageBitmap(BitmapUtils.byte2Bitmap(priceLists.get(i).getPic()));
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


        @Override
        public boolean onLongClick(View v) {
            onItemClickListener.onLongClick(v,getPosition());
            return false;
        }
    }

    public  static interface OnItemClickListener{
        void onItemClick(View view,int position);
        void onLongClick(View view,int position);
    }
}
