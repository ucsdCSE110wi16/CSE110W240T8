package droidsquad.voyage.controller.activityController;

import android.content.DialogInterface;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import droidsquad.voyage.R;
import droidsquad.voyage.view.activity.TripActivity;
import droidsquad.voyage.model.api.GooglePlacesAPI;
import droidsquad.voyage.model.objects.Trip;

public class TripController {
    private static final String TAG = TripController.class.getSimpleName();
    private TripActivity activity;
    private Trip trip;

    private GooglePlacesAPI mGooglePlacesModel;
    private JSONObject mOrigin, mDest;

    public TripController(TripActivity instance) {
        this.activity = instance;
        trip = (Trip) activity.getIntent().getSerializableExtra(
                activity.getString(R.string.intent_key_trip));

        try {
            mOrigin = new JSONObject(trip.getOrigin());
            mDest = new JSONObject(trip.getDestination());
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception occurred: " + e.getMessage());
        }

        mGooglePlacesModel = new GooglePlacesAPI(activity);
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

    /**
     * @return The id of the icon corresponding to the type of transportation
     */
    public int getDrawableId() {
        switch (trip.getTransportation()) {
            case "Bus":
                return R.drawable.ic_bus;
            case "Car":
                return R.drawable.ic_car;
            default:
                return R.drawable.ic_flight;
        }
    }

    public Date getDateFrom() {
        return trip.getDateFrom();
    }

    public Date getDateTo() {
        return trip.getDateTo();
    }

    public String getOrigin() {
        String origin = "";
        try {
            origin = mOrigin.get("city").toString();
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception occurred: " + e.getMessage());
        }

        return origin;
    }

    public String getDestination() {
        String destination = "";
        try {
            destination = mDest.get("city").toString();
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception occurred: " + e.getMessage());
        }

        return destination;
    }

    public CharSequence getTitle() {
        return trip.getName();
    }
}
