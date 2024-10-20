package com.example.education;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.education.databinding.ActivityProfileBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Objects;

import Model.Course_Model;
import Model.User_Model;
import adapter.FireStore_RV_courses;

public class profile extends AppCompatActivity {
    FirebaseFirestore store = FirebaseFirestore.getInstance();
    private final CollectionReference recent_list = store.collection("courses");
    FirebaseAuth auth;
    final int SELECT_PICTURE=654;
    final int SELECT_PICTURE_BG=655;
    FirebaseStorage storage;
    private ActivityProfileBinding binding;
    private FireStore_RV_courses adapter;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        list=new ArrayList<>();
        setUpRecyclerView();


        store.collection("Users").document(Objects.requireNonNull(auth.getUid())).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot!=null){
                    User_Model model=documentSnapshot.toObject(User_Model.class);
                    if(model!=null){
                        binding.usernamePrfPage.setText(model.getUserName());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.prfImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
        });

        binding.diagonalLayout.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE_BG);
        });



    }

    private void setUpRecyclerView() {
        //Query for Recycler View in FireStore
        Query query = recent_list
                .orderBy("course", Query.Direction.DESCENDING)
                .limit(6)
                ;

        adapter = new FireStore_RV_courses(list,profile.this);

        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(profile.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    if (value!=null){
                        for (QueryDocumentSnapshot snap:value
                             ) {
                            list.add(snap.getId());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

        binding.connectionPrfRv.setLayoutManager(new GridLayoutManager(profile.this,2));
        binding.connectionPrfRv.setHasFixedSize(true);
        binding.connectionPrfRv.setAdapter(adapter);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                storage.getReference().child("Users").child("profile_pic").putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storage.getReference().child("Users").child("profile_pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                store.collection("Users").document(auth.getUid()).update("profile_pic",uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(profile.this,"Image Uploaded Successfully!!!", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (requestCode == SELECT_PICTURE_BG) {
                storage.getReference().child("Users").child("profile_bg").putFile(data.getData()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storage.getReference().child("Users").child("profile_bg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                store.collection("Users").document(Objects.requireNonNull(auth.getUid())).update("profile_bg",uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(profile.this,"Image Uploaded Successfully!!!", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(profile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

     @Override
     protected void onStart() {
        super.onStart();

        //For showing the updated pic
        store.collection("Users").document(Objects.requireNonNull(auth.getUid())).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(profile.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }else {
                    if (value!=null){
                        User_Model user_model = value.toObject(User_Model.class);
                        if (user_model!=null){
                            Glide.with(profile.this).load(user_model.getProfile_pic()).error(R.drawable.boy_bag).placeholder(R.drawable.boy_bag).into(binding.prfImg);
                            Glide.with(profile.this).load(user_model.getProfile_bg()).error(R.drawable.classroom_1).placeholder(R.drawable.classroom_1).into(binding.diagonalLayout);
                        }
                    }
                }
            }
        });
     }

    @Override
    protected void onStop() {
        super.onStop();
        //Prevent the frequent update of data when you move on background
    }
}