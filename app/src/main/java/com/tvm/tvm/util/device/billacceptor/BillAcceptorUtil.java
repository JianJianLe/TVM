package com.tvm.tvm.util.device.billacceptor;

public class BillAcceptorUtil {
    //Todo
    //
    private String deviceName;
    //public Object obj;
    private static BillAcceptorUtil instance;
    public synchronized static BillAcceptorUtil getInstance(String deviceName){
        if(instance==null){
            instance = new BillAcceptorUtil(deviceName);
        }
        return instance;
    }

    //ICT or ITL

    public BillAcceptorUtil(String deviceName){
        this.deviceName=deviceName;
    }

    public void init(){
        //Todo
        //obj=new ICTBillAcceptorUtil();

    }
}
