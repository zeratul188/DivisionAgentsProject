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

public class CommandDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_CONTENT = "CONTENT";

    private static final String DATABASE_CREATE = "create table COMMANDS (_id integer primary key, " +
            "NAME text not null, CONTENT text not null);";

    private static final String DATABASE_NAME = "DIVISION_COMMANDS";
    private static final String DATABASE_TABLE = "COMMANDS";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public CommandDBAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS COMMANDS");
            onCreate(db);
        }

        private void copyExcelDataToDatabase(SQLiteDatabase db) {
            Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

            Workbook workbook = null;
            Sheet sheet = null;

            try {
                InputStream is = mCtx.getResources().getAssets().open("commands.xls");
                workbook = Workbook.getWorkbook(is);

                if (workbook != null) {
                    sheet = workbook.getSheet(0);
                    if (sheet != null) {
                        int nMaxColumn = 2;
                        int nRowStartIndex = 0;
                        int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                        int nColumnStartIndex = 0;
                        int nColumnEndIndex = sheet.getRow(1).length - 1;
                        ContentValues[] values = new ContentValues[nRowEndIndex+1];

                        for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                            String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                            String content = sheet.getCell(nColumnStartIndex+1, nRow).getContents();

                            values[nRow] = new ContentValues();
                            values[nRow].put(KEY_NAME, name);
                            values[nRow].put(KEY_CONTENT, content);
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

    public void emptyDelete() throws SQLException {
        String name;
        long rowID;
        Cursor cursor = sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            rowID = cursor.getLong(0);
            name = cursor.getString(1);
            if (name.equals("")) deleteData(rowID);
            cursor.moveToNext();
        }
    }

    public CommandDBAdapter open() throws SQLException {
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

    public long insertData(String name, String content) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_CONTENT, content);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(long rowID) {
        Log.i("Delete called.", "value___"+rowID);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowID, null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, null, null, null, null, null);
    }

    public Cursor fetchData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateData(String undo_name, String name, String content) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_CONTENT, content);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+undo_name+"'", null) > 0;
    }
}
