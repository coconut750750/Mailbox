package com.brandon.mailbox;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class ContactAdd extends AppCompatActivity {

    RecyclerView mRecyclerView;
    HashMap<String, String> possibles;
    ListContactAdapter contactAddAdapter;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);

        setTitle("Add Contacts");

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.potential_contacts);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        possibles = new HashMap<>();

        contactAddAdapter = new ListContactAdapter(possibles, ListContactAdapter.ContactAdd);

        mRecyclerView.setAdapter(contactAddAdapter);
        contactAddAdapter.notifyDataSetChanged();

        add = (Button)findViewById(R.id.addContact);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(add);
            }
        });

    }

    //Button from addContact frag
    public void addContact(View view){
        EditText fragText = (EditText) findViewById(R.id.edit);
        String name = fragText.getText().toString();
        //hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(fragText.getWindowToken(), 0);

        addContact(name);
    }

    public void addContact(String name){
        possibles.clear();
        for(String uid : MainActivity.allUsers.keySet()){
            if(MainActivity.allUsers.get(uid).contains(name) && !MainActivity.contacts.contains(uid) && !uid.equals(MainActivity.uid)){
                possibles.put(uid, MainActivity.allUsers.get(uid));
                /*MainActivity.contacts.add(name);
                MainActivity.contactsAdapter.notifyDataSetChanged();
                MainActivity.contactRef.setValue(MainActivity.contacts);
                return;*/
            }
        }
        contactAddAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        finish();
    }
}
