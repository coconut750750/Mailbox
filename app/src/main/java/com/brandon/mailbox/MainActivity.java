package com.brandon.mailbox;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    //CONSTANTS
    public static final String FirebaseUserList = "users";
    public static final String FirebaseAllUsers = "allUsers";
    public static final String CONTACTS = "CONTACTS";
    public static final String CHATS = "CHATS";
    public static final String ME = "ME";
    public static final String FirebaseTimeZone = "timezone";

    //User Data
    public static FirebaseUser user;
    public static String uid;
    public static String name;
    public static String email;

    //Database References
    public static DatabaseReference mRootRef;
    public static DatabaseReference contactRef;
    public static DatabaseReference requestsRef;
    public static DatabaseReference pendingRef;
    public static DatabaseReference chatRef;
    public static DatabaseReference timeRef;

    //All users
    public static HashMap<String,String> allUsers; //uid, name

    //Contacts activity
    public static List<String> contacts; //Contacts list
    public static ContactsAdapter contactsAdapter;
    public static List<String> favs;
    public static List<String> letters; //letter list
    public static LettersAdapter lettersAdapter;
    //Request
    public static HashMap<String, String> requests;
    public static ListContactAdapter requestsAdapter;
    //Pending
    public static HashMap<String, String> pending;
    public static ListContactAdapter pendingAdapter;

    //Chat activity
    public static List<String> chatNames;
    public static ChatsAdapter chatsAdapter;

    //Views
    public static ViewPager viewPager;

    //Fragments
    public static FragmentManager fragmentManager;
    public static FragmentTransaction fragmentTransaction;
    public static Fragment frag;

    //Files
    public static File chatListFile;
    public static String chatListFilename;
    public static File rootFile;

    //Drawer
    public static DrawerLayout drawerLayout;
    public static TextView drawerName;
    public static TextView drawerEmail;
    public static ActionBarDrawerToggle drawerToggle;
    public static NavigationView navigationView;
    public static ListView navigationFavList;

    public static Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Mailbox");

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //bottom menu
        bottomMenuContacts = (Button) findViewById(R.id.bottom_menu_contacts);
        bottomMenuChats = (Button) findViewById(R.id.bottom_menu_chats);
        bottomMenuMe = (Button) findViewById(R.id.bottom_menu_me);

        white1 = (Button) findViewById(R.id.white1);
        black = (Button) findViewById(R.id.black);
        white2 = (Button) findViewById(R.id.white2);

        //Contact page starts
        setBottomMenuItem(CONTACTS);

        //Getting User Data
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }
        uid = user.getUid();
        name = user.getDisplayName();
        email = user.getEmail();


        //getting view pager
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float absolutePos = (float) position + positionOffset;
                LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) white1.getLayoutParams();
                params1.weight = 3.0f - absolutePos;
                white1.setLayoutParams(params1);

                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) white2.getLayoutParams();
                params2.weight = 1.0f + absolutePos;
                white2.setLayoutParams(params2);

                int roundedPos = Math.round(absolutePos);
                switch (roundedPos) {
                    case 0:
                        setBottomMenuItem(CONTACTS);
                        break;
                    case 1:
                        setBottomMenuItem(CHATS);
                        break;
                    case 2:
                        setBottomMenuItem(ME);
                        ContactsAdapter.hasAppeared = false;
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        final SwipeAdapter swipeAdapter = new SwipeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(swipeAdapter);


        //firebase start
        mRootRef = FirebaseDatabase.getInstance().getReference();
        //getting all users
        final DatabaseReference allRef = mRootRef.child(FirebaseAllUsers);
        allUsers = new HashMap<>();
        allRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
                };
                HashMap<String, String> list = dataSnapshot.getValue(t);
                if (list != null) {
                    for (String uid : list.keySet()) {
                        allUsers.put(uid, list.get(uid));
                    }
                    allUsers.put(uid, name);
                    allRef.setValue(allUsers);
                }

                contacts.sort(new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        char c1 = MainActivity.allUsers.get(s).charAt(0);
                        char c2 = MainActivity.allUsers.get(t1).charAt(0);
                        return c1-c2;
                    }
                });

                contactsAdapter.notifyDataSetChanged();
                chatsAdapter.notifyDataSetChanged();
                lettersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //Contacts Reference
        contacts = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(contacts);
        contactRef = mRootRef.child(FirebaseUserList).child(uid).child("Contacts");
        contactRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts.clear();

                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                };

                List<String> list = dataSnapshot.getValue(t);
                if (list != null) {
                    for (String name : list) {
                        if (name == null) {
                            continue;
                        }
                        contacts.add(name);
                    }
                }

                contactsAdapter.notifyDataSetChanged();
                lettersAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Contact Fragment Letters Scroll
        letters = new ArrayList<>();
        for(int i = 65; i < 91; i++){
            letters.add(""+(char)i);
        }
        // is instatiated in contactFragment lettersAdapter = new LettersAdapter(letters);

        //Request Reference
        requests = new HashMap<>();
        requestsAdapter = new ListContactAdapter(requests, ContactList.REQUESTS);
        requestsRef = mRootRef.child(FirebaseUserList).child(uid).child("Requests");
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                requests.clear();

                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {};
                HashMap<String, String> data = dataSnapshot.getValue(t);

                if (data != null) {
                    for (String uid : data.keySet()) {
                        requests.put(uid, MainActivity.allUsers.get(uid));
                    }
                }
                requestsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Pending Reference
        pending = new HashMap<>();
        pendingAdapter = new ListContactAdapter(pending, ContactList.PENDING);
        pendingRef = mRootRef.child(FirebaseUserList).child(uid).child("Pending");
        pendingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pending.clear();

                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {};
                HashMap<String, String> data = dataSnapshot.getValue(t);

                if (data != null) {
                    for (String uid : data.keySet()) {
                        pending.put(uid, MainActivity.allUsers.get(uid));
                    }
                }
                pendingAdapter.notifyDataSetChanged();
                refreshContact();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //ChatList Reference
        chatNames = new ArrayList<>();
        chatsAdapter = new ChatsAdapter(chatNames);
        chatRef = mRootRef.child(FirebaseUserList).child(uid).child("Chats");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Object>> t = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                HashMap<String, Object> data = dataSnapshot.getValue(t);
                if (data != null) {
                    for (String name : data.keySet()) {
                        if (!chatNames.contains(name) || !chatNames.contains(name+ChatActivity.SEPARATOR)) {
                            chatNames.add(name);
                            addChatToFile(name);
                        }
                    }
                }
                chatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Timezone Reference
        timeRef = mRootRef.child(FirebaseUserList).child(uid).child(FirebaseTimeZone);
        timeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Calendar cal = Calendar.getInstance();
                timeRef.setValue(cal.getTimeZone().getOffset(cal.getTimeInMillis()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //Creating files
        rootFile = new File(this.getFilesDir(), uid);
        rootFile.mkdir();

        chatListFilename = "chatNames";
        try {
            chatListFile = new File(rootFile, chatListFilename);
            chatListFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getChatNames();


        //drawer view
        drawerLayout = (DrawerLayout)findViewById(R.id.main_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationFavList = (ListView)findViewById(R.id.navigation_fav_list);

        View header = navigationView.getHeaderView(0);

        drawerName = (TextView) header.findViewById(R.id.drawer_name);
        drawerName.setText(name);
        drawerEmail = (TextView) header.findViewById(R.id.drawer_email);
        drawerEmail.setText(email);

        favs = new ArrayList<>();
        favs.add("Alex Yu");
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favs);

        navigationFavList.setAdapter(listAdapter);


        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Log.d("string", "" + favs.get(item.getItemId()));

                Context c = getApplicationContext();
                //c.startActivity(MainActivity.chatActivity(c, uid));

                return false;
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_app_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_add:

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                Fragment f = fragmentManager.findFragmentById(R.id.fragment_container);
                if(f == null) {
                    frag = new FragmentAdd();
                    fragmentTransaction.add(R.id.fragment_container, frag);
                }
                else{
                    fragmentTransaction.remove(frag);
                }
                fragmentTransaction.commit();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public static void hideFragmentAdd(){
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment f = fragmentManager.findFragmentById(R.id.fragment_container);
        if(f != null) {
            fragmentTransaction.remove(frag);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestorseInstanceState has occurred.
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public static void requestContact(String otherUid){
        if(!pending.containsKey(otherUid)){
            DatabaseReference otherRequestRef = mRootRef.child(FirebaseUserList).child(otherUid).child("Requests");
            otherRequestRef.push().setValue(uid);
            pending.put(otherUid, MainActivity.allUsers.get(otherUid));
            pendingRef.setValue(pending);
        }
    }

    public static void addContact(String otherUid){
        contacts.add(otherUid);
        contactRef.setValue(contacts);
        contactsAdapter.notifyDataSetChanged();
        requests.remove(otherUid);
        requestsRef.setValue(requests);
        requestsAdapter.notifyDataSetChanged();
        DatabaseReference otherRequestRef = mRootRef.child(FirebaseUserList).child(otherUid).child("Requests");
        otherRequestRef.push().setValue(uid);
    }

    public static void refreshContact(){
        for(String rUid:requests.keySet()){
            for(String pUid:pending.keySet()){
                if(rUid.equals(pUid)){
                    requests.remove(rUid);
                    pending.remove(pUid);
                    contacts.add(rUid);
                }
            }
        }
        requestsAdapter.notifyDataSetChanged();
        pendingAdapter.notifyDataSetChanged();
        contactsAdapter.notifyDataSetChanged();
        requestsRef.setValue(requests);
        pendingRef.setValue(pending);
        contactRef.setValue(contacts);

    }

    public static void addChatHelper(String uid){
        if (!chatNames.contains(uid)) {
            chatNames.add(uid);
            addChatToFile(uid);
        }

        chatsAdapter.notifyDataSetChanged();
    }

    public static void addChatToFile(String name){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(chatListFile));
            String line;
            String result = "";
            boolean added = false;
            while ((line = bufferedReader.readLine()) != null) {
                if(line.trim().equals(name.trim())){
                    result = result + name + "\n";
                    added = true;
                } else {
                    result = result + line + "\n";
                }
            }
            if (!added) {
                result = result + name + "\n";
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(chatListFile));
            bufferedWriter.write(result);

            bufferedReader.close();
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getChatNames(){

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(chatListFile));
            String uid;
            while ((uid = bufferedReader.readLine()) != null) {
                if(!chatNames.contains(uid))
                {
                    chatNames.add(uid);
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Intent chatActivity(Context context, String uid){
        String name = MainActivity.allUsers.get(uid);
        if(name == null){
            name = MainActivity.allUsers.get(uid+ChatActivity.SEPARATOR);
        }
        Intent intent = new Intent(context, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("NAME", name);
        intent.putExtra("UID", uid);
        return intent;
    }

    public static boolean isSecret(String uid){
        for(int i = 0; i < MainActivity.chatNames.size(); i++){
            if(MainActivity.chatNames.get(i).trim().equals(uid)){
                return MainActivity.chatNames.get(i).contains(ChatActivity.SEPARATOR);
            }
        }
        return false;
    }

    //Logout button
    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, Authentication.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        finish();
    }

    public static Button bottomMenuContacts;
    public static Button bottomMenuChats;
    public static Button bottomMenuMe;

    public static Button white1;
    public static Button black;
    public static Button white2;

    public static void setBottomMenuItem(String activity){
        bottomMenuContacts.setTextColor(Color.GRAY);
        bottomMenuContacts.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.viewPager.setCurrentItem(0);
                return true;
            }
        });
        bottomMenuChats.setTextColor(Color.GRAY);
        bottomMenuChats.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.viewPager.setCurrentItem(1);
                return true;
            }
        });
        bottomMenuMe.setTextColor(Color.GRAY);
        bottomMenuMe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MainActivity.viewPager.setCurrentItem(2);
                return true;
            }
        });


        switch (activity){
            case CONTACTS:
                bottomMenuContacts.setTextColor(Color.BLACK);
                bottomMenuContacts.setOnTouchListener(null);
                break;
            case CHATS:
                bottomMenuChats.setTextColor(Color.BLACK);
                bottomMenuChats.setOnTouchListener(null);
                break;
            case ME:
                bottomMenuMe.setTextColor(Color.BLACK);
                bottomMenuMe.setOnTouchListener(null);
                break;
        }
    }
}

/* to make drawer on top of tool bar
private void moveDrawerToTop() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        drawerLayout = (DrawerLayout) inflater.inflate(R.layout.drawer_layout, null); // "null" is important.

        // HACK: "steal" the first child of decor view
        ViewGroup decor = (ViewGroup) getWindow().getDecorView();
        View child = decor.getChildAt(0);
        decor.removeView(child);
        LinearLayout container = (LinearLayout) drawerLayout.findViewById(R.id.drawer_content); // This is the container we defined just now.
        container.addView(child, 0);
        drawerLayout.findViewById(R.id.main_drawer_layout).setPadding(0, getStatusBarHeight(), 0, 0);

        // Make the drawer replace the first child
        decor.addView(drawerLayout);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }*/