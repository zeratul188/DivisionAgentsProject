package com.example.divisionsimulation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class SHDDBAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_CONTENT = "CONTENT";

    private static final String DATABASE_CREATE = "create table SHD (_id integer primary key, " +
            "NAME text not null, CONTENT int not null);";

    private static final String DATABASE_NAME = "DIVISION_SHD";
    private static final String DATABASE_TABLE = "SHD";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    private String[] shd_name = {"SHD 레벨", "EXP", "무기 데미지", "헤드샷 데미지", "치명타 확률", "치명타 데미지", "생명력", "전체 방어도", "폭발물 저항", "상태이상 저항",
            "스킬 데미지", "스킬 회복", "스킬 지속 시간", "스킬 가속", "명중률", "안정성", "재장전 속도", "탄약 휴대량", "공격", "방어", "다용도", "기타", "다음 속성", "숙달상자", "아이템"};

    public SHDDBAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        private String[] shd_name = {"SHD 레벨", "EXP", "무기 데미지", "헤드샷 데미지", "치명타 확률", "치명타 데미지", "생명력", "전체 방어도", "폭발물 저항", "상태이상 저항",
                "스킬 데미지", "스킬 회복", "스킬 지속 시간", "스킬 가속", "명중률", "안정성", "재장전 속도", "탄약 휴대량", "공격", "방어", "다용도", "기타", "다음 속성", "숙달상자", "아이템"};

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            ContentValues[] values = new ContentValues[shd_name.length];
            for (int i = 0; i < values.length; i++) {
                values[i] = new ContentValues();
                values[i].put(KEY_NAME, shd_name[i]);
                if (i == 0 || shd_name[i].equals("공격") || shd_name[i].equals("다음 속성")) values[i].put(KEY_CONTENT, 1);
                else values[i].put(KEY_CONTENT, 0);
                db.insert(DATABASE_TABLE, null, values[i]);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS SHD");
            onCreate(db);
        }
    }

    public SHDDBAdapter open() throws SQLException {
        myDBHelper = new DatabaseHelper(mCtx);
        sqlDB = myDBHelper.getWritableDatabase();
        return this;
    }

    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public void databaseReset() {
        sqlDB.delete(DATABASE_TABLE, null, null);
    }

    public void close() {
        myDBHelper.close();
    }

    public long insertSHD(String name, int count) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_CONTENT, count);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean increaseSHD(String option) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, KEY_NAME+"='"+option+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int shd_count = cursor.getInt(2);
        for (int i = 2; i <= 17; i++) if (shd_name[i].equals(option) && shd_count >= 50) return false;
        shd_count++;
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, shd_count);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+option+"'", null) > 0;
    }

    public boolean nextOption() {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, KEY_NAME+"='다음 속성'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int next = cursor.getInt(2);
        if (next == 4) next = 0;
        else next++;
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, next);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='다음 속성'", null) > 0;
    }

    public boolean resetSHD() {
        ContentValues values;
        boolean result = false;
        for (int i = 0; i < getCount(); i++) {
            values = new ContentValues();
            if (i == 0 || shd_name[i].equals("공격") || shd_name[i].equals("다음 속성")) values.put(KEY_CONTENT, 1);
            else values.put(KEY_CONTENT, 0);
            result = sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+shd_name[i]+"'", null) > 0;
        }
        return result;
    }

    public boolean usePoint(String option) {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, KEY_NAME+"='"+option+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int point = cursor.getInt(2);
        if (point > 0) point--;
        else return false;
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, point);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+option+"'", null) > 0;
    }

    public boolean addEXP(int add) {
        Cursor exp_cursor = fetchSHD("EXP");
        int now_exp = exp_cursor.getInt(2);
        now_exp += add;
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, now_exp);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='EXP'", null) > 0;
    }

    public boolean levelUp() {
        int next;
        Cursor next_cursor;
        Cursor exp_cursor = fetchSHD("EXP");
        int now_exp = exp_cursor.getInt(2);
        if (now_exp < 700000) return false;
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, KEY_NAME+"='SHD 레벨'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int level = cursor.getInt(2);
        int add_level = (int)(now_exp/700000);
        now_exp -= add_level*700000;
        level += add_level;
        increaseSHD("SHD 레벨");
        updateSHD("EXP", now_exp);
        for (int i = 0; i < add_level; i++) {
            next_cursor = fetchSHD("다음 속성");
            next = next_cursor.getInt(2);
            switch (next) {
                case 0:
                    increaseSHD("공격"); break;
                case 1:
                    increaseSHD("방어"); break;
                case 2:
                    increaseSHD("다용도"); break;
                case 3:
                    increaseSHD("기타"); break;
                case 4:
                    increaseSHD("아이템"); break;
            }
            nextOption();
            increaseBox();
        }

        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, level);

        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='SHD 레벨'", null) > 0;
    }

    public boolean increaseBox() {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, KEY_NAME+"='숙달상자'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int box = cursor.getInt(2);
        box++;
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, box);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='숙달상자'", null) > 0;
    }

    public boolean downBox() {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, KEY_NAME+"='숙달상자'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int box = cursor.getInt(2);
        if (box <= 0) return false;
        else box--;
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, box);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='숙달상자'", null) > 0;
    }

    public int getBoxCount() {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, KEY_NAME+"='숙달상자'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        int box = cursor.getInt(2);
        return box;
    }

    public boolean deleteSHD(String name) {
        Log.i("Delete called.", "value___"+name);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+name+"'", null) > 0;
    }

    public Cursor fetchAllSHD() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_CONTENT}, null, null, null, null, null);
    }

    public Cursor fetchSHD(String name) throws SQLException {
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

    public boolean updateSHD(String name, int count) {
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, count);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+name+"'", null) > 0;
    }
}
