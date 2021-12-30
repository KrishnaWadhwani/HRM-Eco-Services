package com.eco.hrmecoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.jetbrains.annotations.NotNull;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class OrderPaintingServices extends AppCompatActivity {
    String userPhoneNumber = "";
    String selected_global = "";
    String selected_hint = "";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String price = "";
    EditText editTextAddress;
    String address;
    String pincode;
    String city;
    String state;
    String knownName;
    String country;
    Spinner spinner;
    int totalTax = 0;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_painting_services);
        editTextAddress = findViewById(R.id.userAddress);
        TextView moreInfo = findViewById(R.id.moreInfo);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                ((NetworkInfo) activeNetwork).isConnectedOrConnecting();
        if (isConnected == false) {
            Intent intent = new Intent(OrderPaintingServices.this, NoInternet.class);
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
            Intent toUserLocation = new Intent(OrderPaintingServices.this, UserLocation.class);
            startActivity(toUserLocation);
        } else {
            editTextAddress.setText(address);
        }
        spinner = (Spinner) findViewById(R.id.painting_consultations_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.painting_consultation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        EditText instruction = findViewById(R.id.instruction);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.checkSelfPermission(OrderPaintingServices.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrderPaintingServices.this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){

            }
            else{
                ActivityCompat.requestPermissions(OrderPaintingServices.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(OrderPaintingServices.this);
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
                        else{
                            Toast.makeText(OrderPaintingServices.this, "Some Error Occurred While Loading Taxes", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(OrderPaintingServices.this, NoInternet.class);
            startActivity(intent);
        }
    }

    public void changeLocation(View view) {
        Intent intent = new Intent(OrderPaintingServices.this, UserLocation.class);
        startActivity(intent);
    }

    public void moreInfoActivity(View view){
        Intent intent = new Intent(OrderPaintingServices.this, moreInfoActivity.class);
        intent.putExtra("showInfoOf", "PaintingService");
        startActivity(intent);
    }

    public void addToCart(View view) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(OrderPaintingServices.this);
        EditText instructions = findViewById(R.id.instruction);
        if (spinner.getSelectedItem().toString().lastIndexOf("Select A Type Of Consultation") != -1) {
            Toast.makeText(OrderPaintingServices.this, "Please Select A Type Of Consultation", Toast.LENGTH_SHORT).show();
            return;
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("House Painting Service Add To Cart");
            db.collection("Prices")
                    .whereEqualTo("ServiceId","PaintingService").limit(1).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
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
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    price = (String) doc.get("PaintingServicePrice");
                                    int priceSet = Integer.parseInt(price);
                                    int totalPrice = priceSet+totalTax;
                                    alert.setMessage("Name: " + personName + "\n" + "Email: " + personEmail + "\n" +  "Address: " + address +"\n" + "Total Price: " + totalPrice + "₹ (Included Taxes "+totalTax+"₹)");
                                    alert.setPositiveButton("Yes, Add To Cart", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            if (address.isEmpty()) {
                                                Toast.makeText(OrderPaintingServices.this, "Set The Location", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(OrderPaintingServices.this, UserLocation.class);
                                                startActivity(intent);
                                                return;
                                            }
                                            Random rand = new Random();
                                            long cartId = rand.nextInt(1000000000);
                                            String cartIdString = String.valueOf(cartId);
                                            Map<String, Object> cartData = new HashMap<>();
                                            cartData.put("category", "HousePainting");
                                            cartData.put("cartId", cartIdString);
                                            cartData.put("name", personName);
                                            cartData.put("given_name", personGivenName);
                                            cartData.put("family_name", personFamilyName);
                                            cartData.put("phoneNumber", userPhoneNumber);
                                            cartData.put("email", personEmail);
                                            cartData.put("image", personImage);
                                            cartData.put("account_id", personId);
                                            cartData.put("timestamp", Calendar.getInstance().getTime());
                                            cartData.put("timeStamp", String.valueOf(Calendar.getInstance().getTime()));
                                            cartData.put("full_address", address);
                                            cartData.put("pincode", pincode);
                                            cartData.put("city", city);
                                            cartData.put("status", "pending");
                                            cartData.put("tax", totalTax+"₹");
                                            cartData.put("state", state);
                                            cartData.put("country", country);
                                            cartData.put("knownName", knownName);
                                            cartData.put("addedTo", "Cart");
                                            cartData.put("service_needed", spinner.getSelectedItem().toString()+"(House Painting)");
                                            cartData.put("serviceimage", "https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fpainting.jpg?alt=media&token=7d25835f-8c2b-4acd-8dd1-a74d4b83779b");
                                            cartData.put("Price", totalPrice +"₹");
                                            cartData.put("MoreInstructionsForUs", instructions.getText().toString());
                                            db.collection("Cart").add(cartData)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(OrderPaintingServices.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(OrderPaintingServices.this, MainActivity.class);
                                                            intent.putExtra("toCartFrag", "toCartFrag");
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(OrderPaintingServices.this, "Some Error Occurred Please Try Again Later", Toast.LENGTH_SHORT).show();
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
                            }
                            else {
                                Toast.makeText(OrderPaintingServices.this, "Some Error Occurred While Fetching Prices", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Intent intent = new Intent(OrderPaintingServices.this, ChangeLocation.class);
                startActivity(intent);
                Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
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
}