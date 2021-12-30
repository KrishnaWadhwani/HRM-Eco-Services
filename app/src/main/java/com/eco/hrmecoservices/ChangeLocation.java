package com.eco.hrmecoservices;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ChangeLocation extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);
    }
    public void save_address(View view){
        EditText pincode = findViewById(R.id.pincode);
        EditText knownName = findViewById(R.id.knownName);
        EditText city = findViewById(R.id.city);
        EditText state = findViewById(R.id.state);
        EditText country = findViewById(R.id.country);
        EditText full_address = findViewById(R.id.full_address);
        SharedPreferences mode = getSharedPreferences("UserLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = mode.edit();
        editor.putString("address", String.valueOf(full_address.getText()));
        editor.putString("pincode", String.valueOf(pincode.getText()));
        editor.putString("knownName", String.valueOf(knownName.getText()));
        editor.putString("city", String.valueOf(city.getText()));
        editor.putString("state", String.valueOf(state.getText()));
        editor.putString("country", String.valueOf(country.getText()));
        editor.apply();
        Intent intent = new Intent(ChangeLocation.this, MainActivity.class);
        startActivity(intent);
    }
}