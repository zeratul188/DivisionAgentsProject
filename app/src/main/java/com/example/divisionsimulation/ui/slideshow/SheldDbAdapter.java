package com.example.divisionsimulation.ui.slideshow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class SheldDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_FIRST = "FIRST";
    public static final String KEY_SECOND = "SECOND";
    public static final String KEY_THIRD = "THIRD";
    public static final String KEY_CORE = "CORE";
    public static final String KEY_SUB = "SUB";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_VEST = "VEST";
    public static final String KEY_BACKPACK = "BACKPACK";
    public static final String KEY_IMAGE = "IMAGE";

    private static final String DATABASE_CREATE = "create table SHELD (_id integer primary key, "
            +"NAME text not null, FIRST text, SECOND text, THIRD text, CORE text not null, SUB text not null, "
            +"TYPE text not null, VEST text, BACKPACK text, IMAGE text);";

    private static final String DATABASE_NAME = "DIVISION_SHELD";
    private static final String DATABASE_TABLE = "SHELD";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public SheldDbAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS SHELD");
            onCreate(db);
        }
    }

    public SheldDbAdapter open() throws SQLException {
        myDBHelper = new DatabaseHelper(mCtx);
        sqlDB = myDBHelper.getWritableDatabase();
        return this;
    }

    public void databaseReset() {
        sqlDB.delete(DATABASE_TABLE, null, null);
    }

    public void close() {
        myDBHelper.close();
    }

    public long createWeapon(String name, String first, String second, String third, String core, String sub, String type, String vest, String backpack, String image) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_FIRST, first);
        values.put(KEY_SECOND, second);
        values.put(KEY_THIRD, third);
        values.put(KEY_CORE, core);
        values.put(KEY_SUB, sub);
        values.put(KEY_TYPE, type);
        values.put(KEY_VEST, vest);
        values.put(KEY_BACKPACK, backpack);
        values.put(KEY_IMAGE, image);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteWeapon(long rowId) {
        Log.i("Delete called.", "value___"+rowId);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null) > 0;
    }

    public Cursor fetchAllWeapon() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_FIRST, KEY_SECOND, KEY_THIRD, KEY_CORE, KEY_SUB, KEY_TYPE, KEY_VEST, KEY_BACKPACK, KEY_IMAGE}, null, null, null, null, null);
    }

    public Cursor fetchWeapon(long rowId) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_FIRST, KEY_SECOND, KEY_THIRD, KEY_CORE, KEY_SUB, KEY_TYPE, KEY_VEST, KEY_BACKPACK, KEY_IMAGE}, KEY_ROWID+"="+rowId, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchName(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_FIRST, KEY_SECOND, KEY_THIRD, KEY_CORE, KEY_SUB, KEY_TYPE, KEY_VEST, KEY_BACKPACK, KEY_IMAGE}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public int getAllCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int cnt = cursor.getCount();
        return cnt;
    }

    public boolean updateWeapon(long rowId, String name, String first, String second, String third, String core, String sub, String type, String vest, String backpack, String image) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_FIRST, first);
        values.put(KEY_SECOND, second);
        values.put(KEY_THIRD, third);
        values.put(KEY_CORE, core);
        values.put(KEY_SUB, sub);
        values.put(KEY_TYPE, type);
        values.put(KEY_VEST, vest);
        values.put(KEY_BACKPACK, backpack);
        values.put(KEY_IMAGE, image);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowId, null) > 0;
    }
}
