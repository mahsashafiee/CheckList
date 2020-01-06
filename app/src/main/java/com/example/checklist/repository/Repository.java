package com.example.checklist.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.checklist.greendao.CheckListOpenHelper;
import com.example.checklist.model.DaoMaster;
import com.example.checklist.model.DaoSession;
import com.example.checklist.model.TaskDao;
import com.example.checklist.model.UserDao;
import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.model.User;

import org.greenrobot.greendao.query.DeleteQuery;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Repository implements Serializable {

    private static Repository instance;
    private final DaoSession mDaoSession;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private TaskDao mTaskDao;
    private UserDao mUserDao;


    public static Repository getInstance(Context context) {

        if (instance == null)
            instance = new Repository(context);

        return instance;
    }

    public File getPhotoFile(User user) {
        return new File(mContext.getFilesDir(), user.getPhotoName());
    }

    private Repository(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CheckListOpenHelper(mContext).getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(mDatabase);
        mDaoSession = daoMaster.newSession();
        mTaskDao = mDaoSession.getTaskDao();
        mUserDao = mDaoSession.getUserDao();

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

    public void deleteTasks(Long id) {

        final DeleteQuery<Task> tableDeleteQuery = mDaoSession.queryBuilder(Task.class)
                .where(TaskDao.Properties.User_id.eq(id))
                .buildDelete();
        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
        mDaoSession.clear();
    }

    public void deleteTask(UUID taskId) {

        final DeleteQuery<Task> tableDeleteQuery = mDaoSession.queryBuilder(Task.class)
                .where(TaskDao.Properties.UUID.eq(taskId))
                .buildDelete();
        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
        mDaoSession.clear();
    }

    public void deleteUser(Long userId) {
        mDaoSession.queryBuilder(User.class)
                .where(UserDao.Properties._id.eq(userId))
                .buildDelete().executeDeleteWithoutDetachingEntities();

        mDaoSession.queryBuilder(Task.class)
                .where(TaskDao.Properties.User_id.eq(userId))
                .buildDelete().executeDeleteWithoutDetachingEntities();

        mDaoSession.clear();
    }

    public String getUsername(Long userId) {
        return mUserDao.queryBuilder().where(UserDao.Properties._id.eq(userId)).unique().getUsername();
    }

    public boolean isAdmin(Long userId) {
        return getUser(userId).getIsAdmin();
    }

    public List<Task> getTasks(Long id, State state) {
        return mTaskDao.queryBuilder()
                .where(TaskDao.Properties.User_id.eq(id), TaskDao.Properties.State.eq(state.getValue()))
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

    public User getUser(Long id) {
        return mUserDao.queryBuilder().where(UserDao.Properties._id.eq(id)).unique();
    }

    public int getUserTaskNumber(Long id) {
        return mTaskDao.queryBuilder().where(TaskDao.Properties.User_id.eq(id)).list().size();
    }

    public User getUser(String username) {
        return mUserDao.queryBuilder().where(UserDao.Properties.Username.eq(username)).unique();
    }

    public List<User> getUsers() {
        return mUserDao.loadAll();
    }

    public void deleteAll() {

        mUserDao.deleteAll();
        mTaskDao.deleteAll();
        mDaoSession.clear();

    }

}

