package com.example.divisionsimulation.dbdatas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

import static android.content.ContentValues.TAG;

public class ExoticFMDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_COREOPTION = "COREOPTION";
    public static final String KEY_SUBOPTION1 = "SUBOPTION1";
    public static final String KEY_SUBOPTION2 = "SUBOPTION2";
    public static final String KEY_CORE = "CORE";
    public static final String KEY_SUB1 = "SUB1";
    public static final String KEY_SUB2 = "SUB2";
    public static final String KEY_TALENT = "TALENT";
    public static final String KEY_DROPED = "DROPED";
    public static final String KEY_WS = "WS";

    private static final String DATABASE_CREATE = "create table FARMING_EXOTIC (_id integer primary key, " +
            "NAME text not null, TYPE text not null, COREOPTION text, SUBOPTION1 text, SUBOPTION2 text, " +
            "CORE text, SUB1 text, SUB2 text, TALENT text not null, DROPED int not null, WS text not null);";

    private static final String DATABASE_NAME = "DIVISION_FARMING_EXOTIC";
    private static final String DATABASE_TABLE = "FARMING_EXOTIC";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public ExoticFMDBAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS FARMING_EXOTIC");
            onCreate(db);
        }

        private void copyExcelDataToDatabase(SQLiteDatabase db) {
            Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

            Workbook workbook = null;
            Sheet sheet = null;

            try {
                InputStream is = mCtx.getResources().getAssets().open("farming_exotic.xls");
                workbook = Workbook.getWorkbook(is);

                if (workbook != null) {
                    sheet = workbook.getSheet(0);
                    if (sheet != null) {
                        int nMaxColumn = 11;
                        int nRowStartIndex = 0;
                        int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                        int nColumnStartIndex = 0;
                        int nColumnEndIndex = sheet.getRow(1).length - 1;
                        ContentValues[] values = new ContentValues[nRowEndIndex+1];

                        for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                            String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                            String type = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                            String coreoption = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                            String suboption1 = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                            String suboption2 = sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                            String core = sheet.getCell(nColumnStartIndex+5, nRow).getContents();
                            String sub1 = sheet.getCell(nColumnStartIndex+6, nRow).getContents();
                            String sub2 = sheet.getCell(nColumnStartIndex+7, nRow).getContents();
                            String talent = sheet.getCell(nColumnStartIndex+8, nRow).getContents();
                            int droped = Integer.parseInt(sheet.getCell(nColumnStartIndex+9, nRow).getContents());
                            String ws = sheet.getCell(nColumnStartIndex+10, nRow).getContents();

                            values[nRow] = new ContentValues();
                            values[nRow].put(KEY_NAME, name);
                            values[nRow].put(KEY_TYPE, type);
                            values[nRow].put(KEY_COREOPTION, coreoption);
                            values[nRow].put(KEY_SUBOPTION1, suboption1);
                            values[nRow].put(KEY_SUBOPTION2, suboption2);
                            values[nRow].put(KEY_CORE, core);
                            values[nRow].put(KEY_SUB1, sub1);
                            values[nRow].put(KEY_SUB2, sub2);
                            values[nRow].put(KEY_TALENT, talent);
                            values[nRow].put(KEY_DROPED, droped);
                            values[nRow].put(KEY_WS, ws);

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

    public ExoticFMDBAdapter open() throws SQLException {
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

    public long insertData(String name, String type, String coreoption, String suboption1, String suboption2, String core, String sub1, String sub2, String talent, int droped, String ws) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_COREOPTION, coreoption);
        values.put(KEY_SUBOPTION1, suboption1);
        values.put(KEY_SUBOPTION2, suboption2);
        values.put(KEY_CORE, core);
        values.put(KEY_SUB1, sub1);
        values.put(KEY_SUB2, sub2);
        values.put(KEY_TALENT, talent);
        values.put(KEY_DROPED, droped);
        values.put(KEY_WS, ws);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(String name) {
        Log.i("Delete called.", "value___"+name);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+name+"'", null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_COREOPTION, KEY_SUBOPTION1, KEY_SUBOPTION2, KEY_CORE, KEY_SUB1, KEY_SUB2, KEY_TALENT, KEY_DROPED, KEY_WS}, null, null, null, null, null);
    }

    public Cursor fetchData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_COREOPTION, KEY_SUBOPTION1, KEY_SUBOPTION2, KEY_CORE, KEY_SUB1, KEY_SUB2, KEY_TALENT, KEY_DROPED, KEY_WS}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public ArrayList<String> arrayAllData() {
        Cursor cursor = sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_COREOPTION, KEY_SUBOPTION1, KEY_SUBOPTION2, KEY_CORE, KEY_SUB1, KEY_SUB2, KEY_TALENT, KEY_DROPED, KEY_WS}, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        ArrayList<String> arrayList = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            arrayList.add(name);
            cursor.moveToNext();
        }
        return arrayList;
    }

    public Cursor fetchIDData(long rowid) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_COREOPTION, KEY_SUBOPTION1, KEY_SUBOPTION2, KEY_CORE, KEY_SUB1, KEY_SUB2, KEY_TALENT, KEY_DROPED, KEY_WS}, KEY_ROWID+"="+rowid, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public long rowidDroped() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_COREOPTION, KEY_SUBOPTION1, KEY_SUBOPTION2, KEY_CORE, KEY_SUB1, KEY_SUB2, KEY_TALENT, KEY_DROPED, KEY_WS}, KEY_DROPED+"="+1, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        ArrayList<String> items = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            items.add(cursor.getString(1));
            cursor.moveToNext();
        }
        Cursor result = fetchData(items.get(random(0, items.size())));
        if (result != null) result.moveToFirst();
        return result.getLong(0);
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateData(String undo_name, String name, String type, String coreoption, String suboption1, String suboption2, String core, String sub1, String sub2, String talent, int droped, String ws) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_COREOPTION, coreoption);
        values.put(KEY_SUBOPTION1, suboption1);
        values.put(KEY_SUBOPTION2, suboption2);
        values.put(KEY_CORE, core);
        values.put(KEY_SUB1, sub1);
        values.put(KEY_SUB2, sub2);
        values.put(KEY_TALENT, talent);
        values.put(KEY_DROPED, droped);
        values.put(KEY_WS, ws);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+undo_name+"'", null) > 0;
    }

    private int random(int min, int length) {
        return (int)(Math.random()*1234567)%length+min;
    }
}
