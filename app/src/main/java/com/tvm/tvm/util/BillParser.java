package com.tvm.tvm.util;

import android.util.Log;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.dao.SettingDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BillParser {

    private static Setting setting;
    private static SettingDao settingDao;
    private String billAcceptorCashAmountType;

    public BillParser(){
        initDB();
    }

    private void initDB(){
        settingDao = AppApplication.getApplication().getDaoSession().getSettingDao();
        setting = settingDao.queryBuilder().where(SettingDao.Properties.Id.eq(1)).unique();
    }

    public String getBillTypeCMD(){
        //initDB();
        return Integer.toHexString(Integer.parseInt(parseBillType(setting.getBillType()),2)).toUpperCase() + "00";
    }

    //billTimesNumber
    //billAcceptorCashAmountType;//纸币面额
    //7F001DF00030333430434E590000010601050A14326402020202020200006404F42A
    public void parseBillChannel(String cmdStr){
        //initDB();
        int totalNum=Integer.parseInt(cmdStr.substring(15*2, 15*2+2));
        setting.setBillTimesNumber(Integer.parseInt(cmdStr.substring(14*2, 14*2+2)));
        setting.setBillAcceptorCashAmountType(getCmdBill(cmdStr.substring(16*2, 16*2 + totalNum * 2)));
        settingDao.save(setting);
    }

    private String getCmdBill(String targetStr){
        List<String> billTemplateList=Arrays.asList("1","1","1","1","1","1","1","1");
        for(int i=0;i<targetStr.length()/2;i++){
            String hexStr = targetStr.substring(i*2,i*2+2);
            billTemplateList.set(i, "["+Integer.parseInt(hexStr,16)+"]");
        }
        Collections.reverse(billTemplateList);//反转
        String result=billTemplateList.toString().replace(", ", "");
        return result.substring(1,result.length()-1);
    }

    private String parseBillType(String billType){
        String billTypeResult=setting.getBillAcceptorCashAmountType();
        String[] tempArr=billType.split(",");
        printInfo("billType="+billType);
        for(int i=0;i<tempArr.length;i++){
            String tempStr="["+tempArr[i].trim()+"]";
            billTypeResult=billTypeResult.replace(tempStr, "1");
        }

        billTypeResult= billTypeResult.replaceAll("\\[.*?\\]", "0");
        printInfo("billTypeResult="+billTypeResult);
        return billTypeResult;
    }

    public int getReceivedMoney(String resultStr){
        int rcvdMoney=-1;
        if(!resultStr.equals("00")){
            int index=Integer.parseInt(resultStr);
            //[1][5][10][20][50][100]11
            String tempStr=reverseBillTemplate(setting.getBillAcceptorCashAmountType());
            List<String> result = getDataByRegex(tempStr,"(?<=\\[).*?(?=\\])");
            rcvdMoney=Integer.parseInt(result.get(index-1));
        }else{
            rcvdMoney=0;
        }
        return rcvdMoney;
    }

    private List<String> getDataByRegex(String targetStr, String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m =r.matcher(targetStr);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    private String reverseBillTemplate(String billTemplate){
        char[] arr=billTemplate.toCharArray();
        StringBuffer sb=new StringBuffer();
        for(int i=arr.length-1;i>=0;i--){
            String temp=String.valueOf(arr[i]);
            if(temp.equals("["))
                temp="]";
            else if(temp.equals("]"))
                temp="[";
            sb.append(temp);
        }
        return sb.toString();
    }

    private void printInfo(String infoStr){
        Log.i("Test", infoStr);
    }
}
