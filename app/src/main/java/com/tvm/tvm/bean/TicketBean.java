package com.tvm.tvm.bean;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/3/24
 * - @Time： 22:10
 */
public class TicketBean {

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

    /**
     * 票数
     */
    private int number;

    public void copyFromPrice(Price price){
        this.id = price.getId();
        this.description = price.getDescription();
        this.isDelete = price.getIsDelete();
        this.number = 0;
        this.pic = price.getPic();
        this.price = price.getPrice();
        this.title = price.getTitle();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPic() {
        return pic;
    }

    public void setPic(byte[] pic) {
        this.pic = pic;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
