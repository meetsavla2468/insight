package com.example.education;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Model.Course_Model;

public class Content_main extends RecyclerView.Adapter<Content_main.ViewHolder>{

    private final Context context;
    ArrayList<Course_Model> list;

    public Content_main(Context context, ArrayList<Course_Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Content_main.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_popular_courses,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Content_main.ViewHolder holder, int position) {
        Course_Model model = list.get(holder.getAdapterPosition());
        if(model!=null) {
            holder.textView.setSelected(true);
            holder.textView.setText(model.getName());
//        holder.img.setBackgroundResource(model.getImg());
        holder.img.setImageResource(model.getImg());
//        holder.img.setImageDrawable(ContextCompat.getDrawable(context,model.getImg()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,Pdf_Videos.class);
                    intent.putExtra("course",model.getName().trim());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_courses);
            textView = itemView.findViewById(R.id.text_courses_name);
        }
    }
}
