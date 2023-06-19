package com.emmanuel.biblevirst;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Info_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}