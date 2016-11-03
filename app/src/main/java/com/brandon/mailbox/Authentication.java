package com.brandon.mailbox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Authentication extends AppCompatActivity {

    public final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    public static boolean allowCamera = false;

    public static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static DatabaseReference mRootRef;

    DatabaseReference mConnected;
    ValueEventListener connectListener;

    public static EditText username;
    public static EditText password;

    public static Button login;
    public static Button register;
    public static ArrayList<Button> buttons;

    public static File file;
    public static String filename;

    public static String name;
    public static String pass;

    public static boolean loggedIn;
    public static boolean registering;

    public static ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Mailbox");

        loggedIn = false;

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(Authentication.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    toggleButtons(false, buttons);
                    progressDialog.show();
                    DatabaseReference nameRef = mRootRef.child(MainActivity.FirebaseUserList).child(user.getUid()).child("Name");
                    nameRef.setValue(user.getDisplayName());

                    loggedIn = true;
                    progressDialog.setMessage("Signing in...");
                } else {
                    // User is signed out
                }
                // ...
            }
        };

        mRootRef = FirebaseDatabase.getInstance().getReference();

        connectListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                final boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            while (!loggedIn || registering){
                            }
                            progressDialog.dismiss();
                            Intent intent = new Intent(Authentication.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            startActivity(intent);
                            overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                        }
                    }).start();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mConnected  = mRootRef.child(".info/connected");
        mConnected.addValueEventListener(connectListener);

        username = (EditText) findViewById(R.id.username);
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        password = (EditText)findViewById(R.id.password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        name = "";
        pass = "";

        login = (Button)findViewById(R.id.signin);
        register = (Button)findViewById(R.id.registration);
        register.setPaintFlags(register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        buttons = new ArrayList<Button>();
        buttons.add(login);
        buttons.add(register);


        filename = "data";

        try {
            file = new File(this.getFilesDir(), filename);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FileInputStream fis;

        try {
            fis = this.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            int index = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if(index == 0){
                    name = line;
                }
                else if (index == 1){
                    pass = line;
                }
                index++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!name.equals("") && !pass.equals("")){
            username.setText(name, TextView.BufferType.EDITABLE);
            username.setSelection(username.getText().toString().length());
            password.setText(pass, TextView.BufferType.EDITABLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleButtons(true, buttons);

        registering = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void login(View view){
        progressDialog.show();
        progressDialog.setMessage("Authenticating...");

        //hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

        name = username.getText().toString();
        pass = password.getText().toString();
        if (name.isEmpty() || pass.isEmpty()){
            return;
        }
        toggleButtons(false, buttons);

        firebaseLogin(name, pass);

        FileOutputStream outputStream;

        try {
            String string = name+"\n"+pass;

            outputStream = openFileOutput(filename, this.MODE_PRIVATE);
            outputStream.write(string.getBytes());

            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void firebaseLogin(String name, String pass){
        mAuth.signInWithEmailAndPassword(name, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(Authentication.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            toggleButtons(true, buttons);
                            username.setText("");
                            password.setText("");
                        }
                    }
                });

    }

    public void register(View view){
        registering = true;
        Intent intent = new Intent(Authentication.this, Registration.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    public static void toggleButtons(boolean isOn, ArrayList<Button> buttons){
        for(Button b : buttons){
            b.setEnabled(isOn);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    allowCamera = true;
                    break;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
    }
}
