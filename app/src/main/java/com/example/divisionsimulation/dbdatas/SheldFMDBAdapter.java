package com.example.divisionsimulation.dbdatas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.divisionsimulation.ui.share.SheldItem;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

import static android.content.ContentValues.TAG;

public class SheldFMDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_SUB = "SUB";
    public static final String KEY_VEST = "VEST";
    public static final String KEY_BACKPACK = "BACKPACK";

    private static final String DATABASE_CREATE = "create table FARMING_SHELD (_id integer primary key, " +
            "NAME text not null, TYPE text not null, SUB text not null, VEST text, BACKPACK text);";

    private static final String DATABASE_NAME = "DIVISION_FARMING_SHELD";
    private static final String DATABASE_TABLE = "FARMING_SHELD";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public SheldFMDBAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS FARMING_SHELD");
            onCreate(db);
        }

        private void copyExcelDataToDatabase(SQLiteDatabase db) {
            Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

            Workbook workbook = null;
            Sheet sheet = null;

            try {
                InputStream is = mCtx.getResources().getAssets().open("farming_sheld.xls");
                workbook = Workbook.getWorkbook(is);

                if (workbook != null) {
                    sheet = workbook.getSheet(0);
                    if (sheet != null) {
                        int nMaxColumn = 5;
                        int nRowStartIndex = 0;
                        int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                        int nColumnStartIndex = 0;
                        int nColumnEndIndex = sheet.getRow(1).length - 1;
                        ContentValues[] values = new ContentValues[nRowEndIndex+1];

                        for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                            String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                            String type = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                            String sub = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                            String vest = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                            String backpack = sheet.getCell(nColumnStartIndex+4, nRow).getContents();

                            values[nRow] = new ContentValues();
                            values[nRow].put(KEY_NAME, name);
                            values[nRow].put(KEY_TYPE, type);
                            values[nRow].put(KEY_SUB, sub);
                            values[nRow].put(KEY_VEST, vest);
                            values[nRow].put(KEY_BACKPACK, backpack);

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

    public SheldFMDBAdapter open() throws SQLException {
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

    public long insertData(String name, String type, String sub, String vest, String backpack) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_SUB, sub);
        values.put(KEY_VEST, vest);
        values.put(KEY_BACKPACK, backpack);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(String name) {
        Log.i("Delete called.", "value___"+name);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+name+"'", null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_SUB, KEY_VEST, KEY_BACKPACK}, null, null, null, null, null);
    }

    public ArrayList<String> arrayGearData(String type) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_SUB, KEY_VEST, KEY_BACKPACK}, KEY_TYPE+"='"+type+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        ArrayList<String> arrayList = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            arrayList.add(name);
            cursor.moveToNext();
        }
        return arrayList;
    }

    public Cursor fetchData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_SUB, KEY_VEST, KEY_BACKPACK}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public SheldItem fetchRandomData(String type) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TYPE, KEY_SUB, KEY_VEST, KEY_BACKPACK}, KEY_TYPE+"='"+type+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        ArrayList<SheldItem> sheldItems = new ArrayList<SheldItem>();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            String asp = cursor.getString(3);
            String vest = cursor.getString(4);
            String backpack = cursor.getString(5);

            SheldItem item = new SheldItem(name, type, asp, vest, backpack);
            sheldItems.add(item);
            cursor.moveToNext();
        }
        int index = percent(0, sheldItems.size());
        return sheldItems.get(index);
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateData(String undo_name, String name, String type, String sub, String vest, String backpack) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TYPE, type);
        values.put(KEY_SUB, sub);
        values.put(KEY_VEST, vest);
        values.put(KEY_BACKPACK, backpack);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+undo_name+"'", null) > 0;
    }

    public int percent(int min, int length) {
        return (int)(Math.random()*12345678)%length+min;
    }
}
