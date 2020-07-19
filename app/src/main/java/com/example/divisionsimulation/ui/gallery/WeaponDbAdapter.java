package com.example.divisionsimulation.ui.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class WeaponDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_DEMAGE = "DEMAGE";
    public static final String KEY_RPM = "RPM";
    public static final String KEY_MAG = "MAG";
    public static final String KEY_RELOADTIME = "RELOADTIME";
    public static final String KEY_FIRE_METHOD = "FIRE_METHOD";
    public static final String KEY_MODE = "MODE";
    public static final String KEY_VARIATION = "VARIATION";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_CONTENT = "CONTENT";

    private static final String DATABASE_CREATE = "create table WEAPON (_id integer primary key, "
            +"NAME text not null, DEMAGE text not null, "
            +"RPM text not null, MAG text not null, "
            +"RELOADTIME text not null, FIRE_METHOD text, "
            +"MODE text, VARIATION text, TYPE text not null, "
            +"CONTENT text);";

    private static final String DATABASE_NAME = "DIVISION_WEAPON";
    private static final String DATABASE_TABLE = "WEAPON";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public WeaponDbAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS WEAPON");
            onCreate(db);
        }
    }

    public WeaponDbAdapter open() throws SQLException {
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

    public long createWeapon(String name, String demage, String rpm, String mag, String reload_time, String fire_method, String mode, String variation, String type, String content) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DEMAGE, demage);
        values.put(KEY_RPM, rpm);
        values.put(KEY_MAG, mag);
        values.put(KEY_RELOADTIME, reload_time);
        values.put(KEY_FIRE_METHOD, fire_method);
        values.put(KEY_MODE, mode);
        values.put(KEY_VARIATION, variation);
        values.put(KEY_TYPE, type);
        values.put(KEY_CONTENT, content);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteWeapon(long rowId) {
        Log.i("Delete called.", "value___"+rowId);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null) > 0;
    }

    public Cursor fetchAllWeapon() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DEMAGE, KEY_RPM, KEY_MAG, KEY_RELOADTIME, KEY_FIRE_METHOD, KEY_MODE, KEY_VARIATION, KEY_TYPE, KEY_CONTENT}, null, null, null, null, null);
    }

    public Cursor fetchTypeWeapon(String type) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DEMAGE, KEY_RPM, KEY_MAG, KEY_RELOADTIME, KEY_FIRE_METHOD, KEY_MODE, KEY_VARIATION, KEY_TYPE, KEY_CONTENT}, KEY_TYPE+"='"+type+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchNameWeapon(String name) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DEMAGE, KEY_RPM, KEY_MAG, KEY_RELOADTIME, KEY_FIRE_METHOD, KEY_MODE, KEY_VARIATION, KEY_TYPE, KEY_CONTENT}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchWeapon(long rowId) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DEMAGE, KEY_RPM, KEY_MAG, KEY_RELOADTIME, KEY_FIRE_METHOD, KEY_MODE, KEY_VARIATION, KEY_TYPE, KEY_CONTENT}, KEY_ROWID+"="+rowId, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount(String type) {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+" where TYPE = '"+type+"';", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public int getAllCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int cnt = cursor.getCount();
        return cnt;
    }

    public boolean updateWeapon(long rowId, String name, String demage, String rpm, String mag, String reload_time, String fire_method, String mode, String variation, String type, String content) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DEMAGE, demage);
        values.put(KEY_RPM, rpm);
        values.put(KEY_MAG, mag);
        values.put(KEY_RELOADTIME, reload_time);
        values.put(KEY_FIRE_METHOD, fire_method);
        values.put(KEY_MODE, mode);
        values.put(KEY_VARIATION, variation);
        values.put(KEY_TYPE, type);
        values.put(KEY_CONTENT, content);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowId, null) > 0;
    }
}
