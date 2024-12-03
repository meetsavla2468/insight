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

import com.example.education.QuizPlayer;
import com.example.education.R;

import java.util.ArrayList;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.viewHold> {
    Context context;
    ArrayList<String> topics;
    ArrayList<String> scores;

    public QuizAdapter(Context context, ArrayList<String> topics, ArrayList<String> scores) {
        this.context = context;
        this.topics = topics;
        this.scores = scores;
    }

    @NonNull
    @Override
    public QuizAdapter.viewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.topics_lay, parent, false);
        return new viewHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizAdapter.viewHold holder, int position) {
        holder.tv.setText(topics.get(position));
        holder.score.setText(scores.get(position));

        switch (topics.get(position)) {
            case "Science":
                holder.img.setImageResource(R.drawable.science);
                break;
            case "Mathematics":
                holder.img.setImageResource(R.drawable.mathematics);
                break;
            case "English Language":
                holder.img.setImageResource(R.drawable.english);
                break;
            case "Geography":
                holder.img.setImageResource(R.drawable.geography);
                break;
            case "History":
                holder.img.setImageResource(R.drawable.history);
                break;
            case "Physics":
                holder.img.setImageResource(R.drawable.physics);
                break;
            case "Chemistry":
                holder.img.setImageResource(R.drawable.chemistry);
                break;
            case "Biology":
                holder.img.setImageResource(R.drawable.biology);
                break;
            case "Computer Science":
                holder.img.setImageResource(R.drawable.computer_science);
                break;
            case "Python":
                holder.img.setImageResource(R.drawable.python);
                break;
            case "Programming with C":
                holder.img.setImageResource(R.drawable.c);
                break;
            case "C++":
                holder.img.setImageResource(R.drawable.cplusplus);
                break;
            case "Java":
                holder.img.setImageResource(R.drawable.java);
                break;
            case "DSA":
                holder.img.setImageResource(R.drawable.datastructure);
                break;
            case "Web Development":
                holder.img.setImageResource(R.drawable.web);
                break;
            case "App Development":
                holder.img.setImageResource(R.drawable.app);
                break;
            case "Probability and Statistics":
                holder.img.setImageResource(R.drawable.probability);
                break;
            case "Computer Graphics":
                holder.img.setImageResource(R.drawable.cg);
                break;
            case "Operating Systems":
                holder.img.setImageResource(R.drawable.os);
                break;
            case "Databases (DBMS)":
                holder.img.setImageResource(R.drawable.database);
                break;
            case "Artificial Intelligence":
                holder.img.setImageResource(R.drawable.ai);
                break;
            case "Machine Learning":
                holder.img.setImageResource(R.drawable.ml);
                break;
            case "Blockchain":
                holder.img.setImageResource(R.drawable.blockchain);
                break;
            case "Software Engineering":
                holder.img.setImageResource(R.drawable.software);
                break;
            case "Computer Networks":
                holder.img.setImageResource(R.drawable.cn);
                break;
            case "Economics":
                holder.img.setImageResource(R.drawable.economics);
                break;
            default:
                holder.img.setImageResource(R.drawable.android_developer);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizPlayer.class);
            intent.putExtra("topic", topics.get(holder.getAdapterPosition()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class viewHold extends RecyclerView.ViewHolder {
        TextView tv, score;
        ImageView img;

        public viewHold(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.text_courses_name);
            img = itemView.findViewById(R.id.img_courses);
            score = itemView.findViewById(R.id.text_score); // Add this TextView to courses_lay.xml
        }
    }
}