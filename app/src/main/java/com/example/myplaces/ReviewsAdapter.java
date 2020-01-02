package com.example.myplaces;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {


    private Context context;
    private View itemView;
    private ArrayList<Review> reviews = new ArrayList<>();


    public ReviewsAdapter(Context context, ArrayList<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_review, parent, false);

        return new ReviewsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(reviews.get(position).getUser());
        holder.rating.setText(String.valueOf(reviews.get(position).getRating()));
        holder.description.setText(reviews.get(position).getMessage());
        if(reviews.get(position).getPhoto_link() != null){
            Picasso.with(context).load(reviews.get(position).getPhoto_link()).into(holder.avatar);

        }

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView rating;
        TextView description;
        ImageView avatar;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            rating = (TextView) itemView.findViewById(R.id.rating);
            description = (TextView) itemView.findViewById(R.id.desription);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
