package com.example.chaquopy_tutorial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StartWalkActivity extends AppCompatActivity {

    private Button btnStartWalk;
    private TextView tvStepCount;
    private TextView tvConnectionStatus;
    private EditText etTargetValue;
    private long stepCount = 0;
    private long targetValue = 0;
    private boolean isWalkStarted = false;
    BroadcastReceiver receiver;
    private List<DogProfile> updatedDogProfiles;
    private DogProfile dogProfile;
    private long lastUpdateTimeStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_walk);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnStartWalk = findViewById(R.id.btnStartWalk);
        tvStepCount = findViewById(R.id.tvStepCount);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);
        etTargetValue = findViewById(R.id.etTargetValue);

        Intent intent = getIntent();
        dogProfile = (DogProfile) intent.getSerializableExtra("dogProfile");
        tvConnectionStatus.setText("Connection Status: " + dogProfile.getWifi());

        IntentFilter filter = new IntentFilter("DATA_UPDATED");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updatedDogProfiles = (List<DogProfile>) intent.getSerializableExtra("dogProfiles");
                if (isWalkStarted && updatedDogProfiles != null) {
                    for (DogProfile updatedProfile : updatedDogProfiles) {
                        if (updatedProfile.getId() == dogProfile.getId()) {
                            tvConnectionStatus.setText("Connection Status: " + updatedProfile.getWifi());
                            Map<String, List<Object>> deviceData = updatedProfile.getDeviceData();
                            if (deviceData != null) {
                                for (Map.Entry<String, List<Object>> entry : deviceData.entrySet()) {
                                    List<Object> record = entry.getValue();
                                    if (record != null) {
                                        String timestampString = (String) record.get(0);
                                        try {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd yyyy HH:mm:ss");
                                            Date timestampDate = dateFormat.parse(timestampString);
                                            long timestampMillis = timestampDate.getTime();
                                            long currentTimeMillis = System.currentTimeMillis();
                                            if (timestampMillis > lastUpdateTimeStamp) {
                                                lastUpdateTimeStamp = currentTimeMillis;
                                                long newStepCount = (long) record.get(1);
                                                updateStepCount(newStepCount);
                                                if (stepCount >= targetValue) {
                                                    finishWalk();
                                                }
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        };
        registerReceiver(receiver, filter);

        btnStartWalk.setOnClickListener(v -> startWalkActivity());
    }

    private void startWalkActivity() {
        String targetString = etTargetValue.getText().toString();
        if (!targetString.isEmpty()) {
            targetValue = Long.parseLong(targetString);
            isWalkStarted = true;
            Toast.makeText(StartWalkActivity.this, "Starting the walk activity with target: " + targetString, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(StartWalkActivity.this, "Please enter a target value", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStepCount(long newStepCount) {
        if (lastUpdateTimeStamp != 0) {
            stepCount = newStepCount;
            tvStepCount.setText("Step Count: " + stepCount);
        } else {
            stepCount = newStepCount;
            tvStepCount.setText("Step Count: " + stepCount);
        }
    }

    private void finishWalk() {
        Utils.showNotification(this, "Walk", "Target steps achieved");
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver); // Unregister the broadcast receiver
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
