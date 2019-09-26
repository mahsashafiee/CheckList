package com.example.checklist.model.database.CheckList.DataBaseSchema;

public class UserHasTaskDataBaseSchema {

    public static final class UserHasTaskTable {
        public static final String NAME = "user_has_task";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String TASK_UUID = "task_uuid";
            public static final String USER_UUID = "user_uuid";
        }
    }
}
