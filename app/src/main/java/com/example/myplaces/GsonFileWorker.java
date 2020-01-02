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
        }
        else {
            Toast.makeText(mContext,"Already in favs", Toast.LENGTH_LONG).show();
        }

    }

    public ArrayList<String> loadFromFile(Context mContext) {
        ArrayList<String> fav_places = new ArrayList<String>();

        Gson gson = new Gson();
        final SharedPreferences sharedPreferences = mContext.getSharedPreferences("USER",MODE_PRIVATE);
        String json = sharedPreferences.getString("Favs", "");
        if (json.isEmpty()) {
            Toast.makeText(mContext,"There is something error", Toast.LENGTH_LONG).show();
        } else {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            fav_places = gson.fromJson(json, type);
            Log.d("TEO",fav_places.toString());
        }
        return fav_places;

    }
}
