package droidsquad.voyage.controller.activityController;

import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import droidsquad.voyage.R;
import droidsquad.voyage.model.api.GooglePlacesAPI;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.view.activity.AddFriendsActivity;
import droidsquad.voyage.view.activity.TripActivity;

public class TripController {
    private TripActivity mActivity;
    private Trip trip;

    private static final String TAG = TripController.class.getSimpleName();

    public TripController(TripActivity instance) {
        this.mActivity = instance;

        trip = mActivity.getIntent().getParcelableExtra(
                mActivity.getString(R.string.intent_key_trip));
    }

    public void setGooglePlacePhoto(final ImageView imageView) {
        try {
            Log.d(TAG, "Attempting to get photo from Google Places");

            String placeID = trip.getDestination().getString("placeId");

            final GooglePlacesAPI googlePlacesAPI = new GooglePlacesAPI(mActivity);
            googlePlacesAPI.getPlaceImage(placeID, imageView.getWidth(), imageView.getHeight(),
                    new ResultCallback<PlacePhotoResult>() {
                        @Override
                        public void onResult(PlacePhotoResult placePhotoResult) {
                            if (!placePhotoResult.getStatus().isSuccess()) {
                                Log.d(TAG, "Couldn\'t retrieve the photo successfully.");
                                return;
                            }
                            Log.d(TAG, "Successfully retrieved photo from photo bundle.");

                            imageView.setImageBitmap(placePhotoResult.getBitmap());
                            mActivity.setColors();
                            googlePlacesAPI.disconnectGoogleAPIClient();
                        }
                    });
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
            origin = trip.getOrigin().get("city").toString();
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception occurred: " + e.getMessage());
        }

        return origin;
    }

    public String getDestination() {
        String destination = "";
        try {
            destination = trip.getDestination().get("city").toString();
        } catch (JSONException e) {
            Log.d(TAG, "JSON exception occurred: " + e.getMessage());
        }

        return destination;
    }

    public void launchAddFriends() {
        Intent intent = new Intent(mActivity, AddFriendsActivity.class);
        intent.putExtra(mActivity.getString(R.string.intent_key_trip), trip);
        mActivity.startActivity(intent);
    }

    public CharSequence getTitle() {
        return trip.getName();
    }
}
