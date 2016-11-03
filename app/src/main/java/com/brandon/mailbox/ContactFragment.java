package com.brandon.mailbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class ContactFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView letterRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;


    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_contacts, container, false);

        //getting recycler view and adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.contacts_recycler);
        mLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(MainActivity.contactsAdapter);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                 @Override
                 public boolean onTouch(View v, MotionEvent event) {
                     MainActivity.contactsAdapter.unflip();

                     return false;
                 }
             });



        MainActivity.contactsAdapter.notifyDataSetChanged();


        //getting letter recycler view and adapter
        MainActivity.lettersAdapter = new LettersAdapter(MainActivity.letters, mRecyclerView, mLayoutManager, MainActivity.contacts);

        letterRecyclerView = (RecyclerView) view.findViewById(R.id.letters_recycler);
        RecyclerView.LayoutManager lLayoutManager = new GridLayoutManager(getContext(), 7);
        letterRecyclerView.setLayoutManager(lLayoutManager);

        letterRecyclerView.setAdapter(MainActivity.lettersAdapter);


        return view;
    }

}
