package com.brandon.mailbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by Brandon on 8/20/16.
 */
public class ChatSwipeAdapter extends FragmentStatePagerAdapter{

    public ChatSwipeAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        fragment = new ChatPrivateFragment();
        String uid = MainActivity.chatNames.get(position);
        String name = MainActivity.allUsers.get(uid);
        Bundle b = new Bundle();
        b.putString("NAME",name);
        b.putString("UID",uid);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public int getCount() {
        return MainActivity.chatNames.size();
    }
}