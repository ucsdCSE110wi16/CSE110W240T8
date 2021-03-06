package droidsquad.voyage.controller.activityController;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Network;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import droidsquad.voyage.R;
import droidsquad.voyage.model.api.GooglePlacesAPI;
import droidsquad.voyage.model.objects.Trip;
import droidsquad.voyage.model.parseModels.ParseModel;
import droidsquad.voyage.model.parseModels.ParseTripModel;
import droidsquad.voyage.util.Constants;
import droidsquad.voyage.util.NetworkAlerts;
import droidsquad.voyage.view.activity.CreateTripActivity;

public class CreateTripController {
    private CreateTripActivity activity;
    private Trip trip;
    private Calendar calendarFrom;
    private Calendar calendarTo;
    private boolean isEditMode;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);
    private static final SimpleDateFormat overlapDateFormat = new SimpleDateFormat("MMM dd", Locale.US);
    private static final String TAG = CreateTripController.class.getSimpleName();

    // Old fields to check for changes
    String oldTripName;
    String oldTransportation;
    boolean oldIsPrivate;
    String oldOrigin;
    String oldDestination;
    Date oldDateFrom;
    Date oldDateTo;

    public CreateTripController(CreateTripActivity activity) {
        this.activity = activity;
        trip = activity.getIntent().getParcelableExtra(activity.getString(R.string.intent_key_trip));
        isEditMode = activity.getIntent().getBooleanExtra(activity.getString(R.string.edit_trip), false);
    }

    public void populateUI() {
        initTextFields();
        initDatePickers();
        getOldFields();
        updateDateViews();
    }

    /**
     * Changes button text from Create to Update
     */
    private void changeCreateButtonText() {
        activity.getCreateTripButton().setText(Constants.UPDATE_TRIP);
    }

    private void getOldFields() {
        if (isEditMode) {
            oldTripName = trip.getName();
            oldIsPrivate = trip.isPrivate();

            try {
                JSONObject origin = trip.getOrigin();
                JSONObject dest = trip.getDestination();
                oldOrigin = origin.get("address").toString();
                oldDestination = dest.get("address").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            oldDateFrom = calendarFrom.getTime();
            oldDateTo = calendarTo.getTime();
            oldTransportation = trip.getTransportation();

        } else {
            oldIsPrivate = activity.getPrivateView().isChecked();
            oldDateFrom = calendarFrom.getTime();
            oldDateTo = calendarTo.getTime();
            oldTransportation = activity.getTransportation().getSelectedItem().toString();
        }
    }

    private void initTextFields() {
        // only populate text fields if editing a trip
        if (isEditMode) {
            changeCreateButtonText();

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
        if (!hasChanges()) {
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
     * Update activity's calendars
     */
    private void updateCalendars(Calendar calendarFrom, Calendar calendarTo) {
        activity.setCalendarFrom(calendarFrom);
        activity.setCalendarTo(calendarTo);
    }

    /**
     * Set up default dates on the date pickers
     */
    private void initDatePickers() {
        calendarFrom = Calendar.getInstance();
        calendarTo = Calendar.getInstance();

        if (isEditMode) {
            calendarFrom.setTimeInMillis(trip.getDateFrom().getTime());
            calendarTo.setTimeInMillis(trip.getDateTo().getTime());
        } else {
            calendarTo.add(Calendar.DAY_OF_WEEK, Constants.DEFAULT_TRIP_LENGTH);
        }

        updateCalendars(calendarFrom, calendarTo);
        updateDateViews();

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

                if (calendar == calendarFrom) {
                    long previousDateTime = calendarFrom.getTimeInMillis();

                    // Compute difference and adjust the toDate accordingly
                    calendarFrom.set(year, monthOfYear, dayOfMonth);
                    long diff = calendarFrom.getTimeInMillis() - previousDateTime;

                    // set to calendar
                    calendarTo.setTimeInMillis(calendarTo.getTimeInMillis() + diff);
                } else {
                    // Calendar TO selected
                    calendar.set(year, monthOfYear, dayOfMonth);
                }

                updateDateViews();
                updateCalendars(calendarFrom, calendarTo);
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
        if (calendar == activity.getCalendarFrom()) {
            Log.d(TAG, "getMinDate of TODAY");
            return System.currentTimeMillis() - 1000;
        } else {
            Log.d(TAG, "getMinDate of FROM");
            return calendarFrom.getTimeInMillis() - 1000;
        }
    }

    /**
     * Attempts to save a Trip with the information in the views
     */
    public void attemptSaveTrip() {
        // Get all the information from the views
        String tripName = activity.getTripNameView().getText().toString();
        String transportation = activity.getTransportation().getSelectedItem().toString();
        boolean isPrivate = activity.getPrivateView().isChecked();

        boolean hasError = false;

        /* Check for errors */
        if (tripName.length() < 3) {
            displayError(activity.getTripNameView(), activity.getString(R.string.error_trip_name));
            setFocus(activity.getTripNameView());
            hasError = true;
        }

        if (activity.getOriginPlace() == null && !isEditMode) {
            displayError(activity.getLeavingFromView(), activity.getString(R.string.error_trip_location));
            hasError = true;
        }

        if (activity.getDestinationPlace() == null && !isEditMode) {
            displayError(activity.getDestinationView(), activity.getString(R.string.error_trip_location));
            hasError = true;
        }

        if (hasError) return;

        JSONObject leavingFrom;
        JSONObject destination;

        if (activity.getOriginPlace() != null) {
            leavingFrom = GooglePlacesAPI.getJSONFromPlace(activity.getOriginPlace());
        } else {
            leavingFrom = trip.getOrigin();
        }

        if (activity.getDestinationPlace() != null) {
            destination = GooglePlacesAPI.getJSONFromPlace(activity.getDestinationPlace());
        } else {
            destination = trip.getDestination();
        }

        Date dateFrom = calendarFrom.getTime();
        Date dateTo = calendarTo.getTime();

        Trip newTrip = (isEditMode) ? trip : new Trip();

        newTrip.setName(tripName);
        newTrip.setTransportation(transportation);
        newTrip.setOrigin(leavingFrom);
        newTrip.setDestination(destination);
        newTrip.setPrivate(isPrivate);
        newTrip.setDateFrom(dateFrom);
        newTrip.setDateTo(dateTo);
        finalizeTripCheck(newTrip);
    }

    /**
     * Displays the error on the given view
     *
     * @param view  View to display the error on
     * @param error The error to be displayed
     */
    public void displayError(View view, String error) {
        if (view == activity.getTripNameErrorView()) {
            ((TextView) view).setText(error);
            view.setVisibility(View.VISIBLE);
        } else {
            ((EditText) view).setError(error);
        }
    }

    /**
     * Hides the error on the given view
     *
     * @param view View to display the error on
     */
    public void hideError(View view) {
        ((EditText) view).setError(null);
    }

    /**
     * Sets focus on the given view
     *
     * @param view View to display the error on
     */
    public void setFocus(View view) {
        if (view == activity.getTripNameView()) {
            if (view.requestFocus()) {
                activity.showKeyBoard(activity.getTripNameView());
            }
        }
    }

    /**
     * @return true if user has made changes to the forms
     */
    public boolean hasChanges() {
        if (!isEditMode) {
            return activity.getTripNameView().getText().length() > 0 ||
                    activity.getPrivateView().isChecked() != oldIsPrivate ||
                    activity.getDestinationPlace() != null ||
                    activity.getOriginPlace() != null ||
                    !activity.getCalendarFrom().getTime().equals(oldDateFrom) ||
                    !activity.getCalendarTo().getTime().equals(oldDateTo) ||
                    !activity.getTransportation().getSelectedItem().toString().equals(oldTransportation);
        } else {
            return !activity.getTripNameView().getText().toString().equals(oldTripName) ||
                    activity.getPrivateView().isChecked() != oldIsPrivate ||
                    activity.getOriginPlace() != null ||
                    activity.getDestinationPlace() != null ||
                    !activity.getCalendarFrom().getTime().equals(oldDateFrom) ||
                    !activity.getCalendarTo().getTime().equals(oldDateTo) ||
                    !activity.getTransportation().getSelectedItem().toString().equals(oldTransportation);
        }
    }

    /**
     * Finalization: Before saving the trip to parse, first check if the trip overlaps with
     * any other trips the user is already enrolled in
     */
    public void finalizeTripCheck(final Trip newTrip) {
        Log.d(TAG, "Finalizing the trip creation checks");
        if (isEditMode) newTrip.setId(trip.getId());
        ParseTripModel.getTrips(new ParseTripModel.TripListCallback() {
            @Override
            public void onSuccess(List<Trip> trips) {
                if (!compareForOverlaps(newTrip, trips)) {
                    completeSave(newTrip);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(TAG, "Failed to save the trip: " + error);
            }
        });
    }

    /**
     * Check for trip overlaps between existing trips for a user, and a newly created one
     *
     * @param trip The new trip to be created
     * @param trips   All the trips this user is currently part off
     * @return True if
     */
    public boolean compareForOverlaps(final Trip trip, List<Trip> trips) {
        for (Trip t : trips) {
            if (isEditMode && trip.equals(t)) continue;

            if (trip.overlaps(t)) {
                String message = activity.getString(R.string.error_overlap, t.getName(),
                        overlapDateFormat.format(t.getDateFrom()),
                        overlapDateFormat.format(t.getDateTo()));

                activity.showAlertDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        completeSave(trip);
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
     *
     * @param newTrip Trip object to save to the backend
     */
    public void completeSave(final Trip newTrip) {
        // TODO: Change the text on the Snackbars
        if (isEditMode) {
            Log.i(TAG, "Updating the trip on Parse");
            newTrip.setId(trip.getId());
            if (NetworkAlerts.isNetworkAvailable(activity)) {
                activity.showSpinner();
                ParseTripModel.updateTrip(newTrip, new ParseModel.ParseResponseCallback() {
                    @Override
                    public void onSuccess() {
                        activity.hideSpinner();
                        Intent intent = new Intent();
                        intent.putExtra(activity.getString(R.string.intent_key_trip), newTrip);
                        activity.setResult(Constants.RESULT_CODE_TRIP_UPDATED, intent);
                        activity.finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                error, Snackbar.LENGTH_SHORT);
                    }
                });
            }
            else {
                NetworkAlerts.showNetworkAlert(activity);
            }
        } else {
            Log.i(TAG, "Saving the trip to Parse");
            if (NetworkAlerts.isNetworkAvailable(activity)) {
                activity.showSpinner();
                ParseTripModel.saveNewTrip(newTrip, new ParseModel.ParseResponseCallback() {
                    @Override
                    public void onSuccess() {
                        activity.hideSpinner();
                        activity.setResult(Constants.RESULT_CODE_TRIP_CREATED);
                        activity.finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Snackbar.make(activity.findViewById(android.R.id.content),
                                error, Snackbar.LENGTH_SHORT);
                    }
                });
            }
            else {
                NetworkAlerts.showNetworkAlert(activity);
            }
        }
    }
}
