package com.tvm.tvm.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tvm.tvm.bean.User;
import com.tvm.tvm.bean.dao.DaoMaster;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.UserDao;

import java.util.List;

public class AppApplication extends Application {
    private  static AppApplication application;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        //初始化数据库
        setDataBase();
    }

    public static AppApplication getApplication(){
        return application;
    }

    private void setDataBase(){
        // 通过DaoMaster 的内部类 DevOpenHelper，可以得到一个便利的SQLiteOpenHelper 对象。
        // 并不需要去编写「CREATE TABLE」这样的 SQL 语句，greenDAO 已经做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(new GreenDaoContext(),"tvm-db", null);
        db =mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }


    public SQLiteDatabase getDb() {
        return db;
    }


}
