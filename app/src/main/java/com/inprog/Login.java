package com.inprog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.inprog.main.MainActivity;

public class Login extends AppCompatActivity {
    EditText mEmail,mPassword;
    Button mLoginBtn;
    TextView mRegisterTxt;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.et_name_login);
        mPassword = findViewById(R.id.et_password_login);
        progressBar = findViewById(R.id.progressBarLogin);
        fAuth = FirebaseAuth.getInstance();
        mLoginBtn = findViewById(R.id.btn_SignIn);
        mRegisterTxt = findViewById(R.id.TextRegisterBtn);

        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null && fAuth.getCurrentUser().isEmailVerified()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("Password must be greater that 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Authenticate the user
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(fAuth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(Login.this, " Please verify your email address ", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(Login.this, " Error! " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        //sending activity to register page from login page
        mRegisterTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                finish();
            }
        });


    }


}