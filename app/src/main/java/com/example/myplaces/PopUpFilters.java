package com.example.myplaces;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

public class PopUpFilters extends DialogFragment {

    private DialogListener dialogListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_filters, container, false);


        // Set transparent background
        this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button done = view.findViewById(R.id.done_btn);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().hide();
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


    public interface DialogListener {
        void getQuantity(MyPlace place, int q);
    }
}
