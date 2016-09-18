package com.brandon.mailbox;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Brandon on 6/24/16.
 */
public class ChatPrivateAdapter extends RecyclerView.Adapter<ChatPrivateAdapter.ViewHolder>{

    private ArrayList<String> messages;
    int oppositePos;
    private boolean secretOn;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView textView;
        private TextView timeTextView;
        public String original;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int previousPosition = oppositePos;
                    int position = getAdapterPosition();

                    if (oppositePos == position) {
                        if (secretOn){
                            textView.setText(hidden(original));
                        }
                        oppositePos = -1;
                    } else {
                        textView.setText(original);
                        oppositePos = position;
                    }
                    if (previousPosition!=-1)
                        notifyItemChanged(previousPosition);
                }
            });


            textView = (TextView)cardView.findViewById(R.id.info_text);
            timeTextView = (TextView)cardView.findViewById(R.id.time);
        }
    }

    public ChatPrivateAdapter (ArrayList<String> messages){
        this.messages = messages;
        this.oppositePos = -1;
        secretOn = false;
    }

    @Override
    public ChatPrivateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_card, parent, false);
        return new ChatPrivateAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ChatPrivateAdapter.ViewHolder holder, final int position) {
        TextView textView = holder.textView;
        TextView timeTextView = holder.timeTextView;

        String currentMsg = messages.get(position);
        currentMsg = currentMsg.replace("\\n","\n");

        boolean sent = ChatActivity.getSender(currentMsg).equals(MainActivity.uid);
        String textMsg = ChatActivity.getMessage(currentMsg);
        holder.original = textMsg;
        if(oppositePos != position && secretOn){
            textMsg = hidden(textMsg);
        }

        if(sent){
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            timeTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }
        else{
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            timeTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        textView.setText(textMsg);
        timeTextView.setText(ChatActivity.getReadableTime(ChatActivity.getTime(currentMsg)));

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public String hidden(String msg){
        String newMsg = "";
        String[] msgList = msg.split("");
        for(int i = 0; i<msgList.length;i++){
            if (!msgList[i].equals("\n")){
                if(i!= 0){
                    newMsg += "*";
                }
            }
            else {
                newMsg += "\n";
            }
        }
        return newMsg;
    }

    public void toggleSecret(){
        secretOn = !secretOn;
        notifyDataSetChanged();
        oppositePos = -1;
    }

    public void unflip(){
        int previousPosition = oppositePos;
        oppositePos = -1;
        if (previousPosition != -1){
            notifyItemChanged(previousPosition);
        }
    }
}
