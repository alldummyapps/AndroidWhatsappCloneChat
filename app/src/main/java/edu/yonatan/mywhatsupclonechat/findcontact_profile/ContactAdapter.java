package edu.yonatan.mywhatsupclonechat.findcontact_profile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.mywhatsupclonechat.Contact;
import edu.yonatan.mywhatsupclonechat.R;


//FireBase RecyclerView Adapter:
//requires a pattern class
public class ContactAdapter extends FirebaseRecyclerAdapter<Contact, ContactAdapter.ContactHolder> {

    //props:
    private Context context;
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference fbRef = FirebaseDatabase.getInstance().getReference();

    //ctor:
    public ContactAdapter(@NonNull FirebaseRecyclerOptions<Contact> options, Context context) {
        super(options);
        this.context = context;

    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //inflate layout into RV
        //returns viewHolder:
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.contact_row, parent, false);

        ContactHolder holder = new ContactHolder(v);

        return holder;
    }


    //binds the fields to the required values:
    @Override
    protected void onBindViewHolder(@NonNull ContactHolder contactHolder, int i, @NonNull Contact contact) {


        contactHolder.tvContactName.setText(contact.getName());
        contactHolder.tvContactStatus.setText(contact.getStatus());
        Picasso.get().load(contact.getImage()).placeholder(R.drawable.profile_img).into(contactHolder.civContactImg);
        if (currentUserId.equals(contact.getUid())) {
            contactHolder.tvContactStatus.setVisibility(View.GONE);
            contactHolder.tvContactName.setVisibility(View.GONE);
            contactHolder.civContactImg.setVisibility(View.GONE);
            contactHolder.ivContactOnlineStatus.setVisibility(View.GONE);
        }


    }

    //inner class: init fields to use:
    class ContactHolder extends RecyclerView.ViewHolder {

        TextView tvContactName;
        TextView tvContactStatus;
        CircleImageView civContactImg;
        ImageView ivContactOnlineStatus;


        public ContactHolder(@NonNull View itemView) {
            super(itemView);


            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvContactStatus = itemView.findViewById(R.id.tvContactStatus);
            civContactImg = itemView.findViewById(R.id.civContact);
            ivContactOnlineStatus = itemView.findViewById(R.id.ivContactOnline);


            itemView.setOnClickListener(v -> {

                String visitedUserId = getRef(getAdapterPosition()).getKey();
                Intent contactProfileActivity = new Intent(context, ContactProfileActivity.class);
                contactProfileActivity.putExtra("visitedUserID", visitedUserId);
                context.startActivity(contactProfileActivity);


            });


        }
    }
}
