package com.example.myplaces;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;

public class MyPlace implements Parcelable {

    private String place_id;
    private String name;
    private LatLng location;
    private boolean open_now;
    private int price_level;
    private Double rating;
    private ArrayList<String> types = new ArrayList<String>();
    private String avatar_link;
    private ArrayList<String> photos_links = new ArrayList<>();
    private String vicinity;
    private int user_ratings_total;
    private String icon_link;
    private String contact_number;
    private String email;
    private Double distance;
    private ArrayList<Review> reviews = new ArrayList<>();
    private ArrayList<String> open_hours = new ArrayList<>();
    private String facebook_link;
    private String description;
    private String site_link;

    public MyPlace(){
        this.rating = 0.0;
    }

    // Parcel method used for passing objects between activities via intents.
    // This method reads the object.
    protected MyPlace(Parcel parcel) {
        this.name = parcel.readString();
        this.rating = parcel.readDouble();
        this.icon_link = parcel.readString();
        this.place_id = parcel.readString();
        this.open_now = Boolean.parseBoolean(parcel.readString());
        this.price_level = parcel.readInt();
        this.types = parcel.readArrayList(null);
        this.avatar_link = parcel.readString();
        this.vicinity = parcel.readString();
        this.user_ratings_total = parcel.readInt();
        this.icon_link = parcel.readString();
        double lat = parcel.readDouble();
        double lon = parcel.readDouble();
        this.contact_number = parcel.readString();
        this.email = parcel.readString();
        this.distance = parcel.readDouble();
        this.photos_links = parcel.readArrayList(null);
        this.open_hours = parcel.readArrayList(null);


        this.location = new LatLng(lat,lon);
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

    // Getters

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

    public String getAvatar_link() {
        return avatar_link;
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

    public Double getDistance() {
        return distance;
    }

    public ArrayList<String> getPhotos_links() {
        return photos_links;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public ArrayList<String> getOpen_hours() {
        return open_hours;
    }

    public String getFacebook_link() {
        return facebook_link;
    }

    public String getDescription() {
        return description;
    }

    public String getSite_link() {
        return site_link;
    }

    // Setters

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

    public void setAvatar_link(String avatar_link) {
        this.avatar_link = avatar_link;
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

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setPhotos_links(ArrayList<String> photos_links) {
        this.photos_links = photos_links;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }

    public void setOpen_hours(ArrayList<String> open_hours) {
        this.open_hours = open_hours;
    }

    public void setFacebook_link(String facebook_link) {
        this.facebook_link = facebook_link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSite_link(String site_link) {
        this.site_link = site_link;
    }

    // Ignore that [required]
    @Override
    public int describeContents() {
        return 0;
    }


    // Converts Google's Place Object to MyPlace object
    public void convertPlaceToMyPlace(Place place, LatLng userCoordinates){
        this.name = place.getName();
        this.rating = place.getRating();
        this.contact_number = place.getPhoneNumber();
        this.location = place.getLatLng();
        this.place_id = place.getId();
        this.vicinity = place.getAddress();
        // Hardcoded
        this.open_now = true;
        this.user_ratings_total = place.getUserRatingsTotal();
        if(place.getPriceLevel() != null)
            this.price_level = place.getPriceLevel();

        // Find Distance
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(userCoordinates.latitude);
        startPoint.setLongitude(userCoordinates.longitude);

        Location endPoint = new Location("locationB");
        endPoint.setLatitude(getLocation().latitude);
        endPoint.setLongitude(getLocation().longitude);

        double distance=startPoint.distanceTo(endPoint);
        this.distance = distance;
    }

    // This method writes the object. So another activity can read it afterwards
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeDouble(getRating());
        dest.writeString(getIcon_link());
        dest.writeString(getPlace_id());
        dest.writeString(String.valueOf(isOpen_now()));
        dest.writeInt(getPrice_level());
        dest.writeList(getTypes());
        dest.writeString(getAvatar_link());
        dest.writeString(getVicinity());
        dest.writeInt(getUser_ratings_total());
        dest.writeString(getIcon_link());
        dest.writeDouble(getLocation().latitude);
        dest.writeDouble(getLocation().longitude);
        dest.writeString(getContact_number());
        dest.writeString(getEmail());
        dest.writeDouble(getDistance());
        dest.writeList(getPhotos_links());
        dest.writeList(getOpen_hours());
    }

}
