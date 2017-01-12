package com.rdr.ensiklopediaapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String DEBUG_TAG = "MainActivity: ";
    private String[] alphabets = { "a", "b", "c"};
    private String urlApi = "http://128.199.116.105/ensiklopedia/_graph" +
            "/content/word.php?alphabet=";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean dbExists = getDatabasePath("alphabets.db").exists();
        final IndexDatabase db = new IndexDatabase(this);
        final MainActivity mainActivity = this;
        if (dbExists && db.isTableExists() && !db.emptyTable()) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                        intent = new Intent(mainActivity, ListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }.start();
        } else {
            new AsyncRetrieve().execute(urlApi + alphabets[0]);
        }
    }

    private class AsyncRetrieve extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {
            Log.d(DEBUG_TAG, "Start to retrieve " + path);
            GetData getData = new GetData(path[0]);
            try {
                getData.parseJSON();
            } catch (JSONException jsexc) {
                jsexc.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            getData.close();
            return getData.data.toString();
        }

        @Override
        protected void onPostExecute(String reader) {
            Log.d(DEBUG_TAG, "Data retrieved");
            intent = new Intent(getApplicationContext(), ListActivity.class);
            intent.putExtra("data", reader);
            startActivity(intent);
            finish();
        }
    }

    private class GetData extends JSONRetriever {
        JSONArray data;
        public GetData(String path) {
            super(path);
        }

        public void parseJSON() throws IOException, JSONException {
            JSONObject jsonObject = new JSONObject(toString());
            data = jsonObject.getJSONArray("data");
        }
    }

}
