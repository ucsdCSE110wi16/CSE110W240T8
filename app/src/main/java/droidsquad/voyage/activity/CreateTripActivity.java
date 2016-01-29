package droidsquad.voyage.activity;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.CreateTripController;
import droidsquad.voyage.model.Trip;


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

    private static final int DEFAULT_TRIP_LENGTH = 7;
    private static final String TAG = CreateTripActivity.class.getSimpleName();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        controller = new CreateTripController(this);

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

        // Spinner Drop down elements
        String categories[] = {"Plane", "Metro", "Bus", "Car"};

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mTransportation.setAdapter(dataAdapter);

        // Set up default dates
        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();
        calendarTo.add(Calendar.DAY_OF_WEEK, DEFAULT_TRIP_LENGTH);
        mDateFromView.setText(dateFormat.format(calendarFrom.getTime()));
        mDateToView.setText(dateFormat.format(calendarTo.getTime()));
    }

    public void showFromDateDialog(View view) {
        showDateDialogAndUpdateView(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarFrom.set(year, monthOfYear, dayOfMonth);
                mDateFromView.setText(dateFormat.format(calendarFrom.getTime()));
            }
        }, calendarFrom);
    }

    public void showToDateDialog(View view) {
        showDateDialogAndUpdateView(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarTo.set(year, monthOfYear, dayOfMonth);
                mDateToView.setText(dateFormat.format(calendarTo.getTime()));
            }
        }, calendarTo);
    }

    public void showDateDialogAndUpdateView(DatePickerDialog.OnDateSetListener listener, Calendar cal) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }


    /**
     * Called when the user presses the create trip button
     * TODO: Move to Controller and implement checks
     */
    public void attemptCreateTrip(View view) {
        String tripName = mTripNameView.getText().toString();

        int memberLimit = 0;
        if (!isEmpty(mMemberLimitView)) {
            memberLimit = Integer.parseInt(mMemberLimitView.getText().toString());
        }

        boolean privateTrip = mPrivateView.isChecked();

        String leavingFrom = mLeavingFromView.getText().toString();
        String destination = mDestinationView.getText().toString();

        Date dateFrom = calendarFrom.getTime();
        Date dateTo = calendarTo.getTime();

        String transportation = mTransportation.getSelectedItem().toString();

        // TODO: CHANGE HERE
        Trip newTrip = new Trip(tripName, leavingFrom, destination, privateTrip,
                memberLimit, dateFrom, dateTo, transportation);
        newTrip.save();

        //Create toast
        CharSequence text = "Trip Created";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private static boolean checkDates(DatePicker from, DatePicker to) {
        // TODO: Move to Controller and implement
        return false;
    }
}
