package com.brandon.mailbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ProfileFragment extends Fragment {
    public static Button logout;
    public static ImageView imageView;
    public static TextView name;


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

        imageView = (ImageView)view.findViewById(R.id.image);
        name = (TextView)view.findViewById(R.id.profileName);

        name.setText(MainActivity.name);

        ProfilePicTask profilePicTask = new ProfilePicTask(imageView);
        profilePicTask.execute();

        return view;
    }

}
