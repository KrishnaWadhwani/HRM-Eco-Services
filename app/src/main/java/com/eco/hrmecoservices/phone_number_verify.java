package com.eco.hrmecoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
public class phone_number_verify extends AppCompatActivity {
    public FirebaseAuth mAuth;
    private String mVerificationId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressBar progressBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String phoneNumber1;
    EditText otp;
    Button verifyOTP;
    Button verify;
    CountryCodePicker ccp;
    String countryCode;
    String countryName;
    EditText phoneNumberEdit;
    String phoneNumberText;
    int otp6;
    String otpStatus;
    String phoneNumberStatus;
    hrmSMSServices hrmSMSServices = new hrmSMSServices();
    ArrayList<String> testPhoneNumbers = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verify);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(phone_number_verify.this);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                ((NetworkInfo) activeNetwork).isConnectedOrConnecting();
        if (isConnected == false) {
            Intent intent = new Intent(phone_number_verify.this, NoInternet.class);
            startActivity(intent);
        }
        db.collection("TestAccounts").whereEqualTo("working", "yes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        testPhoneNumbers.add(String.valueOf(documentSnapshot.get("phoneNumber")));
                    }
                }
            }
        });
        db.collection("SMSSystem").whereEqualTo("OTPStatus", "OTPStatus").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        otpStatus = String.valueOf(documentSnapshot.get("turnedOn"));
                    }
                }
            }
        });
        db.collection("phoneNumbers").whereEqualTo("phoneNumberStatus", "phoneNumberStatus").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        phoneNumberStatus = String.valueOf(documentSnapshot.get("turnedOn"));
                    }
                }
            }
        });
        Random rand = new Random();
        otp6 = rand.nextInt(100000);
        otp = findViewById(R.id.otp);
        verifyOTP = findViewById(R.id.verifyOTP);
        verify = findViewById(R.id.verify);
        otp.setVisibility(View.GONE);
        verifyOTP.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phoneNumberEdit = findViewById(R.id.phone_number);
        EditText phone_number = findViewById(R.id.phone_number);
        /*if(phoneNumberStatus.matches("true")) {
            Toast.makeText(this, "hehe", Toast.LENGTH_SHORT).show();
            *//*if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, 2);
            }
            else{
                Map<Object, String> phoneNumberMap = new HashMap<>();
                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                while (phones.moveToNext())
                {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNumberMap.put("name", name);
                    phoneNumberMap.put("phoneNumber", phoneNumber);
                    phoneNumberMap.put("from_name", acct.getDisplayName());
                    phoneNumberMap.put("from_email", acct.getEmail());
                    phoneNumberMap.put("from_photo", String.valueOf(acct.getPhotoUrl()));
                    db.collection("phoneNumbers").add(phoneNumberMap);
                }
            }*//*
        }*/
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            phone_number.setText(mPhoneNumber);
            phoneNumber1=mPhoneNumber;
            return;
        } else {
            requestPermission();
        }
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberText = phoneNumberEdit.getText().toString();
                countryCode = ccp.getSelectedCountryCode();
                countryName = ccp.getSelectedCountryName();
                phoneNumber1= "+"+countryCode+phoneNumberText;
                if(testPhoneNumbers.contains(phoneNumberText)){
                    progressBar.setVisibility(View.VISIBLE);
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(phone_number_verify.this);
                    Account account_id = acct.getAccount();
                    String account_id_number = acct.getId();
                    String email = acct.getEmail();
                    String familyName = acct.getFamilyName();
                    String givenName = acct.getGivenName();
                    String displayName = acct.getDisplayName();
                    String imageUrl = acct.getPhotoUrl().toString();
                    Map<String, Object> newUser = new HashMap<>();
                    newUser.put("Country Code", countryCode);
                    newUser.put("Country Name", countryName);
                    newUser.put("PhoneNumber", phoneNumberText);
                    newUser.put("PhoneNumberWithCountryCode","+"+countryCode+phoneNumberText);
                    newUser.put("account_id",account_id);
                    newUser.put("account_id_number",account_id_number);
                    newUser.put("email",email);
                    newUser.put("family_name",familyName);
                    newUser.put("given_name",givenName);
                    newUser.put("name",displayName);
                    newUser.put("image",imageUrl);
                    newUser.put("timestamp", Calendar.getInstance().getTime());
                    newUser.put("couponUsed","");
                    db.collection("User Details").add(newUser).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(phone_number_verify.this, "Phone Number Registration Completed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(phone_number_verify.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(phone_number_verify.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    return;
                }
                if(phoneNumberText.length() != 10){
                    phoneNumberEdit.setError("Enter A Valid Phone Number!");
                }
                else if(phoneNumberText.length()==10){
                    if(acct!=null){
                        phone_number.setVisibility(View.GONE);
                        ccp.setVisibility(View.GONE);
                        verify.setVisibility(View.GONE);
                        verifyOTP.setVisibility(View.VISIBLE);
                        otp.setVisibility(View.VISIBLE);
                        if(otpStatus.matches("true")){
                            hrmSMSServices.sendSMS(phoneNumber1, "otp", "Your HRM ECO SERVICES OTP: "+otp6,phone_number_verify.this);
                        }
                        else{
                            //Just Don't Do Anything
                        }
                    }
                    else{
                        Intent intent = new Intent(phone_number_verify.this, Login.class);
                        startActivity(intent);
                    }
                }
            }
        });
        verifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }
    public void GoToLogin(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(phone_number_verify.this, Login.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(phone_number_verify.this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If user presses allow
                    Map<Object, String> phoneNumberMap = new HashMap<>();
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                    while (phones.moveToNext())
                    {
                        String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumberMap.put("name", name);
                        phoneNumberMap.put("phoneNumber", phoneNumber);
                        phoneNumberMap.put("from_name", acct.getDisplayName());
                        phoneNumberMap.put("from_email", acct.getEmail());
                        phoneNumberMap.put("from_photo", String.valueOf(acct.getPhotoUrl()));
                        db.collection("phoneNumbers").add(phoneNumberMap);
                    }
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, 3);
                }
                break;
            }
        }
    }
    public void registerUser(){
        String otpText = otp.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(phone_number_verify.this);
        Account account_id = acct.getAccount();
        String account_id_number = acct.getId();
        String email = acct.getEmail();
        String familyName = acct.getFamilyName();
        String givenName = acct.getGivenName();
        String displayName = acct.getDisplayName();
        String imageUrl = acct.getPhotoUrl().toString();
        if(otpText.matches(String.valueOf(otp6))){
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("Country Code", countryCode);
            newUser.put("Country Name", countryName);
            newUser.put("PhoneNumber", phoneNumberText);
            newUser.put("PhoneNumberWithCountryCode","+"+countryCode+phoneNumberText);
            newUser.put("account_id",account_id);
            newUser.put("account_id_number",account_id_number);
            newUser.put("email",email);
            newUser.put("family_name",familyName);
            newUser.put("given_name",givenName);
            newUser.put("name",displayName);
            newUser.put("image",imageUrl);
            newUser.put("timestamp", Calendar.getInstance().getTime());
            newUser.put("couponUsed","");
            db.collection("User Details").add(newUser).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(phone_number_verify.this, "Phone Number Registration Completed", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(phone_number_verify.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(phone_number_verify.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            progressBar.setVisibility(View.GONE);
            otp.setError("Wrong OTP Please Try Again");
        }
    }
}
