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

    @Generated(hash = 455938901)
    public TicketSummary(Long id, String date, int num) {
        this.id = id;
        this.date = date;
        this.num = num;
    }

    @Generated(hash = 1117549325)
    public TicketSummary() {
    }
}
