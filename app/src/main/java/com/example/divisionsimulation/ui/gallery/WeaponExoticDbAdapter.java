package com.example.divisionsimulation.ui.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class WeaponExoticDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_OPTION = "OPTION";
    public static final String KEY_RPM = "RPM";
    public static final String KEY_RELOADTIME = "RELOADTIME";
    public static final String KEY_MAG = "MAG";
    public static final String KEY_FIREMETHOD = "FIREMETHOD";
    public static final String KEY_ITEM = "ITEM";
    public static final String KEY_TALENT = "TALENT";
    public static final String KEY_TALENTCONTENT = "TALENTCONTENT";
    public static final String KEY_DROPED = "DROPED";
    public static final String KEY_CONTENT = "CONTENT";
    public static final String KEY_TYPE = "TYPE";

    private static final String DATABASE_CREATE = "create table EXOTICWEAPON (_id integer primary key, "
            +"NAME text not null, OPTION text not null, "
            +"RPM text not null, RELOADTIME text not null, MAG text not null, "
            +"FIREMETHOD text not null, ITEM text not null, TALENT text not null, "
            +"TALENTCONTENT text not null, DROPED text not null, CONTENT text, TYPE text not null);";

    private static final String DATABASE_NAME = "DIVISION_EXOTIC_WEAPON";
    private static final String DATABASE_TABLE = "EXOTICWEAPON";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public WeaponExoticDbAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS EXOTICWEAPON");
            onCreate(db);
        }
    }

    public WeaponExoticDbAdapter open() throws SQLException {
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

    public long createWeapon(String name, String option, String rpm, String reloadtime, String mag, String firemethod, String item, String talent, String talentcontent, String droped, String content, String type) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_OPTION, option);
        values.put(KEY_RPM, rpm);
        values.put(KEY_RELOADTIME, reloadtime);
        values.put(KEY_MAG, mag);
        values.put(KEY_FIREMETHOD, firemethod);
        values.put(KEY_ITEM, item);
        values.put(KEY_TALENT, talent);
        values.put(KEY_TALENTCONTENT, talentcontent);
        values.put(KEY_DROPED, droped);
        values.put(KEY_CONTENT, content);
        values.put(KEY_TYPE, type);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteWeapon(long rowId) {
        Log.i("Delete called.", "value___"+rowId);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null) > 0;
    }

    public Cursor fetchAllWeapon() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_OPTION, KEY_RPM, KEY_RELOADTIME, KEY_MAG, KEY_FIREMETHOD, KEY_ITEM, KEY_TALENT, KEY_TALENTCONTENT, KEY_DROPED, KEY_CONTENT, KEY_TYPE}, null, null, null, null, null);
    }

    public Cursor fetchWeapon(long rowId) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_OPTION, KEY_RPM, KEY_RELOADTIME, KEY_MAG, KEY_FIREMETHOD, KEY_ITEM, KEY_TALENT, KEY_TALENTCONTENT, KEY_DROPED, KEY_CONTENT, KEY_TYPE}, KEY_ROWID+"="+rowId, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchNeedWeapon() throws SQLException{
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_NAME, KEY_TYPE}, null, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchNameWeapon(String name) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_OPTION, KEY_RPM, KEY_RELOADTIME, KEY_MAG, KEY_FIREMETHOD, KEY_ITEM, KEY_TALENT, KEY_TALENTCONTENT, KEY_DROPED, KEY_CONTENT, KEY_TYPE}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateWeapon(long rowId, String name, String option, String rpm, String reloadtime, String mag, String firemethod, String item, String talent, String talentcontent, String droped, String content, String type) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_OPTION, option);
        values.put(KEY_RPM, rpm);
        values.put(KEY_RELOADTIME, reloadtime);
        values.put(KEY_MAG, mag);
        values.put(KEY_FIREMETHOD, firemethod);
        values.put(KEY_ITEM, item);
        values.put(KEY_TALENT, talent);
        values.put(KEY_TALENTCONTENT, talentcontent);
        values.put(KEY_DROPED, droped);
        values.put(KEY_CONTENT, content);
        values.put(KEY_TYPE, type);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowId, null) > 0;
    }
}
