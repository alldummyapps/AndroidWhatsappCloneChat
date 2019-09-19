package edu.yonatan.mywhatsupclonechat.contact_requests;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class RequestAdapter extends FirebaseRecyclerAdapter<Contact, RequestAdapter.RequestHolder> {

    //props:
    private Context context;
    private DatabaseReference fbUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference fbContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
    private DatabaseReference fbRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");

    //ctor:
    public RequestAdapter(@NonNull FirebaseRecyclerOptions<Contact> options, Context context) {
        super(options);
        this.context = context;
    }

    //inflate layout into RV
    //returns viewHolder:
    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.contact_row, parent, false);
        RequestHolder holder = new RequestHolder(v);

        return holder;
    }


    //set both btns -> accept request and reject request visibility ON;
    //------
    //get the users id that are in the Chat Request -> current user ID entry inside the database;
    //check if the same user ids are inside the Users database entry, if so:
    //show the users details in the recyclerView
    //-------

    @Override
    protected void onBindViewHolder(@NonNull RequestHolder requestHolder, int position, @NonNull Contact contact) {

        requestHolder.btnAcceptRequest.setVisibility(View.VISIBLE);
        requestHolder.btnRejectRequest.setVisibility(View.VISIBLE);

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String list_user_id = getRef(position).getKey();
        DatabaseReference request_status = getRef(position).child("request_type").getRef();



        request_status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String type = dataSnapshot.getValue().toString();
                    if (type.equals("received")) {

                        fbUsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {

                                    String contactImg = dataSnapshot.child("image").getValue().toString();
                                    Picasso.get().load(contactImg).into(requestHolder.civContactImg);


                                }
                                if (dataSnapshot.hasChild("status") && dataSnapshot.hasChild("name")) {

                                    String contactStatus = dataSnapshot.child("status").getValue().toString();
                                    String contactName = dataSnapshot.child("name").getValue().toString();

                                    requestHolder.tvContactStatus.setText("Wants to connect with you.");
                                    requestHolder.tvContactName.setText(contactName);

                                }
                                String contactName = dataSnapshot.child("name").getValue().toString();


                                //on itemView click:
                                //
                                requestHolder.itemView.setOnClickListener(v -> {
                                    //alertDialog options:
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    "Accept",
                                                    "Cancel"

                                            };




                                    //alertBox setup:
                                    //database remove/add user per request:
                                    //see details on method;

                                    btnAlertBox(contactName, options, contactName, currentUser, list_user_id);


                                });
                                //btns setup: accept and decline friend request:
                                onClicksBtns(requestHolder, currentUser, list_user_id);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else if (type.equals("sent")) {

                        Button btnRequestSent = requestHolder.itemView.findViewById(R.id.btnAcceptRequest);
                        btnRequestSent.setText("Request Sent");
                        Button btnRequestDelete = requestHolder.itemView.findViewById(R.id.btnRejectRequest);
                        btnRequestDelete.setText("Remove Request");



                        fbUsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("image")) {

                                    String contactImg = dataSnapshot.child("image").getValue().toString();
                                    Picasso.get().load(contactImg).into(requestHolder.civContactImg);


                                }
                                if (dataSnapshot.hasChild("status") && dataSnapshot.hasChild("name")) {

                                    String contactStatus = dataSnapshot.child("status").getValue().toString();
                                    String contactName = dataSnapshot.child("name").getValue().toString();

                                    requestHolder.tvContactStatus.setText("You have sent request To:" + contactName);
                                    requestHolder.tvContactName.setText(contactName);

                                }
                                String contactName = dataSnapshot.child("name").getValue().toString();


                                //on itemView click:
                                //
                                requestHolder.itemView.setOnClickListener(v -> {
                                    //alertDialog options:
                                    CharSequence options[] = new CharSequence[]
                                            {

                                                    "Cancel Chat Reqest"

                                            };





                                    //alertBox setup:
                                    //database remove/add user per request:
                                    //see details on method;

                                    btnAlertBoxSent(contactName, options, contactName, currentUser, list_user_id);


                                });
                                requestHolder.btnRejectRequest.setOnClickListener(r->{
                                    fbRequestRef.child(currentUser).
                                            child(list_user_id).
                                            removeValue().addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            fbRequestRef.child(list_user_id).
                                                    child(currentUser).
                                                    removeValue().addOnCompleteListener(task3 -> {
                                                if (task3.isSuccessful()) {

                                                    Toast.makeText(context, "Contact Removed", Toast.LENGTH_SHORT).show();


                                                }
                                            });


                                        }
                                    });




                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void onClicksBtns(@NonNull RequestHolder requestHolder, String currentUser, String list_user_id) {
        requestHolder.btnAcceptRequest.setOnClickListener(k->{
            fbContactRef.child(currentUser).child(list_user_id).child("Contact").setValue("saved")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            fbContactRef.child(list_user_id).child(currentUser).child("Contact").setValue("saved")
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            fbRequestRef.child(currentUser).
                                                    child(list_user_id).
                                                    removeValue().addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    fbRequestRef.child(list_user_id).
                                                            child(currentUser).
                                                            removeValue().addOnCompleteListener(task3 -> {
                                                        if (task3.isSuccessful()) {

                                                            Toast.makeText(context, "Contact Saved", Toast.LENGTH_SHORT).show();


                                                        }
                                                    });


                                                }
                                            });

                                        }

                                    });

                        }

                    });



        });


        requestHolder.btnRejectRequest.setOnClickListener(r->{
fbRequestRef.child(currentUser).
child(list_user_id).
removeValue().addOnCompleteListener(task2 -> {
if (task2.isSuccessful()) {
fbRequestRef.child(list_user_id).
    child(currentUser).
    removeValue().addOnCompleteListener(task3 -> {
if (task3.isSuccessful()) {

    Toast.makeText(context, "Contact Removed", Toast.LENGTH_SHORT).show();


}
});


}
});




});
    }

    //alert box on each itemView:
    //create alertBox with the contact name;
    //------
    //if accepted btn -> save user to Contacts:
    //contact->currentUserID->OtherContactID->value=saved;
    //contact->otherCotanctID->CurrentuserID->value=saved;
    //if rejected btn -> remove currentUser entry from request entry in the database
    //remove other Contacnt entry from the request entry in the database

    private void btnAlertBox(String contactName, CharSequence[] options, String contactName2, String currentUser, String list_user_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(contactName2 + " Chat Request");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                fbContactRef.child(currentUser).child(list_user_id).child("Contact").setValue("saved")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                fbContactRef.child(list_user_id).child(currentUser).child("Contact").setValue("saved")
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                fbRequestRef.child(currentUser).
                                                        child(list_user_id).
                                                        removeValue().addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        fbRequestRef.child(list_user_id).
                                                                child(currentUser).
                                                                removeValue().addOnCompleteListener(task3 -> {
                                                            if (task3.isSuccessful()) {

                                                                Toast.makeText(context, "Contact Saved", Toast.LENGTH_SHORT).show();


                                                            }
                                                        });


                                                    }
                                                });

                                            }

                                        });

                            }

                        });
            }
            if (which == 1) {
                fbRequestRef.child(currentUser).
                        child(list_user_id).
                        removeValue().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        fbRequestRef.child(list_user_id).
                                child(currentUser).
                                removeValue().addOnCompleteListener(task3 -> {
                            if (task3.isSuccessful()) {

                                Toast.makeText(context, "Contact Removed", Toast.LENGTH_SHORT).show();


                            }
                        });


                    }
                });

            }

        });
        builder.show();
    }




    private void btnAlertBoxSent(String contactName, CharSequence[] options, String contactName2, String currentUser, String list_user_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Already Sent Request");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                fbRequestRef.child(currentUser).
                        child(list_user_id).
                        removeValue().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        fbRequestRef.child(list_user_id).
                                child(currentUser).
                                removeValue().addOnCompleteListener(task3 -> {
                            if (task3.isSuccessful()) {

                                Toast.makeText(context, "Request Removed Successfully", Toast.LENGTH_SHORT).show();


                            }
                        });


                    }
                });

            }

        });
        builder.show();
    }
    //recycler inner class:
    class RequestHolder extends RecyclerView.ViewHolder {

        TextView tvContactName;
        TextView tvContactStatus;
        CircleImageView civContactImg;
        ImageView ivContactOnlineStatus;
        Button btnAcceptRequest;
        Button btnRejectRequest;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);




            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactStatus = itemView.findViewById(R.id.tvContactStatus);
            civContactImg = itemView.findViewById(R.id.civContact);
            ivContactOnlineStatus = itemView.findViewById(R.id.ivContactOnline);
            btnAcceptRequest = itemView.findViewById(R.id.btnAcceptRequest);
            btnRejectRequest = itemView.findViewById(R.id.btnRejectRequest);



        }
    }
}
