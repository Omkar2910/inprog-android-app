package com.inprog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String userid;
    DatabaseReference reference;
    StorageReference sreference;

    private EditText usernameet;
    private TextView emailtv;
    private CircleImageView profileimagev;
    private Button save;
    private ProgressBar progressBar;

    private Uri resulturi;

    private static final int gallerypic = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        sreference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        usernameet = findViewById(R.id.username);
        emailtv = findViewById(R.id.email);
        profileimagev = findViewById(R.id.profileimage);
        save = findViewById(R.id.save);
        progressBar = findViewById(R.id.progressBarProfile);
        progressBar.setVisibility(View.GONE);

        profileimagev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimage();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedetails();
            }
        });

        getuserinfo();
    }


    private void updatedetails() {
        final String nusername = usernameet.getText().toString();
        if (nusername.isEmpty()) {
            Toast.makeText(this, "Please Enter a username", Toast.LENGTH_SHORT).show();
        } else {
            //put progress bar below
            //above
            if (resulturi != null) {
                final StorageReference filepath = sreference.child(userid + ".jpg");
                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String profileimageurl = task.getResult().toString();
                                reference.child("ProfileImage").setValue(profileimageurl);
                                reference.child("Name").setValue(nusername);
                            }
                        });
                    }
                });
            } else {
                reference.child("Name").setValue(nusername);
            }
        }
    }

    private void selectimage() {
        Intent galleryintent = new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, gallerypic);
    }

    private void getuserinfo() {
        progressBar.setVisibility(View.VISIBLE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final String checknull = "null";
                    String username = snapshot.child("Name").getValue().toString();
                    String email = snapshot.child("Email").getValue().toString();
                    String profileimage = snapshot.child("ProfileImage").getValue().toString();
                    if ((TextUtils.equals(profileimage, checknull)) && resulturi == null) {
                        emailtv.setText(email);
                        usernameet.setText(username);
                    } else if ((TextUtils.equals(profileimage, checknull)) && resulturi != null) {
                        emailtv.setText(email);
                        usernameet.setText(username);
                        Picasso.get().load(profileimage).into(profileimagev);
                    } else {
                        emailtv.setText(email);
                        usernameet.setText(username);
                        Picasso.get().load(profileimage).into(profileimagev);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallerypic && resultCode == RESULT_OK && data != null) {
            resulturi = data.getData();
            profileimagev.setImageURI(resulturi);
        }
    }
}