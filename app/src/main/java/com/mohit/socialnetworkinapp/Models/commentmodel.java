package com.mohit.socialnetworkinapp.Models;

public class commentmodel {
    String image,username,date,time,discription,uid,postid,uniquekey;

    public String getPostid() {
        return postid;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public commentmodel(String image, String username, String date, String time, String discription, String uid, String postid, String uniquekey) {
        this.image = image;
        this.username = username;
        this.date = date;
        this.time = time;
        this.discription = discription;
        this.uid = uid;
        this.postid = postid;
        this.uniquekey = uniquekey;
    }

    public commentmodel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }
}
