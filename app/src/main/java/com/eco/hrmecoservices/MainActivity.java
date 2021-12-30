package com.eco.hrmecoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        setContentView(R.layout.activity_main);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                ((NetworkInfo) activeNetwork).isConnectedOrConnecting();
        if(isConnected==false){
            Intent intent = new Intent(MainActivity.this, NoInternet.class);
            startActivity(intent);
        }
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Don't Do Anything
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
        Log.d("email", "email:"+user.getEmail());
        BottomNavigationView bottomnav=findViewById(R.id.bottom_navigation);
        bottomnav.setOnNavigationItemSelectedListener(navListner);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new home_fragment()).commit();
        db.collection("User Details").whereEqualTo("email", user.getEmail()).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean taskbool = task.getResult().isEmpty();
                        if(taskbool){
                            Intent intent = new Intent(MainActivity.this, phone_number_verify.class);
                            startActivity(intent);
                        }
                        else{
                            String phoneNumber = "";
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                 phoneNumber = doc.get("PhoneNumber").toString();
                            }
                            SharedPreferences mode = getSharedPreferences("UserPhoneNumber", MODE_PRIVATE);
                            SharedPreferences.Editor editor = mode.edit();
                            editor.putString("PhoneNumber", phoneNumber);
                        }
                    }
                });
        try{
            String toCartFrag = getIntent().getStringExtra("toCartFrag");
            if (toCartFrag.matches("toCartFrag")){
                bottomnav.setSelectedItemId(R.id.cart);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new cart_fragment()).commit();
            }
        }
        catch (Exception e){

        }
    }
    @Override
    public void onResume(){
        super.onResume();
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                ((NetworkInfo) activeNetwork).isConnectedOrConnecting();
        if(isConnected==false){
            Intent intent = new Intent(MainActivity.this, NoInternet.class);
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        return super.onCreateOptionsMenu(menu);
    }
    public void comingSoon(View view){
        Intent intent = new Intent(MainActivity.this, ComingSoon.class);
        startActivity(intent);
    }
    public void CallUs(View view){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);

        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+91-8950961643"));
            startActivity(intent);
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListner =
        new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()){
                case R.id.home:
                    selectedFragment = new home_fragment();
                    break;
                case R.id.cart:
                    selectedFragment = new cart_fragment();
                    break;
                case R.id.orders:
                    selectedFragment = new order_fragment();
                    break;
                case R.id.settings:
                    selectedFragment = new settings_fragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
            return true;
        }
    };
    public void OrderSanitization(View v){
        Intent i = new Intent(MainActivity.this, OrderSanitization.class);
        startActivity(i);
    }
    public void OrderHomeCleaning(View v){
        Intent i = new Intent(MainActivity.this, OrderHomeCleaning.class);
        startActivity(i);
    }
    public void OrderRoomCleaning(View v){
        Intent i = new Intent(MainActivity.this, OrderRoomCleaning.class);
        startActivity(i);
    }
    public void OrderSofaCleaning(View v){
        Intent i = new Intent(MainActivity.this, OrderSofaCleaning.class);
        startActivity(i);
    }
    public void OrderKitchenCleaning(View v){
        Intent i = new Intent(MainActivity.this, OrderKitchenCleaning.class);
        startActivity(i);
    }
    public void OrderCarpetCleaning(View v){
        Intent i = new Intent(MainActivity.this, OrderCarpetCleaning.class);
        startActivity(i);
    }
    public void OrderCarCleaning(View v){
        Intent i = new Intent(MainActivity.this, OrderCarCleaning.class);
        startActivity(i);
    }
    public void OrderBathroomCleaning(View v){
        Intent i = new Intent(MainActivity.this, OrderBathroomCleaning.class);
        startActivity(i);
    }
    public void OrderToiletCleaning(View v){
        Intent i = new Intent(MainActivity.this, OrderToiletCleaning.class);
        startActivity(i);
    }
    public void OrderPaintingServices(View v){
        Intent i = new Intent(MainActivity.this, OrderPaintingServices.class);
        startActivity(i);
    }
    public void logout(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Logout From HRM Eco Services?");
        alert.setMessage("Caution! Logging Out From HRM Eco Services");

        alert.setPositiveButton("Yes, Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(MainActivity.this, Login.class);
                startActivity(login);
            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    public void start_shopping(View view) {
        BottomNavigationView bottomnav = findViewById(R.id.bottom_navigation);
        bottomnav.setSelectedItemId(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new home_fragment()).commit();
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
    public void change_location_settings(View view){
        Intent intent = new Intent(MainActivity.this, UserLocation.class);
        startActivity(intent);
    }
    /*public void removeFromCart(View view){
        *//*FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = rootRef.collection("Cart");
        Query query = itemsRef.whereEqualTo("cartId", datalist.get(position).getCartId());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        itemsRef.document(document.getId()).delete();
                    }
                } else {
                    //Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });*//*
    }*/
}