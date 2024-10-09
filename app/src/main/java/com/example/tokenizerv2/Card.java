package com.example.tokenizerv2;

import android.graphics.Bitmap;
public class Card {
    private String name;
    private String imageUrl;
    private Bitmap image;

    public Card(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }
}