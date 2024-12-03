package com.example.tokenizerv2;

import android.graphics.Bitmap;
public class Card {
    private String name;
    private String type;

    private String rulesText;
    private String power;
    private String toughness;
    private String imageUrl;
    private String artUrl;
    private Bitmap image;

    private Bitmap art;
    private byte[] imageByteArray;


    public Card(String name, String imageUrl, String artUrl, String type, String rules, String power, String toughness) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.artUrl = artUrl;
        this.type = type;
        this.rulesText = rules;
        this.power = power;
        this.toughness = toughness;
    }

    public String getName() {
        return name;
    }

    public String getType(){
        return type;
    }

    public String getRules() {return rulesText;}

    public String getPower(){
        return "Power: " + power;
    }
    public byte[] getPowBytes() {
        byte pow = (byte) Integer.parseInt(power);
        return new byte[]{pow};
    }

    public String getToughness(){
        return "Toughness: " + toughness;
    }

    public byte[] getTufBytes() {
        byte pow = (byte) Integer.parseInt(toughness);
        return new byte[]{pow};
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public String getArtUrl() { return artUrl; }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setArt(Bitmap image) { this.art = image; }

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