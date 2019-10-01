package com.example.checklist.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.checklist.model.database.DataBaseSchema.TaskDataBaseSchema.TaskTable;
import com.example.checklist.model.database.DataBaseSchema.UserDataBaseSchema.UserTable;

import androidx.annotation.Nullable;

public class CheckListOpenHelper extends SQLiteOpenHelper {

    private static final String NAME = "checklist.db";
    private static final int VERSION = 1;

    private static final String CREATE_TABLE_TASK = "CREATE TABLE " + TaskTable.NAME +
            "(" +
            TaskTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TaskTable.Cols.UUID + " NOT NULL, " +
            TaskTable.Cols.USER_UUID + " NOT NULL ," +
            TaskTable.Cols.TITLE + " NOT NULL, " +
            TaskTable.Cols.DESCRIPTION + " NOT NULL, " +
            TaskTable.Cols.DATE + " NOT NULL, " +
            TaskTable.Cols.STATE + " NOT NULL " +
            " )";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + UserTable.NAME +
            "(" +
            UserTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            UserTable.Cols.UUID + ", " +
            UserTable.Cols.USERNAME + ", " +
            UserTable.Cols.PASSWORD +
            " ,FOREIGN KEY (" + UserTable.Cols.UUID+ ") REFERENCES " + TaskTable.NAME + "(" + TaskTable.Cols.USER_UUID +"))";



    public CheckListOpenHelper(@Nullable Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_TABLE_TASK);
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskTable.NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserTable.NAME);

    }
}
