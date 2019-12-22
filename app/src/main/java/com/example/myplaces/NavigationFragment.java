package com.example.myplaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NavigationFragment extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";
    private MyPlace place = new MyPlace();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        place = getArguments().getParcelable("place");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.navigation_map, container, false);

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) rootView.findViewById(R.id.mapView2);
        mMapView.onCreate(mapViewBundle);
        mMapView.onResume();


        mMapView.getMapAsync(this);

        return rootView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setMyLocationEnabled(true);
        Log.d("TEO",place.getName());
        googleMap.addMarker(new MarkerOptions().position(place.getLocation()).title(place.getName()).snippet("-"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLocation(),  16));

    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
