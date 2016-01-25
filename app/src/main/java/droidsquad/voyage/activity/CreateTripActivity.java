package droidsquad.voyage.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import droidsquad.voyage.R;
import droidsquad.voyage.controller.CreateTripController;

public class CreateTripActivity extends AppCompatActivity {

    private CreateTripController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
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
