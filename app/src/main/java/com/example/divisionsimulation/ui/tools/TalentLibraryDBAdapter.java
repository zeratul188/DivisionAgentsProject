package com.example.divisionsimulation.ui.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.divisionsimulation.ui.share.OptionItem;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

import static android.content.ContentValues.TAG;

public class TalentLibraryDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_AR = "AR"; //돌격소총
    public static final String KEY_SR = "SR"; //기관단총
    public static final String KEY_BR = "BR"; //경기관총
    public static final String KEY_RF = "RF"; //소총
    public static final String KEY_MMR = "MMR"; //지정사수소총
    public static final String KEY_SG = "SG"; //산탄총
    public static final String KEY_PT = "PT"; //권총
    public static final String KEY_VEST = "VEST"; //조끼
    public static final String KEY_BACKPACK = "BACKPACK"; //백팩

    private static final String DATABASE_CREATE = "create table TALENTLIBRARY (_id integer primary key, " +
            "NAME text not null, AR int not null, SR int not null, BR int not null, RF int not null, " +
            "MMR int not null, SG int not null, PT int not null, VEST int not null, BACKPACK int not null);";

    private static final String DATABASE_NAME = "DIVISION_TALENTLIBRARY";
    private static final String DATABASE_TABLE = "TALENTLIBRARY";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public TalentLibraryDBAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private Context mCtx = null;

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
            db.execSQL("DROP TABLE IF EXISTS TALENTLIBRARY");
            onCreate(db);
        }
    }

    public TalentLibraryDBAdapter open() throws SQLException {
        myDBHelper = new DatabaseHelper(mCtx);
        sqlDB = myDBHelper.getWritableDatabase();
        return this;
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public boolean databaseReset() {
        return sqlDB.delete(DATABASE_TABLE, null, null) > 0;
    }

    public void close() {
        myDBHelper.close();
    }

    public long insertData(String name, int ar, int sr, int br, int rf, int mmr, int sg, int pt, int vest, int backpack) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_AR, ar);
        values.put(KEY_SR, sr);
        values.put(KEY_BR, br);
        values.put(KEY_RF, rf);
        values.put(KEY_MMR, mmr);
        values.put(KEY_SG, sg);
        values.put(KEY_PT, pt);
        values.put(KEY_VEST, vest);
        values.put(KEY_BACKPACK, backpack);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(String content) {
        Log.i("Delete called.", "value___"+content);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+content+"'", null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, null, null, null, null, null);
    }

    public Cursor fetchData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public boolean haveTalent(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getCount() > 0;
    }

    public Cursor fetchTypeData(String type) throws SQLException {
        Cursor cursor;
        if (type.equals("돌격소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_AR+"="+1, null, null, null, null, null);
        else if (type.equals("기관단총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_SR+"="+1, null, null, null, null, null);
        else if (type.equals("경기관총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_BR+"="+1, null, null, null, null, null);
        else if (type.equals("소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_RF+"="+1, null, null, null, null, null);
        else if (type.equals("지정사수소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_MMR+"="+1, null, null, null, null, null);
        else if (type.equals("산탄총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_SG+"="+1, null, null, null, null, null);
        else if (type.equals("권총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_PT+"="+1, null, null, null, null, null);
        else if (type.equals("조끼")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_VEST+"="+1, null, null, null, null, null);
        else cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_BACKPACK+"="+1, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public int getTypeCount(String type) throws SQLException {
        Cursor cursor;
        if (type.equals("돌격소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_AR+"="+1, null, null, null, null, null);
        else if (type.equals("기관단총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_SR+"="+1, null, null, null, null, null);
        else if (type.equals("경기관총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_BR+"="+1, null, null, null, null, null);
        else if (type.equals("소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_RF+"="+1, null, null, null, null, null);
        else if (type.equals("지정사수소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_MMR+"="+1, null, null, null, null, null);
        else if (type.equals("산탄총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_SG+"="+1, null, null, null, null, null);
        else if (type.equals("권총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_PT+"="+1, null, null, null, null, null);
        else if (type.equals("조끼")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_VEST+"="+1, null, null, null, null, null);
        else cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK}, KEY_BACKPACK+"="+1, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getCount();
    }

    public boolean updateData(String undo_name, String name) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+undo_name+"'", null) > 0;
    }
}
