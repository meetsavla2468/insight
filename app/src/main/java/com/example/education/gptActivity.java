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
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private gptAdapter chatAdapter;
    private List<gptMessage_Model> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellness);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        chatAdapter = new gptAdapter(this, messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        sendToGPT("Your task is to return as your response the following sentence exactly word to word :-Hello! I are here to support your mental wellness. Let's chat about anything on your mind and find some peace together.");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = messageInput.getText().toString().trim();
                if (!TextUtils.isEmpty(userMessage)) {
                    // Add the user's message to the chat
                    messageList.add(new gptMessage_Model(userMessage, true));
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    messageInput.setText("");

                    // Call GPT API to get the response
                    sendToGPT(userMessage);
                } else {
                    messageInput.setError("Please enter a message.");
                }
            }
        });
    }

    // Function to send the user message to GPT and get the response
    private void sendToGPT(String userMessage) {
        OkHttpClient client = new OkHttpClient();

        // Formulate the JSON request body
        String json = "{\"messages\": [{\"role\": \"system\", \"content\": \"You are ChatGPT, a language model. For the input prompt if frame the response under 300 tokens strictly.\"}, {\"role\": \"user\", \"content\": \"" + userMessage + "\"}], \"max_tokens\": 500, \"temperature\": 0, \"model\": \"gpt-3.5-turbo\"}";
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, mediaType);

        // Make the POST request to the GPT API
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer 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")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

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
            updateUIWithResponse(msg.getString("content"));
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
