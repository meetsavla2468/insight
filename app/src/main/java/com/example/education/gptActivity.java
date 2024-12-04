package com.example.education;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import adapter.gptAdapter;
import Model.gptMessage_Model;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class gptActivity extends AppCompatActivity {
    String OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY;
    private String inputMessage = "";
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private gptAdapter chatAdapter;
    private List<gptMessage_Model> messageList;
    private List<JSONObject> conversationHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellness);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        conversationHistory = new ArrayList<>();
        messageList = new ArrayList<>();
        chatAdapter = new gptAdapter(this, messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        try {
            sendToGPT1("Your task is to return as your response the following sentence exactly word to word :-Hello! I are here to support your mental wellness. Let's chat about anything on your mind and find some peace together.'");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are ChatGPT, a language model. Respond concisely and frame responses under 300 tokens.");
            conversationHistory.add(systemMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = messageInput.getText().toString().trim();
                if (!TextUtils.isEmpty(userMessage)) {
                    try {
                        addMessageToHistory("user", userMessage);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    inputMessage = userMessage;
                    messageList.add(new gptMessage_Model(userMessage, true));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    messageInput.setText("");

                    // Call GPT API to get the response
                    try {
                        sendToGPT();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    messageInput.setError("Please enter a message.");
                }
            }
        });
    }

    private void sendToGPT1(String userMessage) throws JSONException {
        OkHttpClient client = new OkHttpClient();

        // Formulate the JSON request body

        JSONObject jsonObject = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are ChatGPT, a language model. For the input prompt if frame the response under 300 tokens strictly.");
        messagesArray.put(systemMessage);

        JSONObject userMessageObj = new JSONObject();
        userMessageObj.put("role", "user");
        userMessageObj.put("content", userMessage);  // This will automatically escape special characters
        messagesArray.put(userMessageObj);

        jsonObject.put("messages", messagesArray);
        jsonObject.put("max_tokens", 500);
        jsonObject.put("temperature", 0);
        jsonObject.put("model", "gpt-3.5-turbo");

        // Formulate the JSON request body
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(), mediaType);

        // Make the POST request to the GPT API
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Log.d("Gpt request", String.valueOf(body));

        // Execute the API call asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    extractResponseContent(responseBody);
                    Log.d("Gpt response", responseBody);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Function to send the user message to GPT and get the response
    private void addMessageToHistory(String role, String content) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("role", role);
        message.put("content", content);
        conversationHistory.add(message);
    }

    private void sendToGPT() throws JSONException {
        OkHttpClient client = new OkHttpClient();

        // Prepare JSON request body
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messages", new JSONArray(conversationHistory));
        jsonObject.put("max_tokens", 500);
        jsonObject.put("temperature", 0);
        jsonObject.put("model", "gpt-4");

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(), mediaType);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Log.d("Gpt request", String.valueOf(body));

        // Execute the API call asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String responseBody = Objects.requireNonNull(response.body()).string();
                    extractResponseContent(responseBody);
                    Log.d("Gpt response", responseBody);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Extract the response from the GPT API response
    public void extractResponseContent(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray choicesArray = jsonObject.getJSONArray("choices");
            JSONObject firstChoice = choicesArray.getJSONObject(0);
            JSONObject msg = firstChoice.getJSONObject("message");
            String response = msg.getString("content");
            if (inputMessage.toLowerCase().contains("gpt") || inputMessage.toLowerCase().contains("are you") || inputMessage.toLowerCase().contains("generative pretrained transformer") || response.toLowerCase().contains("gpt") || response.toLowerCase().contains("generative pretrained transformer")) {
                addMessageToHistory("assistant", "I am a fine tuned Large Language Model");
                updateUIWithResponse("I am a fine tuned Large Language Model");
            } else {
                addMessageToHistory("assistant", response);
                updateUIWithResponse(msg.getString("content"));
            }
            Log.d("Info", responseBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Update the UI with the GPT response
    private void updateUIWithResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Add GPT response to chat
                messageList.add(new gptMessage_Model(response, false));  // false means GPT
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }
}
