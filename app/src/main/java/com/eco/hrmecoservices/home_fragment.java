package com.eco.hrmecoservices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class home_fragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container,false);
        ImageView sanitizationImage = rootView.findViewById(R.id.sanitizationImage);
        ImageView homeImage = rootView.findViewById(R.id.homeImage);
        ImageView roomCleaningImage = rootView.findViewById(R.id.roomCleaningImage);
        ImageView sofaImage = rootView.findViewById(R.id.sofaImage);
        ImageView kitchenImage = rootView.findViewById(R.id.kitchenImage);
        ImageView carpetImage = rootView.findViewById(R.id.carpetImage);
        ImageView carImage = rootView.findViewById(R.id.carImage);
        ImageView bathroomImage = rootView.findViewById(R.id.bathroomImage);
        ImageView toiletImage = rootView.findViewById(R.id.toiletImage);
        ImageView paintingImage = rootView.findViewById(R.id.paintingImage);
        Glide.with(this)
            .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fsanitization.jpg?alt=media&token=8a8c8c83-7447-432b-b175-3762953ce347")
            .into(sanitizationImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fhouse.jpg?alt=media&token=3b33acfe-f714-47ba-8465-48be64cf6bc2")
                .into(homeImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Froom.jpeg?alt=media&token=17912ad4-8916-4331-a24f-44c68a979e36")
                .into(roomCleaningImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fsofa.jpg?alt=media&token=d51e5f9d-547e-4e60-b2e4-c4550d61acca")
                .into(sofaImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fkitchen.jpeg?alt=media&token=a3c54695-239a-42a7-98fe-8b2ec09be214")
                .into(kitchenImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fcarpet.jpeg?alt=media&token=7eddf33d-56db-4c3e-bd38-c39d1e5b137b")
                .into(carpetImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fcar.jpg?alt=media&token=1851e54c-adc1-4fdf-a46e-14fbbf9ad63a")
                .into(carImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fbathroom.jpeg?alt=media&token=eef68b9c-4a0a-4363-9512-e5ce694d3759")
                .into(bathroomImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Ftoilet.jpeg?alt=media&token=ff357865-5fea-4f1a-b88b-64f9a483a9ff")
                .into(toiletImage);
        Glide.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fpainting.jpg?alt=media&token=7d25835f-8c2b-4acd-8dd1-a74d4b83779b")
                .into(paintingImage);
        if (sanitizationImage.getDrawable()==null||homeImage.getDrawable()==null||roomCleaningImage.getDrawable()==null||sofaImage.getDrawable()==null||kitchenImage.getDrawable()==null||carpetImage.getDrawable()==null||carImage.getDrawable()==null||bathroomImage.getDrawable()==null||toiletImage.getDrawable()==null){
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fsanitization.jpg?alt=media&token=8a8c8c83-7447-432b-b175-3762953ce347")
                    .into(sanitizationImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fhouse.jpg?alt=media&token=3b33acfe-f714-47ba-8465-48be64cf6bc2")
                    .into(homeImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Froom.jpeg?alt=media&token=17912ad4-8916-4331-a24f-44c68a979e36")
                    .into(roomCleaningImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fsofa.jpg?alt=media&token=d51e5f9d-547e-4e60-b2e4-c4550d61acca")
                    .into(sofaImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fkitchen.jpeg?alt=media&token=a3c54695-239a-42a7-98fe-8b2ec09be214")
                    .into(kitchenImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fcarpet.jpeg?alt=media&token=7eddf33d-56db-4c3e-bd38-c39d1e5b137b")
                    .into(carpetImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fcar.jpg?alt=media&token=1851e54c-adc1-4fdf-a46e-14fbbf9ad63a")
                    .into(carImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fbathroom.jpeg?alt=media&token=eef68b9c-4a0a-4363-9512-e5ce694d3759")
                    .into(bathroomImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Ftoilet.jpeg?alt=media&token=ff357865-5fea-4f1a-b88b-64f9a483a9ff")
                    .into(toiletImage);
            Glide.with(this)
                    .load("https://firebasestorage.googleapis.com/v0/b/hrm-eco-services.appspot.com/o/homepage_images%2Fpainting.jpg?alt=media&token=7d25835f-8c2b-4acd-8dd1-a74d4b83779b")
                    .into(paintingImage);
        }
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
