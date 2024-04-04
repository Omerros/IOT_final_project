package com.example.chaquopy_tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

public class MonitoringActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private NumberPicker lowerTempPicker;
    private NumberPicker upperTempPicker;
    private Switch switchNotifyDark;
    private TextView tvAlarmStatus;
    private Button btnTurnOffAlarm;
    private DogProfile dogProfile;
    private boolean isAlarmSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        lowerTempPicker = findViewById(R.id.lowerTempPicker);
        upperTempPicker = findViewById(R.id.upperTempPicker);
        switchNotifyDark = findViewById(R.id.switchNotifyDark);
        tvAlarmStatus = findViewById(R.id.tvAlarmStatus);
        btnTurnOffAlarm = findViewById(R.id.btnTurnOffAlarm);

        upperTempPicker = findViewById(R.id.upperTempPicker);

        checkAlarmStatus();
        btnTurnOffAlarm.setOnClickListener(v -> turnOffAlarm());

        // Set the range for the temperature pickers to 0°C to 50°C
        lowerTempPicker.setMinValue(0);
        lowerTempPicker.setMaxValue(50);
        upperTempPicker.setMinValue(0);
        upperTempPicker.setMaxValue(50);

        lowerTempPicker.setValue(18);
        upperTempPicker.setValue(28);
        timePicker.setCurrentHour(0);
        timePicker.setCurrentMinute(0);

        Intent intent = getIntent();
        dogProfile = (DogProfile) intent.getSerializableExtra("dogProfile");

        if (dogProfile.getAlarm() != null) {
            tvAlarmStatus.setText("Alarm: Set");
        } else {
            btnTurnOffAlarm.setVisibility(View.GONE);
        }

        lowerTempPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int upperTemp = upperTempPicker.getValue();
            if (newVal >= upperTemp) {
                // If lower bound is greater than or equal to upper bound, set it to upper bound - 1
                int newLowerValue = Math.max(0, upperTemp - 1);
                picker.setValue(newLowerValue);
                Toast.makeText(this, "Lower bound adjusted to be less than upper bound", Toast.LENGTH_SHORT).show();
            }
        });

        upperTempPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            int lowerTemp = lowerTempPicker.getValue();
            if (newVal <= lowerTemp) {
                // If upper bound is less than or equal to lower bound, set it to lower bound + 1
                int newUpperValue = Math.min(lowerTemp + 1, upperTempPicker.getMaxValue());
                picker.setValue(newUpperValue);
                Toast.makeText(this, "Upper bound adjusted to be greater than lower bound", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnApply).setOnClickListener(view -> {

            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();
            int lowerTemp = lowerTempPicker.getValue();
            int upperTemp = upperTempPicker.getValue();
            boolean notifyDark = switchNotifyDark.isChecked();

            if (lowerTemp >= upperTemp) {
                Toast.makeText(this, "Lower bound cannot be equal to or greater than upper bound", Toast.LENGTH_SHORT).show();
                return;
            }

            saveAlarmtoFirebase(hour, minute, lowerTemp, upperTemp, notifyDark);
        });
    }

    public void saveAlarmtoFirebase(int hour, int minute, int lowerTemp, int upperTemp, boolean notifyDark) {
        int dogId = dogProfile.getId();
        DatabaseReference dogRef = FirebaseDatabase.getInstance().getReference("dogs").child(String.valueOf(dogId));
        dogRef.child("alarm").setValue(hour + "," + minute + "," + lowerTemp + "," + upperTemp + "," + notifyDark)
                .addOnSuccessListener(aVoid -> {
                    // Settings applied successfully
                    String message = "Settings applied: Time - " + hour + " hours " + minute + " minutes, Temp Range - " + lowerTemp + "°C to " + upperTemp + "°C, Notify in dark: " + notifyDark;
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to update settings
                    Toast.makeText(this, "Failed to apply settings", Toast.LENGTH_SHORT).show();
                });
        isAlarmSet = true;
        checkAlarmStatus();
        btnTurnOffAlarm.setVisibility(View.VISIBLE);
    }

    public void deleteAlarmtoFirebase() {
        int dogId = dogProfile.getId();
        DatabaseReference dogRef = FirebaseDatabase.getInstance().getReference("dogs").child(String.valueOf(dogId));
        dogRef.child("alarm").removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Settings applied successfully
                    String message = "Alarm removed";
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to update settings
                    Toast.makeText(this, "Failed to apply settings", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to check if alarm is set and update UI accordingly
    private void checkAlarmStatus() {
        if (isAlarmSet) {
            tvAlarmStatus.setText("Alarm: Set");
        } else {
            tvAlarmStatus.setText("Alarm: Not Set");
        }
    }

    // Method to turn off the alarm
    private void turnOffAlarm() {
        isAlarmSet = false;
        deleteAlarmtoFirebase();
        tvAlarmStatus.setText("Alarm: Not Set");
        btnTurnOffAlarm.setVisibility(View.GONE);
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
