package com.example.tokenizerv2;

import android.graphics.Bitmap;
public class Card {
    private String name;
    private String description;
    private String power;
    private String toughness;
    private String imageUrl;
    private Bitmap image;
    private byte[] imageByteArray;


    public Card(String name, String imageUrl, String description, String power, String toughness) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
        this.power = power;
        this.toughness = toughness;
    }

    public String getName() {
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getPower(){
        return "Power: " + power;
    }

    public String getToughness(){
        return "Toughness: " + toughness;
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
    public void setImageByteArray(byte[] imageByteArray) {
        this.imageByteArray = imageByteArray;
    }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }


}