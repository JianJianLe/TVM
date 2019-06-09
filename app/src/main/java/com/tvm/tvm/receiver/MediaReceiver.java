package com.tvm.tvm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tvm.tvm.util.AuthorizeUtil;
import com.tvm.tvm.util.FileUtil;
import com.tvm.tvm.util.FileUtils;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.view.ToastUtils;


public class MediaReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action==null)
            return;
        if(Intent.ACTION_MEDIA_EJECT.equals(action) ||
                Intent.ACTION_MEDIA_UNMOUNTED.equals(action))
            return;
        else if(Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            String usbFolder = intent.getDataString().split("file://")[1];
            if(FileUtils.readKeyFile(usbFolder)){
                AuthorizeUtil.getInstance().initTVMKey();
                ToastUtils.showText_Long(context,StringUtils.KEY_REGISTERED);
            }
            if(FileUtils.copyTemplateFolder(usbFolder))
                ToastUtils.showText_Long(context,StringUtils.COPY_TEMPLATE_SUCCESS);
        }
    }
}
