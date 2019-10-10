package com.example.checklist.greendao;

import android.content.Context;
import com.example.checklist.model.DaoMaster;

public class CheckListOpenHelper extends DaoMaster.OpenHelper {

    public static final String NAME = "checklist.db";

    public CheckListOpenHelper(Context context) {
        super(context, NAME);
    }

}
