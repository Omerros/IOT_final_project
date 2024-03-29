package com.example.chaquopy_tutorial;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DogProfileAdapter extends RecyclerView.Adapter<DogProfileAdapter.ViewHolder> {
    private Context context;
    private List<DogProfile> dogProfiles;
    private DatabaseReference dRef;

    public DogProfileAdapter(Context context, List<DogProfile> dogProfiles, DatabaseReference dRef) {
        this.context = context;
        this.dogProfiles = dogProfiles;
        this.dRef = dRef;
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

        holder.btnDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                deleteDogProfile(adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dogProfiles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDogName, tvDogBreed, tvTargetSteps;
        ImageView imgDogPhoto;
        ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDogName = itemView.findViewById(R.id.tvDogName);
            tvDogBreed = itemView.findViewById(R.id.tvDogBreed);
            tvTargetSteps = itemView.findViewById(R.id.tvTargetSteps);
            imgDogPhoto = itemView.findViewById(R.id.imgDogPhoto);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void deleteDogProfile(int position) {
        DogProfile dogProfile = dogProfiles.get(position);
        String id = String.valueOf(dogProfile.getId());

        dRef.child(id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dogProfiles.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Profile deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
