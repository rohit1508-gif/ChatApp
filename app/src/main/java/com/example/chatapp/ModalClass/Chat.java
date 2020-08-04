package com.example.chatapp.ModalClass;

public class Chat {
    public String sender;
    public String receiver;
    public String msg;
    public String key;
    String time;
    boolean isseen;
    public Chat() {

    }

    public Chat(String sender, String receiver, String msg,String key,String time,boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.key = key;
        this.time = time;
        this.isseen = isseen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getKey(){
        return key;
    }
    public String getTime(){
        return time;
    }

    public boolean isIsseen() {
        return isseen;
    }
}
