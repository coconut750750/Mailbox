package com.brandon.mailbox;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/***
 * Created by Brandon on 6/16/16.
 */
class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{

    private List<String> names;
    private int flippedPos;
    private static long animationDuration;
    public Context context;
    static boolean hasAppeared;

    //Fragments
    private FragmentManager fragmentManager;
    private Fragment frag;

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;
        Button addChat;
        Button dismiss;
        Button profile;
        //contact cards
        private AnimatorSet mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_out);
        private AnimatorSet mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.card_flip_in);
        private boolean mIsBackVisible;
        private View mCardFrontLayout;
        private View mCardBackLayout;
        View.OnClickListener textViewListener;
        View.OnClickListener dismissListener;
        View.OnClickListener addChatListener;
        View.OnClickListener profileListener;

        ViewHolder(CardView v) {
            super(v);
            cardView = v;

            mCardBackLayout = cardView.findViewById(R.id.card_back);
            mCardFrontLayout = cardView.findViewById(R.id.card_front);

            addChat = (Button)cardView.findViewById(R.id.add_chat_prompt);
            dismiss = (Button)cardView.findViewById(R.id.dismiss_prompt);
            profile = (Button)cardView.findViewById(R.id.profile_prompt);
            textView = (TextView)cardView.findViewById(R.id.contact_name);

            mCardBackLayout.setVisibility(View.VISIBLE);
            mCardFrontLayout.setVisibility(View.VISIBLE);
            addChat.setVisibility(View.VISIBLE);
            dismiss.setVisibility(View.VISIBLE);
            profile.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);


            mSetRightOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    cardView.setCardElevation(0);
                    if (!mIsBackVisible){
                        textView.setVisibility(View.VISIBLE);

                    }
                    toggleListeners(false);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    cardView.setCardElevation(2);
                    if(mIsBackVisible){
                        textView.setVisibility(View.INVISIBLE);
                    }
                    toggleListeners(true);
                    addChat.setText("Chat");
                    dismiss.setText("Dismiss");
                    profile.setText("Profile");
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

            final int distance = 8000;
            float scale = context.getResources().getDisplayMetrics().density * distance;
            mCardFrontLayout.setCameraDistance(scale);
            mCardBackLayout.setCameraDistance(scale);
            mIsBackVisible = false;

            textViewListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mIsBackVisible){
                        int position = getAdapterPosition();
                        int previousPosition = flippedPos;
                        flippedPos = position;
                        flipCard();

                        if (previousPosition != -1){
                            notifyItemChanged(previousPosition);
                        }
                    }
                }
            };

            dismissListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    flippedPos = -1;
                    flipCard();
                }
            };

            addChatListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsBackVisible){
                        Context context = cardView.getContext();
                        String uid = MainActivity.contacts.get(getAdapterPosition());
                        MainActivity.addChatHelper(uid);

                        context.startActivity(MainActivity.chatActivity(context, uid));
                        ((Activity)context).overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

                        flippedPos = -1;
                        flipCard();
                    }
                }
            };

            profileListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment f = fragmentManager.findFragmentById(R.id.profile_fragment);
                    if(f == null){
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        frag = new ContactProfileFragment();
                        Bundle b = new Bundle();
                        b.putString(ContactProfileFragment.NAME, MainActivity.allUsers.get(names.get(getAdapterPosition())));
                        b.putString(ContactProfileFragment.EMAIL, names.get(getAdapterPosition()));
                        frag.setArguments(b);
                        fragmentTransaction.add(R.id.profile_fragment, frag);
                        fragmentTransaction.commit();
                    }
                }
            };


            toggleListeners(true);

            }
        void flipCard() {

            if (!mIsBackVisible) {
                mIsBackVisible = true;
                mSetRightOut.setTarget(mCardFrontLayout);
                mSetLeftIn.setTarget(mCardBackLayout);
                setDurations(animationDuration);
                mSetRightOut.start();
                mSetLeftIn.start();

            } else {
                mIsBackVisible = false;
                mSetRightOut.setTarget(mCardBackLayout);
                mSetLeftIn.setTarget(mCardFrontLayout);
                if(hasAppeared){
                    setDurations(animationDuration);
                } else{
                    setDurations(0);
                }
                mSetRightOut.start();
                mSetLeftIn.start();

                Fragment f = fragmentManager.findFragmentById(R.id.profile_fragment);
                if(f != null){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(f);
                    fragmentTransaction.commit();
                }
            }
        }

        void toggleListeners(boolean on){
            if(on){
                textView.setOnClickListener(textViewListener);
                dismiss.setOnClickListener(dismissListener);
                addChat.setOnClickListener(addChatListener);
                profile.setOnClickListener(profileListener);
            }
            else{
                textView.setOnClickListener(null);
                dismiss.setOnClickListener(null);
                addChat.setOnClickListener(null);
                profile.setOnClickListener(null);
            }
        }

        void setDurations(long animationDuration){
            mSetLeftIn.getChildAnimations().get(1).setDuration(animationDuration);
            mSetRightOut.getChildAnimations().get(0).setDuration(animationDuration);
        }

        }

    ContactsAdapter (List<String> names){
        this.names = names;
        this.flippedPos = -1;
        animationDuration = 500;
        hasAppeared = false;

    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
        cv.setCardBackgroundColor(Color.TRANSPARENT);
        context = cv.getContext();
        fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
        return new ContactsAdapter.ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, final int position) {
        TextView textView = holder.textView;
        String uid = names.get(position);
        String name = MainActivity.allUsers.get(uid);
        textView.setText(name);

        if(flippedPos != position && holder.mIsBackVisible){
            holder.flipCard();
        } else if (!holder.mIsBackVisible){
            holder.mIsBackVisible = true;
            holder.flipCard();
        }

        if(position == getItemCount()-1){
            hasAppeared = true;
        }

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    void unflip(){
        int previousPosition = flippedPos;
        flippedPos = -1;
        if (previousPosition != -1){
            notifyItemChanged(previousPosition);
        }
    }
}