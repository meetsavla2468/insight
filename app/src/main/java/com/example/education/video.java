package com.example.education;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.education.databinding.FragmentVideoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import adapter.VideoAdapter;

public class video extends Fragment {
    TextToSpeech textToSpeech;
    SpeechRecognizer speechRecognizer;
    Intent speechRecognizerIntent;
    FragmentVideoBinding bind;
    ArrayList<Data> video_list;
    VideoAdapter adapter;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    String course;

    public video() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bind = FragmentVideoBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        assert getArguments() != null;
        course = getArguments().getString("course");

        video_list = new ArrayList<>();

        // Initialize Text to Speech
        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        // Initialize Speech Recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {
                Log.e("SpeechRecognizer", "Error: " + error);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0).trim();
                    navigateToVideo(spokenText, video_list);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0).trim();
                    navigateToVideo(spokenText, video_list);
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        bind.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
//
        adapter = new VideoAdapter(getContext(), video_list, course);
//
        bind.recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return bind.getRoot();
    }

    public void onStart() {
        super.onStart();
        video_list.clear();
        fireStore.collection("courses").document(course).collection("video").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots
                    ) {
                        Data data = doc.toObject(Data.class);
                        video_list.add(data);
                        Log.d("video", data.getUrl());
                        downloadVideoSubtitles(data.getUrl());
                        adapter.notifyDataSetChanged();
                        //promptUserForVideo();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "No result found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void downloadVideoSubtitles(String url) {
        VideoTextExtractorTask videoTask = new VideoTextExtractorTask(requireContext());
        videoTask.downloadVideoFromFirebase(url);
    }

    private void navigateToVideo(String spokenText, ArrayList<Data> video_list) {
        for (Data video : video_list) {
            if (video.title.toLowerCase().contains(spokenText.toLowerCase())) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(video.getUrl()), "application/video");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                requireContext().startActivity(intent);
            }
        }
    }

//    public void onVideoItemClick(String videoUrl) {
//        File audioFile = new File(requireContext().getExternalFilesDir(null), "extracted_audio.wav");
//        downloadAndExtractAudio(videoUrl, audioFile,
//                unused -> {
//                    transcribeAudio(audioFile);
//                },
//                e -> {
//                    Toast.makeText(getContext(), "Error downloading or processing video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }

    private void promptUserForVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak("Please speak the name of the video", TextToSpeech.QUEUE_FLUSH, null, "INITIAL_PROMPT");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Queue<String> videoNamesQueue = new LinkedList<>();
                    for (Data item : video_list) {
                        videoNamesQueue.add(item.title);
                    }
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            if (!videoNamesQueue.isEmpty()) {
                                String nextCourse = videoNamesQueue.poll();  // Get and remove the next course name from the queue
                                textToSpeech.speak(nextCourse, TextToSpeech.QUEUE_FLUSH, null, "VIDEO_NAME");
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                            // Handle TTS errors
                        }
                    });

                    // Start speaking the first course name from the queue
                    if (!videoNamesQueue.isEmpty()) {
                        String firstCourse = videoNamesQueue.poll();
                        textToSpeech.speak(firstCourse, TextToSpeech.QUEUE_FLUSH, null, "COURSE_NAME");
                    }
                }
            }, 2000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            }, 3000);
        }
    }

}