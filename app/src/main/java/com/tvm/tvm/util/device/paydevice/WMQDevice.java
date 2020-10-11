package com.tvm.tvm.util.device.paydevice;

import android.os.Handler;
import android.util.Log;

import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.PaymentRecord;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.Setting;
import com.tvm.tvm.bean.TicketBean;
import com.tvm.tvm.bean.dao.PaymentRecordDao;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.bean.dao.SettingDao;
import com.tvm.tvm.util.DataUtils;
import com.tvm.tvm.util.LogUtils;
import com.tvm.tvm.util.TimeUtil;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.SerialPortUtil;
import com.tvm.tvm.util.device.printer.PrinterAction;
import com.tvm.tvm.util.device.printer.PrinterCase;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WMQDevice {
    public String QRData=null;
    public String activityRecord=null;
    public boolean paySuccess=false;//判断是否在支付界面支付成功
    public String strUniquePayCode=null;

    private String receivedCMD;
    private int payAmount;
    private boolean hasGotServerStatus=false;

    private Setting setting;
    private SettingDao settingDao;
    private List<Price> priceList;
    private PriceDao priceDao;
    private SerialPortUtil serialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private String strPreUniquePayCode="NULL";

    private static WMQDevice instance;
    public synchronized static WMQDevice getInstance(){
        if(instance==null)
            instance=new WMQDevice();
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
            serialPort = new SerialPortUtil(serialPortFile,9600,0);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        hasGotServerStatus=false;
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

        QueryBuilder qb = priceDao.queryBuilder();
        qb.where(PriceDao.Properties.IsDelete.eq(0));
        qb.orderAsc(PriceDao.Properties.Id);
        priceList = qb.list();
    }

    private Handler mHandler=new Handler();

    private class ReadThread extends Thread {
        private SendRunnable runnable = new SendRunnable();
        //第一次运行线程时设置成true
        private boolean beginning = false;
        //缓冲区()
        byte[] buffer = new byte[512];

        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                int size;
                try {
                    if (mInputStream == null) return;
                    //读取数据,同时获取数据长度(数据长度不是数组长度,而是实际接收到的数据长度),数据被读取到了缓冲区 buffer中
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        //临时数组,将缓冲区buffer中的有效数据读取出来,临时数据长度就是接收到的数据长度。
                        byte[] temp = new byte[size];
                        System.arraycopy(buffer, 0, temp, 0, size);
                        //具体注释见init方法
                        runnable.init(temp, size);
                        //如果程序第一次运行
                        if (!beginning) {
                            //运行runnable,只在第一次执行,如果重复执行虽不会抛出异常,但是也无法正常执行功能
                            mHandler.post(runnable);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public class SendRunnable implements Runnable {
        private byte[] lastBuffer;
        int time = 0;
        boolean work = true;
        private int lastBufferLength;

        //断包处理逻辑包含其中
        public void init(byte[] buffer, int size) {

            if (lastBuffer == null) {
                lastBuffer = buffer;
            } else {
                lastBufferLength = lastBuffer.length;
                byte[] temp = new byte[lastBufferLength + size];
                //先拷贝之前的数据
                System.arraycopy(lastBuffer, 0, temp, 0, lastBufferLength);
                //再拷贝刚接收到的数据
                System.arraycopy(buffer, 0, temp, lastBufferLength, size);
                lastBuffer = null;
                lastBuffer = temp;
                temp = null;
            }
            work = true;
            time = 0;
        }

        public void reStart() {
            work = true;
            time = 0;
        }

        public void stop() {
            work = false;
            time = 0;
        }
        //接收完成后重置完整消息缓冲区
        public void reset() {
            work = false;
            time = 0;
            lastBuffer = null;
        }

        @Override
        public void run() {
            while (work) {
                try {
                    Thread.sleep(20);
                    time += 20;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (time >= 100) {
                    byte[] finalBuffer = lastBuffer;
                    reset();
                    //业务处理方法
                    onDataReceived(finalBuffer);
                }
            }

        }
    }

    private void onDataReceived(final byte[] buffer) {
        String cmdStr=DataUtils.bytesToHex(buffer);
        printInfo("cmdStr="+cmdStr);
        if(cmdStr.startsWith("AA")){
            receivedCMD=getAllCMD(cmdStr);

            if(receivedCMD==null){
                printInfo("receivedCMD=null, Invalid Server CMD:"+cmdStr);
                return;
            }
            printInfo("Server CMD:"+receivedCMD);


            if(checkSC()){
                printInfo("checkSC=true");

                //查询 0x00
                if(hasServerQuery()){
                    hasGotServerStatus=true;
                    cmd_ReplySever();
                }

                //售票机发送获取二维码支付链接指令
                if(hasQRCode()){
                    String strQRCodeData=getQRCodeData();
                    if(strQRCodeData!=null)
                        QRData=strQRCodeData;
                }

                if(hasRepliedUploadParam()){
                    printInfo("Upload param 成功");
                }

                //通知主板支付结果-0x34
                //用户扫码支付后，由维码器通知主板支付结果
                //指令超时时间 15s,应答超时维码器会重发2次,主板每次接收到指令需应答,如主板超时无应答此命令,维码器会自动退款
                if(hasPayResult()){
                    printInfo("hasPayResult() 0x34");
                    strUniquePayCode=getPayResultUniqueCode();
                    printInfo("strUniquePayCode="+strUniquePayCode);
                    if(strPreUniquePayCode!=null && !strUniquePayCode.equals(strPreUniquePayCode)) {
                        if (checkPayResult()) {
                            strPreUniquePayCode = strUniquePayCode;
                            cmd_ReplyPayMatch();
                            printInfo("activityRecord=" + activityRecord);
                            if (activityRecord.equals("PayDetailActivity"))
                                paySuccess = true;
                            else {
                                printTicketDirectly();
                            }
                        } else {
                            cmd_ReplyPayMismatch();
                        }
                    }
                }

                //售票机出票成功后发送上报交易结果 B5
                if(hasReplyPrintSuccess())
                    printInfo("B5: 上传出货结果成功，支付盒子应答");


            }else {
                printInfo("checkSC=false");

            }
        }else{
            printInfo("Invalid Server CMD:"+cmdStr);
        }
    }



    //======== Server Query Start ========
    //查询
    //累加和校验：命令+数据长度+数据域的累加和 (低字节在前)
    //支付盒子发送：AA 00 01 00 01 00 AB //0x00:维码器已联网 0x01:维码器未联网
    //售票机应答：AA 80 09 01 1A 00 00 00 00 00 00 00 A4 00 AB
    private boolean hasServerQuery(){
        return compareCMD(receivedCMD,"AA0001.*AB");
    }

    private void cmd_ReplySever(){
        printInfo("hasServerQuery receivedCMD="+receivedCMD);
        printInfo("Reply Server:AA8009011A00000000000000A400AB");
        write("AA8009011A00000000000000A400AB");
    }
    //======== Server Query End ========


    //======== Upload Param Start ========
    //当第一次连接维码器后，通信成功 即能收到AA300100.*AB指令
    private boolean isReadyUploadParams(){
        return compareCMD(receivedCMD,"AA300100.*AB");
    }

    public void cmd_UploadParams(){
        initDB();
        thread_CheckUploadParams();
    }
    //本地商品信息同步到服务器-0x32
    //AA 32 19 01 01 30 31 00 00 10 27 00 00 00 00 00 00 00 64 64 00 00 00 00 00 00 00 00 AD 01 AB
    private void thread_CheckUploadParams(){
        new Thread(){
            public void run() {
                Price price=null;
                for(int i=0;i<priceList.size();i++){
                    price = priceList.get(i);
                    int currentChannel=i+1;
                    String dataStr="01"+//此次上报的货道号数量
                            DataUtils.getDecToHex(currentChannel,2) + //货道号 1
                            stringToHex(DataUtils.getDecToHex(currentChannel,2),8) + //4byte 货道号 1_货柜贴纸标识
                            decToHex((int)price.getPrice() * 100 ,8) + //货道号 1_购买单价/Pcs
                            "00000000" + //货道号 1_游戏单价/Pcs， 口红机游戏类
                            "00" + //货道号 1_游戏概率，口红 机游戏类
                            decToHex(200,2)+ //货道号 1_库存上限
                            decToHex(200,2)+ //货道号 1_当前库存
                            "00000000" + //货道号 1_货道交易总金额 账目
                            "00000000" + //货道号 1_货道累计出货总 数量
                            "";
                    String lengthStr = DataUtils.decToHex((dataStr.length())/2);
                    dataStr = "32" + lengthStr + dataStr;
                    String cmdStr =  "AA"  + addEndCMD(dataStr);
                    write(cmdStr);
                    TimeUtil.delay(2000);
                }
            }
        }.start();
    }


    //支付盒子应答上报本地通道信息
    //AAB201 00 XXAB: 数据域 00：上传成功, 01:支付盒子无网络，02：上传超时，主板可重发
    private boolean hasRepliedUploadParam(){
        return compareCMD(receivedCMD,"AAB20100.*AB");
    }
    //======== Upload Param End ========

    //======== QR Code Start ========
    //售票机发送获取二维码支付链接指令（子命令 0x0A）
    //AA 0E 02 C9 0A  [6byte随机数]  [4byte支付金额] Check DD
    //支付盒子应答：AA XX 01 C9 0A data[n] Check DD （data[n]为支付二维码链接数据）
    public void init_QRCode(){
        strUniquePayCode=null;
        QRData=null;
    }


    private boolean hasQRCode(){
        return compareCMD(receivedCMD,"AAB3.*AB");
    }

    //主板请求支付二维码链接地址，由微码器应答返回，指令应答超时时间为 2s 可重发 2 次
    //AA3306005A10270000CA00AB
    public void cmd_GetQRCode(int amount){
        payAmount=amount;
        printInfo("Amount = "+amount+", Send CMD to get QR code.");
        String cmdStr="3306005A"+decToHex(amount,8);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    private String getQRCodeData(){
        String hexStr= getCMDDataByRegex(receivedCMD,"(?<=AAB3..).*(?=....AB)");
        printInfo(hexStr);
        return DataUtils.convertHexToString(hexStr);
    }
    //======== QR Code End ========


    //======== Pay result Start ========
    //支付盒子收款成功发送同步支付结果指令（子命令 0x03）
    //AA 29 01 C9 03  [6byte支付唯一码]  [4byte支付金额]  [用户ID] Check DD
    //售票机匹配正确则应答：AA 0E 02 C9 03  [6byte支付唯一码]  [4byte支付金额]   Check DD
    //售票机匹配错误则应答：AA 0E 02 C9 03  [6byte支付唯一码]  00 00 00 00   Check DD
    //匹配正确可打印票据，如果匹配错误则不打印票据
    private boolean hasPayResult(){
        return compareCMD(receivedCMD,"AA340F.*AB");
    }

    private String getPayResultUniqueCode(){
        return getCMDDataByRegex(receivedCMD,"(?<=AA340F).{20}");
    }

    private String getPayResultAmount(){
        return getCMDDataByRegex(receivedCMD,"(?<=AA340F.{22}).{8}");
    }

    private boolean checkPayResult(){
        return decToHex(payAmount,8).equals(getPayResultAmount());
    }

    private void cmd_ReplyPayMatch(){
        printInfo("Pay successfully - 支付成功，售票机匹配正确应答");
        String cmdStr="B40F"+strUniquePayCode+
                      "00" + //出货状态,0x00:出货完成，订单完成,0x01:正在出货中
                      "00000000";
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    private void cmd_ReplyPayMismatch(){
        printInfo("Mismatch - 支付成功，售票机匹配错误，发送退款指令");
        String cmdStr="B40F"+strUniquePayCode+
                "03" + //0x03-0x07:出货故障代码，此时必须退款，下边 内容是退款金额
                decToHex(payAmount,8);
        cmdStr = "AA" + addEndCMD(cmdStr);
        write(cmdStr);
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
    //======== Pay result End ========

    //======== Cash Pay Start ========
    //AA8009011A00000000000000A400AB
    public void cmd_CashPay(int amount){
        String cmdStr = "8009011A000000000000" + decToHex(amount,2);
        cmdStr="AA" + addEndCMD(cmdStr);
        write(cmdStr);
    }

    //======== Cash Pay End ========


    //======== Print Success Record Start ========
    //主板上报出货结果-0x35
    //主板出货或发生交易时，需要上报次命令;指令应答超时时间 15s
    public void cmd_UploadOnlinePayReuslt(TicketBean ticket, int payType){
        printInfo("35: 售票机出票成功，主板上报出货结果");
        String dataStr = getCMD_PayResult(ticket,payType);
        String lengthStr = DataUtils.decToHex((dataStr.length())/2);
        dataStr = "35" + lengthStr + dataStr;
        String cmdStr = "AA" + addEndCMD(dataStr);
        write(cmdStr);
    }

    private String getCMD_PayResult(TicketBean ticket, int payType){
        String uniquePayCode=null;
        String payTypeCode=null;
        String totalAmount=null;
        String totalNumber=null;
        if(payType==StringUtils.OnlinePay){//移动支付
            payTypeCode="01";
            totalAmount=decToHex(getAccumulateAmount(ticket,StringUtils.OnlinePay),8);
            totalNumber=decToHex(accumulateTicket(ticket.getId(),StringUtils.OnlinePay),8);
            uniquePayCode=strUniquePayCode;
        }
        else{
            payTypeCode="02";//线下支付
            totalAmount=decToHex(getAccumulateAmount(ticket,StringUtils.CashPay),8);
            totalNumber=decToHex(accumulateTicket(ticket.getId(),StringUtils.CashPay),8);
            uniquePayCode="FFFFFFFFFFFF"+ totalNumber;
        }

        String cmdStr = uniquePayCode + //支付唯一码(若为线下支付，前6字节为全FF，后面四字节为自增ID)
                "01" + //此次上报的货道号数量
                decToHex(getCurrentChannel(ticket.getId()),2) + // 物理货道号 1
                "01" + //货道号 1_购买方式
                payTypeCode + //货道号 1_支付类型
                decToHex(payAmount,8) +//货道号 1_交易金额
                "01" + // 货道号 1_出货数量
                totalAmount + //货道号 1_货道累计交易金额 账目
                totalNumber; //  货道号 1_货道累计出货数量

        return cmdStr;
    }


    //ticketType: 0->线上支付，2->线下支付
    private int accumulateTicket(long ticketId,int ticketType){
        PaymentRecordDao paymentRecordDao = AppApplication.getApplication().getDaoSession().getPaymentRecordDao();
        QueryBuilder qb = paymentRecordDao.queryBuilder();
        qb.where(qb.and(PaymentRecordDao.Properties.PriceId.eq(ticketId),PaymentRecordDao.Properties.Type.eq(ticketType)));
        List<PaymentRecord> paymentRecordList= qb.list();
        //printInfo("Ticket ID = " +ticketId+", Ticket Type = " + ticketType + ", Ticket total number:" + qb.list().size());
        return paymentRecordList.size();
    }


    private int getAccumulateAmount(TicketBean ticket,int ticketType){
        return (int)(ticket.getPrice() * 100 * accumulateTicket(ticket.getId(),ticketType));
    }
     private int getCurrentChannel(long ticketId){
        int currentChannel=0;
        for(int i=0;i<priceList.size();i++){
            if(priceList.get(i).getId()==ticketId){
                currentChannel=i+1;
                break;
            }
        }
        return currentChannel;
    }

    private boolean hasReplyPrintSuccess(){
        return compareCMD(receivedCMD,"AAB50100.*AB");
    }
    //======== Print Success Record End ========


    //###########################
    //Common Function -- Start
    //###########################


    private String decToHex(int number, int count){
        return DataUtils.addRightZeros(DataUtils.decToHex(number),count);
    }

    private String stringToHex(String content, int count){
        return DataUtils.addRightZeros(DataUtils.convertStringToHex(content),count);
    }

    private String addEndCMD(String cmdStr){
        //printInfo("校验结果：" + DataUtils.makeChecksum(cmdStr));
        return DataUtils.removeSpace(cmdStr) + DataUtils.makeChecksum(cmdStr) + "AB";
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
        String pattern="AA.*AB";
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

    //累加和校验 SC:Sum Check
    private boolean checkSC(){
        String hexStr = getCMDDataByRegex(receivedCMD,"(?<=AA).*(?=AB)");
        String result = hexStr.substring(hexStr.length()-4,hexStr.length());
        //printInfo("result="+result);
        String cmdStr = hexStr.substring(0,hexStr.length()-4);
        //printInfo("cmdStr="+cmdStr);
        String scStr = DataUtils.makeChecksum(cmdStr);
        //printInfo("scStr="+scStr);
        return scStr.equals(result);
    }

    private void printInfo(String infoStr){
        //Log.i("Test", infoStr);
        LogUtils.i("Test", infoStr);
    }
    //###########################
    //Common Function -- End
    //###########################
}
