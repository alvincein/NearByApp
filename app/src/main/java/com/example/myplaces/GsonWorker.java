package com.example.myplaces;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.places.PlaceManager;
import com.facebook.places.model.PlaceFields;
import com.facebook.places.model.PlaceSearchRequestParams;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GsonWorker {

    private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String SINGLE_PLACE_URL = "https://maps.googleapis.com/maps/api/place/details/json?place_id=";
    private static final String PLACES_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";
    private static final String FREE_PLACES_KEY = "AIzaSyADJxQj5W1mseer-bn2hdL362JnlfsS1-Y";

    private static final String FACEBOOK_PLACES_API_KEY = "e4bccf3d79fb53014eca8e85098539b0";

    private static final String requestsBoundary = "OVER_QUERY_LIMIT";

    private String TAG = "GsonWorkerTAG";

    private Gson gson = new GsonBuilder().create();



    // Nearby places search with Google Places API

    // Nearby places call. Needs user's location, parameters (filters) and an Activity's context.
    // Returns a list of nearby places of MyPlace type.

    public ArrayList<MyPlace> getNearbyPlaces(LatLng userCoordinates, FilterParameters parameters, Context context) {

        ArrayList<MyPlace> places = new ArrayList<MyPlace>();

        // Return empty if parameters are null
        // That's almost impossible cause we check them before calling this method. (But safety first)
        if (parameters == null)
            return null;

        // Get JSON results
        String json = this.getNearbyPlacesJSON(userCoordinates, parameters);

        // Turn string to JSONObject if there's a response on API call.
        if (json != null) {
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

            // Check the status response
            String status = jsonObject.getAsJsonPrimitive("status").getAsString();
            if (status.equals("OK")) {

                for(int i =0 ; jsonObject.getAsJsonArray("results").size()> i ; i++) {

                    MyPlace place = new MyPlace();

                    // Get the every element of the "results" array
                    JsonObject result = jsonObject.getAsJsonArray("results").get(i).getAsJsonObject();

                    // Turn JSONObject to MyPlace Object
                    place = parsePlace(result, userCoordinates);

                    // Add place to list
                    places.add(place);

                }
            } else if(status.equals(requestsBoundary)){

                return null;
            }
        } else  {
            return null;
        }
        // Return nearby places list.
        return places;
    }

    // Prepares a request for nearby places based on specific parameters passed by user.
    // Returns a string that represents a JSON Object ( an array of place results)

    public String getNearbyPlacesJSON(LatLng userCoordinates, FilterParameters parameters) {

        HttpURLConnection conn = null;

        // Create a StringBuilder for the final URL
        StringBuilder finalURL = new StringBuilder();
        // Append API URL
        finalURL.append(PLACES_URL);
        // Append location
        finalURL.append("location=" + userCoordinates.latitude + "," + userCoordinates.longitude);
        // Language
        finalURL.append("&language=el");
        // Get Reviews
        finalURL.append("&fields=review");

        // PARAMETERS
        // Append Radius or Rankby
        if(parameters.getRankby() != "distance") {
            finalURL.append("&radius=" + parameters.getDistance());
        }
        else
            finalURL.append("&rankby=" + parameters.getRankby());
        // Append parameters
        finalURL.append("&type=" + parameters.getType());
        // Append keyword
        if(parameters.getKeyword() != null)
            finalURL.append("&keyword=" + parameters.getKeyword());
        // Append Max Price
        if(!parameters.isPark() && !parameters.isCinema())
            finalURL.append("&maxprice=" + parameters.getMax_price());


        // Append API KEY
        finalURL.append("&key=" + PLACES_KEY);

        Log.d(TAG,finalURL.toString());

        // Create a StringBuilder to store the JSON string
        StringBuilder result = new StringBuilder();

        try {
            // Make a connection with the API
            URL url = new URL(finalURL.toString());
            conn = (HttpURLConnection) url.openConnection();

            // Begin streaming the JSON
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            // Usually indicates a lack of Internet connection
            return null;
        } finally {
            // Close connection
            if (conn != null)
                conn.disconnect();
        }

        // Return the JSON string
        return result.toString();
    }

    // Get single Place details by passing its id. ( Used for favourites section )
    // This method needs user's location to calculate the distance.


    public MyPlace getPlace(LatLng userCoordinates, String place_id, Context context) {

        MyPlace myPlace = new MyPlace();

        Log.d(TAG,"Searching for place with id : " + place_id);

        HttpURLConnection conn = null;
        String line;

        // Create a StringBuilder for the final URL
        StringBuilder finalURL = new StringBuilder();
        // Append API URL
        finalURL.append(SINGLE_PLACE_URL);
        // Append Place ID
        finalURL.append(place_id);
        // Language
        finalURL.append("&language=el");
        // Append API KEY
        finalURL.append("&key=" + PLACES_KEY);

        Log.d(TAG,finalURL.toString());

        // Create a StringBuilder to store the JSON string
        StringBuilder result = new StringBuilder();
        try {
            // Make a connection with the API
            URL url = new URL(finalURL.toString());
            conn = (HttpURLConnection) url.openConnection();

            // Begin streaming the JSON
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        } catch (Exception e) {
            // Usually indicates a lack of Internet connection
            Log.d(TAG,e.toString());
            Log.d(TAG,"API call error. Is internet connection enabled?");
            return null;
        } finally {
            // Close connection
            if (conn != null)
                conn.disconnect();
        }

        String json = result.toString();

        if (json != null) {
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

            // Check the status response
            String status = jsonObject.getAsJsonPrimitive("status").getAsString();
            if (status.equals("OK")) {


                    MyPlace place = new MyPlace();

                    // Get the first element of the "results" array, the nearest store
                    JsonObject jsonresult = jsonObject.getAsJsonObject("result");

                    myPlace = parsePlace(jsonresult, userCoordinates);


            } else if(status.equals(requestsBoundary)) {

                Log.d(TAG, "Requests boundary reached.");
                return null;
            } else {
                Log.d(TAG, status);
            }
        } else  {
            Log.d(TAG,"API returned null");
            return null;
        }
        Log.d(TAG,myPlace.toString());
        return myPlace;

    }


    public Place getFromPlace(String place_id, Context context, List<Place.Field> fields) {

        final Place[] place = new Place[1];

        PlacesClient placesClient = initPlacesClient(context);

        // Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(place_id, fields);

        try {
            Tasks.await(placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                place[0] = response.getPlace();
            }));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


            if(place[0] == null){
                return null;
            }
            else {
                return place[0];
            }

    }


    // Returns a MyPlace object obtained by facebook API.
    public MyPlace getFacebookPlace(Location location, String placeName) throws JSONException {

        MyPlace fb_place = new MyPlace();

        FacebookSdk.setClientToken(FACEBOOK_PLACES_API_KEY);
        PlaceSearchRequestParams.Builder builder =
                new PlaceSearchRequestParams.Builder();

        builder.setSearchText(placeName); // search via name
        builder.setLimit(1); // restricted to 1 result
        //builder.setDistance(5000); // 5,000 m. max distance.
        builder.addField(PlaceFields.RESTAURANT_SPECIALTIES); // not used, but could be useful in future
        builder.addField(PlaceFields.NAME); // place's name
        builder.addField(PlaceFields.LINK); // place's fb page link
        builder.addField(PlaceFields.WEBSITE); // place's site link
        builder.addField(PlaceFields.DESCRIPTION); // place's fb description

        GraphRequest request =
                PlaceManager.newPlaceSearchRequestForLocation(builder.build(), location);


        GraphResponse response = request.executeAndWait();
        Log.d(TAG,response.toString());
        if(response.getError() != null){
            Log.d(TAG,"Error with status : " + response.getError().getErrorMessage());
        }
        else {
            JSONObject data = response.getJSONObject().getJSONArray("data").getJSONObject(0);
            if(data.has("link"))
                fb_place.setFacebook_link(data.getString("link"));
            if(data.has("website"))
                fb_place.setSite_link(data.getString("website"));
            if(data.has("description"))
                fb_place.setDescription(data.getString("description"));

        }
        return fb_place;

    }


    // Nearby search with facebook API , not used yet
    public ArrayList<MyPlace> getFacebookPlaces(Location location, FilterParameters parameters) throws JSONException {

        ArrayList<MyPlace> places = new ArrayList<>();

        FacebookSdk.setClientToken(FACEBOOK_PLACES_API_KEY);
        PlaceSearchRequestParams.Builder builder =
                new PlaceSearchRequestParams.Builder();

        //builder.setSearchText(setQuery(parameters));
        builder.setDistance(parameters.getDistance()); // 1,000 m. max distance.
        builder.setLimit(50);
        builder.addCategory("FOOD_BEVERAGE");
        builder.addField(PlaceFields.NAME);
        builder.addField(PlaceFields.DESCRIPTION);
        builder.addField(PlaceFields.PRICE_RANGE);
        builder.addField(PlaceFields.PHONE);
        builder.addField(PlaceFields.SINGLE_LINE_ADDRESS);
        builder.addField(PlaceFields.OVERALL_STAR_RATING);
        builder.addField(PlaceFields.COVER);
        builder.addField(PlaceFields.RATING_COUNT);

        GraphRequest request =
                PlaceManager.newPlaceSearchRequestForLocation(builder.build(), location);


        Log.d(TAG,request.executeAndWait().getJSONObject().toString());

        return places;
    }


    private PlacesClient initPlacesClient(Context context) {

        // Initialize the SDK
        Places.initialize(context, PLACES_KEY);

        PlacesClient placesClient;

        // Create a new Places client instance
        placesClient = Places.createClient(context);

        return placesClient;
    }


    /** Gets a JSONObject
     * parses all its data
     * and returns a MyPlace object.
     * @param result
     * @return MyPlace Object
     */
    private MyPlace parsePlace(JsonObject result, LatLng userCoordinates) {

        MyPlace place = new MyPlace();


        // Get vicinity
        String vicinity = null;
        if(result.has("vicinity"))
            vicinity = result.getAsJsonPrimitive("vicinity").getAsString();

        // We need to move in geometry -> location to get the coordinates
        Double lat = result.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
        Double lng = result.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();

        // place id
        place.setPlace_id(result.get("place_id").getAsString());

        // Open hours (Boolean)
        if(result.has("opening_hours")){

            JsonObject opening_hours = result.get("opening_hours").getAsJsonObject();

            place.setOpen_now(opening_hours.get("open_now").getAsBoolean());
            if(opening_hours.has("weekday_text")){
                ArrayList<String> temp_weekday = new ArrayList<>();

                JsonArray weekday = opening_hours.get("weekday_text").getAsJsonArray();
                for(int i=0 ; i< weekday.size(); i++){
                    temp_weekday.add(weekday.get(i).getAsString());
                }

                place.setOpen_hours(temp_weekday);

                Log.d(TAG,place.getOpen_hours().toString());
            }

        }

        // Place name
        place.setName(result.getAsJsonPrimitive("name").getAsString());

        // Place rating
        Log.d(TAG,result.toString());
        if (result.has("rating"))
            place.setRating(result.get("rating").getAsDouble());

        // Create the Location object and add it to the list
        place.setLocation(new LatLng(lat,lng));
        place.setVicinity(vicinity);

        // Set place types
        ArrayList<String> types = new ArrayList<String>();
        for(int j=0 ; j<result.getAsJsonArray("types").size(); j++){
            types.add(result.getAsJsonArray("types").get(j).getAsString());
        }
        place.setTypes(types);

        // Price level
        if(result.has("price_level"))
            place.setPrice_level(result.get("price_level").getAsInt());

        // User's rating
        if(result.has("user_ratings_total"))
            place.setUser_ratings_total(result.get("user_ratings_total").getAsInt());

        if(result.has("formatted_phone_number")){
            place.setContact_number(result.get("formatted_phone_number").getAsString());
        }

        Log.d(TAG,result.getAsJsonObject().toString());
        // Photo's link
        if(result.has("photos")) {
            place.setAvatar_link(result.getAsJsonArray("photos").get(0).getAsJsonObject().get("photo_reference").getAsString());
            ArrayList<String> photos = new ArrayList<>();
            if(result.getAsJsonArray("photos").isJsonArray()) {
                for (int i = 0; i < result.getAsJsonArray("photos").size(); i++) {
                    String photo_reference = null;
                    photo_reference = result.getAsJsonArray("photos").get(i).getAsJsonObject().get("photo_reference").getAsString();
                    photos.add(photo_reference);
                }
                place.setPhotos_links(photos);
            }
        }

        // Icon link
        if(result.has("icon")){
            place.setIcon_link(result.get("icon").getAsString());
        }

        // Reviews
        Log.d(TAG + ": Reviews Section", String.valueOf(result.has("reviews")));
        if(result.has("reviews")){
            ArrayList<Review> reviews = new ArrayList<>();
            for(int i=0; i<result.getAsJsonArray("reviews").size(); i++){
                JsonObject row = result.getAsJsonArray("reviews").get(i).getAsJsonObject();
                String message = row.get("text").getAsString();
                String user = row.get("author_name").getAsString();
                double rating = row.get("rating").getAsDouble();

                Review review = new Review(message,user,rating);
                review.setPhoto_link(row.get("profile_photo_url").getAsString());
                reviews.add(review);
            }
            Log.d(TAG + ": Reviews Section",reviews.toString());
            place.setReviews(reviews);
        }

        Log.d(TAG,result.toString());

        // Distance from user's location
        Location startPoint=new Location("locationA");
        startPoint.setLatitude(userCoordinates.latitude);
        startPoint.setLongitude(userCoordinates.longitude);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(place.getLocation().latitude);
        endPoint.setLongitude(place.getLocation().longitude);

        double distance=startPoint.distanceTo(endPoint);
        place.setDistance(distance);

        return place;

    }




    /**
     * Returns UTF-8 encoded text for use in URLs
     *
     * @param str The string to encode
     * @return The encoded string
     */
    private String encode(String str) {
        try {
            // Spaces are converted to '+', we need "%20" instead
            str = URLEncoder.encode(str, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            // Never occurs, totally useless but required
        }
        return str;
    }
}
