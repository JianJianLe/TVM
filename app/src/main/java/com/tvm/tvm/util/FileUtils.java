package com.tvm.tvm.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;


public class FileUtils {

    public static String SDPATH = Environment.getExternalStorageDirectory()
            + "/TVM/";

    public static String KEY_FILE_PATH="/TVM/TVM_key.properties";

    public static String LOCAL_KEY_FILE=".tvm.backup";

    //private static String localKeyFilePath = Environment.getExternalStorageDirectory()+ "/TVM_key.properties";

    public static void saveBitmap(Bitmap bm, String picName) {
        try {
            if (!isFileExist("")) {
                File tempf = createSDDir("");
            }
            File f = new File(SDPATH, picName + ".JPEG");
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(SDPATH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            //System.out.println("createSDDir:" + dir.getAbsolutePath());
            //System.out.println("createSDDir:" + dir.mkdir());
        }
        return dir;
    }


    public static boolean isFileExist(String fileName) {
        File file = new File(SDPATH + fileName);
        file.isFile();
        return file.exists();
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(SDPATH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }


    public static String readFileStr(String path) {
        String fileStr = null;
        try {
            FileReader file = new FileReader(path);
            BufferedReader br = new BufferedReader(file);
            String strLine = "";
            String temp = "";
            while ((strLine = br.readLine()) != null) {
                temp += strLine;
            }
            br.close();
            file.close();
            fileStr = temp;
        } catch (Exception e) {
            Log.e("Test", e.getMessage());
        }
        return fileStr;
    }

    public static boolean readKeyFile(String usbFolder) {
        //.TVM folder
        String localKeyFilePath=FolderUtil.getDefaultFolder(FolderUtil.TempTVM_FolderName)+File.separator+LOCAL_KEY_FILE;
        try {
            File localFile=new File(localKeyFilePath);
            if(localFile.exists()){
                String temp = readFileStr(localKeyFilePath);
                if (temp.contains(StringUtils.TVMKEY)){
                    return true;
                }
            }
        }catch (Exception e){
            Log.e("Test",e.getMessage());
        }

        
        String path = usbFolder + KEY_FILE_PATH;
        File file = new File(path);
        if (file.exists()) {
            String temp = readFileStr(path);
            if (temp.contains(StringUtils.TVMKEY)){
                copyFile(file,new File(localKeyFilePath));
                return true;
            }
        } else
            Log.i("Test", "Cannot find the TVM_key file in USB folder");

        return false;
    }

    public static void copyFile(File source, File dest){
        if(dest.exists())
            deleteFile(dest.getParent());
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            inputChannel.close();
            outputChannel.close();
        }catch (IOException e){
            Log.e("Test", e.getMessage());
        }
    }

}