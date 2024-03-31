package com.example.chaquopy_tutorial;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DataUpdateService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "DataUpdateChannel";
    private static final String CHANNEL_NAME = "Data Update Channel";
    private static final long INTERVAL_MILLIS = 15 * 1000; // 15 seconds

    private DatabaseReference dRef;
    private List<DogProfile> dogProfiles;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("DataUpdateService", "Service created");
        dRef = FirebaseDatabase.getInstance().getReference("dogs");
        dogProfiles = new ArrayList<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("DataUpdateService", "Service started");
        startForeground(NOTIFICATION_ID, createNotification());
        scheduleDataUpdate();
        loadDataFromFirebase();
        return START_NOT_STICKY;
    }

    private void scheduleDataUpdate() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent serviceIntent = new Intent(this, DataUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, serviceIntent, PendingIntent.FLAG_IMMUTABLE);

        long triggerAtMillis = SystemClock.elapsedRealtime() + INTERVAL_MILLIS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, INTERVAL_MILLIS, pendingIntent);
        }
    }

    private Notification createNotification() {
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Data Update Service")
                .setContentText("Updating data...")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void loadDataFromFirebase() {
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("DataUpdateService", "Data received from Firebase"); // Add this line
                if (dataSnapshot.exists()) {
                    dogProfiles.clear();
                    for (DataSnapshot dogSnapshot : dataSnapshot.getChildren()) {
                        DogProfile dog = dogSnapshot.getValue(DogProfile.class);
                        Log.i("firebase", "Read new dog: " + dog);
                        dogProfiles.add(dog);
                        // Check if alarm thresholds are breached
                        checkAlarmThresholds(dog);
                    }
                    // Send broadcast to notify MainActivity of updated data
                    Intent broadcastIntent = new Intent();
                    Log.i("DataUpdateService", "Sending broadcast");
                    broadcastIntent.setAction("DATA_UPDATED");
                    sendBroadcast(broadcastIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving data: " + databaseError.getMessage());
            }
        });
    }

    private void checkAlarmThresholds(DogProfile dog) {
        String alarmData = dog.getAlarm();
        if (alarmData != null && !alarmData.isEmpty()) {
            String[] alarmParts = alarmData.split(",");
            if (alarmParts.length == 5) {
                int hour = Integer.parseInt(alarmParts[0]);
                int minute = Integer.parseInt(alarmParts[1]);
                int lowerTempThreshold = Integer.parseInt(alarmParts[2]);
                int upperTempThreshold = Integer.parseInt(alarmParts[3]);
                boolean darknessAlarmEnabled = Boolean.parseBoolean(alarmParts[4]);

                // Extract device data samples
                Map<String, List<Object>> deviceData = dog.getDeviceData();
                if (deviceData != null && !deviceData.isEmpty()) {
                    int totalSamples = 0;
                    int alarmBreachesTemp = 0;
                    int alarmBreachesDark = 0;

                    // Calculate the start time of the time window in milliseconds since midnight
                    long currentTimeMillis = System.currentTimeMillis();
                    long windowStartTime = currentTimeMillis - (hour * 3600 * 1000 + minute * 60 * 1000);

                    // Iterate through device data samples
                    for (Map.Entry<String, List<Object>> entry : deviceData.entrySet()) {
                        // Extract timestamp and temperature from the sample
                        List<Object> sample = entry.getValue();
                        String timestampString = (String) sample.get(0);
                        long temperature = (long) sample.get(2);
                        long darkness = (long) sample.get(3); //TODO - change to string after updating the logic in the esp

                        // Parse timestamp string to obtain milliseconds since epoch
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd yyyy HH:mm:ss");
                            Date timestampDate = dateFormat.parse(timestampString);
                            long timestampMillis = timestampDate.getTime();

                            // Check if the sample falls within the time window
                            if (timestampMillis >= windowStartTime && timestampMillis <= currentTimeMillis) {
                                totalSamples++;

                                if (temperature > upperTempThreshold || temperature < lowerTempThreshold) {
                                    alarmBreachesTemp++;
                                }

                                if (darknessAlarmEnabled && darkness < 40) {
                                    alarmBreachesDark++;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Alarm", "Error parsing timestamp: " + e.getMessage());
                        }
                    }

                    // Check if 90% or more samples breach alarm conditions
                    if (totalSamples > 0 && alarmBreachesTemp >= totalSamples * 0.9) {
                        sendTemperatureAlarmNotification(dog.getName());
                    } else if (totalSamples > 0 && alarmBreachesDark >= totalSamples * 0.9){
                        sendDarknessAlarmNotification(dog.getName());
                    }
                } else {
                    Log.e("Alarm", "No device data available");
                }
            } else {
                Log.e("Alarm", "Invalid alarm data format");
            }
        } else {
            Log.e("Alarm", "No alarm data available");
        }
    }

    private void sendTemperatureAlarmNotification(String dogName) {
        // Create and send temperature alarm notification
        Log.i("Alarm", "Temperature alarm for " + dogName);
        String message = "Temperature alarm for " + dogName;
        Utils.showNotification(this, "Temperature Alarm", message);
    }

    private void sendDarknessAlarmNotification(String dogName) {
        // Create and send darkness alarm notification
        Log.i("Alarm", "Darkness alarm for " + dogName);
        String message = "Darkness alarm for " + dogName;
        Utils.showNotification(this, "Darkness Alarm", message);
    }
}