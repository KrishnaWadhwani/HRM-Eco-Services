package com.eco.hrmecoservices;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class hrmSMSServices {
    public static void sendSMS(String phoneNumber, String type, String message, Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dbOtp = new HashMap<>();
        dbOtp.put("phoneNumber", phoneNumber);
        dbOtp.put("type", type);
        dbOtp.put("message", message);
        db.collection("SMSSystem").add(dbOtp).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                if(task.isSuccessful()&&type.matches("orderNotification")){
                    Toast.makeText(context, "Order Confirmation Message Sent To: "+phoneNumber, Toast.LENGTH_LONG).show();
                }
                else if(task.isSuccessful()&&type.matches("otp")){
                    Toast.makeText(context, "OTP Sent To: "+phoneNumber, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Some Error Occurred, Please Check Your Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
