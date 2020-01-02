package com.example.myplaces;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.places.PlaceManager;

public class PlaceSearchRequestCallback
        implements PlaceManager.OnRequestReadyCallback, GraphRequest.Callback {

    @Override
    public void onRequestReady(GraphRequest graphRequest) {
        // The place search request is ready to be executed.
        // The request can be customized here if needed.

        // Sets the callback and executes the request.
        graphRequest.setCallback(this);
        graphRequest.executeAsync();
    }

    @Override
    public void onCompleted(GraphResponse response) {
        // This event is invoked when the place search response is received.
        // Parse the places from the response object.
    }

    @Override
    public void onLocationError(PlaceManager.LocationError error) {
        // Invoked if the Places Graph SDK failed to retrieve
        // the device location.
    }
}
