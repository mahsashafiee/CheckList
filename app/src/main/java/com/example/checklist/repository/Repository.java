package com.example.checklist.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.example.checklist.greendao.CheckListOpenHelper;
import com.example.checklist.model.DaoMaster;
import com.example.checklist.model.DaoSession;
import com.example.checklist.model.Hash;
import com.example.checklist.model.TaskDao;
import com.example.checklist.model.UserDao;
import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.model.User;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Repository implements Serializable {

    private static Repository instance;
    private final DaoSession daoSession;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private TaskDao mTaskDao;
    private UserDao mUserDao;


    public static Repository getInstance(Context context) {

        if (instance == null)
            instance = new Repository(context);

        return instance;
    }

    private Repository(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CheckListOpenHelper(mContext).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(mDatabase);
        daoSession = daoMaster.newSession();
        mTaskDao = daoSession.getTaskDao();
        mUserDao = daoSession.getUserDao();

        User user = new User();
        user.setUsername("admin");
        user.setPassword(Hash.MD5("123456"));
        if (getUser(user.getUsername()) == null)
            this.insertUser(user);

    }

    public void insertUser(User user) {
        mUserDao.insert(user);
    }

    public void insertTask(Task task) {
        mTaskDao.insert(task);
    }

    public void updateTask(Task task) {
        mTaskDao.update(task);
    }

    public void deleteTasks(UUID id) {

        final DeleteQuery<Task> tableDeleteQuery = daoSession.queryBuilder(Task.class)
                .where(TaskDao.Properties.UserId.eq(id.toString()))
                .buildDelete();
        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
        daoSession.clear();
    }

    public void deleteTask(UUID taskId) {

        final DeleteQuery<Task> tableDeleteQuery = daoSession.queryBuilder(Task.class)
                .where(TaskDao.Properties.UUID.eq(taskId))
                .buildDelete();
        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
        daoSession.clear();
    }

    public String getUsername(String userId) {

        return mUserDao.queryBuilder().where(UserDao.Properties.UserId.eq(userId)).unique().getUsername();

    }

    public List<Task> getTasks(UUID id, State state) {
        return mTaskDao.queryBuilder()
                .where(TaskDao.Properties.UserId.eq(id.toString()), TaskDao.Properties.State.eq(state.getValue()))
                .list();
    }

    public List<Task> getTasks(State state) {
        return mTaskDao.queryBuilder()
                .where(TaskDao.Properties.State.eq(state.getValue()))
                .list();
    }

    public Task getTask(UUID taskId) {

        return mTaskDao.queryBuilder().where(TaskDao.Properties.UUID.eq(taskId)).unique();
    }

    public User getUser(UUID id) {
        return mUserDao.queryBuilder().where(UserDao.Properties.Id.eq(id)).unique();
    }

    public User getUser(String username) {
        return mUserDao.queryBuilder().where(UserDao.Properties.Username.eq(username)).unique();
    }

    public List<User> getUsers() {
        return mUserDao.loadAll();
    }


}

