package droidsquad.voyage.model;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.activity.TripActivity;
import droidsquad.voyage.controller.PlaceArrayAdapter;

public class GooglePlacesAPI implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private List<PlaceArrayAdapter> mAdapters;
    private Context context;
    private static final String TAG = GooglePlacesAPI.class.getSimpleName();


    private String mSourceCityName, mDestCityName,mSourceCityFullAddress,
            mDestCityFullAddress, mSourceID, mDestID;

    public GooglePlacesAPI(Context context) {
        this.context = context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();

        mAdapters = new ArrayList<>();
    }


    // cityType 0 = Source City, 1 = Dest City
    public void setUpPlacesAutofill(AutoCompleteTextView autoCompleteTextView, final int cityType) {
        autoCompleteTextView.setThreshold(3);
        AutocompleteFilter.Builder filter = new AutocompleteFilter.Builder();
        filter.setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES);
        final PlaceArrayAdapter placeArrayAdapter = new PlaceArrayAdapter(context,
                android.R.layout.simple_list_item_1, null , filter.build());
        mAdapters.add(placeArrayAdapter);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (cityType == 0) {
                    mSourceCityName = null;
                    mSourceCityFullAddress = null;
                } else {
                    mDestCityName = null;
                    mDestCityFullAddress = null;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}
        };

        autoCompleteTextView.addTextChangedListener(watcher);

        autoCompleteTextView.setAdapter(placeArrayAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PlaceArrayAdapter.PlaceAutocomplete item = placeArrayAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.d(TAG, "Selected: " + item.description);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            Log.d(TAG, "Place query did not complete. Error: " +
                                    places.getStatus().toString());
                            return;
                        }

                        // Selecting the first object buffer.
                        final Place place = places.get(0);

                        if (cityType == 0) {
                            mSourceCityName = place.getName().toString();
                            mSourceCityFullAddress = place.getAddress().toString();
                            mSourceID = place.getId();

                        } else {
                            mDestCityName = place.getName().toString();
                            mDestCityFullAddress = place.getAddress().toString();
                            mDestID = place.getId();
                        }
                        Log.d(TAG, "City name: " + place.getName() + "\nCity Address: " + place.getAddress() + "\nID: " + place.getId());

                        places.release();
                    }
                });
                Log.d(TAG, "Fetching details for ID: " + item.placeId);
            }
        });
    }

    public boolean isSourceCityValid() {
        return mSourceCityName != null;
    }

    public boolean isDestCityValid() {
        return mDestCityName != null;
    }


    /**
     * Load a bitmap from the photos API asynchronously
     * by using buffers and result callbacks.
     */
    public void loadPlaceImage(final ImageView imageView, String placeId, final TripActivity activity) {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            Log.d(TAG, "Couldn\'t receive photos bundle successfully.");
                            return;
                        }

                        Log.d(TAG, "Photo bundle received successfully");

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            // Display the first bitmap in an ImageView in the size of the view
                            photoMetadataBuffer.get(0)
                                    .getScaledPhoto(mGoogleApiClient, imageView.getWidth(),
                                            imageView.getHeight())
                                    .setResultCallback(new ResultCallback<PlacePhotoResult>() {
                                        @Override
                                        public void onResult(PlacePhotoResult placePhotoResult) {
                                            if (!placePhotoResult.getStatus().isSuccess()) {
                                                Log.d(TAG, "Couldn\'t retrieve the photo successfully.");
                                                return;
                                            }

                                            Log.d(TAG, "Successfully retrieved photo from photo bundle.");

                                            imageView.setImageBitmap(placePhotoResult.getBitmap());
                                            activity.setColors();
                                        }
                                    });
                        } else {
                            Log.d(TAG, "0 images in the buffer.");
                        }
                        photoMetadataBuffer.release();
                    }
                });

    }

    public JSONObject getSourceCityJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("city", mSourceCityName);
            object.put("address", mSourceCityFullAddress);
            object.put("placeId", mSourceID);
        } catch (JSONException e) {
            Log.d(TAG, "JSON Exception Occurred. " + e.getMessage());
        }

        return object;
    }

    public JSONObject getDestCityJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("city", mDestCityName);
            object.put("address", mDestCityFullAddress);
            object.put("placeId", mDestID);
        } catch (JSONException e) {
            Log.d(TAG, "JSON Exception Occurred. " + e.getMessage());
        }

        return object;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google Places API connected.");

        for (PlaceArrayAdapter adapter : mAdapters) {
            adapter.setGoogleApiClient(mGoogleApiClient);
        }
    }

    public void disconnectGoogleAPIClient() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Places API connection suspended.");

        for (PlaceArrayAdapter adapter: mAdapters) {
            adapter.setGoogleApiClient(null);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

    }

}
