package com.eco.hrmecoservices;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ordersViewHolder>{

    ArrayList<ordersmodel> datalist;

    public OrdersAdapter(ArrayList<ordersmodel> datalist) {
        this.datalist = datalist;
    }

    @NonNull
    @NotNull
    @Override
    public ordersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderscart, parent, false);
        return new ordersViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull OrdersAdapter.ordersViewHolder holder, int position) {
        Picasso.get().load(datalist.get(position).getServiceimage()).placeholder(R.drawable.loading).into(holder.serviceImage);
        holder.serviceName.setText("Service: "+datalist.get(position).getService_needed());
        holder.price.setText("Total Price: "+datalist.get(position).getPrice()+" (Already Included Tax "+datalist.get(position).getTax()+")");
        holder.address.setText("Address: "+datalist.get(position).getFull_address());
        holder.instructions.setText("Instructions For Us: "+datalist.get(position).getMoreInstructionsForUs());
        holder.timeStamp.setText("Time Ordered: "+datalist.get(position).getTimeStamp());
        holder.cartId.setText("CartId: "+datalist.get(position).getCartId());
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    static class ordersViewHolder extends RecyclerView.ViewHolder{
        ImageView serviceImage;
        TextView serviceName, price, address, instructions, timeStamp, cartId;
        public ordersViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceName);
            price = itemView.findViewById(R.id.price);
            address = itemView.findViewById(R.id.address);
            instructions = itemView.findViewById(R.id.instruction);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            cartId = itemView.findViewById(R.id.cartId);
        }
    }
}
