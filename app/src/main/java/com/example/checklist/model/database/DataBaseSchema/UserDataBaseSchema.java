package com.example.checklist.model.database.DataBaseSchema;

public class UserDataBaseSchema {

    public static final class UserTable {
        public static final String NAME = "user";

        public static final class Cols {
            public static final String ID = "_id";
            public static final String UUID = "uuid";
            public static final String USERNAME = "username";
            public static final String PASSWORD = "password";
        }
    }
}
