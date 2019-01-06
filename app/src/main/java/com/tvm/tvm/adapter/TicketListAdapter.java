package com.tvm.tvm.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/6
 * - @Time： 20:18
 */
public class TicketListAdapter extends RecyclerView.Adapter {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class TicketItemViewHolder extends RecyclerView.ViewHolder{

        public TicketItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}
