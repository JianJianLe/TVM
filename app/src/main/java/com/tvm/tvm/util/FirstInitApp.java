package com.tvm.tvm.util;

import android.content.Context;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.User;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.UserDao;
import com.tvm.tvm.util.constant.PreConfig;

/**
 * - @Description:  初始化系统
 * - @Author:  Jat
 * - @Date:  2018/12/17
 * - @Time： 22:18
 */
public class FirstInitApp {

    static DaoSession daoSession;

    private static Context context;

    /**
     * 初始化数据库
     */
    public static void initDatabase(Context applicationContext){
        daoSession = AppApplication.getApplication().getDaoSession();
        UserDao userDao = daoSession.getUserDao();
        context = applicationContext;
        //没有数据
        if (userDao.queryBuilder().list().size()==0){
            initUser();
            initNormalSetting();
        }

    }

    /**
     * 初始化用户
     */
    private static void initUser(){
        UserDao userDao = daoSession.getUserDao();
        //添加初始用户
        User admin = new User();
        admin.setUserName("admin");
        admin.setPassword("admin");
        User manager = new User();
        manager.setUserName("manager");
        manager.setPassword("manager123");
        User superManager = new User();
        superManager.setUserName("superManager");
        superManager.setPassword("star2config");
        userDao.save(admin);
        userDao.save(manager);
        userDao.save(superManager);
    }

    private static void initNormalSetting(){
        SharedPrefsUtil.putValue(context,PreConfig.COMPANY_NAME,"广州市亿儿美有限公司");
        SharedPrefsUtil.putValue(context,PreConfig.PAY_DESC,"本机只接收5元、10元、20元，不设找零");
        SharedPrefsUtil.putValue(context,PreConfig.SELECT_TIME_OUT,15);
        SharedPrefsUtil.putValue(context,PreConfig.PAY_TIME_OUT,15);
        SharedPrefsUtil.putValue(context,PreConfig.PRINT_TIME_OUT,15);
    }
}
