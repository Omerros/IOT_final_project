package com.example.chaquopy_tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class DogDetailsActivity extends AppCompatActivity {

    private ImageView dogImage;
    private TextView dogName;
    private Button btnShowProgress, btnStartWalk, btnSetAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_details);

        // Enable the back button in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dogImage = findViewById(R.id.dogImage);
        dogName = findViewById(R.id.dogName);
        btnShowProgress = findViewById(R.id.btnShowProgress);
        btnStartWalk = findViewById(R.id.btnStartWalk);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);

        Intent intent = getIntent();
        DogProfile dogProfile = (DogProfile) intent.getSerializableExtra("dogProfile");
        if (dogProfile != null) {
            dogName.setText(dogProfile.getName());
            Glide.with(this)
                    .load(dogProfile.getPhotoPath())
                    .error(R.drawable.ic_launcher_background) // Add a placeholder for errors
                    .into(dogImage);

        }

        // Set up listeners for the buttons
        // Example:
        btnShowProgress.setOnClickListener(v -> {
            Intent progressIntent = new Intent(DogDetailsActivity.this, ProgressChartActivity.class);
            startActivity(progressIntent);
        });
        btnStartWalk.setOnClickListener(v -> {
            // Handle "Start Walk" button click
        });
        btnSetAlarm.setOnClickListener(v -> {
            // Handle "Set Alarm" button click
        });
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
