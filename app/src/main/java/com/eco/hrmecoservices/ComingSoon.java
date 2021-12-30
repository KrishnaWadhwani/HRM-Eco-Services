package com.eco.hrmecoservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ComingSoon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
    }
    public void back_to_homepage(View view){
        Intent intent = new Intent(ComingSoon.this, MainActivity.class);
        startActivity(intent);
    }
}