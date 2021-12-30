package com.eco.hrmecoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class coupon_code extends AppCompatActivity {
    EditText coupon_code_input;
    Button apply_coupon_code;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> activeCouponCodes;
    int size = 0;
    int discountPercent;
    String priceBefore;
    int priceBeforeInt;
    float priceAfter;
    float totalPrice;
    float priceForOneItem;
    long cartId;
    String couponUsed = "";
    GoogleSignInAccount acct;
    com.airbnb.lottie.LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_code);
        animationView = findViewById(R.id.animationView);
        animationView.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        cartId = Long.parseLong(bundle.getString("cartId"));
        acct = GoogleSignIn.getLastSignedInAccount(this);
        db.collection("couponCodes").whereEqualTo("couponCodeIsActive", "true").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        size+=1;
                    }
                }
            }
        });
        db.collection("User Details").whereEqualTo("email", acct.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        couponUsed+=documentSnapshot.get("couponUsed");
                    }
                }
            }
        });
        activeCouponCodes = new ArrayList<String>(size);
        db.collection("couponCodes").whereEqualTo("couponCodeIsActive", "true").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        activeCouponCodes.add(String.valueOf(documentSnapshot.get("couponCode")));
                    }
                }
            }
        });
        db.collection("Cart")
                .whereEqualTo("addedTo", "Cart").whereEqualTo("email", acct.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String price = String.valueOf(document.get("Price"));
                                StringBuilder builder = new StringBuilder(price);
                                StringBuilder ForPrice = builder.deleteCharAt(price.length()-1);
                                float priceCount = Float.parseFloat(String.valueOf(ForPrice));
                                totalPrice+=priceCount;
                            }
                        }
                        else{
                            Toast.makeText(coupon_code.this, "Some Error Occurred While Getting Total Price", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        coupon_code_input = findViewById(R.id.coupon_code_input);
        apply_coupon_code = findViewById(R.id.check_coupon_code);
    }
    String coupon_code_input_text;
    public void checkCouponCode(View view){
        if(couponUsed.lastIndexOf(coupon_code_input.getText().toString())!=-1){
            coupon_code_input.setError("Coupon Already Used");
            animationView.setVisibility(View.GONE);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Coupon Are For Only One Time Use")
                    .setMessage("You Can Use \""+coupon_code_input.getText().toString()+"\" For Only 1 Time After This You Will Not Be Able To Use This Code")
                    .setPositiveButton("Apply Coupon", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            coupon_code_input_text = coupon_code_input.getText().toString();
                            if (activeCouponCodes.contains(coupon_code_input_text)){
                                db.collection("couponCodes").whereEqualTo("couponCode", coupon_code_input_text).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                                discountPercent = Integer.parseInt(String.valueOf(documentSnapshot.get("discount")));
                                            }
                                            discountPercent = 100-discountPercent;
                                            priceAfter = (totalPrice/100)*discountPercent;
                                            db.collection("Cart").whereEqualTo("email",acct.getEmail()).whereEqualTo("addedTo","Cart").whereEqualTo("cartId", String.valueOf(cartId)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                                            db.collection("Cart").document(queryDocumentSnapshot.getId()).update("Price", priceAfter+"₹");
                                                        }
                                                        animationView.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });
                                            db.collection("User Details").whereEqualTo("email",acct.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                                            db.collection("User Details").document(queryDocumentSnapshot.getId()).update("couponUsed", coupon_code_input.getText().toString()+","+couponUsed);
                                                        }
                                                        animationView.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });
                                            new java.util.Timer().schedule(
                                                    new java.util.TimerTask(){
                                                        @Override
                                                        public void run(){
                                                            Intent intent = new Intent(coupon_code.this, MainActivity.class);
                                                            intent.putExtra("toCartFrag", "toCartFrag");
                                                            startActivity(intent);
                                                        }
                                                    },2000
                                            );
                                        }
                                    }
                                });
                            }
                            else{
                                coupon_code_input.setError("Invalid Coupon Code");
                                animationView.setVisibility(View.GONE);
                            }
                        }
                    })
                    .setNegativeButton("Leave It", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
    }
}