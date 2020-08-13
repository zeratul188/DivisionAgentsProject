package com.example.divisionsimulation.dbdatas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.divisionsimulation.ui.share.NamedItem;

import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

import static android.content.ContentValues.TAG;

public class NamedFMDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_TALENT = "TALENT";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_LITE = "LITE";
    public static final String KEY_DARK = "DARK";
    public static final String KEY_SUB = "SUB";
    public static final String KEY_WS = "WS";
    public static final String KEY_BRAND = "BRAND";
    public static final String KEY_ASP = "ASP";
    public static final String KEY_TALENTCONTENT = "TALENTCONTENT";

    private static final String DATABASE_CREATE = "create table FARMING_NAMED (_id integer primary key, " +
            "NAME text not null, TALENT text not null, TYPE text not null, " +
            "LITE int not null, DARK int not null, SUB int not null, WS text not null, BRAND text, ASP text, TALENTCONTENT text);";

    private static final String DATABASE_NAME = "DIVISION_FARMING_NAMED";
    private static final String DATABASE_TABLE = "FARMING_NAMED";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public NamedFMDBAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS FARMING_NAMED");
            onCreate(db);
        }

        private void copyExcelDataToDatabase(SQLiteDatabase db) {
            Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

            Workbook workbook = null;
            Sheet sheet = null;

            try {
                InputStream is = mCtx.getResources().getAssets().open("farming_named.xls");
                workbook = Workbook.getWorkbook(is);

                if (workbook != null) {
                    sheet = workbook.getSheet(0);
                    if (sheet != null) {
                        int nMaxColumn = 10;
                        int nRowStartIndex = 0;
                        int nRowEndIndex = sheet.getColumn(nMaxColumn-1).length - 1;
                        int nColumnStartIndex = 0;
                        int nColumnEndIndex = sheet.getRow(1).length - 1;
                        ContentValues[] values = new ContentValues[nRowEndIndex+1];

                        for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                            String name = sheet.getCell(nColumnStartIndex, nRow).getContents();
                            String talent = sheet.getCell(nColumnStartIndex+1, nRow).getContents();
                            String type = sheet.getCell(nColumnStartIndex+2, nRow).getContents();
                            int lite = Integer.parseInt(sheet.getCell(nColumnStartIndex+3, nRow).getContents());
                            int dark = Integer.parseInt(sheet.getCell(nColumnStartIndex+4, nRow).getContents());
                            int sub = Integer.parseInt(sheet.getCell(nColumnStartIndex+5, nRow).getContents());
                            String ws = sheet.getCell(nColumnStartIndex+6, nRow).getContents();
                            String brand = sheet.getCell(nColumnStartIndex+7, nRow).getContents();
                            String asp = sheet.getCell(nColumnStartIndex+8, nRow).getContents();
                            String talentcontent = sheet.getCell(nColumnStartIndex+9, nRow).getContents();

                            values[nRow] = new ContentValues();
                            values[nRow].put(KEY_NAME, name);
                            values[nRow].put(KEY_TALENT, talent);
                            values[nRow].put(KEY_TYPE, type);
                            values[nRow].put(KEY_LITE, lite);
                            values[nRow].put(KEY_DARK, dark);
                            values[nRow].put(KEY_SUB, sub);
                            values[nRow].put(KEY_WS, ws);
                            values[nRow].put(KEY_BRAND, brand);
                            values[nRow].put(KEY_ASP, asp);
                            values[nRow].put(KEY_TALENTCONTENT, talentcontent);
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

    public NamedFMDBAdapter open() throws SQLException {
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

    public long insertData(String name, String talent, String type, int lite, int dark, int sub, String ws, String brand, String asp, String talentcontent) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TALENT, talent);
        values.put(KEY_TYPE, type);
        values.put(KEY_LITE, lite);
        values.put(KEY_DARK, dark);
        values.put(KEY_SUB, sub);
        values.put(KEY_WS, ws);
        values.put(KEY_BRAND, brand);
        values.put(KEY_ASP, asp);
        values.put(KEY_TALENTCONTENT, talentcontent);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(String name) {
        Log.i("Delete called.", "value___"+name);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+name+"'", null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, null, null, null, null, null);
    }

    public ArrayList<String> arrayAllData() {
        Cursor cursor = sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, null, null, null, null, null);
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
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public String fetchNoTalentData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, KEY_NAME+"='"+name+"' and "+KEY_SUB+"=1", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getString(2);
    }

    public boolean haveNoTalentData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, KEY_NAME+"='"+name+"' and "+KEY_SUB+"=1", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int count = cursor.getCount();
        return count > 0;
    }

    public boolean haveTalentData(String talent) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, KEY_TALENT+"='"+talent+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int count = cursor.getCount();
        return count > 0;
    }

    public String fetchTalentData(String talent) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, KEY_TALENT+"='"+talent+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getString(10);
    }

    public boolean haveItem(String name) {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_NAME+"='"+name+"';", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count > 0;
    }

    public boolean haveDarkItem(String name) {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_NAME+"='"+name+"' and "+KEY_LITE+" = 0 and "+KEY_DARK+" = 1;", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count > 0;
    }

    public NamedItem fetchLiteData_Random(String ws) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, KEY_LITE+"="+1+" and "+KEY_WS+"='"+ws+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        ArrayList<NamedItem> namedItems = new ArrayList<NamedItem>();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            String talent = cursor.getString(2);
            String type = cursor.getString(3);
            int noTalent = Integer.parseInt(cursor.getString(6));
            String brand = cursor.getString(8);
            String asp = cursor.getString(9);
            String talentcontent = cursor.getString(10);
            NamedItem item = new NamedItem(name, talent, type, brand, asp, talentcontent);
            item.setNoTalent(noTalent);
            namedItems.add(item);
            cursor.moveToNext();
        }
        int index = percent(0, namedItems.size());
        return namedItems.get(index);
    }

    public NamedItem fetchDarkData_Random(String ws) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_TALENT, KEY_TYPE, KEY_LITE, KEY_DARK, KEY_SUB, KEY_WS, KEY_BRAND, KEY_ASP, KEY_TALENTCONTENT}, KEY_DARK+"="+1+" and "+KEY_WS+"='"+ws+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        ArrayList<NamedItem> namedItems = new ArrayList<NamedItem>();
        while (!cursor.isAfterLast()) {
            String name = cursor.getString(1);
            String talent = cursor.getString(2);
            String type = cursor.getString(3);
            int noTalent = Integer.parseInt(cursor.getString(6));
            String brand = cursor.getString(8);
            String asp = cursor.getString(9);
            String talentcontent = cursor.getString(10);
            NamedItem item = new NamedItem(name, talent, type, brand, asp, talentcontent);
            item.setNoTalent(noTalent);
            namedItems.add(item);
            cursor.moveToNext();
        }
        int index = percent(0, namedItems.size());
        return namedItems.get(index);
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateData(String undo_name, String name, String talent, String type, int lite, int dark, int sub, String ws, String brand, String asp, String talentcontent) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_TALENT, talent);
        values.put(KEY_TYPE, type);
        values.put(KEY_LITE, lite);
        values.put(KEY_DARK, dark);
        values.put(KEY_SUB, sub);
        values.put(KEY_WS, ws);
        values.put(KEY_BRAND, brand);
        values.put(KEY_ASP, asp);
        values.put(KEY_TALENTCONTENT, talentcontent);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+undo_name+"'", null) > 0;
    }

    public int percent(int min, int length) {
        return (int)(Math.random()*12345678)%length+min;
    }
}
