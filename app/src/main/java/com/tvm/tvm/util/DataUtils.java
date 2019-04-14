package com.tvm.tvm.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtils {

    public static byte[] hexToByteArray(String hexStr){
        int hexlen = hexStr.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            hexStr="0"+hexStr;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(hexStr.substring(i,i+2));
            j++;
        }
        return result;
    }

    public static byte hexToByte(String hexStr){
        return (byte)Integer.parseInt(hexStr,16);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }


    public static String byteToHex(byte b){
        String hex = Integer.toHexString(b & 0xFF);
        if(hex.length() < 2){
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }

    public static String getCmdData(String cmdStr){
        String partern="AA.*DD";
        Pattern r=Pattern.compile(partern);
        Matcher m=r.matcher(cmdStr);
        if(m.find())
            return m.group(0);
        else
            return null;
    }
}
