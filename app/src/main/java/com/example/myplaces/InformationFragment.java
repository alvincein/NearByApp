package com.example.myplaces;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class InformationFragment extends Fragment {

    private TextView address;
    private TextView reviews;
    private TextView telephone;
    private TextView email;
    private Button fb_btn;
    private Button site_btn;
    GsonWorker gson = new GsonWorker();
    private static String TAG = "TEO";
    private MyPlace place = new MyPlace();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.information, container, false);


        place = getArguments().getParcelable("place");


        address = rootView.findViewById(R.id.address);
        reviews = rootView.findViewById(R.id.reviews);
        telephone = rootView.findViewById(R.id.telephone);
        fb_btn = rootView.findViewById(R.id.fb_btn);
        site_btn = rootView.findViewById(R.id.site_btn);
        fb_btn.setEnabled(false);
        site_btn.setEnabled(false);



        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                new Thread(() -> {
                    ArrayList<String> links = new ArrayList<>();
                    try {
                        links = gson.getFacebookLink(location,place.getName());
                        Log.d(TAG,links.toString());
                        String fbLink = links.get(0);

                        String siteLink = null;
                        if(links.size() > 1)
                            siteLink = links.get(1);
                        String finalSiteLink = siteLink;

                        getActivity().runOnUiThread(() -> {
                            telephone.setText(fbLink);
                            if(finalSiteLink != null){
                                site_btn.setEnabled(true);

                            }
                            reviews.setText(String.valueOf(place.getUser_ratings_total()));
                            fb_btn.setEnabled(true);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(getContext(), locationResult);

        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                int versionCode = getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

                if (versionCode >= 3002850) {
                    Uri uri = Uri.parse("fb://facewebmodal/f?href=" + telephone.getText().toString());
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(telephone.getText().toString())));
                }
                } catch (PackageManager.NameNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(telephone.getText().toString())));

                }

            }
        });

        site_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reviews.getText().toString().contains("http")){
                    Log.d("TEO","fuck " + reviews.getText().toString());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(reviews.getText().toString())));
                }
                else {
                    StringBuilder link = new StringBuilder();
                    link.append("https://");
                    link.append(reviews.getText().toString());
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.toString())));
                }
            }
        });

        return rootView;
    }
}
