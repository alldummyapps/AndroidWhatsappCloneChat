package edu.yonatan.mywhatsupclonechat.findcontact_profile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.yonatan.mywhatsupclonechat.Contact;
import edu.yonatan.mywhatsupclonechat.R;


public class FindContact extends AppCompatActivity {

    //props:
    private Toolbar tbFindContact;
    private RecyclerView rvFindContact;


    private DatabaseReference fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_contact);

        //data base ref to the users table:
        fbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //init fields:
        rvFindContact = findViewById(R.id.rvFindContact);
        tbFindContact = findViewById(R.id.tbFindContact);

        //inflate rv
        rvFindContact.setLayoutManager(new LinearLayoutManager(this));

        //set toolbar:
        setSupportActionBar(tbFindContact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Contacts");


    }

    //on activity start:
    @Override
    protected void onStart() {
        super.onStart();
        //init FB options
        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>().
                        setQuery(fbRef, Contact.class).
                        build();

        //start contact adapter:
        ContactAdapter adapter = new ContactAdapter(options, this);
        rvFindContact.setAdapter(adapter);
        adapter.startListening();

    }
}
