package com.tvm.tvm.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.TVMKey;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.TVMKeyDao;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;


public class AuthorizeUtil {

    private TVMKeyDao tvmKeyDao;
    private static AuthorizeUtil instance;

    public synchronized static AuthorizeUtil getInstance(){
        if (instance==null) {
            instance = new AuthorizeUtil();
        }
        return instance;
    }

    public AuthorizeUtil(){
        DaoSession daoSession = AppApplication.getApplication().getDaoSession();
        tvmKeyDao=daoSession.getTVMKeyDao();
    }

    public void initTVMKey(){
        TVMKey tvmKey=new TVMKey();
        tvmKey.setKey(StringUtils.TVMKEY);
        if(tvmKeyDao.count()==0)
            tvmKeyDao.save(tvmKey);
    }

    public boolean isKeyExisted(){
        if(tvmKeyDao.count()>0){
            TVMKey tvmKey=tvmKeyDao.queryBuilder().where(TVMKeyDao.Properties.Id.eq(1)).unique();
            if(tvmKey.getKey().equals(StringUtils.TVMKEY))
                return true;
        }

        if(FileUtils.readKeyFile(PreConfig.USB_FOLDER)){
            initTVMKey();
            return true;
        }
        return false;
    }

}
