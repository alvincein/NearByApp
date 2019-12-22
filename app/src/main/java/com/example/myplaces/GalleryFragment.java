package com.example.myplaces;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {


    private ArrayList<String> links = new ArrayList<>();

    private GalleryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.photo_gallery, container, false);

        generateImageLinks();
        // set up the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.image_gallery);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new GalleryAdapter(getContext(), links);
        recyclerView.setAdapter(adapter);

        return rootView;
    }


    private void generateImageLinks(){
        links.add("https://api.time.com/wp-content/uploads/2019/08/better-smartphone-photos.jpg?w=600&quality=85");
        links.add("https://www.androidpolice.com/wp-content/uploads/2019/10/Google-Photos-Colorize-Beta.png");
        links.add("https://www.groovypost.com/wp-content/uploads/2019/07/sunset-beach-phone-photos-featured.jpg");
        links.add("https://images.pexels.com/photos/1820567/pexels-photo-1820567.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500");
        links.add("https://media.gettyimages.com/photos/spring-field-picture-id539016480?s=612x612");
        links.add("https://dynaimage.cdn.cnn.com/cnn/q_auto,h_600/https%3A%2F%2Fcdn.cnn.com%2Fcnnnext%2Fdam%2Fassets%2F191010165517-28-week-in-photos-1011.jpg");
    }
}
