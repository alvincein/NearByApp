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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;


public class InformationFragment extends Fragment {

    private TextView address;
    private TextView reviews;
    private TextView telephone;
    private TextView info;
    private TextView description;
    private Button fb_btn;
    private Button site_btn;
    private Button call_btn;
    GsonWorker gson = new GsonWorker();
    private static String TAG = "TEO";
    private MyPlace place = new MyPlace();
    private MyPlace placeDetails = new MyPlace();
    private MyPlace fb_place = new MyPlace();
    private LatLng location;

    private String site_link = null;
    private String fb_link = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.information, container, false);


        place = getArguments().getParcelable("place");

        Log.d(TAG,"Info" + placeDetails.getContact_number());

        address = rootView.findViewById(R.id.address);
        reviews = rootView.findViewById(R.id.reviews);
        telephone = rootView.findViewById(R.id.telephone);
        description = rootView.findViewById(R.id.place_description);
        fb_btn = rootView.findViewById(R.id.fb_btn);
        site_btn = rootView.findViewById(R.id.site_btn);
        call_btn = rootView.findViewById(R.id.call_btn);
        info = rootView.findViewById(R.id.info_txt);
        fb_btn.setEnabled(false);
        site_btn.setEnabled(false);
        call_btn.setEnabled(false);


        address.setText(place.getVicinity());

        if(placeDetails == null){
            reviews.setText(String.valueOf(place.getUser_ratings_total()));
            telephone.setText(place.getContact_number());
        }
        else {
            reviews.setText(String.valueOf(placeDetails.getUser_ratings_total()));
            telephone.setText(placeDetails.getContact_number());
        }

        if(!(place.getContact_number() == null) || !(placeDetails.getContact_number() == null))
            call_btn.setEnabled(true);

        Log.d(TAG + " openhours",placeDetails.getOpen_hours().toString());
        if(!placeDetails.getOpen_hours().isEmpty()){
            StringBuilder description_txt = new StringBuilder();

            for(int i=0 ;i<placeDetails.getOpen_hours().size(); i++){
                description_txt.append(placeDetails.getOpen_hours().get(i) + "\n");
            }

            description.setText(description_txt.toString());
        }


        // Find user's location
        GPSTracker gps = new GPSTracker(getContext());
        if (gps.canGetLocation()) {
            location = new LatLng(gps.getLatitude(),gps.getLongitude());
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        // Call to facebook API, using a thread
                new Thread(() -> {
                    try {
                        // LatLng to Location object
                        Location loc = new Location("");
                        loc.setLatitude(location.latitude);
                        loc.setLongitude(location.longitude);

                        fb_place = gson.getFacebookPlace(loc, place.getName());

                        // When we have our response, update UI.

                        if(fb_place.getFacebook_link() != null)
                            fb_link = fb_place.getFacebook_link();

                        if(fb_place.getSite_link() != null)
                            site_link = fb_place.getSite_link();

                        getActivity().runOnUiThread(() -> {

                            if(site_link != null)
                                site_btn.setEnabled(true);

                            if(fb_link != null)
                                fb_btn.setEnabled(true);

                            if(fb_place.getDescription() != null){
                                info.setText(fb_place.getDescription());
                            }

                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).start();



        // Opens facebook app or facebook.com and redirects to place's facebook page link
        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                int versionCode = getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;

                if (versionCode >= 3002850) {
                    Uri uri = Uri.parse("fb://facewebmodal/f?href=" + fb_link);
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fb_link)));
                }
                } catch (PackageManager.NameNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fb_link)));

                }

            }
        });

        // Opens place's website on Browser
        site_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reviews.getText().toString().contains("http")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(site_link)));
                }
                else {
                    // If there's no 'https://' append
                    StringBuilder link = new StringBuilder();
                    link.append("https://");
                    link.append(site_link);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.toString())));
                }
            }
        });

        // If there's a contact number, open dial app to allow user to call
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + telephone.getText().toString()));
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void onInfoChange(MyPlace place){
        this.placeDetails = place;
    }
}
