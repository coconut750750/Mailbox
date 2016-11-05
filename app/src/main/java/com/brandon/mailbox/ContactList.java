package com.brandon.mailbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ContactList extends AppCompatActivity {
    public static final String TYPE = "TYPE";
    public static final String REQUESTS = "REQUESTS";
    public static final String PENDING = "PENDING";

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Intent intent = getIntent();
        String type = intent.getStringExtra(TYPE);


        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.contacts);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);


        if(type.equals(REQUESTS)){
            mRecyclerView.setAdapter(MainActivity.requestsAdapter);
            MainActivity.requestsAdapter.notifyDataSetChanged();
            setTitle("Contact Requests");
        } else if (type.equals(PENDING)){
            mRecyclerView.setAdapter(MainActivity.pendingAdapter);
            MainActivity.pendingAdapter.notifyDataSetChanged();
            setTitle("Pending Requests");
        }
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
