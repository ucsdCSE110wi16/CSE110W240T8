package droidsquad.voyage.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

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

    private AutoCompleteTextView mLeavingFromView;
    private AutoCompleteTextView mDestinationView;

    private CheckBox mPrivateView;
    private Spinner mTransportation;

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

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.trip_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        initDatePickers();
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

        mDateFromView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.showDateDialog(calendarFrom, mDateFromView);
            }
        });

        mDateToView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.showDateDialog(calendarTo, mDateToView);
            }
        });
    }


    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
        return mMemberLimitWrapper.getEditText();
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

    public Calendar getCalendarFrom() { return calendarFrom; }

    public Calendar getCalendarTo() {
        return calendarTo;
    }

    public Spinner getTransportation() {
        return mTransportation;
    }

    public EditText getDateToView() {
        return mDateToView;
    }

    public EditText getDateFromView() {
        return mDateFromView;
    }
}
