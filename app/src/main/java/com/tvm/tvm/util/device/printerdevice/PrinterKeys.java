package com.tvm.tvm.util.device.printerdevice;

import java.io.InputStream;

public class PrinterKeys {

    private String deviceNumber;
    private String ticketName; //成人票，儿童票
    private String payType; //现金,支付宝，微信
    private String ticketNumber;
    private String price;
    private InputStream qrData;
    private String dateStr;

    public PrinterKeys(){

    }

    public String getPrice() { return price; }
    public String getDeviceNumber() { return deviceNumber; }
    public String getPayType() { return payType; }
    public String getTicketNumber() { return ticketNumber; }
    public InputStream getQrData() { return qrData; }
    public String getTicketName() { return ticketName; }
    public String getDateStr() { return dateStr; }

    public void setPrice(String price) { this.price = price; }
    public void setDeviceNumber(String deviceNumber) { this.deviceNumber = deviceNumber; }
    public void setPayType(String payType) { this.payType = payType; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public void setQrData(InputStream qrData) { this.qrData = qrData; }
    public void setTicketName(String ticketName) { this.ticketName = ticketName; }
    public void setDateStr(String dateStr) { this.dateStr = dateStr; }

}
