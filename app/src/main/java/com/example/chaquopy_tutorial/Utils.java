package com.example.chaquopy_tutorial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class Utils
{
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
    }
}

