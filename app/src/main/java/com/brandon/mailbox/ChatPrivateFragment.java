package com.brandon.mailbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class ChatPrivateFragment extends Fragment {

    public String name;
    private String uid;
    public int contactTimeZone;

    public Button sendMsg;
    public EditText editMsg;
    public Button toggleSecret;

    public RecyclerView mRecyclerView;
    public ChatPrivateAdapter chatPrivateAdapter;

    public DatabaseReference sendToRef;
    public DatabaseReference receiveFromRef;
    public DatabaseReference contactTimeRef;

    private ArrayList<String> chatMap;
    private ArrayList<String> newChatMap;

    public File file;
    public Thread t;

    public ChatPrivateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat, container, false);

        Bundle data = getArguments();
        name = data.getString("NAME");
        uid = data.getString("UID");

        sendMsg = (Button)view.findViewById(R.id.sendMessage);
        editMsg = (EditText)view.findViewById(R.id.editMessage);
        toggleSecret = (Button)view.findViewById(R.id.toggleSecret);
        editMsg.setInputType(InputType.TYPE_CLASS_TEXT);

        toggleSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSecret();
            }
        });

        sendMsg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String msg = editMsg.getText().toString();
                if (!msg.equals("")){
                    sendMessage(msg);
                }
                return true;
            }
        });

        chatMap = new ArrayList<>();
        newChatMap = new ArrayList<>();
        chatPrivateAdapter = new ChatPrivateAdapter(chatMap);

        sendToRef = MainActivity.mRootRef.child(MainActivity.FirebaseUserList).child(uid).child("Chats").child(MainActivity.uid);

        receiveFromRef = MainActivity.mRootRef.child(MainActivity.FirebaseUserList).child(MainActivity.uid).child("Chats").child(uid);
        receiveFromRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {};
                HashMap<String, String> data = dataSnapshot.getValue(t);
                ArrayList<String> messages = new ArrayList<>();
                if (data != null){
                    for (String stamp : data.keySet()){
                        messages.add(data.get(stamp));
                    }
                    messages = sortChatlist(messages);

                    for (String fullMsg : messages){
                        String time = ChatActivity.getTime(fullMsg);
                        String msg = ChatActivity.getMessage(fullMsg);
                        addMessageToFile(msg, time, ChatActivity.getSender(fullMsg));
                    }
                    receiveFromRef.setValue(new ArrayList<String>());
                }
                getChatHistory();
                updateEverything();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        contactTimeZone = 1;
        contactTimeRef = MainActivity.mRootRef.child(MainActivity.FirebaseUserList).child(uid).child(MainActivity.FirebaseTimeZone);
        contactTimeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactTimeZone = dataSnapshot.getValue(int.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            file = new File(MainActivity.rootFile, uid);
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecyclerView = (RecyclerView)view.findViewById(R.id.chat_messages_recycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setAdapter(chatPrivateAdapter);
        chatPrivateAdapter.notifyDataSetChanged();
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                chatPrivateAdapter.unflip();
                return false;
            }
        });

        //scroll the view when edit text is touched
        editMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mRecyclerView.scrollToPosition(chatMap.size()-1);
            }
        });
        mRecyclerView.scrollToPosition(chatMap.size()-1);

        final String n = name;
        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        try {
                            String title = getActivity().getTitle().toString();
                            final String name = title.substring(0, title.indexOf(ChatActivity.titleSep));
                            if(name.equals(n)){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (contactTimeZone != 1) {
                                            Calendar cal = Calendar.getInstance();
                                            SimpleDateFormat sdf = new SimpleDateFormat(ChatActivity.noDayFormat, Locale.ENGLISH);
                                            cal.setTime(new Date());
                                            cal.add(Calendar.MILLISECOND, -cal.getTimeZone().getOffset(cal.getTimeInMillis()));
                                            cal.add(Calendar.MILLISECOND, contactTimeZone);
                                            Date d = cal.getTime();
                                            getActivity().setTitle(name + ChatActivity.titleSep + sdf.format(d));
                                        }
                                    }
                                });
                            }
                        } catch (NullPointerException e){
                            return;
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

        return view;
    }

    public void sendMessage(String msg){
        msg = msg.replace("\n","\\n");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, -cal.getTimeZone().getOffset(cal.getTimeInMillis()));
        Date date = cal.getTime();
        String time = new SimpleDateFormat(ChatActivity.timeFormat).format(date);

        String toAdd = time+ChatActivity.SEPARATOR+MainActivity.uid+ChatActivity.SEPARATOR+msg;
        newChatMap.add(toAdd);

        chatMap.add(toAdd); //add to chatMap after newChatMap is sorted
        updateEverything();
        editMsg.setText("");
        sendToRef.push().setValue(toAdd);

        addMessageToFile(msg,time, MainActivity.uid);
    }

    public void addMessageToFile(String msg, String time, String uid){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result = result + line+"\n";
            }
            result = result + (time+ChatActivity.SEPARATOR+uid+ChatActivity.SEPARATOR+msg) + "\n";

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(result);

            bufferedReader.close();
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateEverything(){
        chatPrivateAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(chatMap.size()-1);
    }

    public void getChatHistory(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(!chatMap.contains(line.trim()))
                {
                    chatMap.add(line.trim());
                }
            }
            bufferedReader.close();
            updateEverything();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> sortChatlist(ArrayList<String> messages){

        Collections.sort(messages, new Comparator<String>() {
            public int compare(String m1, String m2) {
                SimpleDateFormat formatter = new SimpleDateFormat(ChatActivity.timeFormat);
                try {
                    Date t1 = formatter.parse(ChatActivity.getTime(m1));
                    Date t2 = formatter.parse(ChatActivity.getTime(m2));
                    return t1.compareTo(t2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        return messages;
    }

    public void toggleSecret(){
        chatPrivateAdapter.toggleSecret();
        if (editMsg.getInputType() == InputType.TYPE_CLASS_TEXT){
            editMsg.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        else{
            editMsg.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }
}
