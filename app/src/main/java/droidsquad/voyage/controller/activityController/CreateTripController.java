package droidsquad.voyage.controller.activityController;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.view.activity.CreateTripActivity;
import droidsquad.voyage.model.api.GooglePlacesAPI;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.objects.Trip;

public class CreateTripController {
    private CreateTripActivity activity;
    private GooglePlacesAPI googlePlacesModel;
    private Trip trip;
    private boolean edit = false;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
    private Calendar calendarFrom;
    private Calendar calendarTo;

    public CreateTripController(CreateTripActivity activity) {
        this.activity = activity;
        googlePlacesModel = new GooglePlacesAPI(activity);
        // determine whether this is an editing activity or not
        trip = (Trip) activity.getIntent().getSerializableExtra(
                activity.getString(R.string.intent_key_trip));
        edit = activity.getIntent().getBooleanExtra(activity.getString(R.string.edit_trip), edit);
    }

    /**
     * Display alert dialog if user has unsaved changes, exit activity otherwise
     */
    public void attemptClose() {
        if (!activity.hasChanges()) {
            activity.exitActivity();
        } else {
            activity.hideKeyboard();
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
     * Get the minimum date user is allowed to pick from the calendar
     *
     * @param calendar Calendar for which minimum date will be computed from
     * @return {long} The minimum date allowed in Millis
     */
    public long getMinDateAllowed(Calendar calendar) {
        return (calendar.equals(calendarFrom))
                ? System.currentTimeMillis() - 1000
                : calendarFrom.getTimeInMillis() - 1000;
    }

    /**
     * TODO: Add documentation
     *
     * @param textView
     * @param i
     */
    public void setUpPlacesAutofill(AutoCompleteTextView textView, int i) {
        googlePlacesModel.setUpPlacesAutofill(textView, i);
    }

    /**
     * Attempts to create a Trip with the information in the views
     */
    public void attemptSaveTrip() {
        if (!edit) {
            attemptCreateTrip();
        }
        else {
            attemptUpdateTrip();
        }
    }

    private void attemptUpdateTrip() {
        // TODO: update the trip
        Toast.makeText(activity, "Updating trip",
                Toast.LENGTH_SHORT).show();
    }

    private void attemptCreateTrip() {
        // Get all the information from the views
        String tripName = activity.getTripNameView().getText().toString();
        String leavingFrom = googlePlacesModel.getSourceCityJSON().toString();
        String destination = googlePlacesModel.getDestCityJSON().toString();
        String transportation = activity.getTransportation().getSelectedItem().toString();
        String creatorId = ParseTripModel.getUser();

        Date dateFrom = calendarFrom.getTime();
        Date dateTo = calendarTo.getTime();

        boolean isPrivate = activity.getPrivateView().isChecked();
        boolean hasError = hasError(tripName, leavingFrom, destination, transportation, creatorId,
                dateFrom, dateTo);

        if(hasError) return;

        Trip newTrip = new Trip(tripName, leavingFrom, destination, isPrivate,
                dateFrom, dateTo, transportation, creatorId);

        finalizeTripCheck(newTrip);
    }

    /**
     * Populates UI depending on whether the trip is being created or edited
     */
    public void populateUI() {
        initTextFields();
        initDatePickers();
    }

    private void initTextFields() {
        if (isEditing()) {
            Trip trip = getTrip();
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
                JSONObject origin = new JSONObject(trip.getOrigin());
                JSONObject dest = new JSONObject(trip.getDestination());

                activity.getLeavingFromView().setText(origin.get("address").toString());
                activity.getDestinationView().setText(dest.get("address").toString());

                googlePlacesModel.setmSourceID(origin.get("placeId").toString());
                googlePlacesModel.setmSourceCityName(origin.get("city").toString());
                googlePlacesModel.setmSourceCityFullAddress(origin.get("address").toString());

                googlePlacesModel.setmDestID(dest.get("placeId").toString());
                googlePlacesModel.setmDestCityName(dest.get("city").toString());
                googlePlacesModel.setmDestCityFullAddress(dest.get("address").toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set up default dates on the date pickers
     */
    private void initDatePickers() {

        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();

        if(isEditing()) {
            Trip trip = getTrip();
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

    private boolean hasError(String tripName, String leavingFrom,String destination, String
            transportation, String creatorId, Date dateFrom, Date dateTo) {

        boolean hasError = false;

        // TODO: Scroll to and set the focus on the first View that has error
        /* Check for errors */
        if (tripName.length() < 3) {
            activity.displayError(activity.getTripNameView(), activity.getString(R.string.error_trip_name));
            hasError = true;
        }

        if(!googlePlacesModel.isSourceCityValid()) {
            activity.displayError(activity.getLeavingFromWrapper(), activity.getString(R.string.error_trip_location));
            hasError = true;
        }

        if(!googlePlacesModel.isDestCityValid()) {
            activity.displayError(activity.getDestinationWrapper(), activity.getString(R.string.error_trip_location));
            hasError = true;
        }

        return hasError;
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
     * @param newTrip
     */
    public void completeSave(Trip newTrip) {
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

    public Trip getTrip() {
        return trip;
    }

    public boolean isEditing() {
        return edit;
    }

}
