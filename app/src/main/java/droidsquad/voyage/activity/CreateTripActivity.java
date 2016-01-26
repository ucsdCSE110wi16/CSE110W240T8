package droidsquad.voyage.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.CreateTripController;
import droidsquad.voyage.model.Trip;


public class CreateTripActivity extends AppCompatActivity {

    private CreateTripController controller;

    private EditText mTripNameView;
    private EditText mMemberLimitView;
    private CheckBox mPrivateView;
    private EditText mLeavingFromView;
    private EditText mDestinationView;
    private EditText mTransportation;
    private TextView mDateFromView;
    private TextView mDateToView;
    private DatePicker datePicker;
    private Calendar calendarFrom;
    private Calendar calendarTo;

    private int yearFrom, monthFrom, dayFrom;
    private int yearTo, monthTo, dayTo;

    static final int DATE_FROM_PICKER_ID = 999;
    static final int DATE_TO_PICKER_ID = 1111;
    static final int DEFAULT_TRIP_LENGTH = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        controller = new CreateTripController(this);

        // Set up the trip form.
        mTripNameView = (EditText) findViewById(R.id.trip_name);
        mMemberLimitView = (EditText) findViewById(R.id.member_limit);
        mPrivateView = (CheckBox) findViewById(R.id.private_check);
        mLeavingFromView = (EditText) findViewById(R.id.leaving_from);
        mDestinationView = (EditText) findViewById(R.id.destination);
        //mTransportation = (EditText) findViewById(R.id.transportation);

        mDateFromView = (TextView) findViewById(R.id.date_from);
        mDateToView = (TextView) findViewById(R.id.date_to);

        calendarFrom = new GregorianCalendar();
        calendarTo = new GregorianCalendar();
        calendarTo.add(Calendar.DAY_OF_WEEK, DEFAULT_TRIP_LENGTH);
        showDate();

        // Create trip button
        Button mCreateTripButton = (Button) findViewById(R.id.create_trip_button);
        mCreateTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateTrip();
            }
        });

    }

    @SuppressWarnings("deprecation")
    public void setDateFrom(View view){
        showDialog(DATE_FROM_PICKER_ID);
    }

    @SuppressWarnings("deprecation")
    public void setDateTo(View view){
        System.out.println("SET DATE TO...");
        showDialog(DATE_TO_PICKER_ID);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_FROM_PICKER_ID) {
            return new DatePickerDialog(this, DateFromListener, yearFrom, monthFrom-1, dayFrom);
        }
        else if (id == DATE_TO_PICKER_ID) {
            return new DatePickerDialog(this, DateToListener, yearTo, monthTo-1, dayTo);
        }
        return null;
    }

    // SET DATE TO
    private DatePickerDialog.OnDateSetListener DateToListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            calendarTo.set(arg1, arg2, arg3);
            showDate();
        }
    };

    // SET DATE FROM
    private DatePickerDialog.OnDateSetListener DateFromListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            calendarFrom.set(arg1, arg2, arg3);
            showDate();
        }
    };

    private void showDate() {

        yearFrom = calendarFrom.get(Calendar.YEAR);
        monthFrom = calendarFrom.get(Calendar.MONTH) + 1;
        dayFrom = calendarFrom.get(Calendar.DAY_OF_MONTH);

        // TODO: Date TO set up to 1 week from TODAY
        //calendarTo.add(Calendar.DAY_OF_WEEK, DEFAULT_TRIP_LENGTH);

        yearTo = calendarTo.get(Calendar.YEAR);
        monthTo = calendarTo.get(Calendar.MONTH) + 1;
        dayTo = calendarTo.get(Calendar.DAY_OF_MONTH);

        mDateFromView.setText(new StringBuilder().append(dayFrom).append("/")
                .append(monthFrom).append("/").append(yearFrom));
        mDateToView.setText(new StringBuilder().append(dayTo).append("/")
                .append(monthTo).append("/").append(yearTo));
    }

    /** Called when the user touches the button */
    public void attemptCreateTrip() {

        String tripName = mTripNameView.getText().toString();

        int memberLimit = 0;
        if (!isEmpty(mMemberLimitView)) {
            memberLimit = Integer.parseInt(mMemberLimitView.getText().toString());
        }

        boolean privateTrip;
        if (mPrivateView.isChecked()) {
            privateTrip = true;
        }
        else {
            privateTrip = false;
        }

        String leavingFrom = mLeavingFromView.getText().toString();
        String destination = mDestinationView.getText().toString();
        //String transp = mTransportation.getText().toString();

        // TODO: CHANGE HERE
        Trip newTrip = new Trip(tripName, leavingFrom, destination, privateTrip);
        newTrip.save();

        System.out.print(newTrip);

    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }


    private static boolean checkDates( DatePicker from, DatePicker to ) {

        Date dateFrom = getDateFromDatePicker(from);
        Date dateTo = getDateFromDatePicker(to);

        return dateFrom.before(dateTo);
    }

    private static Date getDateFromDatePicker(DatePicker datePicker){

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        System.out.println("Date: " + calendar.getTime());

        return calendar.getTime();
    }
}
