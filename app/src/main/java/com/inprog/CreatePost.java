package com.inprog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.inprog.main.MainActivity;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class CreatePost extends AppCompatActivity {

    private EditText title, desc;
    private ImageView postimg;
    private Button post;

    private static final int gallerypic = 1;

    private Uri resulturi = null;

    FirebaseAuth mAuth;
    String userid;
    DatabaseReference reference,ref;
    StorageReference sreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        title = findViewById(R.id.posttitle);
        desc = findViewById(R.id.postdesc);
        postimg = findViewById(R.id.postimg);
        post = findViewById(R.id.postbutton);

        mAuth = FirebaseAuth.getInstance();
        userid = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        sreference = FirebaseStorage.getInstance().getReference().child("PostImages");

        postimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickiamge();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String titleu = title.getText().toString();
                final String descu = desc.getText().toString();
                if (titleu.isEmpty())
                {
                    Toast.makeText(CreatePost.this, "Title is empty", Toast.LENGTH_SHORT).show();
                }
                else if (descu.isEmpty())
                {
                    Toast.makeText(CreatePost.this, "Description is Empty", Toast.LENGTH_SHORT).show();
                }
                else if (resulturi == null)
                {
                    Toast.makeText(CreatePost.this, "Image Field is Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                final String puname = snapshot.child("Name").getValue().toString();
                                final String pupimg = snapshot.child("ProfileImage").getValue().toString();

                                Long tsLong = System.currentTimeMillis()/1000;
                                final String ts = tsLong.toString();
                                final StorageReference filepath = sreference.child(ts + ".jpg");
                                filepath.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                String postimagef = task.getResult().toString();
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("Title", titleu);
                                                user.put("Desc", descu);
                                                user.put("PostImage",postimagef);
                                                user.put("User",userid);
                                                user.put("Name",puname);
                                                user.put("Pfpimg",pupimg);
                                                user.put("Time",ts);

                                                reference.push().setValue(user);
                                                Intent intent2 = new Intent(CreatePost.this, MainActivity.class);
                                                startActivity(intent2);
                                            }
                                        });
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }

    private void pickiamge() {
        Intent galleryintent = new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent, gallerypic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == gallerypic && resultCode == RESULT_OK && data != null) {
            resulturi = data.getData();
            postimg.setImageURI(resulturi);
        }
    }
}