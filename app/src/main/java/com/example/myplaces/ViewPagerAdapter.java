package com.example.myplaces;


import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private MyPlace place = new MyPlace();
    private MyPlace placeDetails = new MyPlace();

    // Initialize fragments
    private ReviewsFragment reviewsFragment = new ReviewsFragment();
    private InformationFragment informationFragment = new InformationFragment();
    private GalleryFragment galleryFragment = new GalleryFragment();

    private static final String TAG = "ViewPagerAdapter";

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, MyPlace place, MyPlace placeDetails) {
        super(fm);
        this.place = place;
        this.placeDetails = placeDetails;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("place",place);
        bundle.putParcelable("placeDetails",placeDetails);
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new NavigationFragment();
            fragment.setArguments(bundle);
        }
        else if (position == 1)
        {
            fragment = galleryFragment;
            fragment.setArguments(bundle);
        }
        else if (position == 2)
        {
            fragment = reviewsFragment;
        }
        else if (position == 3){
            fragment = informationFragment;
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }


    // Updates detailed place when api call is finished
    public void replacePlace(MyPlace place){
        this.placeDetails = place;
        // Update data on reviews fragment
        this.reviewsFragment.onReviewsChange(placeDetails.getReviews());
        // Update data on information fragment
        this.informationFragment.onInfoChange(placeDetails);
        // Update data on gallery fragment
        this.galleryFragment.onPhotosChange(placeDetails.getPhotos_links());
        Log.d(TAG,placeDetails.getReviews().toString());
    }
}
