package com.example.education;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_DELAY = 4000; // Delay in milliseconds (6 seconds)
    TextView tv;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        tv = findViewById(R.id.txtSplashInfo);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.splash_text);
        tv.startAnimation(animation);
        mAuth = FirebaseAuth.getInstance();

        // Create a handler to delay the navigation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Check if the user is authenticated
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    // User is authenticated, navigate to the main activity
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    // User is not authenticated, navigate to the Registration module screen
                    Intent loginIntent = new Intent(SplashScreen.this, Registration.class);
                    startActivity(loginIntent);
                }
                finish();
            }
        }, SPLASH_DELAY);
            }
}
