package com.example.education;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.education.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;
import java.util.Queue;

import Model.Course_Model;
import Model.User_Model;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    TextToSpeech textToSpeech;
    SpeechRecognizer speechRecognizer;
    Intent speechRecognizerIntent;


    TextView gradient;
    Content_main adapter;
    ArrayList<Course_Model> list;
    ActivityMainBinding bind;
    RecyclerView courseRv;
    FirebaseAuth auth;
    FirebaseFirestore store;
    NavigationView navigationView;
    TextView name, email;
    ImageView profile_pic;
    CardView card1, card2, card3, card4;
    private BottomNavigationView bottom_nav_view;
    private DrawerLayout draw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        bottom_nav_view = findViewById(R.id.navigation);
        courseRv = findViewById(R.id.recycler_courses);
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        navigationView = bind.navView;
        View header = navigationView.getHeaderView(0);
        name = header.findViewById(R.id.text_navigation);
        email = header.findViewById(R.id.text_navigation_email);
        profile_pic = header.findViewById(R.id.imageView);
        draw = findViewById(R.id.drawer_layout);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);

        FirebaseFirestore.getInstance().setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {

                    case R.id.nav_Upload:
                        Intent intent = new Intent(MainActivity.this, Pdf_Video_Upload.class);
                        startActivity(intent);
                        draw.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.discuss:
                        Intent intent4 = new Intent(MainActivity.this, PersonalChat.class);
                        startActivity(intent4);
                        draw.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.browse:
                        Intent intent2 = new Intent(MainActivity.this, IncognitoActivity.class);
                        startActivity(intent2);
                        draw.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.gpt:
                        Intent intent3 = new Intent(MainActivity.this, Setting.class);
                        startActivity(intent3);
                        draw.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.gpt1:
                        Intent intent5 = new Intent(MainActivity.this, gptActivity.class);
                        startActivity(intent5);
                        draw.closeDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_logout:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Log out!!")
                                .setMessage("Are you sure?").setIcon(R.drawable.baseline_delete_24)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        auth.signOut();
                                        startActivity(new Intent(getApplicationContext(), Login_Module.class));
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        dialog.show();
                        return true;
                    case R.id.nav_share:
                        shareApp();
                        return true;
                }
                return true;
            }
        });
        profile_pic.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), profile.class));
            draw.closeDrawer(GravityCompat.START);
        });

        ArrayList<String> courseNameList = new ArrayList<>();

        // Initialize Text to Speech
        textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        // Initialize Speech Recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognizer", "Error: " + error);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0).trim();
                    navigateToCourse(spokenText, courseNameList);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0).trim();
                    navigateToCourse(spokenText, courseNameList);
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        store.collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String courseName = document.getId();
                                courseNameList.add(courseName);
                            }
                            list = new ArrayList<>();
                            for (String courseName : courseNameList) {
                                if (!Objects.equals(courseName, "Basic Maths") || !Objects.equals(courseName, "Stories") || !Objects.equals(courseName, "KG") || !Objects.equals(courseName, "Cartoons")) {
                                    list.add(new Course_Model(courseName, getIconForCourse(courseName)));
                                }
                            }
                            courseRv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            adapter = new Content_main(MainActivity.this, list);
                            courseRv.setAdapter(adapter);
                            promptUserForCourse();
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    }

                    private int getIconForCourse(String courseName) {
                        int iconResId;

                        switch (courseName.toLowerCase()) {
                            case "mathematics":
                                iconResId = R.drawable.ic_pi;
                                break;
                            case "app development":
                                iconResId = R.drawable.android_developer;
                                break;
                            case "web development":
                                iconResId = R.drawable.web;
                                break;
                            case "java":
                                iconResId = R.drawable.java_logo;
                                break;
                            case "python":
                                iconResId = R.drawable.py;
                                break;
                            default:
                                iconResId = R.drawable.android_developer;
                                break;
                        }

                        return iconResId;
                    }
                });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
                intent.putExtra("course", "Stories");
                startActivity(intent);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
                intent.putExtra("course", "KG");
                startActivity(intent);
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
                intent.putExtra("course", "Basic Maths");
                startActivity(intent);
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
                intent.putExtra("course", "Cartoons");
                startActivity(intent);
            }
        });


        bottom_nav_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.nav_Search:
                        intent = new Intent(MainActivity.this, search.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_MyProfile:
                        intent = new Intent(MainActivity.this, profile.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_Menu:
                        draw.openDrawer(GravityCompat.START);
                        return true;
                    case R.id.nav_Quiz:
                        intent = new Intent(MainActivity.this, quiz.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });


    }

    //share function in Navigation
    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareText = "Check out this awesome app!";
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void navigateToCourse(String spokenText, ArrayList<String> courseNameList) {
        for (Course_Model course : list) {
            if (course.getName().toLowerCase().contains(spokenText.toLowerCase())) {
                Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
                intent.putExtra("course", course.getName());
                startActivity(intent);
                return;
            }
        }
        if (spokenText.equalsIgnoreCase("stories")) {
            Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
            intent.putExtra("course", "Stories");
            startActivity(intent);
            return;
        } else if (spokenText.equalsIgnoreCase("kg")) {
            Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
            intent.putExtra("course", "KG");
            startActivity(intent);
            return;
        } else if (spokenText.equalsIgnoreCase("cartoons")) {
            Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
            intent.putExtra("course", "Cartoons");
            startActivity(intent);
            return;
        } else if (spokenText.equalsIgnoreCase("basic maths")) {
            Intent intent = new Intent(MainActivity.this, Pdf_Videos.class);
            intent.putExtra("course", "Basic Maths");
            startActivity(intent);
            return;
        }
        Toast.makeText(MainActivity.this, "Course not found", Toast.LENGTH_SHORT).show();
    }

    private void promptUserForCourse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak("What do you want to do", TextToSpeech.QUEUE_FLUSH, null, "INITIAL_PROMPT");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Queue<String> courseNamesQueue = new LinkedList<>();
                    for (Course_Model item : list) {
                        courseNamesQueue.add(item.getName());
                    }
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            if (!courseNamesQueue.isEmpty()) {
                                String nextCourse = courseNamesQueue.poll();  // Get and remove the next course name from the queue
                                textToSpeech.speak(nextCourse, TextToSpeech.QUEUE_FLUSH, null, "COURSE_NAME");
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                            // Handle TTS errors
                        }
                    });

                    // Start speaking the first course name from the queue
                    if (!courseNamesQueue.isEmpty()) {
                        String firstCourse = courseNamesQueue.poll();
                        textToSpeech.speak(firstCourse, TextToSpeech.QUEUE_FLUSH, null, "COURSE_NAME");
                    }
                }
            }, 2000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }, list.size() * 1000L + 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        store.collection("Users").document(Objects.requireNonNull(auth.getUid())).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (value != null) {
                        User_Model user_model = value.toObject(User_Model.class);
                        if (user_model != null) {
                            Glide.with(MainActivity.this).load(user_model.getProfile_pic()).apply(RequestOptions.circleCropTransform()).error(R.drawable.boy_bag).placeholder(R.drawable.boy_bag).into(profile_pic)
                            ;
                            name.setText(user_model.getUserName());
                            email.setText(user_model.getEmail());
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}