package com.xuwanjin.inchoate.database.dao.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xuwanjin.inchoate.model.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class GreenDaoDBHelper extends DaoMaster.OpenHelper {
    public GreenDaoDBHelper(Context context, String name) {
        super(context, name);
    }

    public GreenDaoDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        for (int migrateVersion = oldVersion +1; migrateVersion <= newVersion; migrateVersion++){
            upgrade(db, migrateVersion);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
    public void upgrade(Database database, int migrateVersion){

    }
}
