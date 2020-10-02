package com.example.divisionsimulation.dbdatas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.divisionsimulation.ui.share.WeaponItem;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

import static android.content.ContentValues.TAG;

public class InventoryDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_CORE1 = "CORE1";
    public static final String KEY_CORE2 = "CORE2";
    public static final String KEY_SUB1 = "SUB1";
    public static final String KEY_SUB2 = "SUB2";
    public static final String KEY_CORE1VALUE = "CORE1VALUE";
    public static final String KEY_CORE2VALUE = "CORE2VALUE";
    public static final String KEY_SUB1VALUE = "SUB1VALUE";
    public static final String KEY_SUB2VALUE = "SUB2VALUE";
    public static final String KEY_TALENT = "TALENT";
    public static final String KEY_EDIT1 = "EDIT1";
    public static final String KEY_EDIT2 = "EDIT2";
    public static final String KEY_EDIT3 = "EDIT3";
    public static final String KEY_TALENTEDIT = "TALENTEDIT";
    public static final String KEY_FAVORITE = "FAVORITE";
    public static final String KEY_NEW = "NEW";

    private String[] weapon_type = {"돌격소총", "소총", "지정사수소총", "산탄총", "기관단총", "경기관총"};

    private static final String DATABASE_CREATE = "create table INVENTORY (_id integer primary key, " +
            "NAME text not null, TYPE text not null, CORE1 text, CORE2 text, SUB1 text, SUB2 text, "+
            "CORE1VALUE double, CORE2VALUE double, SUB1VALUE double, SUB2VALUE double, "+
            "TALENT text not null, EDIT1 text not null, EDIT2 text not null, EDIT3 text not null, TALENTEDIT text not null, FAVORITE integer not null DEFAULT 0, NEW integer not null DEFAULT 0);";

    private static final String DATABASE_NAME = "DIVISION_INVENTORY";
    private static final String DATABASE_TABLE = "INVENTORY";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public InventoryDBAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS INVENTORY");
            onCreate(db);
        }
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public InventoryDBAdapter open() throws SQLException {
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

    public long insertWeaponData(String name, String type, String core1, String core2, String sub1, double core1_value, double core2_value, double sub1_value, String talent) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_CORE1, core1);
        values.put(KEY_CORE2, core2);
        values.put(KEY_SUB1, sub1);
        values.put(KEY_SUB2, "-");
        values.put(KEY_CORE1VALUE, core1_value);
        values.put(KEY_CORE2VALUE, core2_value);
        values.put(KEY_SUB1VALUE, sub1_value);
        values.put(KEY_SUB2VALUE, 0.0);
        values.put(KEY_TALENT, talent);
        values.put(KEY_EDIT1, "false");
        values.put(KEY_EDIT2, "false");
        values.put(KEY_EDIT3, "false");
        values.put(KEY_TALENTEDIT, "false");
        values.put(KEY_FAVORITE, 0);
        values.put(KEY_NEW, 1);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public long insertSheldData(String name, String type, String core1, String sub1, String sub2, double core1_value, double sub1_value, double sub2_value, String talent) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_CORE1, core1);
        values.put(KEY_CORE2, "-");
        values.put(KEY_SUB1, sub1);
        values.put(KEY_SUB2, sub2);
        values.put(KEY_CORE1VALUE, core1_value);
        values.put(KEY_CORE2VALUE, 0.0);
        values.put(KEY_SUB1VALUE, sub1_value);
        values.put(KEY_SUB2VALUE, sub2_value);
        values.put(KEY_TALENT, talent);
        values.put(KEY_EDIT1, "false");
        values.put(KEY_EDIT2, "false");
        values.put(KEY_EDIT3, "false");
        values.put(KEY_TALENTEDIT, "false");
        values.put(KEY_FAVORITE, 0);
        values.put(KEY_NEW, 1);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(long rowID) {
        Log.i("Delete called.", "value___"+rowID);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean deleteAllData() {
        return sqlDB.delete(DATABASE_TABLE, null, null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, null, null, null, null, null);
    }

    public Cursor fetchData(String type) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='"+type+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchIDData(long rowID) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_ROWID+"="+rowID, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public int getTypeCount(String type) {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_TYPE+" = '"+type+"';", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean isEdited(long rowID) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_ROWID+"="+rowID, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        boolean[] edited = new boolean[4];
        for (int i = 0; i < edited.length; i++) {
            edited[i] = Boolean.parseBoolean(cursor.getString(12+i));
            if (edited[i]) return true;
        }
        return false;
    }

    public boolean isFavorite(long rowID) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_ROWID+"="+rowID, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int result = cursor.getInt(16);
        if (result == 1) return true;
        else return false;
    }

    public boolean updateCore1Data(long rowID, String core1, double core1_value) {
        ContentValues values = new ContentValues();
        values.put(KEY_CORE1, core1);
        values.put(KEY_CORE1VALUE, core1_value);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean updateCore2Data(long rowID, String core2, double core2_value) {
        ContentValues values = new ContentValues();
        values.put(KEY_CORE2, core2);
        values.put(KEY_CORE2VALUE, core2_value);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean updateSub1Data(long rowID, String sub1, double sub1_value) {
        ContentValues values = new ContentValues();
        values.put(KEY_SUB1, sub1);
        values.put(KEY_SUB1VALUE, sub1_value);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean updateSub2Data(long rowID, String sub2, double sub2_value) {
        ContentValues values = new ContentValues();
        values.put(KEY_SUB2, sub2);
        values.put(KEY_SUB2VALUE, sub2_value);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean updateTalentData(long rowID, String talent) {
        ContentValues values = new ContentValues();
        values.put(KEY_TALENT, talent);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean updateFavoriteData(long rowID, boolean isFavorite) {
        int favorite;
        if (isFavorite) favorite = 1;
        else favorite = 0;
        ContentValues values = new ContentValues();
        values.put(KEY_FAVORITE, favorite);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean updateEditData(long rowID, boolean edit1, boolean edit2, boolean edit3, boolean talentedit) {
        ContentValues values = new ContentValues();
        values.put(KEY_EDIT1, Boolean.toString(edit1));
        values.put(KEY_EDIT2, Boolean.toString(edit2));
        values.put(KEY_EDIT3, Boolean.toString(edit3));
        values.put(KEY_TALENTEDIT, Boolean.toString(talentedit));
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean isNewWeapon() throws SQLException {
        Cursor cursor = null;
        for (int i = 0; i < weapon_type.length; i++) {
            cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='"+weapon_type[i]+"' and "+KEY_NEW+"="+1, null, null, null, null, null);
            if (cursor.getCount() > 0) return true;
        }
        return false;
    }

    public boolean isNewSubWeapon() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='권총' and "+KEY_NEW+"="+1, null, null, null, null, null);
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    public boolean isNewMask() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='마스크' and "+KEY_NEW+"="+1, null, null, null, null, null);
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    public boolean isNewBackpack() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='백팩' and "+KEY_NEW+"="+1, null, null, null, null, null);
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    public boolean isNewVest() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='조끼' and "+KEY_NEW+"="+1, null, null, null, null, null);
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    public boolean isNewGlove() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='장갑' and "+KEY_NEW+"="+1, null, null, null, null, null);
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    public boolean isNewHolster() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='권총집' and "+KEY_NEW+"="+1, null, null, null, null, null);
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    public boolean isNewKneeped() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_CORE1, KEY_CORE2, KEY_SUB1, KEY_SUB2, KEY_CORE1VALUE, KEY_CORE2VALUE, KEY_SUB1VALUE, KEY_SUB2VALUE, KEY_TALENT, KEY_EDIT1, KEY_EDIT2, KEY_EDIT3, KEY_TALENTEDIT, KEY_FAVORITE, KEY_NEW}, KEY_TYPE+"='무릎보호대' and "+KEY_NEW+"="+1, null, null, null, null, null);
        if (cursor.getCount() > 0) return true;
        else return false;
    }

    public boolean showData(long rowID) {
        ContentValues values = new ContentValues();
        values.put(KEY_NEW, 0);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }
}
