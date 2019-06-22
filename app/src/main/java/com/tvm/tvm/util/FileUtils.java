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
        String localKeyFilePath=FolderUtil.getDefaultHiddenTVMPath()+File.separator+".TVM_key.properties";
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

        
        String path = usbFolder + PreConfig.KEY_FILE_PATH;
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

    public static boolean copyTemplateFolder(String usbFolder) {
        String oldFolderPath = usbFolder + PreConfig.TEMPLATE_FOLDER;
        String newFolderPath = FolderUtil.getDefaultTemplatePath();

        File oldFolder = new File(oldFolderPath);
        File newFolder = new File(newFolderPath);

        if (oldFolder.isDirectory() && oldFolder.exists() && newFolder.list().length == 0) {
            copyFolder(oldFolderPath, newFolderPath);
            return true;
        }
        return false;
    }

    public static void createFolder(String folderPath){
        File folder=new File(folderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
    }

    public static void copyFolder(String resourceFolder, String targetFolder){
        try{
            File resfile=new File(resourceFolder);
            File tarfile=new File(targetFolder);
            if(!resfile.exists()){
                resfile.mkdirs();//创建文件夹
            }
            if(!tarfile.exists()){
                tarfile.mkdirs();//创建文件夹
            }
            File files[]=resfile.listFiles();//取出当前文件夹的所有文件
            for (File file : files) {
                if(file.isDirectory()){//判断是否为目录
                    file.getName();
                    copyFolder(file.getAbsolutePath(), targetFolder+"/"+file.getName());//如果是目录则递归复制
                }else {
                    FileInputStream fis=new FileInputStream(file);
                    File f=new File(targetFolder+"/"+file.getName());
                    f.createNewFile();//创建文件
                    FileOutputStream fos=new FileOutputStream(f);
                    int c=fis.available();//估算文件的长度
                    byte b[]=new byte[c];
                    fis.read(b);//将文件读取到b数组中
                    fos.write(b);//将b数组中的内容写入文件
                    fis.close();//关闭文件输入流
                    fos.close();//关闭文件输出流
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}