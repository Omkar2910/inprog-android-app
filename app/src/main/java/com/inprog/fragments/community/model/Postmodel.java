package com.inprog.fragments.community.model;

public class Postmodel {

    String Title, Desc, PostImage, User, Name, Pfpimg, Time;

    public Postmodel() {
    }

    public Postmodel(String title, String desc, String postImage, String user, String name, String pfpimg, String time) {
        Title = title;
        Desc = desc;
        PostImage = postImage;
        User = user;
        Name = name;
        Pfpimg = pfpimg;
        Time = time;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPfpimg() {
        return Pfpimg;
    }

    public void setPfpimg(String pfpimg) {
        Pfpimg = pfpimg;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
