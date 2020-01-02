package com.example.myplaces;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {


    private ArrayList<MyPlace> places = new ArrayList<>();
    private Context context;
    private View itemView;



    // data is passed into the constructor
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
        holder.name.setText(places.get(position).getName());
        holder.rating.setText(String.valueOf(places.get(position).getRating()));
        if(places.get(position).getPhotos_link() != null){
            Log.d("TEO", places.get(position).getPhotos_link());
            Picasso.with(context).load(places.get(position).getPhotos_link()).into(holder.background_img);
            Picasso.with(context).load(places.get(position).getPhotos_link()).into(holder.avatar);

        }

        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PlaceActivity.class);
                intent.putExtra("place",(Parcelable) places.get(position));
                context.startActivity(intent);
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView rating;
        ImageView background_img;
        ImageView avatar;
        ImageButton select;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            rating = (TextView) itemView.findViewById(R.id.rating);
            background_img = (ImageView) itemView.findViewById(R.id.background_img);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            select = (ImageButton) itemView.findViewById(R.id.select);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
