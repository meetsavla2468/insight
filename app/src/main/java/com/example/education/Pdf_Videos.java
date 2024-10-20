package com.example.education;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.education.databinding.ActivityPdfVideosBinding;

import adapter.pdf_video;

public class Pdf_Videos extends AppCompatActivity {

    ActivityPdfVideosBinding bind;

    pdf_video pagerAdapter;

    RecyclerView rv_pdf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind=ActivityPdfVideosBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Intent intent=getIntent();
        String course = intent.getStringExtra("course");
        pagerAdapter=new pdf_video(getSupportFragmentManager(),course);

        bind.viewPager2.setAdapter(pagerAdapter);
        bind.tabLayout.setupWithViewPager(bind.viewPager2);

        }
}