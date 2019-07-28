package com.tvm.tvm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.tvm.tvm.R;
import com.tvm.tvm.adapter.PriceListAdapter;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.paydevice.PayDeviceUtil;
import com.tvm.tvm.util.view.ConfirmDialogUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/6
 * - @Time： 10:14
 */
public class PriceListActivity extends BaseActivity {

    @BindView(R.id.rv_ticket_list_list)
    RecyclerView rv_ticket_list_list;

    @BindView(R.id.ib_ticket_list_add)
    ImageButton ib_ticket_list_add;

    @BindView(R.id.ib_ticket_list_back)
    ImageButton ib_ticket_list_back;

    private List<Price> priceList;

    private PriceListAdapter priceListAdapter;

    private Context context = PriceListActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getList();
        initLayout();
    }

    public void getList(){
        priceList = AppApplication.getApplication().getDaoSession().getPriceDao().queryBuilder().where(PriceDao.Properties.IsDelete.eq(0)).list();
    }

    private void initLayout(){
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_ticket_list_list.setLayoutManager(layoutManager);
        PriceListAdapter.OnItemClickListener onItemClickListener =  new PriceListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = (int) view.getTag();
                Price item = priceList.get(position);
                Intent intent = new Intent(context,PriceEditActivity.class);
                intent.putExtra("priceId",item.getId());
                context.startActivity(intent);
            }

            @Override
            public void onLongClick(final View view) {
                final int position = (int) view.getTag();
                final ConfirmDialogUtils confirmDialogUtils = new ConfirmDialogUtils(context,"删除价格","请确认是否删除价格【"+priceList.get(position).getTitle()+"】");
                confirmDialogUtils.show();
                confirmDialogUtils.setOnDialogClickListener(new ConfirmDialogUtils.OnDialogClickListener() {
                    @Override
                    public void onOKClick() {
                        deletePrice(priceList.get(position).getId());
                        priceList.remove(position);
                        priceListAdapter.notifyDataSetChanged();
                        ToastUtils.showText(context,StringUtils.DELETE_SUCCESS);
                        confirmDialogUtils.dismiss();
                        //上报本地通道信息到支付盒子
                        PayDeviceUtil.getInstance().cmd_UploadParams();
                    }

                    @Override
                    public void onCancelClick() {
                        confirmDialogUtils.dismiss();
                    }
                });
            }
        };
        priceListAdapter = new PriceListAdapter(context,priceList,onItemClickListener);
        rv_ticket_list_list.setAdapter(priceListAdapter);
        rv_ticket_list_list.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        rv_ticket_list_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
    }


    private void deletePrice(Long priceId){
        PriceDao priceDao = AppApplication.getApplication().getDaoSession().getPriceDao();
        Price price = priceDao.queryBuilder().where(PriceDao.Properties.Id.eq(priceId)).unique();
        if (price!=null){
            price.setIsDelete(1);
            priceDao.update(price);
        }
    }

    @OnClick({R.id.ib_ticket_list_add,R.id.ib_ticket_list_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_ticket_list_back:
                this.finish();
                break;
            case R.id.ib_ticket_list_add:
                if (priceList.size()==20){
                    ToastUtils.showText(PriceListActivity.this, "不能超过20种票价，请删除重新设置！！！");
                    return;
                }
                startActivity(this,PriceEditActivity.class);
                break;
        }
    }
}
