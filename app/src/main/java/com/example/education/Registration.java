package com.example.education;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.education.databinding.ActivityRegisterationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import Model.User_Model;

public class Registration extends AppCompatActivity {

    ActivityRegisterationBinding bind;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityRegisterationBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        firestore=FirebaseFirestore.getInstance();
    mAuth = FirebaseAuth.getInstance();
    bind.btnRegister.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //ProgressBar is Visible to show the loading symbol
            bind.progressBar.setVisibility(View.VISIBLE);

            //Variables are initiated for inputs
            String user,email,password;
            user = bind.userName.getText().toString();
            email = bind.email.getText().toString();
            password = bind.password.getText().toString();
            //Validation for inputs
            if (TextUtils.isEmpty(user)){
                bind.progressBar.setVisibility(View.GONE);
                Toast.makeText(Registration.this, "Please Enter the User Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                bind.progressBar.setVisibility(View.GONE);
                Toast.makeText(Registration.this, "Please Enter the valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)){
                bind.progressBar.setVisibility(View.GONE);
                Toast.makeText(Registration.this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
                return;
            }


            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Now code if task i.e. Registration is successful or not
                            if (task.isSuccessful()){
                                firestore.collection("Users").document(Objects.requireNonNull(mAuth.getUid())).set(new User_Model(user,email,password)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Registration.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                                        //Hide Progressbar
                                        bind.progressBar.setVisibility(View.GONE);

                                        //Intent Passing
                                        Intent intent = new Intent(Registration.this,Login_Module.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Registration.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            else{
                                //Registration Failed
                                Toast.makeText(Registration.this, "Registration Failed !!!"+"/n "+"Please try again later", Toast.LENGTH_LONG).show();

                                bind.progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    });

    //Code for the 'Click to Login Page' text
    bind.LoginNow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Registration.this,Login_Module.class));
            finish();
        }
    });

    }
}