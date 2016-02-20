package droidsquad.voyage.controller.activityController;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.DatePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.api.GooglePlacesAPI;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.CreateTripActivity;

public class CreateTripController {
    private CreateTripActivity activity;
    private Trip trip;
    private boolean edit = false;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
    private Calendar calendarFrom;
    private Calendar calendarTo;

    public CreateTripController(CreateTripActivity activity) {
        this.activity = activity;
        trip = activity.getIntent().getParcelableExtra(
                activity.getString(R.string.intent_key_trip));
        edit = activity.getIntent().getBooleanExtra(activity.getString(R.string.edit_trip), edit);
    }

    public void populateUI() {
        initTextFields();
        initDatePickers();
    }
    private void initTextFields() {
        if (edit) {     // only populate text fields if editing a trip
            activity.getTripNameView().setText(trip.getName());
            activity.getPrivateView().setChecked(trip.isPrivate());

            int pos = 0;
            String[] transportationMethods = activity.getResources().getStringArray(R.array.array_transportation_modes);
            for (int i = 0; i < transportationMethods.length; i++) {
                if (trip.getTransportation().equals(transportationMethods[i])) {
                    pos = i;
                    break;
                }
            }
            activity.getTransportation().setSelection(pos);

            try {
                JSONObject origin = trip.getOrigin();
                JSONObject dest = trip.getDestination();

                activity.getLeavingFromView().setText(origin.get("address").toString());
                activity.getDestinationView().setText(dest.get("address").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
     * Update the dates views to display current state of calendars
     */
    private void updateDateViews() {
        activity.getDateFromView().setText(dateFormat.format(calendarFrom.getTime()));
        activity.getDateToView().setText(dateFormat.format(calendarTo.getTime()));
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

                updateDateViews();
            }
        }, calendar);
    }

    /**
     * Set up default dates on the date pickers
     */
    private void initDatePickers() {

        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();

        if(edit) {
            calendarFrom.setTimeInMillis(trip.getDateFrom().getTime());
            calendarTo.setTimeInMillis(trip.getDateTo().getTime());
        }
        else {

            calendarTo.add(Calendar.DAY_OF_WEEK, Constants.DEFAULT_TRIP_LENGTH);
        }

        activity.getDateFromView().setText(dateFormat.format(calendarFrom.getTime()));
        activity.getDateToView().setText(dateFormat.format(calendarTo.getTime()));

        activity.getDateFromView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(calendarFrom);
            }
        });
        activity.getDateToView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(calendarTo);
            }
        });
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
     * Attempts to save a Trip with the information in the views
     */
    public void attemptSaveTrip() {
        if (edit) {
            attemptUpdateTrip();
        }
        else {
            attemptCreateTrip();
        }
    }

    private void attemptCreateTrip() {
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

    private void attemptUpdateTrip() {

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
