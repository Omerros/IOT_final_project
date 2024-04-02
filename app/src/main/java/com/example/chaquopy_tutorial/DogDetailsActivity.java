package com.example.chaquopy_tutorial;

import static com.example.chaquopy_tutorial.Utils.updateIcons;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import android.Manifest;
import android.os.Build;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.List;

public class DogDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private ImageView dogImage;
    private TextView dogName;
    private Button btnShowProgress, btnStartWalk, btnSetAlarm;
    BroadcastReceiver receiver;
    private DogProfile dogProfile;
    @SuppressLint("CheckResult")
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

        // Check and request storage permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission();
            }
        }

        Intent intent = getIntent();
        dogProfile = (DogProfile) intent.getSerializableExtra("dogProfile");
        if (dogProfile != null) {
            dogName.setText(dogProfile.getName());
            updateIcons(
                    findViewById(R.id.iconDarkness),
                    findViewById(R.id.iconWhereabouts),
                    dogProfile.getLightDark(),
                    dogProfile.getInOut()
            );
            String photoPath = dogProfile.getPhotoPath();
            Log.d("image_loader_details", "Photo path: " + photoPath);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.error(R.drawable.ic_launcher_background);

            if (photoPath != null && !photoPath.isEmpty()) {

                Glide.with(this)
                        .load(photoPath)
                        .apply(requestOptions)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("image_loader_details", "Failed to load image", e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("image_loader_details", "Image loaded successfully");
                                return false;
                            }
                        })
                        .into(dogImage);
            } else {
                Log.e("image_loader_details", "Invalid photo path: " + photoPath);
            }
        }
        // Set up listeners for the buttons
        btnShowProgress.setOnClickListener(v -> {
            Intent progressIntent = new Intent(DogDetailsActivity.this, ProgressChartActivity.class);
            progressIntent.putExtra("dogProfile", dogProfile);
            startActivity(progressIntent);
        });
        btnStartWalk.setOnClickListener(v -> {
            Intent startWalkIntent = new Intent(DogDetailsActivity.this, StartWalkActivity.class);
            startWalkIntent.putExtra("dogProfile", dogProfile);
            startActivity(startWalkIntent);
        });
        btnSetAlarm.setOnClickListener(v -> {
            Intent monitornigIntent = new Intent(DogDetailsActivity.this, MonitoringActivity.class);
            monitornigIntent.putExtra("dogProfile", dogProfile);
            startActivity(monitornigIntent);
        });

        IntentFilter filter = new IntentFilter("DATA_UPDATED");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Update dogProfiles list and notify adapter
                List<DogProfile> updatedDogProfiles = (List<DogProfile>) intent.getSerializableExtra("dogProfiles");
                if (updatedDogProfiles != null) {
                    for (DogProfile updatedProfile : updatedDogProfiles) {
                        if (updatedProfile.getId() == dogProfile.getId()) {
                            dogProfile = updatedProfile;
                        }
                    }
                    updateIcons(
                            findViewById(R.id.iconDarkness),
                            findViewById(R.id.iconWhereabouts),
                            dogProfile.getLightDark(),
                            dogProfile.getInOut()
                    );
                }
                Log.i("DogDetailsActivity", "got updated dog list from broadcast");
            }
        };
        registerReceiver(receiver, filter);
    }
    // Method to request storage permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_STORAGE_PERMISSION);
    }
    // Override onRequestPermissionsResult to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access the storage
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
            }
        }
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
