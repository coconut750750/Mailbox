package com.brandon.mailbox;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatActivity extends AppCompatActivity {
    public static final String timeFormat = "yyyy:MM:dd K:mm:ss:SSS:a";
    public static final String dateFormat = "yyyy:MM:dd";
    public static final String noDayFormat = "K:mm:ss a";

    public static String SEPARATOR = "\t";
    public static String titleSep = " - ";

    String name;
    String uid;

    public File file;
    Thread t;

    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        name = intent.getStringExtra("NAME");
        uid = intent.getStringExtra("UID");


        setTitle(name+titleSep);

        //getting view pager
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        final ChatSwipeAdapter swipeAdapter = new ChatSwipeAdapter(getSupportFragmentManager());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                String uid = MainActivity.chatNames.get(position).trim();
                setTitle(MainActivity.allUsers.get(uid)+ChatActivity.titleSep);
                /*t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (!isInterrupted()) {
                                Thread.sleep(1000);
                                try {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (contactTimeZone != 1) {
                                                Calendar cal = Calendar.getInstance();
                                                SimpleDateFormat sdf = new SimpleDateFormat(ChatActivity.noDayFormat, Locale.ENGLISH);
                                                cal.setTime(new Date());
                                                cal.add(Calendar.MILLISECOND, -cal.getTimeZone().getOffset(cal.getTimeInMillis()));
                                                cal.add(Calendar.MILLISECOND, contactTimeZone);
                                                Date d = cal.getTime();
                                                setTitle(name + ChatActivity.titleSep + sdf.format(d));
                                            }
                                        }
                                    });

                                } catch (NullPointerException e){
                                    return;
                                }
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                };

                t.start();*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setAdapter(swipeAdapter);
        int item = MainActivity.chatNames.indexOf(uid);
        if (item == -1){
            item = MainActivity.chatNames.indexOf(uid+ChatActivity.SEPARATOR);
        }
        viewPager.setCurrentItem(item);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

    public static String readableTimeHelper(String datetime){
        String dateSeparator = ".";
        String timeSeparator = ":";
        String result = "";
        String[] data = datetime.split(" ");
        String current = new SimpleDateFormat(dateFormat, Locale.US).format(new java.util.Date());
        String date = data[0];

        if (current.equals(date)){
            //same day
            result += "Today ";
        }
        else {
            String[] dateData = date.split(":");
            date = dateData[1]+dateSeparator+dateData[2]+dateSeparator+dateData[0];
            result += date+" ";
        }

        String time = data[1];
        String[] timeData = time.split(":");
        time = timeData[0]+timeSeparator+timeData[1]+" "+timeData[4];

        result += time;
        return result;
    }

    public static String getReadableTime(String datetime){
        String result = "";

        datetime = datetime.trim();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
        try {
            cal.setTime(sdf.parse(datetime));
            cal.add(Calendar.MILLISECOND, cal.getTimeZone().getOffset(cal.getTimeInMillis()));
            Date d = cal.getTime();
            datetime = new SimpleDateFormat(timeFormat, Locale.US).format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        result = result + readableTimeHelper(datetime);

        return result;
    }

    public static String getTime(String message){
        return message.substring(0,message.indexOf(SEPARATOR));
    }
    public static String getMessage(String message){
        return message.substring(message.lastIndexOf(SEPARATOR)+1);
    }
    public static String getSender(String message){
        return message.substring(message.indexOf(SEPARATOR)+1,message.lastIndexOf(SEPARATOR));
    }

    /*public static boolean isSecret(String uid){
        return MainActivity.chatNames.contains(SEPARATOR);
    }*/
}