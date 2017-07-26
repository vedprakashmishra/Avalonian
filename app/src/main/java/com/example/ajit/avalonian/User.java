package com.example.ajit.avalonian;

import android.util.Log;

/**
 * Created by vpmishra on 24-07-2017.
 */

public class User  {
    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    public String StringChanger(String s) {
        s=s.replace(".","_");
        return (s);
    }
}
