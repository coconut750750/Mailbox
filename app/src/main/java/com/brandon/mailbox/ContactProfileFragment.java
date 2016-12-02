package com.brandon.mailbox;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


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
        String cname = getArguments().getString(NAME);
        String cemail = getArguments().getString(EMAIL);

        name.setText(cname);
        email.setText(cemail);
        // Inflate the layout for this fragment
        return view;
    }
}

