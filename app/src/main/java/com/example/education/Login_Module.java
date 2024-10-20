package com.example.education;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.education.databinding.ActivityLoginModuleBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login_Module extends AppCompatActivity {

    ActivityLoginModuleBinding binding;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();


        //Code for login button
        binding.btnLogin.setOnClickListener(v -> {
            //ProgressBar is Visible to show the loading symbol
            //binding.progressBar.setVisibility(View.VISIBLE);

            //Variables are initiated for inputs
            String email,password;
            email=binding.emaIl.getText().toString().trim(); //????? as emaIl?
            password=binding.passWord.getText().toString().trim(); //???? as passWord?

            //Validation for inputs
            if (TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emaIl.setError("Please Enter the valid email");
                binding.emaIl.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(password)){
                binding.passWord.setError("Please Enter the Password");
                binding.passWord.requestFocus();
                return;
            }

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    binding.progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()){
                        startActivity(new Intent(Login_Module.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(Login_Module.this, Objects.requireNonNull(task.getException()).getLocalizedMessage() , Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });

   binding.regNow.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           startActivity(new Intent(Login_Module.this,Registration.class));
           finish();
       }
   });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(Login_Module.this, MainActivity.class));
            finish();
        }
    }
}