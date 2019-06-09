package com.tvm.tvm.util;

import android.os.Environment;
import android.util.Log;

import com.tvm.tvm.util.constant.PreConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderUtil {

    public static String getTempFolder() {
        File file = Environment.getExternalStorageDirectory();
        String tempFolderPath = file.getAbsolutePath() + File.separator + "TVM" + File.separator + "Temp";
        file = new File(tempFolderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return tempFolderPath;
    }

    public static String getImagePath() {
        FolderUtil folderUtil = new FolderUtil();
        String usbPath = folderUtil.getUSBfolder();
        String imagePath = folderUtil.getTargetImagePath(usbPath);

        if (imagePath == null) {
            String sdcardPath = folderUtil.getSDCardfolder();
            imagePath = folderUtil.getTargetImagePath(sdcardPath);
        }
        if (imagePath == null) {
            imagePath = folderUtil.getDefaultImagePath();
        }
        return imagePath;
    }

    public static String getVideoPath() {
        FolderUtil folderUtil = new FolderUtil();
        String usbPath = folderUtil.getUSBfolder();
        String videoPath = folderUtil.getTargetVideoPath(usbPath);

        if (videoPath == null) {
            String sdcardPath = folderUtil.getSDCardfolder();
            videoPath = folderUtil.getTargetVideoPath(sdcardPath);
        }
        if (videoPath == null) {
            videoPath = folderUtil.getDefaultVideoPath();
        }
        return videoPath;
    }

    public static String getTicketTemplatePath() {
        FolderUtil folderUtil = new FolderUtil();
        String usbPath = folderUtil.getUSBfolder();
        String templateTath = folderUtil.getTargetTemplatePath(usbPath);

        if (templateTath == null) {
            String sdcardPath = folderUtil.getSDCardfolder();
            templateTath = folderUtil.getTargetTemplatePath(sdcardPath);
        }
        if (templateTath == null) {
            templateTath = folderUtil.getDefaultTemplatePath();
        }
        Log.i("Test", templateTath);
        File file = new File(templateTath);
        Log.i("Test", "number=" + file.list().length);
        return templateTath;
    }

    public static List<String> getFolderFiles(String path) {
        FolderUtil folderUtil = new FolderUtil();
        List<String> link = new ArrayList<String>();
        File files = new File(path);
        if (folderUtil.isFileExisted(files)) {
            String name[] = files.list();
            for (int i = 0; i < name.length; ++i) {
                File f = new File(path, name[i]);
                link.add(f.getPath());
            }
        }
        return link;
    }

    private String getTargetImagePath(String folder) {
        String targetImagePath = null;
        if (folder != null) {
            String tempPath = folder + File.separator + "Image";
            if (isFolderExisted(tempPath)) {
                if (isFileExisted(new File(tempPath))) {
                    targetImagePath = tempPath;
                }
            }
        }
        return targetImagePath;
    }

    private String getTargetVideoPath(String folder) {
        String targetVideoPath = null;
        if (folder != null) {
            String tempPath = folder + File.separator + "Video";
            if (isFolderExisted(tempPath)) {
                if (isFileExisted(new File(tempPath))) {
                    targetVideoPath = tempPath;
                }
            }
        }
        return targetVideoPath;
    }

    private String getTargetTemplatePath(String folder) {
        String targetTemplatePath = null;
        if (folder != null) {
            String tempPath = folder + File.separator + "Template";
            if (isFolderExisted(tempPath)) {
                if (isFileExisted(new File(tempPath))) {
                    targetTemplatePath = tempPath;
                }
            }
        }
        return targetTemplatePath;
    }

    private String getDefaultVideoPath() {
        File file = Environment.getExternalStorageDirectory();
        String VideoPath = file.getAbsolutePath() + File.separator + "TVM" + File.separator + "Video";
        file = new File(VideoPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return VideoPath;
    }

    private String getDefaultImagePath() {
        File file = Environment.getExternalStorageDirectory();
        String ImagePath = file.getAbsolutePath() + File.separator + "TVM" + File.separator + "Image";
        file = new File(ImagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return ImagePath;
    }

    public static String getDefaultTemplatePath() {
        File file = Environment.getExternalStorageDirectory();
        String templatePath = file.getAbsolutePath() + File.separator + "TVM" + File.separator + "Template";
        file = new File(templatePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return templatePath;
    }

    private boolean isFileExisted(File files) {
        boolean flag = false;
        if (files.exists() && files.isDirectory()) {
            String name[] = files.list();
            if (name.length > 0) {
                flag = true;
            }
        }

        return flag;
    }

    private String getUSBfolder() {
        return PreConfig.USB_FOLDER + File.separator + "TVM";
    }

    private String getExtSdcardPath() {
        //return System.getenv("SECONDARY_STORAGE");
        String folder = getSDPath();
        return folder;
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    private String getSDCardfolder() {
        String path = null;
        String extSdcardPath = getExtSdcardPath();

        if (extSdcardPath != null) {
            String tempPath = extSdcardPath + File.separator + "TVM";
            if (isFolderExisted(tempPath)) {
                path = tempPath;
            }
        }
        return path;
    }

    private boolean isFolderExisted(String folderStr) {
        boolean flag = false;
        File folder = new File(folderStr);
        if (folder.exists()) {
            flag = true;
        } else {
            flag = false;
        }
        folder = null;
        return flag;
    }


//	public static void createDefaultJJLFolder(){
//		File file=Environment.getExternalStorageDirectory();
//		String filePath=file.getAbsolutePath()+File.separator+"JJL";
//		file=new File(filePath);
//		if(!file.exists()){
//			file.mkdirs();
//		}
//	}

//	public static String getDefaultJJLFolderPath(){
//		File file=Environment.getExternalStorageDirectory();
//		String folderpath=file.getAbsolutePath()+File.separator+"JJL";
//		file=new File(folderpath);
//		if(!file.exists()){
//			file.mkdirs();
//		}
//		return folderpath;
//	}

//	public static String getDefaultJJLapkPath(){
//		return getDefaultImagePath()+File.separator+"apk";
//	}

}
