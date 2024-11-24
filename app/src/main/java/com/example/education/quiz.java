package com.example.education;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.education.databinding.ActivityQuizBinding;

import java.util.ArrayList;
import java.util.Map;

import adapter.QuizAdapter;

public class quiz extends AppCompatActivity {
    QuizAdapter adapter;
    ActivityQuizBinding bind;
    ArrayList<String> topics;
    ArrayList<String> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        topics = new ArrayList<>();
        scores = new ArrayList<>();

        loadTopics();
        loadScores();

        adapter = new QuizAdapter(this, topics, scores);
        bind.quizRv.setLayoutManager(new LinearLayoutManager(this));
        bind.quizRv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshScores(); // Refresh scores every time the activity resumes
    }

    private void refreshScores() {
        loadScores(); // Reload scores from SharedPreferences
        adapter.notifyDataSetChanged(); // Notify adapter to refresh the list
    }

    private void loadTopics() {
        topics.add("MATHEMATICS");
        topics.add("Programming with C");
        topics.add("App Development");
        topics.add("Data Structures and Algorithms");
        topics.add("Python");
    }

    private void loadScores() {
        SharedPreferences sharedPreferences = getSharedPreferences("QuizScores", Context.MODE_PRIVATE);
        Map<String, ?> allScores = sharedPreferences.getAll();
        scores.clear();

        for (String topic : topics) {
            String score = allScores.containsKey(topic) ? "Score: " + allScores.get(topic).toString() : "Score: 0";
            scores.add(score);
        }
    }
}