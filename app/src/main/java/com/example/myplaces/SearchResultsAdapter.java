package com.example.myplaces;


import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private static final String TAG = "SearchResultsAdapter";

    private ArrayList<MyPlace> places = new ArrayList<>();
    private Context context;
    private View itemView;
    private GsonWorker gsonWorker = new GsonWorker();



    // Data is passed into the constructor
    SearchResultsAdapter(Context context, ArrayList<MyPlace> places) {
        this.context = context;
        this.places = places;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result, parent, false);

        return new ViewHolder(itemView);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Loading invisible.
        holder.result_progress.setVisibility(View.INVISIBLE);

        holder.name.setText(places.get(position).getName());
        holder.rating.setText(String.valueOf(places.get(position).getRating()));
        holder.distance.setText(String.valueOf(places.get(position).getDistance().intValue()) + " μ.");
        holder.ratings_count.setText("(" + String.valueOf(places.get(position).getUser_ratings_total()) + ")");

        holder.avatar.setFocusable(true);
        holder.avatar.setSelected(true);


        // Setting up query for image google API call
        String link = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";

        String reference = places.get(position).getAvatar_link();

        // Final link
        String request_link = link + reference +"&key=" + context.getString(R.string.places_api_key);

        if(reference != null) {
            // Load image through Picasso, passing the link
            Picasso.with(context).load(request_link).into(holder.avatar);
        }
        else {
            // If something goes wrong, load our logo as a default.
            holder.avatar.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo_w_bg));
        }


        // On place selection
        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start loading
                holder.result_progress.setVisibility(View.VISIBLE);

                // Move to Place details activity
                Intent intent = new Intent(context, PlaceActivity.class);
                intent.putExtra("place",(Parcelable) places.get(position));
                context.startActivity(intent);

                // Stop loading
                holder.result_progress.setVisibility(View.INVISIBLE);
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView rating;
        TextView distance;
        TextView ratings_count;
        ImageView background_img;
        ImageView avatar;
        ImageButton select;
        ProgressBar result_progress;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            name.setSelected(true);
            rating = (TextView) itemView.findViewById(R.id.rating);
            ratings_count = (TextView) itemView.findViewById(R.id.ratings_count);
            background_img = (ImageView) itemView.findViewById(R.id.background_img);
            background_img.setClipToOutline(true);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            avatar.setClipToOutline(true);
            select = (ImageButton) itemView.findViewById(R.id.select);
            distance = (TextView) itemView.findViewById(R.id.distance_text);
            result_progress = (ProgressBar) itemView.findViewById(R.id.result_progress_bar);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }


}
