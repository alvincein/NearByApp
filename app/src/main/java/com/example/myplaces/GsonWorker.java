package com.example.myplaces;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.places.PlaceManager;
import com.facebook.places.model.PlaceFields;
import com.facebook.places.model.PlaceSearchRequestParams;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class GsonWorker {

    private static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String PLACES_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";

    private static final String FACEBOOK_PLACES_API_KEY = "e4bccf3d79fb53014eca8e85098539b0";

    private static final String requestsBoundary = "OVER_QUERY_LIMIT";

    private String link = "";

    private String TAG = "TEO";

    private Gson gson = new GsonBuilder().create();
    private String json;

    public String getPlacesJSON( LatLng userCoordinates, FilterParameters parameters) {
        HttpURLConnection conn = null;
        String line;

        // Create a StringBuilder for the final URL
        StringBuilder finalURL = new StringBuilder();
        // Append API URL
        finalURL.append(PLACES_URL);
        // Append location
        finalURL.append("location=" + userCoordinates.latitude + "," + userCoordinates.longitude);
        // Append Radius
        finalURL.append("&radius=" + parameters.getDistance());
        // Append parameters
        finalURL.append("&type=" + parameters.getTypesList().get(0));
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
            return null;
        } finally {
            // Close connection
            if (conn != null)
                conn.disconnect();
        }

        // Return the JSON string
        return result.toString();
    }


    public ArrayList<MyPlace> getNearbyPlaces(LatLng userCoordinates, FilterParameters parameters, Context context) {

        ArrayList<MyPlace> places = new ArrayList<MyPlace>();


        // Return empty if stores are null
        if (parameters == null)
            return null;

        Log.d(TAG, String.valueOf(userCoordinates.latitude));

            // Get JSON results for our current chain
            json = this.getPlacesJSON(userCoordinates, parameters);

            // Retrieve lat, lng, vicinity if nothing ugly happened
            if (json != null) {
                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

                // Check the status response
                String status = jsonObject.getAsJsonPrimitive("status").getAsString();
                if (status.equals("OK")) {

                    for(int i =0 ; jsonObject.getAsJsonArray("results").size()> i ; i++) {

                        MyPlace place = new MyPlace();

                        // Get the first element of the "results" array, the nearest store
                        JsonObject result = jsonObject.getAsJsonArray("results").get(i).getAsJsonObject();

                        // Get vicinity
                        String vicinityResponse = result.getAsJsonPrimitive("vicinity").getAsString();

                        // Remove city from the response
                        String vicinity = vicinityResponse;
                        if (vicinity.contains(","))
                            vicinity = vicinity.substring(0, vicinity.indexOf(","));

                        // We need to move in geometry -> location to get the coordinates
                        Double lat = result.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
                        Double lng = result.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();

                        // place id
                        place.setPlace_id(result.get("place_id").getAsString());

                        // Open hours (Boolean)
                        if(result.get("opening_hours")!=null)
                            place.setOpen_now(result.get("opening_hours").getAsJsonObject().get("open_now").getAsBoolean());

                        // place name
                        place.setName(result.getAsJsonPrimitive("name").getAsString());

                        // place rating
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

                        // Photo's link
                        if(result.has("photo_reference"))
                            place.setPhotos_link(result.get("photo_reference").getAsString());

                        // Icon link
                        if(result.has("icon")){
                            place.setIcon_link(result.get("icon").getAsString());
                        }


                        // Add place to list
                        places.add(place);

                    }
                } else if(status.equals(requestsBoundary)){

                    return null;
                }
            } else  {
                return null;
            }
        return places;
    }


    public ArrayList<String> getFacebookLink(Location location, String placeName) throws JSONException {

        ArrayList<String> links = new ArrayList<>();

        FacebookSdk.setClientToken(FACEBOOK_PLACES_API_KEY);
        PlaceSearchRequestParams.Builder builder =
                new PlaceSearchRequestParams.Builder();

        builder.setSearchText(placeName);
        builder.setDistance(1000); // 1,000 m. max distance.
        builder.setLimit(30);
        builder.addField(PlaceFields.NAME);
        builder.addField(PlaceFields.LINK);
        builder.addField(PlaceFields.WEBSITE);

        GraphRequest request =
                PlaceManager.newPlaceSearchRequestForLocation(builder.build(), location);

        //Log.d(TAG,request.executeAndWait().getJSONObject().getJSONArray("data").getJSONObject(0).toString());

        GraphResponse response = request.executeAndWait();
        Log.d(TAG,response.toString());
        if(response.getError() != null){
            Log.d(TAG,"Error with status : " + response.getError().getErrorMessage());
        }
        else {
            JSONObject data = response.getJSONObject().getJSONArray("data").getJSONObject(0);
            links.add(data.getString("link"));
            if(data.has("website"))
                links.add(data.getString("website"));
        }
        return links;

    }


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

    private String setQuery(FilterParameters parameters){

        String query;

        query = parameters.getTypesList().toString();

        return query;
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
