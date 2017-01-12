package com.rdr.ensiklopediaapp;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by lenovo pc on 10/11/2016.
 */

public class IndexWordProvider extends ContentProvider {
    private static final String DEBUG_TAG = "IndexWordProvider";
    private static final  String PROVIDER_NAME = "com.rdr.ensiklopediaapp";
    private static final Uri CONTENT_URI = Uri.parse("content://"
            +PROVIDER_NAME+"/word");
    private static final int SEARCH_SUGGEST = 1;
    private static final UriMatcher uriMatcher = getUriMatcher();
    private static UriMatcher getUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        uriMatcher.addURI(PROVIDER_NAME, SearchManager.SUGGEST_URI_PATH_QUERY +"/*", SEARCH_SUGGEST);
        return uriMatcher;
    }
    private IndexDatabase mDB;
    private static final String[] COLUMN = {
            "_id", SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
            SearchManager.SUGGEST_COLUMN_INTENT_ACTION
    };

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:break;
        }
        return "";
    }

    @Override
    public boolean onCreate() {
        mDB = new IndexDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Log.d(DEBUG_TAG, "query=" + uri);
        switch (uriMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                Log.d(DEBUG_TAG, "Search suggestion requested.");
                String query = uri.getLastPathSegment().toLowerCase();
                Log.d(DEBUG_TAG, "query: " + query);
                Cursor cursor = mDB.getTitles(query);
                MatrixCursor matrixCursor = new MatrixCursor(COLUMN);
                cursor.move(-1);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex("_id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    /*
                    Log.d(DEBUG_TAG, String.valueOf(id));
                    Log.d(DEBUG_TAG, title);
                    */
                    matrixCursor.addRow(new Object[] {id, title,
                            "content://com.rdr.ensiklopediaapp/search_suggest_query/" +
                                    String.valueOf(id),
                            "android.intent.action.VIEW"
                    });
                }
                return matrixCursor;
        }
        //return mDB.getIndexList();
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
