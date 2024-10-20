package adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.education.R;
import Model.gptMessage_Model;
import java.util.List;

public class gptAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_GPT = 2;

    private Context context;
    private List<gptMessage_Model> messageList;

    public gptAdapter(Context context, List<gptMessage_Model> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_GPT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_gpt_message, parent, false);
            return new GPTMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        gptMessage_Model message = messageList.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).messageText.setText(message.getMessage());
        } else {
            ((GPTMessageViewHolder) holder).messageText.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public UserMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.userMessageText);
        }
    }

    public static class GPTMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public GPTMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.gptMessageText);
        }
    }
}
