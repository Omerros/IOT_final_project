package com.example.chaquopy_tutorial;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DogManager {
    public static DogProfile addNewDog(int id, String name, String breed, String photoPath, int targetSteps) {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("dogs");
        DogProfile newDog = new DogProfile(id, name, breed, photoPath, targetSteps, null, null, "in", "light", "HOME");
        dRef.child(String.valueOf(newDog.getId())).setValue(newDog)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("firebase", "Dog added successfully to the database");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("firebase", "Failed to add dog to the database: " + e.getMessage());
                    }
                });
        return newDog;
    }
}