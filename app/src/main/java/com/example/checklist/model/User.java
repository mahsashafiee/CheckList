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
    private List<Task> mTaskToDo = new ArrayList<>();
    private List<Task> mTaskDone = new ArrayList<>();
    private List<Task> mTaskDoing = new ArrayList<>();

    public User() {
        mId = UUID.randomUUID();

    }

    public User(String username, String password) {

        this();
        mUsername = username;
        mPassword = password;
    }

    public User(UUID id) {
        this();
        mId = id;
    }

    public List<Task> getTaskDone() {
        return mTaskDone;
    }

    public void setTaskDone(List<Task> taskDone) {
        mTaskDone = taskDone;
    }

    public List<Task> getTaskDoing() {
        return mTaskDoing;
    }

    public void setTaskDoing(List<Task> taskDoing) {
        mTaskDoing = taskDoing;
    }

    public List<Task> getTaskToDo() {
        return mTaskToDo;
    }

    public void setTaskToDo(List<Task> taskToDo) {
        mTaskToDo = taskToDo;
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

    public Task getSingleTask(UUID taskId) {

        for (Task t : mTaskDone) {
            if (t.getID().equals(taskId))
                return t;
        }
        for (Task t : mTaskDoing) {
            if (t.getID().equals(taskId))
                return t;
        }
        for (Task t : mTaskToDo) {
            if (t.getID().equals(taskId))
                return t;
        }
        return null;
    }

    public List<Task> getTasks(State state) {

        if (state.equals(State.TODO))
            return mTaskToDo;
        else if (state.equals(State.DONE))
            return mTaskDone;
        else
            return mTaskDoing;


    }

    public void removeSingleTask(UUID taskId) {

        for (Task t : mTaskToDo) {
            if (t.getID().equals(taskId)) {
                mTaskToDo.remove(t);
                return;
            }
        }
        for (Task t : mTaskDone) {
            if (t.getID().equals(taskId)) {
                mTaskDone.remove(t);
                return;
            }
        }
        for (Task t : mTaskDoing) {
            if (t.getID().equals(taskId)) {
                mTaskDoing.remove(t);
                return;
            }
        }

    }

    public void removeTasks(){
        mTaskDoing.clear();
        mTaskDone.clear();
        mTaskToDo.clear();
    }


    public void addTask(Task task) {

        this.removeSingleTask(task.getID());

        if (task.getState().equals(State.DONE)) {
            addTaskDone(task);
            return;
        } else if (task.getState().equals(State.DOING)) {
            addTaskDoing(task);
            return;
        } else
            addTaskToDo(task);
        return;
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


    private void addTaskToDo(Task task) {
        if (!mTaskToDo.contains(task)) {
            task.setState(State.TODO);
            mTaskToDo.add(task);
        }
    }

    private void addTaskDone(Task task) {
        if (!mTaskDone.contains(task)) {
            task.setState(State.DONE);
            mTaskDone.add(task);
        }
    }

    private void addTaskDoing(Task task) {
        if (!mTaskDoing.contains(task)) {
            task.setState(State.DOING);
            mTaskDoing.add(task);
        }
    }

}
