package com.example.chaquopy_tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_DOG_PROFILE_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private DogProfileAdapter adapter;
    private List<DogProfile> dogProfiles;
    private FloatingActionButton btnAddProfile;
    DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dogs = new ArrayList<>();
        dRef = FirebaseDatabase.getInstance().getReference("dogs");
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                   for (DataSnapshot dogSnapshot : dataSnapshot.getChildren()) {
                       DogProfile dog = dogSnapshot.getValue(DogProfile.class);
                       Log.i("firebase", "Read new dog: " + dog);
                       dogs.add(dog);
                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addNewDog(123, "Max", "Labrador", "path/to/photo", 10000);
        recyclerView = findViewById(R.id.recyclerView);
        btnAddProfile = findViewById(R.id.btnAddProfile);
        dogProfiles = new ArrayList<>();

        adapter = new DogProfileAdapter(this, dogProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddDogProfileActivity.class);
                startActivityForResult(intent, ADD_DOG_PROFILE_REQUEST_CODE);
            }
        });
    }
    public void addNewDog(int id, String name, String breed, String photoPath, int targetSteps) {
        DogProfile newDog = new DogProfile(id, name, breed, photoPath, targetSteps);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DOG_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            DogProfile newProfile = (DogProfile) data.getSerializableExtra("newDogProfile");
            dogProfiles.add(newProfile);
            adapter.notifyDataSetChanged();
        }
    }
}
