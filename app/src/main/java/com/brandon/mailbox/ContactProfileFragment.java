package com.brandon.mailbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.MalformedURLException;
import java.net.URL;


public class ContactProfileFragment extends Fragment {
    public final static String NAME = "NAME";
    public final static String EMAIL = "EMAIL";

    public ContactProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_profile, container, false);
        TextView name = (TextView)view.findViewById(R.id.contact_name);
        TextView email = (TextView)view.findViewById(R.id.contact_email);
        final ImageView imageView = (ImageView)view.findViewById(R.id.contact_pic);

        String cname = getArguments().getString(NAME);
        String cUID = getArguments().getString(EMAIL);

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://messenger-9a197.appspot.com");
        StorageReference imagesRef = storageRef.child("profilepic/"+cUID);
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Log.d("asdf",uri.toString());
                    ProfilePicTask profilePicTask = new ProfilePicTask(imageView, new URL(uri.toString()));
                    profilePicTask.execute();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                // Handle any errors
            }
        });


        name.setText(cname);
        email.setText(cUID);
        // Inflate the layout for this fragment
        return view;
    }
}

