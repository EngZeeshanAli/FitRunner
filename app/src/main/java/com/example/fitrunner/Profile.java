package com.example.fitrunner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    EditText name, email;
    ImageView editName, editImg;
    CircleImageView img;
    Button deleteAcc, save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initGui();
    }

    private void initGui() {
        // initialize objects and setting click listener
        name = findViewById(R.id.name);
        email = findViewById(R.id.email_profile);
        editImg = findViewById(R.id.edit_photo);
        editImg.setOnClickListener(this);
        editName = findViewById(R.id.edit_name);
        editName.setOnClickListener(this);
        img = findViewById(R.id.profile_image);
        deleteAcc = findViewById(R.id.delete_account);
        save = findViewById(R.id.save_changes);
        save.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_name:
                name.setEnabled(true);
                save.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_photo:
                selectImage(Profile.this);
                break;
            case R.id.delete_account:

                break;
            case R.id.save_changes:
                name.setEnabled(false);
                save.setVisibility(View.GONE);
                break;
        }
    }


    private void selectImage(final Activity c) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    c.startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    c.startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        img.setImageBitmap(selectedImage);
                        //setUplodImg(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                img.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                //setUplodImg(selectedImage);
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }
}
