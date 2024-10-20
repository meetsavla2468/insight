package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.education.R; // Replace with your actual package name

import java.util.List;

import Model.Course_Model;

public class Course_adapter extends RecyclerView.Adapter<Course_adapter.CourseViewHolder> {
    private List<Course_Model> courseList;

    public Course_adapter(List<Course_Model> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_popular_courses, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course_Model course = courseList.get(position);
        holder.courseName.setText(course.getName());
        holder.courseImage.setImageResource(course.getImg());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView courseName;
        ImageView courseImage;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.text_courses_name);
            courseImage = itemView.findViewById(R.id.img_courses);
        }
    }
}
