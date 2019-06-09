package com.tvm.tvm.util;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.BillSetting;
import com.tvm.tvm.bean.dao.BillSettingDao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/2/27
 * - @Time： 20:10
 */
public class TxtUtils {

    /**
     * 读取文件
     * @param path
     * @param charset
     * @return
     */
    public static String readFile(String path, String charset){
        //设置默认编码
        if(charset == null){
            charset = "UTF-8";
        }
        File pathFile = new File(path);
        File[] fileList = pathFile.listFiles();
        for (File file:fileList){
            if(file.isFile() && file.exists() && !file.getName().startsWith("._")){
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charset);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuffer sb = new StringBuffer();
                    String text = null;
                    String ticketName = "";
                    String templateNum = "";
                    int i = 0;
                    while((text = bufferedReader.readLine()) != null){
                        if (i==0){
                            templateNum = text.split("[TemplateNumber]=")[0];
                        }else if (i==1){
                            ticketName = text.split("[TicketName]=")[0];
                        }else{
                            sb.append(text + "\n");
                        }
                        i++;
                    }

                    BillSettingDao billSettingDao = AppApplication.getApplication().getDaoSession().getBillSettingDao();
                    BillSetting billSetting = billSettingDao.queryBuilder().where(BillSettingDao.Properties.TemplateNum.eq(templateNum)).unique();
                    if (billSetting==null){
                        billSetting = new BillSetting();
                        billSetting.setTemplateNum(templateNum);
                        billSetting.setTicketName(ticketName);
                        billSetting.setTicketBody(sb.toString());
                        billSetting.setCreateDate(new Date());
                        billSettingDao.save(billSetting);
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }
        return null;
    }

    /**
     * 以FileWriter方式写入txt文件。
     * @param  path：文件路径
     * @param  content： 要写入的内容
     * @param  charset:要写入内容的编码方式
     */
    public static void writeToFile1(String path,String content ,String charset){

        try {
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file,false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close(); fw.close();
            System.out.println("test1 done!");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
