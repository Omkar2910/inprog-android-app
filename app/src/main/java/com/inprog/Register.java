package com.inprog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText etName, etEmail, etPassword;
    Button btnRegister;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    TextView tvLogin;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.et_name_register);
        etEmail = findViewById(R.id.et_email_register);
        etPassword = findViewById(R.id.et_password_register);
        btnRegister = findViewById(R.id.btn_SignUp);
        tvLogin = findViewById(R.id.TextLoginBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBarRegister);


        // Event for clicking sign up button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = etName.getText().toString();
                final String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    etName.setError("Name is required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    return;
                }
                if (password.length() < 8) {
                    etPassword.setError("Password must be greater that 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //register the user in firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Send verification link
                            fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Register.this, "User Created. Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                        etName.setText("");
                                        etEmail.setText("");
                                        etPassword.setText("");

                                        //Storing user data in firestore for creating user profile
                                        userID = fAuth.getCurrentUser().getUid();

                                        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                                        //mapping data
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("Name", name);
                                        user.put("Email", email);
                                        user.put("ProfileImage","null");

                                        ref.updateChildren(user);
                                    } else {
                                        Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(Register.this, "Error! " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });


            }
        });


        // Switch from registration page to login page
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

    }

}