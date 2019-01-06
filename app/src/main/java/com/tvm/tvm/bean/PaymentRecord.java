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

    private int amount;

    private int num;

    /**
     * 0--支付宝
     * 1--微信
     * 2--现金
     */
    private int type;

    private Date payTime;

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
            return "支付宝";
        }else if (type==1){
            return "微信";
        }else{
            return "现金";
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

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1393729170)
    public PaymentRecord(Long id, int amount, int num, int type, Date payTime) {
        this.id = id;
        this.amount = amount;
        this.num = num;
        this.type = type;
        this.payTime = payTime;
    }

    @Generated(hash = 473776023)
    public PaymentRecord() {
    }

}
