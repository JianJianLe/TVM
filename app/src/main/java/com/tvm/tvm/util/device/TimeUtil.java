package com.tvm.tvm.util.device;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeUtil {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Date returnDate=null;
    /*
     *gc.add(1,-1)表示年份减一.
     *gc.add(2,-1)表示月份减一.
     *gc.add(3.-1)表示周减一.
     *gc.add(5,-1)表示天减一
     */
    public static Date addTime(Date date,int field,int value){
        GregorianCalendar gc=new GregorianCalendar();
        gc.setTime(date);
        gc.add(field,value);
        return gc.getTime();
    }

    public static int compareDate(String strDate1,String strDate2){
        int flag=0;
        Date date1=getDateFromString(strDate1);
        Date date2=getDateFromString(strDate2);
        if(date1.getTime()>date2.getTime()){
            flag=1;//date1>date2
        }else if(date1.getTime()<date2.getTime()){
            flag=-1;//date1<date2
        }else{
            flag=0;//相等
        }
        return flag;
    }

    public static boolean isBetweenTime(Date date,String beginTime,String endTime){
        boolean flag=false;
        try{
            String strDate=dateFormat.format(date);
            // 截取当前时间时分秒
            int strDateH = Integer.parseInt(strDate.substring(11, 13));
            int strDateM = Integer.parseInt(strDate.substring(14, 16));
            int strDateS = Integer.parseInt(strDate.substring(17, 19));
            // 截取开始时间时分秒
            int strDateBeginH = Integer.parseInt(beginTime.substring(0, 2));
            int strDateBeginM = Integer.parseInt(beginTime.substring(3, 5));
            int strDateBeginS = Integer.parseInt(beginTime.substring(6, 8));
            // 截取结束时间时分秒
            int strDateEndH = Integer.parseInt(endTime.substring(0, 2));
            int strDateEndM = Integer.parseInt(endTime.substring(3, 5));
            int strDateEndS = Integer.parseInt(endTime.substring(6, 8));

            int nowData=strDateH * 3600 + strDateM*60 + strDateS;
            int beginData=strDateBeginH * 3600 + strDateBeginM*60 + strDateBeginS;
            int endData=strDateEndH * 3600 + strDateEndM*60 + strDateEndS;

            if(endData<beginData){
                endData=endData+24*60*60;
            }

            if(nowData >=beginData && nowData <=endData){
                flag=true;
            }else{
                flag=false;
            }
        }catch(Exception e){
            Log.e("Test", "TimeUtil error:" + e.getMessage());
        }
        return flag;
    }

    public static Date getDateFromString(String s){
        try {
            returnDate=dateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("Date:"+ returnDate, "" );
        return returnDate;
    }

    public static Date prase(String date) throws ParseException{
        if(date==null || date.equals("")){
            return null;
        }
        return dateFormat.parse(date);
    }
    public static String FormatDateStr(String dateStr){
        String str=null;
        str=dateFormat.format(getDateFromString(dateStr));
        return str;
    }

    public static boolean isWeekEnd(){
        boolean flag=false;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            flag=true;
        }
        return flag;
    }

    public static void delay(int second){
        try{
            Thread.currentThread();
            Thread.sleep(second);//毫秒
        }catch(Exception e){
            //System.out.println(e);
            Log.e("Test", e.getMessage());
        }
    }
}
