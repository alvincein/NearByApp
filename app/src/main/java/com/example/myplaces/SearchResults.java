package com.example.myplaces;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {

    private static String TAG = "TEO";
    private SearchResultsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

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



    }


}
