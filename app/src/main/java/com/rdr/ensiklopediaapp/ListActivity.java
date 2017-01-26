package com.rdr.ensiklopediaapp;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ListActivity extends AppCompatActivity {

    private static final String urlApi = "http://183.91.78.12/ensiklopedia/_graph" +
            "/content/word.php?alphabet=";
    private final static String[] alphabets = { "a", "b", "c", "d", "e",
    "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
    "r", "s", "t", "u", "v", "w", "x", "y", "z" };
    private final static String DEBUG_TAG = "ListActivity";
    private int currentAlphabets = 1;
    private IndexDatabase mDB;
    private ListView mListView;
    private SimpleCursorAdapter mAdapter;
    private ProgressDialog mProgress;
    private long elapsedTime = 0;
    private final static long DELTA_CLICK = 750;
    private static boolean inProgress = false;
    public Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mListView = (ListView) findViewById(R.id.listview);
        mDB = new IndexDatabase(this);
        Log.d(DEBUG_TAG, "Starting activity");

        getSupportActionBar()
                .setBackgroundDrawable(new ColorDrawable(getResources()
                        .getColor(R.color.primary)));

        Intent intent = getIntent();
        String segment = intent.getStringExtra("data");
        if (segment != null) {
            try {
                populateDB(new JSONArray(segment));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new IndexRetriever().execute(urlApi+alphabets[currentAlphabets]);

            Log.d(DEBUG_TAG, "Safely retrieve JsonReader");
        }

        mAdapter = makeAdapter();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - elapsedTime > DELTA_CLICK) {
                    Intent i = new Intent(getApplicationContext(), DetailActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("position", position);
                    startActivity(i);
                }
                elapsedTime = currentTime;
            }
        });
        mListView.setAdapter(mAdapter);
        updateListView();
    }

    private SimpleCursorAdapter makeAdapter(){
        mCursor = mDB.getIndexList();
        return new SimpleCursorAdapter(this,
                R.layout.index_list,
                mCursor,
                new String[] { "title" },
                new int[] {R.id.index_label},
                0);
    }

    private void updateListView() {
        mAdapter.notifyDataSetChanged();
        Log.d(DEBUG_TAG, "Update list view");
    }

    private class GetIndexAlphabets extends JSONRetriever {
        JSONArray data;
        public GetIndexAlphabets(String path) {
            super(path);
        }

        public void parseJSON() throws IOException, JSONException {
            JSONObject jsonObject = new JSONObject(toString());
            data = jsonObject.getJSONArray("data");
        }
    }

    private void startDialog() {
        if (!inProgress) {
            inProgress = true;
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Mengambil Data...");
            mProgress.setCancelable(false);
            mProgress.show();
        }
    }

    private void stopDialog() {
        mProgress.dismiss();
    }

    private class IndexRetriever extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected void onPreExecute() {
            startDialog();
        }

        @Override
        protected JSONArray doInBackground(String... path) {
            GetIndexAlphabets idx = new GetIndexAlphabets(path[0]);
            Log.d(DEBUG_TAG, "currentAlphabets: " + currentAlphabets);
            Log.d(DEBUG_TAG, path[0]);
            try {
                idx.parseJSON();
            } catch (IOException e) {
                e.printStackTrace();
                idx.data = null;
            } catch (JSONException e) {
                e.printStackTrace();
                idx.data = null;
            }
            idx.close();
            return idx.data;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            populateDB(jsonArray);
            updateListView();
            if (++currentAlphabets < alphabets.length) {
                new IndexRetriever().execute(urlApi+alphabets[currentAlphabets]);

            } else if (inProgress && currentAlphabets == alphabets.length) {
                stopDialog();
                inProgress = false;
            }
        }
    }

    private void populateDB(JSONArray jsonArray) {
        if (jsonArray == null)
            return;

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                int word_id = obj.getInt("word_id");
                String title = obj.getString("title");
                mDB.addIndex(word_id, title);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager manager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        SearchView view = (SearchView)
                MenuItemCompat.getActionView(
                        menu.findItem(R.id.menu_search));
        view.setSearchableInfo(
                manager.getSearchableInfo(getComponentName()));
        Log.d(DEBUG_TAG, "in OnCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mDB.dropTable();
                mDB.createTable();
                currentAlphabets = 0;
                new IndexRetriever().execute(urlApi+alphabets[currentAlphabets]);
                mAdapter = makeAdapter();
                updateListView();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
