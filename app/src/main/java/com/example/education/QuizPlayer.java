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

        // Probability and Statistics questions
        List<Question> probabilityAndStatisticsQuestions = new ArrayList<>();
        probabilityAndStatisticsQuestions.add(new Question("What is the probability of getting a 6 on a fair die?", "1/2", "1/6", "1/12", "1/6"));
        probabilityAndStatisticsQuestions.add(new Question("What is the mean of the numbers 2, 4, 6?", "3", "4", "5", "4"));
        probabilityAndStatisticsQuestions.add(new Question("Which of the following is not a measure of central tendency?", "Mean", "Median", "Variance", "Variance"));
        probabilityAndStatisticsQuestions.add(new Question("What is the variance of the numbers 1, 2, 3?", "0.5", "1", "2", "0.5"));
        probabilityAndStatisticsQuestions.add(new Question("In a normal distribution, what is the value of the mean?", "0", "1", "It varies", "0"));
        probabilityAndStatisticsQuestions.add(new Question("Which of the following is a continuous random variable?", "Number of heads in coin toss", "Height of a person", "Number of students in a class", "Height of a person"));
        probabilityAndStatisticsQuestions.add(new Question("What does a standard deviation of 0 indicate?", "No spread in the data", "Maximum spread in the data", "Mean is 0", "No spread in the data"));
        probabilityAndStatisticsQuestions.add(new Question("What is the range of a data set?", "Highest value - Lowest value", "Sum of all values", "Mean of values", "Highest value - Lowest value"));
        probabilityAndStatisticsQuestions.add(new Question("What is the formula for the probability of two independent events A and B?", "P(A) + P(B)", "P(A and B)", "P(A) * P(B)", "P(A) * P(B)"));
        probabilityAndStatisticsQuestions.add(new Question("Which probability rule is used when events are mutually exclusive?", "Addition Rule", "Multiplication Rule", "Conditional Probability", "Addition Rule"));

        questionMap.put("Probability and Statistics", probabilityAndStatisticsQuestions);

        // Computer Graphics questions
        List<Question> computerGraphicsQuestions = new ArrayList<>();
        computerGraphicsQuestions.add(new Question("What does the term 'rendering' refer to in computer graphics?", "Storing data", "Generating an image from a model", "Editing images", "Generating an image from a model"));
        computerGraphicsQuestions.add(new Question("Which of the following is a 3D graphics software?", "Photoshop", "Blender", "Paint", "Blender"));
        computerGraphicsQuestions.add(new Question("What is the purpose of anti-aliasing in computer graphics?", "To add more colors", "To smooth jagged edges", "To increase brightness", "To smooth jagged edges"));
        computerGraphicsQuestions.add(new Question("Which algorithm is used to draw a line in computer graphics?", "Bresenham's Algorithm", "QuickSort", "Dijkstra's Algorithm", "Bresenham's Algorithm"));
        computerGraphicsQuestions.add(new Question("What is the term for adjusting the appearance of 3D objects in computer graphics?", "Shading", "Lighting", "Texturing", "Shading"));
        computerGraphicsQuestions.add(new Question("Which transformation changes the position of an object in computer graphics?", "Scaling", "Translation", "Reflection", "Translation"));
        computerGraphicsQuestions.add(new Question("Which of these is a vector graphics format?", "JPG", "PNG", "SVG", "SVG"));
        computerGraphicsQuestions.add(new Question("What is the primary use of a graphics card?", "To manage network connections", "To accelerate image rendering", "To process data", "To accelerate image rendering"));
        computerGraphicsQuestions.add(new Question("What is a texture map in 3D graphics?", "An image applied to a 3D object", "A 3D model", "A lighting effect", "An image applied to a 3D object"));
        computerGraphicsQuestions.add(new Question("Which technique is used to create realistic lighting in 3D rendering?", "Ray tracing", "Texture mapping", "Shading", "Ray tracing"));

        questionMap.put("Computer Graphics", computerGraphicsQuestions);

        // Computer Networks questions
        List<Question> computerNetworksQuestions = new ArrayList<>();
        computerNetworksQuestions.add(new Question("What does TCP/IP stand for?", "Transmission Control Protocol/Internet Protocol", "Total Control Protocol/Internet Protocol", "Transmission Communication Protocol/Internet Protocol", "Transmission Control Protocol/Internet Protocol"));
        computerNetworksQuestions.add(new Question("What is the maximum length of an Ethernet cable?", "100 meters", "500 meters", "200 meters", "100 meters"));
        computerNetworksQuestions.add(new Question("Which protocol is used to obtain an IP address in a network?", "FTP", "DHCP", "SMTP", "DHCP"));
        computerNetworksQuestions.add(new Question("What is the primary purpose of a router?", "To connect two networks", "To send emails", "To store data", "To connect two networks"));
        computerNetworksQuestions.add(new Question("Which device is used to amplify signals in a network?", "Hub", "Repeater", "Router", "Repeater"));
        computerNetworksQuestions.add(new Question("Which OSI layer is responsible for routing?", "Network Layer", "Data Link Layer", "Transport Layer", "Network Layer"));
        computerNetworksQuestions.add(new Question("What does DNS stand for?", "Dynamic Network Service", "Domain Name System", "Data Network System", "Domain Name System"));
        computerNetworksQuestions.add(new Question("What is the main function of the Transport Layer?", "Error detection", "Routing packets", "End-to-end communication", "End-to-end communication"));
        computerNetworksQuestions.add(new Question("Which type of network topology connects all devices to a central device?", "Bus", "Star", "Ring", "Star"));
        computerNetworksQuestions.add(new Question("What does a switch do in a network?", "Directs traffic between computers", "Connects different networks", "Encrypts data", "Directs traffic between computers"));

        questionMap.put("Computer Networks", computerNetworksQuestions);

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

        // Python questions
        List<Question> pythonQuestions = new ArrayList<>();
        pythonQuestions.add(new Question("Who created Python?", "James Gosling", "Guido van Rossum", "Dennis Ritchie", "Guido van Rossum"));
        pythonQuestions.add(new Question("Which keyword is used to define a function in Python?", "def", "function", "define", "def"));
        pythonQuestions.add(new Question("What is the extension of a Python file?", ".py", ".java", ".txt", ".py"));
        pythonQuestions.add(new Question("What is the output of print(2**3)?", "5", "6", "8", "8"));
        pythonQuestions.add(new Question("Which data structure is built into Python?", "List", "Array", "Vector", "List"));
        pythonQuestions.add(new Question("What is used to store key-value pairs in Python?", "Tuple", "Dictionary", "List", "Dictionary"));
        pythonQuestions.add(new Question("What is the default value of a boolean variable in Python?", "true", "false", "None", "false"));
        pythonQuestions.add(new Question("Which operator is used for exponentiation in Python?", "**", "^", "exp", "**"));
        pythonQuestions.add(new Question("Which method is used to remove an element from a list in Python?", "remove()", "delete()", "pop()", "remove()"));
        pythonQuestions.add(new Question("Which of the following is not a Python data type?", "int", "str", "char", "char"));

        questionMap.put("Python", pythonQuestions);


        // DSA questions
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

        questionMap.put("DSA", dataStructuresAndAlgorithmsQuestions);


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
            Toast.makeText(this, "Quiz Finished! Your score: " + score * 2, Toast.LENGTH_LONG).show();
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