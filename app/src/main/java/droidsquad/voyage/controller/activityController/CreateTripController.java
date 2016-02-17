package droidsquad.voyage.controller.activityController;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import droidsquad.voyage.R;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.api.GooglePlacesAPI;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.CreateTripActivity;

public class CreateTripController {
    private CreateTripActivity activity;

    public CreateTripController(CreateTripActivity activity) {
        this.activity = activity;
    }

    /**
     * Display alert dialog if user has unsaved changes, exit activity otherwise
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
            }, activity.getString(R.string.create_trip_alert_dialog_message));
        }
    }

    /**
     * Shows the date picker dialog and updates the calendar with date selected
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
        String creatorId = ParseTripModel.getUser();

        // Get all the information from the views
        String tripName = activity.getTripNameView().getText().toString();
        String transportation = activity.getTransportation().getSelectedItem().toString();

        boolean isPrivate = activity.getPrivateView().isChecked();
        boolean hasError = false;

        /* Check for errors */
        if (tripName.length() < 3) {
            activity.displayError(activity.getTripNameView(), activity.getString(R.string.error_trip_name));
            activity.setFocus(activity.getTripNameView());
            hasError = true;
        }

        if (activity.getOriginPlace() == null) {
            activity.displayError(activity.getLeavingFromView(), activity.getString(R.string.error_trip_location));
            activity.setFocus(activity.getLeavingFromView());
            hasError = true;
        }

        if (activity.getDestinationPlace() == null) {
            activity.displayError(activity.getDestinationView(), activity.getString(R.string.error_trip_location));
            activity.setFocus(activity.getDestinationView());
            hasError = true;
        }

        if (hasError) return;

        JSONObject leavingFrom = GooglePlacesAPI.getJSONFromPlace(activity.getOriginPlace());
        JSONObject destination = GooglePlacesAPI.getJSONFromPlace(activity.getDestinationPlace());

        Date dateFrom = activity.getCalendarFrom().getTime();
        Date dateTo = activity.getCalendarTo().getTime();

        Trip newTrip = new Trip(tripName, creatorId, transportation, leavingFrom,
                destination, isPrivate, dateFrom, dateTo);

        finalizeTripCheck(newTrip);
    }

    /**
     * Finalization: Before saving the trip to parse, first check if the trip overlaps with
     * any other trips the user is already enrolled in
     */
    public void finalizeTripCheck(final Trip newTrip) {
        ParseTripModel.searchForAllTrips(new ParseTripModel.ParseTripCallback() {
            @Override
            public void onCompleted(ArrayList<Trip> trip) {
                if (!compareForOverlaps(newTrip, trip)) {
                    completeSave(newTrip);
                }
            }
        });
    }

    /**
     * Check for trip overlaps between existing trips for a user, and a newly created one
     * @param newTrip
     * @param trip
     * @return
     */
    public boolean compareForOverlaps(final Trip newTrip, ArrayList<Trip> trip) {
        for(Trip t: trip) {
            if(newTrip.overlaps(t)) {
                String message = activity.getString(R.string.error_overlap) + t.getName() +
                        activity.getString(R.string.error_overlap_continue);
                activity.showAlertDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        completeSave(newTrip);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }, message);
                return true;
            }
        }
        return false;
    }

    /**
     * Completes the save process after we know adding a new trip is a valid action
     * @param newTrip Trip object to save to the backend
     */
    public void completeSave(Trip newTrip) {
        ParseTripModel.saveTrip(newTrip);
        // TODO show progress spinning thingy and wait till the trip has been saved to parse

        // if success
        activity.setResult(Constants.RESULT_CODE_TRIP_CREATED);
        activity.finish();

        // TODO else : stay on the same page and show snackBar with error and button to retry.
        // for reference for snackBar with button you can look at LoginActivity.java
    }
}
