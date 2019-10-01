package com.example.checklist.model.database.DataBaseSchema;

public class TaskDataBaseSchema {

    public static final class TaskTable {
        public static final String NAME = "task";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String UUID = "uuid";
            public static final String USER_UUID = "user_uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DESCRIPTION = "description";
            public static final String STATE = "state";
        }
    }
}
