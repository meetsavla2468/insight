package com.example.education;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.example.education.databinding.ActivitySearchBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import Model.Course_Model;
import adapter.SearchAdapter;

public class search extends AppCompatActivity {
    SearchAdapter adapter;
    ActivitySearchBinding bind;
    ArrayList<String> arrayList;
    ArrayList<String> list;
    FirebaseAuth auth;
    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        arrayList = new ArrayList<>();
        list = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        adapter = new SearchAdapter(this, arrayList);
        bind.searchRv.setLayoutManager(new LinearLayoutManager(this));
        bind.searchRv.setAdapter(adapter);
        fireStore.collection("courses").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null) {
                    list.clear();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots
                    ) {
                        String course = snap.getId();
                        if (!course.equals("Basic Maths") && !course.equals("Stories") && !course.equals("KG") && !course.equals("Cartoons")) {
                            list.add(course);
                        }
                    }
                    arrayList.clear();
                    arrayList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(search.this, "Problem occur", Toast.LENGTH_SHORT).show();
            }
        });

        bind.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFun(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchFun(CharSequence str) {


        if (TextUtils.isEmpty(str)) {
            arrayList.clear();
            arrayList.addAll(list);
            adapter.notifyDataSetChanged();
        } else {
            arrayList.clear();
            for (String item : list
            ) {
                if (item.contains(str)) {
                    arrayList.add(item);
                }
            }
            adapter.notifyDataSetChanged();
        }
//        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}