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

        switch (topics.get(position).toLowerCase()) {
            case "mathematics":
                holder.img.setImageResource(R.drawable.ic_pi);
                break;
            case "programming with c":
                holder.img.setImageResource(R.drawable.cgrm);
                break;
            case "app development":
                holder.img.setImageResource(R.drawable.android_developer);
                break;
            case "data structures and algorithms":
                holder.img.setImageResource(R.drawable.algo);
                break;
            default:
                holder.img.setImageResource(R.drawable.py);
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