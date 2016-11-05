package com.brandon.mailbox;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/***
 * Created by Brandon on 6/24/16.
 */
class ChatPrivateAdapter extends RecyclerView.Adapter<ChatPrivateAdapter.ViewHolder>{

    private ArrayList<String> messages;
    private int oppositePos;
    private boolean secretOn;

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView textView;
        private TextView timeTextView;
        private LinearLayout linearLayout;
        String original;

        ViewHolder(CardView v) {
            super(v);
            cardView = v;
            linearLayout = (LinearLayout)v.findViewById(R.id.chat_message_card_background);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editMsg = (EditText)v.getRootView().findViewById(R.id.editMessage);

                    ChatPrivateFragment.clearEditText(editMsg, v.getContext(), v.getRootView());
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

    ChatPrivateAdapter (ArrayList<String> messages){
        this.messages = messages;
        this.oppositePos = -1;
        this.secretOn = false;
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

        String sender = ChatActivity.getSender(currentMsg);
        boolean permSecret = sender.contains(ChatActivity.SEPARATOR);
        if (permSecret){
            sender = sender.trim();
        }

        boolean sent = sender.equals(MainActivity.uid);
        String textMsg = ChatActivity.getMessage(currentMsg);
        holder.original = textMsg;
        if(oppositePos != position && secretOn || permSecret){
            textMsg = hidden(textMsg);
            holder.linearLayout.setBackground(ResourcesCompat.getDrawable(textView.getContext().getResources(), R.drawable.contact_item_border_dark, null));
        } else {
            holder.linearLayout.setBackground(ResourcesCompat.getDrawable(textView.getContext().getResources(), R.drawable.contact_item_border, null));
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

    private String hidden(String msg){
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

    void toggleSecret(){
        secretOn = !secretOn;
        notifyDataSetChanged();
        oppositePos = -1;
    }

    void unflip(){
        int previousPosition = oppositePos;
        oppositePos = -1;
        if (previousPosition != -1){
            notifyItemChanged(previousPosition);
        }
    }
}
