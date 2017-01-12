package com.rdr.ensiklopediaapp;

import android.util.JsonReader;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by lenovo pc on 9/28/2016.
 */

public class RowData implements Serializable{
    public int word_id = 0;
    public String title = "";

    public RowData() {
    }

    public RowData (int word_id, String title) {
        this.word_id = word_id;
        this.title = title;
    }

    public RowData(JsonReader reader) throws IOException {
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("word_id"))
                word_id = reader.nextInt();
            else if (name.equals("title"))
                title = reader.nextString();
            else
                reader.skipValue();
        }
        reader.endObject();
    }
}
