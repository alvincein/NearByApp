package com.example.myplaces;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.util.ArrayList;


public class PlaceActivity extends AppCompatActivity{

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TextView name;
    TextView street;
    private ArrayList<MyPlace> places = new ArrayList<>();
    private MyPlace place = new MyPlace();
    private ImageButton bck_btn;
    private  ImageButton fav_btn;
    private static String TAG = "TEO";
    GsonFileWorker gsonFileWorker = new GsonFileWorker();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place);

        Intent intent = getIntent();
        place = intent.getParcelableExtra("place");

        name = (TextView) findViewById(R.id.name);
        street = (TextView) findViewById(R.id.street);
        fav_btn = (ImageButton) findViewById(R.id.fav_btn);
        bck_btn = (ImageButton) findViewById(R.id.back_btn);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),place);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Tabs Icons [HAVE TO IMPORT]
        tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.navigation_icon_red));
        tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gallery_icon_red));
        tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.review_icon_red));
        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info_icon_red));

        name.setText(place.getName());
        street.setText(place.getVicinity());


        bck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(gsonFileWorker.loadFromFile(getApplicationContext()).contains(place.getPlace_id())){
            fav_btn.setPressed(true);
        }

        fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gsonFileWorker.saveFavPlace(place.getPlace_id(), getApplicationContext());
            }
        });


    }
}
