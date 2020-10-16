package com.example.divisionsimulation.librarydatas;

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

public class MMRLibraryDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CONTENT = "CONTENT";
    public static final String KEY_MAX = "MAX";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_ATTRIBUTE = "ATTRIBUTE";
    public static final String KEY_TAIL = "TAIL";

    private static final String DATABASE_CREATE = "create table MMR_LIBRARY (_id integer primary key, " +
            "CONTENT text not null, MAX text not null, TYPE text, ATTRIBUTE text, TAIL text);";

    private static final String DATABASE_NAME = "DIVISION_MMR_LIBRARY";
    private static final String DATABASE_TABLE = "MMR_LIBRARY";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public MMRLibraryDBAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS MMR_LIBRARY");
            onCreate(db);
        }

        private void copyExcelDataToDatabase(SQLiteDatabase db) {
            Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

            Workbook workbook = null;
            Sheet sheet = null;

            try {
                InputStream is = mCtx.getResources().getAssets().open("farming_MMRmaxoptions.xls");
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
                            String content = sheet.getCell(nColumnStartIndex, nRow).getContents();
                            String max = "0";
                            String type = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                            String attribute = sheet.getCell(nColumnStartIndex+3, nRow).getContents();
                            String tail = sheet.getCell(nColumnStartIndex+4, nRow).getContents();

                            values[nRow] = new ContentValues();
                            values[nRow].put(KEY_CONTENT, content);
                            values[nRow].put(KEY_MAX, max);
                            values[nRow].put(KEY_TYPE, type);
                            values[nRow].put(KEY_ATTRIBUTE, attribute);
                            values[nRow].put(KEY_TAIL, tail);

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

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public MMRLibraryDBAdapter open() throws SQLException {
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

    public long insertData(String content, String max, String type, String attribute, String tail) {
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, content);
        values.put(KEY_MAX, max);
        values.put(KEY_TYPE, type);
        values.put(KEY_ATTRIBUTE, attribute);
        values.put(KEY_TAIL, tail);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(String content) {
        Log.i("Delete called.", "value___"+content);
        return sqlDB.delete(DATABASE_TABLE, KEY_CONTENT+"='"+content+"'", null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE}, null, null, null, null, null);
    }

    public Cursor fetchData(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public boolean notWeaponCore(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getCount() > 0;
    }

    public Cursor fetchExoticWeaponData(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"' and "+KEY_TYPE+"='"+"무기 부속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchTypeData(String type) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_TYPE+"='"+type+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public boolean updateTypeData(String type, String find_content, String max) {
        ContentValues values = new ContentValues();
        values.put(KEY_MAX, max);
        return sqlDB.update(DATABASE_TABLE, values, KEY_CONTENT+"='"+find_content+"' and "+KEY_TYPE+"='"+type+"'", null) > 0;
    }

    public Cursor fetchSubData(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"' and "+KEY_TYPE+"='"+"무기 부속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchSubAllData() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_TYPE+"='"+"무기 부속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public boolean updateSubData(String find_content, String max) {
        ContentValues values = new ContentValues();
        values.put(KEY_MAX, max);
        return sqlDB.update(DATABASE_TABLE, values, KEY_CONTENT+"='"+find_content+"' and "+KEY_TYPE+"='"+"무기 부속성"+"'", null) > 0;
    }

    public Cursor fetchSheldSubData(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"' and "+KEY_TYPE+"='"+"보호장구 부속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchSheldSubAllData() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_TYPE+"='"+"보호장구 부속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public boolean updateSheldSubData(String find_content, String max) {
        ContentValues values = new ContentValues();
        values.put(KEY_MAX, max);
        return sqlDB.update(DATABASE_TABLE, values, KEY_CONTENT+"='"+find_content+"' and "+KEY_TYPE+"='"+"보호장구 부속성"+"'", null) > 0;
    }

    public boolean isSheldSubData(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"' and "+KEY_TYPE+"='"+"보호장구 부속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getCount() > 0;
    }

    public Cursor fetchSheldCoreData(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"' and "+KEY_TYPE+"='"+"보호장구 핵심속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public Cursor fetchSheldCoreAllData() throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_TYPE+"='"+"보호장구 핵심속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public boolean updateSheldCoreData(String find_content, String max) {
        ContentValues values = new ContentValues();
        values.put(KEY_MAX, max);
        return sqlDB.update(DATABASE_TABLE, values, KEY_CONTENT+"='"+find_content+"' and "+KEY_TYPE+"='"+"보호장구 핵심속성"+"'", null) > 0;
    }

    public boolean isSheldCoreData(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"' and "+KEY_TYPE+"='"+"보호장구 핵심속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getCount() > 0;
    }

    public boolean isSheldCore(String content) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_CONTENT+"='"+content+"' and "+KEY_TYPE+"='"+"보호장구 핵심속성"+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getCount() > 0;
    }

    public OptionItem fetchRandomData(String type) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_CONTENT, KEY_MAX, KEY_TYPE, KEY_ATTRIBUTE, KEY_TAIL}, KEY_TYPE+"='"+type+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        ArrayList<OptionItem> optionItems = new ArrayList<OptionItem>();
        while (!cursor.isAfterLast()) {
            String content = cursor.getString(1);
            Double value = Double.parseDouble(cursor.getString(2));
            String reter = cursor.getString(5);
            String option = cursor.getString(4);
            OptionItem item = new OptionItem(content, value, option, reter);
            optionItems.add(item);
            cursor.moveToNext();
        }
        int index = percent(0, optionItems.size());
        return optionItems.get(index);
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateData(String undo_content, String content, String max, String type, String attribute, String tail) {
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, content);
        values.put(KEY_MAX, max);
        values.put(KEY_TYPE, type);
        values.put(KEY_ATTRIBUTE, attribute);
        values.put(KEY_TAIL, tail);
        return sqlDB.update(DATABASE_TABLE, values, KEY_CONTENT+"='"+undo_content+"'", null) > 0;
    }

    public boolean updateContentData(String undo_content, String max) {
        ContentValues values = new ContentValues();
        values.put(KEY_MAX, max);
        return sqlDB.update(DATABASE_TABLE, values, KEY_CONTENT+"='"+undo_content+"'", null) > 0;
    }

    public boolean updateIDData(long rowID, String max) {
        ContentValues values = new ContentValues();
        values.put(KEY_MAX, max);
        return sqlDB.update(DATABASE_TABLE, values, KEY_ROWID+"="+rowID, null) > 0;
    }

    public boolean resetAllData() {
        ContentValues values = new ContentValues();
        values.put(KEY_MAX, "0");
        return sqlDB.update(DATABASE_TABLE, values, null, null) > 0;
    }

    public boolean updateOption(String content, String max) {
        ContentValues values = new ContentValues();
        values.put(KEY_MAX, max);
        return sqlDB.update(DATABASE_TABLE, values, KEY_CONTENT+"='"+content+"'", null) > 0;
    }

    public void makeFull() {
        Workbook workbook = null;
        Sheet sheet = null;

        try {
            InputStream is = mCtx.getResources().getAssets().open("farming_MMRmaxoptions.xls");
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
                        String content = sheet.getCell(nColumnStartIndex, nRow).getContents();
                        String max = sheet.getCell(nColumnStartIndex+1, nRow).getContents();

                        updateOption(content, max);
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

    public int percent(int min, int length) {
        return (int)(Math.random()*12345678)%length+min;
    }
}
