package com.example.fitrunner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fitrunner.Authentications.User;
import com.example.fitrunner.UiControllers.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    EditText name, email;
    ImageView editName, editImg;
    CircleImageView img;
    Button deleteAcc, save;
    FirebaseUser user;
    ProgressDialog processing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initGui();
    }

    private void initGui() {
        // initialize objects and setting click listener
        user = FirebaseAuth.getInstance().getCurrentUser();
        name = findViewById(R.id.name);
        email = findViewById(R.id.email_profile);
        editImg = findViewById(R.id.edit_photo);
        editImg.setOnClickListener(this);
        editName = findViewById(R.id.edit_name);
        editName.setOnClickListener(this);
        img = findViewById(R.id.profile_image);
        deleteAcc = findViewById(R.id.delete_account);
        deleteAcc.setOnClickListener(this);
        save = findViewById(R.id.save_changes);
        save.setOnClickListener(this);
        getUserDetail();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_name:
                name.setEnabled(true);
                Toast.makeText(this, "Now you can edit name!", Toast.LENGTH_SHORT).show();
                save.setVisibility(View.VISIBLE);
                break;
            case R.id.edit_photo:
                selectImage(Profile.this);
                break;
            case R.id.delete_account:
                setDeleteAcc();
                break;
            case R.id.save_changes:
                setUpdateName(name.getText().toString());
                name.setEnabled(false);
                save.setVisibility(View.GONE);
                break;
        }
    }

    private void getUserDetail() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USER_TABLE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    name.setText(user.getName());
                    email.setText(user.getEmail());
                    Glide.with(Profile.this).load(user.getImg()).placeholder(R.drawable.home).into(img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        setUplodImg(selectedImage);
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
                                setUplodImg(selectedImage);
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    void setUplodImg(Uri uri) {
        processing = new ProgressDialog(this);
        processing.setMessage("Processing");
        processing.setCancelable(false);
        processing.show();
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child("images/" + System.currentTimeMillis()).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.USER_TABLE).child(user.getUid()).child("img").setValue(imageUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        processing.dismiss();
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                processing.dismiss();
                                Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        processing.dismiss();
                    }
                });

    }


    void setUplodImg(Bitmap bitmap) {
        processing = new ProgressDialog(this);
        processing.setCancelable(false);
        processing.setMessage("Processing");
        processing.show();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        StorageReference reference = FirebaseStorage.getInstance().getReference();
        reference.child("images/" + System.currentTimeMillis()).putBytes(byteArray).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                        db.child(Constants.USER_TABLE).child(user.getUid()).child("img").setValue(img)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        processing.dismiss();
                                    }
                                });
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        processing.dismiss();
                    }
                });

    }

    void setUpdateName(String name) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(Constants.USER_TABLE).child(user.getUid()).child("name").setValue(name)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        processing.dismiss();
                    }
                });

    }

    void setDeleteAcc() {
        final Dialog delAcc = new Dialog(this);
        delAcc.setContentView(R.layout.del_acc);
        delAcc.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText email = delAcc.findViewById(R.id.email_dell);
        final EditText pass = delAcc.findViewById(R.id.pass_dell);
        Button dell = delAcc.findViewById(R.id.confirm_dell);
        dell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    email.setError("empty");
                    return;
                }
                if (TextUtils.isEmpty(pass.getText().toString())) {
                    pass.setError("empty");
                } else {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(email.getText().toString(), pass.getText().toString());

                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        delAcc.dismiss();
                                                        Toast.makeText(Profile.this, "Account Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                        startActivity(new Intent(Profile.this, MainActivity.class));
                                                    }
                                                }
                                            });

                                }
                            });
                }
            }
        });
        delAcc.show();
    }

}
