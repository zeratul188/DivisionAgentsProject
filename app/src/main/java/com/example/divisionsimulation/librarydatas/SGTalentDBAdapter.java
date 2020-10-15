package com.example.divisionsimulation.librarydatas;

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

public class SGTalentDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_HAVE = "HAVE";

    private static final String DATABASE_CREATE = "create table SGTALENTLIBRARY (_id integer primary key, " +
            "NAME text not null, HAVE int not null);";

    private static final String DATABASE_NAME = "DIVISION_SGTALENTLIBRARY";
    private static final String DATABASE_TABLE = "SGTALENTLIBRARY";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public SGTalentDBAdapter(Context mCtx) {
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
            copyExcelDataToDatabase(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS SGTALENTLIBRARY");
            onCreate(db);
        }

        private void copyExcelDataToDatabase(SQLiteDatabase db) {
            Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

            Workbook workbook = null;
            Sheet sheet = null;

            try {
                InputStream is = mCtx.getResources().getAssets().open("farming_talent.xls");
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
                            int sg = Integer.parseInt(sheet.getCell(nColumnStartIndex+6, nRow).getContents());

                            boolean isHave;
                            
                            if (sg == 1) isHave = true;
                            else isHave = false;
                            
                            if (isHave) {
                                values[nRow] = new ContentValues();
                                values[nRow].put(KEY_NAME, name);
                                values[nRow].put(KEY_HAVE, 0);

                                db.insert(DATABASE_TABLE, null, values[nRow]);
                            }
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

    public SGTalentDBAdapter open() throws SQLException {
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

    public long insertData(String name, int have) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_HAVE, have);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(String content) {
        Log.i("Delete called.", "value___"+content);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+content+"'", null) > 0;
    }

    public Cursor fetchAllData() {
        Cursor cursor = sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public boolean haveTalent(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, KEY_NAME+"='"+name+"' and "+KEY_HAVE+"="+1, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getCount() > 0;
    }

    public Cursor fetchHaveData() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, KEY_HAVE+"="+1, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getHaveCount() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME}, KEY_HAVE+"="+1, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getCount();
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateData(String undo_name, String name) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+undo_name+"'", null) > 0;
    }

    public boolean resetAllData() {
        ContentValues values = new ContentValues();
        values.put(KEY_HAVE, 0);
        return sqlDB.update(DATABASE_TABLE, values, null, null) > 0;
    }

    public boolean saveTalent(String name) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(KEY_HAVE, 1);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+name+"'", null) > 0;
    }
}
