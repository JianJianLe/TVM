package com.tvm.tvm.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * - @Description:  支付记录
 * - @Author:  Jat
 * - @Date:  2018/12/24
 * - @Time： 19:30
 */
@Entity
public class PaymentRecord {

    @Id(autoincrement = true)
    private Long id;
    /**
     * 总金额
     */
    private double amount;
    /**
     * 数量
     */
    private int num;

    /**
     * 票ID
     */
    private Long priceId;

    /**
     * 票标题
     */
    private String title;

    /**
     * 价格
     */
    private Double price;
    /**
     * 0--网络支付
     * 2--现金
     */
    private int type;
    /**
     * 支付时间
     */
    private Date payTime;

    @Generated(hash = 828805517)
    public PaymentRecord(Long id, double amount, int num, Long priceId,
            String title, Double price, int type, Date payTime) {
        this.id = id;
        this.amount = amount;
        this.num = num;
        this.priceId = priceId;
        this.title = title;
        this.price = price;
        this.type = type;
        this.payTime = payTime;
    }

    @Generated(hash = 473776023)
    public PaymentRecord() {
    }

    public Date getPayTime() {
        return this.payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public int getType() {
        return this.type;
    }

    public String getTypeStr() {
        if (type==0){
            return "网络支付";
        }else{
            return "现金";
        }
    }

    public int getTypeNumber(String type){
        if(type.equals("网络支付")){
            return 0;
        }else{
            return 2;
        }

    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getPriceId() {
        return this.priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }


}
