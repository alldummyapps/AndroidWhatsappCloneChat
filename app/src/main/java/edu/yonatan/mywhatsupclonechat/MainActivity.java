package edu.yonatan.mywhatsupclonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import edu.yonatan.mywhatsupclonechat.findcontact_profile.FindContact;
import edu.yonatan.mywhatsupclonechat.login_register_activities.LoginActivity;
import edu.yonatan.mywhatsupclonechat.settings.SettingsActivity;
import edu.yonatan.mywhatsupclonechat.tabs.TabsAccessorAdapter;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabsAccessorAdapter mTabsAccessorAdapter;
    private String currentUserID;

    private FirebaseUser currentUser;
    private FirebaseAuth fbAuth;
    private DatabaseReference fbRootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fbAuth = FirebaseAuth.getInstance();
        currentUser = fbAuth.getCurrentUser();
        fbRootRef = FirebaseDatabase.getInstance().getReference();



        //initialize the properties ->findviews
        findViews();


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("YonsUp");


        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);

        mTabLayout.setupWithViewPager(mViewPager);


    }

    // on the start of the app - checks if the user is loged in;
    // if the user is logged in -> continue;
    // else -> send user to login page;
    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {

            sendUserToLoginActivity();
        } else {
            currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            updateUserStatus("online");

            verifyUser();


        }


    }


    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null) {


            updateUserStatus("offline");

        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (currentUser != null) {


            updateUserStatus("offline");

        }

    }

    private void verifyUser() {

        String currentUserId = fbAuth.getCurrentUser().getUid();
        fbRootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())) {


                } else {


                    sendUserToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //sends the user to login activity:
    private void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        loginActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivity);
        finish();
    }


    //find views:
    private void findViews() {
        mToolbar = findViewById(R.id.main_page_toolbar);
        mViewPager = findViewById(R.id.vpTabsPager);
        mTabLayout = findViewById(R.id.tabLayout);
    }

    //main menu inflater:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    //menu items change
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.main_menu_find_contact:

                sendUserToFindContactActivity();

                return true;

            case R.id.main_menu_Settings:
                sendUserToSettingActivity();

                return true;

            case R.id.main_menu_Log_out:

                updateUserStatus("offline");
                fbAuth.signOut();
                currentUser = null;
                sendUserToLoginActivity();
                return true;
            case R.id.main_menu_new_group:

                createNewGroup();

        }

        return true;
    }

    //sends the user to the find contact activity:
    private void sendUserToFindContactActivity() {
        Intent findContactActivity = new Intent(this, FindContact.class);
        startActivity(findContactActivity);
    }


    //creating a group chat: Alert Dialog
    //group button -> alertdialog -> group name:
    //uses a 'style' from styles.xml called AlertDialog
    private void createNewGroup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");
        final EditText groupNameField = new EditText(this);
        groupNameField.setHint("My Group...");

        builder.setView(groupNameField);
        //create group button:


        builder.setPositiveButton("Create", (dialog, which) -> {
            String groupName = groupNameField.getText().toString();
            if (TextUtils.isEmpty(groupName)) {
                Toast.makeText(MainActivity.this, "Please Enter a Valid Group Name.", Toast.LENGTH_SHORT).show();
            } else {

                createDBGroup(groupName);

            }

        });
        // cancel button:
        builder.setNegativeButton("Cancel", (dialog, which) -> {

            dialog.cancel();

        });


        builder.show();
    }


    //registers the group into the database;
    private void createDBGroup(String groupName) {
        fbRootRef.child("Groups").child(groupName).setValue("").
                addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(this, groupName + " Group Created Successfully.", Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();

                    }


                });


    }


    //sends the user to the setting activity:
    private void sendUserToSettingActivity() {
        Intent settingActivity = new Intent(this, SettingsActivity.class);
        settingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingActivity);
        finish();
    }

    //update the user status into the database:
    //adds time and date of login:
    //Users -> userID -> userState -> (date, time, state)
    private void updateUserStatus(String state) {

        String saveCurrentTime, saveCurrentDate;

        //time and date formatter:
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM/dd/yy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());

        //hashmap that holds the  time & date and state of the user:
        HashMap<String, Object> onlineUserState = new HashMap<>();
        onlineUserState.put("time", saveCurrentTime);
        onlineUserState.put("date", saveCurrentDate);
        onlineUserState.put("state", state);


        //adds the hashmap into the database
      fbRootRef.child("Users").child(currentUserID).child("userState").updateChildren(onlineUserState);


    }
}
