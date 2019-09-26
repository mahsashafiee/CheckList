package com.example.checklist.model.database.CheckList.CursorWrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.checklist.model.State;
import com.example.checklist.model.Task;
import com.example.checklist.model.database.CheckList.DataBaseSchema.TaskDataBaseSchema;

import java.util.Date;
import java.util.UUID;

public class TaskCursorWrapper extends CursorWrapper {
    public TaskCursorWrapper(Cursor cursor) {
        super(cursor);
    }


    public Task getTaskWrapper() {

        String stringUUID = getString(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.TITLE));
        String description = getString(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.DESCRIPTION));
        Long longDate = getLong(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.DATE));
        State state;
        if (getString(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.STATE)).equals(State.DONE.toString()))
            state = State.DONE;
        else if (getString(getColumnIndex(TaskDataBaseSchema.TaskTable.Cols.STATE)).equals(State.DOING.toString()))
            state = State.DOING;
        else
            state = State.TODO;

        UUID uuid = UUID.fromString(stringUUID);
        Date date = new Date(longDate);

        Task task = new Task(uuid);
        task.setTitle(title);
        task.setDescription(description);
        task.setDate(date);
        task.setState(state);

        return task;
    }
}
