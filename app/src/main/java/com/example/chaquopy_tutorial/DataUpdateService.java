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

import java.util.ArrayList;
import java.util.List;

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
}
