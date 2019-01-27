package com.tvm.tvm.util.device;

public class PrinterMessage extends PrinterKeys {

    private String title;
    private String titleDesc;
    private String ticketDesc;

    public PrinterMessage(){

    }

    public String getTicketDesc() { return ticketDesc; }
    public String getTitle() { return title; }
    public String getTitleDesc() { return titleDesc; }

    public void setTicketDesc(String ticketDesc) { this.ticketDesc = ticketDesc; }
    public void setTitle(String title) { this.title = title; }
    public void setTitleDesc(String titleDesc) { this.titleDesc = titleDesc; }

    @Override
    public String toString() {
        return "Print Message: 欢迎光临！";
    }
}
