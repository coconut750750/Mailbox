package com.brandon.mailbox.chat;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brandon.mailbox.MainActivity;
import com.brandon.mailbox.R;

import java.util.List;

/***
 * Created by Brandon on 6/16/16.
 */
public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>{

    private static List<String> chats;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        ViewHolder(CardView v) {
            super(v);
            cardView = v;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = cardView.getContext();

                    String uid = chats.get(getAdapterPosition()).trim();
                    context.startActivity(MainActivity.chatActivity(context, uid));
                    ((Activity)context).overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                }
            });
        }
    }

    public ChatsAdapter (List<String> chats){
        ChatsAdapter.chats = chats;
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
        String uid = chats.get(position).trim();

        String name = MainActivity.allUsers.get(uid);

        RelativeLayout relativeLayout = (RelativeLayout)cardView.findViewById(R.id.contact_card_background);

        if(chats.get(position).contains(ChatActivity.SEPARATOR)){
            relativeLayout.setBackground(ResourcesCompat.getDrawable(textView.getContext().getResources(), R.drawable.contact_item_border_dark, null));
        } else {
            relativeLayout.setBackground(ResourcesCompat.getDrawable(textView.getContext().getResources(), R.drawable.contact_item_border, null));
        }

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