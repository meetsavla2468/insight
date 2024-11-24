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
        topics.add("Science");
        topics.add("Mathematics");
        topics.add("English Language");
        topics.add("Social Studies");
        topics.add("Geography");
        topics.add("History");
        topics.add("Environmental Studies");
        topics.add("Computer Science");
        topics.add("C++");
        topics.add("Java");
        topics.add("Economics");
        topics.add("Operating Systems");
        topics.add("Databases (DBMS)");
        topics.add("Artificial Intelligence");
        topics.add("Machine Learning");
    }

    private void loadScores(OnScoresLoadedCallback callback) {
        Map<String, Long> allScores = new HashMap<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Quiz")
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
                        String score = allScores.containsKey(topic) ? allScores.get(topic).toString() + "/5" : "0/5";
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