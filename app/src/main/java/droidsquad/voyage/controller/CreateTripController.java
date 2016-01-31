package droidsquad.voyage.controller;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import droidsquad.voyage.activity.CreateTripActivity;
import droidsquad.voyage.model.GooglePlacesAPI;
import droidsquad.voyage.model.ParseModel;
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
        String leavingFrom = activity.getLeavingFromView().getText().toString();
        String destination = activity.getDestinationView().getText().toString();
        String transportation = activity.getTransportation().getSelectedItem().toString();
        Date dateFrom = activity.getCalendarFrom().getTime();
        Date dateTo = activity.getCalendarTo().getTime();
        boolean privateTrip = activity.getPrivateView().isChecked();

        int memberLimit = (!isEmpty(activity.getMemberLimitView()))
                ? Integer.parseInt(activity.getMemberLimitView().getText().toString())
                : 0;

        Trip newTrip = new Trip(tripName, leavingFrom, destination, privateTrip,
                memberLimit, dateFrom, dateTo, transportation);

        if (tripName.length() < 3) {
            activity.displayError(activity.getTripNameView(), "Name must be at least 3 character long");
            return;
        }

        ParseModel.saveTrip(newTrip);
        activity.exitActivity();

        CharSequence text = "Trip Created";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(activity.getApplicationContext(), text, duration);
        toast.show();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    public void setUpPlacesAutofill(AutoCompleteTextView textView, int i) {
        googlePlacesModel.setUpPlacesAutofill(textView, i);
    }
}
