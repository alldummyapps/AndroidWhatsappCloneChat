package edu.yonatan.mywhatsupclonechat.contact_chat;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.mywhatsupclonechat.R;


public class ChatActivity extends AppCompatActivity {

    //props:
    private String messageReceiverID, messageReceiverName, messageReceiverImg;
    private String currentUserID;
    private TextView tvCustomProfileName, tvCustomProfileLastSeen;
    private CircleImageView civCustomProfileImage;
    private Toolbar chatToolbar;

    private FloatingActionButton fabPrivateSendMessage;
    private EditText etPrivateChatBox;

    private DatabaseReference fbRootRef;

    private final List<PrivateMessage> messageList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager linearLayoutManager;

    private RecyclerView rvMessagesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //get the props from the ChatContactsAdapter class:
        //props are from the database Contacts compared to Users entry, if a contact id does exist
        //in the Users entry, his values are extracted to a var inside the ChatContactsAdapter and passed
        //with an intent to this class.
        messageReceiverID = getIntent().getStringExtra("visited_user_id");
        messageReceiverName = getIntent().getStringExtra("visited_user_name");
        messageReceiverImg = getIntent().getStringExtra("visited_user_image");

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fbRootRef = FirebaseDatabase.getInstance().getReference();


        //init and setup the toolbar,
        //adds custom toolbar layout with the user fields:
        //adds custom toolbar Support, adds back home screen arrow;
        toolBarInit();

        //init fields:
        initFields();


        //set the custom toolbar props with the user values:
        tvCustomProfileName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImg).placeholder(R.drawable.profile_img).into(civCustomProfileImage);


        //send msg BUTTON
        fabPrivateSendMessage.setOnClickListener((v) -> {


            sendMessage();


        });


//sets the messages adapter:
        messagesAdapter = new MessagesAdapter(this, messageList);
        linearLayoutManager = new LinearLayoutManager(this);
        rvMessagesList.setLayoutManager(linearLayoutManager);
        rvMessagesList.setAdapter(messagesAdapter);


    }

    //on activity start:
    //gets the messages from the 'Messages' entry in the database into a list,
    //than into the adapter:
    //listen to dataChange.

    @Override
    protected void onStart() {
        super.onStart();

        updateToolBarLastSeen();

        fbRootRef.child("Messages").child(currentUserID).child(messageReceiverID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                PrivateMessage messages = dataSnapshot.getValue(PrivateMessage.class);
                messageList.add(messages);
                messagesAdapter.notifyDataSetChanged();
                rvMessagesList.smoothScrollToPosition(rvMessagesList.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //updates the toolbar user last seen:
    //gets the last login of the user from the database;
    private void updateToolBarLastSeen() {

        fbRootRef.child("Users").child(messageReceiverID).child("userState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("time") && dataSnapshot.hasChild("date")) {
                    String time = dataSnapshot.child("time").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();

                    tvCustomProfileLastSeen.setText("Last Seen: " + date + " | " + time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //send msg onCLick:
    //enters the current date(real world date) to each message in the chat;
    private void sendMessage() {

        //enters the current date(real world date) to each message in the chat:
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String messageText = etPrivateChatBox.getText().toString();
        if (TextUtils.isEmpty(messageText)) {

        } else {

            messageText += "\n \n" + saveCurrentTime;
            //both receiver and sender refs inside Message entry in the database:
            String messageSenderRef = "Messages/" + currentUserID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + currentUserID;

            //generates random key:
            DatabaseReference userMessageKey = fbRootRef.child("Messages").child(currentUserID).child(messageReceiverID).push();

            // gets the random key into a String var:
            String messagePushId = userMessageKey.getKey();


            //creates a map to enter the data into the database:
            //message -> message contant
            //type: -> type of message
            //from -> the user who sent the message:
            Map messageDBTextBody = new HashMap();
            messageDBTextBody.put("message", messageText);
            messageDBTextBody.put("type", "text");
            messageDBTextBody.put("from", currentUserID);

            //creates another hash map, that puts the message textBody and the message details
            //into the message ref : 'Messages' ->currentUserId -> receiverUserID -> messageDBTextBody
            //into the message ref : 'Messages' ->receiverUserID -> currentUserId -> messageDBTextBody
            Map messageDBDetails = new HashMap();
            messageDBDetails.put(messageSenderRef + "/" + messagePushId, messageDBTextBody);
            messageDBDetails.put(messageReceiverRef + "/" + messagePushId, messageDBTextBody);

            //insert the hashmap into the DB, and updates the entry(not overwrite it)
            fbRootRef.updateChildren(messageDBDetails).addOnCompleteListener(task -> {

                if (!task.isSuccessful()) {

                    Toast.makeText(this, "Something went Wrong...", Toast.LENGTH_SHORT).show();

                }
                etPrivateChatBox.setText("");

            });


        }


    }

    //init fields:
    private void initFields() {


        chatToolbar = findViewById(R.id.tbChatToolBar);
        tvCustomProfileLastSeen = findViewById(R.id.tvCustomProfileLastSeen);
        tvCustomProfileName = findViewById(R.id.tvCustomProfileName);
        civCustomProfileImage = findViewById(R.id.civCustomProfileImage);
        fabPrivateSendMessage = findViewById(R.id.fabPrivateSendMessage);
        etPrivateChatBox = findViewById(R.id.etPrivateChatBox);
        rvMessagesList = findViewById(R.id.rvPrivateMessages);
    }

    //init and setup the toolbar,
    //adds custom toolbar layout with the user fields:
    //adds custom toolbar Support, adds back home screen arrow;
    private void toolBarInit() {

        chatToolbar = findViewById(R.id.tbChatToolBar);

        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();

        //sets the <- arrow to home screen button int he toolbar:
        assert actionBar != null;

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setHomeButtonEnabled(true);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View actionBarView = inflater.inflate(R.layout.custom_chat_bar, null, false);
        actionBar.setCustomView(actionBarView);
    }
}
