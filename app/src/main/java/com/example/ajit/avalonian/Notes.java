package com.example.ajit.avalonian;

/**
 * Created by vpmishra on 26-07-2017.
 */

public class Notes {
    public String title;
    public String des;
    public String time;

    public Notes() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Notes(String t, String dis, String time) {
        this.title = t;
        this.des = dis;
        this.time=time;
    }

    @Override
    public String toString() {
        return "Notes{" +
                "title='" + title + '\'' +
                ", des='" + des + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
