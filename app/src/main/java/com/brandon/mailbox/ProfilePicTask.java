package com.brandon.mailbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Brandon on 7/17/16.
 */
public class ProfilePicTask extends AsyncTask<String, Void, Bitmap> {

    ImageView imageView;

    ProfilePicTask (ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        try {
            try {
                URL newurl = new URL(MainActivity.user.getPhotoUrl().toString());
                Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                return mIcon_val;
            } catch (NullPointerException e){
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap mIcon_val) {
        imageView.setImageBitmap(mIcon_val);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
