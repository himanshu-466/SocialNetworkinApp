package com.mohit.socialnetworkinapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mohit.socialnetworkinapp.R;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageView image = findViewById(R.id.fullsizeimage);
        String url = getIntent().getStringExtra("imageurl");
        Glide.with(this).load(url).placeholder(R.drawable.placeholder).into(image);
    }
}