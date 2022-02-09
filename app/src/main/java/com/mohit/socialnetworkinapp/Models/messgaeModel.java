package com.mohit.socialnetworkinapp.Models;

public class messgaeModel {
    private String messageid,message,imageurl,senderid;
    private long timestamp;

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }



    public messgaeModel() {
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public messgaeModel(String messageid, String message, String senderid, long timestamp) {
        this.messageid = messageid;
        this.message = message;
        this.senderid = senderid;
        this.timestamp = timestamp;
    }
}
