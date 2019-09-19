package edu.yonatan.mywhatsupclonechat.friend_list_contact;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.yonatan.mywhatsupclonechat.Contact;
import edu.yonatan.mywhatsupclonechat.R;


public class ContactsFragment extends Fragment {


    //props:
    private RecyclerView rvContactsList;

    private DatabaseReference fbChatRequestRef;
    private String currentUserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fbChatRequestRef = FirebaseDatabase.getInstance().getReference().
                child("Contacts").
                child(currentUserID);


        //init fields:
        initFields();

    }

    //on fragment start:
    @Override
    public void onStart() {
        super.onStart();

        //init FB options
        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contact>().
                        setQuery(fbChatRequestRef, Contact.class)
                        .build();


        ContactsListAdapter adapter = new ContactsListAdapter(options, getContext());
        rvContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void initFields() {


        rvContactsList = getActivity().findViewById(R.id.rvContactsList);
        rvContactsList.setLayoutManager(new LinearLayoutManager(getContext()));


    }
}

