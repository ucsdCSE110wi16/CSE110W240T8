package droidsquad.voyage.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    private AutoCompleteTextView mLeavingFromView;
    private AutoCompleteTextView mDestinationView;

    private CheckBox mPrivateView;
    private Spinner mTransportation;

    private Calendar calendarFrom;
    private Calendar calendarTo;

    private long minDateAllowed;
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
                if (mPrivateView.isChecked()) {
                    mPrivateHelpView.setText(R.string.help_trip_private);
                    mMemberLimitWrapper.setVisibility(View.GONE);
                } else {
                    mPrivateHelpView.setText(R.string.help_trip_public);
                    mMemberLimitWrapper.setVisibility(View.VISIBLE);
                }
            }
        });

        initDatePickers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                attemptClose();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
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
        hideKeyboard();
        datePickerDialog.show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Called when user presses the close toolbar button
     */
    public void attemptClose() {
        controller.attemptClose();
    }

    public void showAlertDialog(DialogInterface.OnClickListener positiveListener,
                                DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to discard the changes?")
                .setPositiveButton(android.R.string.yes, positiveListener)
                .setNegativeButton(android.R.string.no, negativeListener)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
