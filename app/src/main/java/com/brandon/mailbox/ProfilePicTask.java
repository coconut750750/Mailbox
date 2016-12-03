package com.brandon.mailbox;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/***
 * Created by Brandon on 7/17/16.
 */
class ProfilePicTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;

    ProfilePicTask (ImageView imageView){
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        try {
            try {
                URL newurl = new URL(MainActivity.user.getPhotoUrl().toString());
                return BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            } catch (NullPointerException e){
                return null;
            }

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

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mIcon_val.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap source = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        imageView.setImageBitmap(source);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
