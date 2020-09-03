package com.example.divisionsimulation.dbdatas;

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

public class MakeNamedDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_TALENT = "TALENT";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_ASP = "ASP";
    public static final String KEY_NOTALENT = "NOTALENT";
    public static final String KEY_TALENTCONTENT = "TALENTCONTENT";
    public static final String KEY_BRAND = "BRAND";

    private static final String DATABASE_CREATE = "create table MAKE_NAMED (_id integer primary key, " +
            "NAME text not null, TALENT text not null, TYPE text not null, ASP text not null, NOTALENT integer not null, TALENTCONTENT text, BRAND text not null);";

    private static final String DATABASE_NAME = "DIVISION_MAKE_NAMED";
    private static final String DATABASE_TABLE = "MAKE_NAMED";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public MakeNamedDBAdapter(Context mCtx) {
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
            copyExcelDataToDatabase(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS MAKE_NAMED");
            onCreate(db);
        }

        private void copyExcelDataToDatabase(SQLiteDatabase db) {
            Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

            Workbook workbook = null;
            Sheet sheet = null;

            try {
                InputStream is = mCtx.getResources().getAssets().open("make_named.xls");
                workbook = Workbook.getWorkbook(is);

                if (workbook != null) {
                    sheet = workbook.getSheet(0);
                    if (sheet != null) {
                        int nMaxColumn = 7;
                        int nRowStartIndex = 0;
                        int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                        int nColumnStartIndex = 0;
                        int nColumnEndIndex = sheet.getRow(1).length - 1;
                        ContentValues[] values = new ContentValues[nRowEndIndex+1];

                        for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                            String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                            String talent = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                            String type = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                            String asp = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                            int notalent = Integer.parseInt(sheet.getCell(nColumnStartIndex+4, nRow).getContents());
                            String talentcontent = sheet.getCell(nColumnStartIndex+5, nRow).getContents();
                            String brand = sheet.getCell(nColumnStartIndex+6, nRow).getContents();

                            values[nRow] = new ContentValues();
                            values[nRow].put(KEY_NAME, name);
                            values[nRow].put(KEY_TALENT, talent);
                            values[nRow].put(KEY_TYPE, type);
                            values[nRow].put(KEY_ASP, asp);
                            values[nRow].put(KEY_NOTALENT, notalent);
                            values[nRow].put(KEY_TALENTCONTENT, talentcontent);
                            values[nRow].put(KEY_BRAND, brand);

                            db.insert(DATABASE_TABLE, null, values[nRow]);
                        }
                        //Toast.makeText(getApplicationContext(), "불러오기 성공", Toast.LENGTH_SHORT).show();
                    } else System.out.println("Sheet is null!!!");
                } else System.out.println("WorkBook is null!!!");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (workbook != null) workbook.close();
            }
        }
    }

    public MakeNamedDBAdapter open() throws SQLException {
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

    public long insertData(String name, String talent, String type, String asp, int notalent, String talentcontent, String brand) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TALENT, talent);
        values.put(KEY_TYPE, type);
        values.put(KEY_ASP, asp);
        values.put(KEY_NOTALENT, notalent);
        values.put(KEY_TALENTCONTENT, talentcontent);
        values.put(KEY_BRAND, brand);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(String name) {
        Log.i("Delete called.", "value___"+name);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+name+"'", null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_ASP, KEY_NOTALENT, KEY_TALENTCONTENT, KEY_BRAND}, null, null, null, null, null);
    }

    public boolean haveNoTalentData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_ASP, KEY_NOTALENT, KEY_TALENTCONTENT, KEY_BRAND}, KEY_NAME+"='"+name+"' and "+KEY_NOTALENT+"=1", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int count = cursor.getCount();
        return count > 0;
    }

    public String fetchNoTalentData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_ASP, KEY_NOTALENT, KEY_TALENTCONTENT, KEY_BRAND}, KEY_NAME+"='"+name+"' and "+KEY_NOTALENT+"=1", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getString(2);
    }

    public String fetchTalentData(String talent) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_ASP, KEY_NOTALENT, KEY_TALENTCONTENT, KEY_BRAND}, KEY_TALENT+"='"+talent+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getString(6);
    }

    public Cursor fetchData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_ASP, KEY_NOTALENT, KEY_TALENTCONTENT, KEY_BRAND}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchTypeData(String type) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_ASP, KEY_NOTALENT, KEY_TALENTCONTENT, KEY_BRAND}, KEY_TYPE+"='"+type+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean haveTalentData(String talent) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_ASP, KEY_NOTALENT, KEY_TALENTCONTENT, KEY_BRAND}, KEY_TALENT+"='"+talent+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int count = cursor.getCount();
        return count > 0;
    }

    public boolean haveItem(String name) {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_NAME+"='"+name+"';", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count > 0;
    }

    public boolean noTalent(String name) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_ASP, KEY_NOTALENT, KEY_TALENTCONTENT, KEY_BRAND}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        if (cursor.getInt(5) == 1) return true;
        else return false;
    }

    public boolean updateData(String undo_name, String name, String talent, String type, String asp, int notalent, String talentcontent, String brand) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TALENT, talent);
        values.put(KEY_TYPE, type);
        values.put(KEY_ASP, asp);
        values.put(KEY_NOTALENT, notalent);
        values.put(KEY_TALENTCONTENT, talentcontent);
        values.put(KEY_BRAND, brand);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+undo_name+"'", null) > 0;
    }
}
