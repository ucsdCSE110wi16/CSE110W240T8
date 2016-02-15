package droidsquad.voyage.model.api;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;
import org.json.JSONObject;

public class GooglePlacesAPI implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;

    private static final String TAG = GooglePlacesAPI.class.getSimpleName();

    public GooglePlacesAPI(Context context) {
        this.mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    /**
     * Load a bitmap from the photos API asynchronously
     * by using buffers and result callbacks.
     */
    public void getPlaceImage(String placeId, final int width, final int height,
                              final ResultCallback<PlacePhotoResult> callback) {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            Log.d(TAG, "Couldn\'t receive photos bundle successfully.");
                            return;
                        }

                        Log.i(TAG, "Photo bundle received successfully");
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                        // Display the first bitmap in an ImageView in the size of the view
                        if (photoMetadataBuffer.getCount() > 0) photoMetadataBuffer.get(0)
                                .getScaledPhoto(mGoogleApiClient, width, height)
                                .setResultCallback(callback);
                        else {
                            Log.d(TAG, "0 images in the buffer.");
                        }
                        photoMetadataBuffer.release();
                    }
                });
    }

    /**
     * Parses a Place object into a JSONObject
     *
     * @param place Place to parse into JSON
     * @return The parse JSONObject
     */
    public static JSONObject getJSONFromPlace(Place place) {
        JSONObject object = new JSONObject();
        try {
            object.put("city", place.getName().toString());
            object.put("address", place.getAddress().toString());
            object.put("placeId", place.getId());
            return object;
        } catch (JSONException e) {
            Log.d(TAG, "JSON Exception Occurred. " + e.getMessage());
            return null;
        }
    }

    public void disconnectGoogleAPIClient() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO:
    }
}
