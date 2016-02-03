package droidsquad.voyage.controller;


import android.util.Log;
import android.widget.ImageView;

import droidsquad.voyage.activity.TripActivity;
import droidsquad.voyage.model.GooglePlacesAPI;

/**
 * Created by Raghav on 2/2/2016.
 */
public class TripController {
    private static final String TAG = TripController.class.getSimpleName();
    private TripActivity activity;
    private GooglePlacesAPI mGooglePlacesModel;

    public TripController(TripActivity intance) {
        this.activity = intance;
        mGooglePlacesModel = new GooglePlacesAPI(activity);
    }

    public void setGooglePlacePhoto(ImageView imageView) {
        // TODO update place ID from the trip object that comes in the intent bundle
        // temporary place id for now
        String placeID = "ChIJrTLr-GyuEmsRBfy61i59si0";

        Log.d(TAG, "Attempting to get photo from Google Places");

        mGooglePlacesModel.loadPlaceImage(imageView, placeID);
    }
}
