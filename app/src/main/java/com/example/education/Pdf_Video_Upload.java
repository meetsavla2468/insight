package com.example.education;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.education.databinding.ActivityPdfVideoUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

public class Pdf_Video_Upload extends AppCompatActivity {
    ActivityPdfVideoUploadBinding binding;
    String course = "";
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseFirestore fireStore;
    ProgressDialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (requestCode == 12) {
                    binding.vw.setVisibility(View.GONE);
                    binding.openVideo.setVisibility(View.GONE);
                    binding.parent.setVisibility(View.VISIBLE);
                    binding.pdfView.setVisibility(View.VISIBLE);
                    binding.pdfName.setVisibility(View.VISIBLE);
                    binding.cvUploadBtn.setVisibility(View.VISIBLE);
                    Uri uri = data.getData();
                    getData(12, uri);

                    // Get the Uri of the selected file

                    String displayName = getFileName(uri);
                    binding.pdfName.setText(displayName);
                    binding.pdfView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            startActivity(new Intent(Pdf_Video_Upload.this,PdfViewer.class).putExtra("uri",uri.toString()));
                            openPdfFile(uri);

                        }
                    });

                }
                if (requestCode == 13) {
                    binding.pdfView.setVisibility(View.GONE);
                    binding.pdfName.setVisibility(View.GONE);
                    binding.parent.setVisibility(View.VISIBLE);
                    binding.vw.setVisibility(View.VISIBLE);
                    binding.openVideo.setVisibility(View.VISIBLE);
                    binding.vw.setVideoURI(data.getData());
                    binding.cvUploadBtn.setVisibility(View.VISIBLE);

// Create a MediaController
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(binding.vw);

// Attach the MediaController to the VideoView
                    binding.vw.setMediaController(mediaController);
                    binding.vw.requestFocus();


// Start playing the video
                    binding.vw.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            binding.vw.start();
                            mp.setLooping(true);
                        }
                    });
                    Uri videoUri = data.getData();

                    binding.openVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // Create an intent to play the video
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(videoUri, "video/*");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            // Start the activity to play the video
                            startActivity(intent);
                        }
                    });
                    getData(13, videoUri);
                }
            } else {
                eraseView();
            }

        } else {
            eraseView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityPdfVideoUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.pdfName.setSelected(true);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading...");
        dialog.setMessage("Please wait work in progress");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        binding.parent.setVisibility(View.GONE);
        // Spinner click listener
        //binding.spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Select Category");
        categories.add("Science");
        categories.add("Mathematics");
        categories.add("English Language");
        categories.add("Social Studies");
        categories.add("Geography");
        categories.add("History");
        categories.add("Environmental Studies");
        categories.add("Art and Craft");
        categories.add("Computer Science");
        categories.add("Python");
        categories.add("Physics");
        categories.add("Chemistry");
        categories.add("Biology");
        categories.add("Programming with C");
        categories.add("C++");
        categories.add("Data Structures and Algorithms");
        categories.add("Web Development");
        categories.add("App Development");
        categories.add("Java");
        categories.add("Economics");
        categories.add("Business Studies");
        categories.add("Statistics");
        categories.add("Ruby");
        categories.add("Operating Systems");
        categories.add("Databases (DBMS)");
        categories.add("Artificial Intelligence");
        categories.add("Machine Learning");
        categories.add("Blockchain");
        categories.add("Software Engineering");
        categories.add("Networking");
        categories.add("Other");

        // Creating adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);

        //Drop down layout style - list view with radio button
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //set Adapter to spinner
        binding.spinner.setAdapter(adapter);


        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                course = categories.get(position);
                if (course.equals("Other")) {
                    showCategoryInputDialog();
                } else if (course.equals("Select Category")) {
                    course = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                course = "";
                Toast.makeText(Pdf_Video_Upload.this, "Please Select the category!!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.rdbPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 12);

            }
        });
        binding.rdbVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 13);
            }
        });
    }

    private void showCategoryInputDialog() {
        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Custom Category");

        // Create an EditText to input the category name
        final EditText input = new EditText(this);
        input.setHint("Category Name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String customCategory = input.getText().toString().trim();
                if (!TextUtils.isEmpty(customCategory)) {
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.spinner.getAdapter();
                    adapter.add(customCategory);
                    adapter.notifyDataSetChanged();
                    binding.spinner.setSelection(adapter.getPosition(customCategory));
                } else {
                    Toast.makeText(Pdf_Video_Upload.this, "Category name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                binding.spinner.setSelection(0);
            }
        });

        builder.show();
    }

    private void getData(int requestCode, Uri uri) {
        binding.cvUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.title.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    binding.title.setError("Title is Required");
                    binding.title.requestFocus();
                }
                if (TextUtils.isEmpty(course)) {
                    Toast.makeText(Pdf_Video_Upload.this, "Please select the category", Toast.LENGTH_SHORT).show();
                }
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(course)) {
                    dialog.show();
                    String timeStamp = String.valueOf(new Date().getTime());
                    if (requestCode == 12) {
                        StorageMetadata metadata = new StorageMetadata.Builder()
                                .setContentType("application/pdf")
                                .build();
                        storage.getReference().child("upload").child(timeStamp + auth.getUid()).putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storage.getReference().child("upload").child(timeStamp + auth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        course = course.trim();
                                        fireStore.collection("courses").document(course).collection("pdf").document(timeStamp + auth.getUid()).set(new Data(timeStamp, uri.toString(), title)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(Pdf_Video_Upload.this, "Data uploaded", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(Pdf_Video_Upload.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(Pdf_Video_Upload.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(Pdf_Video_Upload.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        StorageMetadata metadata = new StorageMetadata.Builder()
                                .setContentType("video/mp4")
                                .build();
                        storage.getReference().child("upload").child(timeStamp + auth.getUid()).putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storage.getReference().child("upload").child(timeStamp + auth.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        course = course.trim();
                                        fireStore.collection("courses").document(course).collection("video").document(timeStamp + auth.getUid()).set(new Data(timeStamp, uri.toString(), title)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toast.makeText(Pdf_Video_Upload.this, "Data uploaded", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toast.makeText(Pdf_Video_Upload.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(Pdf_Video_Upload.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(Pdf_Video_Upload.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (displayNameIndex >= 0) {
                        result = cursor.getString(displayNameIndex);
                        // use the displayName value
                    }
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void openPdfFile(Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void eraseView() {
        binding.parent.setVisibility(View.GONE);
        binding.vw.setVisibility(View.GONE);
        binding.openVideo.setVisibility(View.GONE);
        binding.pdfView.setVisibility(View.GONE);
        binding.pdfName.setVisibility(View.GONE);
        binding.cvUploadBtn.setVisibility(View.GONE);
    }
}