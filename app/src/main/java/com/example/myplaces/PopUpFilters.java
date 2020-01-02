package com.example.myplaces;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class PopUpFilters extends DialogFragment {

    private PopUpDialogListener popUpDialogListener;
    private FilterParameters parameters = new FilterParameters();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_filters, container, false);


        // Set transparent background
        this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button done = view.findViewById(R.id.done_btn);
        SeekBar distance_bar = view.findViewById(R.id.distance_bar);
        TextView distance = view.findViewById(R.id.distance);
        CheckBox restaurant = view.findViewById(R.id.restaurant);
        CheckBox bar = view.findViewById(R.id.bar);
        CheckBox cinema = view.findViewById(R.id.cinema);
        CheckBox cafe = view.findViewById(R.id.cafe);
        CheckBox club = view.findViewById(R.id.club);
        CheckBox park = view.findViewById(R.id.park);

        distance.setText(String.valueOf(distance_bar.getProgress()));


        distance_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance.setText(String.valueOf(progress));
                parameters.setDistance(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                parameters.setRestaurant(restaurant.isChecked());
                parameters.setBar(bar.isChecked());
                parameters.setCinema(cinema.isChecked());
                parameters.setCafe(cafe.isChecked());
                parameters.setClub(club.isChecked());
                parameters.setPark(park.isChecked());

                popUpDialogListener.onDialogFinish(parameters);
                getDialog().dismiss();
            }
        });

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        // Get window
        Window window = getDialog().getWindow();

        // Get window attributes
        WindowManager.LayoutParams windowParams = window.getAttributes();

        // Reduce background dim effect
        windowParams.dimAmount = 0.2f;

        // Add params
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        // Set window attributes
        window.setAttributes(windowParams);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        popUpDialogListener = (PopUpDialogListener) context;
    }

    public interface PopUpDialogListener {
        void onDialogFinish(FilterParameters parameters);
    }
}
