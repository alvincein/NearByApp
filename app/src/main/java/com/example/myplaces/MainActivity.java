package com.example.myplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PopUpFilters.PopUpDialogListener {

    private String TAG = "MainActivity";

    // Data request
    private GsonWorker gson = new GsonWorker();

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng myLocation;
    private boolean locationFound = false;

    // Map
    private MapView mMapView;
    private GoogleMap googleMap;
    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";


    // May be useful in the future
    private PlacesClient placesClient;

    // Permissions manager
    private MyPermissions myPermissions;

    // List with places found
    private ArrayList<MyPlace> places = new ArrayList<MyPlace>();

    // Parameters object for search query
    private FilterParameters parameters = new FilterParameters();

    // Pop up parameters window
    private PopUpFilters dialogFragment = new PopUpFilters();

    // Search query (for user)
    private TextView query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check permissions everytime activity starts
        myPermissions = new MyPermissions(this, this);

        // Connect UI
        Button search = findViewById(R.id.srch_button);
        Button favs_btn = findViewById(R.id.favs_btn);
        Button filters_btn = findViewById(R.id.filters_btn);

        query = findViewById(R.id.query_txt);

        query.setText(setQueryText());

        // Get user's current location
        updateCurrentLocation();

        // Create Map
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);


        // Search Button
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Make sure permissions are granted
                MyPermissions.requestLocation(v);
                MyPermissions.requestCoarseLocation(v);

                // Checks if required parameters are fulfilled
                if(parameters.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Παρακαλώ συμπληρώστε τα απαραίτητα φίλτρα αναζήτησης. (*)",Toast.LENGTH_LONG).show();
                }
                else {
                    placesSearch();
                }


            }
        });

        // Open Favourites Activity
        favs_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepare search results activity, pass down "places"
                Intent intent = new Intent(MainActivity.this, FavouritesActivity.class);
                startActivity(intent);
            }

        });

        // Open filter parameters dialog
        filters_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dialog with filter selection
                dialogFragment.show(getSupportFragmentManager(),TAG);
            }
        });

    }

    public void placesSearch(){

        // Find user's location
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            myLocation = new LatLng(gps.getLatitude(),gps.getLongitude());
            locationFound = true;
        } else {
            locationFound = false;
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        // Have to know user's location
        if(locationFound) {

            // Run places search as thread, for performance purposes
            new Thread(() -> {

                if(places != null)
                    places.clear();

                // API CALL
                places = gson.getNearbyPlaces(myLocation, parameters, this);

                // Temporary fake data
                //places = createTempPlaces();

                // If search is successful
                if (places != null && places.size()>0) {

                    // Log them to be sure
                    for (MyPlace place : places) {
                        Log.d(TAG, " place is " + place.getName());
                        Log.d(TAG, " rating is " + place.getRating());
                    }

                    // Clear parameters for next search
                    parameters.clear();
                    // Prepare search results activity, pass down "places"
                    Intent intent = new Intent(MainActivity.this, SearchResults.class);
                    intent.putParcelableArrayListExtra("Places", places);
                    startActivity(intent);

                } else {
                    // Inform user in case of error
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast=Toast.makeText(getApplicationContext(),"Σφάλμα κατά την λήψη τοπθεσιών!",Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    Log.d(TAG, "error");
                }

            }).start();
        }
        else {
            // User's location data missing
            Toast toast=Toast.makeText(getApplicationContext(),"Δεν είναι δυνατή η εύρεση τοποθεσίας της συσκευής.",Toast.LENGTH_LONG);
            toast.show();
        }

    }


    // Prepares a query with search's parameters to display it to user.
    // For debugging purposes
    public String setQueryText(){
        StringBuilder query = new StringBuilder();
        query.append("Απόσταση: " + parameters.getDistance() + " μ.");

        if(parameters.getType() != null)
            query.append(", Τύπος: " + parameters.getType());

        query.append(", Μέγιστη Τιμή: ");
        for(int i=0 ; i<parameters.getMax_price(); i++){
            query.append("€");
        }

        if(parameters.getRankby() != null)
            query.append(", Κατάταξη κατά: "+ parameters.getRankby());

        if(parameters.getKeyword()!=null)
            query.append(", Λέξη Κλειδί: " + parameters.getKeyword());


        return query.toString();
    }



    // Updates user's current location and updates map as well.
    public void updateCurrentLocation(){
        // Set Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Update location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {

                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {

                        // Logic to handle location object
                        double cur_lat = location.getLatitude();
                        double cur_lon = location.getLongitude();
                        myLocation = new LatLng(cur_lat, cur_lon);
                        locationFound = true;

                        // Animate camera to zoom in user's location
                        if(googleMap != null)
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.latitude, myLocation.longitude),  16));

                        // Inform user
                        Toast toast=Toast.makeText(getApplicationContext(),"Η τοποθεσία σας ενημερώθηκε.",Toast.LENGTH_SHORT);
                        toast.show();

                    }
                });
    }

    /** Google Map Stuff **/
    // Prepares map
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    // Triggers when map is ready for display.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

            // Check if required permissions are granted
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                myPermissions.requestLocation(findViewById(android.R.id.content));
                myPermissions.requestCoarseLocation(findViewById(android.R.id.content));
            }
            else {
                googleMap.setMyLocationEnabled(true);
                if(myLocation != null){
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.latitude, myLocation.longitude),  16));
                }
            }


    }


    // Relocate position after resume.
    @Override
    public void onResume() {
        super.onResume();
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            myLocation = new LatLng(gps.getLatitude(),gps.getLongitude());
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                myPermissions.requestLocation(findViewById(android.R.id.content));
                myPermissions.requestCoarseLocation(findViewById(android.R.id.content));
            }
            gps.showSettingsAlert();
        }
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    // Runs when filter selection is done. Returns result parameters.
    @Override
    public void onDialogFinish(FilterParameters parameters) {
        this.parameters = parameters;

        // Print the query for search in simple format. (for user)
        query.setText(setQueryText());

        googleMap.clear();

        // Create a radius circle on map
        googleMap.addCircle(new CircleOptions()
                .center(myLocation)
                .radius(parameters.getDistance())
                .strokeColor(Color.RED)
                .fillColor(0x40ff0000)
                .strokeWidth(3));
        float zoom;

        // Zoom in
        if(parameters.getDistance() >=2500){
            zoom = 12;
        }
        else if(parameters.getDistance() >=1000){
            zoom = 13;
        }
        else if(parameters.getDistance() >=500) {
            zoom = (float) 14.3;
        }
        else zoom = 15;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.latitude, myLocation.longitude),zoom));
    }



    /** NOT USED METHODS **/

    // Not necessary for now
    public void getCurrentPlace(PlacesClient placesClient){
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.newInstance(placeFields);

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();

                    Toast toast=Toast.makeText(getApplicationContext(),
                            String.format("Βρίσκεσαι στην τοποθεσία '%s' με πιθανότητα: %f",
                                    response.getPlaceLikelihoods().get(0).getPlace().getName(),
                                    response.getPlaceLikelihoods().get(0).getLikelihood())
                            ,Toast.LENGTH_LONG);
                    toast.show();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {

                        Log.i(TAG, String.format("place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "place not found: " + apiException.getStatusCode());
                    }
                }
            });
        }
    }



    // FOR TESTING PURPOSES
    // Temp fake data. Will be deleted when app is kinda ready for use.
    public ArrayList<MyPlace> createTempPlaces() {
        ArrayList<MyPlace> temp = new ArrayList<>();
        MyPlace place1 = new MyPlace();
        MyPlace place2 = new MyPlace();
        MyPlace place3 = new MyPlace();

        place1.setName("Τα Γοριλάκια");
        place1.setRating(4.7);
        place1.setOpen_now(true);
        place1.setVicinity("Κάτω Τούμπα");
        place1.setPrice_level(2);
        place1.setUser_ratings_total(100);
        place1.setAvatar_link("https://www.bestofrestaurants.gr/thessaloniki/anatoliki_thessaloniki/sites/restaurants/ta_gorilakia/photogallery/original/03.jpg");
        place1.setLocation(new LatLng(40.608772, 22.979280));
        place1.setPlace_id("000000");

        place2.setName("Γυράδικο");
        place2.setRating(4.8);
        place2.setOpen_now(true);
        place2.setVicinity("Άνω Τούμπα");
        place2.setPrice_level(2);
        place2.setUser_ratings_total(230);
        place2.setAvatar_link("https://www.thelosouvlakia.gr/cms/Uploads/shopImages/gyradiko_thessaloniki_1209_katastima.jpg");
        place2.setLocation(new LatLng(40.615029, 22.976974));
        place2.setPlace_id("111111");

        place3.setName("Ωμέγα");
        place3.setRating(4.1);
        place3.setOpen_now(true);
        place3.setVicinity("Κάτω Τούμπα");
        place3.setPrice_level(2);
        place3.setUser_ratings_total(42);
        place3.setAvatar_link("https://www.tavernoxoros.gr/img/i/rOeC6vBxb-kj");
        place3.setLocation(new LatLng(40.608587, 22.978683 ));
        place3.setPlace_id("222222");

        temp.add(place1);
        temp.add(place2);
        temp.add(place3);

        return temp;

    }
}
