package com.brandon.mailbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAdd extends Fragment {


    public FragmentAdd() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_add, container, false);

        Button addContact = (Button)view.findViewById(R.id.addContactFrag);
        Button requests = (Button)view.findViewById(R.id.contact_requests);
        Button pending = (Button)view.findViewById(R.id.contact_pending);
        final Context c = getContext();

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, ContactAdd.class);
                getActivity().startActivity(intent);
                ((Activity)c).overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

                MainActivity.hideFragmentAdd();
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, ContactList.class);
                intent.putExtra(ContactList.TYPE,ContactList.REQUESTS);
                getActivity().startActivity(intent);
                ((Activity)c).overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

                MainActivity.hideFragmentAdd();
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, ContactList.class);
                intent.putExtra(ContactList.TYPE,ContactList.PENDING);
                getActivity().startActivity(intent);
                ((Activity)c).overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

                MainActivity.hideFragmentAdd();
            }
        });

        return view;
    }

}
