package com.rdr.ensiklopediaapp;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by lenovo pc on 10/6/2016.
 */

public class SearchResultActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "SearchResultActivity";
    private IndexDatabase mDB;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mDB = new IndexDatabase(this);
        /*
        setContentView(R.layout.activity_search_result);
        mTextViewSearchResult = (TextView) findViewById(
                R.id.textview_search_result);
        */
        Intent intent = getIntent();
        Log.d(DEBUG_TAG, "Ready to look for query");
        if (Intent.ACTION_SEARCH.equals(
                intent.getAction()))
        {
            handleSearch(intent.getStringExtra(
                    SearchManager.QUERY));
            return;
        } else if (Intent.ACTION_VIEW.equals(
                intent.getAction()))
        {
            Uri uri = intent.getData();
            Log.d(DEBUG_TAG, "uri: " + uri);
            long id = Long.parseLong(uri.getLastPathSegment(), 10);
            Log.d(DEBUG_TAG, "id: " + id);
            Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
            detailIntent.putExtra("id", id);
            startActivity(detailIntent);
            finish();
            return;
        }
        Toast.makeText(getApplicationContext(), "Cannot search", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void handleSearch(String query) {
        Log.d(DEBUG_TAG, "handling search!");
        Log.d(DEBUG_TAG, "query=" + query);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("id", mDB.findIndexID(query));
        intent.putExtra("query", query);
        startActivity(intent);
        finish();
    }
}
