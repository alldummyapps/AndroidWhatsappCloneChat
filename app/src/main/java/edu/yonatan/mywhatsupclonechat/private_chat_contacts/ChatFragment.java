package edu.yonatan.mywhatsupclonechat.private_chat_contacts;


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


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    //props:
    private RecyclerView rvPrivateContacts;


    private DatabaseReference fbContactRef;
    private FirebaseAuth fbCurrentUser;
    private String currentUserID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init the fields:
        initField();

        currentUserID = fbCurrentUser.getInstance().getCurrentUser().getUid();
        fbContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>().
                        setQuery(fbContactRef,Contact.class).
                        build();


        ChatContactsAdapter adapter = new ChatContactsAdapter(options,getContext());
        rvPrivateContacts.setAdapter(adapter);
        adapter.startListening();


    }


    //init fields:
    private void initField() {

        rvPrivateContacts = getActivity().findViewById(R.id.rvPrivateContacts);
        rvPrivateContacts.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}
