package com.tvm.tvm.util.device.printer;


public class SummaryTicket {
    private String startDateTime;
    private String endDateTime;
    private String cashTotalAmount;
    private String cashTotalCount;
    private String onlinePayTotalAmount;
    private String onlinePayTotalCount;
    private String totalAmount;
    private String totalCount;

    public String getStartDateTime(){
        return startDateTime;
    }

    public String getEndDateTime(){
        return endDateTime;
    }

    public String getCashTotalAmount(){
        return cashTotalAmount;
    }

    public String getCashTotalCount(){
        return cashTotalCount;
    }
    public String getOnlinePayTotalAmount(){
        return onlinePayTotalAmount;
    }

    public String getOnlinePayTotalCount(){
        return onlinePayTotalCount;
    }

    public String getTotalAmount(){
        return totalAmount;
    }

    public String getTotalCount(){
        return totalCount;
    }


    public void setStartDateTime(String startDateTime){
        this.startDateTime=startDateTime;
    }

    public void setEndDateTime(String endDateTime){
        this.endDateTime=endDateTime;
    }

    public void setCashTotalAmount(String cashTotalAmount){
        this.cashTotalAmount=cashTotalAmount;
    }

    public void setCashTotalCount(String cashTotalCount){
        this.cashTotalCount=cashTotalCount;
    }

    public void setOnlinePayTotalAmount(String onlinePayTotalAmount) {
        this.onlinePayTotalAmount = onlinePayTotalAmount;
    }

    public void setOnlinePayTotalCount(String onlinePayTotalCount) {
        this.onlinePayTotalCount = onlinePayTotalCount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }
}
