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
    View view = LayoutInflater.from(context).inflate(R.layout.courses_lay,parent,false);
    return  new viewHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.viewHold holder, int position) {
        holder.tv.setText(arraylist.get(position));
        switch (arraylist.get(position)){
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
                intent.putExtra("course",arraylist.get(holder.getAdapterPosition()));
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
            tv=itemView.findViewById(R.id.text_courses_name);
            img=itemView.findViewById(R.id.img_courses);
        }
    }
}
