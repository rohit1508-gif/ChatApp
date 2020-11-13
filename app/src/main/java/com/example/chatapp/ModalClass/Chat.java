package com.example.chatapp.ModalClass;

public class Chat {
    public String sender;
    public String receiver;
    public String msg;
    public String key;
    String time;
    boolean isseen;
    String type;
    public Chat() {

    }

    public Chat(String sender, String receiver, String msg,String key,String time,boolean isseen,String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.key = key;
        this.time = time;
        this.isseen = isseen;
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
