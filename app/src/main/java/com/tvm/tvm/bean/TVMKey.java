package com.tvm.tvm.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 - @Description:  第一次安装APP需授权
 - @Author:  Star
 - @Date:  ${DATE}
 - @Time： ${TIME}
 */
@Entity
public class TVMKey {
    @Id(autoincrement = true)
    private Long id;
    private String key;
    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 2023019837)
    public TVMKey(Long id, String key) {
        this.id = id;
        this.key = key;
    }
    @Generated(hash = 1319512409)
    public TVMKey() {
    }

}
