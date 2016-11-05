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
import java.util.List;

/***
 * Created by Brandon on 11/5/16.
 * Used for contactAdd, requests, and pending
 */

public class ListContactAdapter extends RecyclerView.Adapter<ListContactAdapter.ViewHolder> {

    private HashMap<String, String> list;
    private String type;
    public static final String ContactAdd = "CONTACTADD";

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textEmail;
        CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            this.cardView = v;
            textName = (TextView) v.findViewById(R.id.contact_name);
            textEmail = (TextView) v.findViewById(R.id.email);
            if (type.equals(ContactAdd)) {
                this.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.addContact(textEmail.getText().toString());
                        Context c = v.getContext();
                        ((Activity) c).finish();
                        ((Activity) c).overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                    }
                });
            }
        }
    }

    public ListContactAdapter(HashMap<String, String> list, String type) {
        this.list = list;
        this.type = type;
    }

    @Override
    public ListContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_potential_card, parent, false);
        return new ListContactAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ListContactAdapter.ViewHolder holder, int position) {
        TextView textName = holder.textName;
        TextView textEmail = holder.textEmail;
        ArrayList<String> uids = new ArrayList<>();
        for (String uid : list.keySet()) {
            uids.add(uid);
        }
        String uid = uids.get(position);
        String name = list.get(uid);
        textName.setText(name);
        textEmail.setText(uid);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
