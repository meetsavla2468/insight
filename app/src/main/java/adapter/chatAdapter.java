package adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.education.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Model.MsgModel;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.MyViewHolder> {

    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Context context;
    ArrayList<MsgModel> list;
    String username;
//    private final ItemClickListener itemClickListener;

    public chatAdapter(Context context, ArrayList<MsgModel> list) {
        this.context = context;
        this.list = list;
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.msg_send_item,parent,false);


        // create an ItemTouchHelper instance and attach it to the RecyclerView
        return new chatAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MsgModel msgModel=list.get(holder.getAdapterPosition());
        if(msgModel!=null){
            if(msgModel.getByWho().equals(auth.getUid())){
                String you="you";
                holder.name.setText(you);
                holder.layout.setGravity(Gravity.END);
            }else{
                holder.layout.setGravity(Gravity.START);
//                firestore.collection("Users").document(msgModel.getByWho()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if(documentSnapshot!=null){
//                            holder.name.setText(Objects.requireNonNull(documentSnapshot.get("userName")).toString());
//                        }
//                    }
//                });
                holder.name.setText(msgModel.getName());
//                holder.name.setText(username);

            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Handle long click on item view
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirm Deletion");
                        builder.setMessage("Are you sure you want to delete this item?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete the item
                                firestore.collection("Discussion").document(msgModel.getDocumentId()).delete();
                                list.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss the dialog
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }

                    return false;
                }
            });
            holder.img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Handle long click on item view
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirm Deletion");
                        builder.setMessage("Are you sure you want to delete this item?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete the item
                                firestore.collection("Discussion").document(msgModel.getDocumentId()).delete();
                                list.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss the dialog
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        return true;
                    }

                    return false;
                }
            });



            if(msgModel.getType().equals("text")) {
                holder.img.setVisibility(View.GONE);
                holder.msg.setVisibility(View.VISIBLE);
                holder.msg.setText(msgModel.getMsg());
            }
            if(msgModel.getType().equals("Image")){
                holder.msg.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                Glide.with(context).load(msgModel.getMsg()).placeholder(R.drawable.boy_bag).error(R.drawable.profile_luffy).into(holder.img);
                holder.img.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(msgModel.getMsg()));
//            if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
//            }
                });
            }
            if(msgModel.getType().equals("Video")){
                holder.msg.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.profile_luffy).placeholder(R.drawable.boy_bag).error(R.drawable.profile_luffy).into(holder.img);
                holder.img.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(msgModel.getMsg()), "video/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    context.startActivity(intent);
//            }
                });


            }
            if(msgModel.getType().equals("PDF")){
                holder.msg.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.profile_luffy).placeholder(R.drawable.boy_bag).error(R.drawable.profile_luffy).into(holder.img);
                holder.img.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(msgModel.getMsg()), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
//            }
                });


            }
            if(msgModel.getType().equals("Word Document")){
                dataResource(holder,msgModel);

            }
            if(msgModel.getType().equals("PowerPoint Presentation")){
                dataResource(holder,msgModel);
            }
            if(msgModel.getType().equals("Excel Spreadsheet")){
                dataResource(holder,msgModel);

            }
            holder.time.setText(msgModel.getDate());

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    private void dataResource(@NonNull MyViewHolder holder, MsgModel msgModel){
        holder.msg.setVisibility(View.GONE);
        holder.img.setVisibility(View.VISIBLE);
        Glide.with(context).load(R.drawable.profile_luffy).placeholder(R.drawable.boy_bag).error(R.drawable.profile_luffy).into(holder.img);
        holder.img.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(msgModel.getMsg()));
//            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        });

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //        ItemClickListener itemClickListener;
        LinearLayout layout;
        TextView name, time, msg;
        ImageView img;
        String message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView3);
            msg = itemView.findViewById(R.id.textView4);
            time = itemView.findViewById(R.id.reply_time);
            layout = itemView.findViewById(R.id.layout);
            img = itemView.findViewById(R.id.img);


        }
    }
}