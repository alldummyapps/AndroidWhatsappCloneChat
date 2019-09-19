package edu.yonatan.mywhatsupclonechat.contact_requests;


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
public class RequestFragment extends Fragment {

    //props:
    private RecyclerView rvRequestsList;
    private DatabaseReference fbRequestRef;
    private String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_status, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fbRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");

        //init fields:
        initField();


    }

    //on fragment start:
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>().
                setQuery(fbRequestRef.child(currentUserID), Contact.class).
                build();

        //set adapter:
        RequestAdapter adapter = new RequestAdapter(options, getContext());
        rvRequestsList.setAdapter(adapter);
        adapter.startListening();

    }

    //init fields
    private void initField() {
        rvRequestsList = getActivity().findViewById(R.id.rvRequestsList);
        rvRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}
