package com.example.tokenizerv2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    private Card card;
    private ImageView imageView;
    private CardAdapter adapter;

    public ImageLoader(Card card, ImageView imageView, CardAdapter adapter) {
        this.card = card;
        this.imageView = imageView;
        this.adapter = adapter;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            card.setImage(result);
            imageView.setImageBitmap(result);
            adapter.notifyDataSetChanged();
        }
    }
}