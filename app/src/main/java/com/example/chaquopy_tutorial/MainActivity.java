package com.example.chaquopy_tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_DOG_PROFILE_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private DogProfileAdapter adapter;
    private List<DogProfile> dogProfiles;
    private FloatingActionButton btnAddProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
