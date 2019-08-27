package com.tvm.tvm.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * - @Description:  首页票数
 * - @Author:  Jat
 * - @Date:  2018/12/19
 * - @Time： 22:32
 */
@Entity
public class TicketSummary {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 日期
     * 每天统计，新的一天自动创建一个新记录
     */
    private String date;
    /**
     * 票数
     */
    private int num;

    private String flagStr;

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlagStr() {
        return this.flagStr;
    }

    public void setFlagStr(String flagStr) {
        this.flagStr = flagStr;
    }

    @Generated(hash = 1533884495)
    public TicketSummary(Long id, String date, int num, String flagStr) {
        this.id = id;
        this.date = date;
        this.num = num;
        this.flagStr = flagStr;
    }

    @Generated(hash = 1117549325)
    public TicketSummary() {
    }
}
