package com.example.checklist.model;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.checklist.greendao.StateConverter;
import com.example.checklist.greendao.UUIDConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "task")
public class Task {

    @Id(autoincrement = true)
    private Long _id;

    @Property(nameInDb = "user_id")
    private Long user_id;

    @Property(nameInDb = "title")
    private String title;

    @Property(nameInDb = "state")
    @Convert(converter = StateConverter.class, columnType = Integer.class)
    private State state;

    @Property(nameInDb = "task_uuid")
    @Index(unique = true)
    @Convert(converter = UUIDConverter.class, columnType = String.class)
    private UUID UUID;

    @Property(nameInDb = "date")
    private Date date;

    @Property(nameInDb = "description")
    private String description;

    @Transient
    private SimpleDateFormat mFormat;


    public Task(Long userId) {
        this.user_id = userId;
        UUID = java.util.UUID.randomUUID();
        date = new Date();
    }


    @Generated(hash = 1365658026)
    public Task(Long _id, Long user_id, String title, State state, UUID UUID,
            Date date, String description) {
        this._id = _id;
        this.user_id = user_id;
        this.title = title;
        this.state = state;
        this.UUID = UUID;
        this.date = date;
        this.description = description;
    }


    @Generated(hash = 733837707)
    public Task() {
    }


    public String getSimpleDate() {
        mFormat = new SimpleDateFormat("dd MMM yyyy");
        return mFormat.format(date);

    }

    public String getSimpleTime() {
        mFormat = new SimpleDateFormat("hh:mm a");
        return mFormat.format(date);

    }


    public Long get_id() {
        return this._id;
    }


    public void set_id(Long _id) {
        this._id = _id;
    }


    public Long getUser_id() {
        return this.user_id;
    }


    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }


    public String getTitle() {
        return this.title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public State getState() {
        return this.state;
    }


    public void setState(State state) {
        this.state = state;
    }


    public UUID getUUID() {
        return this.UUID;
    }


    public void setUUID(UUID UUID) {
        this.UUID = UUID;
    }


    public Date getDate() {
        return this.date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public String getDescription() {
        return this.description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


}
