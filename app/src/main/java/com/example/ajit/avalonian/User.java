package com.example.ajit.avalonian;

import android.util.Log;

public class User {
    public String username;
    public String email;
    public String pwd;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(user.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    public void User_phone(String username, String pwd) {
        this.username = username;
        this.pwd = pwd;
    }


    public String StringChanger(String s) {
        s = s.replace(".", "_");
        return (s);
    }
}
