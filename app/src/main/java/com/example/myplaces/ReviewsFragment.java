package com.example.myplaces;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ReviewsFragment extends Fragment {

    private ArrayList<Review> reviews = new ArrayList<>();
    private ReviewsAdapter adapter;
    private Thread t = new Thread();
    private ImageButton bck_btn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.reviews_section, container, false);

        TextView reviews_found = (TextView) rootView.findViewById(R.id.reviews_found);

        // set up the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.reviews_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Runnable r = () -> {
            generateReviews();
            reviews_found.setText("Βρέθηκαν " + reviews.size() + " κριτικές");
            adapter = new ReviewsAdapter(getContext(), reviews);
            recyclerView.setAdapter(adapter);
            getActivity().runOnUiThread(adapter::notifyDataSetChanged);
        };

        if (t.isAlive()) {
            t.interrupt();
        }
        t = new Thread(r);
        t.start();

        return rootView;
    }

    public void generateReviews(){
        Review review1 = new Review("Καλό εστιατόριο", "Μάκης Δημάκης", 4.7);
        Review review2 = new Review("Σκατά μελάτα", "Γιώργος Σαμπάνιας", 3.2);
        Review review3 = new Review("Εξαιρετικό", "Πάο Λάρα", 4.9);
        review1.setPhoto_link("https://www.kratisinow.gr/wp-content/uploads/2017/08/makis-dimakis-viografiko-diskografia.jpg");
        review2.setPhoto_link("https://www.kratisinow.gr/wp-content/uploads/2019/04/romeo-club-live-stage-paraliaki-giorgos-sampanis.jpg");
        review3.setPhoto_link("https://www.gossiplife.gr/wp-content/uploads/2017/01/paola-tsiggana.jpg");

        reviews.add(review1);
        reviews.add(review2);
        reviews.add(review3);
    }
}
