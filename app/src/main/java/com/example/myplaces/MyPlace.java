package com.example.myplaces;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MyPlace implements Parcelable {

    private String place_id;
    private String name;
    private LatLng location;
    private boolean open_now;
    private int price_level;
    private double rating;
    private ArrayList<String> types = new ArrayList<String>();
    private String photos_link;
    private String vicinity;
    private int user_ratings_total;
    private String icon_link;

    public MyPlace(){

    }

    protected MyPlace(Parcel parcel) {
        this.name = parcel.readString();
        this.rating = parcel.readDouble();
        this.icon_link = parcel.readString();
    }

    public static final Creator<MyPlace> CREATOR = new Creator<MyPlace>() {
        @Override
        public MyPlace createFromParcel(Parcel in) {
            return new MyPlace(in);
        }

        @Override
        public MyPlace[] newArray(int size) {
            return new MyPlace[size];
        }
    };


    public String getPlace_id() {
        return place_id;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public boolean isOpen_now() {
        return open_now;
    }

    public int getPrice_level() {
        return price_level;
    }

    public double getRating() {
        return rating;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public String getIcon_link() {
        return icon_link;
    }

    public String getPhotos_link() {
        return photos_link;
    }

    public String getVicinity() {
        return vicinity;
    }

    public int getUser_ratings_total() {
        return user_ratings_total;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon_link(String icon_link) {
        this.icon_link = icon_link;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public void setPrice_level(int price_level) {
        this.price_level = price_level;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public void setPhotos_link(String photos_link) {
        this.photos_link = photos_link;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public void setUser_ratings_total(int user_ratings_total) {
        this.user_ratings_total = user_ratings_total;
    }

    // Ignore that
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeDouble(getRating());
        dest.writeString(getIcon_link());
    }
}
