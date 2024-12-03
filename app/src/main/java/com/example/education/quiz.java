package com.example.education;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.education.databinding.ActivityQuizBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import adapter.QuizAdapter;

public class quiz extends AppCompatActivity {
    QuizAdapter adapter;
    ActivityQuizBinding bind;
    ArrayList<String> topics;
    ArrayList<String> scores;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        topics = new ArrayList<>();
        scores = new ArrayList<>();

        loadTopics();
        loadScores(new OnScoresLoadedCallback() {
            @Override
            public void onScoresLoaded() {
                adapter = new QuizAdapter(quiz.this, topics, scores);
                bind.quizRv.setLayoutManager(new LinearLayoutManager(quiz.this));
                bind.quizRv.setAdapter(adapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScores();
    }

    private void refreshScores() {
        loadScores(new OnScoresLoadedCallback() {
            @Override
            public void onScoresLoaded() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void loadTopics() {
        topics.add("Probability and Statistics");
        topics.add("Computer Graphics");
        topics.add("Computer Networks");
        topics.add("Machine Learning");
        topics.add("DSA");
        topics.add("Python");
        topics.add("Java");
        topics.add("Mathematics");
        topics.add("Science");
        topics.add("English Language");
    }

    private void loadScores(OnScoresLoadedCallback callback) {
        Map<String, Long> allScores = new HashMap<>();
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
                        if (scoresArray != null && !scoresArray.isEmpty()) {
                            int startIndex = Math.max(scoresArray.size() - 3, 0);
                            List<Long> lastScores = scoresArray.subList(startIndex, scoresArray.size());
                            Long lastScore = scoresArray.get(scoresArray.size() - 1);
                            allScores.put(topic, lastScore);
                        } else {
                            allScores.put(topic, 0L);
                        }
                    }

                    scores.clear();
                    for (String topic : topics) {
                        Log.d("msg", "Processing topic: " + topic);
                        String score = allScores.containsKey(topic) ? "Score: " + (allScores.get(topic) * 2) + "/10" : "Score: 0/10";
                        scores.add(score);
                    }
                    Log.d("msg", "Scores list: " + scores);
                    callback.onScoresLoaded();
                })
                .addOnFailureListener(e ->
                        Log.d("Msg", "Failed to get scores")
                );
    }

    interface OnScoresLoadedCallback {
        void onScoresLoaded();
    }
}