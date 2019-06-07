package com.yunliaoim.firechat.bean;

import java.io.Serializable;

public class User implements Serializable {

    private String mUsername;
    private String mPassword;

    public User() {

    }

    public User(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

}
