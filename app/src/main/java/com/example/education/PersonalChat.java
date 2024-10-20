package com.example.education;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.education.databinding.ActivityPersonalChatBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import Model.MsgModel;
import adapter.chatAdapter;

public class PersonalChat extends AppCompatActivity{

    private static final int REQUEST_CODE = 1932;
    private ActivityPersonalChatBinding binding;
    private String id;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ProgressDialog progressDialogExample;

    private ArrayList<MsgModel> list;

    private chatAdapter adapter;
    private ListenerRegistration listenerRegistration;
    private  String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        getSupportActionBar().hide();
        progressDialogExample=new ProgressDialog(this);
        progressDialogExample.setMessage("Sending...");
        progressDialogExample.setCancelable(false);


        list = new ArrayList<>();
        adapter = new chatAdapter(this, list);
        binding.msgRv.setHasFixedSize(true);
        binding.usernameTxt.setSelected(true);
        binding.msgRv.setLayoutManager(new LinearLayoutManagerWrapper(PersonalChat.this, LinearLayoutManager.VERTICAL, true));
        binding.msgRv.setAdapter(adapter);
        firestore.collection("Users").document(Objects.requireNonNull(auth.getUid())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot!=null){
                            name=Objects.requireNonNull(documentSnapshot.get("userName")).toString();
                        }
                    }
                });





        show();
        notChecked();
    }


    public void getRes(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*", "application/pdf", "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel"});
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                progressDialogExample.show();

                Uri selectedFileUri = data.getData();
//                String filePath=getFilePath(selectedFileUri);
//                String fileType = getFileType(filePath);
                String fileType = getFileType(selectedFileUri);
//                Toast.makeText(this, fileType, Toast.LENGTH_SHORT).show();
                DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
                Date date = new Date();
                String timeS = formatter.format(date);
                String timeStamp=String.valueOf(new Date().getTime());
                storage.getReference().child("Discussion").child(auth.getUid()+timeStamp).putFile(selectedFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storage.getReference().child("Discussion").child(auth.getUid()+timeStamp).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                firestore.collection("Discussion").document().set(new MsgModel(timeStamp, auth.getUid(), uri.toString(), timeS,name,fileType)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialogExample.dismiss();

                                    }
                                }).addOnFailureListener(e -> {
                                    progressDialogExample.dismiss();

                                    Toast.makeText(PersonalChat.this, "Failed to send msg", Toast.LENGTH_SHORT).show();
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialogExample.dismiss();
                                Toast.makeText(PersonalChat.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialogExample.dismiss();
                        Toast.makeText(PersonalChat.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


//                Toast.makeText(this, selectedFileUri.toString(), Toast.LENGTH_SHORT).show();


// Use the filePath to access the selected file

                // Use the selectedFileUri to access the file data


            }

        }

    }
    private String getFileType(Uri uri) {
//        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimeType = mimeTypeMap.getMimeTypeFromExtension(fileExtension.toLowerCase());

        if (mimeType != null) {
            if (mimeType.startsWith("image")) {
                return "Image";
            } else if (mimeType.startsWith("video")) {
                return "Video";
            } else if (mimeType.equals("application/pdf")) {
                return "PDF";
            } else if (mimeType.equals("application/msword") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                return "Word Document";
            } else if (mimeType.equals("application/vnd.ms-powerpoint") || mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                return "PowerPoint Presentation";
            } else if (mimeType.equals("application/vnd.ms-excel") || mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                return "Excel Spreadsheet";
            } else {
                return "Unknown1";
            }
        }
        String filePath=getFilePath(uri);

        return getFileType2(filePath);
    }
    private String getFileType2(String filePath) {
        String fileType = null;
        if (filePath != null) {
            String extension = getFileExtension(filePath);
            if (extension != null && !extension.isEmpty()) {
                if (extension.equalsIgnoreCase("pdf")) {
                    fileType = "PDF";
                } else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx")) {
                    fileType = "Word Document";
                } else if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx")) {
                    fileType = "PowerPoint Presentation";
                } else if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx")) {
                    fileType = "Excel Spreadsheet";
                } else if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
                    fileType = "Image";
                } else if (extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("3gp") || extension.equalsIgnoreCase("avi")) {
                    fileType = "Video";
                } else {
                    fileType = "Unknown";
                }
            }
        }
        return fileType;
    }
    private String getFileExtension(String filePath) {
        String extension = null;
        if (filePath != null && !filePath.isEmpty()) {
            int dotIndex = filePath.lastIndexOf(".");
            if (dotIndex != -1 && dotIndex < filePath.length() - 1) {
                extension = filePath.substring(dotIndex + 1).toLowerCase();
            }
        }
        return extension;
    }



    private String getFilePath(Uri selectedFileUri) {

        String filePath = null;
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(selectedFileUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        if (filePath == null) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedFileUri);
                if (inputStream != null) {
                    File tempFile = createTempFileFromInputStream(inputStream);
                    filePath = tempFile.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }
    private File createTempFileFromInputStream(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile("temp", null);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
        out.close();
        inputStream.close();
        return tempFile;
    }

    private void show(){
        final boolean[] firstLoad = {true};
        list.clear();
        listenerRegistration= firestore.collection("Discussion").orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(  (value, error) -> {

            if(error!=null){
                Toast.makeText(PersonalChat.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            else{
                if(value!=null) {
                    if (firstLoad[0]) {
                        list.clear();

                        for (DocumentChange snapshot : value.getDocumentChanges()) {
                            if(snapshot.getType()== DocumentChange.Type.ADDED) {
                                MsgModel msgModel = snapshot.getDocument().toObject(MsgModel.class);
                                msgModel.setDocumentId(snapshot.getDocument().getId());

                             //   Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();

                                list.add(msgModel);
                                adapter.notifyItemInserted(list.size() - 1);
                            }
                        }

                        firstLoad[0] = false;
                    } else {
                        for (DocumentChange snapshot : value.getDocumentChanges()) {
                            if (snapshot.getType() == DocumentChange.Type.ADDED) {
                                MsgModel msgModel = snapshot.getDocument().toObject(MsgModel.class);
                                msgModel.setDocumentId(snapshot.getDocument().getId());
                                list.add(0, msgModel);
                                adapter.notifyItemInserted(0);

                            }
                        }
                        binding.msgRv.scrollToPosition(value.getDocumentChanges().size() - 1);

                    }
                }
            }
        });
    }


    private void notChecked() {
        binding.imageButton2.setOnClickListener(v -> {
            String msg = binding.editTextTextMultiLine.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) {
                binding.editTextTextMultiLine.setError("Empty");
                binding.editTextTextMultiLine.requestFocus();
            } else {
                binding.editTextTextMultiLine.setText("");
                DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
                Date date = new Date();
                String timeS = formatter.format(date);
                String timeStamp=String.valueOf(new Date().getTime());
                firestore.collection("Discussion").document().set(new MsgModel(timeStamp, auth.getUid(), msg, timeS,name,"text")).addOnFailureListener(e -> Toast.makeText(PersonalChat.this, "Failed to send msg", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

}