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

    //10进制转16进制，低位在前
    //decToHex(1000); This is E803
    public static String decToHex(int dec) {
        String hex = "";
        while(dec != 0) {
            String h = Integer.toString(dec & 0xff, 16);
            if((h.length() & 0x01) == 1)
                h = '0' + h;
            hex = hex + h;
            dec = dec >> 8;
        }
        return hex.toUpperCase();
    }

    //低位在前
    public static String getDecToHex(int dec, int count){
        String hexStr = DataUtils.decToHex(dec);
        while (hexStr.length()<count)
            hexStr += "0";
        return hexStr;
    }

    public static String xor(String content) {
        content = addSpace(content.replace(" ",""));
        String[] b = content.split(" ");
        int a = 0;
        for (int i = 0; i < b.length; i++) {
            a = a ^ Integer.parseInt(b[i], 16);
        }
        if (a < 10) {
            StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(a);
            return sb.toString();
        }
        return Integer.toHexString(a).toUpperCase();
    }

    public static String addSpace(String content) {
        String str = "";
        for (int i = 0; i < content.length(); i++) {
            if (i % 2 == 0) {
                str += " " + content.substring(i, i + 1);
            } else {
                str += content.substring(i, i + 1);
            }
        }
        return str.trim();
    }

    public static String convertStringToHex(String str){
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }
        return hex.toString();
    }

    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    public static String removeSpace(String content){
        return content.replace(" ","");
    }


    public static String setZeros(int count){
        return String.format("%"+count+"s", "0").replaceAll("\\s", "0");
    }

    public static String addZeros(String content, int count){
        return String.format("%"+count+"s", content).replaceAll("\\s", "0");
    }
}
