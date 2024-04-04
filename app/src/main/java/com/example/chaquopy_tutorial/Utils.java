package com.example.chaquopy_tutorial;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class Utils
{
    public static void showNotification(Context Context, String notificationTitle, String s) {
        NotificationManager notificationManager = (NotificationManager) Context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i("bolbolon","im here");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i("bolbolon","im here1");
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID", "YOUR_CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(Context, "YOUR_CHANNEL_ID")
                .setContentTitle(notificationTitle)
                .setContentText(s)
                .setSmallIcon(R.drawable.ic_notification) // Set the icon for the notification
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    public static class FileUtil {

        public static String saveBitmapToInternalStorage(Context context, Bitmap bitmap) {
            // Create a directory within internal storage to save images
            File directory = context.getDir("images", Context.MODE_PRIVATE);

            // Create a unique file name for the image using a timestamp
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";

            // Create the file object
            File imageFile = new File(directory, fileName);

            // Write the bitmap to the file
            try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                bitmap.compress(CompressFormat.JPEG, 100, outputStream);
                return imageFile.getAbsolutePath(); // Return the absolute path of the saved image
            } catch (IOException e) {
                e.printStackTrace();
                return null; // Return null if there was an error saving the image
            }
        }
        public static void showNotification(Context context, String title, String message) {

        }
    }

    public static void updateIcons(ImageView iconDarkness, ImageView iconWhereabouts, String darkness, String whereAbouts) {
        if (darkness.equals("dark")) {
            iconDarkness.setImageResource(R.mipmap.ic_launcher_moon);
        } else { // light
            iconDarkness.setImageResource(R.mipmap.ic_launcher_sun);
        }
        if (whereAbouts.equals("in")) {
            iconWhereabouts.setImageResource(R.mipmap.ic_indoors);
        } else { // out
            iconWhereabouts.setImageResource(R.mipmap.ic_outdoors);
        }
    }
}

