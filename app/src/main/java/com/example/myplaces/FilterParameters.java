package com.example.myplaces;

import java.util.ArrayList;

public class FilterParameters {

    private boolean restaurant = false;
    private boolean bar = false;
    private boolean cinema = false;
    private boolean cafe = false;
    private boolean club = false;
    private boolean park = false;

    private ArrayList<String> types = new ArrayList<>();

    private static int DEFAULT_DISTANCE = 500;

    private int distance = DEFAULT_DISTANCE;


    // RESTAURANT
    public boolean isRestaurant() {
        return restaurant;
    }

    public String getRestaurantType(){
        return "restaurant";
    }

    public void setRestaurant(boolean restaurant) {
        this.restaurant = restaurant;
    }


    // BAR
    public boolean isBar() {
        return bar;
    }

    public String getBarType(){
        return "bar";
    }

    public void setBar(boolean bar) {
        this.bar = bar;
    }


    // CINEMA
    public boolean isCinema() {
        return cinema;
    }

    public String getCinemaType(){
        return "movie_theater";
    }

    public void setCinema(boolean cinema) {
        this.cinema = cinema;
    }


    // CAFE
    public boolean isCafe() {
        return cafe;
    }

    public String getCafeType(){
        return "cafe";
    }

    public void setCafe(boolean cafe) {
        this.cafe = cafe;
    }


    // CLUB
    public boolean isClub() {
        return club;
    }

    public String getClubType(){
        return "night_club";
    }

    public void setClub(boolean club) {
        this.club = club;
    }


    // PARK
    public boolean isPark() {
        return park;
    }

    public String getParkType(){
        return "park";
    }

    public void setPark(boolean park) {
        this.park = park;
    }


    // DISTANCE
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    // IS EMPTY
    public boolean isEmpty(){
        if(!club && !restaurant && !cafe && !cinema && !park && !bar){
            return false;
        }
        else return true;
    }

    public ArrayList<String> getTypesList(){

        if(this.isRestaurant())
            types.add(this.getRestaurantType());
        if(this.isBar())
            types.add(this.getBarType());
        if(this.isCafe())
            types.add(this.getCafeType());
        if(this.isCinema())
            types.add(this.getCinemaType());
        if(this.isClub())
            types.add(this.getClubType());
        if(this.isPark())
            types.add(this.getParkType());

        return this.types;
    }
}
