package com.example.checklist.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import com.example.checklist.model.database.CheckList.CursorWrapper.UserCursorWrapper;
import com.example.checklist.model.database.CheckList.DataBaseSchema.UserDataBaseSchema.UserTable;
import com.example.checklist.model.database.CheckList.DataBaseSchema.TaskDataBaseSchema.TaskTable;
import com.example.checklist.model.database.CheckList.DataBaseSchema.UserHasTaskDataBaseSchema.UserHasTaskTable;
import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.model.User;
import com.example.checklist.model.database.CheckList.CheckListOpenHelper;

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

    public List<Task> getTasks(UUID id, State state) {
        return getUser(id).getTasks(state);
    }

    public Task getSingleTask(UUID userId, UUID taskId) {
        return getUser(userId).getSingleTask(taskId);
    }

    private Repository(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CheckListOpenHelper(mContext).getWritableDatabase();

    }


    public void insertUser(User user) {
        ContentValues values = getUserContentValues(user);
        mDatabase.insert(UserTable.NAME, null, values);
    }

    public void addTask(UUID id, Task task) {
        ContentValues taskValues = getTaskContentValues(task);
        ContentValues userHasTaskValues = getUserHasTaskContentValues(id, task.getID());
        mDatabase.insert(TaskTable.NAME, null, taskValues);
        mDatabase.insert(UserHasTaskTable.NAME, null, userHasTaskValues);
    }

    public void removeTasks(UUID userId) {
        getUser(userId).removeTasks();
    }

    public void removeSingleTask(UUID id, UUID taskId) {
        getUser(id).removeSingleTask(taskId);
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

    private CursorWrapper queryGetTask(String userId, String State){

            String selectQuery = "SELECT " + TaskTable.Cols.UUID + ", " +
                    TaskTable.Cols.TITLE + ", " +
                    TaskTable.Cols.DESCRIPTION + ", " +
                    TaskTable.Cols.DATE + ", " +
                    TaskTable.Cols.STATE + " FROM " +
                    UserHasTaskTable.NAME + " INNER JOIN " +
                    TaskTable.NAME + " ON " +
                    UserTable.Cols.UUID + " = " + UserHasTaskTable.Cols.USER_UUID ;/*+
                    " INNER JOIN " + TaskTable.NAME + " ON " +*/

            Cursor cursor = mDatabase.rawQuery(selectQuery, null);


            return null;
    }

    private ContentValues getUserContentValues(User user) {

        ContentValues values = new ContentValues();

        values.put(UserTable.Cols.UUID, user.getID().toString());
        values.put(UserTable.Cols.USERNAME, user.getUsername());
        values.put(UserTable.Cols.PASSWORD, user.getPassword());

        return values;
    }

    private ContentValues getTaskContentValues(Task task) {

        ContentValues values = new ContentValues();

        values.put(TaskTable.Cols.UUID, task.getID().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DESCRIPTION, task.getDescription());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.STATE, task.getState().toString());

        return values;
    }

    private ContentValues getUserHasTaskContentValues(UUID userId, UUID taskId) {

        ContentValues values = new ContentValues();

        values.put(UserHasTaskTable.Cols.TASK_UUID, taskId.toString());
        values.put(UserHasTaskTable.Cols.USER_UUID, userId.toString());

        return values;
    }

}

