package com.tvm.tvm.util.device.billacceptor;

import android.os.Handler;
import android.util.Log;

import com.tvm.tvm.util.constant.PreConfig;
import com.tvm.tvm.util.device.SerialPortUtil;
import com.tvm.tvm.util.TimeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BillAcceptorUtil {

    SerialPortUtil serialPort;
    OutputStream mOutputStream;
    InputStream mInputStream;
    ReadThread mReadThread;

    String TAG = "Test";
    int TIME = 50;
    int BA_UART_RW_LENGTH = 1;

    int BA_fd;
    public byte[] BA_receiveData;
    public byte[] BA_sendData;

    public byte[] BA_startCode;
    public byte[] BA_responseCode;
    public byte[] BA_resetCode;
    public byte[] BA_acceptCode;
    public byte[] BA_rejectCode;

    public byte[] BA_enable;
    public byte[] BA_disable;

    private byte moneyCode;
    public int rcvdMoney=-1;
    public int tmpRcvdMoney=-1;

    public Boolean responsedStatus = true;


    //String printString;

    private static BillAcceptorUtil instance;

    public synchronized static BillAcceptorUtil getInstance(){
        if (instance==null) {
            instance = new BillAcceptorUtil();
        }
        return instance;
    }

    // 接收投币器数据的定时器
    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                try {
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[5];
                    int size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onDataReceived(final byte[] buffer, final int size) {

        try {
            for(int i=0;i<size;i++){
                dataCheck(buffer[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int responseFlag = 0;
    private int moneyCheckFlag = 0;
    private int moneySureFlag = 0;

    private void dataCheck(final byte buffer) throws IOException {
        if (buffer == (byte) 0x80) {
            Log.i("Test", "come in 0x80");
            responseFlag = 1;
            responsedStatus=false;
            return;
        }

        if (buffer == (byte) 0x8f && responseFlag == 1) {
            mOutputStream.write(BA_responseCode);
            Log.i("Test", "80 8F response Billacceptor!");
            TimeUtil.delay(200);
            mOutputStream.write(BA_disable); //Disable command : 0x5E
            responsedStatus=true;
            responseFlag = 0;
            return;
        }

        if (buffer == (byte) 0x81) {
            Log.i("Test", "come in 0x81");
            moneyCheckFlag = 1;
            return;
        }

        if (buffer == (byte) 0x8f && moneyCheckFlag == 1) {
            Log.i("Test", "come in 0x8f");
            moneySureFlag = 1;
            moneyCheckFlag = 0;
            return;
        }

        if (moneySureFlag == 1) {

            moneySureFlag = 0;
            moneyCode = buffer;
            tmpRcvdMoney = moneyCheck(moneyCode);

            if (tmpRcvdMoney == -1) {
                Log.i(TAG, "Get Money ERR!");
            }

            Log.i(TAG, "Get RMB: " + tmpRcvdMoney);
            return;
        }

        if (buffer == (byte) 0x10) {
            Log.i(TAG, "Get Money Success!");
            rcvdMoney = tmpRcvdMoney;
            tmpRcvdMoney = 0;
            return;
        }

        if (buffer == (byte) 0x11) {
            Log.i(TAG, "Get Money failed!");
            rcvdMoney = -1;
            tmpRcvdMoney = 0;
            return;
        }
    }

    // 保持投币器与板子连接的定时器
    Handler SendCmdHandler = new Handler();
    Runnable SendCmdRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                mOutputStream.write(BA_startCode);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    /***********************************************公共方法***********************************************/

    //初始化投币器命令
    public void init_BillAcceptorCmd() {
        BA_receiveData = new byte[BA_UART_RW_LENGTH];
        BA_sendData = new byte[BA_UART_RW_LENGTH];

        BA_startCode = new byte[BA_UART_RW_LENGTH];
        BA_responseCode = new byte[BA_UART_RW_LENGTH];
        BA_resetCode = new byte[BA_UART_RW_LENGTH];

        BA_acceptCode = new byte[BA_UART_RW_LENGTH];
        BA_rejectCode = new byte[BA_UART_RW_LENGTH];

        BA_enable = new byte[BA_UART_RW_LENGTH];
        BA_disable = new byte[BA_UART_RW_LENGTH];

        initAllBuffer();

        BA_startCode[0] = 0x0c;
        BA_responseCode[0] = 0x02;
        BA_resetCode[0] = 0x30;
        BA_acceptCode[0] = 0x02;
        BA_rejectCode[0] = 0x0f;

        BA_enable[0] = 0x3e;
        BA_disable[0] = 0x5e;
    }



    //初始化投币器设备
    public void init_BillAcceptorDevice() {

        String serialPortFilePath_One = "/dev/ttyS3";
        String serialPortFilePath_Two = "/dev/ttyGS3";

        if(PreConfig.AndroidBoardVersion=="2.0"){
            serialPortFilePath_One = "/dev/ttyS4";
            serialPortFilePath_Two = "/dev/ttyGS4";
        }

        File serialPortFile = new File(serialPortFilePath_One);
        if(!serialPortFile.exists()){
            serialPortFile = new File(serialPortFilePath_Two);
        }
        try {
            serialPort = new SerialPortUtil(serialPortFile,9600, 0);
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


    // 启动收款机
    public void ba_Enable() {
        try {
            mOutputStream.write(BA_enable);
            //mOutputStream.write(BA_disable);//Test May12 - Star
            Log.i("Test", "ba_Enable()");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 停止收款机
    public void ba_Disable() {
        try {
            mOutputStream.write(BA_disable);
            Log.i("Test", "ba_Disable()");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***********************************************私有方法***********************************************/

    private void initAllBuffer() {
        int i;

        for (i = 0; i < BA_UART_RW_LENGTH; i++) {
            BA_receiveData[i] = 0;
            BA_sendData[i] = 0;
            BA_startCode[i] = 0;
            BA_responseCode[i] = 0;
            BA_resetCode[i] = 0;
            BA_acceptCode[i] = 0;
            BA_rejectCode[i] = 0;
        }
    }

    //接收纸币
    public void moneyAccept() {
        try {
            mOutputStream.write(BA_acceptCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //拒收纸币
    public void moneyReject() {
        try {
            mOutputStream.write(BA_rejectCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //金额判断
    private int moneyCheck(byte moneyCode) {
        int tMoney = -1;

        switch (moneyCode) {

            case 0x40:
                moneyAccept();
                tMoney = 1;
                Log.d("Test", "---MoneyAccept=1");
                break;
            case 0x41:
                moneyAccept();
                tMoney = 5;
                Log.d("Test", "---MoneyAccept=5");
                //jBAdataSend(BA_startCode);//连接纸钞机
                break;
            case 0x42:
                moneyAccept();
                tMoney = 10;
                Log.d("Test", "---MoneyAccept=10");
                break;
            case 0x43:
                moneyAccept();
                tMoney = 20;
                Log.d("Test", "---MoneyAccept=20");
                break;

            case 0x44:
                moneyAccept();
                tMoney = 50;
                Log.d("Test", "---MoneyAccept=50");
                break;

            case 0x45:
                moneyAccept();
                tMoney = 100;
                Log.d("Test", "---MoneyAccept=100");
                break;

            default:
                tMoney = -1;
                moneyReject();
                break;
        }

        return tMoney;
    }

}
