package com.example.checklist.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Task implements Serializable {
    private String mTitle;
    private State mState;
    private UUID mUUID;
    private Date mDate;
    private String mDescription /*"Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit. There is no one who loves pain itself, who seeks after it and wants to have it."*/;
    private SimpleDateFormat mFormat;

    public Task(UUID UUID) {
        mUUID = UUID;
    }

    public String getDescription() {
        return mDescription;
    }


    public void setDescription(String description) {
        mDescription = description;
    }

    public Task() {
        mUUID = UUID.randomUUID();
        mDate = new Date();
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        mState = state;
    }

    public UUID getID() {
        return mUUID;
    }

    @NonNull
    @Override
    public String toString() {
        return "Task{" +
                "mTitle='" + mTitle + '\'' +
                ", mState=" + mState +
                '}';
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(mUUID, task.mUUID);
    }

    public String getSimpleDate(){
        mFormat = new SimpleDateFormat("dd MMM yyyy");
        return mFormat.format(mDate);

    }
    public String getSimpleTime(){
        mFormat = new SimpleDateFormat("hh:mm a");
        return mFormat.format(mDate);

    }

}
