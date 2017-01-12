package com.rdr.ensiklopediaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lenovo pc on 10/6/2016.
 */

public class IndexDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "alphabets.db";
    private static final String TABLE = "alphabets";
    private static final String ID_FIELD = "_id";
    private static final String WORDID_FIELD = "word_id";
    private static final String TITLE_FIELD = "title";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE + "(" + ID_FIELD +
            /*
            " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            WORDID_FIELD + " TEXT, " +
            */
            " INTEGER PRIMARY KEY, " +
            TITLE_FIELD + " TEXT);";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS "
            + TABLE;

    public IndexDatabase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("SQLITE", "onCreate()");
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long addIndex (int word_id, String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(WORDID_FIELD, word_id);
        values.put(ID_FIELD, word_id);
        values.put(TITLE_FIELD, title);
        return db.insert(TABLE, null, values);
    }

    public long addIndex (HashMap<Integer, String> map) {
        SQLiteDatabase db = getWritableDatabase();
        long result = -1;
        for (Map.Entry<Integer, String> entry: map.entrySet()) {
            int word_id = entry.getKey();
            String title = entry.getValue();
            result = addIndex(word_id, title);
        }
        return result;
    }

    public int updateIndex (int word_id, String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_FIELD, word_id);
        values.put(TITLE_FIELD, title);
        return db.update(TABLE, values, ID_FIELD +"=?",
                new String[] {String.valueOf(word_id)});
    }

    public int deleteIndex (int word_id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, "_id=?",
                new String[] {String.valueOf(word_id)});
    }

    public long findIndexID (String title) {
        /*
        long returnVal = -1;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + ID_FIELD +
                " FROM " + TABLE +
                " WHERE " +
                TITLE_FIELD + "=?", new String[] {title});
        //Cursor cursor = toFind(ID_FIELD, TITLE_FIELD, title);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            returnVal = cursor.getInt(0);
        }
        return returnVal;
        */
        return findIndex(ID_FIELD, TITLE_FIELD, title);
    }

    public long findIndexID (int id) {
        /*
        long returnVal = -1;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + WORDID_FIELD +
        " FROM " + TABLE + " WHERE " + ID_FIELD + "=?",
                new String[] {String.valueOf(id)} );
        //Cursor cursor = toFind(WORDID_FIELD, ID_FIELD, String.valueOf(id));
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            returnVal = cursor.getInt(0);
        }
        return returnVal;
        */
        return findIndex (WORDID_FIELD, ID_FIELD, String.valueOf(id));
    }

    public long findWordID (String title) {
        /*
        long returnVal = -1;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = toFind(WORDID_FIELD, TITLE_FIELD, title);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            returnVal = cursor.getInt(0);
        }
        return returnVal;
        */
        return findIndex(WORDID_FIELD, TITLE_FIELD, title);
    }

    private Cursor toFind (String what, String from, String title) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + what + " FROM " + TABLE +
                " WHERE " + from + "=?";
        return db.rawQuery(query, new String[] { title });
    }

    private long findIndex (String what, String from, String value) {
        long retval = -1;
        Cursor cursor = toFind(what, from, value);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            retval = cursor.getInt(0);
        }
        return retval;
    }


    public Cursor getIndexList () {
        SQLiteDatabase db = getReadableDatabase();
        if (!isTableExists(db)) {
            db.execSQL(CREATE_TABLE);
        }
        String query = "SELECT " + ID_FIELD + ", " + TITLE_FIELD +
                " FROM " + TABLE + " ORDER BY " + TITLE_FIELD +
                " COLLATE NOCASE ASC";
        return db.rawQuery(query, null);
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(DROP_TABLE);
    }

    public void createTable() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(CREATE_TABLE);
    }

    private boolean isTableExists(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name" +
                " from sqlite_master where tbl_name='" +
                TABLE + "';", null);
        if (cursor != null && cursor.getCount() >= 1) {
            cursor.close();
            return true;
        }
        return false;
    }

    public Cursor getTitles(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String thequery = "SELECT " + ID_FIELD +
                ", " + TITLE_FIELD + " from " + TABLE +
                " where " + TITLE_FIELD + " like '%" +
                query + "%' order by title collate nocase asc;";
        Log.d("IndexDatabase", thequery);
        return db.rawQuery(thequery, null);
    }

    public boolean isTableExists() {
        SQLiteDatabase db = getReadableDatabase();
        return isTableExists(db);
    }

    public boolean emptyTable() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) from " + TABLE, null);
        cursor.moveToFirst();
        int icount = cursor.getInt(0);
        if (icount > 0)
            return false;
        else
            return true;
    }
}
