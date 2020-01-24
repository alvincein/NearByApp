package com.example.myplaces;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

public class PopUpFilters extends DialogFragment {

    private PopUpDialogListener popUpDialogListener;
    private FilterParameters parameters = new FilterParameters();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.popup_filters, container, false);


        // Set transparent background
        this.getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Connect UI
        Button done = view.findViewById(R.id.done_btn);
        SeekBar distance_bar = view.findViewById(R.id.distance_bar);
        EditText distance = view.findViewById(R.id.distance_text);
        RadioButton restaurant = view.findViewById(R.id.restaurant);
        RadioButton bar = view.findViewById(R.id.bar);
        RadioButton cinema = view.findViewById(R.id.cinema);
        RadioButton cafe = view.findViewById(R.id.cafe);
        RadioButton club = view.findViewById(R.id.club);
        RadioButton park = view.findViewById(R.id.park);

        RadioButton cost1 = view.findViewById(R.id.cost1);
        RadioButton cost2 = view.findViewById(R.id.cost2);
        RadioButton cost3 = view.findViewById(R.id.cost3);
        RadioButton cost4 = view.findViewById(R.id.cost4);

        RadioButton rankby_distance = view.findViewById(R.id.distance_rank);
        RadioButton prominence = view.findViewById(R.id.prominence);
        RadioButton no_rankby = view.findViewById(R.id.no_rankby);

        EditText keyword = view.findViewById(R.id.keyword_txt);

        distance.setText(String.valueOf(distance_bar.getProgress()));

        // On distance text change, seekbar should change as well
        distance.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(distance.getText().toString().equals("")){
                        distance.setText("20");
                    }
                    else {
                        if(Integer.parseInt(distance.getText().toString()) > 5000){
                            distance_bar.setProgress(Integer.parseInt(distance.getText().toString()));
                            distance.setText("5000");
                        }
                    }

                    distance_bar.setProgress(Integer.parseInt(distance.getText().toString()));
                    distance.setSelection(distance.length());

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        // When seekbar changes, distance text should change as well
        distance_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Saving parameters and passing them to MainActivity when done
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parameters.clear();

                parameters.setDistance(Integer.parseInt(distance.getText().toString()));

                parameters.setRestaurant(restaurant.isChecked());
                parameters.setBar(bar.isChecked());
                parameters.setCinema(cinema.isChecked());
                parameters.setCafe(cafe.isChecked());
                parameters.setClub(club.isChecked());
                parameters.setPark(park.isChecked());

                if(!keyword.getText().toString().isEmpty())
                    parameters.setKeyword(keyword.getText().toString());

                if(cost1.isChecked())
                    parameters.setMax_price(1);
                else if(cost2.isChecked())
                    parameters.setMax_price(2);
                else if(cost3.isChecked())
                    parameters.setMax_price(3);
                else if(cost4.isChecked())
                    parameters.setMax_price(4);

                if(prominence.isChecked())
                    parameters.setRankby("prominence");
                else if (rankby_distance.isChecked())
                    parameters.setRankby("distance");
                else if(no_rankby.isChecked())
                    parameters.setRankby(null);


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
