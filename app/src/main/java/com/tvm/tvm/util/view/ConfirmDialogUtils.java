package com.tvm.tvm.util.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tvm.tvm.R;

public class ConfirmDialogUtils extends Dialog {
    private Context context;
    private TextView tv_confirm_layout_title,tv_confirm_layout_content;
    private View okBtn,cancelBtn;
    private OnDialogClickListener dialogClickListener;
    //标题
    String title;
    //内容
    String content;
    //是否显示"取消"按钮
    boolean isShowCancel;

    public ConfirmDialogUtils(Context context,String title,String content) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        initialize();
    }

    public ConfirmDialogUtils(Context context,String title,String content, boolean isShowCancel) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.isShowCancel=isShowCancel;
        initialize();
    }
 
    //初始化View
    private void initialize() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.confirm_dialog_layout, null);
        setContentView(view);
        initWindow();

        tv_confirm_layout_title = (TextView) findViewById(R.id.tv_confirm_layout_title);
        tv_confirm_layout_content = (TextView) findViewById(R.id.tv_confirm_layout_content);
        tv_confirm_layout_title.setText(title);
        tv_confirm_layout_content.setText(content);
        okBtn = findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                dismiss();
                if(dialogClickListener != null){
                    dialogClickListener.onOKClick();
                }
            }
        });

        cancelBtn = findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View v) {
                dismiss();
                if(dialogClickListener != null){
                    dialogClickListener.onCancelClick();
                }
            }
        });
        if(!isShowCancel)
            cancelBtn.setVisibility(View.GONE);
    }
 
    /**
     *添加黑色半透明背景
     */
    private void initWindow() {
        Window dialogWindow = getWindow();
        dialogWindow.setBackgroundDrawable(new ColorDrawable(0));//设置window背景
        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//设置输入法显示模式
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics();//获取屏幕尺寸
        lp.width = (int) (d.widthPixels * 0.8); //宽度为屏幕80%
        lp.gravity = Gravity.CENTER;  //中央居中
        dialogWindow.setAttributes(lp);
    }
 
    public void setOnDialogClickListener(OnDialogClickListener clickListener){
        dialogClickListener = clickListener;
    }

    /**
     *添加按钮点击事件
     */
    public interface OnDialogClickListener{
        void onOKClick();
        void onCancelClick();
    }
}
