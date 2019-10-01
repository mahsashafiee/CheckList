package com.example.checklist.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {

    private UUID mId;
    private String mUsername;
    private String mPassword;

    public User() {
        mId = UUID.randomUUID();

    }

    public User(String username, String password) {

        this();
        mUsername = username;
        mPassword = password;
    }

    public User(UUID id) {
        mId = id;
    }

    public UUID getID() {
        return mId;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getPassword() {
        return mPassword;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User users = (User) o;
        return Objects.equals(mUsername, users.mUsername) &&
                Objects.equals(mPassword, users.mPassword);
    }


    @Override
    public String toString() {
        return "User{" +
                "mUsername='" + mUsername + '\'' +
                ", mPassword='" + mPassword + '\'' +
                '}';
    }



}
