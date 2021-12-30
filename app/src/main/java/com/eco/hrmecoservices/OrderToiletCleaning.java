package com.eco.hrmecoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class OrderToiletCleaning extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userPhoneNumber = "";
    EditText toilets;
    EditText editTextAddress;
    EditText instructions;
    String address;
    String pincode;
    String city;
    String state;
    String knownName;
    String country;
    int totalTax = 0;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_toilet_cleaning);
        editTextAddress = findViewById(R.id.userAddress);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                ((NetworkInfo) activeNetwork).isConnectedOrConnecting();
        if (isConnected == false) {
            Intent intent = new Intent(OrderToiletCleaning.this, NoInternet.class);
            startActivity(intent);
        }
        SharedPreferences userLocation = getSharedPreferences("UserLocation", MODE_PRIVATE);
        address = userLocation.getString("address", "");
        pincode = userLocation.getString("pincode", "");
        city = userLocation.getString("city", "");
        state = userLocation.getString("state", "");
        knownName = userLocation.getString("knownName", "");
        country = userLocation.getString("country", "");
        if (address.isEmpty()) {
            Intent toUserLocation = new Intent(OrderToiletCleaning.this, UserLocation.class);
            startActivity(toUserLocation);
        } else {
            editTextAddress.setText(address);
        }
        toilets = findViewById(R.id.toilets);
        editTextAddress = findViewById(R.id.userAddress);
        instructions = findViewById(R.id.instruction);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(OrderToiletCleaning.this);
        db.collection("Taxes")
                .whereEqualTo("Tax", "Tax")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String tax = String.valueOf(document.get("Tax Amount"));
                                int taxCount = Integer.parseInt(tax);
                                totalTax+=taxCount;
                            }
                        }
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                ((NetworkInfo) activeNetwork).isConnectedOrConnecting();
        if (isConnected == false) {
            Intent intent = new Intent(OrderToiletCleaning.this, NoInternet.class);
            startActivity(intent);
        }
    }
    public void changeLocation(View view) {
        Intent intent = new Intent(OrderToiletCleaning.this, UserLocation.class);
        startActivity(intent);
    }
    String latitude;
    String longitude;
    @SuppressLint("MissingPermission")
    public void getCurrentLocation(View view){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location!=null){
                        latitude=String.valueOf(location.getLatitude());
                        longitude=String.valueOf(location.getLongitude());
                        conf_location();
                    }
                    else{
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                latitude = String.valueOf(location1.getLatitude());
                                longitude = String.valueOf(location1.getLongitude());
                                conf_location();
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        }
        else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
    public void conf_location(){
        double longitude_local = Double.parseDouble(longitude);
        double latitude_local = Double.parseDouble(latitude);
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude_local, longitude_local, 1);
        }
        catch (Exception e){
            Toast.makeText(this, "Some Problem Occurred While Decoding Your Lat Long", Toast.LENGTH_SHORT).show();
        }
        address = addresses.get(0).getAddressLine(0);
        city = addresses.get(0).getLocality();
        state = addresses.get(0).getAdminArea();
        country = addresses.get(0).getCountryName();
        pincode = addresses.get(0).getPostalCode();
        knownName = addresses.get(0).getFeatureName();
        editTextAddress.setText(address);
    }
    public void moreInfoActivity(View view){
        Intent intent = new Intent(OrderToiletCleaning.this, moreInfoActivity.class);
        intent.putExtra("showInfoOf", "ToiletCleaning");
        startActivity(intent);
    }
    public void addToCart(View view) {
        /*
        ------------------------
        EditText rooms;         |
        EditText hall;          |
        EditText kitchen;       |
        EditText bathroom;      |
        EditText toilet;        |
        EditText seatsOnSofa;   |
        EditText balcony;       |
        EditText chimney;       |
        EditText lawn;          |
        EditText carpets;       |
        ------------------------
        */
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        String toiletString = toilets.getText().toString();
        if (toiletString.isEmpty()) {
            toilets.setError("This Field Can't Be Empty");
        } else {
            if (address.isEmpty()) {
                Toast.makeText(this, "Set The Location", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OrderToiletCleaning.this, UserLocation.class);
                startActivity(intent);
                return;
            }
            if (acct != null) {
                /*db.collection("Prices")
                        .whereEqualTo("ServiceName", "HomeCleaning")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            String hallCleaningPriceString;
                            String roomCleaningPriceString;
                            String kitchenCleaningPriceString;
                            String bathroomCleaningPriceString;
                            String toiletCleaningPriceString;
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("DATA", document.getId() + " => " + document.getData());
                                        if(document.getId().matches("RoomCleaning")){
                                            roomCleaningPriceString=document.get("RoomCleaningPrice").toString();
                                        }
                                        if(document.getId().matches("HallCleaning")){
                                            hallCleaningPriceString=document.get("HallCleaningPrice").toString();
                                        }
                                        if(document.getId().matches("BathroomCleaning")){
                                            bathroomCleaningPriceString=document.get("BathroomCleaningPrice").toString();
                                        }
                                        if(document.getId().matches("KitchenCleaning")){
                                            kitchenCleaningPriceString=document.get("KitchenCleaningPrice").toString();
                                        }
                                        if(document.getId().matches("ToiletCleaning")){
                                            toiletCleaningPriceString=document.get("ToiletCleaningPrice").toString();
                                        }
                                    }
                                    *//*Log.d("DATAROOM", roomCleaningPriceString);
                                    Log.d("DATAHALL", hallCleaningPriceString);
                                    Log.d("DATABATHROOM", bathroomCleaningPriceString);
                                    Log.d("DATAKITCHEN", kitchenCleaningPriceString);
                                    Log.d("DATATOILET", toiletCleaningPriceString);*//*
                                } else {
                                    Log.w("DATA", "Error getting documents.", task.getException());
                                }
                            }
                        });*/
                AlertDialog.Builder alert = new AlertDialog.Builder(OrderToiletCleaning.this);
                alert.setTitle("Toilet Cleaning Service Add To Cart");
                db.collection("Prices")
                        .whereEqualTo("ServiceId", "ToiletCleaning")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            String BathroomsCleaningPriceString;
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("DATA", document.getId() + " => " + document.getData());
                                        BathroomsCleaningPriceString = document.get("ToiletCleaningPrice").toString();
                                    }
                                    int roomTotalPrice = Integer.parseInt(BathroomsCleaningPriceString)*Integer.parseInt(toiletString);
                                    int totalPrice = roomTotalPrice + totalTax;
                                    String personName = acct.getDisplayName();
                                    String personGivenName = acct.getGivenName();
                                    String personFamilyName = acct.getFamilyName();
                                    String personEmail = acct.getEmail();
                                    String personId = acct.getId();
                                    String personImage = acct.getPhotoUrl().toString();
                                    db.collection("User Details").whereEqualTo("email", personEmail)
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    userPhoneNumber = (String) document.get("PhoneNumberWithCountryCode");
                                                }
                                            }
                                        }
                                    });
                                    alert.setMessage("Name: " + personName + "\n" + "Email: " + personEmail + "\n" + "Address: " + address + "\n" + "Total Price: " + totalPrice + "₹ (Included Taxes "+totalTax+"₹)");
                                    alert.setPositiveButton("Yes, Add To Cart", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Random rand = new Random();
                                            long cartId = rand.nextInt(1000000000);
                                            String cartIdString = String.valueOf(cartId);
                                            Map<String, Object> cartData = new HashMap<>();
                                            cartData.put("category", "BathroomCleaning");
                                            cartData.put("cartId", cartIdString);
                                            cartData.put("name", personName);
                                            cartData.put("given_name", personGivenName);
                                            cartData.put("family_name", personFamilyName);
                                            cartData.put("phoneNumber", userPhoneNumber);
                                            cartData.put("email", personEmail);
                                            cartData.put("image", personImage);
                                            cartData.put("account_id", personId);
                                            cartData.put("timeStamp", String.valueOf(Calendar.getInstance().getTime()));
                                            cartData.put("timestamp", Calendar.getInstance().getTime());
                                            cartData.put("full_address", address);
                                            cartData.put("pincode", pincode);
                                            cartData.put("city", city);
                                            cartData.put("state", state);
                                            cartData.put("tax", totalTax +"₹");
                                            cartData.put("country", country);
                                            cartData.put("addedTo", "Cart");
                                            cartData.put("status", "pending");
                                            cartData.put("knownName", knownName);
                                            cartData.put("service_needed", "Full Toilet Cleaning");
                                            cartData.put("ToiletCount", toiletString);
                                            cartData.put("numberOf_CopyOf_selected_hint", toiletString);
                                            cartData.put("serviceimage", "https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Ftoilet.jpeg?alt=media&token=ff357865-5fea-4f1a-b88b-64f9a483a9ff");
                                            cartData.put("Price", totalPrice +"₹");
                                            cartData.put("MoreInstructionsForUs", instructions.getText().toString());
                                            db.collection("Cart").add(cartData)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(OrderToiletCleaning.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(OrderToiletCleaning.this, MainActivity.class);
                                                            intent.putExtra("toCartFrag", "toCartFrag");
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(OrderToiletCleaning.this, "Some Error Occurred Please Try Again Later", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                                    alert.setNegativeButton("Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            });

                                    alert.show();
                                }
                                else {
                                    Toast.makeText(OrderToiletCleaning.this, "Some Error Occurred While Getting The Prices", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }
}