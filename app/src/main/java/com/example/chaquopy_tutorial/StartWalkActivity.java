package com.example.chaquopy_tutorial;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StartWalkActivity extends AppCompatActivity {

    private Button btnConnectDevice;
    private TextView tvConnectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_walk);

        btnConnectDevice = findViewById(R.id.btnConnectDevice);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);

        btnConnectDevice.setOnClickListener(v -> {
            // Implement the logic to connect to the ESP device
            // Update the connection status indicator accordingly
            // For demonstration purposes, let's just toggle the visibility of the status indicator
            if (tvConnectionStatus.getVisibility() == View.VISIBLE) {
                tvConnectionStatus.setVisibility(View.GONE);
            } else {
                tvConnectionStatus.setVisibility(View.VISIBLE);
            }
        });

        // Set up listener for "Start Walk" button
        Button btnStartWalk = findViewById(R.id.btnStartWalk);
        btnStartWalk.setOnClickListener(v -> {
            // Implement the logic to start the walk
            // This may involve reading step count from Firebase and updating UI accordingly
            // For demonstration purposes, let's just display a toast
            Toast.makeText(StartWalkActivity.this, "Walk started!", Toast.LENGTH_SHORT).show();
        });
    }
}
