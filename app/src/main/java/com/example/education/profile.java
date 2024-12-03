package com.example.education;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.education.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Model.User_Model;
import adapter.FireStore_RV_courses;

public class profile extends AppCompatActivity {
    final int SELECT_PICTURE = 654;
    final int SELECT_PICTURE_BG = 655;
    FirebaseFirestore store = FirebaseFirestore.getInstance();
    private final CollectionReference recent_list = store.collection("courses");
    FirebaseAuth auth;
    FirebaseStorage storage;
    ArrayList<String> list;
    Map<String, Long> allScores = new HashMap<>();
    private ActivityProfileBinding binding;
    private FireStore_RV_courses adapter;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                storage.getReference().child("Users").child("profile_pic").putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storage.getReference().child("Users").child("profile_pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                store.collection("Users").document(auth.getUid()).update("profile_pic", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(profile.this, "Image Uploaded Successfully!!!", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (requestCode == SELECT_PICTURE_BG) {
                storage.getReference().child("Users").child("profile_bg").putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storage.getReference().child("Users").child("profile_bg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                store.collection("Users").document(Objects.requireNonNull(auth.getUid())).update("profile_bg", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(profile.this, "Image Uploaded Successfully!!!", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        list = new ArrayList<>();
        setUpRecyclerView();


        store.collection("Users").document(Objects.requireNonNull(auth.getUid())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    User_Model model = documentSnapshot.toObject(User_Model.class);
                    if (model != null) {
                        binding.usernamePrfPage.setText(model.getUserName());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.prfImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        });

        binding.diagonalLayout.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE_BG);
        });


    }

    private void setUpRecyclerView() {
        //Query for Recycler View in FireStore
        Query query = recent_list
                .orderBy("course", Query.Direction.DESCENDING)
                .limit(6);

        adapter = new FireStore_RV_courses(list, profile.this);

        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(profile.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (value != null) {
                        for (QueryDocumentSnapshot snap : value
                        ) {
                            list.add(snap.getId());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        binding.connectionPrfRv.setLayoutManager(new GridLayoutManager(profile.this, 2));
        binding.connectionPrfRv.setHasFixedSize(true);
        binding.connectionPrfRv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //For showing the updated pic
        store.collection("Users").document(Objects.requireNonNull(auth.getUid())).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(profile.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (value != null) {
                        User_Model user_model = value.toObject(User_Model.class);
                        if (user_model != null) {
                            Glide.with(profile.this).load(user_model.getProfile_pic()).error(R.drawable.boy_bag).placeholder(R.drawable.boy_bag).into(binding.prfImg);
                            Glide.with(profile.this).load(user_model.getProfile_bg()).error(R.drawable.classroom_1).placeholder(R.drawable.classroom_1).into(binding.diagonalLayout);
                        }
                    }
                }
            }
        });

        loadScores(new OnScoresLoadedCallback() {
            @Override
            public void onScoresLoaded() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Prevent the frequent update of data when you move on background
    }

    private void loadScores(OnScoresLoadedCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db.collection("Quiz")
                .document(userId)
                .collection("topics")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        String topic = document.getId();
                        List<Long> scoresArray = (List<Long>) document.get("scores");
                        Long sum = 0L;
                        Long count = 0L;
                        for (int i = 0; i < scoresArray.size(); i++) {
                            sum += scoresArray.get(i);
                            count++;
                        }
                        allScores.put(topic, sum / count);
                    }
                    displayScores();
                    callback.onScoresLoaded();
                })
                .addOnFailureListener(e ->
                        Log.d("Msg", "Failed to get scores")
                );
    }

    @SuppressLint("ResourceAsColor")
    private void displayScores() {
        LinearLayout scoresContainer = findViewById(R.id.subject_scores_container);
        scoresContainer.removeAllViews(); // Clear existing views if any

        for (Map.Entry<String, Long> entry : allScores.entrySet()) {
            String subjectName = entry.getKey();
            Long score = entry.getValue();

            // Inflate the custom subject score layout
            View scoreView = LayoutInflater.from(this).inflate(R.layout.item_subject_score, scoresContainer, false);

            // Set subject name and score
            TextView subjectNameView = scoreView.findViewById(R.id.subject_name);
            TextView subjectScoreView = scoreView.findViewById(R.id.subject_score);
            ImageView subjectImageView = scoreView.findViewById(R.id.subject_image);

            subjectNameView.setText(subjectName);
            subjectScoreView.setText("Score: " + score * 2 + "/10");

            // Set the subject image (use Glide or a default placeholder)
            Glide.with(this)
                    .load(getSubjectImage(subjectName))
                    .placeholder(getSubjectImage(subjectName))
                    .into(subjectImageView);
            CardView cardView = (CardView) scoreView;
            if (score * 2 <= 5) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_blue_light));
            }
            // Add the score view to the container
            scoresContainer.addView(scoreView);
        }
    }

    private int getSubjectImage(String subjectName) {
        switch (subjectName) {
            case "Science":
                return R.drawable.science;
            case "Mathematics":
                return R.drawable.mathematics;
            case "English Language":
                return R.drawable.english;
            case "Geography":
                return R.drawable.geography;
            case "History":
                return R.drawable.history;
            case "Physics":
                return R.drawable.physics;
            case "Chemistry":
                return R.drawable.chemistry;
            case "Biology":
                return R.drawable.biology;
            case "Computer Science":
                return R.drawable.computer_science;
            case "Python":
                return R.drawable.python;
            case "Programming with C":
                return R.drawable.c;
            case "C++":
                return R.drawable.cplusplus;
            case "Java":
                return R.drawable.java;
            case "DSA":
                return R.drawable.datastructure;
            case "Web Development":
                return R.drawable.web;
            case "App Development":
                return R.drawable.app;
            case "Probability and Statistics":
                return R.drawable.probability;
            case "Computer Graphics":
                return R.drawable.cg;
            case "Operating Systems":
                return R.drawable.os;
            case "Databases (DBMS)":
                return R.drawable.database;
            case "Artificial Intelligence":
                return R.drawable.ai;
            case "Machine Learning":
                return R.drawable.ml;
            case "Blockchain":
                return R.drawable.blockchain;
            case "Software Engineering":
                return R.drawable.software;
            case "Computer Networks":
                return R.drawable.cn;
            case "Economics":
                return R.drawable.economics;
            case "Stories":
                return R.drawable.stories;
            case "KG":
                return R.drawable.kg;
            case "Basic Maths":
                return R.drawable.kid_maths;
            case "Cartoons":
                return R.drawable.cartoon;
            default:
                return R.drawable.android_developer;

        }
    }

    interface OnScoresLoadedCallback {
        void onScoresLoaded();
    }
}