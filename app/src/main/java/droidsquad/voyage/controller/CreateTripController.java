package droidsquad.voyage.controller;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import droidsquad.voyage.R;
import droidsquad.voyage.activity.CreateTripActivity;
import droidsquad.voyage.model.GooglePlacesAPI;
import droidsquad.voyage.model.ParseModel;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.Trip;

public class CreateTripController {
    private CreateTripActivity activity;
    private GooglePlacesAPI googlePlacesModel;

    public CreateTripController(CreateTripActivity activity) {
        this.activity = activity;
        googlePlacesModel = new GooglePlacesAPI(activity);

    }

    /**
     * Display alert dialog if user has unsaved changes
     */
    public void attemptClose() {
        if (!activity.hasChanges()) {
            activity.exitActivity();
        } else {
            activity.showAlertDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.exitActivity();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
    }

    /**
     * Shows the date picker dialog and updates the calendar upon date selected
     *
     * @param calendar Calendar to be contain date selected
     */
    public void showDateDialog(final Calendar calendar) {
        activity.showDatePickerDialog(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendarFrom = activity.getCalendarFrom();
                Calendar calendarTo = activity.getCalendarTo();

                if (calendar.equals(calendarFrom)) {
                    long previousDateTime = calendarFrom.getTimeInMillis();

                    // Compute difference and adjust the toDate accordingly
                    calendarFrom.set(year, monthOfYear, dayOfMonth);
                    long diff = calendarFrom.getTimeInMillis() - previousDateTime;

                    // set to calendar
                    calendarTo.setTimeInMillis(calendarTo.getTimeInMillis() + diff);
                } else {
                    calendar.set(year, monthOfYear, dayOfMonth);
                }

                activity.updateDateViews();
            }
        }, calendar);
    }

    /**
     * Get the minimum date user is allowed to pick from the calendar
     *
     * @param calendar Calendar for which minimum date will be computed from
     * @return {long} The minimum date allowed in Millis
     */
    public long getMinDateAllowed(Calendar calendar) {
        return (calendar.equals(activity.getCalendarFrom()))
                ? System.currentTimeMillis() - 1000
                : activity.getCalendarFrom().getTimeInMillis() - 1000;
    }

    /**
     * Attempts to create a Trip with the information in the views
     */
    public void attemptCreateTrip() {
        // Get all the information from the views
        String tripName = activity.getTripNameView().getText().toString();
        String leavingFrom = googlePlacesModel.getSourceCityJSON().toString();
        String destination = googlePlacesModel.getDestCityJSON().toString();
        //JSONObject leavingFrom = googlePlacesModel.getSourceCityJSON();
        //JSONObject destination = googlePlacesModel.getDestCityJSON();
        String transportation = activity.getTransportation().getSelectedItem().toString();
        Date dateFrom = activity.getCalendarFrom().getTime();
        Date dateTo = activity.getCalendarTo().getTime();
        String creatorId = ParseTripModel.getUser();
        boolean privateTrip = activity.getPrivateView().isChecked();
        boolean error = false;

        int memberLimit;
        try {
            memberLimit = (!isEmpty(activity.getMemberLimitView()))
                    ? Integer.parseInt(activity.getMemberLimitView().getText().toString())
                    : 0;
        }
        catch(NumberFormatException e) {
            memberLimit = 0;
        }

        Trip newTrip = new Trip(tripName, leavingFrom, destination, privateTrip,
                memberLimit, dateFrom, dateTo, transportation, creatorId);

        if (tripName.length() < 3) {
            activity.displayError(activity.getTripNameView(), activity.getString(R.string.error_trip_name));
            error = true;
        }

        if(!privateTrip && memberLimit <= 0) {
            activity.displayError(activity.getMemberLimitView(), activity.getString(R.string.error_member_limit));
            error = true;
        }

        if(!googlePlacesModel.isSourceCityValid()) {
            activity.displayError(activity.getLeavingFromView(), activity.getString(R.string.error_trip_location));
            error = true;
        }

        if(!googlePlacesModel.isDestCityValid()) {
            activity.displayError(activity.getDestinationView(), activity.getString(R.string.error_trip_location));
            error = true;
        }

        if(error)
            return;

        ParseTripModel.saveTrip(newTrip);

        // TODO show progress spinning thingy and wait till the trip has been saved to parse

        // if success
        activity.exitActivity();
        CharSequence text = "Trip Created";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(activity.getApplicationContext(), text, duration);
        toast.show();

        // TODO else : stay on the same page and show snackBar with error and button to retry.
        // for reference for snackBar with button you can look at LoginActivity.java

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    public void setUpPlacesAutofill(AutoCompleteTextView textView, int i) {
        googlePlacesModel.setUpPlacesAutofill(textView, i);
    }
}
