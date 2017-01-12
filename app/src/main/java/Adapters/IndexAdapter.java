package Adapters;

import android.app.Activity;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rdr.ensiklopediaapp.R;
import com.rdr.ensiklopediaapp.RowData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lenovo pc on 9/27/2016.
 */

public class IndexAdapter extends BaseAdapter {
    private JSONArray jsonArray;
    private LayoutInflater inflater;
    private ArrayList<JSONArray> jsonArrays = null;
    private ArrayList<RowData> rowDatas = null;
    private String DEBUG_TAG = "IndexAdapter";

    public IndexAdapter(Activity activity, JsonReader jsonReader) throws IOException {
        this.inflater = LayoutInflater.from(activity);
        this.rowDatas = getAllRow(jsonReader);
    }

    public IndexAdapter(Activity activity, HashMap<String, JSONArray> hashMap) {
        this.inflater = LayoutInflater.from(activity);
        this.jsonArrays = new ArrayList<>();
        for (String key: hashMap.keySet()) {
            jsonArrays.add(hashMap.get(key));
        }
    }

    public IndexAdapter(final Activity activity, JSONArray jsonArray){
        this.inflater = LayoutInflater.from(activity);
        this.jsonArray = jsonArray;
    }

    private ArrayList<RowData> getAllData (JsonReader reader) throws IOException {
        ArrayList<RowData> data = new ArrayList<>();
        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("data") && reader.peek() != JsonToken.NULL) {
                data = getAllRow(reader);
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return data;
    }

    private ArrayList<RowData> getAllRow (JsonReader reader) throws IOException {
        ArrayList<RowData> allRows = new ArrayList<>();
        reader.beginArray();
        while(reader.hasNext()){
            RowData row = new RowData();
            String name = reader.nextName();
            if (name.equals("word_id"))
                row.word_id = reader.nextInt();
            else if (name.equals("title"))
                row.title = reader.nextString();
            else
                reader.skipValue();

            allRows.add(row);
        }
        reader.endArray();
        return allRows;
    }

    private class ViewHolder {
        TextView textView;
    }

    @Override
    public int getCount() {
        if (jsonArray != null)
            return jsonArray.length();
        else if (rowDatas != null)
            return rowDatas.size();

        return 0;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.index_list, null);
            viewHolder.textView = (TextView) view.findViewById(R.id.index_label);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        try {
            viewHolder.textView.setText(jsonArray.getJSONObject(i).getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
