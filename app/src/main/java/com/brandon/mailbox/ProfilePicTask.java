package com.brandon.mailbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/***
 * Created by Brandon on 7/17/16.
 */
class ProfilePicTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private URL imageUrl;

    ProfilePicTask (ImageView imageView, URL url){
        this.imageView = imageView;
        this.imageUrl = url;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        try {
            return BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
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
