package com.rdr.ensiklopediaapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by lenovo pc on 10/6/2016.
 */

public class IndexCursorAdapter extends CursorAdapter {
    private Context mContext;
    private Cursor mCursor;
    private final LayoutInflater inflater;

    public IndexCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        //super(context, layout, c, from, to, flags);
        this.mContext = context;
        this.mCursor = c;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.activity_list, parent, false);
    }

    @Override
    public long getItemId (int position) {
        Cursor c = getCursor();
        c.move(position);
        return c.getLong(c.getColumnIndex("word_id"));
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //super.bindView(view, context, cursor);
        TextView index = (TextView) view.findViewById(R.id.index_label);
        int index_pos = cursor.getColumnIndex("index");
        index.setText(cursor.getString(index_pos));
    }
}
