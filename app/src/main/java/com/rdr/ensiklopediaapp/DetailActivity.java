package com.rdr.ensiklopediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class DetailActivity extends AppCompatActivity {
    private final String DEBUG_TAG = "DetailActivity";
    private long currentId;
    private ImageView imageView;
    private GetDetail currentDetail;
    private String url = "http://183.91.78.12/ensiklopedia/_graph" +
            "/content/detail-word.php?word_id=";
    private ProgressDialog mProgress;
    private Toolbar toolbar;
    private NestedScrollView nestedScrollView;
    private IndexDatabase mDB;
    private int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mDB = new IndexDatabase(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        /*
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        */

        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_view);
        //toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationIcon(R.drawable.arrow_left_white24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*
        NestedScrollView.OnScrollChangeListener listener = new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v,
                                       int scrollX, int scrollY,
                                       int oldScrollX, int oldScrollY)
            {
                int baseColor = getResources().getColor(R.color.primary);
                float alpha = Math.min(1, (float) scrollY / 48);
                toolbar.setBackgroundColor(adjustAlpha(baseColor, alpha));
            }
        };
        nestedScrollView.setOnScrollChangeListener(listener);
        */
        populateView();
    }

    private int adjustAlpha (int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int blue = Color.blue(color);
        int green = Color.green(color);
        return Color.argb(alpha, red, green, blue);
    }

    private class GetDetail extends JSONRetriever {
        String result = "";
        int word_id = 0;
        String title = "";
        String description = "";
        String imagePath = null;

        public GetDetail (String path) {
            super(path);
        }

        public void parseJSON () throws IOException {
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(toString());
                word_id = jsonObject.isNull("word_id")?
                        0: jsonObject.getInt("word_id");
                title = jsonObject.isNull("title")?
                        "": jsonObject.getString("title");
                description = jsonObject.isNull("description")?
                        "": jsonObject.getString("description");
                imagePath = jsonObject.isNull("image")?
                        "" : jsonObject.getString("image");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class FetchDataAsync extends AsyncTask<String, Void, GetDetail> {
        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(DetailActivity.this);
            mProgress.setMessage("Mengambil detail...");
            //mProgress.setCancelable(false);
            mProgress.show();
        }
        @Override
        protected GetDetail doInBackground(String... path) {
            GetDetail detail = new GetDetail(path[0]);
            try {
                detail.parseJSON();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return detail;
        }

        @Override
        protected void onPostExecute(GetDetail result) {
            currentDetail = result;
            result.close();
            Log.d(DEBUG_TAG, "Safely get the result");

            Log.d(DEBUG_TAG, "title: " + currentDetail.title);
            Log.d(DEBUG_TAG, "imagePath: " + currentDetail.imagePath);
            Log.d(DEBUG_TAG, "word_id: " + currentDetail.word_id);
            Log.d(DEBUG_TAG, "Choose for imagePath");

            mProgress.dismiss();
            toolbar.setTitle(currentDetail.title);
            //getSupportActionBar().setTitle(currentDetail.title);
            /*
            AppBarLayout appBarLayout = (AppBarLayout)
                    findViewById(R.id.appbar_layout);
            */

            TextView contentView = (TextView) findViewById(R.id.textview_content);
            imageView = (ImageView) findViewById(R.id.imageview_main);
            CoordinatorLayout relativeLayout =
                    (CoordinatorLayout)
                            findViewById(R.id.activity_detail);
            int height = relativeLayout.getHeight();
            int width = relativeLayout.getWidth();
            imageView.setMinimumHeight(height * 3/4);
            if (!currentDetail.imagePath.equals(""))
            {
                Log.d(DEBUG_TAG, "fetching image");
                Picasso.with(getApplicationContext())
                        .load(currentDetail.imagePath)
                        //.resize(width, height / 4)
                        .into(imageView);
            } else {
                /*
                imageView.setMaxWidth(width);
                imageView.setMaxHeight(height);
                imageView.setMinimumHeight(height / 4);
                */
                imageView.setImageResource(R.color.primary);
            }
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                contentView.setText(Html.fromHtml(currentDetail.description,
                       Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                contentView.setText(Html.fromHtml(currentDetail.description));
            }

            Log.d(DEBUG_TAG, "toolbar.getTitle()=" + toolbar.getTitle());
        }
    }

    private void populateView() {
        Intent intent = getIntent();
        long id = intent.getLongExtra("id", 0);
        currentPos = intent.getIntExtra("position", 0);
        Log.d(DEBUG_TAG, "id: " + id);
        if (id <= 0) {
            Toast.makeText(this,
                    "No data for " +
                            intent.getStringExtra("query") +
                    " available",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        if (id == 3) ++id;
        currentId = id;
        /*
        Cursor c = new ListActivity().mCursor;
        c.move(currentPos);
        populateView(c.getInt(0));
        */
        //populateView(mDB.findIndexID((int) currentId));
        populateView(currentId);
    }

    private void populateView(long id) {
        if (id < 1)
            Toast.makeText(getApplicationContext(),
                    "Cannot fetch data", Toast.LENGTH_LONG).show();
        else
            new FetchDataAsync().execute(url+id);
    }

    private void newActivityThis(long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }
    public void onClickForward(View view) {
        //populateView(++currentId);
        if (currentId == 3) ++currentId;
        newActivityThis(++currentId);
    }

    public void onClickBackward (View view) {
        //if (currentId > 0) populateView(--currentId);
        if (currentId == 3) --currentId;
        if (currentId> 0) newActivityThis(--currentId);
        else toastNotif("No previous data available.");

    }

    public void viewImageFull (View view) {
        if (!currentDetail.imagePath.equals("")) {
            Intent intent = new Intent(this, ImageFullActivity.class);
            intent.putExtra("imgPath", currentDetail.imagePath);
            startActivity(intent);
            //finish();
        }

    }

    private void toastNotif(String msg) {
        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_LONG).show();
    }

    /*
    public static void setRefreshToolbarEnable(CollapsingToolbarLayout collapsingToolbarLayout,
                                               boolean refreshToolbarEnable)
    {
        try {
            Field field = CollapsingToolbarLayout.class.getDeclaredField("mRefreshToolbar");
            field.setAccessible(true);
            field.setBoolean(collapsingToolbarLayout, refreshToolbarEnable);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
