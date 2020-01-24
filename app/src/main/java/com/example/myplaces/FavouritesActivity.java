package com.example.myplaces;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class FavouritesActivity extends AppCompatActivity {

    private SearchResultsAdapter adapter;
    private ImageButton bck_btn;
    private Button clear_btn;
    private ProgressBar progressBar;
    private GsonFileWorker gsonFileWorker = new GsonFileWorker();
    private GsonWorker gsonWorker = new GsonWorker();
    private static String TAG = "FavouritesActivity";

    // List of favourite places' ids
    private ArrayList<String> places_ids = new ArrayList<>();
    private ArrayList<MyPlace> places = new ArrayList<>();

    private LatLng location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);


        bck_btn = (ImageButton) findViewById(R.id.back_btn);
        clear_btn = (Button) findViewById(R.id.clear_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initialize loading spinner
        progressBar.setVisibility(View.INVISIBLE);

        // Get favourites places' ids from internal storage
        places_ids = gsonFileWorker.loadFromFile(getApplicationContext());


        if(places_ids != null) {

            // Start loading
            Toast.makeText(getApplicationContext(), "Απόκτηση αγαπημένων τοποθεσιών.\n Παρακαλώ περιμένετε...", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.VISIBLE);

            // Set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.favs_list);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new SearchResultsAdapter(this, places);
            recyclerView.setAdapter(adapter);

            // Display data to RecyclerView
            displayFavPlaces();

        }
        else {
            Toast.makeText(getApplicationContext(),"Δεν υπάρχουν αγαπημένες τοποθεσίες.",Toast.LENGTH_SHORT).show();
        }

        // On clear button press
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Open "are you sure" dialog
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                // Delete all places from internal storage
                                gsonFileWorker.deleteAllPlaces(getApplicationContext());
                                // Clear places list and update RecyclerView
                                places.clear();
                                runOnUiThread(adapter::notifyDataSetChanged);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //Do nothing
                                break;
                        }
                    }
                };

                // Build Alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(FavouritesActivity.this);
                builder.setMessage("Είσαι σίγουρος ότι θέλεις να διαγράψεις όλες τις αγαπημένες τοποθεσίες σου;").setPositiveButton("Ναι", dialogClickListener)
                        .setNegativeButton("Όχι", dialogClickListener).show();

            }
        });

        // On back button press
        bck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    public void displayFavPlaces(){

        // Get user's location
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            location = new LatLng(gps.getLatitude(), gps.getLongitude());
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        // Start searching for favourite places
        new Thread(() -> {
            places.clear();

            for(int i=0; i<places_ids.size(); i++){

                MyPlace myPlace = new MyPlace();

                // Do Google placeDetails search for each id
                myPlace = gsonWorker.getPlace(location,places_ids.get(i),getApplicationContext());

                if(myPlace != null){
                    places.add(myPlace);
                }
            }
            runOnUiThread(() -> {
                // Stop Loading
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.INVISIBLE);

                // Inform user for result
                if(!places_ids.isEmpty())
                    Toast.makeText(getApplicationContext(),"Οι αγαπημένες τοποθεσίες φορτώθηκαν με επιτυχία.",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(),"Δεν υπάρχουν αγαπημένες τοποθεσίες.",Toast.LENGTH_SHORT).show();

            });

        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        places_ids = gsonFileWorker.loadFromFile(getApplicationContext());
    }
}
