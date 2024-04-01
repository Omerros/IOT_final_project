package com.example.chaquopy_tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final int ADD_DOG_PROFILE_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private DogProfileAdapter adapter;
    private List<DogProfile> dogProfiles;
    private FloatingActionButton btnAddProfile;
    DatabaseReference dRef;
    BroadcastReceiver receiver;

    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        btnAddProfile = findViewById(R.id.btnAddProfile);

        dogProfiles = new ArrayList<>();

        dRef = FirebaseDatabase.getInstance().getReference("dogs");

        adapter = new DogProfileAdapter(MainActivity.this, dogProfiles, dRef);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);

        serviceIntent = new Intent(this, DataUpdateService.class);
        startService(serviceIntent);
        // Register BroadcastReceiver to receive data update broadcasts from DataUpdateService
        IntentFilter filter = new IntentFilter("DATA_UPDATED");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Update dogProfiles list and notify adapter
                List<DogProfile> updatedDogProfiles = (List<DogProfile>) intent.getSerializableExtra("dogProfiles");
                if (updatedDogProfiles != null) {
                    dogProfiles.clear();
                    dogProfiles.addAll(updatedDogProfiles);
                    adapter.notifyDataSetChanged(); // Notify adapter of data change
                }
                Log.i("MainActivity", "got updated dog list from broadcast");
            }
        };
        registerReceiver(receiver, filter);

        btnAddProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddDogProfileActivity.class);
                startActivityForResult(intent, ADD_DOG_PROFILE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No need to start the foreground service here, as it is already started once in the MainActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
        unregisterReceiver(receiver);
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
