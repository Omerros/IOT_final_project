package com.example.chaquopy_tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartWalkActivity extends AppCompatActivity {

    private Button btnConnectDevice;
    private TextView tvStepCount;
    private EditText etTargetValue;
    private DatabaseReference networkReference;
    private DatabaseReference stepReference;
    private long stepCount = 0;
    private long targetValue = 0; // Added targetValue
    private Handler handler = new Handler();
    private Runnable updateStepCountRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_walk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnConnectDevice = findViewById(R.id.btnConnectDevice);
        tvStepCount = findViewById(R.id.tvStepCount);
        etTargetValue = findViewById(R.id.etTargetValue); // Added etTargetValue

        // Initialize Firebase Database reference
        networkReference = FirebaseDatabase.getInstance().getReference("network");
        stepReference = FirebaseDatabase.getInstance().getReference("steps");

        btnConnectDevice.setOnClickListener(v -> {
            // Read the network value from Firebase
            networkReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String network = snapshot.getValue(String.class);
                        // Check if the device is not connected to the home network
                        if (!"home".equalsIgnoreCase(network)) {
                            // Start the walk activity
                            startWalkActivity();
                        } else {
                            // Prompt the user that the device is connected to the home network
                            Toast.makeText(StartWalkActivity.this, "You are already connected to the home network", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle the case where the network value doesn't exist
                        Toast.makeText(StartWalkActivity.this, "Network value not found in Firebase", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where reading from Firebase fails
                    Toast.makeText(StartWalkActivity.this, "Failed to read network value from Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Start updating step count
        startUpdatingStepCount();
    }

    // Method to start the walk activity
    private void startWalkActivity() {
        // Get the target value entered by the user
        String targetString = etTargetValue.getText().toString();
        if (!targetString.isEmpty()) {
            targetValue = Long.parseLong(targetString);
            // Start the walk activity here
            Toast.makeText(StartWalkActivity.this, "Starting the walk activity with target: " + targetValue, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(StartWalkActivity.this, "Please enter a target value", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to start updating step count from Firebase
    private void startUpdatingStepCount() {
        // Start updating step count from Firebase every second
        updateStepCountRunnable = new Runnable() {
            @Override
            public void run() {
                updateStepCountFromFirebase();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(updateStepCountRunnable);
    }

    // Method to update step count from Firebase
    private void updateStepCountFromFirebase() {
        stepReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long newStepCount = (long) dataSnapshot.getValue();
                    long stepIncrement = newStepCount - stepCount;
                    stepCount = newStepCount;
                    // Update the TextView with the current step count
                    tvStepCount.setText("Step Count: " + stepCount);
                    // Check if the step count exceeds the target value
                    if (stepCount >= targetValue) {
                        Toast.makeText(StartWalkActivity.this, "Target achieved!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the updateStepCountRunnable from the handler
        handler.removeCallbacks(updateStepCountRunnable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when the back button is pressed
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
