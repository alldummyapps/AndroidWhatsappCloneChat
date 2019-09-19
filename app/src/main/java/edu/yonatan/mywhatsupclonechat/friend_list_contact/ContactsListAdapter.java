package edu.yonatan.mywhatsupclonechat.friend_list_contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.mywhatsupclonechat.Contact;
import edu.yonatan.mywhatsupclonechat.R;


public class ContactsListAdapter extends FirebaseRecyclerAdapter<Contact, ContactsListAdapter.ContactsListHolder> {

    //props:
    private Context context;
    private DatabaseReference fbUsersRef;


    //ctor:
    public ContactsListAdapter(@NonNull FirebaseRecyclerOptions<Contact> options, Context context) {
        super(options);
        this.context = context;
    }


    //inflate layout into RV
    //returns viewHolder:
    @NonNull
    @Override
    public ContactsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        //inflate layout into RV
        //returns viewHolder:
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.contact_row, parent, false);

        ContactsListHolder holder = new ContactsListHolder(v);

        return holder;
    }


    //usersIDs gets all the users ID from the Contact child in the database
    //under contacts -> current user ->
    //all the user ids that accepted our friend request.
    //fbUsersRef -> ref to the users entry int he database;
    //takes all the users id - that we got from the contacts ids whom accepted our friends request
    //from the Users entry with all their details and show them in the RV
    @Override
    protected void onBindViewHolder(@NonNull ContactsListHolder contactsListHolder, int position, @NonNull Contact contact) {
        String userIDs = getRef(position).getKey();
        fbUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        fbUsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String contactName;
                String contactStatus;
                String contactImg;

                if (dataSnapshot.hasChild("image")) {
                    contactImg = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(contactImg).placeholder(R.drawable.profile_img).into(contactsListHolder.civContactImg);
                }

                contactName = dataSnapshot.child("name").getValue().toString();
                contactStatus = dataSnapshot.child("status").getValue().toString();

                contactsListHolder.tvContactName.setText(contactName);
                contactsListHolder.tvContactStatus.setText(contactStatus);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //recycler inner class:
    class ContactsListHolder extends RecyclerView.ViewHolder {

        TextView tvContactName;
        TextView tvContactStatus;
        CircleImageView civContactImg;
        ImageView ivContactOnlineStatus;

        public ContactsListHolder(@NonNull View itemView) {
            super(itemView);

            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactStatus = itemView.findViewById(R.id.tvContactStatus);
            civContactImg = itemView.findViewById(R.id.civContact);
            ivContactOnlineStatus = itemView.findViewById(R.id.ivContactOnline);
        }
    }
}
