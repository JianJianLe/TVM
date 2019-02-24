package com.tvm.tvm.util;

import android.content.Context;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.User;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.bean.dao.UserDao;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.view.ToastUtils;

import java.util.Date;

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
        SettingDao settingDao = daoSession.getSettingDao();
        Setting setting = new Setting();
        setting.setDeviceNo("00001");
        setting.setShopName("广州市亿儿美有限公司");
        setting.setPayDesc("本机只接收5元、10元、20元，不设找零");
        setting.setPayTimeOut(15);
        setting.setPrintTimeOut(15);
        setting.setSelectTimeOut(15);
        settingDao.save(setting);
    }

    public static void addData(){
        PaymentRecordDao paymentRecordDao = daoSession.getPaymentRecordDao();

        for (int i = 0 ; i < 10 ; i++){
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setAmount(10*(i+1));
            paymentRecord.setNum(i+1);
            paymentRecord.setPrice(10d);
            paymentRecord.setPayTime(new Date());
            paymentRecord.setType(2);
            paymentRecordDao.save(paymentRecord);
        }
    }
}
