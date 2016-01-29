package droidsquad.voyage.controller;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import droidsquad.voyage.activity.CreateTripActivity;
import droidsquad.voyage.model.Trip;

public class CreateTripController implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private CreateTripActivity activity;

    private GoogleApiClient mGoogleApiClient;
    private List<PlaceArrayAdapter> mAdapters;

    private String mSourceCityName, mDestCityName, mSourceCityFullAddress, mDestCityFullAddress;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

    private static final String TAG = CreateTripController.class.getSimpleName();

    public CreateTripController(CreateTripActivity activity) {
        this.activity = activity;

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(activity, 0, this)
                .addConnectionCallbacks(this)
                .build();

        mGoogleApiClient.connect();

        mAdapters = new ArrayList<>();
    }

    public void showDateDialog(final Calendar calendar, final TextView dateView) {
        showDateDialogAndUpdateView(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth);
                dateView.setText(dateFormat.format(calendar.getTime()));
            }
        }, calendar);
    }

    public void showDateDialogAndUpdateView(DatePickerDialog.OnDateSetListener listener, Calendar cal) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, listener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void attemptCreateTrip() {
        String tripName = activity.getTripNameView().getText().toString();

        int memberLimit = 0;
        if (!isEmpty(activity.getMemberLimitView())) {
            memberLimit = Integer.parseInt(activity.getMemberLimitView().getText().toString());
        }

        boolean privateTrip = activity.getPrivateView().isChecked();

        String leavingFrom = activity.getLeavingFromView().getText().toString();
        String destination = activity.getDestinationView().getText().toString();

        Date dateFrom = activity.getCalendarFrom().getTime();
        Date dateTo = activity.getCalendarTo().getTime();

        String transportation = activity.getTransportation().getSelectedItem().toString();


        Trip newTrip = new Trip(tripName, leavingFrom, destination, privateTrip,
                memberLimit, dateFrom, dateTo, transportation);
        newTrip.save();

        boolean tripValid = isTripValid(tripName, memberLimit, dateFrom, dateTo);
        if (!tripValid) {
            activity.notifyTripInvalid();
        }
    }

    private boolean isTripValid(String tripName, int memberLimit, Date dateFrom, Date dateTo) {
        // TODO: put checks in here
        return false;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private static boolean checkDates(DatePicker from, DatePicker to) {
        return false;
    }

    public void saveTrip(Trip trip){
        // TODO.
        // If the user selected an option from the Google places drop down
        // the member variables mSourceCityName, mDestCityName, mSourceCityFullAddress
        // and mDestCityFullAddress would be appropriately set
    }

    // cityType 0 = Source City, 1 = Dest City
    public void setUpPlacesAutofill(AutoCompleteTextView autoCompleteTextView, final int cityType) {
        autoCompleteTextView.setThreshold(3);
        AutocompleteFilter.Builder filter = new AutocompleteFilter.Builder();
        filter.setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES);
        final PlaceArrayAdapter placeArrayAdapter = new PlaceArrayAdapter(activity,
                android.R.layout.simple_list_item_1, null , filter.build());
        mAdapters.add(placeArrayAdapter);


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
                        } else {
                            mDestCityName = place.getName().toString();
                            mDestCityFullAddress = place.getAddress().toString();
                        }
                        Log.d(TAG, "City name: " + place.getName() + "\nCity Address: " + place.getAddress());
                    }
                });
                Log.d(TAG, "Fetching details for ID: " + item.placeId);
            }
        });
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
