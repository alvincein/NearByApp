package com.example.myplaces;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavouritesActivity extends Activity {

    private SearchResultsAdapter adapter;
    private ImageButton bck_btn;
    private GsonFileWorker gsonFileWorker = new GsonFileWorker();
    private GsonWorker gsonWorker = new GsonWorker();
    private static String TAG = "TEO";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites);


        bck_btn = (ImageButton) findViewById(R.id.back_btn);

        ArrayList<String> places_ids = gsonFileWorker.loadFromFile(getApplicationContext());



        if(places_ids != null){
            Toast.makeText(getApplicationContext(),"Getting favourite places",Toast.LENGTH_LONG).show();

            new Thread(() -> {
                ArrayList<MyPlace> places = new ArrayList<>();

                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.list);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new SearchResultsAdapter(this, places);
                recyclerView.setAdapter(adapter);

                for(int i=0; i<places_ids.size(); i++){
                    // search place from gson
                    //places.add(gsonWorker.getPlace());
                }
            }).start();




        }
    }
}
