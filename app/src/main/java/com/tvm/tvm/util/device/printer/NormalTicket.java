package com.tvm.tvm.util.device.printer;

import java.io.InputStream;

public class NormalTicket {

    private String deviceNumber;
    private String ticketName; //成人票，儿童票 用于识别和识别
    private String ticketTitle;//成人票，儿童票 用于补单显示
    private String payType; //现金,支付宝，微信
    private String ticketNumber;
    private String price;
    private String qrData;
    private String dateStr;

    public NormalTicket(){

    }

    public String getPrice() {
        return price;
    }
    public String getDeviceNumber() {
        return deviceNumber;
    }
    public String getPayType() {
        return payType;
    }
    public String getTicketNumber() {
        return ticketNumber;
    }
    public String getQrData() {
        return qrData;
    }
    public String getTicketName() {
        return ticketName;
    }
    public String getDateStr() {
        return dateStr;
    }

    public String getTicketTitle(){
        return ticketTitle;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }
    public void setPayType(String payType) {
        this.payType = payType;
    }
    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
    public void setQrData(String qrData) {
        this.qrData = qrData;
    }
    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public void setTicketTitle(String ticketTitle) {
        this.ticketTitle = ticketTitle;
    }
}
