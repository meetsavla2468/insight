package com.example.education;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import Model.Question;

public class QuizPlayer extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseFirestore fireStore;
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

        // Science questions
        List<Question> scienceQuestions = new ArrayList<>();
        scienceQuestions.add(new Question("What is the boiling point of water at sea level?", "90°C", "100°C", "110°C", "100°C"));
        scienceQuestions.add(new Question("What is H2O commonly known as?", "Oxygen", "Water", "Hydrogen", "Water"));
        scienceQuestions.add(new Question("Which gas do plants primarily use for photosynthesis?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Carbon Dioxide"));
        scienceQuestions.add(new Question("What is the center of an atom called?", "Electron", "Nucleus", "Proton", "Nucleus"));
        scienceQuestions.add(new Question("Which organ in the human body pumps blood?", "Liver", "Brain", "Heart", "Heart"));
        scienceQuestions.add(new Question("What is the hardest natural substance on Earth?", "Gold", "Iron", "Diamond", "Diamond"));
        scienceQuestions.add(new Question("Which planet is known as the Red Planet?", "Earth", "Mars", "Jupiter", "Mars"));
        scienceQuestions.add(new Question("What is the process of converting liquid to gas called?", "Melting", "Evaporation", "Condensation", "Evaporation"));
        scienceQuestions.add(new Question("Which part of the plant conducts photosynthesis?", "Root", "Leaf", "Stem", "Leaf"));
        scienceQuestions.add(new Question("What is the chemical symbol for Sodium?", "Na", "S", "So", "Na"));

        questionMap.put("Science", scienceQuestions);

        // Mathematics questions
        List<Question> mathQuestions = new ArrayList<>();
        mathQuestions.add(new Question("What is 15 - 6?", "8", "9", "7", "9"));
        mathQuestions.add(new Question("What is the value of π approximately?", "3.12", "3.14", "3.16", "3.14"));
        mathQuestions.add(new Question("What is 9 squared?", "18", "81", "27", "81"));
        mathQuestions.add(new Question("What is 25% of 200?", "25", "50", "75", "50"));
        mathQuestions.add(new Question("What is the factorial of 5?", "20", "60", "120", "120"));
        mathQuestions.add(new Question("What is the sum of the angles in a triangle?", "90°", "180°", "360°", "180°"));
        mathQuestions.add(new Question("What is 10 to the power of 3?", "100", "1000", "10", "1000"));
        mathQuestions.add(new Question("What is the derivative of x^2?", "x", "2x", "x^2", "2x"));
        mathQuestions.add(new Question("What is the smallest prime number?", "0", "1", "2", "2"));
        mathQuestions.add(new Question("What is 2/3 + 1/3?", "1", "2/3", "3/3", "1"));

        questionMap.put("Mathematics", mathQuestions);

        // English Language questions
        List<Question> englishQuestions = new ArrayList<>();
        englishQuestions.add(new Question("Which is a synonym of 'happy'?", "Sad", "Joyful", "Angry", "Joyful"));
        englishQuestions.add(new Question("What is the plural of 'child'?", "Childs", "Children", "Child", "Children"));
        englishQuestions.add(new Question("What is the past tense of 'run'?", "Runs", "Running", "Ran", "Ran"));
        englishQuestions.add(new Question("What is an antonym of 'big'?", "Large", "Huge", "Small", "Small"));
        englishQuestions.add(new Question("What part of speech is 'quickly'?", "Noun", "Adverb", "Adjective", "Adverb"));
        englishQuestions.add(new Question("Which article is used before a vowel sound?", "A", "An", "The", "An"));
        englishQuestions.add(new Question("What is the correct spelling?", "Recieve", "Receive", "Recive", "Receive"));
        englishQuestions.add(new Question("Which word is a pronoun?", "Car", "He", "Jump", "He"));
        englishQuestions.add(new Question("What is the comparative form of 'good'?", "Gooder", "Better", "Best", "Better"));
        englishQuestions.add(new Question("What punctuation marks the end of a question?", "Period", "Exclamation", "Question Mark", "Question Mark"));

        questionMap.put("English Language", englishQuestions);

        // Social Studies questions
        List<Question> socialStudiesQuestions = new ArrayList<>();
        socialStudiesQuestions.add(new Question("Who is known as the Father of the Indian Constitution?", "Jawaharlal Nehru", "Mahatma Gandhi", "B.R. Ambedkar", "B.R. Ambedkar"));
        socialStudiesQuestions.add(new Question("What does the term 'democracy' mean?", "Rule by one", "Rule by the people", "Rule by the rich", "Rule by the people"));
        socialStudiesQuestions.add(new Question("What is the primary occupation in rural areas?", "Manufacturing", "Farming", "Trading", "Farming"));
        socialStudiesQuestions.add(new Question("What does a map's legend represent?", "Scale", "Symbols", "Distance", "Symbols"));
        socialStudiesQuestions.add(new Question("Which body governs India?", "Supreme Court", "Parliament", "State Government", "Parliament"));
        socialStudiesQuestions.add(new Question("What is the smallest unit of government in India?", "District", "Village", "Panchayat", "Panchayat"));
        socialStudiesQuestions.add(new Question("What does GDP stand for?", "Gross Domestic Product", "Global Domestic Power", "General Data Policy", "Gross Domestic Product"));
        socialStudiesQuestions.add(new Question("What is the capital of India?", "Mumbai", "New Delhi", "Kolkata", "New Delhi"));
        socialStudiesQuestions.add(new Question("Who discovered America?", "Christopher Columbus", "Vasco da Gama", "James Cook", "Christopher Columbus"));
        socialStudiesQuestions.add(new Question("What is a key feature of a monarchy?", "Elected Leaders", "A King or Queen", "No Government", "A King or Queen"));

        questionMap.put("Social Studies", socialStudiesQuestions);

        // Geography questions
        List<Question> geographyQuestions = new ArrayList<>();
        geographyQuestions.add(new Question("Which is the longest river in the world?", "Amazon", "Nile", "Yangtze", "Nile"));
        geographyQuestions.add(new Question("What is the largest continent?", "Africa", "Asia", "Europe", "Asia"));
        geographyQuestions.add(new Question("Which country has the highest population?", "India", "China", "USA", "China"));
        geographyQuestions.add(new Question("Which layer of Earth do we live on?", "Mantle", "Core", "Crust", "Crust"));
        geographyQuestions.add(new Question("What is the smallest country in the world?", "Monaco", "Vatican City", "San Marino", "Vatican City"));
        geographyQuestions.add(new Question("Which ocean is the largest?", "Atlantic", "Indian", "Pacific", "Pacific"));
        geographyQuestions.add(new Question("What is the capital of France?", "Rome", "Berlin", "Paris", "Paris"));
        geographyQuestions.add(new Question("What is the term for a group of islands?", "Archipelago", "Atoll", "Lagoon", "Archipelago"));
        geographyQuestions.add(new Question("Which desert is the largest in the world?", "Sahara", "Kalahari", "Gobi", "Sahara"));
        geographyQuestions.add(new Question("What is the tallest mountain on Earth?", "K2", "Mount Everest", "Kanchenjunga", "Mount Everest"));

        questionMap.put("Geography", geographyQuestions);

        // History questions
        List<Question> historyQuestions = new ArrayList<>();
        historyQuestions.add(new Question("Who was the first President of the United States?", "George Washington", "Abraham Lincoln", "Thomas Jefferson", "George Washington"));
        historyQuestions.add(new Question("Which war was fought between the North and South regions in the United States?", "World War I", "Civil War", "Revolutionary War", "Civil War"));
        historyQuestions.add(new Question("Who discovered the sea route to India?", "Christopher Columbus", "Vasco da Gama", "Ferdinand Magellan", "Vasco da Gama"));
        historyQuestions.add(new Question("What year did World War II end?", "1940", "1945", "1950", "1945"));
        historyQuestions.add(new Question("Who was known as the Iron Lady?", "Queen Elizabeth I", "Margaret Thatcher", "Indira Gandhi", "Margaret Thatcher"));
        historyQuestions.add(new Question("What empire was ruled by Julius Caesar?", "Roman Empire", "Ottoman Empire", "British Empire", "Roman Empire"));
        historyQuestions.add(new Question("What is the oldest known civilization?", "Egyptian", "Mesopotamian", "Indus Valley", "Mesopotamian"));
        historyQuestions.add(new Question("Who was the first emperor of China?", "Kublai Khan", "Qin Shi Huang", "Sun Yat-sen", "Qin Shi Huang"));
        historyQuestions.add(new Question("What was the name of the ship that carried the Pilgrims to America?", "Santa Maria", "Mayflower", "Endeavor", "Mayflower"));
        historyQuestions.add(new Question("What year did the French Revolution begin?", "1776", "1789", "1804", "1789"));

        questionMap.put("History", historyQuestions);

        // Environmental Studies questions
        List<Question> environmentalStudiesQuestions = new ArrayList<>();
        environmentalStudiesQuestions.add(new Question("What is the main cause of global warming?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Carbon Dioxide"));
        environmentalStudiesQuestions.add(new Question("Which is a renewable source of energy?", "Coal", "Wind", "Natural Gas", "Wind"));
        environmentalStudiesQuestions.add(new Question("What is deforestation?", "Planting Trees", "Cutting Down Trees", "Growing Forests", "Cutting Down Trees"));
        environmentalStudiesQuestions.add(new Question("Which layer of the atmosphere contains the ozone layer?", "Troposphere", "Stratosphere", "Mesosphere", "Stratosphere"));
        environmentalStudiesQuestions.add(new Question("What is the term for conserving wildlife in its natural habitat?", "Ex-situ Conservation", "In-situ Conservation", "Bio-Conservation", "In-situ Conservation"));
        environmentalStudiesQuestions.add(new Question("What is the most abundant greenhouse gas?", "Methane", "Carbon Dioxide", "Water Vapor", "Water Vapor"));
        environmentalStudiesQuestions.add(new Question("Which waste is biodegradable?", "Plastic", "Paper", "Metal", "Paper"));
        environmentalStudiesQuestions.add(new Question("What is an example of an endangered species?", "Tiger", "Elephant", "Pigeon", "Tiger"));
        environmentalStudiesQuestions.add(new Question("Which protocol aims to reduce greenhouse gas emissions?", "Kyoto Protocol", "Paris Agreement", "Montreal Protocol", "Kyoto Protocol"));
        environmentalStudiesQuestions.add(new Question("What is the primary source of freshwater on Earth?", "Oceans", "Rivers", "Groundwater", "Groundwater"));

        questionMap.put("Environmental Studies", environmentalStudiesQuestions);

        // Computer Science questions
        List<Question> computerScienceQuestions = new ArrayList<>();
        computerScienceQuestions.add(new Question("What does CPU stand for?", "Central Process Unit", "Central Processing Unit", "Computer Processing Unit", "Central Processing Unit"));
        computerScienceQuestions.add(new Question("Which is an example of an input device?", "Printer", "Keyboard", "Monitor", "Keyboard"));
        computerScienceQuestions.add(new Question("What is the base of the binary number system?", "8", "10", "2", "2"));
        computerScienceQuestions.add(new Question("What is the full form of RAM?", "Random Access Memory", "Read Access Memory", "Run Access Memory", "Random Access Memory"));
        computerScienceQuestions.add(new Question("Which software controls the hardware of a computer?", "Operating System", "Browser", "Compiler", "Operating System"));
        computerScienceQuestions.add(new Question("What is an algorithm?", "A programming language", "A step-by-step process to solve a problem", "A hardware device", "A step-by-step process to solve a problem"));
        computerScienceQuestions.add(new Question("What is an example of system software?", "MS Word", "Windows OS", "Chrome Browser", "Windows OS"));
        computerScienceQuestions.add(new Question("What is the smallest unit of data in a computer?", "Byte", "Bit", "Kilobyte", "Bit"));
        computerScienceQuestions.add(new Question("What does HTTP stand for?", "HyperText Transfer Protocol", "High Text Transfer Protocol", "Hyper Transfer Text Protocol", "HyperText Transfer Protocol"));
        computerScienceQuestions.add(new Question("What is the function of a compiler?", "Execute code", "Translate code into machine language", "Debug code", "Translate code into machine language"));

        questionMap.put("Computer Science", computerScienceQuestions);

        // Java questions
        List<Question> javaQuestions = new ArrayList<>();
        javaQuestions.add(new Question("Who developed Java?", "Microsoft", "Sun Microsystems", "Oracle", "Sun Microsystems"));
        javaQuestions.add(new Question("Which method is used to print in Java?", "console.log()", "print()", "System.out.println()", "System.out.println()"));
        javaQuestions.add(new Question("What is the file extension for Java files?", ".java", ".class", ".jav", ".java"));
        javaQuestions.add(new Question("What is the size of an int in Java?", "2 bytes", "4 bytes", "8 bytes", "4 bytes"));
        javaQuestions.add(new Question("Which keyword is used to create an object in Java?", "new", "create", "make", "new"));
        javaQuestions.add(new Question("What is inheritance in Java?", "Sharing methods", "Creating child classes from parent classes", "Overloading methods", "Creating child classes from parent classes"));
        javaQuestions.add(new Question("What is the superclass of all classes in Java?", "Object", "Class", "Main", "Object"));
        javaQuestions.add(new Question("Which loop is used when the number of iterations is unknown?", "for", "while", "do-while", "while"));
        javaQuestions.add(new Question("What does JVM stand for?", "Java Variable Model", "Java Virtual Machine", "Java Value Method", "Java Virtual Machine"));
        javaQuestions.add(new Question("What is the default value of a boolean in Java?", "true", "false", "null", "false"));

        questionMap.put("Java", javaQuestions);

        // Economics questions
        List<Question> economicsQuestions = new ArrayList<>();
        economicsQuestions.add(new Question("What does GDP stand for?", "Gross Domestic Product", "Global Domestic Product", "Gross Development Policy", "Gross Domestic Product"));
        economicsQuestions.add(new Question("What is demand?", "Supply of goods", "Desire to buy goods and services", "Goods in stock", "Desire to buy goods and services"));
        economicsQuestions.add(new Question("What is inflation?", "Rise in unemployment", "Rise in the price level", "Fall in currency value", "Rise in the price level"));
        economicsQuestions.add(new Question("What is the primary goal of economics?", "Maximizing production", "Efficient use of resources", "Promoting trade", "Efficient use of resources"));
        economicsQuestions.add(new Question("What is a market?", "Place where goods are produced", "Place where goods and services are exchanged", "Place for banking", "Place where goods and services are exchanged"));
        economicsQuestions.add(new Question("What is opportunity cost?", "The cost of an alternative foregone", "The price of a product", "The cost of goods in bulk", "The cost of an alternative foregone"));
        economicsQuestions.add(new Question("What does fiscal policy deal with?", "Taxation and spending", "Monetary control", "Import policies", "Taxation and spending"));
        economicsQuestions.add(new Question("What is the law of supply?", "Supply increases as price decreases", "Supply increases as price increases", "Supply decreases with demand", "Supply increases as price increases"));
        economicsQuestions.add(new Question("Which is a factor of production?", "Banking", "Labor", "Demand", "Labor"));
        economicsQuestions.add(new Question("What is a monopoly?", "Single buyer", "Single seller", "Multiple sellers", "Single seller"));

        questionMap.put("Economics", economicsQuestions);

        // C++ questions
        List<Question> cppQuestions = new ArrayList<>();
        cppQuestions.add(new Question("Who developed C++?", "Bjarne Stroustrup", "James Gosling", "Dennis Ritchie", "Bjarne Stroustrup"));
        cppQuestions.add(new Question("What is the extension of C++ source files?", ".java", ".cpp", ".cs", ".cpp"));
        cppQuestions.add(new Question("Which symbol is used for single-line comments in C++?", "//", "/* */", "#", "//"));
        cppQuestions.add(new Question("Which keyword is used to define a class in C++?", "class", "object", "define", "class"));
        cppQuestions.add(new Question("What does the 'new' keyword do?", "Defines variables", "Allocates memory", "Creates methods", "Allocates memory"));
        cppQuestions.add(new Question("What is polymorphism in C++?", "Multiple inheritance", "Same function behaving differently", "Multiple objects", "Same function behaving differently"));
        cppQuestions.add(new Question("What is a pointer in C++?", "Variable storing memory address", "Class object", "Loop iterator", "Variable storing memory address"));
        cppQuestions.add(new Question("What is encapsulation in C++?", "Binding data and methods", "Inheritance", "Access control", "Binding data and methods"));
        cppQuestions.add(new Question("What is the default return type of the main function in C++?", "void", "int", "char", "int"));
        cppQuestions.add(new Question("Which header file is used for input/output operations in C++?", "<iostream>", "<stdio.h>", "<stdlib.h>", "<iostream>"));

        questionMap.put("C++", cppQuestions);

        // Data Structures and Algorithms questions
        List<Question> dataStructuresAndAlgorithmsQuestions = new ArrayList<>();
        dataStructuresAndAlgorithmsQuestions.add(new Question("What is the time complexity of accessing an element in an array?", "O(n)", "O(1)", "O(log n)", "O(1)"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("Which data structure uses LIFO order?", "Queue", "Stack", "Array", "Stack"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("What does 'DFS' stand for in graph algorithms?", "Depth First Search", "Direct File Search", "Depth File Search", "Depth First Search"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("Which algorithm is used to find the shortest path in a graph?", "DFS", "Bubble Sort", "Dijkstra's Algorithm", "Dijkstra's Algorithm"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("What is the space complexity of a linked list?", "O(n)", "O(1)", "O(log n)", "O(n)"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("What is the basic operation of a stack?", "Push", "Insert", "Delete", "Push"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("Which sorting algorithm has the best average-case time complexity?", "Insertion Sort", "Bubble Sort", "Quick Sort", "Quick Sort"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("What is the worst-case time complexity of bubble sort?", "O(n)", "O(n^2)", "O(log n)", "O(n^2)"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("Which data structure is used for implementing recursion?", "Queue", "Stack", "Linked List", "Stack"));
        dataStructuresAndAlgorithmsQuestions.add(new Question("What is the time complexity of binary search?", "O(n)", "O(log n)", "O(n^2)", "O(log n)"));

        questionMap.put("Data Structures and Algorithms", dataStructuresAndAlgorithmsQuestions);

        // Databases (DBMS) questions
        List<Question> dbmsQuestions = new ArrayList<>();
        dbmsQuestions.add(new Question("What does DBMS stand for?", "Data Base Management System", "Distributed Base Management System", "Data Base Mapping System", "Data Base Management System"));
        dbmsQuestions.add(new Question("What is SQL used for?", "Managing databases", "Programming", "Creating websites", "Managing databases"));
        dbmsQuestions.add(new Question("Which command is used to fetch data from a database?", "SELECT", "GET", "RETRIEVE", "SELECT"));
        dbmsQuestions.add(new Question("What does 'Normalization' refer to in DBMS?", "Reducing redundancy", "Creating backups", "Increasing query speed", "Reducing redundancy"));
        dbmsQuestions.add(new Question("What is the primary key in a database?", "A unique identifier for records", "A password", "A foreign key", "A unique identifier for records"));
        dbmsQuestions.add(new Question("Which type of join returns all records from both tables?", "INNER JOIN", "LEFT JOIN", "OUTER JOIN", "OUTER JOIN"));
        dbmsQuestions.add(new Question("What does the 'JOIN' operation do?", "Combines columns from two tables", "Deletes records", "Filters data", "Combines columns from two tables"));
        dbmsQuestions.add(new Question("Which data type is used to store text in SQL?", "VARCHAR", "INT", "DATE", "VARCHAR"));
        dbmsQuestions.add(new Question("What is an example of a non-relational database?", "MySQL", "MongoDB", "PostgreSQL", "MongoDB"));
        dbmsQuestions.add(new Question("What is a foreign key?", "A unique identifier in a table", "A link between two tables", "A constraint", "A link between two tables"));

        questionMap.put("Databases (DBMS)", dbmsQuestions);

        // Artificial Intelligence questions
        List<Question> aiQuestions = new ArrayList<>();
        aiQuestions.add(new Question("What does AI stand for?", "Artificial Intelligence", "Automatic Interface", "Algorithm Intelligence", "Artificial Intelligence"));
        aiQuestions.add(new Question("Which algorithm is often used for machine learning?", "Decision Trees", "Bubble Sort", "Dijkstra's Algorithm", "Decision Trees"));
        aiQuestions.add(new Question("What is natural language processing?", "A type of search engine", "Processing large datasets", "Understanding human language by machines", "Understanding human language by machines"));
        aiQuestions.add(new Question("What is a neural network in AI?", "A collection of computers", "A system of interconnected nodes", "A software for data analysis", "A system of interconnected nodes"));
        aiQuestions.add(new Question("What is the Turing Test used for?", "Testing AI's ability to perform tasks", "Testing human intelligence", "Testing AI's ability to mimic human behavior", "Testing AI's ability to mimic human behavior"));
        aiQuestions.add(new Question("What is machine learning?", "A type of AI that learns from data", "Programming computers to solve tasks", "Data analysis", "A type of AI that learns from data"));
        aiQuestions.add(new Question("What is reinforcement learning?", "Learning from errors and rewards", "Learning from labeled data", "Learning from a supervisor", "Learning from errors and rewards"));
        aiQuestions.add(new Question("What does an AI system do?", "Performs actions without data", "Performs actions based on data", "Requires human intervention", "Performs actions based on data"));
        aiQuestions.add(new Question("What is deep learning?", "A type of reinforcement learning", "A subfield of machine learning using neural networks", "Programming AI systems", "A subfield of machine learning using neural networks"));
        aiQuestions.add(new Question("What is computer vision?", "The ability of computers to see", "The ability of computers to understand text", "The ability of computers to process images", "The ability of computers to process images"));

        questionMap.put("Artificial Intelligence", aiQuestions);

        // Machine Learning questions
        List<Question> machineLearningQuestions = new ArrayList<>();
        machineLearningQuestions.add(new Question("What is supervised learning?", "Learning with labeled data", "Learning from unlabeled data", "Learning through trial and error", "Learning with labeled data"));
        machineLearningQuestions.add(new Question("What does a regression model predict?", "Categorical outputs", "Continuous outputs", "Data classification", "Continuous outputs"));
        machineLearningQuestions.add(new Question("What is the purpose of feature selection in machine learning?", "Reducing the number of data points", "Selecting the best model", "Improving the performance by removing irrelevant features", "Improving the performance by removing irrelevant features"));
        machineLearningQuestions.add(new Question("What is overfitting in machine learning?", "Model is too simple", "Model is too complex and performs poorly on unseen data", "Model performs well on training data", "Model is too complex and performs poorly on unseen data"));
        machineLearningQuestions.add(new Question("Which of the following is an unsupervised learning algorithm?", "Linear Regression", "K-Means Clustering", "Decision Trees", "K-Means Clustering"));
        machineLearningQuestions.add(new Question("What is cross-validation in machine learning?", "Splitting data into training and testing sets", "Selecting features", "Evaluating model performance on different data splits", "Evaluating model performance on different data splits"));
        machineLearningQuestions.add(new Question("What is the purpose of a confusion matrix?", "To evaluate classification algorithms", "To evaluate regression algorithms", "To visualize data", "To evaluate classification algorithms"));
        machineLearningQuestions.add(new Question("What does 'training data' refer to?", "Data used to test a model", "Data used to teach a model", "Data not used in the model", "Data used to teach a model"));
        machineLearningQuestions.add(new Question("What is the most commonly used activation function in neural networks?", "ReLU", "Sigmoid", "Tanh", "ReLU"));
        machineLearningQuestions.add(new Question("What is a decision tree?", "A tree structure used for data visualization", "A flowchart for making decisions", "A model used for classification and regression", "A model used for classification and regression"));

        questionMap.put("Machine Learning", machineLearningQuestions);
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
            Toast.makeText(this, "Correct Answer!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect Answer!", Toast.LENGTH_SHORT).show();
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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db.collection("Quiz")
                .document(userId)
                .collection("topics")
                .document(currentTopic)
                .update("scores", FieldValue.arrayUnion(score))
                .addOnSuccessListener(aVoid ->
                        Log.d("Msg", "Score saved to firebase")
                )
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseFirestoreException &&
                            ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.NOT_FOUND) {
                        Map<String, Object> initialData = new HashMap<>();
                        initialData.put("scores", Collections.singletonList(score));
                        db.collection("Quiz")
                                .document(userId)
                                .collection("topics")
                                .document(currentTopic)
                                .set(initialData)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("Msg", "New topic and score saved to firebase")
                                )
                                .addOnFailureListener(err ->
                                        Log.d("Msg", "Failed to save score to firebase")
                                );
                    } else {
                        Log.d("Msg", "Failed to save score to firebase");
                    }
                });
    }
}