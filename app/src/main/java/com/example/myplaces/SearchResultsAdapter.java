package com.example.myplaces;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {


    private LayoutInflater mInflater;
    private ArrayList<MyPlace> places = new ArrayList<>();
    private Context context;

    // data is passed into the constructor
    SearchResultsAdapter(Context context, ArrayList<MyPlace> places) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.places = places;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result, parent, false);

        return new ViewHolder(itemView);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(places.get(position).getName());
        holder.rating.setText(String.valueOf(places.get(position).getRating()));
        if(places.get(position).getIcon_link() != null){
            Log.d("TEO", places.get(position).getIcon_link());
            Picasso.with(context).load(places.get(position).getIcon_link()).into(holder.image);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView rating;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            rating = (TextView) itemView.findViewById(R.id.rating);
            image = (ImageView) itemView.findViewById(R.id.avatar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
