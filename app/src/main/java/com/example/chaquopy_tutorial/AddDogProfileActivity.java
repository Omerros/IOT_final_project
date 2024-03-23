package com.example.chaquopy_tutorial;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class AddDogProfileActivity extends Activity {
    private EditText etName, etTargetSteps;
    private Spinner spinnerDogBreed;
    private Button btnSave;
    private ImageView imgDogPhoto;
    private String photoPath = ""; // Store the path of the selected image
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog_profile);

        etName = findViewById(R.id.etName);
        spinnerDogBreed = findViewById(R.id.spinnerDogBreed);
        etTargetSteps = findViewById(R.id.etTargetSteps);
        btnSave = findViewById(R.id.btnSave);
        imgDogPhoto = findViewById(R.id.imgDogPhoto);

        // Set up the spinner with an array of dog breeds
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dog_breeds, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDogBreed.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String breed = spinnerDogBreed.getSelectedItem().toString();
                int targetSteps = Integer.parseInt(etTargetSteps.getText().toString());
                // random number between 0 - 1000
                int id = (int)(Math.random() * (1000 - 0 + 1));
                DogProfile newDog = DogManager.addNewDog(id, name, breed, photoPath, targetSteps);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newDogProfile", newDog);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    public void selectImage(View view) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDogProfileActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, TAKE_PHOTO_REQUEST);
                } else if (options[which].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                    pickPhoto.setType("image/*");
                    startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);
                } else if (options[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri selectedImage = data.getData();
                imgDogPhoto.setImageURI(selectedImage);
                photoPath = selectedImage.toString();
            } else if (requestCode == TAKE_PHOTO_REQUEST) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imgDogPhoto.setImageBitmap(imageBitmap);
                photoPath = Utils.FileUtil.saveBitmapToInternalStorage(this, imageBitmap);
            }
        }
    }
}
