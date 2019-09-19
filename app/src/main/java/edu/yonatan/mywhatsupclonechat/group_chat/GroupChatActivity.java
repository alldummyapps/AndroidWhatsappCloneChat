package edu.yonatan.mywhatsupclonechat.group_chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.yonatan.mywhatsupclonechat.R;


public class GroupChatActivity extends AppCompatActivity {
    private List<ChatMessage> messagesList = new ArrayList<>();


    //properties:
    private Toolbar toolbar;
    private FloatingActionButton ibtnGroupSend;
    private EditText etGroupChatBox;

    private RecyclerView rvGroupChat;


    private String currentGroupName, currentUserID, currentUserName, currentMessageDate, currentMessageTime;

    private FirebaseAuth fbAuth;
    private DatabaseReference fbUserRef, fbGroupNameRef, fbGroupKeyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //init props:
        initializeViews();

        currentGroupName = getIntent().getStringExtra("groupName");


        //sets the inside group toolbar to the group name:
        setCurrentGroupNameToolBar(currentGroupName);

        //database init:
        fbAuth = FirebaseAuth.getInstance();
        currentUserID = fbAuth.getCurrentUser().getUid();
        //creates a ref to the database child -> Users;
        fbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //ref to Group:  -> gets the specific group name the user has:
        fbGroupNameRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        //retrieves some of the user info into properties so it can be pass on:
        retrieveUserInfo();


        //group send button:
        ibtnGroupSend.setOnClickListener(v -> {

            saveMessageToDatabase();

            //clears the edittext message box:
            etGroupChatBox.setText("");

        });

    }

    //sets the current group name to the current group toolbar:
    private void setCurrentGroupNameToolBar(String currentGroupName) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupName);
    }


    @Override
    protected void onStart() {
        super.onStart();
        fbGroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //if the group exists:
                if (dataSnapshot.exists()) {

                    displayMessages(dataSnapshot);

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {

                    displayMessages(dataSnapshot);

                }

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

    private void displayMessages(DataSnapshot dataSnapshot) {

        //extract the messages out of the database into a list:
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()) {
            String messageDate = (String) ((DataSnapshot) iterator.next()).getValue();
            String messageContent = (String) ((DataSnapshot) iterator.next()).getValue();
            String messageName = (String) ((DataSnapshot) iterator.next()).getValue();
            String messageTime = (String) ((DataSnapshot) iterator.next()).getValue();

            messagesList.add(new ChatMessage(messageName, messageContent, messageTime));


        }
        //inflates and run the rvAdapter:
        //insert the list:
        GroupChatAdapter adapter = new GroupChatAdapter(messagesList, this);
        rvGroupChat.setLayoutManager(new LinearLayoutManager(this));
        rvGroupChat.setAdapter(adapter);
        rvGroupChat.scrollToPosition(messagesList.size() - 1);
    }


    //initialize properties:
    private void initializeViews() {

        toolbar = findViewById(R.id.group_chat_bar);


        ibtnGroupSend = findViewById(R.id.ibtnGroupSendMessage);
        etGroupChatBox = findViewById(R.id.etGroupChatBox);
        rvGroupChat = findViewById(R.id.rvMessagesGroup);


    }

    //retrieve the current user info;
    //checks if the current user exists -> if so save his name into a String prop:
    private void retrieveUserInfo() {
        fbUserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //saves the sent message into the database:
    //saves the time and the date via libraries:
    private void saveMessageToDatabase() {

        // the editext message -> user message:
        String userMessage = etGroupChatBox.getText().toString();

        //adds a key to each group name:
        String messageKey = fbGroupNameRef.push().getKey();

        if (TextUtils.isEmpty(userMessage)) {


        } else {
            //gets current date + formatted:
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentMessageDate = simpleDateFormat.format(calendarDate.getTime());
            //gets current time + formatted:
            Calendar calendarTime = Calendar.getInstance();
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm");
            currentMessageTime = simpleTimeFormat.format(calendarTime.getTime());


            //  HashMap<String, Object> groupMessageKey = new HashMap<>();
            //ref to the user group name ->
            //adds a groupMessageKey
            //  fbGroupNameRef.updateChildren(groupMessageKey);

            //ref to the group->groupName -> groupNameKey:
            fbGroupKeyRef = fbGroupNameRef.child(messageKey);

            //the total message details:
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", userMessage);
            messageInfoMap.put("date", currentMessageDate);
            messageInfoMap.put("time", currentMessageTime);

            //updates all the children of the key->
            fbGroupKeyRef.updateChildren(messageInfoMap);

        }
    }
}
