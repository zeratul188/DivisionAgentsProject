package com.example.divisionsimulation.ui.home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

import static android.content.ContentValues.TAG;

public class LoadoutDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_WEAPONDEMAGE = "WEAPONDEMAGE";
    public static final String KEY_RPM = "RPM";
    public static final String KEY_CRITICAL = "CRITICAL";
    public static final String KEY_CRITICALDEMAGE = "CRITICALDEMAGE";
    public static final String KEY_HEADSHOT = "HEADSHOT";
    public static final String KEY_HEADSHOTDEMAGE = "HEADSHOTDEMAGE";
    public static final String KEY_NOHIDE = "NOHIDE";
    public static final String KEY_ARMOR = "ARMOR";
    public static final String KEY_HEALTH = "HEALTH";
    public static final String KEY_RELOAD = "RELOAD";
    public static final String KEY_AMMO = "AMMO";
    public static final String KEY_AIM = "AIM";
    public static final String KEY_MAKE = "MAKE";

    private static final String DATABASE_CREATE = "create table LOADOUT (_id integer primary key, " +
            "WEAPONDEMAGE double not null, RPM double not null, CRITICAL double, CRITICALDEMAGE double, HEADSHOT double, HEADSHOTDEMAGE double, "+
            "NOHIDE double, ARMOR double, HEALTH double, RELOAD double, AMMO double not null, AIM double not null, MAKE text not null);";

    private static final String DATABASE_NAME = "DIVISION_LOADOUT";
    private static final String DATABASE_TABLE = "LOADOUT";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public LoadoutDBAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        Context mCtx = null;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.mCtx = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS LOADOUT");
            onCreate(db);
        }

    }

    public LoadoutDBAdapter open() throws SQLException {
        myDBHelper = new DatabaseHelper(mCtx);
        sqlDB = myDBHelper.getWritableDatabase();
        return this;
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public void databaseReset() {
        sqlDB.delete(DATABASE_TABLE, null, null);
    }

    public void close() {
        myDBHelper.close();
    }

    public long insertData(double weapondemage, double rpm, double critical, double critical_demage, double headshot, double headshot_demage, double nohide, double armor, double health, double reload, double ammo, double aim, String make) {
        ContentValues values = new ContentValues();
        values.put(KEY_WEAPONDEMAGE, weapondemage);
        values.put(KEY_RPM, rpm);
        values.put(KEY_CRITICAL, critical);
        values.put(KEY_CRITICALDEMAGE, critical_demage);
        values.put(KEY_HEADSHOT, headshot);
        values.put(KEY_HEADSHOTDEMAGE, headshot_demage);
        values.put(KEY_NOHIDE, nohide);
        values.put(KEY_ARMOR, armor);
        values.put(KEY_HEALTH, health);
        values.put(KEY_RELOAD, reload);
        values.put(KEY_AMMO, ammo);
        values.put(KEY_AIM, aim);
        values.put(KEY_MAKE, make);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(long rowID) {
        Log.i("Delete called.", "value___"+rowID);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean deleteAllData() {
        Log.i("Delete called.", "value___ALL Data");
        return sqlDB.delete(DATABASE_TABLE, null, null) > 0;
    }

    public Cursor fetchAllData() {
        Cursor cursor = sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_WEAPONDEMAGE, KEY_RPM, KEY_CRITICAL, KEY_CRITICALDEMAGE, KEY_HEADSHOT, KEY_HEADSHOTDEMAGE, KEY_NOHIDE, KEY_ARMOR, KEY_HEALTH, KEY_RELOAD, KEY_AMMO, KEY_AIM, KEY_MAKE}, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchData(long rowID) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_WEAPONDEMAGE, KEY_RPM, KEY_CRITICAL, KEY_CRITICALDEMAGE, KEY_HEADSHOT, KEY_HEADSHOTDEMAGE, KEY_NOHIDE, KEY_ARMOR, KEY_HEALTH, KEY_RELOAD, KEY_AMMO, KEY_AIM, KEY_MAKE}, KEY_ROWID+"="+rowID, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public long getRowID(String make) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_WEAPONDEMAGE, KEY_RPM, KEY_CRITICAL, KEY_CRITICALDEMAGE, KEY_HEADSHOT, KEY_HEADSHOTDEMAGE, KEY_NOHIDE, KEY_ARMOR, KEY_HEALTH, KEY_RELOAD, KEY_AMMO, KEY_AIM, KEY_MAKE}, KEY_MAKE+"='"+make+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getLong(0);
    }

    public boolean updateData(long rowID, double weapondemage, double rpm, double critical, double critical_demage, double headshot, double headshot_demage, double nohide, double armor, double health, double reload, double ammo, double aim, String make) {
        ContentValues values = new ContentValues();
        values.put(KEY_WEAPONDEMAGE, weapondemage);
        values.put(KEY_RPM, rpm);
        values.put(KEY_CRITICAL, critical);
        values.put(KEY_CRITICALDEMAGE, critical_demage);
        values.put(KEY_HEADSHOT, headshot);
        values.put(KEY_HEADSHOTDEMAGE, headshot_demage);
        values.put(KEY_NOHIDE, nohide);
        values.put(KEY_ARMOR, armor);
        values.put(KEY_HEALTH, health);
        values.put(KEY_RELOAD, reload);
        values.put(KEY_AMMO, ammo);
        values.put(KEY_AIM, aim);
        values.put(KEY_MAKE, make);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }
}
