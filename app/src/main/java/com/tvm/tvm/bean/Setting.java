package com.tvm.tvm.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/2/23
 * - @Time： 21:24
 */
@Entity
public class Setting {

    @Id(autoincrement = true)
    private Long id;

    private String shopName;

    private String deviceNo;

    private String md5Key;

    private int selectTimeOut;

    private int payTimeOut;

    private int printTimeOut;

    private String printQRCodeFlag;

    private String payDesc;

    private String payDeviceID;

    private String payDeviceName;

    private String billAcceptorName;

    private String billType;

    private String showMainViewFlag;

    private String showOrderNumberFlag;

    private int initalTicketNumber;

    private int billTimesNumber;//Value Multiplier 倍率

    private String billAcceptorCashAmountType;//纸币面额

    public String getPayDesc() {
        return this.payDesc;
    }

    public void setPayDesc(String payDesc) {
        this.payDesc = payDesc;
    }

    public String getPrintQRCodeFlag() {
        return this.printQRCodeFlag;
    }

    public void setPrintQRCodeFlag(String printQRCodeFlag){
        this.printQRCodeFlag = printQRCodeFlag;
    }

    public int getPrintTimeOut() {
        return this.printTimeOut;
    }

    public void setPrintTimeOut(int printTimeOut) {
        this.printTimeOut = printTimeOut;
    }

    public int getPayTimeOut() {
        return this.payTimeOut;
    }

    public void setPayTimeOut(int payTimeOut) {
        this.payTimeOut = payTimeOut;
    }

    public int getSelectTimeOut() {
        return this.selectTimeOut;
    }

    public void setSelectTimeOut(int selectTimeOut) {
        this.selectTimeOut = selectTimeOut;
    }

    public String getMd5Key() {
        return this.md5Key;
    }

    public void setMd5Key(String md5Key) {
        this.md5Key = md5Key;
    }

    public String getDeviceNo() {
        return this.deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getShopName() {
        return this.shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayDeviceID() {
        return this.payDeviceID;
    }

    public void setPayDeviceID(String payDeviceID) {
        this.payDeviceID = payDeviceID;
    }

    public int getInitalTicketNumber() {
        return this.initalTicketNumber;
    }

    public void setInitalTicketNumber(int initalTicketNumber) {
        this.initalTicketNumber = initalTicketNumber;
    }

    public String getShowMainViewFlag() {
        return this.showMainViewFlag;
    }

    public void setShowMainViewFlag(String showMainViewFlag) {
        this.showMainViewFlag = showMainViewFlag;
    }

    public String getPayDeviceName() {
        return this.payDeviceName;
    }

    public void setPayDeviceName(String payDeviceName) {
        this.payDeviceName = payDeviceName;
    }

    public String getBillAcceptorName() {
        return this.billAcceptorName;
    }

    public void setBillAcceptorName(String billAcceptorName) {
        this.billAcceptorName = billAcceptorName;
    }

    public String getBillType() {
        return this.billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getBillAcceptorCashAmountType() {
        return this.billAcceptorCashAmountType;
    }

    public void setBillAcceptorCashAmountType(String billAcceptorCashAmountType) {
        this.billAcceptorCashAmountType = billAcceptorCashAmountType;
    }

    public int getBillTimesNumber() {
        return this.billTimesNumber;
    }

    public void setBillTimesNumber(int billTimesNumber) {
        this.billTimesNumber = billTimesNumber;
    }

    public String getShowOrderNumberFlag() {
        return this.showOrderNumberFlag;
    }

    public void setShowOrderNumberFlag(String showOrderNumberFlag) {
        this.showOrderNumberFlag = showOrderNumberFlag;
    }

    @Generated(hash = 851088808)
    public Setting(Long id, String shopName, String deviceNo, String md5Key, int selectTimeOut, int payTimeOut,
            int printTimeOut, String printQRCodeFlag, String payDesc, String payDeviceID, String payDeviceName,
            String billAcceptorName, String billType, String showMainViewFlag, String showOrderNumberFlag,
            int initalTicketNumber, int billTimesNumber, String billAcceptorCashAmountType) {
        this.id = id;
        this.shopName = shopName;
        this.deviceNo = deviceNo;
        this.md5Key = md5Key;
        this.selectTimeOut = selectTimeOut;
        this.payTimeOut = payTimeOut;
        this.printTimeOut = printTimeOut;
        this.printQRCodeFlag = printQRCodeFlag;
        this.payDesc = payDesc;
        this.payDeviceID = payDeviceID;
        this.payDeviceName = payDeviceName;
        this.billAcceptorName = billAcceptorName;
        this.billType = billType;
        this.showMainViewFlag = showMainViewFlag;
        this.showOrderNumberFlag = showOrderNumberFlag;
        this.initalTicketNumber = initalTicketNumber;
        this.billTimesNumber = billTimesNumber;
        this.billAcceptorCashAmountType = billAcceptorCashAmountType;
    }

    @Generated(hash = 909716735)
    public Setting() {
    }

}
