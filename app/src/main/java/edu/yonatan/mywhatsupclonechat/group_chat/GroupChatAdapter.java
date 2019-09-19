package edu.yonatan.mywhatsupclonechat.group_chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.yonatan.mywhatsupclonechat.R;


public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.GroupAdapter> {

    //props:
    private Context context;
    private List<ChatMessage> messageList;

    //ctor:
    public GroupChatAdapter(List<ChatMessage> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //binder:
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.row_layout, parent, false);

        GroupAdapter holder = new GroupAdapter(v);


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter holder, int position) {
        ChatMessage message = messageList.get(position);


        holder.tvGroupMessage.setText(message.getBody());
        holder.tvMessageTime.setText(message.getTime());
        holder.tvMessageTitle.setText(message.getTitle());


    }

    //returns the size of the list: -> how many items to display:
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    //adapter class:
    class GroupAdapter extends RecyclerView.ViewHolder {

        TextView tvGroupMessage;
        TextView tvMessageTitle;
        TextView tvMessageTime;

        public GroupAdapter(@NonNull View itemView) {
            super(itemView);

            tvGroupMessage = itemView.findViewById(R.id.tvGroupChatDisplay);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
            tvMessageTitle = itemView.findViewById(R.id.tvMessageTitle);


        }
    }
}
