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

    @Generated(hash = 1189605304)
    public Setting(Long id, String shopName, String deviceNo, String md5Key, int selectTimeOut, int payTimeOut,
            int printTimeOut, String printQRCodeFlag, String payDesc) {
        this.id = id;
        this.shopName = shopName;
        this.deviceNo = deviceNo;
        this.md5Key = md5Key;
        this.selectTimeOut = selectTimeOut;
        this.payTimeOut = payTimeOut;
        this.printTimeOut = printTimeOut;
        this.printQRCodeFlag = printQRCodeFlag;
        this.payDesc = payDesc;
    }

    @Generated(hash = 909716735)
    public Setting() {
    }

}
