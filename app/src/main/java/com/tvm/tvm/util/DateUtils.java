package com.tvm.tvm.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换工具类
 */
public class DateUtils {

    private static String DATE_FORMAT = "yyyy-MM-dd";

    private static String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

    static DateFormat df = new SimpleDateFormat(DATE_FORMAT);

    static DateFormat df1 = new SimpleDateFormat(TIME_FORMAT);

    /**
     * 日期转换日期字符串
     * @param date
     * @return
     */
    public static String formatDate(Date date){
        String dateStr = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateStr = dateFormat.format(date);
        return dateStr;
    }

    /**
     * 获取开始或者结束时间
     * @param dateStr
     * @param isStart
     * @return
     */
    public static Date formatDate(String dateStr,int isStart){
        if (isStart==0){
            dateStr = dateStr + " 00:00:00";
        }else {
            dateStr = dateStr + " 23:59:59";
        }
        Date date = null;
        try {
            date = df1.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 日期转换 for yyyy-MM-dd hh:mm:ss
     * @param dateStr
     * @return
     */
    public static Date formatDate(String dateStr){
        Date date = null;
        try {
            date = df1.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取开始或者结束时间
     * @param dateStr
     * @param isStart
     * @return
     */
    public static String formatDateStr(String dateStr,int isStart){
        if (isStart==0){
            dateStr = dateStr + " 00:00:00";
        }else {
            dateStr = dateStr + " 23:59:59";
        }
        return dateStr;
    }

    /**
     * 比较两个时间，返回大于等于0即开始时间大于等于结束时间
     * @param startDate
     * @param endDate
     * @return
     */
    public static boolean compare2Date(String startDate,String endDate){
        try {
            Date start = df1.parse(startDate);
            Date end = df1.parse(endDate);
            return start.before(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

}
