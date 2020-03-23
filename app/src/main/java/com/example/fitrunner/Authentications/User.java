package com.example.fitrunner.Authentications;

public class User {
    String name;
    String email;
    String img;
    String uid;
    public User() {
    }

    public User(String name, String email, String uid) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }

    public User(String name, String email, String img, String uid) {
        this.name = name;
        this.email = email;
        this.img = img;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImg() {
        return img;
    }
}
