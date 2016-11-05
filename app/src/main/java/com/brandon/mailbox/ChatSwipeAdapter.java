package com.brandon.mailbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/***
 * Created by Brandon on 8/20/16.
 */
class ChatSwipeAdapter extends FragmentStatePagerAdapter{

    ChatSwipeAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        fragment = new ChatPrivateFragment();
        String uid = MainActivity.chatNames.get(position).trim();
        String name = MainActivity.allUsers.get(uid);
        if(name == null){
            name = MainActivity.allUsers.get(uid+ChatActivity.SEPARATOR);
        }

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