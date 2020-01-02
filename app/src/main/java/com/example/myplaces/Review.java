package com.example.myplaces;

public class Review {

    private String message;
    private String user;
    private double rating;
    private String photo_link;

    public Review(String message, String user, double rating) {
        this.message = message;
        this.user = user;
        this.rating = rating;
    }

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
}
