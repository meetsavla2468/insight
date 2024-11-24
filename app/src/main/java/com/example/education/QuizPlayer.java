package com.example.education;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.Question;

public class QuizPlayer extends AppCompatActivity {

    private TextView questionTextView;
    private Button option1Button, option2Button, option3Button, nextButton;

    private int currentQuestionIndex = 0;
    private int score = 0;

    private Map<String, List<Question>> questionMap;
    private List<Question> selectedQuestions;
    private String currentTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_player);

        Intent intent = getIntent();
        currentTopic = intent.getStringExtra("topic");

        questionTextView = findViewById(R.id.question);
        option1Button = findViewById(R.id.option1);
        option2Button = findViewById(R.id.option2);
        option3Button = findViewById(R.id.option3);
        nextButton = findViewById(R.id.next);

        initializeQuestions();
        selectTopic(currentTopic);
        selectRandomQuestions(); // Select 5 random questions
        loadQuestion();

        option1Button.setOnClickListener(v -> checkAnswer(option1Button.getText().toString()));
        option2Button.setOnClickListener(v -> checkAnswer(option2Button.getText().toString()));
        option3Button.setOnClickListener(v -> checkAnswer(option3Button.getText().toString()));

        nextButton.setOnClickListener(v -> nextQuestion());
    }

    private void initializeQuestions() {
        questionMap = new HashMap<>();

        // Math questions
        List<Question> mathQuestions = new ArrayList<>();
        mathQuestions.add(new Question("What is 5 + 3?", "8", "9", "7", "8"));
        mathQuestions.add(new Question("What is 12 x 2?", "22", "24", "26", "24"));
        mathQuestions.add(new Question("What is the square root of 16?", "2", "4", "8", "4"));
        mathQuestions.add(new Question("What is 10 / 2?", "2", "5", "10", "5"));
        mathQuestions.add(new Question("What is 7 x 3?", "21", "24", "27", "21"));

        // Programming questions (C)
        List<Question> programmingQuestions = new ArrayList<>();
        programmingQuestions.add(new Question("What is the output of: printf(\"%d\", 2 + 3 * 2)?", "8", "10", "12", "8"));
        programmingQuestions.add(new Question("Which header file is used for input/output functions in C?", "<stdio.h>", "<stdlib.h>", "<string.h>", "<stdio.h>"));
        programmingQuestions.add(new Question("What is the size of an `int` in C?", "2 bytes", "4 bytes", "8 bytes", "4 bytes"));
        programmingQuestions.add(new Question("Which loop executes at least once?", "for", "while", "do-while", "do-while"));
        programmingQuestions.add(new Question("What is the keyword to define a constant?", "define", "const", "static", "const"));

        // Python questions
        List<Question> pythonQuestions = new ArrayList<>();
        pythonQuestions.add(new Question("Which keyword is used to define a function in Python?", "function", "def", "func", "def"));
        pythonQuestions.add(new Question("Which method is used to add an item to a list?", "add()", "append()", "insert()", "append()"));
        pythonQuestions.add(new Question("What is the output of len([1, 2, 3])?", "2", "3", "4", "3"));
        pythonQuestions.add(new Question("How do you create a dictionary in Python?", "{key: value}", "[key: value]", "(key, value)", "{key: value}"));
        pythonQuestions.add(new Question("What is the default value of the `range()` function's start parameter?", "0", "1", "-1", "0"));

        // Add to map
        questionMap.put("MATHEMATICS", mathQuestions);
        questionMap.put("Programming with C", programmingQuestions);
        questionMap.put("Python", pythonQuestions);
    }

    private void selectTopic(String topic) {
        List<Question> topicQuestions = questionMap.get(topic);
        if (topicQuestions == null) {
            Toast.makeText(this, "Topic not found!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            selectedQuestions = new ArrayList<>(topicQuestions); // Copy the topic's questions
            score = 0;
            currentQuestionIndex = 0;
        }
    }

    private void selectRandomQuestions() {
        if (selectedQuestions != null) {
            Collections.shuffle(selectedQuestions); // Shuffle the questions
            selectedQuestions = selectedQuestions.subList(0, Math.min(5, selectedQuestions.size())); // Select first 5
        }
    }

    private void loadQuestion() {
        if (selectedQuestions != null && currentQuestionIndex < selectedQuestions.size()) {
            Question currentQuestion = selectedQuestions.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getQuestion());
            option1Button.setText(currentQuestion.getOption1());
            option2Button.setText(currentQuestion.getOption2());
            option3Button.setText(currentQuestion.getOption3());
        } else {
            saveScore();
            Toast.makeText(this, "Quiz Finished! Your score: " + score, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void checkAnswer(String selectedAnswer) {
        String correctAnswer = selectedQuestions.get(currentQuestionIndex).getCorrectAnswer();
        if (selectedAnswer.equals(correctAnswer)) {
            score++;
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        loadQuestion();
    }

    private void saveScore() {
        SharedPreferences sharedPreferences = getSharedPreferences("QuizScores", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(currentTopic, score);
        editor.apply();
    }
}