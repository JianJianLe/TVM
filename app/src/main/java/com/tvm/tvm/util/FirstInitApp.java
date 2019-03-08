package com.tvm.tvm.util;

import android.content.Context;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.User;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.bean.dao.UserDao;
import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.File;
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
            addData();
//            initBillSettings();
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

    //测试数据
    public static void addData(){
        PaymentRecordDao paymentRecordDao = daoSession.getPaymentRecordDao();

        PaymentRecord paymentRecord1 = new PaymentRecord();
        paymentRecord1.setAmount(10*(1));
        paymentRecord1.setNum(1);
        paymentRecord1.setPrice(10d);
        paymentRecord1.setPayTime(new Date());
        paymentRecord1.setType(2);
        paymentRecord1.setTitle("票据1");
        paymentRecord1.setPriceId(2l);
        paymentRecordDao.save(paymentRecord1);

        for (int i = 0 ; i < 10 ; i++){
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setAmount(10*(i+1));
            paymentRecord.setNum(i+1);
            paymentRecord.setPrice(10d);
            paymentRecord.setPayTime(new Date());
            paymentRecord.setType(2);
            paymentRecord.setTitle("票据"+i);
            paymentRecord.setPriceId(2l);
            paymentRecordDao.save(paymentRecord);
        }
    }

    public static void initBillSettings(){
        String path = FileUtil.getBillSettingsPath(context);
        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }
        String fileName = "/billSetting";
        String ticketName = "[TicketName]=儿童票";
        String templateNum = "[TemplateNumber]=";
        for (int i = 1; i<6 ; i++){
            String content = templateNum+i+"/r/n"+ticketName+i+"/r/n"+StringUtils.billContext;
            TxtUtils.writeToFile1(path+fileName+i+".txt",content,"UTF-8");
        }
    }

}
