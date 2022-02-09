package com.mohit.socialnetworkinapp.Models;

public class messageactivitymodel {
    String name,photo,uid,token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public messageactivitymodel() {
    }

    public messageactivitymodel(String name, String photo, String uid) {
        this.name = name;
        this.photo = photo;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
