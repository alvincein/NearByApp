package com.example.myplaces;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    private String message;
    private String user;
    private double rating;
    private String photo_link;

    public Review(String message, String user, double rating) {
        this.message = message;
        this.user = user;
        this.rating = rating;
    }

    protected Review(Parcel in) {
        message = in.readString();
        user = in.readString();
        rating = in.readDouble();
        photo_link = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public double getRating() {
        return rating;
    }

    public String getPhoto_link() {
        return photo_link;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setPhoto_link(String photo_link) {
        this.photo_link = photo_link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(user);
        dest.writeDouble(rating);
        dest.writeString(photo_link);
    }
}
