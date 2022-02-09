package com.mohit.socialnetworkinapp.Models;

public class registerinformatiom {
    String name,uid,email,password,image;

    public registerinformatiom() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public registerinformatiom(String name, String uid, String email, String password, String image) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.image = image;
    }
}
