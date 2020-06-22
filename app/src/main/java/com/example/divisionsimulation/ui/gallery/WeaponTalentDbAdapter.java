package com.example.divisionsimulation.ui.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class WeaponTalentDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_CONTENT = "CONTENT";
    public static final String KEY_AR = "AR"; //돌격소총
    public static final String KEY_SR = "SR"; //기관단총
    public static final String KEY_BR = "BR"; //경기관총
    public static final String KEY_RF = "RF"; //소총
    public static final String KEY_MMG = "MMG"; //지정사수소총
    public static final String KEY_SG = "SG"; //산탄총
    public static final String KEY_PT = "PT"; //권총


    private static final String DATABASE_CREATE = "create table WEAPONTALENT (_id integer primary key, "
            +"NAME text not null, CONTENT text not null, "
            +"AR text, SR text, BR text, "
            +"RF text, MMG text, SG text, PT text);";

    private static final String DATABASE_NAME = "DIVISION_WEAPON_TALENT";
    private static final String DATABASE_TABLE = "WEAPONTALENT";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public WeaponTalentDbAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS WEAPONTALENT");
            onCreate(db);
        }
    }

    public WeaponTalentDbAdapter open() throws SQLException {
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

    public long createWeapon(String name, String content, String ar, String sr, String br, String rf, String mmg, String sg, String pt) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_CONTENT, content);
        values.put(KEY_AR, ar);
        values.put(KEY_SR, sr);
        values.put(KEY_BR, br);
        values.put(KEY_RF, rf);
        values.put(KEY_MMG, mmg);
        values.put(KEY_SG, sg);
        values.put(KEY_PT, pt);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteWeapon(long rowId) {
        Log.i("Delete called.", "value___"+rowId);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null) > 0;
    }

    public Cursor fetchAllWeapon() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMG, KEY_SG, KEY_PT}, null, null, null, null, null);
    }

    public Cursor fetchWeapon(long rowId) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMG, KEY_SG, KEY_PT}, KEY_ROWID+"="+rowId, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateWeapon(long rowId, String name, String content, String ar, String sr, String br, String rf, String mmg, String sg, String pt) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_CONTENT, content);
        values.put(KEY_AR, ar);
        values.put(KEY_SR, sr);
        values.put(KEY_BR, br);
        values.put(KEY_RF, rf);
        values.put(KEY_MMG, mmg);
        values.put(KEY_SG, sg);
        values.put(KEY_PT, pt);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowId, null) > 0;
    }
}
