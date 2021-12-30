package com.eco.hrmecoservices;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class order_fragment extends Fragment {
    float totalPrice = 0;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order, container, false);
        ProgressBar progressBar = rootView.findViewById(R.id.progressBar1);
        TextView ordersEmpty = rootView.findViewById(R.id.ordersEmpty);
        TextView ordersEmptyDesc = rootView.findViewById(R.id.orders_empty_desc);
        Button startShopping = rootView.findViewById(R.id.start_shopping1);
        Button showmoreinfo = rootView.findViewById(R.id.showmoreinfo);
        Button hide = rootView.findViewById(R.id.hide);
        androidx.appcompat.widget.Toolbar moreInfoBox = rootView.findViewById(R.id.moreInfoBox);
        moreInfoBox.setVisibility(View.GONE);
        hide.setVisibility(View.GONE);
        showmoreinfo.setVisibility(View.GONE);
        TextView totalAmount = rootView.findViewById(R.id.totalAmount);
        totalAmount.setVisibility(View.GONE);
        com.airbnb.lottie.LottieAnimationView no_orders = rootView.findViewById(R.id.no_orders);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
        progressBar.setVisibility(View.VISIBLE);
        ordersEmpty.setVisibility(View.GONE);
        ordersEmptyDesc.setVisibility(View.GONE);
        startShopping.setVisibility(View.GONE);
        no_orders.setVisibility(View.GONE);
        ArrayList<ordersmodel> datalist1;
        FirebaseFirestore db;
        OrdersAdapter adapter;
        RecyclerView recview;
        recview = (RecyclerView) rootView.findViewById(R.id.recview1);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        datalist1=new ArrayList<>();
        adapter = new OrdersAdapter(datalist1);
        recview.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        String personEmail = acct.getEmail();
        db.collection("Cart")
                .whereEqualTo("addedTo", "Orders").whereEqualTo("status", "pending").whereEqualTo("email", acct.getEmail())
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
                            /**/
                        }
                        else{
                            Toast.makeText(getActivity(), "Some Error Occurred While Loading Total Price", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreInfoBox.setVisibility(View.GONE);
                    totalAmount.setVisibility(View.GONE);
                    totalAmount.setText("Total Amount To Pay: "+String.valueOf(totalPrice)+"₹");
                    showmoreinfo.setVisibility(View.VISIBLE);
                    hide.setVisibility(View.GONE);
                }
            });
            showmoreinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moreInfoBox.setVisibility(View.VISIBLE);
                    totalAmount.setVisibility(View.VISIBLE);
                    totalAmount.setText("Total Amount To Pay: "+String.valueOf(totalPrice)+"₹");
                    hide.setVisibility(View.VISIBLE);
                    showmoreinfo.setVisibility(View.GONE);
                }
            });
            db.collection("Cart")
                .whereEqualTo("addedTo", "Orders").whereEqualTo("status", "pending").whereEqualTo("email", personEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            ordersmodel obj = d.toObject(ordersmodel.class);
                            datalist1.add(obj);
                        }
                        if (!(datalist1.isEmpty())) {
                            progressBar.setVisibility(View.GONE);
                            showmoreinfo.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            no_orders.setVisibility(View.VISIBLE);
                            ordersEmpty.setVisibility(View.VISIBLE);
                            ordersEmptyDesc.setVisibility(View.VISIBLE);
                            startShopping.setVisibility(View.VISIBLE);
                        }
                    }});
        return rootView;
    }
}
