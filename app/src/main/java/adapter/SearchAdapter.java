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

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.viewHold> {
    Context context;
    ArrayList<String> arraylist;

    public SearchAdapter(Context context, ArrayList<String> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
    }

    @NonNull
    @Override
    public SearchAdapter.viewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.courses_lay, parent, false);
        return new viewHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.viewHold holder, int position) {
        holder.tv.setText(arraylist.get(position));
        switch (arraylist.get(position)) {
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Pdf_Videos.class);
                intent.putExtra("course", arraylist.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public static class viewHold extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView img;

        public viewHold(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.text_courses_name);
            img = itemView.findViewById(R.id.img_courses);
        }
    }
}
