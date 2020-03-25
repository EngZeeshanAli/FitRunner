package com.example.fitrunner.Authentications;

import androidx.annotation.NonNull;

import com.example.fitrunner.UiControllers.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
