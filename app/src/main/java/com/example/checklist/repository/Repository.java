package com.example.checklist.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.example.checklist.model.Hash;
import com.example.checklist.model.database.CursorWrapper.TaskCursorWrapper;
import com.example.checklist.model.database.CursorWrapper.UserCursorWrapper;
import com.example.checklist.model.database.DataBaseSchema.UserDataBaseSchema.UserTable;
import com.example.checklist.model.database.DataBaseSchema.TaskDataBaseSchema.TaskTable;
import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.model.User;
import com.example.checklist.model.database.CheckListOpenHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Repository implements Serializable {

    private static Repository instance;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static Repository getInstance(Context context) {

        if (instance == null)
            instance = new Repository(context);

        return instance;
    }

    private Repository(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CheckListOpenHelper(mContext).getWritableDatabase();
        User user = new User("admin", Hash.MD5("123456"));
        this.insertUser(user);

    }

    public void insertUser(User user) {
        ContentValues values = getUserContentValues(user);
        mDatabase.insert(UserTable.NAME, null, values);
    }

    public void insertTask(UUID id, Task task) {
        ContentValues taskValues = getTaskContentValues(id, task);
        mDatabase.insert(TaskTable.NAME, null, taskValues);
    }

    public void updateTask(Task task) {
        ContentValues taskValues = getTaskContentValues(task);
        mDatabase.update(TaskTable.NAME, taskValues, TaskTable.Cols.UUID + " = ?", new String[]{task.getID().toString()});
    }

    public void deleteTasks(UUID userId) {
        mDatabase.delete(TaskTable.NAME, TaskTable.Cols.USER_UUID + " = ?", new String[]{userId.toString()});
    }

    public void deleteTask(UUID taskId) {
        mDatabase.delete(TaskTable.NAME, TaskTable.Cols.UUID + " = ?", new String[]{taskId.toString()});
    }

    public UUID getUsername(UUID taskId){

        TaskCursorWrapper cursor = (TaskCursorWrapper) queryTasks(
                new String[]{TaskTable.Cols.USER_UUID},
                TaskTable.Cols.UUID + " = ?",
                new String[]{taskId.toString()});
        try {
            cursor.moveToFirst();

            if (cursor == null || cursor.getCount() == 0)
                return null;
            return cursor.getUserId();
        } finally {
            cursor.close();
        }
    }

    public List<Task> getTasks(UUID userId, State state) {
        List<Task> tasks = new ArrayList<>();

        TaskCursorWrapper cursor = (TaskCursorWrapper) queryGetTasks(userId, state);

        try {
            cursor.moveToFirst();

            if (cursor == null || cursor.getCount() == 0)
                return null;

            while (!cursor.isAfterLast()) {

                tasks.add(cursor.getTaskWrapper());

                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }

        return tasks;
    }

    public List<Task> getTasks(State state) {
        List<Task> tasks = new ArrayList<>();

        TaskCursorWrapper cursor = (TaskCursorWrapper) queryGetTasks(state);

        try {
            cursor.moveToFirst();

            if (cursor == null || cursor.getCount() == 0)
                return null;

            while (!cursor.isAfterLast()) {

                tasks.add(cursor.getTaskWrapper());

                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }

        return tasks;
    }

    public Task getTask(UUID taskId) {
        TaskCursorWrapper cursor = (TaskCursorWrapper) queryTasks(
                null,
                TaskTable.Cols.UUID + " = ?",
                new String[]{taskId.toString()});
        try {
            cursor.moveToFirst();

            if (cursor == null || cursor.getCount() == 0)
                return null;
            return cursor.getTaskWrapper();
        } finally {
            cursor.close();
        }

    }

    public User getUser(UUID id) {
        UserCursorWrapper cursor = (UserCursorWrapper) queryUsers(
                null,
                UserTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        try {
            cursor.moveToFirst();

            if (cursor == null || cursor.getCount() == 0)
                return null;
            return cursor.getUserWrapper();
        } finally {
            cursor.close();
        }
    }

    public User getUser(String username) {
        UserCursorWrapper cursor = (UserCursorWrapper) queryUsers(
                null,
                UserTable.Cols.USERNAME + " = ?",
                new String[]{username});
        try {
            cursor.moveToFirst();

            if (cursor == null || cursor.getCount() == 0)
                return null;
            return cursor.getUserWrapper();
        } finally {
            cursor.close();
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        UserCursorWrapper cursor = (UserCursorWrapper) queryUsers(
                null,
                null,
                null);

        try {
            cursor.moveToFirst();

            if (cursor == null || cursor.getCount() == 0)
                return null;

            while (!cursor.isAfterLast()) {

                users.add(cursor.getUserWrapper());

                cursor.moveToNext();
            }

        } finally {
            cursor.close();
        }

        return users;
    }

    private CursorWrapper queryUsers(String[] columns, String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(UserTable.NAME,
                columns,
                where,
                whereArgs,
                null,
                null,
                null);

        return new UserCursorWrapper(cursor);
    }

    private CursorWrapper queryTasks(String[] columns, String where, String[] whereArgs) {
        Cursor cursor = mDatabase.query(TaskTable.NAME,
                columns,
                where,
                whereArgs,
                null,
                null,
                null);

        return new TaskCursorWrapper(cursor);
    }

    private CursorWrapper queryGetTasks(State state) {

        String selectQuery = "SELECT " + TaskTable.NAME + "." + TaskTable.Cols.UUID + ", " +
                TaskTable.Cols.TITLE + ", " +
                TaskTable.Cols.DESCRIPTION + ", " +
                TaskTable.Cols.DATE + ", " +
                TaskTable.Cols.STATE + " FROM " +
                TaskTable.NAME + " JOIN " +
                UserTable.NAME + " ON " +
                TaskTable.NAME + "." + TaskTable.Cols.USER_UUID + " = " + UserTable.NAME + "." + UserTable.Cols.UUID + " WHERE " + TaskTable.Cols.STATE + "=" + state.getValue();

        Cursor cursor = mDatabase.rawQuery(selectQuery, null);


        return new TaskCursorWrapper(cursor);
    }

    private CursorWrapper queryGetTasks(UUID userId, State state) {

        String selectQuery = "SELECT * FROM " + TaskTable.NAME + " WHERE " +
                TaskTable.Cols.USER_UUID + "=\"" + userId.toString() + "\" AND " +
                TaskTable.Cols.STATE + "=" + state.getValue();

        Cursor cursor = mDatabase.rawQuery(selectQuery, null);


        return new TaskCursorWrapper(cursor);
    }

    private ContentValues getUserContentValues(User user) {

        ContentValues values = new ContentValues();

        values.put(UserTable.Cols.UUID, user.getID().toString());
        values.put(UserTable.Cols.USERNAME, user.getUsername());
        values.put(UserTable.Cols.PASSWORD, user.getPassword());

        return values;
    }

    private ContentValues getTaskContentValues(UUID userId, Task task) {

        ContentValues values = new ContentValues();

        values.put(TaskTable.Cols.UUID, task.getID().toString());
        values.put(TaskTable.Cols.USER_UUID, userId.toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DESCRIPTION, task.getDescription());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.STATE, task.getState().getValue());

        return values;
    }

    private ContentValues getTaskContentValues(Task task) {

        ContentValues values = new ContentValues();

        values.put(TaskTable.Cols.UUID, task.getID().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DESCRIPTION, task.getDescription());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.STATE, task.getState().getValue());

        return values;
    }


}

