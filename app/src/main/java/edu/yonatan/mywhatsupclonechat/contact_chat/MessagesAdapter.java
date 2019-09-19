package edu.yonatan.mywhatsupclonechat.contact_chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.yonatan.mywhatsupclonechat.R;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder> {

    //props:
    private Context context;
    private List<PrivateMessage> messageList;
    private FirebaseAuth fbAuth;
    private DatabaseReference fbUserRef;

    //ctor:
    public MessagesAdapter(Context context, List<PrivateMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }


    //inflate layout into RV
    //returns viewHolder:
    @NonNull
    @Override
    public MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.message_layout, parent, false);
        MessagesHolder holder = new MessagesHolder(v);

        fbAuth = FirebaseAuth.getInstance();

        return holder;
    }


    //binds the fields to the required values:
    //Connect the model ref to the adapter -> messageList;
    //checks the msg type;
    //set visibility of the current custom layout text fields:
    //disable visibility to the sender/receiver text fields as needed:
    //set custom Layouts to the required fields:
    @Override
    public void onBindViewHolder(@NonNull MessagesHolder holder, int position) {

        String senderID = fbAuth.getCurrentUser().getUid();
        PrivateMessage message = messageList.get(position);

        String fromUserID = message.getFrom();
        String fromMessageType = message.getType();

        //'Users' Database ref:
        fbUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        //checks msg type:
        if (fromMessageType.equals("text")) {
            //makes the receiver ans sender textField invisible:
            holder.tvReceiverMessage.setVisibility(View.INVISIBLE);
            holder.tvSenderMessage.setVisibility(View.INVISIBLE);

            //checks  the sendser ID:
            //sender id is visible:
            if (fromUserID.equals(senderID)) {
                holder.tvSenderMessage.setVisibility(View.VISIBLE);
                holder.tvSenderMessage.setBackgroundResource(R.drawable.sender_message_layout);
                holder.tvSenderMessage.setText(message.getMessage());


            } else {
                //swap the invisible fields:
                //each sender will only see his msg once, and the receiver msg once instead of twice;
                // each msg is printed twice, in two fields, and one of those fields are invisible, so
                //the user only see one field at a time:

                //receiver id is visible:
                holder.tvReceiverMessage.setVisibility(View.VISIBLE);

                holder.tvReceiverMessage.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.tvReceiverMessage.setText(message.getMessage());

            }
        }

    }

    //the list size:
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    //inner class: init fields to use:
    class MessagesHolder extends RecyclerView.ViewHolder {

        TextView tvReceiverMessage, tvSenderMessage;


        public MessagesHolder(@NonNull View itemView) {
            super(itemView);


            tvSenderMessage = itemView.findViewById(R.id.tvSenderBody);
            tvReceiverMessage = itemView.findViewById(R.id.tvReceiverBody);


        }
    }
}
