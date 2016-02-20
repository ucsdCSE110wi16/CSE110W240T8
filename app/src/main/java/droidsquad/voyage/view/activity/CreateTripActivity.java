package droidsquad.voyage.view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.activityController.CreateTripController;

public class CreateTripActivity extends AppCompatActivity {
    private CreateTripController controller;

    private TextInputLayout mLeavingFromWrapper;
    private TextInputLayout mDestinationWrapper;

    private EditText mTripNameView;
    private EditText mLeavingFromView;
    private EditText mDestinationView;

    private TextView mDateFromView;
    private TextView mDateToView;
    private TextView mPrivateHelpView;
    private TextView mTripNameErrorView;

    private CheckBox mPrivateView;
    private Spinner mTransportation;

    private Calendar mCalendarFrom;
    private Calendar mCalendarTo;

    private Place mOriginPlace;
    private Place mDestinationPlace;

    private static final int DEFAULT_TRIP_LENGTH = 7;
    private static final int FROM_PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int TO_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
    private static final String TAG = CreateTripActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        controller = new CreateTripController(this);
        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Place place = handlePlaceSelected(resultCode, data);
            if (place != null) {
                mLeavingFromView.setText(place.getAddress());
                mOriginPlace = place;
            }
        } else if (requestCode == TO_PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            Place place = handlePlaceSelected(resultCode, data);
            if (place != null) {
                mDestinationView.setText(place.getAddress());
                mDestinationPlace = place;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                controller.attemptClose();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onBackPressed() {
        controller.attemptClose();
    }

    /**
     * Initialize all the UI elements of this activity
     */
    private void initUI() {
        mTripNameView = (EditText) findViewById(R.id.trip_name);
        mLeavingFromView = (EditText) findViewById(R.id.leaving_from);
        mDestinationView = (EditText) findViewById(R.id.destination);

        mDateFromView = (TextView) findViewById(R.id.date_from);
        mDateToView = (TextView) findViewById(R.id.date_to);
        mPrivateHelpView = (TextView) findViewById(R.id.trip_private_help);
        mTripNameErrorView = (TextView) findViewById(R.id.trip_name_error);

        mLeavingFromWrapper = (TextInputLayout) findViewById(R.id.leaving_from_wrapper);
        mDestinationWrapper = (TextInputLayout) findViewById(R.id.destination_wrapper);

        mPrivateView = (CheckBox) findViewById(R.id.private_check);
        mTransportation = (Spinner) findViewById(R.id.transportation);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.trip_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        setOnClickListeners();

        // initialize the UI for either creating or editing a trip
        controller.populateUI();
    }

    /**
     * Set all the onClickListeners on views of this activity
     */
    private void setOnClickListeners() {
        // Set Checkbox Listener for changing Help Text
        mPrivateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePrivateCheckbox();
            }
        });

        // Set the TextChanged listener to clear TripName error messages
        mTripNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                controller.hideError(mTripNameView);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set the Locations listener to show the PlaceAutocomplete Dialogs
        mLeavingFromView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaceAutocompleteIntent(FROM_PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        mLeavingFromView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                controller.hideError(mLeavingFromView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDestinationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaceAutocompleteIntent(TO_PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        mDestinationView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                controller.hideError(mDestinationView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /**
     * Start the PlaceAutocomplete Intent for the user to selecte a google place
     *
     * @param requestCode The request code to be returned after this Intent finishes
     */
    private void startPlaceAutocompleteIntent(int requestCode) {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    /**
     * Get the Place object once user has selected a place from the dialog
     *
     * @param resultCode Activity result from the PlaceAutocomplete Intent response
     * @param data       The Data from the PlaceAutocomplete Intent
     * @return The Place object selected by the user
     */
    private Place handlePlaceSelected(int resultCode, Intent data) {
        Place place = null;
        if (resultCode == RESULT_OK) {
            place = PlaceAutocomplete.getPlace(this, data);
            Log.i(TAG, "Place: " + place.getName());
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(this, data);
            // TODO: Handle the error.
            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
        return place;
    }

    /**
     * Hide the soft keyboard
     */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Change the help text according to checkbox check state
     */
    public void togglePrivateCheckbox() {
        mPrivateHelpView.setText(mPrivateView.isChecked()
                ? R.string.help_trip_private
                : R.string.help_trip_public);
    }

    /**
     * Show an alert dialog with cancel and ok options
     * Triggered when the user tries to close the activity after making changes
     *
     * @param positiveListener Callback if user presses OK button
     * @param negativeListener Callback if user presses Cancel button
     */
    public void showAlertDialog(DialogInterface.OnClickListener positiveListener,
                                DialogInterface.OnClickListener negativeListener,
                                String message) {
        hideKeyboard();
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, positiveListener)
                .setNegativeButton(android.R.string.no, negativeListener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Show the date picker dialog and update the calendar with selected date
     *
     * @param listener Callback for when user picks a date
     * @param calendar Calendar to save selected date to
     */
    public void showDatePickerDialog(DatePickerDialog.OnDateSetListener listener, Calendar calendar) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(controller.getMinDateAllowed(calendar));
        hideKeyboard();
        datePickerDialog.show();
    }

    /**
     * Called when the user presses the create trip button
     */
    public void createTripButtonPressed(View view) {
        controller.attemptSaveTrip();
    }

    /**
     * Called to show soft keyboard
     */
    public void showKeyBoard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Finish this activity and go back to previous activity on the stack
     */
    public void exitActivity() {
        finish();
        // tell the parent activity it has been updated
    }

    /* GETTERS */

    public EditText getTripNameView() {
        return mTripNameView;
    }

    public TextView getTripNameErrorView() {
        return mTripNameErrorView;
    }

    public CheckBox getPrivateView() {
        return mPrivateView;
    }

    public EditText getLeavingFromView() {
        return mLeavingFromView;
    }

    public EditText getDestinationView() {
        return mDestinationView;
    }

    public Calendar getCalendarFrom() {
        return mCalendarFrom;
    }

    public Calendar getCalendarTo() {
        return mCalendarTo;
    }

    public Spinner getTransportation() {
        return mTransportation;
    }

    public TextView getDateToView() {
        return mDateToView;
    }

    public TextView getDateFromView() {
        return mDateFromView;
    }

    public TextInputLayout getLeavingFromWrapper() {
        return mLeavingFromWrapper;
    }

    public TextInputLayout getDestinationWrapper() {
        return mDestinationWrapper;
    }

    public Place getOriginPlace() {
        return mOriginPlace;
    }

    public Place getDestinationPlace() {
        return mDestinationPlace;
    }
}
