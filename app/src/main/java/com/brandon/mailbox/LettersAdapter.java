package com.brandon.mailbox;

import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/***
 * Created by Brandon on 11/3/16.
 */

class LettersAdapter extends RecyclerView.Adapter<LettersAdapter.ViewHolder> {
    private List<String> letters;
    private int black;
    private int previousPosition;
    private RecyclerView contactRecyclerView;
    private RecyclerView.LayoutManager contactLayoutManager;
    private List<String> contactUids;

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;

        ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            textView = (TextView)cardView.findViewById(R.id.letter_text);
            cardView.setCardElevation(0);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    previousPosition = black;
                    black = getAdapterPosition();
                    String letter = letters.get(black);

                    notifyItemChanged(black);
                    if (previousPosition != -1){
                        notifyItemChanged(previousPosition);
                    }

                    int maxPos = 0;
                    char previousChar = 'a';
                    for(int u = 0; u<contactUids.size(); u++){
                        String uid = contactUids.get(u);
                        String name = MainActivity.allUsers.get(uid);
                        char nameChar = name.charAt(0);
                        char letChar = letter.charAt(0);
                        if(nameChar == previousChar){
                            continue;
                        }

                        previousChar = nameChar;

                        if(nameChar < letChar){
                            maxPos = u;
                        } else if(nameChar==letChar){
                            maxPos = u;
                            break;
                        } else {
                            break;
                        }
                    }

                    contactLayoutManager.smoothScrollToPosition(contactRecyclerView, null, maxPos);
                }
            });
        }
    }

    LettersAdapter(List<String> letters, RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager, List<String> contacts){
        this.letters = letters;
        black = 0;
        previousPosition = -1;
        contactRecyclerView = recyclerView;
        contactLayoutManager = layoutManager;
        contactUids = contacts;
    }

    @Override
    public LettersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.letter_card, parent, false);
        cv.setCardBackgroundColor(Color.TRANSPARENT);
        return new LettersAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String letter = letters.get(position);
        holder.textView.setText(letter);
        int color;
        if(position == black){
            color = Color.BLACK;
        } else{
            color = ResourcesCompat.getColor(holder.textView.getResources(), R.color.favorite2dark, null);
        }
        holder.textView.setTextColor(color);

    }

    @Override
    public int getItemCount() {
        return letters.size();
    }
}
