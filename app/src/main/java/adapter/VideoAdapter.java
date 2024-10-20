package adapter;

        import android.content.Context;
        import android.content.Intent;
        import android.net.Uri;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.education.Data;
        import com.example.education.R;

        import java.util.ArrayList;

        import Model.Course_Model;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{

    private final Context context;
    ArrayList<Data> pdf_list;

    String  course;
    public VideoAdapter(Context context, ArrayList<Data> pdf_list,String  course) {
        this.context = context;
        this.pdf_list = pdf_list;
        this.course=course;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item,parent,false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
        Data data=pdf_list.get(position);
        if (data != null) {
            holder.tv.setText(data.getTitle());
            switch (course){
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
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        // Create an intent to play the video
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(data.getUrl()), "video/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        // Start the activity to play the video
                        context.startActivity(intent);
                }

            });
        }

    }

    @Override
    public int getItemCount() {
        return pdf_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.text_courses_name);
            img = itemView.findViewById(R.id.img_courses);
            tv.setSelected(true);
        }
    }
}

