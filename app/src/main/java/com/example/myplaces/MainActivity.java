package com.example.myplaces;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PopUpFilters.PopUpDialogListener {

    private String TAG = "TEO";

    // Data request
    private GsonWorker gson = new GsonWorker();
    private String API_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng myLocation;
    private Location loc;
    private boolean locationFound = false;

    // Map
    private MapView mMapView;
    private GoogleMap googleMap;
    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";


    // May be useful in the future
    private PlacesClient placesClient;

    // Permissions manager
    private MyPermissions myPermissions;

    private ArrayList<MyPlace> places = new ArrayList<MyPlace>();

    private FilterParameters parameters = new FilterParameters();
    private ArrayList<Boolean> types_bool = new ArrayList<Boolean>();
    private static ArrayList<String> types = new ArrayList<String>() {
        {
            add("restaurant");
            add("bar");
            add("cinema");
            add("cafe");
            add("club");
            add("park");
        }

    };

    private PopUpFilters dialogFragment = new PopUpFilters();

    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check permissions everytime activity starts
        myPermissions = new MyPermissions(this, this);

        Button search = findViewById(R.id.srch_button);
        Button refresh = findViewById(R.id.refresh_button);
        Button filters_btn = findViewById(R.id.filters_btn);

        checkBox1 = findViewById(R.id.checkBox);
        checkBox2 = findViewById(R.id.checkBox6);
        checkBox3 = findViewById(R.id.checkBox7);

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

        // May be useful in futere
            // Initialize the SDK
            Places.initialize(getApplicationContext(), API_KEY);

            // Create a new Places client instance
            placesClient = Places.createClient(this);


        // Search Button
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Make sure permissions are granted
                MyPermissions.requestLocation(v);
                MyPermissions.requestCoarseLocation(v);

                if(!parameters.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Παρακαλώ συμπληρώστε κάποιο φίλτρο αναζήτησης",Toast.LENGTH_LONG).show();
                }
                else {
                    placesSearch();
                }


            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getCurrentPlace(placesClient);
                updateCurrentLocation();
            }

        });

        filters_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dialog with filter selection
                dialogFragment.show(getSupportFragmentManager(),TAG);
            }
        });

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
            }
        });
        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox1.setChecked(false);
                checkBox3.setChecked(false);
            }
        });
        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBox2.setChecked(false);
                checkBox1.setChecked(false);
            }
        });

    }

    public void placesSearch(){
        // Have to know user's location
        if(locationFound) {

            /* categories for google

            StringBuilder s_types = new StringBuilder();

            for(int i=0 ; i<types.size(); i++){
                if(types_bool.get(i)){
                    s_types.append(types.get(i) + " ");
                }

            }

             */

            // Run places search as thread, for performance purposes
            new Thread(() -> {

                // API CALL
                places = gson.getNearbyPlaces(myLocation, parameters, this);

                // FB CALL
                /*
                try {
                    places = gson.getFacebookPlaces(loc,parameters);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */


                // Temporary fake data
                //places = createTempPlaces();


                // If search is successful
                if (places != null && places.size()>0) {

                    // Log them to be sure
                    for (MyPlace place : places) {
                        Log.d(TAG, " place is " + place.getName());
                        Log.d(TAG, " rating is " + place.getRating());
                    }

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
            Toast toast=Toast.makeText(getApplicationContext(),"Δεν είναι δυνατή η εύρεση τοποθεσίας της συσκευής",Toast.LENGTH_LONG);
            toast.show();
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
        place1.setPhotos_link("https://www.bestofrestaurants.gr/thessaloniki/anatoliki_thessaloniki/sites/restaurants/ta_gorilakia/photogallery/original/03.jpg");
        place1.setLocation(new LatLng(40.608772, 22.979280));
        place1.setPlace_id("000000");

        place2.setName("Γυράδικο");
        place2.setRating(4.8);
        place2.setOpen_now(true);
        place2.setVicinity("Άνω Τούμπα");
        place2.setPrice_level(2);
        place2.setUser_ratings_total(230);
        place2.setPhotos_link("https://www.thelosouvlakia.gr/cms/Uploads/shopImages/gyradiko_thessaloniki_1209_katastima.jpg");
        place2.setLocation(new LatLng(40.615029, 22.976974));
        place2.setPlace_id("111111");

        place3.setName("Ωμέγα");
        place3.setRating(4.1);
        place3.setOpen_now(true);
        place3.setVicinity("Κάτω Τούμπα");
        place3.setPrice_level(2);
        place3.setUser_ratings_total(42);
        place3.setPhotos_link("https://www.tavernoxoros.gr/img/i/rOeC6vBxb-kj");
        place3.setLocation(new LatLng(40.608587, 22.978683 ));
        place3.setPlace_id("222222");

        temp.add(place1);
        temp.add(place2);
        temp.add(place3);

        return temp;

    }


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

    public void updateCurrentLocation(){
        // Set Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Update location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {

                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        loc = location;

                        // Logic to handle location object
                        double cur_lat = location.getLatitude();
                        double cur_lon = location.getLongitude();
                        myLocation = new LatLng(cur_lat, cur_lon);
                        locationFound = true;

                        // Animate camera to zoom in user's location
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

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                myPermissions.requestLocation(getCurrentFocus());
                myPermissions.requestCoarseLocation(getCurrentFocus());
                return;
            }

            googleMap.setMyLocationEnabled(true);
            if(myLocation != null){
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.latitude, myLocation.longitude),  16));
            }
    }


    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public void onDialogFinish(FilterParameters parameters) {
        this.parameters = parameters;
    }
}
