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

public class TalentFMDBAdapter {
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
    public static final String KEY_TALENTCONTENT = "TALENTCONTENT";

    private static final String DATABASE_CREATE = "create table FARMING_TALENT (_id integer primary key, " +
            "NAME text not null, AR int not null, SR int not null, BR int not null, RF int not null, " +
            "MMR int not null, SG int not null, PT int not null, VEST int not null, BACKPACK int not null, TALENTCONTENT text);";

    private static final String DATABASE_NAME = "DIVISION_FARMING_TALENT";
    private static final String DATABASE_TABLE = "FARMING_TALENT";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public TalentFMDBAdapter(Context mCtx) {
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
            db.execSQL("DROP TABLE IF EXISTS FARMING_TALENT");
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
                            int ar = Integer.parseInt(sheet.getCell(nColumnStartIndex+1, nRow).getContents());
                            int sr = Integer.parseInt(sheet.getCell(nColumnStartIndex+2, nRow).getContents());
                            int br = Integer.parseInt(sheet.getCell(nColumnStartIndex+3, nRow).getContents());
                            int rf = Integer.parseInt(sheet.getCell(nColumnStartIndex+4, nRow).getContents());
                            int mmr = Integer.parseInt(sheet.getCell(nColumnStartIndex+5, nRow).getContents());
                            int sg = Integer.parseInt(sheet.getCell(nColumnStartIndex+6, nRow).getContents());
                            int pt = Integer.parseInt(sheet.getCell(nColumnStartIndex+7, nRow).getContents());
                            int vest = Integer.parseInt(sheet.getCell(nColumnStartIndex+8, nRow).getContents());
                            int backpack = Integer.parseInt(sheet.getCell(nColumnStartIndex+9, nRow).getContents());
                            String talentcontent = sheet.getCell(nColumnStartIndex+10, nRow).getContents();

                            values[nRow] = new ContentValues();
                            values[nRow].put(KEY_NAME, name);
                            values[nRow].put(KEY_AR, ar);
                            values[nRow].put(KEY_SR, sr);
                            values[nRow].put(KEY_BR, br);
                            values[nRow].put(KEY_RF, rf);
                            values[nRow].put(KEY_MMR, mmr);
                            values[nRow].put(KEY_SG, sg);
                            values[nRow].put(KEY_PT, pt);
                            values[nRow].put(KEY_VEST, vest);
                            values[nRow].put(KEY_BACKPACK, backpack);
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

    public TalentFMDBAdapter open() throws SQLException {
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

    public long insertData(String name, int ar, int sr, int br, int rf, int mmr, int sg, int pt, int vest, int backpack, String talentcontent) {
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
        values.put(KEY_TALENTCONTENT, talentcontent);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteData(String name) {
        Log.i("Delete called.", "value___"+name);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+name+"'", null) > 0;
    }

    public Cursor fetchAllData() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, null, null, null, null, null);
    }

    public Cursor fetchData(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public String findContent(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        String result = cursor.getString(11);
        return result;
    }

    public String fetchRandomData(String type) {
        Cursor cursor;
        if (type.equals("돌격소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_AR+"="+1, null, null, null, null, null);
        else if (type.equals("기관단총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_SR+"="+1, null, null, null, null, null);
        else if (type.equals("경기관총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_BR+"="+1, null, null, null, null, null);
        else if (type.equals("소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_RF+"="+1, null, null, null, null, null);
        else if (type.equals("지정사수소총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_MMR+"="+1, null, null, null, null, null);
        else if (type.equals("산탄총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_SG+"="+1, null, null, null, null, null);
        else if (type.equals("권총")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_PT+"="+1, null, null, null, null, null);
        else if (type.equals("조끼")) cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_VEST+"="+1, null, null, null, null, null);
        else cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_AR, KEY_SR, KEY_BR, KEY_RF, KEY_MMR, KEY_SG, KEY_PT, KEY_VEST, KEY_BACKPACK, KEY_TALENTCONTENT}, KEY_BACKPACK+"="+1, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        ArrayList<String> items = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
            String item = cursor.getString(1);
            items.add(item);
            cursor.moveToNext();
        }
        int index = percent(0, items.size());
        return items.get(index);
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateData(String undo_name, String name, int ar, int sr, int br, int rf, int mmr, int sg, int pt, int vest, int backpack, String talentcontent) {
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
        values.put(KEY_TALENTCONTENT, talentcontent);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+undo_name+"'", null) > 0;
    }

    public int percent(int min, int length) {
        return (int)(Math.random()*12345678)%length+min;
    }
}
