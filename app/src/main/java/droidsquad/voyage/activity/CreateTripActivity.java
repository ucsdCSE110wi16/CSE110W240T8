package droidsquad.voyage.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.CreateTripController;


public class CreateTripActivity extends AppCompatActivity {
    private CreateTripController controller;

    private EditText mTripNameView;
    private EditText mMemberLimitView;
    private CheckBox mPrivateView;

    private AutoCompleteTextView mLeavingFromView;
    private AutoCompleteTextView mDestinationView;

    private Spinner mTransportation;
    private TextView mDateFromView;
    private TextView mDateToView;
    private Calendar calendarFrom;
    private Calendar calendarTo;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
    private static final int DEFAULT_TRIP_LENGTH = 7;
    private static final String TAG = CreateTripActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        controller = new CreateTripController(this);

        initUI();
    }

    private void initUI() {
        // Set up the trip form.
        mTripNameView = (EditText) findViewById(R.id.trip_name);
        mMemberLimitView = (EditText) findViewById(R.id.member_limit);
        mPrivateView = (CheckBox) findViewById(R.id.private_check);
        mTransportation = (Spinner) findViewById(R.id.transportation);
        mDateFromView = (TextView) findViewById(R.id.date_from);
        mDateToView = (TextView) findViewById(R.id.date_to);
        mLeavingFromView = (AutoCompleteTextView) findViewById(R.id.leaving_from);
        mDestinationView = (AutoCompleteTextView) findViewById(R.id.destination);

        controller.setUpPlacesAutofill(mLeavingFromView, 0);
        controller.setUpPlacesAutofill(mDestinationView, 1);

        // populate the transportation mode spinners
        initTransportationSpinner();
        // populate/init the from and to date pickers
        initDatePickers();
    }

    private void initTransportationSpinner() {
        // Spinner Drop down elements
        // TODO: do not hardcode these, move this somewhere else...
        String categories[] = {"Plane", "Metro", "Bus", "Car"};

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        mTransportation.setAdapter(dataAdapter);
    }

    /**
     * Set up default dates
     */
    private void initDatePickers() {
        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();
        calendarTo.add(Calendar.DAY_OF_WEEK, DEFAULT_TRIP_LENGTH);
        mDateFromView.setText(dateFormat.format(calendarFrom.getTime()));
        mDateToView.setText(dateFormat.format(calendarTo.getTime()));
    }

    /**
     * Called when the from date picker is pressed
     */
    public void showFromDateDialog(View view) {
        controller.showDateDialog(calendarFrom, mDateFromView);
    }

    /**
     * Called when the to date picker is pressed
     */
    public void showToDateDialog(View view) {
        controller.showDateDialog(calendarTo, mDateToView);
    }

    /**
     * Called when the user presses the create trip button
     */
    public void createTripButtonPressed(View view) {
        controller.attemptCreateTrip();
    }

    // TODO: show what field is missing, navigate to it perhaps. will need more input args
    public void notifyTripInvalid() {

    }

    public void exitActivity() {
        Intent intent = new Intent(getApplicationContext(), TripListActivity.class);
        startActivity(intent);
    }

    /* GETTERS */

    public EditText getTripNameView() {
        return mTripNameView;
    }

    public EditText getMemberLimitView() {
        return mMemberLimitView;
    }

    public CheckBox getPrivateView() {
        return mPrivateView;
    }

    public AutoCompleteTextView getLeavingFromView() {
        return mLeavingFromView;
    }

    public AutoCompleteTextView getDestinationView() {
        return mDestinationView;
    }

    public Calendar getCalendarFrom() {
        return calendarFrom;
    }

    public Calendar getCalendarTo() {
        return calendarTo;
    }

    public Spinner getTransportation() {
        return mTransportation;
    }

}
