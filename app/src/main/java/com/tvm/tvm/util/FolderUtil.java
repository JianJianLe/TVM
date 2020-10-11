package com.tvm.tvm.util;

import android.os.Environment;
import android.util.Log;

import com.tvm.tvm.util.constant.PreConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FolderUtil {


    public static String USB_FOLDER="/mnt/usb_storage";
    public static String TEMPLATE_FOLDER="/TVM/Template/";
    public static String Project_FolderName = "TVM";
    public static String Image_FolderName = "Image";
    public static String Video_FolderName = "Video";
    public static String Template_FolderName = "Template";
    public static String TempTVM_FolderName = ".TVM";

    private String getUSBFolder(String folderName) {
        String path=null;
        String folderPath= USB_FOLDER + File.separator + Project_FolderName + File.separator + folderName;
        if(isFolderExisted(folderPath)){
            path = folderPath;
        }
        return path;
    }


    private String getSDCardFolder(String folderName) {
        String path = null;
        String extSDCardPath = getExtSDCardPath();

        if (extSDCardPath != null) {
            String folderPath = extSDCardPath + File.separator + Project_FolderName + File.separator + folderName;
            if (isFolderExisted(folderPath)) {
                path = folderPath;
            }
        }
        return path;
    }

    public static String getDefaultLogPath(){
        File file=Environment.getExternalStorageDirectory();
        String logPath=file.getAbsolutePath()+File.separator+Project_FolderName+File.separator+"Log";
        file=new File(logPath);
        if(!file.exists()){
            file.mkdirs();
        }
        return logPath;
    }


    //Use below to create the folder ".TVM"
    //Folder Name could be "Image", "Video", "Template", ".TVM"
    public static String getDefaultFolder(String folderName) {
        File file = Environment.getExternalStorageDirectory();
        String defaultFolderPath = file.getAbsolutePath() + File.separator + Project_FolderName + File.separator + folderName+ File.separator;
        if (!isFolderExisted(defaultFolderPath)) {
            file = new File(defaultFolderPath);
            file.mkdirs();
        }
        return defaultFolderPath;
    }

    //Folder Name: Image, Video, Template
    public static String getTargetFolderPath(String folderName){
        FolderUtil folderUtil=new FolderUtil();
        String folderPath=folderUtil.getUSBFolder(folderName);
        if(folderPath==null){
            folderPath=folderUtil.getSDCardFolder(folderName);
        }
        if(folderPath==null){
            folderPath=folderUtil.getDefaultFolder(folderName);
        }
        return folderPath;
    }

    private String getExtSDCardPath() {
        File sdDir = null;
        boolean isSDCardDirExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (isSDCardDirExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    public static boolean isFolderExisted(String folderStr) {
        File file = new File(folderStr);
        if (file.exists() && file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    public static List<String> getFolderFiles(String folderPath) {
        FolderUtil folderUtil = new FolderUtil();
        List<String> link = new ArrayList<>();
        File folder = new File(folderPath);
        if (folderUtil.isFileExisted(folder)) {
            String nameList[] = folder.list();
            for (int i = 0; i < nameList.length; ++i) {
                if(!nameList[i].startsWith(".")){
                    File f = new File(folderPath, nameList[i]);
                    link.add(f.getPath());
                }
            }
        }
        return link;
    }
    private boolean isFileExisted(File file) {
        boolean flag = false;
        if (file.exists() && file.isDirectory()) {
            String nameList[] = file.list();
            if (nameList!=null && nameList.length > 0) {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean copyTemplateFolder(String usbFolder) {
        String oldFolderPath = usbFolder + TEMPLATE_FOLDER;
        String newFolderPath = FolderUtil.getTargetFolderPath(FolderUtil.Template_FolderName);

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
