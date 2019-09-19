package edu.yonatan.mywhatsupclonechat.login_register_activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import edu.yonatan.mywhatsupclonechat.MainActivity;
import edu.yonatan.mywhatsupclonechat.R;


public class RegisterActivity extends AppCompatActivity {

    //props:
    private EditText etRegisterEmail, etRegisterPassword;
    private Button btnRegister;
    private TextView tvAlreadyHaveAccount;

    private FirebaseAuth fbAuth;
    private DatabaseReference fbRootRef;

    private ProgressBar pbLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);

        //initialize findViews
        initializeFindViews();


        //initialize FireBase:
        fbAuth = FirebaseAuth.getInstance();
        fbRootRef = FirebaseDatabase.getInstance().getReference();


        pbLoading.setVisibility(View.GONE);


        //already have account register btn:
        tvAlreadyHaveAccount.setOnClickListener((v) -> {

            sendUserToLoginActivity();

        });

        //user register to fireBase database:
        btnRegister.setOnClickListener(v -> {

            createNewAccount();


        });


    }


    //creates new account in the database:
    //adds data token to the database from the user device:
    //sends the user to main activity on success;
    //adds dataToken, userName,password,status to database;
    private void createNewAccount() {
        //user pass and email:
        String userEmail = etRegisterEmail.getText().toString();
        String userPassword = etRegisterPassword.getText().toString();
        pbLoading.setVisibility(View.VISIBLE);

        //checks if pass/email are empty, if so returns a toast error:
        //if is not empty -> adds email and password to the database;
        if (TextUtils.isEmpty(userEmail)) {
            pbLoading.setVisibility(View.GONE);
            Toast.makeText(this, "Please Enter a Valid Email Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPassword)) {
            pbLoading.setVisibility(View.GONE);
            Toast.makeText(this, "Please Enter a Valid Password Address", Toast.LENGTH_SHORT).show();
        } else {

            fbAuth.createUserWithEmailAndPassword(userEmail, userPassword).
                    addOnCompleteListener((authResultTask) -> {
                        if (authResultTask.isSuccessful()) {

                            //adds data token to the database from the user device:
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(token -> {
                                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String deviceToken = token.getToken();
                                fbRootRef.child("Users").child(currentUserID).child("device_token").setValue(deviceToken);
                            });

                            String currentUserID = fbAuth.getCurrentUser().getUid();


                            fbRootRef.child("Users").child(currentUserID).setValue("");


                            Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            pbLoading.setVisibility(View.GONE);
                            sendUserToMainActivity();

                        } else {

                            pbLoading.setVisibility(View.GONE);
                            String errorMessage = authResultTask.getException().toString();
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    });
        }


    }

    //sends the user to main activity:
    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    //sends user to login activity btn
    private void sendUserToLoginActivity() {

        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);

    }

    //initialize findViews
    private void initializeFindViews() {

        btnRegister = findViewById(R.id.btnRegister);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        tvAlreadyHaveAccount = findViewById(R.id.tvAlreadyHaveAccount);
        pbLoading = findViewById(R.id.pbRegisterLoading);


    }


}
