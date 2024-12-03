package com.example.education;

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

import com.example.education.databinding.FragmentPdfBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import adapter.pdf_RecyclerAdapter;

public class pdf extends Fragment {
    private static final int MAX_TTS_INPUT_LENGTH = 1000; // Max input length for TTS
    TextToSpeech textToSpeech;
    SpeechRecognizer speechRecognizer;
    Intent speechRecognizerIntent;
    FragmentPdfBinding bind;
    ArrayList<Data> pdf_list;
    pdf_RecyclerAdapter adapter;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;
    String course;

    public pdf() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bind = FragmentPdfBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        assert getArguments() != null;
        course = getArguments().getString("course");

        pdf_list = new ArrayList<>();

        String downloadedPdfUrl = "";
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
                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    Toast.makeText(getContext(), "Timeout! Please try speaking again.", Toast.LENGTH_SHORT).show();
                    speechRecognizer.startListening(speechRecognizerIntent);
                } else {
                    Log.e("SpeechRecognizer", "Error: " + error);
                }
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0).trim();
                    navigateToPdf(spokenText, pdf_list);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0).trim();
                    navigateToPdf(spokenText, pdf_list);
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });

        bind.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new pdf_RecyclerAdapter(getContext(), pdf_list, course);

        bind.recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return bind.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        pdf_list.clear();
        fireStore.collection("courses").document(course).collection("pdf").orderBy("timeStamp", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots
                    ) {
                        Data data = doc.toObject(Data.class);
                        pdf_list.add(data);
                        adapter.notifyDataSetChanged();
                        promptUserForPdf();
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

    private void promptUserForPdf() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak("Please speak the name of the pdf", TextToSpeech.QUEUE_FLUSH, null, "INITIAL_PROMPT");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Queue<String> pdfNamesQueue = new LinkedList<>();
                    for (Data item : pdf_list) {
                        pdfNamesQueue.add(item.title);
                    }
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            if (!pdfNamesQueue.isEmpty()) {
                                String nextCourse = pdfNamesQueue.poll();  // Get and remove the next course name from the queue
                                textToSpeech.speak(nextCourse, TextToSpeech.QUEUE_FLUSH, null, "PDF_NAME");
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                            // Handle TTS errors
                        }
                    });

                    // Start speaking the first course name from the queue
                    if (!pdfNamesQueue.isEmpty()) {
                        String firstCourse = pdfNamesQueue.poll();
                        textToSpeech.speak(firstCourse, TextToSpeech.QUEUE_FLUSH, null, "COURSE_NAME");
                    }
                }
            }, 2000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechRecognizer.startListening(speechRecognizerIntent);
                    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
                    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
                }
            }, 3000);
        }
    }

    private void navigateToPdf(String spokenText, ArrayList<Data> pdf_list) {

        PdfTextExtractorTask task = new PdfTextExtractorTask(getContext(), new PdfExtractionCallback() {
            @Override
            public void onPdfTextExtracted(String text) {
                Log.d("Extracted Text", text);
                text = "I will now read out the pdf. " + text;
                speakLongText(text);
            }
        });

        for (Data pdf : pdf_list) {
            if (pdf.title.toLowerCase().contains(spokenText.toLowerCase())) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(pdf.getUrl()), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                requireContext().startActivity(intent);
                String pdfUrl = pdf.getUrl();
                Log.d("msg", pdfUrl);
                task.execute(pdfUrl);
            }
        }

    }

    public void speakLongText(String longText) {
        if (longText.length() > MAX_TTS_INPUT_LENGTH) {
            // If the text is too long, split it into chunks
            List<String> textChunks = splitTextIntoChunks(longText, MAX_TTS_INPUT_LENGTH);

            for (String chunk : textChunks) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(chunk, TextToSpeech.QUEUE_ADD, null, "PDF_TEXT"); // Queue the chunks
                }
            }
        } else {
            // If the text is short enough, speak it directly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(longText, TextToSpeech.QUEUE_FLUSH, null, "PDF_TEXT");
            }
        }
    }

    private List<String> splitTextIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int index = 0;

        while (index < text.length()) {
            // Split the text into manageable chunks
            int endIndex = Math.min(index + chunkSize, text.length());
            chunks.add(text.substring(index, endIndex));
            index = endIndex;
        }

        return chunks;
    }

    private void promptUserForTTS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak("Should I read out the text", TextToSpeech.QUEUE_FLUSH, null, "OCR_PROMPT");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    speechRecognizer.startListening(speechRecognizerIntent);
                    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
                    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
                }
            }, 3000);
        }
    }

    public interface PdfExtractionCallback {
        void onPdfTextExtracted(String text);
    }
}