package com.brandon.mailbox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_add, container, false);

        Button addContact = (Button)view.findViewById(R.id.addContactFrag);

        /*addContact.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.fragmentTransaction = MainActivity.fragmentManager.beginTransaction();
                MainActivity.frag = new addContact();
                MainActivity.fragmentTransaction.replace(R.id.fragment_container, MainActivity.frag);
                MainActivity.fragmentTransaction.commit();
                return true;
            }
        });*/

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = getContext();
                Intent intent = new Intent(c, ContactAdd.class);
                getActivity().startActivity(intent);
                ((Activity)c).overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

                MainActivity.hideFragmentAdd();
            }
        });

        return view;
    }

}
