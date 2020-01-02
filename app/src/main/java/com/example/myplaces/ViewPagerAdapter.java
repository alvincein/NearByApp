package com.example.myplaces;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private MyPlace place = new MyPlace();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public ViewPagerAdapter(FragmentManager fm, MyPlace place) {
        super(fm);
        this.place = place;
    }

    @Override
    public Fragment getItem(int position) {


        Bundle bundle = new Bundle();
        bundle.putParcelable("place",place);
        Fragment fragment = null;
        if (position == 0)
        {
            fragment = new NavigationFragment();
            fragment.setArguments(bundle);
        }
        else if (position == 1)
        {
            fragment = new GalleryFragment();
            fragment.setArguments(bundle);
        }
        else if (position == 2)
        {
            fragment = new ReviewsFragment();
            fragment.setArguments(bundle);
        }
        else if (position == 3){
            fragment = new InformationFragment();
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
