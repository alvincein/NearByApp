package com.example.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {

    private static String TAG = "TEO";
    private SearchResultsAdapter adapter;
    private ImageButton bck_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        bck_btn = (ImageButton) findViewById(R.id.back_btn);

        Intent intent = getIntent();
        ArrayList<MyPlace> places = intent.getParcelableArrayListExtra("Places");

        ArrayList<String> temp = new ArrayList<String>();
        for(MyPlace place : places){
            temp.add(place.getName());
        }


        if(places != null){

            // set up the RecyclerView
            RecyclerView recyclerView = findViewById(R.id.list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new SearchResultsAdapter(this, places);
            recyclerView.setAdapter(adapter);

        }

        bck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }


}
