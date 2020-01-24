package com.example.myplaces;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<String> links = new ArrayList<>();
    private Context context;
    private View itemView;
    private static final String PLACES_KEY = "AIzaSyAtj0HrLGRjXpeDBpoi2jpuaAZEPIOC5kA";

    public GalleryAdapter(Context context, ArrayList<String> links) {
        this.links = links;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_photo, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String link = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";

        List<Place.Field> fields = Arrays.asList(Place.Field.PHOTO_METADATAS);

        String reference = links.get(position);

        Log.d("TEO","p = " + reference);

        String request_link = link + reference +"&key=" + PLACES_KEY;

        if(reference != null) {
            Picasso.with(context).load(request_link).into(holder.image);
        }

    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.img);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}

