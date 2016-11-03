package com.brandon.mailbox;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brandon on 11/3/16.
 */

public class LettersAdapter extends RecyclerView.Adapter<LettersAdapter.ViewHolder> {
    private List<String> letters;

    public LettersAdapter(List<String> letters){
        this.letters = letters;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardview;
        TextView textView;

        ViewHolder(CardView cardView) {
            super(cardView);
            this.cardview = cardView;
            textView = (TextView)cardView.findViewById(R.id.letter_text);
            cardView.setCardElevation(0);
        }
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

    }

    @Override
    public int getItemCount() {
        return letters.size();
    }
}
