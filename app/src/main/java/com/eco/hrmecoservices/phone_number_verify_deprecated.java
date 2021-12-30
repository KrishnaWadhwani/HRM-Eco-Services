/*
package com.eco.hrmecoservices;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_verify);
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                ((NetworkInfo) activeNetwork).isConnectedOrConnecting();
        if (isConnected == false) {
            Intent intent = new Intent(phone_number_verify.this, NoInternet.class);
            startActivity(intent);
        }
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
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) ==
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager)   this.getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();
            phone_number.setText(mPhoneNumber);
            return;
        } else {
            requestPermission();
        }
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(phone_number_verify.this, "Invalid Request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    otp.setText("Our SMS Servers Are Currently Busy Please Try Again Tomorrow");
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
            }
        };
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumberText = phoneNumberEdit.getText().toString();
                countryCode = ccp.getSelectedCountryCode();
                countryName = ccp.getSelectedCountryName();
                phoneNumber1= "+"+countryCode+phoneNumberText;
                if(phoneNumberText.length() != 10){
                    phoneNumberEdit.setError("Enter A Valid Phone Number!");
                }
                else if(phoneNumberText.length()==10){
                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(phone_number_verify.this);
                    if(acct!=null){
                        phone_number.setVisibility(View.GONE);
                        ccp.setVisibility(View.GONE);
                        verify.setVisibility(View.GONE);
                        verifyOTP.setVisibility(View.VISIBLE);
                        otp.setVisibility(View.VISIBLE);
                        Toast.makeText(phone_number_verify.this, "otp sent", Toast.LENGTH_SHORT).show();
                        sendOTP(phoneNumber1);
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
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(otp.getText().toString());
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "Register Your Phone Number",
                    Toast.LENGTH_LONG).show();
        return false;
    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                EditText phone_number = findViewById(R.id.phone_number);
                TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();
                phone_number.setText(mPhoneNumber);
                break;
        }
    }
    public void GoToLogin(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(phone_number_verify.this, Login.class);
        startActivity(intent);
    }
    public void sendOTP(String phoneNumber){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Toast.makeText(this, "otp senttt", Toast.LENGTH_SHORT).show();
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(phone_number_verify.this);
        Account account_id = acct.getAccount();
        String account_id_number = acct.getId();
        String email = acct.getEmail();
        String familyName = acct.getFamilyName();
        String givenName = acct.getGivenName();
        String displayName = acct.getDisplayName();
        String imageUrl = acct.getPhotoUrl().toString();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "success");
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
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(phone_number_verify.this, Login.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(phone_number_verify.this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.d("login", "failure");
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                otp.setError("OTP Is Invalid, Please Try Again");
                            }
                        }
                    }
                });
    }
    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithPhoneAuthCredential(credential);
    }
}
*/
