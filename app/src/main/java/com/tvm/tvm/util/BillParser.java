package com.tvm.tvm.util;

import android.util.Log;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.dao.SettingDao;

public class BillParser {

    private static Setting setting;
    private static SettingDao settingDao;

    public BillParser(){
        initDB();
    }

    private void initDB(){
        settingDao = AppApplication.getApplication().getDaoSession().getSettingDao();
        setting = settingDao.queryBuilder().where(SettingDao.Properties.Id.eq(1)).unique();
    }

    public String getBillTypeCMD(){
        initDB();
        return Integer.toHexString(Integer.parseInt(parseBillType(setting.getBillType()),2)).toUpperCase() + "00";
    }

    //billTimesNumber
    //billAcceptorCashAmountType;//纸币面额
    //7F001DF00030333430434E590000010601050A14326402020202020200006404F42A
    public void parseBillChannel(String cmdStr){

    }

    private String parseBillType(String billType){
        String billTypeResult="11[100][50][20][10][5][1]";
        String[] tempArr=billType.split(",");
        printInfo("billType="+billType);
        for(int i=0;i<tempArr.length;i++){
            String tempStr="["+tempArr[i].trim()+"]";
            if(tempStr.equals("[1]")){
                billTypeResult=billTypeResult.replace("[1]", "1");
            }
            if(tempStr.equals("[5]")){
                billTypeResult=billTypeResult.replace("[5]", "1");
            }
            if(tempStr.equals("[10]")){
                billTypeResult=billTypeResult.replace("[10]", "1");
            }
            if(tempStr.equals("[20]")){
                billTypeResult=billTypeResult.replace("[20]", "1");
            }
            if(tempStr.equals("[50]")){
                billTypeResult=billTypeResult.replace("[50]", "1");
            }
            if(tempStr.equals("[100]")){
                billTypeResult=billTypeResult.replace("[100]", "1");
            }
        }

        billTypeResult= billTypeResult.replace("[1]", "0")
                .replace("[5]", "0")
                .replace("[10]", "0")
                .replace("[20]", "0")
                .replace("[50]", "0")
                .replace("[100]", "0");
        printInfo("billTypeResult="+billTypeResult);
        return billTypeResult;
    }

    public int getReceivedMoney(String resultStr){
        int rcvdMoney=0;
        if(!resultStr.equals("00")){
            if(resultStr.equals("01")){
                rcvdMoney=1;
                printInfo("收到1元");
            }
            if(resultStr.equals("02")){
                rcvdMoney=5;
                printInfo("收到5元");
            }
            if(resultStr.equals("03")){
                rcvdMoney=10;
                printInfo("收到10元");
            }
            if(resultStr.equals("04")){
                rcvdMoney=20;
                printInfo("收到20元");
            }
            if(resultStr.equals("05")){
                rcvdMoney=50;
                printInfo("收到50元");
            }
            if(resultStr.equals("06")){
                rcvdMoney=100;
                printInfo("收到100元");
            }
        }else{
            rcvdMoney=-1;
        }
        return rcvdMoney;
    }
    private void printInfo(String infoStr){
        Log.i("Test", infoStr);
    }
}
