package droidsquad.voyage.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

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
    private Calendar calendar;
    private int year, month, day;

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
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);



        // Create trip button
        Button mCreateTripButton = (Button) findViewById(R.id.create_trip_button);
        mCreateTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateTrip();
            }
        });

    }

    private void showDate(int year, int month, int day) {
        mDateFromView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
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
