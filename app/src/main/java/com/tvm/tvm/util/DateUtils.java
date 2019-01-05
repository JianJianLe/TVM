package com.tvm.tvm.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换工具类
 */
public class DateUtils {

    private static String DATE_FORMAT = "yyyy-MM-dd";

    private static String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

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
            dateStr = dateStr + " 13:59:59";
        }
        Date date = new Date(dateStr);
        return date;
    }

    /**
     * 比较两个时间，返回大于等于0即开始时间大于等于结束时间
     * @param startDate
     * @param endDate
     * @return
     */
    public static int compare2Date(String startDate,String endDate){
        Date start = new Date(startDate);
        Date end = new Date(endDate);
        return end.compareTo(start);
    }

}
