package com.eco.hrmecoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RemoveFromCart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_from_cart);
        Intent getIntent = getIntent();
        Bundle intentBundle = getIntent.getExtras();
        Button backToHomepage = findViewById(R.id.back_to_homepage);
        backToHomepage.setVisibility(View.GONE);
        if(intentBundle!=null){
            String cartId = (String) intentBundle.get("cartId");
            TextView details = findViewById(R.id.details);
            details.setText("Cart Id: "+cartId);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Cart")
                    .whereEqualTo("cartId", cartId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boolean isEmpty = task.getResult().isEmpty();
                            if (isEmpty) {
                                Intent intent = new Intent(RemoveFromCart.this, MainActivity.class);
                                intent.putExtra("toCartFrag", "toCartFrag");
                                startActivity(intent);
                            }
                            else{
                                backToHomepage.setVisibility(View.VISIBLE);
                                Map<String, Object> errorLog = new HashMap<>();
                                errorLog.put("errorLogCartId", cartId);
                                db.collection("errorLog")
                                        .add(errorLog)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(RemoveFromCart.this, "We Have Informed This Error To Our Customer Care Executives", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RemoveFromCart.this, "We Are Facing Some Difficulties To Log Your Error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                Toast.makeText(RemoveFromCart.this, "Sorry, We Are Unable To Delete This Item At The Moment", Toast.LENGTH_SHORT).show();
                                TextView error = findViewById(R.id.error);
                                error.setText("Share This Cart Id With Our Customer Care Executives And They Will Handle The Rest...");
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    }
    public void backToHomepage(View view){
        Intent intent = new Intent(RemoveFromCart.this, MainActivity.class);
        startActivity(intent);
    }
}