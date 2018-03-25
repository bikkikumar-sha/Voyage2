package com.fallout.android.voyage2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;


public class SignupActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confPassEditText;
    private EditText nameEditText;
    private EditText regnoEditText;
    private TextView logInTextView;
    private Button signUpButton;
    private String email;
    private String password;
    private String name;
//    private String regno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //getting XML references
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confPassEditText = findViewById(R.id.conf_pass_edit_text);
        nameEditText = findViewById(R.id.name_edit_text);
        //todo implemet register number if needed
//        regnoEditText = findViewById(R.id.regno_edit_text);
        logInTextView = findViewById(R.id.login_text_view);
        signUpButton = findViewById(R.id.signup_button);
        //onclick listeners
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //return data to main activity
                if(validateUserData()) {
                    //Sending user data via intent for sign up !!
                    Intent intent = getIntent();
                    intent.putExtra("iEmail", email);
                    intent.putExtra("iPassword", password);
                    intent.putExtra("iName",name);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    //verifying validity of data entered by the user
    public Boolean validateUserData(){
        Boolean isValid = false;
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        name = nameEditText.getText().toString();
//        regno = regnoEditText.getText().toString();
        String confPassword = confPassEditText.getText().toString();

        //validating email and passwords first
        if(email.isEmpty()) {     //if either email or password empty
            emailEditText.setError("Email Address is required!");
            emailEditText.requestFocus();
        }
        else if(email.isEmpty()) {     //if either email or password empty
            emailEditText.setError("Password is required!");
            emailEditText.requestFocus();
        }
        else if(name.length() < 5) {     //if either email or password empty
            nameEditText.setError("Name too short!");
            nameEditText.requestFocus();
        }
//        else if(regno.length() < 5) {     //if either email or password empty
//            regnoEditText.setError("Invalid Register Number");
//            regnoEditText.requestFocus();
//        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a Valid Email Address !");
            emailEditText.requestFocus();
        }
        else if(password.length() < 6){
            passwordEditText.setError("Minimum length of password should be 6!");
            passwordEditText.requestFocus();
        }
        else if(!Objects.equals(password, confPassword)){
            confPassEditText.setError("Passwords are different!");
            confPassEditText.requestFocus();
        }
        else {
            isValid = true;
        }
        return isValid;
    }
}
