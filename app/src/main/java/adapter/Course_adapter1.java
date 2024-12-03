package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.education.R;

import java.util.ArrayList;

public class Course_adapter1 extends RecyclerView.Adapter<Course_adapter1.ViewHolder> {

    Context context;
    ArrayList<String> courses_list;

    public Course_adapter1(Context context, ArrayList<String> courses_list) {
        this.courses_list = courses_list;
        this.context = context;
    }


    @NonNull
    @Override
    public Course_adapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.courses_lay, parent, false);
        return new Course_adapter1.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Course_adapter1.ViewHolder holder, int position) {
        holder.tv.setText(courses_list.get(position));
        switch (courses_list.get(position)) {
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
            case "Stories":
                holder.img.setImageResource(R.drawable.stories);
                break;
            case "KG":
                holder.img.setImageResource(R.drawable.kg);
                break;
            case "Basic Maths":
                holder.img.setImageResource(R.drawable.kid_maths);
                break;
            case "Cartoons":
                holder.img.setImageResource(R.drawable.cartoon);
                break;
            default:
                holder.img.setImageResource(R.drawable.android_developer);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return courses_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.text_courses_name);
            img = itemView.findViewById(R.id.img_courses);
        }
    }
}
