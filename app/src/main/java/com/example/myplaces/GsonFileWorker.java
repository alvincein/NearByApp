package com.example.myplaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class GsonFileWorker {

    private Gson gson = new GsonBuilder().create();
    private static final String TAG = "GsonFileWorkerTag";

    // Adds a place in user's favourites
    public void saveFavPlace(String place_id, Context mContext){

        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("USER",MODE_PRIVATE);

        ArrayList<String> favs = new ArrayList<String>();

        favs = loadFromFile(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(!favs.contains(place_id)){
            favs.add(place_id);
            Gson gson = new Gson();
            String json = gson.toJson(favs);
            editor.putString("Favs",json );
            editor.commit();
            Toast.makeText(mContext,"Η τοποθεσία προστέθηκε στα αγαπημένα.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(mContext,"Η τοποθεσία είναι στα αγαπημένα.", Toast.LENGTH_LONG).show();
        }

    }

    // Deletes all user's favourite places
    public void deleteAllPlaces(Context mContext){

        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Log.d(TAG,loadFromFile(mContext).toString());
        Toast.makeText(mContext,"Διαγράφηκαν όλα τα αγαπημένα μέρη", Toast.LENGTH_LONG).show();

    }

    // Returns a list with all user's favourite places ids.
    public ArrayList<String> loadFromFile(Context mContext) {
        ArrayList<String> fav_places = new ArrayList<String>();

        Gson gson = new Gson();
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("USER",MODE_PRIVATE);
        String json = sharedPreferences.getString("Favs", "");
        if (json.isEmpty()) {
            Log.d(TAG,"Probably this place isn't in user's favourites");
        } else {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            fav_places = gson.fromJson(json, type);
            Log.d(TAG,fav_places.toString());
        }
        return fav_places;

    }

    // Deletes a given place from user's favourites
    public void deleteFavPlace(String place_id, Context mContext) {

        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("USER",MODE_PRIVATE);

        ArrayList<String> favs = new ArrayList<String>();

        favs = loadFromFile(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        favs.remove(place_id);
        Gson gson = new Gson();
        String json = gson.toJson(favs);
        editor.putString("Favs",json );
        editor.commit();
        Toast.makeText(mContext,"Η τοποθεσία διαγράφηκε από τα αγαπημένα.", Toast.LENGTH_LONG).show();
    }
}
