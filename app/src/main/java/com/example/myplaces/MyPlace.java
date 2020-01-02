package com.example.myplaces;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class MyPlace implements Parcelable {

    private String place_id;
    private String name;
    private LatLng location;
    private boolean open_now;
    private int price_level;
    private Double rating;
    private ArrayList<String> types = new ArrayList<String>();
    private String photos_link;
    private String vicinity;
    private int user_ratings_total;
    private String icon_link;
    private String contact_number;
    private String email;

    public MyPlace(){
        this.rating = 0.0;

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected MyPlace(Parcel parcel) {
        this.name = parcel.readString();
        this.rating = parcel.readDouble();
        this.icon_link = parcel.readString();
        this.place_id = parcel.readString();
        this.open_now = parcel.readBoolean();
        this.price_level = parcel.readInt();
        this.types = parcel.readArrayList(null);
        this.photos_link = parcel.readString();
        this.vicinity = parcel.readString();
        this.user_ratings_total = parcel.readInt();
        this.icon_link = parcel.readString();
        double lat = parcel.readDouble();
        double lon = parcel.readDouble();
        this.location = new LatLng(lat,lon);
        this.contact_number = parcel.readString();
        this.email = parcel.readString();
    }

    public static final Creator<MyPlace> CREATOR = new Creator<MyPlace>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
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

    public Double getRating() {
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

    public String getContact_number() {
        return contact_number;
    }

    public String getEmail() {
        return email;
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

    public void setRating(Double rating) {
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

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Ignore that
    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeDouble(getRating());
        dest.writeString(getIcon_link());
        dest.writeString(getPlace_id());
        dest.writeBoolean(isOpen_now());
        dest.writeInt(getPrice_level());
        dest.writeList(getTypes());
        dest.writeString(getPhotos_link());
        dest.writeString(getVicinity());
        dest.writeInt(getUser_ratings_total());
        dest.writeString(getIcon_link());
        dest.writeDouble(getLocation().latitude);
        dest.writeDouble(getLocation().longitude);
        dest.writeString(getContact_number());
        dest.writeString(getEmail());
    }
}
