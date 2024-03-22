package com.example.chaquopy_tutorial;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DogProfileAdapter extends RecyclerView.Adapter<DogProfileAdapter.ViewHolder> {
    private Context context;
    private List<DogProfile> dogProfiles;

    public DogProfileAdapter(Context context, List<DogProfile> dogProfiles) {
        this.context = context;
        this.dogProfiles = dogProfiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dog_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DogProfile profile = dogProfiles.get(position);
        holder.tvDogName.setText(profile.getName());
        holder.tvDogBreed.setText(profile.getBreed());
        holder.tvTargetSteps.setText("Target Steps: " + profile.getTargetSteps());

        if (!profile.getPhotoPath().isEmpty()) {
            Glide.with(context)
                    .load(profile.getPhotoPath())
                    .into(holder.imgDogPhoto);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DogDetailsActivity.class);
            intent.putExtra("dogProfile", profile);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return dogProfiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDogName, tvDogBreed, tvTargetSteps;
        ImageView imgDogPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDogName = itemView.findViewById(R.id.tvDogName);
            tvDogBreed = itemView.findViewById(R.id.tvDogBreed);
            tvTargetSteps = itemView.findViewById(R.id.tvTargetSteps);
            imgDogPhoto = itemView.findViewById(R.id.imgDogPhoto);
        }
    }
}
