package com.example.education;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import adapter.gptAdapter;
import Model.gptMessage_Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class Setting extends AppCompatActivity implements PdfExtractionCallback {
    private static final int PICK_PDF_REQUEST = 1;
    String OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton uploadButton;
    private gptAdapter chatAdapter;
    private List<gptMessage_Model> messageList;
    private String extractedPdfText = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri pdfUri = data.getData();
            if (pdfUri != null) {
                File pdfFile = createFileFromUri(pdfUri);
                if (pdfFile != null) {
                    new PdfTextExtractorTaskGPT(this, this).execute(pdfFile.getAbsolutePath());
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpt);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> openFilePicker());

        messageList = new ArrayList<>();
        chatAdapter = new gptAdapter(this, messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        try {
            sendToGPT("Your task is to return as your response the following sentence exactly word to word :- 'Hello there, ask your doubts freely and I will do my best to answer them.'");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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
                    try {
                        sendToGPT(userMessage);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    messageInput.setError("Please enter a message.");
                }
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    // Function to send the user message to GPT and get the response
    private void sendToGPT(String userMessage) throws JSONException {
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

    private File createFileFromUri(Uri uri) {
        File pdfFile = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            String fileName = getFileName(uri);
            pdfFile = new File(getCacheDir(), fileName);
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            Log.e("PDF File", "Error creating file from URI", e);
        }
        return pdfFile;
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String displayName = "document.pdf";
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return displayName;
    }

    @Override
    public void onPdfTextExtracted(String extractedText) throws JSONException {
        this.extractedPdfText = extractedText;
        Toast.makeText(this, "PDF uploaded successfully!", Toast.LENGTH_SHORT).show();
        sendToGPT(extractedText);
    }
}