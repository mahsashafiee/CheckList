package com.example.checklist.model.database.CheckList.CursorWrapper;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.checklist.model.User;
import com.example.checklist.model.database.CheckList.DataBaseSchema.UserDataBaseSchema;

import java.util.UUID;

public class UserCursorWrapper extends CursorWrapper {

    public UserCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public User getUserWrapper() {

        String stringUUID = getString(getColumnIndex(UserDataBaseSchema.UserTable.Cols.UUID));
        String username = getString(getColumnIndex(UserDataBaseSchema.UserTable.Cols.USERNAME));
        String password = getString(getColumnIndex(UserDataBaseSchema.UserTable.Cols.PASSWORD));

        UUID uuid = UUID.fromString(stringUUID);

        User user = new User(uuid);

        user.setUsername(username);
        user.setPassword(password);


        return user;
    }
}
