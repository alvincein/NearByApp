package com.example.myplaces;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class PlaceActivity extends AppCompatActivity{

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    ProgressBar loading;
    TextView name;
    TextView street;
    private ArrayList<MyPlace> places = new ArrayList<>();
    private MyPlace place = new MyPlace();
    private MyPlace myPlace = new MyPlace();
    private ImageButton bck_btn;
    private ToggleButton fav_btn;
    private TextView distance_text;
    private ImageView isOpen_icon;
    private ImageView icon1;
    private ImageView icon2;
    private ImageView icon3;
    private TextView isOpen_text;
    private TextView rating;
    private LatLng location;
    private static String TAG = "TEO";
    GsonFileWorker gsonFileWorker = new GsonFileWorker();
    GsonWorker gsonWorker = new GsonWorker();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place);

        Intent intent = getIntent();
        place = intent.getParcelableExtra("place");

        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            location = new LatLng(gps.getLatitude(),gps.getLongitude());
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        name = (TextView) findViewById(R.id.name);
        name.setSelected(true);
        street = (TextView) findViewById(R.id.street);
        fav_btn = (ToggleButton) findViewById(R.id.fav_btn);
        bck_btn = (ImageButton) findViewById(R.id.back_btn);
        distance_text = (TextView) findViewById(R.id.distance_text);
        isOpen_icon = (ImageView) findViewById(R.id.isOpen_icon);
        isOpen_text = (TextView) findViewById(R.id.isOpen_text);
        rating = (TextView) findViewById(R.id.rating);
        loading = (ProgressBar) findViewById(R.id.loading_bar_viewpager);
        icon1 = (ImageView) findViewById(R.id.icon1);
        icon2 = (ImageView) findViewById(R.id.icon2);
        icon3 = (ImageView) findViewById(R.id.icon3);
        icon1.setVisibility(View.INVISIBLE);
        icon2.setVisibility(View.INVISIBLE);
        icon3.setVisibility(View.INVISIBLE);


        name.setText(place.getName());
        street.setText(place.getVicinity());
        rating.setText(String.valueOf(place.getRating()));
        distance_text.setText(String.valueOf(place.getDistance().intValue()) + " μ.");
        if(place.isOpen_now()){
            isOpen_text.setText("Ανοιχτό\nτώρα");
            isOpen_icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.open_now_icon));
        }
        else {
            isOpen_text.setText("Κλειστό\nτώρα");
            isOpen_icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.closed_icon));
        }

        if(place.getTypes().contains("bar"))
            icon1.setVisibility(View.VISIBLE);
        if(place.getTypes().contains("cafe"))
            icon2.setVisibility(View.VISIBLE);
        if(place.getTypes().contains("restaurant"))
            icon3.setVisibility(View.VISIBLE);


        if(gsonFileWorker.loadFromFile(getApplicationContext()).contains(place.getPlace_id())){
            fav_btn.setChecked(true);
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),place, myPlace);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager, false);

        // Tabs Icons [HAVE TO IMPORT]
        tabLayout.getTabAt(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.navigation_icon_red));
        tabLayout.getTabAt(1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gallery_icon_red));
        tabLayout.getTabAt(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.review_icon_red));
        tabLayout.getTabAt(3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info_icon_red));

        // Start loading
        viewPager.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);

        new Thread(() -> {


            myPlace = gsonWorker.getPlace(location,place.getPlace_id(),getApplicationContext());
            Log.d(TAG,"Details of place found");
            Log.d(TAG + "place", myPlace.getReviews().toString());
            Log.d("TEOplace",myPlace.getPhotos_links().toString());
            runOnUiThread(() -> {
                viewPagerAdapter.replacePlace(myPlace);
                viewPagerAdapter.notifyDataSetChanged();

                loading.setVisibility(View.INVISIBLE);
                viewPager.setVisibility(View.VISIBLE);
            });


        }).start();


        bck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fav_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        gsonFileWorker.deleteFavPlace(place.getPlace_id(), getApplicationContext());
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        fav_btn.setChecked(true);
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
                        builder.setMessage("Είσαι σίγουρος ότι θέλεις να διαγράψεις το μέρος '" + place.getName() + "' από τα αγαπημένα σου;").setPositiveButton("Ναι", dialogClickListener)
                                .setNegativeButton("Όχι", dialogClickListener).show();


                }
                    else {
                        gsonFileWorker.saveFavPlace(place.getPlace_id(), getApplicationContext());
                        fav_btn.setChecked(true);
                    }
                }
            });

        }




}

