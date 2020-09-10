package com.example.divisionsimulation.ui.gallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

import static android.content.ContentValues.TAG;

public class WeaponDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_DEMAGE = "DEMAGE";
    public static final String KEY_RPM = "RPM";
    public static final String KEY_MAG = "MAG";
    public static final String KEY_RELOADTIME = "RELOADTIME";
    public static final String KEY_FIRE_METHOD = "FIRE_METHOD";
    public static final String KEY_MODE = "MODE";
    public static final String KEY_VARIATION = "VARIATION";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_CONTENT = "CONTENT";

    private static final String DATABASE_CREATE = "create table WEAPON (_id integer primary key, "
            +"NAME text not null, DEMAGE text not null, "
            +"RPM text not null, MAG text not null, "
            +"RELOADTIME text not null, FIRE_METHOD text, "
            +"MODE text, VARIATION text, TYPE text not null, "
            +"CONTENT text);";

    private static final String DATABASE_NAME = "DIVISION_WEAPON";
    private static final String DATABASE_TABLE = "WEAPON";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public WeaponDbAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {
        Context mCtx = null;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mCtx = context;
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
            db.execSQL("DROP TABLE IF EXISTS WEAPON");
            onCreate(db);
        }

        private void copyExcelDataToDatabase(SQLiteDatabase db) {
            Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

            Workbook workbook = null;
            Sheet sheet = null;

            try {
                InputStream is = mCtx.getResources().getAssets().open("weapon.xls");
                workbook = Workbook.getWorkbook(is);

                if (workbook != null) {
                    sheet = workbook.getSheet(0);
                    if (sheet != null) {
                        int nMaxColumn = 10;
                        int nRowStartIndex = 0;
                        int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                        int nColumnStartIndex = 0;
                        int nColumnEndIndex = sheet.getRow(10).length - 1;
                        ContentValues[] values = new ContentValues[nRowEndIndex+1];

                        for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                            String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                            String demage = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                            String rpm = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                            String mag = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                            String reload_time = sheet.getCell(nColumnStartIndex+4, nRow).getContents();
                            String fire_method = sheet.getCell(nColumnStartIndex+5, nRow).getContents();
                            String mode = sheet.getCell(nColumnStartIndex+6, nRow).getContents();
                            String variation = sheet.getCell(nColumnStartIndex+7, nRow).getContents();
                            String type = sheet.getCell(nColumnStartIndex+8, nRow).getContents();
                            String content = sheet.getCell(nColumnStartIndex+9, nRow).getContents();

                            values[nRow] = new ContentValues();
                            values[nRow].put(KEY_NAME, name);
                            values[nRow].put(KEY_DEMAGE, demage);
                            values[nRow].put(KEY_RPM, rpm);
                            values[nRow].put(KEY_MAG, mag);
                            values[nRow].put(KEY_RELOADTIME, reload_time);
                            values[nRow].put(KEY_FIRE_METHOD, fire_method);
                            values[nRow].put(KEY_MODE, mode);
                            values[nRow].put(KEY_VARIATION, variation);
                            values[nRow].put(KEY_TYPE, type);
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

    public WeaponDbAdapter open() throws SQLException {
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

    public long createWeapon(String name, String demage, String rpm, String mag, String reload_time, String fire_method, String mode, String variation, String type, String content) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DEMAGE, demage);
        values.put(KEY_RPM, rpm);
        values.put(KEY_MAG, mag);
        values.put(KEY_RELOADTIME, reload_time);
        values.put(KEY_FIRE_METHOD, fire_method);
        values.put(KEY_MODE, mode);
        values.put(KEY_VARIATION, variation);
        values.put(KEY_TYPE, type);
        values.put(KEY_CONTENT, content);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteWeapon(long rowId) {
        Log.i("Delete called.", "value___"+rowId);
        return sqlDB.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null) > 0;
    }

    public Cursor fetchAllWeapon() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DEMAGE, KEY_RPM, KEY_MAG, KEY_RELOADTIME, KEY_FIRE_METHOD, KEY_MODE, KEY_VARIATION, KEY_TYPE, KEY_CONTENT}, null, null, null, null, null);
    }

    public Cursor fetchTypeWeapon(String type) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DEMAGE, KEY_RPM, KEY_MAG, KEY_RELOADTIME, KEY_FIRE_METHOD, KEY_MODE, KEY_VARIATION, KEY_TYPE, KEY_CONTENT}, KEY_TYPE+"='"+type+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchNameWeapon(String name) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DEMAGE, KEY_RPM, KEY_MAG, KEY_RELOADTIME, KEY_FIRE_METHOD, KEY_MODE, KEY_VARIATION, KEY_TYPE, KEY_CONTENT}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchWeapon(long rowId) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_DEMAGE, KEY_RPM, KEY_MAG, KEY_RELOADTIME, KEY_FIRE_METHOD, KEY_MODE, KEY_VARIATION, KEY_TYPE, KEY_CONTENT}, KEY_ROWID+"="+rowId, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount(String type) {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+" where TYPE = '"+type+"';", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public int getAllCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int cnt = cursor.getCount();
        return cnt;
    }

    public boolean updateWeapon(long rowId, String name, String demage, String rpm, String mag, String reload_time, String fire_method, String mode, String variation, String type, String content) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_DEMAGE, demage);
        values.put(KEY_RPM, rpm);
        values.put(KEY_MAG, mag);
        values.put(KEY_RELOADTIME, reload_time);
        values.put(KEY_FIRE_METHOD, fire_method);
        values.put(KEY_MODE, mode);
        values.put(KEY_VARIATION, variation);
        values.put(KEY_TYPE, type);
        values.put(KEY_CONTENT, content);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowId, null) > 0;
    }
}
