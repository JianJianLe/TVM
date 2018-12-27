package com.tvm.tvm.util;

import android.os.Environment;
import android.util.Log;

import com.tvm.tvm.util.constant.PreConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderUtil {
  
	public static String getImagePath(){
		String imagePath=null; 
		//----------
		String usbPath= getUSBfolder();
		imagePath=getTargetImagePath(usbPath);
		
		//-----------
		if(imagePath==null){
			String sdcardPath= getSDCardfolder();
			imagePath=getTargetImagePath(sdcardPath);
		}
		
		//-----------
		if(imagePath==null){
			imagePath=getDefaultImagePath();
		}
		return imagePath;
	}
	
	public static String getTargetImagePath(String folder){
		String targetImagePath=null;
		if(folder!=null){
			String tempPath=folder +File.separator+ "Image";
			if(isFolderExisted(tempPath)){ 
				if(isFileExisted(new File(tempPath))){
					targetImagePath=tempPath;
				}
			}
		}
		return targetImagePath;
	}
	
	public static String getVideoPath(){
		String videoPath=null;
		
		//----
		String usbPath= getUSBfolder();
		videoPath=getTargetVideoPath(usbPath);
		
		//-----
		if(videoPath==null){
			String sdcardPath= getSDCardfolder();
			videoPath=getTargetVideoPath(sdcardPath);
		}
		//----
		if(videoPath==null){
			videoPath=getDefaultVideoPath();
		}
		return videoPath;
	}
	
	public static String getTargetVideoPath(String folder){
		String targetVideoPath=null;
		if(folder!=null){
			String tempPath=folder +File.separator+ "Video";
			if(isFolderExisted(tempPath)){ 
				if(isFileExisted(new File(tempPath))){
					targetVideoPath=tempPath;
				}
			}
		}
		return targetVideoPath;
	}
	 
	
	public static List<String> getFolderFiles(String path){
		List<String> link=new ArrayList<String>(); 
		File files=new File(path);
		if(isFileExisted(files))
		{ 
			String name[]=files.list();
			for(int i=0;i<name.length;++i){
				File f=new File(path,name[i]);
				link.add(f.getPath());
			} 
		}
		return link;
	}
	
	public static boolean isFileExisted(File files){
		boolean flag=false; 
		if(files.exists() && files.isDirectory())
		{
			String name[]=files.list();
			if(name.length>0){
				flag=true;
			}
		}
		
		return flag;
	}
	
	public static String getUSBfolder(){
		return PreConfig.USB_FOLDER+File.separator+"TVM";
	}
	 
	
	public static String getExtSdcardPath(){  
		return System.getenv("SECONDARY_STORAGE");
	}
	
	public static boolean isFolderExisted(String folderStr){
		boolean flag=false;
		File folder=new File(folderStr);
		if(folder.exists()){
			flag=true;
		}else{
			flag=false;
		}
		folder=null;
		return flag;
	}
	
	public static String getSDCardfolder(){
		String path=null;
		String extSdcardPath=getExtSdcardPath();
		
		if(extSdcardPath!=null){
			String tempPath=extSdcardPath +File.separator+ "TVM";
			if(isFolderExisted(tempPath)){
				path=tempPath;
			}
		}
		return path;
	}
	
	

	public static void createDefaultJJLFolder(){ 
		File file=Environment.getExternalStorageDirectory();
		String filePath=file.getAbsolutePath()+File.separator+"JJL";
		file=new File(filePath);
		if(!file.exists()){
			file.mkdirs();
		}
	}
	
	public static String getDefaultJJLFolderPath(){ 
		File file=Environment.getExternalStorageDirectory();
		String folderpath=file.getAbsolutePath()+File.separator+"JJL";
		file=new File(folderpath);
		if(!file.exists()){
			file.mkdirs();
		}
		return folderpath;
	}
	
	public static String getDefaultJJLapkPath(){
		return getDefaultImagePath()+File.separator+"apk";
	}
	
	public static String getDefaultVideoPath(){ 
		File file=Environment.getExternalStorageDirectory();
		String VideoPath=file.getAbsolutePath()+File.separator+"TVM"+File.separator+"Video";
		file=new File(VideoPath);
		if(!file.exists()){
			file.mkdirs();
		}
		return VideoPath;
	}
	
	public static String getDefaultImagePath(){ 
		File file=Environment.getExternalStorageDirectory();
		String ImagePath=file.getAbsolutePath()+File.separator+"JJL"+File.separator+"Image";
		file=new File(ImagePath);
		if(!file.exists()){
			file.mkdirs();
		}
		return ImagePath;
	}
	
	public static void clearJJLapkFile(){
		File apk=new File(getDefaultJJLapkPath()+File.separator+"JJL.apk");
		if(apk.exists()){
			apk.delete();
		}
	}
	
	
	

	public static void readFolder(String path){
		File folder=new File(path);
		String name[]=folder.list();
		for(int i=0;i<name.length;i++){
			Log.d("Test",name[i]);
		}
	}
	 
	
//	public static boolean readJJLKeyFile(){
//		boolean flag=false;
//		String path="/mnt/usb_storage/JJL/JJL_key.properties";///mnt/usb_storage
//		File file=new File(path);
//		if(file.exists()){
//			String temp=FileUtils.readFileStr(path);
//			if(temp.indexOf(APPConfig.keyStr)>-1){
//				flag=true;
//			}
//		}else{
//			Log.d("Test","no file");
//		}
//
//		return flag;
//	}
	 
}
