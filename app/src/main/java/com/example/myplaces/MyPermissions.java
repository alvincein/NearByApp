package com.example.myplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

// Responsible for every permission required from user allowing app to work correctly.
public class MyPermissions extends MainActivity {

    private static boolean LOCATION_GRANTED = false;
    private static boolean COARSE_LOCATION_GRANTED = false;
    private static final int REQUEST_CODE_LOCATION = 1;
    private static final int REQUEST_CODE_COARSE_LOCATION = 2;
    private static String TAG = "MyPermissions";
    private static Context context;
    private static Activity activity;

    public MyPermissions(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public static void requestLocation(View v) {

        // FINE LOCATION
        int hasReadLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "HAS Read Location Permissions " + hasReadLocationPermission);

        if (hasReadLocationPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission for location Granted");
            LOCATION_GRANTED = true;
        } else {
            Log.d(TAG, "Requesting permission for location...");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        }

        if (!LOCATION_GRANTED) {

            Snackbar.make(v, "You must provide permission to location!", Snackbar.LENGTH_LONG).show();
        }
    }


    public static void requestCoarseLocation(View v){

        // COARSE LOCATION
        int hasReadCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG, "HAS Read Coarse Location Permissions " + hasReadCoarseLocationPermission);

        if (hasReadCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission for coarse location Granted");
            COARSE_LOCATION_GRANTED = true;
        } else {
            Log.d(TAG, "Requesting permission for coarse location");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_COARSE_LOCATION);
        }

        if (!COARSE_LOCATION_GRANTED) {

            Snackbar.make(v, "You must provide permission to location!", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, Integer.toString(requestCode));
        Log.d(TAG, Integer.toString(REQUEST_CODE_LOCATION));
        Log.d(TAG, Integer.toString(grantResults[0]));

        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Permission to Location Granted!");
                    LOCATION_GRANTED = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "Permission to Location refused");

                }
                return;
            }
            case REQUEST_CODE_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "Permission to Coarse Location Granted!");
                    COARSE_LOCATION_GRANTED = true;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "Permission to Coarse Location refused");

                }
                return;

            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
