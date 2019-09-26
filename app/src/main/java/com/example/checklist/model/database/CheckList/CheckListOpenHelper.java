package com.example.checklist.model.database.CheckList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.checklist.model.database.CheckList.DataBaseSchema.TaskDataBaseSchema.TaskTable;
import com.example.checklist.model.database.CheckList.DataBaseSchema.UserDataBaseSchema.UserTable;
import com.example.checklist.model.database.CheckList.DataBaseSchema.UserHasTaskDataBaseSchema.UserHasTaskTable;

import androidx.annotation.Nullable;

public class CheckListOpenHelper extends SQLiteOpenHelper {

    private static final String NAME = "checklist.db";
    private static final int VERSION = 1;

    private static final String CREATE_TABLE_TASK = "CREATE TABLE " + TaskTable.NAME +
            "(" +
            TaskTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TaskTable.Cols.UUID + ", " +
            TaskTable.Cols.TITLE + ", " +
            TaskTable.Cols.DESCRIPTION + ", " +
            TaskTable.Cols.DATE + ", " +
            TaskTable.Cols.STATE +
            " )";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + UserTable.NAME +
            "(" +
            UserTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            UserTable.Cols.UUID + ", " +
            UserTable.Cols.USERNAME + ", " +
            UserTable.Cols.PASSWORD +
            ")";

    private static final String CREATE_TABLE_USER_HAS_TASK = "CREATE TABLE " + UserHasTaskTable.NAME +
            "(" +
            UserHasTaskTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            UserHasTaskTable.Cols.TASK_UUID + ", " +
            UserHasTaskTable.Cols.USER_UUID +
            ")";


    public CheckListOpenHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_TASK);
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
        sqLiteDatabase.execSQL(CREATE_TABLE_USER_HAS_TASK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskTable.NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserTable.NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserHasTaskTable.NAME);

    }
}
