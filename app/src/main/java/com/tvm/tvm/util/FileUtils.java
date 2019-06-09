package com.tvm.tvm.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.constant.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class FileUtils {

	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/TVM/";

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
	
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
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
	
	 
	public static String readFileStr(String path){
		String fileStr=null;
		 try{  
	         FileReader file = new FileReader(path);  
	         BufferedReader br = new BufferedReader(file);  
	         String strLine = ""; 
	         String temp="";
	         while ((strLine = br.readLine()) != null){  
	              temp+=strLine;
	         }  
	         br.close();  
	         file.close();  
	         fileStr=temp;
	     } catch (Exception e) {  
	         Log.e("Test",e.getMessage());
	     }  
		return fileStr;
	}

	public static boolean readKeyFile(String usbFolder){
		String path= usbFolder + PreConfig.KEY_FILE_PATH;
		File file=new File(path);
		if(file.exists()){
			String temp=readFileStr(path);
			if(temp.indexOf(StringUtils.TVMKEY)>-1)
				return true;
		}else
			Log.i("Test","Cannot find the TVM_key file");
		return false;
	}
}
