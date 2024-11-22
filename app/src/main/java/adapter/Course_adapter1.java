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
            case "Mathematics":
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
