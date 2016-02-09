package droidsquad.voyage.controller;


import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import droidsquad.voyage.R;
import droidsquad.voyage.activity.TripActivity;
import droidsquad.voyage.model.GooglePlacesAPI;
import droidsquad.voyage.model.Trip;

public class TripController {
    private static final String TAG = TripController.class.getSimpleName();
    private TripActivity activity;
    private GooglePlacesAPI mGooglePlacesModel;
    private JSONObject mOrigin, mDest;

    public TripController(TripActivity intance, Trip trip) {
        this.activity = intance;
        mGooglePlacesModel = new GooglePlacesAPI(activity);

        // Initialize the info on the page
        activity.setTripName(trip.getName());

        try {
            mOrigin = new JSONObject(trip.getOrigin());
            mDest = new JSONObject(trip.getDestination());

            int drawableId;

            switch (trip.getTransportation()) {
                case "Bus":
                    drawableId = R.drawable.ic_bus;
                    break;

                case "Car":
                    drawableId = R.drawable.ic_car;
                    break;

                default:
                    drawableId = R.drawable.ic_flight;
                    break;
            }

            activity.setTripTransportationAndIcon(mOrigin.get("city") + " –> " + mDest.get("city"), drawableId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setGooglePlacePhoto(ImageView imageView) {
        try {
            String placeID = (String) mDest.get("placeId");


            Log.d(TAG, "Attempting to get photo from Google Places");

            mGooglePlacesModel.loadPlaceImage(imageView, placeID, activity);
        } catch (JSONException e) {
            Log.d(TAG, "JSONException occurred: " + e.getMessage());
        }
    }
}
