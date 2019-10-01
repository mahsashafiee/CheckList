package com.example.checklist.model.database.CursorWrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.model.database.DataBaseSchema.TaskDataBaseSchema;

import java.util.Date;
import java.util.UUID;

public class TaskCursorWrapper extends CursorWrapper {
    public TaskCursorWrapper(Cursor cursor) {
        super(cursor);
    }


    public Task getTaskWrapper() {

        String taskUUID = getString(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.TITLE));
        String description = getString(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.DESCRIPTION));
        Long longDate = getLong(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.DATE));
        State state= State.fromInt(getInt(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.STATE)));


        Date date = new Date(longDate);

        Task task = new Task(UUID.fromString(taskUUID));
        task.setTitle(title);
        task.setDescription(description);
        task.setDate(date);
        task.setState(state);

        return task;
    }
}
