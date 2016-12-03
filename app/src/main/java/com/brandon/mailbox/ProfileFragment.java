package com.brandon.mailbox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class ProfileFragment extends Fragment {
    //public Button logout;
    public ImageView imageView;
    public TextView name;
    public Button takePic;

    public static Uri photoURI;
    StorageReference imagesRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_profile, container, false);

        takePic = (Button)view.findViewById(R.id.takePic);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        imageView = (ImageView)view.findViewById(R.id.image);
        name = (TextView)view.findViewById(R.id.profileName);

        name.setText(MainActivity.name);

        ProfilePicTask profilePicTask = new ProfilePicTask(imageView);
        profilePicTask.execute();

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://messenger-9a197.appspot.com");
        imagesRef = storageRef.child("profilepic/"+MainActivity.uid);

        return view;
    }

    public void takePicture(){
        if (!Authentication.allowCamera){
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, Authentication.MY_PERMISSIONS_REQUEST_CAMERA);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();

            Bitmap imageBitmap = ((Bitmap)extras.get("data"));

            ProfilePicTask profilePicTask = new ProfilePicTask(imageView);
            profilePicTask.execute();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] d = baos.toByteArray();

            UploadTask uploadTask = imagesRef.putBytes(d);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    photoURI = taskSnapshot.getDownloadUrl();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(photoURI).build();
                    MainActivity.user.updateProfile(profileUpdates);
                }
            });

        }
    }

}
