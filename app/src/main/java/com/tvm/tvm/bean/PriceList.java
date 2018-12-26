package com.tvm.tvm.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 价格列表
 */
@Entity
public class PriceList {

    @Id(autoincrement = true)
    private Long id;

    private double price;

    private String title;

    private String description;

    private int isDelete;

    public int getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 670365187)
    public PriceList(Long id, double price, String title, String description,
            int isDelete) {
        this.id = id;
        this.price = price;
        this.title = title;
        this.description = description;
        this.isDelete = isDelete;
    }

    @Generated(hash = 2042841242)
    public PriceList() {
    }


}
