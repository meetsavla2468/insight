package com.example.education;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class VideoTextExtractorTask extends AppCompatActivity {

    private static final String TAG = "SubtitleExtractor";
    private static final String VIDEO_URL = "https://firebasestorage.googleapis.com/v0/b/insight-210f4.appspot.com/o/upload%2F1731680415331PdbS5NoLERN5LTWLlvgW0He1RjR2?alt=media&token=1a2a5bf6-1740-41f7-8b5e-6e054dde4f1c";
    private final Context context;

    public VideoTextExtractorTask(@NonNull Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadVideoFromFirebase(VIDEO_URL);
    }

    public void downloadVideoFromFirebase(String videoUrl) {
        if (context == null) {
            Log.e(TAG, "Context is null. Cannot proceed with video download.");
            return;
        }
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        try {
            File localFile = new File(context.getCacheDir(), "temp_video.mp4");
            storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "Video downloaded successfully");
                extractSubtitles(localFile.getPath());
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to download video", e);
                Toast.makeText(VideoTextExtractorTask.this, "Video download failed", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error creating temp file", e);
        }
    }

    private void extractSubtitles(String videoPath) {
        try {
            File outputSubtitleFile = new File(context.getCacheDir(), "output.srt");
            String subtitlePath = outputSubtitleFile.getAbsolutePath();

            // FFmpeg command to extract subtitles
            String[] command = new String[]{
                    "-i", videoPath,
                    "-map", "0:s:0",
                    subtitlePath
            };

            int resultCode = FFmpeg.execute(command);

            if (resultCode == Config.RETURN_CODE_SUCCESS) {
                Log.d(TAG, "Subtitles extracted successfully: " + subtitlePath);
                logExtractedSubtitles(subtitlePath);
            } else {
                Log.e(TAG, "Failed to extract subtitles, result code: " + resultCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting subtitles", e);
        }
    }

    private void logExtractedSubtitles(String subtitlePath) {
        try {
            File subtitleFile = new File(subtitlePath);
            if (subtitleFile.exists()) {
                StringBuilder subtitlesContent = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(subtitleFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    subtitlesContent.append(line).append("\n");
                }
                reader.close();
                Log.d(TAG, "Extracted Subtitles: \n" + subtitlesContent);
            } else {
                Log.e(TAG, "Subtitle file does not exist at: " + subtitlePath);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading subtitle file", e);
        }
    }
}
