package com.tvm.tvm.util.device.paydevice;

import android.util.Log;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.util.DataUtils;
import com.tvm.tvm.util.FolderUtil;
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.device.SerialPortUtil;
import com.tvm.tvm.util.device.printer.PrinterAction;
import com.tvm.tvm.util.device.printer.PrinterCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayDeviceUtil {
    public String QRData=null;
    public String activityRecord=null;
    public boolean paySuccess=false;//判断是否在支付界面支付成功
    public String strUniquePayCode=null;

    private String receivedCMD;
    private int payAmount;
    private String strRandomHex;
    private boolean randomHexFlag=true;// One QR code command, one RandomHexStr.
    private boolean hasServerStatus=false;

    private Setting setting;
    private SettingDao settingDao;
    private List<Price> priceList;
    private PriceDao priceDao;
    private SerialPortUtil serialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    private static PayDeviceUtil instance;
    public synchronized static PayDeviceUtil getInstance(){
        if(instance==null)
            instance=new PayDeviceUtil();
        return instance;
    }

    public void initPayDevice() {
        initDB();

        printInfo("Init PayDevice");
        String serialPortFilePath1="/dev/ttyS0";
        String serialPortFilePath2="/dev/ttyGS0";
        File serialPortFile = new File(serialPortFilePath1);
        if(!serialPortFile.exists()){
            serialPortFile = new File(serialPortFilePath2);
        }
        try {
            serialPort = new SerialPortUtil(serialPortFile,38400);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mOutputStream = (FileOutputStream) serialPort.getOutputStream();
        mInputStream = (FileInputStream) serialPort.getInputStream();

        if(mReadThread==null || !mReadThread.isAlive()){
            mReadThread = new ReadThread();
            mReadThread.start();
        }
    }

    private void initDB(){
        settingDao = AppApplication.getApplication().getDaoSession().getSettingDao();
        setting = settingDao.queryBuilder().where(SettingDao.Properties.Id.eq(1)).unique();
        priceDao = AppApplication.getApplication().getDaoSession().getPriceDao();
        priceList = AppApplication.getApplication().getDaoSession().getPriceDao().queryBuilder().where(PriceDao.Properties.IsDelete.eq(0)).list();
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    receivedCMD="";
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[1024];
                    int size = mInputStream.read(buffer);
                    if (size > 0)
                        onDataReceived(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //For test
    private void showData(final byte[] buffer){
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<buffer.length;i++){
            stringBuffer.append(buffer[i]);
            stringBuffer.append(",");
        }
        Log.i("Test","original CMD="+stringBuffer.toString());
    }

    private void onDataReceived(final byte[] buffer) {
        //showData(buffer);
        String cmdStr=DataUtils.bytesToHex(buffer);
        if(cmdStr.startsWith("AA")){
            receivedCMD=getAllCMD(cmdStr);
            if(receivedCMD==null){
                printInfo("receivedCMD = null, Server CMD:"+cmdStr);
                return;
            }
            printInfo("Server CMD:"+receivedCMD);

            //售票机发送获取二维码支付链接指令（子命令 0x0A）
            if(hasQRCode()){
                //Log.i("Test","hasQRCode=true");
                String strQRCodeData=getQRCodeData();
                //Log.i("Test","Before strQRCodeData="+strQRCodeData);
                Log.i("Test","PayDeviceID="+setting.getPayDeviceID());
                if(strQRCodeData!=null && setting.getPayDeviceID()!=null)
                    QRData = strQRCodeData.replaceAll("(?<=topay/).*(?=/)", setting.getPayDeviceID());
                //Log.i("Test","After strQRCodeData="+QRData);
            }
            else if(checkXOR()){

                Log.i("Test","checkXOR=true");
                //查询链接CMD01
                if(hasServerQuery()){
                    cmd_ReplySever();
                    thread_QueryServerStatus();
                }

                //查询支付盒子网络状态
                if(hasConnectedServer()){
                    Log.i("Test", "Receieved Server Status:" + receivedCMD);
                    hasServerStatus=true;
                    cmd_UploadParams();
                }

                //修改本地通道信息
                if(hasReceivedUpdateParamCMD()){
                    updateClientPrice();
                    cmd_ReplyUpdateParamCompleted();
                }

                //支付盒子发送获取设备状态指令（子命令 0x01)
                if(hasQueryClientStatus())
                    cmd_ReplyClientStatus();

                //售票机超时未收到支付结果，发送上报上分结果指令（子命令 0x04）
                if(hasReplyDrawBack()){
                    printInfo("Draw back pay code:" + getDrawBackPayCode());
                    if(strUniquePayCode!=null && strUniquePayCode.equals(getDrawBackPayCode()))
                        printInfo("退款成功！");
                }

                //支付盒子收款成功发送同步支付结果指令（子命令 0x03）
                if(hasPayResult()){
                    if(checkPayResult()){
                        cmd_ReplyPayMatch();
                        printInfo("activityRecord="+activityRecord);
                        if(activityRecord.equals("PayDetailActivity"))
                            paySuccess=true;
                        else{
                            printTicketDirectly();
                        }
                    }
                    else
                        cmd_ReplyPayMismatch();
                }

                //售票机出票成功后发送上报交易结果（子命令 0x09）
                if(hasReplyPrintSuccess())
                    printInfo("支付盒子应答出票成功");

                //售票机收到现金上报交易结果（线下支付）（子命令 0x06）
                if(hasReplyCashReport())
                    printInfo("支付盒子应答现金上报");
            }else {
                Log.i("Test","checkXOR=false");
            }
        }else{
            printInfo("Invalid Server CMD:"+cmdStr);
        }
    }

    private void printTicketDirectly(){
        printInfo("Print Ticket Directly");
        PrinterCase.getInstance().normalTicket.setPayType("线上支付");
        PrinterAction printerAction=new PrinterAction();
        printerAction.PrintTicket();
        PrinterCase.getInstance().balanceRecord=0;
        PrinterCase.getInstance().amountRecord=0;
        paySuccess=false;
    }

    //======== Server Query Start ========
    //查询链接CMD01
    //支付盒子发送：AA 0B 01 01 8byte的支付盒子码 Check DD
    //售票机应答：AA 05 02 01 7C 08 Check DD  (登录标识087CH=2172,发送低位在前)
    private boolean hasServerQuery(){
        return compareCMD(receivedCMD,"AA0B0101.*DD");
    }

    private void cmd_ReplySever(){
        Log.i("Test","hasServerQuery receivedCMD="+receivedCMD);
        setting.setPayDeviceID(getPayDeviceNO());
        settingDao.update(setting);
        printInfo("Reply Server:AA0502017C0872DD");
        write("AA0502017C0872DD");
    }

    private String getPayDeviceNO(){
        String deviceNo=getCMDDataByRegex(receivedCMD,"(?<=AA0B0101).*(?=..DD)");//0000000091203832
        return deviceNo.replaceAll("^(0+)", "");//91203832
    }
    //======== Server Query End ========


    //======== Query Server Status Start ========
    //2.查询支付盒子网络状态-详见3.8.2指令说明
    //售货机主板 -------------> 支付盒子
    //发送：AA 03 02 19 18 DD
    //支付盒子 -------------> 售货机主板
    //发送：AA 05 01 19 31 00 2C DD
    private void thread_QueryServerStatus(){
        hasServerStatus=false;
        new Thread(){
            public void run() {
                while (!isInterrupted()){
                    if(hasServerStatus)
                        break;
                    TimeUtil.delay(200);
                    cmd_QuerySeverStatus();
                    TimeUtil.delay(800);
                }
            }
        }.start();
    }

    private void cmd_QuerySeverStatus(){
        printInfo("Query Server Status");
        write("AA03021918DD");
    }

    private boolean hasConnectedServer(){
        return compareCMD(receivedCMD,"AA050119..00..DD");
    }

    //======== Query Server Status End ========

    //======== Upload Param Start ========
    //上报本地通道信息
    //售货机-->支付盒子
    //成人票：AA 1B 02 C9 02 03 00 03 00 D0 07 00 00 00 00 00 00 64 00 C8 00 06 B3 C9 C8 CB C6 B1 A1 DD
    // AA XX 02 C9 [Data] Check DD
    //子命令 02
    //通道总数 [2]
    //通道序号 [2]
    //购票价格 [4]
    //游戏价格 [4]
    //通道货品数量 [2]
    //通道货品容量 [2]
    //通道名称字段长度 [1]
    //通道名称 [n]
    private void cmd_UploadParams(){
        thread_CheckUploadParams();
    }

    private void thread_CheckUploadParams(){
        new Thread(){
            public void run() {
                Price price=null;
                int totalChannel=priceList.size();
                for(int i=0;i<priceList.size();i++){
                    price = priceList.get(i);
                    int currentChannel=i+1;
                    String hex_ChannelName = getHex_TicketName(price.getTitle());
                    String hex_ChannelNameLength = DataUtils.decToHex(hex_ChannelName.length()/2);
                    //上报本地通道信息数据
                    String dataStr = "02C902" +
                            decToHex(totalChannel,4) + //通道总数
                            decToHex(currentChannel,4) + //通道序号
                            decToHex((int)price.getPrice() * 100,8) +//购票价格
                            DataUtils.setZeros( 8) +//游戏价格
                            decToHex(1000,4) +//通道货品数量
                            decToHex(1000,4) +//通道货品容量
                            hex_ChannelNameLength +//通道名称字段长度
                            hex_ChannelName;//通道名称
                    //Length
                    String lengthStr = DataUtils.decToHex((dataStr.length()+2)/2);
                    dataStr = lengthStr + dataStr;
                    //Send CMD
                    String cmdStr =  "AA"  + addEndCMD(dataStr);
                    Log.i("Test", price.getTitle() + " cmd_UploadParams = " + cmdStr);
                    write(cmdStr);
                    TimeUtil.delay(500);
                }
            }
        }.start();
    }

    private String decToHex(int number, int count){
        return DataUtils.addRightZeros(DataUtils.decToHex(number),count);
    }

    private String getHex_TicketName(String name){
        return DataUtils.bytesToHex(DataUtils.convertStringToBytes(name));
    }

    //支付盒子应答上报本地通道信息
    //支付盒子-->售货机
    //AA 08 01 C9 02 03 00 03 00 C2 DD
    private boolean hasRepliedUploadParam(){
        return compareCMD(receivedCMD,"AA0801C902.*DD");
    }
    //======== Upload Param End ========


    //======== Update Client Param Start ========
    //修改本地通道信息
    //AA1B01C90800000100B004000064000000E803E80306B6F9CDAFC6B156DD
    private boolean hasReceivedUpdateParamCMD(){
        return compareCMD(receivedCMD,"AA..01C908.*DD");
    }

    private void updateClientPrice(){
        int priceData = DataUtils.hexToDec(receivedCMD.substring(18,26))/100;
        int index = DataUtils.hexToDec(receivedCMD.substring(14,18));
        Price price=priceList.get(index-1);
        price.setPrice(Double.valueOf(priceData));
        priceDao.update(price);
    }

    private void cmd_ReplyUpdateParamCompleted(){
        String cmdStr="0802C908"+decToHex(priceList.size(),4)+receivedCMD.substring(14,18);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }
    //======== Update Client Param End ========



    //======== QR Code Start ========
    //售票机发送获取二维码支付链接指令（子命令 0x0A）
    //AA 0E 02 C9 0A  [6byte随机数]  [4byte支付金额] Check DD
    //支付盒子应答：AA XX 01 C9 0A data[n] Check DD （data[n]为支付二维码链接数据）
    public void init_QRCode(){
        randomHexFlag=true;
        strRandomHex=null;
        QRData=null;
    }

    private boolean hasQRCode(){
        return compareCMD(receivedCMD,"AA..01C90A.*DD");
    }

    public void cmd_GetQRCode(int amount){
        payAmount=amount;
        printInfo("Amount = "+amount+", Send CMD to get QR code.");

        //重发的数据帧中的[6byte随机数]不需要变化
        //randomHexFlag=false时，不创建新的随机数
        if(randomHexFlag){
            strRandomHex = getRandomHex(6);
            randomHexFlag=false;
        }

        String cmdStr="0E02C90A"+ strRandomHex +getAmountHex(amount);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    private String getRandomHex(int count){
        return DataUtils.bytesToHex(getRandomByte(count));
    }

    private byte[] getRandomByte(int count){
        byte[] randomBytes=new byte[count];
        Random random=new Random();
        random.nextBytes(randomBytes);
        return randomBytes;
    }

    private String getAmountHex(int amount){
        String hexStr = DataUtils.decToHex(amount);
        while (hexStr.length()<8)
            hexStr += "0";
        return hexStr;
    }

    private String getQRCodeData(){
        String hexStr= getCMDDataByRegex(receivedCMD,"(?<=AA..01C90A).*(?=..DD)");
        printInfo(hexStr);
        return DataUtils.convertHexToString(hexStr);
    }
    //======== QR Code End ========



    //======== Server Query Client Status Start ========
    //支付盒子发送获取设备状态指令（子命令 0x01）
    //AA 0C 01 C9 01  [6byte支付唯一码]  00 00 Check DD
    //售票机应答：AA 0C 02 C9 01  [6byte支付唯一码]  01 00 Check DD
    private boolean hasQueryClientStatus(){
        return compareCMD(receivedCMD,"AA0C01C901.*DD");
    }

    private String getUniquePayCode(){
        return getCMDDataByRegex(receivedCMD,"(?<=AA0C01C901).*(?=0000..DD)");
    }

    private void cmd_ReplyClientStatus(){
        strUniquePayCode=getUniquePayCode();
        printInfo("Unique Pay Code:"+ strUniquePayCode);
        String cmdStr="0C02C901"+strUniquePayCode+"0100";
        cmdStr = "AA" + addEndCMD(cmdStr);
        printInfo("Reply Client Status:" + cmdStr);
        write(cmdStr);
    }
    //======== Server Query Client Status End ========


    //======== Draw back Start ========
    //售票机超时未收到支付结果，发送上报上分结果指令（子命令 0x04）
    //AA 0E 02 C9 04  6byte支付唯一码  00 00 00 00 Check DD
    //支付盒子退款后应答：AA 0E 01 C9 04  [6byte支付唯一码]  00 00 00 00 Check DD
    public void cmd_DrawBack(){
        printInfo("超时退款");
        String cmdStr="0E02C904"+strUniquePayCode+"00000000";
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    public void cmd_DrawBack_Test(){
        if(strUniquePayCode!=null){
            String cmdStr = "0E02C904" + strUniquePayCode + "00000000";
            cmdStr = "AA" + addEndCMD(cmdStr);
            write(cmdStr);
        }
    }

    private boolean hasReplyDrawBack(){
        return compareCMD(receivedCMD,"AA0E01C904.*DD");
    }

    private String getDrawBackPayCode(){
        return getCMDDataByRegex(receivedCMD,"(?<=AA0E01C904).*(?=00000000..DD)");
    }
    //======== Draw back End ========


    //======== Pay result Start ========
    //支付盒子收款成功发送同步支付结果指令（子命令 0x03）
    //AA 29 01 C9 03  [6byte支付唯一码]  [4byte支付金额]  [用户ID] Check DD
    //售票机匹配正确则应答：AA 0E 02 C9 03  [6byte支付唯一码]  [4byte支付金额]   Check DD
    //售票机匹配错误则应答：AA 0E 02 C9 03  [6byte支付唯一码]  00 00 00 00   Check DD
    //匹配正确可打印票据，如果匹配错误则不打印票据
    private boolean hasPayResult(){
        return compareCMD(receivedCMD,"AA..01C903.*DD");
    }

    private String getPayResultUniqueCode(){
        return getCMDDataByRegex(receivedCMD,"(?<=AA..01C903).{12}");
    }

    private String getPayResultAmount(){
        return getCMDDataByRegex(receivedCMD,"(?<=AA..01C903.{12}).{8}");
    }

    private boolean checkPayResult(){
        return strUniquePayCode.equals(getPayResultUniqueCode()) &&
                getAmountHex(payAmount).equals(getPayResultAmount());
    }

    private void cmd_ReplyPayMatch(){
        printInfo("支付成功，售票机匹配正确应答");
        String cmdStr="0E02C903"+strUniquePayCode+getAmountHex(payAmount);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    private void cmd_ReplyPayMismatch(){
        printInfo("支付成功，售票机匹配错误，发送退款指令");
        String cmdStr="0E02C903"+strUniquePayCode+"00000000";
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }
    //======== Pay result End ========


    //======== Print Success Record Start ========
    //售票机出票成功后发送上报交易结果（子命令 0x09）
    //AA 26 02 C9 09  [6byte支付唯一码]  [4byte支付金额]  [data11-34均为00] Check DD
    //支付盒子应答：AA 0A 01 C9 09  [6byte支付唯一码] Check DD
    public void cmd_ReportPrintSuccess(){
        printInfo("售票机出票成功，发送上报交易结果");
        String cmdStr="2602C909"+strUniquePayCode+getAmountHex(payAmount)+DataUtils.setZeros(48);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    private boolean hasReplyPrintSuccess(){
        return compareCMD(receivedCMD,"AA0A01C909.*DD");
    }
    //======== Print Success Record End ========


    //======== Report Cash Result Start ========
    //售票机收到现金上报交易结果（线下支付）（子命令 0x06）
    //AA 24 02 C9 06  [6byte随机数]  [4byte支付金额]  [data11-32] Check DD
    //支付盒子应答：AA 0A 01 C9 06  [6byte随机数] Check DD
    public void cmd_CashReport(int amount){
        printInfo("售票机收到现金上报：" + amount);
        String cmdStr="2402C906"+getRandomHex(6)+getAmountHex(amount)+DataUtils.setZeros(44);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    public void cmd_ReportCashPrintSuccess(int amount){
        printInfo("现金支付，售票机出票成功，发送上报交易结果");
        String cmdStr="2602C909"+getRandomHex(6)+getAmountHex(amount)+DataUtils.setZeros(48);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    private boolean hasReplyCashReport(){
        return compareCMD(receivedCMD,"AA0A01C906.*DD");
    }
    //======== Report Cash Result End ========


    //###########################
    //Common Function -- Start
    //###########################
    private String addEndCMD(String cmdStr){
        return DataUtils.removeSpace(cmdStr) + DataUtils.xor(cmdStr) + "DD";
    }

    private void write(String cmdStr) {
        try {
            printInfo( "Client Write CMD: "+cmdStr);
            byte[] sendData = DataUtils.hexToByteArray(cmdStr);
            mOutputStream.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getAllCMD(String cmdStr){
        String pattern="AA.*DD";
        Pattern r=Pattern.compile(pattern);
        Matcher m=r.matcher(cmdStr);
        if(m.find())
            return m.group(0);
        else
            return null;
    }

    private String getCMDDataByRegex(String content,String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher((content));
        if(m.find())
            return m.group(0);
        else
            return null;
    }

    private boolean compareCMD(String cmdStr, String pattern){
        Pattern r = Pattern.compile(pattern);
        Matcher m =r.matcher(cmdStr);
        return m.find();
    }

    private boolean checkXOR(){
        String hexStr = getCMDDataByRegex(receivedCMD,"(?<=AA).*(?=DD)");

        Log.i("Test", "hexStr=" + hexStr);

        String result = hexStr.substring(hexStr.length()-2,hexStr.length());
        String cmdStr = hexStr.substring(0,hexStr.length()-2);

        Log.i("Test", "cmdStr=" + cmdStr);

        String xorStr = DataUtils.xor(cmdStr);

        Log.i("Test","result="+result);
        Log.i("Test","xorStr="+xorStr);
        return xorStr.equals(result);
    }

    private void printInfo(String infoStr){
        Log.i("Test", infoStr);
    }
    //###########################
    //Common Function -- End
    //###########################
}
