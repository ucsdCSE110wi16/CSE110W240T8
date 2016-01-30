package droidsquad.voyage.activity;

import android.app.DatePickerDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.CreateTripController;

public class CreateTripActivity extends AppCompatActivity {
    private CreateTripController controller;

    private TextInputLayout mMemberLimitWrapper;
    private TextInputLayout mLeavingFromWrapper;
    private TextInputLayout mDestinationWrapper;
    private TextInputLayout mDateFromWrapper;
    private TextInputLayout mDateToWrapper;

    private EditText mTripNameView;
    private EditText mDateFromView;
    private EditText mDateToView;

    private TextView mPrivateHelpView;

    private CheckBox mPrivateView;
    private Spinner mTransportation;

    private AutoCompleteTextView mLeavingFromView;
    private AutoCompleteTextView mDestinationView;

    private Calendar calendarFrom;
    private Calendar calendarTo;

    private long minDateAllowed;
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
        mPrivateView = (CheckBox) findViewById(R.id.private_check);
        mTransportation = (Spinner) findViewById(R.id.transportation);
        mLeavingFromView = (AutoCompleteTextView) findViewById(R.id.leaving_from);
        mDestinationView = (AutoCompleteTextView) findViewById(R.id.destination);

        mPrivateHelpView = (TextView) findViewById(R.id.trip_private_help);

        mMemberLimitWrapper = (TextInputLayout) findViewById(R.id.member_limit_wrapper);
        mLeavingFromWrapper = (TextInputLayout) findViewById(R.id.leaving_from_wrapper);
        mDestinationWrapper = (TextInputLayout) findViewById(R.id.destination_wrapper);
        mDateFromWrapper = (TextInputLayout) findViewById(R.id.date_from_wrapper);
        mDateToWrapper = (TextInputLayout) findViewById(R.id.date_to_wrapper);

        mDateFromView = mDateFromWrapper.getEditText();
        mDateToView = mDateToWrapper.getEditText();

        controller.setUpPlacesAutofill(mLeavingFromView, 0);
        controller.setUpPlacesAutofill(mDestinationView, 1);

        // Set Checkbox Listener for changing Help Text
        mPrivateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPrivateHelpView.setText(
                        (mPrivateView.isChecked())
                                 ? R.string.help_trip_private
                                 : R.string.help_trip_public
                );
            }
        });

        // Set up Date Picker listeners
        mDateFromView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minDateAllowed = System.currentTimeMillis() - 1000;
                setFromDate(v);
            }
        });

        mDateToView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minDateAllowed = calendarFrom.getTimeInMillis() - 1000;
                setToDate(v);
            }
        });

        // Set up default dates
        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();
        calendarTo.add(Calendar.DAY_OF_WEEK, DEFAULT_TRIP_LENGTH);
        mDateFromView.setText(dateFormat.format(calendarFrom.getTime()));
        mDateToView.setText(dateFormat.format(calendarTo.getTime()));

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.trip_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void setFromDate(View view) {
        showDateDialog(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                long previousDateTime = calendarFrom.getTimeInMillis();
                calendarFrom.set(year, monthOfYear, dayOfMonth);

                // Compute difference and adjust the toDate accordingly
                long diff = calendarFrom.getTimeInMillis() - previousDateTime;
                calendarTo.setTimeInMillis(calendarTo.getTimeInMillis() + diff);

                mDateToView.setText(dateFormat.format(calendarTo.getTime()));
                mDateFromView.setText(dateFormat.format(calendarFrom.getTime()));
            }
        }, calendarFrom);
    }

    public void setToDate(View view) {
        showDateDialog(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarTo.set(year, monthOfYear, dayOfMonth);
                mDateToView.setText(dateFormat.format(calendarTo.getTime()));
            }
        }, calendarTo);
    }

    public void showDateDialog(DatePickerDialog.OnDateSetListener listener, Calendar cal) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, listener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(minDateAllowed);
        datePickerDialog.show();
    }


    /**
     * Called when the user presses the create trip button
     * TODO: Move to Controller and implement checks
     */
    public void attemptCreateTrip(View view) {
        String tripName = mTripNameView.getText().toString();

        if (tripName.length() < 3) {
            mTripNameView.setError("Trip name must be at least 3 characters long.");
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }
}
