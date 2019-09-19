package edu.yonatan.mywhatsupclonechat.findcontact_profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.mywhatsupclonechat.R;


public class ContactProfileActivity extends AppCompatActivity {


    //db request type -> "sent", "received"
    //class current_state --> NEW, SENT, REQUEST_RECEIVED,FRIENDS


    //props:
    private String currentUserId, visitedUserID, current_request_state;
    private CircleImageView civVisitedProfileImg;
    private TextView tvVisitedUserName, tvVisitedUserStatus;
    private Button btnVisitedSendMsg, btnDeclineRequest;

    //fb props:
    private DatabaseReference fbUserRef, fbChatRequestRef, fbContactsRef, notificationRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //FireBase init fields:
        fireBaseInitFields();
        //init fields:
        initFields();


    }


    @Override
    protected void onStart() {
        super.onStart();
        //gets the visited user info and place it into the fields:
        current_request_state = "NEW";
        retrieveVisitedUserInfo();


    }

    //gets the visited user info and place it into the XML fields:
    private void retrieveVisitedUserInfo() {
        fbUserRef.child(visitedUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tvVisitedUserName.setText(dataSnapshot.child("name").getValue().toString());
                tvVisitedUserStatus.setText(dataSnapshot.child("status").getValue().toString());
                if (dataSnapshot.hasChild("image")) {
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(civVisitedProfileImg);
                }

                manageChatRequest();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //db request type -> "sent", "received"
    //class current_state --> NEW, SENT, REQUEST_RECEIVED, FRIENDS
    //-------
    //on click -> btnSend
    //-------
    //changes the user sent/received status in the database while sending/canceling request:
    //adds a users->requests database field:
    //adds requests -> current user -> visited user ->status
    //adda requests -> visited user -> current user ->status
    //methods in use:
    //sendChatRequest(); , cancelChatRequest();, acceptChatRequest();, removeSpecificFriend();
    private void manageChatRequest() {

        fbChatRequestRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(visitedUserID)) {
                    //get the current value of the request
                    //if it equals to sent -> request sent, change the status of the
                    // current request sent inner class field to the same name
                    String request_type = dataSnapshot.child(visitedUserID).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {

                        current_request_state = "SENT";
                        btnVisitedSendMsg.setText("Cancel Request");


                    } else if (request_type.equals("received")) {

                        current_request_state = "REQUEST_RECEIVED";
                        btnVisitedSendMsg.setText("Accept Chat Request");
                        btnDeclineRequest.setVisibility(View.VISIBLE);
                        btnDeclineRequest.setEnabled(true);

                        btnDeclineRequest.setOnClickListener(v -> {

                            cancelChatRequest();

                        });

                    }


                    //if chat requests entry int he database has no value (current user id -> receiver user id
                    // change the send request btn text to 'remove contact' text;
                    // change the current request state to "FRIENDS"
                } else {
                    fbContactsRef.child(currentUserId).
                            addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(visitedUserID)) {

                                        current_request_state = "FRIENDS";
                                        btnVisitedSendMsg.setText("Remove Contact");

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!currentUserId.equals(visitedUserID)) {
            btnVisitedSendMsg.setOnClickListener(v -> {

                btnVisitedSendMsg.setEnabled(false);

                //if current request state is NEW, allow to send a chat req:
                if (current_request_state.equals("NEW")) {
                    //adds the user chat request to the database:
                    sendChatRequest();
                }
                // if current request status is SENT, cancel the chat req:
                if (current_request_state.equals("SENT")) {
                    // removes the database chat request value
                    cancelChatRequest();
                }
                if (current_request_state.equals("REQUEST_RECEIVED")) {

                    acceptChatRequest();
                }
                if (current_request_state.equals("FRIENDS")) {

                    removeSpecificFriend();
                }

            });
        }

    }

    //activity default value:
    //removes the 'contacts' entry of each user(current and receiver
    //changes current request state to default value
    //changes the btn text to default value;
    private void removeSpecificFriend() {
        fbContactsRef.child(currentUserId).child(visitedUserID).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fbContactsRef.child(visitedUserID).child(currentUserId).removeValue().addOnCompleteListener(taskTwo -> {
                    if (taskTwo.isSuccessful()) {

                        btnVisitedSendMsg.setEnabled(true);
                        current_request_state = "NEW";
                        btnVisitedSendMsg.setText("Send Chat Request");

                        btnDeclineRequest.setVisibility(View.GONE);
                        btnDeclineRequest.setEnabled(false);


                    }
                });

            }
        });


    }


    //fbcontactsref = "contacts" db entry:
    //creates anew entry in the data base : 'contacts' -> current user id -> reciever user id -> 'contacts_list'
    //creates anew entry in the data base : 'contacts' -> receiver user id -> current user id -> 'contacts_list'
    // in the contacts_ist entry adds value saved;
    //--------
    // removes the request_status entry of both current user id and receiver user id from the database:
    //--------
    //changes the btnVisitedSengMsg text to -> remove friend
    //btn decline is set to visibility gone, and enable false
    //current_request_state is changed to 'FRIENDS'
    private void acceptChatRequest() {
        fbContactsRef.child(currentUserId).
                child(visitedUserID).
                child("contacts_list").
                setValue("saved").
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fbContactsRef.child(visitedUserID).
                                child(currentUserId).
                                child("contacts_list").
                                setValue("saved").
                                addOnCompleteListener(taskTwo -> {
                                    if (taskTwo.isSuccessful()) {
                                        fbChatRequestRef.child(currentUserId).
                                                child(visitedUserID).
                                                removeValue().addOnCompleteListener(taskThree -> {
                                            if (taskThree.isSuccessful()) {
                                                fbChatRequestRef.child(visitedUserID).
                                                        child(currentUserId).
                                                        removeValue().addOnCompleteListener(taskFour -> {
                                                    if (taskFour.isSuccessful()) {

                                                        btnVisitedSendMsg.setEnabled(true);
                                                        current_request_state = "FRIENDS";
                                                        btnVisitedSendMsg.setText("Remove Friend");
                                                        btnDeclineRequest.setVisibility(View.GONE);
                                                        btnDeclineRequest.setEnabled(false);


                                                    }

                                                });


                                            }

                                        });

                                    }
                                });
                    }
                });


    }

    //removes the requests- > currentUser/visitedUser;
    // change the current request state to NEW (send new request)
    private void cancelChatRequest() {
        fbChatRequestRef.child(currentUserId).child(visitedUserID).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fbChatRequestRef.child(visitedUserID).child(currentUserId).removeValue().addOnCompleteListener(taskTwo -> {
                    if (taskTwo.isSuccessful()) {

                        btnVisitedSendMsg.setEnabled(true);
                        current_request_state = "NEW";
                        btnVisitedSendMsg.setText("Send Chat Request");

                        btnDeclineRequest.setVisibility(View.GONE);
                        btnDeclineRequest.setEnabled(false);


                    }
                });

            }
        });

    }

    //adds two fields into Users->requests ->
    // of of the current user and the other of the receiver User:
    // both fields has children fields with the status of the request;
    // if task is successful -> current_request_state is status SENT
    private void sendChatRequest() {

        //current user field:
        fbChatRequestRef.child(currentUserId).child(visitedUserID).
                child("request_type").
                setValue("sent").
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        //receiver user field:
                        fbChatRequestRef.child(visitedUserID).child(currentUserId).
                                child("request_type").
                                setValue("received").addOnCompleteListener(taskTwo -> {

                            //if task is successful -> change the btn text to to 'cancel request'
                            if (taskTwo.isSuccessful()) {

                                HashMap<String,String> chatNotification = new HashMap<>();
                                chatNotification.put("from", currentUserId);
                                chatNotification.put("type", "request");

                                notificationRef.child(visitedUserID).push().setValue(chatNotification).addOnCompleteListener(task1 -> {

                                    if(task1.isSuccessful()){

                                        btnVisitedSendMsg.setEnabled(true);
                                        current_request_state = "SENT";
                                        btnVisitedSendMsg.setText("Cancel Request");

                                    }
                                });




                            }


                        });

                    }


                });

    }

    //init the fields:
    private void initFields() {
        civVisitedProfileImg = findViewById(R.id.civProfileVisitedContact);
        tvVisitedUserName = findViewById(R.id.tvVisitedUserName);
        tvVisitedUserStatus = findViewById(R.id.tvVisitedUserStatus);
        btnVisitedSendMsg = findViewById(R.id.btnSendVisitedContactMsg);
        btnDeclineRequest = findViewById(R.id.btnDeclineRequest);

    }

    //init firebase fields:
    private void fireBaseInitFields() {
        fbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        visitedUserID = getIntent().getStringExtra("visitedUserID");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fbUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        fbChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        fbContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
    }
}
