package com.brandon.mailbox;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brandon on 6/16/16.
 */
public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{

    public static List<String> chats;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = cardView.getContext();
                    TextView tv = (TextView) cardView.findViewById(R.id.info_text);

                    String uid = chats.get(getAdapterPosition());
                    context.startActivity(MainActivity.chatActivity(context, uid));
                    ((Activity)context).overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                }
            });
        }
    }

    public ChatsAdapter (List<String> chats){
        this.chats = chats;
    }

    @Override
    public ChatsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_card, parent, false);
        return new ChatsAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ChatsAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView textView = (TextView)cardView.findViewById(R.id.info_text);
        String uid = chats.get(position);

        String name = MainActivity.allUsers.get(uid);
        textView.setText(name);

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }
}