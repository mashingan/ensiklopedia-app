package com.rdr.ensiklopediaapp;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rahmatullah on 9/28/2016.
 * Wrapper class for fetching JSON from string urlpath
 * and save its InputStream to JsonReader
 * Each class inherits this class should define its own
 * parseJSON that actually take the values of each fields
 */

public class JSONRetriever {
    private HttpURLConnection conn = null;
    private JsonReader reader = null;
    private String DEBUG_TAG = "JSONRETRIEVER: ";
    private String result;

    public JSONRetriever(String path) {
        URL url;
        try {
            url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Log.d(DEBUG_TAG, "Success getting HttpURLConnection");
            conn.connect();
            Log.d(DEBUG_TAG, "Connecting success");

            //InputStream inputStream = conn.getInputStream();
            InputStream is = conn.getInputStream();
            BufferedReader streamReader = new BufferedReader(
                    new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            result = responseStrBuilder.toString();
            reader = new JsonReader(
                    new InputStreamReader(is, "UTF-8"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    final public JsonReader getReader() {
        return reader;
    }
    final public String toString() { return result; }

    final public void close() {
        if (conn != null)
            conn.disconnect();
        if (reader != null)
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
