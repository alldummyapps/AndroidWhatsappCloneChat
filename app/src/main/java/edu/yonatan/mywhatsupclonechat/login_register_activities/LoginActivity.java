package edu.yonatan.mywhatsupclonechat.login_register_activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import edu.yonatan.mywhatsupclonechat.MainActivity;
import edu.yonatan.mywhatsupclonechat.R;


public class LoginActivity extends AppCompatActivity {


    //props:
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin, btnPhoneLogin;
    private EditText etUserEmail, etUserPassword;
    private TextView tvNewAccount, tvForgotPassword;
    private ProgressBar pbLoadingLogin;
    private Button btnGmailLogin;

    private FirebaseAuth fbAuth;
    private FirebaseAuth mAuth;
    private DatabaseReference fbUsersRef;



    public static final int RC_SIGN_IN = 3;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        //initialize findviews properties:
        initializeFindViews();

        mAuth = FirebaseAuth.getInstance();
        fbAuth = FirebaseAuth.getInstance();
        fbUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        //sends the user to register activity:
        tvNewAccount.setOnClickListener(v -> {
            sendUserToRegisterActivity();
        });

        //logs the play into the app:
        btnLogin.setOnClickListener(v -> {
            //checks user Credentials and send him to the main activity if true:
            userLoginCheck();

        });

        pbLoadingLogin.setVisibility(View.GONE);


//sends the user to register via his phone number:
        btnPhoneLogin.setOnClickListener(v -> {
            Intent phoneLoginActivity = new Intent(this, UserPhoneRegisterActivity.class);
            startActivity(phoneLoginActivity);
        });



        googleSignin();





        btnGmailLogin.setOnClickListener(v -> {

signIn();


        });


    }

    //checks login credentials -> if true to the databse entry -> log in user:
    //sends user to main activity;
    //if credentials wrong -> error msg;
    //adds data token to the database from the user device:
    private void userLoginCheck() {
        //user pass and email:
        String userEmail = etUserEmail.getText().toString();
        String userPassword = etUserPassword.getText().toString();

        pbLoadingLogin.setVisibility(View.VISIBLE);
        //checks if pass/email are empty, if so returns a toast error:
        if (TextUtils.isEmpty(userEmail)) {
            pbLoadingLogin.setVisibility(View.GONE);
            Toast.makeText(this, "Please Enter a Valid Email Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPassword)) {
            pbLoadingLogin.setVisibility(View.GONE);
            Toast.makeText(this, "Please Enter a Valid Password Address", Toast.LENGTH_SHORT).show();
        } else {
            pbLoadingLogin.setVisibility(View.GONE);
            fbAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener((task) -> {

                if (task.isSuccessful()) {

                    //adds data token to the database from the user device:
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(token -> {
                        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String deviceToken = token.getToken();

                        fbUsersRef.child(currentUserID).child("device_token").setValue(deviceToken).addOnCompleteListener(task2 -> {

                            pbLoadingLogin.setVisibility(View.GONE);
                            sendUserToMainActivity();
                            Toast.makeText(this, "Logging Successfully", Toast.LENGTH_SHORT).show();

                        });


                    });


                } else {

                    Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show();

                }

            });

        }
    }

    //findviews initilizer:
    private void initializeFindViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnPhoneLogin = findViewById(R.id.btnPhoneLogin);
        etUserEmail = findViewById(R.id.etEmail);
        etUserPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvNewAccount = findViewById(R.id.tvCreateAccount);
        pbLoadingLogin = findViewById(R.id.pbLoadingLogin);
        btnGmailLogin = findViewById(R.id.btnGmailLogin);

    }


    //sends the user to main activity:
    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    //sends the user to the register activity:
    private void sendUserToRegisterActivity() {

        Intent registerActivity = new Intent(this, RegisterActivity.class);
        startActivity(registerActivity);

    }











    //google sign in init -> options and client:
    private void googleSignin() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    //google sign in method: gets the gmail from the phone
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);


            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Temporary Unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        sendUserToMainActivity();
                        // updateUI(user);
                    } else {

                        // updateUI(null);
                    }

                });
    }

}
