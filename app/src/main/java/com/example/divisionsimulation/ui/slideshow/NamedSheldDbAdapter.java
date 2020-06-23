package com.example.divisionsimulation.ui.slideshow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class NamedSheldDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_BRAND = "BRAND";
    public static final String KEY_TALENT = "TALENT";
    public static final String KEY_CONTENT = "CONTENT";
    public static final String KEY_LOCATION = "LOCATION";
    public static final String KEY_TYPE = "TYPE";

    private static final String DATABASE_CREATE = "create table NAMEDSHELD (_id integer primary key, "
            +"NAME text not null, BRAND text, TALENT text not null, CONTENT text, "
            +"LOCATION text, TYPE text not null);";

    private static final String DATABASE_NAME = "DIVISION_NAMED_SHELD";
    private static final String DATABASE_TABLE = "NAMEDSHELD";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public NamedSheldDbAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS NAMEDSHELD");
            onCreate(db);
        }
    }

    public NamedSheldDbAdapter open() throws SQLException {
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

    public long createWeapon(String name, String brand, String talent, String content, String location, String type) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_BRAND, brand);
        values.put(KEY_TALENT, talent);
        values.put(KEY_CONTENT, content);
        values.put(KEY_LOCATION, location);
        values.put(KEY_TYPE, type);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteWeapon(long rowId) {
        Log.i("Delete called.", "value___"+rowId);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null) > 0;
    }

    public Cursor fetchAllWeapon() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_BRAND, KEY_TALENT, KEY_CONTENT, KEY_LOCATION, KEY_TYPE}, null, null, null, null, null);
    }

    public Cursor fetchWeapon(long rowId) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_BRAND, KEY_TALENT, KEY_CONTENT, KEY_LOCATION, KEY_TYPE}, KEY_ROWID+"="+rowId, null, null, null, null, null);
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

    public boolean updateWeapon(long rowId, String name, String brand, String talent, String content, String location, String type) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_BRAND, brand);
        values.put(KEY_TALENT, talent);
        values.put(KEY_CONTENT, content);
        values.put(KEY_LOCATION, location);
        values.put(KEY_TYPE, type);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowId, null) > 0;
    }
}
