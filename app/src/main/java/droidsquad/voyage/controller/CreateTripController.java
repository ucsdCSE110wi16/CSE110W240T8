package droidsquad.voyage.controller;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import droidsquad.voyage.activity.CreateTripActivity;
import droidsquad.voyage.model.GooglePlacesAPI;
import droidsquad.voyage.model.ParseModel;
import droidsquad.voyage.model.Trip;

public class CreateTripController {
    private CreateTripActivity activity;
    private GooglePlacesAPI googlePlacesModel;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);


    public CreateTripController(CreateTripActivity activity) {
        this.activity = activity;
        googlePlacesModel = new GooglePlacesAPI(activity);

    }

    public void attemptClose() {
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


    // TODO: perhaps make this two functions for each of the date dialogs
    public void showDateDialog(final Calendar calendar, final TextView dateView) {
        showDateDialogAndUpdateView(new DatePickerDialog.OnDateSetListener() {
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

                    // update text views
                    activity.getDateFromView().setText(dateFormat.format(calendar.getTime()));
                    activity.getDateToView().setText(dateFormat.format(calendarTo.getTime()));
                }
                else {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    dateView.setText(dateFormat.format(calendar.getTime()));
                }
            }
        }, calendar);
    }

    public void showDateDialogAndUpdateView(DatePickerDialog.OnDateSetListener listener, Calendar cal) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, listener,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        );
        long minDateAllowed;
        if (cal.equals(activity.getCalendarFrom())) {
            minDateAllowed = System.currentTimeMillis() - 1000;
        }
        else {
            minDateAllowed = activity.getCalendarFrom().getTimeInMillis() - 1000;
        }

        datePickerDialog.getDatePicker().setMinDate(minDateAllowed);
        activity.hideKeyboard();
        datePickerDialog.show();
    }

    public void attemptCreateTrip() {
        String tripName = activity.getTripNameView().getText().toString();

        int memberLimit = 0;
        if (!isEmpty(activity.getMemberLimitView())) {
            memberLimit = Integer.parseInt(activity.getMemberLimitView().getText().toString());
        }

        boolean privateTrip = activity.getPrivateView().isChecked();

        String leavingFrom = activity.getLeavingFromView().getText().toString();
        String destination = activity.getDestinationView().getText().toString();

        Date dateFrom = activity.getCalendarFrom().getTime();
        Date dateTo = activity.getCalendarTo().getTime();

        String transportation = activity.getTransportation().getSelectedItem().toString();

        Trip newTrip = new Trip(tripName, leavingFrom, destination, privateTrip,
                memberLimit, dateFrom, dateTo, transportation);

        boolean tripValid = isTripValid(tripName, memberLimit, dateFrom, dateTo);
        if (!tripValid) {
            activity.notifyTripInvalid();
        }
        else {
            ParseModel.saveTrip(newTrip);
            activity.exitActivity();
        }

        CharSequence text = "Trip Created";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(activity.getApplicationContext(), text, duration);
        toast.show();
    }

    private boolean isTripValid(String tripName, int memberLimit, Date dateFrom, Date dateTo) {

        // TODO: do other validation checks (check if edittext fields are empty or not, etc)
        boolean tripDatesValid = checkDates(dateFrom, dateTo);

        // Use googlePlacesModel.isSourceCityValid() and googlePlacesModel.isDestCityValid()
        // to know if the user did select a valid Google Maps location or not.

        return true;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private static boolean checkDates(Date from, Date to) {
        // TODO: make sure start/end dates are reasonable
        return false;
    }


    public void setUpPlacesAutofill(AutoCompleteTextView textView, int i) {
        googlePlacesModel.setUpPlacesAutofill(textView, i);
    }
}
