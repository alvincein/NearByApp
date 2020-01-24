package com.example.myplaces;

import java.util.ArrayList;

public class FilterParameters {

    private boolean restaurant = false;
    private boolean bar = false;
    private boolean cinema = false;
    private boolean cafe = false;
    private boolean club = false;
    private boolean park = false;
    private String keyword = null;
    private int max_price = 4;
    private String rankby = null;

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


    // KEYWORD
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    // MAX PRICE
    public int getMax_price() {
        return max_price;
    }

    public void setMax_price(int max_price) {
        this.max_price = max_price;
    }


    // RANKBY
    public String getRankby() {
        return rankby;
    }

    public void setRankby(String rankby) {
        this.rankby = rankby;
    }

    // DISTANCE
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    // IS EMPTY | CHECK IF REQUIRED PARAMETERS ARE FULFILLED
    public boolean isEmpty(){
        if(!club && !restaurant && !cafe && !cinema && !park && !bar){
            return true;
        }
        else return false;
    }

    // Returns a String that describes a parameter type
    public String getType(){

        String type = null;

        if(this.isRestaurant())
            type = this.getRestaurantType();
        if(this.isBar())
            type = this.getBarType();
        if(this.isCafe())
            type = this.getCafeType();
        if(this.isCinema())
            type = this.getCinemaType();
        if(this.isClub())
            type = this.getClubType();
        if(this.isPark())
            type = this.getParkType();

        return type;
    }

    // Reseting parameters
    public void clear(){
        this.club = false;
        this.cinema = false;
        this.cafe = false;
        this.park = false;
        this.restaurant = false;
        this.bar = false;
        types.clear();
    }
}
