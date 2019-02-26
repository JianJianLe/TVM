package com.tvm.tvm.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BillSetting {

    @Id(autoincrement = true)
    private Long id;

    private String templateNum;

    private String ticketName;

    private String ticketBody;

    private Date createDate;

    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getTicketBody() {
        return this.ticketBody;
    }

    public void setTicketBody(String ticketBody) {
        this.ticketBody = ticketBody;
    }

    public String getTicketName() {
        return this.ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getTemplateNum() {
        return this.templateNum;
    }

    public void setTemplateNum(String templateNum) {
        this.templateNum = templateNum;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 657907997)
    public BillSetting(Long id, String templateNum, String ticketName,
            String ticketBody, Date createDate) {
        this.id = id;
        this.templateNum = templateNum;
        this.ticketName = ticketName;
        this.ticketBody = ticketBody;
        this.createDate = createDate;
    }

    @Generated(hash = 1203130324)
    public BillSetting() {
    }
}
