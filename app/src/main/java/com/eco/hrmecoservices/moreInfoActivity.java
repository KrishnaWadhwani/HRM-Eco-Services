package com.eco.hrmecoservices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class moreInfoActivity extends AppCompatActivity {

    TextView information;
    TextView heading;
    ImageView serviceImage;
    String showInfoOf = "";
    String text = "";
    String headingText = "";
    String imageUrl = "";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        Bundle bundle = getIntent().getExtras();
        showInfoOf = bundle.getString("showInfoOf");
        information = findViewById(R.id.information);
        heading = findViewById(R.id.heading);
        serviceImage = findViewById(R.id.serviceImage);
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                information.setTextColor(Color.parseColor("#dfdfdf"));
                heading.setTextColor(Color.parseColor("#dfdfdf"));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                information.setTextColor(Color.parseColor("#212121"));
                heading.setTextColor(Color.parseColor("#212121"));
                break;
        }
        db.collection("MoreInfoAboutCleaning").whereEqualTo("infoName", showInfoOf).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                        headingText+=documentSnapshot.get("heading");
                        text+=documentSnapshot.get("info");
                        imageUrl+=documentSnapshot.get("serviceImage");
                    }
                    text = text.replace("<newLine>", "<br>");
                    text = text.replace("<bold>", "<b>");
                    text = text.replace("</bold>", "</b>");
                    heading.setText(Html.fromHtml(headingText));
                    information.setText(Html.fromHtml(text));
                    Glide.with(moreInfoActivity.this)
                            .load(imageUrl)
                            .into(serviceImage);
                }
            }
        });
    }
    public void goBack(View view){
        finish();
    }
}