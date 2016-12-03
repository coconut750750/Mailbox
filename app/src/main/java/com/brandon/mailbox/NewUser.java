package com.brandon.mailbox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class NewUser extends AppCompatActivity {

    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference imagesRef;


    public EditText first;
    public EditText last;
    public ImageView imageView;
    public Button takePic;
    public Button create;

    public static Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Registration");

        first = (EditText)findViewById(R.id.firstName);
        last = (EditText)findViewById(R.id.lastName);
        imageView = (ImageView)findViewById(R.id.image);
        takePic = (Button)findViewById(R.id.takePic);
        create = (Button)findViewById(R.id.createUser);

        user = FirebaseAuth.getInstance().getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://messenger-9a197.appspot.com");
        imagesRef = storageRef.child("profilepic/"+user.getUid());

        if (!Authentication.allowCamera){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, Authentication.MY_PERMISSIONS_REQUEST_CAMERA);

            //imageView.setVisibility(View.INVISIBLE);
            //takePic.setVisibility(View.INVISIBLE);
            //takePic.setEnabled(false);
        }
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
            }
        });

    }

    public void create(){

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(last.getWindowToken(), 0);

        String name = "";
        name += first.getText().toString()+" "+last.getText().toString();

        create.setEnabled(false);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(photoURI).build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(NewUser.this, Authentication.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                            finish();
                        }
                    }
                });
    }

    public void takePic(View view){
        create.setEnabled(false);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] d = baos.toByteArray();

            UploadTask uploadTask = imagesRef.putBytes(d);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    photoURI = taskSnapshot.getDownloadUrl();
                    create.setEnabled(true);
                }
            });
        }
    }
}
