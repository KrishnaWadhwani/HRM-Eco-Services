package com.eco.hrmecoservices;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;

public class settings_fragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View rootView = inflater.inflate(R.layout.fragment_settings, container,false);
        //Get the value back
        TextView userName = rootView.findViewById(R.id.userName);
        int nightModeFlags =
                getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                userName.setTextColor(Color.parseColor("#ffffff"));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                userName.setTextColor(Color.parseColor("#212121"));
                break;
        }
        CheckBox nightMode = rootView.findViewById(R.id.nightMode);
        SharedPreferences mode = getActivity().getSharedPreferences("AppTheme", MODE_PRIVATE);
        SharedPreferences.Editor editor = mode.edit();
        nightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nightMode.isChecked()){
                    editor.putString("theme","NightModeOn");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Intent intent = new Intent(getActivity(), SplashScreen.class);
                    startActivity(intent);
                }
                else {
                    editor.putString("theme", "NightModeOff");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Intent intent = new Intent(getActivity(), SplashScreen.class);
                    startActivity(intent);
                }
                //bottomnav.setSelectedItemId(R.id.home);
                /*getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new home_fragment()).commit();*/

                editor.apply();
            }
        });
        SharedPreferences getShared = getActivity().getSharedPreferences("AppTheme", MODE_PRIVATE);
        String val = getShared.getString("theme", "NightModeOff");
        if(val.equals("NightModeOn")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            nightMode.setChecked(true);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            nightMode.setChecked(false);
        }

        db.collection("User Details")
            .whereEqualTo("email",user.getEmail()).limit(1).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ImageView userImage = rootView.findViewById(R.id.userImage);
                    TextView username = rootView.findViewById(R.id.userName);
                    TextView email = rootView.findViewById(R.id.userEmail);
                    TextView userPhoneNumber = rootView.findViewById(R.id.phoneNumber);
                    if (task.isSuccessful()) {
                        String imurl = "";
                        String usernme = "";
                        String useremail = "";
                        String phoneNumber = "";
                        String countryCode = "";
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            imurl = (String) doc.get("image");
                            usernme = (String) doc.get("name");
                            useremail = (String) doc.get("email");
                            phoneNumber = (String) doc.get("PhoneNumberWithCountryCode");
                        }
                        Picasso.get().load(imurl).into(userImage);
                        username.setText(usernme);
                        email.setText(useremail);
                        userPhoneNumber.setText(phoneNumber);
                    }
                    else {
                        Toast.makeText(getActivity(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        return rootView;
    }
}
