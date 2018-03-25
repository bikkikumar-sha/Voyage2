package com.fallout.android.voyage2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {
    final String TAG = "firebaseAuth";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUpButton;
    private int REQUEST_CODE = 101;
    private ProgressBar progressBar1;
    private int storyNo;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //check if user is starting the app for the first time
        final String PREFS_NAME = "MyPref";
        final SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        Log.d("Comments", " "+ prefs.getString("my_first", "no"));

        if (Objects.equals(prefs.getString("my_first", "yes"), "yes")) {
            //the app is being launched for first time, do something
            // record the fact that the app has been started at least once
            editor.putString("my_first", "no").apply();
            // first time task--- show tutorial to the user
            Intent k = new Intent(getApplicationContext(), TutoActivity.class);
            startActivity(k);
            finish();
        }


        progressDialog = new ProgressDialog(this);
        //Declare an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //update ui now
                    updateUI(firebaseAuth.getCurrentUser());
                }
            }
        };
        //Obtaining xml references
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);
        signUpButton = findViewById(R.id.signup_text_view);

        //set onclick listeners which then calls signin() method to log in existing users.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }

        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });


    }

    //Receving data from signup activity and Creating new User....
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Retrieve data in the intent
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    String email = extras.getString("iEmail");
                    String password = extras.getString("iPassword");
                    String name = extras.getString("iName");
                    Toast.makeText(LoginActivity.this, email, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, email + password);
                    createNewUser(email, password, name);

                }
            }
        } catch (Exception ex) {
            Toast.makeText(LoginActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }


//    //method to sign in users calls "signInWithEmailAndPassword" (For Existing Users)

    private void signIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //validating email and passwords first
        if (email.isEmpty()) {     //if either email or password empty
            emailEditText.setError("Email Address is required!");
            emailEditText.requestFocus();
        } else if (email.isEmpty()) {     //if either email or password empty
            emailEditText.setError("Password is required!");
            emailEditText.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a Valid Email Address !");
            emailEditText.requestFocus();
        } else if (password.length() < 6) {
            passwordEditText.setError("Minimum length of password should be 6!");
            passwordEditText.requestFocus();
        }
        //If valid credentials Try SignUp...
        else {
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Authenticating");
            progressDialog.setMessage("please wait...");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
//                                progressBar1.setVisibility(View.GONE);                            // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(LoginActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                progressDialog.dismiss();
                            }

                            // ...
                        }
                    });
        }
//        progressBar1.setVisibility(View.GONE);
    }


    //Create new user
    void createNewUser(String email, String password, final String name) {
        progressDialog.setTitle("Creating User");
        progressDialog.setMessage("pleaseWait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Registration Successful!",
                                    Toast.LENGTH_SHORT).show();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();
                            try {
                                user.updateProfile(profileUpdates);
                            } catch (Exception e) {
                            }
                            progressDialog.dismiss();

                            //updateUI(user);
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //updateUI(currentUser);
        mAuth.addAuthStateListener(authStateListener);
    }

    public void updateUI(FirebaseUser user) {
        if (user != null) {       //logging in user if already logged in automatically on app start
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

        }
    }

    @Override
    public void onBackPressed() {
        //disable back button from loginn screen
        //super.onBackPressed();
    }
}
