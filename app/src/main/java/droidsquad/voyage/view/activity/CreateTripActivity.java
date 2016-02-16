package droidsquad.voyage.view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.activityController.CreateTripController;
import droidsquad.voyage.model.api.GooglePlacesAPI;
import droidsquad.voyage.model.objects.Trip;

public class CreateTripActivity extends AppCompatActivity {
    private CreateTripController controller;

    private TextInputLayout mLeavingFromWrapper;
    private TextInputLayout mDestinationWrapper;

    private EditText mTripNameView;

    private TextView mDateFromView;
    private TextView mDateToView;
    private TextView mPrivateHelpView;
    private TextView mTripNameErrorView;

    private AutoCompleteTextView mLeavingFromView;
    private AutoCompleteTextView mDestinationView;

    private CheckBox mPrivateView;
    private Spinner mTransportation;


    private static final String TAG = CreateTripActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        controller = new CreateTripController(this);

        initUI();
    }

    /**
     * Initialize all the UI elements of this activity
     */
    private void initUI() {
        mTripNameView = (EditText) findViewById(R.id.trip_name);
        mPrivateView = (CheckBox) findViewById(R.id.private_check);
        mTransportation = (Spinner) findViewById(R.id.transportation);
        mLeavingFromView = (AutoCompleteTextView) findViewById(R.id.leaving_from);
        mDestinationView = (AutoCompleteTextView) findViewById(R.id.destination);

        mDateFromView = (TextView) findViewById(R.id.date_from);
        mDateToView = (TextView) findViewById(R.id.date_to);

        mPrivateHelpView = (TextView) findViewById(R.id.trip_private_help);
        mTripNameErrorView = (TextView) findViewById(R.id.trip_name_error);

        mLeavingFromWrapper = (TextInputLayout) findViewById(R.id.leaving_from_wrapper);
        mDestinationWrapper = (TextInputLayout) findViewById(R.id.destination_wrapper);

        // Set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.trip_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        controller.setUpPlacesAutofill(mLeavingFromView, 0);
        controller.setUpPlacesAutofill(mDestinationView, 1);

        // Set Checkbox Listener for changing Help Text
        mPrivateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePrivateCheckbox();
            }
        });

        // TODO: Listen for text changes on mTripNameView and clear the error when it changes
        // Clear error by calling mTripNameErrorView.setVisibility(View.GONE);

        controller.populateUI();

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
     * Displays the error on the given view
     *
     * @param view  View to display the error on
     * @param error The error to be displayed
     */
    public void displayError(View view, String error) {
        if (view == mTripNameView) {
            mTripNameErrorView.setText(error);
            mTripNameErrorView.setVisibility(View.VISIBLE);
        } else {
            ((TextInputLayout) view).setError(error);
        }
    }

    /**
     * @return true if user has made changes to the forms
     */
    public boolean hasChanges() {
        return mTripNameView.getText().length() > 0 ||
                mLeavingFromView.getText().length() > 0 ||
                mDestinationView.getText().length() > 0;
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

    public CheckBox getPrivateView() {
        return mPrivateView;
    }

    public AutoCompleteTextView getLeavingFromView() {
        return mLeavingFromView;
    }

    public AutoCompleteTextView getDestinationView() {
        return mDestinationView;
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
}
