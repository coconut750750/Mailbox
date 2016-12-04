package com.brandon.mailbox;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


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

        try {
            Uri uri = MainActivity.user.getPhotoUrl();
            ProfilePicTask profilePicTask = new ProfilePicTask(imageView, new URL(uri.toString()));
            profilePicTask.execute();
        } catch (MalformedURLException e) {
        } catch (java.lang.NullPointerException e){
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://messenger-9a197.appspot.com");
        imagesRef = storageRef.child("profilepic/"+MainActivity.uid);

        return view;
    }

    public void takePicture(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, Authentication.MY_PERMISSIONS_REQUEST_CAMERA);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        photoURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            try {
                final Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
                imageView.setImageBitmap(imageBitmap);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Log.d("asdf","asdf");
                        scaleDown(imageBitmap, 1500).compress(Bitmap.CompressFormat.PNG, 100, baos);
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
                });
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize) {
        float ratio = Math.min(maxImageSize / (float)realImage.getWidth(), maxImageSize / (float)realImage.getHeight());
        int width = Math.round(ratio * (float)realImage.getWidth());
        int height = Math.round(ratio * (float)realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, true);

        Log.d("ratio",""+ratio);

        return newBitmap;
    }

}
