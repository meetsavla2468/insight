package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.education.Pdf_Videos;
import com.example.education.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

import Model.Course_Model;

public class FireStore_RV_courses extends RecyclerView.Adapter<FireStore_RV_courses.ViewHolder> {
    ArrayList<String> list;
    Context context;

    public FireStore_RV_courses(ArrayList<String> list, Context context) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item,parent,false);
        return new FireStore_RV_courses.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv.setText(list.get(holder.getAdapterPosition()));
        switch (list.get(position)){
            case "App Development":
                holder.img.setImageResource(R.drawable.android_developer);
                break;
            case "C++":
                holder.img.setImageResource(R.drawable.plus);
                break;
            case "Data Structures and Algorithms":
                holder.img.setImageResource(R.drawable.algo);
                break;
            case "Java":
                holder.img.setImageResource(R.drawable.java_logo);
                break;
            case "MATHEMATICS":
                holder.img.setImageResource(R.drawable.ic_pi);
                break;
            case "Programming with C":
                holder.img.setImageResource(R.drawable.cgrm);
                break;
            case "Python":
                holder.img.setImageResource(R.drawable.py);
                break;
            case "Ruby":
                holder.img.setImageResource(R.drawable.ruby);
                break;
            case "Web Development":
                holder.img.setImageResource(R.drawable.web);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Pdf_Videos.class);
                intent.putExtra("course",list.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv= itemView.findViewById(R.id.text_courses_name);
            img= itemView.findViewById(R.id.img_courses);
        }
    }
}
