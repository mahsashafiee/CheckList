package com.example.checklist.repository;

import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Repository implements Serializable {

    private List<User> mUsers = new ArrayList<>();


    private static Repository instance;


    public static Repository getInstance(User user) {

        if (instance == null)
            instance = new Repository(user);

        return instance;
    }

    public List<Task> getTasks(UUID id, State state) {
        return getUser(id).getTasks(state);
    }

    public Task getSingleTask(UUID userId, UUID taskId ){
        return getUser(userId).getSingleTask(taskId);
    }

    private Repository(User user) {
        if (!mUsers.contains(user))
            mUsers.add(user);

    }

    public void addTask(UUID id, Task task) {
        getUser(id).addTask(task);
    }

    public void removeTasks(UUID userId){
        getUser(userId).removeTasks();
    }

    public void removeSingleTask(UUID id, UUID taskId) {
        getUser(id).removeSingleTask(taskId);
    }

    public User getUser(UUID id) {
        for (User user : mUsers)
            if (user.getID().equals(id))
                return user;
        return null;
    }



}

