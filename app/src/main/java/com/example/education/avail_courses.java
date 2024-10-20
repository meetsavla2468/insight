package com.example.education;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.example.education.databinding.ActivityCoursesBinding;

import java.util.ArrayList;

import adapter.Course_adapter1;

public class avail_courses extends AppCompatActivity {
    ActivityCoursesBinding binding;
    Course_adapter1 courseAdapter;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCoursesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        list=new ArrayList<>();
        list.add("Android App development");
        list.add("C++");
        list.add("Programming with C");
        list.add("Web Development");
        list.add("Ruby");
        list.add("PHP");
        list.add("SQL");
        list.add("JavaScript(JS)");
        list.add("Firebase Storage");
        list.add("High School Science");
        list.add("High School Social Science");
        list.add("High School Mathematics");
        list.add("High School English");
        list.add("Senior Secondary Science");
        list.add("Senior Secondary Mathematics");
        list.add("English");

        courseAdapter=new Course_adapter1(avail_courses.this,list);
        binding.cRv.setLayoutManager(new GridLayoutManager(avail_courses.this,1));
        binding.cRv.setAdapter(courseAdapter);
    }
}