package com.mohit.socialnetworkinapp.Models;

public class postmodel {
    String uid, username, userprofile,caption, tag, postimage, date, time, postid;
    Long countpost;

    public Long getCountpost() {
        return countpost;
    }

    public void setCountpost(Long countpost) {
        this.countpost = countpost;
    }

    public postmodel() {
    }

    public postmodel(String uid, String username, String userprofile, String caption, String tag, String postimage, String date, String time, String postid, Long countpost) {
        this.uid = uid;
        this.username = username;
        this.userprofile = userprofile;
        this.caption = caption;
        this.tag = tag;
        this.postimage = postimage;
        this.date = date;
        this.time = time;
        this.postid = postid;
        this.countpost = countpost;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserprofile() {
        return userprofile;
    }

    public void setUserprofile(String userprofile) {
        this.userprofile = userprofile;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
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

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

}

