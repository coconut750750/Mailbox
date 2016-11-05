package com.brandon.mailbox;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Registration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public EditText username;
    public EditText password1;
    public EditText password2;
    public Button button;
    public TextView message;

    public static String name;
    public static String pass1;
    public static String pass2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Registration");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(Registration.this, NewUser.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                    finish();
                }
                // ...
            }
        };

        username = (EditText)findViewById(R.id.username);
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        password1 = (EditText)findViewById(R.id.password);
        password2 = (EditText)findViewById(R.id.password2);
        button = (Button) findViewById(R.id.signin);
        message = (TextView)findViewById(R.id.message);

    }

    @Override
    protected void onResume() {
        super.onResume();
        button.setEnabled(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void login(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password2.getWindowToken(), 0);

        name = username.getText().toString();
        pass1 = password1.getText().toString();
        pass2 = password2.getText().toString();
        if (name.isEmpty() || pass1.isEmpty()|| pass2.isEmpty()){
            return;
        }

        if (!pass2.equals(pass1)){
            message.setText("Passwords do not match.");
            return;
        }
        else{
            message.setText("");
        }

        if(pass1.length() < 6){
            message.setText("Password must be at least 6 characters.");
            return;
        }

        button.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(name, pass1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            message.setText("Please enter a valid email.");
                            button.setEnabled(true);
                        }

                        // ...
                    }
                });

        FileOutputStream outputStream;

        try {
            String string = name+"\n"+pass1;

            outputStream = openFileOutput(Authentication.filename, this.MODE_PRIVATE);
            outputStream.write(string.getBytes());

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
