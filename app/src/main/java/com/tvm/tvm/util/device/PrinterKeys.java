package com.tvm.tvm.util.device;

import java.io.InputStream;

public class PrinterKeys {

    private String shopNumber;
    private String ticketType; //成人票，儿童票
    private String payType; //现金,支付宝，微信
    private String ticketNumber;
    private String price;
    private InputStream qrData;
    private String timeData;
    private String dateStr;

    public PrinterKeys(){

    }

    public String getPrice() { return price; }
    public String getShopNumber() { return shopNumber; }
    public String getPayType() { return payType; }
    public String getTicketNumber() { return ticketNumber; }
    public InputStream getQrData() { return qrData; }
    public String getTicketType() { return ticketType; }
    public String getTimeData() { return timeData; }
    public String getDateStr() { return dateStr; }

    public void setPrice(String price) { this.price = price; }
    public void setShopNumber(String shopNumber) { this.shopNumber = shopNumber; }
    public void setPayType(String payType) { this.payType = payType; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
    public void setQrData(InputStream qrData) { this.qrData = qrData; }
    public void setTicketType(String tickType) { this.ticketType = tickType; }
    public void setTimeData(String timeData) { this.timeData = timeData; }
    public void setDateStr(String dateStr) { this.dateStr = dateStr; }

}