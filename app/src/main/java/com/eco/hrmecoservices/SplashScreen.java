package com.eco.hrmecoservices;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashScreen extends AppCompatActivity {
    private View decorView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility == 0){
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences getShared = getSharedPreferences("AppTheme", MODE_PRIVATE);
        String val = getShared.getString("theme", "NightModeOff");
        if(val.equals("NightModeOn")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {
                if (user != null) {
                    db.collection("User Details").whereEqualTo("email", user.getEmail()).limit(1)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    boolean taskbool = task.getResult().isEmpty();
                                    if(taskbool){
                                        Intent intent = new Intent(SplashScreen.this, phone_number_verify.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        SharedPreferences getShared = getSharedPreferences("UserLocation", MODE_PRIVATE);
                                        String address = getShared.getString("address", "");
                                        if(address.isEmpty()){
                                            Intent intent = new Intent(SplashScreen.this, UserLocation.class);
                                            startActivity(intent);
                                        }
                                        else{
                                            Intent i = new Intent(SplashScreen.this,
                                                    MainActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                }
                            });
                } else {
                    Intent i = new Intent(SplashScreen.this,
                            Login.class);
                    startActivity(i);
                }
            }

        }, 2*1000);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars(){
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}