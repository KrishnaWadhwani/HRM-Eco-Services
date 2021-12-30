package com.eco.hrmecoservices;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.cartViewHolder>{

    ArrayList<model> datalist;

    public CartAdapter(ArrayList<model> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @NotNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartcard, parent, false);
        return new cartViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull CartAdapter.cartViewHolder holder, int position) {
        Picasso.get().load(datalist.get(position).getServiceimage()).placeholder(R.drawable.loading).into(holder.serviceImage);
        holder.serviceName.setText("Service: "+datalist.get(position).getService_needed());
        holder.price.setText("Total Price: "+datalist.get(position).getPrice()+" (Already Included Tax "+datalist.get(position).getTax()+")");
        holder.address.setText("Address: "+datalist.get(position).getFull_address());
        holder.instructions.setText("Instructions For Us: "+datalist.get(position).getMoreInstructionsForUs());
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = rootRef.collection("Cart");
        Query query = itemsRef.whereEqualTo("cartId", datalist.get(position).getCartId());
        holder.removeFromCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                itemsRef.document(document.getId()).delete();
                            }
                            Intent intent = new Intent(v.getContext(), RemoveFromCart.class);
                            intent.putExtra("cartId", datalist.get(position).getCartId());
                            v.getContext().startActivity(intent);
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
            }
        });
        holder.applyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), coupon_code.class);
                intent.putExtra("cartId", datalist.get(position).getCartId());
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    static class cartViewHolder extends RecyclerView.ViewHolder{
        ImageView serviceImage;
        TextView serviceName, price, address, instructions;
        Button removeFromCart, applyCoupon;
        public cartViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            removeFromCart = itemView.findViewById(R.id.removeFromCart);
            applyCoupon = itemView.findViewById(R.id.applyCoupon);
            serviceName = itemView.findViewById(R.id.serviceName);
            price = itemView.findViewById(R.id.price);
            address = itemView.findViewById(R.id.address);
            instructions = itemView.findViewById(R.id.instruction);
            serviceImage = itemView.findViewById(R.id.serviceImage);
        }
    }
}
