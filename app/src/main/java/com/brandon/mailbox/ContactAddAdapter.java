package com.brandon.mailbox;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brandon on 8/21/16.
 */
public class ContactAddAdapter extends RecyclerView.Adapter<ContactAddAdapter.ViewHolder>{

    private HashMap<String, String> possibles;
    int flippedPos;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textEmail;

        public ViewHolder(CardView v) {
            super(v);
            textName = (TextView)v.findViewById(R.id.contact_name);
            textEmail = (TextView)v.findViewById(R.id.email);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.addContact(textEmail.getText().toString());
                    Context c = v.getContext();
                    ((Activity)c).finish();
                    ((Activity)c).overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                }
            });
        }
    }

    public ContactAddAdapter (HashMap<String, String> possibles){
        this.possibles = possibles;
        this.flippedPos = -1;
    }

    @Override
    public ContactAddAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_potential_card, parent, false);
        return new ContactAddAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ContactAddAdapter.ViewHolder holder, final int position) {
        ArrayList<String> uids = new ArrayList<>();
        for(String uid:possibles.keySet()){
            uids.add(uid);
        }

        String uid = uids.get(position);
        String name = possibles.get(uid);

        TextView textName = holder.textName;
        TextView textEmail = holder.textEmail;
        textName.setText(name);
        textEmail.setText(uid);
    }

    @Override
    public int getItemCount() {
        return possibles.size();
    }
}