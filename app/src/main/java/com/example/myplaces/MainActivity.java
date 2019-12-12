package com.example.myplaces;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String API_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";
    private String TAG = "TEO";


    private FusedLocationProviderClient fusedLocationClient;

    private GsonWorker gson = new GsonWorker();

    private MapView mMapView;

    private PlacesClient placesClient;

    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";

    private double cur_lat = 0;
    private double cur_lon = 0;

    private LatLng myLocation;
    private boolean locationFound = false;

    private ArrayList<MyPlace> places = new ArrayList<MyPlace>();

    private GoogleMap googleMap;

    private CheckBox restaurant;
    private CheckBox bar;
    private CheckBox coffee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyPermissions myPermissions = new MyPermissions(this, this);

        Button btn = findViewById(R.id.srch_button);

        Button btn2 = findViewById(R.id.refresh_button);

        restaurant = findViewById(R.id.checkBox);
        bar = findViewById(R.id.checkBox6);
        coffee = findViewById(R.id.checkBox7);

        updateCurrentLocation();

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);




        // Initialize the SDK
        Places.initialize(getApplicationContext(), API_KEY);

        // Create a new Places client instance
        placesClient = Places.createClient(this);



        // Search Button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPermissions.requestLocation(v);
                MyPermissions.requestCoarseLocation(v);

                printPlaces();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getCurrentPlace(placesClient);
                updateCurrentLocation();
            }

        });

        restaurant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bar.setChecked(false);
                coffee.setChecked(false);
            }
        });
        bar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                restaurant.setChecked(false);
                coffee.setChecked(false);
            }
        });
        coffee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bar.setChecked(false);
                restaurant.setChecked(false);
            }
        });

    }

    public void printPlaces(){
        if(locationFound) {
            Toast toast2 =Toast.makeText(getApplicationContext(),"Αναζήτηση τοποθεσιών γύρω σας... \n Παρακαλώ περιμένετε",Toast.LENGTH_LONG);
            toast2.show();
            new Thread(() -> {
                String type = null;
                if(restaurant.isChecked())
                    type = "restaurant";
                else if(bar.isChecked())
                    type = "bar";
                else if(coffee.isChecked())
                    type = "cafe";

                places = gson.getNearbyStores(myLocation, type);
                if (places != null && places.size()>0) {
                    //Get best store name
                    Log.d(TAG, String.valueOf(places.size()));
                    for (MyPlace place : places) {
                        Log.d(TAG, " place is " + place.getName());
                        Log.d(TAG, " rating is " + place.getRating());
                    }
                    //run on UI thread cause its a TextView
                    //runOnUiThread(() -> bestSupermarket.setText(bestStore));

                    Intent intent = new Intent(MainActivity.this, SearchResults.class);
                    intent.putParcelableArrayListExtra("Places", places);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "error");
                }
            }).start();
        }
        else {
            Toast toast=Toast.makeText(getApplicationContext(),"Δεν είναι δυνατή η εύρεση τοποθεσίας της συσκευής",Toast.LENGTH_LONG);
            toast.show();
        }

    }

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            cur_lat = location.getLatitude();
                            cur_lon = location.getLongitude();
                            myLocation = new LatLng(cur_lat, cur_lon);
                            locationFound = true;
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.latitude, myLocation.longitude),  16));
                            Toast toast=Toast.makeText(getApplicationContext(),"Η τοποθεσία σας ενημερώθηκε.",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
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
}
