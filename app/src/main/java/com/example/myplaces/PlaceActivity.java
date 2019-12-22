package com.example.myplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class PlaceActivity extends AppCompatActivity{

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TextView name;
    TextView street;
    private ArrayList<MyPlace> places = new ArrayList<>();
    private MyPlace place = new MyPlace();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place);

        Intent intent = getIntent();
        places = intent.getParcelableArrayListExtra("Places");
        place = places.get(0);

        name = (TextView) findViewById(R.id.name);
        street = (TextView) findViewById(R.id.street);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),place);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Tabs Icons [HAVE TO IMPORT]
        tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo));
        tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo));
        tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo));
        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo));

        name.setText(place.getName());
        street.setText(place.getVicinity());






    }
}
