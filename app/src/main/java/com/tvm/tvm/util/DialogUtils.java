package com.tvm.tvm.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {

    public static void showDialog(Context context, String title, String message){
        Dialog dialog=new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        return;
                    }
                }).create();
        dialog.show();
    }

    public static Dialog showDialog(Context context,String msg){

        Dialog dialog=new AlertDialog.Builder(context).setTitle("提示")
                .setMessage(msg)
                .setCancelable(false)
                .create();
        dialog.show();
        return dialog;
    }
}
