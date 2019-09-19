package edu.yonatan.mywhatsupclonechat.private_chat_contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.mywhatsupclonechat.Contact;
import edu.yonatan.mywhatsupclonechat.R;
import edu.yonatan.mywhatsupclonechat.contact_chat.ChatActivity;


public class ChatContactsAdapter extends FirebaseRecyclerAdapter<Contact, ChatContactsAdapter.ChatContactsHolder> {

    //props:
    private Context context;
    private String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference fbUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


    //ctor:
    public ChatContactsAdapter(@NonNull FirebaseRecyclerOptions<Contact> options, Context context) {
        super(options);
        this.context = context;
    }


    //inflate layout into RV
    //returns viewHolder:
    @NonNull
    @Override
    public ChatContactsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.private_chat_contact_row, parent, false);

        ChatContactsHolder holder = new ChatContactsHolder(v);


        return holder;

    }

    //sets online status icon
    // sets last seen:
    //sets the online//offline icon, visbility compared to the online database state
    //of the user.
    //retrieve the date and time from the database of the last user login:
    @Override
    protected void onBindViewHolder(@NonNull ChatContactsHolder chatContactsHolder, int position, @NonNull Contact contact) {

       final String usersId = getRef(position).getKey();


        fbUsersRef.child(usersId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("image")) {
                        Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.profile_img).into(chatContactsHolder.civPrivateContactImg);
final String userImg = dataSnapshot.child("image").getValue().toString();

                        final String userName = dataSnapshot.child("name").getValue().toString();

                        //sets the online//offline icon, visbility compared to the online database state
                        //of the user.
                        if (dataSnapshot.child("userState").hasChild("state")) {
                            String userState = dataSnapshot.child("userState").child("state").getValue().toString();
                            if (userState.equals("online")) {
                                chatContactsHolder.ivOnline.setVisibility(View.VISIBLE);
                                chatContactsHolder.ivOffline.setVisibility(View.INVISIBLE);

                            } else {
                                chatContactsHolder.ivOnline.setVisibility(View.INVISIBLE);
                                chatContactsHolder.ivOffline.setVisibility(View.VISIBLE);
                            }

                            //retrieve the date and time from the database of the last user login:
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();

                            //print the date and time of the user login:
                            chatContactsHolder.tvPrivateContactLastSeen.setText(date + " | " + time);
                        } else {

                            chatContactsHolder.tvPrivateContactLastSeen.setText("");

                        }

                        chatContactsHolder.tvPrivateContactName.setText(userName);
                        chatContactsHolder.tvPrivateContactStatus.setText(dataSnapshot.child("status").getValue().toString());

                        chatContactsHolder.itemView.setOnClickListener(v -> {

                            Intent chatActivity = new Intent(context, ChatActivity.class);
                            chatActivity.putExtra("visited_user_id", usersId);
                            chatActivity.putExtra("visited_user_name", userName);
                            chatActivity.putExtra("visited_user_image", userImg);
                            context.startActivity(chatActivity);


                        });
                    }else{


                        final String userName = dataSnapshot.child("name").getValue().toString();

                        //sets the online//offline icon, visbility compared to the online database state
                        //of the user.
                        if (dataSnapshot.child("userState").hasChild("state")) {
                            String userState = dataSnapshot.child("userState").child("state").getValue().toString();
                            if (userState.equals("online")) {
                                chatContactsHolder.ivOnline.setVisibility(View.VISIBLE);
                                chatContactsHolder.ivOffline.setVisibility(View.INVISIBLE);

                            } else {
                                chatContactsHolder.ivOnline.setVisibility(View.INVISIBLE);
                                chatContactsHolder.ivOffline.setVisibility(View.VISIBLE);
                            }

                            //retrieve the date and time from the database of the last user login:
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();

                            //print the date and time of the user login:
                            chatContactsHolder.tvPrivateContactLastSeen.setText(date + " | " + time);
                        } else {

                            chatContactsHolder.tvPrivateContactLastSeen.setText("");

                        }

                        chatContactsHolder.tvPrivateContactName.setText(userName);
                        chatContactsHolder.tvPrivateContactStatus.setText(dataSnapshot.child("status").getValue().toString());

                        chatContactsHolder.itemView.setOnClickListener(v -> {

                            Intent chatActivity = new Intent(context, ChatActivity.class);
                            chatActivity.putExtra("visited_user_id", usersId);
                            chatActivity.putExtra("visited_user_name", userName);
                            chatActivity.putExtra("visited_user_image", "dd");
                            context.startActivity(chatActivity);


                        });



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    //inner class: init fields to use:
    //contact row fields:
    class ChatContactsHolder extends RecyclerView.ViewHolder {
        TextView tvPrivateContactName;
        TextView tvPrivateContactStatus;
        CircleImageView civPrivateContactImg;
        ImageView ivPrivateContactOnlineStatus;
        TextView tvPrivateContactLastSeen;
        ImageView ivOnline;
        ImageView ivOffline;

        public ChatContactsHolder(@NonNull View itemView) {
            super(itemView);


            tvPrivateContactName = itemView.findViewById(R.id.tvPrivateContactName);
            tvPrivateContactStatus = itemView.findViewById(R.id.tvPrivateContactStatus);
            civPrivateContactImg = itemView.findViewById(R.id.civPrivateContact);
            ivPrivateContactOnlineStatus = itemView.findViewById(R.id.ivPrivateContactOnline);
            tvPrivateContactLastSeen = itemView.findViewById(R.id.tvPrivateContactLastSeen);
            ivOffline = itemView.findViewById(R.id.ivOffline);
            ivOnline = itemView.findViewById(R.id.ivOnline);




        }
    }
}
