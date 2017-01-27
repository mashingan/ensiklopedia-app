package com.rdr.ensiklopediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageFullActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imgView = (ImageView) findViewById(R.id.img_holder);
        Intent intent = getIntent();
        String imgPath = intent.getStringExtra("imgPath");
        Picasso.with(getApplicationContext())
                .load(imgPath)
                .into(imgView);
    }

    public void closeActivity(View view) { finish(); }
}
