package edu.yonatan.mywhatsupclonechat.group_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.yonatan.mywhatsupclonechat.R;



public class GroupsFragment extends Fragment {

    //props:
    private ListView lvGroupList;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> groupList = new ArrayList<>();


    private DatabaseReference fbRootRef;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //reference to the database:
        fbRootRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        //find views/properties initializer:
        initializedFindViews();

        //method used to receive groups from the database:
        groupManager();


        lvGroupList.setOnItemClickListener((parent, view1, position, id) -> {

            String currentGroupName = parent.getItemAtPosition(position).toString();

            Intent groupChat = new Intent(getContext(), GroupChatActivity.class);
            groupChat.putExtra("groupName", currentGroupName);
            startActivity(groupChat);


        });

    }

    //used to retrieve and display the groups from the database:
    private void groupManager() {
        fbRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                //set to prevent duplicate groups:
                Set<String> set = new HashSet<>();
                while (iterator.hasNext()) {
                    //adding the database groups into the set(set must have uniq values)
                    set.add(((DataSnapshot) iterator.next()).getKey());

                }
                //clear the arraylist:
                groupList.clear();
                //insert the set with the groups into the arraylist:
                groupList.addAll(set);
                //adapter notify listener:
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // initialize the properties:
    // setting the adapter and the listView
    private void initializedFindViews() {
        lvGroupList = getActivity().findViewById(R.id.lvGroupList);
        arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, groupList);
        lvGroupList.setAdapter(arrayAdapter);

    }
}
