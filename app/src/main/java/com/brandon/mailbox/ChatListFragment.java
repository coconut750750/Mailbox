package com.brandon.mailbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


public class ChatListFragment extends Fragment {
    //Data
    public static String uid;
    public static String name;
    public static String email;

    public static RecyclerView mRecyclerView;



    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_chats, container, false);

        //getting recycler view and adapter
        mRecyclerView = (RecyclerView) view.findViewById(R.id.contacts_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(MainActivity.chatsAdapter);

        MainActivity.chatsAdapter.notifyDataSetChanged();

    return view;
}

}
