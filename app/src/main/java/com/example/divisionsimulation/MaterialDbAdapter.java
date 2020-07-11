package com.example.divisionsimulation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MaterialDbAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "NAME";
    public static final String KEY_COUNT = "COUNT";

    /*public static final String KEY_GUN_PART = "GUN_PART";
    public static final String KEY_FABRIC = "FABRIC";
    public static final String KEY_STEEL = "STEEL";
    public static final String KEY_CERAMIC = "CERAMIC";
    public static final String KEY_POLYCARBONATE = "POLYCARBONATE";
    public static final String KEY_CARBON = "CARBON";
    public static final String KEY_ELECTRONIC = "ELECTRONIC";
    public static final String KEY_TITANIUM = "TITANIUM";*/

    private static final String DATABASE_CREATE = "create table MATERIAL (_id integer primary key, " +
            "NAME text not null, COUNT int not null);";

    private static final String DATABASE_NAME = "DIVISION_MATERIAL";
    private static final String DATABASE_TABLE = "MATERIAL";
    private static final int DATABASE_VERSION = 2;
    private final Context mCtx;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase sqlDB;

    public MaterialDbAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        private String[] material_name = {"총몸부품", "보호용 옷감", "강철", "세라믹", "폴리카보네이트", "탄소섬유", "전자부품", "티타늄"};

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);

            String sql = "";

            try {
                ContentValues[] values = new ContentValues[material_name.length];
                for (int i = 0; i < values.length; i++) {
                    values[i] = new ContentValues();
                    values[i].put(KEY_NAME, material_name[i]);
                    values[i].put(KEY_COUNT, 0);
                    db.insert(DATABASE_TABLE, null, values[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS MATERIAL");
            onCreate(db);
        }
    }

    public MaterialDbAdapter open() throws SQLException {
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

    public long insertMaterial(String name, int count) {
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_COUNT, count);
        return sqlDB.insert(DATABASE_TABLE, null, values);
    }

    public boolean deleteMaterial(String name) {
        Log.i("Delete called.", "value___"+name);
        return sqlDB.delete(DATABASE_TABLE, KEY_NAME+"='"+name+"'", null) > 0;
    }

    public Cursor fetchAllMaterial() {
        return sqlDB.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_COUNT}, null, null, null, null, null);
    }

    public Cursor fetchMaterial(String name) throws SQLException {
        Cursor cursor = sqlDB.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_COUNT}, KEY_NAME+"='"+name+"'", null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int getCount() {
        Cursor cursor = sqlDB.rawQuery("select * from "+DATABASE_TABLE+";", null);
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        return count;
    }

    public boolean updateMaterial(String name, int count) {
        ContentValues values = new ContentValues();
        values.put(KEY_COUNT, count);
        return sqlDB.update(DATABASE_TABLE, values, KEY_NAME+"='"+name+"'", null) > 0;
    }
}
