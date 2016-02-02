package droidsquad.voyage.model;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.PlaceArrayAdapter;

/**
 * Created by Andrew on 1/29/16.
 */
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
                .enableAutoManage((FragmentActivity) context, 0, this)
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
                        Log.d(TAG, "City name: " + place.getName() + "\nCity Address: " + place.getAddress());

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

    public JSONObject getSourceCityJSON() {
        JSONObject object = new JSONObject();
        try {
            object.put("city", mSourceCityName);
            object.put("address", mSourceCityFullAddress);
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
