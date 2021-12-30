package com.eco.hrmecoservices;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class cart_fragment extends Fragment {
    float totalPrice = 0;
    String userPhoneNumber = "";
    String message = "";
    String smsStatus = "";
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        Button start_shopping = rootView.findViewById(R.id.start_shopping);
        TextView checkoutPrice = rootView.findViewById(R.id.checkoutPrice);
        TextView paymentMethod = rootView.findViewById(R.id.paymentMethod);
        TextView placing_order = rootView.findViewById(R.id.placing_order);
        placing_order.setVisibility(View.GONE);
        Button placeOrder = rootView.findViewById(R.id.placeOrder);
        Button cancelCheckout = rootView.findViewById(R.id.cancel);
        Button checkout = rootView.findViewById(R.id.checkout);
        androidx.appcompat.widget.Toolbar checkoutBox = rootView.findViewById(R.id.checkoutBox);
        com.airbnb.lottie.LottieAnimationView animationView = rootView.findViewById(R.id.empty_cart);
        com.airbnb.lottie.LottieAnimationView loader = rootView.findViewById(R.id.loader);
        loader.setVisibility(View.GONE);
        TextView cartEmpty = rootView.findViewById(R.id.cartEmpty);
        TextView cartEmptyDesc = rootView.findViewById(R.id.cart_empty_desc);
        ProgressBar bar = rootView.findViewById(R.id.progressBar);
        checkoutBox.setVisibility(View.GONE);
        animationView.setVisibility(View.GONE);
        cancelCheckout.setVisibility(View.GONE);
        start_shopping.setVisibility(View.GONE);
        placeOrder.setVisibility(View.GONE);
        paymentMethod.setVisibility(View.GONE);
        cartEmpty.setVisibility(View.GONE);
        cartEmptyDesc.setVisibility(View.GONE);
        checkoutPrice.setVisibility(View.GONE);
        checkout.setVisibility(View.GONE);
        ArrayList<model> datalist;
        FirebaseFirestore db;
        CartAdapter adapter;
        RecyclerView recview;
        bar.setVisibility(View.VISIBLE);
        recview = (RecyclerView) rootView.findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        datalist=new ArrayList<>();
        adapter = new CartAdapter(datalist);
        recview.setAdapter(adapter);
        db=FirebaseFirestore.getInstance();
        String personEmail = acct.getEmail();
        db.collection("User Details").whereEqualTo("email", personEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        userPhoneNumber = String.valueOf(documentSnapshot.get("PhoneNumber"));
                    }
                }
            }
        });
        db.collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        message = String.valueOf(documentSnapshot.get("message"));
                    }
                }
            }
        });
        db.collection("SMSSystem").whereEqualTo("SMSStatus", "SMSStatusForOrderConfirmation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        smsStatus = String.valueOf(documentSnapshot.get("turnedOn"));
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
                            Toast.makeText(getActivity(), "Some Error Occurred While Loading Total Price", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            db.collection("Cart")
                .whereEqualTo("addedTo", "Cart").whereEqualTo("email", personEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot d:list){
                            model obj=d.toObject(model.class);
                            datalist.add(obj);
                        }
                        if(!datalist.isEmpty()){
                            Button start_shopping = rootView.findViewById(R.id.start_shopping);
                            com.airbnb.lottie.LottieAnimationView animationView = rootView.findViewById(R.id.empty_cart);
                            TextView cartEmpty = rootView.findViewById(R.id.cartEmpty);
                            TextView cartEmptyDesc = rootView.findViewById(R.id.cart_empty_desc);
                            animationView.setVisibility(View.GONE);
                            start_shopping.setVisibility(View.GONE);
                            cartEmpty.setVisibility(View.GONE);
                            cartEmptyDesc.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            bar.setVisibility(View.GONE);
                            checkout.setVisibility(View.VISIBLE);
                            loader.setVisibility(View.GONE);
                            placing_order.setVisibility(View.GONE);
                        }
                        else{
                            Button start_shopping = rootView.findViewById(R.id.start_shopping);
                            com.airbnb.lottie.LottieAnimationView animationView = rootView.findViewById(R.id.empty_cart);
                            TextView cartEmpty = rootView.findViewById(R.id.cartEmpty);
                            TextView cartEmptyDesc = rootView.findViewById(R.id.cart_empty_desc);
                            animationView.setVisibility(View.VISIBLE);
                            start_shopping.setVisibility(View.VISIBLE);
                            cartEmpty.setVisibility(View.VISIBLE);
                            cartEmptyDesc.setVisibility(View.VISIBLE);
                            bar.setVisibility(View.GONE);
                            checkout.setVisibility(View.GONE);
                            loader.setVisibility(View.GONE);
                            placing_order.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getActivity(), "Some Error Occurred Can't Fetch Your Cart This Moment", Toast.LENGTH_SHORT).show();
                    }
                });
            cancelCheckout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkout.setVisibility(View.VISIBLE);
                    checkoutBox.setVisibility(View.GONE);
                    cancelCheckout.setVisibility(View.GONE);
                    checkoutPrice.setVisibility(View.GONE);
                    paymentMethod.setVisibility(View.GONE);
                    placeOrder.setVisibility(View.GONE);
                    loader.setVisibility(View.GONE);
                    placing_order.setVisibility(View.GONE);
                }
            });
            checkout.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    checkout.setVisibility(View.GONE);
                    checkoutBox.setVisibility(View.VISIBLE);
                    cancelCheckout.setVisibility(View.VISIBLE);
                    checkoutPrice.setVisibility(View.VISIBLE);
                    checkoutPrice.setText("Total Price: "+totalPrice+"₹");
                    paymentMethod.setVisibility(View.VISIBLE);
                    placeOrder.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                    placing_order.setVisibility(View.GONE);
                }
            });
            placeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkout.setVisibility(View.GONE);
                    checkoutBox.setVisibility(View.VISIBLE);
                    cancelCheckout.setVisibility(View.GONE);
                    checkoutPrice.setVisibility(View.GONE);
                    paymentMethod.setVisibility(View.GONE);
                    placeOrder.setVisibility(View.GONE);
                    loader.setVisibility(View.VISIBLE);
                    placing_order.setVisibility(View.VISIBLE);
                    placing_order.setVisibility(View.VISIBLE);
                    String userEmail = acct.getEmail();
                    db.collection("Cart")
                            .whereEqualTo("email", userEmail)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()){
                                            db.collection("Cart").document(document.getId()).update("addedTo", "Orders");
                                        }
                                        Toast.makeText(getActivity(), "Order Placed", Toast.LENGTH_SHORT).show();
                                        if(smsStatus.matches("true")){
                                            hrmSMSServices.sendSMS(userPhoneNumber, "orderNotification", message, getActivity());
                                        }
                                        else{
                                            // Just Don't Do Anything
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                                            NotificationChannel channel = new NotificationChannel("HRMNotifications","HRMNotifications", NotificationManager.IMPORTANCE_DEFAULT);
                                            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                                            manager.createNotificationChannel(channel);
                                        }
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "HRMNotifications")
                                                .setSmallIcon(R.drawable.ic_baseline_cleaning_services_24)
                                                .setContentTitle("HRM ECO SERVICES ORDER CONFIRMED")
                                                .setStyle(new NotificationCompat.BigTextStyle()
                                                        .bigText(message)
                                                )
                                                .setPriority(NotificationCompat.PRIORITY_HIGH);
                                        String audioUrl = "https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/Sounds%2F1636479962666-voicemaker.in-speech.mp3?alt=media&token=d759b675-10f0-49d4-a518-7b310682e85e";
                                        MediaPlayer mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        try {
                                            mediaPlayer.setDataSource(audioUrl);
                                            mediaPlayer.prepare();
                                            mediaPlayer.start();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        NotificationManagerCompat manager = NotificationManagerCompat.from(getActivity());
                                        manager.notify(999, builder.build());
                                        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                                new order_fragment()).commit();
                                        BottomNavigationView bottomnav = getActivity().findViewById(R.id.bottom_navigation);
                                        bottomnav.setSelectedItemId(R.id.orders);
                                    }
                                    else{
                                        Toast.makeText(getActivity(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                                    }
                            }});
                    }
            });
        return rootView;
    }
}
