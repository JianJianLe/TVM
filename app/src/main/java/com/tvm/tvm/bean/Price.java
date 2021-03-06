package com.tvm.tvm.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 价格列表
 */
@Entity
public class Price {

    @Id(autoincrement = true)
    private Long id;
    /**
     * 图片字节码
     */
    private byte[] pic;
    /**
     * 价格
     */
    private double price;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
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

    public byte[] getPic() {
        return this.pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    @Generated(hash = 1173672343)
    public Price(Long id, byte[] pic, double price, String title,
            String description, int isDelete) {
        this.id = id;
        this.pic = pic;
        this.price = price;
        this.title = title;
        this.description = description;
        this.isDelete = isDelete;
    }

    @Generated(hash = 812905808)
    public Price() {
    }

}
